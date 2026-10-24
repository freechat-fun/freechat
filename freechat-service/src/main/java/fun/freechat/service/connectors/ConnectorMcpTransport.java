package fun.freechat.service.connectors;

import static fun.freechat.service.connectors.ConnectorException.Code.*;
import static fun.freechat.service.connectors.ConnectorMcpMessages.*;

import com.fasterxml.jackson.databind.JsonNode;
import dev.langchain4j.mcp.client.McpCallContext;
import dev.langchain4j.mcp.client.transport.McpJson;
import dev.langchain4j.mcp.client.transport.McpOperationHandler;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.protocol.McpClientMessage;
import dev.langchain4j.mcp.protocol.McpInitializationNotification;
import dev.langchain4j.mcp.protocol.McpInitializeRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Flow;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

// JDK resolves again when connecting; public-address validation is not DNS pinning.
final class ConnectorMcpTransport implements McpTransport {
    private static final int WIRE_BYTES = 2 * 1024 * 1024;
    private static final Semaphore WORKERS = new Semaphore(256);
    private static final ScheduledThreadPoolExecutor EXPIRY = expiryExecutor();
    private static final ProxySelector DIRECT = new ProxySelector() {
        @Override
        public List<Proxy> select(URI uri) {
            return List.of(Proxy.NO_PROXY);
        }

        @Override
        public void connectFailed(URI uri, SocketAddress address, IOException error) {}
    };

    private final ConnectorConfiguration configuration;
    private final URI origin;
    private final boolean legacySse;
    private final Runnable authorize;
    private final Predicate<String> publicDestination;
    private final Runnable onClosed;
    private final long expires;
    private volatile HttpClient http;
    private final Object dispatchGate = new Object();
    private final Object envelopeGate = new Object();
    private final AtomicReference<ConnectorException.Code> terminal = new AtomicReference<>();
    private final AtomicReference<Operation> operation = new AtomicReference<>();
    private final Set<Future<?>> workers = ConcurrentHashMap.newKeySet();
    private final Set<ScheduledFuture<?>> controlDeadlines = ConcurrentHashMap.newKeySet();
    private final Set<CompletableFuture<?>> exchanges = ConcurrentHashMap.newKeySet();
    private final Set<CompletableFuture<?>> initializationResults = ConcurrentHashMap.newKeySet();
    private final Set<InputStream> bodies = ConcurrentHashMap.newKeySet();
    private final Set<Flow.Subscription> subscriptions = ConcurrentHashMap.newKeySet();
    private final Map<Long, Pending> pending = new ConcurrentHashMap<>();
    private final Semaphore localWorkers = new Semaphore(8);
    private final AtomicInteger sends = new AtomicInteger();
    private final AtomicInteger events = new AtomicInteger();
    private final AtomicInteger messages = new AtomicInteger();
    private final AtomicLong wire = new AtomicLong();
    private final CompletableFuture<URI> advertisedEndpoint = new CompletableFuture<>();
    private volatile McpOperationHandler handler;
    private volatile String session;
    private volatile String protocol;
    private volatile ScheduledFuture<?> expiry;

    private static ScheduledThreadPoolExecutor expiryExecutor() {
        var executor = new ScheduledThreadPoolExecutor(2, task -> {
            Thread thread = new Thread(task, "connector-expiry");
            thread.setDaemon(true);
            return thread;
        });
        executor.setRemoveOnCancelPolicy(true);
        return executor;
    }

    ConnectorMcpTransport(
            ConnectorConfiguration configuration,
            Duration lifetime,
            Runnable authorize,
            Predicate<String> publicDestination,
            Runnable onClosed) {
        this.configuration = configuration;
        this.origin = URI.create(configuration.url()).normalize();
        this.legacySse = "sse".equals(configuration.transport());
        this.protocol = legacySse ? "2024-11-05" : "2025-11-25";
        this.authorize = authorize;
        this.publicDestination = publicDestination;
        this.onClosed = onClosed;
        Duration bounded = lifetime.compareTo(Duration.ofMinutes(10)) > 0 ? Duration.ofMinutes(10) : lifetime;
        this.expires = System.nanoTime() + bounded.toNanos();
        // Arm lifetime before initialization or even HTTP-client construction starts.
        expiry = EXPIRY.schedule(() -> fail(CONNECTOR_TIMEOUT), Math.max(1, remaining(null)), TimeUnit.NANOSECONDS);
        if (terminal.get() != null) {
            expiry.cancel(false);
        }
    }

