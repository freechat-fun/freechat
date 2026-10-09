package fun.freechat;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fun.freechat.service.channel.ChannelPollingLease;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

@Timeout(15)
class ChannelPollingLeaseTest {
    private static final Duration INTERVAL = Duration.ofMillis(10);
    private static final String LOCK_NAME = "private-polling-test-lock";
    private static final String PRIVATE_FAILURE = "fabricated-provider-token-and-response-body";
    private final RedissonClient client =
            mock(RedissonClient.class, withSettings().mockMaker(org.mockito.MockMakers.PROXY));
    private final RLock lock = mock(RLock.class, withSettings().mockMaker(org.mockito.MockMakers.PROXY));
    private final List<ChannelPollingLease> leases = new CopyOnWriteArrayList<>();
    private final AtomicReference<Thread> owner = new AtomicReference<>();
    private final AtomicBoolean held = new AtomicBoolean(true);
    private final AtomicInteger lost = new AtomicInteger();
    private final AtomicInteger closed = new AtomicInteger();

    ChannelPollingLeaseTest() {
        when(client.getLock(LOCK_NAME)).thenReturn(lock);
        when(lock.tryLock()).thenAnswer(call -> {
            owner.set(Thread.currentThread());
            assertTrue(Thread.currentThread().isVirtual());
            return true;
        });
        when(lock.isHeldByCurrentThread()).thenAnswer(call -> {
            assertOwner();
            return held.get();
        });
        doAnswer(call -> {
                    assertOwner();
                    assertEquals(1, lost.get(), "Revoke before unlocking");
                    held.set(false);
                    return null;
                })
                .when(lock)
                .unlock();
    }

    @AfterEach
    void settleOwnedLeases() throws Exception {
        leases.forEach(ChannelPollingLease::close);
        for (ChannelPollingLease lease : leases) {
            lease.settled()
                    .handle((ignored, failure) -> null)
                    .toCompletableFuture()
                    .get(5, TimeUnit.SECONDS);
        }
        verify(client, never()).shutdown();
    }

    @Test
    void contentionSettlesWithoutRegisteringRetryingOrUnlocking() throws Exception {
        doReturn(false).when(lock).tryLock();
        ChannelPollingLease lease = start(() -> fail("A follower must not register"));
        settle(lease);
        assertFalse(lease.isActive());
        assertEquals(1, lost.get());
        verify(lock).tryLock();
        verify(lock, never()).isHeldByCurrentThread();
        verify(lock, never()).unlock();
    }

    @ParameterizedTest
    @ValueSource(strings = {"throw", "null", "error"})
    void failedRegistrationRevokesAndReleasesOwnLockWithSanitizedSettlement(String failure) throws Exception {
        ChannelPollingLease lease = start(() -> switch (failure) {
            case "throw" -> throw new IllegalStateException(PRIVATE_FAILURE);
            case "error" -> throw new AssertionError(PRIVATE_FAILURE);
            default -> null;
        });
        assertSafeFailure(lease);
        assertFalse(lease.isActive());
        assertEquals(1, lost.get());
        assertEquals(0, closed.get());
        verify(lock).unlock();
    }

    @Test
    void uncertainAcquisitionOnlyUnlocksAfterSameOwnerVerification() throws Exception {
        doAnswer(call -> {
                    owner.set(Thread.currentThread());
                    throw new IllegalStateException(PRIVATE_FAILURE);
                })
                .when(lock)
                .tryLock();
        ChannelPollingLease lease = start(() -> fail("Uncertain acquisition must not register"));
        assertSafeFailure(lease);
        assertEquals(1, lost.get());
        verify(lock).isHeldByCurrentThread();
        verify(lock).unlock();
    }

    @Test
    void uncertaintyDuringAcquisitionAndCleanupNeverAttemptsUnlock() throws Exception {
        doThrow(new IllegalStateException(PRIVATE_FAILURE)).when(lock).tryLock();
        doThrow(new IllegalStateException(PRIVATE_FAILURE)).when(lock).isHeldByCurrentThread();
        ChannelPollingLease lease = start(() -> fail("Uncertain acquisition must not register"));
        assertSafeFailure(lease);
        assertEquals(1, lost.get());
        verify(lock, never()).unlock();
    }

