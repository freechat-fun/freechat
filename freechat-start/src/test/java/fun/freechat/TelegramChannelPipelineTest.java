package fun.freechat;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.service.TokenStream;
import fun.freechat.channels.spi.*;
import fun.freechat.channels.telegram.DefaultTelegramChannel;
import fun.freechat.channels.telegram.TelegramChannelPlugin;
import fun.freechat.channels.telegram.command.HelpCommand;
import fun.freechat.channels.telegram.command.ResetCommand;
import fun.freechat.channels.telegram.command.StartCommand;
import fun.freechat.channels.telegram.handler.ChatBindingTelegramMessageHandler;
import fun.freechat.channels.telegram.handler.TelegramUpdateDispatcher;
import fun.freechat.service.channel.ChannelRegistry;
import fun.freechat.service.channel.ChannelRuntime;
import fun.freechat.service.chat.*;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import org.mockito.MockMakers;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.ResponseParameters;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

class TelegramChannelPipelineTest {
    @Test
    void sdkUpdateTraversesRegisteredPluginScheduledDeliveryAndHistoryBeforeReleasingTurn() throws Exception {
        try (var fixture = new Pipeline()) {
            var consumer = fixture.manager.registration().consumer();
            fixture.callbackThread = Thread.currentThread();
            consumer.accept(List.of(
                    TelegramChannelManagerTest.update(1, "question"), TelegramChannelManagerTest.update(2, null)));
            assertTrue(fixture.stream.started.await(5, TimeUnit.SECONDS));
            assertEquals(2, fixture.runtime.pendingReceives("telegram"));
            // A second, plain-text-only Spring plugin is usable while Telegram waits for its model.
            fixture.text
                    .instance
                    .get(5, TimeUnit.SECONDS)
                    .receive("room", "event", "echo")
                    .get(5, TimeUnit.SECONDS);
            assertEquals(List.of("echo"), fixture.text.sent);
            fixture.stream.partial.accept("Hello *world* ![photo](https://unused.invalid/generated.png)");
            assertTrue(fixture.partial.await(5, TimeUnit.SECONDS), "A scheduled partial must reach the raw SDK");
            fixture.stream.complete();
            fixture.stream.complete();
            fixture.stream.error.accept(new IllegalStateException("late callback"));
            await().atMost(Duration.ofSeconds(5))
                    .until(() -> fixture.history.stream()
                                    .filter(row -> "out".equals(row.direction()))
                                    .count()
                            == 2);
            assertEquals(List.of("typing", "placeholder", "partial", "final", "photo"), fixture.operations);
            assertTrue(fixture.history.contains(new History(42L, "out", "text", "Hello *world* ")));
            assertTrue(
                    fixture.history.contains(new History(43L, "out", "photo", "https://unused.invalid/generated.png")));
            verify(fixture.bindings, times(1))
                    .getOrCreate(anyString(), anyLong(), anyString(), any(), any(), any(), any(), any());
            assertEquals(2, fixture.runtime.pendingReceives("telegram"), "Model callback is not service settlement");
            fixture.stream.settlement.complete(null);
            await().atMost(Duration.ofSeconds(5)).until(() -> fixture.runtime.pendingReceives("telegram") == 0);
            assertTrue(fixture.history.contains(new History(2L, "in", "unsupported", null)));
            verify(fixture.chats).streamSendManaged(eq("chat"), any(), isNull());
            verify(fixture.manager.client).execute(any(SendPhoto.class));
            verify(fixture.manager.client).execute(any(SendChatAction.class));
            assertEquals(0, fixture.stream.cancelled.get());
            assertEquals(0, fixture.runtime.pendingSends("telegram"));
        }
    }

