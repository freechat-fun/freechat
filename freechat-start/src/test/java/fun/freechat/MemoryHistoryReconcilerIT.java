package fun.freechat;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.TokenCountEstimator;
import fun.freechat.mapper.ChatHistoryMapper;
import fun.freechat.mapper.ChatMemoryCommitMapper;
import fun.freechat.mapper.ChatMemoryCoordinationMapper;
import fun.freechat.mapper.ChatMemoryStateMapper;
import fun.freechat.model.ChatHistory;
import fun.freechat.model.ChatMemoryState;
import fun.freechat.service.chat.SystemPromptSnapshotStore;
import fun.freechat.service.chat.memory.LongTermMemoryProperties;
import fun.freechat.service.chat.memory.MemoryBounds;
import fun.freechat.service.chat.memory.MemoryHistoryReconciler;
import fun.freechat.service.chat.memory.MemoryPublicationRepository;
import fun.freechat.service.chat.memory.MemoryPublicationRepository.Operation;
import fun.freechat.service.chat.memory.MemoryScope;
import fun.freechat.service.chat.memory.MemorySourceReader;
import fun.freechat.service.chat.memory.MemorySourceReader.Evidence;
import fun.freechat.service.chat.memory.MemorySourceReader.Position;
import fun.freechat.service.chat.memory.MemorySourceReader.Turn;
import fun.freechat.service.chat.memory.MemoryTurnRepository;
import fun.freechat.service.common.impl.LocalFileStoreImpl;
import fun.freechat.service.enums.EmbeddingStoreType;
import fun.freechat.service.util.InfoUtils;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.ibatis.logging.nologging.NoLoggingImpl;
import org.apache.ibatis.session.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/** Isolated real MySQL; no application context, providers, embeddings, Redis or FileStore. */
@Testcontainers
@Timeout(120)
class MemoryHistoryReconcilerIT {
    @Container
    private static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.0.36")
            .withUsername("root")
            .withDatabaseName("freechat")
            .withInitScript("sql/schema.sql");

    private static HikariDataSource dataSource;
    private static JdbcTemplate sql;
    private static ChatMemoryCoordinationMapper coordination;
    private static ChatMemoryStateMapper states;
    private static ChatHistoryMapper histories;
    private static ChatMemoryCommitMapper commits;
    private static DataSourceTransactionManager transactions;
    private static final String FINGERPRINT = "legacy-test-fingerprint";
    private static final LocalDateTime SOURCE_TIME = LocalDateTime.of(2025, 1, 2, 3, 4, 5);

    @TempDir
    Path snapshotDirectory;

    private SystemPromptSnapshotStore snapshots;
    private LongTermMemoryProperties properties;
    private MemoryScope scope;
    private MemoryTurnRepository turns;
    private MemoryHistoryReconciler reconciler;
    private MemorySourceReader reader;
    private MemoryPublicationRepository publication;
    private MemoryBounds bounds;

    @BeforeAll
    static void configureSqlOnly() throws Exception {
        HikariConfig pool = new HikariConfig();
        pool.setJdbcUrl(MYSQL.getJdbcUrl());
        pool.setUsername(MYSQL.getUsername());
        pool.setPassword(MYSQL.getPassword());
        pool.setMaximumPoolSize(4);
        pool.setConnectionInitSql("SET time_zone = '+00:00'");
        dataSource = new HikariDataSource(pool);
        sql = new JdbcTemplate(dataSource);
        Configuration configuration = new Configuration();
        configuration.setLogImpl(NoLoggingImpl.class);
        configuration.addMapper(ChatMemoryStateMapper.class);
        configuration.addMapper(ChatMemoryCoordinationMapper.class);
        configuration.addMapper(ChatMemoryCommitMapper.class);
        configuration.addMapper(ChatHistoryMapper.class);
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setConfiguration(configuration);
        SqlSessionTemplate sessions = new SqlSessionTemplate(factory.getObject());
        states = sessions.getMapper(ChatMemoryStateMapper.class);
        coordination = sessions.getMapper(ChatMemoryCoordinationMapper.class);
        commits = sessions.getMapper(ChatMemoryCommitMapper.class);
        histories = sessions.getMapper(ChatHistoryMapper.class);
        transactions = new DataSourceTransactionManager(dataSource);
    }

    @AfterAll
    static void closePool() {
        if (dataSource != null) {
            dataSource.close();
        }
    }

