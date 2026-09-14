package fun.freechat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.store.embedding.filter.Filter;
import fun.freechat.mapper.ChatContextMapper;
import fun.freechat.mapper.ChatHistoryMapper;
import fun.freechat.mapper.ChatMemoryCommitMapper;
import fun.freechat.mapper.ChatMemoryCoordinationMapper;
import fun.freechat.mapper.ChatMemoryStateMapper;
import fun.freechat.model.ChatContext;
import fun.freechat.model.ChatHistory;
import fun.freechat.model.ChatMemoryState;
import fun.freechat.service.chat.SystemPromptSnapshotStore;
import fun.freechat.service.chat.memory.LongTermMemoryProperties;
import fun.freechat.service.chat.memory.MemoryGarbageCollector;
import fun.freechat.service.chat.memory.MemoryHistoryReconciler;
import fun.freechat.service.chat.memory.MemoryLifecycleRepository;
import fun.freechat.service.chat.memory.MemoryMaintenance;
import fun.freechat.service.chat.memory.MemoryModelResolver;
import fun.freechat.service.chat.memory.MemoryScope;
import fun.freechat.service.chat.memory.MemoryTurnRepository;
import fun.freechat.service.chat.memory.MemoryWorkRepository;
import fun.freechat.service.common.impl.LocalFileStoreImpl;
import fun.freechat.service.enums.EmbeddingStoreType;
import fun.freechat.service.rag.MemoryEmbeddingCleanupService;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.logging.nologging.NoLoggingImpl;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockMakers;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/** Recovery against owned SQL/Redis, without application bootstrap, credentials, models or files. */
@Testcontainers
@Timeout(120)
class MemoryMaintenanceIT {
    private static final String FINGERPRINT = "a".repeat(64);
    private static final String CHANGED = "b".repeat(64);
    private static final LocalDateTime OLD = LocalDateTime.of(2020, 1, 2, 3, 4, 5);
    private static final String CURSOR = "freechat:memory:maintenance:v1:cursor:";

    @Container
    private static final MySQLContainer<?> MYSQL =
            new MySQLContainer<>("mysql:8.0.36").withDatabaseName("freechat").withInitScript("sql/schema.sql");

    @Container
    private static final GenericContainer<?> REDIS =
            new GenericContainer<>("redis:7.4.2-alpine").withExposedPorts(6379);

    private static HikariDataSource dataSource;
    private static JdbcTemplate sql;
    private static ChatContextMapper contexts;
    private static ChatHistoryMapper histories;
    private static ChatMemoryStateMapper states;
    private static ChatMemoryCommitMapper commits;
    private static ChatMemoryCoordinationMapper coordination;
    private static DataSourceTransactionManager transactions;
    private static final Writes WRITES = new Writes();
    private final Map<String, MemoryModelResolver.Configuration> configurations = new HashMap<>();
    private final List<RedissonClient> clients = new ArrayList<>();

    @TempDir
    Path snapshotDirectory;

    private SystemPromptSnapshotStore snapshots;
    private LongTermMemoryProperties properties;
    private MemoryModelResolver models;
    private MemoryLifecycleRepository lifecycle;
    private MemoryTurnRepository turns;
    private MemoryHistoryReconciler reconciler;
    private MemoryWorkRepository work;
    private MemoryGarbageCollector garbage;
    private Cleanup cleanup;
    private RedissonClient redis;
    private MemoryMaintenance maintenance;

    @BeforeAll
    static void sqlOnly() throws Exception {
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
        configuration.addInterceptor(WRITES);
        for (Class<?> mapper : List.of(
                ChatContextMapper.class,
                ChatHistoryMapper.class,
                ChatMemoryStateMapper.class,
                ChatMemoryCommitMapper.class,
                ChatMemoryCoordinationMapper.class)) {
            configuration.addMapper(mapper);
        }
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setConfiguration(configuration);
        SqlSessionTemplate sessions = new SqlSessionTemplate(factory.getObject());
        contexts = sessions.getMapper(ChatContextMapper.class);
        histories = sessions.getMapper(ChatHistoryMapper.class);
        states = sessions.getMapper(ChatMemoryStateMapper.class);
        commits = sessions.getMapper(ChatMemoryCommitMapper.class);
        coordination = sessions.getMapper(ChatMemoryCoordinationMapper.class);
        transactions = new DataSourceTransactionManager(dataSource);
    }

