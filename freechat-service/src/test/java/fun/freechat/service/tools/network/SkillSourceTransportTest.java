package fun.freechat.service.tools.network;

import static fun.freechat.service.tools.network.NetworkTestSupport.*;
import static org.junit.jupiter.api.Assertions.*;

import com.sun.net.httpserver.HttpServer;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@Timeout(5)
class SkillSourceTransportTest {
    private static final URI SOURCE =
            URI.create("https://clawhub.ai/api/v1/skills/story/file?path=SKILL.md&version=1.0.0");
    private static final Set<String> MEDIA = Set.of("text/markdown");

    @ParameterizedTest
    @ValueSource(strings = {"application/octet-stream", "text/x-config", "application/json"})
    void emptyMediaSetAcceptsAnyMimeForBoundedUtf8ButNonemptySetStillRejects(String mediaType) throws Exception {
        String text = "key = café, 中文\r\n";
        int bytes = text.getBytes(StandardCharsets.UTF_8).length;
        Calls calls = new Calls(request -> response(request, 200, mediaType, text));
        var http = new NetworkHttp(new NetworkPolicy(Set.of("clawhub.ai")), calls, true);

        NetworkHttp.Payload payload =
                http.exchange(SOURCE, null, null, Set.of(), bytes, new NetworkHttp.Deadline(context()));
        assertEquals(text, payload.text());
        assertEquals(mediaType, payload.mediaType());
        assertEquals("*/*", calls.request.headers().firstValue("Accept").orElseThrow());
        assertEquals(
                "identity",
                calls.request.headers().firstValue("Accept-Encoding").orElseThrow());
        assertThrows(
                IOException.class,
                () -> http.exchange(SOURCE, null, null, MEDIA, bytes, new NetworkHttp.Deadline(context())));
        assertEquals(
                "text/markdown", calls.request.headers().firstValue("Accept").orElseThrow());
        assertThrows(
                IOException.class,
                () -> http.exchange(SOURCE, null, null, Set.of(), bytes - 1, new NetworkHttp.Deadline(context())));
    }

    @Test
    void emptyMediaSetStillRejectsInvalidUtf8Bytes() {
        for (byte[] bytes : List.of(new byte[] {(byte) 0xff}, new byte[] {(byte) 0xc3, 0x28}, new byte[] {
            (byte) 0xed, (byte) 0xa0, (byte) 0x80
        })) {
            Calls calls = new Calls(request -> response(request, 200, "application/octet-stream", "")
                    .withHeader("Content-Length", Integer.toString(bytes.length))
                    .withBody(new ByteArrayInputStream(bytes)));
            var http = new NetworkHttp(new NetworkPolicy(Set.of("clawhub.ai")), calls, true);
            assertThrows(
                    IOException.class,
                    () -> http.exchange(
                            SOURCE, null, null, Set.of(), bytes.length, new NetworkHttp.Deadline(context())));
        }
    }

    @Test
    void emptyMediaSetStillRejectsMalformedHeadersCompressionAndFraming() {
        for (Script script : List.<Script>of(
                request -> response(request, 200, "text/x-config", "text").withHeader("Content-Type", null),
                request -> response(request, 200, "text/x-config", "text").addHeader("Content-Type", "text/plain"),
                request -> response(request, 200, "invalid", "text"),
                request -> response(request, 200, "text/x-config; charset=iso-8859-1", "text"),
                request -> response(request, 200, "text/x-config", "text").withHeader("Content-Encoding", "gzip"),
                request -> response(request, 200, "text/x-config", "text")
                        .withHeader("Content-Encoding", "identity")
                        .addHeader("Content-Encoding", "identity"),
                request -> response(request, 200, "text/x-config", "text").addHeader("Content-Length", "4"),
                request -> response(request, 200, "text/x-config", "text").withHeader("Content-Length", "3"),
                request ->
                        response(request, 200, "text/x-config", "text").withHeader("Transfer-Encoding", "chunked"))) {
            var http = new NetworkHttp(new NetworkPolicy(Set.of("clawhub.ai")), new Calls(script), true);
            assertThrows(
                    IOException.class,
                    () -> http.exchange(SOURCE, null, null, Set.of(), 32, new NetworkHttp.Deadline(context())));
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void chunkedIsAcceptedOnlyForExplicitProviderImportsAndStillBounded(boolean anyMedia) throws Exception {
        Set<String> media = anyMedia ? Set.of() : MEDIA;
        Calls calls = new Calls(request -> response(request, 200, "text/markdown", "document")
                .withHeader("Content-Length", null)
                .withHeader("Transfer-Encoding", "chunked"));
        var policy = new NetworkPolicy(Set.of("clawhub.ai"));
        assertThrows(
                IOException.class,
                () -> new NetworkHttp(policy, calls)
                        .exchange(SOURCE, null, null, media, 8, new NetworkHttp.Deadline(context())));
        assertEquals(
                "document",
                new NetworkHttp(policy, calls, true)
                        .exchange(SOURCE, null, null, media, 8, new NetworkHttp.Deadline(context()))
                        .text());
        assertThrows(
                IOException.class,
                () -> new NetworkHttp(policy, calls, true)
                        .exchange(SOURCE, null, null, media, 7, new NetworkHttp.Deadline(context())));
        for (String encoding : new String[] {"gzip, chunked", "chunked, chunked", "identity", "gzip"}) {
            Calls invalid = new Calls(request -> response(request, 200, "text/markdown", "text")
                    .withHeader("Content-Length", null)
                    .withHeader("Transfer-Encoding", encoding));
            assertThrows(
                    IOException.class,
                    () -> new NetworkHttp(policy, invalid, true)
                            .exchange(SOURCE, null, null, media, 32, new NetworkHttp.Deadline(context())));
        }
        Calls ambiguous = new Calls(
                request -> response(request, 200, "text/markdown", "text").withHeader("Transfer-Encoding", "chunked"));
        assertThrows(
                IOException.class,
                () -> new NetworkHttp(policy, ambiguous, true)
                        .exchange(SOURCE, null, null, media, 32, new NetworkHttp.Deadline(context())));
    }

    @Test
    void jdkDecodesRealChunkedProviderResponsesWithinDocumentBudget() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        byte[] bytes = "---\nname: story\ndescription: Narration\n---\n中文 story".getBytes(StandardCharsets.UTF_8);
        server.createContext("/file", exchange -> {
            exchange.getResponseHeaders().set("Content-Type", "text/markdown");
            exchange.sendResponseHeaders(200, 0);
            try (var output = exchange.getResponseBody()) {
                output.write(bytes);
            }
        });
        server.start();
        try (HttpClient client = NetworkHttp.client()) {
            NetworkHttp.Transport transport = request -> client.send(
                    HttpRequest.newBuilder(URI.create(
                                    "http://127.0.0.1:" + server.getAddress().getPort() + "/file"))
                            .build(),
                    HttpResponse.BodyHandlers.ofInputStream());
            var http = new NetworkHttp(new NetworkPolicy(Set.of("clawhub.ai")), transport, true);
            assertEquals(
                    new String(bytes, StandardCharsets.UTF_8),
                    http.exchange(SOURCE, null, null, MEDIA, bytes.length, new NetworkHttp.Deadline(context()))
                            .text());
            assertThrows(
                    IOException.class,
                    () -> http.exchange(
                            SOURCE, null, null, MEDIA, bytes.length - 1, new NetworkHttp.Deadline(context())));
        } finally {
            server.stop(0);
        }
    }
}
