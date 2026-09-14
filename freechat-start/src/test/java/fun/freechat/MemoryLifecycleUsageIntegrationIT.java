package fun.freechat;

import static fun.freechat.service.util.CacheUtils.LONG_PERIOD_CACHE_NAME;
import static fun.freechat.service.util.CacheUtils.MIDDLE_PERIOD_CACHE_NAME;
import static org.junit.jupiter.api.Assertions.*;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.output.TokenUsage;
import fun.freechat.mapper.ChatContextMapper;
import fun.freechat.mapper.ChatHistoryMapper;
import fun.freechat.mapper.ChatMemoryCommitMapper;
import fun.freechat.mapper.ChatMemoryCoordinationMapper;
import fun.freechat.mapper.ChatMemoryStateMapper;
import fun.freechat.model.ChatContext;
import fun.freechat.model.ChatHistory;
import fun.freechat.model.ChatMemoryState;
import fun.freechat.service.cache.FullNameKeyGenerator;
import fun.freechat.service.chat.ChatContextService;
import fun.freechat.service.chat.ChatMessageRecord;
import fun.freechat.service.chat.MemoryUsage;
import fun.freechat.service.chat.SystemPromptSnapshotStore;
import fun.freechat.service.chat.impl.ChatContextServiceImpl;
import fun.freechat.service.chat.impl.ChatTaskQueueManager;
import fun.freechat.service.chat.impl.MysqlChatMemoryStoreImpl;
import fun.freechat.service.chat.memory.LongTermMemoryProperties;
import fun.freechat.service.chat.memory.MemoryLifecycleRepository;
import fun.freechat.service.chat.memory.MemoryPublicationRepository;
import fun.freechat.service.chat.memory.MemoryScope;
import fun.freechat.service.chat.memory.MemoryTurnRepository;
import fun.freechat.service.chat.memory.MemoryTurnRepository.Origin;
import fun.freechat.service.chat.memory.MemoryTurnRepository.TurnLease;
import fun.freechat.service.chat.memory.MemoryWorkRepository;
import fun.freechat.service.common.EncryptionService;
import fun.freechat.service.common.impl.LocalFileStoreImpl;
import fun.freechat.service.enums.EmbeddingStoreType;
import fun.freechat.service.util.CacheUtils;
import fun.freechat.service.util.InfoUtils;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.ibatis.logging.nologging.NoLoggingImpl;
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
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.AnnotationCacheOperationSource;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.interceptor.CacheInterceptor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.test.util.ReflectionTestUtils;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/** Real isolated SQL plus a deterministic same-thread watchdog-lock double. No providers or payload files. */
@Testcontainers
@Timeout(60)
class MemoryLifecycleUsageIntegrationIT {
    @Container
    private static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.0.36")
            .withUsername("root")
            .withDatabaseName("freechat")
            .withInitScript("sql/schema.sql");

    private static final String FINGERPRINT = "a".repeat(64);
    private static HikariDataSource dataSource;
    private static JdbcTemplate sql;
    private static ChatContextMapper contexts;
    private static ChatHistoryMapper histories;
    private static ChatMemoryStateMapper states;
    private static ChatMemoryCommitMapper commits;
    private static ChatMemoryCoordinationMapper coordination;
    private static DataSourceTransactionManager transactions;
    private static final UsageQueryProbe usageQueries = new UsageQueryProbe();
    private final ReentrantLock lock = new ReentrantLock();
    private final ConcurrentMapCacheManager cache =
            new ConcurrentMapCacheManager(LONG_PERIOD_CACHE_NAME, MIDDLE_PERIOD_CACHE_NAME);
    private final ConcurrentMapCacheManager local = new ConcurrentMapCacheManager(LONG_PERIOD_CACHE_NAME);
    private CacheManager previousCache;
    private CacheManager previousLocal;
    private ChatTaskQueueManager queues;
    private ChatContextServiceImpl service;
    private MysqlChatMemoryStoreImpl store;

    @TempDir
    Path snapshotDirectory;

    private SystemPromptSnapshotStore snapshots;
    private LongTermMemoryProperties properties;
    private MemoryLifecycleRepository lifecycle;
    private MemoryTurnRepository turns;
    private MemoryWorkRepository work;
    private MemoryPublicationRepository publication;
    private MemoryScope scope;
    private int acquisitions;

