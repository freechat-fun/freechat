package fun.freechat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.agent.tool.ToolSpecifications;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.message.*;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.TokenCountEstimator;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.model.output.TokenUsage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import fun.freechat.mapper.*;
import fun.freechat.model.CharacterBackend;
import fun.freechat.model.ChatContext;
import fun.freechat.model.ChatHistory;
import fun.freechat.model.ChatMemoryCommit;
import fun.freechat.model.ChatMemoryState;
import fun.freechat.service.character.CharacterService;
import fun.freechat.service.chat.*;
import fun.freechat.service.chat.impl.AlbumTool;
import fun.freechat.service.chat.impl.ChatServiceImpl;
import fun.freechat.service.chat.impl.ChatTaskQueueManager;
import fun.freechat.service.chat.impl.LongTermChatMemoryStoreImpl;
import fun.freechat.service.chat.memory.*;
import fun.freechat.service.chat.memory.MemoryTurnRepository.Origin;
import fun.freechat.service.enums.EmbeddingStoreType;
import fun.freechat.service.enums.PromptFormat;
import fun.freechat.service.prompt.ChatPromptContent;
import fun.freechat.service.prompt.PromptService;
import fun.freechat.service.rag.EmbeddingModelService;
import fun.freechat.service.rag.ExactEmbeddingStoreService;
import fun.freechat.service.skills.*;
import fun.freechat.service.tools.*;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockMakers;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.SimpleTransactionStatus;

/** Live service/store/session and pinned LC 1.20 tool loop; only provider and persistence boundaries are fakes. */
@Timeout(20)
class ChatServiceMemoryTest {
    static final String FINGERPRINT = "a".repeat(64);
    static final String PRIVATE = "synthetic-private-provider-payload";
    static final UserMessage ORIGINAL = UserMessage.from("original input");
    static final UserMessage TRANSFORMED = UserMessage.from("templated input");
    static final TokenUsage USAGE = new TokenUsage(7, 3);
    static final ObjectMapper JSON = new ObjectMapper();
    private static final String SKILL_ID = "b".repeat(32);
    private static final String SKILL_BODY = "Preserve narrative continuity. ltm-private-SKILL-md-sentinel {literal}";
    private static final String SKILL_REFERENCE = "Prefer dialogue. ltm-private-reference-sentinel {{literal}}";
    private static final String CUSTOM_ROLE = "Existing role: a patient guide. {{not-a-variable-now}} {literal}";
    private static final String SKILL_POLICY = "\n\nFreeChat skill policy:";
    private static final String SKILL_JSON = "Skill context JSON:\n";

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void skillsOverlayRealMemoryWithoutReplacingRoleProfilesOrKnowledge(boolean streaming) throws Exception {
        SkillSetup skills = new SkillSetup(SKILL_BODY, SKILL_REFERENCE);
        try (Fixture f = new Fixture(null, skills)) {
            MemoryDocument profile = new MemoryDocument(
                    UUID.randomUUID().toString(),
                    f.document.scope(),
                    UUID.randomUUID().toString(),
                    MemoryDocument.Kind.PROFILE_SNAPSHOT,
                    false,
                    1,
                    2,
                    f.document.observedAt(),
                    FINGERPRINT,
                    "",
                    List.of(new MemoryDocument.Fact(
                            "drink", "Prefers tea", List.of(1L), f.document.observedAt(), false)),
                    List.of(new MemoryDocument.Fact(
                            "promise", "Will guide", List.of(2L), f.document.observedAt(), true)));
            f.publish(profile);
            f.turns.read(f.id).orElseThrow().setProfileId(profile.id());
            String knowledge = "Verified character knowledge";
            AtomicInteger retrievals = new AtomicInteger();
            var retriever = dev.langchain4j.rag.DefaultRetrievalAugmentor.builder()
                    .contentRetriever(query -> {
                        assertEquals(ORIGINAL.singleText(), query.text());
                        retrievals.incrementAndGet();
                        return List.of(dev.langchain4j.rag.content.Content.from(knowledge));
                    })
                    .executor(Runnable::run)
                    .build();
            f.base.getAiServiceContext().retrievalAugmentor = retriever;
            var rag = testMock(fun.freechat.service.rag.RagTaskService.class);
            when(rag.hasAnyTask("character")).thenReturn(true);
            when(f.contexts.getCharacterUid(f.id)).thenReturn("character");
            when(f.characters.getNameByUid("character")).thenReturn("Guide");
            ReflectionTestUtils.setField(f.service, "characterService", f.characters);
            ReflectionTestUtils.setField(f.service, "ragTaskService", rag);
            String variable = fun.freechat.service.enums.ChatVar.RELEVANT_INFORMATION.text();
            String template = CUSTOM_ROLE + "\nKnowledge: {{" + variable + "}}";
            f.base.getPrompt().setSystem(template);
            when(f.prompts.apply(eq(template), anyMap(), eq(PromptFormat.MUSTACHE)))
                    .thenAnswer(call -> CUSTOM_ROLE + "\nKnowledge: "
                            + Objects.toString(((Map<?, ?>) call.getArgument(1)).get(variable), ""));
            List<List<ChatMessage>> baseMessages = new ArrayList<>();
            f.provider.observe = request -> {
                assertSame(f.binding().memory(), f.binding().session().getChatMemory(f.id));
                assertInstanceOf(
                        MemoryStreamingModel.class, f.binding().session().getStreamingChatModel());
                List<ChatMessage> base = f.binding().memory().messages();
                assertNoSkillContent(ChatMessageSerializer.messagesToJson(base));
                baseMessages.add(base);
            };
            ChatResponse answer = f.run(
                    streaming,
                    List.of(
                            response(AiMessage.from(loadSkill("load"))),
                            response(AiMessage.from(readSkill("reference"))),
                            response(AiMessage.from(loadSkill("reload"), readSkill("reread"))),
                            response(AiMessage.from(call("search", "search_memory", "{\"query\":\"prior event\"}"))),
                            response(AiMessage.from(
                                    call("read", "read_memory", "{\"recordId\":\"" + f.document.id() + "\"}"))),
                            response(AiMessage.from("answer with guidance"))));
            assertEquals("answer with guidance", answer.aiMessage().text());
            assertEquals(new TokenUsage(42, 18), answer.tokenUsage());
            assertEquals(6, f.provider.requests.size());
            assertEquals(6, baseMessages.size());
            assertEquals(1, skills.binds.get());
            assertEquals(1, skills.toolBinds.get());
            assertEquals(1, retrievals.get());
            assertSame(retriever, f.binding().session().getRetriever());
            assertEquals(knowledge, f.binding().session().getVariables().get(variable));
            for (int index = 0; index < 6; index++) {
                ChatRequest request = f.provider.requests.get(index);
                assertEquals(
                        Set.of("skill", "read_skill_resource", "search_memory", "read_memory"),
                        BuiltinToolChatTest.names(request.toolSpecifications()));
                assertEquals(4, request.toolSpecifications().size(), "No duplicate registrations");
                assertTrue(request.toolSpecifications().contains(SkillTools.LOAD.specification()));
                assertTrue(request.toolSpecifications().contains(SkillTools.READ.specification()));
                assertEquals(
                        1,
                        request.messages().stream()
                                .filter(SystemMessage.class::isInstance)
                                .count());
                String system = ((SystemMessage) request.messages().getFirst()).text();
                assertEquals(1, occurrences(system, SKILL_POLICY));
                String base = ((SystemMessage) baseMessages.get(index).getFirst()).text();
                assertTrue(base.startsWith(CUSTOM_ROLE + "\nKnowledge: " + knowledge));
                assertTrue(system.startsWith(base + SKILL_POLICY));
                JsonNode memory = json(
                        base.substring(base.indexOf("Session memory JSON:\n") + "Session memory JSON:\n".length()));
                assertEquals("Prefers tea", memory.at("/userFacts/0/value").asText());
                assertEquals("Will guide", memory.at("/characterDeltas/0/value").asText());
                assertTrue(memory.at("/characterDeltas/0/fictional").asBoolean());
                JsonNode data = skillData(request);
                assertEquals(1, data.path("available").size());
                JsonNode catalog = data.at("/available/0");
                assertEquals(
                        Set.of("skillId", "name", "description", "files"),
                        JSON.convertValue(catalog, Map.class).keySet());
                assertEquals(SKILL_ID, catalog.path("skillId").asText());
                assertEquals("storytelling", catalog.path("name").asText());
                assertEquals("Narrative guidance", catalog.path("description").asText());
                assertEquals(JSON.valueToTree(List.of("SKILL.md", "references/style.md")), catalog.path("files"));
                JsonNode loaded = data.path("loaded").path(SKILL_ID);
                assertEquals(index == 0 ? 0 : 1, data.path("loaded").size());
                assertEquals(index == 0 ? 0 : index == 1 ? 1 : 2, loaded.size());
                assertEquals(
                        index == 0 ? "" : SKILL_BODY, loaded.path("SKILL.md").asText());
                assertEquals(
                        index < 2 ? "" : SKILL_REFERENCE,
                        loaded.path("references/style.md").asText());
                assertEquals(index == 0 ? 0 : 1, occurrences(system, SKILL_BODY));
                assertEquals(index < 2 ? 0 : 1, occurrences(system, SKILL_REFERENCE));
                assertEquals(
                        baseMessages
                                .get(index)
                                .subList(1, baseMessages.get(index).size()),
                        request.messages().subList(1, request.messages().size()));
                assertEquals(TRANSFORMED, request.messages().get(1));
            }
            List<ToolExecutionResultMessage> results = toolResults(f.provider.requests.getLast());
            assertEquals(
                    List.of(
                            "skill",
                            "read_skill_resource",
                            "skill",
                            "read_skill_resource",
                            "search_memory",
                            "read_memory"),
                    results.stream().map(ToolExecutionResultMessage::toolName).toList());
            results.subList(0, 4).forEach(result -> assertEquals("{\"loaded\":true}", result.text()));
            assertEquals("ok", json(results.get(4).text()).path("status").asText());
            assertEquals("ok", json(results.get(5).text()).path("status").asText());
            verify(f.vectorStore).search(any());
            assertSkillPersistenceIsPrivate(f);
            assertSkillExtractionIsPrivate(f, skills);
            assertEquals(0, f.albumCalls.get());
            assertEquals(0, f.aborts.get());
            assertEquals(6, f.usageRows().size());
            assertTrue(f.binding().memory().completed());
            assertThrows(
                    IllegalStateException.class,
                    () -> f.binding().session().getToolExecutors().get("skill").execute(loadSkill("closed"), f.id));
            f.assertTimersDisposed();
            if (streaming) {
                f.provider.complete(5, response(AiMessage.from("late duplicate")));
                assertEquals(6, f.usageRows().size());
                assertEquals(6, f.provider.requests.size());
            }
            var previous = f.binding();
            f.run(streaming, List.of(response(AiMessage.from("fresh invocation"))));
            assertEquals(2, skills.binds.get());
            assertEquals(2, skills.toolBinds.get());
            assertNotSame(previous.memory(), f.binding().memory());
            assertNotSame(
                    previous.session().getSkillInvocation(),
                    f.binding().session().getSkillInvocation());
            assertTrue(skillData(f.provider.requests.getLast()).path("loaded").isEmpty());
            assertEquals(
                    0,
                    occurrences(
                            ((SystemMessage) f.provider
                                            .requests
                                            .getLast()
                                            .messages()
                                            .getFirst())
                                    .text(),
                            SKILL_BODY));
            assertEquals(List.of(f.albumSpec), f.base.getToolSpecifications());
            assertTrue(f.base.getChatMemory(f.id).messages().isEmpty());
            assertEquals(Map.of("base", "unchanged"), f.base.getVariables());
            assertSkillPersistenceIsPrivate(f);
            assertEquals(0, f.aborts.get());
            f.assertTimersDisposed();
        }
    }

