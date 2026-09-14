package fun.freechat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.TokenCountEstimator;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import fun.freechat.mapper.*;
import fun.freechat.model.ChatContext;
import fun.freechat.model.ChatHistory;
import fun.freechat.model.ChatMemoryCommit;
import fun.freechat.model.ChatMemoryState;
import fun.freechat.service.character.CharacterService;
import fun.freechat.service.chat.ChatContextService;
import fun.freechat.service.chat.ChatSession;
import fun.freechat.service.chat.impl.LongTermChatMemoryStoreImpl;
import fun.freechat.service.chat.memory.*;
import fun.freechat.service.enums.EmbeddingStoreType;
import fun.freechat.service.enums.PromptFormat;
import fun.freechat.service.prompt.ChatPromptContent;
import fun.freechat.service.prompt.PromptService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockMakers;
import org.mybatis.dynamic.sql.dsl.UpdateDSL;
import org.mybatis.dynamic.sql.dsl.UpdateDSLCompleter;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.SimpleTransactionStatus;

/** Real store/lifecycle/reconciliation/invocation; only configuration and persistence boundaries are fakes. */
class MemoryRenderedExamplesTest {
    private static final String FINGERPRINT = "a".repeat(64);
    private static final List<ChatMessage> RENDERED =
            List.of(UserMessage.from("hello Alice"), AiMessage.from("hello Bob"));

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void sameSnapshotExamplesReconcileExistingHistoryAndSeedNewHistory(boolean existing) {
        Fixture f = new Fixture(existing);
        var binding = f.store.open("chat", f.session).orElseThrow();
        try {
            verify(f.models).resolveForSession("chat", FINGERPRINT);
            verifyNoMoreInteractions(f.models);
            verifyNoInteractions(f.contexts, f.characters);
            if (existing) {
                assertEquals(2L, f.state.getReconciledThroughId());
                assertEquals(List.of("template-example", "template-example"), f.origins);
                verify(f.turns, never()).append(any(), any(), any(), any(), any(), any());
            } else {
                assertEquals(0L, f.state.getReconciledThroughId());
                for (ChatMessage example : RENDERED) {
                    verify(f.turns)
                            .append(
                                    eq(f.lease),
                                    eq(example),
                                    isNull(),
                                    any(SystemMessage.class),
                                    eq(MemoryTurnRepository.Origin.TEMPLATE_EXAMPLE),
                                    isNull());
                }
                verify(f.turns, times(2)).append(any(), any(), any(), any(), any(), any());
            }
            assertEquals(FINGERPRINT, f.state.getFingerprint());
            assertEquals("user", f.state.getUserId());
            assertEquals("character", f.state.getCharacterUid());
            assertEquals(
                    List.of(UserMessage.from("{{stale}}")),
                    f.session.getPrompt().getMessages());
        } finally {
            binding.lifetime().close();
        }
    }

    @Test
    void unavailableConfigurationDoesNotActivateOrReadHistoryOrSessionContent() {
        Fixture f = new Fixture(false);
        when(f.models.resolveForSession(eq("chat"), nullable(String.class)))
                .thenThrow(new IllegalStateException("Memory model resolution unavailable"));
        ChatSession session = fake(ChatSession.class);
        IllegalStateException failure = assertThrows(IllegalStateException.class, () -> f.store.open("chat", session));
        assertEquals("Memory model resolution unavailable", failure.getMessage());
        verify(session).getMemoryFingerprint();
        verifyNoMoreInteractions(session);
        verifyNoInteractions(f.coordination, f.histories, f.turns);
    }

    @Test
    void disabledSnapshotDisablesWithoutReadingSessionContentOrStartingTurn() {
        Fixture f = new Fixture(false);
        when(f.models.resolveForSession("chat", null)).thenReturn(Optional.empty());
        ChatSession session = fake(ChatSession.class);
        assertTrue(f.store.open("chat", session).isEmpty());
        assertEquals("disabled", f.state.getStatus());
        verify(session).getMemoryFingerprint();
        verifyNoMoreInteractions(session);
        verifyNoInteractions(f.histories, f.turns);
    }

