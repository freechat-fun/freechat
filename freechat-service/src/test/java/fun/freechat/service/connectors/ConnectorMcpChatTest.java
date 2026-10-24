package fun.freechat.service.connectors;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.request.json.JsonStringSchema;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.service.tool.ToolExecutor;
import fun.freechat.service.chat.ChatExecution;
import fun.freechat.service.chat.ChatSession;
import fun.freechat.service.tools.ToolArguments;
import fun.freechat.service.tools.ToolContext;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

@Timeout(20)
class ConnectorMcpChatTest {
    private static final ObjectMapper JSON = new ObjectMapper();
    private static final String ID = "3".repeat(32);
    private static final String REVISION = "b".repeat(32);
    // Deliberately collides with the builtin name; the model must only see a namespaced tool.
    private static final String REMOTE = "calculate";
    private static final String ANSWER = "Remote calculation: 35";
    private static final String PRIVATE_INPUT = "private prepared chat input";
    private static final String PRIVATE_TEMPLATE = "private character template";

    @ParameterizedTest(name = "streaming={0}, transport={1}")
    @CsvSource({"false,http", "true,http", "false,sse", "true,sse"})
    void realMcpToolsCompleteTwoRoundsWithFreshSessionsForEachSendAndChat(boolean streaming, String transport)
            throws Exception {
        try (Fixture fixture = new Fixture(transport)) {
            Provider provider = new Provider(false);
            ChatSession first = fixture.session(provider, "chat-one");
            ChatSession other = fixture.session(provider, "chat-two");
            assertTrue(fixture.server.posts.isEmpty());
            assertTrue(fixture.server.peers.isEmpty());

            for (int invocation = 0; invocation < 3; invocation++) {
                boolean anotherChat = invocation == 2;
                String chat = anotherChat ? "chat-two" : "chat-one";
                if (invocation == 1) first.getChatMemory(chat).add(UserMessage.from(PRIVATE_INPUT));
                ChatResponse response = run(anotherChat ? other : first, chat, streaming);
                assertEquals(ANSWER, response.aiMessage().text());
                assertEquals((invocation + 1) * 3, provider.requests.size());
                assertEquals(invocation + 1, fixture.bound.size());
                assertEquals(invocation + 1, fixture.server.peers.size());
                assertEquals((invocation + 1) * 5, fixture.server.posts.size());
                fixture.assertDisposed(invocation);
                fixture.server.assertProtocol(fixture.server.peers.get(invocation), 2);
            }

            assertEquals(
                    3,
                    fixture.server.peers.stream()
                            .map(peer -> peer.id)
                            .distinct()
                            .count());
            String modelName =
                    provider.requests.getFirst().toolSpecifications().getFirst().name();
            assertTrue(provider.requests.stream()
                    .allMatch(request -> modelName.equals(
                            request.toolSpecifications().getFirst().name())));
        }
    }

    @ParameterizedTest(name = "streaming={0}, transport={1}")
    @CsvSource({"false,http", "true,http", "false,sse", "true,sse"})
    void hallucinatedUnnamespacedToolNeverReachesMcp(boolean streaming, String transport) throws Exception {
        try (Fixture fixture = new Fixture(transport)) {
            Provider provider = new Provider(true);
            assertThrows(Exception.class, () -> run(fixture.session(provider, "chat-one"), "chat-one", streaming));
            assertEquals(1, provider.requests.size());
            assertEquals(1, fixture.bound.size());
            assertEquals(1, fixture.server.peers.size());
            assertEquals(3, fixture.server.posts.size()); // Discovery occurred, but never tools/call.
            fixture.assertDisposed(0);
            fixture.server.assertProtocol(fixture.server.peers.getFirst(), 0);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"http", "sse"})
    void canceledBeforeStreamStartClosesDiscoveredSessionWithoutCallingModelOrTool(String transport) throws Exception {
        try (Fixture fixture = new Fixture(transport)) {
            Provider provider = new Provider(false);
            var stream = new ChatExecution(fixture.session(provider, "chat-one"), "chat-one").stream();
            assertEquals(3, fixture.server.posts.size());
            assertInstanceOf(AutoCloseable.class, stream).close();
            assertThrows(IllegalStateException.class, stream::start);
            assertTrue(provider.requests.isEmpty());
            assertEquals(1, fixture.bound.size());
            assertEquals(1, fixture.server.peers.size());
            assertEquals(3, fixture.server.posts.size());
            fixture.assertDisposed(0);
            fixture.server.assertProtocol(fixture.server.peers.getFirst(), 0);
        }
    }

