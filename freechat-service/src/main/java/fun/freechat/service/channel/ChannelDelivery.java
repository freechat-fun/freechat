package fun.freechat.service.channel;

import fun.freechat.channels.spi.*;
import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

final class ChannelDelivery implements ChannelOutbound {
    interface Tracker {
        <T> CompletableFuture<T> track(
                Supplier<ChannelTaskScheduler.Operation<T>> submission, boolean transientOperation);
    }

    private final ChannelAddress address;
    private final Object owner;
    private final ChannelTransport transport;
    private final ChannelPolicy policy;
    private final ChannelTaskScheduler<ChannelAddress> scheduler;
    private final ScheduledExecutorService timer;
    private final BooleanSupplier usable;
    private final Tracker tracker;
    private final AtomicBoolean typingPending = new AtomicBoolean();

    ChannelDelivery(
            ChannelAddress address,
            Object owner,
            ChannelTransport transport,
            ChannelPolicy policy,
            ChannelTaskScheduler<ChannelAddress> scheduler,
            ScheduledExecutorService timer,
            BooleanSupplier usable) {
        this(address, owner, transport, policy, scheduler, timer, usable, null);
    }

    ChannelDelivery(
            ChannelAddress address,
            Object owner,
            ChannelTransport transport,
            ChannelPolicy policy,
            ChannelTaskScheduler<ChannelAddress> scheduler,
            ScheduledExecutorService timer,
            BooleanSupplier usable,
            Tracker tracker) {
        this.address = address;
        this.owner = owner;
        this.transport = transport;
        this.policy = policy;
        this.scheduler = scheduler;
        this.timer = timer;
        this.usable = usable;
        this.tracker = tracker;
    }

    @Override
    public boolean supports(Class<?> capability) {
        return capability.isInstance(transport);
    }

    @Override
    public CompletableFuture<ChannelReceipt> sendText(ChannelText text) {
        return submit(() -> transport.sendText(address, text), true);
    }

    @Override
    public CompletableFuture<ChannelReceipt> sendMedia(ChannelMedia media) {
        if (!(transport instanceof ChannelMediaTransport delivery)
                || !delivery.mediaKinds().contains(media.kind())) {
            return unsupported();
        }
        return submit(() -> delivery.sendMedia(address, media), true);
    }

    @Override
    public CompletableFuture<Void> editText(String messageId, ChannelText text) {
        if (!(transport instanceof ChannelMessageEditor editor)) {
            return unsupported();
        }
        return submit(
                () -> {
                    editor.editText(address, messageId, text);
                    return null;
                },
                true);
    }

    @Override
    public CompletableFuture<Void> sendTyping(BooleanSupplier stillNeeded) {
        if (!(transport instanceof ChannelStatusTransport status)) {
            return unsupported();
        }
        if (!typingPending.compareAndSet(false, true)) {
            return CompletableFuture.completedFuture(null);
        }
        CompletableFuture<Void> result = submit(
                () -> {
                    if (stillNeeded.getAsBoolean()) {
                        status.sendTyping(address);
                    }
                    return null;
                },
                false);
        result.whenComplete((ignored, error) -> typingPending.set(false));
        return result;
    }

    private <T> CompletableFuture<T> submit(Callable<T> attempt, boolean retry) {
        if (!usable.getAsBoolean()) {
            return CompletableFuture.failedFuture(new ChannelFailure(ChannelFailure.Kind.CANCELLED));
        }
        AtomicReference<CompletableFuture<Void>> waiting = new AtomicReference<>();
        AtomicBoolean cancelled = new AtomicBoolean();
        long deadline = System.nanoTime() + policy.deliveryTimeout().toNanos();
        Supplier<ChannelTaskScheduler.Operation<T>> submission = () -> scheduler.submitTracked(
                address,
                owner,
                () -> {
                    for (int count = 1; ; count++) {
                        if (cancelled.get() || !usable.getAsBoolean()) {
                            throw new ChannelFailure(ChannelFailure.Kind.CANCELLED);
                        }
                        if (System.nanoTime() >= deadline) {
                            throw new ChannelFailure(ChannelFailure.Kind.FAILED);
                        }
                        try {
                            return CompletableFuture.completedFuture(attempt.call());
                        } catch (Exception error) {
                            ChannelFailure failure = ChannelTaskScheduler.failure(error);
                            if (!retry || !failure.retryable() || count >= policy.deliveryAttempts()) {
                                throw failure;
                            }
                            Duration delay =
                                    failure.retryAfter().isZero() ? Duration.ofMillis(250) : failure.retryAfter();
                            long remaining = deadline - System.nanoTime();
                            if (delay.toNanos() >= remaining) {
                                throw failure;
                            }
                            CompletableFuture<Void> wakeup = new CompletableFuture<>();
                            waiting.set(wakeup);
                            var scheduled =
                                    timer.schedule(() -> wakeup.complete(null), delay.toNanos(), TimeUnit.NANOSECONDS);
                            if (cancelled.get() || !usable.getAsBoolean()) {
                                wakeup.completeExceptionally(new ChannelFailure(ChannelFailure.Kind.CANCELLED));
                            }
                            try {
                                wakeup.get();
                            } catch (InterruptedException interrupted) {
                                Thread.currentThread().interrupt();
                                throw new ChannelFailure(ChannelFailure.Kind.CANCELLED);
                            } catch (Exception interrupted) {
                                throw ChannelTaskScheduler.failure(interrupted);
                            } finally {
                                scheduled.cancel(false);
                                waiting.compareAndSet(wakeup, null);
                            }
                        }
                    }
                },
                () -> {
                    cancelled.set(true);
                    CompletableFuture<Void> wakeup = waiting.get();
                    if (wakeup != null) {
                        wakeup.completeExceptionally(new ChannelFailure(ChannelFailure.Kind.CANCELLED));
                    }
                });
        if (tracker != null) {
            return tracker.track(submission, !retry);
        }
        ChannelTaskScheduler.Operation<T> operation = submission.get();
        if (!usable.getAsBoolean()) {
            operation.requestCancellation();
        }
        return operation.future();
    }

    private static <T> CompletableFuture<T> unsupported() {
        return CompletableFuture.failedFuture(new ChannelFailure(ChannelFailure.Kind.UNSUPPORTED));
    }
}
