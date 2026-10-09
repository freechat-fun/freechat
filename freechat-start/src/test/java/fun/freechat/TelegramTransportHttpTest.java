package fun.freechat;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.jupiter.api.Assertions.*;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import fun.freechat.channels.spi.*;
import fun.freechat.channels.telegram.DefaultTelegramChannel;
import fun.freechat.channels.telegram.TelegramChannelManager;
import fun.freechat.service.channel.ChannelRegistry;
import fun.freechat.service.channel.ChannelRuntime;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.TelegramUrl;

class TelegramTransportHttpTest {
    private static final String TOKEN = "12345:FAKE-channel-test-token";
    private static final String PREFIX = "/bot" + TOKEN + "/";
    // Telegram uses date=0 for inaccessible messages, not successful send responses.
    private static final String MESSAGE = "{\"ok\":true,\"result\":{\"message_id\":42,\"date\":1,"
            + "\"chat\":{\"id\":7,\"type\":\"private\"},\"text\":\"answer\"}}";
    private WireMockServer server;
    private OkHttpClient http;
    private OkHttpTelegramClient client;
    private ChannelRuntime runtime;
    private ChannelOutbound outbound;

    @BeforeEach
    void startOnlyLocalHttpAndChannelRuntime() throws Exception {
        server = new WireMockServer(options().dynamicPort());
        server.start();
        http = new OkHttpClient.Builder().callTimeout(Duration.ofSeconds(5)).build();
        TelegramUrl url = TelegramUrl.builder()
                .schema("http")
                .host("127.0.0.1")
                .port(server.port())
                .testServer(false)
                .build();
        client = new OkHttpTelegramClient(http, TOKEN, url);
        TelegramChannelManager manager = new TelegramChannelManager(url, null, null, null, null, token -> client) {
            @Override
            public org.telegram.telegrambots.meta.generics.TelegramClient getClient(String backendId) {
                assertEquals("backend", backendId);
                return client;
            }
        };
        DefaultTelegramChannel transport = new DefaultTelegramChannel(manager);
        CompletableFuture<ChannelInstance<String>> ready = new CompletableFuture<>();
        ChannelPlugin<String> plugin = new ChannelPlugin<>() {
            private ChannelInstance<String> instance;

            public String id() {
                return "telegram-http-test";
            }

            public ChannelTransport transport() {
                return transport;
            }

            public ChannelInboundHandler<String> inboundHandler() {
                return (envelope, turn) -> CompletableFuture.completedFuture(null);
            }

            public void start(ChannelRuntimeContext<String> context) {
                instance = context.openInstance("backend");
                ready.complete(instance);
            }

            public void stopReceiving() {
                if (instance != null) {
                    instance.close();
                }
            }

            public void close() {
                stopReceiving();
            }
        };
        runtime = new ChannelRuntime(new ChannelRegistry(List.of(plugin)));
        runtime.start();
        outbound = ready.get(5, TimeUnit.SECONDS).outbound("7");
    }

    private static com.github.tomakehurst.wiremock.matching.UrlPattern apiPath(String path) {
        return urlPathMatching(java.util.regex.Pattern.quote(PREFIX) + "(?i)"
                + java.util.regex.Pattern.quote(path.substring(PREFIX.length())));
    }

    @AfterEach
    void closeOwnedResources() {
        if (runtime != null) {
            runtime.stop();
        }
        if (http != null) {
            http.dispatcher().executorService().shutdown();
            http.connectionPool().evictAll();
        }
        if (server != null) {
            try {
                for (var request : server.getAllServeEvents()) {
                    assertTrue(
                            request.getWasMatched(),
                            "Unmatched SDK request: " + request.getRequest().getUrl());
                }
            } finally {
                server.stop();
            }
        }
    }

    @Test
    void rawSdkAcceptsTheLocalResponseFixture() throws Exception {
        server.stubFor(post(apiPath(PREFIX + "sendmessage")).willReturn(okJson(MESSAGE)));
        var response = client.execute(org.telegram.telegrambots.meta.api.methods.send.SendMessage.builder()
                .chatId(7L)
                .text("plain")
                .build());
        assertEquals(42, response.getMessageId());
    }

