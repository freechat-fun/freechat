package fun.freechat;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import fun.freechat.channels.telegram.TelegramChannelManager;
import fun.freechat.channels.telegram.TelegramHttpClient;
import fun.freechat.channels.telegram.TelegramPollingSession;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.TelegramUrl;
import org.telegram.telegrambots.meta.api.methods.GetMe;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

class TelegramPollingHttpTest {
    private static final String PRIVATE = "FABRICATED_PRIVATE_POLLING_SENTINEL";
    private static final String TOKEN = "12345:FAKE-polling-test-token";
    private static final String PREFIX = "/bot" + TOKEN + "/";
    private static final String TRUE = "{\"ok\":true,\"result\":true}";
    private static final String USER =
            "{\"ok\":true,\"result\":{\"id\":1,\"first_name\":\"Bot\"," + "\"is_bot\":true,\"username\":\"safe_bot\"}}";
    private static final String UPDATE = "{\"ok\":true,\"result\":[{\"update_id\":1,\"message\":{\"message_id\":1,"
            + "\"date\":1,\"chat\":{\"id\":7,\"type\":\"private\"},\"text\":\"local\"}}]}";
    private static final String EMPTY = "{\"ok\":true,\"result\":[]}";
    private final List<Owned> owned = new CopyOnWriteArrayList<>();
    private WireMockServer server;
    private TelegramUrl url;

    @BeforeEach
    void startLocalServer() {
        server = new WireMockServer(options().dynamicPort());
        server.start();
        url = TelegramUrl.builder()
                .schema("http")
                .host("127.0.0.1")
                .port(server.port())
                .testServer(false)
                .build();
        server.stubFor(post(urlPathMatching("/bot[^/]+/deleteWebhook")).willReturn(okJson(TRUE)));
        server.stubFor(post(urlPathMatching("/bot[^/]+/getupdates"))
                .willReturn(okJson(UPDATE).withFixedDelay(100)));
        server.stubFor(post(urlPathMatching("/bot[^/]+/getme")).willReturn(okJson(USER)));
    }

    @AfterEach
    void stopLocalServer() {
        server.stop();
    }

    @ParameterizedTest
    @ValueSource(
            strings = {"{\"ok\":false,\"error_code\":500,\"description\":\"private\"}", "{\"ok\":true,\"result\":false}"
            })
    void failedInitialDeleteWebhookIsTransactionalAndReconcileCanRetry(String failure) throws Exception {
        String path = "/bot" + TelegramChannelManagerTest.Fixture.TOKEN + "/deleteWebhook";
        server.stubFor(post(urlEqualTo(path))
                .inScenario("registration")
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(okJson(failure))
                .willSetStateTo("retry"));
        server.stubFor(post(urlEqualTo(path))
                .inScenario("registration")
                .whenScenarioStateIs("retry")
                .willReturn(okJson(TRUE)));
        try (var fixture = new TelegramChannelManagerTest.Fixture(url, factory(UnaryOperator.identity()))) {
            assertEquals(0, server.getAllServeEvents().size(), "Constructors must not contact the provider");
            fixture.start();
            fixture.awaitOwnerExit();
            assertEquals(1, owned.size());
            assertClosed(owned.getFirst());
            assertFalse(fixture.instances.getFirst().isActive());
            fixture.manager.reconcile();
            await().atMost(Duration.ofSeconds(5))
                    .until(() -> owned.size() == 2 && owned.getLast().worker.get() != null);
            assertTrue(fixture.instances.getLast().isActive());
            assertEquals("safe_bot", fixture.manager.getUsername("backend"));
            fixture.manager.shutdown();
            owned.forEach(TelegramPollingHttpTest::assertClosed);
            server.verify(2, postRequestedFor(urlEqualTo(path)));
        }
    }