    String initialProtocol() {
        return legacySse ? "2024-11-05" : "2025-11-25";
    }

    private static final class Operation {
        final long deadline;
        final Discovery discovery = new Discovery();
        volatile boolean finished;

        Operation(long deadline) {
            this.deadline = deadline;
        }
    }

    private record Pending(String method, Operation operation, CompletableFuture<String> future) {}

    <T> T withinDeadline(Callable<T> action) {
        guard(null);
        Operation op = new Operation(
                Math.min(expires, System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(configuration.timeout())));
        if (!operation.compareAndSet(null, op)) {
            throw limit();
        }
        Future<T> task = null;
        try {
            task = worker(() -> {
                reauthorize(op);
                T result = action.call();
                reauthorize(op);
                return result;
            });
            return task.get(Math.max(1, remaining(op)), TimeUnit.NANOSECONDS);
        } catch (TimeoutException ignored) {
            fail(CONNECTOR_TIMEOUT);
            throw new ConnectorException(CONNECTOR_TIMEOUT);
        } catch (InterruptedException ignored) {
            fail(CONNECTOR_UNAVAILABLE);
            Thread.currentThread().interrupt();
            throw unavailable();
        } catch (Exception error) {
            ConnectorException failure =
                    terminal.get() == null ? sanitized(error) : new ConnectorException(terminal.get());
            fail(failure.code());
            throw failure;
        } finally {
            op.finished = true;
            operation.compareAndSet(op, null);
            if (task != null && !task.isDone()) {
                task.cancel(true);
            }
        }
    }

    private <T> Future<T> worker(Callable<T> action) {
        guard(null);
        if (!WORKERS.tryAcquire()) {
            throw limit();
        }
        if (!localWorkers.tryAcquire()) {
            WORKERS.release();
            throw limit();
        }
        FutureTask<T> task = new FutureTask<>(action);
        workers.add(task);
        try {
            Thread.startVirtualThread(() -> {
                try {
                    if (terminal.get() == null) {
                        task.run();
                    } else {
                        task.cancel(true);
                    }
                } finally {
                    workers.remove(task);
                    localWorkers.release();
                    // Keep admission until blocked callbacks actually exit, not until cancellation.
                    WORKERS.release();
                }
            });
        } catch (RuntimeException error) {
            workers.remove(task);
            localWorkers.release();
            WORKERS.release();
            throw unavailable();
        }
        return task;
    }

    private long remaining(Operation op) {
        return (op == null ? expires : Math.min(expires, op.deadline)) - System.nanoTime();
    }

    private void guard(Operation op) {
        ConnectorException.Code code = terminal.get();
        if (code != null) {
            throw new ConnectorException(code);
        }
        if (remaining(op) <= 0) {
            throw new ConnectorException(CONNECTOR_TIMEOUT);
        }
        if (Thread.currentThread().isInterrupted() || (op != null && op.finished)) {
            throw unavailable();
        }
    }

    private void reauthorize(Operation op) {
        guard(op);
        try {
            authorize.run();
        } catch (Exception error) {
            throw error instanceof ConnectorException connector
                    ? new ConnectorException(connector.code())
                    : new ConnectorException(CONNECTOR_ACCESS_DENIED);
        }
        guard(op);
    }

    private Operation current() {
        Operation op = operation.get();
        if (op == null) {
            throw unavailable();
        }
        guard(op);
        return op;
    }