    @Test
    void assistantNeverResolvesOrActivatesDurableMemory() {
        Fixture f = new Fixture(false);
        ChatSession session = fake(ChatSession.class);
        assertTrue(f.store.open("chat-assist", session).isEmpty());
        verifyNoInteractions(f.models, f.coordination, f.histories, f.turns, session);
    }

    private static final class Fixture {
        final ChatContextService contexts = fake(ChatContextService.class);
        final CharacterService characters = fake(CharacterService.class);
        final MemoryModelResolver models = fake(MemoryModelResolver.class);
        final MemoryTurnRepository turns = fake(MemoryTurnRepository.class);
        final ChatMemoryCoordinationMapper coordination = fake(ChatMemoryCoordinationMapper.class);
        final ChatHistoryMapper histories = fake(ChatHistoryMapper.class);
        final List<String> origins = new ArrayList<>();
        final MemoryScope scope =
                new MemoryScope("chat", "user", "character", 1, EmbeddingStoreType.EN_LONG_TERM_MEMORY);
        final ChatMemoryState state = new ChatMemoryState()
                .withChatId("chat")
                .withUserId("user")
                .withCharacterUid("character")
                .withGeneration(1L)
                .withStoreType(scope.storeType().text())
                .withStatus("active")
                .withFingerprint(FINGERPRINT)
                .withVersion(0L)
                .withEpisode(0L)
                .withLatestFinalizedId(0L)
                .withOverflowThroughId(0L)
                .withReconciledThroughId(0L)
                .withIdleThroughId(0L);
        final MemoryTurnRepository.TurnLease lease = new MemoryTurnRepository.TurnLease(
                scope, UUID.randomUUID().toString(), LocalDateTime.now().plusMinutes(1), 1);
        final ChatSession session;
        final LongTermChatMemoryStoreImpl store;

