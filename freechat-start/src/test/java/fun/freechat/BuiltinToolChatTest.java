package fun.freechat;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.JsonNode;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.data.message.*;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.ModelProvider;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.request.json.JsonStringSchema;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.output.FinishReason;
import dev.langchain4j.model.output.TokenUsage;
import dev.langchain4j.service.tool.ToolService;
import fun.freechat.service.chat.ChatExecution;
import fun.freechat.service.chat.ChatSession;
import fun.freechat.service.tools.*;
import fun.freechat.service.tools.builtin.RoleplayTools;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/** Offline contracts through real AiServices; only provider and policy/storage boundaries are fakes. */
@Timeout(10)
class BuiltinToolChatTest {
    static final String CHAT = "chat";
    static final String SECRET = "private-credential-sentinel";
    static final Set<String> ROLEPLAY = Set.of("calculate", "get_current_time", "roll_dice");
    static final Set<String> DEFAULTS = CharacterToolConfiguration.DEFAULTS;

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void defaultsAdvertiseRealSchemasAndExecuteMultipleToolRounds(boolean streaming) {
        Fixture f = new Fixture();
        ChatSession session = f.session(CHAT, "character");
        // Arbitrary candidates cannot replace a registered runtime implementation.
        session.tools(Map.of(
                f.registry.find("calculate").orElseThrow().specification(),
                (request, id) -> fail("Untrusted calculate candidate executed")));
        ChatResponse answer = f.run(
                session,
                CHAT,
                streaming,
                tools(call("calculate", "{\"expression\":\"6 * 7\"}"), call("get_current_time", "{}")),
                tools(call("roll_dice", "{\"count\":2,\"sides\":6}")),
                reply());

        assertEquals("The answer is 42.", answer.aiMessage().text());
        assertEquals(new TokenUsage(6, 3), answer.tokenUsage());
        assertEquals(List.of("calculate", "get_current_time", "roll_dice"), f.runtime.executed);
        assertEquals(1, f.bindings.get(), "Bind once, not once per model round");
        assertEquals(3, f.provider.requests.size());
        f.provider.requests.forEach(request -> {
            assertEquals(ROLEPLAY, names(request.toolSpecifications()));
            request.toolSpecifications()
                    .forEach(spec -> assertEquals(
                            f.registry.find(spec.name()).orElseThrow().specification(), spec));
        });
        var schema = f.registry.find("calculate").orElseThrow().specification().parameters();
        assertEquals(List.of("expression"), schema.required());
        assertInstanceOf(JsonStringSchema.class, schema.properties().get("expression"));
        assertEquals(Boolean.FALSE, schema.additionalProperties());
        List<ToolExecutionResultMessage> results =
                results(f.provider.requests.getLast().messages());
        assertEquals(
                List.of("calculate", "get_current_time", "roll_dice"),
                results.stream().map(ToolExecutionResultMessage::toolName).toList());
        assertEquals(
                "42", ToolArguments.parse(results.get(0).text()).path("result").asText());
        assertEquals(
                "2026-01-02T03:04:05Z",
                ToolArguments.parse(results.get(1).text()).path("datetime").asText());
        Random expected = new Random(17);
        assertEquals(
                expected.nextInt(6) + expected.nextInt(6) + 2,
                ToolArguments.parse(results.get(2).text()).path("total").asInt());
        assertEquals(answer.aiMessage(), session.getChatMemory(CHAT).messages().getLast());
    }

