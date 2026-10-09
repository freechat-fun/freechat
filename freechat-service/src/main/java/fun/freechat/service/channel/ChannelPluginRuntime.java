package fun.freechat.service.channel;

import fun.freechat.channels.spi.*;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;

@Slf4j
final class ChannelPluginRuntime<E> implements ChannelRuntimeContext<E> {
    private final ChannelPlugin<E> plugin;
    private final ChannelPolicy policy;
    private final ChannelTaskScheduler<ChannelAddress> receives;
    private final ChannelTaskScheduler<ChannelAddress> sends;
    private final ScheduledThreadPoolExecutor timer;
    private final ExecutorService cancellationWorkers;
    private final Map<String, Instance> instances = new ConcurrentHashMap<>();
    private volatile boolean closed;

    ChannelPluginRuntime(ChannelPlugin<E> plugin, ScheduledThreadPoolExecutor timer) {
        this.plugin = plugin;
        this.policy = plugin.policy();
        this.timer = timer;
        this.receives = new ChannelTaskScheduler<>(
                "channel-" + plugin.id() + "-receive",
                policy.receiveConcurrency(),
                policy.receiveCapacity(),
                policy.conversationReceiveCapacity());
        this.sends = new ChannelTaskScheduler<>(
                "channel-" + plugin.id() + "-send",
                policy.sendConcurrency(),
                policy.sendCapacity(),
                policy.conversationSendCapacity());
        this.cancellationWorkers = Executors.newFixedThreadPool(
                2,
                Thread.ofVirtual()
                        .name("channel-" + plugin.id() + "-cancel-", 0)
                        .factory());
        receives.settled().whenComplete((ignored, error) -> cancellationWorkers.shutdown());
    }

    @Override
    public synchronized ChannelInstance<E> openInstance(String instanceId) {
        new ChannelAddress(plugin.id(), instanceId, "validation");
        if (closed || instances.size() >= policy.maxInstances()) {
            throw new ChannelFailure(ChannelFailure.Kind.REJECTED);
        }
        Instance instance = new Instance(instanceId);
        if (instances.putIfAbsent(instanceId, instance) != null) {
            throw new IllegalStateException("Channel instance is already open");
        }
        return instance;
    }

    int pendingReceives() {
        return receives.pendingCount();
    }

    int pendingSends() {
        return sends.pendingCount();
    }

    void close(Duration timeout) {
        long deadline = System.nanoTime() + timeout.toNanos();
        closed = true;
        List.copyOf(instances.values()).forEach(Instance::close);
        receives.close(remaining(deadline));
        sends.close(remaining(deadline));
        if (receives.pendingCount() == 0) {
            cancellationWorkers.shutdown();
        } else {
            log.warn("Channel {} still has {} unsettled receive operations", plugin.id(), receives.pendingCount());
        }
        if (sends.pendingCount() != 0) {
            log.warn("Channel {} still has {} unsettled send operations", plugin.id(), sends.pendingCount());
        }
    }

    private static Duration remaining(long deadline) {
        return Duration.ofNanos(Math.max(0, deadline - System.nanoTime()));
    }

    private final class Instance implements ChannelInstance<E> {
        private final String id;
        private final AtomicBoolean active = new AtomicBoolean(true);
        private final Set<Turn> turns = ConcurrentHashMap.newKeySet();

        Instance(String id) {
            this.id = id;
        }

        @Override
        public String id() {
            return id;
        }

        @Override
        public boolean isActive() {
            return active.get() && !closed;
        }

