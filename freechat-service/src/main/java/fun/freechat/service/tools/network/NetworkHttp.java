package fun.freechat.service.tools.network;

import fun.freechat.service.tools.ToolContext;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

final class NetworkHttp {
    private static final HttpClient CLIENT = client();
    // A blocked resolver or server must not create unbounded work across sessions.
    private static final ThreadPoolExecutor WORKERS =
            new ThreadPoolExecutor(0, 16, 30, TimeUnit.SECONDS, new SynchronousQueue<>(), runnable -> {
                Thread thread = new Thread(runnable, "builtin-network");
                thread.setDaemon(true);
                return thread;
            });
    private final NetworkPolicy policy;
    private final Transport transport;

    @FunctionalInterface
    interface Transport {
        HttpResponse<InputStream> send(HttpRequest request) throws IOException, InterruptedException;
    }

    NetworkHttp(NetworkPolicy policy, Transport transport) {
        this.policy = policy;
        this.transport = transport == null
                ? request -> CLIENT.send(request, HttpResponse.BodyHandlers.ofInputStream())
                : transport;
    }

    record Payload(String mediaType, String text) {}

    static final class Deadline {
        private final ToolContext context;
        private final long end;

        Deadline(ToolContext context) {
            this.context = context;
            end = System.nanoTime()
                    + Math.min(TimeUnit.SECONDS.toNanos(15), context.remaining().toNanos());
        }

        long remaining() {
            context.check();
            long left = end - System.nanoTime();
            if (left <= 0) {
                throw new IllegalStateException("Network tool deadline exceeded");
            }
            return left;
        }
    }

    static HttpClient client() {
        return HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(3))
                .followRedirects(HttpClient.Redirect.NEVER)
                .proxy(HttpClient.Builder.NO_PROXY)
                .build();
    }

    Payload exchange(
            URI destination, String jsonBody, String apiKey, Set<String> mediaTypes, int maxBytes, Deadline deadline)
            throws IOException {
        URI uri = policy.url(destination.toString());
        HttpRequest.Builder builder = HttpRequest.newBuilder(uri)
                .timeout(Duration.ofNanos(deadline.remaining()))
                .header("Accept", String.join(", ", mediaTypes))
                .header("Accept-Encoding", "identity")
                .header("User-Agent", "FreeChat-Network-Tools/1.0");
        if (jsonBody != null) {
            builder.header("Content-Type", "application/json; charset=utf-8")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8));
        }
        if (apiKey != null) {
            builder.header("Authorization", "Bearer " + apiKey);
        }
        HttpRequest request = builder.build();
        AtomicReference<InputStream> activeBody = new AtomicReference<>();
        Future<Payload> pending = null;
        try {
            deadline.remaining();
            pending = WORKERS.submit(() -> {
                deadline.remaining();
                HttpResponse<InputStream> response = transport.send(request);
                try (InputStream body = response.body()) {
                    activeBody.set(body);
                    return read(response, mediaTypes, maxBytes, deadline);
                } finally {
                    activeBody.set(null);
                }
            });
            Payload result = pending.get(deadline.remaining(), TimeUnit.NANOSECONDS);
            deadline.remaining();
            return result;
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
            throw failure();
        } catch (ExecutionException | TimeoutException | RejectedExecutionException ignored) {
            throw failure();
        } finally {
            if (pending != null) {
                pending.cancel(true);
            }
            InputStream body = activeBody.getAndSet(null);
            if (body != null) {
                try {
                    body.close();
                } catch (IOException ignored) {
                    // Transport errors must not disclose URLs, response bodies or credentials.
                }
            }
        }
    }

    private static Payload read(
            HttpResponse<InputStream> response, Set<String> mediaTypes, int maxBytes, Deadline deadline)
            throws IOException {
        deadline.remaining();
        var headers = response.headers();
        if (response.statusCode() != 200
                || response.body() == null
                || !headers.allValues("Transfer-Encoding").isEmpty()) {
            throw failure();
        }
        int headerCount = 0;
        long headerBytes = 0;
        for (var entry : headers.map().entrySet()) {
            for (String value : entry.getValue()) {
                headerCount++;
                headerBytes += (long) entry.getKey().length() + value.length();
            }
        }
        if (headerCount > 64
                || headerBytes > 16_384
                || headers.allValues("Content-Type").size() != 1
                || headers.allValues("Content-Encoding").size() > 1
                || headers.allValues("Content-Length").size() > 1
                || !"identity"
                        .equalsIgnoreCase(headers.firstValue("Content-Encoding").orElse("identity"))) {
            throw failure();
        }
        MimeType type;
        try {
            type = MimeTypeUtils.parseMimeType(
                    headers.firstValue("Content-Type").orElseThrow());
            if (!mediaTypes.contains(type.getType() + "/" + type.getSubtype())
                    || type.getCharset() != null && !type.getCharset().equals(StandardCharsets.UTF_8)) {
                throw failure();
            }
        } catch (IllegalArgumentException ignored) {
            throw failure();
        }
        long declared = -1;
        String length = headers.firstValue("Content-Length").orElse(null);
        if (length != null) {
            if (!length.matches("[0-9]{1,10}") || (declared = Long.parseLong(length)) > maxBytes) {
                throw failure();
            }
        }
        ByteArrayOutputStream bytes = new ByteArrayOutputStream(Math.min(maxBytes, 8192));
        byte[] buffer = new byte[8192];
        InputStream input = response.body();
        while (true) {
            deadline.remaining();
            int count = input.read(buffer, 0, Math.min(buffer.length, maxBytes - bytes.size() + 1));
            if (count == -1) {
                break;
            }
            if (count == 0 || count > maxBytes - bytes.size()) {
                throw failure();
            }
            bytes.write(buffer, 0, count);
        }
        if (declared >= 0 && declared != bytes.size()) {
            throw failure();
        }
        String text = StandardCharsets.UTF_8
                .newDecoder()
                .onMalformedInput(CodingErrorAction.REPORT)
                .onUnmappableCharacter(CodingErrorAction.REPORT)
                .decode(ByteBuffer.wrap(bytes.toByteArray()))
                .toString();
        deadline.remaining();
        return new Payload(type.getType() + "/" + type.getSubtype(), text);
    }

    static IOException failure() {
        return new IOException("Network request failed or exceeded limits");
    }
}