    @BeforeEach
    void isolate() {
        sql.update("DELETE FROM chat_memory_commit");
        sql.update("DELETE FROM chat_memory_state");
        sql.update("DELETE FROM chat_history");
        sql.update("DELETE FROM chat_context");
        redis = client();
        redis.getKeys().flushdb(); // Only this fixture's private Testcontainers Redis.
        properties = new LongTermMemoryProperties();
        properties.setDispatchBatchSize(1);
        properties.afterPropertiesSet();
        models = mock(
                MemoryModelResolver.class,
                withSettings().mockMaker(MockMakers.SUBCLASS).defaultAnswer(call -> {
                    throw new AssertionError("Recovery must use configuration only, not "
                            + call.getMethod().getName());
                }));
        doAnswer(call -> Optional.ofNullable(configurations.get(call.getArgument(0))))
                .when(models)
                .configuration(anyString());
        lifecycle = new MemoryLifecycleRepository(coordination, states, histories, contexts, transactions, properties);
        var files = new LocalFileStoreImpl();
        org.springframework.test.util.ReflectionTestUtils.setField(files, "basePathStr", snapshotDirectory.toString());
        snapshots = new SystemPromptSnapshotStore(files);
        turns = new MemoryTurnRepository(coordination, states, histories, transactions, properties, snapshots);
        reconciler = new MemoryHistoryReconciler(coordination, states, histories, commits, transactions, properties);
        work = new MemoryWorkRepository(coordination, states, transactions, properties);
        cleanup = new Cleanup();
        garbage = new MemoryGarbageCollector(coordination, commits, cleanup, properties, transactions);
        maintenance = service(redis);
    }

    @AfterEach
    void closeClientsAndVerifyConfigurationOnly() {
        try {
            // These are the only entry points that can acquire credentials/build a model or a session.
            // A fail-fast default answer above also rejects any future resolver entry point.
            verify(models, never()).resolve(anyString());
            verify(models, never()).resolveWithConfiguration(anyString());
            assertTrue(mockingDetails(models).getInvocations().stream()
                    .allMatch(call -> call.getMethod().getName().equals("configuration")));
        } finally {
            clients.forEach(client -> client.shutdown(0, 5, TimeUnit.SECONDS));
        }
    }

    @AfterAll
    static void closeSql() {
        if (dataSource != null) {
            dataSource.close();
        }
    }

    @Test
    void idleNeverActivatedLegacyHistoryNeedsMultipleBoundedScansAndPartialProgressIsNotClaimable() {
        String id = context("idle-legacy");
        legacyPairs(id, 1253); // More than both 1000-row pages in a single contexts + states scan.
        List<Map<String, Object>> source = source(id);
        long last = source.getLast().get("id") instanceof Number number ? number.longValue() : -1;
        assertTrue(states.selectByPrimaryKey(id).isEmpty());
        maintenance.scan();
        assertEquals(2000, annotated(id));
        assertEquals("reconciling", reconciliationStatus(id));
        assertTrue(state(id).getLatestFinalizedId() > 0, "Eligibility must be blocked even after complete early turns");
        assertTrue(state(id).getDueAt().isBefore(coordination.databaseNow()));
        assertTrue(work.due(null, 1).isEmpty());
        assertTrue(work.claim(id).isEmpty());
        assertEquals(source, source(id));
        assertEquals(id, cursor("contexts").get());
        assertEquals(id, cursor("states").get());

        maintenance = service(client()); // A new recovery owner resumes durable progress, not process memory.
        maintenance.scan();
        assertEquals("", cursor("contexts").get());
        assertEquals("", cursor("states").get());
        assertEquals(2000, annotated(id));
        maintenance.scan();
        assertEquals(2506, annotated(id));
        assertEquals("reconciled", reconciliationStatus(id));
        assertEquals(last, state(id).getReconciledThroughId());
        assertEquals(last, state(id).getLatestFinalizedId());
        assertEquals(OLD, state(id).getLastActivity());
        assertEquals(OLD.plus(properties.getIdleTimeout()), state(id).getDueAt());
        assertEquals(source, source(id));
        assertEquals(List.of(id), work.due(null, 1));
        var claim = work.claim(id).orElseThrow();
        assertEquals(last, claim.throughId());
        work.release(claim);
        assertEquals(0, cleanup.lookups.size(), "Legacy vector deletion must observe its grace period");
    }

