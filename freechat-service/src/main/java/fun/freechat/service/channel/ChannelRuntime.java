package fun.freechat.service.channel;

import fun.freechat.channels.spi.ChannelBackendListener;
import fun.freechat.channels.spi.ChannelPlugin;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Import(ChannelPluginBeanPostProcessor.class)
@Slf4j
public final class ChannelRuntime implements SmartLifecycle {
    private final Map<String, State<?>> states = new LinkedHashMap<>();
    private final ScheduledThreadPoolExecutor timer = new ScheduledThreadPoolExecutor(
            2, Thread.ofPlatform().daemon().name("channel-timer-", 0).factory());
    private final ChannelTaskScheduler<String> lifecycle;
    private volatile boolean running;
    private final AtomicBoolean ready = new AtomicBoolean();
    private final AtomicBoolean stopped = new AtomicBoolean();

    public ChannelRuntime(ChannelRegistry registry) {
        timer.setRemoveOnCancelPolicy(true);
        int count = Math.max(1, registry.plugins().size());
        lifecycle = new ChannelTaskScheduler<>("channel-lifecycle", Math.min(8, count), Math.max(64, count * 16), 16);
        registry.plugins().forEach(this::register);
    }

    private <E> void register(ChannelPlugin<E> plugin) {
        states.put(plugin.id(), new State<>(plugin));
    }

    @Override
    public void start() {
        if (stopped.get()) {
            throw new IllegalStateException("A stopped channel runtime cannot be restarted");
        }
        running = true;
        if (ready.compareAndSet(false, true)) {
            states.values().forEach(this::scheduleReconciliation);
        }
    }

    @Scheduled(fixedDelay = 15_000, initialDelay = 15_000)
    public void reconcile() {
        if (running && ready.get()) {
            states.values().forEach(this::scheduleReconciliation);
        }
    }

    public void backendChanged(String backendId) {
        if (!running || !ready.get() || backendId == null) {
            return;
        }
        for (State<?> state : states.values()) {
            if (state.plugin instanceof ChannelBackendListener listener) {
                lifecycle
                        .submit(
                                state.plugin.id(),
                                state,
                                () -> {
                                    synchronized (state) {
                                        if (state.started && running) {
                                            listener.backendChanged(backendId);
                                        }
                                    }
                                    return CompletableFuture.completedFuture(null);
                                },
                                () -> {})
                        .exceptionally(error -> {
                            log.warn("Channel {} backend refresh deferred", state.plugin.id());
                            return null;
                        });
            }
        }
    }

    public int pendingReceives(String pluginId) {
        return state(pluginId).runtime.pendingReceives();
    }

    public int pendingSends(String pluginId) {
        return state(pluginId).runtime.pendingSends();
    }

    private State<?> state(String pluginId) {
        State<?> state = states.get(pluginId);
        if (state == null) {
            throw new IllegalArgumentException("Unknown channel plugin");
        }
        return state;
    }

    private void scheduleReconciliation(State<?> state) {
        if (!state.refreshPending.compareAndSet(false, true)) {
            return;
        }
        lifecycle
                .submit(
                        state.plugin.id(),
                        state,
                        () -> {
                            if (running) {
                                state.refresh();
                            }
                            return CompletableFuture.completedFuture(null);
                        },
                        () -> {})
                .whenComplete((ignored, error) -> {
                    state.refreshPending.set(false);
                    if (error != null) {
                        log.warn("Channel {} activation or reconciliation deferred", state.plugin.id());
                    }
                });
    }

    @Override
    public void stop() {
        if (!stopped.compareAndSet(false, true)) {
            return;
        }
        running = false;
        long timeout = states.values().stream()
                .mapToLong(state -> state.plugin.policy().shutdownTimeout().toNanos())
                .max()
                .orElse(Duration.ofSeconds(10).toNanos());
        long deadline = System.nanoTime() + timeout;
        CompletableFuture<?>[] stopReceiving = states.values().stream()
                .map(state -> lifecycle.submit(
                        state.plugin.id(),
                        state,
                        () -> {
                            synchronized (state) {
                                state.plugin.stopReceiving();
                            }
                            return CompletableFuture.completedFuture(null);
                        },
                        () -> {}))
                .toArray(CompletableFuture[]::new);
        try {
            CompletableFuture.allOf(stopReceiving).get(Math.max(0, deadline - System.nanoTime()), TimeUnit.NANOSECONDS);
        } catch (Exception ignored) {
            log.warn("Channel source shutdown did not settle before its deadline");
        }
        states.values().forEach(state -> state.runtime.close(remaining(deadline)));
        CompletableFuture<?>[] cleanup =
                states.values().stream().map(State::dispose).toArray(CompletableFuture[]::new);
        try {
            CompletableFuture.allOf(cleanup).get(Math.max(0, deadline - System.nanoTime()), TimeUnit.NANOSECONDS);
        } catch (Exception ignored) {
            log.warn("Channel resource cleanup has not settled before its deadline");
        }
        lifecycle.close(remaining(deadline));
        timer.shutdown();
    }

    private static Duration remaining(long deadline) {
        return Duration.ofNanos(Math.max(0, deadline - System.nanoTime()));
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE - 50;
    }

    private final class State<E> {
        final ChannelPlugin<E> plugin;
        final ChannelPluginRuntime<E> runtime;
        final AtomicBoolean refreshPending = new AtomicBoolean();
        volatile boolean started;
        boolean disposed;

        State(ChannelPlugin<E> plugin) {
            this.plugin = plugin;
            this.runtime = new ChannelPluginRuntime<>(plugin, timer);
        }

        synchronized void refresh() {
            if (!running || disposed) {
                return;
            }
            if (!started) {
                try {
                    plugin.start(runtime);
                    started = true;
                } catch (Exception ignored) {
                    throw new IllegalStateException("Channel startup failed");
                }
            } else if (plugin instanceof ChannelBackendListener listener) {
                listener.reconcile();
            }
        }

        CompletableFuture<Void> dispose() {
            CompletableFuture<Void> result = new CompletableFuture<>();
            Thread.ofVirtual().name("channel-" + plugin.id() + "-cleanup").start(() -> {
                synchronized (this) {
                    disposed = true;
                    try {
                        try {
                            plugin.stopReceiving();
                        } finally {
                            plugin.close();
                        }
                        result.complete(null);
                    } catch (Throwable ignored) {
                        log.warn("Channel {} resource cleanup failed", plugin.id());
                        result.completeExceptionally(new IllegalStateException("Channel cleanup failed"));
                    }
                }
            });
            return result;
        }
    }
}