    @Test
    void retiringLeaseWaitsForCancelledHttpDispatcherBeforeUnlockOrReplacement() throws Exception {
        CountDownLatch cleanupEntered = new CountDownLatch(1);
        CompletableFuture<Void> releaseCleanup = new CompletableFuture<>();
        AtomicBoolean first = new AtomicBoolean(true);
        server.stubFor(post(urlPathMatching("/bot[^/]+/getupdates"))
                .willReturn(okJson(UPDATE).withFixedDelay(15000)));
        var factory = factory(http -> first.getAndSet(false)
                ? http.newBuilder()
                        .addNetworkInterceptor(chain -> {
                            if (!chain.request().url().encodedPath().endsWith("/getupdates")) {
                                return chain.proceed(chain.request());
                            }
                            try {
                                return chain.proceed(chain.request());
                            } finally {
                                cleanupEntered.countDown();
                                releaseCleanup.join();
                            }
                        })
                        .build()
                : http);
        try (var fixture = new TelegramChannelManagerTest.Fixture(url, factory)) {
            try {
                fixture.start();
                await().atMost(Duration.ofSeconds(5))
                        .untilAsserted(
                                () -> server.verify(1, postRequestedFor(urlPathMatching("/bot[^/]+/getupdates"))));
                fixture.token.set("123:FAKE-rotated");
                fixture.manager.activate("backend");
                assertTrue(cleanupEntered.await(5, TimeUnit.SECONDS));
                assertTrue(fixture.owner.get().isAlive());
                assertFalse(fixture.instances.getFirst().isActive());
                assertEquals(1, owned.getFirst().http.dispatcher().runningCallsCount());
                verify(fixture.lock, never()).unlock();
                fixture.manager.reconcile();
                assertEquals(1, owned.size(), "Retiring physical HTTP must fence the replacement");
            } finally {
                releaseCleanup.complete(null);
            }
            fixture.awaitOwnerExit();
            assertClosed(owned.getFirst());
            verify(fixture.lock).unlock();
            server.stubFor(post(urlPathMatching("/bot[^/]+/getupdates"))
                    .willReturn(okJson(UPDATE).withFixedDelay(100)));
            fixture.manager.reconcile();
            await().atMost(Duration.ofSeconds(5))
                    .until(() -> owned.size() == 2 && owned.getLast().worker.get() != null);
            fixture.manager.shutdown();
            owned.forEach(TelegramPollingHttpTest::assertClosed);
        }
    }

    @Test
    void stopDuringRealRegistrationClosesEventualResourcesAndNeverAdmitsUpdates() throws Exception {
        CountDownLatch registering = new CountDownLatch(1);
        CompletableFuture<Void> release = new CompletableFuture<>();
        var factory = factory(http -> http.newBuilder()
                .addNetworkInterceptor(chain -> {
                    var response = chain.proceed(chain.request());
                    if (chain.request().url().encodedPath().endsWith("/deleteWebhook")) {
                        registering.countDown();
                        release.join();
                    }
                    return response;
                })
                .build());
        try (var fixture = new TelegramChannelManagerTest.Fixture(url, factory)) {
            try {
                fixture.start();
                assertTrue(registering.await(5, TimeUnit.SECONDS));
                fixture.manager.stopReceiving();
                assertFalse(fixture.instances.getFirst().isActive());
                verify(fixture.lock, never()).unlock();
            } finally {
                release.complete(null);
            }
            fixture.awaitOwnerExit();
            assertTrue(fixture.instances.getFirst().received.isEmpty());
            owned.forEach(TelegramPollingHttpTest::assertClosed);
            verify(fixture.lock).unlock();
        }
    }

    @Test
    void repeatedTokenRotationLeavesNoOwnedWorkersDispatchersOrConnections() throws Exception {
        try (var fixture = new TelegramChannelManagerTest.Fixture(url, factory(UnaryOperator.identity()))) {
            fixture.start();
            for (int i = 0; i < 5; i++) {
                int expected = i + 1;
                await().atMost(Duration.ofSeconds(5))
                        .until(() -> owned.size() == expected
                                && owned.getLast().worker.get() != null);
                assertTrue(owned.getLast().worker.get().isVirtual());
                Owned previous = owned.getLast();
                Thread owner = fixture.owner.get();
                fixture.token.set("123:FAKE-rotation-" + i);
                fixture.manager.activate("backend");
                owner.join(5000);
                assertFalse(owner.isAlive());
                assertClosed(previous);
                fixture.manager.reconcile();
            }
            await().atMost(Duration.ofSeconds(5))
                    .until(() -> owned.size() == 6 && owned.getLast().worker.get() != null);
            fixture.manager.shutdown();
            owned.forEach(TelegramPollingHttpTest::assertClosed);
            assertEquals(
                    6,
                    owned.stream()
                            .map(item -> item.http.dispatcher())
                            .distinct()
                            .count());
            assertEquals(
                    6,
                    owned.stream()
                            .map(item -> item.http.connectionPool())
                            .distinct()
                            .count());
        }
    }