    @Test
    void finalRateLimitRetriesAfterProviderDelayWithoutAnotherTokenOrDuplicatePhoto() throws Exception {
        try (var fixture = new Pipeline()) {
            AtomicInteger attempts = new AtomicInteger();
            List<Long> times = new CopyOnWriteArrayList<>();
            TelegramApiRequestException limit = TelegramChannelManagerTest.stub(TelegramApiRequestException.class);
            ResponseParameters parameters = new ResponseParameters();
            parameters.setRetryAfter(1);
            when(limit.getParameters()).thenReturn(parameters);
            when(limit.getMessage()).thenReturn("fabricated private provider response");
            when(fixture.manager.client.execute(any(EditMessageText.class))).thenAnswer(call -> {
                fixture.checkDeliveryThread();
                EditMessageText edit = call.getArgument(0);
                assertEquals("Markdown", edit.getParseMode());
                times.add(System.nanoTime());
                if (attempts.incrementAndGet() == 1) {
                    throw limit;
                }
                fixture.operations.add("final");
                return Boolean.TRUE;
            });
            fixture.manager.registration().consumer().accept(List.of(TelegramChannelManagerTest.update(1, "question")));
            assertTrue(fixture.stream.started.await(5, TimeUnit.SECONDS));
            fixture.stream.partial.accept("answer ![image](https://unused.invalid/photo)");
            fixture.stream.complete();
            fixture.stream.settlement.complete(null);
            await().atMost(Duration.ofSeconds(5)).until(() -> fixture.runtime.pendingReceives("telegram") == 0);
            assertEquals(2, attempts.get());
            assertTrue(times.get(1) - times.get(0) >= TimeUnit.SECONDS.toNanos(1));
            assertEquals(List.of("typing", "placeholder", "final", "photo"), fixture.operations);
            verify(fixture.manager.client).execute(any(SendPhoto.class));
            assertEquals(3, fixture.history.size());
        }
    }

    @Test
    void deactivationCancelsManagedStreamButRetainsSlotUntilActualSettlement() throws Exception {
        try (var fixture = new Pipeline()) {
            var consumer = fixture.manager.registration().consumer();
            consumer.accept(List.of(TelegramChannelManagerTest.update(1, "question")));
            assertTrue(fixture.stream.started.await(5, TimeUnit.SECONDS));
            fixture.manager.manager.deactivate("backend");
            await().atMost(Duration.ofSeconds(5)).until(() -> fixture.stream.cancelled.get() > 0);
            assertEquals(1, fixture.runtime.pendingReceives("telegram"));
            consumer.accept(List.of(TelegramChannelManagerTest.update(2, "stale")));
            fixture.stream.partial.accept("late ![image](https://unused.invalid/stale)");
            fixture.stream.complete();
            fixture.stream.settlement.complete(null);
            await().atMost(Duration.ofSeconds(5)).until(() -> fixture.runtime.pendingReceives("telegram") == 0);
            verify(fixture.chats).streamSendManaged(eq("chat"), any(), isNull());
            verify(fixture.manager.client, never()).execute(any(SendPhoto.class));
            assertEquals(List.of(new History(1L, "in", "text", "question")), fixture.history);
        }
    }

    record History(Long message, String direction, String kind, String content) {}

    static final class ManagedStream implements ChatStreamHandle {
        final CompletableFuture<TokenStream> ready = new CompletableFuture<>();
        final CompletableFuture<Void> settlement = new CompletableFuture<>();
        final CountDownLatch started = new CountDownLatch(1);
        final AtomicInteger cancelled = new AtomicInteger();
        final TokenStream stream = mock(
                TokenStream.class, withSettings().mockMaker(MockMakers.SUBCLASS).defaultAnswer(RETURNS_SELF));
        volatile Consumer<String> partial;
        volatile Consumer<ChatResponse> terminal;
        volatile Consumer<Throwable> error;
        RuntimeException cancellationFailure;

        ManagedStream() {
            doAnswer(call -> {
                        partial = call.getArgument(0);
                        return stream;
                    })
                    .when(stream)
                    .onPartialResponse(any());
            doAnswer(call -> {
                        terminal = call.getArgument(0);
                        return stream;
                    })
                    .when(stream)
                    .onCompleteResponse(any());
            doAnswer(call -> {
                        error = call.getArgument(0);
                        return stream;
                    })
                    .when(stream)
                    .onError(any());
            doAnswer(call -> {
                        started.countDown();
                        return null;
                    })
                    .when(stream)
                    .start();
            ready.complete(stream);
        }

        public CompletableFuture<TokenStream> ready() {
            return ready;
        }

        public CompletableFuture<Void> settled() {
            return settlement;
        }

        public void cancel() {
            cancelled.incrementAndGet();
            if (cancellationFailure != null) {
                throw cancellationFailure;
            }
        }

        void complete() {
            terminal.accept(
                    ChatResponse.builder().aiMessage(AiMessage.from("complete")).build());
        }
    }

    static final class TextPlugin implements ChannelPlugin<String> {
        final CompletableFuture<ChannelInstance<String>> instance = new CompletableFuture<>();
        final List<String> sent = new CopyOnWriteArrayList<>();

        public String id() {
            return "text-only";
        }

        public ChannelTransport transport() {
            return (address, text) -> {
                sent.add(text.text());
                return new ChannelReceipt("1");
            };
        }

