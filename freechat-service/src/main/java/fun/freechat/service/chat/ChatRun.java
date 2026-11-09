package fun.freechat.service.chat;

import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.service.TokenStream;
import fun.freechat.service.chat.memory.MemoryBounds.PreparationDeferredException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;

/** In-process run lifecycle; deliberately not a durable run registry or replay log. */
@Slf4j
public final class ChatRun implements ChatRunHandle {
    private static final int PROJECTION_CAPACITY = 256;
    private final String id = UUID.randomUUID().toString();
    private final String sessionId;
    private final ArrayBlockingQueue<ChatRunEvent> projection = new ArrayBlockingQueue<>(PROJECTION_CAPACITY);
    private final CompletableFuture<Void> settlement = new CompletableFuture<>();
    private final CompletableFuture<Void> delivery = new CompletableFuture<>();
    private final Thread projector;
    private final ChatTask.Stream task;
    private long seq;
    private String phase;
    private boolean partial;
    private boolean cancelling;
    private boolean terminal;
    private boolean deliveryFailed;
    private boolean completing;
    private boolean responseFinalized;
    private String outcome;
    private Map<String, Object> result;
    private int activeTools;
    private Function<ChatResponse, Long> finalizer = ignored -> null;

    private ChatRun(String sessionId, Consumer<ChatRunEvent> observer, Function<ChatRun, TokenStream> builder) {
        this.sessionId = Objects.requireNonNull(sessionId);
        Objects.requireNonNull(observer);
        task = new ChatTask.Stream(() -> builder.apply(this), this::started, this::cancelling);
        projector = Thread.ofVirtual().name("chat-run-projection-" + id).unstarted(() -> project(observer));
        task.ready().whenComplete((stream, failure) -> {
            if (failure != null || stream == null) {
                fail(failure);
                return;
            }
            try {
                stream.onPartialResponse(this::messageDelta)
                        .onPartialThinking(thinking -> reasoningDelta(thinking.text()))
                        .onCompleteResponse(response -> complete(response, stream))
                        .onError(this::fail);
                if (!task.isCancelled()) stream.start();
            } catch (Throwable error) {
                fail(error);
                task.cancel();
            }
        });
        task.settled().whenComplete(this::settle);
    }

    public static ChatRunHandle start(
            String sessionId,
            Consumer<ChatRunEvent> observer,
            Function<ChatRun, TokenStream> builder,
            Consumer<ChatTask.Stream> submit) {
        ChatRun run = new ChatRun(sessionId, observer, builder);
        synchronized (run) {
            run.phase = "queued";
            run.emit("run.queued", Map.of("phase", "queued"));
        }
        run.projector.start();
        try {
            submit.accept(run.task);
        } catch (Throwable failure) {
            // Queue acquisition failed before ownership passed to a worker.
            run.task.future.completeExceptionally(failure);
            run.task.settlement.complete(null);
        }
        return run;
    }

    @Override
    public String runId() {
        return id;
    }

    @Override
    public CompletableFuture<Void> settled() {
        return settlement.copy();
    }

    @Override
    public CompletableFuture<Void> eventsDelivered() {
        return delivery.copy();
    }

    @Override
    public void cancel() {
        synchronized (this) {
            if (terminal || outcome != null || completing) return;
            cancelling();
        }
        task.cancel();
    }

    private synchronized void cancelling() {
        if (terminal || outcome != null || completing || cancelling) return;
        cancelling = true;
        status("cancelling");
    }

    private synchronized void started() {
        if (!live()) return;
        phase = "preparing";
        emit("run.started", Map.of("phase", phase));
    }

    /** Only main conversational model entry points call this, not preparation/extraction models. */
    public synchronized void waitingModel() {
        checkActive();
        if (activeTools == 0) status("waiting_model");
    }

    synchronized void finalizing() {
        if (live()) status("finalizing");
    }

    public synchronized void checkActive() {
        if (!live()) throw new IllegalStateException("Chat run is no longer active");
    }

    public synchronized void toolStarted(ToolExecutionRequest request) {
        checkActive();
        activeTools++;
        status("tool_execution");
        emit("tool.started", toolData(request));
    }

    public synchronized void toolCompleted(ToolExecutionRequest request, boolean error) {
        activeTools = Math.max(0, activeTools - 1);
        if (!live()) return;
        Map<String, Object> data = new HashMap<>(toolData(request));
        data.put("error", error);
        emit("tool.completed", data);
        // Stay in tool_execution until the actual next model call, including parallel/sequential tools.
    }

    private static Map<String, Object> toolData(ToolExecutionRequest request) {
        return Map.of("tool", Objects.toString(request.name(), ""), "tool_call_id", Objects.toString(request.id(), ""));
    }

