package fun.freechat.service.connectors;

import static fun.freechat.service.connectors.ConnectorException.Code.*;
import static org.junit.jupiter.api.Assertions.*;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.LoggerFactory;

@Timeout(15)
class ConnectorMcpClientFactoryTest {
    private static final ObjectMapper JSON = new ObjectMapper();
    private static final Duration LIFETIME = Duration.ofSeconds(10);
    private static final Runnable AUTHORIZED = () -> {};
    private static final String TOOL = "demo.echo-v1";

    @Test
    void streamableJsonInitializesListsAndCallsUsingNegotiatedVersionAndSession() throws Exception {
        try (Server server = new Server(false, false)) {
            AtomicInteger authorizations = new AtomicInteger();
            AtomicInteger destinations = new AtomicInteger();
            try (var connection = factory(url -> {
                        destinations.incrementAndGet();
                        return true;
                    })
                    .open("json", server.config(), LIFETIME, authorizations::incrementAndGet)) {
                assertEquals(TOOL, connection.listTools().getFirst().name());
                assertEquals("hello\nworld", connection.execute(TOOL, "{\"value\":\"hello\"}"));
                assertEquals(4, server.requests.size());
                assertEquals(
                        "2025-11-25",
                        server.requests
                                .getFirst()
                                .path("params")
                                .path("protocolVersion")
                                .asText());
                assertEquals("2025-06-18", server.headers.getLast().getFirst("MCP-Protocol-Version"));
                assertNull(server.headers.getFirst().getFirst("Mcp-Session-Id"));
                assertEquals("session-1", server.headers.getLast().getFirst("Mcp-Session-Id"));
                assertEquals(
                        "hello",
                        server.requests
                                .getLast()
                                .path("params")
                                .path("arguments")
                                .path("value")
                                .asText());
                assertEquals(4, destinations.get());
                assertTrue(authorizations.get() >= 10);
                assertFalse(server.requests.toString().contains("hidden-chat-state"));
                assertTrue(server.requests.stream()
                        .noneMatch(node ->
                                "server/discover".equals(node.path("method").asText())));
            }
        }
    }

    @Test
    void streamableSseAcceptsMultilineDataCrLfAndIgnoresUnknownMessages() throws Exception {
        try (Server server = new Server(false, true);
                var connection = factory(url -> true).open("multiline", server.config(), LIFETIME, AUTHORIZED)) {
            assertEquals(1, connection.listTools().size());
            assertEquals("hello\nworld", connection.execute(TOOL, "{}"));
        }
    }

    @Test
    void legacySseUsesAdvertisedSameOriginQueryEndpoint() throws Exception {
        try (Server server = new Server(true, false);
                var connection = factory(url -> true).open("legacy", server.config(), LIFETIME, AUTHORIZED)) {
            assertEquals(1, connection.listTools().size());
            assertEquals("hello\nworld", connection.execute(TOOL, "{}"));
            assertEquals(
                    "2024-11-05",
                    server.requests
                            .getFirst()
                            .path("params")
                            .path("protocolVersion")
                            .asText());
            assertEquals(1, server.gets.get());
            assertTrue(server.paths.stream().allMatch(path -> path.equals("/messages?session=opaque-token")));
        }
    }

    @Test
    void legacySessionRemainsUsableAfterInitializationBudgetOnSameGet() throws Exception {
        try (Server server = new Server(true, false);
                var connection = factory(url -> true).open("legacy-idle", server.config(1000), LIFETIME, AUTHORIZED)) {
            connection.listTools();
            OutputStream original = server.stream;
            assertFalse(server.stopped.await(1200, TimeUnit.MILLISECONDS));
            assertEquals("hello\nworld", connection.execute(TOOL, "{}"));
            assertSame(original, server.stream);
            assertEquals(1, server.gets.get());
        }
    }

    @Test
    void actualPrivateDestinationIsBlockedWithoutSending() throws Exception {
        try (Server server = new Server(false, false)) {
            assertCode(
                    CONNECTOR_UNSAFE_ADDRESS,
                    () -> new ConnectorMcpClientFactory().open("private", server.config(), LIFETIME, AUTHORIZED));
            assertTrue(server.requests.isEmpty());
        }
    }

    @Test
    void redirectsAreNeverFollowedAnd404IsNeverReplayed() throws Exception {
        for (int status : List.of(302, 307, 308, 404)) {
            try (Server target = new Server(false, false);
                    Server server = new Server(false, false)) {
                server.behavior = request -> new Reply(
                        status,
                        "application/json",
                        "{}".getBytes(StandardCharsets.UTF_8),
                        Map.of("Location", target.url()));
                assertCode(
                        status == 404 ? CONNECTOR_UNAVAILABLE : CONNECTOR_UNSAFE_ADDRESS,
                        () -> factory(url -> true).open("redirect", server.config(), LIFETIME, AUTHORIZED));
                assertEquals(1, server.requests.size());
                assertTrue(target.requests.isEmpty());
            }
        }
    }

    @Test
    void crossOriginAndUnsafeAdvertisedEndpointsAreBlockedBeforePost() throws Exception {
        try (Server target = new Server(false, false)) {
            for (String endpoint : List.of(
                    target.url(),
                    "https://example.com/messages",
                    "//example.com/messages",
                    "/messages#fragment",
                    "http://user@127.0.0.1/messages",
                    "/messages?x=%0d%0aInjected")) {
                try (Server server = new Server(true, false)) {
                    server.endpoint = endpoint;
                    assertCode(
                            CONNECTOR_UNSAFE_ADDRESS,
                            () -> factory(url -> true).open("endpoint", server.config(), LIFETIME, AUTHORIZED));
                    assertTrue(server.requests.isEmpty());
                    assertTrue(target.requests.isEmpty());
                }
            }
        }
    }

    @Test
    void checksDestinationAgainBeforeEveryPost() throws Exception {
        AtomicInteger checks = new AtomicInteger();
        try (Server server = new Server(false, false)) {
            assertCode(
                    CONNECTOR_UNSAFE_ADDRESS,
                    () -> factory(url -> checks.incrementAndGet() == 1)
                            .open("dns-change", server.config(), LIFETIME, AUTHORIZED));
            assertEquals(1, server.requests.size()); // initialized notification is blocked
            assertEquals(2, checks.get());
        }
    }

