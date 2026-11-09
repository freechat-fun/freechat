package fun.freechat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.PartialThinking;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.service.TokenStream;
import fun.freechat.service.chat.*;
import fun.freechat.service.chat.memory.MemoryBounds.PreparationDeferredException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockMakers;

@Timeout(15)
class ChatRunTest {
    @Test
    void observerInstalledBeforeAdmissionAndQueueCancellationNeverBuilds() throws Exception {
        try (Worker worker = new Worker(false)) {
            Events events = new Events();
            ChatRunHandle run = worker.start(events, ignored -> fail("Cancelled builder ran"));
            assertEquals("run.queued", events.next().event());
            run.cancel();
            assertEquals("cancelling", events.next().data().get("phase"));
            assertEquals("run.cancelled", events.next().event());
            run.settled().get(2, TimeUnit.SECONDS);
            assertTrue(worker.queue.isIdle());
            assertNotNull(java.util.UUID.fromString(run.runId()));
            events.assertOrdered(run);
        }
    }

    @Test
    void queuedCoversCrossNodeLockWaitAndPreparingStartsOnlyAfterAcquisition() throws Exception {
        try (Worker worker = new Worker(true)) {
            Events events = new Events();
            worker.lock.lock();
            ChatRunHandle run;
            try {
                run = worker.start(events, ignored -> {
                    throw new PreparationDeferredException("private input");
                });
                assertEquals("run.queued", events.next().event());
                assertNull(events.pending.poll(100, TimeUnit.MILLISECONDS));
            } finally {
                worker.lock.unlock();
            }
            assertEquals("run.started", events.next().event());
            ChatRunEvent failed = events.next();
            assertEquals("run.failed", failed.event());
            assertEquals("CHAT_MEMORY_PREPARING", failed.data().get("errorCode"));
            assertEquals(1, failed.data().get("retryAfter"));
            assertEquals(false, failed.data().get("partial"));
            assertFalse(failed.data().toString().contains("private input"));
            run.settled().get(2, TimeUnit.SECONDS);
            assertFalse(worker.lock.isLocked());
            events.assertOrdered(run);
        }
    }

    @Test
    void cancellationDuringPreparationRetainsCoordinationUntilCloseAndUnlock() throws Exception {
        CountDownLatch building = new CountDownLatch(1);
        CountDownLatch built = new CountDownLatch(1);
        CountDownLatch closing = new CountDownLatch(1);
        CountDownLatch closed = new CountDownLatch(1);
        try (Worker worker = new Worker(true)) {
            Raw raw = new Raw();
            raw.close = () -> {
                closing.countDown();
                await(closed);
            };
            Events events = new Events();
            ChatRunHandle run = worker.start(events, ignored -> {
                building.countDown();
                await(built);
                return raw.stream;
            });
            try {
                await(building);
                events.through("run.started");
                run.cancel();
                assertEquals("cancelling", events.next().data().get("phase"));
                assertFalse(run.settled().isDone());
                built.countDown();
                await(closing);
                assertTrue(worker.lock.isLocked());
                assertFalse(run.settled().isDone());
                assertTrue(events.pending.isEmpty());
                closed.countDown();
                assertEquals("run.cancelled", events.next().event());
                run.settled().get(2, TimeUnit.SECONDS);
                verify(raw.stream, never()).start();
                assertFalse(worker.lock.isLocked());
            } finally {
                built.countDown();
                closed.countDown();
            }
        }
    }