    private synchronized void messageDelta(String delta) {
        if (!live() || delta == null || delta.isEmpty()) return;
        partial = true;
        if (activeTools == 0) status("responding");
        emit("message.delta", Map.of("delta", delta));
    }

    private synchronized void reasoningDelta(String delta) {
        if (!live() || delta == null || delta.isEmpty()) return;
        partial = true;
        if (activeTools == 0) status("thinking");
        emit("reasoning.delta", Map.of("delta", delta));
    }

    /** Legacy accounting only. MemoryTokenStream already owns durable IDs and cumulative usage. */
    public void onFinalResponse(Function<ChatResponse, Long> finalizer) {
        this.finalizer = Objects.requireNonNull(finalizer);
    }

    private void complete(ChatResponse response, TokenStream stream) {
        boolean accepted;
        synchronized (this) {
            if (terminal || responseFinalized) return;
            responseFinalized = true;
            accepted = live();
            if (accepted) {
                completing = true;
                status("finalizing");
            }
        }
        try {
            Long messageId = stream instanceof QueueAwareTokenStream queued ? queued.finalMessageId() : null;
            // A cancelled, non-cancellable provider may still persist a response and consume quota.
            Long legacyId = finalizer.apply(response);
            if (!accepted) return;
            if (messageId == null) messageId = legacyId;
            Map<String, Object> data = new HashMap<>();
            data.put("response", response);
            if (messageId != null) data.put("messageId", messageId);
            data.put("completed", true);
            data.put("partial", false);
            synchronized (this) {
                outcome = "run.completed";
                result = Map.copyOf(data);
            }
        } catch (Throwable failure) {
            synchronized (this) {
                completing = false;
                fail(failure);
            }
        }
    }

    private synchronized void fail(Throwable failure) {
        if (!live()) return;
        recordFailure(failure);
    }

    private void recordFailure(Throwable failure) {
        while ((failure instanceof java.util.concurrent.CompletionException
                        || failure instanceof java.util.concurrent.ExecutionException)
                && failure.getCause() != null) {
            failure = failure.getCause();
        }
        boolean deferred = failure instanceof PreparationDeferredException;
        Map<String, Object> data = new HashMap<>();
        data.put(
                "errorCode",
                deferred
                        ? "CHAT_MEMORY_PREPARING"
                        : failure instanceof ChatQueueRejectedException ? "CHAT_QUEUE_UNAVAILABLE" : "CHAT_RUN_FAILED");
        data.put(
                "message",
                deferred
                        ? "Conversation memory is still being prepared. Please retry your message."
                        : "The chat could not be completed. Please try again.");
        if (deferred) data.put("retryAfter", 1);
        data.put("completed", false);
        data.put("partial", partial);
        outcome = "run.failed";
        result = Map.copyOf(data);
    }

    private void settle(Void ignored, Throwable failure) {
        synchronized (this) {
            if (failure != null) {
                recordFailure(failure);
            } else if (outcome == null) {
                if (cancelling) {
                    outcome = "run.cancelled";
                    result = Map.of("completed", false, "partial", partial, "interrupted", true);
                } else {
                    fail(failure);
                }
            }
            terminal = true;
            emit(outcome, result);
        }
        if (failure == null) settlement.complete(null);
        else settlement.completeExceptionally(new IllegalStateException("Chat cleanup failed"));
    }

    private boolean live() {
        return !terminal && !cancelling && !completing && outcome == null;
    }

    private void status(String next) {
        if (Objects.equals(phase, next)) return;
        phase = next;
        emit("run.status", Map.of("phase", next));
    }

    // Called under the lifecycle monitor: sequence allocation and nonblocking enqueue are atomic.
    private void emit(String event, Map<String, Object> data) {
        if (deliveryFailed) return;
        if (!projection.offer(new ChatRunEvent(event, id, sessionId, ++seq, Instant.now(), data))) {
            deliveryFailure();
        }
    }

    private void project(Consumer<ChatRunEvent> observer) {
        try {
            while (true) {
                synchronized (this) {
                    if (deliveryFailed) return;
                }
                ChatRunEvent event = projection.take();
                observer.accept(event);
                if (event.event().equals("run.completed")
                        || event.event().equals("run.failed")
                        || event.event().equals("run.cancelled")) {
                    delivery.complete(null);
                    return;
                }
            }
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
            deliveryFailure();
        } catch (Throwable failure) {
            deliveryFailure();
        } finally {
            projection.clear();
        }
    }

    private synchronized void deliveryFailure() {
        if (deliveryFailed) return;
        deliveryFailed = true;
        projection.clear();
        projector.interrupt();
        // Delivery is not the model outcome: never turn an already successful model into run.failed.
        log.warn("Chat run event delivery stopped; runId={}", id);
        cancel();
        Thread.startVirtualThread(
                () -> delivery.completeExceptionally(new IllegalStateException("Chat run event delivery failed")));
    }
}