    @BeforeEach
    void freshScope() {
        properties = new LongTermMemoryProperties();
        properties.afterPropertiesSet();
        scope = new MemoryScope(
                UUID.randomUUID().toString().replace("-", ""),
                "owner",
                "character",
                1,
                EmbeddingStoreType.DEFAULT_LONG_TERM_MEMORY);
        var files = new LocalFileStoreImpl();
        org.springframework.test.util.ReflectionTestUtils.setField(files, "basePathStr", snapshotDirectory.toString());
        snapshots = new SystemPromptSnapshotStore(files);
        turns = new MemoryTurnRepository(coordination, states, histories, transactions, properties, snapshots);
        turns.initialize(scope, FINGERPRINT);
        reconciler = repository(transactions);
        reader = new MemorySourceReader(turns);
        publication =
                new MemoryPublicationRepository(coordination, states, commits, histories, transactions, properties);
        bounds = new MemoryBounds(
                new TokenCountEstimator() {
                    @Override
                    public int estimateTokenCountInText(String text) {
                        return text.codePointCount(0, text.length());
                    }

                    @Override
                    public int estimateTokenCountInMessage(ChatMessage message) {
                        throw new AssertionError("No provider requests");
                    }

                    @Override
                    public int estimateTokenCountInMessages(Iterable<ChatMessage> messages) {
                        throw new AssertionError("No provider requests");
                    }
                },
                properties);
    }

    @Test
    void completeToolTurnSpansBoundedCallsWithoutPublishingCursorOrChangingSource() throws Exception {
        long first = legacy(UserMessage.from("private legacy input"));
        for (int index = 0; index < 1005; index++) {
            legacy(index % 2 == 0 ? toolRequest() : toolResult());
        }
        long last = legacy(AiMessage.from("private final answer"));
        long disabled = legacy(UserMessage.from("disabled raw source"));
        sql.update("UPDATE chat_history SET enabled = 0 WHERE id = ?", disabled);
        List<Map<String, Object>> before = sourceSnapshot();
        var partial = reconcile();
        assertFalse(partial.complete());
        assertEquals(
                1000L,
                sql.queryForObject(
                        "SELECT COUNT(*) FROM chat_history WHERE memory_id = ? AND turn_id IS NOT NULL",
                        Long.class,
                        scope.chatId()));
        assertEquals(partial.throughId(), state().getReconciledThroughId());
        assertEquals(0L, state().getLatestFinalizedId());
        assertEquals(0L, state().getIdleThroughId());
        assertEquals(0L, state().getOverflowThroughId());
        assertTrue(reader.nextTurn(scope, 0, Long.MAX_VALUE).isEmpty());
        assertEquals("turn-start", row(first).getRecordKind());
        JsonNode progress = progress();
        assertEquals(first, progress.get("pendingStartId").longValue());
        assertEquals(row(first).getTurnId(), progress.get("pendingTurnId").textValue());
        assertMetadataOnly(progress);
        // A fresh object must resume exclusively from durable metadata.
        reconciler = repository(transactions);
        assertEquals(new MemoryHistoryReconciler.Result(true, last), reconcile());
        assertEquals(last, state().getLatestFinalizedId());
        assertEquals("turn-complete", row(last).getRecordKind());
        assertEquals(row(first).getTurnId(), row(last).getTurnId());
        assertEquals("tool", row(first + 1).getMessageOrigin());
        assertEquals(before, sourceSnapshot());
        assertEquals(SOURCE_TIME, state().getLastActivity());
        assertEquals(SOURCE_TIME.plus(properties.getIdleTimeout()), state().getDueAt());
        assertEquals(0L, state().getIdleThroughId());
        assertEquals(0L, state().getOverflowThroughId());
        assertEquals(1, progressRows());
        assertTrue(progress().get("pendingTurnId").isNull());
        assertEquals("reconciled", progressStatus());
        assertEquals(1000, turns.sourcePage(scope, 0, last, 1000).size());
        assertEquals(before, sourceSnapshot());
    }

    @Test
    void trustedSystemAndExactFewShotPrefixNeverBecomeProfileEvidence() {
        long system = legacy(SystemMessage.from("private system"));
        UserMessage exampleUser = UserMessage.from("example input");
        AiMessage exampleAi = AiMessage.from("example answer");
        long templateStart = legacy(exampleUser);
        long templateEnd = legacy(exampleAi);
        long user = legacy(UserMessage.from("real legacy input"));
        long ai = legacy(AiMessage.from("real final answer"));
        List<Map<String, Object>> before = sourceSnapshot();
        assertTrue(reconciler
                .reconcile(scope, FINGERPRINT, List.of(exampleUser, exampleAi))
                .complete());
        assertEquals("system", row(system).getMessageOrigin());
        assertEquals("template-example", row(templateStart).getMessageOrigin());
        assertEquals("template-example", row(templateEnd).getMessageOrigin());
        assertNull(row(system).getTurnId());
        assertNull(row(templateStart).getTurnId());
        assertEquals(before, sourceSnapshot());
        List<Evidence> evidence = collect(reader.nextTurn(scope, 0, ai).orElseThrow(), 100);
        assertEquals(
                List.of(user, ai), evidence.stream().map(Evidence::sourceId).toList());
        assertTrue(evidence.stream().noneMatch(Evidence::originalInput));
        assertNull(row(user).getSourceMessage());
        assertEquals("real legacy input", evidence.getFirst().text());
    }

