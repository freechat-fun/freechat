package fun.freechat.service.connectors;

import static org.junit.jupiter.api.Assertions.*;

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
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import fun.freechat.service.chat.ChatExecution;
import fun.freechat.service.chat.ChatSession;
import fun.freechat.service.tools.ToolArguments;
import fun.freechat.service.tools.ToolContext;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@Timeout(10)
class ConnectorRuntimeBinderTest {
    private static final String ID = "1".repeat(32);
    private static final String OTHER = "2".repeat(32);
    private static final String REVISION = "a".repeat(32);
    private static final String SECRET = "connector-private-token";
    private static final String REMOTE = "remote.lookup";

    @Test
    void creatorCredentialsAreSelectedInsteadOfChattersAndSessionsAreNeverShared() {
        Fixture fixture = new Fixture();
        fixture.store.put("viewer", OTHER, true);
        try (var first = fixture.bind("chat-one");
                var second = fixture.bind("chat-two")) {
            assertEquals(2, fixture.clients.opened.size());
            assertNotSame(fixture.clients.opened.get(0), fixture.clients.opened.get(1));
            assertTrue(fixture.clients.owners.stream().allMatch("creator"::equals));
            var tool = first.tools().entrySet().iterator().next();
            tool.getValue().execute(call(tool.getKey().name(), "{}"), "chat-one");
            tool.getValue().execute(call(tool.getKey().name(), "{}"), "chat-one");
            assertEquals(2, fixture.clients.opened.getFirst().calls);
            assertEquals(0, fixture.clients.opened.getLast().calls);
        }
        assertEquals(2, fixture.clients.closed.get());
    }

    @Test
    void identicalRemoteNamesAreNamespacedAndCannotReplaceBuiltins() {
        Fixture fixture = new Fixture();
        fixture.store.put("creator", OTHER, true);
        fixture.policy = new ConnectorPolicySource.Policy("creator", Set.of(ID, OTHER), true);
        try (var invocation = fixture.bind("chat")) {
            assertEquals(2, invocation.tools().size());
            var names = invocation.tools().keySet().stream()
                    .map(ToolSpecification::name)
                    .toList();
            assertEquals(2, Set.copyOf(names).size());
            names.forEach(name -> assertTrue(name.matches("[A-Za-z][A-Za-z0-9_]{0,63}")));
            assertNotEquals(REMOTE, names.getFirst());
            invocation
                    .tools()
                    .keySet()
                    .forEach(tool -> assertTrue(tool.description().contains("\"Private connector\"")));
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"empty", "disabled", "inactive", "foreign", "assistant"})
    void deniedOrAbsentSelectionsNeverOpenNetworkClients(String mode) {
        Fixture fixture = new Fixture();
        switch (mode) {
            case "empty" -> fixture.policy = new ConnectorPolicySource.Policy("creator", Set.of(), true);
            case "disabled" -> fixture.policy = new ConnectorPolicySource.Policy("creator", Set.of(ID), false);
            case "inactive" -> fixture.store.put("creator", ID, false);
            case "foreign" -> fixture.policy = new ConnectorPolicySource.Policy("viewer", Set.of(ID), true);
            case "assistant" -> {}
            default -> fail();
        }
        assertNull(fixture.bind(mode.equals("assistant") ? "chat-assist" : "chat"));
        assertTrue(fixture.clients.opened.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"owner", "selection", "disabled", "revision", "inactive", "deleted"})
    void alreadyBoundExecutorsRecheckPolicyAndConfiguration(String change) {
        Fixture fixture = new Fixture();
        try (var invocation = fixture.bind("chat")) {
            var tool = invocation.tools().entrySet().iterator().next();
            switch (change) {
                case "owner" -> fixture.policy = new ConnectorPolicySource.Policy("viewer", Set.of(ID), true);
                case "selection" -> fixture.policy = new ConnectorPolicySource.Policy("creator", Set.of(), true);
                case "disabled" -> fixture.policy = new ConnectorPolicySource.Policy("creator", Set.of(ID), false);
                case "revision" ->
                    fixture.store.records.put(
                            ID, new ConnectorService.Resolved(ID, "creator", "b".repeat(32), configuration(), true));
                case "inactive" -> fixture.store.put("creator", ID, false);
                case "deleted" -> fixture.store.records.remove(ID);
                default -> fail();
            }
            assertThrows(
                    IllegalStateException.class,
                    () -> tool.getValue().execute(call(tool.getKey().name(), "{}"), "chat"));
            assertEquals(0, fixture.clients.opened.getFirst().calls);
        }
        assertEquals(1, fixture.clients.closed.get());
    }