    @BeforeAll
    static void configureSql() throws Exception {
        HikariConfig pool = new HikariConfig();
        pool.setJdbcUrl(MYSQL.getJdbcUrl());
        pool.setUsername(MYSQL.getUsername());
        pool.setPassword(MYSQL.getPassword());
        pool.setMaximumPoolSize(6);
        pool.setConnectionInitSql("SET time_zone = '+00:00'");
        dataSource = new HikariDataSource(pool);
        sql = new JdbcTemplate(dataSource);
        Configuration configuration = new Configuration();
        configuration.setLogImpl(NoLoggingImpl.class);
        configuration.addInterceptor(usageQueries);
        configuration.addMapper(ChatContextMapper.class);
        configuration.addMapper(ChatHistoryMapper.class);
        configuration.addMapper(ChatMemoryStateMapper.class);
        configuration.addMapper(ChatMemoryCommitMapper.class);
        configuration.addMapper(ChatMemoryCoordinationMapper.class);
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

    @AfterAll
    static void closePool() {
        if (dataSource != null) {
            dataSource.close();
        }
    }

    @BeforeEach
    void setup() {
        properties = new LongTermMemoryProperties();
        properties.afterPropertiesSet();
        lifecycle = repository(contexts);
        var files = new LocalFileStoreImpl();
        ReflectionTestUtils.setField(files, "basePathStr", snapshotDirectory.toString());
        snapshots = new SystemPromptSnapshotStore(files);
        turns = new MemoryTurnRepository(coordination, states, histories, transactions, properties, snapshots);
        work = new MemoryWorkRepository(coordination, states, transactions, properties);
        publication =
                new MemoryPublicationRepository(coordination, states, commits, histories, transactions, properties);
        scope = new MemoryScope(id(), "owner", "character", 1, EmbeddingStoreType.DEFAULT_LONG_TERM_MEMORY);
        contexts.insertSelective(new ChatContext()
                .withChatId(scope.chatId())
                .withUserId(scope.userId())
                .withBackendId("backend")
                .withApiKeyValue("encrypted:synthetic")
                .withUserProfile("baseline")
                .withAbout("scene")
                .withUserNickname("user")
                .withCharacterNickname("character")
                .withApiKeyName("key-name")
                .withGmtCreate(LocalDateTime.now())
                .withGmtModified(LocalDateTime.now()));
        lifecycle.activate(scope, FINGERPRINT);
        RLock watchdog = (RLock) Proxy.newProxyInstance(
                RLock.class.getClassLoader(), new Class<?>[] {RLock.class}, (proxy, method, args) -> {
                    switch (method.getName()) {
                        case "lock" -> {
                            assertTrue(args == null || args.length == 0, "Use watchdog lock, not a fixed lease");
                            lock.lock();
                            acquisitions++;
                            return null;
                        }
                        case "unlock" -> {
                            assertTrue(lock.isHeldByCurrentThread(), "Unlock must run on the acquiring thread");
                            lock.unlock();
                            return null;
                        }
                        default -> throw new AssertionError("Unexpected lock operation");
                    }
                });
        RedissonClient redis = (RedissonClient) Proxy.newProxyInstance(
                RedissonClient.class.getClassLoader(), new Class<?>[] {RedissonClient.class}, (proxy, method, args) -> {
                    assertEquals("getLock", method.getName());
                    assertEquals("freechat:chat:coordination:v1:" + scope.chatId(), args[0]);
                    return watchdog;
                });
        queues = new ChatTaskQueueManager(redis);
        service = new ChatContextServiceImpl();
        ReflectionTestUtils.setField(service, "chatContextMapper", contexts);
        ReflectionTestUtils.setField(service, "lifecycle", lifecycle);
        ReflectionTestUtils.setField(service, "queueManager", queues);
        ReflectionTestUtils.setField(service, "encryptionService", new EncryptionService() {
            public String encrypt(String value) {
                return value == null ? null : "encrypted:" + value;
            }

            public String decrypt(String value) {
                return value == null ? null : value.substring("encrypted:".length());
            }
        });
        store = new MysqlChatMemoryStoreImpl();
        ReflectionTestUtils.setField(store, "chatHistoryMapper", histories);
        ReflectionTestUtils.setField(store, "chatMemoryCoordinationMapper", coordination);
        ReflectionTestUtils.setField(store, "lifecycle", lifecycle);
        ReflectionTestUtils.setField(store, "queueManager", queues);
        ReflectionTestUtils.setField(store, "cacheManager", cache);
        ReflectionTestUtils.setField(store, "maxSize", 1000);
        previousCache = (CacheManager) ReflectionTestUtils.getField(CacheUtils.class, "defaultCacheManager");
        previousLocal = (CacheManager) ReflectionTestUtils.getField(CacheUtils.class, "inProcessCacheManager");
        ReflectionTestUtils.setField(CacheUtils.class, "defaultCacheManager", cache);
        ReflectionTestUtils.setField(CacheUtils.class, "inProcessCacheManager", local);
    }

    @AfterEach
    void teardown() {
        assertFalse(lock.isLocked());
        if (queues != null) {
            queues.stop();
        }
        ReflectionTestUtils.setField(CacheUtils.class, "defaultCacheManager", previousCache);
        ReflectionTestUtils.setField(CacheUtils.class, "inProcessCacheManager", previousLocal);
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "userId",
                "backendId",
                "userNickname",
                "characterNickname",
                "userProfile",
                "about",
                "apiKeyName",
                "apiKeyValue"
            })
    void relevantSelectiveChangesFenceOldWorkButKeepScopeUntilAdmission(String field) {
        complete();
        sql.update(
                "UPDATE chat_memory_state SET due_at = UTC_TIMESTAMP(6) - INTERVAL 1 SECOND, summary_id = ?,"
                        + " profile_id = ? WHERE chat_id = ?",
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                scope.chatId());
        var claim = work.claim(scope.chatId()).orElseThrow();
        TurnLease live = turns.begin(scope, FINGERPRINT);
        ChatMemoryState before = state();
        ChatContext patch = new ChatContext().withChatId(scope.chatId());
        ReflectionTestUtils.setField(patch, field, "replacement");
        seedCaches();
        assertTrue(service.update(patch));
        ChatMemoryState after = state();
        assertEquals("disabled", after.getStatus());
        assertEquals(before.getGeneration(), after.getGeneration());
        assertEquals(before.getUserId(), after.getUserId());
        assertEquals(before.getCharacterUid(), after.getCharacterUid());
        assertEquals(before.getStoreType(), after.getStoreType());
        assertEquals(before.getFingerprint(), after.getFingerprint());
        assertEquals(before.getSummaryId(), after.getSummaryId());
        assertEquals(before.getProfileId(), after.getProfileId());
        assertNull(after.getTurnToken());
        assertNull(after.getTurnLeaseUntil());
        assertNull(after.getTurnDeadline());
        assertNull(after.getClaimToken());
        assertNull(after.getClaimLeaseUntil());
        assertNull(after.getClaimDeadline());
        assertNull(after.getDueAt());
        assertThrows(IllegalStateException.class, () -> turns.check(live));
        assertThrows(IllegalStateException.class, () -> work.check(claim));
        assertCachesEvicted();
        assertEquals(1, acquisitions);
        if (field.equals("apiKeyValue")) {
            assertEquals("replacement", patch.getApiKeyValue());
            assertEquals(
                    "encrypted:replacement",
                    contexts.selectByPrimaryKey(scope.chatId()).orElseThrow().getApiKeyValue());
        }
        MemoryScope next = field.equals("userId")
                ? new MemoryScope(scope.chatId(), "replacement", scope.characterUid(), 1, scope.storeType())
                : scope;
        ChatMemoryState active = lifecycle.activate(next, "b".repeat(64));
        assertEquals("active", active.getStatus());
        assertEquals(before.getGeneration() + (field.equals("userId") ? 1 : 0), active.getGeneration());
        if (!field.equals("userId")) {
            assertEquals(before.getProfileId(), active.getProfileId());
            assertEquals((byte) 1, active.getProfileRevalidationPending());
        }
    }

