package fun.freechat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.output.TokenUsage;
import fun.freechat.mapper.ChatContextMapper;
import fun.freechat.mapper.ChatHistoryDynamicSqlSupport;
import fun.freechat.mapper.ChatHistoryMapper;
import fun.freechat.mapper.ChatMemoryCommitMapper;
import fun.freechat.mapper.ChatMemoryCoordinationMapper;
import fun.freechat.mapper.ChatMemoryStateMapper;
import fun.freechat.model.ChatHistory;
import fun.freechat.model.ChatMemoryState;
import fun.freechat.service.chat.SystemPromptSnapshotStore;
import fun.freechat.service.chat.memory.LongTermMemoryProperties;
import fun.freechat.service.chat.memory.MemoryLifecycleRepository;
import fun.freechat.service.chat.memory.MemoryPublicationRepository;
import fun.freechat.service.chat.memory.MemoryPublicationRepository.Operation;
import fun.freechat.service.chat.memory.MemoryScope;
import fun.freechat.service.chat.memory.MemoryTurnRepository;
import fun.freechat.service.chat.memory.MemoryTurnRepository.Origin;
import fun.freechat.service.chat.memory.MemoryTurnRepository.TurnLease;
import fun.freechat.service.chat.memory.MemoryWorkRepository;
import fun.freechat.service.chat.memory.MemoryWorkRepository.Claim;
import fun.freechat.service.common.impl.LocalFileStoreImpl;
import fun.freechat.service.enums.EmbeddingStoreType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.nio.file.Path;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/** Real isolated SQL/transactions only: no application bootstrap, files, vectors, Redis, or providers. */
@Testcontainers
@Timeout(60)
class MemoryLifecycleRepositoryIT {
    @Container
    private static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.0.36")
            // Root permits failure-injection triggers on this disposable binary-logging server.
            .withUsername("root")
            .withDatabaseName("freechat")
            .withInitScript("sql/schema.sql")
            .withCommand("--max-allowed-packet=32M");

    private static final String FINGERPRINT = "a".repeat(64);
    private static final String REPLACEMENT = "b".repeat(64);
    private static final String FAILURE = "Memory lifecycle transaction failed";
    private static HikariDataSource dataSource;
    private static JdbcTemplate sql;
    private static ChatMemoryCoordinationMapper coordination;
    private static ChatMemoryStateMapper states;
    private static ChatHistoryMapper histories;
    private static ChatContextMapper contexts;
    private static ChatMemoryCommitMapper commits;
    private static DataSourceTransactionManager transactionManager;

    @TempDir
    Path snapshotDirectory;

    private SystemPromptSnapshotStore snapshots;
    private LongTermMemoryProperties properties;
    private MemoryLifecycleRepository lifecycle;
    private MemoryTurnRepository turns;
    private MemoryWorkRepository work;
    private MemoryPublicationRepository publication;
    private MemoryScope scope;

    @BeforeAll
    static void configureSqlOnly() throws Exception {
        HikariConfig pool = new HikariConfig();
        pool.setJdbcUrl(MYSQL.getJdbcUrl());
        pool.setUsername(MYSQL.getUsername());
        pool.setPassword(MYSQL.getPassword());
        pool.setMaximumPoolSize(5);
        pool.setConnectionInitSql("SET time_zone = '+00:00'");
        dataSource = new HikariDataSource(pool);
        sql = new JdbcTemplate(dataSource);
        Configuration configuration = new Configuration();
        configuration.setLogImpl(NoLoggingImpl.class);
        configuration.addMapper(ChatMemoryStateMapper.class);
        configuration.addMapper(ChatMemoryCoordinationMapper.class);
        configuration.addMapper(ChatHistoryMapper.class);
        configuration.addMapper(ChatContextMapper.class);
        configuration.addMapper(ChatMemoryCommitMapper.class);
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setConfiguration(configuration);
        SqlSessionTemplate sessions = new SqlSessionTemplate(factory.getObject());
        states = sessions.getMapper(ChatMemoryStateMapper.class);
        coordination = sessions.getMapper(ChatMemoryCoordinationMapper.class);
        histories = sessions.getMapper(ChatHistoryMapper.class);
        contexts = sessions.getMapper(ChatContextMapper.class);
        commits = sessions.getMapper(ChatMemoryCommitMapper.class);
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
        lifecycle = repository(states);
        var files = new LocalFileStoreImpl();
        org.springframework.test.util.ReflectionTestUtils.setField(files, "basePathStr", snapshotDirectory.toString());
        snapshots = new SystemPromptSnapshotStore(files);
        turns = new MemoryTurnRepository(coordination, states, histories, transactionManager, properties, snapshots);
        work = new MemoryWorkRepository(coordination, states, transactionManager, properties);
        publication = new MemoryPublicationRepository(
                coordination, states, commits, histories, transactionManager, properties);
        scope = new MemoryScope(id(), "owner", "character", 1, EmbeddingStoreType.DEFAULT_LONG_TERM_MEMORY);
        insertContext(scope);
        lifecycle.activate(scope, FINGERPRINT);
    }