    private static ChatResponse run(ChatSession session, String chat, boolean streaming) throws Exception {
        ChatExecution execution = new ChatExecution(session, chat);
        if (!streaming) return execution.send(null, () -> {}).getLeft();
        CompletableFuture<ChatResponse> completed = new CompletableFuture<>();
        List<String> partials = new CopyOnWriteArrayList<>();
        execution.stream()
                .onPartialResponse(partials::add)
                .onCompleteResponse(completed::complete)
                .onError(completed::completeExceptionally)
                .start();
        ChatResponse response = completed.get(3, TimeUnit.SECONDS);
        assertEquals(response.aiMessage().text(), String.join("", partials));
        // Do not explicitly close here: the tests must observe ChatExecution's terminal cleanup.
        return response;
    }

    private static ToolExecutionRequest call(String id, String name, String expression) {
        return ToolExecutionRequest.builder()
                .id(id)
                .name(name)
                .arguments(JSON.valueToTree(Map.of("expression", expression)).toString())
                .build();
    }

    private static Set<String> fields(JsonNode node) {
        Set<String> names = new HashSet<>();
        node.fieldNames().forEachRemaining(names::add);
        return names;
    }

    private record Bound(
            String chat,
            ConnectorInvocation invocation,
            Map<ToolSpecification, ToolExecutor> tools,
            AtomicInteger guards) {}

    private static final class Fixture implements AutoCloseable {
        final Server server;
        // Localhost approval is confined to this test; no production address policy is changed.
        final ConnectorMcpClientFactory factory = new ConnectorMcpClientFactory(url -> true);
        final List<Bound> bound = new ArrayList<>();

        Fixture(String transport) throws IOException {
            server = new Server(transport.equals("sse"));
        }

        ChatSession session(Provider provider, String chat) {
            var memory = MessageWindowChatMemory.withMaxMessages(30);
            memory.add(SystemMessage.from(PRIVATE_TEMPLATE));
            memory.add(UserMessage.from(PRIVATE_INPUT));
            return ChatSession.builder()
                    .chatModel(provider.sync)
                    .streamingChatModel(provider)
                    .chatMemory(memory)
                    .variables(Map.of())
                    .toolBinder(ignored -> Map.of())
                    .connectorBinder(() -> bind(chat))
                    .build();
        }

        private ConnectorInvocation bind(String chat) {
            AtomicInteger guards = new AtomicInteger();
            var invocation = new ConnectorInvocation(
                    new ToolContext(
                            chat, "viewer-private", "character-private", "backend-private", Duration.ofSeconds(5)),
                    guards::incrementAndGet);
            try {
                invocation.add(
                        new ConnectorService.Resolved(
                                ID,
                                "creator",
                                REVISION,
                                new ConnectorConfiguration(
                                        "Local calculator",
                                        server.legacy ? "sse" : "http",
                                        server.url(),
                                        1000,
                                        Map.of()),
                                true),
                        factory);
                bound.add(new Bound(chat, invocation, invocation.tools(), guards));
                return invocation;
            } catch (RuntimeException | Error failure) {
                invocation.close();
                throw failure;
            }
        }

        void assertDisposed(int index) throws Exception {
            Bound binding = bound.get(index);
            assertTrue(binding.guards().get() > 0);
            var failure = assertThrows(IllegalStateException.class, binding.invocation()::check);
            // An expired lifetime must not masquerade as automatic invocation disposal.
            assertEquals("Chat connector execution unavailable", failure.getMessage());
            var tool = binding.tools().entrySet().iterator().next();
            int sent = server.posts.size();
            assertThrows(
                    IllegalStateException.class,
                    () -> tool.getValue().execute(call("stale", tool.getKey().name(), "2 + 3"), binding.chat()));
            assertEquals(sent, server.posts.size());
            if (server.legacy) {
                Peer peer = server.peers.get(index);
                peer.probeClose.countDown();
                assertTrue(peer.peerClosed.await(2, TimeUnit.SECONDS), "Client must close the legacy SSE stream");
            }
            server.assertHealthy();
        }

