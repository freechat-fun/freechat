package fun.freechat.service.tools.network;

import static fun.freechat.service.tools.network.NetworkTestSupport.*;
import static org.junit.jupiter.api.Assertions.*;

import fun.freechat.service.tools.ToolArguments;
import fun.freechat.service.tools.ToolDefinition;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@org.junit.jupiter.api.Timeout(5)
class WebFetchToolTest {
    private static final Set<String> HOSTS = Set.of("example.com");
    private static final String ARGUMENTS = "{\"url\":\"https://example.com/page?q=1\"}";

    @Test
    void unavailableByDefaultButDefinitionIsOfflineAndNetworkBounded() throws Exception {
        WebFetchTool disabled = new WebFetchTool(Set.of());
        assertFalse(disabled.configured());
        assertFalse(new WebFetchTool(null).configured());
        assertEquals("web_fetch", disabled.definition().name());
        assertEquals(ToolDefinition.Boundary.NETWORK, disabled.definition().boundary());
        assertFalse(disabled.definition().sideEffects());
        assertThrows(IllegalStateException.class, () -> disabled.execute(args(ARGUMENTS), context()));
    }

    @Test
    void extractsTextWithoutScriptsResourcesCredentialsOrTenantIdentifiers() throws Exception {
        Calls calls = new Calls(request ->
                response(request, 200, "text/html; charset=UTF-8", """
                <html><head><style>hidden-css</style><script>hidden-script</script></head>
                <body><h1>Actual title</h1><p>Hello <b>world</b>.</p>
                <iframe src="https://internal.local/">hidden-frame</iframe>
                <img src="https://evil.com/track"><a href="javascript:steal()">A reference</a>
                <svg><text>hidden-svg</text></svg><template>hidden-template</template></body></html>
                """).withHeader("Set-Cookie", "tenant=secret"));
        WebFetchTool tool = new WebFetchTool(HOSTS, calls);
        var result = args(tool.execute(args(ARGUMENTS), context()));
        assertEquals("https://example.com/page?q=1", result.path("url").asText());
        assertEquals(
                "Actual title Hello world. A reference", result.path("text").asText());
        assertTrue(result.path("untrusted").asBoolean());
        assertFalse(result.path("truncated").asBoolean());
        assertEquals("GET", calls.request.method());
        assertEquals(
                "identity",
                calls.request.headers().firstValue("Accept-Encoding").orElseThrow());
        assertTrue(calls.request.headers().firstValue("Authorization").isEmpty());
        assertTrue(calls.request.headers().firstValue("Cookie").isEmpty());
        assertTrue(calls.request.timeout().orElseThrow().compareTo(Duration.ofSeconds(10)) <= 0);
        assertFalse(calls.request.toString().contains("chat-secret"));
        tool.execute(args(ARGUMENTS), context());
        assertEquals(2, calls.count.get());
        assertTrue(calls.request.headers().firstValue("Cookie").isEmpty());
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "null",
                "[]",
                "{}",
                "{\"url\":1}",
                "{\"url\":null}",
                "{\"url\":\"\"}",
                "{\"url\":\"https://example.com\",\"headers\":{}}",
                "{\"url\":\"https://example.com\",\"credentials\":\"secret\"}",
                "{\"url\":\"https://not-allowed.com\"}"
            })
    void malformedArgumentsNeverReachTransport(String json) {
        Calls calls = new Calls(request -> {
            throw new AssertionError("Must not call HTTP");
        });
        assertThrows(
                IllegalArgumentException.class, () -> new WebFetchTool(HOSTS, calls).execute(args(json), context()));
        assertEquals(0, calls.count.get());
    }

    @Test
    void rejectsOversizedAndMalformedUnicodeArguments() {
        WebFetchTool tool = new WebFetchTool(HOSTS);
        assertThrows(
                IllegalArgumentException.class,
                () -> tool.execute(
                        ToolArguments.JSON.createObjectNode().put("url", "https://example.com/" + "x".repeat(2048)),
                        context()));
        assertThrows(
                IllegalArgumentException.class,
                () -> tool.execute(
                        ToolArguments.JSON.createObjectNode().put("url", "https://example.com/\uD800"), context()));
    }

    @ParameterizedTest
    @ValueSource(ints = {201, 204, 206, 301, 302, 303, 307, 308, 401, 407, 408, 429, 503})
    void rejectsRedirectsAndErrorsWithoutFollowupOrErrorBodyDisclosure(int status) {
        Calls calls = new Calls(request -> response(request, status, "text/plain", "secret provider error")
                .withHeader("Location", "https://169.254.169.254/latest/meta-data/")
                .withHeader("Retry-After", "0"));
        Exception error = assertThrows(
                IOException.class, () -> new WebFetchTool(HOSTS, calls).execute(args(ARGUMENTS), context()));
        assertEquals(1, calls.count.get());
        assertFalse(error.toString().contains("secret"));
        assertNull(error.getCause());
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "application/json",
                "image/png",
                "application/pdf",
                "application/xhtml+xml",
                "text/event-stream",
                "text/plain; charset=ISO-8859-1",
                "text/html; charset=unknown",
                "invalid-content-type"
            })
    void rejectsNontextAndNonUtf8Content(String type) {
        Calls calls = new Calls(request -> response(request, 200, type, "provider-content"));
        assertThrows(IOException.class, () -> new WebFetchTool(HOSTS, calls).execute(args(ARGUMENTS), context()));
    }

    @Test
    void rejectsCompressionInvalidUtf8AndAmbiguousHeaders() {
        for (String encoding : List.of("gzip", "br", "deflate", "gzip, identity")) {
            Calls calls = new Calls(request ->
                    response(request, 200, "text/plain", "compressed").withHeader("Content-Encoding", encoding));
            assertThrows(IOException.class, () -> new WebFetchTool(HOSTS, calls).execute(args(ARGUMENTS), context()));
        }
        Calls utf8 = new Calls(request -> response(request, 200, "text/plain", "")
                .withHeader("Content-Length", "2")
                .withBody(new ByteArrayInputStream(new byte[] {(byte) 0xc0, (byte) 0xaf})));
        assertThrows(IOException.class, () -> new WebFetchTool(HOSTS, utf8).execute(args(ARGUMENTS), context()));
        Calls ambiguous = new Calls(
                request -> response(request, 200, "text/plain", "text").addHeader("Content-Type", "image/png"));
        assertThrows(IOException.class, () -> new WebFetchTool(HOSTS, ambiguous).execute(args(ARGUMENTS), context()));
        for (String length : List.of("99999999", "1", "5", "-1", "x", "1, 2")) {
            Calls calls = new Calls(
                    request -> response(request, 200, "text/plain", "text").withHeader("Content-Length", length));
            assertThrows(IOException.class, () -> new WebFetchTool(HOSTS, calls).execute(args(ARGUMENTS), context()));
        }
    }

    @Test
    void rejectedHeadersCloseBodyWithoutConsumingIt() {
        AtomicLong reads = new AtomicLong();
        AtomicBoolean closed = new AtomicBoolean();
        InputStream input = new InputStream() {
            public int read() {
                reads.incrementAndGet();
                return -1;
            }

            public void close() {
                closed.set(true);
            }
        };
        Calls calls = new Calls(request -> response(request, 200, "text/plain", "")
                .withHeader("Transfer-Encoding", "chunked")
                .withBody(input));
        assertThrows(IOException.class, () -> new WebFetchTool(HOSTS, calls).execute(args(ARGUMENTS), context()));
        assertEquals(0, reads.get());
        assertTrue(closed.get());
    }

    @Test
    void boundsHeaderCountHeaderBytesAndHtmlNodeCount() {
        Calls headerCount = new Calls(request -> {
            Response response = response(request, 200, "text/plain", "text");
            for (int i = 0; i < 65; i++) response = response.withHeader("X-Fixture-" + i, "value");
            return response;
        });
        assertThrows(IOException.class, () -> new WebFetchTool(HOSTS, headerCount).execute(args(ARGUMENTS), context()));
        Calls headerBytes = new Calls(
                request -> response(request, 200, "text/plain", "text").withHeader("X-Fixture", "x".repeat(16_385)));
        assertThrows(IOException.class, () -> new WebFetchTool(HOSTS, headerBytes).execute(args(ARGUMENTS), context()));
        Calls html = new Calls(request -> response(request, 200, "text/html", "<i>x</i>".repeat(20_000)));
        assertThrows(IOException.class, () -> new WebFetchTool(HOSTS, html).execute(args(ARGUMENTS), context()));
    }

    @Test
    void rejectsOversizedDeclaredAndUnknownLengthBodiesAndClosesInput() {
        Calls declared = new Calls(request -> response(request, 200, "text/plain", "x".repeat(262_145)));
        assertThrows(IOException.class, () -> new WebFetchTool(HOSTS, declared).execute(args(ARGUMENTS), context()));
        AtomicLong read = new AtomicLong();
        AtomicBoolean closed = new AtomicBoolean();
        InputStream input = new InputStream() {
            public int read() {
                read.incrementAndGet();
                return 'a';
            }

            public void close() {
                closed.set(true);
            }
        };
        Calls unknown = new Calls(request -> response(request, 200, "text/plain", "")
                .withHeader("Content-Length", null)
                .withBody(input));
        assertThrows(IOException.class, () -> new WebFetchTool(HOSTS, unknown).execute(args(ARGUMENTS), context()));
        assertTrue(closed.get());
        assertEquals(262_145, read.get());
    }

    @Test
    void outputBoundsEscapingUnicodeAndControlCharacters() throws Exception {
        String content = ("\"\\中文\uD834\uDD1E\u202e\u0001").repeat(12_000);
        Calls calls = new Calls(request -> response(request, 200, "text/plain", content));
        String output = new WebFetchTool(HOSTS, calls).execute(args(ARGUMENTS), context());
        assertTrue(output.getBytes(StandardCharsets.UTF_8).length <= 65_536);
        var result = args(output);
        assertTrue(result.path("truncated").asBoolean());
        String text = result.path("text").asText();
        assertTrue(text.length() <= 8000);
        assertFalse(text.contains("\u202e"));
        assertFalse(text.contains("\u0001"));
        assertFalse(Character.isHighSurrogate(text.charAt(text.length() - 1)));
    }

    @Test
    void totalDeadlineInterruptsStalledTransport() throws Exception {
        CountDownLatch interrupted = new CountDownLatch(1);
        Calls calls = new Calls(request -> {
            try {
                new CountDownLatch(1).await();
                throw new AssertionError("Must be interrupted");
            } catch (InterruptedException failure) {
                interrupted.countDown();
                throw failure;
            }
        });
        assertTimeoutPreemptively(
                Duration.ofSeconds(2),
                () -> assertThrows(
                        IOException.class,
                        () -> new WebFetchTool(HOSTS, calls)
                                .execute(args(ARGUMENTS), context(Duration.ofMillis(100)))));
        assertTrue(interrupted.await(1, TimeUnit.SECONDS));
    }

    @Test
    void totalDeadlineAlsoInterruptsSlowBodyAndClosesIt() throws Exception {
        CountDownLatch closed = new CountDownLatch(1);
        InputStream input = new InputStream() {
            public int read() throws IOException {
                try {
                    new CountDownLatch(1).await();
                    return -1;
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                    throw new IOException("Interrupted fixture");
                }
            }

            public void close() {
                closed.countDown();
            }
        };
        Calls calls = new Calls(request -> response(request, 200, "text/plain", "")
                .withHeader("Content-Length", null)
                .withBody(input));
        assertTimeoutPreemptively(
                Duration.ofSeconds(2),
                () -> assertThrows(
                        IOException.class,
                        () -> new WebFetchTool(HOSTS, calls)
                                .execute(args(ARGUMENTS), context(Duration.ofMillis(100)))));
        assertTrue(closed.await(1, TimeUnit.SECONDS));
    }

    @Test
    void realJdkTransportSendsHeadersAndDoesNotFollowRedirects() throws Exception {
        var server = com.sun.net.httpserver.HttpServer.create(new java.net.InetSocketAddress("127.0.0.1", 0), 0);
        var hits = new java.util.concurrent.atomic.AtomicInteger();
        var seenEncoding = new java.util.concurrent.atomic.AtomicReference<String>();
        server.createContext("/page", exchange -> {
            hits.incrementAndGet();
            seenEncoding.set(exchange.getRequestHeaders().getFirst("Accept-Encoding"));
            byte[] text = "Local JDK response".getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
            exchange.sendResponseHeaders(200, text.length);
            try (var output = exchange.getResponseBody()) {
                output.write(text);
            }
        });
        server.createContext("/redirect", exchange -> {
            exchange.getResponseHeaders().set("Location", "/page");
            exchange.sendResponseHeaders(302, -1);
            exchange.close();
        });
        server.start();
        try (var client = NetworkHttp.client()) {
            NetworkHttp.Transport local = request -> {
                var builder = java.net.http.HttpRequest.newBuilder(java.net.URI.create("http://127.0.0.1:"
                                + server.getAddress().getPort() + request.uri().getRawPath()))
                        .timeout(request.timeout().orElseThrow());
                request.headers().map().forEach((name, values) -> values.forEach(value -> builder.header(name, value)));
                return client.send(builder.build(), java.net.http.HttpResponse.BodyHandlers.ofInputStream());
            };
            WebFetchTool tool = new WebFetchTool(HOSTS, local);
            assertEquals(
                    "Local JDK response",
                    args(tool.execute(args(ARGUMENTS), context())).path("text").asText());
            assertEquals("identity", seenEncoding.get());
            assertThrows(
                    IOException.class,
                    () -> tool.execute(args("{\"url\":\"https://example.com/redirect\"}"), context()));
            assertEquals(1, hits.get());
        } finally {
            server.stop(0);
        }
    }

    @Test
    void realJdkResponseBodyIsCancelledAtTheInvocationDeadline() throws Exception {
        CountDownLatch release = new CountDownLatch(1);
        var server = com.sun.net.httpserver.HttpServer.create(new java.net.InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/slow", exchange -> {
            exchange.getResponseHeaders().set("Content-Type", "text/plain");
            exchange.sendResponseHeaders(200, 100);
            try (var output = exchange.getResponseBody()) {
                output.write('a');
                output.flush();
                try {
                    release.await();
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                }
            } catch (IOException ignored) {
            } finally {
                exchange.close();
            }
        });
        server.start();
        try (var client = NetworkHttp.client()) {
            NetworkHttp.Transport local = request -> client.send(
                    java.net.http.HttpRequest.newBuilder(java.net.URI.create(
                                    "http://127.0.0.1:" + server.getAddress().getPort() + "/slow"))
                            .timeout(request.timeout().orElseThrow())
                            .build(),
                    java.net.http.HttpResponse.BodyHandlers.ofInputStream());
            assertTimeoutPreemptively(
                    Duration.ofSeconds(2),
                    () -> assertThrows(
                            IOException.class,
                            () -> new WebFetchTool(HOSTS, local)
                                    .execute(args(ARGUMENTS), context(Duration.ofMillis(200)))));
        } finally {
            release.countDown();
            server.stop(0);
        }
    }

    @Test
    void expiredOrInterruptedContextNeverMakesRequest() {
        Calls calls = new Calls(request -> {
            throw new AssertionError("Must not make a request");
        });
        WebFetchTool tool = new WebFetchTool(HOSTS, calls);
        assertThrows(IllegalStateException.class, () -> tool.execute(args(ARGUMENTS), context(Duration.ofNanos(1))));
        try {
            Thread.currentThread().interrupt();
            assertThrows(IllegalStateException.class, () -> tool.execute(args(ARGUMENTS), context()));
        } finally {
            Thread.interrupted();
        }
        assertEquals(0, calls.count.get());
    }
}