    @Test
    void activationIsIdempotentAndNeverAcceptsCallerGenerationAsAnInstruction() {
        ChatMemoryState before = state();
        assertEquals(1L, before.getGeneration());
        assertEquals(0L, before.getVersion());
        assertEquals("active", before.getStatus());
        assertNull(before.getDueAt());
        assertEquals(before, lifecycle.activate(withGeneration(scope, 99), FINGERPRINT));
        TurnLease live = turns.begin(scope, FINGERPRINT);
        append(live);
        before = state();
        List<ChatHistory> source = rows();
        assertEquals(before, lifecycle.activate(scope, FINGERPRINT));
        assertEquals(source, rows());
        turns.check(live);
    }

    @Test
    void activationRequiresExistingContextAndExactCurrentOwnerEvenOnFastPath() {
        MemoryScope absent = new MemoryScope(id(), scope.userId(), scope.characterUid(), 1, scope.storeType());
        assertFailed(() -> lifecycle.activate(absent, FINGERPRINT));
        assertTrue(states.selectByPrimaryKey(absent.chatId()).isEmpty());
        ChatMemoryState before = state();
        MemoryScope wrongOwner =
                new MemoryScope(scope.chatId(), "other-owner", scope.characterUid(), 1, scope.storeType());
        assertFailed(() -> lifecycle.activate(wrongOwner, FINGERPRINT));
        sql.update("UPDATE chat_context SET user_id = ? WHERE chat_id = ?", "OWNER", scope.chatId());
        assertFailed(() -> lifecycle.activate(scope, FINGERPRINT));
        sql.update("UPDATE chat_context SET user_id = NULL WHERE chat_id = ?", scope.chatId());
        assertFailed(() -> lifecycle.activate(scope, FINGERPRINT));
        contexts.deleteByPrimaryKey(scope.chatId());
        assertFailed(() -> lifecycle.activate(scope, FINGERPRINT));
        assertEquals(before, state());
    }

    @Test
    void invalidArgumentsCannotMutateStateOrHistory() {
        ChatMemoryState before = state();
        for (String invalid : new String[] {null, "", " ", "x".repeat(33), "chat\nprivate"}) {
            assertFailed(() -> lifecycle.disable(invalid));
            assertFailed(() -> lifecycle.clear(invalid));
            assertFailed(() -> lifecycle.delete(invalid));
            assertFailed(() -> lifecycle.rollback(invalid, 1));
        }
        for (String invalid : new String[] {null, "", "a".repeat(63), "a".repeat(65), "A".repeat(64), "g".repeat(64)}) {
            assertFailed(() -> lifecycle.activate(scope, invalid));
        }
        assertFailed(() -> lifecycle.rollback(scope.chatId(), 0));
        assertFailed(() -> lifecycle.rollback(scope.chatId(), -1));
        assertFailed(() -> lifecycle.activate(null, FINGERPRINT));
        assertEquals(before, state());
        assertTrue(rows().isEmpty());
    }

    @Test
    void disableRevokesTurnAndClaimButReenablePreservesGenerationHeadsAndSources() {
        completeTurn();
        Claim claim = claim();
        TurnLease live = turns.begin(scope, FINGERPRINT);
        append(live);
        seedHeads();
        ChatMemoryState before = state();
        List<ChatHistory> source = rows();
        lifecycle.disable(scope.chatId());
        ChatMemoryState disabled = state();
        assertEquals("disabled", disabled.getStatus());
        assertEquals(before.getGeneration(), disabled.getGeneration());
        assertEquals(before.getVersion() + 1, disabled.getVersion());
        assertEquals(before.getSummaryId(), disabled.getSummaryId());
        assertEquals(before.getProfileId(), disabled.getProfileId());
        assertEquals(before.getOverflowThroughId(), disabled.getOverflowThroughId());
        assertEquals(before.getIdleThroughId(), disabled.getIdleThroughId());
        assertNull(disabled.getDueAt());
        assertRevoked(disabled);
        assertEquals(source, rows().subList(0, source.size()));
        ChatHistory abort = rows().getLast();
        assertEquals("turn-abort", abort.getRecordKind());
        assertEquals(live.token(), abort.getTurnId());
        assertEquals(before.getEpisode(), abort.getEpisode());
        assertNull(abort.getMessage());
        assertEquals(abort.getId(), disabled.getLatestFinalizedId());
        assertFenced(live, claim);
        lifecycle.disable(scope.chatId());
        assertEquals(disabled, state());
        ChatMemoryState active = lifecycle.activate(scope, FINGERPRINT);
        assertEquals("active", active.getStatus());
        assertEquals(before.getGeneration(), active.getGeneration());
        assertEquals(before.getProfileId(), active.getProfileId());
        assertEquals(before.getSummaryId(), active.getSummaryId());
        assertEquals(0, active.getRetryAttempts());
        assertNull(active.getRetryAt());
        assertEquals(active.getLastActivity().plus(properties.getIdleTimeout()), active.getDueAt());
        assertFenced(live, claim);
        turns.check(turns.begin(scope, FINGERPRINT));
    }

    enum Identity {
        OWNER,
        CHARACTER,
        STORE
    }