    @Test
    void identicalFullContextAndReadQuotaRoutingOrExtUpdatesDoNotRevokeTurn() {
        TurnLease live = turns.begin(scope, FINGERPRINT);
        ChatMemoryState before = state();
        ChatContext full = service.get(scope.chatId());
        assertTrue(service.update(full));
        assertEquals("synthetic", full.getApiKeyValue());
        assertTrue(service.update(new ChatContext()
                .withChatId(scope.chatId())
                .withGmtRead(LocalDateTime.now())
                .withQuota(3L)
                .withQuotaType("messages")
                .withChatType("telegram")
                .withTgChatId(7L)
                .withTgUserId(8L)
                .withExt("{\"ui\":true}")));
        assertEquals(before, state());
        assertEquals(
                "encrypted:synthetic",
                contexts.selectByPrimaryKey(scope.chatId()).orElseThrow().getApiKeyValue());
        turns.check(live);
        assertTrue(service.update(new ChatContext().withChatId(scope.chatId()).withUserProfile("")));
        assertEquals(
                "", contexts.selectByPrimaryKey(scope.chatId()).orElseThrow().getUserProfile());
        assertEquals("disabled", state().getStatus());
    }

    @Test
    void failedContextWriteRollsBackRevocationAndRestoresCallerCredential() {
        TurnLease live = turns.begin(scope, FINGERPRINT);
        ChatMemoryState before = state();
        ChatContext previous = contexts.selectByPrimaryKey(scope.chatId()).orElseThrow();
        ChatContext patch = new ChatContext()
                .withChatId(scope.chatId())
                .withUserProfile("changed")
                .withApiKeyValue("replacement");
        sql.execute("CREATE TRIGGER context_update_failure BEFORE UPDATE ON chat_context FOR EACH ROW SIGNAL SQLSTATE"
                + " '45000' SET MESSAGE_TEXT = 'synthetic-failure'");
        try {
            var failure = assertThrows(IllegalStateException.class, () -> service.update(patch));
            assertEquals("Memory lifecycle transaction failed", failure.getMessage());
            assertNull(failure.getCause());
            assertEquals("replacement", patch.getApiKeyValue());
            assertEquals(before, state());
            assertEquals(previous, contexts.selectByPrimaryKey(scope.chatId()).orElseThrow());
            turns.check(live);
        } finally {
            sql.execute("DROP TRIGGER context_update_failure");
        }
    }

