package fun.freechat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.delegatesTo;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import fun.freechat.mapper.ChatContextMapper;
import fun.freechat.mapper.ChatHistoryMapper;
import fun.freechat.mapper.ChatMemoryCommitMapper;
import fun.freechat.mapper.ChatMemoryCoordinationMapper;
import fun.freechat.mapper.ChatMemoryStateMapper;
import fun.freechat.model.ChatMemoryState;
import fun.freechat.service.chat.SystemPromptSnapshotStore;
import fun.freechat.service.chat.memory.LongTermMemoryProperties;
import fun.freechat.service.chat.memory.MemoryGarbageCollector;
import fun.freechat.service.chat.memory.MemoryHistoryReconciler;
import fun.freechat.service.chat.memory.MemoryIdleWorker;
import fun.freechat.service.chat.memory.MemoryLifecycleRepository;
import fun.freechat.service.chat.memory.MemoryMaintenance;
import fun.freechat.service.chat.memory.MemoryModelResolver;
import fun.freechat.service.chat.memory.MemoryPublicationRepository;
import fun.freechat.service.chat.memory.MemoryScheduler;
import fun.freechat.service.chat.memory.MemoryScheduler.DispatchTask;
import fun.freechat.service.chat.memory.MemoryScheduler.ExtractionTask;
import fun.freechat.service.chat.memory.MemoryScheduler.MaintenanceTask;
import fun.freechat.service.chat.memory.MemoryScope;
import fun.freechat.service.chat.memory.MemorySourceReader;
import fun.freechat.service.chat.memory.MemoryTurnRepository;
import fun.freechat.service.chat.memory.MemoryWorkRepository;
import fun.freechat.service.chat.memory.MemoryWorkRepository.Claim;
import fun.freechat.service.common.impl.LocalFileStoreImpl;
import fun.freechat.service.enums.EmbeddingStoreType;
import fun.freechat.service.rag.MemoryEmbeddingCleanupService;
import io.netty.buffer.ByteBuf;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.logging.nologging.NoLoggingImpl;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
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
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.redisson.Redisson;
import org.redisson.api.RScheduledExecutorService;
import org.redisson.api.RedissonClient;
import org.redisson.api.options.ExecutorOptions;
import org.redisson.client.codec.StringCodec;
import org.redisson.codec.Kryo5Codec;
import org.redisson.config.Config;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/** Private real Redis + MySQL; Spring task injection only, without application/model bootstrap. */
@Testcontainers
@Timeout(40)
class MemorySchedulerIT {
    private static final long WAIT = 10;
    private static final String SECRET = "scheduler-test-body-and-provider-secret-not-for-redis";

    @Container
    private static final GenericContainer<?> REDIS =
            new GenericContainer<>("redis:7.4.2-alpine").withExposedPorts(6379);

    @Container
    private static final MySQLContainer<?> MYSQL =
            new MySQLContainer<>("mysql:8.0.36").withDatabaseName("freechat").withInitScript("sql/schema.sql");

    private static HikariDataSource dataSource;
    private static JdbcTemplate sql;
    private static ChatMemoryStateMapper states;
    private static ChatMemoryCoordinationMapper coordination;
    private static ChatContextMapper contexts;
    private static ChatHistoryMapper histories;
    private static ChatMemoryCommitMapper commits;
    private static DataSourceTransactionManager transactions;
    private static final SqlEvents EVENTS = new SqlEvents();
    private static int nextDatabase;
    private int database;
    private final List<RedissonClient> clients = new ArrayList<>();
    private final List<Node> nodes = new ArrayList<>();
    private final List<MemoryIdleWorker> workers = new ArrayList<>();
    private final List<CountDownLatch> gates = new ArrayList<>();
    private final LinkedBlockingQueue<String> maintenanceRuns = new LinkedBlockingQueue<>();

    @TempDir
    Path snapshotDirectory;

    private SystemPromptSnapshotStore snapshots;
    private LongTermMemoryProperties properties;
    private MemoryWorkRepository work;

    @BeforeAll
    static void sqlOnly() throws Exception {
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
        configuration.addInterceptor(EVENTS);
        for (Class<?> mapper : List.of(
                ChatMemoryStateMapper.class,
                ChatMemoryCoordinationMapper.class,
                ChatContextMapper.class,
                ChatHistoryMapper.class,
                ChatMemoryCommitMapper.class)) {
            configuration.addMapper(mapper);
        }
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setConfiguration(configuration);
        SqlSessionTemplate sessions = new SqlSessionTemplate(factory.getObject());
        states = sessions.getMapper(ChatMemoryStateMapper.class);
        coordination = sessions.getMapper(ChatMemoryCoordinationMapper.class);
        contexts = sessions.getMapper(ChatContextMapper.class);
        histories = sessions.getMapper(ChatHistoryMapper.class);
        commits = sessions.getMapper(ChatMemoryCommitMapper.class);
        transactions = new DataSourceTransactionManager(dataSource);
    }