        @Override
        public void close() throws InterruptedException {
            try {
                bound.forEach(binding -> binding.invocation().close());
            } finally {
                server.close();
            }
        }
    }

    private static final class Provider implements StreamingChatModel {
        final List<ChatRequest> requests = new CopyOnWriteArrayList<>();
        final boolean hallucinate;
        // LC 1.22's sync/stream default return types require separate model implementations.
        final ChatModel sync = new ChatModel() {
            @Override
            public ChatResponse chat(ChatRequest request) {
                return answer(request);
            }
        };

        Provider(boolean hallucinate) {
            this.hallucinate = hallucinate;
        }

        private ChatResponse answer(ChatRequest request) {
            requests.add(request);
            assertEquals(1, request.toolSpecifications().size());
            var specification = request.toolSpecifications().getFirst();
            String name = specification.name();
            assertNotEquals(REMOTE, name);
            assertTrue(name.startsWith("mcp_" + ID + "_"));
            assertTrue(name.matches("[A-Za-z][A-Za-z0-9_]{0,63}"));
            assertEquals(
                    Set.of("expression"),
                    specification.parameters().properties().keySet());
            assertEquals(List.of("expression"), specification.parameters().required());
            assertInstanceOf(
                    JsonStringSchema.class,
                    specification.parameters().properties().get("expression"));

            int round = (requests.size() - 1) % 3;
            AiMessage message;
            if (round == 0) {
                message = AiMessage.from(call("call-" + requests.size(), hallucinate ? REMOTE : name, "2 + 3"));
            } else {
                var result = request.messages().stream()
                        .filter(ToolExecutionResultMessage.class::isInstance)
                        .map(ToolExecutionResultMessage.class::cast)
                        .toList()
                        .getLast();
                assertEquals(name, result.toolName());
                assertEquals("call-" + (requests.size() - 1), result.id());
                int expected = round == 1 ? 5 : 35;
                JsonNode envelope = ToolArguments.parse(result.text());
                assertEquals(Set.of("untrusted", "source", "result"), fields(envelope));
                assertTrue(envelope.path("untrusted").booleanValue());
                assertEquals("connector", envelope.path("source").asText());
                assertTrue(envelope.path("result").isTextual());
                JsonNode value = ToolArguments.parse(envelope.path("result").asText());
                assertEquals(JSON.valueToTree(Map.of("value", expected)), value);
                // The second call and final answer depend on the real server's returned text.
                message = round == 1
                        ? AiMessage.from(call(
                                "call-" + requests.size(),
                                name,
                                value.path("value").asInt() + " * 7"))
                        : AiMessage.from(
                                "Remote calculation: " + value.path("value").asInt());
            }
            return ChatResponse.builder().aiMessage(message).build();
        }

        @Override
        public void chat(ChatRequest request, StreamingChatResponseHandler handler) {
            try {
                ChatResponse response = answer(request);
                if (!response.aiMessage().hasToolExecutionRequests()) {
                    handler.onPartialResponse("Remote calculation: ");
                    handler.onPartialResponse("35");
                }
                handler.onCompleteResponse(response);
            } catch (RuntimeException failure) {
                handler.onError(failure);
            }
        }
    }

    private record Seen(Peer peer, String path, Headers headers, JsonNode body) {}

    private static final class Peer {
        final String id;
        final CountDownLatch probeClose = new CountDownLatch(1);
        final CountDownLatch peerClosed = new CountDownLatch(1);
        OutputStream stream;

        Peer(String id) {
            this.id = id;
        }

        synchronized void write(String frame) throws IOException {
            stream.write(frame.getBytes(StandardCharsets.UTF_8));
            stream.flush();
        }
    }

    /** Only initialize, initialized, list and call; no external server, TLS setup or subprocess. */
    private static final class Server implements AutoCloseable {
        final boolean legacy;
        final HttpServer http;
        final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        final List<Peer> peers = new CopyOnWriteArrayList<>();
        final List<Seen> posts = new CopyOnWriteArrayList<>();
        final AtomicInteger sequence = new AtomicInteger();
        final AtomicReference<Throwable> failure = new AtomicReference<>();
        final CountDownLatch stopped = new CountDownLatch(1);

        Server(boolean legacy) throws IOException {
            this.legacy = legacy;
            http = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
            http.setExecutor(executor);
            http.createContext("/", this::handle);
            http.start();
        }