    @Test
    void contextAndRevocationCommitTogetherWhileOldWritersWaitOnStateLock() throws Exception {
        complete();
        sql.update(
                "UPDATE chat_memory_state SET due_at = UTC_TIMESTAMP(6) - INTERVAL 1 SECOND WHERE chat_id = ?",
                scope.chatId());
        var claim = work.claim(scope.chatId()).orElseThrow();
        TurnLease live = turns.begin(scope, FINGERPRINT);
        CountDownLatch changed = new CountDownLatch(1);
        CountDownLatch commit = new CountDownLatch(1);
        ChatContextMapper gated = (ChatContextMapper) Proxy.newProxyInstance(
                ChatContextMapper.class.getClassLoader(),
                new Class<?>[] {ChatContextMapper.class},
                (proxy, method, args) -> {
                    Object result;
                    try {
                        result = method.invoke(contexts, args);
                    } catch (InvocationTargetException failure) {
                        throw failure.getCause();
                    }
                    if (method.getName().equals("updateByPrimaryKeySelective")) {
                        assertTrue(lock.isHeldByCurrentThread());
                        changed.countDown();
                        assertTrue(commit.await(15, TimeUnit.SECONDS));
                    }
                    return result;
                });
        ReflectionTestUtils.setField(service, "lifecycle", repository(gated));
        try (var workers = Executors.newFixedThreadPool(3)) {
            var change = workers.submit(() ->
                    service.update(new ChatContext().withChatId(scope.chatId()).withUserId("replacement")));
            try {
                assertTrue(changed.await(10, TimeUnit.SECONDS));
                assertEquals(
                        "owner",
                        contexts.selectByPrimaryKey(scope.chatId())
                                .orElseThrow()
                                .getUserId());
                assertEquals("active", state().getStatus());
                var turnWriter = workers.submit(() -> assertThrows(IllegalStateException.class, () -> append(live)));
                var claimWriter =
                        workers.submit(() -> assertThrows(IllegalStateException.class, () -> work.renew(claim)));
                awaitWaiters(2);
                commit.countDown();
                assertTrue(change.get(15, TimeUnit.SECONDS));
                turnWriter.get(15, TimeUnit.SECONDS);
                claimWriter.get(15, TimeUnit.SECONDS);
                assertEquals(
                        "replacement",
                        contexts.selectByPrimaryKey(scope.chatId())
                                .orElseThrow()
                                .getUserId());
                assertEquals("disabled", state().getStatus());
                assertEquals("owner", state().getUserId());
            } finally {
                commit.countDown();
            }
        }
    }

