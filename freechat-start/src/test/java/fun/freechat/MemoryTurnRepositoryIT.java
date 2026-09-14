package fun.freechat;

import static org.junit.jupiter.api.Assertions.*;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.output.TokenUsage;
import fun.freechat.mapper.ChatContextMapper;
import fun.freechat.mapper.ChatHistoryMapper;
import fun.freechat.mapper.ChatMemoryCoordinationMapper;
import fun.freechat.mapper.ChatMemoryStateMapper;
import fun.freechat.model.ChatHistory;
import fun.freechat.model.ChatMemoryState;
import fun.freechat.service.chat.SystemPromptSnapshotStore;
import fun.freechat.service.chat.memory.LongTermMemoryProperties;
import fun.freechat.service.chat.memory.MemoryLifecycleRepository;
import fun.freechat.service.chat.memory.MemoryScope;
import fun.freechat.service.chat.memory.MemoryTurnRepository;
import fun.freechat.service.chat.memory.MemoryTurnRepository.Origin;
import fun.freechat.service.chat.memory.MemoryTurnRepository.TurnLease;
import fun.freechat.service.common.impl.LocalFileStoreImpl;
import fun.freechat.service.enums.EmbeddingStoreType;
import fun.freechat.service.util.InfoUtils;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.ibatis.logging.nologging.NoLoggingImpl;
import org.apache.ibatis.session.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@Timeout(60)
class MemoryTurnRepositoryIT {
    @Container
    private static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.0.36")
            // Failure-injection triggers require SUPER when this isolated server enables binary logging.
            .withUsername("root")
            .withDatabaseName("freechat")
            .withInitScript("sql/schema.sql");

    private static HikariDataSource dataSource;
    private static JdbcTemplate sql;
    private static ChatMemoryCoordinationMapper coordination;
    private static ChatMemoryStateMapper states;
    private static ChatHistoryMapper histories;
    private static ChatContextMapper contexts;
    private static DataSourceTransactionManager transactionManager;
    private MemoryTurnRepository repository;

    @TempDir
    Path snapshotDirectory;