        String url() {
            return "http://127.0.0.1:" + http.getAddress().getPort() + "/mcp";
        }

        private Peer newPeer() {
            Peer peer = new Peer("session-" + sequence.incrementAndGet());
            peers.add(peer);
            return peer;
        }

        private void handle(HttpExchange exchange) {
            try {
                if ("GET".equals(exchange.getRequestMethod())) {
                    legacyStream(exchange);
                } else {
                    assertEquals("POST", exchange.getRequestMethod());
                    post(exchange);
                }
            } catch (Exception | AssertionError error) {
                if (error instanceof InterruptedException)
                    Thread.currentThread().interrupt();
                if (stopped.getCount() != 0) failure.compareAndSet(null, error);
            } finally {
                exchange.close();
            }
        }

        private void legacyStream(HttpExchange exchange) throws IOException, InterruptedException {
            assertTrue(legacy);
            assertEquals("/mcp", exchange.getRequestURI().toString());
            Peer peer = newPeer();
            exchange.getResponseHeaders().set("Content-Type", "text/event-stream");
            exchange.sendResponseHeaders(200, 0);
            peer.stream = exchange.getResponseBody();
            peer.write("event: endpoint\r\ndata: /messages?session=" + peer.id + "\r\n\r\n");
            peer.probeClose.await();
            try {
                // Probe only after completion/cancellation, before fixture shutdown. Valid, bounded
                // comments cannot themselves trip the client's wire/event limits and fake a close.
                for (int i = 0; i < 32 && stopped.getCount() != 0; i++) {
                    peer.write(":" + " ".repeat(1024) + "\r\n\r\n");
                    if (stopped.await(20, TimeUnit.MILLISECONDS)) return;
                }
                stopped.await();
            } catch (IOException disconnected) {
                peer.peerClosed.countDown();
            }
        }

        private void post(HttpExchange exchange) throws IOException {
            JsonNode request = JSON.readTree(exchange.getRequestBody().readNBytes(128 * 1024));
            String method = request.path("method").asText();
            String token = legacy
                    ? exchange.getRequestURI().getRawQuery()
                    : exchange.getRequestHeaders().getFirst("Mcp-Session-Id");
            Peer peer = !legacy && method.equals("initialize")
                    ? newPeer()
                    : peers.stream()
                            .filter(candidate -> (legacy ? "session=" + candidate.id : candidate.id).equals(token))
                            .findFirst()
                            .orElseThrow();
            Headers headers = new Headers();
            headers.putAll(exchange.getRequestHeaders());
            posts.add(new Seen(peer, exchange.getRequestURI().toString(), headers, request));
            Object result =
                    switch (method) {
                        case "initialize" ->
                            Map.of(
                                    "protocolVersion",
                                    "2025-06-18",
                                    "capabilities",
                                    Map.of("tools", Map.of()),
                                    "serverInfo",
                                    Map.of("name", "local-calculator", "version", "1"));
                        case "tools/list" ->
                            Map.of(
                                    "tools",
                                    List.of(Map.of(
                                            "name",
                                            REMOTE,
                                            "description",
                                            "Calculate an expression",
                                            "inputSchema",
                                            Map.of(
                                                    "type",
                                                    "object",
                                                    "properties",
                                                    Map.of("expression", Map.of("type", "string")),
                                                    "required",
                                                    List.of("expression")))));
                        case "tools/call" -> {
                            assertEquals(
                                    REMOTE, request.path("params").path("name").asText());
                            String expression = request.path("params")
                                    .path("arguments")
                                    .path("expression")
                                    .asText();
                            int value =
                                    switch (expression) {
                                        case "2 + 3" -> 5;
                                        case "5 * 7" -> 35;
                                        default -> throw new AssertionError("Unexpected expression: " + expression);
                                    };
                            yield Map.of(
                                    "content",
                                    List.of(Map.of(
                                            "type", "text", "text", JSON.writeValueAsString(Map.of("value", value)))));
                        }
                        case "notifications/initialized" -> null;
                        default -> throw new AssertionError("Unexpected MCP method: " + method);
                    };
            String reply = result == null
                    ? null
                    : JSON.writeValueAsString(
                            Map.of("jsonrpc", "2.0", "id", request.path("id").asLong(), "result", result));
            if (legacy || reply == null) {
                exchange.sendResponseHeaders(202, -1);
                // Acknowledge POST before publishing its response on the long-lived GET stream.
                exchange.close();
                if (reply != null) peer.write("event: message\r\ndata: " + reply + "\r\n\r\n");
            } else {
                if (method.equals("initialize")) exchange.getResponseHeaders().set("Mcp-Session-Id", peer.id);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                byte[] bytes = reply.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(200, bytes.length);
                try (var output = exchange.getResponseBody()) {
                    output.write(bytes);
                }
            }
        }

