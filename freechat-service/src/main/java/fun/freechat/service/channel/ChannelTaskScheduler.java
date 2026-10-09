package fun.freechat.service.channel;

import fun.freechat.channels.spi.ChannelFailure;
import fun.freechat.util.TraceUtils;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public final class ChannelTaskScheduler<K> {
    private final Object monitor = new Object();
    private final Map<K, Lane> lanes = new HashMap<>();
    private final ArrayDeque<Lane> ready = new ArrayDeque<>();
    private final ExecutorService workers;
    private final int concurrency;
    private final int capacity;
    private final int laneCapacity;
    private final CompletableFuture<Void> settlement = new CompletableFuture<>();
    private int pending;
    private int active;
    private boolean closed;

    public ChannelTaskScheduler(String name, int concurrency, int capacity, int laneCapacity) {
        if (concurrency < 1 || capacity < concurrency || laneCapacity < 1 || laneCapacity > capacity) {
            throw new IllegalArgumentException("Invalid channel scheduler limits");
        }
        this.concurrency = concurrency;
        this.capacity = capacity;
        this.laneCapacity = laneCapacity;
        this.workers = Executors.newThreadPerTaskExecutor(
                Thread.ofVirtual().name(name + "-", 0).factory());
    }

    public <T> CompletableFuture<T> submit(
            K key, Object owner, Supplier<? extends CompletionStage<T>> action, Runnable cancellation) {
        return submitTracked(key, owner, action, cancellation, true).future();
    }

    <T> Operation<T> submitTracked(
            K key, Object owner, Supplier<? extends CompletionStage<T>> action, Runnable cancellation) {
        return submitTracked(key, owner, action, cancellation, false);
    }

    <T> Operation<T> submitTracked(
            K key,
            Object owner,
            Supplier<? extends CompletionStage<T>> action,
            Runnable cancellation,
            boolean cancelObservation) {
        Objects.requireNonNull(key);
        Entry<T> entry = new Entry<>(owner, action, cancellation, cancelObservation);
        synchronized (monitor) {
            Lane lane = lanes.get(key);
            if (closed || pending >= capacity || (lane != null && lane.entries.size() >= laneCapacity)) {
                entry.completed = true;
            } else {
                if (lane == null) {
                    lane = new Lane(key);
                    lanes.put(key, lane);
                }
                entry.lane = lane;
                lane.entries.addLast(entry);
                pending++;
                enqueue(lane);
                dispatch();
                return entry.operation;
            }
        }
        entry.complete(null, new ChannelFailure(ChannelFailure.Kind.REJECTED));
        return entry.operation;
    }

    public void cancelOwner(Object owner) {
        List<Entry<?>> entries = new ArrayList<>();
        synchronized (monitor) {
            for (Lane lane : lanes.values()) {
                for (Entry<?> entry : lane.entries) {
                    if (entry.owner == owner) {
                        entries.add(entry);
                    }
                }
            }
        }
        entries.forEach(Entry::cancelOwned);
    }

    public int pendingCount() {
        synchronized (monitor) {
            return pending;
        }
    }

    public int activeCount() {
        synchronized (monitor) {
            return active;
        }
    }

    public CompletionStage<Void> settled() {
        return settlement.minimalCompletionStage();
    }

    public void close(Duration timeout) {
        List<Entry<?>> entries = new ArrayList<>();
        synchronized (monitor) {
            closed = true;
            lanes.values().forEach(lane -> entries.addAll(lane.entries));
        }
        entries.forEach(Entry::cancelOwned);
        workers.shutdown();
        signalSettlement();
        try {
            workers.awaitTermination(Math.max(0, timeout.toMillis()), TimeUnit.MILLISECONDS);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }

    public static ChannelFailure failure(Throwable error) {
        while ((error instanceof CompletionException || error instanceof ExecutionException)
                && error.getCause() != null) {
            error = error.getCause();
        }
        return error instanceof ChannelFailure failure
                ? new ChannelFailure(failure.kind(), failure.retryAfter())
                : new ChannelFailure(ChannelFailure.Kind.FAILED);
    }

    private void enqueue(Lane lane) {
        if (!lane.running && !lane.queued && !lane.entries.isEmpty() && !closed) {
            lane.queued = true;
            ready.addLast(lane);
        }
    }

    private void dispatch() {
        while (!closed && active < concurrency && !ready.isEmpty()) {
            Lane lane = ready.removeFirst();
            lane.queued = false;
            if (lane.entries.isEmpty() || lanes.get(lane.key) != lane) {
                continue;
            }
            Entry<?> entry = lane.entries.getFirst();
            lane.running = true;
            entry.started = true;
            active++;
            workers.execute(() -> execute(lane, entry));
        }
    }

    private <T> void execute(Lane lane, Entry<T> entry) {
        T value = null;
        Throwable error = null;
        TraceUtils.startTrace(entry.traceId);
        if (entry.traceAttributes != null && !entry.traceAttributes.isEmpty()) {
            TraceUtils.setTraceAttributes(entry.traceAttributes);
        }
        try {
            if (entry.cancelled.get()) {
                throw new ChannelFailure(ChannelFailure.Kind.CANCELLED);
            }
            value = Objects.requireNonNull(entry.action.get())
                    .toCompletableFuture()
                    .join();
        } catch (Throwable failed) {
            error = failure(failed);
        } finally {
            TraceUtils.endTrace();
            synchronized (monitor) {
                entry.completed = true;
                lane.entries.remove(entry);
                pending--;
                active--;
                lane.running = false;
                if (lane.entries.isEmpty()) {
                    lanes.remove(lane.key, lane);
                } else {
                    enqueue(lane);
                }
                dispatch();
            }
        }
        entry.complete(value, error);
        signalSettlement();
    }

    private void cancel(Entry<?> entry) {
        boolean removed;
        synchronized (monitor) {
            if (entry.completed || !entry.cancelled.compareAndSet(false, true)) {
                return;
            }
            Lane lane = entry.lane;
            removed = !entry.started && lane.entries.remove(entry);
            if (removed) {
                entry.completed = true;
                pending--;
                if (lane.entries.isEmpty()) {
                    lanes.remove(lane.key, lane);
                    ready.remove(lane);
                    lane.queued = false;
                }
            }
        }
        try {
            entry.cancellation.run();
        } catch (RuntimeException ignored) {
            // Cancellation requests must not prevent the remaining owners from being notified.
        }
        if (removed) {
            entry.complete(null, new ChannelFailure(ChannelFailure.Kind.CANCELLED));
        }
        signalSettlement();
    }

    private void signalSettlement() {
        boolean settled;
        synchronized (monitor) {
            settled = closed && pending == 0;
        }
        if (settled) {
            settlement.complete(null);
        }
    }

    private final class Lane {
        final K key;
        final ArrayDeque<Entry<?>> entries = new ArrayDeque<>();
        boolean running;
        boolean queued;

        Lane(K key) {
            this.key = key;
        }
    }

    static final class Operation<T> {
        private final CompletableFuture<T> future;
        private final CompletableFuture<T> physicalResult;
        private final Runnable cancellation;

        private Operation(CompletableFuture<T> future, CompletableFuture<T> physicalResult, Runnable cancellation) {
            this.future = future;
            this.physicalResult = physicalResult;
            this.cancellation = cancellation;
        }

        CompletableFuture<T> future() {
            return future;
        }

        CompletionStage<T> physicalResult() {
            return physicalResult.minimalCompletionStage();
        }

        void requestCancellation() {
            cancellation.run();
        }
    }

    private final class Entry<T> {
        final Object owner;
        final Supplier<? extends CompletionStage<T>> action;
        final Runnable cancellation;
        final boolean cancelObservation;
        final CompletableFuture<T> result = new CompletableFuture<>();
        final CompletableFuture<T> physicalResult = new CompletableFuture<>();
        final Operation<T> operation = new Operation<>(result, physicalResult, () -> cancel(this));
        final AtomicBoolean cancelled = new AtomicBoolean();
        final String traceId = TraceUtils.getTraceId();
        final Map<String, String> traceAttributes = TraceUtils.getTraceAttributes();
        Lane lane;
        boolean started;
        boolean completed;

        Entry(
                Object owner,
                Supplier<? extends CompletionStage<T>> action,
                Runnable cancellation,
                boolean cancelObservation) {
            this.owner = owner;
            this.action = Objects.requireNonNull(action);
            this.cancellation = Objects.requireNonNull(cancellation);
            this.cancelObservation = cancelObservation;
            result.whenComplete((value, error) -> {
                if (result.isCancelled()) {
                    cancel(this);
                }
            });
        }

        void cancelOwned() {
            if (cancelObservation) {
                result.cancel(false);
            } else {
                operation.requestCancellation();
            }
        }

        void complete(T value, Throwable error) {
            // Publish accepted receipts before notifying the turn that physical delivery has settled.
            if (error == null) {
                result.complete(value);
                physicalResult.complete(value);
            } else {
                result.completeExceptionally(error);
                physicalResult.completeExceptionally(error);
            }
        }
    }
}