    @Test
    void lateDnsApprovalCannotSendAfterDeadline() throws Exception {
        CountDownLatch entered = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        CountDownLatch exited = new CountDownLatch(1);
        AtomicReference<Thread> validationWorker = new AtomicReference<>();
        try (Server server = new Server(false, false)) {
            var factory = factory(url -> {
                validationWorker.set(Thread.currentThread());
                entered.countDown();
                awaitIgnoringInterrupt(release);
                exited.countDown();
                return true;
            });
            CompletableFuture<ConnectorException.Code> result = CompletableFuture.supplyAsync(() -> {
                try {
                    factory.open("late-dns", server.config(1000), LIFETIME, AUTHORIZED);
                    return null;
                } catch (ConnectorException error) {
                    return error.code();
                }
            });
            assertTrue(entered.await(2, TimeUnit.SECONDS));
            assertEquals(CONNECTOR_TIMEOUT, result.get(3, TimeUnit.SECONDS));
            release.countDown();
            assertTrue(exited.await(2, TimeUnit.SECONDS));
            validationWorker.get().join(2000);
            assertFalse(validationWorker.get().isAlive());
            assertTrue(server.requests.isEmpty());
        } finally {
            release.countDown();
        }
    }

    @Test
    void authorizationIsRecheckedAfterDnsAndBeforeReturningToolResult() throws Exception {
        try (Server server = new Server(false, false)) {
            AtomicBoolean allowed = new AtomicBoolean(true);
            assertCode(
                    CONNECTOR_ACCESS_DENIED,
                    () -> factory(url -> {
                                allowed.set(false);
                                return true;
                            })
                            .open("revoked-dns", server.config(), LIFETIME, () -> {
                                if (!allowed.get()) throw new ConnectorException(CONNECTOR_ACCESS_DENIED);
                            }));
            assertTrue(server.requests.isEmpty());
            allowed.set(true);
            try (var connection = factory(url -> true).open("revoked-result", server.config(), LIFETIME, () -> {
                if (!allowed.get()) throw new ConnectorException(CONNECTOR_ACCESS_DENIED);
            })) {
                connection.listTools();
                server.behavior = request -> {
                    if ("tools/call".equals(request.path("method").asText())) allowed.set(false);
                    return null;
                };
                assertCode(CONNECTOR_ACCESS_DENIED, () -> connection.execute(TOOL, "{}"));
            }
        }
    }

    @Test
    void rejectsMalformedUtf8OversizedJsonCompressionAndInvalidSessionHeaders() throws Exception {
        List<Reply> replies = List.of(
                new Reply(200, "application/json", new byte[] {(byte) 0xc3, 0x28}, Map.of()),
                Reply.json("not-json"),
                Reply.json("{\"jsonrpc\":\"2.0\",\"id\":0,\"result\":{},\"result\":{}}"),
                Reply.json("x".repeat(256 * 1024 + 1)),
                new Reply(
                        200,
                        "application/json",
                        "{}".getBytes(StandardCharsets.UTF_8),
                        Map.of("Content-Encoding", "gzip")),
                new Reply(
                        200,
                        "application/json",
                        "{}".getBytes(StandardCharsets.UTF_8),
                        Map.of("Mcp-Session-Id", "x".repeat(257))),
                new Reply(
                        200,
                        "application/json",
                        "{}".getBytes(StandardCharsets.UTF_8),
                        Map.of("X-Large", "x".repeat(17 * 1024))));
        for (Reply reply : replies) {
            try (Server server = new Server(false, false)) {
                server.behavior = request -> reply;
                ConnectorException error = assertThrows(
                        ConnectorException.class,
                        () -> factory(url -> true).open("malformed", server.config(), LIFETIME, AUTHORIZED));
                assertTrue(java.util.Set.of(CONNECTOR_UNAVAILABLE, CONNECTOR_LIMIT_EXCEEDED)
                        .contains(error.code()));
                assertNull(error.getCause());
            }
        }
    }

    @Test
    void rejectsOversizedSseLineAndUnterminatedEvent() throws Exception {
        for (String body : List.of(
                "data: " + "x".repeat(256 * 1024 + 1), "data: {\"jsonrpc\":\"2.0\",\"id\":0,\"result\":{}}\n")) {
            try (Server server = new Server(false, false)) {
                server.behavior =
                        request -> new Reply(200, "text/event-stream", body.getBytes(StandardCharsets.UTF_8), Map.of());
                assertThrows(
                        ConnectorException.class,
                        () -> factory(url -> true).open("bad-sse", server.config(), LIFETIME, AUTHORIZED));
            }
        }
    }

