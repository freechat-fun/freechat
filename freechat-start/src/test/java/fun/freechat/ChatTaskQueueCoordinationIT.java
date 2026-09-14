package fun.freechat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.service.TokenStream;
import fun.freechat.service.chat.ChatQueueRejectedException;
import fun.freechat.service.chat.ChatTask;
import fun.freechat.service.chat.ChatTaskQueue;
import fun.freechat.service.chat.impl.ChatTaskQueueManager;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.function.Consumer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.test.util.ReflectionTestUtils;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/** Real, private Redis; no Spring context, SQL, memory cancellation, or external model providers. */
@Testcontainers
@Timeout(30)
class ChatTaskQueueCoordinationIT {
    private static final String PREFIX = "freechat:chat:coordination:v1:";
    private static final long WAIT_SECONDS = 5;
    private static final long WATCHDOG_MS = 3_000;

    @Container
    private static final GenericContainer<?> REDIS =
            new GenericContainer<>("redis:7.4.2-alpine").withExposedPorts(6379);

    private static RedissonClient firstClient;
    private static RedissonClient secondClient;
    private final List<Worker> workers = new ArrayList<>();
    private final List<ExecutorService> executors = new ArrayList<>();
    private final List<ChatTaskQueueManager> managers = new ArrayList<>();

    @BeforeAll
    static void connectOnlyToOwnedRedis() {
        firstClient = newClient();
        secondClient = newClient();
        assertNotSame(firstClient, secondClient);
    }

    private static RedissonClient newClient() {
        Config config = new Config();
        config.setLockWatchdogTimeout(WATCHDOG_MS);
        config.setThreads(2);
        config.setNettyThreads(2);
        config.useSingleServer()
                .setAddress("redis://" + REDIS.getHost() + ":" + REDIS.getMappedPort(6379))
                .setConnectionMinimumIdleSize(1)
                .setConnectionPoolSize(4)
                .setSubscriptionConnectionMinimumIdleSize(1)
                .setSubscriptionConnectionPoolSize(2);
        return Redisson.create(config);
    }

    @AfterEach
    void stopOwnedWorkers() throws InterruptedException {
        workers.forEach(worker -> worker.queue.drain(0));
        executors.forEach(ExecutorService::shutdownNow);
        managers.forEach(ChatTaskQueueManager::stop);
        for (ExecutorService executor : executors) {
            assertTrue(executor.awaitTermination(WAIT_SECONDS, TimeUnit.SECONDS), "Test worker leaked");
        }
    }

    @AfterAll
    static void closeOwnedClients() {
        // Client shutdown only. Testcontainers stops our container; never issue Redis SHUTDOWN/FLUSHALL.
        try {
            if (firstClient != null) {
                firstClient.shutdown(0, WAIT_SECONDS, TimeUnit.SECONDS);
            }
        } finally {
            if (secondClient != null) {
                secondClient.shutdown(0, WAIT_SECONDS, TimeUnit.SECONDS);
            }
        }
    }

    @Test
    void sameChatSyncTasksExcludeOtherClientAndWatchdogRenewsUntilWorkerUnlocks() throws Exception {
        String id = chatId();
        Worker first = worker(firstClient, id);
        Worker second = worker(secondClient, id);
        CountDownLatch entered = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        ChatTask.Sync<String> owner = new ChatTask.Sync<>(() -> {
            assertTrue(first.lock.delegate.isHeldByCurrentThread());
            entered.countDown();
            awaitGate(release);
            return "owner";
        });
        first.queue.submit(owner);
        try {
            await(entered);
            ChatTask.Sync<String> next = new ChatTask.Sync<>(() -> {
                assertTrue(second.lock.delegate.isHeldByCurrentThread());
                return "next";
            });
            second.queue.submit(next);
            await(second.lock.attempted);
            // This exceeds the configured watchdog TTL: without renewal the second client would run.
            assertThrows(TimeoutException.class, () -> next.future().get(WATCHDOG_MS + 1_000, TimeUnit.MILLISECONDS));
            assertTrue(
                    first.lock.delegate.isHeldByThread(get(first.lock.acquired).threadId()));
            assertTrue(first.lock.delegate.remainTimeToLive() > 0);
            release.countDown();
            assertEquals("owner", get(owner.future()));
            assertEquals("next", get(next.future()));
            assertWorkerUnlocked(first);
            assertWorkerUnlocked(second);
        } finally {
            release.countDown();
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"complete", "error"})
    void streamKeepsOtherClientOutThroughTerminalCallbackOnAnotherThread(String terminal) throws Exception {
        String id = chatId();
        Worker first = worker(firstClient, id);
        Worker second = worker(secondClient, id);
        ControlledStream raw = new ControlledStream();
        ControlledStream nextRaw = new ControlledStream();
        TokenStream stream =
                get(first.queue.submit(new ChatTask.Stream(() -> raw.stream)).future());
        CountDownLatch callbackEntered = new CountDownLatch(1);
        CountDownLatch finishCallback = new CountDownLatch(1);
        AtomicReference<Thread> callbackThread = new AtomicReference<>();
        Consumer<Object> terminalHandler = ignored -> {
            callbackThread.set(Thread.currentThread());
            callbackEntered.countDown();
            awaitGate(finishCallback);
        };
        stream.onCompleteResponse(terminalHandler::accept)
                .onError(terminalHandler::accept)
                .start();
        await(raw.started);
        ChatTask.Stream next = new ChatTask.Stream(() -> nextRaw.stream);
        second.queue.submit(next);
        try {
            await(second.lock.attempted);
            // Returning a TokenStream is not terminal completion; renewal must outlive the watchdog TTL.
            assertThrows(TimeoutException.class, () -> next.future().get(WATCHDOG_MS + 1_000, TimeUnit.MILLISECONDS));
            Future<?> callback = executor().submit(() -> raw.terminate(terminal));
            await(callbackEntered);
            assertNotSame(get(first.lock.acquired), callbackThread.get());
            assertTrue(
                    first.lock.delegate.isHeldByThread(get(first.lock.acquired).threadId()));
            assertBlocked(next.future()); // Even the user terminal handler still occupies the slot.
            finishCallback.countDown();
            get(callback);
            TokenStream successor = get(next.future());
            assertWorkerUnlocked(first);
            successor.start();
            await(nextRaw.started);
            get(executor().submit(() -> nextRaw.terminate("complete")));
            assertWorkerUnlocked(second);
        } finally {
            finishCallback.countDown();
        }
    }

