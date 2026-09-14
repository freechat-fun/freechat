package fun.freechat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.data.message.*;
import dev.langchain4j.exception.ToolArgumentsException;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.ModelProvider;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.request.ChatRequestParameters;
import dev.langchain4j.model.chat.request.DefaultChatRequestParameters;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.output.FinishReason;
import dev.langchain4j.model.output.TokenUsage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.tool.ToolExecutor;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import dev.langchain4j.store.memory.chat.InMemoryChatMemoryStore;
import fun.freechat.langchain4j.memory.chat.SystemAlwaysOnTopMessageWindowChatMemory;
import fun.freechat.langchain4j.memory.chat.TokenUsageChatMemoryStore;
import fun.freechat.model.CharacterBackend;
import fun.freechat.model.CharacterInfo;
import fun.freechat.model.ChatContext;
import fun.freechat.service.character.CharacterService;
import fun.freechat.service.chat.*;
import fun.freechat.service.chat.impl.ChatServiceImpl;
import fun.freechat.service.chat.impl.ChatTaskQueueManager;
import fun.freechat.service.enums.ChatVar;
import fun.freechat.service.enums.PromptFormat;
import fun.freechat.service.prompt.ChatPromptContent;
import fun.freechat.service.prompt.PromptService;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockMakers;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;

/** Offline contract tests: real AiServices, ChatSession and memory; only external boundaries are fakes. */
@Timeout(10)
class ChatExecutionTest {
    private static final String STORE_ID = "prepared-history";
    private static final String TRIGGER = "Continue the prepared conversation";
    private static final TokenUsage FIRST_USAGE = new TokenUsage(7, 3);
    private static final TokenUsage SECOND_USAGE = new TokenUsage(11, 5);
    private static final TokenUsage FINAL_USAGE = new TokenUsage(13, 6);
    private static final TokenUsage TOTAL_USAGE = new TokenUsage(31, 14);
    private static final SystemMessage POLICY =
            SystemMessage.from("Prepared policy: {{literal}} / {{}} / ${untouched}");
    private static final ToolSpecification LOOKUP = specification("lookup");
    private static final ToolSpecification FETCH = specification("fetch");
    private static final ChatRequestParameters DEFAULTS = DefaultChatRequestParameters.builder()
            .modelName("offline-model")
            .temperature(0.25)
            .topP(0.8)
            .topK(12)
            .frequencyPenalty(0.2)
            .presencePenalty(0.3)
            .maxOutputTokens(123)
            .stopSequences("literal {{stop}}")
            .build();