    @Test
    void paginationHasOneDeadlineNotOnePerPage() throws Exception {
        try (Server server = new Server(false, false);
                var connection =
                        factory(url -> true).open("pages-timeout", server.config(1000), LIFETIME, AUTHORIZED)) {
            server.behavior = request -> {
                if ("tools/list".equals(request.path("method").asText())) {
                    try {
                        new CountDownLatch(1).await(650, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException ignored) {
                        Thread.currentThread().interrupt();
                    }
                    return result(
                            request,
                            Map.of(
                                    "tools",
                                    List.of(),
                                    "nextCursor",
                                    UUID.randomUUID().toString()));
                }
                return null;
            };
            long start = System.nanoTime();
            assertCode(CONNECTOR_TIMEOUT, connection::listTools);
            assertTrue(Duration.ofNanos(System.nanoTime() - start).toMillis() < 1800);
            assertTrue(server.requests.stream()
                            .filter(r -> "tools/list".equals(r.path("method").asText()))
                            .count()
                    <= 2);
        }
    }

    @Test
    void paginationAndAggregateToolSchemaLimitsAreEnforced() throws Exception {
        List<Function<JsonNode, Reply>> cases = List.of(
                request -> result(
                        request,
                        Map.of(
                                "tools",
                                List.of(),
                                "nextCursor",
                                UUID.randomUUID().toString())),
                request -> result(
                        request,
                        Map.of(
                                "tools",
                                java.util.stream.IntStream.range(0, 33)
                                        .mapToObj(i -> tool("tool" + i, Map.of("type", "object")))
                                        .toList())),
                request -> result(
                        request,
                        Map.of(
                                "tools",
                                List.of(tool(
                                        TOOL, Map.of("type", "object", "description", "x".repeat(128 * 1024 + 1)))))));
        for (var behavior : cases) {
            try (Server server = new Server(false, false);
                    var connection = factory(url -> true).open("limits", server.config(), LIFETIME, AUTHORIZED)) {
                server.behavior = behavior;
                assertCode(CONNECTOR_LIMIT_EXCEEDED, connection::listTools);
                assertTrue(server.requests.size() <= 10);
            }
        }
    }

    @Test
    void acceptsMultipleDiscoveryPagesAndRejectsDuplicateNames() throws Exception {
        try (Server server = new Server(false, false);
                var connection = factory(url -> true).open("pages", server.config(), LIFETIME, AUTHORIZED)) {
            server.behavior = request -> request.path("params").hasNonNull("cursor")
                    ? result(request, Map.of("tools", List.of(tool("second", Map.of("type", "object")))))
                    : result(
                            request,
                            Map.of("tools", List.of(tool(TOOL, Map.of("type", "object"))), "nextCursor", "page2"));
            assertEquals(2, connection.listTools().size());
            server.behavior = request -> result(
                    request,
                    Map.of(
                            "tools",
                            List.of(tool(TOOL, Map.of("type", "object")), tool(TOOL, Map.of("type", "object")))));
            assertCode(CONNECTOR_UNAVAILABLE, connection::listTools);
        }
    }

    @Test
    void structuredOnlyAndStructuredPlusTextUseBoundedStringResults() throws Exception {
        for (boolean legacy : List.of(false, true)) {
            try (Server server = new Server(legacy, false);
                    var connection = factory(url -> true).open("structured", server.config(), LIFETIME, AUTHORIZED)) {
                connection.listTools();
                Map<String, Object> structured =
                        Map.of("value", "hello", "allOf", List.of(1, 2), "url", "https://example.com/do-not-fetch");
                server.behavior = request -> result(request, Map.of("structuredContent", structured));
                assertEquals(JSON.valueToTree(structured), JSON.readTree(connection.execute(TOOL, "{}")));
                server.behavior = request -> result(
                        request,
                        Map.of(
                                "structuredContent",
                                structured,
                                "content",
                                List.of(Map.of("type", "text", "text", "original text"))));
                String combined = connection.execute(TOOL, "{}");
                assertTrue(combined.startsWith("original text\n"));
                assertEquals(
                        JSON.valueToTree(structured), JSON.readTree(combined.substring("original text\n".length())));
                assertEquals(5, server.requests.size());
                JsonNode forwarded =
                        ConnectorMcpMessages.call(JSON.valueToTree(Map.of("structuredContent", structured)));
                assertFalse(forwarded.has("structuredContent"));
                assertEquals(1, forwarded.path("content").size());
            }
        }
    }

    @Test
    void applicationErrorIsSanitizedAndNextCallSucceedsOnSameConnection() throws Exception {
        for (boolean legacy : List.of(false, true)) {
            try (Server server = new Server(legacy, false);
                    var connection =
                            factory(url -> true).open("application-error", server.config(), LIFETIME, AUTHORIZED)) {
                connection.listTools();
                server.behavior = request -> result(
                        request,
                        Map.of(
                                "isError",
                                true,
                                "structuredContent",
                                Map.of("exception", "private-provider-stacktrace"),
                                "content",
                                List.of(Map.of("type", "text", "text", CONNECTOR_TIMEOUT.name()))));
                JsonNode error = JSON.readTree(connection.execute(TOOL, "{}"));
                assertTrue(error.path("isError").booleanValue());
                assertEquals(
                        "Remote tool execution failed", error.path("message").textValue());
                assertEquals(2, error.size());
                server.behavior = request -> null;
                assertEquals("hello\nworld", connection.execute(TOOL, "{}"));
                assertEquals(
                        1,
                        server.requests.stream()
                                .filter(r ->
                                        "initialize".equals(r.path("method").asText()))
                                .count());
                if (legacy) assertEquals(1, server.gets.get());
            }
        }
    }

    @Test
    void structuredDepthNodesAndCombinedUtf8SizeAreBounded() throws Exception {
        Object deep = Map.of("leaf", "value");
        for (int i = 0; i < 33; i++) deep = Map.of("nested", deep);
        for (Map<String, ?> payload : List.<Map<String, ?>>of(
                Map.of("structuredContent", Map.of("value", "é".repeat(32768))),
                Map.of("structuredContent", deep),
                Map.of("structuredContent", Map.of("values", java.util.Collections.nCopies(4096, 0))),
                Map.of(
                        "structuredContent",
                        Map.of("value", "é".repeat(20000)),
                        "content",
                        List.of(Map.of("type", "text", "text", "é".repeat(15000)))))) {
            try (Server server = new Server(false, false);
                    var connection =
                            factory(url -> true).open("structured-limit", server.config(), LIFETIME, AUTHORIZED)) {
                connection.listTools();
                server.behavior = request -> result(request, payload);
                assertCode(CONNECTOR_LIMIT_EXCEEDED, () -> connection.execute(TOOL, "{}"));
            }
        }
    }

    @Test
    void refusesBinaryMalformedStructuredErrorFlagsAndOversizedResultsAndArguments() throws Exception {
        for (Map<String, ?> result : List.<Map<String, ?>>of(
                Map.of("content", List.of(Map.of("type", "image", "data", "secret", "mimeType", "image/png"))),
                Map.of("content", List.of(Map.of("type", "resource_link", "uri", "https://example.com/private"))),
                Map.of("structuredContent", List.of("not an object")),
                Map.of("structuredContent", "not an object"),
                Map.of("isError", "true", "content", List.of()),
                Map.of("isError", 1, "content", List.of()),
                Map.of("isError", JSON.nullNode(), "content", List.of()),
                Map.of("content", List.of(Map.of("type", "text", "text", "x".repeat(64 * 1024 + 1)))))) {
            try (Server server = new Server(false, false);
                    var connection = factory(url -> true).open("binary", server.config(), LIFETIME, AUTHORIZED)) {
                connection.listTools();
                server.behavior = request -> result(request, result);
                assertThrows(ConnectorException.class, () -> connection.execute(TOOL, "{}"));
            }
        }
        try (Server server = new Server(false, false);
                var connection = factory(url -> true).open("arguments", server.config(), LIFETIME, AUTHORIZED)) {
            connection.listTools();
            int sent = server.requests.size();
            assertCode(
                    CONNECTOR_LIMIT_EXCEEDED,
                    () -> connection.execute(TOOL, "{\"value\":\"" + "x".repeat(65536) + "\"}"));
            assertEquals(sent, server.requests.size());
        }
    }

    @Test
    void deniesServerSamplingAndReturnsOnlyEmptyRoots() throws Exception {
        try (Server server = new Server(true, false);
                var connection = factory(url -> true).open("server-requests", server.config(), LIFETIME, AUTHORIZED)) {
            CountDownLatch replies = new CountDownLatch(4);
            server.behavior = request -> {
                if ("tools/list".equals(request.path("method").asText())) {
                    server.event(
                            "{\"jsonrpc\":\"2.0\",\"id\":901,\"method\":\"sampling/createMessage\",\"params\":{\"secret\":\"ignored\"}}");
                    server.event("{\"jsonrpc\":\"2.0\",\"id\":902,\"method\":\"roots/list\"}");
                    server.event("{\"jsonrpc\":\"2.0\",\"id\":903,\"method\":\"elicitation/create\"}");
                    server.event("{\"jsonrpc\":\"2.0\",\"id\":\"ping-text\",\"method\":\"ping\"}");
                    try {
                        assertTrue(replies.await(3, TimeUnit.SECONDS));
                    } catch (InterruptedException error) {
                        Thread.currentThread().interrupt();
                    }
                } else if (!request.has("method")) {
                    if (request.path("id").asLong() == 901 || request.path("id").asLong() == 903)
                        assertEquals(-32601, request.path("error").path("code").asInt());
                    if (request.path("id").asLong() == 902)
                        assertTrue(request.path("result").path("roots").isEmpty());
                    if ("ping-text".equals(request.path("id").asText()))
                        assertEquals("{}", request.path("result").toString());
                    replies.countDown();
                }
                return null;
            };
            assertEquals(1, connection.listTools().size());
            assertEquals(0, replies.getCount());
        }
    }

    @Test
    void idleLegacyPingPreservesScalarIdsAndNeverAnswersNotifications() throws Exception {
        try (Server server = new Server(true, false);
                var connection = factory(url -> true).open("idle-ping", server.config(1000), LIFETIME, AUTHORIZED)) {
            CountDownLatch replied = new CountDownLatch(2);
            List<JsonNode> replies = new CopyOnWriteArrayList<>();
            server.behavior = request -> {
                if (!request.has("method")) {
                    replies.add(request);
                    replied.countDown();
                }
                return null;
            };
            server.event("{\"jsonrpc\":\"2.0\",\"method\":\"ping\"}");
            server.event("{\"jsonrpc\":\"2.0\",\"id\":null,\"method\":\"ping\"}");
            server.event("{\"jsonrpc\":\"2.0\",\"id\":true,\"method\":\"ping\"}");
            String textId = "quoted!\"\\\n" + "é".repeat(59); // Exactly 128 UTF-8 bytes, including escaping characters.
            assertEquals(128, textId.getBytes(StandardCharsets.UTF_8).length);
            server.event(JSON.writeValueAsString(Map.of("jsonrpc", "2.0", "id", textId, "method", "ping")));
            server.event("{\"jsonrpc\":\"2.0\",\"id\":123456789012345678901234567890,\"method\":\"ping\"}");
            assertTrue(replied.await(2, TimeUnit.SECONDS));
            assertEquals(2, replies.size());
            assertTrue(replies.stream()
                    .anyMatch(r -> r.path("id").isTextual()
                            && textId.equals(r.path("id").textValue())));
            assertTrue(replies.stream()
                    .anyMatch(r -> r.path("id").isIntegralNumber()
                            && "123456789012345678901234567890"
                                    .equals(r.path("id").asText())));
            assertTrue(
                    replies.stream().allMatch(r -> "{}".equals(r.path("result").toString())));
            assertEquals(4, server.requests.size());
            assertFalse(
                    server.stopped.await(1200, TimeUnit.MILLISECONDS)); // Completed control timers must be disarmed.
            assertEquals(1, connection.listTools().size());
        }
    }

    @Test
    void queuedServerReplySurvivesCompletionOfMainOperation() throws Exception {
        CountDownLatch entered = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        CountDownLatch replied = new CountDownLatch(1);
        AtomicBoolean blockReply = new AtomicBoolean();
        try (Server server = new Server(true, false);
                var connection = factory(url -> {
                            if (blockReply.compareAndSet(true, false)) {
                                entered.countDown();
                                awaitIgnoringInterrupt(release);
                            }
                            return true;
                        })
                        .open("queued-reply", server.config(), LIFETIME, AUTHORIZED)) {
            server.behavior = request -> {
                if ("tools/list".equals(request.path("method").asText())) {
                    blockReply.set(true);
                    server.event("{\"jsonrpc\":\"2.0\",\"id\":\"queued\",\"method\":\"ping\"}");
                    try {
                        assertTrue(entered.await(1, TimeUnit.SECONDS));
                    } catch (InterruptedException error) {
                        Thread.currentThread().interrupt();
                    }
                } else if ("queued".equals(request.path("id").asText())) {
                    replied.countDown();
                }
                return null;
            };
            assertEquals(1, connection.listTools().size()); // Main operation finishes before control DNS returns.
            assertEquals(1, replied.getCount());
            release.countDown();
            assertTrue(replied.await(1, TimeUnit.SECONDS));
            assertEquals("hello\nworld", connection.execute(TOOL, "{}"));
            assertEquals(1, server.gets.get());
        } finally {
            release.countDown();
        }
    }

    @Test
    void idleControlBodyAndBlockedDnsHaveIndependentTimeoutAndClose() throws Exception {
        for (boolean blockDns : List.of(false, true)) {
            CountDownLatch entered = new CountDownLatch(1);
            CountDownLatch release = new CountDownLatch(1);
            AtomicBoolean blockReply = new AtomicBoolean();
            AtomicReference<Thread> blockedWorker = new AtomicReference<>();
            try (Server server = new Server(true, false);
                    var connection = factory(url -> {
                                if (blockReply.compareAndSet(true, false)) {
                                    blockedWorker.set(Thread.currentThread());
                                    entered.countDown();
                                    awaitIgnoringInterrupt(release);
                                }
                                return true;
                            })
                            .open("idle-control-timeout", server.config(1000), LIFETIME, AUTHORIZED)) {
                if (blockDns) blockReply.set(true);
                else server.stallMethod = "";
                server.event("{\"jsonrpc\":\"2.0\",\"id\":\"idle\",\"method\":\"ping\"}");
                assertTrue((blockDns ? entered : server.callStarted).await(2, TimeUnit.SECONDS));
                assertFalse(server.stopped.await(1300, TimeUnit.MILLISECONDS));
                assertCode(CONNECTOR_TIMEOUT, connection::listTools);
                release.countDown();
                assertPeerClosed(server.stream);
                if (blockDns) {
                    blockedWorker.get().join(2000);
                    assertFalse(blockedWorker.get().isAlive());
                    assertEquals(2, server.requests.size());
                } else {
                    assertPeerClosed(server.callStream);
                }
            } finally {
                release.countDown();
            }
        }
    }

    @Test
    void stalledEndpointInitializationNotificationAndDiscoveryPhysicallyClose() throws Exception {
        try (Server server = new Server(true, false)) {
            server.stallEndpoint = true;
            assertCode(
                    CONNECTOR_TIMEOUT,
                    () -> factory(url -> true).open("stalled-endpoint", server.config(1000), LIFETIME, AUTHORIZED));
            assertTrue(server.requests.isEmpty());
            assertPeerClosed(server.stream);
        }
        for (boolean legacy : List.of(false, true)) {
            for (String method : List.of("initialize", "notifications/initialized", "tools/list")) {
                try (Server server = new Server(legacy, false)) {
                    if ("tools/list".equals(method)) {
                        try (var connection = factory(url -> true)
                                .open("stalled-discovery", server.config(1000), LIFETIME, AUTHORIZED)) {
                            server.stallMethod = method;
                            assertCode(CONNECTOR_TIMEOUT, connection::listTools);
                            assertPeerClosed(server.callStream);
                            if (legacy) assertPeerClosed(server.stream);
                        }
                    } else {
                        server.stallMethod = method;
                        assertCode(
                                CONNECTOR_TIMEOUT,
                                () -> factory(url -> true)
                                        .open("stalled-init", server.config(1000), LIFETIME, AUTHORIZED));
                        assertPeerClosed(server.callStream);
                        if (legacy) assertPeerClosed(server.stream);
                    }
                }
            }
        }
    }

    @Test
    void stalledPartialUtf8AndLegacyStreamsAreClosedOnTimeout() throws Exception {
        for (boolean legacy : List.of(false, true)) {
            try (Server server = new Server(legacy, false);
                    var connection = factory(url -> true).open("stalled", server.config(1000), LIFETIME, AUTHORIZED)) {
                server.stallCalls = true;
                connection.listTools();
                assertCode(CONNECTOR_TIMEOUT, () -> connection.execute(TOOL, "{}"));
                assertCode(CONNECTOR_TIMEOUT, connection::listTools);
                assertEquals(
                        1,
                        server.requests.stream()
                                .filter(r ->
                                        "tools/call".equals(r.path("method").asText()))
                                .count());
                assertPeerClosed(server.callStream);
                if (legacy) assertPeerClosed(server.stream);
            }
        }
    }

    @Test
    void explicitCloseCancelsPendingCallAndNeverRetries() throws Exception {
        try (Server server = new Server(false, false);
                var connection = factory(url -> true).open("cancel", server.config(), LIFETIME, AUTHORIZED)) {
            connection.listTools();
            server.stallCalls = true;
            var call = CompletableFuture.supplyAsync(() -> {
                try {
                    return connection.execute(TOOL, "{}");
                } catch (ConnectorException error) {
                    return error.code().name();
                }
            });
            assertTrue(server.callStarted.await(3, TimeUnit.SECONDS));
            connection.close();
            assertEquals(CONNECTOR_UNAVAILABLE.name(), call.get(2, TimeUnit.SECONDS));
            assertEquals(4, server.requests.size());
        }
    }

    @Test
    void idleLifetimeAndInitializationFailureReleaseOwnerAdmission() throws Exception {
        try (Server server = new Server(false, false)) {
            var factory = factory(url -> true);
            List<ConnectorClientFactory.Connection> clients = new ArrayList<>();
            try {
                for (int i = 0; i < 8; i++)
                    clients.add(factory.open("owner-limit", server.config(), LIFETIME, AUTHORIZED));
                assertCode(
                        CONNECTOR_LIMIT_EXCEEDED,
                        () -> factory.open("owner-limit", server.config(), LIFETIME, AUTHORIZED));
                clients.removeFirst().close();
                clients.add(factory.open("owner-limit", server.config(), LIFETIME, AUTHORIZED));
            } finally {
                clients.forEach(ConnectorClientFactory.Connection::close);
            }
            for (int i = 0; i < 10; i++) {
                assertCode(
                        CONNECTOR_UNSAFE_ADDRESS,
                        () -> factory(url -> false).open("failed-owner", server.config(), LIFETIME, AUTHORIZED));
            }
            CountDownLatch expired = new CountDownLatch(1);
            try (var transport = new ConnectorMcpTransport(
                    server.config(), Duration.ofMillis(50), AUTHORIZED, url -> true, expired::countDown)) {
                assertTrue(expired.await(2, TimeUnit.SECONDS));
                assertCode(CONNECTOR_TIMEOUT, transport::checkHealth);
            }
            assertEquals(18, server.requests.size()); // 9 successful init + initialized; no TTL GET required
        }
    }

    @Test
    void blockedInitializedAuthorizationDoesNotHoldCloseLockOrSendLate() throws Exception {
        CountDownLatch entered = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        AtomicInteger checks = new AtomicInteger();
        try (Server server = new Server(false, false)) {
            var opening = CompletableFuture.supplyAsync(() -> {
                try {
                    factory(url -> true).open("late-auth", server.config(1000), LIFETIME, () -> {
                        if (checks.incrementAndGet() == 3) {
                            entered.countDown();
                            awaitIgnoringInterrupt(release);
                        }
                    });
                    return null;
                } catch (ConnectorException error) {
                    return error.code();
                }
            });
            assertTrue(entered.await(2, TimeUnit.SECONDS));
            assertEquals(CONNECTOR_TIMEOUT, opening.get(2, TimeUnit.SECONDS));
            release.countDown();
            assertEquals(1, server.requests.size());
        } finally {
            release.countDown();
        }
    }

    @Test
    void globalClientAdmissionIsSharedAcrossFactoryInstances() throws Exception {
        try (Server server = new Server(false, false)) {
            List<ConnectorClientFactory.Connection> clients = new ArrayList<>();
            try {
                for (int i = 0; i < 64; i++) {
                    clients.add(factory(url -> true)
                            .open("global-" + i, server.config(), Duration.ofMinutes(1), AUTHORIZED));
                }
                assertCode(
                        CONNECTOR_LIMIT_EXCEEDED,
                        () -> factory(url -> true).open("global-overflow", server.config(), LIFETIME, AUTHORIZED));
            } finally {
                clients.forEach(ConnectorClientFactory.Connection::close);
            }
        }
    }

    @Test
    void httpMessageAndTotalWireBudgetsStopFurtherCalls() throws Exception {
        for (boolean large : List.of(false, true)) {
            try (Server server = new Server(false, false);
                    var connection = factory(url -> true)
                            .open("wire-budget", server.config(), Duration.ofMinutes(1), AUTHORIZED)) {
                connection.listTools();
                server.behavior = request -> result(
                        request,
                        Map.of(
                                "content",
                                List.of(Map.of("type", "text", "text", large ? "x".repeat(60 * 1024) : "ok"))));
                int completed = 0;
                while (true) {
                    try {
                        connection.execute(TOOL, "{}");
                        completed++;
                    } catch (ConnectorException error) {
                        assertEquals(CONNECTOR_LIMIT_EXCEEDED, error.code());
                        break;
                    }
                    assertTrue(completed <= 125);
                }
                if (large) assertTrue(completed < 40);
                else assertEquals(125, completed);
                assertTrue(server.requests.size() <= 128);
            }
        }
    }

    @Test
    void unknownNotificationsAndCommentsHaveFiniteBudgets() throws Exception {
        for (String event :
                List.of(": comment\n\n", "data: {\"jsonrpc\":\"2.0\",\"method\":\"notifications/message\"}\n\n")) {
            try (Server server = new Server(false, false);
                    var connection = factory(url -> true).open("event-budget", server.config(), LIFETIME, AUTHORIZED)) {
                server.behavior = request -> new Reply(
                        200, "text/event-stream", event.repeat(257).getBytes(StandardCharsets.UTF_8), Map.of());
                assertCode(CONNECTOR_LIMIT_EXCEEDED, connection::listTools);
            }
        }
    }

    @Test
    void normalizedOriginComparisonIncludesEffectivePortAndScheme() {
        assertTrue(ConnectorMcpTransport.sameOrigin(
                URI.create("https://EXAMPLE.com/mcp"), URI.create("https://example.com:443/post?session=x")));
        assertFalse(ConnectorMcpTransport.sameOrigin(
                URI.create("https://example.com/mcp"), URI.create("http://example.com:443/post")));
        assertFalse(ConnectorMcpTransport.sameOrigin(
                URI.create("https://example.com/mcp"), URI.create("https://example.com:444/post")));
    }

    @Test
    void ordinaryHeadersAndGeneratedEnvelopeKeysDoNotTriggerCredentialEchoProtection(@TempDir Path temporary)
            throws Exception {
        SSLContext previous = SSLContext.getDefault();
        SSLContext context = testTlsContext(temporary);
        SSLContext.setDefault(context);
        Map<String, String> headers = Map.of(
                "Accept-Language",
                "en",
                "User-Agent",
                "ordinary-client",
                "Cache-Control",
                "no-cache",
                "Pragma",
                "no-cache",
                "X-Project",
                "type",
                "Authorization",
                "Bearer content");
        try (Server server = new Server(context);
                var connection = factory(url -> true)
                        .open(
                                "ordinary-headers",
                                new ConnectorConfiguration("test", "http", server.url(), 2000, headers),
                                LIFETIME,
                                AUTHORIZED)) {
            assertEquals(1, connection.listTools().size());
            assertEquals("hello\nworld", connection.execute(TOOL, "{}"));
            assertEquals("en", server.headers.getLast().getFirst("Accept-Language"));
        } finally {
            SSLContext.setDefault(previous);
        }
    }

    @Test
    void credentialClassificationIncludesSchemesButNotOrdinaryOrShortCustomValues() {
        var credentials = ConnectorMcpMessages.credentialValues(Map.of(
                "Authorization",
                "bEaReR raw-token",
                "Cookie",
                "session=private",
                "X-Api-Key",
                "short",
                "X-Password",
                "pw",
                "X-Custom",
                "opaque-value",
                "X-Project",
                "en",
                "Accept-Language",
                "en",
                "User-Agent",
                "ordinary-client",
                "Cache-Control",
                "no-cache",
                "Pragma",
                "no-cache"));
        assertEquals(
                java.util.Set.of("bEaReR raw-token", "raw-token", "session=private", "short", "pw", "opaque-value"),
                new java.util.HashSet<>(credentials));
        assertThrows(UnsupportedOperationException.class, () -> credentials.add("mutation"));
        assertEquals(
                List.of("Basic dXNlcjpwYXNz", "dXNlcjpwYXNz"),
                ConnectorMcpMessages.credentialValues(Map.of("Authorization", "Basic dXNlcjpwYXNz")));
    }

    @Test
    void credentialEchoInTextStructuredNamesAndSchemasIsBlockedBeforeLc(@TempDir Path temporary) throws Exception {
        SSLContext previous = SSLContext.getDefault();
        SSLContext context = testTlsContext(temporary);
        SSLContext.setDefault(context);
        try {
            for (String echo : List.of("Bearer echoed-secret", "echoed-secret", "quoted-\"secret\\value")) {
                String credential = echo.startsWith("quoted-") ? echo : "echoed-secret";
                for (boolean structured : List.of(false, true)) {
                    try (Server server = new Server(context);
                            var connection = factory(url -> true)
                                    .open(
                                            "credential-echo",
                                            new ConnectorConfiguration(
                                                    "test",
                                                    "http",
                                                    server.url(),
                                                    2000,
                                                    Map.of("Authorization", "Bearer " + credential)),
                                            LIFETIME,
                                            AUTHORIZED)) {
                        connection.listTools();
                        server.behavior = request -> result(
                                request,
                                structured
                                        ? Map.of("structuredContent", Map.of(echo, "value", "value", echo))
                                        : Map.of(
                                                "content",
                                                List.of(Map.of("type", "text", "text", "provider echoed " + echo))));
                        assertCode(CONNECTOR_UNAVAILABLE, () -> connection.execute(TOOL, "{}"));
                    }
                }
            }
            for (boolean schema : List.of(false, true)) {
                try (Server server = new Server(context);
                        var connection = factory(url -> true)
                                .open(
                                        "credential-schema",
                                        new ConnectorConfiguration(
                                                "test",
                                                "http",
                                                server.url(),
                                                2000,
                                                Map.of("X-Custom", "opaque-secret")),
                                        LIFETIME,
                                        AUTHORIZED)) {
                    server.behavior = request -> result(
                            request,
                            Map.of(
                                    "tools",
                                    List.of(tool(
                                            schema ? TOOL : "opaque-secret",
                                            schema
                                                    ? Map.of(
                                                            "type",
                                                            "object",
                                                            "properties",
                                                            Map.of("opaque-secret", Map.of("type", "string")))
                                                    : Map.of("type", "object")))));
                    assertCode(CONNECTOR_UNAVAILABLE, connection::listTools);
                }
            }
        } finally {
            SSLContext.setDefault(previous);
        }
    }

    @Test
    void oversizedServerIdsAndFailurePayloadsNeverReachLcLogs(@TempDir Path temporary) throws Exception {
        SSLContext previousTls = SSLContext.getDefault();
        SSLContext context = testTlsContext(temporary);
        SSLContext.setDefault(context);
        Logger logger = (Logger) LoggerFactory.getLogger("dev.langchain4j.mcp");
        Level previous = logger.getLevel();
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);
        logger.setLevel(Level.TRACE);
        String secret = "never-log-failure-secret";
        try {
            for (String id : List.of(secret + "x".repeat(128), "é".repeat(65))) {
                try (Server server = new Server(true, false);
                        var connection =
                                factory(url -> true).open("oversized-id", server.config(), LIFETIME, AUTHORIZED)) {
                    server.event(JSON.writeValueAsString(Map.of("jsonrpc", "2.0", "id", id, "method", "ping")));
                    assertCode(CONNECTOR_LIMIT_EXCEEDED, connection::listTools);
                    assertTrue(server.requests.stream().allMatch(r -> r.has("method")));
                    assertPeerClosed(server.stream);
                }
            }
            try (Server server = new Server(context);
                    var connection = factory(url -> true)
                            .open(
                                    "failure-logs",
                                    new ConnectorConfiguration(
                                            "test",
                                            "http",
                                            server.url(),
                                            2000,
                                            Map.of("Authorization", "Bearer " + secret)),
                                    LIFETIME,
                                    AUTHORIZED)) {
                connection.listTools();
                server.behavior = request -> result(
                        request, Map.of("isError", true, "content", List.of(Map.of("type", "text", "text", secret))));
                assertTrue(JSON.readTree(connection.execute(TOOL, "{}"))
                        .path("isError")
                        .booleanValue());
                server.behavior =
                        request -> result(request, Map.of("content", List.of(Map.of("type", "text", "text", secret))));
                assertCode(CONNECTOR_UNAVAILABLE, () -> connection.execute(TOOL, "{}"));
            }
            String logs = appender.list.stream()
                    .map(ILoggingEvent::getFormattedMessage)
                    .collect(java.util.stream.Collectors.joining("\n"));
            assertFalse(logs.contains(secret));
            assertFalse(logs.contains("é".repeat(65)));
            assertFalse(logs.contains("unknown notification"));
        } finally {
            logger.detachAppender(appender);
            logger.setLevel(previous);
            appender.stop();
            SSLContext.setDefault(previousTls);
        }
    }