    @Test
    void partiallyMatchingOrRepeatedExamplesAreOrdinaryDialogue() {
        UserMessage example = UserMessage.from("matching first message");
        long user = legacy(example);
        long ai = legacy(AiMessage.from("not configured example answer"));
        long repeated = legacy(example);
        long last = legacy(AiMessage.from("actual answer"));
        assertTrue(reconciler
                .reconcile(scope, FINGERPRINT, List.of(example, AiMessage.from("configured answer")))
                .complete());
        assertEquals("user-input", row(user).getMessageOrigin());
        assertEquals("assistant-output", row(ai).getMessageOrigin());
        assertEquals("user-input", row(repeated).getMessageOrigin());
        assertEquals(
                List.of(user, ai),
                collect(reader.nextTurn(scope, 0, last).orElseThrow(), 100).stream()
                        .map(Evidence::sourceId)
                        .toList());
    }

    @Test
    void thousandRowTemplatePrefixIsVerifiedCompletelyBeforeClassification() throws Exception {
        List<ChatMessage> examples = new ArrayList<>();
        for (int index = 0; index < 1001; index++) {
            ChatMessage example =
                    index % 2 == 0 ? UserMessage.from("example-" + index) : AiMessage.from("example-" + index);
            examples.add(example);
            legacy(example);
        }
        long user = legacy(UserMessage.from("actual input"));
        long last = legacy(AiMessage.from("actual answer"));
        assertFalse(reconciler.reconcile(scope, FINGERPRINT, examples).complete());
        assertEquals(0L, state().getReconciledThroughId());
        assertEquals(1000, progress().get("templatePosition").intValue());
        assertTrue(progress().get("templatePrefix").booleanValue());
        assertMetadataOnly(progress());
        assertFalse(reconciler.reconcile(scope, FINGERPRINT, examples).complete());
        assertEquals(0L, state().getLatestFinalizedId());
        assertTrue(reconciler.reconcile(scope, FINGERPRINT, examples).complete());
        assertEquals(
                List.of(user, last),
                collect(reader.nextTurn(scope, 0, last).orElseThrow(), 100).stream()
                        .map(Evidence::sourceId)
                        .toList());
        assertEquals(1, progressRows());
    }

    @Test
    void unicodeFragmentsIncludeBothBodyBearingBoundariesAndCompleteOnlyAfterFinalFragment() {
        String text = "A\uD83E\uDDEA中e\u0301".repeat(20);
        long user = legacy(UserMessage.from(text));
        long ai = legacy(AiMessage.from(text));
        assertTrue(reconcile().complete());
        Turn turn = reader.nextTurn(scope, 0, ai).orElseThrow();
        List<Evidence> evidence = collect(turn, 10);
        assertTrue(evidence.size() > 2);
        for (long id : List.of(user, ai)) {
            StringBuilder joined = new StringBuilder();
            int offset = 0;
            for (Evidence fragment :
                    evidence.stream().filter(item -> item.sourceId() == id).toList()) {
                assertEquals(offset, fragment.offset());
                assertFalse(fragment.originalInput());
                assertFalse(Character.isLowSurrogate(fragment.text().charAt(0)));
                assertFalse(Character.isHighSurrogate(
                        fragment.text().charAt(fragment.text().length() - 1)));
                joined.append(fragment.text());
                offset += fragment.text().codePointCount(0, fragment.text().length());
            }
            assertEquals(text, joined.toString());
        }
        assertEquals(ai, evidence.getLast().sourceId());
        var complete = reader.nextFragment(scope, turn, new Position(ai, 0), bounds, 10);
        assertTrue(complete.complete());
        assertNull(complete.evidence());
    }

