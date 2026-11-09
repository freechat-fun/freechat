package fun.freechat.service.channel;

import static org.junit.jupiter.api.Assertions.*;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.response.ChatResponse;
import fun.freechat.channels.spi.*;
import fun.freechat.service.chat.ChatRunEvent;
import fun.freechat.service.chat.ChatRunHandle;
import fun.freechat.service.chat.ChatService;
import java.lang.reflect.Proxy;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;

class ChannelChatBridgeTest {
    @Test
    void textOnlyRepliesWaitForServiceSettlementAndSplitWithoutBreakingSurrogates() throws Exception {
        Fixture fixture = new Fixture();
        CompletableFuture<Void> reply = fixture.reply(3);
        fixture.partial.accept("ab" + Character.toString(0x1f600) + "cd");
        fixture.complete.accept(null);
        assertEquals(List.of("ab", Character.toString(0x1f600) + "c", "d"), fixture.sent);
        assertTrue(fixture.typingStopped);
        assertFalse(reply.isDone());
        fixture.settled.complete(null);
        reply.get(5, TimeUnit.SECONDS);
    }

    @Test
    void finalOnlyModelResponseIsDeliveredOnce() throws Exception {
        Fixture fixture = new Fixture();
        CompletableFuture<Void> reply = fixture.reply(4000);
        ChatResponse response =
                ChatResponse.builder().aiMessage(AiMessage.from("answer")).build();
        fixture.complete.accept(response);
        fixture.complete.accept(response);
        fixture.settled.complete(null);
        reply.get(5, TimeUnit.SECONDS);
        assertEquals(List.of("answer"), fixture.sent);
    }

    @Test
    void whitespaceDeltasDoNotDiscardTheFinalResponse() throws Exception {
        Fixture fixture = new Fixture();
        CompletableFuture<Void> reply = fixture.reply(4000);
        fixture.partial.accept(" \n\t");
        assertTrue(fixture.sent.isEmpty());
        fixture.complete.accept(
                ChatResponse.builder().aiMessage(AiMessage.from("answer")).build());
        fixture.settled.complete(null);
        reply.get(5, TimeUnit.SECONDS);
        assertEquals(List.of("answer"), fixture.sent);
    }

    @Test
    void eventDeliveryFailureCannotLeaveChannelWaitingAfterSettlement() throws Exception {
        Fixture fixture = new Fixture();
        CompletableFuture<Void> reply = fixture.reply(4000);
        fixture.settled.complete(null);
        assertFalse(reply.isDone());
        fixture.eventDelivery.completeExceptionally(new IllegalStateException("private delivery failure"));
        var failure = assertThrows(java.util.concurrent.ExecutionException.class, () -> reply.get(2, TimeUnit.SECONDS));
        assertInstanceOf(ChannelFailure.class, failure.getCause());
        assertTrue(fixture.typingStopped);
        assertTrue(fixture.sent.isEmpty());
    }

    @Test
    void providerFailuresAreSanitizedAndWaitForCancellationCleanup() throws Exception {
        Fixture fixture = new Fixture();
        CompletableFuture<Void> reply = fixture.reply(4000);
        fixture.error.accept(new IllegalStateException("private response body"));
        assertEquals(1, fixture.cancellations.get());
        assertTrue(fixture.typingStopped);
        assertFalse(reply.isDone());
        fixture.settled.complete(null);
        var failure = assertThrows(java.util.concurrent.ExecutionException.class, () -> reply.get(5, TimeUnit.SECONDS));
        assertInstanceOf(ChannelFailure.class, failure.getCause());
        assertFalse(failure.getCause().toString().contains("private response"));
    }

    @Test
    void responseBufferIsBoundedAndCancelledInsteadOfSilentlyTruncated() {
        Fixture fixture = new Fixture();
        CompletableFuture<Void> reply = fixture.reply(4000);
        fixture.partial.accept("x".repeat(65_537));
        assertEquals(1, fixture.cancellations.get());
        assertTrue(fixture.sent.isEmpty());
        assertFalse(reply.isDone());
        fixture.settled.complete(null);
        assertTrue(reply.isCompletedExceptionally());
    }

    @Test
    void turnCancellationRequestsTheManagedHandle() {
        Fixture fixture = new Fixture();
        fixture.reply(4000);
        fixture.cancel.run();
        assertEquals(1, fixture.cancellations.get());
        fixture.error.accept(new ChannelFailure(ChannelFailure.Kind.CANCELLED));
        fixture.settled.complete(null);
    }