    @Test
    void realFrameworkToolsAndMultipleModelsReturnToWaitingThinkingAndResponding() throws Exception {
        BlockingQueue<StreamingChatResponseHandler> calls = new LinkedBlockingQueue<>();
        StreamingChatModel model = new StreamingChatModel() {
            @Override
            public void doChat(ChatRequest request, StreamingChatResponseHandler handler) {
                calls.add(handler);
            }
        };
        var memory = MessageWindowChatMemory.withMaxMessages(30);
        memory.add(UserMessage.from("input"));
        ChatSession session = ChatSession.builder()
                .streamingChatModel(model)
                .chatMemory(memory)
                .tools(Map.of(
                        ToolSpecification.builder().name("lookup").build(), (request, id) -> "private tool result"))
                .build();
        try (Worker worker = new Worker(true)) {
            Events events = new Events();
            ChatRunHandle run = worker.start(events, state -> new ChatExecution(session, "chat", state).stream());
            StreamingChatResponseHandler first = take(calls);
            events.through("run.status");
            assertEquals("waiting_model", events.seen.getLast().data().get("phase"));
            first.onPartialResponse("");
            first.onPartialThinking(new PartialThinking("consider"));
            assertEquals("thinking", events.next().data().get("phase"));
            assertEquals("reasoning.delta", events.next().event());
            first.onCompleteResponse(response(AiMessage.from(tool("one"), tool("two"))));
            StreamingChatResponseHandler second = take(calls);
            events.through("tool.completed");
            events.through("tool.completed");
            assertEquals("waiting_model", events.next().data().get("phase"));
            second.onPartialResponse("answer");
            assertEquals("responding", events.next().data().get("phase"));
            assertEquals("message.delta", events.next().event());
            second.onCompleteResponse(response(AiMessage.from("answer")));
            assertEquals("finalizing", events.next().data().get("phase"));
            ChatRunEvent complete = events.next();
            assertEquals("run.completed", complete.event());
            run.settled().get(2, TimeUnit.SECONDS);
            run.cancel();
            assertTrue(events.pending.isEmpty());
            assertEquals(
                    2,
                    events.seen.stream()
                            .filter(e -> e.event().equals("tool.started"))
                            .count());
            assertFalse(events.seen.toString().contains("private tool result"));
            assertFalse(events.seen.toString().contains("private arguments"));
            events.assertOrdered(run);
        }
    }

