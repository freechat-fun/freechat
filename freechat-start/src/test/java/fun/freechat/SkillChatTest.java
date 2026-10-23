package fun.freechat;

import static fun.freechat.BuiltinToolChatTest.*;
import static org.junit.jupiter.api.Assertions.*;

import dev.langchain4j.data.message.*;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.TokenCountEstimator;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import fun.freechat.service.chat.ChatExecution;
import fun.freechat.service.chat.ChatSession;
import fun.freechat.service.skills.*;
import fun.freechat.service.tools.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

@Timeout(10)
class SkillChatTest {
    private static final String ID = "a".repeat(32);
    private static final String BODY = "Maintain narrative continuity. private-skill-instruction-sentinel";
    private static final String REFERENCE = "Prefer dialogue. private-skill-reference-sentinel";
    private static final String CUSTOM_SYSTEM =
            "Custom role template rendered verbatim: {{not-a-variable-now}} {literal}";

    @ParameterizedTest
    @CsvSource({"false,false", "true,false", "false,true", "true,true"})
    void loadsProgressivelyWithoutPersistingInternalBodiesOrGrantingImageAccess(boolean streaming, boolean derived) {
        Fixture f = new Fixture();
        AtomicBoolean live = new AtomicBoolean(true);
        ChatMemory memory = memoryWithSystem();
        ChatSession session = session(f, memory, live, false);
        if (derived) {
            session = session.forInvocation(memory, f.provider, List.of(new Recall()), 8, () -> assertTrue(live.get()));
        }
        f.provider.script.addAll(List.of(
                tools(call("skill", "{\"skillId\":\"" + ID + "\"}")),
                tools(call("read_skill_resource", "{\"skillId\":\"" + ID + "\",\"file\":\"references/style.md\"}")),
                tools(call("calculate", "{\"expression\":\"6*7\"}")),
                reply()));
        ChatResponse response = run(session, streaming);
        assertEquals("The answer is 42.", response.aiMessage().text());
        assertEquals(4, f.provider.requests.size());
        String initial = text(f.provider.requests.getFirst());
        assertTrue(initial.contains("storytelling"));
        assertFalse(initial.contains(BODY));
        assertFalse(initial.contains(REFERENCE));
        assertTrue(text(f.provider.requests.get(1)).contains(BODY));
        assertFalse(text(f.provider.requests.get(1)).contains(REFERENCE));
        assertTrue(text(f.provider.requests.get(2)).contains(REFERENCE));
        for (ChatRequest request : f.provider.requests) {
            assertEquals(
                    1,
                    request.messages().stream()
                            .filter(SystemMessage.class::isInstance)
                            .count());
            assertTrue(((SystemMessage) request.messages().getFirst()).text().startsWith(CUSTOM_SYSTEM));
            assertEquals(1, text(request).split("FreeChat skill policy:", -1).length - 1);
            assertTrue(SkillTools.SAFE_TOOLS.containsAll(names(request.toolSpecifications())));
            assertFalse(names(request.toolSpecifications()).contains("find_image"));
        }
        String persisted = ChatMessageSerializer.messagesToJson(memory.messages());
        assertFalse(persisted.contains(BODY));
        assertFalse(persisted.contains(REFERENCE));
        assertFalse(persisted.contains("FreeChat skill policy:"));
        assertEquals(CUSTOM_SYSTEM, ((SystemMessage) memory.messages().getFirst()).text());
        assertEquals(0, f.album.calls);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void explicitToolDisableHasNeitherLoaderNorCatalog(boolean streaming) {
        Fixture f = new Fixture();
        ChatSession session = session(f, memoryWithSystem(), new AtomicBoolean(true), true);
        f.provider.script.add(reply());
        run(session, streaming);
        assertTrue(names(f.provider.requests.getFirst().toolSpecifications()).isEmpty());
        assertFalse(text(f.provider.requests.getFirst()).contains("storytelling"));
        assertFalse(text(f.provider.requests.getFirst()).contains("FreeChat skill policy:"));
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void revocationAfterModelResponseDeniesCapturedLoader(boolean streaming) {
        Fixture f = new Fixture();
        AtomicBoolean live = new AtomicBoolean(true);
        ChatSession session = session(f, memoryWithSystem(), live, false);
        f.provider.beforeDelivery = () -> live.set(false);
        f.provider.script.add(tools(call("skill", "{\"skillId\":\"" + ID + "\"}")));
        assertThrows(RuntimeException.class, () -> run(session, streaming));
        assertEquals(1, f.provider.requests.size());
        assertFalse(
                ChatMessageSerializer.messagesToJson(session.getChatMemory(CHAT).messages())
                        .contains(BODY));
    }

    @Test
    void referenceReadsRequireActivationAndExactMembershipAndCannotCrossChatScope() {
        Fixture f = new Fixture();
        var bound =
                session(f, memoryWithSystem(), new AtomicBoolean(true), false).bindInvocation();
        var reader = bound.tools().get(SkillTools.READ.specification());
        var loader = bound.tools().get(SkillTools.LOAD.specification());
        assertThrows(
                RuntimeException.class,
                () -> reader.execute(
                        call("read_skill_resource", "{\"skillId\":\"" + ID + "\",\"file\":\"references/style.md\"}"),
                        CHAT));
        assertThrows(
                RuntimeException.class,
                () -> loader.execute(call("skill", "{\"skillId\":\"" + ID + "\"}"), "another-chat"));
        assertThrows(
                RuntimeException.class,
                () -> loader.execute(call("skill", "{\"skillId\":\"" + "b".repeat(32) + "\"}"), CHAT));
        assertEquals("{\"loaded\":true}", loader.execute(call("skill", "{\"skillId\":\"" + ID + "\"}"), CHAT));
        for (String file : List.of("../SKILL.md", "/etc/passwd", "references/missing.md", "https://example.com/")) {
            assertThrows(
                    RuntimeException.class,
                    () -> reader.execute(
                            call("read_skill_resource", ToolArguments.result(Map.of("skillId", ID, "file", file))),
                            CHAT));
        }
        assertThrows(
                RuntimeException.class,
                () -> reader.execute(
                        call(
                                "read_skill_resource",
                                ToolArguments.result(
                                        Map.of("skillId", ID, "file", "references/style.md", "ownerId", "creator"))),
                        CHAT));
    }

    @Test
    void activeContentDoesNotSurviveAnotherInvocation() {
        Fixture f = new Fixture();
        ChatSession session = session(f, memoryWithSystem(), new AtomicBoolean(true), false);
        var first = session.bindInvocation();
        first.tools().get(SkillTools.LOAD.specification()).execute(call("skill", "{\"skillId\":\"" + ID + "\"}"), CHAT);
        assertTrue(ChatMessageSerializer.messagesToJson(
                        first.skills().messages(memoryWithSystem().messages()))
                .contains(BODY));
        var second = session.bindInvocation();
        assertFalse(ChatMessageSerializer.messagesToJson(
                        second.skills().messages(memoryWithSystem().messages()))
                .contains(BODY));
    }

    private static ChatSession session(Fixture f, ChatMemory memory, AtomicBoolean live, boolean disabled) {
        SkillTools.definitions().forEach(f.registry::register);
        Set<String> allowed = new HashSet<>(CharacterToolConfiguration.DEFAULTS);
        allowed.addAll(SkillTools.NAMES);
        f.broker.setAllowed(allowed);
        f.select(CHAT, "character", disabled ? Set.of() : allowed, true, true);
        return ChatSession.builder()
                .chatModel(f.provider.sync)
                .streamingChatModel(f.provider)
                .chatMemory(memory)
                .variables(new HashMap<>())
                .objectsWithTools(List.of(f.album))
                .toolBinder(f.broker.binder(CHAT, "owner", "character", "backend-character"))
                .skillBinder(() -> new SkillInvocation(
                        List.of(new SkillService.Installed(
                                ID,
                                "creator",
                                "revision",
                                new SkillBundle(
                                        "storytelling",
                                        "Narrative guidance",
                                        Map.of("SKILL.md", BODY, "references/style.md", REFERENCE),
                                        "digest"),
                                Instant.EPOCH)),
                        new ToolContext(CHAT, "owner", "character", "backend-character", Duration.ofMinutes(1)),
                        () -> {
                            if (!live.get()) throw new IllegalStateException("revoked");
                        },
                        estimator(),
                        8192))
                .build();
    }

    private static ChatMemory memoryWithSystem() {
        ChatMemory memory = MessageWindowChatMemory.withMaxMessages(100);
        memory.add(SystemMessage.from(CUSTOM_SYSTEM));
        memory.add(UserMessage.from("Tell a story."));
        return memory;
    }

    private static ChatResponse run(ChatSession session, boolean streaming) {
        ChatExecution execution = new ChatExecution(session, CHAT);
        if (!streaming) return execution.send(null, () -> {}).getLeft();
        CompletableFuture<ChatResponse> result = new CompletableFuture<>();
        execution.stream()
                .onPartialResponse(ignored -> {})
                .onCompleteResponse(result::complete)
                .onError(result::completeExceptionally)
                .start();
        assertTrue(result.isDone());
        return result.join();
    }

    private static String text(ChatRequest request) {
        return ChatMessageSerializer.messagesToJson(request.messages());
    }

    static TokenCountEstimator estimator() {
        return new TokenCountEstimator() {
            public int estimateTokenCountInText(String text) {
                return (text.length() + 3) / 4;
            }

            public int estimateTokenCountInMessage(ChatMessage message) {
                return estimateTokenCountInText(ChatMessageSerializer.messageToJson(message));
            }

            public int estimateTokenCountInMessages(Iterable<ChatMessage> messages) {
                int count = 0;
                for (ChatMessage message : messages) count += estimateTokenCountInMessage(message);
                return count;
            }
        };
    }
}