    @BeforeEach
    void isolate() {
        database = nextDatabase++;
        assertTrue(database < 16, "Each test uses its own database in the owned Redis container");
        sql.update("DELETE FROM chat_memory_commit");
        sql.update("DELETE FROM chat_memory_state");
        sql.update("DELETE FROM chat_history");
        sql.update("DELETE FROM chat_context");
        EVENTS.reset();
        properties = new LongTermMemoryProperties();
        properties.setScanInterval(Duration.ofMillis(100));
        properties.setWorkersPerNode(1);
        properties.setDispatchBatchSize(1);
        properties.afterPropertiesSet();
        var files = new LocalFileStoreImpl();
        ReflectionTestUtils.setField(files, "basePathStr", snapshotDirectory.toString());
        snapshots = new SystemPromptSnapshotStore(files);
        work = new MemoryWorkRepository(coordination, states, transactions, properties);
    }

    @AfterEach
    void stopOnlyOwnedNodesAndClients() {
        gates.forEach(CountDownLatch::countDown);
        nodes.forEach(node -> node.scheduler.stop());
        workers.forEach(MemoryIdleWorker::close);
        clients.forEach(client -> client.shutdown(0, 5, TimeUnit.SECONDS));
    }

    @AfterAll
    static void closeSql() {
        if (dataSource != null) {
            dataSource.close();
        }
    }

    @Test
    void immutableMetadataRoundTripsThroughExecutorCodecAndTransientSpringInjection() throws Exception {
        String id = ready("metadata");
        sql.update(
                "UPDATE chat_memory_state SET idle_through_id = 3, profile_id = 'profile-head', "
                        + "profile_revalidation_pending = 1 WHERE chat_id = ?",
                id);
        Claim claim = work.claim(id).orElseThrow();
        assertTrue(
                new MemoryWorkRepository(coordination, states, transactions, properties)
                        .claim(id)
                        .isEmpty(),
                "A second repository/node cannot claim the same live SQL work");
        RedissonClient client = client();
        LinkedBlockingQueue<Claim> received = new LinkedBlockingQueue<>();
        Node node = node(client, selected -> {
            work.check(selected);
            received.add(selected);
            work.release(selected);
        });
        ExtractionTask task = new ExtractionTask(claim);
        // A REAL final service with secret-bearing dependency must not be serialized.
        MemoryModelResolver originalModels = resolver();
        MemoryIdleWorker original = worker(originalModels);
        ReflectionTestUtils.setField(task, "worker", original);
        Kryo5Codec codec = new Kryo5Codec();
        ByteBuf encoded = codec.getValueEncoder().encode(task);
        try {
            assertFalse(encoded.toString(StandardCharsets.ISO_8859_1).contains(SECRET));
            ExtractionTask decoded = (ExtractionTask) codec.getValueDecoder().decode(encoded, null);
            assertEquals(claim, ReflectionTestUtils.getField(decoded, "claim"));
            assertNull(ReflectionTestUtils.getField(decoded, "worker"));
        } finally {
            encoded.release();
        }
        assertTrue(
                Modifier.isFinal(ExtractionTask.class.getDeclaredField("claim").getModifiers()));
        assertTrue(Modifier.isTransient(
                ExtractionTask.class.getDeclaredField("worker").getModifiers()));
        assertTrue(Modifier.isTransient(
                DispatchTask.class.getDeclaredField("scheduler").getModifiers()));
        assertEquals(Set.of("claim", "worker"), instanceFields(ExtractionTask.class));
        assertNotNull(Kryo5Codec.class
                .getConstructor(ClassLoader.class)
                .newInstance(getClass().getClassLoader()));
        assertEquals(Duration.ofSeconds(30), new LongTermMemoryProperties().getScanInterval());

        MaintenanceTask maintenanceTask = new MaintenanceTask();
        ReflectionTestUtils.setField(maintenanceTask, "maintenance", node.maintenance);
        assertEquals(Set.of("maintenance"), instanceFields(MaintenanceTask.class));
        assertTrue(Modifier.isTransient(
                MaintenanceTask.class.getDeclaredField("maintenance").getModifiers()));
        assertTrue(Modifier.isStatic(MaintenanceTask.class.getModifiers()));
        ByteBuf maintenanceBytes = codec.getValueEncoder().encode(maintenanceTask);
        try {
            assertFalse(maintenanceBytes.toString(StandardCharsets.ISO_8859_1).contains(SECRET));
            MaintenanceTask decoded = (MaintenanceTask) codec.getValueDecoder().decode(maintenanceBytes, null);
            assertNull(ReflectionTestUtils.getField(decoded, "maintenance"));
        } finally {
            maintenanceBytes.release();
        }

        var executor = executor(client, MemoryScheduler.WORK_EXECUTOR);
        var future = executor.submit(claim.token(), task, Duration.ofSeconds(20));
        assertEquals(claim.token(), future.getTaskId());
        assertTrue(executor.hasTask(claim.token()));
        assertStoredTasksRedacted(client, executor);
        var maintenanceExecutor = executor(client, MemoryScheduler.MAINTENANCE_EXECUTOR);
        maintenanceExecutor.scheduleWithFixedDelay(
                MemoryScheduler.MAINTENANCE_TASK, maintenanceTask, Duration.ZERO, properties.getScanInterval());
        assertStoredTasksRedacted(client, maintenanceExecutor);
        assertThrows(
                IllegalArgumentException.class, () -> executor.submit(claim.token(), task, Duration.ofSeconds(20)));
        start(node);
        assertNull(future.get(WAIT, TimeUnit.SECONDS));
        Claim transported = take(received);
        assertEquals(claim, transported);
        assertNotSame(claim, transported);
        verifyNoInteractions(originalModels);
        verify(node.models).resolve(id); // The real worker reached its locally injected deterministic model boundary.
        assertTrue(take(node.scans).startsWith("memory-maintenance-"), "Only the Redisson maintenance worker scans");
        assertEquals(Set.of(MemoryScheduler.MAINTENANCE_TASK), maintenanceExecutor.getTaskIds());
    }