    @Test
    void realSdkSerializesTextFormatsAndTypingThroughTheGenericQueue() throws Exception {
        server.stubFor(post(apiPath(PREFIX + "sendmessage")).willReturn(okJson(MESSAGE)));
        server.stubFor(post(apiPath(PREFIX + "sendchataction")).willReturn(okJson("{\"ok\":true,\"result\":true}")));
        assertEquals(
                "42",
                outbound.sendText(ChannelText.plain("plain"))
                        .get(5, TimeUnit.SECONDS)
                        .messageId());
        outbound.sendText(new ChannelText("*bold*", ChannelText.Format.MARKDOWN))
                .get(5, TimeUnit.SECONDS);
        outbound.sendTyping(() -> true).get(5, TimeUnit.SECONDS);
        server.verify(postRequestedFor(apiPath(PREFIX + "sendmessage"))
                .withRequestBody(matchingJsonPath("$.text", equalTo("plain"))));
        server.verify(postRequestedFor(apiPath(PREFIX + "sendmessage"))
                .withRequestBody(matchingJsonPath("$.parse_mode", equalTo("Markdown"))));
        server.verify(postRequestedFor(apiPath(PREFIX + "sendchataction"))
                .withRequestBody(matchingJsonPath("$.action", equalTo("typing"))));
    }

    @Test
    void everyMediaKindKeepsItsReferenceAndCaption() throws Exception {
        ChannelMedia.Kind[] kinds = ChannelMedia.Kind.values();
        String[] methods = {"sendphoto", "sendvoice", "sendvideo", "sendaudio", "senddocument"};
        for (int i = 0; i < kinds.length; i++) {
            server.stubFor(post(apiPath(PREFIX + methods[i])).willReturn(okJson(MESSAGE)));
            outbound.sendMedia(ChannelMedia.reference(kinds[i], "provider-file-reference", "caption"))
                    .get(5, TimeUnit.SECONDS);
            server.verify(postRequestedFor(apiPath(PREFIX + methods[i]))
                    .withRequestBody(containing("provider-file-reference"))
                    .withRequestBody(containing("caption")));
        }
    }

    @Test
    void finalEditRateLimitRetriesWithoutAnyFurtherTokens() throws Exception {
        String path = PREFIX + "editmessagetext";
        server.stubFor(post(apiPath(path))
                .inScenario("edit-limit")
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(okJson("{\"ok\":false,\"error_code\":429,\"description\":\"Too Many Requests\","
                        + "\"parameters\":{\"retry_after\":1}}"))
                .willSetStateTo("accepted"));
        server.stubFor(post(apiPath(path))
                .inScenario("edit-limit")
                .whenScenarioStateIs("accepted")
                .willReturn(okJson(MESSAGE)));
        long start = System.nanoTime();
        outbound.editText("42", new ChannelText("final", ChannelText.Format.MARKDOWN))
                .get(8, TimeUnit.SECONDS);
        assertTrue(System.nanoTime() - start >= TimeUnit.SECONDS.toNanos(1));
        server.verify(2, postRequestedFor(apiPath(path)));
    }

    @Test
    void uploadIsReopenedAfterConfirmedNonAcceptanceAndClosedAfterBothAttempts() throws Exception {
        String path = PREFIX + "senddocument";
        server.stubFor(post(apiPath(path))
                .inScenario("upload-limit")
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(okJson("{\"ok\":false,\"error_code\":429,\"description\":\"Too Many Requests\","
                        + "\"parameters\":{\"retry_after\":1}}"))
                .willSetStateTo("accepted"));
        server.stubFor(post(apiPath(path))
                .inScenario("upload-limit")
                .whenScenarioStateIs("accepted")
                .willReturn(okJson(MESSAGE)));
        AtomicInteger opened = new AtomicInteger();
        AtomicInteger closed = new AtomicInteger();
        ChannelMedia.Upload resource = new ChannelMedia.Upload("example.txt", () -> {
            opened.incrementAndGet();
            return new ByteArrayInputStream("upload body".getBytes(StandardCharsets.UTF_8)) {
                private boolean counted;

                @Override
                public void close() {
                    if (!counted) {
                        counted = true;
                        closed.incrementAndGet();
                    }
                }
            };
        });
        outbound.sendMedia(new ChannelMedia(ChannelMedia.Kind.DOCUMENT, resource, "caption"))
                .get(8, TimeUnit.SECONDS);
        assertEquals(2, opened.get());
        assertEquals(2, closed.get());
        server.verify(2, postRequestedFor(apiPath(path)).withRequestBody(containing("upload body")));
    }

    @Test
    void anAmbiguousSendIsNotAutomaticallyDuplicated() {
        String path = PREFIX + "sendmessage";
        server.stubFor(post(apiPath(path))
                .willReturn(okJson("{\"ok\":false,\"error_code\":500,\"description\":\"Internal Server Error\"}")));
        var error = assertThrows(
                ExecutionException.class,
                () -> outbound.sendText(ChannelText.plain("answer")).get(5, TimeUnit.SECONDS));
        assertEquals(ChannelFailure.Kind.AMBIGUOUS, ((ChannelFailure) error.getCause()).kind());
        server.verify(1, postRequestedFor(apiPath(path)));
    }
}
