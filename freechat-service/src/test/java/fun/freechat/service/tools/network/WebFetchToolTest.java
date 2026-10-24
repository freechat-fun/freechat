package fun.freechat.service.tools.network;

import static fun.freechat.service.tools.network.NetworkTestSupport.*;
import static org.junit.jupiter.api.Assertions.*;

import dev.langchain4j.model.chat.request.json.JsonStringSchema;
import fun.freechat.service.tools.ToolArguments;
import fun.freechat.service.tools.ToolDefinition;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@org.junit.jupiter.api.Timeout(5)
class WebFetchToolTest {
    private static final String ARGUMENTS = "{\"url\":\"https://example.com/page?q=1\"}";

    @Test
    void defaultDefinitionIsAvailableOfflineAndNetworkBounded() {
        // Inspect the default definition without executing a URL or resolving a hostname.
        var definition = new WebFetchTool().definition();
        assertEquals("web_fetch", definition.name());
        assertEquals(ToolDefinition.Boundary.NETWORK, definition.boundary());
        assertFalse(definition.sideEffects());
        assertEquals(Set.of("network", "web", "read"), definition.tags());
        var specification = definition.specification();
        var schema = specification.parameters();
        assertEquals(Set.of("url"), schema.properties().keySet());
        assertEquals(List.of("url"), schema.required());
        assertEquals(false, schema.additionalProperties());
        var urlSchema =
                assertInstanceOf(JsonStringSchema.class, schema.properties().get("url"));
        String urlDescription = urlSchema.description().toLowerCase(Locale.ROOT);
        assertTrue(urlDescription.contains("http"));
        assertTrue(urlDescription.contains("public")
                || urlDescription.contains("safe")
                || urlDescription.contains("private"));
        String description = specification.description().toLowerCase(Locale.ROOT);
        assertTrue(description.contains("untrusted"));
        assertTrue(description.contains("redirect"));
        assertTrue(description.contains("compression"));
        assertTrue(description.contains("chunked"));
        for (String obsolete : List.of("allowed host", "approved host", "allowlist")) {
            assertFalse(description.contains(obsolete));
            assertFalse(urlDescription.contains(obsolete));
        }
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
        WebFetchTool tool = new WebFetchTool(calls, url -> true);
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
                "https://not-allowed.com",
                "https://docs.example.org/reference",
                "http://www.example.net/page?q=1",
                "https://Docs.Example.COM:8443/a%2Fb?value=%2F%2520&value=a+b&empty=",
                "HTTP://Mixed.Example.technology:8080/a/../b?q=%E4%B8%AD&next=https%3A%2F%2Fother.org%2F",
                "HTTPS://Other.Example.net:9443/%7euser?case=%2f&case=%2F"
            })
    void acceptsCheckedDomainsHttpAndCustomPortsWithoutChangingRawUrlOrSendingCredentials(String source)
            throws Exception {
        AtomicInteger checks = new AtomicInteger();
        Calls calls = new Calls(request -> {
            assertEquals(1, checks.get(), "Safety validation must precede HTTP");
            return response(request, 200, "text/plain", "Public fixture").withHeader("Set-Cookie", "session=secret");
        });
        WebFetchTool tool = new WebFetchTool(calls, url -> {
            checks.incrementAndGet();
            return true;
        });
        var result = args(tool.execute(ToolArguments.JSON.createObjectNode().put("url", source), context()));
        URI original = URI.create(source);
        String normalized = original.getScheme().toLowerCase(Locale.ROOT) + source.substring(source.indexOf(':'));
        String sent = calls.request.uri().toString();
        assertTrue(source.equals(sent) || normalized.equals(sent), "Only the HTTP scheme may be normalized");
        assertEquals(original.getRawAuthority(), calls.request.uri().getRawAuthority());
        assertEquals(original.getRawPath(), calls.request.uri().getRawPath());
        assertEquals(original.getRawQuery(), calls.request.uri().getRawQuery());
        assertEquals(original, URI.create(result.path("url").asText()));
        assertEquals("Public fixture", result.path("text").asText());
        assertTrue(result.path("untrusted").asBoolean());
        assertEquals("GET", calls.request.method());
        assertTrue(calls.request.bodyPublisher().isEmpty());
        assertTrue(calls.request.headers().firstValue("Authorization").isEmpty());
        assertTrue(calls.request.headers().firstValue("Proxy-Authorization").isEmpty());
        assertTrue(calls.request.headers().firstValue("Cookie").isEmpty());
        assertEquals(1, checks.get());
        assertEquals(1, calls.count.get());
    }

    @Test
    void rejectedSafetyCheckNeverReachesHttpAndDoesNotDiscloseUrl() {
        String source = "https://not-allowed.com/private?token=checker-secret";
        AtomicInteger checks = new AtomicInteger();
        Calls calls = new Calls(request -> {
            throw new AssertionError("Must not call HTTP");
        });
        WebFetchTool tool = new WebFetchTool(calls, url -> {
            assertEquals(source, url);
            checks.incrementAndGet();
            return false;
        });
        IOException error = assertThrows(
                IOException.class,
                () -> tool.execute(ToolArguments.JSON.createObjectNode().put("url", source), context()));
        assertFalse(error.toString().contains("not-allowed.com"));
        assertFalse(error.toString().contains("checker-secret"));
        assertNull(error.getCause());
        assertEquals(0, error.getSuppressed().length);
        assertEquals(1, checks.get());
        assertEquals(0, calls.count.get());
    }

    @Test
    void safetyCheckerExceptionIsSanitizedAndFailsClosed() {
        String source = "https://example.com/private?token=checker-secret";
        AtomicInteger checks = new AtomicInteger();
        Calls calls = new Calls(request -> {
            throw new AssertionError("Must not call HTTP");
        });
        WebFetchTool tool = new WebFetchTool(calls, url -> {
            checks.incrementAndGet();
            throw new IllegalStateException("Resolver leaked " + url, new IOException("provider-secret"));
        });
        IOException error = assertThrows(
                IOException.class,
                () -> tool.execute(ToolArguments.JSON.createObjectNode().put("url", source), context()));
        assertFalse(error.toString().contains("example.com"));
        assertFalse(error.toString().contains("secret"));
        assertFalse(error.toString().contains("Resolver leaked"));
        assertNull(error.getCause());
        assertEquals(0, error.getSuppressed().length);
        assertEquals(1, checks.get());
        assertEquals(0, calls.count.get());
    }

    @Test
    void validatesBeforeEveryRequestIncludingRepeatedUrls() throws Exception {
        AtomicInteger checks = new AtomicInteger();
        AtomicInteger sends = new AtomicInteger();
        Thread caller = Thread.currentThread();
        Calls calls = new Calls(request -> {
            assertEquals(sends.incrementAndGet(), checks.get(), "Each send needs a fresh safety check");
            return response(request, 200, "text/plain", "Checked fixture");
        });
        WebFetchTool tool = new WebFetchTool(calls, url -> {
            assertNotSame(caller, Thread.currentThread(), "Validation belongs inside the bounded worker");
            assertEquals("https://example.com/page?q=1", url);
            return checks.incrementAndGet() <= 2;
        });
        assertEquals(0, checks.get());
        tool.execute(args(ARGUMENTS), context());
        tool.execute(args(ARGUMENTS), context());
        assertEquals(2, checks.get());
        assertEquals(2, calls.count.get());
        assertThrows(IOException.class, () -> tool.execute(args(ARGUMENTS), context()));
        assertEquals(3, checks.get());
        assertEquals(2, calls.count.get());
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "http://127.0.0.1/",
                "https://127.0.0.2/",
                "https://10.0.0.1/",
                "http://192.168.1.1:8080/",
                "https://172.16.0.1/",
                "http://172.31.255.254/",
                "http://169.254.169.254/latest/meta-data/",
                "https://169.254.1.1/",
                "http://[::1]/",
                "https://[fc00::1]/",
                "http://[fe80::1]/"
            })
    void realHttpUtilsSafetyCheckerRejectsNonpublicIpLiteralsWithoutHttp(String source) {
        Calls calls = new Calls(request -> {
            throw new AssertionError("Nonpublic addresses must not reach HTTP");
        });
        // The transport-only constructor uses HttpUtils; IP literals require no external DNS.
        WebFetchTool tool = new WebFetchTool(calls);
        IOException error = assertThrows(
                IOException.class,
                () -> tool.execute(ToolArguments.JSON.createObjectNode().put("url", source), context()));
        assertFalse(error.toString().contains(source));
        assertFalse(error.toString().contains(URI.create(source).getHost()));
        assertNull(error.getCause());
        assertEquals(0, calls.count.get());
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "null",
                "[]",
                "{}",
                "{\"url\":1}",
                "{\"url\":null}",
                "{\"url\":true}",
                "{\"url\":[]}",
                "{\"url\":{}}",
                "{\"url\":\"https://example.com\",\"headers\":{}}",
                "{\"url\":\"https://example.com\",\"credentials\":\"secret\"}"
            })
    void malformedArgumentsNeverReachSafetyCheckOrTransport(String json) {
        AtomicInteger checks = new AtomicInteger();
        Calls calls = new Calls(request -> {
            throw new AssertionError("Must not call HTTP");
        });
        WebFetchTool tool = new WebFetchTool(calls, url -> {
            checks.incrementAndGet();
            return true;
        });
        assertThrows(IllegalArgumentException.class, () -> tool.execute(args(json), context()));
        assertEquals(0, checks.get());
        assertEquals(0, calls.count.get());
    }

    @ParameterizedTest
    @ValueSource(strings = {"https://example.com/with space", "https://example.com/%zz", "https://[::1"})
    void malformedLiteralUrisRejectBeforeSafetyCheckOrTransport(String source) {
        AtomicInteger checks = new AtomicInteger();
        Calls calls = new Calls(request -> {
            throw new AssertionError("Must not call HTTP");
        });
        WebFetchTool tool = new WebFetchTool(calls, url -> {
            checks.incrementAndGet();
            return true;
        });
        // URI.create rejects literal syntax before validation enters the bounded worker.
        assertThrows(
                IllegalArgumentException.class,
                () -> tool.execute(ToolArguments.JSON.createObjectNode().put("url", source), context()));
        assertEquals(0, checks.get());
        assertEquals(0, calls.count.get());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "/relative", "file:///etc/passwd", "ftp://127.0.0.1/file", "http:///missing-host"})
    void syntacticallyValidButUnsafeUrlsFailInsideBoundedWorker(String source) {
        Calls calls = new Calls(request -> {
            throw new AssertionError("Must not call HTTP");
        });
        // These parse as URIs, but the real safety checker rejects them without DNS.
        IOException error = assertThrows(
                IOException.class,
                () -> new WebFetchTool(calls)
                        .execute(ToolArguments.JSON.createObjectNode().put("url", source), context()));
        assertNull(error.getCause());
        assertEquals(0, calls.count.get());
    }

    @Test
    void rejectsOversizedAndMalformedUnicodeArgumentsBeforeSafetyCheckOrTransport() {
        AtomicInteger checks = new AtomicInteger();
        Calls calls = new Calls(request -> {
            throw new AssertionError("Must not call HTTP");
        });
        WebFetchTool tool = new WebFetchTool(calls, url -> {
            checks.incrementAndGet();
            return true;
        });
        String prefix = "https://example.com/";
        for (String source :
                List.of(prefix + "x".repeat(2049 - prefix.length()), prefix + "\uD800", prefix + "\uDC00")) {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> tool.execute(ToolArguments.JSON.createObjectNode().put("url", source), context()));
        }
        assertEquals(0, checks.get());
        assertEquals(0, calls.count.get());
    }

    @Test
    void acceptsUrlAtTheArgumentLengthLimit() throws Exception {
        String prefix = "https://example.com/";
        String source = prefix + "x".repeat(2048 - prefix.length());
        Calls calls = new Calls(request -> response(request, 200, "text/plain", "Bounded fixture"));
        new WebFetchTool(calls, url -> true)
                .execute(ToolArguments.JSON.createObjectNode().put("url", source), context());
        assertEquals(source, calls.request.uri().toString());
        assertEquals(1, calls.count.get());
    }

    @ParameterizedTest
    @ValueSource(ints = {201, 204, 206, 301, 302, 303, 307, 308, 401, 407, 408, 429, 503})
    void rejectsRedirectsAndErrorsWithoutFollowupOrErrorBodyDisclosure(int status) {
        Calls calls = new Calls(request -> response(request, status, "text/plain", "secret provider error")
                .withHeader("Location", "https://169.254.169.254/latest/meta-data/")
                .withHeader("Retry-After", "0"));
        Exception error = assertThrows(
                IOException.class, () -> new WebFetchTool(calls, url -> true).execute(args(ARGUMENTS), context()));
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
        assertThrows(IOException.class, () -> new WebFetchTool(calls, url -> true).execute(args(ARGUMENTS), context()));
    }

    @Test
    void rejectsCompressionInvalidUtf8AndAmbiguousHeaders() {
        for (String encoding : List.of("gzip", "br", "deflate", "gzip, identity")) {
            Calls calls = new Calls(request ->
                    response(request, 200, "text/plain", "compressed").withHeader("Content-Encoding", encoding));
            assertThrows(
                    IOException.class, () -> new WebFetchTool(calls, url -> true).execute(args(ARGUMENTS), context()));
        }
        Calls utf8 = new Calls(request -> response(request, 200, "text/plain", "")
                .withHeader("Content-Length", "2")
                .withBody(new ByteArrayInputStream(new byte[] {(byte) 0xc0, (byte) 0xaf})));
        assertThrows(IOException.class, () -> new WebFetchTool(utf8, url -> true).execute(args(ARGUMENTS), context()));
        Calls ambiguous = new Calls(
                request -> response(request, 200, "text/plain", "text").addHeader("Content-Type", "image/png"));
        assertThrows(
                IOException.class, () -> new WebFetchTool(ambiguous, url -> true).execute(args(ARGUMENTS), context()));
        for (String length : List.of("99999999", "1", "5", "-1", "x", "1, 2")) {
            Calls calls = new Calls(
                    request -> response(request, 200, "text/plain", "text").withHeader("Content-Length", length));
            assertThrows(
                    IOException.class, () -> new WebFetchTool(calls, url -> true).execute(args(ARGUMENTS), context()));
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
        assertThrows(IOException.class, () -> new WebFetchTool(calls, url -> true).execute(args(ARGUMENTS), context()));
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
        assertThrows(
                IOException.class,
                () -> new WebFetchTool(headerCount, url -> true).execute(args(ARGUMENTS), context()));
        Calls headerBytes = new Calls(
                request -> response(request, 200, "text/plain", "text").withHeader("X-Fixture", "x".repeat(16_385)));
        assertThrows(
                IOException.class,
                () -> new WebFetchTool(headerBytes, url -> true).execute(args(ARGUMENTS), context()));
        Calls html = new Calls(request -> response(request, 200, "text/html", "<i>x</i>".repeat(20_000)));
        assertThrows(IOException.class, () -> new WebFetchTool(html, url -> true).execute(args(ARGUMENTS), context()));
    }

    @Test
    void rejectsOversizedDeclaredAndUnknownLengthBodiesAndClosesInput() {
        Calls declared = new Calls(request -> response(request, 200, "text/plain", "x".repeat(262_145)));
        assertThrows(
                IOException.class, () -> new WebFetchTool(declared, url -> true).execute(args(ARGUMENTS), context()));
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
        assertThrows(
                IOException.class, () -> new WebFetchTool(unknown, url -> true).execute(args(ARGUMENTS), context()));
        assertTrue(closed.get());
        assertEquals(262_145, read.get());
    }

    @Test
    void outputBoundsEscapingUnicodeAndControlCharacters() throws Exception {
        String content = ("\"\\中文\uD834\uDD1E\u202e\u0001").repeat(12_000);
        Calls calls = new Calls(request -> response(request, 200, "text/plain", content));
        String output = new WebFetchTool(calls, url -> true).execute(args(ARGUMENTS), context());
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
    void totalDeadlineInterruptsSafetyCheckerAndPreventsSendAfterLateApproval() throws Exception {
        CountDownLatch interrupted = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        CountDownLatch returned = new CountDownLatch(1);
        CountDownLatch sent = new CountDownLatch(1);
        AtomicInteger checks = new AtomicInteger();
        Calls calls = new Calls(request -> {
            sent.countDown();
            return response(request, 200, "text/plain", "Must not be sent");
        });
        WebFetchTool tool = new WebFetchTool(calls, url -> {
            checks.incrementAndGet();
            while (true) {
                try {
                    release.await();
                    break;
                } catch (InterruptedException ignored) {
                    // Simulate a resolver that clears interruption and eventually approves the URL.
                    interrupted.countDown();
                }
            }
            returned.countDown();
            return true;
        });
        try {
            assertTimeoutPreemptively(
                    Duration.ofSeconds(2),
                    () -> assertThrows(
                            IOException.class, () -> tool.execute(args(ARGUMENTS), context(Duration.ofMillis(100)))));
            assertTrue(interrupted.await(1, TimeUnit.SECONDS));
            assertEquals(1, checks.get());
            assertEquals(0, calls.count.get());
        } finally {
            // Approval occurs only after execute has timed out, with the interrupt flag cleared.
            release.countDown();
        }
        assertTrue(returned.await(1, TimeUnit.SECONDS));
        assertFalse(sent.await(200, TimeUnit.MILLISECONDS), "An expired invocation must never send late HTTP");
        assertEquals(0, calls.count.get());
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
                        () -> new WebFetchTool(calls, url -> true)
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
                        () -> new WebFetchTool(calls, url -> true)
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
            WebFetchTool tool = new WebFetchTool(local, url -> true);
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
                            () -> new WebFetchTool(local, url -> true)
                                    .execute(args(ARGUMENTS), context(Duration.ofMillis(200)))));
        } finally {
            release.countDown();
            server.stop(0);
        }
    }

    @Test
    void expiredOrInterruptedContextNeverCallsSafetyCheckerOrTransport() {
        AtomicInteger checks = new AtomicInteger();
        Calls calls = new Calls(request -> {
            throw new AssertionError("Must not make a request");
        });
        WebFetchTool tool = new WebFetchTool(calls, url -> {
            checks.incrementAndGet();
            return true;
        });
        assertThrows(IllegalStateException.class, () -> tool.execute(args(ARGUMENTS), context(Duration.ofNanos(1))));
        try {
            Thread.currentThread().interrupt();
            assertThrows(IllegalStateException.class, () -> tool.execute(args(ARGUMENTS), context()));
        } finally {
            Thread.interrupted();
        }
        assertEquals(0, checks.get());
        assertEquals(0, calls.count.get());
    }
}