    @Test
    void partialReconcileBlocksBothDiscoveryAndClaimUntilFreshRecoveryFinishes() {
        String id = context("partial-recovery");
        legacyPairs(id, 501);
        List<Map<String, Object>> before = source(id);
        maintenance.recover(id);
        assertEquals(1000, annotated(id));
        assertEquals("reconciling", reconciliationStatus(id));
        assertTrue(state(id).getLatestFinalizedId() > 0);
        assertTrue(state(id).getDueAt().isBefore(coordination.databaseNow()));
        assertTrue(work.due(null, 1).isEmpty());
        ChatMemoryState partial = state(id);
        assertTrue(work.claim(id).isEmpty());
        assertEquals(partial, state(id), "Rejected claims must not change partial reconciliation state");
        maintenance = service(client());
        maintenance.recover(id);
        assertEquals(1002, annotated(id));
        assertEquals("reconciled", reconciliationStatus(id));
        assertEquals(before, source(id));
        assertEquals(List.of(id), work.due(null, 1));
        assertEquals(
                state(id).getLatestFinalizedId().longValue(),
                work.claim(id).orElseThrow().throughId());
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void expiredActiveTurnWithNoFinalizedHistoryIsRecoveredWithoutWaitingForDueWork(boolean deadlineExpired) {
        String id = context("expired-turn");
        maintenance.recover(id);
        var lease = turns.begin(scope(id), FINGERPRINT);
        assertEquals(0L, state(id).getLatestFinalizedId());
        assertTrue(work.due(null, 1).isEmpty());
        sql.update(
                "UPDATE chat_memory_state SET " + (deadlineExpired ? "turn_deadline" : "turn_lease_until")
                        + " = UTC_TIMESTAMP(6) - INTERVAL 1 SECOND WHERE chat_id = ?",
                id);
        long revision = state(id).getTurnRevision();
        maintenance.scan();
        ChatMemoryState recovered = state(id);
        assertNull(recovered.getTurnToken());
        assertNull(recovered.getTurnDeadline());
        assertNull(recovered.getTurnLeaseUntil());
        assertEquals(revision + 1, recovered.getTurnRevision());
        var marker =
                histories.selectByPrimaryKey(recovered.getLatestFinalizedId()).orElseThrow();
        assertEquals("turn-abort", marker.getRecordKind());
        assertEquals(lease.token(), marker.getTurnId());
        assertNull(marker.getMessage());
        assertEquals("reconciled", reconciliationStatus(id));
        assertThrows(IllegalStateException.class, () -> turns.check(lease));
        maintenance.recover(id);
        assertEquals(recovered, state(id));
        assertEquals(2, source(id).size(), "Exactly one start and one recovered abort, without duplicate terminals");
        sql.update("UPDATE chat_memory_state SET due_at = UTC_TIMESTAMP(6) - INTERVAL 1 SECOND WHERE chat_id = ?", id);
        assertEquals(List.of(id), work.due(null, 1));
        assertEquals(marker.getId().longValue(), work.claim(id).orElseThrow().throughId());
    }

    @Test
    void liveSqlTurnIsNotStolenEvenWhenItsRedisCoordinationLockDisappears() {
        String id = context("live-turn");
        maintenance.recover(id);
        var lease = turns.begin(scope(id), FINGERPRINT);
        ChatMemoryState before = state(id);
        List<Map<String, Object>> history = source(id);
        configurations.remove(id); // A zero setting must still not steal a live SQL lease.
        clearInvocations(models);
        RedissonClient owner = client();
        String key = "freechat:chat:coordination:v1:" + id;
        var lock = owner.getLock(key);
        lock.lock();
        try {
            maintenance.recover(id);
            assertEquals(before, state(id));
            assertTrue(redis.getBucket(key).delete(), "Simulate loss of this owned lock key only");
            maintenance.recover(id);
            assertEquals(before, state(id));
            assertEquals(history, source(id));
            assertFalse(redis.getBucket(key).isExists());
            turns.check(lease);
            verifyNoInteractions(models);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Test
    void keysetWrapAndDeletedCursorsDoNotLetBusyOrFailingRowsStarveSuccessors() {
        String busy = context("a-busy");
        String broken = context("b-error");
        String healthy = context("c-healthy");
        legacyPairs(healthy, 1);
        doThrow(new IllegalStateException("private configuration detail"))
                .when(models)
                .configuration(broken);
        var lock = client().getLock("freechat:chat:coordination:v1:" + busy);
        lock.lock();
        try {
            maintenance.scan();
            assertEquals(busy, cursor("contexts").get());
            assertTrue(states.selectByPrimaryKey(busy).isEmpty());
            maintenance.scan();
            assertEquals(broken, cursor("contexts").get());
            assertTrue(states.selectByPrimaryKey(broken).isEmpty());
            maintenance.scan();
            assertEquals(healthy, cursor("contexts").get());
            assertEquals(List.of(healthy), work.due(null, 1));
            maintenance.scan();
            assertEquals("", cursor("contexts").get());
            maintenance.scan();
            assertEquals(busy, cursor("contexts").get());
        } finally {
            lock.unlock();
        }
        doAnswer(call -> Optional.of(configurations.get(broken))).when(models).configuration(broken);
        assertTrue(cursor("contexts").delete());
        cursor("states").delete();
        maintenance = service(client());
        maintenance.scan();
        assertEquals(busy, cursor("contexts").get());
        assertEquals("active", state(busy).getStatus());
        maintenance.scan();
        assertEquals(broken, cursor("contexts").get());
        assertEquals("active", state(broken).getStatus());
        maintenance.scan();
        assertEquals(healthy, cursor("contexts").get());
        assertEquals(List.of(healthy), work.due(null, 1));
    }

    @Test
    void modelFingerprintChangeRevalidatesExistingProfileWithoutResettingIdentityOrCursors() {
        String id = context("model-change");
        legacyPairs(id, 1);
        maintenance.recover(id);
        sql.update(
                "UPDATE chat_memory_state SET idle_through_id = latest_finalized_id, overflow_through_id ="
                        + " latest_finalized_id, profile_id = 'profile-head', summary_id = 'summary-head', due_at = NULL,"
                        + " retry_attempts = ? WHERE chat_id = ?",
                properties.getMaxAttempts(),
                id);
        ChatMemoryState before = state(id);
        configurations.put(id, configured(CHANGED)); // Models/baselines are represented by the resolved fingerprint.
        maintenance.recover(id);
        ChatMemoryState after = state(id);
        assertEquals(before.getGeneration(), after.getGeneration());
        assertEquals(before.getUserId(), after.getUserId());
        assertEquals(before.getCharacterUid(), after.getCharacterUid());
        assertEquals(before.getStoreType(), after.getStoreType());
        assertEquals(before.getIdleThroughId(), after.getIdleThroughId());
        assertEquals(before.getOverflowThroughId(), after.getOverflowThroughId());
        assertEquals(before.getLatestFinalizedId(), after.getLatestFinalizedId());
        assertEquals(before.getProfileId(), after.getProfileId());
        assertEquals(before.getSummaryId(), after.getSummaryId());
        assertEquals(CHANGED, after.getFingerprint());
        assertEquals((byte) 1, after.getProfileRevalidationPending());
        assertEquals(0, after.getRetryAttempts());
        assertNull(after.getRetryAt());
        assertEquals(List.of(id), work.due(null, 1));
        var claim = work.claim(id).orElseThrow();
        assertTrue(claim.revalidation());
        assertEquals(claim.idleThroughId(), claim.throughId());
        assertEquals(CHANGED, claim.fingerprint());
        work.release(claim);
    }

    @Test
    void zeroConfigurationDisablesAndFencesClaimThenReenableRestoresUnfinishedWork() {
        String id = context("zero-setting");
        legacyPairs(id, 1);
        maintenance.recover(id);
        var old = work.claim(id).orElseThrow();
        configurations.remove(id); // configuration() returns empty for a zero memory setting.
        maintenance.recover(id);
        ChatMemoryState disabled = state(id);
        assertEquals("disabled", disabled.getStatus());
        assertNull(disabled.getDueAt());
        assertNull(disabled.getClaimToken());
        assertNull(disabled.getClaimLeaseUntil());
        assertNull(disabled.getClaimDeadline());
        assertTrue(work.due(null, 1).isEmpty());
        assertTrue(work.claim(id).isEmpty());
        assertThrows(IllegalStateException.class, () -> work.check(old));
        maintenance.recover(id);
        assertEquals(disabled, state(id));
        configurations.put(id, configured(FINGERPRINT));
        maintenance.recover(id);
        assertEquals("active", state(id).getStatus());
        assertEquals(disabled.getGeneration(), state(id).getGeneration());
        assertEquals(disabled.getLatestFinalizedId(), state(id).getLatestFinalizedId());
        assertEquals(List.of(id), work.due(null, 1));
    }

    @Test
    void exhaustedRetriesStayExhaustedAcrossUnchangedRecoveryAndRestart() {
        String id = context("exhausted");
        legacyPairs(id, 1);
        maintenance.recover(id);
        for (int index = 0; index < properties.getMaxAttempts(); index++) {
            var claim = work.claim(id).orElseThrow();
            work.fail(claim);
            sql.update(
                    "UPDATE chat_memory_state SET retry_at = UTC_TIMESTAMP(6) - INTERVAL 1 SECOND WHERE chat_id = ?",
                    id);
        }
        ChatMemoryState exhausted = state(id);
        assertEquals(properties.getMaxAttempts(), exhausted.getRetryAttempts());
        maintenance.recover(id);
        maintenance.scan();
        maintenance = service(client());
        maintenance.recover(id);
        assertEquals(exhausted, state(id));
        assertTrue(work.due(null, 1).isEmpty());
        assertTrue(work.claim(id).isEmpty());
    }

    @Test
    void orphanStateIsTombstonedAndLegacyCleanupRunsThroughRealGarbageCollector() {
        String id = context("orphan");
        legacyPairs(id, 1);
        maintenance.recover(id);
        long generation = state(id).getGeneration();
        contexts.deleteByPrimaryKey(id);
        clearInvocations(models);
        maintenance.scan();
        ChatMemoryState deleted = state(id);
        assertEquals("deleted", deleted.getStatus());
        assertEquals(generation + 1, deleted.getGeneration());
        assertEquals(0L, deleted.getLatestFinalizedId());
        assertNull(deleted.getDueAt());
        assertTrue(source(id).stream().allMatch(row -> ((Number) row.get("enabled")).intValue() == 0));
        assertTrue(work.claim(id).isEmpty());
        verifyNoInteractions(models);
        sql.update(
                "UPDATE chat_memory_commit SET gc_after = UTC_TIMESTAMP(6) - INTERVAL 1 SECOND "
                        + "WHERE chat_id = ? AND operation = 'LEGACY_GC'",
                id);
        cursor("attempts").delete();
        maintenance.scan();
        assertEquals(
                List.of(
                        EmbeddingStoreType.EN_LONG_TERM_MEMORY,
                        EmbeddingStoreType.ZH_LONG_TERM_MEMORY,
                        EmbeddingStoreType.DEFAULT_LONG_TERM_MEMORY),
                cleanup.lookups);
        assertEquals(3, cleanup.removals);
        assertEquals(deleted, state(id));
        assertTrue(
                garbage.candidates(null, 1).isEmpty(), "Cleanup tombstone remains but is deferred until grace elapses");
    }

    @Test
    void unchangedCompletedReconciliationIsReadOnlyAndRecoveryDoesNotChurnVersions() {
        String id = context("unchanged");
        legacyPairs(id, 2);
        maintenance.recover(id);
        ChatMemoryState before = state(id);
        List<Map<String, Object>> attempts = sql.queryForList("SELECT * FROM chat_memory_commit ORDER BY attempt_id");
        List<Map<String, Object>> history = sql.queryForList("SELECT * FROM chat_history ORDER BY id");
        WRITES.count.set(0);
        assertTrue(reconciler.reconcile(scope(id), FINGERPRINT, List.of()).complete());
        assertTrue(reconciler.reconcile(scope(id), FINGERPRINT, List.of()).complete());
        assertEquals(
                0, WRITES.count.get(), "No INSERT/UPDATE/DELETE statements for completed unchanged reconciliation");
        maintenance.recover(id);
        maintenance.scan();
        assertEquals(before, state(id));
        assertEquals(attempts, sql.queryForList("SELECT * FROM chat_memory_commit ORDER BY attempt_id"));
        assertEquals(history, sql.queryForList("SELECT * FROM chat_history ORDER BY id"));
    }

    @Test
    void recoveryUsesExactConfiguredExamplesRatherThanTreatingThemAsLegacyEvidence() {
        String id = context("examples");
        List<ChatMessage> examples =
                List.of(UserMessage.from("configured question"), AiMessage.from("configured answer"));
        configurations.put(
                id, new MemoryModelResolver.Configuration("owner", "character", "en", FINGERPRINT, examples));
        long example = legacy(id, examples.getFirst());
        legacy(id, examples.getLast());
        long actual = legacy(id, UserMessage.from("real input"));
        long last = legacy(id, AiMessage.from("real answer"));
        maintenance.recover(id);
        assertEquals(
                "template-example",
                histories.selectByPrimaryKey(example).orElseThrow().getMessageOrigin());
        assertNull(histories.selectByPrimaryKey(example).orElseThrow().getTurnId());
        assertEquals(
                "turn-start", histories.selectByPrimaryKey(actual).orElseThrow().getRecordKind());
        assertEquals(last, state(id).getLatestFinalizedId());
        verify(models).configuration(id);
    }

    private MemoryMaintenance service(RedissonClient client) {
        return new MemoryMaintenance(
                contexts, states, coordination, models, lifecycle, turns, reconciler, garbage, client, properties);
    }

    private RedissonClient client() {
        Config config = new Config();
        config.setThreads(2).setNettyThreads(2);
        config.useSingleServer()
                .setAddress("redis://" + REDIS.getHost() + ":" + REDIS.getMappedPort(6379))
                .setConnectionMinimumIdleSize(1)
                .setConnectionPoolSize(4)
                .setSubscriptionConnectionMinimumIdleSize(1)
                .setSubscriptionConnectionPoolSize(2);
        RedissonClient client = Redisson.create(config);
        clients.add(client);
        return client;
    }

    private String context(String id) {
        assertEquals(
                1,
                contexts.insertSelective(new ChatContext()
                        .withChatId(id)
                        .withUserId("owner")
                        .withBackendId("backend")
                        .withChatType("u2c")
                        .withGmtCreate(OLD)
                        .withGmtModified(OLD)));
        configurations.put(id, configured(FINGERPRINT));
        return id;
    }

    private static MemoryModelResolver.Configuration configured(String fingerprint) {
        return new MemoryModelResolver.Configuration("owner", "character", "en", fingerprint, List.of());
    }

    private void legacyPairs(String id, int pairs) {
        List<Object[]> rows = new ArrayList<>();
        for (int index = 0; index < pairs; index++) {
            rows.add(new Object[] {
                id, OLD, OLD, ChatMessageSerializer.messageToJson(UserMessage.from("private input " + index))
            });
            rows.add(new Object[] {
                id, OLD, OLD, ChatMessageSerializer.messageToJson(AiMessage.from("private final " + index))
            });
        }
        sql.batchUpdate(
                "INSERT INTO chat_history (memory_id, gmt_create, gmt_modified, message) VALUES (?, ?, ?, ?)", rows);
    }

    private long legacy(String id, ChatMessage message) {
        ChatHistory row = new ChatHistory()
                .withMemoryId(id)
                .withGmtCreate(OLD)
                .withGmtModified(OLD)
                .withMessage(ChatMessageSerializer.messageToJson(message));
        assertEquals(1, histories.insertSelective(row));
        return row.getId();
    }

    private ChatMemoryState state(String id) {
        return states.selectByPrimaryKey(id).orElseThrow();
    }

    private MemoryScope scope(String id) {
        ChatMemoryState state = state(id);
        return new MemoryScope(
                id,
                state.getUserId(),
                state.getCharacterUid(),
                state.getGeneration(),
                EmbeddingStoreType.of(state.getStoreType()));
    }

    private RBucket<String> cursor(String category) {
        return redis.getBucket(CURSOR + category, StringCodec.INSTANCE);
    }

    private int annotated(String id) {
        return sql.queryForObject(
                "SELECT COUNT(*) FROM chat_history WHERE memory_id = ? AND turn_id IS NOT NULL", Integer.class, id);
    }

    private String reconciliationStatus(String id) {
        return sql.queryForObject(
                "SELECT status FROM chat_memory_commit WHERE chat_id = ? AND operation = 'RECONCILE'",
                String.class,
                id);
    }

    private List<Map<String, Object>> source(String id) {
        return sql.queryForList(
                "SELECT id, memory_id, message, source_message, system_message_ref, ext, enabled, gmt_create, gmt_modified "
                        + "FROM chat_history WHERE memory_id = ? ORDER BY id",
                id);
    }

    @Intercepts(
            @Signature(
                    type = Executor.class,
                    method = "update",
                    args = {MappedStatement.class, Object.class}))
    static final class Writes implements Interceptor {
        private final AtomicInteger count = new AtomicInteger();

        @Override
        public Object intercept(Invocation invocation) throws Throwable {
            count.incrementAndGet();
            return invocation.proceed();
        }
    }

    private static final class Cleanup implements MemoryEmbeddingCleanupService {
        private final List<EmbeddingStoreType> lookups = new ArrayList<>();
        private int removals;

        @Override
        public void removeExact(EmbeddingStoreType type, List<String> ids, Filter scope) {
            fail("This fixture contains only legacy cleanup candidates");
        }

        @Override
        public List<String> legacyIds(EmbeddingStoreType type, String chatId, int limit) {
            assertEquals("orphan", chatId);
            assertEquals(100, limit);
            lookups.add(type);
            return List.of("00000000-0000-0000-0000-000000000001");
        }

        @Override
        public void removeLegacy(EmbeddingStoreType type, String chatId, List<String> ids) {
            assertEquals("orphan", chatId);
            assertEquals(List.of("00000000-0000-0000-0000-000000000001"), ids);
            removals++;
        }
    }
}