    @Test
    void rollbackClearAndDirectDeleteUseReentrantCoordinationAndRetainUsage() {
        insert(SystemMessage.from("system"), null, null);
        insert(UserMessage.from("input"), null, null);
        long answer = insert(AiMessage.from("legacy"), null, new TokenUsage(2, 3));
        MemoryUsage before = store.usage(scope.chatId());
        seedCaches();
        local.getCache(LONG_PERIOD_CACHE_NAME).put("MysqlChatMemoryStoreImpl_record_" + answer, "stale");
        lock.lock(); // Simulates already-owned queue work; no drain/reset is allowed here.
        try {
            assertEquals(List.of(answer), store.rollback(scope.chatId(), 1));
            assertEquals(1, lock.getHoldCount());
            assertNull(local.getCache(LONG_PERIOD_CACHE_NAME).get("MysqlChatMemoryStoreImpl_record_" + answer));
            assertEquals(before, store.usage(scope.chatId()));
            assertEquals(2L, state().getGeneration());
            seedCaches();
            store.deleteMessages(scope.chatId());
            assertEquals(1, lock.getHoldCount());
            assertEquals(3L, state().getGeneration());
            assertTrue(store.getMessages(scope.chatId()).isEmpty());
            assertEquals(before, store.usage(scope.chatId()));
            seedCaches();
            assertTrue(service.delete(scope.chatId()));
            assertEquals(1, lock.getHoldCount());
            assertEquals("deleted", state().getStatus());
            assertTrue(contexts.selectByPrimaryKey(scope.chatId()).isEmpty());
            assertEquals(before, store.usage(scope.chatId()));
            assertFalse(service.delete(scope.chatId()));
        } finally {
            lock.unlock();
        }
        assertCachesEvicted();
        assertEquals(4, acquisitions);
        assertTrue(store.rollback(scope.chatId(), 0).isEmpty());
        assertTrue(store.rollback(scope.chatId(), null).isEmpty());
        assertEquals(4, acquisitions);
    }

    @Test
    void directDeleteOfNeverActivatedContextCreatesTombstoneAndRollbackFailureUnlocks() {
        states.deleteByPrimaryKey(scope.chatId());
        sql.update("DELETE FROM chat_memory_commit WHERE chat_id = ?", scope.chatId());
        assertTrue(service.delete(scope.chatId()));
        assertEquals("deleted", state().getStatus());
        assertThrows(IllegalStateException.class, () -> store.rollback(scope.chatId(), 1));
        assertFalse(lock.isLocked());
    }