    @Test
    void independentChatIdsCanExecuteWhileAnotherChatIsBusy() throws Exception {
        Worker first = worker(firstClient, chatId());
        Worker second = worker(secondClient, chatId());
        CountDownLatch entered = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        ChatTask.Sync<String> busy = new ChatTask.Sync<>(() -> {
            entered.countDown();
            awaitGate(release);
            return "released";
        });
        first.queue.submit(busy);
        try {
            await(entered);
            assertEquals(
                    "independent",
                    get(second.queue
                            .submit(new ChatTask.Sync<>(() -> "independent"))
                            .future()));
            assertFalse(busy.future().isDone());
            assertTrue(
                    first.lock.delegate.isHeldByThread(get(first.lock.acquired).threadId()));
        } finally {
            release.countDown();
        }
        assertEquals("released", get(busy.future()));
    }

    @Test
    void localFifoIncludesFullStreamLifetime() throws Exception {
        Worker worker = worker(firstClient, chatId());
        List<Integer> order = new ArrayList<>(); // One queue worker writes; future completion publishes it.
        CountDownLatch entered = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        ChatTask.Sync<Integer> first = new ChatTask.Sync<>(() -> {
            order.add(1);
            entered.countDown();
            awaitGate(release);
            return 1;
        });
        worker.queue.submit(first);
        try {
            await(entered);
            ControlledStream raw = new ControlledStream();
            ChatTask.Stream middle = new ChatTask.Stream(() -> {
                order.add(2);
                return raw.stream;
            });
            ChatTask.Sync<Integer> last = new ChatTask.Sync<>(() -> {
                order.add(3);
                return 3;
            });
            worker.queue.submit(middle);
            worker.queue.submit(last);
            release.countDown();
            assertEquals(1, get(first.future()));
            TokenStream stream = get(middle.future());
            stream.start();
            assertBlocked(last.future());
            get(executor().submit(() -> raw.terminate("complete")));
            assertEquals(3, get(last.future()));
            assertEquals(List.of(1, 2, 3), order);
        } finally {
            release.countDown();
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"sync-throw", "sync-null", "stream-builder-throw", "stream-null"})
    void failedOrNullWorkReleasesLockForOtherClientAndLocalSuccessor(String mode) throws Exception {
        String id = chatId();
        Worker first = worker(firstClient, id);
        Worker second = worker(secondClient, id);
        IllegalStateException failure = new IllegalStateException("synthetic task failure");
        ChatTask<?> task =
                switch (mode) {
                    case "sync-throw" ->
                        new ChatTask.Sync<>(() -> {
                            throw failure;
                        });
                    case "sync-null" -> new ChatTask.Sync<>(() -> null);
                    case "stream-builder-throw" ->
                        new ChatTask.Stream(() -> {
                            throw failure;
                        });
                    case "stream-null" -> new ChatTask.Stream(() -> null);
                    default -> throw new AssertionError(mode);
                };
        first.queue.submit(task);
        if (mode.equals("stream-builder-throw")) {
            Throwable reported = assertThrows(ExecutionException.class, () -> get(task.future()))
                    .getCause();
            assertInstanceOf(IllegalStateException.class, reported);
            assertEquals("Chat stream failed", reported.getMessage());
            assertNull(reported.getCause(), "Provider payload must not escape the stream builder");
            assertEquals(0, reported.getSuppressed().length);
        } else if (mode.endsWith("throw")) {
            assertSame(
                    failure,
                    assertThrows(ExecutionException.class, () -> get(task.future()))
                            .getCause());
        } else {
            assertNull(get(task.future()));
        }
        assertWorkerUnlocked(first);
        assertEquals(
                "remote",
                get(second.queue.submit(new ChatTask.Sync<>(() -> "remote")).future()));
        assertEquals(
                "local",
                get(first.queue.submit(new ChatTask.Sync<>(() -> "local")).future()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"complete", "error"})
    void throwingTerminalHandlerStillReleasesWorkerLock(String terminal) throws Exception {
        String id = chatId();
        Worker first = worker(firstClient, id);
        Worker second = worker(secondClient, id);
        ControlledStream raw = new ControlledStream();
        TokenStream stream =
                get(first.queue.submit(new ChatTask.Stream(() -> raw.stream)).future());
        IllegalArgumentException failure = new IllegalArgumentException("synthetic callback failure");
        stream.onCompleteResponse(ignored -> {
                    throw failure;
                })
                .onError(ignored -> {
                    throw failure;
                })
                .start();
        ChatTask.Sync<String> next = new ChatTask.Sync<>(() -> "after callback failure");
        second.queue.submit(next);
        await(second.lock.attempted);
        assertBlocked(next.future());
        Future<?> callback = executor().submit(() -> raw.terminate(terminal));
        assertSame(
                failure,
                assertThrows(ExecutionException.class, () -> get(callback)).getCause());
        assertEquals("after callback failure", get(next.future()));
        assertWorkerUnlocked(first);
    }

    @Test
    void throwingStreamStartReleasesLockForOtherClient() throws Exception {
        String id = chatId();
        Worker first = worker(firstClient, id);
        Worker second = worker(secondClient, id);
        ControlledStream raw = new ControlledStream();
        IllegalStateException failure = new IllegalStateException("synthetic start failure");
        doThrow(failure).when(raw.stream).start();
        TokenStream stream =
                get(first.queue.submit(new ChatTask.Stream(() -> raw.stream)).future());
        ChatTask.Sync<String> next = new ChatTask.Sync<>(() -> "after start failure");
        second.queue.submit(next);
        await(second.lock.attempted);
        assertBlocked(next.future());
        assertSame(failure, assertThrows(IllegalStateException.class, stream::start));
        assertEquals("after start failure", get(next.future()));
        assertWorkerUnlocked(first);
    }

    @Test
    void interruptedLockWaiterRejectsPendingWorkAndNeverUnlocksAnotherOwner() throws Exception {
        String id = chatId();
        RLock owner = firstClient.getLock(PREFIX + id);
        owner.lockInterruptibly();
        try {
            Worker waiter = worker(secondClient, id);
            AtomicInteger executions = new AtomicInteger();
            ChatTask.Sync<Integer> waiting = new ChatTask.Sync<>(executions::incrementAndGet);
            ChatTask.Sync<Integer> queued = new ChatTask.Sync<>(executions::incrementAndGet);
            waiter.queue.submit(waiting);
            await(waiter.lock.attempted);
            waiter.queue.submit(queued);
            assertBlocked(waiting.future());
            waiter.lock.attemptThread.get().interrupt();
            rejected(waiting);
            rejected(queued);
            assertWorkerExited(waiter);
            assertEquals(0, executions.get());
            assertEquals(0, waiter.lock.unlockCalls.get(), "No unlock unless this worker acquired the lock");
            assertFalse(waiter.lock.acquired.isDone());
            assertTrue(owner.isHeldByCurrentThread());
            assertEquals(1, owner.getHoldCount());
        } finally {
            owner.unlock();
        }
    }

    @Test
    void drainedLockWaiterRejectsAfterEventualAcquisitionAndWorkerExits() throws Exception {
        String id = chatId();
        RLock owner = firstClient.getLock(PREFIX + id);
        Worker waiter = worker(secondClient, id);
        AtomicInteger executions = new AtomicInteger();
        ChatTask.Sync<Integer> waiting = new ChatTask.Sync<>(executions::incrementAndGet);
        ChatTask.Sync<Integer> queued = new ChatTask.Sync<>(executions::incrementAndGet);
        owner.lockInterruptibly();
        try {
            waiter.queue.submit(waiting);
            await(waiter.lock.attempted);
            waiter.queue.submit(queued);
            assertBlocked(waiting.future());
            waiter.queue.drain(0);
            rejected(queued);
            rejected(waiter.queue.submit(new ChatTask.Sync<>(executions::incrementAndGet)));
            assertTrue(owner.isHeldByCurrentThread());
            assertEquals(0, waiter.lock.unlockCalls.get());
        } finally {
            owner.unlock();
        }
        get(waiter.lock.acquired); // The drain check must also run after lockInterruptibly returns.
        rejected(waiting);
        assertWorkerUnlocked(waiter);
        assertWorkerExited(waiter);
        assertEquals(0, executions.get());
        assertTrue(waiter.queue.isIdle());
    }

    @Test
    void drainAllowsActiveTaskToFinishButRejectsQueuedAndNewWork() throws Exception {
        Worker worker = worker(firstClient, chatId());
        CountDownLatch entered = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        ChatTask.Sync<String> active = new ChatTask.Sync<>(() -> {
            entered.countDown();
            awaitGate(release);
            return "completed";
        });
        worker.queue.submit(active);
        try {
            await(entered);
            AtomicInteger unwanted = new AtomicInteger();
            ChatTask.Sync<Integer> queued = new ChatTask.Sync<>(unwanted::incrementAndGet);
            worker.queue.submit(queued);
            worker.queue.drain(0);
            rejected(queued);
            rejected(worker.queue.submit(new ChatTask.Sync<>(unwanted::incrementAndGet)));
            assertFalse(active.future().isDone());
            release.countDown();
            assertEquals("completed", get(active.future()));
            assertWorkerUnlocked(worker);
            assertWorkerExited(worker);
            assertEquals(0, unwanted.get());
        } finally {
            release.countDown();
        }
    }

    @Test
    void enqueueRacingDrainCannotLeaveAnUncompletedTaskBehind() throws Exception {
        Worker worker = unstartedWorker(firstClient, chatId());
        PausingQueue pending = new PausingQueue();
        // Pause exactly between submit's initial draining check and insertion, without sleep/retry races.
        ReflectionTestUtils.setField(worker.queue, "taskQueue", pending);
        AtomicInteger executions = new AtomicInteger();
        ChatTask.Sync<Integer> task = new ChatTask.Sync<>(executions::incrementAndGet);
        Future<?> submission = executor().submit(() -> worker.queue.submit(task));
        try {
            await(pending.addEntered);
            worker.queue.drain(0); // Sees an empty queue while submit has already passed its first check.
        } finally {
            pending.allowAdd.countDown();
        }
        get(submission);
        rejected(task);
        worker.queue.startWorker(worker.executor);
        assertWorkerExited(worker);
        assertEquals(0, executions.get());
        assertTrue(worker.queue.isIdle());
    }

    @Test
    void idleDrainedWorkerExitsWithoutExecutorInterruption() throws Exception {
        Worker worker = worker(firstClient, chatId());
        assertEquals(
                "ready",
                get(worker.queue.submit(new ChatTask.Sync<>(() -> "ready")).future()));
        assertWorkerUnlocked(worker);
        worker.queue.drain(0);
        assertWorkerExited(worker);
        assertTrue(worker.queue.isIdle());
    }

    @Test
    void managersUseVersionedSharedLockAndDoNotShutDownInjectedClients() throws Exception {
        String id = chatId();
        ChatTaskQueueManager first = manager(firstClient);
        ChatTaskQueueManager second = manager(secondClient);
        first.start();
        second.start();
        assertTrue(first.isRunning());
        assertEquals(PREFIX + id, first.coordinationLock(id).getName());
        assertEquals(PREFIX + id, second.coordinationLock(id).getName());
        ChatTaskQueue firstQueue = first.getOrCreateQueue(id);
        ChatTaskQueue secondQueue = second.getOrCreateQueue(id);
        assertSame(firstQueue, first.getOrCreateQueue(id));
        assertNotSame(firstQueue, secondQueue);
        CountDownLatch entered = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        ChatTask.Sync<String> active = new ChatTask.Sync<>(() -> {
            assertTrue(first.coordinationLock(id).isHeldByCurrentThread());
            entered.countDown();
            awaitGate(release);
            return "first manager";
        });
        firstQueue.submit(active);
        try {
            await(entered);
            ChatTask.Sync<String> next = new ChatTask.Sync<>(() -> {
                assertTrue(second.coordinationLock(id).isHeldByCurrentThread());
                return "second manager";
            });
            secondQueue.submit(next);
            assertBlocked(next.future());
            release.countDown();
            assertEquals("first manager", get(active.future()));
            assertEquals("second manager", get(next.future()));
        } finally {
            release.countDown();
        }
        first.stop();
        second.stop();
        assertFalse(first.isRunning());
        assertFalse(second.isRunning());
        assertFalse(firstClient.isShutdown());
        assertFalse(secondClient.isShutdown());
        String key = "coordination-it-client-lifetime:" + id;
        firstClient.getBucket(key).set("still connected");
        assertEquals("still connected", secondClient.getBucket(key).get());
        RLock reusable = second.coordinationLock(id);
        assertTrue(reusable.tryLock(WAIT_SECONDS, TimeUnit.SECONDS));
        reusable.unlock();
    }

    @ParameterizedTest
    @ValueSource(strings = {"complete-constructor", "error-constructor", "complete-published", "error-published"})
    void earlyTerminalIsReplayedOnceWithoutStartOrCallbackRegistration(String mode) throws Exception {
        Worker worker = worker(firstClient, chatId());
        ControlledStream raw = new ControlledStream();
        ChatResponse response =
                ChatResponse.builder().aiMessage(AiMessage.from("early answer")).build();
        IllegalStateException failure = new IllegalStateException("early error");
        if (mode.equals("complete-constructor")) {
            doAnswer(call -> {
                        ((Consumer<ChatResponse>) call.getArgument(0)).accept(response);
                        return raw.stream;
                    })
                    .when(raw.stream)
                    .onCompleteResponse(any());
        } else if (mode.equals("error-constructor")) {
            doAnswer(call -> {
                        ((Consumer<Throwable>) call.getArgument(0)).accept(failure);
                        return raw.stream;
                    })
                    .when(raw.stream)
                    .onError(any());
        }
        TokenStream stream =
                get(worker.queue.submit(new ChatTask.Stream(() -> raw.stream)).future());
        ChatTask.Sync<String> next = new ChatTask.Sync<>(() -> "after early terminal");
        worker.queue.submit(next);
        if (mode.endsWith("published")) {
            assertBlocked(next.future());
            if (mode.startsWith("complete")) {
                raw.complete.get().accept(response);
            } else {
                raw.error.get().accept(failure);
            }
        }
        assertEquals("after early terminal", get(next.future()));
        assertWorkerUnlocked(worker);
        AtomicInteger completions = new AtomicInteger();
        AtomicInteger errors = new AtomicInteger();
        stream.onCompleteResponse(value -> {
                    assertSame(response, value);
                    completions.incrementAndGet();
                })
                .onError(value -> {
                    assertSame(failure, value);
                    errors.incrementAndGet();
                });
        stream.onCompleteResponse(value -> completions.incrementAndGet()).onError(value -> errors.incrementAndGet());
        assertEquals(mode.startsWith("complete") ? 1 : 0, completions.get());
        assertEquals(mode.startsWith("error") ? 1 : 0, errors.get());
        assertThrows(IllegalStateException.class, stream::start);
        verify(raw.stream, never()).start();
    }

    @Test
    void memoryTimeoutBeforeStartReleasesRedisAndReplaysFailureAfterSqlFence() throws Exception {
        String id = chatId().replace("-", "");
        Worker first = worker(firstClient, id);
        Worker second = worker(secondClient, id);
        try (var f = memoryFixture(first.queue)) {
            TokenStream stream = f.service.streamSend(f.id, ChatServiceMemoryTest.ORIGINAL, null);
            ChatTask.Sync<String> remote = new ChatTask.Sync<>(() -> {
                assertEquals(1, f.aborts.get());
                assertTrue(second.lock.delegate.isHeldByCurrentThread());
                return "after timeout";
            });
            second.queue.submit(remote);
            await(second.lock.attempted);
            assertBlocked(remote.future());
            get(executor().submit(f.timeout));
            assertEquals("after timeout", get(remote.future()));
            assertWorkerUnlocked(first);
            AtomicInteger errors = new AtomicInteger();
            stream.onError(error -> errors.incrementAndGet());
            stream.onError(error -> errors.incrementAndGet());
            assertEquals(1, errors.get());
            assertThrows(IllegalStateException.class, stream::start);
            assertTrue(f.provider.requests.isEmpty());
            assertEquals(1, f.aborts.get());
            f.assertTimersDisposed();
            f.idle();
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"interrupt", "drain", "interrupted-drainer"})
    void activeMemoryStreamIsCancelledBeforeUnlockAndPendingWorkCannotOvertakeCleanup(String shutdown)
            throws Exception {
        String id = chatId().replace("-", "");
        Worker first = worker(firstClient, id);
        Worker second = worker(secondClient, id);
        try (var f = memoryFixture(first.queue)) {
            AtomicInteger errors = new AtomicInteger();
            TokenStream stream = f.service.streamSend(f.id, ChatServiceMemoryTest.ORIGINAL, null);
            stream.onError(error -> errors.incrementAndGet()).start();
            AtomicInteger cancellations = new AtomicInteger();
            var handle = new dev.langchain4j.model.chat.response.StreamingHandle() {
                public void cancel() {
                    cancellations.incrementAndGet();
                }

                public boolean isCancelled() {
                    return cancellations.get() != 0;
                }
            };
            f.provider
                    .handlers
                    .getFirst()
                    .onPartialResponse(
                            new dev.langchain4j.model.chat.response.PartialResponse("token"),
                            new dev.langchain4j.model.chat.response.PartialResponseContext(handle));
            CountDownLatch abortEntered = new CountDownLatch(1);
            CountDownLatch releaseAbort = new CountDownLatch(1);
            f.beforeAbort = () -> {
                assertFalse(Thread.currentThread().isInterrupted(), "Cleanup needs an un-interrupted SQL/Redis thread");
                assertSame(first.lock.attemptThread.get(), Thread.currentThread(), "Only the worker performs cleanup");
                abortEntered.countDown();
                awaitGate(releaseAbort);
                assertFalse(Thread.currentThread().isInterrupted(), "Repeated drain must not interrupt cleanup");
            };
            ChatTask.Sync<String> pending = new ChatTask.Sync<>(() -> fail("Pending local work ran after shutdown"));
            first.queue.submit(pending);
            ChatTask.Sync<String> remote = new ChatTask.Sync<>(() -> {
                assertEquals(1, f.aborts.get(), "Durable abort must precede successor admission");
                return "after cleanup";
            });
            second.queue.submit(remote);
            await(second.lock.attempted);
            try {
                Future<Boolean> drain = null;
                if (shutdown.equals("interrupt")) {
                    get(first.lock.acquired).interrupt();
                } else {
                    // Draining a stream whose caller disappeared must request cleanup, not wait for abort IO.
                    drain = executor().submit(() -> {
                        boolean interrupted = shutdown.equals("interrupted-drainer");
                        if (interrupted) {
                            Thread.currentThread().interrupt();
                        }
                        first.queue.drain(interrupted ? 30_000 : 0);
                        return Thread.currentThread().isInterrupted();
                    });
                }
                await(abortEntered);
                if (drain != null) {
                    assertEquals(shutdown.equals("interrupted-drainer"), get(drain));
                }
                get(executor().submit(() -> first.queue.drain(0))); // Still bounded while abort is blocked.
                assertBlocked(remote.future());
                assertEquals(0, first.lock.unlockCalls.get());
                releaseAbort.countDown();
                assertEquals("after cleanup", get(remote.future()));
                rejected(pending);
                assertWorkerUnlocked(first);
                assertWorkerExited(first);
                assertEquals(1, errors.get());
                assertEquals(1, cancellations.get());
                assertEquals(1, f.aborts.get());
                f.provider.complete(0, ChatServiceMemoryTest.response(AiMessage.from("late answer")));
                assertEquals(1, f.usageRows().size(), "Late returned usage is still accountable");
                assertEquals(1, f.writes.size(), "Late output cannot follow the abort");
                assertFalse(f.binding().memory().completed());
                f.assertTimersDisposed();
            } finally {
                releaseAbort.countDown();
            }
        }
    }

    @Test
    void synchronousStartThrowClosesTransportBeforeQueuedSuccessorCanAcquire() throws Exception {
        String id = chatId();
        Worker first = worker(firstClient, id);
        Worker second = worker(secondClient, id);
        TokenStream raw = mock(
                TokenStream.class,
                withSettings()
                        .mockMaker(org.mockito.MockMakers.SUBCLASS)
                        .extraInterfaces(AutoCloseable.class)
                        .defaultAnswer(RETURNS_SELF));
        CountDownLatch closeEntered = new CountDownLatch(1);
        CountDownLatch releaseClose = new CountDownLatch(1);
        java.util.concurrent.atomic.AtomicBoolean closed = new java.util.concurrent.atomic.AtomicBoolean();
        doAnswer(call -> {
                    closeEntered.countDown();
                    awaitGate(releaseClose);
                    closed.set(true);
                    return null;
                })
                .when((AutoCloseable) raw)
                .close();
        IllegalStateException failure = new IllegalStateException("synthetic start failure");
        doThrow(failure).when(raw).start();
        TokenStream stream =
                get(first.queue.submit(new ChatTask.Stream(() -> raw)).future());
        ChatTask.Sync<String> next = new ChatTask.Sync<>(() -> {
            assertTrue(closed.get());
            return "after transport cleanup";
        });
        second.queue.submit(next);
        Future<?> start = executor().submit(stream::start);
        try {
            await(closeEntered);
            await(second.lock.attempted);
            assertBlocked(next.future());
            assertEquals(0, first.lock.unlockCalls.get());
            releaseClose.countDown();
            assertSame(
                    failure,
                    assertThrows(ExecutionException.class, () -> get(start)).getCause());
            assertEquals("after transport cleanup", get(next.future()));
            assertWorkerUnlocked(first);
            verify((AutoCloseable) raw, atLeastOnce()).close();
        } finally {
            releaseClose.countDown();
        }
    }

    @Test
    void drainBetweenDequeueAndRegistrationRejectsWithoutExecutingOrAcquiringRedis() throws Exception {
        Worker worker = unstartedWorker(firstClient, chatId());
        CountDownLatch dequeued = new CountDownLatch(1);
        CountDownLatch allowRegistration = new CountDownLatch(1);
        LinkedBlockingQueue<ChatTask<?>> pending = new LinkedBlockingQueue<>() {
            @Override
            public ChatTask<?> poll(long timeout, TimeUnit unit) throws InterruptedException {
                ChatTask<?> task = super.poll(timeout, unit);
                if (task != null) {
                    dequeued.countDown();
                    awaitGate(allowRegistration);
                }
                return task;
            }
        };
        ReflectionTestUtils.setField(worker.queue, "taskQueue", pending);
        ChatTask.Stream task = new ChatTask.Stream(() -> fail("Drained task reached stream builder"));
        worker.queue.submit(task);
        worker.queue.startWorker(worker.executor);
        try {
            await(dequeued);
            get(executor().submit(() -> worker.queue.drain(0)));
        } finally {
            allowRegistration.countDown();
        }
        rejected(task);
        assertWorkerExited(worker);
        assertNull(worker.lock.attemptThread.get());
        assertEquals(0, worker.lock.unlockCalls.get());
        assertTrue(worker.queue.isIdle());
    }

    @ParameterizedTest
    @ValueSource(strings = {"external", "worker"})
    void drainDuringStreamConstructionIsNotLostAndNeverWaitsOnItsOwnWorker(String caller) throws Exception {
        String id = chatId();
        Worker first = worker(firstClient, id);
        Worker second = worker(secondClient, id);
        CountDownLatch builderEntered = new CountDownLatch(1);
        CountDownLatch releaseBuilder = new CountDownLatch(1);
        TokenStream raw = mock(
                TokenStream.class,
                withSettings()
                        .mockMaker(org.mockito.MockMakers.SUBCLASS)
                        .extraInterfaces(AutoCloseable.class)
                        .defaultAnswer(RETURNS_SELF));
        AtomicInteger closes = new AtomicInteger();
        doAnswer(call -> {
                    assertSame(first.lock.attemptThread.get(), Thread.currentThread());
                    assertTrue(first.lock.delegate.isHeldByCurrentThread());
                    assertFalse(Thread.currentThread().isInterrupted());
                    closes.incrementAndGet();
                    return null;
                })
                .when((AutoCloseable) raw)
                .close();
        ChatTask.Stream active = new ChatTask.Stream(() -> {
            builderEntered.countDown();
            awaitGate(releaseBuilder);
            if (caller.equals("worker")) {
                first.queue.drain(30_000); // Cannot wait for the builder that is calling drain.
            }
            assertFalse(Thread.currentThread().isInterrupted(), "Drain must not interrupt builder IO");
            return raw;
        });
        first.queue.submit(active);
        try {
            await(builderEntered);
            ChatTask<String> pending = first.queue.submit(new ChatTask.Sync<>(() -> fail("Pending task ran")));
            ChatTask<String> next = second.queue.submit(new ChatTask.Sync<>(() -> {
                assertEquals(1, closes.get(), "Cleanup must finish before successor acquisition");
                return "after builder cleanup";
            }));
            await(second.lock.attempted);
            if (caller.equals("external")) {
                get(executor().submit(() -> first.queue.drain(0)));
                rejected(pending);
            }
            assertBlocked(next.future());
            assertEquals(0, first.lock.unlockCalls.get());
            releaseBuilder.countDown();
            TokenStream stream = get(active.future());
            assertEquals("after builder cleanup", get(next.future()));
            AtomicInteger errors = new AtomicInteger();
            stream.onError(ignored -> errors.incrementAndGet());
            assertEquals(1, errors.get());
            assertThrows(IllegalStateException.class, stream::start);
            verify(raw, never()).start();
            rejected(pending);
            assertWorkerUnlocked(first);
            assertWorkerExited(first);
        } finally {
            releaseBuilder.countDown();
        }
    }

    @Test
    void synchronousTaskCanDrainItselfWithoutWaitingOrInterruptingItsWork() throws Exception {
        Worker worker = worker(firstClient, chatId());
        ChatTask.Sync<String> task = new ChatTask.Sync<>(() -> {
            worker.queue.drain(30_000);
            assertFalse(Thread.currentThread().isInterrupted());
            assertTrue(worker.lock.delegate.isHeldByCurrentThread());
            return "self drain returned";
        });
        assertEquals("self drain returned", get(worker.queue.submit(task).future()));
        assertWorkerUnlocked(worker);
        assertWorkerExited(worker);
        rejected(worker.queue.submit(new ChatTask.Sync<>(() -> fail("New work ran after self drain"))));
    }

    @Test
    void streamCanFinishNormallyDuringDrainGraceInterval() throws Exception {
        Worker worker = worker(firstClient, chatId());
        ControlledStream raw = new ControlledStream();
        TokenStream stream =
                get(worker.queue.submit(new ChatTask.Stream(() -> raw.stream)).future());
        AtomicInteger completions = new AtomicInteger();
        AtomicInteger errors = new AtomicInteger();
        stream.onCompleteResponse(ignored -> completions.incrementAndGet())
                .onError(ignored -> errors.incrementAndGet())
                .start();
        CountDownLatch drainStarted = new CountDownLatch(1);
        Future<?> drain = executor().submit(() -> {
            drainStarted.countDown();
            worker.queue.drain(30_000);
        });
        await(drainStarted);
        assertBlocked(drain);
        assertEquals(0, errors.get());
        assertEquals(0, worker.lock.unlockCalls.get());
        raw.terminate("complete");
        get(drain);
        assertEquals(1, completions.get());
        assertEquals(0, errors.get());
        assertWorkerUnlocked(worker);
        assertWorkerExited(worker);
    }

    @Test
    void synchronousTaskInterruptIsClearedForRedisUnlockAndRestoredWhenWorkerReturns() throws Exception {
        String id = chatId();
        Worker first = unstartedWorker(firstClient, id);
        Worker second = worker(secondClient, id);
        CountDownLatch entered = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        ChatTask<String> task = first.queue.submit(new ChatTask.Sync<>(() -> {
            entered.countDown();
            awaitGate(release);
            Thread.currentThread().interrupt();
            return "interrupted owner";
        }));
        Future<Boolean> exitedInterrupted = first.executor.submit(() -> {
            // Observe before ExecutorService clears interruption when preparing its next runnable.
            ReflectionTestUtils.invokeMethod(first.queue, "workerLoop");
            return Thread.currentThread().isInterrupted();
        });
        try {
            await(entered);
            ChatTask<String> pending = first.queue.submit(new ChatTask.Sync<>(() -> fail("Pending task ran")));
            ChatTask<String> next = second.queue.submit(new ChatTask.Sync<>(() -> "remote successor"));
            await(second.lock.attempted);
            assertBlocked(next.future());
            release.countDown();
            assertEquals("interrupted owner", get(task.future()));
            assertEquals("remote successor", get(next.future()));
            assertWorkerUnlocked(first);
            assertTrue(get(exitedInterrupted), "Worker must retain its shutdown interrupt after unlock");
            rejected(pending);
        } finally {
            release.countDown();
        }
    }

    private static ChatServiceMemoryTest.Fixture memoryFixture(ChatTaskQueue queue) throws Exception {
        return new ChatServiceMemoryTest.Fixture(queue);
    }

    private Worker worker(RedissonClient client, String id) {
        Worker worker = unstartedWorker(client, id);
        worker.queue.startWorker(worker.executor);
        return worker;
    }

    private Worker unstartedWorker(RedissonClient client, String id) {
        ObservedLock lock = new ObservedLock(client.getLock(PREFIX + id));
        Worker worker = new Worker(new ChatTaskQueue(id, lock), lock, executor());
        workers.add(worker);
        return worker;
    }

    private ExecutorService executor() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executors.add(executor);
        return executor;
    }