    @Test
    void singleUserAndMultipleOrphansAreBoundedAbortRowsAndCanAdvanceWithoutParsing() {
        long system = legacy(SystemMessage.from("system"));
        List<ChatMessage> examples = List.of(UserMessage.from("example"), AiMessage.from("example final"));
        long template = legacy(examples.getFirst());
        legacy(examples.getLast());
        long orphanAi = legacy(AiMessage.from("orphan biography must not be evidence"));
        long orphanTool = legacy(toolResult());
        long orphanRequest = legacy(toolRequest());
        long singleUser = legacy(UserMessage.from("unfinished"));
        assertTrue(reconciler.reconcile(scope, FINGERPRINT, examples).complete());
        String claim = claim();
        sql.update(
                "UPDATE chat_history SET message = ? WHERE id IN (?, ?)",
                "\"invalid prefix payload\"",
                system,
                template);
        long after = 0;
        for (long id : List.of(orphanAi, orphanTool, orphanRequest, singleUser)) {
            assertEquals("turn-abort", row(id).getRecordKind());
            // A JSON string is valid SQL JSON, but not a valid ChatMessage.
            sql.update("UPDATE chat_history SET message = ? WHERE id = ?", "\"invalid aborted payload\"", id);
            Turn turn = reader.nextTurn(scope, after, singleUser).orElseThrow();
            assertTrue(turn.aborted());
            assertEquals(id, turn.throughId());
            assertTrue(collect(turn, 0).isEmpty());
            publication.advanceAborted(publication.snapshot(scope, Operation.IDLE, id, claim));
            assertEquals(id, state().getIdleThroughId());
            after = id;
        }
        assertNull(state().getDueAt());
        assertEquals(
                4,
                new HashSet<>(List.of(
                                row(orphanAi).getTurnId(),
                                row(orphanTool).getTurnId(),
                                row(orphanRequest).getTurnId(),
                                row(singleUser).getTurnId()))
                        .size());
        List<Map<String, Object>> before = sourceSnapshot();
        resetGeneration();
        assertTrue(reconcile().complete());
        assertEquals(before, sourceSnapshot());
        assertEquals(singleUser, state().getLatestFinalizedId());
    }

    @Test
    void interruptedAndUnfinishedMultiRowTurnsAbortAtTheirExistingLastRows() {
        long first = legacy(UserMessage.from("first user"));
        long tool = legacy(toolResult());
        long second = legacy(UserMessage.from("new user interrupted prior turn"));
        long answer = legacy(AiMessage.from("completed answer"));
        long third = legacy(UserMessage.from("last user"));
        long last = legacy(toolRequest());
        assertTrue(reconcile().complete());
        assertEquals("turn-start", row(first).getRecordKind());
        assertEquals("turn-abort", row(tool).getRecordKind());
        assertEquals(row(first).getTurnId(), row(tool).getTurnId());
        assertEquals("turn-start", row(second).getRecordKind());
        assertEquals("turn-complete", row(answer).getRecordKind());
        assertNotEquals(row(first).getTurnId(), row(second).getTurnId());
        assertEquals("turn-start", row(third).getRecordKind());
        assertEquals("turn-abort", row(last).getRecordKind());
        assertEquals(row(third).getTurnId(), row(last).getTurnId());
        assertTrue(collect(reader.nextTurn(scope, 0, last).orElseThrow(), 100).isEmpty());
        assertEquals(
                List.of(second, answer),
                collect(reader.nextTurn(scope, tool, last).orElseThrow(), 100).stream()
                        .map(Evidence::sourceId)
                        .toList());
        assertTrue(
                collect(reader.nextTurn(scope, answer, last).orElseThrow(), 100).isEmpty());
    }

    @Test
    void exactThousandRowIncompleteTurnAbortsOnlyAtActualEnd() {
        long first = legacy(UserMessage.from("input"));
        long last = first;
        for (int index = 0; index < 999; index++) {
            last = legacy(toolResult());
        }
        assertEquals(new MemoryHistoryReconciler.Result(true, last), reconcile());
        assertEquals("turn-abort", row(last).getRecordKind());
        assertEquals(first, progressStartForTurn(row(last).getTurnId()));
        assertEquals(last, state().getLatestFinalizedId());
    }

    @Test
    void successfulTurnCannotBeSkippedToReachLaterAbortAndUntrustedPrefixesAreRejected() {
        long user = legacy(UserMessage.from("successful input"));
        long answer = legacy(AiMessage.from("successful answer"));
        long abort = legacy(UserMessage.from("unfinished"));
        assertTrue(reconcile().complete());
        String token = claim();
        var snapshot = publication.snapshot(scope, Operation.IDLE, abort, token);
        sanitized(() -> publication.advanceAborted(snapshot));
        assertEquals(0L, state().getIdleThroughId());
        assertEquals("turn-complete", row(answer).getRecordKind());
        sql.update("UPDATE chat_history SET record_kind = 'message', turn_id = NULL WHERE id = ?", user);
        sanitized(() -> reader.nextTurn(scope, 0, abort));
        sanitized(() -> publication.advanceAborted(publication.snapshot(scope, Operation.IDLE, abort, token)));
    }

