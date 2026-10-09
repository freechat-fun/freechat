package fun.freechat.service.channel;

import static org.junit.jupiter.api.Assertions.*;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.service.TokenStream;
import fun.freechat.channels.spi.*;
import fun.freechat.service.chat.ChatService;
import fun.freechat.service.chat.ChatStreamHandle;
import java.lang.reflect.Proxy;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
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
    void rejectedCancellationHookStillCancelsAnAdmittedModelTask() {
        Fixture fixture = new Fixture();
        fixture.rejectHook = true;
        CompletableFuture<Void> reply = fixture.reply(4000);
        assertEquals(1, fixture.cancellations.get());
        assertEquals(0, fixture.starts.get());
        assertFalse(reply.isDone());
        fixture.settled.complete(null);
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

    private static final class Fixture implements ChannelTurnContext, ChannelOutbound, ChatStreamHandle {
        final CompletableFuture<Void> settled = new CompletableFuture<>();
        final AtomicInteger cancellations = new AtomicInteger();
        final AtomicInteger starts = new AtomicInteger();
        final List<String> sent = new ArrayList<>();
        Consumer<String> partial;
        Consumer<ChatResponse> complete;
        Consumer<Throwable> error;
        Runnable cancel;
        boolean typingStopped;
        boolean deliveryFailure;
        boolean statusFailure;
        boolean rejectHook;
        final TokenStream stream = (TokenStream) Proxy.newProxyInstance(
                TokenStream.class.getClassLoader(), new Class<?>[] {TokenStream.class}, (proxy, method, args) -> {
                    switch (method.getName()) {
                        case "onPartialResponse" -> partial = consumer(args[0]);
                        case "onCompleteResponse" -> complete = consumer(args[0]);
                        case "onError" -> error = consumer(args[0]);
                        case "start" -> {
                            starts.incrementAndGet();
                            return null;
                        }
                        default -> throw new UnsupportedOperationException(method.getName());
                    }
                    return proxy;
                });

        @SuppressWarnings("unchecked")
        private static <T> Consumer<T> consumer(Object value) {
            return (Consumer<T>) value;
        }

        CompletableFuture<Void> reply(int limit) {
            ChatService service = (ChatService) Proxy.newProxyInstance(
                    ChatService.class.getClassLoader(), new Class<?>[] {ChatService.class}, (proxy, method, args) -> {
                        if (method.getName().equals("streamSendManaged")) {
                            assertEquals("authorized-chat", args[0]);
                            return this;
                        }
                        throw new UnsupportedOperationException(method.getName());
                    });
            return new ChannelChatBridge(service).reply("authorized-chat", UserMessage.from("hello"), this, limit);
        }

        public CompletableFuture<TokenStream> ready() {
            return CompletableFuture.completedFuture(stream);
        }

        public CompletableFuture<Void> settled() {
            return settled.copy();
        }

        public void cancel() {
            cancellations.incrementAndGet();
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