    private ChatTaskQueueManager manager(RedissonClient client) {
        ChatTaskQueueManager manager = new ChatTaskQueueManager(client);
        managers.add(manager);
        return manager;
    }

    private static String chatId() {
        return UUID.randomUUID().toString();
    }

    private static void await(CountDownLatch latch) throws InterruptedException {
        assertTrue(latch.await(WAIT_SECONDS, TimeUnit.SECONDS), "Expected controlled test event");
    }

    private static void awaitGate(CountDownLatch latch) {
        try {
            assertTrue(latch.await(15, TimeUnit.SECONDS), "Test did not release task/callback");
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
            throw new AssertionError("Test task/callback interrupted", interrupted);
        }
    }

    private static <T> T get(Future<T> future) throws Exception {
        return future.get(WAIT_SECONDS, TimeUnit.SECONDS);
    }

    private static void assertBlocked(Future<?> future) {
        assertThrows(TimeoutException.class, () -> future.get(150, TimeUnit.MILLISECONDS));
    }

    private static void rejected(ChatTask<?> task) {
        ExecutionException failure = assertThrows(ExecutionException.class, () -> get(task.future()));
        assertInstanceOf(ChatQueueRejectedException.class, failure.getCause());
    }

    private static void assertWorkerUnlocked(Worker worker) throws Exception {
        assertSame(get(worker.lock.acquired), get(worker.lock.unlocked), "Only the owning worker may unlock");
        assertFalse(get(worker.lock.unlockInterrupted), "Redis unlock must run with interruption cleared");
    }