    @Test
    void onlyBodyBearingAbortMayServeAsItsOwnStart() {
        long orphan = legacy(AiMessage.from("orphan"));
        assertTrue(reconcile().complete());
        String token = claim();
        sql.update("UPDATE chat_history SET message = NULL WHERE id = ?", orphan);
        sanitized(() -> reader.nextTurn(scope, 0, orphan));
        sanitized(() -> publication.advanceAborted(publication.snapshot(scope, Operation.IDLE, orphan, token)));
        assertEquals(0L, state().getIdleThroughId());
    }

    @Test
    void taggedNewTurnsAndPriorLegacyBoundariesSurviveGenerationRebuildUnchanged() {
        long legacyUser = legacy(UserMessage.from("legacy"));
        long legacyEnd = legacy(AiMessage.from("legacy final"));
        assertTrue(reconcile().complete());
        var lease = turns.begin(scope, FINGERPRINT);
        turns.append(
                lease,
                UserMessage.from("rendered"),
                UserMessage.from("original"),
                null,
                MemoryTurnRepository.Origin.USER_INPUT,
                null);
        turns.complete(lease, AiMessage.from("new final"), null, null);
        var aborted = turns.begin(scope, FINGERPRINT);
        turns.append(aborted, UserMessage.from("aborted"), null, null, MemoryTurnRepository.Origin.USER_INPUT, null);
        turns.abort(aborted);
        List<ChatHistory> before = turns.sourcePage(scope, 0, Long.MAX_VALUE, 1000);
        assertTrue(reconcile().complete());
        assertEquals(before, turns.sourcePage(scope, 0, Long.MAX_VALUE, 1000));
        resetGeneration();
        assertTrue(reconcile().complete());
        assertEquals(before, turns.sourcePage(scope, 0, Long.MAX_VALUE, 1000));
        assertEquals("turn-start", row(legacyUser).getRecordKind());
        assertEquals("turn-complete", row(legacyEnd).getRecordKind());
        assertEquals(before.getLast().getId(), state().getLatestFinalizedId());
    }

    @Test
    void rollbackDisablingLegacyFinalRebuildsRemainingTurnWithoutNewIds() {
        long first = legacy(UserMessage.from("retained successful input"));
        long firstFinal = legacy(AiMessage.from("retained successful final"));
        long interrupted = legacy(UserMessage.from("rolled back turn input"));
        long tool = legacy(toolResult());
        long removedFinal = legacy(AiMessage.from("removed final"));
        assertTrue(reconcile().complete());
        String retainedTurn = row(first).getTurnId();
        String interruptedTurn = row(interrupted).getTurnId();
        sql.update("UPDATE chat_history SET enabled = 0 WHERE id = ?", removedFinal);
        resetGeneration();
        // Lifecycle is allowed to reset only the reconciliation cursor, leaving an old terminal ID.
        sql.update(
                "UPDATE chat_memory_state SET latest_finalized_id = ? WHERE chat_id = ?", removedFinal, scope.chatId());
        List<Map<String, Object>> before = sourceSnapshot();
        assertTrue(reconcile().complete());
        assertEquals(before, sourceSnapshot());
        assertEquals("turn-complete", row(firstFinal).getRecordKind());
        assertEquals(retainedTurn, row(firstFinal).getTurnId());
        assertEquals("turn-abort", row(tool).getRecordKind());
        assertEquals(interruptedTurn, row(tool).getTurnId());
        assertEquals(tool, state().getLatestFinalizedId());
        assertFalse(reader.nextTurn(scope, 0, tool).orElseThrow().aborted());
        assertTrue(reader.nextTurn(scope, firstFinal, tool).orElseThrow().aborted());
    }

    @Test
    void sourceGmtNotReconciliationClockDeterminesIdleEpisodes() {
        long first = legacy(UserMessage.from("first"));
        legacy(AiMessage.from("one"));
        long second = legacy(UserMessage.from("second"));
        long secondEnd = legacy(AiMessage.from("two"));
        LocalDateTime later = SOURCE_TIME.plus(properties.getIdleTimeout());
        sql.update("UPDATE chat_history SET gmt_create = ? WHERE id IN (?, ?)", later, second, secondEnd);
        assertTrue(reconcile().complete());
        assertEquals(0L, row(first).getEpisode());
        assertEquals(1L, row(second).getEpisode());
        assertEquals(1L, row(secondEnd).getEpisode());
        assertEquals(later, state().getLastActivity());
        assertEquals(1L, state().getEpisode());
    }