    @Test
    void deliveryFailureNeverRestartsTheModelOrResends() {
        Fixture fixture = new Fixture();
        fixture.deliveryFailure = true;
        CompletableFuture<Void> reply = fixture.reply(4000);
        fixture.partial.accept("answer");
        fixture.complete.accept(null);
        fixture.settled.complete(null);
        assertTrue(reply.isCompletedExceptionally());
        assertEquals(1, fixture.starts.get());
        assertEquals(List.of("answer"), fixture.sent);
    }

    @Test
    void rejectedCancellationHookDoesNotAdmitAModelRun() {
        Fixture fixture = new Fixture();
        fixture.rejectHook = true;
        CompletableFuture<Void> reply = fixture.reply(4000);
        assertEquals(0, fixture.cancellations.get());
        assertEquals(0, fixture.starts.get());
        assertTrue(reply.isCompletedExceptionally());
    }

    @Test
    void statusFailureDoesNotDisableText() throws Exception {
        Fixture fixture = new Fixture();
        fixture.statusFailure = true;
        CompletableFuture<Void> reply = fixture.reply(4000);
        fixture.partial.accept("answer");
        fixture.complete.accept(null);
        fixture.settled.complete(null);
        reply.get(5, TimeUnit.SECONDS);
        assertEquals(List.of("answer"), fixture.sent);
    }

    @Test
    void settlementCannotReleaseLaneBeforeTerminalObserverAndActualDelivery() {
        Fixture fixture = new Fixture();
        fixture.pendingDelivery = new CompletableFuture<>();
        var reply = fixture.reply(4000);
        fixture.partial.accept("answer");
        fixture.settled.complete(null);
        assertFalse(reply.isDone());
        fixture.complete.accept(null);
        assertFalse(reply.isDone());
        fixture.pendingDelivery.complete(new ChannelReceipt("1"));
        reply.join();
        assertEquals(List.of("answer"), fixture.sent);
    }

    @Test
    void exceptionalSettlementDoesNotReleasePendingDelivery() {
        Fixture fixture = new Fixture();
        fixture.pendingDelivery = new CompletableFuture<>();
        var reply = fixture.reply(4000);
        fixture.partial.accept("answer");
        fixture.complete.accept(null); // Defensive: even an early terminal cannot bypass settlement.
        fixture.settled.completeExceptionally(new IllegalStateException("private cleanup body"));
        assertFalse(reply.isDone());
        fixture.pendingDelivery.complete(new ChannelReceipt("1"));
        var error = assertThrows(java.util.concurrent.CompletionException.class, reply::join);
        assertInstanceOf(ChannelFailure.class, error.getCause());
        assertNull(error.getCause().getCause());
    }

    @Test
    void cancellationBeforeHandleAssignmentIsRememberedEvenWhenCancelThrows() {
        Fixture fixture = new Fixture();
        fixture.cancelDuringStart = true;
        fixture.cancellationFailure = true;
        var reply = fixture.reply(4000);
        assertEquals(1, fixture.cancellations.get());
        assertFalse(reply.isDone());
        fixture.complete.accept(
                ChatResponse.builder().aiMessage(AiMessage.from("late")).build());
        fixture.settled.complete(null);
        assertTrue(reply.isCompletedExceptionally());
        assertTrue(fixture.sent.isEmpty());
    }

    @Test
    void setupFailureIsSanitizedWithoutWaitingForANonexistentHandle() {
        Fixture fixture = new Fixture();
        fixture.setupFailure = true;
        var reply = fixture.reply(4000);
        var error = assertThrows(java.util.concurrent.CompletionException.class, reply::join);
        assertInstanceOf(ChannelFailure.class, error.getCause());
        assertNull(error.getCause().getCause());
        assertTrue(fixture.sent.isEmpty());
    }

    @Test
    void statusesControlHeartbeatAndPrivateEventsAndEmptyRepliesStayOffChannel() {
        Fixture fixture = new Fixture();
        var reply = fixture.reply(4000);
        fixture.emit("run.started", Map.of("phase", "preparing"));
        fixture.emit("run.status", Map.of("phase", "thinking"));
        fixture.emit("reasoning.delta", Map.of("delta", "private"));
        fixture.emit("tool.started", Map.of("tool", "private", "tool_call_id", "id"));
        fixture.emit("tool.completed", Map.of("tool", "private", "tool_call_id", "id"));
        assertEquals(1, fixture.typingStarts);
        assertEquals(Duration.ofSeconds(4), fixture.typingInterval);
        assertFalse(fixture.typingStopped);
        assertTrue(fixture.sent.isEmpty());
        fixture.emit("run.status", Map.of("phase", "finalizing"));
        assertTrue(fixture.typingStopped);
        assertFalse(reply.isDone());
        fixture.settled.complete(null);
        fixture.complete.accept(null);
        reply.join();
        assertTrue(fixture.sent.isEmpty());
    }

