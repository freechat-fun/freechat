package fun.freechat.service.channel;

import static org.junit.jupiter.api.Assertions.*;

import fun.freechat.channels.spi.ChannelFailure;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@Timeout(15)
class ChannelTaskSchedulerTest {
    @Test
    void rejectsInvalidLimits() {
        assertThrows(IllegalArgumentException.class, () -> new ChannelTaskScheduler<>("test", 0, 1, 1));
        assertThrows(IllegalArgumentException.class, () -> new ChannelTaskScheduler<>("test", 2, 1, 1));
        assertThrows(IllegalArgumentException.class, () -> new ChannelTaskScheduler<>("test", 1, 1, 0));
        assertThrows(IllegalArgumentException.class, () -> new ChannelTaskScheduler<>("test", 1, 1, 2));
    }

    @Test
    void interruptingAnObserverCannotProvePhysicalWorkHasSettled() throws Exception {
        try (Fixture fixture = new Fixture(1, 2, 2)) {
            var physical = fixture.gate();
            var worker = new java.util.concurrent.atomic.AtomicReference<Thread>();
            var entered = new CountDownLatch(1);
            var first = fixture.scheduler.submit(
                    "same",
                    this,
                    () -> {
                        worker.set(Thread.currentThread());
                        entered.countDown();
                        return physical;
                    },
                    () -> worker.get().interrupt());
            await(entered);
            first.cancel(false);
            var following =
                    fixture.scheduler.submit("same", this, () -> CompletableFuture.completedFuture(null), () -> {});
            assertThrows(java.util.concurrent.TimeoutException.class, () -> following.get(50, TimeUnit.MILLISECONDS));
            assertEquals(1, fixture.scheduler.activeCount());
            assertEquals(2, fixture.scheduler.pendingCount());
            physical.complete(null);
            get(following);
        }
    }

    @Test
    void holdsFifoUntilAsyncCompletionButRunsAnotherKeyConcurrently() throws Exception {
        try (Fixture fixture = new Fixture(2, 6, 3)) {
            var scheduler = fixture.scheduler;
            var firstStage = fixture.gate();
            var secondStage = fixture.gate();
            var started = new CountDownLatch(1);
            var secondStarted = new CountDownLatch(1);
            List<String> order = new CopyOnWriteArrayList<>();
            var first = scheduler.submit(
                    "a",
                    this,
                    () -> {
                        order.add("a1");
                        started.countDown();
                        return firstStage;
                    },
                    () -> {});
            await(started);
            var second = scheduler.submit(
                    "a",
                    this,
                    () -> {
                        order.add("a2");
                        secondStarted.countDown();
                        return secondStage;
                    },
                    () -> {});
            var third = scheduler.submit(
                    "a",
                    this,
                    () -> {
                        order.add("a3");
                        return CompletableFuture.completedFuture(null);
                    },
                    () -> {});
            get(scheduler.submit(
                    "b",
                    this,
                    () -> {
                        order.add("b1");
                        return CompletableFuture.completedFuture(null);
                    },
                    () -> {}));
            assertEquals(List.of("a1", "b1"), order);
            assertEquals(3, scheduler.pendingCount());
            assertEquals(1, scheduler.activeCount());
            firstStage.complete(null);
            get(first);
            await(secondStarted);
            assertFalse(third.isDone(), "The asynchronous second turn must still own its lane");
            secondStage.complete(null);
            get(second);
            get(third);
            assertEquals(List.of("a1", "b1", "a2", "a3"), order);
            assertEquals(0, scheduler.pendingCount());
            assertEquals(0, scheduler.activeCount());
        }
    }