    private LocalFileStoreImpl files;
    private SystemPromptSnapshotStore snapshots;
    private LongTermMemoryProperties properties;
    private MemoryScope scope;
    private static final String FINGERPRINT = "test-fingerprint";

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
        configuration.addMapper(ChatHistoryMapper.class);
        configuration.addMapper(ChatMemoryCoordinationMapper.class);
        configuration.addMapper(ChatContextMapper.class);
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setConfiguration(configuration);
        SqlSessionTemplate sessions = new SqlSessionTemplate(factory.getObject());
        states = sessions.getMapper(ChatMemoryStateMapper.class);
        histories = sessions.getMapper(ChatHistoryMapper.class);
        coordination = sessions.getMapper(ChatMemoryCoordinationMapper.class);
        contexts = sessions.getMapper(ChatContextMapper.class);
        transactionManager = new DataSourceTransactionManager(dataSource);
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
        files = new LocalFileStoreImpl();
        ReflectionTestUtils.setField(files, "basePathStr", snapshotDirectory.toString());
        snapshots = new SystemPromptSnapshotStore(files);
        repository =
                new MemoryTurnRepository(coordination, states, histories, transactionManager, properties, snapshots);
        scope = new MemoryScope(
                UUID.randomUUID().toString().replace("-", ""),
                "owner",
                "character",
                1,
                EmbeddingStoreType.DEFAULT_LONG_TERM_MEMORY);
        repository.initialize(scope, FINGERPRINT);
    }

    @Test
    void initializePreservesDefaultsGenerationAndTombstone() {
        ChatMemoryState original = state();
        assertEquals(1L, original.getGeneration());
        assertEquals(0L, original.getVersion());
        assertEquals(0L, original.getLatestFinalizedId());
        assertEquals(0L, original.getEpisode());
        assertEquals(0L, original.getTurnRevision());
        assertEquals(0, original.getRetryAttempts());
        assertEquals((byte) 0, original.getProfileRevalidationPending());
        assertEquals("active", original.getStatus());
        assertNull(original.getDueAt());
        assertNull(original.getTurnToken());
        assertEquals(original, repository.initialize(scope, "replacement-fingerprint"));
        sql.update(
                "UPDATE chat_memory_state SET generation = 7, status = 'deleted', version = 9 WHERE chat_id = ?",
                scope.chatId());
        ChatMemoryState tombstone = state();
        assertEquals(tombstone, repository.initialize(scope, "replacement-fingerprint"));
        assertEquals(7L, state().getGeneration());
        assertEquals(FINGERPRINT, state().getFingerprint());
        assertThrows(IllegalStateException.class, () -> repository.begin(scope, FINGERPRINT));
        assertThrows(IllegalStateException.class, () -> repository.sourcePage(scope, 0, Long.MAX_VALUE, 10));
        assertTrue(repository.read("nonexistent-chat").isEmpty());
    }

    @Test
    void initializationCannotChangeTrustedScope() {
        for (MemoryScope other : List.of(
                new MemoryScope(scope.chatId(), "other-owner", scope.characterUid(), 1, scope.storeType()),
                new MemoryScope(scope.chatId(), scope.userId(), "other-character", 1, scope.storeType()),
                new MemoryScope(
                        scope.chatId(),
                        scope.userId(),
                        scope.characterUid(),
                        1,
                        EmbeddingStoreType.EN_LONG_TERM_MEMORY))) {
            assertThrows(IllegalStateException.class, () -> repository.initialize(other, FINGERPRINT));
        }
        assertEquals(scope.userId(), state().getUserId());
        assertEquals(scope.characterUid(), state().getCharacterUid());
        assertEquals(scope.storeType().text(), state().getStoreType());
    }

    @Test
    void admissionAndAppendAreFencedByFingerprintTokenGenerationAndOwner() {
        assertThrows(IllegalStateException.class, () -> repository.begin(scope, "wrong-fingerprint"));
        TurnLease lease = repository.begin(scope, FINGERPRINT);
        assertThrows(IllegalStateException.class, () -> repository.begin(scope, FINGERPRINT));
        TurnLease wrongToken = new TurnLease(scope, UUID.randomUUID().toString(), lease.deadline(), lease.startId());
        assertThrows(IllegalStateException.class, () -> append(wrongToken));
        MemoryScope wrongGeneration =
                new MemoryScope(scope.chatId(), scope.userId(), scope.characterUid(), 2, scope.storeType());
        TurnLease wrongScope = new TurnLease(wrongGeneration, lease.token(), lease.deadline(), lease.startId());
        assertThrows(IllegalStateException.class, () -> append(wrongScope));
        MemoryScope wrongOwner =
                new MemoryScope(scope.chatId(), "other-owner", scope.characterUid(), 1, scope.storeType());
        assertThrows(
                IllegalStateException.class,
                () -> append(new TurnLease(wrongOwner, lease.token(), lease.deadline(), lease.startId())));
        assertEquals(1, rows().size());
        long id = append(lease);
        assertTrue(id > lease.startId());
        assertEquals(id, rows().getLast().getId());
        sql.update("UPDATE chat_memory_state SET generation = 2 WHERE chat_id = ?", scope.chatId());
        assertThrows(IllegalStateException.class, () -> append(lease));
        assertThrows(IllegalStateException.class, () -> repository.check(lease));
        assertThrows(IllegalStateException.class, () -> repository.abort(lease));
        assertThrows(IllegalStateException.class, () -> repository.sourcePage(scope, 0, Long.MAX_VALUE, 10));
    }

    @Test
    void originalInputRenderedSnapshotUsageAndAllOriginsRoundTripInSql() {
        TurnLease lease = repository.begin(scope, FINGERPRINT);
        UserMessage original = UserMessage.from("Original user input: café and 中文");
        UserMessage rendered = UserMessage.from("Template instructions around original user input");
        SystemMessage snapshot = SystemMessage.from("Rendered system snapshot with a quoted memory section");
        long id = repository.append(lease, rendered, original, snapshot, Origin.USER_INPUT, null);
        ChatHistory saved = histories.selectByPrimaryKey(id).orElseThrow();
        assertEquals(rendered, ChatMessageDeserializer.messageFromJson(saved.getMessage()));
        assertEquals(original, ChatMessageDeserializer.messageFromJson(saved.getSourceMessage()));
        assertNotNull(saved.getSystemMessageRef());
        assertEquals(
                snapshot,
                ChatMessageDeserializer.messageFromJson(snapshots.read(scope.chatId(), saved.getSystemMessageRef())));
        assertEquals("user-input", saved.getMessageOrigin());
        assertEquals((byte) 1, saved.getEnabled());
        repository.append(lease, UserMessage.from("example"), null, null, Origin.TEMPLATE_EXAMPLE, null);
        repository.append(lease, snapshot, null, null, Origin.SYSTEM, null);
        ToolExecutionRequest request = ToolExecutionRequest.builder()
                .id("call-1")
                .name("lookup")
                .arguments("{}")
                .build();
        repository.append(
                lease, AiMessage.from(request), null, snapshot, Origin.ASSISTANT_OUTPUT, new TokenUsage(2, 3));
        repository.append(
                lease,
                ToolExecutionResultMessage.from("call-1", "lookup", "quoted result"),
                null,
                null,
                Origin.TOOL,
                null);
        long answerId = repository.complete(lease, AiMessage.from("Final answer"), snapshot, new TokenUsage(7, 11));
        ChatHistory answer = histories.selectByPrimaryKey(answerId).orElseThrow();
        assertEquals(new TokenUsage(7, 11), InfoUtils.deserialize(answer.getExt()));
        assertNotNull(answer.getSystemMessageRef());
        assertNotEquals(saved.getSystemMessageRef(), answer.getSystemMessageRef());
        List<String> references = rows().stream()
                .map(ChatHistory::getSystemMessageRef)
                .filter(java.util.Objects::nonNull)
                .toList();
        assertEquals(3, references.size());
        assertEquals(3, references.stream().distinct().count(), "Each SQL snapshot uses a fresh object name");
        for (String reference : references) {
            assertEquals(141, reference.length());
            assertTrue(reference.matches("[0-9a-f]{64}/[A-Za-z0-9_-]{43}-[0-9a-f]{32}"));
            assertEquals(ChatMessageSerializer.messageToJson(snapshot), snapshots.read(scope.chatId(), reference));
        }
        assertEquals(
                snapshot,
                ChatMessageDeserializer.messageFromJson(snapshots.read(scope.chatId(), answer.getSystemMessageRef())));
        assertEquals(
                List.of("user-input", "template-example", "system", "assistant-output", "tool", "assistant-output"),
                rows().stream()
                        .filter(row -> "message".equals(row.getRecordKind()))
                        .map(ChatHistory::getMessageOrigin)
                        .toList());
        for (ChatHistory marker : rows().stream()
                .filter(row -> !"message".equals(row.getRecordKind()))
                .toList()) {
            assertNull(marker.getMessage());
            assertNull(marker.getSourceMessage());
            assertNull(marker.getSystemMessageRef());
            assertNull(marker.getExt());
            assertNull(marker.getMessageOrigin());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"append", "complete"})
    void snapshotFileFailureLeavesHistoryAndTurnStateUntouched(String operation) throws Exception {
        TurnLease lease = repository.begin(scope, FINGERPRINT);
        append(lease);
        List<ChatHistory> before = rows();
        ChatMemoryState previous = state();
        Files.writeString(snapshotDirectory.resolve("private"), "private filesystem obstruction");
        SystemMessage snapshot = SystemMessage.from("Private rendered policy must never reach SQL on file failure");
        IllegalStateException failure = assertThrows(IllegalStateException.class, () -> {
            if (operation.equals("append")) {
                repository.append(lease, UserMessage.from("Not inserted"), null, snapshot, Origin.USER_INPUT, null);
            } else {
                repository.complete(lease, AiMessage.from("Not committed"), snapshot, new TokenUsage(3, 5));
            }
        });
        assertEquals("System prompt snapshot persistence failed", failure.getMessage());
        assertNull(failure.getCause());
        assertEquals(0, failure.getSuppressed().length);
        assertEquals(before, rows());
        assertEquals(previous, state());
    }

    @ParameterizedTest
    @ValueSource(strings = {"append", "complete"})
    void leaseExpiryDuringSnapshotSaveIsRecheckedBeforeSqlWrites(String operation) {
        TurnLease lease = repository.begin(scope, FINGERPRINT);
        List<ChatHistory> before = rows();
        AtomicReference<String> published = new AtomicReference<>();
        AtomicReference<ChatMemoryState> expired = new AtomicReference<>();
        SystemMessage snapshot = SystemMessage.from("Snapshot created before lease expiry");
        SystemPromptSnapshotStore expiring = new SystemPromptSnapshotStore(files) {
            @Override
            public String save(String chatId, SystemMessage message) {
                assertFalse(
                        TransactionSynchronizationManager.isActualTransactionActive(),
                        "Snapshot FileStore IO must run outside SQL transactions");
                String reference = super.save(chatId, message);
                published.set(reference);
                sql.update(
                        "UPDATE chat_memory_state SET turn_lease_until = UTC_TIMESTAMP(6) - INTERVAL 1 SECOND "
                                + "WHERE chat_id = ?",
                        chatId);
                expired.set(states.selectByPrimaryKey(chatId).orElseThrow());
                return reference;
            }
        };
        MemoryTurnRepository racing =
                new MemoryTurnRepository(coordination, states, histories, transactionManager, properties, expiring);
        IllegalStateException failure = assertThrows(IllegalStateException.class, () -> {
            if (operation.equals("append")) {
                racing.append(lease, UserMessage.from("Late input"), null, snapshot, Origin.USER_INPUT, null);
            } else {
                racing.complete(lease, AiMessage.from("Late answer"), snapshot, null);
            }
        });
        assertEquals("Memory turn or scope is no longer valid", failure.getMessage());
        assertNotNull(published.get());
        assertEquals(ChatMessageSerializer.messageToJson(snapshot), snapshots.read(scope.chatId(), published.get()));
        assertEquals(before, rows());
        assertEquals(expired.get(), state(), "Only the injected expiry may change SQL state");
    }

    @Test
    void ambiguousFinalCommitRetainsThePublishedSnapshotAndDoesNotDuplicateOnRetry() {
        TurnLease lease = repository.begin(scope, FINGERPRINT);
        append(lease);
        SystemMessage snapshot = SystemMessage.from("Snapshot survives a lost commit acknowledgement");
        DataSourceTransactionManager ambiguous = new DataSourceTransactionManager(dataSource) {
            @Override
            protected void doCommit(DefaultTransactionStatus status) {
                super.doCommit(status);
                // Ignore any preliminary read-only fencing transaction; lose only the final commit acknowledgement.
                if (sql.queryForObject(
                                "SELECT COUNT(*) FROM chat_history WHERE memory_id = ? "
                                        + "AND record_kind = 'turn-complete'",
                                Integer.class,
                                scope.chatId())
                        > 0) {
                    throw new IllegalStateException("private commit acknowledgement detail");
                }
            }
        };
        MemoryTurnRepository uncertain =
                new MemoryTurnRepository(coordination, states, histories, ambiguous, properties, snapshots);
        IllegalStateException failure = assertThrows(
                IllegalStateException.class,
                () -> uncertain.complete(lease, AiMessage.from("Durably committed answer"), snapshot, null));
        assertEquals("Memory transaction failed", failure.getMessage());
        assertNull(failure.getCause());
        List<ChatHistory> committed = rows();
        assertEquals(
                List.of("turn-start", "message", "message", "turn-complete"),
                committed.stream().map(ChatHistory::getRecordKind).toList());
        ChatHistory answer = committed.get(2);
        assertNotNull(answer.getSystemMessageRef());
        assertEquals(
                ChatMessageSerializer.messageToJson(snapshot),
                snapshots.read(scope.chatId(), answer.getSystemMessageRef()));
        assertNull(state().getTurnToken());
        assertEquals(committed.getLast().getId(), state().getLatestFinalizedId());
        assertThrows(
                IllegalStateException.class,
                () -> repository.complete(lease, AiMessage.from("Must not duplicate"), snapshot, null));
        assertEquals(committed, rows());
        assertEquals(
                ChatMessageSerializer.messageToJson(snapshot),
                snapshots.read(scope.chatId(), answer.getSystemMessageRef()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"clear", "rollback", "delete"})
    void softDeletionRetainsSnapshotReferencesAndReadableImmutableFiles(String operation) {
        TurnLease lease = repository.begin(scope, FINGERPRINT);
        SystemMessage first = SystemMessage.from("Policy rendered for input");
        SystemMessage last = SystemMessage.from("Policy rendered for final answer");
        long inputId =
                repository.append(lease, UserMessage.from("Original question"), null, first, Origin.USER_INPUT, null);
        long answerId = repository.complete(lease, AiMessage.from("Final answer"), last, null);
        List<ChatHistory> before = rows();
        MemoryLifecycleRepository lifecycle = new MemoryLifecycleRepository(
                coordination, states, histories, contexts, transactionManager, properties);
        if (operation.equals("clear")) {
            lifecycle.clear(scope.chatId());
        } else if (operation.equals("delete")) {
            lifecycle.delete(scope.chatId());
            assertEquals("deleted", state().getStatus());
        } else {
            assertEquals(List.of(answerId), lifecycle.rollback(scope.chatId(), 1));
        }
        for (ChatHistory original : before) {
            ChatHistory retained =
                    histories.selectByPrimaryKey(original.getId()).orElseThrow();
            assertEquals(original.getMessage(), retained.getMessage());
            assertEquals(original.getSourceMessage(), retained.getSourceMessage());
            assertEquals(original.getSystemMessageRef(), retained.getSystemMessageRef());
            assertEquals(
                    (byte) (!operation.equals("rollback") || original.getId() == answerId ? 0 : 1),
                    retained.getEnabled());
            if (retained.getSystemMessageRef() != null) {
                assertEquals(
                        ChatMessageSerializer.messageToJson(retained.getId() == inputId ? first : last),
                        snapshots.read(scope.chatId(), retained.getSystemMessageRef()));
            }
        }
        assertEquals(2L, state().getGeneration());
        if (operation.equals("rollback")) {
            assertEquals(
                    "turn-abort",
                    histories
                            .selectByPrimaryKey(before.getLast().getId())
                            .orElseThrow()
                            .getRecordKind());
        }
    }

    @Test
    void completeCommitsFinalAnswerAndTerminalTogetherAndResetsRetry() {
        TurnLease lease = repository.begin(scope, FINGERPRINT);
        append(lease);
        sql.update(
                "UPDATE chat_memory_state SET retry_attempts = 3, retry_at = UTC_TIMESTAMP(6) WHERE chat_id = ?",
                scope.chatId());
        long answerId = repository.complete(lease, AiMessage.from("Done"), null, null);
        ChatMemoryState completed = state();
        List<ChatHistory> records = rows();
        assertEquals(
                List.of("turn-start", "message", "message", "turn-complete"),
                records.stream().map(ChatHistory::getRecordKind).toList());
        assertEquals(answerId, records.get(2).getId());
        assertEquals(records.getLast().getId(), completed.getLatestFinalizedId());
        assertEquals(1L, completed.getVersion());
        assertNull(completed.getTurnToken());
        assertNull(completed.getTurnDeadline());
        assertNull(completed.getTurnLeaseUntil());
        assertNull(completed.getRetryAt());
        assertEquals(0, completed.getRetryAttempts());
        assertEquals(completed.getLastActivity().plus(properties.getIdleTimeout()), completed.getDueAt());
        assertThrows(IllegalStateException.class, () -> repository.abort(lease));
        assertThrows(
                IllegalStateException.class, () -> repository.complete(lease, AiMessage.from("Again"), null, null));
        assertThrows(IllegalStateException.class, () -> append(lease));
    }

    @Test
    void failureInTerminalInsertOrStateUpdateRollsBackFinalAnswerButRetainsSnapshot() {
        TurnLease lease = repository.begin(scope, FINGERPRINT);
        append(lease);
        ChatMemoryState before = state();
        List<ChatHistory> originalRows = rows();
        SystemMessage snapshot = SystemMessage.from("Private snapshot survives SQL rollback");
        String json = ChatMessageSerializer.messageToJson(snapshot);
        List<String> published = new ArrayList<>();
        SystemPromptSnapshotStore recording = new SystemPromptSnapshotStore(files) {
            @Override
            public String save(String chatId, SystemMessage message) {
                assertFalse(
                        TransactionSynchronizationManager.isActualTransactionActive(),
                        "Snapshot FileStore IO must run outside SQL transactions");
                String reference = super.save(chatId, message);
                if (reference != null) {
                    published.add(reference);
                }
                return reference;
            }
        };
        MemoryTurnRepository attempts =
                new MemoryTurnRepository(coordination, states, histories, transactionManager, properties, recording);
        assertTrue(published.isEmpty());
        sql.execute("""
            CREATE TRIGGER fail_memory_terminal BEFORE INSERT ON chat_history FOR EACH ROW
            BEGIN
              IF NEW.record_kind = 'turn-complete' THEN
                SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'test-only-private-trigger-detail';
              END IF;
            END
            """);
        try {
            IllegalStateException failure = assertThrows(
                    IllegalStateException.class,
                    () -> attempts.complete(lease, AiMessage.from("private-answer"), snapshot, null));
            assertEquals("Memory transaction failed", failure.getMessage());
            assertNull(failure.getCause());
            assertEquals(originalRows, rows());
            assertEquals(before, state());
            assertEquals(1, published.size());
            assertEquals(json, snapshots.read(scope.chatId(), published.getFirst()));
        } finally {
            sql.execute("DROP TRIGGER fail_memory_terminal");
        }
        sql.execute("""
            CREATE TRIGGER fail_memory_state BEFORE UPDATE ON chat_memory_state FOR EACH ROW
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'test-only-state-update-failure'
            """);
        try {
            IllegalStateException failure = assertThrows(
                    IllegalStateException.class,
                    () -> attempts.complete(lease, AiMessage.from("private-answer"), snapshot, null));
            assertEquals("Memory transaction failed", failure.getMessage());
            assertNull(failure.getCause());
            assertEquals(originalRows, rows());
            assertEquals(before, state());
            assertEquals(2, published.size());
            assertNotEquals(published.getFirst(), published.getLast());
            for (String reference : published) {
                assertEquals(json, snapshots.read(scope.chatId(), reference));
                assertTrue(rows().stream().noneMatch(row -> reference.equals(row.getSystemMessageRef())));
            }
        } finally {
            sql.execute("DROP TRIGGER fail_memory_state");
        }
        long answerId = attempts.complete(lease, AiMessage.from("Retry succeeds"), snapshot, null);
        assertEquals(4, rows().size());
        ChatHistory answer = histories.selectByPrimaryKey(answerId).orElseThrow();
        assertNotNull(answer.getSystemMessageRef());
        assertEquals(3, published.size());
        assertEquals(3, published.stream().distinct().count());
        assertEquals(published.getLast(), answer.getSystemMessageRef());
        for (String reference : published) {
            assertEquals(141, reference.length());
            assertTrue(reference.matches("[0-9a-f]{64}/[A-Za-z0-9_-]{43}-[0-9a-f]{32}"));
            assertEquals(json, snapshots.read(scope.chatId(), reference));
        }
        assertEquals(json, snapshots.read(scope.chatId(), answer.getSystemMessageRef()));
    }

    @Test
    void abortAndCompletionRaceHasExactlyOneWinner() throws Exception {
        TurnLease lease = repository.begin(scope, FINGERPRINT);
        append(lease);
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch go = new CountDownLatch(1);
        try (var workers = Executors.newFixedThreadPool(2)) {
            var completion = workers.submit(
                    () -> compete(ready, go, () -> repository.complete(lease, AiMessage.from("Done"), null, null)));
            var abort = workers.submit(() -> compete(ready, go, () -> repository.abort(lease)));
            try {
                assertTrue(ready.await(10, TimeUnit.SECONDS));
            } finally {
                go.countDown();
            }
            boolean completed = completion.get(20, TimeUnit.SECONDS);
            boolean aborted = abort.get(20, TimeUnit.SECONDS);
            assertNotEquals(completed, aborted);
            List<ChatHistory> records = rows();
            assertEquals(
                    completed ? "turn-complete" : "turn-abort",
                    records.getLast().getRecordKind());
            assertEquals(completed ? 4 : 3, records.size());
            assertEquals(1L, state().getVersion());
            assertEquals(records.getLast().getId(), state().getLatestFinalizedId());
            assertNull(state().getTurnToken());
        }
    }

    @Test
    void expiredLeaseTakeoverAbortsOldTurnAndRejectsEveryStaleOperation() {
        TurnLease old = repository.begin(scope, FINGERPRINT);
        append(old);
        sql.update("""
            UPDATE chat_memory_state SET turn_lease_until = UTC_TIMESTAMP(6) - INTERVAL 1 MICROSECOND,
                last_activity = UTC_TIMESTAMP(6) - INTERVAL 2 HOUR WHERE chat_id = ?
            """, scope.chatId());
        assertThrows(IllegalStateException.class, () -> repository.renew(old));
        TurnLease successor = repository.begin(scope, FINGERPRINT);
        assertNotEquals(old.token(), successor.token());
        List<ChatHistory> records = rows();
        assertEquals(
                List.of("turn-start", "message", "turn-abort", "turn-start"),
                records.stream().map(ChatHistory::getRecordKind).toList());
        assertEquals(old.token(), records.get(2).getTurnId());
        assertEquals(0L, records.get(2).getEpisode());
        assertEquals(1L, records.get(3).getEpisode());
        assertEquals(records.get(2).getId(), state().getLatestFinalizedId());
        assertThrows(IllegalStateException.class, () -> append(old));
        assertThrows(IllegalStateException.class, () -> repository.complete(old, AiMessage.from("Late"), null, null));
        assertThrows(IllegalStateException.class, () -> repository.abort(old));
        assertThrows(IllegalStateException.class, () -> repository.renew(old));
        assertThrows(IllegalStateException.class, () -> repository.check(old));
        repository.check(successor);
        append(successor);
        assertEquals(successor.token(), state().getTurnToken());
    }

    @Test
    void absoluteDeadlineBoundsRenewalWithoutChangingActivityAndExpiredMatchingTokenCanAbort() {
        TurnLease lease = repository.begin(scope, FINGERPRINT);
        assertEquals(state().getLastActivity().plus(properties.getTurnMaxDuration()), lease.deadline());
        assertEquals(state().getLastActivity().plus(properties.getTurnLease()), state().getTurnLeaseUntil());
        sql.update("""
            UPDATE chat_memory_state SET turn_deadline = UTC_TIMESTAMP(6) + INTERVAL 30 SECOND,
                turn_lease_until = UTC_TIMESTAMP(6) + INTERVAL 10 SECOND,
                retry_attempts = 2, retry_at = UTC_TIMESTAMP(6) WHERE chat_id = ?
            """, scope.chatId());
        ChatMemoryState before = state();
        assertEquals(before.getTurnDeadline(), repository.renew(lease));
        assertEquals(before.getTurnDeadline(), repository.renew(lease));
        repository.check(lease);
        ChatMemoryState after = state();
        assertEquals(before.getLastActivity(), after.getLastActivity());
        assertEquals(before.getDueAt(), after.getDueAt());
        assertEquals(before.getVersion(), after.getVersion());
        assertEquals(before.getRetryAt(), after.getRetryAt());
        assertEquals(before.getRetryAttempts(), after.getRetryAttempts());
        assertEquals(before.getTurnDeadline(), after.getTurnDeadline());
        assertEquals(before.getTurnRevision() + 2, after.getTurnRevision());
        sql.update("""
            UPDATE chat_memory_state SET turn_deadline = UTC_TIMESTAMP(6),
                turn_lease_until = UTC_TIMESTAMP(6) + INTERVAL 1 MINUTE WHERE chat_id = ?
            """, scope.chatId());
        assertThrows(IllegalStateException.class, () -> repository.renew(lease));
        assertThrows(IllegalStateException.class, () -> repository.check(lease));
        assertThrows(IllegalStateException.class, () -> append(lease));
        assertThrows(IllegalStateException.class, () -> repository.complete(lease, AiMessage.from("Late"), null, null));
        repository.abort(lease);
        assertEquals("turn-abort", rows().getLast().getRecordKind());
        assertNull(state().getTurnToken());
        assertThrows(IllegalStateException.class, () -> repository.complete(lease, AiMessage.from("Late"), null, null));
    }

    @Test
    void databaseClockIsReadAfterWaitingForTheStateLock() throws Exception {
        TurnLease lease = repository.begin(scope, FINGERPRINT);
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
            var admission = worker.submit(() -> repository.begin(scope, FINGERPRINT));
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
                assertTrue(waiting, "Admission must wait for the state row lock");
                // This expiry is later than any clock value obtained before the lock wait.
                try (var expire = blocker.prepareStatement(
                        "UPDATE chat_memory_state SET turn_lease_until = UTC_TIMESTAMP(6) WHERE chat_id = ?")) {
                    expire.setString(1, scope.chatId());
                    expire.executeUpdate();
                }
            } finally {
                blocker.commit();
            }
            TurnLease successor = admission.get(10, TimeUnit.SECONDS);
            assertNotEquals(lease.token(), successor.token());
            assertEquals("turn-abort", rows().get(1).getRecordKind());
        }
    }

    @Test
    void expiredAbsoluteDeadlineAlsoAllowsTakeoverWithFutureLease() {
        TurnLease old = repository.begin(scope, FINGERPRINT);
        sql.update("""
            UPDATE chat_memory_state SET turn_deadline = UTC_TIMESTAMP(6),
                turn_lease_until = UTC_TIMESTAMP(6) + INTERVAL 1 MINUTE WHERE chat_id = ?
            """, scope.chatId());
        TurnLease current = repository.begin(scope, FINGERPRINT);
        assertNotEquals(old.token(), current.token());
        assertEquals("turn-abort", rows().get(1).getRecordKind());
        repository.check(current);
    }

    @Test
    void idleGapCreatesEpisodeBoundaryButConsecutiveTurnsDoNot() {
        TurnLease first = repository.begin(scope, FINGERPRINT);
        repository.complete(first, AiMessage.from("Done"), null, null);
        TurnLease second = repository.begin(scope, FINGERPRINT);
        assertEquals(0L, state().getEpisode());
        repository.abort(second);
        sql.update(
                "UPDATE chat_memory_state SET last_activity = UTC_TIMESTAMP(6) - INTERVAL 1 HOUR WHERE chat_id = ?",
                scope.chatId());
        TurnLease third = repository.begin(scope, FINGERPRINT);
        assertEquals(1L, state().getEpisode());
        assertEquals(
                1L, histories.selectByPrimaryKey(third.startId()).orElseThrow().getEpisode());
    }

    @Test
    void sourcePagesHaveNoThousandTotalCapAndRespectEnabledRangeAndScope() {
        TurnLease lease = repository.begin(scope, FINGERPRINT);
        List<Long> expectedIds = new ArrayList<>();
        expectedIds.add(lease.startId());
        for (int i = 0; i < 1005; i++) {
            expectedIds.add(append(lease));
        }
        long disabledId = expectedIds.remove(500);
        sql.update("UPDATE chat_history SET enabled = 0 WHERE id = ?", disabledId);
        long answerId = repository.complete(lease, AiMessage.from("Done"), null, null);
        expectedIds.add(answerId);
        long through = state().getLatestFinalizedId();
        expectedIds.add(through);
        TurnLease beyond = repository.begin(scope, FINGERPRINT);
        append(beyond);
        MemoryScope other = new MemoryScope("other-chat", scope.userId(), scope.characterUid(), 1, scope.storeType());
        repository.initialize(other, FINGERPRINT);
        repository.begin(other, FINGERPRINT);
        List<Long> actualIds = new ArrayList<>();
        long cursor = 0;
        while (true) {
            List<ChatHistory> page = repository.sourcePage(scope, cursor, through, 127);
            if (page.isEmpty()) {
                break;
            }
            for (ChatHistory row : page) {
                assertTrue(row.getId() > cursor);
                assertTrue(row.getId() <= through);
                assertEquals(scope.chatId(), row.getMemoryId());
                assertEquals((byte) 1, row.getEnabled());
                actualIds.add(row.getId());
                cursor = row.getId();
            }
        }
        assertTrue(actualIds.size() > 1000);
        assertEquals(expectedIds, actualIds);
        assertEquals(1000, repository.sourcePage(scope, 0, through, 1000).size());
        assertTrue(repository.sourcePage(scope, through, through, 1).isEmpty());
        assertThrows(IllegalArgumentException.class, () -> repository.sourcePage(scope, 0, through, 0));
        assertThrows(IllegalArgumentException.class, () -> repository.sourcePage(scope, 0, through, 1001));
        assertThrows(IllegalArgumentException.class, () -> repository.sourcePage(scope, -1, through, 1));
        assertThrows(IllegalArgumentException.class, () -> repository.sourcePage(scope, through, 0, 1));
    }

    @Test
    void toolRequestCannotCompleteTurn() {
        TurnLease lease = repository.begin(scope, FINGERPRINT);
        AiMessage toolRequest = AiMessage.from(ToolExecutionRequest.builder()
                .id("call")
                .name("lookup")
                .arguments("{}")
                .build());
        assertThrows(IllegalArgumentException.class, () -> repository.complete(lease, toolRequest, null, null));
        assertEquals(1, rows().size());
        repository.check(lease);
    }

    private ChatMemoryState state() {
        return repository.read(scope.chatId()).orElseThrow();
    }

    private List<ChatHistory> rows() {
        return repository.sourcePage(scope, 0, Long.MAX_VALUE, 1000);
    }

    private long append(TurnLease lease) {
        return repository.append(lease, UserMessage.from("Test source input"), null, null, Origin.USER_INPUT, null);
    }

    private static boolean compete(CountDownLatch ready, CountDownLatch go, Runnable operation)
            throws InterruptedException {
        ready.countDown();
        if (!go.await(10, TimeUnit.SECONDS)) {
            throw new IllegalStateException("Test race barrier timed out");
        }
        try {
            operation.run();
            return true;
        } catch (IllegalStateException rejected) {
            assertEquals("Memory turn or scope is no longer valid", rejected.getMessage());
            return false;
        }
    }
}
