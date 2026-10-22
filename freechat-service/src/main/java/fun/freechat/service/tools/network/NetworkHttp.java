package fun.freechat.service.tools.network;

import fun.freechat.service.tools.ToolContext;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Proxy;
import java.nio.ByteBuffer;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import okhttp3.Authenticator;
import okhttp3.Call;
import okhttp3.ConnectionPool;
import okhttp3.CookieJar;
import okhttp3.Dns;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/** One exchange, no ambient credentials/proxy/cache, bounded even when platform DNS stalls. */
final class NetworkHttp {
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    // No queue: stuck native resolvers cannot create unbounded work across tenants.
    private static final ThreadPoolExecutor WORKERS =
            new ThreadPoolExecutor(0, 16, 30, TimeUnit.SECONDS, new SynchronousQueue<>(), runnable -> {
                Thread thread = new Thread(runnable, "builtin-network");
                thread.setDaemon(true);
                return thread;
            });
    private final NetworkPolicy policy;
    private final Call.Factory scriptedCalls;

    NetworkHttp(NetworkPolicy policy) {
        this(policy, null);
    }

    /** Package-only HTTP boundary for offline tests; public tools always use the production client. */
    NetworkHttp(NetworkPolicy policy, Call.Factory scriptedCalls) {
        this.policy = policy;
        this.scriptedCalls = scriptedCalls;
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

    static OkHttpClient client(NetworkPolicy policy, Deadline deadline) {
        long remaining = deadline.remaining();
        Dns checkedDns = policy.dns(Dns.SYSTEM);
        return new OkHttpClient.Builder()
                .proxy(Proxy.NO_PROXY)
                .cookieJar(CookieJar.NO_COOKIES)
                .authenticator(Authenticator.NONE)
                .proxyAuthenticator(Authenticator.NONE)
                .cache(null)
                .protocols(List.of(Protocol.HTTP_1_1))
                .connectionPool(new ConnectionPool(0, 1, TimeUnit.SECONDS))
                .followRedirects(false)
                .followSslRedirects(false)
                .retryOnConnectionFailure(false)
                .callTimeout(remaining, TimeUnit.NANOSECONDS)
                .connectTimeout(Math.min(remaining, TimeUnit.SECONDS.toNanos(3)), TimeUnit.NANOSECONDS)
                .readTimeout(Math.min(remaining, TimeUnit.SECONDS.toNanos(4)), TimeUnit.NANOSECONDS)
                .writeTimeout(Math.min(remaining, TimeUnit.SECONDS.toNanos(3)), TimeUnit.NANOSECONDS)
                .dns(host -> {
                    deadline.remaining();
                    var addresses = checkedDns.lookup(host);
                    deadline.remaining();
                    return addresses;
                })
                .addNetworkInterceptor(chain -> {
                    deadline.remaining();
                    Response response = chain.proceed(chain.request());
                    // Chunk extensions are unbounded in OkHttp 4; cancel before close can drain the body.
                    if (response.code() != 200
                            || !response.headers("Transfer-Encoding").isEmpty()) {
                        chain.call().cancel();
                        response.close();
                        throw failure();
                    }
                    return response;
                })
                .build();
    }

    Payload exchange(
            HttpUrl destination,
            String jsonBody,
            String apiKey,
            Set<String> mediaTypes,
            int maxBytes,
            Deadline deadline)
            throws IOException {
        // Policy remains enforced even with a scripted HTTP boundary.
        HttpUrl url = policy.url(destination.toString());
        Request.Builder builder = new Request.Builder()
                .url(url)
                .header("Accept", String.join(", ", mediaTypes))
                .header("Accept-Encoding", "identity")
                .header("User-Agent", "FreeChat-Network-Tools/1.0");
        if (jsonBody != null) {
            builder.post(RequestBody.create(jsonBody, JSON));
        }
        if (apiKey != null) {
            builder.header("Authorization", "Bearer " + apiKey);
        }
        Call call = (scriptedCalls == null ? client(policy, deadline) : scriptedCalls).newCall(builder.build());
        Future<Payload> pending = null;
        try {
            deadline.remaining();
            pending = WORKERS.submit(() -> {
                deadline.remaining();
                Response response = call.execute();
                try {
                    return read(response, mediaTypes, maxBytes, deadline);
                } finally {
                    call.cancel();
                    response.close();
                }
            });
            Payload result = pending.get(deadline.remaining(), TimeUnit.NANOSECONDS);
            deadline.remaining();
            return result;
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
            throw failure();
        } catch (ExecutionException | TimeoutException | RejectedExecutionException ignored) {
            // Do not propagate URLs, provider error bodies, queries or credentials through causes/logs.
            throw failure();
        } finally {
            call.cancel();
            if (pending != null) {
                pending.cancel(true);
            }
        }
    }

    private static Payload read(Response response, Set<String> mediaTypes, int maxBytes, Deadline deadline)
            throws IOException {
        deadline.remaining();
        if (response.code() != 200
                || response.body() == null
                || !response.headers("Transfer-Encoding").isEmpty()
                || response.headers().size() > 64) {
            throw failure();
        }
        int headerBytes = 0;
        for (int i = 0; i < response.headers().size(); i++) {
            headerBytes += response.headers().name(i).length()
                    + response.headers().value(i).length();
        }
        if (headerBytes > 16_384
                || response.headers("Content-Type").size() != 1
                || response.headers("Content-Encoding").size() > 1
                || response.headers("Content-Length").size() > 1) {
            throw failure();
        }
        String encoding = response.header("Content-Encoding", "identity");
        if (!"identity".equalsIgnoreCase(encoding)) {
            // No transparent decompression, compressed bombs or encoding-dependent fallback.
            throw failure();
        }
        MediaType type = MediaType.parse(response.header("Content-Type", ""));
        if (type == null || !mediaTypes.contains(type.type() + "/" + type.subtype())) {
            throw failure();
        }
        String charset = type.parameter("charset");
        if (charset != null && !charset.equalsIgnoreCase("utf-8") && !charset.equalsIgnoreCase("utf8")) {
            throw failure();
        }
        long declared = response.body().contentLength();
        if (declared < -1 || declared > maxBytes) {
            throw failure();
        }
        String lengthHeader = response.header("Content-Length");
        if (lengthHeader != null) {
            if (!lengthHeader.matches("[0-9]{1,10}")) {
                throw failure();
            }
            long headerLength = Long.parseLong(lengthHeader);
            if (headerLength > maxBytes || declared >= 0 && declared != headerLength) {
                throw failure();
            }
            declared = headerLength;
        }
        ByteArrayOutputStream bytes = new ByteArrayOutputStream(Math.min(maxBytes, 8192));
        byte[] buffer = new byte[8192];
        try (var input = response.body().byteStream()) {
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
        return new Payload(type.type() + "/" + type.subtype(), text);
    }

    static IOException failure() {
        return new IOException("Network request failed or exceeded limits");
    }
}