    @Test
    void perLaneAndGlobalCapacityIncludeActiveWorkAndAreReusable() throws Exception {
        try (Fixture fixture = new Fixture(1, 3, 2)) {
            var scheduler = fixture.scheduler;
            var gate = fixture.gate();
            var started = new CountDownLatch(1);
            var active = scheduler.submit(
                    "a",
                    this,
                    () -> {
                        started.countDown();
                        return gate;
                    },
                    () -> {});
            await(started);
            var sameLane = scheduler.submit("a", this, () -> CompletableFuture.completedFuture(null), () -> {});
            assertFailure(
                    ChannelFailure.Kind.REJECTED,
                    scheduler.submit("a", this, () -> fail("Lane overflow executed"), () -> {}));
            var otherLane = scheduler.submit("b", this, () -> CompletableFuture.completedFuture(null), () -> {});
            assertEquals(3, scheduler.pendingCount());
            assertEquals(1, scheduler.activeCount());
            assertFailure(
                    ChannelFailure.Kind.REJECTED,
                    scheduler.submit("c", this, () -> fail("Global overflow executed"), () -> {}));
            sameLane.cancel(false);
            assertEquals(2, scheduler.pendingCount());
            var replacement = scheduler.submit("c", this, () -> CompletableFuture.completedFuture(null), () -> {});
            gate.complete(null);
            get(active);
            get(otherLane);
            get(replacement);
            for (int index = 0; index < 20; index++) {
                assertEquals(
                        1,
                        get(scheduler.submit(
                                "a",
                                this,
                                () -> CompletableFuture.completedFuture(scheduler.pendingCount()),
                                () -> {})));
            }
            assertEquals(0, scheduler.pendingCount());
        }
    }

    @Test
    void cancelledQueuedOwnerCannotRemoveSuccessorLaneWithTheSameKey() throws Exception {
        try (Fixture fixture = new Fixture(1, 4, 2)) {
            var scheduler = fixture.scheduler;
            var gate = fixture.gate();
            var started = new CountDownLatch(1);
            var blocker = scheduler.submit(
                    "blocker",
                    this,
                    () -> {
                        started.countDown();
                        return gate;
                    },
                    () -> {});
            await(started);
            Object oldOwner = new String("owner");
            Object newOwner = new String("owner");
            AtomicInteger cancelled = new AtomicInteger();
            var old = scheduler.submit(
                    "reused", oldOwner, () -> fail("Revoked queued owner ran"), cancelled::incrementAndGet);
            scheduler.cancelOwner(oldOwner);
            assertTrue(old.isCancelled());
            assertEquals(1, scheduler.pendingCount());
            var replacement = scheduler.submit(
                    "reused",
                    newOwner,
                    () -> CompletableFuture.completedFuture("new"),
                    () -> fail("New owner was cancelled"));
            scheduler.cancelOwner(oldOwner);
            assertFalse(replacement.isCancelled(), "Owner matching must use generation identity, not equals");
            assertEquals(1, cancelled.get());
            gate.complete(null);
            get(blocker);
            assertEquals("new", get(replacement));
            assertEquals(0, scheduler.pendingCount());
        }
    }