    private static final class Fixture implements ChannelTurnContext, ChannelOutbound, ChatRunHandle {
        final CompletableFuture<Void> settled = new CompletableFuture<>();
        final CompletableFuture<Void> eventDelivery = new CompletableFuture<>();
        final AtomicInteger cancellations = new AtomicInteger();
        final AtomicInteger starts = new AtomicInteger();
        final List<String> sent = new ArrayList<>();
        Consumer<ChatRunEvent> observer;
        final Consumer<String> partial = delta -> emit("message.delta", Map.of("delta", delta));
        final Consumer<ChatResponse> complete =
                response -> emit("run.completed", response == null ? Map.of() : Map.of("response", response));
        final Consumer<Throwable> error = failure -> emit(
                failure instanceof ChannelFailure safe && safe.kind() == ChannelFailure.Kind.CANCELLED
                        ? "run.cancelled"
                        : "run.failed",
                Map.of("message", "Safe failure", "completed", false));
        Runnable cancel;
        boolean typingStopped;
        boolean deliveryFailure;
        boolean statusFailure;
        boolean rejectHook;
        boolean cancelDuringStart;
        boolean cancellationFailure;
        boolean setupFailure;
        int typingStarts;
        Duration typingInterval;
        CompletableFuture<ChannelReceipt> pendingDelivery;

        void emit(String name, Map<String, Object> data) {
            observer.accept(new ChatRunEvent(name, "run", "authorized-chat", 1, Instant.now(), data));
        }

        @SuppressWarnings("unchecked")
        private static <T> Consumer<T> consumer(Object value) {
            return (Consumer<T>) value;
        }

        CompletableFuture<Void> reply(int limit) {
            ChatService service = (ChatService) Proxy.newProxyInstance(
                    ChatService.class.getClassLoader(), new Class<?>[] {ChatService.class}, (proxy, method, args) -> {
                        if (method.getName().equals("startRun")) {
                            assertEquals("authorized-chat", args[0]);
                            starts.incrementAndGet();
                            observer = consumer(args[3]);
                            if (setupFailure) throw new IllegalStateException("private provider response");
                            emit("run.queued", Map.of("phase", "queued"));
                            if (cancelDuringStart) cancel.run();
                            return this;
                        }
                        throw new UnsupportedOperationException(method.getName());
                    });
            return new ChannelChatBridge(service).reply("authorized-chat", UserMessage.from("hello"), this, limit);
        }

        public String runId() {
            return "run";
        }

        public CompletableFuture<Void> settled() {
            return settled.copy();
        }

        public CompletableFuture<Void> eventsDelivered() {
            return eventDelivery.copy();
        }

        public void cancel() {
            cancellations.incrementAndGet();
            if (cancellationFailure) throw new IllegalStateException("private cancellation error");
        }

        public ChannelAddress address() {
            return new ChannelAddress("test", "account", "conversation");
        }

        public ChannelOutbound outbound() {
            return this;
        }

        public Instant deadline() {
            return Instant.now().plusSeconds(30);
        }

        public boolean isCancelled() {
            return false;
        }

        public void onCancel(Runnable action) {
            if (rejectHook) {
                throw new ChannelFailure(ChannelFailure.Kind.REJECTED);
            }
            cancel = action;
        }

        public ScheduledFuture<?> schedule(Runnable action, Duration delay) {
            throw new UnsupportedOperationException();
        }

        public AutoCloseable typing(Duration interval) {
            typingStarts++;
            typingInterval = interval;
            if (statusFailure) {
                throw new ChannelFailure(ChannelFailure.Kind.FAILED);
            }
            return () -> typingStopped = true;
        }

        public boolean supports(Class<?> capability) {
            return false;
        }

        public CompletableFuture<ChannelReceipt> sendText(ChannelText text) {
            sent.add(text.text());
            if (pendingDelivery != null) return pendingDelivery;
            return deliveryFailure
                    ? CompletableFuture.failedFuture(new ChannelFailure(ChannelFailure.Kind.AMBIGUOUS))
                    : CompletableFuture.completedFuture(new ChannelReceipt(Integer.toString(sent.size())));
        }

        public CompletableFuture<ChannelReceipt> sendMedia(ChannelMedia media) {
            throw new UnsupportedOperationException();
        }

        public CompletableFuture<Void> editText(String id, ChannelText text) {
            throw new UnsupportedOperationException();
        }

        public CompletableFuture<Void> sendTyping(BooleanSupplier needed) {
            throw new UnsupportedOperationException();
        }
    }
}
