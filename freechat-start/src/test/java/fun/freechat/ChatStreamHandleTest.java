package fun.freechat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.service.AiServiceTokenStream;
import dev.langchain4j.service.TokenStream;
import fun.freechat.service.chat.ChatExecution;
import fun.freechat.service.chat.ChatQueueRejectedException;
import fun.freechat.service.chat.ChatSession;
import fun.freechat.service.chat.ChatStreamHandle;
import fun.freechat.service.chat.ChatTask;
import fun.freechat.service.chat.ChatTaskQueue;
import fun.freechat.service.chat.impl.ChatServiceImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockMakers;
import org.springframework.test.util.ReflectionTestUtils;

@Timeout(20)
class ChatStreamHandleTest {
    @Test
    void nullMessageReturnsAnAlreadySettledHandleWithoutQueueAdmission() throws Exception {
        ChatServiceImpl service = new ChatServiceImpl();
        ChatStreamHandle handle = service.streamSendManaged("unused", null, null);
        assertNull(get(handle.ready()));
        assertNull(get(handle.settled()));
        handle.cancel();
        assertNull(service.streamSend("unused", null, null));
    }

    @Test
    void queuedCancellationRemovesWorkAndDoesNotCancelTheActiveTask() throws Exception {
        try (Worker worker = new Worker(false)) {
            RawStream raw = new RawStream();
            ChatTask.Stream active = worker.submit(() -> raw.stream);
            TokenStream stream = get(active.ready());
            ChatTask.Stream pending = worker.submit(() -> fail("Cancelled builder ran"));
            pending.cancel();
            pending.cancel();
            rejected(pending);
            assertNull(get(pending.settled()));
            assertFalse(active.settled().isDone());
            assertFalse(worker.queue.isIdle());
            stream.start();
            raw.complete.get().accept(response());
            get(active.settled());
            assertEquals(
                    "next",
                    get(worker.queue.submit(new ChatTask.Sync<>(() -> "next")).future()));
        }
    }

    @Test
    void cancellationBetweenDequeueAndRegistrationSkipsCoordinationAndBuilder() throws Exception {
        CountDownLatch dequeued = new CountDownLatch(1);
        CountDownLatch register = new CountDownLatch(1);
        try (Worker worker = new Worker(false, false)) {
            LinkedBlockingQueue<ChatTask<?>> pending = new LinkedBlockingQueue<>() {
                @Override
                public ChatTask<?> poll(long timeout, TimeUnit unit) throws InterruptedException {
                    ChatTask<?> task = super.poll(timeout, unit);
                    if (task != null) {
                        dequeued.countDown();
                        gate(register);
                    }
                    return task;
                }
            };
            ReflectionTestUtils.setField(worker.queue, "taskQueue", pending);
            ChatTask.Stream task = worker.submit(() -> fail("Cancelled builder ran"));
            worker.queue.startWorker(worker.executor);
            try {
                await(dequeued);
                task.cancel();
                assertFalse(task.settled().isDone(), "Dequeued task still belongs to the worker");
            } finally {
                register.countDown();
            }
            rejected(task);
            get(task.settled());
            assertNull(worker.lock.worker.get());
            assertTrue(worker.queue.isIdle());
        }
    }

    @Test
    void cancellationRacingSubmissionCannotOrphanTheTask() throws Exception {
        CountDownLatch adding = new CountDownLatch(1);
        CountDownLatch insert = new CountDownLatch(1);
        try (Worker worker = new Worker(false, false);
                ExecutorService caller = Executors.newSingleThreadExecutor()) {
            LinkedBlockingQueue<ChatTask<?>> pending = new LinkedBlockingQueue<>() {
                @Override
                public boolean add(ChatTask<?> task) {
                    adding.countDown();
                    gate(insert);
                    return super.add(task);
                }
            };
            ReflectionTestUtils.setField(worker.queue, "taskQueue", pending);
            ChatTask.Stream task = new ChatTask.Stream(() -> fail("Cancelled builder ran"));
            Future<?> submission = caller.submit(() -> worker.queue.submit(task));
            try {
                await(adding);
                task.cancel();
            } finally {
                insert.countDown();
            }
            get(submission);
            rejected(task);
            get(task.settled());
            assertTrue(worker.queue.isIdle());
        }
    }