    @ParameterizedTest
    @EnumSource(Identity.class)
    void identityChangeInvalidatesHeadsAndGenerationButOnlyOwnerChangeDisablesHistory(Identity identity) {
        completeTurn();
        Claim oldClaim = claim();
        TurnLease live = turns.begin(scope, FINGERPRINT);
        append(live);
        seedHeads();
        ChatMemoryState before = state();
        List<ChatHistory> source = rows();
        MemoryScope replacement =
                switch (identity) {
                    case OWNER ->
                        new MemoryScope(scope.chatId(), "new-owner", scope.characterUid(), 55, scope.storeType());
                    case CHARACTER ->
                        new MemoryScope(scope.chatId(), scope.userId(), "new-character", 55, scope.storeType());
                    case STORE ->
                        new MemoryScope(
                                scope.chatId(),
                                scope.userId(),
                                scope.characterUid(),
                                55,
                                EmbeddingStoreType.EN_LONG_TERM_MEMORY);
                };
        if (identity == Identity.OWNER) {
            sql.update("UPDATE chat_context SET user_id = ? WHERE chat_id = ?", replacement.userId(), scope.chatId());
        }
        ChatMemoryState changed = lifecycle.activate(replacement, FINGERPRINT);
        assertInvalidated(before, changed);
        assertRevoked(changed);
        assertEquals(replacement.userId(), changed.getUserId());
        assertEquals(replacement.characterUid(), changed.getCharacterUid());
        assertEquals(replacement.storeType().text(), changed.getStoreType());
        assertSourcesPreserved(source);
        assertTrue(rows().stream().allMatch(row -> row.getEnabled() == (identity == Identity.OWNER ? 0 : 1)));
        assertEquals(identity == Identity.OWNER ? 0L : rows().getLast().getId(), changed.getLatestFinalizedId());
        if (identity == Identity.OWNER) {
            assertNull(changed.getDueAt());
        }
        assertFenced(live, oldClaim);
        assertThrows(IllegalStateException.class, () -> turns.begin(scope, FINGERPRINT));
        assertThrows(IllegalStateException.class, () -> turns.sourcePage(scope, 0, Long.MAX_VALUE, 100));
        turns.check(turns.begin(withGeneration(replacement, changed.getGeneration()), FINGERPRINT));
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void fingerprintChangeRetainsProfileReferenceButMarksItPendingAndRevokesWork(boolean hasProfile) {
        completeTurn();
        Claim oldClaim = claim();
        seedHeads();
        if (!hasProfile) {
            sql.update("UPDATE chat_memory_state SET profile_id = NULL WHERE chat_id = ?", scope.chatId());
        }
        ChatMemoryState before = state();
        List<ChatHistory> source = rows();
        ChatMemoryState changed = lifecycle.activate(scope, REPLACEMENT);
        assertEquals(before.getGeneration(), changed.getGeneration());
        assertEquals(before.getProfileId(), changed.getProfileId());
        assertEquals(before.getSummaryId(), changed.getSummaryId());
        assertEquals(before.getOverflowThroughId(), changed.getOverflowThroughId());
        assertEquals(before.getIdleThroughId(), changed.getIdleThroughId());
        assertEquals(before.getLastActivity(), changed.getLastActivity());
        assertEquals((byte) (hasProfile ? 1 : 0), changed.getProfileRevalidationPending());
        assertEquals(REPLACEMENT, changed.getFingerprint());
        assertEquals(source, rows());
        assertRevoked(changed);
        assertThrows(IllegalStateException.class, () -> work.renew(oldClaim));
        assertThrows(IllegalStateException.class, () -> turns.begin(scope, FINGERPRINT));
        assertEquals(
                hasProfile ? changed.getLastActivity().plus(properties.getIdleTimeout()) : null, changed.getDueAt());
        assertEquals(changed, lifecycle.activate(scope, REPLACEMENT));
        turns.check(turns.begin(scope, REPLACEMENT));
    }

    @Test
    void rollbackSelectsOnlyLastEnabledMessageBearingIdsAndPreservesEverySourceField() {
        completeTurn();
        List<ChatHistory> firstTurn = rows();
        TurnLease affected = turns.begin(scope, FINGERPRINT);
        long input = append(affected);
        long answer = turns.complete(affected, AiMessage.from("answer"), null, null);
        long terminal = state().getLatestFinalizedId();
        ChatHistory legacy = insertMessage(scope.chatId(), "legacy source", null, "turn-complete");
        ChatHistory disabled = insertMessage(scope.chatId(), "already disabled", null, "message");
        sql.update("UPDATE chat_history SET enabled = 0 WHERE id = ?", disabled.getId());
        MemoryScope other = otherScope();
        ChatHistory otherRow = insertMessage(other.chatId(), "other source", affected.token(), "turn-complete");
        seedHeads();
        sql.update(
                "UPDATE chat_memory_state SET idle_through_id = 0, retry_attempts = 0, retry_at = NULL WHERE chat_id ="
                        + " ?",
                scope.chatId());
        Claim oldClaim = claim();
        var snapshot = publication.snapshot(scope, Operation.IDLE, terminal, oldClaim.token());
        publication.checkSnapshot(snapshot);
        ChatMemoryState before = state();
        List<ChatHistory> source = rows();
        assertEquals(List.of(legacy.getId(), answer), lifecycle.rollback(scope.chatId(), 2));
        assertInvalidated(before, state());
        assertEquals(before.getLatestFinalizedId(), state().getLatestFinalizedId());
        assertSourcesPreserved(source);
        assertEquals(source.size(), rows().size());
        assertEquals(firstTurn, rows().subList(0, firstTurn.size()));
        assertEquals((byte) 1, histories.selectByPrimaryKey(input).orElseThrow().getEnabled());
        assertEquals(
                (byte) 0, histories.selectByPrimaryKey(answer).orElseThrow().getEnabled());
        assertEquals(
                (byte) 0,
                histories.selectByPrimaryKey(legacy.getId()).orElseThrow().getEnabled());
        assertEquals(
                "turn-complete",
                histories.selectByPrimaryKey(legacy.getId()).orElseThrow().getRecordKind());
        assertEquals(
                "turn-abort",
                histories.selectByPrimaryKey(terminal).orElseThrow().getRecordKind());
        assertEquals(
                (byte) 1, histories.selectByPrimaryKey(terminal).orElseThrow().getEnabled());
        assertEquals(otherRow, histories.selectByPrimaryKey(otherRow.getId()).orElseThrow());
        assertThrows(IllegalStateException.class, () -> publication.checkSnapshot(snapshot));
        assertThrows(IllegalStateException.class, () -> work.release(oldClaim));
    }

    @Test
    void rollbackConvertsSelectedMessageBearingTaggedTerminalWithoutTouchingAnotherTurn() {
        completeTurn();
        List<ChatHistory> completed = rows();
        String turn = UUID.randomUUID().toString();
        ChatHistory terminal = insertMessage(scope.chatId(), "reconciled source", turn, "turn-complete");
        assertEquals(List.of(terminal.getId()), lifecycle.rollback(scope.chatId(), 1));
        ChatHistory actual = histories.selectByPrimaryKey(terminal.getId()).orElseThrow();
        assertEquals("turn-abort", actual.getRecordKind());
        assertEquals((byte) 0, actual.getEnabled());
        assertEquals(completed, rows().subList(0, completed.size()));
        assertSourcesPreserved(List.of(terminal));
    }

    @Test
    void emptyRollbackIsANoopEvenWithLiveTurn() {
        TurnLease live = turns.begin(scope, FINGERPRINT);
        ChatMemoryState before = state();
        List<ChatHistory> source = rows();
        assertTrue(lifecycle.rollback(scope.chatId(), Integer.MAX_VALUE).isEmpty());
        assertEquals(before, state());
        assertEquals(source, rows());
        turns.check(live);
    }

    @Test
    void rollbackWithoutStateSupportsLegacyHistoryAndDoesNotTouchOtherChats() {
        MemoryScope legacy = new MemoryScope(id(), scope.userId(), scope.characterUid(), 1, scope.storeType());
        insertContext(legacy);
        ChatHistory old = insertMessage(legacy.chatId(), "legacy", null, "message");
        ChatMemoryState untouched = state();
        assertEquals(List.of(old.getId()), lifecycle.rollback(legacy.chatId(), 10));
        assertTrue(states.selectByPrimaryKey(legacy.chatId()).isEmpty());
        assertEquals(
                (byte) 0,
                histories.selectByPrimaryKey(old.getId()).orElseThrow().getEnabled());
        assertEquals(untouched, state());
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void clearRevokesAllWorkAndKeepsDisabledHistoryInvisibleAfterReenable(boolean initiallyDisabled) {
        completeTurn();
        Claim oldClaim = claim();
        TurnLease live = turns.begin(scope, FINGERPRINT);
        append(live);
        seedHeads();
        if (initiallyDisabled) {
            lifecycle.disable(scope.chatId());
        }
        ChatMemoryState before = state();
        List<ChatHistory> source = rows();
        MemoryScope other = otherScope();
        ChatMemoryState otherBefore = states.selectByPrimaryKey(other.chatId()).orElseThrow();
        lifecycle.clear(scope.chatId());
        ChatMemoryState cleared = state();
        assertInvalidated(before, cleared);
        assertRevoked(cleared);
        assertEquals(before.getTurnRevision() + 1, cleared.getTurnRevision());
        assertEquals(before.getStatus(), cleared.getStatus());
        assertEquals(0L, cleared.getLatestFinalizedId());
        assertNull(cleared.getDueAt());
        assertEquals(source.size(), rows().size());
        assertSourcesPreserved(source);
        assertTrue(rows().stream().allMatch(row -> row.getEnabled() == 0));
        assertTrue(contexts.selectByPrimaryKey(scope.chatId()).isPresent());
        assertEquals(otherBefore, states.selectByPrimaryKey(other.chatId()).orElseThrow());
        assertFenced(live, oldClaim);
        lifecycle.activate(scope, FINGERPRINT);
        MemoryScope current = withGeneration(scope, cleared.getGeneration());
        assertTrue(turns.sourcePage(current, 0, Long.MAX_VALUE, 1000).isEmpty());
        TurnLease next = turns.begin(current, FINGERPRINT);
        assertTrue(append(next) > source.getLast().getId());
        assertSourcesPreserved(source);
    }

    @Test
    void deletePersistsTombstoneAcrossNewRepositoriesContextRecreationAndOtherLifecycleCalls() {
        completeTurn();
        Claim oldClaim = claim();
        TurnLease live = turns.begin(scope, FINGERPRINT);
        append(live);
        seedHeads();
        ChatMemoryState before = state();
        List<ChatHistory> source = rows();
        assertTrue(lifecycle.delete(scope.chatId()));
        assertInvalidated(before, state());
        assertRevoked(state());
        assertEquals("deleted", state().getStatus());
        assertEquals(0L, state().getLatestFinalizedId());
        assertNull(state().getDueAt());
        assertTrue(contexts.selectByPrimaryKey(scope.chatId()).isEmpty());
        assertTrue(rows().stream().allMatch(row -> row.getEnabled() == 0));
        assertSourcesPreserved(source);
        assertFenced(live, oldClaim);
        lifecycle = repository(states);
        assertFalse(lifecycle.delete(scope.chatId()));
        lifecycle.disable(scope.chatId());
        lifecycle.clear(scope.chatId());
        assertEquals("deleted", state().getStatus());
        insertContext(scope);
        assertFailed(() -> lifecycle.activate(scope, FINGERPRINT));
        assertFailed(() -> lifecycle.activate(scope, REPLACEMENT));
        assertFailed(() -> lifecycle.rollback(scope.chatId(), 1));
        assertEquals("deleted", turns.initialize(scope, FINGERPRINT).getStatus());
        assertThrows(
                IllegalStateException.class,
                () -> turns.begin(withGeneration(scope, state().getGeneration()), FINGERPRINT));
        assertSourcesPreserved(source);
    }

    @Test
    void deletingNeverActivatedContextAlsoLeavesDurableTombstone() {
        MemoryScope legacy = new MemoryScope(id(), scope.userId(), scope.characterUid(), 1, scope.storeType());
        insertContext(legacy);
        ChatHistory source = insertMessage(legacy.chatId(), "legacy", null, "message");
        assertTrue(lifecycle.delete(legacy.chatId()));
        assertTrue(contexts.selectByPrimaryKey(legacy.chatId()).isEmpty());
        assertEquals(
                (byte) 0,
                histories.selectByPrimaryKey(source.getId()).orElseThrow().getEnabled());
        insertContext(legacy);
        assertFailed(() -> repository(states).activate(legacy, FINGERPRINT));
        assertEquals(
                "deleted",
                states.selectByPrimaryKey(legacy.chatId()).orElseThrow().getStatus());
    }

    @Test
    void clearAndDisableUnknownChatsDoNotCreateActiveState() {
        String missing = id();
        lifecycle.disable(missing);
        lifecycle.clear(missing);
        assertTrue(lifecycle.rollback(missing, 1).isEmpty());
        assertFalse(lifecycle.delete(missing));
        assertTrue(states.selectByPrimaryKey(missing)
                .map(row -> "deleted".equals(row.getStatus()))
                .orElse(true));
    }

    @ParameterizedTest
    @ValueSource(strings = {"terminal", "state"})
    void rollbackSqlFailureRollsBackDisabledMessagesAbortMarkerHeadsAndLeasesTogether(String phase) {
        completeTurn();
        Claim oldClaim = claim();
        TurnLease live = turns.begin(scope, FINGERPRINT);
        append(live);
        seedHeads();
        ChatMemoryState before = state();
        List<ChatHistory> source = rows();
        sql.execute(phase.equals("terminal") ? """
            CREATE TRIGGER lifecycle_failure BEFORE UPDATE ON chat_history FOR EACH ROW
            BEGIN
              IF NEW.record_kind = 'turn-abort' THEN
                SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'fabricated-private-terminal';
              END IF;
            END
            """ : """
                CREATE TRIGGER lifecycle_failure BEFORE UPDATE ON chat_memory_state FOR EACH ROW
                SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'fabricated-private-state'
                """);
        try {
            assertFailed(() -> lifecycle.rollback(scope.chatId(), 3));
            assertEquals(before, state());
            assertEquals(source, rows());
            turns.check(live);
            work.check(oldClaim);
        } finally {
            sql.execute("DROP TRIGGER lifecycle_failure");
        }
        assertEquals(3, lifecycle.rollback(scope.chatId(), 3).size());
        assertFenced(live, oldClaim);
    }

    @Test
    void deleteContextFailureRollsBackTombstoneAndSourceDisabling() {
        completeTurn();
        seedHeads();
        ChatMemoryState before = state();
        List<ChatHistory> source = rows();
        sql.execute("""
            CREATE TRIGGER lifecycle_delete_failure BEFORE DELETE ON chat_context FOR EACH ROW
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'fabricated-private-context'
            """);
        try {
            assertFailed(() -> lifecycle.delete(scope.chatId()));
            assertEquals(before, state());
            assertEquals(source, rows());
            assertTrue(contexts.selectByPrimaryKey(scope.chatId()).isPresent());
        } finally {
            sql.execute("DROP TRIGGER lifecycle_delete_failure");
        }
        assertTrue(lifecycle.delete(scope.chatId()));
    }

    @Test
    void generationOverflowCannotPartiallyClearHistory() {
        completeTurn();
        sql.update("UPDATE chat_memory_state SET generation = ? WHERE chat_id = ?", Long.MAX_VALUE, scope.chatId());
        ChatMemoryState before = state();
        List<ChatHistory> source = rows();
        assertFailed(() -> lifecycle.clear(scope.chatId()));
        assertEquals(before, state());
        assertEquals(source, rows());
    }

    @Test
    void lifecycleCommitSurvivesRollbackOfAnUnrelatedAmbientTransaction() {
        completeTurn();
        long generation = state().getGeneration();
        new TransactionTemplate(transactionManager).executeWithoutResult(status -> {
            lifecycle.clear(scope.chatId());
            status.setRollbackOnly();
        });
        assertEquals(generation + 1, state().getGeneration());
        assertTrue(rows().stream().allMatch(row -> row.getEnabled() == 0));
    }

    enum Mutation {
        DISABLE,
        FINGERPRINT,
        OWNER,
        CHARACTER,
        STORE,
        ROLLBACK,
        CLEAR,
        DELETE
    }

    @ParameterizedTest
    @EnumSource(Mutation.class)
    void concurrentClaimAndSourceWritersWaitForLifecycleCommitThenRejectStaleWork(Mutation mutation) throws Exception {
        completeTurn();
        Claim oldClaim = claim();
        var snapshot = publication.snapshot(scope, Operation.IDLE, state().getLatestFinalizedId(), oldClaim.token());
        TurnLease live = turns.begin(scope, FINGERPRINT);
        append(live);
        publication.checkSnapshot(snapshot);
        List<ChatHistory> source = rows();
        CountDownLatch saved = new CountDownLatch(1);
        CountDownLatch commit = new CountDownLatch(1);
        MemoryLifecycleRepository gated = repository(gateAfterRealSave(saved, commit));
        try (var workers = Executors.newFixedThreadPool(3)) {
            var change = workers.submit(() -> mutate(gated, mutation));
            try {
                assertTrue(saved.await(10, TimeUnit.SECONDS), "Lifecycle must reach its real SQL save");
                var sourceWriter = workers.submit(() -> assertThrows(IllegalStateException.class, () -> append(live)));
                var claimWriter =
                        workers.submit(() -> assertThrows(IllegalStateException.class, () -> work.renew(oldClaim)));
                try {
                    awaitRowWaiters(2);
                } finally {
                    commit.countDown();
                }
                change.get(15, TimeUnit.SECONDS);
                sourceWriter.get(15, TimeUnit.SECONDS);
                claimWriter.get(15, TimeUnit.SECONDS);
            } finally {
                commit.countDown();
            }
        }
        assertRevoked(state());
        assertFenced(live, oldClaim);
        assertThrows(IllegalStateException.class, () -> publication.checkSnapshot(snapshot));
        assertSourcesPreserved(source);
        assertEquals(
                source.size() + (mutation == Mutation.CLEAR || mutation == Mutation.DELETE ? 0 : 1), rows().size());
    }

    @Test
    void activationRechecksOwnerAfterWaitingForConcurrentStateWriter() throws Exception {
        ChatMemoryState before = state();
        try (Connection blocker = dataSource.getConnection();
                var worker = Executors.newSingleThreadExecutor()) {
            blocker.setAutoCommit(false);
            try (var lock =
                    blocker.prepareStatement("SELECT chat_id FROM chat_memory_state WHERE chat_id = ? FOR UPDATE")) {
                lock.setString(1, scope.chatId());
                try (var result = lock.executeQuery()) {
                    assertTrue(result.next());
                }
            }
            var activation = worker.submit(() -> {
                try {
                    lifecycle.activate(scope, REPLACEMENT);
                    return true;
                } catch (IllegalStateException failure) {
                    assertEquals(FAILURE, failure.getMessage());
                    return false;
                }
            });
            try {
                awaitRowWaiters(1);
                try (var update =
                        blocker.prepareStatement("UPDATE chat_context SET user_id = 'new-owner' WHERE chat_id = ?")) {
                    update.setString(1, scope.chatId());
                    assertEquals(1, update.executeUpdate());
                }
            } finally {
                blocker.commit();
            }
            assertFalse(
                    activation.get(15, TimeUnit.SECONDS), "Activation must reject an owner changed while it waited");
        }
        assertEquals(before, state());
    }

    @Test
    void rollbackHasNoThousandMessageCap() {
        List<Object[]> batch = new ArrayList<>();
        String message = ChatMessageSerializer.messageToJson(UserMessage.from("small source"));
        for (int i = 0; i < 1005; i++) {
            batch.add(new Object[] {scope.chatId(), message});
        }
        sql.batchUpdate(
                "INSERT INTO chat_history (memory_id, message, gmt_create, gmt_modified) VALUES (?, ?,"
                        + " UTC_TIMESTAMP(), UTC_TIMESTAMP())",
                batch);
        List<ChatHistory> source = rows();
        List<Long> expected = source.stream()
                .map(ChatHistory::getId)
                .sorted(Comparator.reverseOrder())
                .toList();
        assertEquals(expected, lifecycle.rollback(scope.chatId(), 1005));
        assertTrue(rows().stream().allMatch(row -> row.getEnabled() == 0));
        assertSourcesPreserved(source);
    }

    @Test
    void rollbackOfOversizedOriginalSourceDoesNotSortJsonOrRewritePayloads() {
        String large = "large original source 中文 ".repeat(80_000);
        ChatHistory source = insertMessage(scope.chatId(), large, null, "message");
        assertEquals(List.of(source.getId()), lifecycle.rollback(scope.chatId(), 1));
        assertSourcesPreserved(List.of(source));
        assertEquals(
                (byte) 0,
                histories.selectByPrimaryKey(source.getId()).orElseThrow().getEnabled());
    }

    private MemoryLifecycleRepository repository(ChatMemoryStateMapper mapper) {
        return new MemoryLifecycleRepository(coordination, mapper, histories, contexts, transactionManager, properties);
    }

    private void mutate(MemoryLifecycleRepository target, Mutation mutation) {
        switch (mutation) {
            case DISABLE -> target.disable(scope.chatId());
            case FINGERPRINT -> target.activate(scope, REPLACEMENT);
            case OWNER -> {
                sql.update("UPDATE chat_context SET user_id = 'new-owner' WHERE chat_id = ?", scope.chatId());
                target.activate(
                        new MemoryScope(scope.chatId(), "new-owner", scope.characterUid(), 1, scope.storeType()),
                        FINGERPRINT);
            }
            case CHARACTER ->
                target.activate(
                        new MemoryScope(scope.chatId(), scope.userId(), "new-character", 1, scope.storeType()),
                        FINGERPRINT);
            case STORE ->
                target.activate(
                        new MemoryScope(
                                scope.chatId(),
                                scope.userId(),
                                scope.characterUid(),
                                1,
                                EmbeddingStoreType.EN_LONG_TERM_MEMORY),
                        FINGERPRINT);
            case ROLLBACK -> target.rollback(scope.chatId(), 1);
            case CLEAR -> target.clear(scope.chatId());
            case DELETE -> target.delete(scope.chatId());
        }
    }

    private static ChatMemoryStateMapper gateAfterRealSave(CountDownLatch saved, CountDownLatch commit) {
        return (ChatMemoryStateMapper) Proxy.newProxyInstance(
                ChatMemoryStateMapper.class.getClassLoader(),
                new Class<?>[] {ChatMemoryStateMapper.class},
                (proxy, method, arguments) -> {
                    Object result;
                    try {
                        result = method.invoke(states, arguments);
                    } catch (InvocationTargetException failure) {
                        throw failure.getCause();
                    }
                    if (method.getName().equals("updateByPrimaryKey")) {
                        saved.countDown();
                        if (!commit.await(15, TimeUnit.SECONDS)) {
                            throw new IllegalStateException("Lifecycle test commit gate timed out");
                        }
                    }
                    return result;
                });
    }

    private static void awaitRowWaiters(int count) {
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(10);
        while (System.nanoTime() < deadline) {
            if (sql.queryForObject("SELECT COUNT(*) FROM performance_schema.data_lock_waits", Integer.class) >= count) {
                return;
            }
        }
        fail("Expected writers to wait on the real lifecycle row lock");
    }

    private void assertFenced(TurnLease live, Claim claim) {
        assertThrows(IllegalStateException.class, () -> turns.check(live));
        assertThrows(IllegalStateException.class, () -> turns.renew(live));
        assertThrows(IllegalStateException.class, () -> append(live));
        assertThrows(IllegalStateException.class, () -> turns.complete(live, AiMessage.from("late"), null, null));
        assertThrows(IllegalStateException.class, () -> turns.abort(live));
        assertThrows(IllegalStateException.class, () -> work.check(claim));
        assertThrows(IllegalStateException.class, () -> work.renew(claim));
        assertThrows(IllegalStateException.class, () -> work.release(claim));
        assertThrows(IllegalStateException.class, () -> work.fail(claim));
    }

    private static void assertRevoked(ChatMemoryState state) {
        assertNull(state.getTurnToken());
        assertNull(state.getTurnLeaseUntil());
        assertNull(state.getTurnDeadline());
        assertNull(state.getClaimToken());
        assertNull(state.getClaimLeaseUntil());
        assertNull(state.getClaimDeadline());
    }

    private static void assertInvalidated(ChatMemoryState before, ChatMemoryState after) {
        assertEquals(before.getGeneration() + 1, after.getGeneration());
        assertEquals(before.getVersion() + 1, after.getVersion());
        assertEquals(0L, after.getOverflowThroughId());
        assertEquals(0L, after.getIdleThroughId());
        assertEquals(0L, after.getReconciledThroughId());
        assertNull(after.getSummaryId());
        assertNull(after.getProfileId());
        assertEquals((byte) 0, after.getProfileRevalidationPending());
        assertNull(after.getRetryAt());
        assertEquals(0, after.getRetryAttempts());
        assertEquals(before.getGmtCreate(), after.getGmtCreate());
    }

    private static void assertFailed(Runnable action) {
        IllegalStateException failure = assertThrows(IllegalStateException.class, action::run);
        assertEquals(IllegalStateException.class, failure.getClass());
        assertEquals(FAILURE, failure.getMessage());
        assertNull(failure.getCause());
        assertEquals(0, failure.getSuppressed().length);
    }

    private void seedHeads() {
        long through = state().getLatestFinalizedId();
        sql.update(
                """
                UPDATE chat_memory_state SET summary_id = ?, profile_id = ?, overflow_through_id = ?,
                    idle_through_id = ?, reconciled_through_id = ?, retry_attempts = 3,
                    retry_at = UTC_TIMESTAMP(6), profile_revalidation_pending = 0 WHERE chat_id = ?
                """,
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                through,
                through,
                through,
                scope.chatId());
    }

    private Claim claim() {
        sql.update(
                "UPDATE chat_memory_state SET due_at = UTC_TIMESTAMP(6) - INTERVAL 1 SECOND WHERE chat_id = ?",
                scope.chatId());
        return work.claim(scope.chatId()).orElseThrow();
    }

    private void completeTurn() {
        TurnLease lease = turns.begin(scope, FINGERPRINT);
        append(lease);
        turns.complete(lease, AiMessage.from("answer"), SystemMessage.from("system snapshot"), new TokenUsage(2, 3));
    }

    private long append(TurnLease lease) {
        return turns.append(
                lease,
                UserMessage.from("rendered input"),
                UserMessage.from("original input 中文"),
                SystemMessage.from("system snapshot"),
                Origin.USER_INPUT,
                new TokenUsage(1, 2));
    }

    private ChatMemoryState state() {
        return states.selectByPrimaryKey(scope.chatId()).orElseThrow();
    }

    private List<ChatHistory> rows() {
        // No SQL JSON filesort in the test oracle, including the oversized-source regression.
        return histories
                .select(query -> query.where(ChatHistoryDynamicSqlSupport.memoryId, isEqualTo(scope.chatId())))
                .stream()
                .sorted(Comparator.comparing(ChatHistory::getId))
                .toList();
    }

    private ChatHistory insertMessage(String chatId, String original, String turnId, String kind) {
        LocalDateTime timestamp = LocalDateTime.of(2025, 1, 2, 3, 4, 5);
        ChatHistory row = new ChatHistory()
                .withMemoryId(chatId)
                .withTurnId(turnId)
                .withRecordKind(kind)
                .withMessage(ChatMessageSerializer.messageToJson(UserMessage.from("rendered " + original)))
                .withSourceMessage(ChatMessageSerializer.messageToJson(UserMessage.from(original)))
                .withSystemMessageRef(snapshots.save(chatId, SystemMessage.from("retained system")))
                .withExt("{\"syntheticUsage\": 7}")
                .withMessageOrigin("user-input")
                .withEpisode(7L)
                .withTgMessageId(9L)
                .withGmtCreate(timestamp)
                .withGmtModified(timestamp);
        assertEquals(1, histories.insertSelective(row));
        return histories.selectByPrimaryKey(row.getId()).orElseThrow();
    }

    private static void assertSourcesPreserved(List<ChatHistory> before) {
        for (ChatHistory old : before) {
            ChatHistory actual = histories.selectByPrimaryKey(old.getId()).orElseThrow();
            assertEquals(old.getMemoryId(), actual.getMemoryId());
            assertEquals(old.getId(), actual.getId());
            assertEquals(old.getGmtCreate(), actual.getGmtCreate());
            assertEquals(old.getGmtModified(), actual.getGmtModified());
            assertEquals(old.getTurnId(), actual.getTurnId());
            assertEquals(old.getEpisode(), actual.getEpisode());
            assertEquals(old.getMessage(), actual.getMessage());
            assertEquals(old.getSourceMessage(), actual.getSourceMessage());
            assertEquals(old.getSystemMessageRef(), actual.getSystemMessageRef());
            assertEquals(old.getMessageOrigin(), actual.getMessageOrigin());
            assertEquals(old.getExt(), actual.getExt());
            assertEquals(old.getTgMessageId(), actual.getTgMessageId());
        }
    }

    private MemoryScope otherScope() {
        MemoryScope other = new MemoryScope(id(), scope.userId(), scope.characterUid(), 1, scope.storeType());
        insertContext(other);
        lifecycle.activate(other, FINGERPRINT);
        return other;
    }

    private static void insertContext(MemoryScope scope) {
        sql.update("""
            INSERT INTO chat_context (chat_id, user_id, backend_id, gmt_create, gmt_modified)
            VALUES (?, ?, 'synthetic-backend', UTC_TIMESTAMP(), UTC_TIMESTAMP())
            """, scope.chatId(), scope.userId());
    }

    private static MemoryScope withGeneration(MemoryScope original, long generation) {
        return new MemoryScope(
                original.chatId(), original.userId(), original.characterUid(), generation, original.storeType());
    }

    private static String id() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