    @Test
    void eachProviderCallCountsOnceIncludingLateReturnsAndNoExtractionOrCanonicalDoubleCount() {
        insert(AiMessage.from("legacy"), null, new TokenUsage(2, 3));
        insert(AiMessage.from("old tagged history"), UUID.randomUUID().toString(), new TokenUsage(5, 7));
        TurnLease first = turns.begin(scope, FINGERPRINT);
        append(first);
        String firstCall = UUID.randomUUID().toString();
        publication.recordTurnUsage(first, FINGERPRINT, firstCall, "synthetic-model", new TokenUsage(11, 13));
        publication.recordTurnUsage(first, FINGERPRINT, firstCall, "synthetic-model", new TokenUsage(11, 13));
        insert(AiMessage.from("canonical call one"), first.token(), new TokenUsage(11, 13));
        publication.recordTurnUsage(
                first, FINGERPRINT, UUID.randomUUID().toString(), "synthetic-model", new TokenUsage(17, 19));
        turns.complete(first, AiMessage.from("canonical call two"), null, new TokenUsage(17, 19));
        TurnLease late = turns.begin(scope, FINGERPRINT);
        var snapshot = publication.snapshot(
                scope, MemoryPublicationRepository.Operation.OVERFLOW, state().getLatestFinalizedId(), late.token());
        publication.recordUsage(snapshot, UUID.randomUUID().toString(), "synthetic-model", new TokenUsage(1000, 2000));
        append(late);
        store.deleteMessages(scope.chatId());
        publication.recordTurnUsage(
                late, FINGERPRINT, UUID.randomUUID().toString(), "synthetic-model", new TokenUsage(23, 29));
        publication.recordTurnUsage(late, FINGERPRINT, UUID.randomUUID().toString(), "synthetic-model", null);
        assertEquals(new MemoryUsage(6L, new TokenUsage(58, 71)), store.usage(scope.chatId()));
        assertTrue(store.getMessages(scope.chatId()).isEmpty());
        assertTrue(service.delete(scope.chatId()));
        assertEquals(new MemoryUsage(6L, new TokenUsage(58, 71)), store.usage(scope.chatId()));
    }

    @Test
    void usageStreamsOnlyMetadataBeyondLegacyHistoryLimitAndRetainsNullCounters() {
        assertEquals(new MemoryUsage(null, null), store.usage(scope.chatId()));
        insert(AiMessage.from("unknown tokens"), null, null);
        assertEquals(new MemoryUsage(1L, null), store.usage(scope.chatId()));
        insert(UserMessage.from("not a provider call"), null, new TokenUsage(999, 999));
        insert(SystemMessage.from("not a provider call"), null, new TokenUsage(999, 999));
        sql.update("""
            INSERT INTO chat_history (memory_id, message, ext, enabled, gmt_create, gmt_modified)
            WITH RECURSIVE ids AS (
                SELECT 1 AS n UNION ALL SELECT n + 1 FROM ids WHERE n < 1000
            )
            SELECT ?, ?, '[2,3,5]', 0, UTC_TIMESTAMP(), UTC_TIMESTAMP() FROM ids
            """, scope.chatId(), ChatMessageSerializer.messageToJson(AiMessage.from("legacy reply")));
        TurnLease turn = turns.begin(scope, FINGERPRINT);
        publication.recordTurnUsage(turn, FINGERPRINT, UUID.randomUUID().toString(), "synthetic-model", null);
        publication.recordTurnUsage(
                turn, FINGERPRINT, UUID.randomUUID().toString(), "synthetic-model", new TokenUsage(7, 11));
        insert(AiMessage.from("already accounted"), turn.token(), new TokenUsage(7, 11));
        usageQueries.count = 0;
        usageQueries.active = true;
        try {
            var transaction = new org.springframework.transaction.support.TransactionTemplate(transactions);
            transaction.executeWithoutResult(status -> {
                MemoryUsage expected = new MemoryUsage(1003L, new TokenUsage(2007, 3011));
                assertEquals(expected, store.usage(scope.chatId()));
                assertEquals(expected, store.usage(scope.chatId()));
            });
        } finally {
            usageQueries.active = false;
        }
        assertEquals(2, usageQueries.count, "Each aggregation must use one uncached SQL snapshot");
    }

    @org.apache.ibatis.plugin.Intercepts(
            @org.apache.ibatis.plugin.Signature(
                    type = org.apache.ibatis.executor.statement.StatementHandler.class,
                    method = "query",
                    args = {java.sql.Statement.class, org.apache.ibatis.session.ResultHandler.class}))
    static final class UsageQueryProbe implements org.apache.ibatis.plugin.Interceptor {
        boolean active;
        int count;

        @Override
        public Object intercept(org.apache.ibatis.plugin.Invocation invocation) throws Throwable {
            if (!active) {
                return invocation.proceed();
            }
            var handler = (org.apache.ibatis.executor.statement.StatementHandler) invocation.getTarget();
            String query =
                    handler.getBoundSql().getSql().replaceAll("\\s+", " ").trim();
            assertTrue(query.startsWith("SELECT token_usage FROM chat_memory_commit"));
            assertTrue(query.contains("UNION ALL SELECT h.ext FROM chat_history h"));
            assertFalse(query.contains("ORDER BY"));
            assertNotNull(invocation.getArgs()[1], "A ResultHandler must fold rows instead of collecting them");
            var statement = (java.sql.Statement) invocation.getArgs()[0];
            assertEquals(Integer.MIN_VALUE, statement.getFetchSize(), "Use Connector/J row streaming");
            assertEquals(java.sql.ResultSet.TYPE_FORWARD_ONLY, statement.getResultSetType());
            Object result = invocation.proceed();
            assertTrue(result instanceof List<?> rows && rows.isEmpty(), "MyBatis must not retain usage rows");
            count++;
            return result;
        }
    }