        @Override
        public CompletableFuture<Void> receive(String conversationId, String eventId, E payload) {
            if (!isActive()) {
                return cancelled();
            }
            ChannelAddress address = new ChannelAddress(plugin.id(), id, conversationId);
            ChannelEnvelope<E> envelope = new ChannelEnvelope<>(address, eventId, payload);
            Turn turn = new Turn(this, address);
            turns.add(turn);
            ChannelTaskScheduler.Operation<Void> operation = receives.submitTracked(
                    address,
                    this,
                    () -> {
                        turn.started.set(true);
                        if (turn.isCancelled()) {
                            turn.finish(new ChannelFailure(ChannelFailure.Kind.CANCELLED));
                            return turn.completion;
                        }
                        try {
                            Objects.requireNonNull(plugin.inboundHandler().handle(envelope, turn))
                                    .whenComplete((ignored, error) -> turn.finish(error));
                        } catch (Throwable error) {
                            turn.requestCancel();
                            turn.finish(ChannelTaskScheduler.failure(error));
                        }
                        return turn.completion;
                    },
                    turn::requestCancel,
                    true);
            CompletableFuture<Void> result = operation.future();
            operation.physicalResult().whenComplete((ignored, error) -> {
                if (!turn.started.get()) {
                    turn.finish(error);
                }
            });
            ScheduledFuture<?> deadline = timer.schedule(
                    () -> {
                        turn.requestCancel();
                        result.cancel(false);
                    },
                    policy.turnTimeout().toNanos(),
                    TimeUnit.NANOSECONDS);
            result.whenComplete((ignored, error) -> {
                deadline.cancel(false);
                if (error != null && !(error instanceof java.util.concurrent.CancellationException)) {
                    log.warn(
                            "Channel {} receive operation ended with {}",
                            plugin.id(),
                            ChannelTaskScheduler.failure(error).kind());
                }
            });
            if (!isActive()) {
                result.cancel(false);
            }
            return result;
        }

        @Override
        public ChannelOutbound outbound(String conversationId) {
            return new ChannelDelivery(
                    new ChannelAddress(plugin.id(), id, conversationId),
                    this,
                    plugin.transport(),
                    policy,
                    sends,
                    timer,
                    this::isActive);
        }

        @Override
        public void close() {
            if (!active.compareAndSet(true, false)) {
                return;
            }
            receives.cancelOwner(this);
            sends.cancelOwner(this);
            turns.forEach(Turn::requestCancel);
            instances.remove(id, this);
        }
    }

    private final class Turn implements ChannelTurnContext, ChannelDelivery.Tracker {
        private final Instance instance;
        private final ChannelAddress address;
        private final ChannelOutbound outbound;
        private final Instant deadline = Instant.now().plus(policy.turnTimeout());
        private final AtomicBoolean started = new AtomicBoolean();
        private final AtomicBoolean cancelled = new AtomicBoolean();
        private final AtomicBoolean finished = new AtomicBoolean();
        private final List<Runnable> cancellation = new ArrayList<>();
        private final Set<ScheduledFuture<?>> timers = new HashSet<>();
        private final Set<Child> children = new HashSet<>();
        private final CompletableFuture<Void> completion = new CompletableFuture<>();
        private boolean handlerSettled;
        private boolean settled;
        private Throwable handlerError;

        Turn(Instance instance, ChannelAddress address) {
            this.instance = instance;
            this.address = address;
            this.outbound = new ChannelDelivery(
                    address, this, plugin.transport(), policy, sends, timer, () -> !isCancelled(), this);
        }

        @Override
        public ChannelAddress address() {
            return address;
        }

        @Override
        public ChannelOutbound outbound() {
            return outbound;
        }

        @Override
        public Instant deadline() {
            return deadline;
        }

        @Override
        public boolean isCancelled() {
            return cancelled.get() || !instance.isActive();
        }

        @Override
        public void onCancel(Runnable action) {
            Objects.requireNonNull(action);
            synchronized (cancellation) {
                if (!cancelled.get()) {
                    if (!finished.get()) {
                        if (cancellation.size() >= 16) {
                            throw new ChannelFailure(ChannelFailure.Kind.REJECTED);
                        }
                        cancellation.add(action);
                    }
                    return;
                }
            }
            cancelAsync(List.of(action));
        }

        @Override
        public ScheduledFuture<?> schedule(Runnable action, Duration delay) {
            if (delay.isNegative()) {
                throw new IllegalArgumentException("Negative channel timer delay");
            }
            synchronized (timers) {
                timers.removeIf(ScheduledFuture::isDone);
                if (isCancelled() || finished.get()) {
                    throw new ChannelFailure(ChannelFailure.Kind.CANCELLED);
                }
                if (timers.size() >= 8) {
                    throw new ChannelFailure(ChannelFailure.Kind.REJECTED);
                }
                ScheduledFuture<?> future = timer.schedule(
                        () -> {
                            if (!isCancelled() && !finished.get()) {
                                try {
                                    action.run();
                                } catch (RuntimeException ignored) {
                                    log.warn("Channel {} timer callback failed", plugin.id());
                                    requestCancel();
                                }
                            }
                        },
                        delay.toNanos(),
                        TimeUnit.NANOSECONDS);
                timers.add(future);
                return future;
            }
        }