    @ParameterizedTest
    @org.junit.jupiter.params.provider.CsvSource({"false,false", "true,false", "false,true", "true,true"})
    void loadedSkillOrReferenceExhaustsRealMemoryBudgetBeforeNextDispatch(boolean streaming, boolean reference)
            throws Exception {
        String large = " bounded-guidance".repeat(1000);
        SkillSetup skills =
                new SkillSetup(SKILL_BODY + (reference ? "" : large), SKILL_REFERENCE + (reference ? large : ""));
        try (Fixture f = new Fixture(null, skills)) {
            f.properties.setMaxInputTokens(2048);
            List<ChatResponse> script = new ArrayList<>();
            script.add(response(AiMessage.from(loadSkill("load"))));
            if (reference) script.add(response(AiMessage.from(readSkill("reference"))));
            // No terminal provider response: the continuation itself must fail preflight.
            assertTurnFailure(assertThrows(Exception.class, () -> f.run(streaming, script)));
            f.idle();
            int dispatched = reference ? 2 : 1;
            assertEquals(dispatched, f.provider.requests.size());
            assertEquals(dispatched, f.usageRows().size());
            assertEquals(
                    dispatched,
                    f.writes.stream()
                            .filter(write -> write.message() instanceof ToolExecutionResultMessage)
                            .count());
            f.writes.stream()
                    .filter(write -> write.message() instanceof ToolExecutionResultMessage)
                    .forEach(write ->
                            assertEquals("{\"loaded\":true}", ((ToolExecutionResultMessage) write.message()).text()));
            List<ChatMessage> measured = skills.measured.get();
            assertTrue(ChatMessageSerializer.messagesToJson(measured).contains(large));
            var bounds = new MemoryBounds(skills.estimator, f.properties);
            var tools = f.binding().session().getToolSpecifications();
            assertThrows(MemoryBounds.CapacityException.class, () -> bounds.requirePrompt(measured, tools, null));
            List<ChatMessage> withoutOverlay = new ArrayList<>(measured);
            String system = ((SystemMessage) withoutOverlay.getFirst()).text();
            withoutOverlay.set(0, SystemMessage.from(system.substring(0, system.indexOf(SKILL_POLICY))));
            assertDoesNotThrow(
                    () -> bounds.requirePrompt(withoutOverlay, tools, null),
                    "The base context fits; loaded guidance, not history or tool count, exhausts the budget");
            assertSkillPersistenceIsPrivate(f);
            assertFalse(f.binding().memory().completed());
            assertEquals(1, f.aborts.get());
            assertEquals(0, f.albumCalls.get());
            assertThrows(
                    IllegalStateException.class,
                    () -> f.binding()
                            .session()
                            .getToolExecutors()
                            .get("read_skill_resource")
                            .execute(readSkill("closed"), f.id));
            f.binding().close();
            assertEquals(1, f.aborts.get());
            f.assertTimersDisposed();
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void skillSpecificBudgetIsEnforcedWhenMemoryBudgetIsLarger(boolean streaming) throws Exception {
        SkillSetup skills = new SkillSetup(SKILL_BODY + " bounded-guidance".repeat(1000), SKILL_REFERENCE);
        skills.skillTokenLimit = 2048;
        try (Fixture f = new Fixture(null, skills)) {
            f.properties.setMaxInputTokens(8192);
            assertTurnFailure(assertThrows(
                    Exception.class, () -> f.run(streaming, List.of(response(AiMessage.from(loadSkill("load")))))));
            f.idle();
            assertEquals(1, f.provider.requests.size());
            assertEquals(1, f.aborts.get());
            assertSkillPersistenceIsPrivate(f);
            f.assertTimersDisposed();
        }
    }

    @ParameterizedTest
    @org.junit.jupiter.params.provider.CsvSource({
        "false,album",
        "true,album",
        "false,policy",
        "true,policy",
        "false,catalog",
        "true,catalog"
    })
    void skillsCannotExecuteAlbumOrBypassRevokedLoaderPolicy(boolean streaming, String denied) throws Exception {
        SkillSetup skills = new SkillSetup(SKILL_BODY, SKILL_REFERENCE);
        try (Fixture f = new Fixture(null, skills)) {
            f.provider.observe = request -> {
                assertFalse(
                        BuiltinToolChatTest.names(request.toolSpecifications()).contains("find_image"));
                assertTrue(
                        BuiltinToolChatTest.names(request.toolSpecifications()).contains("skill"));
                if (denied.equals("policy"))
                    skills.enabled = Set.of("read_skill_resource", "search_memory", "read_memory", "find_image");
                if (denied.equals("catalog")) assertTrue(skills.registry.unregister("skill"));
            };
            ChatResponse tool =
                    denied.equals("album") ? albumResponse() : response(AiMessage.from(loadSkill("denied")));
            assertTurnFailure(assertThrows(Exception.class, () -> f.run(streaming, List.of(tool))));
            f.idle();
            assertEquals(1, f.provider.requests.size());
            assertEquals(1, f.usageRows().size());
            assertEquals(0, f.albumCalls.get());
            assertTrue(f.writes.stream().noneMatch(write -> write.message() instanceof ToolExecutionResultMessage));
            assertSkillPersistenceIsPrivate(f);
            assertFalse(f.binding().memory().completed());
            assertEquals(1, f.aborts.get());
            f.assertTimersDisposed();
        }
    }

    @Test
    void loadedSkillStreamDeadlineFencesLateContinuationAndDisposesTimers() throws Exception {
        SkillSetup skills = new SkillSetup(SKILL_BODY, SKILL_REFERENCE);
        try (Fixture f = new Fixture(null, skills)) {
            CompletableFuture<ChatResponse> terminal = new CompletableFuture<>();
            TokenStream stream = f.service.streamSend(f.id, ORIGINAL, null);
            assertTrue(f.provider.requests.isEmpty());
            stream.onCompleteResponse(terminal::complete)
                    .onError(terminal::completeExceptionally)
                    .start();
            f.provider.complete(0, response(AiMessage.from(loadSkill("load"))));
            assertEquals(
                    SKILL_BODY,
                    skillData(f.provider.requests.get(1))
                            .path("loaded")
                            .path(SKILL_ID)
                            .path("SKILL.md")
                            .asText());
            f.timeout.run();
            assertTurnFailure(assertThrows(ExecutionException.class, () -> terminal.get(5, TimeUnit.SECONDS)));
            f.idle();
            assertEquals(1, f.aborts.get());
            assertEquals(3, f.writes.size());
            assertThrows(
                    IllegalStateException.class,
                    () -> f.binding()
                            .session()
                            .getToolExecutors()
                            .get("read_skill_resource")
                            .execute(readSkill("closed"), f.id));
            f.provider.complete(1, response(AiMessage.from(readSkill("late"))));
            f.provider.complete(1, response(AiMessage.from(readSkill("duplicate"))));
            assertEquals(2, f.usageRows().size(), "Late returned usage is still accounted exactly once");
            assertEquals(2, f.provider.requests.size());
            assertEquals(3, f.writes.size());
            assertEquals(1, f.aborts.get());
            assertEquals(0, f.albumCalls.get());
            assertSkillPersistenceIsPrivate(f);
            f.assertTimersDisposed();
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void albumAndScopedRecallSurviveRealRegistrationAndContinuations(boolean streaming) throws Exception {
        try (Fixture f = new Fixture()) {
            ChatResponse tools = response(AiMessage.from(
                    call("album", "find_image", "{\"description\":\"current scene\"}"),
                    call("search", "search_memory", "{\"query\":\"prior event\"}")));
            ChatResponse read = response(AiMessage.from(
                    call("read", "read_memory", "{\"recordId\":\"" + f.document.id() + "\",\"offset\":256}")));
            ChatResponse answer = response(AiMessage.from("answer"));
            ChatResponse result;
            if (streaming) {
                TokenStream stream = f.service.streamSend(f.id, ORIGINAL, "context");
                assertTrue(f.provider.requests.isEmpty(), "Admission must not start the provider");
                verify(f.vectorStore, never()).search(any());
                CompletableFuture<ChatResponse> terminal = new CompletableFuture<>();
                stream.onCompleteResponse(value -> {
                            // MemoryTokenStream deliberately contains user callback exceptions: publish them to the
                            // test.
                            try {
                                assertTrue(f.binding().memory().completed());
                                assertEquals("complete", f.events.getLast());
                                terminal.complete(value);
                            } catch (Throwable failure) {
                                terminal.completeExceptionally(failure);
                            }
                        })
                        .onError(terminal::completeExceptionally)
                        .start();
                f.provider.complete(0, tools);
                assertEquals(2, f.provider.requests.size());
                f.provider.complete(1, read);
                assertEquals(3, f.provider.requests.size());
                f.provider.complete(2, answer);
                result = terminal.get(5, TimeUnit.SECONDS);
                f.provider.complete(2, answer); // Late duplicate cannot account or finalize twice.
            } else {
                f.provider.script.addAll(List.of(tools, read, answer));
                var reply = f.service.send(f.id, ORIGINAL, "context");
                assertEquals(99L, reply.getRight());
                result = reply.getLeft();
            }
            f.idle();
            assertEquals("answer", result.aiMessage().text());
            assertEquals(new TokenUsage(21, 9), result.tokenUsage());
            assertEquals(3, f.usageRows().size());
            assertEquals(0, f.aborts.get());
            assertEquals(1, f.albumCalls.get());
            assertEquals(f.id, f.albumMemoryId.get());
            assertEquals(
                    List.of(
                            "input",
                            "usage",
                            "tool-message",
                            "album",
                            "tool-result",
                            "tool-result",
                            "usage",
                            "tool-message",
                            "tool-result",
                            "usage",
                            "complete"),
                    f.events);
            assertEquals(
                    Arrays.asList(null, USAGE, null, null, USAGE, null, USAGE),
                    f.writes.stream().map(Write::usage).toList());
            Write input = f.writes.getFirst();
            assertEquals(Origin.USER_INPUT, input.origin());
            assertEquals(ORIGINAL, input.original());
            assertEquals(TRANSFORMED, input.message());
            assertTrue(input.system().text().contains("Session memory JSON"));
            assertEquals(
                    1,
                    f.writes.stream().filter(write -> write.original() != null).count());
            assertEquals(Origin.ASSISTANT_OUTPUT, f.writes.getLast().origin());
            assertEquals(
                    List.of("find_image", "search_memory", "read_memory"),
                    toolResults(f.provider.requests.get(2)).stream()
                            .map(ToolExecutionResultMessage::toolName)
                            .toList());
            JsonNode search =
                    json(toolResults(f.provider.requests.get(1)).getLast().text());
            assertEquals("ok", search.path("status").asText());
            assertEquals(f.document.id(), search.at("/results/0/recordId").asText());
            assertEquals(256, search.at("/results/0/nextOffset").asInt());
            assertFalse(search.path("authoritative").asBoolean(true));
            JsonNode page =
                    json(toolResults(f.provider.requests.get(2)).getLast().text());
            assertEquals("ok", page.path("status").asText());
            assertEquals(f.document.summary().substring(256), page.path("text").asText());
            assertTrue(page.path("nextOffset").isNull());
            for (ChatRequest request : f.provider.requests) {
                assertEquals(
                        Set.of("find_image", "search_memory", "read_memory"),
                        request.toolSpecifications().stream()
                                .map(ToolSpecification::name)
                                .collect(java.util.stream.Collectors.toSet()));
                assertEquals(3, request.toolSpecifications().size(), "No duplicated registrations");
                assertTrue(request.toolSpecifications().contains(f.albumSpec), "Annotated album schema must survive");
                assertEquals(
                        1,
                        request.messages().stream()
                                .filter(SystemMessage.class::isInstance)
                                .count());
                assertEquals(TRANSFORMED, request.messages().get(1));
                assertFalse(request.messages().contains(ORIGINAL));
                assertFalse(
                        ChatMessageSerializer.messagesToJson(request.messages()).contains("untrusted-ann-body"));
            }
            verify(f.vectorStore, times(1)).search(any());
            assertNull(f.base.getRetriever());
            assertNull(f.binding().session().getRetriever(), "Recall must not install a raw ANN retriever");
            assertTrue(f.base.getChatMemory(f.id).messages().isEmpty());
            assertEquals(Map.of("base", "unchanged"), f.base.getVariables());
            verify(f.sessions).get(f.context);
            verify(f.sessions, never()).get(f.id);
            f.assertTimersDisposed();
        }
    }

    @ParameterizedTest
    @org.junit.jupiter.params.provider.CsvSource({
        "false, hit",
        "true, hit",
        "false, empty",
        "true, empty",
        "false, failure",
        "true, failure"
    })
    void knowledgeRetrievalAndPinnedProfilesSurviveRecallContinuations(boolean streaming, String outcome)
            throws Exception {
        try (Fixture f = new Fixture()) {
            MemoryDocument profile = new MemoryDocument(
                    UUID.randomUUID().toString(),
                    f.document.scope(),
                    UUID.randomUUID().toString(),
                    MemoryDocument.Kind.PROFILE_SNAPSHOT,
                    false,
                    1,
                    2,
                    f.document.observedAt(),
                    FINGERPRINT,
                    "",
                    List.of(new MemoryDocument.Fact(
                            "drink", "Prefers tea", List.of(1L), f.document.observedAt(), false)),
                    List.of(new MemoryDocument.Fact(
                            "promise", "Will guide", List.of(2L), f.document.observedAt(), true)));
            f.publish(profile);
            f.turns.read(f.id).orElseThrow().setProfileId(profile.id());
            String knowledge = "Verified character knowledge";
            AtomicInteger retrievals = new AtomicInteger();
            var retriever = dev.langchain4j.rag.DefaultRetrievalAugmentor.builder()
                    .contentRetriever(query -> {
                        retrievals.incrementAndGet();
                        assertEquals(ORIGINAL.singleText(), query.text());
                        verify(f.vectorStore, never()).search(any());
                        if (outcome.equals("failure")) {
                            throw new IllegalArgumentException(PRIVATE);
                        }
                        return outcome.equals("hit")
                                ? List.of(dev.langchain4j.rag.content.Content.from(knowledge))
                                : List.of();
                    })
                    .executor(Runnable::run)
                    .build();
            f.base.getAiServiceContext().retrievalAugmentor = retriever;
            var rag = testMock(fun.freechat.service.rag.RagTaskService.class);
            when(rag.hasAnyTask("character")).thenReturn(true);
            when(f.contexts.getCharacterUid(f.id)).thenReturn("character");
            when(f.characters.getNameByUid("character")).thenReturn("Guide");
            ReflectionTestUtils.setField(f.service, "characterService", f.characters);
            ReflectionTestUtils.setField(f.service, "ragTaskService", rag);
            String variable = fun.freechat.service.enums.ChatVar.RELEVANT_INFORMATION.text();
            when(f.prompts.apply(anyString(), anyMap(), any()))
                    .thenAnswer(call -> "Configured policy\nKnowledge: "
                            + Objects.toString(((Map<?, ?>) call.getArgument(1)).get(variable), ""));
            ChatResponse result = f.run(
                    streaming,
                    List.of(
                            response(AiMessage.from(call("search", "search_memory", "{\"query\":\"prior event\"}"))),
                            response(AiMessage.from(
                                    call("read", "read_memory", "{\"recordId\":\"" + f.document.id() + "\"}"))),
                            response(AiMessage.from("final with memory"))));
            assertEquals("final with memory", result.aiMessage().text());
            assertEquals(1, retrievals.get());
            assertSame(retriever, f.binding().session().getRetriever());
            assertEquals(
                    outcome.equals("hit") ? knowledge : "",
                    f.binding().session().getVariables().get(variable));
            assertEquals(Map.of("base", "unchanged"), f.base.getVariables());
            assertEquals(3, f.provider.requests.size());
            for (ChatRequest request : f.provider.requests) {
                assertEquals(
                        1,
                        request.messages().stream()
                                .filter(SystemMessage.class::isInstance)
                                .count());
                String system = ((SystemMessage) request.messages().getFirst()).text();
                assertEquals(outcome.equals("hit"), system.contains(knowledge));
                JsonNode fixed = json(
                        system.substring(system.indexOf("Session memory JSON:\n") + "Session memory JSON:\n".length()));
                assertEquals("Prefers tea", fixed.at("/userFacts/0/value").asText());
                assertEquals("Will guide", fixed.at("/characterDeltas/0/value").asText());
                assertTrue(fixed.at("/characterDeltas/0/fictional").asBoolean());
                assertFalse(system.contains(f.document.summary()));
                assertFalse(system.contains(PRIVATE));
                assertEquals(TRANSFORMED, request.messages().get(1));
            }
            assertEquals(
                    "ok",
                    json(toolResults(f.provider.requests.get(1)).getLast().text())
                            .path("status")
                            .asText());
            assertEquals(
                    "ok",
                    json(toolResults(f.provider.requests.get(2)).getLast().text())
                            .path("status")
                            .asText());
            verify(f.vectorStore).search(any());
            assertTrue(f.binding().memory().completed());
            assertEquals(0, f.aborts.get());
            f.assertTimersDisposed();
        }
    }

    @Test
    void cachedBaseGetsFreshInvocationAndRecallDeduplicationStateOnEverySend() throws Exception {
        try (Fixture f = new Fixture()) {
            for (int invocation = 0; invocation < 2; invocation++) {
                f.provider.script.add(response(
                        AiMessage.from(call("search-" + invocation, "search_memory", "{\"query\":\"prior event\"}"))));
                f.provider.script.add(response(AiMessage.from("done")));
                assertNotNull(f.service.send(f.id, ORIGINAL, null));
                f.idle();
                JsonNode search = json(toolResults(f.provider.requests.get(invocation * 2 + 1))
                        .getFirst()
                        .text());
                assertEquals(
                        "ok",
                        search.path("status").asText(),
                        "An earlier invocation must not poison recall deduplication");
            }
            assertEquals(2, f.bindings.size());
            var first = f.bindings.getFirst();
            var second = f.bindings.getLast();
            assertNotSame(first.memory(), second.memory());
            assertNotSame(first.session(), second.session());
            assertNotSame(first.session().getVariables(), second.session().getVariables());
            assertNotSame(
                    first.session().getAiServiceContext(), second.session().getAiServiceContext());
            assertNotEquals(
                    first.memory().lease().token(), second.memory().lease().token());
            assertEquals(List.of(f.albumSpec), f.base.getToolSpecifications());
            assertEquals(Map.of("base", "unchanged"), f.base.getVariables());
            verify(f.models, times(2)).resolveForSession(f.id, FINGERPRINT);
            assertEquals(0, f.aborts.get());
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void exactlyTwoToolRoundsStillPermitTheFinalModelCall(boolean streaming) throws Exception {
        try (Fixture f = new Fixture()) {
            ChatResponse tool = albumResponse();
            f.properties.setMaxToolRounds(2);
            ChatResponse result = f.run(streaming, List.of(tool, tool, response(AiMessage.from("final after budget"))));
            assertEquals("final after budget", result.aiMessage().text());
            assertEquals(new TokenUsage(21, 9), result.tokenUsage());
            assertEquals(3, f.provider.requests.size());
            assertEquals(2, f.albumCalls.get());
            assertEquals(3, f.usageRows().size());
            assertEquals(0, f.aborts.get());
            assertTrue(f.binding().memory().completed());
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void excessToolRoundIsAccountedButNeverWrittenOrExecuted(boolean streaming) throws Exception {
        try (Fixture f = new Fixture()) {
            f.properties.setMaxToolRounds(2);
            assertTurnFailure(assertThrows(
                    Exception.class,
                    () -> f.run(streaming, List.of(albumResponse(), albumResponse(), albumResponse()))));
            f.idle();
            assertEquals(3, f.provider.requests.size());
            assertEquals(3, f.usageRows().size());
            assertEquals(2, f.albumCalls.get());
            assertEquals(5, f.writes.size(), "Only input and two complete tool exchanges");
            assertEquals(
                    List.of(
                            "input",
                            "usage",
                            "tool-message",
                            "album",
                            "tool-result",
                            "usage",
                            "tool-message",
                            "album",
                            "tool-result",
                            "usage",
                            "abort"),
                    f.events);
            assertEquals(1, f.aborts.get());
            assertFalse(f.binding().memory().completed());
            f.assertTimersDisposed();
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void finalPersistenceFailureKeepsReturnedUsageAndClosesTurnBeforeSuccessor(boolean streaming) throws Exception {
        try (Fixture f = new Fixture()) {
            f.failFinal = true;
            assertTurnFailure(assertThrows(
                    Exception.class, () -> f.run(streaming, List.of(response(AiMessage.from("not durable"))))));
            f.idle();
            assertEquals(1, f.usageRows().size());
            assertEquals(List.of("input", "usage", "abort"), f.events);
            assertEquals(1, f.writes.size());
            assertEquals(1, f.aborts.get());
            assertFalse(f.binding().memory().completed());
            f.assertTimersDisposed();
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void transformationFailureAfterAdmissionClosesBindingWithoutCallingProvider(boolean streaming) throws Exception {
        try (Fixture f = new Fixture()) {
            IllegalArgumentException injected = new IllegalArgumentException(PRIVATE);
            doThrow(injected).when(f.prompts).apply(any(UserMessage.class), anyMap(), any());
            RuntimeException failure = assertThrows(RuntimeException.class, () -> {
                if (streaming) {
                    f.service.streamSend(f.id, ORIGINAL, null);
                } else {
                    f.service.send(f.id, ORIGINAL, null);
                }
            });
            assertInstanceOf(IllegalStateException.class, failure);
            assertFalse(failure.toString().contains(PRIVATE));
            assertNull(failure.getCause());
            assertEquals(0, failure.getSuppressed().length);
            f.idle();
            assertEquals(1, f.aborts.get());
            assertTrue(f.provider.requests.isEmpty());
            assertTrue(f.writes.isEmpty());
            assertTrue(f.usageRows().isEmpty());
            f.assertTimersDisposed();
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void synchronousProviderThrowAndToolThrowReleaseTheirBindings(boolean streaming) throws Exception {
        for (boolean toolFailure : List.of(false, true)) {
            try (Fixture f = new Fixture()) {
                f.provider.failStart = !toolFailure;
                f.failAlbum = toolFailure;
                assertTurnFailure(assertThrows(Exception.class, () -> f.run(streaming, List.of(albumResponse()))));
                f.idle();
                assertEquals(1, f.aborts.get());
                assertEquals(toolFailure ? 1 : 0, f.usageRows().size());
                assertEquals(toolFailure ? 2 : 1, f.writes.size());
                assertFalse(f.binding().memory().completed());
                f.assertTimersDisposed();
            }
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void failuresBeforeAdmissionAreSanitizedAndLeaveTheQueueAvailable(boolean streaming) throws Exception {
        for (String phase : List.of("context", "session", "resolution")) {
            try (Fixture f = new Fixture()) {
                IllegalArgumentException injected = new IllegalArgumentException(PRIVATE);
                switch (phase) {
                    case "context" -> when(f.contexts.get(f.id)).thenThrow(injected);
                    case "session" -> when(f.sessions.get(f.context)).thenThrow(injected);
                    case "resolution" ->
                        when(f.models.resolveForSession(f.id, FINGERPRINT)).thenThrow(injected);
                    default -> fail();
                }
                assertTurnFailure(assertThrows(RuntimeException.class, () -> {
                    if (streaming) {
                        f.service.streamSend(f.id, ORIGINAL, null);
                    } else {
                        f.service.send(f.id, ORIGINAL, null);
                    }
                }));
                f.idle();
                assertTrue(f.provider.requests.isEmpty());
                assertTrue(f.writes.isEmpty());
                assertTrue(f.usageRows().isEmpty());
                assertTrue(f.bindings.isEmpty());
                assertTrue(f.timers.isEmpty());
                assertEquals(0, f.aborts.get());
                verifyNoInteractions(f.turns, f.vectorStore, f.scheduler);
            }
        }
    }

    private static ToolExecutionRequest loadSkill(String id) {
        return call(id, "skill", "{\"skillId\":\"" + SKILL_ID + "\"}");
    }

    private static ToolExecutionRequest readSkill(String id) {
        return call(id, "read_skill_resource", "{\"skillId\":\"" + SKILL_ID + "\",\"file\":\"references/style.md\"}");
    }

    private static JsonNode skillData(ChatRequest request) throws Exception {
        String system = ((SystemMessage) request.messages().getFirst()).text();
        assertTrue(system.contains(SKILL_JSON));
        return json(system.substring(system.indexOf(SKILL_JSON) + SKILL_JSON.length()));
    }

    private static int occurrences(String text, String part) {
        return text.split(java.util.regex.Pattern.quote(part), -1).length - 1;
    }

    private static void assertNoSkillContent(String text) {
        assertFalse(text.contains(SKILL_BODY));
        assertFalse(text.contains(SKILL_REFERENCE));
        assertFalse(text.contains("FreeChat skill policy:"));
        assertFalse(text.contains("Skill context JSON:"));
    }

    private static void assertSkillPersistenceIsPrivate(Fixture f) {
        assertFalse(f.writes.isEmpty());
        for (Write write : f.writes) {
            assertNoSkillContent(write.toString());
            assertNoSkillContent(ChatMessageSerializer.messageToJson(write.message()));
            // These are the durable system snapshots and original/source strings consumed by debug views.
            assertNotNull(write.system());
            assertTrue(write.system().text().contains("Session memory JSON:"));
            assertNoSkillContent(ChatMessageSerializer.messageToJson(write.system()));
            if (write.original() != null) {
                assertEquals(ORIGINAL, write.original());
                assertNoSkillContent(ChatMessageSerializer.messageToJson(write.original()));
            }
        }
    }

    private static void assertSkillExtractionIsPrivate(Fixture f, SkillSetup skills) throws Exception {
        // Replay actual repository writes through snapshot storage, source reader and extractor.
        var files = testMock(fun.freechat.service.common.FileStore.class);
        Map<String, byte[]> persisted = new HashMap<>();
        when(files.write(anyString(), any(byte[].class))).thenAnswer(call -> {
            byte[] bytes = call.getArgument(1);
            persisted.put(call.getArgument(0), bytes.clone());
            return (long) bytes.length;
        });
        when(files.size(anyString())).thenAnswer(call -> (long) persisted.get(call.getArgument(0)).length);
        when(files.newInputStream(anyString()))
                .thenAnswer(call -> new java.io.ByteArrayInputStream(persisted.get(call.getArgument(0))));
        var snapshots = new SystemPromptSnapshotStore(files);
        var lease = f.binding().memory().lease();
        LocalDateTime now = LocalDateTime.of(2026, 9, 1, 0, 0);
        List<ChatHistory> rows = new ArrayList<>();
        for (int index = 0; index < f.writes.size(); index++) {
            Write write = f.writes.get(index);
            rows.add(new ChatHistory()
                    .withId((long) index + 1)
                    .withMemoryId(f.id)
                    .withTurnId(lease.token())
                    .withEpisode(1L)
                    .withGmtCreate(now)
                    .withRecordKind(
                            index == 0 ? "turn-start" : index == f.writes.size() - 1 ? "turn-complete" : "message")
                    .withMessageOrigin(write.origin().text())
                    .withMessage(ChatMessageSerializer.messageToJson(write.message()))
                    .withSystemMessageRef(snapshots.save(f.id, write.system()))
                    .withSourceMessage(
                            write.original() == null ? null : ChatMessageSerializer.messageToJson(write.original())));
            String debugSource = snapshots.read(f.id, rows.getLast().getSystemMessageRef());
            assertEquals(ChatMessageSerializer.messageToJson(write.system()), debugSource);
            assertNoSkillContent(debugSource);
        }
        assertEquals(f.writes.size(), persisted.size());
        persisted
                .values()
                .forEach(bytes -> assertNoSkillContent(new String(bytes, java.nio.charset.StandardCharsets.UTF_8)));
        when(f.turns.finalizedPage(eq(lease.scope()), eq(0L), eq((long) rows.size()), eq(1)))
                .thenReturn(List.of(rows.getLast()));
        when(f.turns.sourcePage(eq(lease.scope()), anyLong(), anyLong(), eq(1)))
                .thenAnswer(call -> rows.stream()
                        .filter(row ->
                                row.getId() > (long) call.getArgument(1) && row.getId() <= (long) call.getArgument(2))
                        .limit(1)
                        .toList());
        var reader = new MemorySourceReader(f.turns);
        var turn = reader.nextTurn(lease.scope(), 0, rows.size()).orElseThrow();
        var bounds = new MemoryBounds(skills.estimator, f.properties);
        var position = new MemorySourceReader.Position(0, 0);
        List<MemorySourceReader.Evidence> evidence = new ArrayList<>();
        for (int page = 0; page <= rows.size(); page++) {
            var fragment = reader.nextFragment(lease.scope(), turn, position, bounds, 6000);
            position = fragment.next();
            if (fragment.evidence() != null) evidence.add(fragment.evidence());
            if (fragment.complete()) break;
            assertTrue(page < rows.size(), "Extraction source traversal must terminate");
        }
        assertEquals(8, evidence.size());
        assertEquals(ORIGINAL.singleText(), evidence.getFirst().text());
        assertTrue(evidence.getFirst().originalInput());
        assertEquals("answer with guidance", evidence.getLast().text());
        assertEquals(
                4,
                evidence.stream()
                        .filter(item -> item.text().equals("{\"loaded\":true}"))
                        .count());
        evidence.forEach(item -> assertNoSkillContent(item.text()));
        var input = new MemoryExtractor.Input(
                lease.scope(),
                FINGERPRINT,
                MemoryPublicationRepository.Operation.IDLE,
                "User baseline",
                CUSTOM_ROLE,
                "",
                List.of(),
                List.of(),
                evidence,
                now.toInstant(ZoneOffset.UTC));
        Provider extraction = new Provider();
        extraction.script.add(response(
                AiMessage.from("{\"summary\":\"A guided conversation\",\"userFacts\":[],\"characterDeltas\":[]}")));
        AtomicInteger usage = new AtomicInteger();
        var output = new MemoryExtractor(f.properties, bounds)
                .extract(
                        extraction.sync,
                        null,
                        input,
                        new MemoryExtractor.Budget(
                                1, Duration.ofSeconds(5), () -> {}, (id, tokens) -> usage.incrementAndGet()));
        assertEquals("A guided conversation", output.summary());
        assertEquals(1, usage.get());
        assertEquals(1, extraction.requests.size());
        ChatRequest request = extraction.requests.getFirst();
        assertTrue(BuiltinToolChatTest.names(request.toolSpecifications()).isEmpty());
        assertNoSkillContent(ChatMessageSerializer.messagesToJson(request.messages()));
        JsonNode data = json(((UserMessage) request.messages().getLast()).singleText());
        assertEquals(8, data.path("evidence").size());
        assertEquals(CUSTOM_ROLE, data.path("characterBaseline").asText());
        assertEquals(ORIGINAL.singleText(), data.at("/evidence/0/text").asText());
    }

    /** Opt-in skill configuration: real catalog, broker, runtime binder and token accounting. */
    private static final class SkillSetup {
        private static final Set<String> ALLOWED =
                Set.of("skill", "read_skill_resource", "search_memory", "read_memory", "find_image");
        final ToolRegistry registry = new ChatToolsConfiguration("", "", "").toolRegistry();
        final AtomicInteger binds = new AtomicInteger();
        final AtomicInteger toolBinds = new AtomicInteger();
        final AtomicReference<List<ChatMessage>> measured = new AtomicReference<>();
        final TokenCountEstimator estimator = new TokenCountEstimator() {
            public int estimateTokenCountInText(String text) {
                return (text.length() + 3) / 4;
            }

            public int estimateTokenCountInMessage(ChatMessage message) {
                return estimateTokenCountInText(ChatMessageSerializer.messageToJson(message));
            }

            public int estimateTokenCountInMessages(Iterable<ChatMessage> messages) {
                List<ChatMessage> copy = new ArrayList<>();
                messages.forEach(copy::add);
                measured.set(List.copyOf(copy));
                return copy.stream().mapToInt(this::estimateTokenCountInMessage).sum();
            }
        };
        final SkillService.Installed installed;
        volatile Set<String> enabled = ALLOWED;
        int skillTokenLimit = 8192;
        ToolRuntimeBroker broker;

        SkillSetup(String body, String reference) {
            installed = new SkillService.Installed(
                    SKILL_ID,
                    "creator",
                    "revision",
                    new SkillBundle(
                            "storytelling",
                            "Narrative guidance",
                            Map.of("SKILL.md", body, "references/style.md", reference),
                            "digest"),
                    Instant.EPOCH);
        }

        ToolBinder tools(String chatId) {
            broker = new ToolRuntimeBroker(
                    registry,
                    id -> {
                        assertEquals(chatId, id);
                        return new ToolPolicySource.Policy("owner", "character", "backend", enabled, true, true);
                    },
                    List.of(),
                    ALLOWED);
            ToolBinder binder = broker.binder(chatId, "owner", "character", "backend");
            return candidates -> {
                toolBinds.incrementAndGet();
                assertTrue(BuiltinToolChatTest.names(candidates.keySet()).containsAll(ALLOWED));
                return binder.bind(candidates);
            };
        }

        SkillBinder skills(String chatId) {
            var contexts = testMock(ChatContextMapper.class);
            var backends = testMock(CharacterBackendMapper.class);
            var characters = testMock(CharacterInfoMapper.class);
            var service = testMock(SkillService.class);
            var embeddings = testMock(EmbeddingModelService.class);
            when(contexts.selectByPrimaryKey(chatId))
                    .thenReturn(Optional.of(new ChatContext()
                            .withChatId(chatId)
                            .withUserId("owner")
                            .withBackendId("backend")));
            when(backends.selectByPrimaryKey("backend"))
                    .thenReturn(Optional.of(new CharacterBackend().withCharacterUid("character")));
            when(characters.selectOne(any(org.mybatis.dynamic.sql.dsl.SelectDSLCompleter.class)))
                    .thenReturn(Optional.of(new fun.freechat.model.CharacterInfo()
                            .withUserId("creator")
                            .withLang("en")
                            .withVisibility("public")
                            .withExt("{\"skills\":{\"enabled\":[\"" + SKILL_ID + "\"]}}")));
            when(service.get("creator", SKILL_ID)).thenReturn(installed);
            when(service.list("creator"))
                    .thenReturn(List.of(new SkillService.Summary(
                            SKILL_ID, "revision", "storytelling", "Narrative guidance", Instant.EPOCH)));
            when(embeddings.tokenCountEstimatorForLang("en")).thenReturn(estimator);
            SkillBinder binder = new SkillRuntimeBinder(
                            contexts,
                            backends,
                            characters,
                            service,
                            embeddings,
                            broker,
                            new fun.freechat.service.skills.SkillCharacterAccess(
                                    testMock(fun.freechat.mapper.OrgRelationshipMapper.class)),
                            skillTokenLimit)
                    .binder(chatId, "owner", "character", "backend");
            return () -> {
                binds.incrementAndGet();
                return binder.bind();
            };
        }
    }

    private static void assertTurnFailure(Exception failure) {
        Throwable terminal = failure instanceof ExecutionException ? failure.getCause() : failure;
        assertInstanceOf(IllegalStateException.class, terminal, "A timeout is not a terminal turn failure");
        assertFalse(terminal.toString().contains(PRIVATE));
        assertNoSkillContent(terminal.toString());
        assertNull(terminal.getCause());
        assertEquals(0, terminal.getSuppressed().length);
    }

    static ChatResponse response(AiMessage ai) {
        return ChatResponse.builder().aiMessage(ai).tokenUsage(USAGE).build();
    }

    static ChatResponse albumResponse() {
        return response(AiMessage.from(
                call(UUID.randomUUID().toString(), "find_image", "{\"description\":\"current scene\"}")));
    }

    static ToolExecutionRequest call(String id, String name, String arguments) {
        return ToolExecutionRequest.builder()
                .id(id)
                .name(name)
                .arguments(arguments)
                .build();
    }

    private static List<ToolExecutionResultMessage> toolResults(ChatRequest request) {
        return request.messages().stream()
                .filter(ToolExecutionResultMessage.class::isInstance)
                .map(ToolExecutionResultMessage.class::cast)
                .toList();
    }

    private static JsonNode json(String text) throws Exception {
        return JSON.readTree(text);
    }

    static <T> T testMock(Class<T> type) {
        return mock(type, withSettings().mockMaker(MockMakers.SUBCLASS));
    }

    record Write(ChatMessage message, ChatMessage original, SystemMessage system, Origin origin, TokenUsage usage) {}

    static final class Provider implements StreamingChatModel {
        final List<ChatRequest> requests = new CopyOnWriteArrayList<>();
        final List<StreamingChatResponseHandler> handlers = new CopyOnWriteArrayList<>();
        final Deque<ChatResponse> script = new ArrayDeque<>();
        volatile boolean failStart;
        java.util.function.Consumer<ChatRequest> observe = ignored -> {};
        final ChatModel sync = new ChatModel() {
            @Override
            public ChatResponse chat(ChatRequest request) {
                requests.add(request);
                observe.accept(request);
                if (failStart) {
                    throw new IllegalArgumentException(PRIVATE);
                }
                assertFalse(script.isEmpty(), "Unexpected extra provider call");
                return script.removeFirst();
            }
        };

        @Override
        public void chat(ChatRequest request, StreamingChatResponseHandler handler) {
            requests.add(request);
            handlers.add(handler);
            observe.accept(request);
            if (failStart) {
                throw new IllegalArgumentException(PRIVATE);
            }
        }

        @Override
        public List<dev.langchain4j.model.chat.listener.ChatModelListener> listeners() {
            return List.of();
        }

        @Override
        public Set<dev.langchain4j.model.chat.Capability> supportedCapabilities() {
            return Set.of();
        }

        @Override
        public dev.langchain4j.model.ModelProvider provider() {
            return dev.langchain4j.model.ModelProvider.OTHER;
        }

        @Override
        public dev.langchain4j.model.chat.request.ChatRequestParameters defaultRequestParameters() {
            return dev.langchain4j.model.chat.request.DefaultChatRequestParameters.builder()
                    .build();
        }

        void complete(int index, ChatResponse response) {
            assertTrue(index < handlers.size(), "Pinned tool loop did not request the next provider call");
            handlers.get(index).onCompleteResponse(response);
        }
    }

    /** Shared by the real Redis IT to exercise cancellation through the actual service memory wrapper. */
    static final class Fixture implements AutoCloseable {
        final String id;
        final LongTermMemoryProperties properties = new LongTermMemoryProperties();
        final ChatServiceImpl service = new ChatServiceImpl();
        final ChatContextService contexts = testMock(ChatContextService.class);
        final ChatSessionService sessions = testMock(ChatSessionService.class);
        final CharacterService characters = testMock(CharacterService.class);
        final PromptService prompts = testMock(PromptService.class);
        final MemoryModelResolver models = testMock(MemoryModelResolver.class);
        final MemoryTurnRepository turns = testMock(MemoryTurnRepository.class);
        final ScheduledExecutorService scheduler = testMock(ScheduledExecutorService.class);
        final List<ScheduledFuture<?>> timers = new CopyOnWriteArrayList<>();
        final Provider provider = new Provider();
        final List<String> events = new CopyOnWriteArrayList<>();
        final List<Write> writes = new CopyOnWriteArrayList<>();
        final Map<String, ChatMemoryCommit> commits = new ConcurrentHashMap<>();
        final List<LongTermChatMemoryStore.Binding> bindings = new CopyOnWriteArrayList<>();
        final AtomicInteger aborts = new AtomicInteger();
        final AtomicInteger albumCalls = new AtomicInteger();
        final AtomicReference<Object> albumMemoryId = new AtomicReference<>();
        final EmbeddingStore<TextSegment> vectorStore = testMock(EmbeddingStore.class);
        final ExactEmbeddingStoreService stores = testMock(ExactEmbeddingStoreService.class);
        final MemoryDocumentCodec codec;
        final ChatContext context;
        final ChatTaskQueue queue;
        final ExecutorService ownedWorker;
        final ChatSession base;
        final ToolSpecification albumSpec;
        final MemoryDocument document;
        volatile Runnable timeout;
        volatile boolean failFinal;
        volatile boolean failAlbum;
        volatile Runnable beforeAbort = () -> {};

        Fixture() throws Exception {
            this(null);
        }

        Fixture(ChatTaskQueue externalQueue) throws Exception {
            this(externalQueue, null);
        }

        Fixture(ChatTaskQueue externalQueue, SkillSetup skills) throws Exception {
            id = externalQueue == null
                    ? UUID.randomUUID().toString().replace("-", "")
                    : (String) ReflectionTestUtils.getField(externalQueue, "chatId");
            context = new ChatContext().withChatId(id).withUserId("owner").withBackendId("backend");
            queue = externalQueue == null ? new ChatTaskQueue(id, new ReentrantLock()) : externalQueue;
            ownedWorker = externalQueue == null ? Executors.newSingleThreadExecutor() : null;
            ChatTaskQueueManager manager = testMock(ChatTaskQueueManager.class);
            when(manager.getOrCreateQueue(id)).thenReturn(queue);
            ReflectionTestUtils.setField(service, "queueManager", manager);
            ReflectionTestUtils.setField(service, "chatContextService", contexts);
            ReflectionTestUtils.setField(service, "chatSessionService", sessions);
            ReflectionTestUtils.setField(service, "promptService", prompts);
            when(contexts.get(id)).thenReturn(context);
            when(characters.getBackend("backend")).thenReturn(new CharacterBackend().withLongTermMemoryWindowSize(10));
            when(prompts.apply(anyString(), anyMap(), any())).thenReturn("Configured policy");
            when(prompts.apply(any(UserMessage.class), anyMap(), any())).thenAnswer(call -> {
                assertEquals(ORIGINAL.singleText(), ((Map<?, ?>) call.getArgument(1)).get("input"));
                return TRANSFORMED;
            });
            ChatPromptContent prompt = new ChatPromptContent();
            prompt.setSystem("policy template");
            prompt.setMessageToSend(UserMessage.from("input template"));
            albumSpec = ToolSpecifications.toolSpecificationFrom(
                    AlbumTool.class.getMethod("findAnImage", Object.class, String.class));
            base = ChatSession.builder()
                    .chatModel(provider.sync)
                    .streamingChatModel(provider)
                    .memoryFingerprint(FINGERPRINT)
                    .chatMemory(MessageWindowChatMemory.withMaxMessages(100))
                    .prompt(prompt)
                    .promptFormat(PromptFormat.MUSTACHE)
                    .variables(new HashMap<>(Map.of("base", "unchanged")))
                    .toolBinder(skills == null ? null : skills.tools(id))
                    .skillBinder(skills == null ? null : skills.skills(id))
                    .tools(Map.of(albumSpec, (request, memoryId) -> {
                        albumMemoryId.set(memoryId);
                        albumCalls.incrementAndGet();
                        events.add("album");
                        if (failAlbum) {
                            throw new IllegalArgumentException(PRIVATE);
                        }
                        return "https://example.invalid/current-image";
                    }))
                    .build();
            when(sessions.get(context)).thenReturn(base);

            var scope = new MemoryScope(id, "owner", "character", 1, EmbeddingStoreType.EN_LONG_TERM_MEMORY);
            ChatMemoryState state = new ChatMemoryState()
                    .withChatId(id)
                    .withUserId(scope.userId())
                    .withCharacterUid(scope.characterUid())
                    .withStoreType(scope.storeType().text())
                    .withGeneration(1L)
                    .withStatus("active")
                    .withFingerprint(FINGERPRINT)
                    .withVersion(0L)
                    .withLatestFinalizedId(0L)
                    .withOverflowThroughId(0L)
                    .withReconciledThroughId(0L);
            var resolved = new MemoryModelResolver.Resolved(
                    provider.sync,
                    "fake-model",
                    "en",
                    "",
                    "",
                    FINGERPRINT,
                    scope.characterUid(),
                    scope.userId(),
                    100,
                    10,
                    null);
            var configuration = new MemoryModelResolver.Configuration(
                    scope.userId(), scope.characterUid(), "en", FINGERPRINT, List.of());
            when(models.resolveForSession(id, FINGERPRINT))
                    .thenReturn(Optional.of(new MemoryModelResolver.Resolution(resolved, configuration)));
            when(turns.read(id)).thenReturn(Optional.of(state));
            when(turns.begin(eq(scope), eq(FINGERPRINT))).thenAnswer(call -> {
                var lease = new MemoryTurnRepository.TurnLease(
                        scope, UUID.randomUUID().toString(), LocalDateTime.now().plusMinutes(1), 1);
                state.setTurnToken(lease.token());
                return lease;
            });
            doAnswer(call -> {
                        MemoryTurnRepository.TurnLease lease = call.getArgument(0);
                        assertEquals(lease.token(), state.getTurnToken(), "Closed turn must not perform further IO");
                        return null;
                    })
                    .when(turns)
                    .check(any());
            when(turns.remainingMillis(any())).thenReturn(60_000L);
            doAnswer(call -> {
                        beforeAbort.run();
                        state.setTurnToken(null);
                        aborts.incrementAndGet();
                        events.add("abort");
                        return null;
                    })
                    .when(turns)
                    .abort(any());
            when(turns.append(any(), any(), any(), any(), any(), any())).thenAnswer(call -> {
                ChatMessage message = call.getArgument(1);
                if (message instanceof AiMessage) {
                    assertEquals("usage", events.getLast());
                }
                writes.add(new Write(
                        message, call.getArgument(2), call.getArgument(3), call.getArgument(4), call.getArgument(5)));
                events.add(
                        message instanceof UserMessage
                                ? "input"
                                : message instanceof AiMessage ? "tool-message" : "tool-result");
                return (long) writes.size();
            });
            when(turns.complete(any(), any(), any(), any())).thenAnswer(call -> {
                assertEquals("usage", events.getLast(), "Returned usage must be recorded before final persistence");
                if (failFinal) {
                    throw new IllegalArgumentException(PRIVATE);
                }
                writes.add(new Write(
                        call.getArgument(1), null, call.getArgument(2), Origin.ASSISTANT_OUTPUT, call.getArgument(3)));
                state.setTurnToken(null);
                events.add("complete");
                return 99L;
            });
            doAnswer(call -> {
                        timeout = call.getArgument(0);
                        ScheduledFuture<?> timer = testMock(ScheduledFuture.class);
                        timers.add(timer);
                        return timer;
                    })
                    .when(scheduler)
                    .schedule(any(Runnable.class), anyLong(), any());
            doAnswer(call -> {
                        ScheduledFuture<?> timer = testMock(ScheduledFuture.class);
                        timers.add(timer);
                        return timer;
                    })
                    .when(scheduler)
                    .scheduleWithFixedDelay(any(Runnable.class), anyLong(), anyLong(), any());

            // Same narrow mapper/transaction boundary pattern as MemoryStreamingTest; all memory machinery is real.
            ChatMemoryCoordinationMapper coordination = testMock(ChatMemoryCoordinationMapper.class);
            ChatMemoryStateMapper states = testMock(ChatMemoryStateMapper.class);
            ChatMemoryCommitMapper commitMapper = testMock(ChatMemoryCommitMapper.class);
            ChatHistoryMapper histories = testMock(ChatHistoryMapper.class);
            ChatContextMapper contextMapper = testMock(ChatContextMapper.class);
            PlatformTransactionManager transactions = testMock(PlatformTransactionManager.class);
            when(transactions.getTransaction(any())).thenAnswer(call -> new SimpleTransactionStatus());
            when(coordination.databaseNow()).thenReturn(LocalDateTime.now());
            when(coordination.lock(id)).thenReturn(Optional.of(state));
            when(coordination.retainControl(any())).thenAnswer(call -> {
                ChatMemoryCommit row = call.getArgument(0);
                return commits.putIfAbsent(row.getAttemptId(), row) == null ? 1 : 0;
            });
            when(coordination.scopeProgress(anyString()))
                    .thenAnswer(call -> Optional.ofNullable(commits.get(call.getArgument(0)))
                            .filter(row -> "SCOPE".equals(row.getOperation()) && "control".equals(row.getStatus()))
                            .map(ChatMemoryCommit::getProgress));
            when(contextMapper.selectByPrimaryKey(id)).thenReturn(Optional.of(context));
            when(states.updateByPrimaryKey(any())).thenReturn(1);
            when(commitMapper.selectByPrimaryKey(any()))
                    .thenAnswer(call -> Optional.ofNullable(commits.get(call.getArgument(0))));
            when(commitMapper.insertSelective(any())).thenAnswer(call -> {
                ChatMemoryCommit row = call.getArgument(0);
                assertNull(commits.putIfAbsent(row.getAttemptId(), row));
                if ("CHAT_USAGE".equals(row.getOperation())) {
                    events.add("usage");
                }
                return 1;
            });
            var publications = new MemoryPublicationRepository(
                    coordination, states, commitMapper, histories, transactions, properties);
            var lifecycle = new MemoryLifecycleRepository(
                    coordination, states, histories, contextMapper, transactions, properties);
            var reconciler = new MemoryHistoryReconciler(
                    coordination, states, histories, commitMapper, transactions, properties);
            TokenCountEstimator estimator = skills == null ? testMock(TokenCountEstimator.class) : skills.estimator;
            if (skills == null) {
                when(estimator.estimateTokenCountInText(anyString()))
                        .thenAnswer(call -> Math.max(1, ((String) call.getArgument(0)).length() / 4));
            }
            MemoryBounds bounds = new MemoryBounds(estimator, properties);
            MemoryBoundsFactory boundsFactory = testMock(MemoryBoundsFactory.class);
            when(boundsFactory.forLanguage(anyString())).thenReturn(bounds);
            EmbeddingModelService embeddings = testMock(EmbeddingModelService.class);
            EmbeddingModel embedding = testMock(EmbeddingModel.class);
            when(embeddings.modelForLang("en")).thenReturn(embedding);
            when(embeddings.queryPrefixForLang("en")).thenReturn("query:");
            when(embedding.embed(anyString())).thenReturn(Response.from(Embedding.from(new float[] {1, 0})));
            when(stores.of(id, scope.storeType())).thenReturn(vectorStore);
            var vectors = new MemoryVectorRepository(stores, embeddings, boundsFactory, properties);
            var publisher = new MemoryPublisher(publications, vectors);
            document = new MemoryDocument(
                    UUID.randomUUID().toString(),
                    scope,
                    UUID.randomUUID().toString(),
                    MemoryDocument.Kind.EPISODE_SUMMARY,
                    false,
                    1,
                    2,
                    Instant.parse("2026-09-01T00:00:00Z"),
                    FINGERPRINT,
                    "verified historical detail ".repeat(15),
                    List.of(),
                    List.of());
            codec = vectors.codec(scope);
            publish(document);
            when(vectorStore.search(any()))
                    .thenReturn(new EmbeddingSearchResult<>(List.of(
                            new EmbeddingMatch<>(0.99, document.id(), null, TextSegment.from("untrusted-ann-body")))));
            var actualStore = new LongTermChatMemoryStoreImpl(
                    contexts,
                    characters,
                    prompts,
                    models,
                    lifecycle,
                    reconciler,
                    turns,
                    new MemorySourceReader(turns),
                    publications,
                    publisher,
                    vectors,
                    null,
                    boundsFactory,
                    properties,
                    scheduler);
            ReflectionTestUtils.setField(service, "longTermChatMemoryStore", new LongTermChatMemoryStore() {
                public boolean enabled(String chatId) {
                    return actualStore.enabled(chatId);
                }

                public Optional<Binding> open(String chatId, ChatSession session) {
                    Optional<Binding> binding = actualStore.open(chatId, session);
                    binding.ifPresent(bindings::add);
                    return binding;
                }
            });
            if (ownedWorker != null) {
                queue.startWorker(ownedWorker);
            }
        }

        void publish(MemoryDocument value) {
            TextSegment segment = codec.encode(value);
            var manifest = MemoryManifest.of(List.of(value), codec);
            commits.put(
                    value.commitId(),
                    new ChatMemoryCommit()
                            .withAttemptId(value.commitId())
                            .withChatId(id)
                            .withGeneration(value.scope().generation())
                            .withOperation("IDLE")
                            .withStatus("committed")
                            .withFingerprint(FINGERPRINT)
                            .withSchemaVersion(MemoryScope.SCHEMA_VERSION)
                            .withManifest(MemoryDocumentCodec.encodeManifest(manifest)));
            when(stores.get(eq(value.scope().storeType()), eq(value.id()), any()))
                    .thenAnswer(call -> {
                        assertTrue(((dev.langchain4j.store.embedding.filter.Filter) call.getArgument(2))
                                .test(segment.metadata()));
                        return Optional.of(segment);
                    });
        }

        LongTermChatMemoryStore.Binding binding() {
            return bindings.getLast();
        }

        List<ChatMemoryCommit> usageRows() {
            return commits.values().stream()
                    .filter(row -> "CHAT_USAGE".equals(row.getOperation()))
                    .toList();
        }

        ChatResponse run(boolean streaming, List<ChatResponse> script) throws Exception {
            if (!streaming) {
                provider.script.addAll(script);
                var result = service.send(id, ORIGINAL, null);
                idle();
                return result.getLeft();
            }
            CompletableFuture<ChatResponse> terminal = new CompletableFuture<>();
            int first = provider.handlers.size();
            service.streamSend(id, ORIGINAL, null)
                    .onCompleteResponse(terminal::complete)
                    .onError(terminal::completeExceptionally)
                    .start();
            for (int index = 0; index < script.size() && !terminal.isDone(); index++) {
                provider.complete(first + index, script.get(index));
            }
            ChatResponse result = terminal.get(5, TimeUnit.SECONDS);
            idle();
            return result;
        }

        void idle() throws Exception {
            assertEquals(
                    "successor",
                    queue.submit(new ChatTask.Sync<>(() -> "successor"))
                            .future()
                            .get(5, TimeUnit.SECONDS));
        }

        void assertTimersDisposed() {
            assertFalse(timers.isEmpty());
            timers.forEach(timer -> verify(timer, atLeastOnce()).cancel(false));
            verify(scheduler, never()).shutdown();
        }

        public void close() throws Exception {
            bindings.forEach(LongTermChatMemoryStore.Binding::close);
            if (ownedWorker != null) {
                queue.drain(0);
                ownedWorker.shutdownNow();
                assertTrue(ownedWorker.awaitTermination(5, TimeUnit.SECONDS));
            }
        }
    }
}
