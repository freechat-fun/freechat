package fun.freechat;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fun.freechat.channels.spi.*;
import fun.freechat.channels.telegram.TelegramChannelManager;
import fun.freechat.channels.telegram.TelegramPollingSession;
import fun.freechat.mapper.CharacterBackendMapper;
import fun.freechat.model.CharacterBackend;
import fun.freechat.service.common.EncryptionService;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import org.mockito.MockMakers;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.telegram.telegrambots.meta.TelegramUrl;
import org.telegram.telegrambots.meta.api.methods.GetMe;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.message.Message;

class TelegramChannelManagerTest {
    @Test
    void activationPreservesMetadataAndNormalUnlockRunsOnTheOwner() throws Exception {
        try (var fixture = new Fixture()) {
            fixture.start();
            Registration registration = fixture.registration();
            assertTrue(registration.instance().isActive());
            assertSame(fixture.client, fixture.manager.getClient("backend"));
            assertEquals("safe_bot", fixture.manager.getUsername("backend"));
            assertEquals("https://t.me/safe_bot", fixture.manager.getInviteLink("backend"));
            fixture.manager.activate("backend");
            verify(fixture.client).execute(any(GetMe.class));
            verify(fixture.polling).start(eq(Fixture.TOKEN), any());
            fixture.manager.shutdown();
            assertFalse(registration.instance().isActive());
            assertNull(fixture.manager.getClient("backend"));
            verify(fixture.lock).unlock();
            assertSame(fixture.owner.get(), fixture.unlocker.get());
            verify(fixture.lock, never()).forceUnlock();
            verify(fixture.session).close();
        }
    }

    @Test
    void followerKeepsOutboundMetadataAndOpensInboundOnlyAfterTakeover() throws Exception {
        try (var fixture = new Fixture()) {
            fixture.available.set(false);
            fixture.start();
            fixture.awaitOwnerExit();
            assertEquals("safe_bot", fixture.manager.getUsername("backend"));
            assertSame(fixture.client, fixture.manager.getClient("backend"));
            assertTrue(fixture.instances.isEmpty());
            verify(fixture.polling, never()).start(anyString(), any());
            fixture.available.set(true);
            fixture.manager.reconcile();
            assertTrue(fixture.registration().instance().isActive());
            verify(fixture.client).execute(any(GetMe.class));
        }
    }

    @Test
    void reconciliationRetriesBotMissingAfterStartupLookupFailure() throws Exception {
        try (var fixture = new Fixture()) {
            when(fixture.client.execute(any(GetMe.class)))
                    .thenThrow(new IllegalStateException("fabricated private lookup"))
                    .thenReturn(botUser());
            fixture.start();
            assertNull(fixture.manager.getClient("backend"));
            assertTrue(fixture.instances.isEmpty());
            fixture.manager.reconcile();
            assertTrue(fixture.registration().instance().isActive());
            verify(fixture.client, times(2)).execute(any(GetMe.class));
        }
    }

    @Test
    void registrationFailureClosesGenerationAndRetriesOnReconciliation() throws Exception {
        try (var fixture = new Fixture()) {
            when(fixture.polling.start(eq(Fixture.TOKEN), any()))
                    .thenThrow(new IllegalStateException("fabricated private registration"))
                    .thenAnswer(fixture::register);
            fixture.start();
            fixture.awaitOwnerExit();
            assertEquals(1, fixture.instances.size());
            assertFalse(fixture.instances.getFirst().isActive());
            verify(fixture.lock).unlock();
            fixture.manager.reconcile();
            assertTrue(fixture.registration().instance().isActive());
            assertEquals(2, fixture.instances.size());
        }
    }

    @Test
    void rotationWaitsForRetiringPollingAndRejectsOldConsumer() throws Exception {
        try (var fixture = new Fixture()) {
            fixture.start();
            Registration old = fixture.registration();
            CountDownLatch retiring = new CountDownLatch(1);
            CountDownLatch release = new CountDownLatch(1);
            doAnswer(call -> {
                        retiring.countDown();
                        assertTrue(release.await(5, TimeUnit.SECONDS));
                        return null;
                    })
                    .when(fixture.session)
                    .close();
            try {
                fixture.token.set("fabricated-rotated-token");
                fixture.manager.activate("backend");
                assertTrue(retiring.await(5, TimeUnit.SECONDS));
                old.consumer().accept(List.of(update(1, "stale")));
                assertTrue(old.instance().received.isEmpty());
                assertNull(fixture.manager.getClient("backend"));
                fixture.manager.reconcile();
                verify(fixture.polling, never()).start(eq(fixture.token.get()), any());
            } finally {
                release.countDown();
            }
            fixture.awaitOwnerExit();
            fixture.manager.reconcile();
            Registration current = fixture.registration();
            assertNotSame(old.instance(), current.instance());
            current.consumer().accept(List.of(update(2, "current")));
            assertEquals(1, current.instance().received.size());
            verify(fixture.encryption, times(3)).decrypt("fabricated-rotated-token");
            verify(fixture.polling).start(eq("fabricated-rotated-token"), any());
            verify(fixture.client, times(2)).execute(any(GetMe.class));
        }
    }