    @Test
    void configurationAndGenerationChangesInvalidatePendingProgressWithoutLosingItsTurn() throws Exception {
        long user = legacy(UserMessage.from("input"));
        for (int index = 0; index < 1001; index++) {
            legacy(toolResult());
        }
        long finalId = legacy(AiMessage.from("final"));
        assertFalse(reconcile().complete());
        String originalTurn = row(user).getTurnId();
        String changed = "changed-fingerprint";
        sql.update("UPDATE chat_memory_state SET fingerprint = ? WHERE chat_id = ?", changed, scope.chatId());
        sanitized(this::reconcile);
        assertFalse(reconciler.reconcile(scope, changed, List.of()).complete());
        assertTrue(reconciler.reconcile(scope, changed, List.of()).complete());
        assertEquals(originalTurn, row(finalId).getTurnId());
        assertEquals(1, progressRows());
        assertEquals(
                changed,
                sql.queryForObject(
                        "SELECT fingerprint FROM chat_memory_commit WHERE chat_id = ? AND operation = 'RECONCILE'",
                        String.class,
                        scope.chatId()));
        assertMetadataOnly(progress());
        resetGeneration();
        sql.update("UPDATE chat_memory_state SET fingerprint = ? WHERE chat_id = ?", FINGERPRINT, scope.chatId());
        assertFalse(reconcile().complete());
        assertTrue(reconcile().complete());
        assertEquals(originalTurn, row(finalId).getTurnId());
        assertEquals(finalId, state().getLatestFinalizedId());
    }

    @Test
    void staleScopeFingerprintStatusAndAnyTurnTokenRejectBeforeMutatingRows() {
        legacy(UserMessage.from("input"));
        ChatMemoryState before = state();
        for (MemoryScope invalid : List.of(
                new MemoryScope(scope.chatId(), "other", scope.characterUid(), 1, scope.storeType()),
                new MemoryScope(scope.chatId(), scope.userId(), "other", 1, scope.storeType()),
                new MemoryScope(scope.chatId(), scope.userId(), scope.characterUid(), 2, scope.storeType()),
                new MemoryScope(
                        scope.chatId(),
                        scope.userId(),
                        scope.characterUid(),
                        1,
                        EmbeddingStoreType.EN_LONG_TERM_MEMORY))) {
            sanitized(() -> reconciler.reconcile(invalid, FINGERPRINT, List.of()));
        }
        sanitized(() -> reconciler.reconcile(scope, "stale-fingerprint", List.of()));
        assertEquals(before, state());
        assertEquals(0, progressRows());
        sql.update("UPDATE chat_memory_state SET status = 'disabled' WHERE chat_id = ?", scope.chatId());
        sanitized(this::reconcile);
        sql.update(
                "UPDATE chat_memory_state SET status = 'active', turn_token = ?, turn_lease_until = UTC_TIMESTAMP(6) -"
                        + " INTERVAL 1 HOUR WHERE chat_id = ?",
                UUID.randomUUID().toString(),
                scope.chatId());
        sanitized(this::reconcile);
        assertEquals(0, progressRows());
        assertNull(turns.sourcePage(scope, 0, Long.MAX_VALUE, 1).getFirst().getTurnId());
    }

    @Test
    void corruptEnabledUserFailsClosedAndRollsBackPriorAnnotations() {
        long first = legacy(UserMessage.from("good input"));
        long bad = legacy(UserMessage.from("bad input"));
        legacy(AiMessage.from("answer"));
        sql.update("UPDATE chat_history SET message = ? WHERE id = ?", "{\"type\":\"USER\",\"contents\":null}", bad);
        List<Map<String, Object>> before = sourceSnapshot();
        sanitized(this::reconcile);
        assertEquals(before, sourceSnapshot());
        assertNull(row(first).getTurnId());
        assertEquals(0L, state().getReconciledThroughId());
        assertEquals(0, progressRows());
    }

