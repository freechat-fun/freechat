package fun.freechat.service.chat.memory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/** Invocation-owned timers; the scheduler and worker threads remain owned by their callers. */
public final class MemoryTurnLifetime implements AutoCloseable {
    private enum State {
        ACTIVE,
        FAILING,
        FAILED,
        FINISHED
    }

    private final Object monitor = new Object();
    private final MemoryTurnRepository turns;
    private final MemoryInvocation invocation;
    private final int cancellationLimit;
    private final List<Runnable> cancellations = new ArrayList<>();
    private final List<Consumer<Throwable>> failures = new ArrayList<>();
    private final CompletableFuture<Void> fenced = new CompletableFuture<>();
    private volatile State state = State.ACTIVE;
    private long deadlineNanos;
    private ScheduledFuture<?> heartbeat;
    private ScheduledFuture<?> timeout;

    public MemoryTurnLifetime(
            MemoryTurnRepository turns,
            MemoryInvocation invocation,
            LongTermMemoryProperties properties,
            ScheduledExecutorService scheduler) {
        this.turns = turns;
        this.invocation = invocation;
        try {
            this.cancellationLimit = Math.addExact(properties.getMaxToolRounds(), 1);
            // Count time spent reading SQL conservatively, never using the application wall clock.
            long beforeRead = System.nanoTime();
            long remaining = turns.remainingMillis(invocation.lease());
            if (remaining <= 0) {
                throw unavailable();
            }
            deadlineNanos = beforeRead + TimeUnit.MILLISECONDS.toNanos(remaining);
            install(
                    scheduler.schedule(
                            this::close, Math.max(0, deadlineNanos - System.nanoTime()), TimeUnit.NANOSECONDS),
                    false);
            install(
                    scheduler.scheduleWithFixedDelay(
                            this::renew, 0, properties.getLeaseRenewInterval().toMillis(), TimeUnit.MILLISECONDS),
                    true);
        } catch (Throwable ignored) {
            close();
            throw unavailable();
        }
    }

    private void install(ScheduledFuture<?> timer, boolean renewal) {
        synchronized (monitor) {
            if (renewal) {
                heartbeat = timer;
            } else {
                timeout = timer;
            }
            if (state != State.ACTIVE) {
                timer.cancel(false);
            }
        }
    }

    public void check() {
        if (!active()) {
            throw unavailable();
        }
        try {
            if (System.nanoTime() - deadlineNanos >= 0) {
                throw unavailable();
            }
            invocation.check();
            if (!active()) {
                throw unavailable();
            }
        } catch (Throwable ignored) {
            close();
            throw unavailable();
        }
    }

    private void renew() {
        if (!active()) {
            return;
        }
        if (invocation.completed()) {
            finish();
            return;
        }
        try {
            check();
            turns.renew(invocation.lease());
        } catch (Throwable ignored) {
            // A stale task must never interrupt a queue worker that may already run another turn.
            close();
        }
    }

    public void onFailure(Consumer<Throwable> callback) {
        java.util.Objects.requireNonNull(callback, "A failure callback is required");
        boolean notify;
        synchronized (monitor) {
            notify = state == State.FAILED;
            if (state == State.ACTIVE || state == State.FAILING) {
                failures.add(callback);
            }
        }
        if (notify) {
            safely(() -> callback.accept(unavailable()));
        }
    }

    public void onCancel(Runnable cancelTransport) {
        java.util.Objects.requireNonNull(cancelTransport, "A transport cancellation callback is required");
        boolean cancel;
        boolean overflow = false;
        synchronized (monitor) {
            cancel = state != State.ACTIVE;
            if (!cancel) {
                if (cancellations.size() < cancellationLimit) {
                    cancellations.add(cancelTransport);
                } else {
                    overflow = true;
                }
            }
        }
        if (overflow) {
            close();
        }
        if (cancel || overflow) {
            safely(cancelTransport);
        }
    }

    /** Only an accepted SQL final answer permits successful timer disposal. */
    public void finish() {
        if (!invocation.completed()) {
            close();
            throw unavailable();
        }
        synchronized (monitor) {
            if (state != State.ACTIVE) {
                return;
            }
            state = State.FINISHED;
            cancelTimers();
            failures.clear();
            cancellations.clear();
        }
    }

    @Override
    public void close() {
        boolean owner = false;
        synchronized (monitor) {
            if (state == State.FINISHED || state == State.FAILED) {
                return;
            }
            if (state == State.ACTIVE) {
                if (invocation.completed()) {
                    state = State.FINISHED;
                    cancelTimers();
                    failures.clear();
                    cancellations.clear();
                    return;
                }
                state = State.FAILING;
                cancelTimers();
                owner = true;
            }
        }
        if (!owner) {
            // Coordination cannot be released while another closer is still aborting SQL.
            fenced.join();
            return;
        }
        // No external work or callback executes under the heartbeat monitor.
        safely(invocation::close);
        List<Consumer<Throwable>> notify;
        List<Runnable> cancel;
        synchronized (monitor) {
            state = State.FAILED;
            notify = List.copyOf(failures);
            cancel = List.copyOf(cancellations);
            failures.clear();
            cancellations.clear();
        }
        fenced.complete(null);
        // Notify first: transport cancellation is best effort and may block or call us back.
        notify.forEach(callback -> safely(() -> callback.accept(unavailable())));
        cancel.forEach(MemoryTurnLifetime::safely);
    }

    private void cancelTimers() {
        if (heartbeat != null) {
            heartbeat.cancel(false);
        }
        if (timeout != null) {
            timeout.cancel(false);
        }
    }

    boolean active() {
        return state == State.ACTIVE;
    }

    int cancellationLimit() {
        return cancellationLimit;
    }

    static IllegalStateException unavailable() {
        // Deliberately omit the original cause, suppressed exceptions and provider payloads.
        return new IllegalStateException("Chat memory turn is no longer available");
    }

    static void safely(Runnable action) {
        try {
            action.run();
        } catch (Throwable ignored) {
            // Callback and transport exceptions must not reach provider/framework payload logging.
        }
    }
}