    @Test
    void ownershipLossRevokesGenerationWithoutUnlockingSuccessor() throws Exception {
        try (var fixture = new Fixture()) {
            fixture.start();
            Registration stale = fixture.registration();
            fixture.held.set(false);
            LockSupport.unpark(fixture.owner.get());
            fixture.awaitOwnerExit();
            assertFalse(stale.instance().isActive());
            stale.consumer().accept(List.of(update(1, "stale")));
            assertTrue(stale.instance().received.isEmpty());
            verify(fixture.lock, never()).unlock();
            verify(fixture.lock, never()).forceUnlock();
            fixture.manager.reconcile();
            Registration next = fixture.registration();
            assertTrue(next.instance().isActive());
            assertNotSame(stale.instance(), next.instance());
        }
    }

    @Test
    void deletionAndStopReceivingPreventLateAdmissionAndReactivation() throws Exception {
        try (var fixture = new Fixture()) {
            fixture.start();
            Registration old = fixture.registration();
            fixture.token.set(null);
            fixture.manager.reconcile();
            fixture.awaitOwnerExit();
            assertFalse(old.instance().isActive());
            assertNull(fixture.manager.getClient("backend"));
            assertNull(fixture.manager.getUsername("backend"));
            assertNull(fixture.manager.getInviteLink("backend"));
            fixture.manager.stopReceiving();
            fixture.token.set(Fixture.TOKEN);
            fixture.manager.activate("backend");
            fixture.manager.reconcile();
            old.consumer().accept(List.of(update(1, "late")));
            assertTrue(old.instance().received.isEmpty());
            verify(fixture.polling).start(anyString(), any());
        }
    }

    @Test
    void stopDuringRegistrationClosesEventualSessionAndRejectsItsCallback() throws Exception {
        try (var fixture = new Fixture()) {
            CountDownLatch registering = new CountDownLatch(1);
            CountDownLatch release = new CountDownLatch(1);
            when(fixture.polling.start(anyString(), any())).thenAnswer(call -> {
                registering.countDown();
                assertTrue(release.await(5, TimeUnit.SECONDS));
                Consumer<List<Update>> consumer = call.getArgument(1);
                consumer.accept(List.of(update(1, "late")));
                return fixture.session;
            });
            try {
                fixture.start();
                assertTrue(registering.await(5, TimeUnit.SECONDS));
                fixture.manager.stopReceiving();
                assertFalse(fixture.instances.getFirst().isActive());
                verify(fixture.lock, never()).unlock();
            } finally {
                release.countDown();
            }
            fixture.awaitOwnerExit();
            assertTrue(fixture.instances.getFirst().received.isEmpty());
            verify(fixture.session).close();
            verify(fixture.lock).unlock();
        }
    }

    @Test
    void admissionExceptionDoesNotDiscardFollowingUpdateInSdkBatch() throws Exception {
        try (var fixture = new Fixture()) {
            fixture.start();
            Registration registration = fixture.registration();
            registration.instance().failNext.set(true);
            registration.consumer().accept(List.of(update(1, "failed"), update(2, "following")));
            assertEquals(
                    List.of(2),
                    registration.instance().received.stream()
                            .map(update -> update.getMessage().getMessageId())
                            .toList());
        }
    }

    static Update update(int id, String text) {
        Message message = new Message();
        message.setChat(new Chat(7L, "private"));
        message.setMessageId(id);
        message.setText(text);
        Update update = new Update();
        update.setUpdateId(id);
        update.setMessage(message);
        return update;
    }

    static User botUser() {
        User user = new User(1L, "Bot", true);
        user.setUserName("safe_bot");
        return user;
    }

    static <T> T stub(Class<T> type) {
        return mock(type, withSettings().mockMaker(MockMakers.SUBCLASS));
    }

    record Registration(Consumer<List<Update>> consumer, TestInstance instance) {}