    @Test
    void revocationDuringRemoteExecutionRejectsTheReturnedData() {
        Fixture fixture = new Fixture();
        try (var invocation = fixture.bind("chat")) {
            var tool = invocation.tools().entrySet().iterator().next();
            fixture.clients.opened.getFirst().afterCall = () -> fixture.store.records.remove(ID);
            assertThrows(
                    IllegalStateException.class,
                    () -> tool.getValue().execute(call(tool.getKey().name(), "{}"), "chat"));
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"wrong-chat", "wrong-name", "array", "duplicate", "oversized"})
    void scopeAndMalformedArgumentsFailBeforeDispatch(String mode) {
        Fixture fixture = new Fixture();
        try (var invocation = fixture.bind("chat")) {
            var tool = invocation.tools().entrySet().iterator().next();
            String name =
                    mode.equals("wrong-name") ? "calculate" : tool.getKey().name();
            String chat = mode.equals("wrong-chat") ? "another-chat" : "chat";
            String arguments =
                    switch (mode) {
                        case "array" -> "[]";
                        case "duplicate" -> "{\"x\":1,\"x\":2}";
                        case "oversized" -> "{\"x\":\"" + "a".repeat(65_536) + "\"}";
                        default -> "{}";
                    };
            var failure = assertThrows(
                    IllegalStateException.class, () -> tool.getValue().execute(call(name, arguments), chat));
            assertFalse(failure.toString().contains(SECRET));
            assertEquals(0, fixture.clients.opened.getFirst().calls);
        }
    }

    @Test
    void outputIsUntrustedAndExactHeaderCredentialsAreRedacted() {
        Fixture fixture = new Fixture();
        try (var invocation = fixture.bind("chat")) {
            fixture.clients.opened.getFirst().result = "Bearer " + SECRET + " " + SECRET;
            var tool = invocation.tools().entrySet().iterator().next();
            String result = tool.getValue().execute(call(tool.getKey().name(), "{}"), "chat");
            assertTrue(ToolArguments.parse(result).path("untrusted").booleanValue());
            assertFalse(result.contains(SECRET));
        }
    }

    @Test
    void ordinaryHeadersDoNotRedactRemoteContent() {
        Fixture fixture = new Fixture();
        var config = new ConnectorConfiguration(
                "Connector",
                "http",
                "https://example.com/mcp",
                1000,
                Map.of("Authorization", "Bearer " + SECRET, "Accept-Language", "en"));
        fixture.store.records.put(ID, new ConnectorService.Resolved(ID, "creator", REVISION, config, true));
        try (var invocation = fixture.bind("chat")) {
            fixture.clients.opened.getFirst().result = "content event";
            var tool = invocation.tools().entrySet().iterator().next();
            String result = tool.getValue().execute(call(tool.getKey().name(), "{}"), "chat");
            assertEquals(
                    "content event", ToolArguments.parse(result).path("result").textValue());
        }
    }

