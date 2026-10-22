package fun.freechat.service.tools.network;

import static fun.freechat.service.tools.network.NetworkTestSupport.*;
import static org.junit.jupiter.api.Assertions.*;

import fun.freechat.service.tools.ToolArguments;
import fun.freechat.service.tools.ToolDefinition;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.Okio;
import okio.Source;
import okio.Timeout;
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
        Calls calls = new Calls(request -> response(request, 200, "text/html; charset=UTF-8", """
                <html><head><style>hidden-css</style><script>hidden-script</script></head>
                <body><h1>Actual title</h1><p>Hello <b>world</b>.</p>
                <iframe src="https://internal.local/">hidden-frame</iframe>
                <img src="https://evil.com/track"><a href="javascript:steal()">A reference</a>
                <svg><text>hidden-svg</text></svg><template>hidden-template</template></body></html>
                """)
                .newBuilder()
                .header("Set-Cookie", "tenant=secret")
                .build());
        WebFetchTool tool = new WebFetchTool(HOSTS, calls);
        var result = args(tool.execute(args(ARGUMENTS), context()));
        assertEquals("https://example.com/page?q=1", result.path("url").asText());
        assertEquals(
                "Actual title Hello world. A reference", result.path("text").asText());
        assertTrue(result.path("untrusted").asBoolean());
        assertFalse(result.path("truncated").asBoolean());
        assertEquals("GET", calls.request.method());
        assertEquals("identity", calls.request.header("Accept-Encoding"));
        assertNull(calls.request.header("Authorization"));
        assertNull(calls.request.header("Cookie"));
        assertFalse(calls.request.toString().contains("chat-secret"));
        tool.execute(args(ARGUMENTS), context());
        assertEquals(2, calls.count.get());
        assertNull(calls.request.header("Cookie"));
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
    void malformedArgumentsNeverReachTransport(String json) throws Exception {
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
    void rejectsRedirectsAndErrorsWithoutFollowupOrErrorBodyDisclosure(int status) throws Exception {
        Calls calls = new Calls(request -> response(request, status, "text/plain", "secret provider error")
                .newBuilder()
                .header("Location", "https://169.254.169.254/latest/meta-data/")
                .header("Retry-After", "0")
                .build());
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
                "text/html; charset=unknown"
            })
    void rejectsNontextAndNonUtf8Content(String type) {
        Calls calls = new Calls(request -> response(request, 200, type, "provider-content"));
        assertThrows(IOException.class, () -> new WebFetchTool(HOSTS, calls).execute(args(ARGUMENTS), context()));
    }

    @Test
    void rejectsCompressionInvalidUtf8AndAmbiguousHeaders() {
        for (String encoding : new String[] {"gzip", "br", "deflate", "gzip, identity"}) {
            Calls calls = new Calls(request -> response(request, 200, "text/plain", "compressed")
                    .newBuilder()
                    .header("Content-Encoding", encoding)
                    .build());
            assertThrows(IOException.class, () -> new WebFetchTool(HOSTS, calls).execute(args(ARGUMENTS), context()));
        }
        Calls utf8 = new Calls(request -> response(request, 200, "text/plain", "")
                .newBuilder()
                .body(ResponseBody.create(new byte[] {(byte) 0xc0, (byte) 0xaf}, MediaType.get("text/plain")))
                .build());
        assertThrows(IOException.class, () -> new WebFetchTool(HOSTS, utf8).execute(args(ARGUMENTS), context()));
        Calls ambiguous = new Calls(request -> response(request, 200, "text/plain", "text")
                .newBuilder()
                .addHeader("Content-Type", "image/png")
                .build());
        assertThrows(IOException.class, () -> new WebFetchTool(HOSTS, ambiguous).execute(args(ARGUMENTS), context()));
        Calls length = new Calls(request -> response(request, 200, "text/plain", "text")
                .newBuilder()
                .header("Content-Length", "99999999")
                .build());
        assertThrows(IOException.class, () -> new WebFetchTool(HOSTS, length).execute(args(ARGUMENTS), context()));
        Calls mismatch = new Calls(request -> response(request, 200, "text/plain", "text")
                .newBuilder()
                .header("Content-Length", "1")
                .build());
        assertThrows(IOException.class, () -> new WebFetchTool(HOSTS, mismatch).execute(args(ARGUMENTS), context()));
    }

    @Test
    void rejectsChunkedFramingBeforeReadingAndCancelsBeforeClose() throws Exception {
        for (boolean interceptor : new boolean[] {false, true}) {
            AtomicLong reads = new AtomicLong();
            AtomicBoolean cancelledBeforeClose = new AtomicBoolean();
            var holder = new java.util.concurrent.atomic.AtomicReference<Calls>();
            Source source = new Source() {
                public long read(Buffer buffer, long count) {
                    reads.incrementAndGet();
                    return -1;
                }

                public Timeout timeout() {
                    return Timeout.NONE;
                }

                public void close() {
                    cancelledBeforeClose.set(holder.get().lastCall.isCanceled());
                }
            };
            Calls calls = new Calls(request -> response(request, 200, "text/plain", "")
                    .newBuilder()
                    .header("Transfer-Encoding", "chunked")
                    .body(ResponseBody.create(Okio.buffer(source), MediaType.get("text/plain"), -1))
                    .build());
            holder.set(calls);
            if (interceptor) {
                var request = new okhttp3.Request.Builder()
                        .url("https://example.com/page")
                        .build();
                var call = calls.newCall(request);
                var response = call.execute();
                var chain = (okhttp3.Interceptor.Chain) java.lang.reflect.Proxy.newProxyInstance(
                        getClass().getClassLoader(),
                        new Class<?>[] {okhttp3.Interceptor.Chain.class},
                        (proxy, method, arguments) -> switch (method.getName()) {
                            case "request" -> request;
                            case "call" -> call;
                            case "proceed" -> response;
                            default -> throw new AssertionError(method.getName());
                        });
                var client = NetworkHttp.client(new NetworkPolicy(HOSTS), new NetworkHttp.Deadline(context()));
                assertThrows(
                        IOException.class,
                        () -> client.networkInterceptors().getFirst().intercept(chain));
            } else {
                assertThrows(
                        IOException.class, () -> new WebFetchTool(HOSTS, calls).execute(args(ARGUMENTS), context()));
            }
            assertEquals(0, reads.get());
            assertTrue(cancelledBeforeClose.get());
        }
    }

    @Test
    void boundsHeaderCountHeaderBytesAndHtmlNodeCount() {
        Calls headerCount = new Calls(request -> {
            var builder = response(request, 200, "text/plain", "text").newBuilder();
            for (int i = 0; i < 65; i++) {
                builder.header("X-Fixture-" + i, "value");
            }
            return builder.build();
        });
        assertThrows(IOException.class, () -> new WebFetchTool(HOSTS, headerCount).execute(args(ARGUMENTS), context()));
        Calls headerBytes = new Calls(request -> response(request, 200, "text/plain", "text")
                .newBuilder()
                .header("X-Fixture", "x".repeat(16_385))
                .build());
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
        Source source = new Source() {
            @Override
            public long read(Buffer sink, long byteCount) {
                long count = Math.min(byteCount, 8192);
                sink.write(new byte[(int) count]);
                read.addAndGet(count);
                return count;
            }

            @Override
            public Timeout timeout() {
                return Timeout.NONE;
            }

            @Override
            public void close() {
                closed.set(true);
            }
        };
        Calls unknown = new Calls(request -> response(request, 200, "text/plain", "")
                .newBuilder()
                .body(ResponseBody.create(Okio.buffer(source), MediaType.get("text/plain"), -1))
                .build());
        assertThrows(IOException.class, () -> new WebFetchTool(HOSTS, unknown).execute(args(ARGUMENTS), context()));
        assertTrue(closed.get());
        assertTrue(read.get() <= 262_144 + 8192, "Only one bounded Okio buffer beyond the cap");
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
    void totalDeadlineCancelsStalledTransportAndDoesNotWaitForDnsOrHeaders() {
        CountDownLatch released = new CountDownLatch(1);
        Calls calls = new Calls(request -> {
            try {
                released.await();
                return response(request, 200, "text/plain", "too late");
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
                throw new IOException("Interrupted fixture");
            }
        });
        try {
            assertTimeoutPreemptively(
                    Duration.ofSeconds(2),
                    () -> assertThrows(
                            IOException.class,
                            () -> new WebFetchTool(HOSTS, calls)
                                    .execute(args(ARGUMENTS), context(Duration.ofMillis(100)))));
            assertTrue(calls.lastCall.isCanceled());
        } finally {
            released.countDown();
        }
    }

    @Test
    void totalDeadlineAlsoCancelsSlowBodyAndClosesIt() throws Exception {
        CountDownLatch closed = new CountDownLatch(1);
        CountDownLatch released = new CountDownLatch(1);
        Source source = new Source() {
            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                try {
                    released.await();
                    return -1;
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                    throw new IOException("Interrupted fixture");
                }
            }

            @Override
            public Timeout timeout() {
                return Timeout.NONE;
            }

            @Override
            public void close() {
                closed.countDown();
            }
        };
        Calls calls = new Calls(request -> response(request, 200, "text/plain", "")
                .newBuilder()
                .body(ResponseBody.create(Okio.buffer(source), MediaType.get("text/plain"), -1))
                .build());
        try {
            assertTimeoutPreemptively(
                    Duration.ofSeconds(2),
                    () -> assertThrows(
                            IOException.class,
                            () -> new WebFetchTool(HOSTS, calls)
                                    .execute(args(ARGUMENTS), context(Duration.ofMillis(100)))));
            assertTrue(closed.await(1, TimeUnit.SECONDS));
            assertTrue(calls.lastCall.isCanceled());
        } finally {
            released.countDown();
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