    private static void assertWorkerExited(Worker worker) throws Exception {
        // A sentinel cannot run on this single-thread executor until workerLoop returns naturally.
        worker.executor.submit(() -> {}).get(7, TimeUnit.SECONDS); // Includes the queue's five-second idle poll.
    }

    private record Worker(ChatTaskQueue queue, ObservedLock lock, ExecutorService executor) {}

    private static final class ObservedLock implements Lock {
        private final RLock delegate;
        private final CountDownLatch attempted = new CountDownLatch(1);
        private final AtomicReference<Thread> attemptThread = new AtomicReference<>();
        private final CompletableFuture<Thread> acquired = new CompletableFuture<>();
        private final CompletableFuture<Thread> unlocked = new CompletableFuture<>();
        private final CompletableFuture<Boolean> unlockInterrupted = new CompletableFuture<>();
        private final AtomicInteger unlockCalls = new AtomicInteger();

        private ObservedLock(RLock delegate) {
            this.delegate = delegate;
        }

        @Override
        public void lockInterruptibly() throws InterruptedException {
            attemptThread.set(Thread.currentThread());
            attempted.countDown();
            delegate.lockInterruptibly(); // No lease argument: real Redisson watchdog renewal.
            acquired.complete(Thread.currentThread());
        }

        @Override
        public void unlock() {
            unlockCalls.incrementAndGet();
            unlockInterrupted.complete(Thread.currentThread().isInterrupted());
            try {
                delegate.unlock();
                unlocked.complete(Thread.currentThread());
            } catch (RuntimeException failure) {
                unlocked.completeExceptionally(failure);
                throw failure;
            }
        }