    @Test
    void offsetsNeverRegressAndSdkAllowedUpdatesDefaultsArePreserved() throws Exception {
        server.stubFor(post(urlEqualTo(PREFIX + "getupdates"))
                .inScenario("offset")
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(okJson("{\"ok\":true,\"result\":[{\"update_id\":9},{\"update_id\":3}]}"))
                .willSetStateTo("older"));
        server.stubFor(post(urlEqualTo(PREFIX + "getupdates"))
                .inScenario("offset")
                .whenScenarioStateIs("older")
                .willReturn(okJson("{\"ok\":true,\"result\":[{\"update_id\":4},{\"update_id\":9}]}"))
                .willSetStateTo("empty"));
        server.stubFor(post(urlEqualTo(PREFIX + "getupdates"))
                .inScenario("offset")
                .whenScenarioStateIs("empty")
                .willReturn(okJson(EMPTY).withFixedDelay(100)));
        List<Update> updates = new CopyOnWriteArrayList<>();
        try (var session = factory(UnaryOperator.identity()).start(TOKEN, updates::addAll)) {
            await().atMost(Duration.ofSeconds(5))
                    .untilAsserted(() -> server.verify(
                            moreThanOrExactly(2),
                            postRequestedFor(urlEqualTo(PREFIX + "getupdates"))
                                    .withRequestBody(matchingJsonPath("$.offset", equalTo("10")))));
            assertEquals(
                    List.of(9, 3), updates.stream().map(Update::getUpdateId).toList());
            server.verify(
                    1,
                    postRequestedFor(urlEqualTo(PREFIX + "getupdates"))
                            .withRequestBody(matchingJsonPath("$.offset", equalTo("1")))
                            .withRequestBody(matchingJsonPath("$.timeout", equalTo("50")))
                            .withRequestBody(matchingJsonPath("$.limit", equalTo("100")))
                            .withRequestBody(matchingJsonPath("$.allowed_updates", equalToJson("[]"))));
        }
        owned.forEach(TelegramPollingHttpTest::assertClosed);
    }

    @Test
    void realProviderPollingErrorsLogOnlySanitizedMetadata() throws Exception {
        Logger pollingLogger = (Logger) LoggerFactory.getLogger(TelegramPollingSession.class);
        Logger sdkLogger = (Logger) LoggerFactory.getLogger("org.telegram.telegrambots");
        Level pollingLevel = pollingLogger.getLevel();
        Level sdkLevel = sdkLogger.getLevel();
        ListAppender<ILoggingEvent> events = new ListAppender<>();
        events.list = new CopyOnWriteArrayList<>();
        events.setContext(pollingLogger.getLoggerContext());
        events.start();
        pollingLogger.addAppender(events);
        sdkLogger.addAppender(events);
        pollingLogger.setLevel(Level.WARN);
        sdkLogger.setLevel(Level.DEBUG);
        server.stubFor(post(urlEqualTo(PREFIX + "getupdates"))
                .willReturn(okJson("{\"ok\":false,\"error_code\":500,\"description\":\"" + PRIVATE + TOKEN + "\"}")));
        try {
            try (var session =
                    factory(UnaryOperator.identity()).start(TOKEN, updates -> fail("Failed poll has no updates"))) {
                await().atMost(Duration.ofSeconds(5)).until(() -> !events.list.isEmpty());
            }
            assertTrue(
                    events.list.stream().anyMatch(event -> event.getLoggerName().equals(pollingLogger.getName())));
            for (ILoggingEvent event : events.list) {
                assertFalse(event.getFormattedMessage().contains(PRIVATE));
                assertFalse(event.getFormattedMessage().contains(TOKEN));
                assertNull(event.getThrowableProxy());
            }
            owned.forEach(TelegramPollingHttpTest::assertClosed);
        } finally {
            pollingLogger.detachAppender(events);
            sdkLogger.detachAppender(events);
            pollingLogger.setLevel(pollingLevel);
            sdkLogger.setLevel(sdkLevel);
            events.stop();
        }
    }

    @Test
    void managerOwnedOutboundClientPrevents503FollowUpAndClosesOnlyItsOwnResources() throws Exception {
        server.stubFor(post(urlEqualTo(PREFIX + "sendmessage"))
                .willReturn(aResponse()
                        .withStatus(503)
                        .withHeader("Retry-After", "0")
                        .withBody(PRIVATE)));
        try (var fixture = new TelegramChannelManagerTest.Fixture()) {
            fixture.token.set(TOKEN);
            fixture.available.set(false);
            var callerHttp = TelegramHttpClient.create(Duration.ofSeconds(5));
            var callerClient = new OkHttpTelegramClient(callerHttp, TOKEN, url);
            var manager = new TelegramChannelManager(url, fixture.backends, fixture.encryption, fixture.redisson);
            var injected = new TelegramChannelManager(
                    url, fixture.backends, fixture.encryption, fixture.redisson, null, token -> callerClient);
            OkHttpClient managerHttp = (OkHttpClient) ReflectionTestUtils.getField(manager, "http");
            try {
                assertEquals(0, server.getAllServeEvents().size());
                manager.start(id -> new TelegramChannelManagerTest.TestInstance());
                fixture.awaitOwnerExit();
                assertEquals("safe_bot", manager.getUsername("backend"));
                assertSingleAttemptSettings(managerHttp);
                assertThrows(
                        TelegramApiException.class,
                        () -> manager.getClient("backend")
                                .execute(SendMessage.builder()
                                        .chatId(7L)
                                        .text("single attempt")
                                        .build()));
                server.verify(1, postRequestedFor(urlEqualTo(PREFIX + "sendmessage")));
                manager.shutdown();
                assertClosed(new Owned(managerHttp, new AtomicReference<>()));
                injected.start(id -> new TelegramChannelManagerTest.TestInstance());
                fixture.awaitOwnerExit();
                injected.shutdown();
                assertFalse(callerHttp.dispatcher().executorService().isShutdown());
                assertEquals("safe_bot", callerClient.execute(new GetMe()).getUserName());
            } finally {
                manager.shutdown();
                injected.shutdown();
                TelegramHttpClient.closeOwned(callerHttp);
            }
        }
    }