    @Test
    void equalityFallbackCannotRewriteTaggedInvocationOrOlderEqualLegacyRow() {
        long system = insert(SystemMessage.from("system"), null, null);
        long user = insert(UserMessage.from("input"), null, null);
        long legacy = insert(AiMessage.from("same"), null, new TokenUsage(2, 3));
        assertEquals(
                legacy,
                store.updateChatMessageTokenUsage(scope.chatId(), AiMessage.from("same"), new TokenUsage(5, 7)));
        assertEquals(
                new TokenUsage(5, 7),
                InfoUtils.deserialize(
                        histories.selectByPrimaryKey(legacy).orElseThrow().getExt()));
        long canonical = insert(AiMessage.from("same"), UUID.randomUUID().toString(), new TokenUsage(11, 13));
        cache.getCache(LONG_PERIOD_CACHE_NAME)
                .put(
                        "MysqlChatMemoryStoreImpl_" + scope.chatId(),
                        List.of(
                                record(system, SystemMessage.from("system")),
                                record(user, UserMessage.from("input")),
                                record(canonical, AiMessage.from("same"))));
        assertNull(store.updateChatMessageTokenUsage(scope.chatId(), AiMessage.from("same"), new TokenUsage(99, 99)));
        assertEquals(
                new TokenUsage(11, 13),
                InfoUtils.deserialize(
                        histories.selectByPrimaryKey(canonical).orElseThrow().getExt()));
        assertEquals(
                new TokenUsage(5, 7),
                InfoUtils.deserialize(
                        histories.selectByPrimaryKey(legacy).orElseThrow().getExt()));
    }

