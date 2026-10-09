package fun.freechat.service.channel;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

public final class ChannelPollingLease implements AutoCloseable {
    private enum State {
        STARTING,
        ACTIVE,
        CLOSING,
        SETTLED
    }

    private final RedissonClient client;
    private final String lockName;
    private final Callable<? extends AutoCloseable> onAcquired;
    private final Runnable onLost;
    private final long checkIntervalNanos;
    private final AtomicReference<State> state = new AtomicReference<>(State.STARTING);
    private final CompletableFuture<Void> settlement = new CompletableFuture<>();
    private final CompletionStage<Void> settledView = settlement.minimalCompletionStage();
    private final Thread owner;

    private ChannelPollingLease(
            RedissonClient client,
            String lockName,
            Callable<? extends AutoCloseable> onAcquired,
            Runnable onLost,
            Duration checkInterval) {
        this.client = Objects.requireNonNull(client, "client");
        this.lockName = Objects.requireNonNull(lockName, "lockName");
        if (lockName.isBlank()) {
            throw new IllegalArgumentException("Polling lock name must not be blank");
        }
        this.onAcquired = Objects.requireNonNull(onAcquired, "onAcquired");
        this.onLost = Objects.requireNonNull(onLost, "onLost");
        Objects.requireNonNull(checkInterval, "checkInterval");
        if (checkInterval.isNegative() || checkInterval.isZero()) {
            throw new IllegalArgumentException("Polling check interval must be positive");
        }
        try {
            checkIntervalNanos = checkInterval.toNanos();
        } catch (ArithmeticException ignored) {
            throw new IllegalArgumentException("Polling check interval is too large");
        }
        // Do not put caller-controlled identifiers or credentials in thread names or diagnostics.
        owner = Thread.ofVirtual().name("channel-polling-owner").unstarted(this::run);
    }

    /** Starts asynchronously; uses the exact supplied lock name without adding a namespace. */
    public static ChannelPollingLease start(
            RedissonClient client,
            String lockName,
            Callable<? extends AutoCloseable> onAcquired,
            Runnable onLost,
            Duration checkInterval) {
        ChannelPollingLease lease = new ChannelPollingLease(client, lockName, onAcquired, onLost, checkInterval);
        lease.owner.start();
        return lease;
    }

    /** A local snapshot, not a fresh Redis check; becomes false immediately when close is requested. */
    public boolean isActive() {
        return state.get() == State.ACTIVE;
    }

    public CompletionStage<Void> settled() {
        return settledView;
    }

    /** Requests shutdown without blocking or interrupting provider/Redis cleanup. Safe from any thread. */
    @Override
    public void close() {
        state.getAndUpdate(current -> current == State.SETTLED ? current : State.CLOSING);
        LockSupport.unpark(owner);
    }

    private boolean stopping() {
        return state.get() == State.CLOSING || Thread.currentThread().isInterrupted();
    }

    private void run() {
        RLock lock = null;
        AutoCloseable registration = null;
        boolean releaseNeeded = false;
        boolean failed = false;
        boolean interrupted = false;
        try {
            if (stopping()) {
                return;
            }
            lock = client.getLock(lockName);
            // An uncertain acquisition may have reached Redis; cleanup must verify this owner's lock.
            releaseNeeded = true;
            releaseNeeded = lock.tryLock();
            if (!releaseNeeded || stopping() || !lock.isHeldByCurrentThread()) {
                return;
            }
            registration = Objects.requireNonNull(onAcquired.call(), "Polling registration must not be null");
            if (stopping() || !lock.isHeldByCurrentThread() || !state.compareAndSet(State.STARTING, State.ACTIVE)) {
                return;
            }
            while (!stopping()) {
                LockSupport.parkNanos(this, checkIntervalNanos);
                if (stopping() || !lock.isHeldByCurrentThread()) {
                    return;
                }
            }
        } catch (Throwable failure) {
            // Provider exceptions can contain credentials or response bodies; never retain/log them.
            failed = true;
            interrupted = failure instanceof InterruptedException;
        } finally {
            state.set(State.CLOSING);
            interrupted |= Thread.interrupted();
            try {
                onLost.run();
            } catch (Throwable failure) {
                failed = true;
                interrupted |= failure instanceof InterruptedException;
            }
            interrupted |= Thread.interrupted();
            if (registration != null) {
                try {
                    registration.close();
                } catch (Throwable failure) {
                    failed = true;
                    interrupted |= failure instanceof InterruptedException;
                }
            }
            interrupted |= Thread.interrupted();
            if (releaseNeeded) {
                try {
                    if (lock.isHeldByCurrentThread()) {
                        // Normal unlock atomically rejects a stale owner even after this ownership check.
                        lock.unlock();
                    }
                } catch (IllegalMonitorStateException ignored) {
                    // Ownership changed after the check. Never attempt an unconditional unlock.
                } catch (Throwable failure) {
                    failed = true;
                    interrupted |= failure instanceof InterruptedException;
                }
            }
            interrupted |= Thread.interrupted();
            state.set(State.SETTLED);
            try {
                if (failed) {
                    settlement.completeExceptionally(new IllegalStateException("Polling lease failed"));
                } else {
                    settlement.complete(null);
                }
            } finally {
                if (interrupted) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