    static final class TestInstance implements ChannelInstance<Update> {
        final AtomicBoolean active = new AtomicBoolean(true);
        final AtomicBoolean failNext = new AtomicBoolean();
        final List<Update> received = new CopyOnWriteArrayList<>();
        final ChannelOutbound outbound = stub(ChannelOutbound.class);
        RuntimeException admissionFailure = new IllegalStateException("fabricated private admission");

        public String id() {
            return "backend";
        }

        public boolean isActive() {
            return active.get();
        }

        public ChannelOutbound outbound(String conversation) {
            return outbound;
        }

        public void close() {
            active.set(false);
        }

        public CompletableFuture<Void> receive(String conversation, String event, Update update) {
            if (failNext.compareAndSet(true, false)) {
                throw admissionFailure;
            }
            assertEquals("7", conversation);
            received.add(update);
            return CompletableFuture.completedFuture(null);
        }
    }

    static final class Fixture implements AutoCloseable {
        static final String TOKEN = "fabricated-offline-bot-token";
        final CharacterBackendMapper backends = stub(CharacterBackendMapper.class);
        final EncryptionService encryption = stub(EncryptionService.class);
        final RedissonClient redisson = stub(RedissonClient.class);
        final RLock lock = stub(RLock.class);
        final TelegramPollingSession.Factory polling = stub(TelegramPollingSession.Factory.class);
        final AutoCloseable session = stub(AutoCloseable.class);
        final org.telegram.telegrambots.meta.generics.TelegramClient client =
                stub(org.telegram.telegrambots.meta.generics.TelegramClient.class);
        final AtomicReference<String> token = new AtomicReference<>(TOKEN);
        final AtomicBoolean available = new AtomicBoolean(true);
        final AtomicBoolean held = new AtomicBoolean();
        final AtomicReference<Thread> owner = new AtomicReference<>();
        final AtomicReference<Thread> unlocker = new AtomicReference<>();
        final List<TestInstance> instances = new CopyOnWriteArrayList<>();
        final BlockingQueue<Registration> registrations = new LinkedBlockingQueue<>();
        final TelegramChannelManager manager;

        Fixture() throws Exception {
            this(TelegramUrl.DEFAULT_URL, null);
        }

        Fixture(TelegramUrl url, TelegramPollingSession.Factory sessionFactory) throws Exception {
            when(backends.selectMany(any(SelectStatementProvider.class)))
                    .thenAnswer(call ->
                            token.get() == null ? List.of() : List.of(new CharacterBackend().withBackendId("backend")));
            when(backends.selectByPrimaryKey("backend"))
                    .thenAnswer(call -> token.get() == null
                            ? Optional.empty()
                            : Optional.of(new CharacterBackend()
                                    .withBackendId("backend")
                                    .withTgBotToken(token.get())));
            when(encryption.decrypt(anyString())).thenAnswer(call -> call.getArgument(0));
            when(client.execute(any(GetMe.class))).thenReturn(botUser());
            when(redisson.getLock("freechat:channels:telegram:polling:backend")).thenReturn(lock);
            when(lock.tryLock()).thenAnswer(call -> {
                owner.set(Thread.currentThread());
                held.set(available.get());
                return held.get();
            });
            when(lock.isHeldByCurrentThread()).thenAnswer(call -> held.get() && Thread.currentThread() == owner.get());
            doAnswer(call -> {
                        assertSame(owner.get(), Thread.currentThread());
                        unlocker.set(Thread.currentThread());
                        held.set(false);
                        return null;
                    })
                    .when(lock)
                    .unlock();
            when(polling.start(anyString(), any())).thenAnswer(this::register);
            manager = new TelegramChannelManager(
                    url,
                    backends,
                    encryption,
                    redisson,
                    sessionFactory == null ? polling : sessionFactory,
                    token -> client);
        }

        Object register(org.mockito.invocation.InvocationOnMock call) {
            registrations.add(new Registration(call.getArgument(1), instances.isEmpty() ? null : instances.getLast()));
            return session;
        }

        void start() {
            manager.start(id -> {
                assertEquals("backend", id);
                TestInstance instance = new TestInstance();
                instances.add(instance);
                return instance;
            });
        }

        Registration registration() throws InterruptedException {
            Registration registration = registrations.poll(5, TimeUnit.SECONDS);
            assertNotNull(registration, "Polling must register within the deadline");
            return registration;
        }

        void awaitOwnerExit() throws InterruptedException {
            await().atMost(Duration.ofSeconds(5)).until(() -> owner.get() != null);
            Thread thread = owner.get();
            thread.join(5000);
            assertFalse(thread.isAlive(), "Polling owner must settle");
        }

        @Override
        public void close() {
            manager.shutdown();
        }
    }
}