    @Test
    void sqlFailureRollsBackBoundariesStateAndProgressTogether() {
        long user = legacy(UserMessage.from("private input"));
        legacy(AiMessage.from("private final"));
        ChatMemoryState before = state();
        sql.execute("""
            CREATE TRIGGER fail_reconcile_commit BEFORE INSERT ON chat_memory_commit FOR EACH ROW
            BEGIN
              IF NEW.operation = 'RECONCILE' THEN
                SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'private-trigger-detail';
              END IF;
            END
            """);
        try {
            IllegalStateException error = sanitized(this::reconcile);
            assertEquals("Memory reconciliation transaction failed", error.getMessage());
            assertEquals(before, state());
            assertNull(row(user).getTurnId());
            assertEquals(0, progressRows());
        } finally {
            sql.execute("DROP TRIGGER fail_reconcile_commit");
        }
        assertTrue(reconcile().complete());
    }

    @Test
    void stateRowLockSerializesConcurrentReconcilersIntoOneProgressRow() throws Exception {
        legacy(UserMessage.from("input"));
        for (int index = 0; index < 1000; index++) {
            legacy(toolResult());
        }
        long last = legacy(AiMessage.from("answer"));
        try (var workers = Executors.newFixedThreadPool(2)) {
            var left = workers.submit(this::reconcile);
            var right = workers.submit(this::reconcile);
            var a = left.get(60, TimeUnit.SECONDS);
            var b = right.get(60, TimeUnit.SECONDS);
            assertNotEquals(a.complete(), b.complete());
        }
        assertEquals(last, state().getReconciledThroughId());
        assertEquals(last, state().getLatestFinalizedId());
        assertEquals(1, progressRows());
    }

    @Test
    void clockReadAfterLockAndTransactionBoundaryFailuresAreSanitized() throws Exception {
        legacy(UserMessage.from("input"));
        try (var blocker = dataSource.getConnection();
                var worker = Executors.newSingleThreadExecutor()) {
            blocker.setAutoCommit(false);
            try (var lock =
                    blocker.prepareStatement("SELECT chat_id FROM chat_memory_state WHERE chat_id = ? FOR UPDATE")) {
                lock.setString(1, scope.chatId());
                try (var result = lock.executeQuery()) {
                    assertTrue(result.next());
                }
            }
            var pending = worker.submit(this::reconcile);
            LocalDateTime released;
            try {
                long timeout = System.nanoTime() + TimeUnit.SECONDS.toNanos(10);
                boolean waiting = false;
                while (System.nanoTime() < timeout) {
                    if (sql.queryForObject("SELECT COUNT(*) FROM performance_schema.data_lock_waits", Integer.class)
                            > 0) {
                        waiting = true;
                        break;
                    }
                }
                assertTrue(waiting);
                released = sql.queryForObject("SELECT UTC_TIMESTAMP(6)", LocalDateTime.class);
            } finally {
                blocker.commit();
            }
            assertTrue(pending.get(20, TimeUnit.SECONDS).complete());
            assertFalse(state().getGmtModified().isBefore(released));
        }
        for (String phase : List.of("begin", "commit", "rollback")) {
            MemoryHistoryReconciler broken = repository(new PlatformTransactionManager() {
                @Override
                public TransactionStatus getTransaction(TransactionDefinition definition) {
                    assertEquals(TransactionDefinition.PROPAGATION_REQUIRES_NEW, definition.getPropagationBehavior());
                    assertEquals(TransactionDefinition.ISOLATION_READ_COMMITTED, definition.getIsolationLevel());
                    if ("begin".equals(phase)) {
                        throw new IllegalStateException("private begin text");
                    }
                    return transactions.getTransaction(definition);
                }

                @Override
                public void commit(TransactionStatus status) {
                    transactions.rollback(status);
                    throw new IllegalStateException("private commit text");
                }

                @Override
                public void rollback(TransactionStatus status) {
                    transactions.rollback(status);
                    throw new IllegalStateException("private rollback text");
                }
            });
            IllegalStateException error = sanitized(() ->
                    broken.reconcile(scope, "rollback".equals(phase) ? "wrong fingerprint" : FINGERPRINT, List.of()));
            assertEquals("Memory reconciliation transaction failed", error.getMessage());
        }
    }

    private MemoryHistoryReconciler repository(PlatformTransactionManager manager) {
        return new MemoryHistoryReconciler(coordination, states, histories, commits, manager, properties);
    }

    private MemoryHistoryReconciler.Result reconcile() {
        return reconciler.reconcile(scope, FINGERPRINT, List.of());
    }

    private long legacy(ChatMessage message) {
        ChatHistory row = new ChatHistory()
                .withMemoryId(scope.chatId())
                .withMessage(ChatMessageSerializer.messageToJson(message))
                .withGmtCreate(SOURCE_TIME)
                .withGmtModified(SOURCE_TIME.plusSeconds(1))
                .withExt("{\"legacy\":true}")
                .withSystemMessageRef(snapshots.save(scope.chatId(), SystemMessage.from("snapshot")));
        assertEquals(1, histories.insertSelective(row));
        return row.getId();
    }