        public ChannelInboundHandler<String> inboundHandler() {
            return (envelope, turn) -> {
                assertFalse(turn.outbound().supports(ChannelMessageEditor.class));
                assertFalse(turn.outbound().supports(ChannelMediaTransport.class));
                assertFalse(turn.outbound().supports(ChannelStatusTransport.class));
                return turn.outbound()
                        .sendText(ChannelText.plain(envelope.payload()))
                        .thenApply(receipt -> null);
            };
        }

        public void start(ChannelRuntimeContext<String> runtime) {
            instance.complete(runtime.openInstance("account"));
        }

        public void stopReceiving() {
            if (instance.isDone()) instance.join().close();
        }

        public void close() {}
    }

    static final class Pipeline implements AutoCloseable {
        final TelegramChannelManagerTest.Fixture manager = new TelegramChannelManagerTest.Fixture();
        final TgChatBindingService bindings = TelegramChannelManagerTest.stub(TgChatBindingService.class);
        final TgMessageService messages = TelegramChannelManagerTest.stub(TgMessageService.class);
        final ChatService chats = TelegramChannelManagerTest.stub(ChatService.class);
        final ManagedStream stream = new ManagedStream();
        final TextPlugin text = new TextPlugin();
        final List<History> history = new CopyOnWriteArrayList<>();
        final List<String> operations = new CopyOnWriteArrayList<>();
        final CountDownLatch partial = new CountDownLatch(1);
        final AnnotationConfigApplicationContext spring = new AnnotationConfigApplicationContext();
        final ChannelRuntime runtime;
        volatile Thread callbackThread;

        Pipeline() throws Exception {
            when(bindings.getOrCreate(anyString(), anyLong(), anyString(), any(), any(), any(), any(), any()))
                    .thenReturn("chat");
            when(chats.streamSendManaged(eq("chat"), any(), isNull())).thenReturn(stream);
            when(messages.record(anyString(), anyLong(), any(), anyString(), anyString(), any(), any()))
                    .thenAnswer(call -> {
                        assertNotSame(callbackThread, Thread.currentThread());
                        history.add(new History(
                                call.getArgument(1), call.getArgument(3), call.getArgument(4), call.getArgument(5)));
                        return null;
                    });
            when(manager.client.execute(any(SendMessage.class))).thenAnswer(call -> {
                checkDeliveryThread();
                SendMessage send = call.getArgument(0);
                assertEquals("7", send.getChatId());
                assertEquals("…", send.getText());
                operations.add("placeholder");
                Message message = new Message();
                message.setMessageId(42);
                return message;
            });
            when(manager.client.execute(any(EditMessageText.class))).thenAnswer(call -> {
                checkDeliveryThread();
                EditMessageText edit = call.getArgument(0);
                assertEquals(42, edit.getMessageId());
                assertFalse(edit.getText().contains("!["));
                operations.add(edit.getParseMode() == null ? "partial" : "final");
                if (edit.getParseMode() == null) partial.countDown();
                else assertEquals("Markdown", edit.getParseMode());
                return Boolean.TRUE;
            });
            when(manager.client.execute(any(SendPhoto.class))).thenAnswer(call -> {
                checkDeliveryThread();
                operations.add("photo");
                Message message = new Message();
                message.setMessageId(43);
                return message;
            });
            when(manager.client.execute(any(SendChatAction.class))).thenAnswer(call -> {
                checkDeliveryThread();
                operations.add("typing");
                return Boolean.TRUE;
            });
            var dispatcher = new TelegramUpdateDispatcher(
                    List.of(
                            new StartCommand(bindings, TelegramChannelManagerTest.stub(ChatSessionService.class)),
                            new ResetCommand(bindings, chats),
                            new HelpCommand()),
                    new ChatBindingTelegramMessageHandler(bindings, messages, chats));
            var telegram =
                    new TelegramChannelPlugin(manager.manager, new DefaultTelegramChannel(manager.manager), dispatcher);
            spring.registerBean("telegramPlugin", TelegramChannelPlugin.class, () -> telegram);
            spring.registerBean("textPlugin", TextPlugin.class, () -> text);
            spring.registerBean(ChannelRegistry.class);
            spring.registerBean(ChannelRuntime.class);
            spring.refresh();
            runtime = spring.getBean(ChannelRuntime.class);
        }

        void checkDeliveryThread() {
            assertNotSame(callbackThread, Thread.currentThread(), "SDK callback must never perform delivery");
            assertFalse(Thread.currentThread().getName().startsWith("channel-timer-"));
        }

        public void close() {
            stream.settlement.complete(null);
            spring.close();
            manager.close();
        }
    }
}