    @ParameterizedTest
    @CsvSource({"false, false", "true, false", "false, true", "true, true"})
    void preparedMultimodalMessagesRemainExactWithOrWithoutNewUserInput(boolean streaming, boolean continuation) {
        Fixture f = new Fixture(false, continuation);
        ChatResponse answer = answer("final", "Unchanged answer {{}} {{name}}", FINAL_USAGE);

        ChatResponse result = f.run(streaming, List.of(answer));

        assertEquals(1, f.provider.requests.size());
        assertEquals(f.prepared, f.provider.requests.getFirst().messages());
        assertEquals(append(f.prepared, answer.aiMessage()), f.memory.messages());
        assertEquals(answer, result, "Final response metadata must not be reconstructed from text alone");
        assertEquals(1, f.store.snapshots.size(), "No framework invocation message may be persisted, even transiently");
        assertEquals(Map.of("literal", "MUST NOT SUBSTITUTE"), f.session.getVariables());
        if (!streaming) {
            assertNull(f.lastReply.getRight(), "Ordinary ChatMemory has no durable message ID");
            assertEquals(new MemoryUsage(1L, FINAL_USAGE), f.session.getMemoryUsage());
            assertEquals(1, f.moderations.get());
        }
        f.assertNoSyntheticInput();
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void multipleToolsAndRoundsUseOriginalMemoryIdAndRebuildEveryRequest(boolean streaming) {
        Fixture f = new Fixture(true, true);
        f.session.setMemoryUsage(new MemoryUsage(4L, new TokenUsage(20, 10)));
        ToolExecutionRequest first = call("one", "lookup");
        ToolExecutionRequest second = call("two", "fetch");
        ToolExecutionRequest third = call("three", "lookup");
        ChatResponse roundOne = tools("round-one", FIRST_USAGE, first, second);
        ChatResponse roundTwo = tools("round-two", SECOND_USAGE, third);
        ChatResponse finalAnswer = answer("final-id", "final {{unchanged}}", FINAL_USAGE);
        List<ChatMessage> afterFirst = append(f.prepared, roundOne.aiMessage(), toolResult(first), toolResult(second));
        List<ChatMessage> afterSecond = append(afterFirst, roundTwo.aiMessage(), toolResult(third));

        ChatResponse result = f.run(streaming, List.of(roundOne, roundTwo, finalAnswer));

        assertEquals(
                List.of(f.prepared, afterFirst, afterSecond),
                f.provider.requests.stream().map(ChatRequest::messages).toList());
        assertEquals(append(afterSecond, finalAnswer.aiMessage()), f.memory.messages());
        assertEquals(List.of(first, second, third), f.executed);
        assertEquals(3, f.toolMemoryIds.size());
        f.toolMemoryIds.forEach(
                id -> assertSame(f.memoryId, id, "Do not substitute memory.id() or stringify @MemoryId"));
        assertNotEquals(f.memory.id(), f.memoryId);
        assertEquals(TOTAL_USAGE, result.tokenUsage());
        assertEquals(finalAnswer.aiMessage(), result.aiMessage());
        assertEquals("final-id", result.id());
        assertEquals("offline-model", result.modelName());
        assertEquals(FinishReason.LENGTH, result.finishReason(), "Preserve the final provider's finish reason");
        f.provider.requests.forEach(request -> {
            assertEquals(Set.of(LOOKUP, FETCH), new HashSet<>(request.toolSpecifications()));
            assertEquals(2, request.toolSpecifications().size());
            assertDefaults(request.parameters());
        });
        if (!streaming) {
            assertEquals(
                    List.of(FIRST_USAGE, SECOND_USAGE, FINAL_USAGE),
                    f.store.answers.stream().map(StoredAnswer::usage).toList());
            assertEquals(
                    List.of(roundOne.aiMessage(), roundTwo.aiMessage(), finalAnswer.aiMessage()),
                    f.store.answers.stream().map(StoredAnswer::message).toList());
            f.store.answers.forEach(write -> assertEquals(STORE_ID, write.memoryId()));
            assertEquals(103L, f.lastReply.getRight(), "Return the final persisted AI ID, not a tool round's ID");
            assertEquals(new MemoryUsage(7L, new TokenUsage(51, 24)), f.session.getMemoryUsage());
            assertEquals(1, f.moderations.get());
            assertEquals(
                    List.of(
                            "provider:0",
                            "moderation",
                            "write:AI",
                            "tool:one",
                            "tool:two",
                            "write:TOOL_EXECUTION_RESULT",
                            "write:TOOL_EXECUTION_RESULT",
                            "provider:1",
                            "write:AI",
                            "tool:three",
                            "write:TOOL_EXECUTION_RESULT",
                            "provider:2",
                            "write:AI"),
                    f.events);
        } else {
            assertEquals(List.of(roundOne, roundTwo), f.terminal.intermediate);
            assertEquals(List.of("partial {{}}"), f.terminal.partial);
            assertEquals(1, f.terminal.completions.get());
            assertEquals(0, f.terminal.errors.get());
        }
        f.assertNoSyntheticInput();
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void exactlyConfiguredToolRoundsStillAllowFinalResponse(boolean streaming) {
        Fixture f = new Fixture(true, false);
        f.limit(2);
        ChatResponse first = tools("first", FIRST_USAGE, call("one", "lookup"), call("two", "fetch"));
        ChatResponse second = tools("second", SECOND_USAGE, call("three", "fetch"));
        ChatResponse last = answer("last", "after exactly two rounds", FINAL_USAGE);

        ChatResponse result = f.run(streaming, List.of(first, second, last));

        assertEquals(3, f.provider.requests.size(), "The final model call is not an additional tool round");
        assertEquals(3, f.executed.size(), "Budget counts rounds, not individual tool calls");
        assertEquals(TOTAL_USAGE, result.tokenUsage());
        assertEquals(last.aiMessage(), f.memory.messages().getLast());
        assertEquals(FinishReason.LENGTH, result.finishReason());
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void nextToolRoundIsRejectedBeforeAnyOfItsMessagesOrToolsArePersistedOrExecuted(boolean streaming) {
        Fixture f = new Fixture(true, false);
        f.limit(2);
        ToolExecutionRequest one = call("one", "lookup");
        ToolExecutionRequest two = call("two", "fetch");
        ChatResponse first = tools("first", FIRST_USAGE, one);
        ChatResponse second = tools("second", SECOND_USAGE, two);
        ChatResponse excess =
                tools("excess", FINAL_USAGE, call("forbidden-one", "lookup"), call("forbidden-two", "fetch"));

        RuntimeException failure =
                assertThrows(RuntimeException.class, () -> f.run(streaming, List.of(first, second, excess)));

        assertTrue(rootCause(failure).getMessage().toLowerCase(Locale.ROOT).contains("round"));
        assertEquals(3, f.provider.requests.size());
        assertEquals(List.of(one, two), f.executed);
        List<ChatMessage> permitted =
                append(f.prepared, first.aiMessage(), toolResult(one), second.aiMessage(), toolResult(two));
        assertEquals(permitted, f.memory.messages(), "A rejected tool request must never enter real history");
        assertTrue(f.store.snapshots.stream().noneMatch(messages -> messages.contains(excess.aiMessage())));
        if (!streaming) {
            assertEquals(
                    List.of(FIRST_USAGE, SECOND_USAGE),
                    f.store.answers.stream().map(StoredAnswer::usage).toList());
            assertEquals(new MemoryUsage(2L, new TokenUsage(18, 8)), f.session.getMemoryUsage());
            assertEquals(1, f.moderations.get());
        }
        f.assertNoSyntheticInput();
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void moderationFailureBlocksFirstAiWriteAndToolExecution(boolean toolResponse) {
        Fixture f = new Fixture(true, false);
        IllegalStateException denied = new IllegalStateException("moderation denied");
        ChatResponse response = toolResponse
                ? tools("tools", FIRST_USAGE, call("blocked", "lookup"))
                : answer("answer", "blocked answer", FINAL_USAGE);
        f.provider.script.add(response);

        RuntimeException failure = assertThrows(
                RuntimeException.class,
                () -> new ChatExecution(f.session, f.memoryId).send(null, () -> {
                    f.verifyModeration();
                    throw denied;
                }));

        assertSame(denied, rootCause(failure));
        assertEquals(List.of("provider:0", "moderation"), f.events);
        assertEquals(1, f.moderations.get());
        assertEquals(f.prepared, f.memory.messages());
        assertTrue(f.store.snapshots.isEmpty());
        assertTrue(f.store.answers.isEmpty());
        assertTrue(f.executed.isEmpty());
        assertNull(f.session.getMemoryUsage());
        f.assertNoSyntheticInput();
    }

    @ParameterizedTest
    @CsvSource({"false, false", "true, false", "false, true", "true, true"})
    void providerFailurePropagatesWithoutInventingAnAnswer(boolean streaming, boolean afterToolRound) {
        Fixture f = new Fixture(true, false);
        IllegalArgumentException unavailable = new IllegalArgumentException("offline provider failure");
        f.provider.failure = unavailable;
        f.provider.failureAt = afterToolRound ? 1 : 0;
        ToolExecutionRequest tool = call("allowed", "lookup");
        ChatResponse first = tools("first", FIRST_USAGE, tool);

        RuntimeException failure = assertThrows(
                RuntimeException.class,
                () -> f.run(streaming, List.of(first, answer("never", "must not appear", FINAL_USAGE))));

        assertSame(unavailable, rootCause(failure));
        assertEquals(afterToolRound ? 2 : 1, f.provider.requests.size());
        assertEquals(afterToolRound ? List.of(tool) : List.of(), f.executed);
        assertEquals(
                afterToolRound ? append(f.prepared, first.aiMessage(), toolResult(tool)) : f.prepared,
                f.memory.messages());
        if (!streaming) {
            assertEquals(afterToolRound ? 1 : 0, f.moderations.get());
            assertEquals(
                    afterToolRound ? List.of(FIRST_USAGE) : List.of(),
                    f.store.answers.stream().map(StoredAnswer::usage).toList());
            assertEquals(afterToolRound ? new MemoryUsage(1L, FIRST_USAGE) : null, f.session.getMemoryUsage());
        }
        f.assertNoSyntheticInput();
    }

    @Test
    void asynchronousProviderErrorReachesStreamErrorCallbackWithoutWritingMemory() {
        Fixture f = new Fixture(false, true);
        Terminal terminal = new Terminal();
        terminal.start(new ChatExecution(f.session, f.memoryId).stream());
        IllegalArgumentException unavailable = new IllegalArgumentException("asynchronous provider failure");

        f.provider.handlers.getFirst().onError(unavailable);

        assertSame(unavailable, rootCause(assertThrows(RuntimeException.class, terminal::result)));
        assertEquals(1, terminal.errors.get());
        assertEquals(0, terminal.completions.get());
        assertEquals(f.prepared, f.memory.messages());
        assertTrue(f.store.snapshots.isEmpty());
        assertTrue(f.executed.isEmpty());
    }

    @ParameterizedTest
    @CsvSource({"false, false", "true, false", "false, true", "true, true"})
    void configuredToolFailureHandlersPropagateWithoutToolResultOrFollowup(
            boolean streaming, boolean invalidArguments) {
        Fixture f = new Fixture(true, false);
        RuntimeException toolFailure = invalidArguments
                ? new ToolArgumentsException("invalid offline arguments")
                : new IllegalArgumentException("offline tool failure");
        IllegalStateException rejected = new IllegalStateException("configured tool handler rejected", toolFailure);
        AtomicInteger argumentsErrors = new AtomicInteger();
        AtomicInteger executionErrors = new AtomicInteger();
        f.toolFailure = toolFailure;
        f.session.getAiServiceContext().toolService.argumentsErrorHandler((error, context) -> {
            argumentsErrors.incrementAndGet();
            assertSame(rootCause(toolFailure), rootCause(error));
            throw rejected;
        });
        f.session.getAiServiceContext().toolService.executionErrorHandler((error, context) -> {
            executionErrors.incrementAndGet();
            assertSame(rootCause(toolFailure), rootCause(error));
            throw rejected;
        });
        ToolExecutionRequest tool = call("failure", "lookup");
        ChatResponse response = tools("failed-tool", FIRST_USAGE, tool);

        RuntimeException failure = assertThrows(RuntimeException.class, () -> f.run(streaming, List.of(response)));

        assertSame(rootCause(toolFailure), rootCause(failure));
        assertEquals(invalidArguments ? 1 : 0, argumentsErrors.get());
        assertEquals(invalidArguments ? 0 : 1, executionErrors.get());
        assertEquals(1, f.provider.requests.size());
        assertEquals(List.of(tool), f.executed);
        assertSame(f.memoryId, f.toolMemoryIds.getFirst());
        assertEquals(append(f.prepared, response.aiMessage()), f.memory.messages());
        assertEquals(1, f.store.snapshots.size());
        if (!streaming) {
            assertEquals(
                    List.of(FIRST_USAGE),
                    f.store.answers.stream().map(StoredAnswer::usage).toList());
            assertEquals(new MemoryUsage(1L, FIRST_USAGE), f.session.getMemoryUsage());
            assertEquals(List.of("provider:0", "moderation", "write:AI", "tool:failure"), f.events);
        }
    }

    @ParameterizedTest
    @CsvSource({"false, false", "true, false", "false, true", "true, true"})
    void defaultToolFailuresAreSanitizedWithoutLogsPersistenceOrProviderFollowup(
            boolean streaming, boolean invalidArguments) {
        Fixture f = new Fixture(true, false);
        String privateDetail = "private tool failure sentinel";
        f.toolFailure = invalidArguments
                ? new ToolArgumentsException(privateDetail)
                : new IllegalArgumentException(privateDetail);
        ChatResponse response = tools("failed-tool", FIRST_USAGE, call("failure", "lookup"));
        Logger logger = (Logger) LoggerFactory.getLogger("dev.langchain4j.service.tool.ToolService");
        Level previousLevel = logger.getLevel();
        boolean previousAdditive = logger.isAdditive();
        ListAppender<ILoggingEvent> logs = new ListAppender<>();
        logs.setContext(logger.getLoggerContext());
        logs.start();
        logger.addAppender(logs);
        logger.setLevel(Level.WARN);
        logger.setAdditive(false);
        try {
            Throwable failure =
                    rootCause(assertThrows(RuntimeException.class, () -> f.run(streaming, List.of(response))));
            assertInstanceOf(IllegalStateException.class, failure);
            assertEquals(
                    invalidArguments ? "Invalid chat tool arguments" : "Chat tool execution failed",
                    failure.getMessage());
            assertNull(failure.getCause());
            assertEquals(0, failure.getSuppressed().length);
            assertTrue(logs.list.isEmpty(), "Framework tool failures must not be logged with private details");
            assertEquals(1, f.provider.requests.size());
            assertEquals(1, f.executed.size());
            assertEquals(append(f.prepared, response.aiMessage()), f.memory.messages());
            assertEquals(1, f.store.snapshots.size());
            assertFalse(
                    ChatMessageSerializer.messagesToJson(f.memory.messages()).contains(privateDetail));
        } finally {
            logger.detachAppender(logs);
            logger.setLevel(previousLevel);
            logger.setAdditive(previousAdditive);
            logs.stop();
        }
    }

    @Test
    void freshExecutionsContinueTheSameSessionWithoutAddingInputOrReusingPendingUsage() {
        Fixture f = new Fixture(true, true);
        ChatResponse first = answer("first", "first continuation", FIRST_USAGE);
        ChatResponse second = answer("second", "second continuation", SECOND_USAGE);
        f.provider.script.addAll(List.of(first, second));

        Pair<ChatResponse, Long> one = new ChatExecution(f.session, f.memoryId).send(null, f::verifyModeration);
        // A second invocation has its own moderation and accounting state, but still no new user input.
        Pair<ChatResponse, Long> two =
                new ChatExecution(f.session, f.memoryId).send(null, () -> f.moderations.incrementAndGet());

        assertEquals(101L, one.getRight());
        assertEquals(102L, two.getRight());
        assertEquals(FIRST_USAGE, one.getLeft().tokenUsage());
        assertEquals(SECOND_USAGE, two.getLeft().tokenUsage());
        assertEquals(
                List.of(f.prepared, append(f.prepared, first.aiMessage())),
                f.provider.requests.stream().map(ChatRequest::messages).toList());
        assertEquals(append(f.prepared, first.aiMessage(), second.aiMessage()), f.memory.messages());
        assertEquals(new MemoryUsage(2L, new TokenUsage(18, 8)), f.session.getMemoryUsage());
        assertEquals(2, f.moderations.get());
        f.assertNoSyntheticInput();
    }

    @Test
    void windowWithoutTokenUsageStoreStillPersistsAndAccountsFinalAnswer() {
        Provider provider = new Provider(new ArrayList<>());
        ChatMemory memory = SystemAlwaysOnTopMessageWindowChatMemory.builder()
                .id(STORE_ID)
                .maxMessages(100)
                .chatMemoryStore(new InMemoryChatMemoryStore())
                .build();
        prepared(false).forEach(memory::add);
        ChatSession session = ChatSession.builder()
                .chatModel(provider.sync)
                .chatMemory(memory)
                .build();
        ChatResponse answer = answer("answer", "plain store answer", FINAL_USAGE);
        provider.script.add(answer);

        var result = new ChatExecution(session, "original-id").send(null, () -> {});

        assertEquals(answer, result.getLeft());
        assertNull(result.getRight());
        assertEquals(append(prepared(false), answer.aiMessage()), memory.messages());
        assertEquals(new MemoryUsage(1L, FINAL_USAGE), session.getMemoryUsage());
    }

    @ParameterizedTest
    @CsvSource({"false, false", "true, false", "false, true", "true, true"})
    void assistantServiceUsesTemporaryReversedHistoryWithoutNewInputOrTouchingBaseOrLtm(
            boolean streaming, boolean greeting) {
        String chatId = "base-chat";
        String assistantId = chatId + "-assist";
        ChatServiceImpl service = new ChatServiceImpl();
        ChatSessionService sessions = testMock(ChatSessionService.class);
        CharacterService characters = testMock(CharacterService.class);
        PromptService prompts = testMock(PromptService.class);
        LongTermChatMemoryStore ltm = testMock(LongTermChatMemoryStore.class);
        ChatMemoryService durable = testMock(ChatMemoryService.class);
        ChatTaskQueue queue = testMock(ChatTaskQueue.class);
        ChatTaskQueueManager queues = testMock(ChatTaskQueueManager.class);
        when(queues.getOrCreateQueue(chatId)).thenReturn(queue);
        // Execute admission inline; streaming completion is explicitly driven below, never a sleeping worker.
        when(queue.submit(any())).thenAnswer(invocation -> admitInline(invocation.getArgument(0)));
        ReflectionTestUtils.setField(service, "queueManager", queues);
        ReflectionTestUtils.setField(service, "chatSessionService", sessions);
        ReflectionTestUtils.setField(service, "characterService", characters);
        ReflectionTestUtils.setField(service, "promptService", prompts);
        ReflectionTestUtils.setField(service, "longTermChatMemoryStore", ltm);
        ReflectionTestUtils.setField(service, "chatMemoryService", durable);
        when(characters.getLatestIdByUid("assistant")).thenReturn(9L);
        when(characters.summary(9L)).thenReturn(new CharacterInfo());
        when(characters.getDefaultBackend("assistant"))
                .thenReturn(new CharacterBackend().withBackendId("assistant-backend"));
        when(prompts.apply(anyString(), anyMap(), any())).thenReturn("Assistant policy {{literal}} {{}}");

        Map<String, Object> baseVariables = new HashMap<>(Map.of(
                ChatVar.USER_NICKNAME.text(), "Human",
                ChatVar.CHARACTER_NICKNAME.text(), "Character",
                ChatVar.CHARACTER_DESCRIPTION.text(), "Character profile",
                ChatVar.CHAT_CONTEXT.text(), "Base scene",
                ChatVar.CHARACTER_GREETING.text(), greeting ? "greeting {{}}" : ""));
        Map<String, Object> originalVariables = Map.copyOf(baseVariables);
        ToolExecutionRequest oldTool = call("historical", "lookup");
        List<ChatMessage> original = List.of(
                POLICY,
                UserMessage.from("leading human {{}}"),
                AiMessage.from("character {{first}}"),
                UserMessage.from("human {{reply}}"),
                AiMessage.from(oldTool),
                toolResult(oldTool),
                AiMessage.from("character {{last}}"),
                UserMessage.from("human {{last}}"));
        ChatMemory baseMemory = MessageWindowChatMemory.withMaxMessages(100);
        original.forEach(baseMemory::add);
        MemoryUsage originalUsage = new MemoryUsage(8L, new TokenUsage(40, 20));
        ChatSession base = ChatSession.builder()
                .chatMemory(baseMemory)
                .variables(baseVariables)
                .memoryUsage(originalUsage)
                .build();
        when(sessions.get(chatId)).thenReturn(base);
        Provider provider = new Provider(new ArrayList<>());
        AtomicReference<ChatSession> temporary = new AtomicReference<>();
        AtomicReference<ChatContext> temporaryContext = new AtomicReference<>();
        AtomicReference<List<ChatMessage>> reversedAtCreation = new AtomicReference<>();
        List<Object> toolIds = new ArrayList<>();
        when(sessions.createTemporary(any(), any())).thenAnswer(invocation -> {
            ChatContext context = invocation.getArgument(0);
            ChatMemoryStore store = invocation.getArgument(1);
            temporaryContext.set(context);
            reversedAtCreation.set(List.copyOf(store.getMessages(assistantId)));
            ChatPromptContent prompt = new ChatPromptContent();
            prompt.setSystem("assistant template");
            prompt.setMessageToSend(UserMessage.from("must not create a new {{input}}"));
            ChatSession session = ChatSession.builder()
                    .chatModel(provider.sync)
                    .streamingChatModel(provider)
                    .chatMemory(SystemAlwaysOnTopMessageWindowChatMemory.builder()
                            .id(assistantId)
                            .maxMessages(100)
                            .chatMemoryStore(store)
                            .build())
                    .prompt(prompt)
                    .promptFormat(PromptFormat.MUSTACHE)
                    .variables(new HashMap<>())
                    .tools(Map.of(LOOKUP, (request, memoryId) -> {
                        toolIds.add(memoryId);
                        return toolText(request);
                    }))
                    .build();
            temporary.set(session);
            return session;
        });
        List<ChatMessage> reversed = new ArrayList<>(List.of(POLICY));
        if (greeting) {
            reversed.add(UserMessage.from("greeting {{}}"));
            reversed.add(AiMessage.from("leading human {{}}"));
        }
        reversed.addAll(List.of(
                UserMessage.from("character {{first}}"),
                AiMessage.from("human {{reply}}"),
                UserMessage.from("character {{last}}"),
                AiMessage.from("human {{last}}")));
        List<ChatMessage> prepared = new ArrayList<>(reversed);
        prepared.set(0, SystemMessage.from("Assistant policy {{literal}} {{}}"));
        ToolExecutionRequest tool = call("assistant-tool", "lookup");
        ChatResponse round = tools("assistant-round", FIRST_USAGE, tool);
        ChatResponse answer = answer("assistant-answer", "suggested reply {{}}", SECOND_USAGE);

        ChatResponse result;
        if (streaming) {
            TokenStream stream = service.streamSendAssistant(chatId, "assistant");
            assertTrue(provider.requests.isEmpty());
            Terminal terminal = new Terminal();
            terminal.start(stream);
            provider.complete(0, round);
            provider.complete(1, answer);
            result = terminal.result();
        } else {
            provider.script.addAll(List.of(round, answer));
            result = service.sendAssistant(chatId, "assistant");
            verify(sessions).verifyModerationIfNeeded(null);
        }

        assertEquals(reversed, reversedAtCreation.get(), "Exercise the actual private reverseMessages path");
        assertEquals(
                List.of(prepared, append(prepared, round.aiMessage(), toolResult(tool))),
                provider.requests.stream().map(ChatRequest::messages).toList());
        assertEquals(
                append(prepared, round.aiMessage(), toolResult(tool), answer.aiMessage()),
                temporary.get().getChatMemory(assistantId).messages());
        assertEquals(List.of(assistantId), toolIds);
        assertEquals(answer.aiMessage(), result.aiMessage());
        assertEquals(new TokenUsage(18, 8), result.tokenUsage());
        assertEquals(FinishReason.LENGTH, result.finishReason());
        ChatContext context = temporaryContext.get();
        assertEquals(assistantId, context.getChatId());
        assertEquals("assistant-backend", context.getBackendId());
        assertEquals("Human", context.getCharacterNickname());
        assertEquals("Character", context.getUserNickname());
        assertEquals("Character profile", context.getUserProfile());
        assertEquals("Base scene", context.getAbout());
        assertEquals(original, baseMemory.messages());
        assertEquals(originalVariables, baseVariables);
        assertSame(originalUsage, base.getMemoryUsage());
        assertNotSame(base.getVariables(), temporary.get().getVariables());
        verify(sessions).createTemporary(any(), isA(InMemoryChatMemoryStore.class));
        verify(prompts, never()).apply(any(UserMessage.class), anyMap(), any());
        verifyNoInteractions(ltm, durable);
    }

    private static List<ChatMessage> prepared(boolean continuation) {
        UserMessage multimodal = UserMessage.builder()
                .name("named-user")
                .contents(List.of(
                        TextContent.from("Prepared multimodal {{input}} / {{}}"),
                        ImageContent.from(
                                "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAwMCAO+/l9sAAAAASUVORK5CYII=",
                                "image/png"),
                        TextContent.from("Keep this final text segment {{literal}}")))
                .attributes(Map.of("source", "already-prepared"))
                .build();
        List<ChatMessage> result = new ArrayList<>(List.of(
                POLICY, UserMessage.from("example {{question}}"), AiMessage.from("example {{answer}}"), multimodal));
        if (continuation) {
            result.add(AiMessage.from("Existing assistant response {{continue}} {{}}"));
        }
        return List.copyOf(result);
    }

    private static ToolSpecification specification(String name) {
        return ToolSpecification.builder()
                .name(name)
                .description("Localized tool {{literal}}: " + name)
                .parameters(JsonObjectSchema.builder()
                        .addStringProperty("value")
                        .required("value")
                        .build())
                .build();
    }

    private static ToolExecutionRequest call(String id, String name) {
        return ToolExecutionRequest.builder()
                .id(id)
                .name(name)
                .arguments("{\"value\":\"{{literal}}\"}")
                .build();
    }

    private static String toolText(ToolExecutionRequest request) {
        return "result:" + request.id() + ":{{untouched}}";
    }

    private static ToolExecutionResultMessage toolResult(ToolExecutionRequest request) {
        return ToolExecutionResultMessage.from(request, toolText(request)).toBuilder()
                .isError(false)
                .build();
    }

    private static ChatResponse tools(String id, TokenUsage usage, ToolExecutionRequest... requests) {
        return ChatResponse.builder()
                .id(id)
                .modelName("offline-model")
                .aiMessage(AiMessage.from(requests))
                .tokenUsage(usage)
                .finishReason(FinishReason.TOOL_EXECUTION)
                .build();
    }

    private static ChatResponse answer(String id, String text, TokenUsage usage) {
        return ChatResponse.builder()
                .id(id)
                .modelName("offline-model")
                .aiMessage(AiMessage.from(text))
                .tokenUsage(usage)
                .finishReason(FinishReason.LENGTH)
                .build();
    }

    private static List<ChatMessage> append(List<ChatMessage> before, ChatMessage... messages) {
        List<ChatMessage> result = new ArrayList<>(before);
        result.addAll(List.of(messages));
        return List.copyOf(result);
    }

    private static Throwable rootCause(Throwable failure) {
        while (failure.getCause() != null) {
            failure = failure.getCause();
        }
        return failure;
    }

    private static void assertDefaults(ChatRequestParameters parameters) {
        assertEquals(DEFAULTS.modelName(), parameters.modelName());
        assertEquals(DEFAULTS.temperature(), parameters.temperature());
        assertEquals(DEFAULTS.topP(), parameters.topP());
        assertEquals(DEFAULTS.topK(), parameters.topK());
        assertEquals(DEFAULTS.frequencyPenalty(), parameters.frequencyPenalty());
        assertEquals(DEFAULTS.presencePenalty(), parameters.presencePenalty());
        assertEquals(DEFAULTS.maxOutputTokens(), parameters.maxOutputTokens());
        assertEquals(DEFAULTS.stopSequences(), parameters.stopSequences());
    }

    private static <T> T testMock(Class<T> type) {
        return mock(type, withSettings().mockMaker(MockMakers.SUBCLASS));
    }

    @SuppressWarnings("unchecked")
    private static <T> ChatTask<T> admitInline(ChatTask<T> task) {
        Supplier<T> work = (Supplier<T>)
                ReflectionTestUtils.getField(task, task instanceof ChatTask.Sync<?> ? "work" : "streamBuilder");
        assertNotNull(work);
        try {
            task.future().complete(work.get());
        } catch (Throwable failure) {
            task.future().completeExceptionally(failure);
        }
        return task;
    }

    private static final class Fixture {
        final Object memoryId = new Object();
        final List<String> events = new ArrayList<>();
        final RecordingStore store = new RecordingStore(events);
        final Provider provider = new Provider(events);
        final List<ChatMessage> prepared;
        final ChatMemory memory;
        final ChatSession session;
        final List<ToolExecutionRequest> executed = new ArrayList<>();
        final List<Object> toolMemoryIds = new ArrayList<>();
        final AtomicInteger moderations = new AtomicInteger();
        RuntimeException toolFailure;
        Pair<ChatResponse, Long> lastReply;
        Terminal terminal;

        Fixture(boolean usageWindow, boolean continuation) {
            memory = usageWindow
                    ? SystemAlwaysOnTopMessageWindowChatMemory.builder()
                            .id(STORE_ID)
                            .maxMessages(100)
                            .chatMemoryStore(store)
                            .build()
                    : MessageWindowChatMemory.builder()
                            .id(STORE_ID)
                            .maxMessages(100)
                            .chatMemoryStore(store)
                            .build();
            prepared = prepared(continuation);
            prepared.forEach(memory::add);
            store.snapshots.clear();
            events.clear();
            ToolExecutor executor = (request, id) -> {
                events.add("tool:" + request.id());
                executed.add(request);
                toolMemoryIds.add(id);
                if (toolFailure != null) {
                    throw toolFailure;
                }
                return toolText(request);
            };
            Map<ToolSpecification, ToolExecutor> tools = new LinkedHashMap<>();
            tools.put(LOOKUP, executor);
            tools.put(FETCH, executor);
            session = ChatSession.builder()
                    .chatModel(provider.sync)
                    .streamingChatModel(provider)
                    .chatMemory(memory)
                    .variables(new HashMap<>(Map.of("literal", "MUST NOT SUBSTITUTE")))
                    .tools(tools)
                    .build();
        }

        void limit(int rounds) {
            session.getAiServiceContext().toolService.maxToolCallingRoundTrips(rounds);
        }

        void verifyModeration() {
            moderations.incrementAndGet();
            events.add("moderation");
            assertEquals(1, provider.requests.size(), "Moderation verification follows the first provider response");
            assertTrue(store.snapshots.isEmpty(), "Moderation must precede even the first tool-request write");
            assertTrue(executed.isEmpty(), "Moderation must precede all tools");
        }

        ChatResponse run(boolean streaming, List<ChatResponse> responses) {
            ChatExecution execution = new ChatExecution(session, memoryId);
            if (!streaming) {
                provider.script.addAll(responses);
                lastReply = execution.send(null, this::verifyModeration);
                assertTrue(provider.script.isEmpty(), "All scripted provider rounds must be consumed");
                return lastReply.getLeft();
            }
            TokenStream stream = execution.stream();
            assertTrue(provider.requests.isEmpty(), "Stream construction must not invoke the provider");
            assertTrue(store.snapshots.isEmpty(), "Stream construction must not persist a synthetic input");
            terminal = new Terminal();
            terminal.start(stream);
            for (int index = 0; index < responses.size() && !terminal.future.isDone(); index++) {
                provider.complete(index, responses.get(index));
            }
            return terminal.result();
        }

        void assertNoSyntheticInput() {
            List<ChatMessage> users =
                    prepared.stream().filter(UserMessage.class::isInstance).toList();
            List<List<ChatMessage>> observations = new ArrayList<>(store.snapshots);
            provider.requests.forEach(request -> observations.add(request.messages()));
            observations.add(memory.messages());
            for (List<ChatMessage> messages : observations) {
                assertEquals(
                        users,
                        messages.stream().filter(UserMessage.class::isInstance).toList());
                assertFalse(ChatMessageSerializer.messagesToJson(messages).contains(TRIGGER));
                assertEquals(POLICY, messages.getFirst());
                assertEquals(
                        1,
                        messages.stream()
                                .filter(SystemMessage.class::isInstance)
                                .count());
            }
        }
    }

    private record StoredAnswer(Object memoryId, AiMessage message, TokenUsage usage, Long id) {}

    /** A storage boundary, not a mock ChatMemory; retains every write to catch transient synthetic inputs. */
    private static final class RecordingStore extends InMemoryChatMemoryStore implements TokenUsageChatMemoryStore {
        final List<String> events;
        final List<List<ChatMessage>> snapshots = new ArrayList<>();
        final List<StoredAnswer> answers = new ArrayList<>();

        RecordingStore(List<String> events) {
            this.events = events;
        }

        @Override
        public void updateMessages(Object memoryId, List<ChatMessage> messages) {
            snapshots.add(List.copyOf(messages));
            events.add("write:" + messages.getLast().type());
            super.updateMessages(memoryId, new ArrayList<>(messages));
        }

        @Override
        public Long addAiMessage(Object memoryId, AiMessage message, TokenUsage usage) {
            long id = 101L + answers.size();
            answers.add(new StoredAnswer(memoryId, message, usage, id));
            updateMessages(memoryId, append(getMessages(memoryId), message));
            return id;
        }
    }

    /** Implements doChat so LC's real provider entry point performs default-parameter merging. */
    private static final class Provider implements StreamingChatModel {
        final List<String> events;
        final List<ChatRequest> requests = new ArrayList<>();
        final List<StreamingChatResponseHandler> handlers = new ArrayList<>();
        final Deque<ChatResponse> script = new ArrayDeque<>();
        RuntimeException failure;
        int failureAt = -1;
        final ChatModel sync = new ChatModel() {
            @Override
            public ChatResponse doChat(ChatRequest request) {
                record(request);
                assertFalse(script.isEmpty(), "Unexpected additional synchronous provider call");
                return script.removeFirst();
            }

            @Override
            public ChatRequestParameters defaultRequestParameters() {
                return DEFAULTS;
            }

            @Override
            public ModelProvider provider() {
                return ModelProvider.OTHER;
            }
        };

        Provider(List<String> events) {
            this.events = events;
        }

        private void record(ChatRequest request) {
            int index = requests.size();
            requests.add(request);
            events.add("provider:" + index);
            if (index == failureAt) {
                throw failure;
            }
        }

        @Override
        public void doChat(ChatRequest request, StreamingChatResponseHandler handler) {
            record(request);
            handlers.add(handler);
        }

        @Override
        public ChatRequestParameters defaultRequestParameters() {
            return DEFAULTS;
        }

        @Override
        public ModelProvider provider() {
            return ModelProvider.OTHER;
        }

        void complete(int index, ChatResponse response) {
            assertEquals(index + 1, handlers.size(), "Tool loop must request exactly one next model call");
            StreamingChatResponseHandler handler = handlers.get(index);
            try {
                if (!response.aiMessage().hasToolExecutionRequests()) {
                    handler.onPartialResponse("partial {{}}");
                }
                handler.onCompleteResponse(response);
            } catch (RuntimeException failure) {
                // Match a provider transport delivering callback failures through its error channel.
                handler.onError(failure);
            }
        }
    }

    private static final class Terminal {
        final CompletableFuture<ChatResponse> future = new CompletableFuture<>();
        final List<String> partial = new ArrayList<>();
        final List<ChatResponse> intermediate = new ArrayList<>();
        final AtomicInteger completions = new AtomicInteger();
        final AtomicInteger errors = new AtomicInteger();

        void start(TokenStream stream) {
            stream.onPartialResponse(partial::add)
                    .onIntermediateResponse(intermediate::add)
                    .onCompleteResponse(response -> {
                        completions.incrementAndGet();
                        future.complete(response);
                    })
                    .onError(failure -> {
                        errors.incrementAndGet();
                        future.completeExceptionally(failure);
                    })
                    .start();
        }

        ChatResponse result() {
            assertTrue(future.isDone(), "The scripted stream must terminate without sleeps, polling or timeouts");
            return future.join();
        }
    }
}