    @Test
    void failedDiscoveryClosesEveryClientAlreadyOpened() {
        Fixture fixture = new Fixture();
        fixture.store.put("creator", OTHER, true);
        fixture.policy = new ConnectorPolicySource.Policy("creator", Set.of(ID, OTHER), true);
        fixture.clients.failDiscoveryNumber = 2;
        assertThrows(IllegalStateException.class, () -> fixture.bind("chat"));
        assertEquals(2, fixture.clients.closed.get());
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void realAiServicesExecuteRemoteToolsAndCloseInvocation(boolean streaming) throws Exception {
        Fixture fixture = new Fixture();
        Provider provider = new Provider();
        ChatSession session = session(fixture, provider, "chat");
        ChatResponse response = run(session, streaming);
        assertEquals("Finished", response.aiMessage().text());
        assertEquals(1, fixture.clients.opened.size());
        assertEquals(1, fixture.clients.closed.get());
        assertEquals(1, fixture.clients.opened.getFirst().calls);
        assertEquals(2, provider.requests.size());
        assertEquals(
                "mcp_" + ID + "_",
                provider.requests
                        .getFirst()
                        .toolSpecifications()
                        .getFirst()
                        .name()
                        .substring(0, 37));
        String result = provider.requests.getLast().messages().stream()
                .filter(ToolExecutionResultMessage.class::isInstance)
                .map(ToolExecutionResultMessage.class::cast)
                .findFirst()
                .orElseThrow()
                .text();
        assertTrue(ToolArguments.parse(result).path("untrusted").booleanValue());
        assertFalse(result.contains(SECRET));
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void ltmStyleInvocationIncludesConnectorSchemasInBudgetAndBindsOnlyOnce(boolean streaming) throws Exception {
        Fixture fixture = new Fixture();
        Provider provider = new Provider();
        var memory = MessageWindowChatMemory.withMaxMessages(20);
        memory.add(UserMessage.from("Prepared input"));
        AtomicInteger guards = new AtomicInteger();
        ChatSession invocation = session(fixture, provider, "chat")
                .forInvocation(memory, provider, List.of(), 3, guards::incrementAndGet);
        assertEquals(1, invocation.getToolSpecifications().size());
        assertEquals(1, fixture.clients.opened.size());
        run(invocation, streaming);
        assertEquals(1, fixture.clients.opened.size());
        assertEquals(1, fixture.clients.closed.get());
        assertEquals(2, guards.get());
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void providerFailureClosesConnections(boolean streaming) {
        Fixture fixture = new Fixture();
        Provider provider = new Provider();
        provider.beforeReply = ignored -> {
            throw new IllegalStateException(SECRET);
        };
        assertThrows(Exception.class, () -> run(session(fixture, provider, "chat"), streaming));
        assertEquals(1, fixture.clients.closed.get());
    }

    @Test
    void cancellationBeforeStreamStartClosesConnectionsAndPreventsRemoteCalls() throws Exception {
        Fixture fixture = new Fixture();
        Provider provider = new Provider();
        var stream = new ChatExecution(session(fixture, provider, "chat"), "chat").stream();
        ((AutoCloseable) stream).close();
        assertThrows(IllegalStateException.class, stream::start);
        assertEquals(1, fixture.clients.closed.get());
        assertTrue(provider.requests.isEmpty());
    }

    @Test
    void disposedInvocationCannotExecuteOldTools() {
        Fixture fixture = new Fixture();
        var invocation = fixture.bind("chat");
        var tool = invocation.tools().entrySet().iterator().next();
        invocation.close();
        invocation.close();
        assertThrows(
                IllegalStateException.class,
                () -> tool.getValue().execute(call(tool.getKey().name(), "{}"), "chat"));
        assertEquals(1, fixture.clients.closed.get());
    }

    private static ChatSession session(Fixture fixture, Provider provider, String chat) {
        var memory = MessageWindowChatMemory.withMaxMessages(20);
        memory.add(SystemMessage.from("Custom character template"));
        memory.add(UserMessage.from("Prepared input"));
        return ChatSession.builder()
                .chatModel(provider.sync)
                .streamingChatModel(provider)
                .chatMemory(memory)
                .variables(Map.of())
                .toolBinder(ignored -> Map.of())
                .connectorBinder(() -> fixture.bind(chat))
                .build();
    }

    private static ChatResponse run(ChatSession session, boolean streaming) throws Exception {
        ChatExecution execution = new ChatExecution(session, "chat");
        if (!streaming) return execution.send(null, () -> {}).getLeft();
        CompletableFuture<ChatResponse> result = new CompletableFuture<>();
        execution.stream()
                .onCompleteResponse(result::complete)
                .onError(result::completeExceptionally)
                .start();
        return result.get(2, TimeUnit.SECONDS);
    }

    private static ToolExecutionRequest call(String name, String args) {
        return ToolExecutionRequest.builder()
                .id("call")
                .name(name)
                .arguments(args)
                .build();
    }

    private static ConnectorConfiguration configuration() {
        return new ConnectorConfiguration(
                "Private connector",
                "http",
                "https://example.com/mcp",
                1000,
                Map.of("Authorization", "Bearer " + SECRET));
    }

    private static class Fixture {
        final Store store = new Store();
        final Clients clients = new Clients();
        ConnectorPolicySource.Policy policy = new ConnectorPolicySource.Policy("creator", Set.of(ID), true);
        final ConnectorRuntimeBinder binder = new ConnectorRuntimeBinder(ignored -> policy, store, clients);

        Fixture() {
            store.put("creator", ID, true);
        }

        ConnectorInvocation bind(String chat) {
            return binder.bind(new ToolContext(chat, "viewer", "character", "backend", Duration.ofSeconds(5)));
        }
    }

    private static class Clients implements ConnectorClientFactory {
        final List<Connection> opened = new ArrayList<>();
        final List<String> owners = new ArrayList<>();
        final AtomicInteger closed = new AtomicInteger();
        int failDiscoveryNumber;

        @Override
        public Connection open(
                String owner, ConnectorConfiguration configuration, Duration lifetime, Runnable authorize) {
            authorize.run();
            assertEquals("Bearer " + SECRET, configuration.headers().get("Authorization"));
            Connection connection = new Connection(opened.size() + 1, authorize);
            opened.add(connection);
            owners.add(owner);
            return connection;
        }

        private class Connection implements ConnectorClientFactory.Connection {
            final int number;
            final Runnable authorize;
            boolean done;
            int calls;
            String result = "Remote result";
            Runnable afterCall = () -> {};

            Connection(int number, Runnable authorize) {
                this.number = number;
                this.authorize = authorize;
            }

            @Override
            public List<ToolSpecification> listTools() {
                authorize.run();
                if (number == failDiscoveryNumber) throw new IllegalStateException(SECRET);
                return List.of(ToolSpecification.builder()
                        .name(REMOTE)
                        .description("Find a record")
                        .parameters(JsonObjectSchema.builder().build())
                        .build());
            }

            @Override
            public String execute(String name, String arguments) {
                authorize.run();
                assertFalse(done);
                assertEquals(REMOTE, name);
                calls++;
                afterCall.run();
                return result;
            }

            @Override
            public void close() {
                if (!done) {
                    done = true;
                    closed.incrementAndGet();
                }
            }
        }
    }

    private static class Store implements ConnectorService {
        final Map<String, Resolved> records = new LinkedHashMap<>();

        void put(String owner, String id, boolean active) {
            records.put(id, new Resolved(id, owner, REVISION, configuration(), active));
        }

        @Override
        public List<Summary> list(String owner) {
            return records.values().stream()
                    .filter(record -> record.ownerId().equals(owner))
                    .map(record -> new Summary(
                            record.connectorId(),
                            record.revision(),
                            record.configuration().name(),
                            record.configuration().transport(),
                            record.configuration().url(),
                            record.configuration().timeout(),
                            record.active(),
                            record.configuration().headers().keySet(),
                            Instant.EPOCH))
                    .toList();
        }

        @Override
        public Summary get(String owner, String id) {
            return list(owner).stream()
                    .filter(record -> record.connectorId().equals(id))
                    .findFirst()
                    .orElseThrow();
        }

        @Override
        public Resolved resolve(String owner, String id) {
            var result = records.get(id);
            if (result == null || !result.ownerId().equals(owner)) throw new IllegalStateException("Not found");
            return result;
        }

        @Override
        public Summary create(String owner, ConnectorConfiguration configuration, boolean active) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Summary update(
                String owner,
                String id,
                String revision,
                ConnectorConfiguration configuration,
                Set<String> retained,
                boolean active) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void delete(String owner, String id, String revision) {
            throw new UnsupportedOperationException();
        }
    }

    private static class Provider implements StreamingChatModel {
        final List<ChatRequest> requests = new ArrayList<>();
        final ChatModel sync = new ChatModel() {
            @Override
            public ChatResponse chat(ChatRequest request) {
                return answer(request);
            }
        };
        Consumer<ChatRequest> beforeReply = ignored -> {};

        private ChatResponse answer(ChatRequest request) {
            requests.add(request);
            beforeReply.accept(request);
            AiMessage message = requests.size() == 1
                    ? AiMessage.from(
                            call(request.toolSpecifications().getFirst().name(), "{}"))
                    : AiMessage.from("Finished");
            return ChatResponse.builder().aiMessage(message).build();
        }

        @Override
        public void chat(ChatRequest request, StreamingChatResponseHandler handler) {
            try {
                handler.onCompleteResponse(answer(request));
            } catch (RuntimeException failure) {
                handler.onError(failure);
            }
        }
    }
}