    private static AiMessage toolRequest() {
        return AiMessage.from(ToolExecutionRequest.builder()
                .id("call")
                .name("lookup")
                .arguments("{}")
                .build());
    }

    private static ToolExecutionResultMessage toolResult() {
        return ToolExecutionResultMessage.from("call", "lookup", "private tool result");
    }

    private ChatHistory row(long id) {
        return histories.selectByPrimaryKey(id).orElseThrow();
    }

    private ChatMemoryState state() {
        return states.selectByPrimaryKey(scope.chatId()).orElseThrow();
    }

    private List<Map<String, Object>> sourceSnapshot() {
        return sql.queryForList(
                "SELECT id, memory_id, enabled, message, source_message, system_message_ref, ext, gmt_create,"
                        + " gmt_modified, tg_message_id FROM chat_history WHERE memory_id = ? ORDER BY id",
                scope.chatId());
    }

    private int progressRows() {
        return sql.queryForObject(
                "SELECT COUNT(*) FROM chat_memory_commit WHERE chat_id = ? AND generation = ? AND operation ="
                        + " 'RECONCILE'",
                Integer.class,
                scope.chatId(),
                scope.generation());
    }

    private JsonNode progress() throws Exception {
        return InfoUtils.defaultMapper()
                .readTree(sql.queryForObject(
                        "SELECT progress FROM chat_memory_commit WHERE chat_id = ? AND generation = ? AND operation ="
                                + " 'RECONCILE'",
                        String.class,
                        scope.chatId(),
                        scope.generation()));
    }

    private String progressStatus() {
        return sql.queryForObject(
                "SELECT status FROM chat_memory_commit WHERE chat_id = ? AND generation = ? AND operation ="
                        + " 'RECONCILE'",
                String.class,
                scope.chatId(),
                scope.generation());
    }

    private long progressStartForTurn(String turn) {
        return sql.queryForObject(
                "SELECT MIN(id) FROM chat_history WHERE memory_id = ? AND turn_id = ?",
                Long.class,
                scope.chatId(),
                turn);
    }

    private static void assertMetadataOnly(JsonNode progress) {
        HashSet<String> keys = new HashSet<>();
        progress.fieldNames().forEachRemaining(keys::add);
        assertEquals(
                new HashSet<>(List.of(
                        "pendingTurnId",
                        "pendingStartId",
                        "lastRowId",
                        "episode",
                        "lastTimestamp",
                        "templatePosition",
                        "templatePrefix")),
                keys);
        assertFalse(progress.toString().contains("private"));
        assertTrue(progress.toString().length() < 1024);
    }

    private String claim() {
        String token = UUID.randomUUID().toString();
        sql.update(
                "UPDATE chat_memory_state SET claim_token = ?, claim_lease_until = UTC_TIMESTAMP(6) + INTERVAL 10"
                        + " MINUTE, claim_deadline = UTC_TIMESTAMP(6) + INTERVAL 10 MINUTE WHERE chat_id = ?",
                token,
                scope.chatId());
        return token;
    }

    private void resetGeneration() {
        sql.update(
                "UPDATE chat_memory_state SET generation = generation + 1, reconciled_through_id = 0,"
                        + " latest_finalized_id = 0, idle_through_id = 0, overflow_through_id = 0, claim_token = NULL"
                        + " WHERE chat_id = ?",
                scope.chatId());
        scope = new MemoryScope(
                scope.chatId(), scope.userId(), scope.characterUid(), scope.generation() + 1, scope.storeType());
    }

    private List<Evidence> collect(Turn turn, int tokens) {
        List<Evidence> result = new ArrayList<>();
        Position position = new Position(turn.afterId(), 0);
        for (int count = 0; count < 10000; count++) {
            var fragment = reader.nextFragment(scope, turn, position, bounds, tokens);
            if (fragment.complete()) {
                assertNull(fragment.evidence());
                assertEquals(new Position(turn.throughId(), 0), fragment.next());
                return result;
            }
            assertNotNull(fragment.evidence());
            assertNotEquals(position, fragment.next());
            result.add(fragment.evidence());
            position = fragment.next();
        }
        throw new AssertionError("Source paging did not terminate");
    }

    private static IllegalStateException sanitized(Runnable action) {
        IllegalStateException error = assertThrows(IllegalStateException.class, action::run);
        assertNull(error.getCause());
        assertEquals(0, error.getSuppressed().length);
        assertFalse(error.getMessage().contains("private"));
        return error;
    }
}