    @ParameterizedTest
    @CsvSource({"false,false", "true,false", "false,true", "true,true"})
    void explicitEmptySelectionNeverAdvertisesOrExecutesHallucinatedTools(boolean streaming, boolean hallucinated) {
        Fixture f = new Fixture();
        f.select(
                CHAT,
                "character",
                CharacterToolConfiguration.parse("{\"tools\":{\"enabled\":[]}}").enabled(),
                true,
                true);
        ChatSession session = f.session(CHAT, "character");
        if (hallucinated) {
            assertThrows(
                    RuntimeException.class,
                    () -> f.run(session, CHAT, streaming, tools(call("calculate", "{\"expression\":\"6*7\"}"))));
        } else {
            assertEquals(
                    reply().aiMessage(),
                    f.run(session, CHAT, streaming, reply()).aiMessage());
        }
        assertTrue(names(f.provider.requests.getFirst().toolSpecifications()).isEmpty());
        assertTrue(f.runtime.executed.isEmpty());
        assertEquals(0, f.album.calls);
        assertEquals(1, f.provider.requests.size());
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void exactCharacterAllowlistIsIndependentAndCachedSessionRebinds(boolean streaming) {
        Fixture f = new Fixture();
        f.select(CHAT, "mage", Set.of("calculate"), false, false);
        f.select("other", "bard", Set.of("get_current_time"), false, false);
        ChatSession cached = f.session(CHAT, "mage");
        ChatSession other = f.session("other", "bard");
        f.run(cached, CHAT, streaming, reply());
        f.run(other, "other", streaming, reply());
        f.select(CHAT, "mage", Set.of("roll_dice"), false, false);
        f.run(cached, CHAT, streaming, tools(call("roll_dice", "{}")), reply());
        f.run(other, "other", streaming, reply());
        assertEquals(
                List.of(
                        Set.of("calculate"),
                        Set.of("get_current_time"),
                        Set.of("roll_dice"),
                        Set.of("roll_dice"),
                        Set.of("get_current_time")),
                f.provider.requests.stream()
                        .map(request -> names(request.toolSpecifications()))
                        .toList());
        assertEquals(4, f.bindings.get());
        assertEquals(List.of("roll_dice"), f.runtime.executed);
    }

    @ParameterizedTest
    @CsvSource({
        "false,character",
        "true,character",
        "false,registry",
        "true,registry",
        "false,runtime",
        "true,runtime",
        "false,global",
        "true,global",
        "false,policy",
        "true,policy"
    })
    void revocationAfterProviderResponseInvalidatesAlreadyBoundExecutor(boolean streaming, String revoked) {
        Fixture f = new Fixture();
        ChatSession session = f.session(CHAT, "character");
        f.provider.beforeDelivery = () -> {
            assertEquals(ROLEPLAY, names(f.provider.requests.getFirst().toolSpecifications()));
            assertTrue(f.runtime.executed.isEmpty());
            switch (revoked) {
                case "character" -> f.select(CHAT, "character", Set.of(), false, false);
                case "registry" -> assertTrue(f.registry.unregister("calculate"));
                case "runtime" -> assertTrue(f.broker.unregisterRuntime(f.runtime.id()));
                case "global" -> f.broker.setAllowed(Set.of());
                case "policy" -> f.policies.remove(CHAT);
                default -> fail(revoked);
            }
        };
        sanitized(() -> f.run(session, CHAT, streaming, tools(call("calculate", "{\"expression\":\"6*7\"}"))));
        assertTrue(f.runtime.executed.isEmpty());
        assertEquals(1, f.provider.requests.size());
        assertTrue(results(session.getChatMemory(CHAT).messages()).isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"chat", "user", "character", "backend", "name"})
    void directlyBoundExecutorRejectsIdentityMismatch(String mismatch) {
        Fixture f = new Fixture();
        var executor = f.session(CHAT, "character").bindToolsForInvocation().entrySet().stream()
                .filter(entry -> entry.getKey().name().equals("calculate"))
                .findFirst()
                .orElseThrow()
                .getValue();
        f.policies.put(
                CHAT,
                new ToolPolicySource.Policy(
                        mismatch.equals("user") ? "intruder" : "owner",
                        mismatch.equals("character") ? "other" : "character",
                        mismatch.equals("backend") ? "other" : "backend-character",
                        DEFAULTS,
                        false,
                        false));
        sanitized(() -> executor.execute(
                call(mismatch.equals("name") ? "roll_dice" : "calculate", "{\"expression\":\"6*7\"}"),
                mismatch.equals("chat") ? "other" : CHAT));
        assertTrue(f.runtime.executed.isEmpty());
    }

    @ParameterizedTest
    @CsvSource({"false,false", "true,false", "false,true", "true,true"})
    void invocationMergesRecallThenFiltersAndPreservesLifetimeGuard(boolean streaming, boolean disabled) {
        Fixture f = new Fixture();
        f.select(CHAT, "character", disabled ? Set.of() : DEFAULTS, true, true);
        Recall recall = new Recall();
        AtomicBoolean live = new AtomicBoolean(true);
        AtomicInteger checks = new AtomicInteger();
        ChatMemory memory = memory();
        ChatSession invocation = f.session(CHAT, "character")
                .forInvocation(memory, f.provider, List.of(recall), 3, () -> {
                    checks.incrementAndGet();
                    if (!live.get()) throw new IllegalStateException("Invocation closed");
                });
        // This is the same list passed to MemoryInvocation.tools for its request budget.
        List<ToolSpecification> budgeted = List.copyOf(invocation.getToolSpecifications());
        assertEquals(disabled ? Set.of() : DEFAULTS, names(budgeted));
        if (disabled) {
            f.run(invocation, CHAT, streaming, reply());
            assertEquals(0, checks.get());
            assertEquals(0, recall.calls);
            assertEquals(0, f.album.calls);
        } else {
            f.run(
                    invocation,
                    CHAT,
                    streaming,
                    tools(
                            call("search_memory", "{\"query\":\"past\"}"),
                            call("read_memory", "{\"id\":\"one\"}"),
                            call("find_image", "{\"query\":\"scene\"}")),
                    reply());
            assertEquals(2, recall.calls);
            assertEquals(1, f.album.calls);
            assertEquals(6, checks.get(), "Lifetime checked before AND after every dispatch");
            live.set(false);
            assertThrows(
                    IllegalStateException.class,
                    () -> invocation
                            .getToolExecutors()
                            .get("search_memory")
                            .execute(call("search_memory", "{\"query\":\"past\"}"), CHAT));
            assertEquals(2, recall.calls, "Closed invocation must not enter recall");
            live.set(true);
            recall.afterCall = () -> live.set(false);
            sanitized(() -> invocation
                    .getToolExecutors()
                    .get("search_memory")
                    .execute(call("search_memory", "{\"query\":\"past\"}"), CHAT));
            assertEquals(3, recall.calls, "Post-dispatch lifetime invalidation must reject the result");
        }
        assertEquals(1, f.bindings.get(), "An LTM invocation must not be bound twice");
        f.provider.requests.forEach(
                request -> assertEquals(new HashSet<>(budgeted), new HashSet<>(request.toolSpecifications())));
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void temporaryAssistantRemainsToolFreeEvenWithAllGrants(boolean streaming) {
        Fixture f = new Fixture();
        f.select("chat-assist", "character", DEFAULTS, true, true);
        ChatSession temporary = f.session("chat-assist", "character");
        f.run(temporary, "chat-assist", streaming, reply());
        assertTrue(names(f.provider.requests.getFirst().toolSpecifications()).isEmpty());
        assertTrue(f.runtime.executed.isEmpty());
        assertEquals(0, f.album.calls);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void maliciousJsonNeverReachesRuntimeAndFailuresDoNotExposeSecrets(boolean streaming) {
        for (String json : List.of(
                "{",
                "[]",
                "{\"expression\":1}",
                "{}",
                "{\"expression\":\"1\",\"expression\":\"" + SECRET + "\"}",
                "{\"expression\":\"1\",\"command\":\"" + SECRET + "\"}",
                "{\"expression\":\"1\"} {\"secret\":\"" + SECRET + "\"}")) {
            Fixture f = new Fixture();
            ChatSession session = f.session(CHAT, "character");
            sanitized(() -> f.run(session, CHAT, streaming, tools(call("calculate", json))));
            assertTrue(f.runtime.executed.isEmpty());
            assertTrue(results(session.getChatMemory(CHAT).messages()).isEmpty());
            assertEquals(1, f.provider.requests.size());
        }
        for (boolean runtimeFailure : List.of(false, true)) {
            Fixture f = new Fixture();
            ChatSession session = f.session(CHAT, "character");
            if (runtimeFailure) f.runtime.failure = new IllegalStateException(SECRET);
            String expression = runtimeFailure ? "1" : "Runtime.getRuntime().exec(" + SECRET + ")";
            sanitized(() -> f.run(
                    session, CHAT, streaming, tools(call("calculate", "{\"expression\":\"" + expression + "\"}"))));
            assertEquals(List.of("calculate"), f.runtime.executed);
            assertTrue(results(session.getChatMemory(CHAT).messages()).isEmpty());
            assertEquals(1, f.provider.requests.size());
        }
    }

    static void sanitized(org.junit.jupiter.api.function.Executable action) {
        Throwable error = assertThrows(RuntimeException.class, action);
        while (true) {
            assertEquals(0, error.getSuppressed().length);
            assertFalse(error.toString().contains(SECRET));
            if (error.getCause() == null) break;
            error = error.getCause();
        }
        assertInstanceOf(IllegalStateException.class, error);
        assertEquals("Chat tool execution failed", error.getMessage());
    }

    static Set<String> names(Collection<ToolSpecification> specs) {
        return specs == null
                ? Set.of()
                : new HashSet<>(specs.stream().map(ToolSpecification::name).toList());
    }

    static ToolExecutionRequest call(String name, String arguments) {
        return ToolExecutionRequest.builder()
                .id(name)
                .name(name)
                .arguments(arguments)
                .build();
    }

    static ChatResponse tools(ToolExecutionRequest... calls) {
        return response(AiMessage.from(calls));
    }

    static ChatResponse reply() {
        return response(AiMessage.from("The answer is 42."));
    }

    static ChatResponse response(AiMessage message) {
        return ChatResponse.builder()
                .aiMessage(message)
                .tokenUsage(new TokenUsage(2, 1))
                .finishReason(message.hasToolExecutionRequests() ? FinishReason.TOOL_EXECUTION : FinishReason.STOP)
                .build();
    }

    static List<ToolExecutionResultMessage> results(List<ChatMessage> messages) {
        return messages.stream()
                .filter(ToolExecutionResultMessage.class::isInstance)
                .map(ToolExecutionResultMessage.class::cast)
                .toList();
    }

    static ChatMemory memory() {
        ChatMemory memory = MessageWindowChatMemory.withMaxMessages(100);
        memory.add(UserMessage.from("Use only the tools available for this character."));
        return memory;
    }

    static final class Fixture {
        final Provider provider = new Provider();
        final Map<String, ToolPolicySource.Policy> policies = new HashMap<>();
        final List<BuiltinTool> builtins = RoleplayTools.create(
                Clock.fixed(Instant.parse("2026-01-02T03:04:05Z"), ZoneOffset.UTC), new Random(17));
        final ToolRegistry registry =
                new ToolRegistry(builtins.stream().map(BuiltinTool::definition).toList());
        final RecordingRuntime runtime = new RecordingRuntime(new InProcessToolRuntime(builtins));
        final ToolRuntimeBroker broker = new ToolRuntimeBroker(registry, policies::get, List.of(runtime), DEFAULTS);
        final AtomicInteger bindings = new AtomicInteger();
        final Album album = new Album();

        Fixture() {
            for (Object object : List.of(new Recall(), album)) {
                ToolService.findTools(object)
                        .forEach(tool -> registry.register(new ToolDefinition(
                                tool.toolSpecification(),
                                Set.of("domain"),
                                ToolDefinition.Boundary.IN_PROCESS,
                                false)));
            }
            select(CHAT, "character", CharacterToolConfiguration.parse(null).enabled(), false, false);
        }

        void select(String chat, String character, Set<String> enabled, boolean memory, boolean album) {
            policies.put(
                    chat,
                    new ToolPolicySource.Policy("owner", character, "backend-" + character, enabled, memory, album));
        }

        ChatSession session(String chat, String character) {
            return ChatSession.builder()
                    .chatModel(provider.sync)
                    .streamingChatModel(provider)
                    .chatMemory(memory())
                    .variables(new HashMap<>())
                    .objectsWithTools(List.of(album))
                    .toolBinder(candidates -> {
                        bindings.incrementAndGet();
                        return broker.binder(chat, "owner", character, "backend-" + character)
                                .bind(candidates);
                    })
                    .build();
        }

        ChatResponse run(ChatSession session, String chat, boolean streaming, ChatResponse... responses) {
            provider.script.addAll(List.of(responses));
            ChatExecution execution = new ChatExecution(session, chat);
            if (!streaming) return execution.send(null, () -> {}).getLeft();
            CompletableFuture<ChatResponse> result = new CompletableFuture<>();
            execution.stream()
                    .onPartialResponse(ignored -> {})
                    .onCompleteResponse(result::complete)
                    .onError(result::completeExceptionally)
                    .start();
            assertTrue(result.isDone(), "Scripted stream must finish inline, without waiting or polling");
            return result.join();
        }
    }

    static final class RecordingRuntime implements ToolRuntime {
        final ToolRuntime delegate;
        final List<String> executed = new ArrayList<>();
        RuntimeException failure;

        RecordingRuntime(ToolRuntime delegate) {
            this.delegate = delegate;
        }

        public String id() {
            return delegate.id();
        }

        public boolean isolated() {
            return delegate.isolated();
        }

        public boolean supports(ToolDefinition tool, ToolContext context) {
            return delegate.supports(tool, context);
        }

        public String execute(ToolDefinition tool, JsonNode arguments, ToolContext context) throws Exception {
            executed.add(tool.name());
            if (failure != null) throw failure;
            return delegate.execute(tool, arguments, context);
        }
    }

    static final class Provider implements StreamingChatModel {
        final List<ChatRequest> requests = new ArrayList<>();
        final Deque<ChatResponse> script = new ArrayDeque<>();
        Runnable beforeDelivery = () -> {};
        final ChatModel sync = new ChatModel() {
            public ChatResponse doChat(ChatRequest request) {
                return next(request);
            }

            public ModelProvider provider() {
                return ModelProvider.OTHER;
            }
        };

        ChatResponse next(ChatRequest request) {
            requests.add(request);
            assertFalse(script.isEmpty(), "Unexpected provider call");
            ChatResponse response = script.removeFirst();
            Runnable callback = beforeDelivery;
            beforeDelivery = () -> {};
            callback.run();
            return response;
        }

        public void doChat(ChatRequest request, StreamingChatResponseHandler handler) {
            try {
                ChatResponse response = next(request);
                if (!response.aiMessage().hasToolExecutionRequests())
                    handler.onPartialResponse(response.aiMessage().text());
                handler.onCompleteResponse(response);
            } catch (RuntimeException failure) {
                handler.onError(failure);
            }
        }

        public ModelProvider provider() {
            return ModelProvider.OTHER;
        }
    }

    public static final class Recall {
        int calls;
        Runnable afterCall = () -> {};

        @Tool(name = "search_memory", value = "Search session memory")
        public String searchMemory(@P("Recall query") String query) {
            calls++;
            afterCall.run();
            return "remembered:" + query;
        }

        @Tool(name = "read_memory", value = "Read session memory")
        public String readMemory(@P("Memory identifier") String id) {
            calls++;
            return "memory:" + id;
        }
    }

    public static final class Album {
        int calls;

        @Tool(name = "find_image", value = "Find a character image")
        public String findAnImage(@P("Scene description") String query) {
            calls++;
            return "image:" + query;
        }
    }
}