    @Test
    void confidentialMetadataAndUnknownNotificationsNeverReachLcLogs() throws Exception {
        Logger logger = (Logger) LoggerFactory.getLogger("dev.langchain4j.mcp");
        Level previous = logger.getLevel();
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);
        logger.setLevel(Level.TRACE);
        try (Server server = new Server(false, true)) {
            server.secret = "never-log-this-provider-secret";
            try (var connection = factory(url -> true).open("logs", server.config(), LIFETIME, AUTHORIZED)) {
                connection.listTools();
                connection.execute(TOOL, "{}");
            }
            String logs = appender.list.stream()
                    .map(ILoggingEvent::getFormattedMessage)
                    .collect(java.util.stream.Collectors.joining("\n"));
            assertFalse(logs.contains(server.secret));
            assertFalse(logs.contains("unknown notification"));
        } finally {
            logger.detachAppender(appender);
            logger.setLevel(previous);
            appender.stop();
        }
    }

    @Test
    void httpsCredentialsStayOnConfiguredOriginAndCookiesAreNotReused(@TempDir Path temporary) throws Exception {
        SSLContext previous = SSLContext.getDefault();
        SSLContext context = testTlsContext(temporary);
        SSLContext.setDefault(context);
        try (Server target = new Server(false, false);
                Server server = new Server(context)) {
            Map<String, String> credentials =
                    Map.of("Authorization", "Bearer test-only-secret", "X-Token", "private-value");
            var config = new ConnectorConfiguration("tls", "http", server.url(), 2000, credentials);
            try (var connection = factory(url -> true).open("tls", config, LIFETIME, AUTHORIZED)) {
                connection.listTools();
                assertEquals("Bearer test-only-secret", server.headers.getLast().getFirst("Authorization"));
                assertEquals("private-value", server.headers.getLast().getFirst("X-Token"));
                assertNull(server.headers.getLast().getFirst("Cookie"));
                server.behavior =
                        request -> new Reply(307, "application/json", new byte[0], Map.of("Location", target.url()));
                assertCode(CONNECTOR_UNSAFE_ADDRESS, () -> connection.execute(TOOL, "{}"));
                assertTrue(target.requests.isEmpty());
                assertFalse(server.requests.toString().contains("test-only-secret"));
            }
        } finally {
            SSLContext.setDefault(previous);
        }
    }

    private static SSLContext testTlsContext(Path temporary) throws Exception {
        Path store = temporary.resolve("test-only.p12");
        String keytool =
                Path.of(System.getProperty("java.home"), "bin", "keytool").toString();
        Process process = new ProcessBuilder(
                        keytool,
                        "-genkeypair",
                        "-alias",
                        "test",
                        "-keyalg",
                        "RSA",
                        "-keysize",
                        "2048",
                        "-storetype",
                        "PKCS12",
                        "-keystore",
                        store.toString(),
                        "-storepass",
                        "test-only-password",
                        "-dname",
                        "CN=127.0.0.1",
                        "-ext",
                        "SAN=ip:127.0.0.1",
                        "-validity",
                        "2",
                        "-noprompt")
                .redirectErrorStream(true)
                .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                .start();
        assertTrue(process.waitFor(10, TimeUnit.SECONDS));
        assertEquals(0, process.exitValue());
        KeyStore keys = KeyStore.getInstance("PKCS12");
        try (var input = Files.newInputStream(store)) {
            keys.load(input, "test-only-password".toCharArray());
        }
        KeyManagerFactory km = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        km.init(keys, "test-only-password".toCharArray());
        TrustManagerFactory tm = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tm.init(keys);
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(km.getKeyManagers(), tm.getTrustManagers(), null);
        return context;
    }

    private static ConnectorMcpClientFactory factory(Predicate<String> publicDestination) {
        return new ConnectorMcpClientFactory(publicDestination);
    }

    private static void assertCode(ConnectorException.Code code, org.junit.jupiter.api.function.Executable action) {
        ConnectorException error = assertThrows(ConnectorException.class, action);
        assertEquals(code, error.code());
        assertEquals(code.name(), error.getMessage());
        assertNull(error.getCause());
    }

    private static void assertPeerClosed(OutputStream stream) throws Exception {
        assertNotNull(stream);
        assertTrue(CompletableFuture.supplyAsync(() -> {
                    try {
                        byte[] probe = new byte[8192];
                        for (int i = 0; i < 2048; i++) {
                            stream.write(probe);
                            stream.flush();
                        }
                        return false;
                    } catch (IOException expected) {
                        return true;
                    }
                })
                .get(2, TimeUnit.SECONDS));
    }

    private static void awaitIgnoringInterrupt(CountDownLatch latch) {
        boolean interrupted = false;
        while (true) {
            try {
                latch.await();
                break;
            } catch (InterruptedException ignored) {
                interrupted = true;
            }
        }
        if (interrupted) Thread.currentThread().interrupt();
    }

    private static Map<String, Object> tool(String name, Map<String, ?> schema) {
        return Map.of("name", name, "description", "A test tool", "inputSchema", schema);
    }

    private static Reply result(JsonNode request, Object result) {
        try {
            return Reply.json(JSON.writeValueAsString(
                    Map.of("jsonrpc", "2.0", "id", request.path("id").asLong(), "result", result)));
        } catch (Exception error) {
            throw new AssertionError(error);
        }
    }

    private record Reply(int status, String type, byte[] body, Map<String, String> headers) {
        static Reply json(String value) {
            return new Reply(200, "application/json", value.getBytes(StandardCharsets.UTF_8), Map.of());
        }
    }

    private static final class Server implements AutoCloseable {
        final HttpServer http;
        final boolean legacy;
        final boolean sse;
        final boolean https;
        final java.util.concurrent.ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        final List<JsonNode> requests = new CopyOnWriteArrayList<>();
        final List<Headers> headers = new CopyOnWriteArrayList<>();
        final List<String> paths = new CopyOnWriteArrayList<>();
        final AtomicInteger gets = new AtomicInteger();
        final CountDownLatch stopped = new CountDownLatch(1);
        final CountDownLatch callStarted = new CountDownLatch(1);
        volatile Function<JsonNode, Reply> behavior = request -> null;
        volatile String endpoint = "/messages?session=opaque-token";
        volatile String secret = "provider-secret";
        volatile boolean stallCalls;
        volatile boolean stallEndpoint;
        volatile String stallMethod;
        volatile OutputStream stream;
        volatile OutputStream callStream;

        Server(boolean legacy, boolean sse) throws IOException {
            this(legacy, sse, null);
        }

        Server(SSLContext context) throws IOException {
            this(false, false, context);
        }

        Server(boolean legacy, boolean sse, SSLContext context) throws IOException {
            this.legacy = legacy;
            this.sse = sse;
            this.https = context != null;
            if (context == null) {
                http = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
            } else {
                HttpsServer secure = HttpsServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
                secure.setHttpsConfigurator(new HttpsConfigurator(context));
                http = secure;
            }
            http.setExecutor(executor);
            http.createContext("/", this::handle);
            http.start();
        }

        String url() {
            return (https ? "https" : "http") + "://127.0.0.1:"
                    + http.getAddress().getPort() + "/mcp";
        }

        ConnectorConfiguration config() {
            return config(2000);
        }

        ConnectorConfiguration config(int timeout) {
            return new ConnectorConfiguration("test", legacy ? "sse" : "http", url(), timeout, Map.of());
        }

        void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                gets.incrementAndGet();
                exchange.getResponseHeaders().set("Content-Type", "text/event-stream");
                exchange.sendResponseHeaders(200, 0);
                stream = exchange.getResponseBody();
                synchronized (this) {
                    stream.write((stallEndpoint ? ": waiting\n\n" : "event: endpoint\r\ndata: " + endpoint + "\r\n\r\n")
                            .getBytes(StandardCharsets.UTF_8));
                    stream.flush();
                }
                try {
                    stopped.await();
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                }
                exchange.close();
                return;
            }
            JsonNode request = JSON.readTree(exchange.getRequestBody().readNBytes(128 * 1024));
            requests.add(request);
            headers.add(exchange.getRequestHeaders());
            paths.add(exchange.getRequestURI().toString());
            String method = request.path("method").asText();
            if ((stallCalls && "tools/call".equals(method)) || method.equals(stallMethod)) {
                exchange.getResponseHeaders().set("Content-Type", "text/event-stream");
                exchange.sendResponseHeaders(200, 0);
                callStream = exchange.getResponseBody();
                callStream.write(new byte[] {'d', 'a', 't', 'a', ':', ' ', (byte) 0xc3});
                callStream.flush();
                callStarted.countDown();
                try {
                    stopped.await();
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                }
                exchange.close();
                return;
            }
            Reply reply = behavior.apply(request);
            if (reply == null) {
                reply = switch (method) {
                    case "initialize" ->
                        result(
                                request,
                                Map.of(
                                        "protocolVersion",
                                        "2025-06-18",
                                        "capabilities",
                                        Map.of("tools", Map.of()),
                                        "serverInfo",
                                        Map.of("name", secret, "version", secret),
                                        "instructions",
                                        secret));
                    case "tools/list" ->
                        result(
                                request,
                                Map.of("tools", List.of(tool(TOOL, Map.of("type", "object", "properties", Map.of())))));
                    case "tools/call" ->
                        result(
                                request,
                                Map.of(
                                        "content",
                                        List.of(
                                                Map.of("type", "text", "text", "hello"),
                                                Map.of("type", "text", "text", "world"))));
                    default -> new Reply(202, "application/json", new byte[0], Map.of());
                };
            }
            if (legacy && reply.status() == 200 && request.has("method")) {
                event(new String(reply.body(), StandardCharsets.UTF_8));
                reply = new Reply(202, "application/json", new byte[0], Map.of());
            } else if (sse && reply.status() == 200) {
                String body = new String(reply.body(), StandardCharsets.UTF_8);
                String framed =
                        "event: message\r\ndata: {\"jsonrpc\":\"2.0\",\"method\":\"unknown notification\",\"params\":\""
                                + secret + "\"}\r\n\r\n"
                                + "data: {\"jsonrpc\":\"2.0\",\"id\":9999,\"result\":\"" + secret + "\"}\r\n\r\n"
                                + "data: {\r\ndata: " + body.substring(1) + "\r\n\r\n";
                reply = new Reply(200, "text/event-stream", framed.getBytes(StandardCharsets.UTF_8), Map.of());
            }
            if ("initialize".equals(method) && !legacy) {
                exchange.getResponseHeaders().set("Mcp-Session-Id", "session-1");
                exchange.getResponseHeaders().set("Set-Cookie", "not-reused=private");
            }
            exchange.getResponseHeaders().set("Content-Type", reply.type());
            reply.headers()
                    .forEach((key, value) -> exchange.getResponseHeaders().set(key, value));
            exchange.sendResponseHeaders(reply.status(), reply.status() == 202 || reply.status() == 204 ? -1 : 0);
            try (var out = exchange.getResponseBody()) {
                if (reply.status() != 202 && reply.status() != 204) out.write(reply.body());
            }
        }

        synchronized void event(String json) {
            try {
                stream.write(("data: " + json + "\n\n").getBytes(StandardCharsets.UTF_8));
                stream.flush();
            } catch (IOException error) {
                throw new IllegalStateException("test stream closed");
            }
        }

        @Override
        public void close() {
            stopped.countDown();
            http.stop(0);
            executor.shutdownNow();
        }
    }
}