        void assertProtocol(Peer peer, int calls) {
            assertHealthy();
            List<Seen> requests =
                    posts.stream().filter(post -> post.peer() == peer).toList();
            List<String> methods = new ArrayList<>(List.of("initialize", "notifications/initialized", "tools/list"));
            for (int i = 0; i < calls; i++) methods.add("tools/call");
            assertEquals(
                    methods,
                    requests.stream()
                            .map(post -> post.body().path("method").asText())
                            .toList());
            Set<JsonNode> ids = new HashSet<>();
            for (int i = 0; i < requests.size(); i++) {
                Seen post = requests.get(i);
                JsonNode body = post.body();
                assertEquals("2.0", body.path("jsonrpc").asText());
                assertTrue(Set.of("jsonrpc", "method", "params", "id").containsAll(fields(body)));
                if (i != 1) {
                    assertTrue(body.path("id").isIntegralNumber());
                    assertTrue(ids.add(body.path("id")), "RPC request IDs must be unique within one session");
                } else {
                    assertFalse(body.hasNonNull("id"));
                    assertTrue(fields(body.path("params")).isEmpty());
                }
                assertEquals(legacy ? "/messages?session=" + peer.id : "/mcp", post.path());
                assertEquals(legacy || i == 0 ? null : peer.id, post.headers().getFirst("Mcp-Session-Id"));
                assertEquals(
                        legacy ? null : i == 0 ? "2025-11-25" : "2025-06-18",
                        post.headers().getFirst("MCP-Protocol-Version"));
                assertEquals("application/json", post.headers().getFirst("Content-Type"));
                assertEquals(
                        "application/json, text/event-stream", post.headers().getFirst("Accept"));
                assertNull(post.headers().getFirst("Authorization"));
                assertNull(post.headers().getFirst("Proxy-Authorization"));
                assertNull(post.headers().getFirst("Cookie"));
                String wire = body + post.headers().toString() + post.path();
                for (String privateValue : List.of(
                        "creator",
                        "viewer-private",
                        "character-private",
                        "backend-private",
                        "chat-one",
                        "chat-two",
                        PRIVATE_INPUT,
                        PRIVATE_TEMPLATE,
                        ID,
                        REVISION)) {
                    assertFalse(wire.contains(privateValue), "Private chat context must not reach MCP");
                }
            }
            JsonNode initialization = requests.getFirst().body().path("params");
            assertEquals(Set.of("protocolVersion", "capabilities", "clientInfo"), fields(initialization));
            assertEquals(
                    legacy ? "2024-11-05" : "2025-11-25",
                    initialization.path("protocolVersion").asText());
            assertTrue(initialization.path("capabilities").isObject());
            assertEquals(
                    JSON.valueToTree(Map.of("name", "freechat", "version", "1")), initialization.path("clientInfo"));
            JsonNode listParams = requests.get(2).body().path("params");
            assertTrue(Set.of("cursor").containsAll(fields(listParams)));
            assertFalse(listParams.hasNonNull("cursor"));
            for (int i = 0; i < calls; i++) {
                JsonNode call = requests.get(3 + i).body();
                assertEquals(Set.of("jsonrpc", "id", "method", "params"), fields(call));
                assertEquals(
                        JSON.valueToTree(
                                Map.of("name", REMOTE, "arguments", Map.of("expression", i == 0 ? "2 + 3" : "5 * 7"))),
                        call.path("params"));
            }
        }

        void assertHealthy() {
            assertNull(failure.get(), () -> "Mock MCP server failed: " + failure.get());
        }

        @Override
        public void close() throws InterruptedException {
            stopped.countDown();
            peers.forEach(peer -> peer.probeClose.countDown());
            http.stop(0);
            executor.shutdownNow();
            assertTrue(executor.awaitTermination(2, TimeUnit.SECONDS), "Mock server workers must terminate");
            assertHealthy();
        }
    }
}