    @Test
    void disconnectAfterTransmittingSendDoesNotCauseAnotherNetworkAttempt() throws Exception {
        server.stubFor(
                post(urlEqualTo(PREFIX + "sendmessage")).willReturn(aResponse().withFault(Fault.EMPTY_RESPONSE)));
        OkHttpClient http = TelegramHttpClient.create(Duration.ofSeconds(5));
        try {
            assertSingleAttemptSettings(http);
            var client = new OkHttpTelegramClient(http, TOKEN, url);
            assertThrows(
                    TelegramApiException.class,
                    () -> client.execute(SendMessage.builder()
                            .chatId(7L)
                            .text("transmitted body")
                            .build()));
            server.verify(
                    1,
                    postRequestedFor(urlEqualTo(PREFIX + "sendmessage"))
                            .withRequestBody(matchingJsonPath("$.text", equalTo("transmitted body"))));
        } finally {
            TelegramHttpClient.closeOwned(http);
        }
    }

    @Test
    void eachExplicitCallGetsItsOwnAttemptAndRedirectsAreNotFollowed() throws Exception {
        server.stubFor(post(urlEqualTo(PREFIX + "sendmessage"))
                .willReturn(aResponse().withStatus(307).withHeader("Location", server.baseUrl() + "/redirect-target")));
        server.stubFor(post(urlEqualTo("/redirect-target")).willReturn(okJson(TRUE)));
        OkHttpClient http = TelegramHttpClient.create(Duration.ofSeconds(5));
        try {
            var client = new OkHttpTelegramClient(http, TOKEN, url);
            for (int i = 0; i < 2; i++) {
                assertThrows(
                        TelegramApiException.class,
                        () -> client.execute(SendMessage.builder()
                                .chatId(7L)
                                .text("explicit call")
                                .build()));
            }
            server.verify(2, postRequestedFor(urlEqualTo(PREFIX + "sendmessage")));
            server.verify(0, postRequestedFor(urlEqualTo("/redirect-target")));
        } finally {
            TelegramHttpClient.closeOwned(http);
        }
    }

    private TelegramPollingSession.Factory factory(UnaryOperator<OkHttpClient> customize) {
        return (token, updates) -> {
            OkHttpClient http = customize.apply(TelegramHttpClient.create(Duration.ofSeconds(5)));
            assertSingleAttemptSettings(http);
            Owned resources = new Owned(http, new AtomicReference<>());
            owned.add(resources);
            return TelegramPollingSession.start(
                    url,
                    token,
                    batch -> {
                        resources.worker.set(Thread.currentThread());
                        updates.accept(batch);
                    },
                    http);
        };
    }

    private static void assertSingleAttemptSettings(OkHttpClient http) {
        assertFalse(http.retryOnConnectionFailure());
        assertFalse(http.followRedirects());
        assertFalse(http.followSslRedirects());
        assertTrue(http.callTimeoutMillis() > 0);
        assertTrue(http.readTimeoutMillis() > 0);
        assertTrue(http.connectTimeoutMillis() > 0);
        assertTrue(http.writeTimeoutMillis() > 0);
    }

    private static void assertClosed(Owned resources) {
        assertTrue(resources.http.dispatcher().executorService().isTerminated());
        assertEquals(0, resources.http.dispatcher().runningCallsCount());
        assertEquals(0, resources.http.dispatcher().queuedCallsCount());
        assertEquals(0, resources.http.connectionPool().connectionCount());
        if (resources.worker.get() != null) {
            assertFalse(resources.worker.get().isAlive());
        }
    }

    private record Owned(OkHttpClient http, AtomicReference<Thread> worker) {}
}