        @Override
        public AutoCloseable typing(Duration interval) {
            if (interval.isZero() || interval.isNegative()) {
                throw new IllegalArgumentException("Channel typing interval must be positive");
            }
            if (!outbound.supports(ChannelStatusTransport.class)) {
                return () -> {};
            }
            AtomicBoolean enabled = new AtomicBoolean(true);
            Runnable tick = () -> {
                if (enabled.get() && !isCancelled() && !finished.get()) {
                    outbound.sendTyping(() -> enabled.get() && !isCancelled() && !finished.get());
                }
            };
            tick.run();
            ScheduledFuture<?> heartbeat;
            synchronized (timers) {
                timers.removeIf(ScheduledFuture::isDone);
                if (isCancelled() || finished.get() || timers.size() >= 8) {
                    enabled.set(false);
                    return () -> {};
                }
                heartbeat = timer.scheduleWithFixedDelay(
                        tick, interval.toNanos(), interval.toNanos(), TimeUnit.NANOSECONDS);
                timers.add(heartbeat);
            }
            return () -> {
                enabled.set(false);
                heartbeat.cancel(false);
                synchronized (timers) {
                    timers.remove(heartbeat);
                }
            };
        }

        void requestCancel() {
            if (!cancelled.compareAndSet(false, true)) {
                return;
            }
            List<Runnable> actions;
            synchronized (cancellation) {
                actions = List.copyOf(cancellation);
                cancellation.clear();
            }
            stopTimers();
            cancelAsync(actions);
            sends.cancelOwner(this);
        }

        @Override
        public <T> CompletableFuture<T> track(
                Supplier<ChannelTaskScheduler.Operation<T>> submission, boolean transientOperation) {
            Child child = new Child(transientOperation);
            synchronized (children) {
                if (isCancelled() || finished.get()) {
                    return cancelled();
                }
                children.add(child);
            }
            ChannelTaskScheduler.Operation<T> operation;
            try {
                operation = submission.get();
            } catch (Throwable error) {
                childSettled(child);
                return CompletableFuture.failedFuture(ChannelTaskScheduler.failure(error));
            }
            boolean cancel;
            synchronized (children) {
                child.operation = operation;
                cancel = isCancelled() || (finished.get() && transientOperation);
            }
            operation.physicalResult().whenComplete((ignored, error) -> childSettled(child));
            if (cancel) {
                operation.requestCancellation();
            }
            return operation.future();
        }

        void finish(Throwable error) {
            List<ChannelTaskScheduler.Operation<?>> expired;
            synchronized (children) {
                if (!finished.compareAndSet(false, true)) {
                    return;
                }
                handlerError = error;
                expired = children.stream()
                        .filter(child -> child.transientOperation && child.operation != null)
                        .<ChannelTaskScheduler.Operation<?>>map(child -> child.operation)
                        .toList();
            }
            stopTimers();
            expired.forEach(ChannelTaskScheduler.Operation::requestCancellation);
            synchronized (cancellation) {
                cancellation.clear();
            }
            synchronized (children) {
                handlerSettled = true;
            }
            completeIfSettled();
        }

        private void childSettled(Child child) {
            synchronized (children) {
                children.remove(child);
            }
            completeIfSettled();
        }

        private void completeIfSettled() {
            Throwable error;
            synchronized (children) {
                if (!handlerSettled || !children.isEmpty() || settled) {
                    return;
                }
                settled = true;
                error = handlerError;
            }
            instance.turns.remove(this);
            if (error == null) {
                completion.complete(null);
            } else {
                completion.completeExceptionally(error);
            }
        }

        private final class Child {
            final boolean transientOperation;
            ChannelTaskScheduler.Operation<?> operation;

            Child(boolean transientOperation) {
                this.transientOperation = transientOperation;
            }
        }

        private void stopTimers() {
            synchronized (timers) {
                timers.forEach(future -> future.cancel(false));
                timers.clear();
            }
        }
    }

    private void cancelAsync(List<Runnable> actions) {
        if (actions.isEmpty()) {
            return;
        }
        cancellationWorkers.execute(() -> actions.forEach(action -> {
            try {
                action.run();
            } catch (RuntimeException ignored) {
                log.warn("Channel {} cancellation callback failed", plugin.id());
            }
        }));
    }

    private static <T> CompletableFuture<T> cancelled() {
        return CompletableFuture.failedFuture(new ChannelFailure(ChannelFailure.Kind.CANCELLED));
    }
}