    @Test
    void parallelToolCompletionDoesNotPrematurelyLeaveToolExecution() throws Exception {
        try (Worker worker = new Worker(true)) {
            Raw raw = new Raw();
            Events events = new Events();
            AtomicReference<ChatRun> state = new AtomicReference<>();
            ChatRunHandle run = worker.start(events, current -> {
                state.set(current);
                return raw.stream;
            });
            await(raw.started);
            events.through("run.started");
            state.get().toolStarted(tool("one"));
            state.get().toolStarted(tool("two"));
            state.get().toolCompleted(tool("one"), false);
            raw.partial.accept("parallel text");
            state.get().toolCompleted(tool("two"), true);
            state.get().waitingModel();
            raw.complete.accept(response(AiMessage.from("done")));
            events.through("run.completed");
            List<String> phases = events.seen.stream()
                    .filter(e -> e.event().equals("run.status"))
                    .map(e -> (String) e.data().get("phase"))
                    .toList();
            assertEquals(List.of("tool_execution", "waiting_model", "finalizing"), phases);
            assertEquals(
                    List.of(false, true),
                    events.seen.stream()
                            .filter(e -> e.event().equals("tool.completed"))
                            .map(e -> e.data().get("error"))
                            .toList());
            run.settled().get(2, TimeUnit.SECONDS);
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void modelFailuresAreSanitizedAndPartialReflectsEmittedContent(boolean partial) throws Exception {
        try (Worker worker = new Worker(true)) {
            Raw raw = new Raw();
            Events events = new Events();
            ChatRunHandle run = worker.start(events, ignored -> raw.stream);
            await(raw.started);
            if (partial) raw.partial.accept("part");
            raw.error.accept(new IllegalArgumentException("PRIVATE_PROVIDER_BODY"));
            events.through("run.failed");
            var failure = events.seen.getLast();
            assertEquals(partial, failure.data().get("partial"));
            assertEquals("CHAT_RUN_FAILED", failure.data().get("errorCode"));
            assertFalse(failure.toString().contains("PRIVATE_PROVIDER_BODY"));
            run.settled().get(2, TimeUnit.SECONDS);
            raw.complete.accept(response(AiMessage.from("late")));
            run.cancel();
            assertTrue(events.pending.isEmpty());
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void observerFailureAndBoundedOverflowCancelWithoutBlockingModelCallbacks(boolean overflow) throws Exception {
        CountDownLatch observing = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        try (Worker worker = new Worker(true)) {
            Raw raw = new Raw();
            Consumer<ChatRunEvent> observer = event -> {
                if (!event.event().equals("message.delta")) return;
                observing.countDown();
                if (overflow) {
                    try {
                        release.await();
                    } catch (InterruptedException interrupted) {
                        Thread.currentThread().interrupt();
                    }
                } else throw new IllegalStateException("private observer failure");
            };
            ChatRunHandle run = worker.start(observer, ignored -> raw.stream);
            await(raw.started);
            raw.partial.accept("first");
            await(observing);
            if (overflow) {
                CompletableFuture<Void> callbacks = CompletableFuture.runAsync(() -> {
                    for (int i = 0; i < 1000; i++) raw.partial.accept("delta");
                });
                callbacks.get(2, TimeUnit.SECONDS);
            }
            run.settled().get(2, TimeUnit.SECONDS);
            assertThrows(
                    java.util.concurrent.ExecutionException.class,
                    () -> run.eventsDelivered().get(2, TimeUnit.SECONDS));
            assertEquals(1, raw.closes.get());
            assertFalse(worker.lock.isLocked());
        } finally {
            release.countDown();
        }
    }

    @Test
    void deliveryFailureAfterModelSuccessDoesNotReaccountOrDowngradeOutcome() throws Exception {
        try (Worker worker = new Worker(true)) {
            Raw raw = new Raw();
            AtomicInteger accounts = new AtomicInteger();
            CompletableFuture<ChatRunEvent> delivered = new CompletableFuture<>();
            ChatRunHandle run = worker.start(
                    event -> {
                        if (event.event().equals("run.completed")) {
                            delivered.complete(event);
                            throw new IllegalStateException("transport gone");
                        }
                    },
                    state -> {
                        state.onFinalResponse(response -> {
                            accounts.incrementAndGet();
                            return 73L;
                        });
                        return raw.stream;
                    });
            await(raw.started);
            raw.complete.accept(response(AiMessage.from("answer")));
            assertEquals(73L, delivered.get(2, TimeUnit.SECONDS).data().get("messageId"));
            run.settled().get(2, TimeUnit.SECONDS);
            run.cancel();
            raw.complete.accept(response(AiMessage.from("duplicate")));
            assertEquals(1, accounts.get());
        }
    }

    @Test
    void cleanupFailureCannotPublishACompletedRun() throws Exception {
        ReentrantLock failingRelease = new ReentrantLock() {
            @Override
            public void unlock() {
                super.unlock();
                throw new IllegalStateException("PRIVATE_RELEASE_FAILURE");
            }
        };
        try (Worker worker = new Worker(true, failingRelease)) {
            Raw raw = new Raw();
            Events events = new Events();
            ChatRunHandle run = worker.start(events, ignored -> raw.stream);
            await(raw.started);
            raw.complete.accept(response(AiMessage.from("answer")));
            events.through("run.failed");
            assertThrows(
                    java.util.concurrent.ExecutionException.class,
                    () -> run.settled().get(2, TimeUnit.SECONDS));
            run.eventsDelivered().get(2, TimeUnit.SECONDS);
            assertTrue(events.seen.stream().noneMatch(event -> event.event().equals("run.completed")));
            assertFalse(events.seen.toString().contains("PRIVATE_RELEASE_FAILURE"));
            assertFalse(worker.lock.isLocked());
        }
    }

    @Test
    void cancelledNonCancellableModelStillAccountsItsPersistedResponseOnce() throws Exception {
        BlockingQueue<StreamingChatResponseHandler> calls = new LinkedBlockingQueue<>();
        StreamingChatModel model = new StreamingChatModel() {
            @Override
            public void doChat(ChatRequest request, StreamingChatResponseHandler handler) {
                calls.add(handler);
            }
        };
        var memory = MessageWindowChatMemory.withMaxMessages(30);
        memory.add(UserMessage.from("input"));
        ChatSession session = ChatSession.builder()
                .streamingChatModel(model)
                .chatMemory(memory)
                .build();
        AtomicInteger accounts = new AtomicInteger();
        try (Worker worker = new Worker(true)) {
            Events events = new Events();
            ChatRunHandle run = worker.start(events, state -> {
                state.onFinalResponse(response -> {
                    assertEquals(3, response.tokenUsage().totalTokenCount());
                    accounts.incrementAndGet();
                    return 73L;
                });
                return new ChatExecution(session, "chat", state).stream();
            });
            StreamingChatResponseHandler handler = take(calls);
            run.cancel();
            assertFalse(run.settled().isDone());
            ChatResponse response = ChatResponse.builder()
                    .aiMessage(AiMessage.from("late answer"))
                    .tokenUsage(new dev.langchain4j.model.output.TokenUsage(2, 1))
                    .build();
            handler.onCompleteResponse(response);
            events.through("run.cancelled");
            run.settled().get(2, TimeUnit.SECONDS);
            run.eventsDelivered().get(2, TimeUnit.SECONDS);
            assertEquals(1, accounts.get());
            assertEquals(
                    1,
                    memory.messages().stream()
                            .filter(AiMessage.class::isInstance)
                            .count());
            assertTrue(events.seen.stream().noneMatch(event -> event.event().equals("run.completed")));
            handler.onCompleteResponse(response);
            assertEquals(1, accounts.get());
        }
    }

    @Test
    void serviceRunPreservesMemoryFinalIdAndAccountsCumulativeUsageOnlyOnce() throws Exception {
        try (var fixture = new ChatServiceMemoryTest.Fixture()) {
            CompletableFuture<Void> dispatched = new CompletableFuture<>();
            fixture.base.getAiServiceContext().streamingChatModel = new StreamingChatModel() {
                @Override
                public void chat(ChatRequest request, StreamingChatResponseHandler handler) {
                    fixture.provider.chat(request, handler);
                    dispatched.complete(null);
                }
            };
            Events events = new Events();
            ChatRunHandle run = fixture.service.startRun(fixture.id, ChatServiceMemoryTest.ORIGINAL, null, events);
            dispatched.get(3, TimeUnit.SECONDS);
            fixture.provider.complete(0, ChatServiceMemoryTest.response(AiMessage.from("stored answer")));
            events.through("run.completed");
            assertEquals(99L, events.seen.getLast().data().get("messageId"));
            run.settled().get(3, TimeUnit.SECONDS);
            fixture.provider.complete(0, ChatServiceMemoryTest.response(AiMessage.from("late duplicate")));
            assertEquals(1, fixture.usageRows().size());
            assertEquals(1, fixture.events.stream().filter("complete"::equals).count());
            fixture.assertTimersDisposed();
            fixture.idle();
        }
    }

    @Test
    void serviceRunPreparationFailureArrivesAfterAdmissionAndDoesNotCallProvider() throws Exception {
        try (var fixture = new ChatServiceMemoryTest.Fixture()) {
            CountDownLatch preparing = new CountDownLatch(1);
            CountDownLatch release = new CountDownLatch(1);
            when(fixture.sessions.get(fixture.context)).thenAnswer(call -> {
                preparing.countDown();
                await(release);
                throw new IllegalStateException("PRIVATE_PREPARATION");
            });
            Events events = new Events();
            ChatRunHandle run = fixture.service.startRun(fixture.id, ChatServiceMemoryTest.ORIGINAL, null, events);
            try {
                await(preparing);
                assertEquals("run.queued", events.next().event());
                assertEquals("run.started", events.next().event());
                assertFalse(run.settled().isDone());
                release.countDown();
                assertEquals("run.failed", events.next().event());
                assertFalse(events.seen.toString().contains("PRIVATE_PREPARATION"));
                run.settled().get(3, TimeUnit.SECONDS);
                assertTrue(fixture.provider.requests.isEmpty());
                fixture.idle();
            } finally {
                release.countDown();
            }
        }
    }

    private static ToolExecutionRequest tool(String id) {
        return ToolExecutionRequest.builder()
                .id(id)
                .name("lookup")
                .arguments("{\"private arguments\":true}")
                .build();
    }

    private static ChatResponse response(AiMessage message) {
        return ChatResponse.builder().aiMessage(message).build();
    }

    private static void await(CountDownLatch latch) {
        try {
            assertTrue(latch.await(5, TimeUnit.SECONDS));
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
            throw new AssertionError(interrupted);
        }
    }

    private static <T> T take(BlockingQueue<T> queue) throws InterruptedException {
        T value = queue.poll(3, TimeUnit.SECONDS);
        assertNotNull(value);
        return value;
    }

    private static final class Events implements Consumer<ChatRunEvent> {
        final BlockingQueue<ChatRunEvent> pending = new LinkedBlockingQueue<>();
        final List<ChatRunEvent> seen = new java.util.ArrayList<>();

        @Override
        public void accept(ChatRunEvent event) {
            pending.add(event);
        }

        ChatRunEvent next() throws InterruptedException {
            var event = take(pending);
            seen.add(event);
            return event;
        }

        void through(String name) throws InterruptedException {
            while (!next().event().equals(name)) {}
        }

        void assertOrdered(ChatRunHandle handle) {
            long seq = 0;
            for (ChatRunEvent event : seen) {
                assertEquals(++seq, event.seq());
                assertEquals(handle.runId(), event.runId());
                assertEquals("chat", event.sessionId());
                assertNotNull(event.timestamp());
            }
        }
    }

    private static final class Worker implements AutoCloseable {
        final ReentrantLock lock;
        final ChatTaskQueue queue;
        final ExecutorService executor = Executors.newSingleThreadExecutor();

        Worker(boolean start) {
            this(start, new ReentrantLock());
        }

        Worker(boolean start, ReentrantLock lock) {
            this.lock = lock;
            this.queue = new ChatTaskQueue("chat", lock);
            if (start) queue.startWorker(executor);
        }

        ChatRunHandle start(Consumer<ChatRunEvent> observer, Function<ChatRun, TokenStream> builder) {
            return ChatRun.start("chat", observer, builder, queue::submit);
        }

        @Override
        public void close() throws InterruptedException {
            queue.drain(0);
            executor.shutdownNow();
            assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
        }
    }

    private static final class Raw {
        final TokenStream stream = mock(
                TokenStream.class,
                withSettings()
                        .mockMaker(MockMakers.SUBCLASS)
                        .extraInterfaces(AutoCloseable.class)
                        .defaultAnswer(RETURNS_SELF));
        final CountDownLatch started = new CountDownLatch(1);
        final AtomicInteger closes = new AtomicInteger();
        Consumer<String> partial;
        Consumer<ChatResponse> complete;
        Consumer<Throwable> error;
        Runnable close = () -> {};

        Raw() throws Exception {
            doAnswer(call -> {
                        partial = call.getArgument(0);
                        return stream;
                    })
                    .when(stream)
                    .onPartialResponse(any());
            doAnswer(call -> {
                        complete = call.getArgument(0);
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
            doAnswer(call -> {
                        closes.incrementAndGet();
                        close.run();
                        return null;
                    })
                    .when((AutoCloseable) stream)
                    .close();
        }
    }
}