    @Test
    void closeWhileAcquiringNeverRegistersEvenIfAcquisitionCompletesLate() throws Exception {
        CountDownLatch acquiring = new CountDownLatch(1);
        CountDownLatch finishAcquiring = new CountDownLatch(1);
        doAnswer(call -> {
                    owner.set(Thread.currentThread());
                    acquiring.countDown();
                    gate(finishAcquiring);
                    return true;
                })
                .when(lock)
                .tryLock();
        ChannelPollingLease lease = start(() -> fail("Closed lease must not register"));
        try {
            gate(acquiring);
            closeElsewhere(lease);
            assertFalse(lease.isActive());
            assertFalse(lease.settled().toCompletableFuture().isDone());
        } finally {
            finishAcquiring.countDown();
        }
        settle(lease);
        verify(lock).unlock();
    }

    @Test
    void closeDuringActivationClosesEventualRegistrationWithoutPublishingActive() throws Exception {
        CountDownLatch activating = new CountDownLatch(1);
        CountDownLatch finishActivation = new CountDownLatch(1);
        ChannelPollingLease lease = start(() -> {
            activating.countDown();
            gate(finishActivation);
            assertOwner();
            return this::closeRegistration;
        });
        try {
            gate(activating);
            closeElsewhere(lease);
            assertFalse(lease.isActive());
            assertFalse(lease.settled().toCompletableFuture().isDone());
            verify(lock, never()).unlock();
        } finally {
            finishActivation.countDown();
        }
        settle(lease);
        assertFalse(lease.isActive());
        assertEquals(1, closed.get());
        verify(lock).unlock();
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void ownershipAbsentOrUncertainAfterActivationCannotPublishActive(boolean uncertain) throws Exception {
        AtomicBoolean activated = new AtomicBoolean();
        doAnswer(call -> {
                    assertOwner();
                    if (activated.get()) {
                        if (uncertain) {
                            throw new IllegalStateException(PRIVATE_FAILURE);
                        }
                        return false;
                    }
                    return true;
                })
                .when(lock)
                .isHeldByCurrentThread();
        ChannelPollingLease lease = start(() -> {
            activated.set(true);
            return this::closeRegistration;
        });
        if (uncertain) {
            assertSafeFailure(lease);
        } else {
            settle(lease);
        }
        assertFalse(lease.isActive());
        assertEquals(1, lost.get());
        assertEquals(1, closed.get());
        verify(lock, never()).unlock();
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void periodicOwnershipLossOrRedisFailureRevokesBeforeStoppingRegistration(boolean uncertain) throws Exception {
        AtomicBoolean loseOwnership = new AtomicBoolean();
        doAnswer(call -> {
                    assertOwner();
                    if (loseOwnership.get()) {
                        if (uncertain) {
                            throw new IllegalStateException(PRIVATE_FAILURE);
                        }
                        return false;
                    }
                    return true;
                })
                .when(lock)
                .isHeldByCurrentThread();
        ChannelPollingLease lease = start(() -> this::closeRegistration);
        active(lease);
        loseOwnership.set(true);
        if (uncertain) {
            assertSafeFailure(lease);
        } else {
            settle(lease);
        }
        assertFalse(lease.isActive());
        assertEquals(1, lost.get());
        assertEquals(1, closed.get());
        verify(lock, never()).unlock();
    }

    @ParameterizedTest
    @ValueSource(strings = {"revocation", "registration"})
    void callbackCleanupFailuresCannotSkipRemainingCleanup(String failure) throws Exception {
        ChannelPollingLease lease = start(
                () -> () -> {
                    closeRegistration();
                    if (failure.equals("registration")) {
                        throw new IllegalStateException(PRIVATE_FAILURE);
                    }
                },
                () -> {
                    lost.incrementAndGet();
                    if (failure.equals("revocation")) {
                        throw new IllegalStateException(PRIVATE_FAILURE);
                    }
                },
                INTERVAL);
        active(lease);
        closeElsewhere(lease);
        assertSafeFailure(lease);
        assertEquals(1, closed.get());
        verify(lock).unlock();
    }

    @Test
    void crossThreadCloseWakesLongIntervalButSettlementCannotOvertakeCleanup() throws Exception {
        CountDownLatch closing = new CountDownLatch(1);
        CountDownLatch finishClose = new CountDownLatch(1);
        ChannelPollingLease lease = start(
                () -> () -> {
                    closing.countDown();
                    gate(finishClose);
                    closeRegistration();
                },
                lost::incrementAndGet,
                Duration.ofHours(1));
        active(lease);
        try {
            closeElsewhere(lease);
            gate(closing);
            assertFalse(lease.isActive());
            CompletableFuture<Void> callerView = lease.settled().toCompletableFuture();
            assertFalse(callerView.isDone());
            assertTrue(callerView.cancel(true));
            assertFalse(lease.settled().toCompletableFuture().isDone(), "Caller cannot cancel real settlement");
            closeElsewhere(lease); // Repeated shutdown must not interrupt cleanup.
            assertEquals(1, lost.get());
            verify(lock, never()).unlock();
        } finally {
            finishClose.countDown();
        }
        settle(lease);
        assertEquals(1, closed.get());
        verify(lock).unlock();
    }

    @Test
    void ownerInterruptTriggersRevocationAndIsClearedForCleanupAndOwnedUnlock() throws Exception {
        ChannelPollingLease lease = start(
                () -> this::closeRegistration,
                () -> {
                    assertOwner();
                    lost.incrementAndGet();
                    Thread.currentThread().interrupt(); // Also clear interrupts raised by cleanup callbacks.
                },
                Duration.ofHours(1));
        active(lease);
        owner.get().interrupt();
        settle(lease);
        assertFalse(lease.isActive());
        assertEquals(1, closed.get());
        verify(lock).unlock();
    }

    @Test
    void invalidArgumentsDoNotStartAnOwner() {
        assertThrows(
                NullPointerException.class,
                () -> ChannelPollingLease.start(null, LOCK_NAME, () -> () -> {}, () -> {}, INTERVAL));
        assertThrows(
                IllegalArgumentException.class,
                () -> ChannelPollingLease.start(client, " ", () -> () -> {}, () -> {}, INTERVAL));
        assertThrows(
                NullPointerException.class,
                () -> ChannelPollingLease.start(client, LOCK_NAME, null, () -> {}, INTERVAL));
        assertThrows(
                NullPointerException.class,
                () -> ChannelPollingLease.start(client, LOCK_NAME, () -> () -> {}, null, INTERVAL));
        for (Duration interval : List.of(Duration.ZERO, Duration.ofNanos(-1), Duration.ofSeconds(Long.MAX_VALUE))) {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> ChannelPollingLease.start(client, LOCK_NAME, () -> () -> {}, () -> {}, interval));
        }
        verifyNoInteractions(client);
    }

    private ChannelPollingLease start(Callable<? extends AutoCloseable> acquired) {
        return start(acquired, lost::incrementAndGet, INTERVAL);
    }

    private ChannelPollingLease start(Callable<? extends AutoCloseable> acquired, Runnable revoked, Duration interval) {
        ChannelPollingLease lease = ChannelPollingLease.start(client, LOCK_NAME, acquired, revoked, interval);
        leases.add(lease);
        return lease;
    }

    private void assertOwner() {
        assertSame(owner.get(), Thread.currentThread());
        assertFalse(Thread.currentThread().isInterrupted(), "Cleanup and Redis operations need a clear interrupt");
    }

    private void closeRegistration() {
        assertOwner();
        assertEquals(1, lost.get(), "Revoke admission before provider cleanup");
        assertTrue(leases.stream().noneMatch(ChannelPollingLease::isActive));
        closed.incrementAndGet();
    }

    private static void active(ChannelPollingLease lease) {
        await().atMost(Duration.ofSeconds(5)).pollInterval(INTERVAL).until(lease::isActive);
    }

    private static void closeElsewhere(ChannelPollingLease lease) throws Exception {
        CompletableFuture<Void> requested = new CompletableFuture<>();
        Thread.ofVirtual().start(() -> {
            lease.close();
            requested.complete(null);
        });
        requested.get(5, TimeUnit.SECONDS);
    }

    private static void gate(CountDownLatch gate) throws InterruptedException {
        assertTrue(gate.await(5, TimeUnit.SECONDS), "Test did not release owner");
    }

    private static void settle(ChannelPollingLease lease) throws Exception {
        lease.settled().toCompletableFuture().get(5, TimeUnit.SECONDS);
    }

    private static void assertSafeFailure(ChannelPollingLease lease) {
        Throwable failure =
                assertThrows(ExecutionException.class, () -> settle(lease)).getCause();
        assertInstanceOf(IllegalStateException.class, failure);
        assertEquals("Polling lease failed", failure.getMessage());
        assertNull(failure.getCause());
        assertEquals(0, failure.getSuppressed().length);
    }
}