        Fixture(boolean existing) {
            LongTermMemoryProperties properties = new LongTermMemoryProperties();
            ChatModel provider = fake(ChatModel.class);
            var resolved = new MemoryModelResolver.Resolved(
                    provider, "test-model", "en", "", "", FINGERPRINT, "character", "user", 10, 10, null);
            var configuration = new MemoryModelResolver.Configuration("user", "character", "en", FINGERPRINT, RENDERED);
            when(models.resolveForSession(eq("chat"), nullable(String.class)))
                    .thenReturn(Optional.of(new MemoryModelResolver.Resolution(resolved, configuration)));
            ChatMemoryStateMapper states = fake(ChatMemoryStateMapper.class);
            ChatMemoryCommitMapper commits = fake(ChatMemoryCommitMapper.class);
            ChatContextMapper contextMapper = fake(ChatContextMapper.class);
            PlatformTransactionManager transactions = fake(PlatformTransactionManager.class);
            when(transactions.getTransaction(any())).thenAnswer(call -> new SimpleTransactionStatus());
            when(coordination.databaseNow()).thenReturn(LocalDateTime.now());
            when(coordination.lock("chat")).thenReturn(Optional.of(state));
            Map<String, String> retained = new HashMap<>();
            when(coordination.retainControl(any())).thenAnswer(call -> {
                ChatMemoryCommit row = call.getArgument(0);
                retained.putIfAbsent(row.getAttemptId(), row.getProgress());
                return 1;
            });
            when(coordination.scopeProgress(anyString()))
                    .thenAnswer(call -> Optional.ofNullable(retained.get(call.getArgument(0))));
            when(contextMapper.selectByPrimaryKey("chat"))
                    .thenReturn(Optional.of(new ChatContext().withChatId("chat").withUserId("user")));
            when(states.updateByPrimaryKey(any())).thenReturn(1);
            when(commits.insertSelective(any())).thenReturn(1);
            if (existing) {
                List<ChatHistory> rows = new ArrayList<>();
                for (int i = 0; i < RENDERED.size(); i++) {
                    rows.add(new ChatHistory()
                            .withId((long) i + 1)
                            .withMemoryId("chat")
                            .withEnabled((byte) 1)
                            .withEpisode(0L)
                            .withRecordKind("message")
                            .withGmtCreate(LocalDateTime.now())
                            .withMessage(ChatMessageSerializer.messageToJson(RENDERED.get(i))));
                }
                when(histories.select(any(org.mybatis.dynamic.sql.dsl.SelectDSLCompleter.class)))
                        .thenReturn(rows);
                when(histories.update(any(UpdateDSLCompleter.class))).thenAnswer(call -> {
                    UpdateDSLCompleter completer = call.getArgument(0);
                    var statement = completer
                            .apply(UpdateDSL.update(ChatHistoryDynamicSqlSupport.chatHistory))
                            .build()
                            .render(RenderingStrategies.MYBATIS3);
                    assertTrue(
                            statement.getParameters().containsValue("template-example"),
                            "Rendered legacy prefix must be examples, never user/assistant evidence");
                    origins.add("template-example");
                    return 1;
                });
            }
            when(turns.read("chat")).thenReturn(Optional.of(state));
            when(turns.begin(scope, FINGERPRINT)).thenReturn(lease);
            when(turns.remainingMillis(lease)).thenReturn(60000L);
            ScheduledExecutorService scheduler = fake(ScheduledExecutorService.class);
            ScheduledFuture<?> timer = fake(ScheduledFuture.class);
            doReturn(timer).when(scheduler).schedule(any(Runnable.class), anyLong(), any(TimeUnit.class));
            doReturn(timer).when(scheduler).scheduleWithFixedDelay(any(), anyLong(), anyLong(), any());
            MemoryBoundsFactory bounds = fake(MemoryBoundsFactory.class);
            when(bounds.forLanguage("en")).thenReturn(new MemoryBounds(fake(TokenCountEstimator.class), properties));
            PromptService prompts = fake(PromptService.class);
            when(prompts.apply(anyString(), anyMap(), any())).thenReturn("system");
            ChatPromptContent prompt = new ChatPromptContent();
            prompt.setSystem("system");
            prompt.setMessages(List.of(UserMessage.from("{{stale}}")));
            session = ChatSession.builder()
                    .memoryFingerprint(FINGERPRINT)
                    .chatModel(provider)
                    .streamingChatModel(fake(StreamingChatModel.class))
                    .prompt(prompt)
                    .promptFormat(PromptFormat.MUSTACHE)
                    .variables(Map.of())
                    .build();
            var lifecycle = new MemoryLifecycleRepository(
                    coordination, states, histories, contextMapper, transactions, properties);
            var reconciler =
                    new MemoryHistoryReconciler(coordination, states, histories, commits, transactions, properties);
            // No heads or recall calls are involved in opening: publisher returns empty without vector access.
            var vectors = new MemoryVectorRepository(
                    fake(fun.freechat.service.rag.ExactEmbeddingStoreService.class),
                    fake(fun.freechat.service.rag.EmbeddingModelService.class),
                    bounds,
                    properties);
            var publisher = new MemoryPublisher(null, vectors);
            store = new LongTermChatMemoryStoreImpl(
                    contexts,
                    characters,
                    prompts,
                    models,
                    lifecycle,
                    reconciler,
                    turns,
                    new MemorySourceReader(turns),
                    null,
                    publisher,
                    vectors,
                    null,
                    bounds,
                    properties,
                    scheduler);
        }
    }

    private static <T> T fake(Class<T> type) {
        return mock(type, withSettings().mockMaker(MockMakers.SUBCLASS));
    }
}