    @Test
    void periodicKeysetDispatchRevisitsBeginningWithoutStarvingLaterIds() throws Exception {
        ready("a-always-due");
        ready("b-later");
        ready("c-later");
        LinkedBlockingQueue<String> received = new LinkedBlockingQueue<>();
        Node node = node(client(), claim -> {
            work.check(claim);
            work.release(claim); // Keep all rows due to exercise pagination rather than removal.
            received.add(claim.scope().chatId());
        });
        start(node);
        assertEquals("a-always-due", take(received));
        assertEquals("b-later", take(received));
        assertEquals("c-later", take(received));
        assertEquals("a-always-due", take(received));
        assertTrue(EVENTS.dueParameters.stream().anyMatch(values -> values.contains("b-later")));
        assertEquals(
                Set.of(MemoryScheduler.DISPATCH_TASK),
                executor(node.client, MemoryScheduler.DISPATCH_EXECUTOR).getTaskIds());
        assertEquals(
                Set.of(MemoryScheduler.MAINTENANCE_TASK),
                executor(node.client, MemoryScheduler.MAINTENANCE_EXECUTOR).getTaskIds());
    }

    @Test
    void twoNodesSingleWorkerEachDoNotStarveDispatcherAndStoppingOneLeavesOtherActive() throws Exception {
        RedissonClient first = client();
        RedissonClient second = client();
        CountDownLatch release = gate();
        CountDownLatch bothEntered = new CountDownLatch(2);
        LinkedBlockingQueue<String> executionNodes = new LinkedBlockingQueue<>();
        LinkedBlockingQueue<Claim> completed = new LinkedBlockingQueue<>();
        Consumer<Claim> blocked = claim -> {
            work.check(claim);
            bothEntered.countDown();
            awaitGate(release);
            consume(claim);
            completed.add(claim);
        };
        Node a = node(first, claim -> {
            executionNodes.add("a");
            blocked.accept(claim);
        });
        Node b = node(second, claim -> {
            executionNodes.add("b");
            blocked.accept(claim);
        });
        start(a);
        start(b);
        var dispatch = executor(second, MemoryScheduler.DISPATCH_EXECUTOR);
        var maintenance = executor(second, MemoryScheduler.MAINTENANCE_EXECUTOR);
        assertEquals(2, dispatch.countActiveWorkers());
        assertEquals(2, executor(second, MemoryScheduler.WORK_EXECUTOR).countActiveWorkers());
        assertEquals(2, maintenance.countActiveWorkers());
        assertThrows(
                IllegalArgumentException.class,
                () -> dispatch.scheduleWithFixedDelay(
                        MemoryScheduler.DISPATCH_TASK,
                        new DispatchTask(),
                        Duration.ZERO,
                        properties.getScanInterval()));
        assertThrows(
                IllegalArgumentException.class,
                () -> maintenance.scheduleWithFixedDelay(
                        MemoryScheduler.MAINTENANCE_TASK,
                        new MaintenanceTask(),
                        Duration.ZERO,
                        properties.getScanInterval()));
        ready("a-busy");
        ready("b-busy");
        await(bothEntered);
        assertEquals(Set.of("a", "b"), Set.of(take(executionNodes), take(executionNodes)));
        // Both extraction workers are blocked; SQL dispatch and the third executor must still progress.
        EVENTS.claimed.clear();
        a.scans.clear();
        b.scans.clear();
        ready("c-queued");
        assertEquals("c-queued", take(EVENTS.claimed).scope().chatId());
        assertNotNull(states.selectByPrimaryKey("c-queued").orElseThrow().getClaimToken());
        assertTrue(take(a.scans).startsWith("memory-maintenance-"));
        release.countDown();
        take(completed);
        take(completed);
        take(completed);
        a.scheduler.stop();
        assertFalse(a.scheduler.isRunning());
        assertFalse(first.isShutdown());
        assertFalse(dispatch.isShutdown());
        assertFalse(maintenance.isShutdown());
        assertTrue(dispatch.hasTask(MemoryScheduler.DISPATCH_TASK));
        assertTrue(maintenance.hasTask(MemoryScheduler.MAINTENANCE_TASK));
        assertEquals(1, executor(second, MemoryScheduler.WORK_EXECUTOR).countActiveWorkers());
        assertEquals(1, maintenance.countActiveWorkers());
        assertTrue(((java.util.concurrent.ExecutorService) ReflectionTestUtils.getField(a.scheduler, "maintenancePool"))
                .isShutdown());
        executionNodes.clear();
        b.scans.clear();
        ready("d-survivor");
        assertEquals("d-survivor", take(completed).scope().chatId());
        assertEquals("b", take(executionNodes));
        assertTrue(take(b.scans).startsWith("memory-maintenance-"));
    }

