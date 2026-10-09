package fun.freechat;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fun.freechat.service.channel.ChannelPollingLease;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
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
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/** Private Redis only; no Spring context, application data directory, or live provider. */
@Testcontainers
@Timeout(30)
class ChannelPollingLeaseIT {
    private static final long WAIT_SECONDS = 5;
    private static final long WATCHDOG_MS = 3_000;
    private static final Duration CHECK_INTERVAL = Duration.ofMillis(20);

    @Container
    private static final GenericContainer<?> REDIS =
            new GenericContainer<>("redis:7.4.2-alpine").withExposedPorts(6379);

    private static RedissonClient firstClient;
    private static RedissonClient secondClient;
    private final List<ChannelPollingLease> leases = new ArrayList<>();

    @BeforeAll
    static void connectToPrivateRedis() {
        firstClient = newClient();
        secondClient = newClient();
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
    void settleOwnedLeases() throws Exception {
        leases.forEach(ChannelPollingLease::close);
        for (ChannelPollingLease lease : leases) {
            lease.settled()
                    .handle((ignored, failure) -> null)
                    .toCompletableFuture()
                    .get(WAIT_SECONDS, TimeUnit.SECONDS);
        }
        assertFalse(firstClient.isShutdown());
        assertFalse(secondClient.isShutdown());
    }

    @AfterAll
    static void closeOwnedClients() {
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
    void twoClientsExcludeThroughWatchdogRenewalAndCrossThreadCleanupThenTakeOver() throws Exception {
        String name = lockName();
        ObservedOwner first = new ObservedOwner(firstClient, name);
        CountDownLatch closing = new CountDownLatch(1);
        CountDownLatch finishClose = new CountDownLatch(1);
        AtomicInteger lost = new AtomicInteger();
        AtomicInteger closed = new AtomicInteger();
        ChannelPollingLease leader = start(
                first,
                () -> () -> {
                    first.assertOwner();
                    assertEquals(1, lost.get());
                    assertTrue(first.delegate.isHeldByCurrentThread());
                    closing.countDown();
                    gate(finishClose);
                    closed.incrementAndGet();
                },
                lost::incrementAndGet,
                CHECK_INTERVAL);
        try {
            active(leader);
            ChannelPollingLease follower =
                    start(new ObservedOwner(secondClient, name), () -> fail("Concurrent polling"));
            settle(follower);
            assertFalse(follower.isActive());
            assertThrows(
                    TimeoutException.class,
                    () -> leader.settled().toCompletableFuture().get(WATCHDOG_MS + 1_000, TimeUnit.MILLISECONDS));
            assertTrue(first.delegate.isHeldByThread(first.owner.get().threadId()));
            assertTrue(first.delegate.remainTimeToLive() > 0, "Watchdog must renew a long-lived lease");
            assertTrue(first.checks.get() > 2, "Owner must revalidate rather than trust a cached registration");
            closeElsewhere(leader);
            gate(closing);
            assertFalse(leader.isActive());
            assertFalse(leader.settled().toCompletableFuture().isDone());
            assertEquals(0, first.unlocks.get());
            ChannelPollingLease duringCleanup =
                    start(new ObservedOwner(secondClient, name), () -> fail("Cleanup still owns lock"));
            settle(duringCleanup);
            finishClose.countDown();
            settle(leader);
            assertEquals(1, first.unlocks.get());
            assertEquals(1, closed.get());
            ChannelPollingLease successor = start(new ObservedOwner(secondClient, name), () -> {
                assertEquals(1, closed.get(), "Old registration must close before successor activation");
                return () -> {};
            });
            active(successor);
        } finally {
            finishClose.countDown();
        }
    }

    @Test
    void detectedOwnershipLossRevokesAndClosesWithoutDeletingSuccessor() throws Exception {
        String name = lockName();
        ObservedOwner first = new ObservedOwner(firstClient, name);
        CountDownLatch closing = new CountDownLatch(1);
        CountDownLatch finishClose = new CountDownLatch(1);
        AtomicInteger lost = new AtomicInteger();
        ChannelPollingLease leader = start(
                first,
                () -> () -> {
                    first.assertOwner();
                    assertEquals(1, lost.get());
                    closing.countDown();
                    gate(finishClose);
                },
                lost::incrementAndGet,
                CHECK_INTERVAL);
        try {
            active(leader);
            // Simulate expiration of this test's unique key, never delete application/shared keys.
            assertEquals(1, firstClient.getKeys().delete(name));
            gate(closing);
            assertFalse(leader.isActive());
            ObservedOwner next = new ObservedOwner(secondClient, name);
            ChannelPollingLease successor = start(next, () -> () -> {});
            active(successor);
            finishClose.countDown();
            settle(leader);
            assertEquals(0, first.unlocks.get(), "An observed stale owner must not even try to unlock");
            assertTrue(next.delegate.isHeldByThread(next.owner.get().threadId()));
            assertTrue(successor.isActive());
        } finally {
            finishClose.countDown();
        }
    }

    @Test
    void ownershipChangeBetweenFinalCheckAndNormalUnlockCannotDeleteSuccessor() throws Exception {
        String name = lockName();
        ObservedOwner first = new ObservedOwner(firstClient, name);
        CountDownLatch unlocking = new CountDownLatch(1);
        CountDownLatch finishUnlock = new CountDownLatch(1);
        first.beforeUnlock = () -> {
            unlocking.countDown();
            gate(finishUnlock);
            return null;
        };
        ChannelPollingLease leader = start(first, () -> () -> {});
        try {
            active(leader);
            closeElsewhere(leader);
            gate(unlocking); // The helper has already checked isHeldByCurrentThread() == true.
            assertEquals(1, firstClient.getKeys().delete(name));
            ObservedOwner next = new ObservedOwner(secondClient, name);
            ChannelPollingLease successor = start(next, () -> () -> {});
            active(successor);
            assertFalse(leader.settled().toCompletableFuture().isDone());
            finishUnlock.countDown();
            settle(leader); // Redisson's atomic owner check rejects the now-stale unlock.
            assertEquals(1, first.unlocks.get());
            assertTrue(next.delegate.isHeldByThread(next.owner.get().threadId()));
            assertTrue(successor.isActive());
        } finally {
            finishUnlock.countDown();
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void failedRegistrationSettlesAfterOwnLockReleaseAndPermitsTakeover(boolean nullRegistration) throws Exception {
        String name = lockName();
        ObservedOwner first = new ObservedOwner(firstClient, name);
        AtomicInteger lost = new AtomicInteger();
        ChannelPollingLease failed = start(
                first,
                () -> {
                    assertTrue(first.delegate.isHeldByCurrentThread());
                    if (nullRegistration) {
                        return null;
                    }
                    throw new IllegalStateException("fabricated private provider payload");
                },
                lost::incrementAndGet,
                CHECK_INTERVAL);
        Throwable failure =
                assertThrows(ExecutionException.class, () -> settle(failed)).getCause();
        assertEquals("Polling lease failed", failure.getMessage());
        assertNull(failure.getCause());
        assertEquals(1, first.unlocks.get());
        assertEquals(1, lost.get());
        assertFalse(failed.isActive());
        ChannelPollingLease successor = start(new ObservedOwner(secondClient, name), () -> () -> {});
        active(successor);
    }

    @Test
    void lateActivationAfterCrossThreadCloseClosesOnOriginalOwnerBeforeUnlock() throws Exception {
        String name = lockName();
        ObservedOwner first = new ObservedOwner(firstClient, name);
        CountDownLatch activating = new CountDownLatch(1);
        CountDownLatch finishActivation = new CountDownLatch(1);
        AtomicInteger closed = new AtomicInteger();
        AtomicInteger lost = new AtomicInteger();
        AtomicReference<ChannelPollingLease> reference = new AtomicReference<>();
        ChannelPollingLease lease = start(
                first,
                () -> {
                    first.assertOwner();
                    activating.countDown();
                    gate(finishActivation);
                    return () -> {
                        first.assertOwner();
                        assertFalse(reference.get().isActive());
                        assertEquals(1, lost.get());
                        assertTrue(first.delegate.isHeldByCurrentThread());
                        closed.incrementAndGet();
                    };
                },
                lost::incrementAndGet,
                Duration.ofHours(1));
        reference.set(lease);
        try {
            gate(activating);
            closeElsewhere(lease);
            assertFalse(lease.isActive());
            assertFalse(lease.settled().toCompletableFuture().isDone());
            ChannelPollingLease follower =
                    start(new ObservedOwner(secondClient, name), () -> fail("Activation still owns lock"));
            settle(follower);
            finishActivation.countDown();
            settle(lease);
            assertEquals(1, closed.get());
            assertEquals(1, first.unlocks.get());
            ChannelPollingLease successor = start(new ObservedOwner(secondClient, name), () -> () -> {});
            active(successor);
        } finally {
            finishActivation.countDown();
        }
    }

    private ChannelPollingLease start(ObservedOwner observer, Callable<? extends AutoCloseable> acquired) {
        return start(observer, acquired, () -> {}, CHECK_INTERVAL);
    }

    private ChannelPollingLease start(
            ObservedOwner observer, Callable<? extends AutoCloseable> acquired, Runnable lost, Duration interval) {
        ChannelPollingLease lease = ChannelPollingLease.start(observer.client, observer.name, acquired, lost, interval);
        leases.add(lease);
        return lease;
    }

    private static String lockName() {
        return "channel-polling-it:" + UUID.randomUUID();
    }

    private static void active(ChannelPollingLease lease) {
        await().atMost(Duration.ofSeconds(WAIT_SECONDS))
                .pollInterval(CHECK_INTERVAL)
                .until(lease::isActive);
    }

    private static void gate(CountDownLatch gate) throws InterruptedException {
        assertTrue(gate.await(WAIT_SECONDS, TimeUnit.SECONDS), "Test did not release owner");
    }

    private static void settle(ChannelPollingLease lease) throws Exception {
        lease.settled().toCompletableFuture().get(WAIT_SECONDS, TimeUnit.SECONDS);
    }

    private static void closeElsewhere(ChannelPollingLease lease) throws Exception {
        CompletableFuture<Void> requested = new CompletableFuture<>();
        Thread.ofVirtual().start(() -> {
            lease.close();
            requested.complete(null);
        });
        requested.get(WAIT_SECONDS, TimeUnit.SECONDS);
    }

    /** Observe actual Redis operations without changing their locking semantics. */
    private static final class ObservedOwner {
        private final String name;
        private final RedissonClient client =
                mock(RedissonClient.class, withSettings().mockMaker(org.mockito.MockMakers.PROXY));
        private final RLock delegate;
        private final AtomicReference<Thread> owner = new AtomicReference<>();
        private final AtomicInteger checks = new AtomicInteger();
        private final AtomicInteger unlocks = new AtomicInteger();
        private Callable<Void> beforeUnlock = () -> null;

        private ObservedOwner(RedissonClient realClient, String name) {
            this.name = name;
            delegate = realClient.getLock(name);
            RLock observed = mock(RLock.class, withSettings().mockMaker(org.mockito.MockMakers.PROXY));
            when(client.getLock(name)).thenReturn(observed);
            when(observed.tryLock()).thenAnswer(call -> {
                assertTrue(Thread.currentThread().isVirtual());
                owner.set(Thread.currentThread());
                return delegate.tryLock();
            });
            when(observed.isHeldByCurrentThread()).thenAnswer(call -> {
                assertOwner();
                checks.incrementAndGet();
                return delegate.isHeldByCurrentThread();
            });
            doAnswer(call -> {
                        assertOwner();
                        beforeUnlock.call();
                        unlocks.incrementAndGet();
                        delegate.unlock();
                        return null;
                    })
                    .when(observed)
                    .unlock();
        }

        private void assertOwner() {
            assertSame(owner.get(), Thread.currentThread(), "Acquire, check, close and unlock must share an owner");
            assertFalse(Thread.currentThread().isInterrupted());
        }
    }
}