    @Override
    public void start(McpOperationHandler messageHandler) {
        guard(current());
        if (handler != null) {
            throw unavailable();
        }
        handler = messageHandler;
        // Construction (including TLS defaults) belongs to the initialization deadline worker.
        HttpClient built = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NEVER)
                .proxy(DIRECT)
                .connectTimeout(Duration.ofMillis(configuration.timeout()))
                .build();
        // No cookie handler or authenticator, including JVM-wide defaults. No TLS overrides.
        synchronized (dispatchGate) {
            try {
                guard(current());
                http = built;
            } catch (Exception error) {
                built.shutdownNow();
                throw error;
            }
        }
        if (legacySse) {
            Operation op = current();
            worker(() -> {
                try {
                    readResponse(dispatch(origin, null, op, true), null, null, true);
                    throw unavailable(); // A disconnected legacy stream is never reconnected/replayed.
                } catch (Exception error) {
                    fail(sanitized(error).code());
                }
                return null;
            });
        }
    }

    @Override
    public CompletableFuture<String> sendInitializeRequest(McpInitializeRequest request) {
        // Authorization outside the envelope lock keeps stalled callbacks from blocking close.
        CompletableFuture<String> initialized = new CompletableFuture<>();
        synchronized (envelopeGate) {
            guard(current());
            initializationResults.add(initialized);
        }
        initialized.whenComplete((value, error) -> {
            initializationResults.remove(initialized);
            if (initialized.isCancelled()) fail(CONNECTOR_TIMEOUT);
        });
        try {
            sendRequest(request).whenComplete((result, error) -> {
                if (error != null) {
                    initialized.completeExceptionally(sanitized(error));
                    return;
                }
                try {
                    worker(() -> {
                        try {
                            sendMessage(new McpInitializationNotification());
                            initialized.complete(result);
                        } catch (Exception failure) {
                            initialized.completeExceptionally(sanitized(failure));
                        }
                        return null;
                    });
                } catch (Exception failure) {
                    initialized.completeExceptionally(sanitized(failure));
                }
            });
        } catch (Exception error) {
            initialized.completeExceptionally(sanitized(error));
        }
        return initialized;
    }

    @Override
    public CompletableFuture<String> sendRequest(McpClientMessage request) {
        Operation op = current();
        String json = McpJson.serialize(request);
        JsonNode message = parse(json, REQUEST_BYTES);
        String method = message.path("method").asText();
        if (!Set.of("initialize", "tools/list", "tools/call", "ping").contains(method) || request.getId() == null) {
            throw unavailable();
        }
        if ("tools/list".equals(method) && ++op.discovery.pages > 8) {
            throw limit();
        }
        CompletableFuture<String> answer = new CompletableFuture<>();
        Pending expected = new Pending(method, op, answer);
        synchronized (envelopeGate) {
            guard(op);
            if (pending.putIfAbsent(request.getId(), expected) != null) {
                throw unavailable();
            }
            handler.expectResponse(request.getId(), answer);
        }
        answer.whenComplete((value, error) -> {
            pending.remove(request.getId(), expected);
            if (answer.isCancelled()) {
                fail(CONNECTOR_TIMEOUT);
            }
        });
        try {
            worker(() -> {
                try {
                    post(json, op, answer);
                } catch (Exception error) {
                    if (!answer.isDone()) {
                        fail(sanitized(error).code());
                    }
                }
                return null;
            });
        } catch (Exception error) {
            fail(sanitized(error).code());
        }
        return answer;
    }

    @Override
    public CompletableFuture<String> sendRequest(McpCallContext context) {
        return sendRequest(context.message());
    }

    @Override
    public void sendMessage(McpClientMessage request) {
        // LC cancellation after timeout must not create a fresh dispatch outside the expired scope.
        if (terminal.get() != null) {
            return;
        }
        try {
            post(McpJson.serialize(request), current(), null);
        } catch (Exception error) {
            ConnectorException failure = sanitized(error);
            fail(failure.code());
            throw failure;
        }
    }

    @Override
    public void sendMessage(McpCallContext context) {
        sendMessage(context.message());
    }

    private void post(String json, Operation op, CompletableFuture<String> answer) throws Exception {
        URI target = legacySse ? advertisedEndpoint.get(Math.max(1, remaining(op)), TimeUnit.NANOSECONDS) : origin;
        readResponse(dispatch(target, bytes(json, REQUEST_BYTES), op, false), op, answer, false);
        if (answer != null && !legacySse && !answer.isDone()) {
            throw unavailable();
        }
    }

    private HttpResponse<InputStream> dispatch(URI target, byte[] body, Operation op, boolean longStream)
            throws Exception {
        guard(op);
        if (!sameOrigin(origin, target)) {
            throw new ConnectorException(CONNECTOR_UNSAFE_ADDRESS);
        }
        // DNS may block. It runs only on an admitted deadline worker, never on the caller.
        boolean safe = publicDestination.test(target.toString());
        guard(op); // In particular, a late DNS result may not construct or send a request.
        if (!safe) {
            throw new ConnectorException(CONNECTOR_UNSAFE_ADDRESS);
        }
        reauthorize(op);
        CompletableFuture<HttpResponse<InputStream>> exchange;
        synchronized (dispatchGate) {
            guard(op);
            if (sends.incrementAndGet() > 128) {
                throw limit();
            }
            // Persistent GET body reads must use the session deadline, not the initialization deadline.
            HttpRequest.Builder builder = HttpRequest.newBuilder(target)
                    .timeout(Duration.ofNanos(Math.max(1, remaining(longStream ? null : op))))
                    .header("Accept", body == null ? "text/event-stream" : "application/json, text/event-stream")
                    .header("Accept-Encoding", "identity");
            configuration.headers().forEach(builder::header);
            if (session != null) {
                builder.header("Mcp-Session-Id", session);
            }
            if (!legacySse) {
                builder.header("MCP-Protocol-Version", protocol);
            }
            if (body == null) {
                builder.GET();
            } else {
                countWire(body.length);
                builder.header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofByteArray(body));
            }
            HttpRequest request = builder.build();
            countWire(request.headers().map().entrySet().stream()
                    .mapToLong(header -> header.getKey().length()
                            + header.getValue().stream()
                                    .mapToInt(String::length)
                                    .sum())
                    .sum());
            // There are no app-level retries, redirect follows, 404 replays, cookies or authenticators.
            exchange = http.sendAsync(request, info -> subscriber(info, longStream));
            exchanges.add(exchange);
            exchange.whenComplete((response, failure) -> {
                if (response != null) {
                    bodies.add(response.body());
                    if (terminal.get() != null) {
                        closeBody(response.body());
                    }
                }
            });
        }
        try {
            HttpResponse<InputStream> response = exchange.get(Math.max(1, remaining(op)), TimeUnit.NANOSECONDS);
            bodies.add(response.body());
            try {
                guard(op);
                return response;
            } catch (Exception error) {
                closeBody(response.body());
                throw error;
            }
        } finally {
            exchanges.remove(exchange);
            if (!exchange.isDone()) {
                exchange.cancel(true);
            }
        }
    }

    private HttpResponse.BodySubscriber<InputStream> subscriber(HttpResponse.ResponseInfo info, boolean longStream) {
        try {
            validateHeaders(info, longStream);
        } catch (Exception error) {
            ConnectorException failure = sanitized(error);
            fail(failure.code());
            throw failure;
        }
        var delegate = HttpResponse.BodySubscribers.ofInputStream();
        return new HttpResponse.BodySubscriber<>() {
            private Flow.Subscription subscription;
            private long received;

            @Override
            public CompletionStage<InputStream> getBody() {
                return delegate.getBody();
            }

            @Override
            public void onSubscribe(Flow.Subscription value) {
                subscription = value;
                subscriptions.add(value);
                if (terminal.get() != null) {
                    value.cancel();
                    delegate.onError(unavailable());
                } else {
                    delegate.onSubscribe(value);
                }
            }

            @Override
            public void onNext(List<ByteBuffer> buffers) {
                try {
                    guard(null);
                    long size =
                            buffers.stream().mapToLong(ByteBuffer::remaining).sum();
                    countWire(size);
                    received += size;
                    if (!longStream && received > MESSAGE_BYTES) {
                        throw limit();
                    }
                    // Count bytes before handing them to the bounded JDK input-stream queue.
                    delegate.onNext(buffers);
                } catch (Exception error) {
                    subscription.cancel();
                    delegate.onError(sanitized(error));
                    fail(sanitized(error).code());
                }
            }

            @Override
            public void onError(Throwable error) {
                subscriptions.remove(subscription);
                delegate.onError(sanitized(error));
            }

            @Override
            public void onComplete() {
                subscriptions.remove(subscription);
                delegate.onComplete();
            }
        };
    }

    private void validateHeaders(HttpResponse.ResponseInfo info, boolean longStream) {
        int bytes = 0;
        for (var header : info.headers().map().entrySet()) {
            bytes += header.getKey().length();
            for (String value : header.getValue()) {
                bytes += value.length();
            }
            if (bytes > 16 * 1024) {
                throw limit();
            }
        }
        countWire(bytes);
        if (info.statusCode() >= 300 && info.statusCode() < 400) {
            throw new ConnectorException(CONNECTOR_UNSAFE_ADDRESS);
        }
        if (info.statusCode() != 200 && info.statusCode() != 202 && info.statusCode() != 204) {
            throw unavailable();
        }
        for (String encoding : info.headers().allValues("Content-Encoding")) {
            if (!"identity".equalsIgnoreCase(encoding)) {
                throw unavailable();
            }
        }
        long length = info.headers().firstValueAsLong("Content-Length").orElse(-1);
        if (length > (longStream ? WIRE_BYTES : MESSAGE_BYTES)) {
            throw limit();
        }
        List<String> sessions = info.headers().allValues("Mcp-Session-Id");
        if (!sessions.isEmpty()) {
            String value = sessions.getFirst();
            if (sessions.size() != 1
                    || value.isEmpty()
                    || value.length() > 256
                    || !value.chars().allMatch(ch -> ch >= 0x21 && ch <= 0x7e)) {
                throw unavailable();
            }
            synchronized (dispatchGate) {
                if (session != null && !session.equals(value)) {
                    throw unavailable();
                }
                session = value;
            }
        }
    }

    private void countWire(long count) {
        if (wire.addAndGet(count) > WIRE_BYTES) {
            throw limit();
        }
    }

    private void readResponse(
            HttpResponse<InputStream> response, Operation op, CompletableFuture<String> answer, boolean longStream)
            throws IOException {
        InputStream body = response.body();
        try {
            int status = response.statusCode();
            if (status == 202 || status == 204) {
                if (longStream || (answer != null && !legacySse)) {
                    throw unavailable();
                }
                return;
            }
            String type = response.headers()
                    .firstValue("Content-Type")
                    .orElse("")
                    .split(";", 2)[0]
                    .trim()
                    .toLowerCase(Locale.ROOT);
            if ("text/event-stream".equals(type)) {
                readSse(body, op, answer, longStream);
            } else if (!longStream && "application/json".equals(type)) {
                byte[] raw = body.readNBytes(MESSAGE_BYTES + 1);
                if (raw.length > MESSAGE_BYTES) {
                    throw limit();
                }
                receive(utf8(raw));
            } else {
                throw unavailable();
            }
        } finally {
            closeBody(body);
        }
    }

    private void readSse(InputStream body, Operation op, CompletableFuture<String> answer, boolean longStream)
            throws IOException {
        SseLines input = new SseLines(new java.io.BufferedInputStream(body, 8192));
        String event = "message";
        StringBuilder data = new StringBuilder();
        int eventBytes = 0;
        boolean first = true;
        while (true) {
            guard(longStream ? null : op);
            byte[] raw = input.next();
            if (raw == null) {
                // SSE dispatches only complete blank-line-delimited events.
                return;
            }
            eventBytes += raw.length + 1;
            if (eventBytes > MESSAGE_BYTES) {
                throw limit();
            }
            String line = utf8(raw);
            if (first && line.startsWith("\ufeff")) {
                line = line.substring(1);
            }
            first = false;
            if (line.isEmpty()) {
                if (events.incrementAndGet() > 256) {
                    throw limit();
                }
                if (!data.isEmpty()) {
                    String value = data.substring(0, data.length() - 1);
                    if (longStream && "endpoint".equals(event)) {
                        URI target = endpoint(value);
                        if (!advertisedEndpoint.complete(target)) {
                            throw unavailable();
                        }
                    } else if ("message".equals(event)) {
                        receive(value);
                    }
                }
                data.setLength(0);
                event = "message";
                eventBytes = 0;
                if (answer != null && answer.isDone()) {
                    return;
                }
            } else if (!line.startsWith(":")) {
                int colon = line.indexOf(':');
                String field = colon < 0 ? line : line.substring(0, colon);
                String value = colon < 0 ? "" : line.substring(colon + 1);
                if (value.startsWith(" ")) {
                    value = value.substring(1);
                }
                if ("data".equals(field)) {
                    data.append(value).append('\n');
                } else if ("event".equals(field)) {
                    event = value;
                }
                // id/retry are intentionally ignored: no reconnect, replay or Last-Event-ID.
            }
        }
    }

    private static final class SseLines {
        private final InputStream input;
        private boolean skipLf;

        SseLines(InputStream input) {
            this.input = input;
        }

        byte[] next() throws IOException {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            while (true) {
                int ch = input.read();
                if (ch < 0) {
                    return output.size() == 0 ? null : output.toByteArray();
                }
                if (skipLf) {
                    skipLf = false;
                    if (ch == '\n') continue;
                }
                if (ch == '\n' || ch == '\r') {
                    // Do not read ahead after CR: a server may wait for our reply immediately.
                    skipLf = ch == '\r';
                    return output.toByteArray();
                }
                if (output.size() >= MESSAGE_BYTES) {
                    throw limit();
                }
                output.write(ch);
            }
        }
    }

    private URI endpoint(String advertised) {
        try {
            if (advertised.length() > 2048 || unsafeCharacters(advertised)) {
                throw unavailable();
            }
            URI target = origin.resolve(new URI(advertised)).normalize().parseServerAuthority();
            if (!sameOrigin(origin, target)
                    || target.getRawUserInfo() != null
                    || target.getRawFragment() != null
                    || target.getRawAuthority() == null
                    || target.getRawAuthority().contains("%")
                    || target.getRawAuthority().endsWith(":")
                    || target.getPort() == 0
                    || target.getPort() > 65535
                    || unsafeCharacters(target.getPath())
                    || unsafeCharacters(target.getQuery())) {
                throw unavailable();
            }
            return target;
        } catch (Exception ignored) {
            throw new ConnectorException(CONNECTOR_UNSAFE_ADDRESS);
        }
    }

    private static boolean unsafeCharacters(String text) {
        return text != null
                && text.codePoints()
                        .anyMatch(ch -> ch == '\\'
                                || Character.isISOControl(ch)
                                || Character.isWhitespace(ch)
                                || Character.isSpaceChar(ch)
                                || Character.getType(ch) == Character.FORMAT);
    }

    static boolean sameOrigin(URI a, URI b) {
        return b.getHost() != null
                && a.getScheme().equalsIgnoreCase(b.getScheme())
                && a.getHost().equalsIgnoreCase(b.getHost())
                && port(a) == port(b);
    }

    private static int port(URI uri) {
        return uri.getPort() < 0 ? ("https".equalsIgnoreCase(uri.getScheme()) ? 443 : 80) : uri.getPort();
    }

    private void receive(String json) {
        if (messages.incrementAndGet() > 256) {
            throw limit();
        }
        JsonNode message = parse(json, MESSAGE_BYTES);
        if (!"2.0".equals(message.path("jsonrpc").asText())) {
            throw unavailable();
        }
        JsonNode id = message.get("id");
        if (message.has("method")) {
            if (id != null
                    && (id.isTextual() || id.isIntegralNumber())
                    && message.path("method").isTextual()) {
                replyToServer(serverReply(id, message.get("method").textValue()));
            }
            return; // Never reply to notifications or pass server instructions to LC.
        }
        if (id == null || !id.isIntegralNumber() || !id.canConvertToLong()) {
            return; // Unusable/unknown client-response IDs stop before LC's logging handler.
        }
        long number = id.longValue();
        synchronized (envelopeGate) {
            Pending expected = pending.get(number);
            if (expected == null || expected.future().isDone()) {
                return;
            }
            guard(expected.operation());
            if (message.has("error") || !message.path("result").isObject()) {
                throw unavailable();
            }
            JsonNode result = message.get("result");
            JsonNode clean =
                    switch (expected.method()) {
                        case "initialize" -> ConnectorMcpMessages.initialize(result);
                        case "tools/list" -> tools(result, expected.operation().discovery);
                        case "tools/call" -> call(result);
                        case "ping" -> parse("{}", MESSAGE_BYTES);
                        default -> throw unavailable();
                    };
            if ("initialize".equals(expected.method())) {
                setProtocolVersion(clean.path("protocolVersion").asText());
            } else if (!"tools/call".equals(expected.method())
                    || !result.path("isError").asBoolean(false)) {
                // Scan decoded successful results before JSON escaping; errors contain only local text.
                noResultSecrets(
                        "tools/call".equals(expected.method()) ? result : clean,
                        credentialValues(configuration.headers()));
            }
            // Only expected, validated envelopes reach LC to prevent payload logging.
            handler.onMessage(response(number, clean));
        }
    }

    private void replyToServer(String json) {
        // Idle control replies need their own deadline without acquiring the main operation slot.
        Operation control = new Operation(
                Math.min(expires, System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(configuration.timeout())));
        AtomicBoolean completed = new AtomicBoolean();
        ScheduledFuture<?> timeout;
        synchronized (dispatchGate) {
            guard(control);
            timeout = EXPIRY.schedule(
                    () -> {
                        if (completed.compareAndSet(false, true)) {
                            fail(CONNECTOR_TIMEOUT); // Also cancels stalled body readers and physical exchanges.
                        }
                    },
                    Math.max(1, remaining(control)),
                    TimeUnit.NANOSECONDS);
            controlDeadlines.add(timeout);
        }
        Runnable finish = () -> {
            completed.set(true);
            control.finished = true;
            timeout.cancel(false);
            controlDeadlines.remove(timeout);
        };
        try {
            worker(() -> {
                try {
                    post(json, control, null);
                } catch (Exception error) {
                    fail(sanitized(error).code());
                } finally {
                    finish.run();
                }
                return null;
            });
        } catch (RuntimeException error) {
            finish.run();
            throw error;
        }
    }

    @Override
    public void checkHealth() {
        guard(null);
    }

    @Override
    public void onFailure(Runnable ignored) {
        /* Deliberately never reconnect or replay. */
    }

    @Override
    public boolean requiresCancellationNotification() {
        return false;
    }

    @Override
    public void setModernProtocol(boolean modern) {
        if (modern) throw unavailable();
    }

    @Override
    public void setProtocolVersion(String version) {
        if (!VERSIONS.contains(version)) throw unavailable();
        protocol = version;
    }

    private void closeBody(InputStream body) {
        bodies.remove(body);
        try {
            body.close();
        } catch (IOException ignored) {
            // Closing never logs remote or JDK exception payloads.
        }
    }

    void fail(ConnectorException.Code code) {
        synchronized (dispatchGate) {
            if (!terminal.compareAndSet(null, code)) {
                return;
            }
        }
        ScheduledFuture<?> timer = expiry;
        if (timer != null) timer.cancel(false);
        controlDeadlines.forEach(deadline -> deadline.cancel(false));
        controlDeadlines.clear();
        synchronized (envelopeGate) {
            initializationResults.forEach(result -> result.completeExceptionally(new ConnectorException(code)));
            initializationResults.clear();
            pending.values().forEach(value -> value.future().completeExceptionally(new ConnectorException(code)));
            pending.clear();
            if (handler != null) handler.cancelAllPendingOperations("connector closed");
            advertisedEndpoint.completeExceptionally(new ConnectorException(code));
            workers.forEach(task -> task.cancel(true));
        }
        exchanges.forEach(exchange -> exchange.cancel(true));
        subscriptions.forEach(Flow.Subscription::cancel);
        subscriptions.clear();
        bodies.forEach(this::closeBody);
        HttpClient currentHttp = http;
        if (currentHttp != null) currentHttp.shutdownNow();
        onClosed.run();
    }

    @Override
    public void close() {
        fail(CONNECTOR_UNAVAILABLE);
    }
}
