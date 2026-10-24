package fun.freechat.service.connectors;

import static org.junit.jupiter.api.Assertions.*;

import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.output.TokenUsage;
import dev.langchain4j.service.TokenStream;
import fun.freechat.langchain4j.memory.chat.SystemAlwaysOnTopMessageWindowChatMemory;
import fun.freechat.langchain4j.memory.chat.TokenUsageChatMemoryStore;
import fun.freechat.service.chat.ChatExecution;
import fun.freechat.service.chat.ChatSession;
import fun.freechat.service.chat.MemoryUsage;
import fun.freechat.service.tools.ToolContext;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@Timeout(10)
class ConnectorCancellationTest {
    private static final String SECRET = "private-provider-payload-and-credential";
    private static final String CHAT = "chat";
    private static final TokenUsage USAGE = new TokenUsage(7, 3);
    private static final MemoryUsage INITIAL_USAGE = new MemoryUsage(2L, new TokenUsage(11, 5));
    private static final ToolSpecification CALCULATE = specification("calculate");

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void lateFinalCannotChangeSharedHistoryAfterCancellationAndNewTurnOrClear(boolean clear) throws Exception {
        Fixture f = new Fixture();
        Terminal old = new Terminal(f.stream());
        old.stream.start();
        close(old.stream);
        old.assertCancelled();

        if (clear) f.memory.clear();
        f.memory.add(UserMessage.from("New turn"));
        Terminal next = new Terminal(f.stream());
        next.stream.start();
        List<ChatMessage> history = List.copyOf(f.memory.messages());
        MemoryUsage usage = f.session.getMemoryUsage();

        await(f.provider.complete(0, answer(SECRET)));
        await(f.provider.events(0, handler -> {
            assertSanitized(assertThrows(RuntimeException.class, () -> handler.onPartialResponse(SECRET)));
            handler.onError(new IllegalStateException(SECRET));
        }));
        assertEquals(history, f.memory.messages());
        assertSame(usage, f.session.getMemoryUsage());
        old.assertCancelled();
        assertEquals(0, next.completions.get());
        assertTrue(next.errors.isEmpty());
        assertEquals(0, f.executed.get());
        assertEquals(1, f.provider.callbackFailures.size());
        f.provider.callbackFailures.forEach(ConnectorCancellationTest::assertSanitized);

        await(f.provider.complete(1, answer("Current answer")));
        assertEquals(AiMessage.from("Current answer"), f.memory.messages().getLast());
        assertEquals(1, next.completions.get());
        assertEquals(2, f.closes.get());
    }

    @Test
    void throwingStartThatAlreadyDispatchedAsyncWorkFencesItsLateCompletion() throws Exception {
        Fixture f = new Fixture();
        f.provider.throwAfterDispatch = true;
        Terminal terminal = new Terminal(f.stream());
        List<ChatMessage> history = List.copyOf(f.memory.messages());
        try {
            assertSanitized(assertThrows(RuntimeException.class, terminal.stream::start));
            await(f.provider.dispatched);
            terminal.assertCancelled();
            assertEquals(1, f.closes.get());
        } finally {
            f.provider.releaseDispatched.countDown();
        }
        await(f.provider.dispatchedCompletion);
        assertEquals(history, f.memory.messages());
        assertSame(INITIAL_USAGE, f.session.getMemoryUsage());
        assertEquals(1, f.provider.callbackFailures.size());
        f.provider.callbackFailures.forEach(ConnectorCancellationTest::assertSanitized);
        terminal.assertCancelled();
    }

    @Test
    void lateBuiltinToolRequestNeverReachesTheBoundExecutor() throws Exception {
        Fixture f = new Fixture();
        Terminal terminal = new Terminal(f.stream());
        terminal.stream.start();
        close(terminal.stream);
        List<ChatMessage> history = List.copyOf(f.memory.messages());

        await(f.provider.complete(0, toolRequest("calculate")));

        assertEquals(0, f.executed.get());
        assertEquals(0, f.toolAuthorizations.get());
        assertEquals(history, f.memory.messages());
        assertSame(INITIAL_USAGE, f.session.getMemoryUsage());
        assertEquals(1, f.provider.handlers.size());
        terminal.assertCancelled();
    }