    @Test
    void cancellationDuringConstructionBarsEvenSynchronousReadyStartAndWaitsForCloseAndUnlock() throws Exception {
        CountDownLatch building = new CountDownLatch(1);
        CountDownLatch finishBuilder = new CountDownLatch(1);
        CountDownLatch closing = new CountDownLatch(1);
        CountDownLatch finishClose = new CountDownLatch(1);
        try (Worker worker = new Worker(true)) {
            RawStream raw = new RawStream();
            raw.onClose = () -> {
                assertSame(worker.lock.worker.get(), Thread.currentThread());
                assertTrue(worker.lock.isHeldByCurrentThread());
                assertFalse(Thread.currentThread().isInterrupted());
                closing.countDown();
                gate(finishClose);
            };
            ChatTask.Stream task = worker.submit(() -> {
                building.countDown();
                gate(finishBuilder);
                return raw.stream;
            });
            CompletableFuture<Void> startAttempt =
                    task.ready().thenAccept(stream -> assertThrows(IllegalStateException.class, stream::start));
            try {
                await(building);
                task.cancel();
                assertFalse(task.settled().isDone());
                finishBuilder.countDown();
                get(startAttempt);
                await(closing);
                ChatTask<String> next = worker.queue.submit(new ChatTask.Sync<>(() -> "next"));
                assertFalse(task.settled().isDone());
                assertFalse(next.future().isDone());
                assertFalse(worker.queue.isIdle());
                // Neither cancellation nor mutation of a caller's observation releases the actual task.
                task.settled().cancel(false);
                task.ready().cancel(false);
                task.cancel();
                finishClose.countDown();
                await(worker.lock.unlocking);
                assertFalse(task.settled().isDone(), "Unlock is still in progress");
                assertFalse(next.future().isDone());
                worker.lock.finishUnlock.countDown();
                get(task.settled());
                assertEquals("next", get(next.future()));
                verify(raw.stream, never()).start();
                assertEquals(1, raw.closes.get());
                AtomicInteger errors = new AtomicInteger();
                get(task.ready()).onError(ignored -> errors.incrementAndGet());
                raw.error.get().accept(new IllegalStateException("late provider error"));
                raw.complete.get().accept(response());
                assertEquals(1, errors.get());
            } finally {
                finishBuilder.countDown();
                finishClose.countDown();
                worker.lock.finishUnlock.countDown();
            }
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void activeCancellationIsRequestOnlyAndKeepsSlotUntilCleanupFinishes(boolean started) throws Exception {
        CountDownLatch closing = new CountDownLatch(1);
        CountDownLatch finishClose = new CountDownLatch(1);
        try (Worker worker = new Worker(false)) {
            RawStream raw = new RawStream();
            raw.onClose = () -> {
                assertSame(worker.lock.worker.get(), Thread.currentThread());
                assertFalse(Thread.currentThread().isInterrupted());
                closing.countDown();
                gate(finishClose);
            };
            ChatTask.Stream task = worker.submit(() -> raw.stream);
            TokenStream stream = get(task.ready());
            if (started) {
                stream.start();
            }
            try {
                task.cancel();
                await(closing);
                task.cancel(); // Cannot block on or interrupt the worker's close.
                assertThrows(IllegalStateException.class, stream::start);
                assertFalse(task.settled().isDone());
                assertTrue(worker.lock.isLocked());
                finishClose.countDown();
                get(task.settled());
                assertFalse(worker.lock.isLocked());
                assertTrue(worker.queue.isIdle());
                assertEquals(1, raw.closes.get());
                verify(raw.stream, times(started ? 1 : 0)).start();
            } finally {
                finishClose.countDown();
            }
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void terminalCallbackAndConcurrentCancellationDoNotSettleBeforeCallbackAndClose(boolean error) throws Exception {
        CountDownLatch callbackEntered = new CountDownLatch(1);
        CountDownLatch finishCallback = new CountDownLatch(1);
        CountDownLatch closing = new CountDownLatch(1);
        CountDownLatch finishClose = new CountDownLatch(1);
        try (Worker worker = new Worker(false);
                ExecutorService callbackExecutor = Executors.newSingleThreadExecutor()) {
            RawStream raw = new RawStream();
            raw.onClose = () -> {
                closing.countDown();
                gate(finishClose);
            };
            ChatTask.Stream task = worker.submit(() -> raw.stream);
            TokenStream stream = get(task.ready());
            Consumer<Object> callback = ignored -> {
                callbackEntered.countDown();
                gate(finishCallback);
            };
            stream.onCompleteResponse(callback::accept)
                    .onError(callback::accept)
                    .start();
            Future<?> terminal = callbackExecutor.submit(() -> {
                if (error) {
                    raw.error.get().accept(new IllegalStateException("synthetic failure"));
                } else {
                    raw.complete.get().accept(response());
                }
            });
            try {
                await(callbackEntered);
                task.cancel();
                await(closing);
                assertFalse(task.settled().isDone());
                finishClose.countDown();
                assertFalse(task.settled().isDone(), "The terminal callback is still executing");
                finishCallback.countDown();
                get(terminal);
                get(task.settled());
                assertEquals(1, raw.closes.get());
            } finally {
                finishClose.countDown();
                finishCallback.countDown();
            }
        }
    }

    @Test
    void callerCloseRacingCancellationCannotReleaseWorkerBeforeThatCloseReturns() throws Exception {
        CountDownLatch closing = new CountDownLatch(1);
        CountDownLatch finishClose = new CountDownLatch(1);
        try (Worker worker = new Worker(false);
                ExecutorService caller = Executors.newSingleThreadExecutor()) {
            RawStream raw = new RawStream();
            raw.onClose = () -> {
                closing.countDown();
                gate(finishClose);
            };
            ChatTask.Stream task = worker.submit(() -> raw.stream);
            AutoCloseable stream = (AutoCloseable) get(task.ready());
            Future<?> close = caller.submit(() -> {
                stream.close();
                return null;
            });
            try {
                await(closing);
                task.cancel();
                assertFalse(task.settled().isDone());
                assertTrue(worker.lock.isLocked());
                finishClose.countDown();
                get(close);
                get(task.settled());
                assertEquals(1, raw.closes.get());
            } finally {
                finishClose.countDown();
            }
        }
    }

    @Test
    void cancelledCoordinationWaiterSkipsBuilderButSettlesOnlyAfterEventualUnlock() throws Exception {
        try (Worker worker = new Worker(false)) {
            worker.lock.lock();
            ChatTask.Stream task;
            try {
                task = worker.submit(() -> fail("Cancelled lock waiter ran"));
                await(worker.lock.attempted);
                task.cancel();
                assertFalse(task.settled().isDone());
                assertTrue(worker.lock.isHeldByCurrentThread());
            } finally {
                worker.lock.unlock();
            }
            rejected(task);
            get(task.settled());
            assertFalse(worker.lock.isLocked());
            assertEquals(
                    "next",
                    get(worker.queue.submit(new ChatTask.Sync<>(() -> "next")).future()));
        }
    }

    @Test
    void drainedAndRejectedTasksSettleWithoutAnyWorker() throws Exception {
        ChatTaskQueue queue = new ChatTaskQueue("unstarted", new ReentrantLock());
        ChatTask.Stream pending = new ChatTask.Stream(() -> fail("Drained builder ran"));
        queue.submit(pending);
        queue.drain(0);
        rejected(pending);
        get(pending.settled());
        ChatTask.Stream late = new ChatTask.Stream(() -> fail("Rejected builder ran"));
        queue.submit(late);
        rejected(late);
        get(late.settled());
        assertTrue(queue.isIdle());
    }

    @Test
    void nullAndFailedBuildersSettleAfterUnlockAndSanitizeFailures() throws Exception {
        try (Worker worker = new Worker(false)) {
            ChatTask.Stream empty = worker.submit(() -> null);
            assertNull(get(empty.ready()));
            get(empty.settled());
            ChatTask.Stream failed = worker.submit(() -> {
                throw new IllegalArgumentException("private provider body");
            });
            Throwable failure = assertThrows(ExecutionException.class, () -> get(failed.ready()))
                    .getCause();
            assertEquals("Chat stream failed", failure.getMessage());
            assertNull(failure.getCause());
            get(failed.settled());
            assertFalse(worker.lock.isLocked());
            assertTrue(worker.queue.isIdle());
        }
    }

    @Test
    void interruptedHttpWaitRemovesQueuedTaskWithoutOpeningMemory() throws Exception {
        CompletableFuture<ChatTask<?>> admitted = new CompletableFuture<>();
        ChatTaskQueue queue = new ChatTaskQueue("queued-http", new ReentrantLock()) {
            @Override
            public <T> ChatTask<T> submit(ChatTask<T> task) {
                ChatTask<T> result = super.submit(task);
                admitted.complete(task);
                return result;
            }
        };
        AtomicReference<Thread> callerThread = new AtomicReference<>();
        try (var fixture = new ChatServiceMemoryTest.Fixture(queue);
                ExecutorService caller = Executors.newSingleThreadExecutor()) {
            Future<Boolean> result = caller.submit(() -> {
                callerThread.set(Thread.currentThread());
                assertNull(fixture.service.streamSend(fixture.id, ChatServiceMemoryTest.ORIGINAL, null));
                return Thread.currentThread().isInterrupted();
            });
            ChatTask<?> task = get(admitted);
            callerThread.get().interrupt();
            assertTrue(get(result));
            get(task.settled());
            assertTrue(queue.isIdle());
            verify(fixture.contexts, never()).get(fixture.id);
            assertTrue(fixture.bindings.isEmpty());
            assertTrue(fixture.provider.requests.isEmpty());
        } finally {
            queue.drain(0);
        }
    }

    @Test
    void interruptedHttpWaitCancelsOwningTaskDuringRealMemoryConstruction() throws Exception {
        CountDownLatch building = new CountDownLatch(1);
        CountDownLatch finishBuilder = new CountDownLatch(1);
        AtomicReference<ChatTask<?>> task = new AtomicReference<>();
        AtomicReference<Thread> callerThread = new AtomicReference<>();
        try (var fixture = new ChatServiceMemoryTest.Fixture();
                ExecutorService caller = Executors.newSingleThreadExecutor()) {
            when(fixture.sessions.get(fixture.context)).thenAnswer(call -> {
                Object active = ReflectionTestUtils.getField(fixture.queue, "activeTask");
                task.set((ChatTask<?>) ReflectionTestUtils.getField(active, "task"));
                building.countDown();
                gate(finishBuilder);
                assertFalse(Thread.currentThread().isInterrupted());
                return fixture.base;
            });
            Future<Boolean> result = caller.submit(() -> {
                callerThread.set(Thread.currentThread());
                assertNull(fixture.service.streamSend(fixture.id, ChatServiceMemoryTest.ORIGINAL, null));
                return Thread.currentThread().isInterrupted();
            });
            try {
                await(building);
                callerThread.get().interrupt();
                assertTrue(get(result), "HTTP caller retains its interrupt and returns null");
                assertFalse(task.get().settled().isDone());
                finishBuilder.countDown();
                get(task.get().settled());
                assertTrue(fixture.provider.requests.isEmpty());
                assertEquals(1, fixture.aborts.get());
                fixture.assertTimersDisposed();
                fixture.idle();
            } finally {
                finishBuilder.countDown();
            }
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"cancel-complete", "cancel-error", "close", "interrupt", "timeout", "public-future"})
    void noncancellableStreamRetainsCoordinationUntilNaturalCallbackCleanup(String mode) throws Exception {
        CountDownLatch expire = new CountDownLatch(1);
        CountDownLatch callbackEntered = new CountDownLatch(1);
        CountDownLatch finishCallback = new CountDownLatch(1);
        AtomicInteger callbacks = new AtomicInteger();
        try (Worker worker = new Worker(false);
                ExecutorService provider = Executors.newSingleThreadExecutor()) {
            RawStream raw = new RawStream(false);
            ChatTask.Stream task = new ChatTask.Stream(() -> raw.stream);
            if (mode.equals("timeout")) {
                ReflectionTestUtils.setField(task, "cleanupRequested", new CountDownLatch(1) {
                    @Override
                    public boolean await(long timeout, TimeUnit unit) {
                        gate(expire);
                        return false;
                    }
                });
            }
            worker.queue.submit(task);
            TokenStream stream = get(task.ready());
            Consumer<Object> cleanup = ignored -> {
                callbacks.incrementAndGet();
                callbackEntered.countDown();
                gate(finishCallback);
            };
            stream.onCompleteResponse(cleanup::accept).onError(cleanup::accept).start();
            ChatTask<String> next = worker.queue.submit(new ChatTask.Sync<>(() -> "next"));
            try {
                switch (mode) {
                    case "close" -> ((AutoCloseable) stream).close();
                    case "interrupt" -> worker.lock.worker.get().interrupt();
                    case "timeout" -> expire.countDown();
                    case "public-future" -> {
                        task.ready().cancel(false);
                        task.settled().cancel(false);
                    }
                    default -> task.cancel();
                }
                assertBlocked(task.settled());
                assertBlocked(next.future());
                assertFalse(worker.queue.isIdle());
                assertTrue(worker.lock.isLocked());
                assertEquals(0, callbacks.get(), "Cancellation is not a natural terminal callback");
                Future<?> terminal = provider.submit(() -> {
                    if (mode.equals("cancel-error")) {
                        raw.error.get().accept(new IllegalStateException("synthetic error"));
                    } else {
                        raw.complete.get().accept(response());
                    }
                });
                await(callbackEntered);
                assertBlocked(task.settled());
                assertBlocked(next.future());
                finishCallback.countDown();
                get(terminal);
                get(task.settled());
                if (mode.equals("interrupt")) {
                    assertThrows(ExecutionException.class, () -> get(next.future()));
                    assertFalse(worker.lock.isLocked());
                } else {
                    assertEquals("next", get(next.future()));
                }
                raw.complete.get().accept(response());
                raw.error.get().accept(new IllegalStateException("late error"));
                assertEquals(1, callbacks.get());
                assertEquals(0, raw.closes.get());
            } finally {
                expire.countDown();
                finishCallback.countDown();
                raw.complete.get().accept(response());
            }
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void noncancellableCancellationBeforeStartSettlesWithoutProviderWork(boolean duringConstruction) throws Exception {
        CountDownLatch building = new CountDownLatch(1);
        CountDownLatch finishBuilder = new CountDownLatch(duringConstruction ? 1 : 0);
        try (Worker worker = new Worker(false)) {
            RawStream raw = new RawStream(false);
            ChatTask.Stream task = worker.submit(() -> {
                building.countDown();
                gate(finishBuilder);
                return raw.stream;
            });
            try {
                await(building);
                if (!duringConstruction) {
                    get(task.ready());
                }
                task.cancel();
                CompletableFuture<Void> rejectedStart =
                        task.ready().thenAccept(stream -> assertThrows(IllegalStateException.class, stream::start));
                finishBuilder.countDown();
                get(rejectedStart);
                get(task.settled());
                verify(raw.stream, never()).start();
                AtomicInteger errors = new AtomicInteger();
                get(task.ready()).onError(ignored -> errors.incrementAndGet());
                assertEquals(1, errors.get());
                assertFalse(worker.lock.isLocked());
            } finally {
                finishBuilder.countDown();
            }
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"noncancellable", "noncancellable-throw", "closeable", "closeable-throw"})
    void cancellationCannotCloseOrSettleWhileStartStillOwnsDelegate(String mode) throws Exception {
        CountDownLatch starting = new CountDownLatch(1);
        CountDownLatch finishStart = new CountDownLatch(1);
        boolean closeable = mode.startsWith("closeable");
        boolean throwing = mode.endsWith("throw");
        try (Worker worker = new Worker(false);
                ExecutorService caller = Executors.newSingleThreadExecutor()) {
            RawStream raw = new RawStream(closeable);
            doAnswer(call -> {
                        starting.countDown();
                        gate(finishStart);
                        if (throwing) {
                            throw new IllegalStateException("synthetic start failure");
                        }
                        return null;
                    })
                    .when(raw.stream)
                    .start();
            ChatTask.Stream task = worker.submit(() -> raw.stream);
            TokenStream stream = get(task.ready());
            Future<?> start = caller.submit(stream::start);
            try {
                await(starting);
                task.cancel();
                assertBlocked(task.settled());
                assertEquals(0, raw.closes.get());
                assertTrue(worker.lock.isLocked());
                finishStart.countDown();
                if (throwing) {
                    assertThrows(ExecutionException.class, () -> get(start));
                } else {
                    get(start);
                }
                if (!closeable) {
                    assertBlocked(task.settled());
                    raw.complete.get().accept(response());
                }
                get(task.settled());
                assertEquals(closeable ? 1 : 0, raw.closes.get());
                verify(raw.stream, times(1)).start();
            } finally {
                finishStart.countDown();
                raw.complete.get().accept(response());
            }
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void terminalCallbackCanCloseWhileStartWaitsForThatCallback(boolean closeable) throws Exception {
        try (Worker worker = new Worker(false);
                ExecutorService provider = Executors.newSingleThreadExecutor()) {
            RawStream raw = new RawStream(closeable);
            doAnswer(call -> {
                        get(provider.submit(() -> raw.complete.get().accept(response())));
                        return null;
                    })
                    .when(raw.stream)
                    .start();
            ChatTask.Stream task = worker.submit(() -> raw.stream);
            TokenStream stream = get(task.ready());
            stream.onCompleteResponse(ignored -> {
                task.cancel();
                assertDoesNotThrow(() -> ((AutoCloseable) stream).close());
                assertFalse(task.settled().isDone());
            });
            stream.start();
            get(task.settled());
            assertEquals(closeable ? 1 : 0, raw.closes.get());
        }
    }

    @Test
    void pinnedAiServiceKeepsLateToolAndMemoryWorkInsideCoordinationAfterCancellation() throws Exception {
        List<StreamingChatResponseHandler> responses = new ArrayList<>();
        StreamingChatModel model = new StreamingChatModel() {
            @Override
            public void doChat(ChatRequest request, StreamingChatResponseHandler handler) {
                responses.add(handler);
            }
        };
        var memory = MessageWindowChatMemory.withMaxMessages(10);
        memory.add(UserMessage.from("prepared input"));
        AtomicInteger tools = new AtomicInteger();
        ChatSession session = ChatSession.builder()
                .streamingChatModel(model)
                .chatMemory(memory)
                .tools(Map.of(ToolSpecification.builder().name("lookup").build(), (request, id) -> {
                    tools.incrementAndGet();
                    return "tool result";
                }))
                .build();
        CountDownLatch callbackEntered = new CountDownLatch(1);
        CountDownLatch finishCallback = new CountDownLatch(1);
        try (Worker worker = new Worker(false);
                ExecutorService provider = Executors.newSingleThreadExecutor()) {
            ChatTask.Stream task = worker.submit(() -> {
                TokenStream raw = new ChatExecution(session, "managed-test").stream();
                assertInstanceOf(AiServiceTokenStream.class, raw);
                assertFalse(raw instanceof AutoCloseable);
                return raw;
            });
            TokenStream stream = get(task.ready());
            stream.onCompleteResponse(ignored -> {
                        callbackEntered.countDown();
                        gate(finishCallback);
                    })
                    .start();
            assertEquals(1, responses.size());
            ChatTask<Integer> next = worker.queue.submit(
                    new ChatTask.Sync<>(() -> memory.messages().size()));
            try {
                task.cancel();
                assertBlocked(task.settled());
                assertBlocked(next.future());
                responses
                        .getFirst()
                        .onCompleteResponse(ChatServiceMemoryTest.response(AiMessage.from(ToolExecutionRequest.builder()
                                .id("one")
                                .name("lookup")
                                .arguments("{}")
                                .build())));
                assertEquals(1, tools.get());
                assertEquals(2, responses.size());
                assertEquals(3, memory.messages().size());
                assertBlocked(task.settled());
                assertBlocked(next.future());
                Future<?> terminal = provider.submit(() -> responses
                        .getLast()
                        .onCompleteResponse(ChatServiceMemoryTest.response(AiMessage.from("final answer"))));
                await(callbackEntered);
                assertBlocked(task.settled());
                assertBlocked(next.future());
                finishCallback.countDown();
                get(terminal);
                get(task.settled());
                assertEquals(4, get(next.future()));
                assertEquals(AiMessage.from("final answer"), memory.messages().getLast());
            } finally {
                finishCallback.countDown();
                if (!task.settled().isDone()) {
                    responses.getLast().onError(new IllegalStateException("test cleanup"));
                }
            }
        }
    }

    private static void assertBlocked(Future<?> future) {
        assertThrows(java.util.concurrent.TimeoutException.class, () -> future.get(100, TimeUnit.MILLISECONDS));
    }

    private static ChatResponse response() {
        return ChatResponse.builder()
                .aiMessage(AiMessage.from("synthetic answer"))
                .build();
    }

    private static void rejected(ChatTask.Stream task) {
        Throwable cause =
                assertThrows(ExecutionException.class, () -> get(task.ready())).getCause();
        assertInstanceOf(ChatQueueRejectedException.class, cause);
    }

    private static void await(CountDownLatch latch) throws InterruptedException {
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Expected controlled event");
    }

    private static void gate(CountDownLatch latch) {
        try {
            assertTrue(latch.await(10, TimeUnit.SECONDS), "Test did not release gate");
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
            throw new AssertionError("Cleanup/builder was interrupted", interrupted);
        }
    }

    private static <T> T get(Future<T> future) throws Exception {
        return future.get(5, TimeUnit.SECONDS);
    }

    private static final class Worker implements AutoCloseable {
        final ObservedLock lock;
        final ChatTaskQueue queue;
        final ExecutorService executor = Executors.newSingleThreadExecutor();

        Worker(boolean pauseUnlock) {
            this(pauseUnlock, true);
        }

        Worker(boolean pauseUnlock, boolean start) {
            lock = new ObservedLock(pauseUnlock);
            queue = new ChatTaskQueue("managed-test", lock);
            if (start) {
                queue.startWorker(executor);
            }
        }

        ChatTask.Stream submit(java.util.function.Supplier<TokenStream> builder) {
            ChatTask.Stream task = new ChatTask.Stream(builder);
            queue.submit(task);
            return task;
        }

        public void close() throws InterruptedException {
            lock.finishUnlock.countDown();
            queue.drain(0);
            executor.shutdownNow();
            assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
        }
    }

    private static final class ObservedLock extends ReentrantLock {
        final AtomicReference<Thread> worker = new AtomicReference<>();
        final CountDownLatch attempted = new CountDownLatch(1);
        final CountDownLatch unlocking = new CountDownLatch(1);
        final CountDownLatch finishUnlock;

        ObservedLock(boolean pauseUnlock) {
            finishUnlock = new CountDownLatch(pauseUnlock ? 1 : 0);
        }

        @Override
        public void lockInterruptibly() throws InterruptedException {
            worker.set(Thread.currentThread());
            attempted.countDown();
            super.lockInterruptibly();
        }

        @Override
        public void unlock() {
            unlocking.countDown();
            gate(finishUnlock);
            super.unlock();
        }
    }

    private static final class RawStream {
        final TokenStream stream;
        final AtomicReference<Consumer<ChatResponse>> complete = new AtomicReference<>();
        final AtomicReference<Consumer<Throwable>> error = new AtomicReference<>();
        final AtomicInteger closes = new AtomicInteger();
        volatile Runnable onClose = () -> {};

        RawStream() throws Exception {
            this(true);
        }

        RawStream(boolean closeable) throws Exception {
            var settings = withSettings().mockMaker(MockMakers.SUBCLASS).defaultAnswer(RETURNS_SELF);
            if (closeable) {
                settings.extraInterfaces(AutoCloseable.class);
            }
            stream = mock(TokenStream.class, settings);
            assertEquals(closeable, stream instanceof AutoCloseable);
            doAnswer(call -> {
                        complete.set(call.getArgument(0));
                        return stream;
                    })
                    .when(stream)
                    .onCompleteResponse(any());
            doAnswer(call -> {
                        error.set(call.getArgument(0));
                        return stream;
                    })
                    .when(stream)
                    .onError(any());
            if (closeable) {
                doAnswer(call -> {
                            closes.incrementAndGet();
                            onClose.run();
                            return null;
                        })
                        .when((AutoCloseable) stream)
                        .close();
            }
        }
    }
}
