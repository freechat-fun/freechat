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
import java.time.Instant;
import java.time.LocalDateTime;
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

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void albumAndScopedRecallSurviveRealRegistrationAndContinuations(boolean streaming) throws Exception {
        try (Fixture f = new Fixture()) {
            ChatResponse tools = response(AiMessage.from(
                    call("album", "findAnImage", "{\"description\":\"current scene\"}"),
                    call("search", "searchMemory", "{\"query\":\"prior event\"}")));
            ChatResponse read = response(AiMessage.from(
                    call("read", "readMemory", "{\"recordId\":\"" + f.document.id() + "\",\"offset\":256}")));
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
                    List.of("findAnImage", "searchMemory", "readMemory"),
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
                        Set.of("findAnImage", "searchMemory", "readMemory"),
                        request.toolSpecifications().stream()
                                .map(ToolSpecification::name)
                                .collect(java.util.stream.Collectors.toSet()));
                assertEquals(3, request.toolSpecifications().size(), "No duplicated registrations");
                assertTrue(request.toolSpecifications().contains(f.albumSpec), "Localized album schema must survive");
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
                            response(AiMessage.from(call("search", "searchMemory", "{\"query\":\"prior event\"}"))),
                            response(AiMessage.from(
                                    call("read", "readMemory", "{\"recordId\":\"" + f.document.id() + "\"}"))),
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
                        AiMessage.from(call("search-" + invocation, "searchMemory", "{\"query\":\"prior event\"}"))));
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

    private static void assertTurnFailure(Exception failure) {
        Throwable terminal = failure instanceof ExecutionException ? failure.getCause() : failure;
        assertInstanceOf(IllegalStateException.class, terminal, "A timeout is not a terminal turn failure");
        assertFalse(terminal.toString().contains(PRIVATE));
        assertNull(terminal.getCause());
        assertEquals(0, terminal.getSuppressed().length);
    }

    static ChatResponse response(AiMessage ai) {
        return ChatResponse.builder().aiMessage(ai).tokenUsage(USAGE).build();
    }

    static ChatResponse albumResponse() {
        return response(AiMessage.from(
                call(UUID.randomUUID().toString(), "findAnImage", "{\"description\":\"current scene\"}")));
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
        final ChatModel sync = new ChatModel() {
            @Override
            public ChatResponse chat(ChatRequest request) {
                requests.add(request);
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
            ToolSpecification originalSpec = ToolSpecifications.toolSpecificationFrom(
                    AlbumTool.class.getMethod("findAnImage", Object.class, String.class));
            albumSpec = ToolSpecification.builder()
                    .name(originalSpec.name())
                    .parameters(originalSpec.parameters())
                    .metadata(originalSpec.metadata())
                    .description("Localized album description")
                    .build();
            base = ChatSession.builder()
                    .chatModel(provider.sync)
                    .streamingChatModel(provider)
                    .memoryFingerprint(FINGERPRINT)
                    .chatMemory(MessageWindowChatMemory.withMaxMessages(100))
                    .prompt(prompt)
                    .promptFormat(PromptFormat.MUSTACHE)
                    .variables(new HashMap<>(Map.of("base", "unchanged")))
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
            TokenCountEstimator estimator = testMock(TokenCountEstimator.class);
            when(estimator.estimateTokenCountInText(anyString()))
                    .thenAnswer(call -> Math.max(1, ((String) call.getArgument(0)).length() / 4));
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
            service.streamSend(id, ORIGINAL, null)
                    .onCompleteResponse(terminal::complete)
                    .onError(terminal::completeExceptionally)
                    .start();
            for (int index = 0; index < script.size() && !terminal.isDone(); index++) {
                provider.complete(index, script.get(index));
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