    @Test
    void cancellationBetweenToolRequestMemoryWriteAndBuiltinDispatchRejectsExecutor() throws Exception {
        Fixture f = new Fixture();
        Terminal terminal = new Terminal(f.stream());
        terminal.stream.beforeToolExecution(ignored -> close(terminal.stream));
        terminal.stream.start();

        await(f.provider.complete(0, toolRequest("calculate")));

        assertEquals(0, f.executed.get(), "The builtin itself must be fenced, not just the preceding AI write");
        assertEquals(0, f.toolAuthorizations.get());
        assertEquals(2, f.memory.messages().size());
        assertEquals(1, f.provider.handlers.size());
        terminal.assertCancelled(1); // The intermediate response preceded cancellation.
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void cancellationDrainsAdmittedMemoryOrBuiltinWorkBeforeTerminalDelivery(boolean tool) throws Exception {
        Fixture f = new Fixture();
        CountDownLatch entered = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        Runnable admitted = () -> {
            entered.countDown();
            await(release);
            // A worker reentering close must not wait on the other closer draining this very action.
            f.invocations.getFirst().close();
        };
        if (tool) f.beforeTool = admitted;
        else f.memory.beforeAiWrite = admitted;
        Terminal terminal = new Terminal(f.stream());
        terminal.stream.start();
        CompletableFuture<Void> callback = f.provider.complete(0, tool ? toolRequest("calculate") : answer("Admitted"));
        await(entered);
        CountDownLatch closeReturned = new CountDownLatch(1);
        CountDownLatch repeatedStarted = new CountDownLatch(1);
        CountDownLatch repeatedReturned = new CountDownLatch(1);
        CompletableFuture<Void> cancellation = CompletableFuture.runAsync(() -> {
            close(terminal.stream);
            closeReturned.countDown();
        });
        CompletableFuture<Void> repeated = null;
        try {
            await(f.networkClosed);
            ConnectorInvocation invocation = f.invocations.getFirst();
            assertThrows(IllegalStateException.class, () -> invocation.guard(() -> fail("New work admitted")));
            assertThrows(IllegalStateException.class, invocation::check);
            repeated = CompletableFuture.runAsync(() -> {
                repeatedStarted.countDown();
                invocation.close();
                repeatedReturned.countDown();
            });
            await(repeatedStarted);
            assertFalse(closeReturned.await(100, TimeUnit.MILLISECONDS));
            assertFalse(repeatedReturned.await(100, TimeUnit.MILLISECONDS));
            assertTrue(terminal.errors.isEmpty(), "Queue coordination must not settle before the mutation drains");
            assertEquals(0, terminal.completions.get());
        } finally {
            release.countDown();
        }
        await(callback);
        await(cancellation);
        if (repeated != null) await(repeated);
        List<ChatMessage> history = List.copyOf(f.memory.messages());
        assertEquals(2, history.size());
        assertEquals(tool ? 1 : 0, f.executed.get());
        assertEquals(tool ? 1 : 0, f.toolAuthorizations.get());
        assertEquals(1, f.closes.get());
        terminal.assertCancelled(tool ? 1 : 0);
        await(f.provider.complete(0, answer(SECRET)));
        assertEquals(history, f.memory.messages());
        assertSame(INITIAL_USAGE, f.session.getMemoryUsage());
        terminal.assertCancelled(tool ? 1 : 0);
    }

    @Test
    void syncPendingUsageAndWindowWriteAreDrainedTogether() throws Exception {
        Fixture f = new Fixture();
        CountDownLatch entered = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        AtomicInteger writes = new AtomicInteger();
        var store = new dev.langchain4j.store.memory.chat.InMemoryChatMemoryStore();
        TokenUsageChatMemoryStore usageStore = new TokenUsageChatMemoryStore() {
            @Override
            public List<ChatMessage> getMessages(Object id) {
                return store.getMessages(id);
            }

            @Override
            public void updateMessages(Object id, List<ChatMessage> messages) {
                store.updateMessages(id, messages);
            }

            @Override
            public void deleteMessages(Object id) {
                store.deleteMessages(id);
            }

            @Override
            public Long addAiMessage(Object id, AiMessage message, TokenUsage usage) {
                assertEquals(USAGE, usage);
                entered.countDown();
                await(release);
                var messages = new java.util.ArrayList<>(store.getMessages(id));
                messages.add(message);
                store.updateMessages(id, messages);
                writes.incrementAndGet();
                return 42L;
            }
        };
        ChatMemory window = SystemAlwaysOnTopMessageWindowChatMemory.builder()
                .id(CHAT)
                .maxMessages(20)
                .chatMemoryStore(usageStore)
                .build();
        window.add(UserMessage.from("Prepared input"));
        ChatSession session = ChatSession.builder()
                .chatMemory(window)
                .chatModel(new ChatModel() {
                    @Override
                    public ChatResponse chat(ChatRequest request) {
                        return answer("Admitted");
                    }
                })
                .connectorBinder(f::bind)
                .memoryUsage(INITIAL_USAGE)
                .build();
        CompletableFuture<?> send = CompletableFuture.runAsync(() -> {
            try {
                new ChatExecution(session, CHAT).send(null, () -> {});
            } catch (IllegalStateException failure) {
                assertSanitized(failure);
            }
        });
        await(entered);
        CountDownLatch returned = new CountDownLatch(1);
        CompletableFuture<Void> cancellation = CompletableFuture.runAsync(() -> {
            f.invocations.getFirst().close();
            returned.countDown();
        });
        try {
            await(f.networkClosed);
            assertFalse(returned.await(100, TimeUnit.MILLISECONDS));
            assertSame(INITIAL_USAGE, session.getMemoryUsage());
        } finally {
            release.countDown();
        }
        await(cancellation);
        assertEquals(INITIAL_USAGE.add(1L, USAGE), session.getMemoryUsage());
        assertEquals(1, writes.get());
        assertEquals(AiMessage.from("Admitted"), window.messages().getLast());
        await(send);
    }

    @Test
    void concurrentCloseWaitsForConnectionShutdownAndAllowsReentrantClose() throws Exception {
        Fixture f = new Fixture();
        ConnectorInvocation invocation = f.bind();
        CountDownLatch release = new CountDownLatch(1);
        f.onClose = () -> {
            invocation.close();
            await(release);
        };
        CompletableFuture<Void> first = CompletableFuture.runAsync(invocation::close);
        await(f.networkClosed);
        CountDownLatch started = new CountDownLatch(1);
        CountDownLatch returned = new CountDownLatch(1);
        CompletableFuture<Void> second = CompletableFuture.runAsync(() -> {
            started.countDown();
            invocation.close();
            returned.countDown();
        });
        try {
            await(started);
            assertFalse(returned.await(100, TimeUnit.MILLISECONDS));
            assertThrows(IllegalStateException.class, () -> invocation.guard(() -> fail("Closed")));
        } finally {
            release.countDown();
        }
        await(first);
        await(second);
        assertEquals(1, f.closes.get());
    }

    @Test
    void transportReauthorizationDoesNotWaitForOperationGateAndCloseCanReenterIt() {
        Fixture f = new Fixture();
        ConnectorInvocation invocation = f.bind();
        invocation.guard(() -> {
            await(CompletableFuture.runAsync(invocation::check));
            invocation.close();
            invocation.close();
            return null;
        });
        invocation.close();
        assertEquals(1, f.closes.get());
        assertThrows(IllegalStateException.class, () -> invocation.guard(() -> fail("Closed")));
    }

    @Test
    void successfulAsyncBuiltinStillRunsItsOriginalAuthorizationAndExecutor() {
        Fixture f = new Fixture();
        Terminal terminal = new Terminal(f.stream());
        terminal.stream.start();
        await(f.provider.complete(0, toolRequest("calculate")));
        assertEquals(1, f.executed.get());
        assertEquals(1, f.toolAuthorizations.get());
        assertEquals(2, f.provider.handlers.size());
        await(f.provider.complete(1, answer("Finished")));
        assertEquals(1, terminal.completions.get());
        assertTrue(terminal.errors.isEmpty());
        assertEquals(AiMessage.from("Finished"), f.memory.messages().getLast());
        assertEquals(1, f.closes.get());
    }

    private static class Fixture {
        final RecordingMemory memory = new RecordingMemory();
        final AsyncProvider provider = new AsyncProvider();
        final List<ConnectorInvocation> invocations = new CopyOnWriteArrayList<>();
        final AtomicInteger closes = new AtomicInteger();
        final AtomicInteger executed = new AtomicInteger();
        final AtomicInteger toolAuthorizations = new AtomicInteger();
        final CountDownLatch networkClosed = new CountDownLatch(1);
        Runnable beforeTool = () -> {};
        Runnable onClose = () -> {};
        final ChatSession session = ChatSession.builder()
                .streamingChatModel(provider)
                .chatMemory(memory)
                .memoryUsage(INITIAL_USAGE)
                .connectorBinder(this::bind)
                .tools(Map.of(CALCULATE, (request, id) -> {
                    assertEquals(CHAT, id);
                    beforeTool.run();
                    executed.incrementAndGet();
                    return "2";
                }))
                .toolBinder(tools -> {
                    var bound = new java.util.LinkedHashMap<>(tools);
                    bound.replaceAll((specification, executor) -> (request, id) -> {
                        toolAuthorizations.incrementAndGet();
                        return executor.execute(request, id);
                    });
                    return bound;
                })
                .build();

        Fixture() {
            memory.add(UserMessage.from("Prepared input"));
        }

        TokenStream stream() {
            return new ChatExecution(session, CHAT).stream();
        }

        ConnectorInvocation bind() {
            var invocation = new ConnectorInvocation(
                    new ToolContext(CHAT, "viewer", "character", "backend", Duration.ofSeconds(8)), () -> {});
            invocation.add(
                    new ConnectorService.Resolved(
                            "1".repeat(32),
                            "creator",
                            "a".repeat(32),
                            new ConnectorConfiguration("test", "http", "https://example.com/mcp", 1000, Map.of()),
                            true),
                    (owner, configuration, lifetime, authorize) -> new ConnectorClientFactory.Connection() {
                        @Override
                        public List<ToolSpecification> listTools() {
                            authorize.run();
                            return List.of(specification("remote"));
                        }

                        @Override
                        public String execute(String name, String arguments) {
                            await(CompletableFuture.runAsync(authorize));
                            return "remote result";
                        }

                        @Override
                        public void close() {
                            closes.incrementAndGet();
                            networkClosed.countDown();
                            onClose.run();
                        }
                    });
            invocations.add(invocation);
            return invocation;
        }
    }

    private static class RecordingMemory implements ChatMemory {
        final ChatMemory delegate = MessageWindowChatMemory.withMaxMessages(20);
        Runnable beforeAiWrite = () -> {};

        @Override
        public Object id() {
            return CHAT;
        }

        @Override
        public void add(ChatMessage message) {
            if (message instanceof AiMessage) beforeAiWrite.run();
            delegate.add(message);
        }

        @Override
        public List<ChatMessage> messages() {
            return delegate.messages();
        }

        @Override
        public void clear() {
            delegate.clear();
        }
    }

    /** Real AiServices handlers are captured at the provider boundary and completed on another thread. */
    private static class AsyncProvider implements StreamingChatModel {
        final List<StreamingChatResponseHandler> handlers = new CopyOnWriteArrayList<>();
        final List<Throwable> callbackFailures = new CopyOnWriteArrayList<>();
        final CountDownLatch dispatched = new CountDownLatch(1);
        final CountDownLatch releaseDispatched = new CountDownLatch(1);
        boolean throwAfterDispatch;
        CompletableFuture<Void> dispatchedCompletion;

        @Override
        public void chat(ChatRequest request, StreamingChatResponseHandler handler) {
            handlers.add(handler);
            if (throwAfterDispatch) {
                dispatchedCompletion = CompletableFuture.runAsync(() -> {
                    dispatched.countDown();
                    await(releaseDispatched);
                    deliver(handler, answer(SECRET));
                });
                throw new IllegalStateException(SECRET);
            }
        }

        CompletableFuture<Void> complete(int index, ChatResponse response) {
            return events(index, handler -> deliver(handler, response));
        }

        CompletableFuture<Void> events(int index, Consumer<StreamingChatResponseHandler> event) {
            return CompletableFuture.runAsync(() -> event.accept(handlers.get(index)));
        }

        private void deliver(StreamingChatResponseHandler handler, ChatResponse response) {
            try {
                handler.onCompleteResponse(response);
            } catch (RuntimeException failure) {
                callbackFailures.add(failure);
                handler.onError(failure);
            }
        }
    }

    private static class Terminal {
        final TokenStream stream;
        final AtomicInteger completions = new AtomicInteger();
        final AtomicInteger nonterminal = new AtomicInteger();
        final List<Throwable> errors = new CopyOnWriteArrayList<>();

        Terminal(TokenStream stream) {
            this.stream = stream;
            stream.onPartialResponse(ignored -> nonterminal.incrementAndGet())
                    .onIntermediateResponse(ignored -> nonterminal.incrementAndGet())
                    .onToolExecuted(ignored -> nonterminal.incrementAndGet())
                    .onCompleteResponse(ignored -> completions.incrementAndGet())
                    .onError(errors::add);
        }

        void assertCancelled() {
            assertCancelled(0);
        }

        void assertCancelled(int admittedCallbacks) {
            assertEquals(0, completions.get());
            assertEquals(admittedCallbacks, nonterminal.get());
            assertEquals(1, errors.size());
            assertSanitized(errors.getFirst());
        }
    }

    private static ChatResponse answer(String text) {
        return ChatResponse.builder()
                .aiMessage(AiMessage.from(text))
                .tokenUsage(USAGE)
                .build();
    }

    private static ChatResponse toolRequest(String name) {
        return ChatResponse.builder()
                .aiMessage(AiMessage.from(ToolExecutionRequest.builder()
                        .id("call")
                        .name(name)
                        .arguments("{}")
                        .build()))
                .tokenUsage(USAGE)
                .build();
    }

    private static ToolSpecification specification(String name) {
        return ToolSpecification.builder()
                .name(name)
                .parameters(JsonObjectSchema.builder().build())
                .build();
    }

    private static void close(TokenStream stream) {
        try {
            ((AutoCloseable) stream).close();
        } catch (Exception failure) {
            throw new AssertionError(failure);
        }
    }

    private static void assertSanitized(Throwable failure) {
        assertInstanceOf(IllegalStateException.class, failure);
        assertFalse(failure.toString().contains(SECRET));
        assertNull(failure.getCause());
        assertEquals(0, failure.getSuppressed().length);
    }

    private static void await(CountDownLatch latch) {
        try {
            assertTrue(latch.await(3, TimeUnit.SECONDS), "Latch did not complete");
        } catch (InterruptedException failure) {
            Thread.currentThread().interrupt();
            throw new AssertionError(failure);
        }
    }

    private static void await(CompletableFuture<?> future) {
        try {
            future.get(3, TimeUnit.SECONDS);
        } catch (Exception failure) {
            throw new AssertionError(failure);
        }
    }
}