        @Override
        public void lock() {
            throw new AssertionError("Queue must use lockInterruptibly");
        }

        @Override
        public boolean tryLock() {
            throw new AssertionError("Queue must wait interruptibly, not reject contention");
        }

        @Override
        public boolean tryLock(long time, TimeUnit unit) {
            throw new AssertionError("Queue must use lockInterruptibly without a fixed lease");
        }

        @Override
        public Condition newCondition() {
            throw new UnsupportedOperationException();
        }
    }

    private static final class ControlledStream {
        private final TokenStream stream = mock(
                TokenStream.class,
                withSettings().mockMaker(org.mockito.MockMakers.SUBCLASS).defaultAnswer(RETURNS_SELF));
        private final AtomicReference<Consumer<ChatResponse>> complete = new AtomicReference<>();
        private final AtomicReference<Consumer<Throwable>> error = new AtomicReference<>();
        private final CountDownLatch started = new CountDownLatch(1);

        private ControlledStream() {
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
            doAnswer(call -> {
                        started.countDown();
                        return null;
                    })
                    .when(stream)
                    .start();
        }

        private void terminate(String terminal) {
            if (terminal.equals("complete")) {
                complete.get()
                        .accept(ChatResponse.builder()
                                .aiMessage(AiMessage.from("synthetic response"))
                                .build());
            } else {
                error.get().accept(new IllegalStateException("synthetic stream error"));
            }
        }
    }

    private static final class PausingQueue extends LinkedBlockingQueue<ChatTask<?>> {
        private final CountDownLatch addEntered = new CountDownLatch(1);
        private final CountDownLatch allowAdd = new CountDownLatch(1);

        @Override
        public boolean add(ChatTask<?> task) {
            addEntered.countDown();
            awaitGate(allowAdd);
            return super.add(task);
        }
    }
}