    @Test
    void cancelledActiveFutureSignalsCleanupWithoutReleasingPhysicalSlot() throws Exception {
        try (Fixture fixture = new Fixture(1, 3, 2)) {
            var scheduler = fixture.scheduler;
            var cleanup = fixture.gate();
            var started = new CountDownLatch(1);
            AtomicInteger cancellation = new AtomicInteger();
            var active = scheduler.submit(
                    "a",
                    this,
                    () -> {
                        started.countDown();
                        return cleanup;
                    },
                    cancellation::incrementAndGet);
            await(started);
            var successor =
                    scheduler.submit("a", new Object(), () -> CompletableFuture.completedFuture("next"), () -> {});
            var other = scheduler.submit("b", new Object(), () -> CompletableFuture.completedFuture("other"), () -> {});
            assertTrue(active.cancel(false));
            scheduler.cancelOwner(this);
            assertEquals(1, cancellation.get());
            assertFalse(cleanup.isDone(), "Cancellation is a request, not proof of settlement");
            assertEquals(1, scheduler.activeCount());
            assertEquals(3, scheduler.pendingCount());
            assertFalse(successor.isDone());
            assertFalse(other.isDone());
            assertFailure(
                    ChannelFailure.Kind.REJECTED,
                    scheduler.submit("c", this, () -> fail("Cancelled I/O released capacity early"), () -> {}));
            cleanup.complete(null);
            assertEquals("next", get(successor));
            assertEquals("other", get(other));
            assertEquals(0, scheduler.pendingCount());
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void trackedPhysicalResultSurvivesOwnerOrPublicObservationCancellation(boolean cancelObservation) throws Exception {
        try (Fixture fixture = new Fixture(1, 2, 2)) {
            var release = fixture.gate();
            var entered = new CountDownLatch(1);
            AtomicInteger requests = new AtomicInteger();
            var operation = fixture.scheduler.submitTracked(
                    "a",
                    this,
                    () -> {
                        entered.countDown();
                        return release.thenApply(ignored -> "accepted");
                    },
                    requests::incrementAndGet);
            await(entered);
            var detached = operation.physicalResult().toCompletableFuture();
            assertTrue(detached.cancel(false));
            if (cancelObservation) {
                assertTrue(operation.future().cancel(false));
            }
            fixture.scheduler.cancelOwner(this);
            assertEquals(1, requests.get());
            assertEquals(cancelObservation, operation.future().isDone());
            assertFalse(operation.physicalResult().toCompletableFuture().isDone());
            assertEquals(1, fixture.scheduler.pendingCount());
            release.complete(null);
            assertEquals("accepted", get(operation.physicalResult().toCompletableFuture()));
            if (cancelObservation) {
                assertTrue(operation.future().isCancelled());
            } else {
                assertEquals("accepted", get(operation.future()));
            }
        }
    }

    @Test
    void trackedQueuedCancellationSettlesWithoutInvocationAndDoesNotCancelReplacementGeneration() throws Exception {
        try (Fixture fixture = new Fixture(1, 3, 2)) {
            var release = fixture.gate();
            var entered = new CountDownLatch(1);
            var blocker = fixture.scheduler.submit(
                    "blocker",
                    this,
                    () -> {
                        entered.countDown();
                        return release;
                    },
                    () -> {});
            await(entered);
            Object oldOwner = new String("generation");
            Object newOwner = new String("generation");
            var queued = fixture.scheduler.submitTracked(
                    "same", oldOwner, () -> fail("Cancelled queued operation invoked the provider"), () -> {});
            fixture.scheduler.cancelOwner(oldOwner);
            assertFailure(ChannelFailure.Kind.CANCELLED, queued.future());
            assertFailure(ChannelFailure.Kind.CANCELLED, queued.physicalResult().toCompletableFuture());
            var next = fixture.scheduler.submitTracked(
                    "same",
                    newOwner,
                    () -> CompletableFuture.completedFuture("new"),
                    () -> fail("Wrong generation cancelled"));
            fixture.scheduler.cancelOwner(oldOwner);
            queued.requestCancellation();
            assertFalse(next.future().isDone());
            release.complete(null);
            get(blocker);
            assertEquals("new", get(next.future()));
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void failedActionsDoNotPoisonTheLane(boolean asynchronous) throws Exception {
        try (Fixture fixture = new Fixture(1, 2, 2)) {
            var failedStage = fixture.gate();
            var entered = new CountDownLatch(1);
            var failed = fixture.scheduler.submit(
                    "a",
                    this,
                    () -> {
                        entered.countDown();
                        if (!asynchronous) {
                            throw new IllegalStateException("private provider details");
                        }
                        return failedStage;
                    },
                    () -> {});
            await(entered);
            var next = fixture.scheduler.submit("a", this, () -> CompletableFuture.completedFuture("ok"), () -> {});
            failedStage.completeExceptionally(new IllegalStateException("private provider details"));
            ChannelFailure failure = assertFailure(ChannelFailure.Kind.FAILED, failed);
            assertFalse(failure.getMessage().contains("private provider details"));
            assertEquals("ok", get(next));
            assertEquals(0, fixture.scheduler.pendingCount());
        }
    }

    @Test
    void readyLaneGetsATurnBeforeBusyLaneRunsItsNextEntry() throws Exception {
        try (Fixture fixture = new Fixture(1, 4, 3)) {
            var gate = fixture.gate();
            var started = new CountDownLatch(1);
            List<String> order = new CopyOnWriteArrayList<>();
            var first = fixture.scheduler.submit(
                    "a",
                    this,
                    () -> {
                        started.countDown();
                        return gate;
                    },
                    () -> {});
            await(started);
            var same = fixture.scheduler.submit(
                    "a",
                    this,
                    () -> {
                        order.add("a");
                        return CompletableFuture.completedFuture(null);
                    },
                    () -> {});
            var other = fixture.scheduler.submit(
                    "b",
                    this,
                    () -> {
                        order.add("b");
                        return CompletableFuture.completedFuture(null);
                    },
                    () -> {});
            gate.complete(null);
            get(first);
            get(same);
            get(other);
            assertEquals(List.of("b", "a"), order);
        }
    }

    @Test
    void oneThrowingCancellationHookDoesNotPreventOtherOwnerNotifications() throws Exception {
        try (Fixture fixture = new Fixture(1, 3, 3)) {
            var gate = fixture.gate();
            var started = new CountDownLatch(1);
            var active = fixture.scheduler.submit(
                    "a",
                    this,
                    () -> {
                        started.countDown();
                        return gate;
                    },
                    () -> {
                        throw new IllegalStateException("cleanup failed");
                    });
            await(started);
            AtomicInteger notifications = new AtomicInteger();
            var queued = fixture.scheduler.submit(
                    "a", this, () -> fail("Cancelled task ran"), notifications::incrementAndGet);
            fixture.scheduler.cancelOwner(this);
            assertTrue(active.isCancelled());
            assertTrue(queued.isCancelled());
            assertEquals(1, notifications.get());
            assertEquals(1, fixture.scheduler.pendingCount());
        }
    }

    @Test
    void shutdownIsBoundedCancelsPendingWorkAndRejectsNewWorkUntilCleanupSettles() throws Exception {
        try (Fixture fixture = new Fixture(1, 2, 2)) {
            var cleanup = fixture.gate();
            var started = new CountDownLatch(1);
            AtomicInteger notifications = new AtomicInteger();
            var active = fixture.scheduler.submit(
                    "a",
                    this,
                    () -> {
                        started.countDown();
                        return cleanup;
                    },
                    notifications::incrementAndGet);
            await(started);
            var queued = fixture.scheduler.submit(
                    "a", this, () -> fail("Shutdown ran queued task"), notifications::incrementAndGet);
            get(CompletableFuture.runAsync(() -> fixture.scheduler.close(Duration.ofMillis(30))));
            assertTrue(active.isCancelled());
            assertTrue(queued.isCancelled());
            assertEquals(2, notifications.get());
            assertEquals(1, fixture.scheduler.activeCount());
            assertEquals(1, fixture.scheduler.pendingCount());
            assertFailure(
                    ChannelFailure.Kind.REJECTED,
                    fixture.scheduler.submit("new", this, () -> fail("Closed scheduler accepted work"), () -> {}));
            cleanup.complete(null);
            fixture.scheduler.close(Duration.ofSeconds(2));
            assertEquals(0, fixture.scheduler.activeCount());
            assertEquals(0, fixture.scheduler.pendingCount());
        }
    }

    private static void await(CountDownLatch latch) throws InterruptedException {
        assertTrue(latch.await(3, TimeUnit.SECONDS), "Timed out waiting for controlled task");
    }

    private static <T> T get(Future<T> future) throws Exception {
        return future.get(3, TimeUnit.SECONDS);
    }

    private static ChannelFailure assertFailure(ChannelFailure.Kind kind, Future<?> future) {
        ExecutionException error = assertThrows(ExecutionException.class, () -> future.get(3, TimeUnit.SECONDS));
        ChannelFailure failure = assertInstanceOf(ChannelFailure.class, error.getCause());
        assertEquals(kind, failure.kind());
        return failure;
    }

    private static final class Fixture implements AutoCloseable {
        final ChannelTaskScheduler<String> scheduler;
        final List<CompletableFuture<Void>> gates = new CopyOnWriteArrayList<>();

        Fixture(int concurrency, int capacity, int laneCapacity) {
            scheduler = new ChannelTaskScheduler<>("channel-scheduler-test", concurrency, capacity, laneCapacity);
        }

        CompletableFuture<Void> gate() {
            var gate = new CompletableFuture<Void>();
            gates.add(gate);
            return gate;
        }

        @Override
        public void close() {
            gates.forEach(gate -> gate.complete(null));
            scheduler.close(Duration.ofSeconds(2));
        }
    }
}