    @Test
    void contextReadsThroughSpringCacheAdviceObserveRemoteChangesAndDeletion() {
        DefaultListableBeanFactory beans = new DefaultListableBeanFactory();
        beans.registerSingleton(CacheUtils.KEY_GENERATOR, new FullNameKeyGenerator());
        CacheInterceptor advice = new CacheInterceptor();
        advice.setBeanFactory(beans);
        advice.setCacheManager(cache);
        advice.setCacheOperationSources(new AnnotationCacheOperationSource());
        advice.afterPropertiesSet();
        advice.afterSingletonsInstantiated();
        ProxyFactory proxy = new ProxyFactory(service);
        proxy.addAdvice(advice);
        ChatContextService reader = (ChatContextService) proxy.getProxy();
        ChatContext original = reader.get(scope.chatId());
        cache.getCache(LONG_PERIOD_CACHE_NAME).put("ChatContextService_" + scope.chatId(), original);
        assertEquals("baseline", original.getUserProfile());
        assertEquals("synthetic", original.getApiKeyValue());

        sql.update(
                "UPDATE chat_context SET user_profile = ?, api_key_value = ? WHERE chat_id = ?",
                "remote baseline",
                "encrypted:remote",
                scope.chatId());
        ChatContext fresh = reader.get(scope.chatId());
        assertEquals("remote baseline", fresh.getUserProfile());
        assertEquals("remote", fresh.getApiKeyValue());
        assertEquals(
                "encrypted:remote",
                contexts.selectByPrimaryKey(scope.chatId()).orElseThrow().getApiKeyValue());
        assertEquals("baseline", original.getUserProfile());
        contexts.deleteByPrimaryKey(scope.chatId());
        assertNull(reader.get(scope.chatId()));
        assertNotNull(cache.getCache(LONG_PERIOD_CACHE_NAME).get("ChatContextService_" + scope.chatId()));
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void canonicalTranscriptIgnoresStaleCachesAcrossRemoteCompletionDisableAndClear(boolean emptyCache) {
        SystemMessage system = SystemMessage.from("system");
        insert(system, null, null);
        complete();
        List<ChatMessage> first = List.of(system, UserMessage.from("input"), AiMessage.from("answer"));
        assertEquals(first, store.getMessages(scope.chatId()));
        String key = "MysqlChatMemoryStoreImpl_" + scope.chatId();
        List<ChatMessageRecord> stale = emptyCache
                ? List.of()
                : List.of(record(1, system), record(2, UserMessage.from("input")), record(3, AiMessage.from("answer")));
        cache.getCache(LONG_PERIOD_CACHE_NAME).put(key, stale);

        TurnLease next = turns.begin(scope, FINGERPRINT);
        turns.append(
                next,
                UserMessage.from("remote input"),
                UserMessage.from("remote input"),
                system,
                Origin.USER_INPUT,
                null);
        turns.complete(next, AiMessage.from("remote answer"), null, new TokenUsage(2, 3));
        List<ChatMessage> expected = List.of(
                system,
                UserMessage.from("input"),
                AiMessage.from("answer"),
                UserMessage.from("remote input"),
                AiMessage.from("remote answer"));
        assertEquals(expected, store.getMessages(scope.chatId()));
        lifecycle.disable(scope.chatId());
        assertEquals(expected, store.getMessages(scope.chatId()));
        lifecycle.clear(scope.chatId());
        assertTrue(store.getMessages(scope.chatId()).isEmpty());
        assertEquals(stale, cache.getCache(LONG_PERIOD_CACHE_NAME).get(key).get());
    }

    private MemoryLifecycleRepository repository(ChatContextMapper mapper) {
        return new MemoryLifecycleRepository(coordination, states, histories, mapper, transactions, properties);
    }

    private void complete() {
        TurnLease lease = turns.begin(scope, FINGERPRINT);
        append(lease);
        turns.complete(lease, AiMessage.from("answer"), null, new TokenUsage(2, 3));
    }

    private long append(TurnLease lease) {
        return turns.append(
                lease,
                UserMessage.from("input"),
                UserMessage.from("input"),
                SystemMessage.from("system"),
                Origin.USER_INPUT,
                null);
    }

    private long insert(ChatMessage message, String turnId, TokenUsage usage) {
        ChatHistory row = new ChatHistory()
                .withMemoryId(scope.chatId())
                .withTurnId(turnId)
                .withMessage(ChatMessageSerializer.messageToJson(message))
                .withExt(usage == null ? null : InfoUtils.serialize(usage))
                .withGmtCreate(LocalDateTime.now())
                .withGmtModified(LocalDateTime.now());
        assertEquals(1, histories.insertSelective(row));
        return row.getId();
    }

    private ChatMemoryState state() {
        return states.selectByPrimaryKey(scope.chatId()).orElseThrow();
    }

    private void seedCaches() {
        cache.getCache(LONG_PERIOD_CACHE_NAME).put("ChatContextService_" + scope.chatId(), "stale");
        cache.getCache(LONG_PERIOD_CACHE_NAME).put("MysqlChatMemoryStoreImpl_" + scope.chatId(), List.of());
        local.getCache(LONG_PERIOD_CACHE_NAME).put("ChatSessionService_" + scope.chatId(), "stale");
    }

    private void assertCachesEvicted() {
        assertNull(cache.getCache(LONG_PERIOD_CACHE_NAME).get("ChatContextService_" + scope.chatId()));
        assertNull(cache.getCache(LONG_PERIOD_CACHE_NAME).get("MysqlChatMemoryStoreImpl_" + scope.chatId()));
        assertNull(local.getCache(LONG_PERIOD_CACHE_NAME).get("ChatSessionService_" + scope.chatId()));
    }

    private static ChatMessageRecord record(long id, ChatMessage message) {
        return ChatMessageRecord.builder().id(id).message(message).build();
    }

    private static void awaitWaiters(int count) {
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(10);
        while (System.nanoTime() < deadline) {
            if (sql.queryForObject("SELECT COUNT(*) FROM performance_schema.data_lock_waits", Integer.class) >= count) {
                return;
            }
        }
        fail("Expected SQL writers to wait on the lifecycle state lock");
    }

    private static String id() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
