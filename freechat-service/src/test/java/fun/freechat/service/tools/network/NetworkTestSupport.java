package fun.freechat.service.tools.network;

import com.fasterxml.jackson.databind.JsonNode;
import fun.freechat.service.tools.ToolArguments;
import fun.freechat.service.tools.ToolContext;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;
import java.util.concurrent.atomic.AtomicInteger;
import javax.net.ssl.SSLSession;

final class NetworkTestSupport {
    private NetworkTestSupport() {}

    static ToolContext context() {
        return context(Duration.ofSeconds(10));
    }

    static ToolContext context(Duration timeout) {
        return new ToolContext("chat-secret", "user-secret", "character-secret", "backend-secret", timeout);
    }

    static JsonNode args(String json) throws IOException {
        return ToolArguments.JSON.readTree(json);
    }

    static Response response(HttpRequest request, int status, String mediaType, String content) {
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        return new Response(
                request,
                status,
                HttpHeaders.of(
                        Map.of(
                                "Content-Type",
                                List.of(mediaType),
                                "Content-Length",
                                List.of(Integer.toString(bytes.length))),
                        (name, value) -> true),
                new ByteArrayInputStream(bytes));
    }

    record Response(HttpRequest request, int statusCode, HttpHeaders headers, InputStream body)
            implements HttpResponse<InputStream> {
        public Optional<HttpResponse<InputStream>> previousResponse() {
            return Optional.empty();
        }

        public Optional<SSLSession> sslSession() {
            return Optional.empty();
        }

        public URI uri() {
            return request.uri();
        }

        public HttpClient.Version version() {
            return HttpClient.Version.HTTP_1_1;
        }

        Response withHeader(String name, String value) {
            Map<String, List<String>> updated = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            updated.putAll(headers.map());
            if (value == null) {
                updated.remove(name);
            } else {
                updated.put(name, List.of(value));
            }
            return new Response(request, statusCode, HttpHeaders.of(updated, (key, item) -> true), body);
        }

        Response addHeader(String name, String value) {
            Map<String, List<String>> updated = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            updated.putAll(headers.map());
            List<String> values = new ArrayList<>(headers.allValues(name));
            values.add(value);
            updated.put(name, values);
            return new Response(request, statusCode, HttpHeaders.of(updated, (key, item) -> true), body);
        }

        Response withBody(InputStream input) {
            return new Response(request, statusCode, headers, input);
        }
    }

    @FunctionalInterface
    interface Script {
        HttpResponse<InputStream> send(HttpRequest request) throws IOException, InterruptedException;
    }

    static final class Calls implements NetworkHttp.Transport {
        final AtomicInteger count = new AtomicInteger();
        volatile HttpRequest request;
        private final Script script;

        Calls(Script script) {
            this.script = script;
        }

        @Override
        public HttpResponse<InputStream> send(HttpRequest request) throws IOException, InterruptedException {
            this.request = request;
            count.incrementAndGet();
            return script.send(request);
        }
    }

    static String bodyText(HttpRequest request) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        CompletableFuture<String> result = new CompletableFuture<>();
        request.bodyPublisher().orElseThrow().subscribe(new Flow.Subscriber<>() {
            public void onSubscribe(Flow.Subscription subscription) {
                subscription.request(Long.MAX_VALUE);
            }

            public void onNext(ByteBuffer buffer) {
                byte[] part = new byte[buffer.remaining()];
                buffer.get(part);
                bytes.writeBytes(part);
            }

            public void onError(Throwable failure) {
                result.completeExceptionally(failure);
            }

            public void onComplete() {
                result.complete(bytes.toString(StandardCharsets.UTF_8));
            }
        });
        return result.join();
    }
}