    @Test
    void watchdogRepairsMissingDispatcherAndWorkerMetadataAfterRedisDataLoss() throws Exception {
        RedissonClient first = client();
        RedissonClient second = client();
        LinkedBlockingQueue<Claim> completed = new LinkedBlockingQueue<>();
        Consumer<Claim> finish = claim -> {
            consume(claim);
            completed.add(claim);
        };
        Node a = node(first, finish);
        Node b = node(second, finish);
        start(a);
        start(b);
        var dispatcher = executor(first, MemoryScheduler.DISPATCH_EXECUTOR);
        var maintenance = executor(first, MemoryScheduler.MAINTENANCE_EXECUTOR);
        assertTrue(dispatcher.delete()); // This test's private Redis database only.
        ready("missing-dispatcher");
        assertEquals("missing-dispatcher", take(completed).scope().chatId());
        assertTrue(dispatcher.hasTask(MemoryScheduler.DISPATCH_TASK));
        assertTrue(maintenance.delete());
        maintenanceRuns.clear();
        // Observe the actual periodic local watchdog repair, not a manually invoked registration method.
        assertTrue(take(maintenanceRuns).startsWith("memory-maintenance-"));
        assertTrue(maintenance.hasTask(MemoryScheduler.MAINTENANCE_TASK));
        assertEquals(Set.of(MemoryScheduler.MAINTENANCE_TASK), maintenance.getTaskIds());
        assertEquals(
                0,
                REDIS.execInContainer("redis-cli", "-n", Integer.toString(database), "FLUSHDB")
                        .getExitCode());
        ready("after-redis-loss");
        assertEquals("after-redis-loss", take(completed).scope().chatId());
        ReflectionTestUtils.invokeMethod(a.scheduler, "ensureRegistration");
        ReflectionTestUtils.invokeMethod(b.scheduler, "ensureRegistration");
        assertEquals(2, executor(second, MemoryScheduler.WORK_EXECUTOR).countActiveWorkers());
        assertEquals(2, maintenance.countActiveWorkers());
        assertTrue(dispatcher.hasTask(MemoryScheduler.DISPATCH_TASK));
        assertTrue(maintenance.hasTask(MemoryScheduler.MAINTENANCE_TASK));
        assertFalse(EVENTS.dueParameters.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void failedEnqueueReleasesExactClaimAndNextDispatchRecoversWithoutChargingRetry(boolean accepted) throws Exception {
        RedissonClient real = client();
        RedissonClient intercepted = mock(
                RedissonClient.class,
                withSettings().mockMaker(MockMakers.SUBCLASS).defaultAnswer(delegatesTo(real)));
        var actualExecutor = executor(real, MemoryScheduler.WORK_EXECUTOR);
        var failingExecutor = mock(
                RScheduledExecutorService.class,
                withSettings().mockMaker(MockMakers.SUBCLASS).defaultAnswer(delegatesTo(actualExecutor)));
        AtomicBoolean failOnce = new AtomicBoolean(true);
        LinkedBlockingQueue<Claim> rejected = new LinkedBlockingQueue<>();
        CountDownLatch failedRelease = new CountDownLatch(1);
        EVENTS.onRelease = token -> failedRelease.countDown();
        doAnswer(call -> {
                    if (failOnce.getAndSet(false)) {
                        ExtractionTask task = call.getArgument(1);
                        rejected.add((Claim) ReflectionTestUtils.getField(task, "claim"));
                        if (accepted) {
                            actualExecutor.submit(call.getArgument(0), task, call.getArgument(2));
                        }
                        throw new IllegalStateException(SECRET);
                    }
                    return actualExecutor.submit(
                            call.getArgument(0), (Callable<Void>) call.getArgument(1), call.getArgument(2));
                })
                .when(failingExecutor)
                .submit(anyString(), org.mockito.ArgumentMatchers.<Callable<Void>>any(), any(Duration.class));
        doAnswer(call -> {
                    RScheduledExecutorService result = real.getExecutorService((ExecutorOptions) call.getArgument(0));
                    return result.getName().equals(MemoryScheduler.WORK_EXECUTOR) ? failingExecutor : result;
                })
                .when(intercepted)
                .getExecutorService(any(ExecutorOptions.class));
        LinkedBlockingQueue<Claim> completed = new LinkedBlockingQueue<>();
        Node node = node(intercepted, claim -> {
            awaitGate(failedRelease);
            consume(claim);
            completed.add(claim);
        });
        ready("recoverable");
        start(node);
        Claim failed = take(rejected);
        await(failedRelease);
        Claim success = take(completed);
        assertNotEquals(failed.token(), success.token());
        assertEquals(failed.scope(), success.scope());
        assertEquals(0, states.selectByPrimaryKey("recoverable").orElseThrow().getRetryAttempts());
        assertTrue(EVENTS.failures.isEmpty(), "Neither an ambiguous enqueue nor its stale task may charge a retry");
        if (!accepted) {
            assertFalse(node.executed.contains(failed));
        }
    }

    @Test
    void ambiguousEnqueueAndExpiredQueuedTaskCannotBorrowSuccessorClaim() throws Exception {
        String id = ready("ambiguous");
        Claim old = work.claim(id).orElseThrow();
        RedissonClient client = client();
        var executor = executor(client, MemoryScheduler.WORK_EXECUTOR);
        var staleFuture = executor.submit(old.token(), new ExtractionTask(old), Duration.ofSeconds(20));
        work.release(old);
        Claim successor = work.claim(id).orElseThrow();
        assertNotEquals(old.token(), successor.token());
        Node node = node(client, claim -> fail("Stale task must not pass SQL fencing"));
        start(node);
        assertNull(staleFuture.get(WAIT, TimeUnit.SECONDS));
        assertEquals(
                successor.token(), states.selectByPrimaryKey(id).orElseThrow().getClaimToken());
        work.check(successor);
        assertEquals(
                2,
                EVENTS.claims.values().stream()
                        .filter(claim -> claim.scope().chatId().equals(id))
                        .count());
        assertTrue(node.executed.isEmpty());
        verifyNoInteractions(node.models);
        sql.update(
                "UPDATE chat_memory_state SET due_at = NULL, claim_lease_until = UTC_TIMESTAMP(6) WHERE chat_id = ?",
                id);
        var expiredFuture = executor.submit(successor.token(), new ExtractionTask(successor), Duration.ofSeconds(20));
        assertNull(expiredFuture.get(WAIT, TimeUnit.SECONDS));
        assertTrue(node.executed.isEmpty());
        verifyNoInteractions(node.models);
        ChatMemoryState expired = states.selectByPrimaryKey(id).orElseThrow();
        assertNull(expired.getClaimToken());
        assertNull(expired.getClaimLeaseUntil());
        assertNull(expired.getClaimDeadline());
        assertEquals(1, expired.getRetryAttempts());
        assertNotNull(expired.getRetryAt());
        assertEquals(List.of(id), EVENTS.failures);
    }

    @Test
    void watchdogNeverFallsBackToSqlOrExtractionWhenRedisSchedulingFails() throws Exception {
        RedissonClient real = client();
        RedissonClient intercepted = mock(
                RedissonClient.class,
                withSettings().mockMaker(MockMakers.SUBCLASS).defaultAnswer(delegatesTo(real)));
        CountDownLatch attempts = new CountDownLatch(2);
        doAnswer(call -> {
                    var actual = real.getExecutorService((ExecutorOptions) call.getArgument(0));
                    if (!actual.getName().equals(MemoryScheduler.DISPATCH_EXECUTOR)) {
                        return actual;
                    }
                    var failing = mock(
                            RScheduledExecutorService.class,
                            withSettings().mockMaker(MockMakers.SUBCLASS).defaultAnswer(delegatesTo(actual)));
                    doAnswer(schedule -> {
                                attempts.countDown();
                                throw new IllegalStateException(SECRET);
                            })
                            .when(failing)
                            .scheduleWithFixedDelay(
                                    anyString(), any(Runnable.class), any(Duration.class), any(Duration.class));
                    return failing;
                })
                .when(intercepted)
                .getExecutorService(any(ExecutorOptions.class));
        Node node = node(intercepted, claim -> fail("No local extraction fallback"));
        ready("still-due");
        start(node);
        await(attempts);
        assertTrue(EVENTS.dueParameters.isEmpty());
        assertTrue(EVENTS.claims.isEmpty());
        assertTrue(node.scans.isEmpty(), "The watchdog may repair task metadata but must not scan locally");
        verifyNoInteractions(node.models);
        assertEquals(List.of("still-due"), work.due(null, 1));
    }

    private RedissonClient client() {
        Config config = new Config();
        config.setCodec(StringCodec.INSTANCE); // Deliberately incompatible global codec.
        config.setThreads(2);
        config.setNettyThreads(2);
        config.useSingleServer()
                .setAddress("redis://" + REDIS.getHost() + ":" + REDIS.getMappedPort(6379))
                .setDatabase(database)
                .setTimeout(2_000)
                .setRetryAttempts(0)
                .setConnectionMinimumIdleSize(1)
                .setConnectionPoolSize(6)
                .setSubscriptionConnectionMinimumIdleSize(1)
                .setSubscriptionConnectionPoolSize(4);
        RedissonClient client = Redisson.create(config);
        clients.add(client);
        return client;
    }

    private Node node(RedissonClient client, Consumer<Claim> action) {
        MemoryModelResolver models = resolver();
        List<Claim> executed = new CopyOnWriteArrayList<>();
        doAnswer(call -> {
                    Claim selected = EVENTS.guarded.get();
                    assertNotNull(selected, "Only committed SQL claims reach the real worker's model boundary");
                    assertEquals(call.getArgument(0), selected.scope().chatId());
                    executed.add(selected);
                    action.accept(selected);
                    // Stop here, never build/call a provider. Actions release/consume the selected token;
                    // the real worker's finally cannot charge a successor for this deliberate boundary exit.
                    throw new IllegalStateException("Test model boundary complete");
                })
                .when(models)
                .resolve(anyString());
        MemoryIdleWorker worker = worker(models);
        LinkedBlockingQueue<String> scans = new LinkedBlockingQueue<>();
        // Deterministic discovery is empty so scheduler-only SQL rows are not mistaken for deleted chats.
        // The Maintenance service itself is real and is injected/executed by Redisson, never mocked final.
        ChatContextMapper maintenanceContexts =
                mock(ChatContextMapper.class, withSettings().mockMaker(MockMakers.SUBCLASS));
        doAnswer(call -> {
                    String thread = Thread.currentThread().getName();
                    scans.add(thread);
                    maintenanceRuns.add(thread);
                    return List.of();
                })
                .when(maintenanceContexts)
                .selectMany(any(SelectStatementProvider.class));
        ChatMemoryStateMapper maintenanceStates =
                mock(ChatMemoryStateMapper.class, withSettings().mockMaker(MockMakers.SUBCLASS));
        doReturn(List.of()).when(maintenanceStates).selectMany(any(SelectStatementProvider.class));
        MemoryEmbeddingCleanupService cleanup = mock(
                MemoryEmbeddingCleanupService.class,
                withSettings().mockMaker(MockMakers.SUBCLASS).defaultAnswer(call -> {
                    throw new AssertionError("No vectors in scheduler fixture");
                }));
        MemoryMaintenance maintenance = new MemoryMaintenance(
                maintenanceContexts,
                maintenanceStates,
                coordination,
                models,
                new MemoryLifecycleRepository(coordination, states, histories, contexts, transactions, properties),
                new MemoryTurnRepository(coordination, states, histories, transactions, properties, snapshots),
                new MemoryHistoryReconciler(coordination, states, histories, commits, transactions, properties),
                new MemoryGarbageCollector(coordination, commits, cleanup, properties, transactions),
                client,
                properties);
        DefaultListableBeanFactory beans = new DefaultListableBeanFactory();
        beans.registerSingleton("memoryIdleWorker", worker);
        beans.registerSingleton("memoryMaintenance", maintenance);
        MemoryScheduler scheduler = new MemoryScheduler(client, beans, work, properties);
        beans.registerSingleton("memoryScheduler", scheduler);
        Node node = new Node(client, scheduler, models, maintenance, scans, executed);
        nodes.add(node);
        return node;
    }

    private MemoryModelResolver resolver() {
        return mock(
                MemoryModelResolver.class,
                withSettings().mockMaker(MockMakers.SUBCLASS).name(SECRET).defaultAnswer(call -> {
                    throw new AssertionError("Unexpected model/credential resolution");
                }));
    }

    private MemoryIdleWorker worker(MemoryModelResolver models) {
        MemoryIdleWorker worker = new MemoryIdleWorker(
                work,
                new MemorySourceReader(
                        new MemoryTurnRepository(coordination, states, histories, transactions, properties, snapshots)),
                new MemoryPublicationRepository(coordination, states, commits, histories, transactions, properties),
                models,
                null,
                properties);
        workers.add(worker);
        return worker;
    }

    private static void start(Node node) throws Exception {
        node.scheduler.start();
        assertTrue(node.scheduler.isRunning());
        ScheduledThreadPoolExecutor watchdog =
                (ScheduledThreadPoolExecutor) ReflectionTestUtils.getField(node.scheduler, "watchdog");
        watchdog.submit(() -> {}).get(WAIT, TimeUnit.SECONDS);
    }

    private RScheduledExecutorService executor(RedissonClient client, String name) {
        return client.getExecutorService(ExecutorOptions.name(name)
                .codec(new Kryo5Codec())
                .taskRetryInterval(properties.getJobMaxDuration().plus(properties.getLeaseRenewInterval())));
    }

    private String ready(String id) {
        coordination.initialize(new ChatMemoryState()
                .withChatId(id)
                .withUserId("owner")
                .withCharacterUid("character")
                .withGeneration(1L)
                .withStoreType(EmbeddingStoreType.DEFAULT_LONG_TERM_MEMORY.text())
                .withFingerprint("scheduler-test-fingerprint"));
        MemoryTurnRepository turns =
                new MemoryTurnRepository(coordination, states, histories, transactions, properties, snapshots);
        var lease = turns.begin(
                new MemoryScope(id, "owner", "character", 1, EmbeddingStoreType.DEFAULT_LONG_TERM_MEMORY),
                "scheduler-test-fingerprint");
        turns.append(lease, UserMessage.from(SECRET), null, null, MemoryTurnRepository.Origin.USER_INPUT, null);
        turns.complete(lease, AiMessage.from(SECRET), null, null);
        sql.update("UPDATE chat_memory_state SET due_at = UTC_TIMESTAMP(6) WHERE chat_id = ?", id);
        return id;
    }

    private void consume(Claim claim) {
        work.check(claim);
        sql.update(
                "UPDATE chat_memory_state SET idle_through_id = latest_finalized_id, profile_revalidation_pending = 0 "
                        + "WHERE chat_id = ? AND claim_token = ?",
                claim.scope().chatId(),
                claim.token());
        work.release(claim);
    }

    private static Set<String> instanceFields(Class<?> type) {
        return java.util.Arrays.stream(type.getDeclaredFields())
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .map(java.lang.reflect.Field::getName)
                .collect(java.util.stream.Collectors.toSet());
    }

    private static void assertStoredTasksRedacted(RedissonClient client, RScheduledExecutorService executor) {
        String taskMap = (String) ReflectionTestUtils.getField(executor, "tasksName");
        var values = client.<String, byte[]>getMap(
                        taskMap,
                        new org.redisson.codec.CompositeCodec(
                                StringCodec.INSTANCE, org.redisson.client.codec.ByteArrayCodec.INSTANCE))
                .readAllValues();
        assertFalse(values.isEmpty());
        for (byte[] bytes : values) {
            assertFalse(new String(bytes, StandardCharsets.ISO_8859_1).contains(SECRET));
        }
    }

    private CountDownLatch gate() {
        CountDownLatch gate = new CountDownLatch(1);
        gates.add(gate);
        return gate;
    }

    private static <T> T take(LinkedBlockingQueue<T> queue) throws InterruptedException {
        T result = queue.poll(WAIT, TimeUnit.SECONDS);
        assertNotNull(result, "Expected Redisson task event");
        return result;
    }

    private static void await(CountDownLatch latch) throws InterruptedException {
        assertTrue(latch.await(WAIT, TimeUnit.SECONDS), "Expected controlled scheduler event");
    }

    private static void awaitGate(CountDownLatch gate) {
        try {
            assertTrue(gate.await(20, TimeUnit.SECONDS), "Test did not release worker");
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Test worker interrupted");
        }
    }

    /** Observe real repository SQL instead of using inline mocks/spies of final repositories. */
    @Intercepts({
        @Signature(
                type = Executor.class,
                method = "update",
                args = {MappedStatement.class, Object.class}),
        @Signature(
                type = Executor.class,
                method = "query",
                args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
    })
    static final class SqlEvents implements Interceptor {
        private final Map<String, Claim> claims = new ConcurrentHashMap<>();
        private final ThreadLocal<Claim> guarded = new ThreadLocal<>();
        private final LinkedBlockingQueue<Claim> claimed = new LinkedBlockingQueue<>();
        private final List<List<Object>> dueParameters = new CopyOnWriteArrayList<>();
        private final List<String> failures = new CopyOnWriteArrayList<>();
        private volatile Consumer<String> onRelease = token -> {};

        private void reset() {
            claims.clear();
            claimed.clear();
            dueParameters.clear();
            failures.clear();
            onRelease = token -> {};
        }

        @Override
        public Object intercept(Invocation invocation) throws Throwable {
            MappedStatement statement = (MappedStatement) invocation.getArgs()[0];
            Object parameter = invocation.getArgs()[1];
            var bound = statement.getBoundSql(parameter);
            String query = bound.getSql().toLowerCase(java.util.Locale.ROOT);
            List<Object> values = bound.getParameterMappings().stream()
                    .map(mapping -> bound.hasAdditionalParameter(mapping.getProperty())
                            ? bound.getAdditionalParameter(mapping.getProperty())
                            : statement
                                    .getConfiguration()
                                    .newMetaObject(parameter)
                                    .getValue(mapping.getProperty()))
                    .toList();
            if (query.startsWith("select") && query.contains("chat_memory_state candidate")) {
                dueParameters.add(values);
            }
            boolean claiming = query.startsWith("update chat_memory_state set claim_token = ?");
            boolean releasing = query.startsWith("update chat_memory_state set claim_token = null");
            Object result = invocation.proceed();
            if (statement.getId().equals(ChatMemoryCoordinationMapper.class.getName() + ".lock")
                    && Thread.currentThread().getName().startsWith("memory-extraction-")
                    && StackWalker.getInstance()
                            .walk(frames -> frames.anyMatch(
                                    frame -> frame.getClassName().equals(MemoryWorkRepository.class.getName())
                                            && frame.getMethodName().equals("check")))) {
                List<?> rows = (List<?>) result;
                ChatMemoryState checked = rows.isEmpty() ? null : (ChatMemoryState) rows.getFirst();
                guarded.set(
                        checked == null || checked.getClaimToken() == null
                                ? null
                                : claims.get(checked.getClaimToken()));
            }
            if (claiming) {
                String id = (String) values.getLast();
                ChatMemoryState state = states.selectByPrimaryKey(id).orElseThrow();
                Claim claim = new Claim(
                        new MemoryScope(
                                id,
                                state.getUserId(),
                                state.getCharacterUid(),
                                state.getGeneration(),
                                EmbeddingStoreType.of(state.getStoreType())),
                        state.getClaimToken(),
                        state.getClaimDeadline(),
                        state.getLatestFinalizedId(),
                        state.getProfileRevalidationPending() == 1,
                        state.getFingerprint(),
                        state.getIdleThroughId(),
                        state.getLastActivity(),
                        state.getProfileId());
                claims.put(claim.token(), claim);
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        claimed.add(claim);
                    }
                });
            } else if (releasing) {
                String id = (String) values.getLast();
                if (query.contains("retry_attempts")) {
                    failures.add(id);
                }
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        onRelease.accept(id);
                    }
                });
            }
            return result;
        }
    }

    private record Node(
            RedissonClient client,
            MemoryScheduler scheduler,
            MemoryModelResolver models,
            MemoryMaintenance maintenance,
            LinkedBlockingQueue<String> scans,
            List<Claim> executed) {}
}
