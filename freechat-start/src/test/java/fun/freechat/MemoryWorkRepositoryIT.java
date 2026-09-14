package fun.freechat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.delegatesTo;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import fun.freechat.mapper.ChatHistoryMapper;
import fun.freechat.mapper.ChatMemoryCommitMapper;
import fun.freechat.mapper.ChatMemoryCoordinationMapper;
import fun.freechat.mapper.ChatMemoryStateMapper;
import fun.freechat.model.ChatMemoryState;
import fun.freechat.service.chat.SystemPromptSnapshotStore;
import fun.freechat.service.chat.memory.LongTermMemoryProperties;
import fun.freechat.service.chat.memory.MemoryDocument.Kind;
import fun.freechat.service.chat.memory.MemoryDocumentCodec;
import fun.freechat.service.chat.memory.MemoryManifest;
import fun.freechat.service.chat.memory.MemoryPublicationRepository;
import fun.freechat.service.chat.memory.MemoryPublicationRepository.Operation;
import fun.freechat.service.chat.memory.MemoryScope;
import fun.freechat.service.chat.memory.MemoryTurnRepository;
import fun.freechat.service.chat.memory.MemoryWorkRepository;
import fun.freechat.service.chat.memory.MemoryWorkRepository.Claim;
import fun.freechat.service.common.impl.LocalFileStoreImpl;
import fun.freechat.service.enums.EmbeddingStoreType;
import java.nio.file.Path;
import java.sql.Connection;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/** Isolated real SQL, with no application bootstrap, providers, vector stores, Redis, or memory files. */
@Testcontainers
@Timeout(60)
class MemoryWorkRepositoryIT {
    @Container
    private static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.0.36")
            .withUsername("root") // Isolated failure-injection triggers with binary logging enabled.
            .withDatabaseName("freechat")
            .withInitScript("sql/schema.sql");

    private static final String FINGERPRINT = "work-test-fingerprint";
    private static final String CONFLICT = "Memory work claim or scope is no longer valid";
    private static final String FAILURE = "Memory work transaction failed";
    private static HikariDataSource dataSource;
    private static JdbcTemplate sql;
    private static ChatMemoryCoordinationMapper coordination;
    private static ChatMemoryStateMapper states;
    private static ChatHistoryMapper histories;
    private static ChatMemoryCommitMapper commits;
    private static DataSourceTransactionManager transactions;

    @TempDir
    Path snapshotDirectory;

    private SystemPromptSnapshotStore snapshots;
    private LongTermMemoryProperties properties;
    private MemoryWorkRepository work;
    private MemoryTurnRepository turns;
    private MemoryPublicationRepository publications;
    private MemoryScope scope;

    @BeforeAll
    static void configureSqlOnly() throws Exception {
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
        configuration.addMapper(ChatMemoryStateMapper.class);
        configuration.addMapper(ChatHistoryMapper.class);
        configuration.addMapper(ChatMemoryCommitMapper.class);
        configuration.addMapper(ChatMemoryCoordinationMapper.class);
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setConfiguration(configuration);
        SqlSessionTemplate sessions = new SqlSessionTemplate(factory.getObject());
        states = sessions.getMapper(ChatMemoryStateMapper.class);
        histories = sessions.getMapper(ChatHistoryMapper.class);
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
    void freshState() {
        sql.update("DELETE FROM chat_memory_commit");
        sql.update("DELETE FROM chat_history");
        sql.update("DELETE FROM chat_memory_state");
        properties = new LongTermMemoryProperties();
        properties.afterPropertiesSet();
        work = new MemoryWorkRepository(coordination, states, transactions, properties);
        var files = new LocalFileStoreImpl();
        org.springframework.test.util.ReflectionTestUtils.setField(files, "basePathStr", snapshotDirectory.toString());
        snapshots = new SystemPromptSnapshotStore(files);
        turns = new MemoryTurnRepository(coordination, states, histories, transactions, properties, snapshots);
        publications =
                new MemoryPublicationRepository(coordination, states, commits, histories, transactions, properties);
        scope = new MemoryScope(
                UUID.randomUUID().toString().replace("-", ""),
                "owner",
                "character",
                1,
                EmbeddingStoreType.DEFAULT_LONG_TERM_MEMORY);
        turns.initialize(scope, FINGERPRINT);
    }

    @Test
    void exactDatabaseDueAndRetryBoundariesAndClaimSnapshot() {
        source();
        LocalDateTime now = coordination.databaseNow();
        ChatMemoryCoordinationMapper fixed = mock(
                ChatMemoryCoordinationMapper.class,
                withSettings().mockMaker(org.mockito.MockMakers.SUBCLASS).defaultAnswer(delegatesTo(coordination)));
        doReturn(now).when(fixed).databaseNow();
        MemoryWorkRepository boundary = new MemoryWorkRepository(fixed, states, transactions, properties);
        sql.update(
                "UPDATE chat_memory_state SET due_at = ?, retry_at = ? WHERE chat_id = ?",
                now.plusNanos(1000),
                now,
                scope.chatId());
        assertTrue(boundary.due(null, 10).isEmpty());
        assertTrue(boundary.claim(scope.chatId()).isEmpty());
        sql.update(
                "UPDATE chat_memory_state SET due_at = ?, retry_at = ? WHERE chat_id = ?",
                now,
                now.plusNanos(1000),
                scope.chatId());
        assertTrue(boundary.due(null, 10).isEmpty());
        assertTrue(boundary.claim(scope.chatId()).isEmpty());
        sql.update("UPDATE chat_memory_state SET retry_at = ? WHERE chat_id = ?", now, scope.chatId());
        ChatMemoryState before = state();
        assertEquals(List.of(scope.chatId()), boundary.due(null, 10));
        Claim claim = boundary.claim(scope.chatId()).orElseThrow();
        assertEquals(scope, claim.scope());
        assertEquals(FINGERPRINT, claim.fingerprint());
        assertEquals(before.getLatestFinalizedId(), claim.throughId());
        assertEquals(before.getIdleThroughId(), claim.idleThroughId());
        assertEquals(before.getLastActivity(), claim.lastActivity());
        assertFalse(claim.revalidation());
        assertEquals(UUID.fromString(claim.token()).toString(), claim.token());
        assertEquals(now.plus(properties.getJobMaxDuration()), claim.deadline());
        assertEquals(now.plus(properties.getExtractionClaimLease()), state().getClaimLeaseUntil());
        assertClaimFieldsOnly(before, state());
        assertTrue(boundary.claim(scope.chatId()).isEmpty());
        assertTrue(boundary.due(null, 10).isEmpty());
        boundary.release(claim);
        assertEquals(List.of(scope.chatId()), boundary.due("", 10));
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "due_at = NULL",
                "due_at = UTC_TIMESTAMP(6) + INTERVAL 1 HOUR",
                "retry_at = UTC_TIMESTAMP(6) + INTERVAL 1 HOUR",
                "retry_attempts = 5",
                "status = 'deleted'",
                "status = 'disabled'",
                "idle_through_id = latest_finalized_id",
                "turn_token = 'active', turn_lease_until = UTC_TIMESTAMP(6) + INTERVAL 1 HOUR, turn_deadline ="
                        + " UTC_TIMESTAMP(6) + INTERVAL 2 HOUR",
                "claim_token = 'active', claim_lease_until = UTC_TIMESTAMP(6) + INTERVAL 1 HOUR, claim_deadline ="
                        + " UTC_TIMESTAMP(6) + INTERVAL 2 HOUR"
            })
    void discoveryAndClaimExcludeEveryNoneligibleState(String change) {
        readySource();
        set(change);
        ChatMemoryState before = state();
        assertTrue(work.due(null, Integer.MAX_VALUE).isEmpty());
        assertTrue(work.claim(scope.chatId()).isEmpty());
        assertEquals(before, state());
    }

    @ParameterizedTest
    @ValueSource(strings = {"turn", "claim"})
    void expiredLeaseOrAbsoluteDeadlineDoesNotBlockDiscovery(String prefix) {
        readySource();
        set(prefix + "_token = 'expired', " + prefix + "_lease_until = UTC_TIMESTAMP(6), " + prefix
                + "_deadline = UTC_TIMESTAMP(6) + INTERVAL 1 HOUR");
        assertEquals(List.of(scope.chatId()), work.due(null, 10));
        Claim first = work.claim(scope.chatId()).orElseThrow();
        work.release(first);
        set(prefix + "_token = 'expired', " + prefix + "_deadline = UTC_TIMESTAMP(6), " + prefix
                + "_lease_until = UTC_TIMESTAMP(6) + INTERVAL 1 HOUR");
        assertEquals(List.of(scope.chatId()), work.due(null, 10));
        work.claim(scope.chatId()).orElseThrow();
    }

    @Test
    void pendingRevalidationWithoutSourceCanBeClaimedAndPublished() {
        set("profile_revalidation_pending = 1, due_at = UTC_TIMESTAMP(6)");
        assertEquals(List.of(scope.chatId()), work.due(null, 10));
        Claim claim = work.claim(scope.chatId()).orElseThrow();
        assertTrue(claim.revalidation());
        assertEquals(0, claim.throughId());
        var snapshot = publications.snapshot(scope, Operation.REVALIDATE, claim.idleThroughId(), claim.token());
        publications.prepare(snapshot, manifest(false), "fake-model", null);
        publications.publish(scope, snapshot.attemptId());
        work.release(claim);
        assertEquals((byte) 0, state().getProfileRevalidationPending());
        assertEquals(0L, state().getIdleThroughId());
        assertEquals(0L, state().getOverflowThroughId());
        assertTrue(work.due(null, 10).isEmpty());
    }

    @Test
    void keysetPagesClampAndReachEligibleRowsBehindMoreThanOneBlockedPage() {
        int batch = properties.getDispatchBatchSize();
        List<String> expected = new ArrayList<>();
        for (int index = 0; index < batch * 2 + 3; index++) {
            String id = "z-%04d".formatted(index);
            seedState(id, true);
            expected.add(id);
            seedState("a-%04d".formatted(index), false);
        }
        assertEquals(expected.subList(0, batch), work.due(null, Integer.MAX_VALUE));
        assertEquals(expected.subList(0, 3), work.due(null, 3));
        List<String> actual = new ArrayList<>();
        String after = null;
        while (true) {
            List<String> page = work.due(after, Integer.MAX_VALUE);
            if (page.isEmpty()) {
                break;
            }
            assertTrue(page.size() <= batch);
            actual.addAll(page);
            after = page.getLast();
        }
        assertEquals(expected, actual);
        // A dispatcher restart rescans SQL; discovery never destructively consumes work.
        assertEquals(
                expected.subList(0, batch),
                new MemoryWorkRepository(coordination, states, transactions, properties).due(null, batch));
    }

    @Test
    void duplicateDispatchAcrossRepositoryInstancesHasExactlyOneWinner() throws Exception {
        readySource();
        CountDownLatch ready = new CountDownLatch(4);
        CountDownLatch go = new CountDownLatch(1);
        try (var workers = Executors.newFixedThreadPool(4)) {
            List<java.util.concurrent.Future<Optional<Claim>>> results = new ArrayList<>();
            for (int index = 0; index < 4; index++) {
                results.add(workers.submit(() -> {
                    ready.countDown();
                    assertTrue(go.await(10, TimeUnit.SECONDS));
                    return new MemoryWorkRepository(coordination, states, transactions, properties)
                            .claim(scope.chatId());
                }));
            }
            try {
                assertTrue(ready.await(10, TimeUnit.SECONDS));
            } finally {
                go.countDown();
            }
            List<Claim> winners = new ArrayList<>();
            for (var result : results) {
                result.get(15, TimeUnit.SECONDS).ifPresent(winners::add);
            }
            assertEquals(1, winners.size());
            assertEquals(winners.getFirst().token(), state().getClaimToken());
        }
    }

    @Test
    void expiredTakeoverFencesAllOldOperationsIncludingFailureAndRelease() {
        readySource();
        Claim old = work.claim(scope.chatId()).orElseThrow();
        set("claim_lease_until = UTC_TIMESTAMP(6)");
        rejected(() -> work.check(old));
        rejected(() -> work.renew(old));
        Claim current = work.claim(scope.chatId()).orElseThrow();
        assertNotEquals(old.token(), current.token());
        ChatMemoryState before = state();
        rejected(() -> work.check(old));
        rejected(() -> work.renew(old));
        rejected(() -> work.release(old));
        rejected(() -> work.fail(old));
        assertEquals(before, state());
        work.check(current);
        set("claim_deadline = UTC_TIMESTAMP(6)");
        rejected(() -> work.check(current));
        work.release(current); // Expired but not replaced remains safe to clear.
        assertNull(state().getClaimToken());
        assertEquals(List.of(scope.chatId()), work.due(null, 10));
    }

    @Test
    void heartbeatActuallyExtendsLeaseWithoutChangingTheSelectedWork() {
        readySource();
        Claim claim = work.claim(scope.chatId()).orElseThrow();
        set("claim_lease_until = UTC_TIMESTAMP(6) + INTERVAL 10 SECOND, retry_attempts = 2");
        ChatMemoryState before = state();
        LocalDateTime until = work.renew(claim);
        assertTrue(until.isAfter(before.getClaimLeaseUntil()));
        assertEquals(state().getGmtModified().plus(properties.getExtractionClaimLease()), until);
        assertEquals(claim.deadline(), state().getClaimDeadline());
        assertClaimFieldsOnly(before, state());
        work.check(claim);
    }

    @Test
    void absoluteDeadlineBoundsEveryRenewalAndNeverExtendsActivity() {
        readySource();
        // Validated properties allow a lease longer than the absolute job lifetime.
        properties.setExtractionClaimLease(Duration.ofMinutes(20));
        properties.afterPropertiesSet();
        Claim claim = work.claim(scope.chatId()).orElseThrow();
        assertEquals(claim.deadline(), state().getClaimLeaseUntil());
        ChatMemoryState before = state();
        assertEquals(claim.deadline(), work.renew(claim));
        assertEquals(claim.deadline(), work.renew(claim));
        assertClaimFieldsOnly(before, state());
        assertEquals(claim.deadline(), state().getClaimDeadline());
        set("claim_deadline = UTC_TIMESTAMP(6) + INTERVAL 10 SECOND");
        assertEquals(state().getClaimDeadline(), work.renew(claim));
        set("claim_deadline = UTC_TIMESTAMP(6), claim_lease_until = UTC_TIMESTAMP(6) + INTERVAL 1 HOUR");
        rejected(() -> work.renew(claim));
        rejected(() -> work.check(claim));
        assertEquals(List.of(scope.chatId()), work.due(null, 10));
    }

    @ParameterizedTest
    @ValueSource(strings = {"claim", "check", "renew", "fail"})
    void databaseTimeIsReadAfterActualRowLockWait(String operation) throws Exception {
        readySource();
        Claim claim = work.claim(scope.chatId()).orElseThrow();
        try (Connection blocker = dataSource.getConnection();
                var worker = Executors.newSingleThreadExecutor()) {
            blocker.setAutoCommit(false);
            lockRow(blocker);
            var pending = worker.submit(() -> {
                switch (operation) {
                    case "claim" -> {
                        Claim successor = work.claim(scope.chatId()).orElseThrow();
                        assertNotEquals(claim.token(), successor.token());
                    }
                    case "check" -> rejected(() -> work.check(claim));
                    case "renew" -> rejected(() -> work.renew(claim));
                    case "fail" -> work.fail(claim);
                    default -> fail("Invalid test operation");
                }
            });
            LocalDateTime lockRelease;
            try {
                awaitLockWait();
                try (var expire = blocker.prepareStatement(
                        "UPDATE chat_memory_state SET claim_lease_until = UTC_TIMESTAMP(6), due_at = UTC_TIMESTAMP(6)"
                                + " WHERE chat_id = ?")) {
                    expire.setString(1, scope.chatId());
                    expire.executeUpdate();
                }
                lockRelease = coordination.databaseNow();
            } finally {
                blocker.commit();
            }
            pending.get(15, TimeUnit.SECONDS);
            if (operation.equals("claim") || operation.equals("fail")) {
                assertFalse(state().getGmtModified().isBefore(lockRelease));
            }
        }
    }

    @Test
    void selectedPrefixSurvivesNewActivityRenewalPublicationAndRelease() {
        long first = source();
        long selected = readySource();
        Claim claim = work.claim(scope.chatId()).orElseThrow();
        assertEquals(selected, claim.throughId());
        // A worker may select a smaller finalized chunk, not the entire claimed upper bound.
        var snapshot = publications.snapshot(scope, Operation.IDLE, first, claim.token());
        publications.prepare(snapshot, manifest(true), "fake-model", null);
        long newer = source();
        var active = turns.begin(scope, FINGERPRINT);
        ChatMemoryState activity = state();
        assertTrue(newer > claim.throughId());
        work.check(claim);
        work.renew(claim);
        assertClaimFieldsOnly(activity, state());
        publications.publish(scope, snapshot.attemptId());
        assertEquals(first, state().getIdleThroughId());
        assertEquals(newer, state().getLatestFinalizedId());
        assertEquals(activity.getLastActivity(), state().getLastActivity());
        assertEquals(activity.getDueAt(), state().getDueAt());
        assertEquals(activity.getTurnToken(), state().getTurnToken());
        assertEquals(activity.getTurnDeadline(), state().getTurnDeadline());
        assertEquals(activity.getVersion() + 1, state().getVersion());
        ChatMemoryState published = state();
        work.release(claim);
        assertClaimFieldsOnly(published, state());
        assertNull(state().getClaimToken());
        turns.check(active);
        assertTrue(work.due(null, 10).isEmpty());
        turns.abort(active);
        set("due_at = UTC_TIMESTAMP(6)");
        assertEquals(List.of(scope.chatId()), work.due(null, 10));
    }

    @Test
    void publishingEntireClaimedPrefixPreservesDueTimeOfANewUnfinishedTurn() {
        readySource();
        Claim claim = work.claim(scope.chatId()).orElseThrow();
        var snapshot = publications.snapshot(scope, Operation.IDLE, claim.throughId(), claim.token());
        publications.prepare(snapshot, manifest(true), "fake-model", null);
        var active = turns.begin(scope, FINGERPRINT);
        ChatMemoryState before = state();
        work.renew(claim);
        publications.publish(scope, snapshot.attemptId());
        work.release(claim);
        assertEquals(claim.throughId(), state().getIdleThroughId());
        assertEquals(state().getLatestFinalizedId(), state().getIdleThroughId());
        assertEquals(before.getDueAt(), state().getDueAt());
        assertEquals(before.getLastActivity(), state().getLastActivity());
        turns.check(active);
        assertTrue(work.due(null, 10).isEmpty());
    }

    @Test
    void publicationProgressDoesNotInvalidateHeartbeatOrChargeAnOldFailure() {
        long first = source();
        readySource();
        Claim claim = work.claim(scope.chatId()).orElseThrow();
        var snapshot = publications.snapshot(scope, Operation.IDLE, first, claim.token());
        publications.prepare(snapshot, manifest(true), "fake-model", null);
        publications.publish(scope, snapshot.attemptId());
        ChatMemoryState before = state();
        work.check(claim);
        work.renew(claim);
        work.fail(claim);
        assertClaimFieldsOnly(before, state());
        assertEquals(0, state().getRetryAttempts());
        assertNull(state().getClaimToken());
        assertEquals(List.of(scope.chatId()), work.due(null, 10));
    }

    @Test
    void failureBackoffIsBoundedExponentialAndMaximumDoesNotDisableForeground() {
        readySource();
        properties.setRetryInitialDelay(Duration.ofSeconds(2));
        properties.setRetryMaxDelay(Duration.ofSeconds(5));
        properties.afterPropertiesSet();
        long[] caps = {2000, 4000, 5000, 5000, 5000};
        for (int attempt = 1; attempt <= properties.getMaxAttempts(); attempt++) {
            Claim claim = work.claim(scope.chatId()).orElseThrow();
            ChatMemoryState before = state();
            work.fail(claim);
            ChatMemoryState failed = state();
            assertEquals(attempt, failed.getRetryAttempts());
            long delay = Duration.between(failed.getGmtModified(), failed.getRetryAt())
                    .toMillis();
            assertTrue(delay >= caps[attempt - 1] / 2 && delay <= caps[attempt - 1]);
            assertEquals(before.getDueAt(), failed.getDueAt());
            assertEquals(before.getLastActivity(), failed.getLastActivity());
            assertEquals(before.getVersion(), failed.getVersion());
            assertEquals("active", failed.getStatus());
            assertNull(failed.getClaimToken());
            assertNull(failed.getClaimDeadline());
            assertNull(failed.getClaimLeaseUntil());
            assertTrue(work.due(null, 10).isEmpty());
            assertTrue(work.claim(scope.chatId()).isEmpty());
            rejected(() -> work.fail(claim));
            set("retry_at = UTC_TIMESTAMP(6)");
        }
        assertTrue(work.due(null, 10).isEmpty());
        assertTrue(work.claim(scope.chatId()).isEmpty());
        var active = turns.begin(scope, FINGERPRINT);
        assertEquals(0, state().getRetryAttempts());
        assertNull(state().getRetryAt());
        turns.abort(active);
        set("due_at = UTC_TIMESTAMP(6)");
        assertEquals(List.of(scope.chatId()), work.due(null, 10));
        work.claim(scope.chatId()).orElseThrow();
    }

    @Test
    void retrySaturatesWithoutIntegerOrExponentialOverflow() {
        readySource();
        properties.setMaxAttempts(Integer.MAX_VALUE);
        properties.setRetryInitialDelay(Duration.ofMillis(1));
        properties.setRetryMaxDelay(Duration.ofDays(365));
        properties.afterPropertiesSet();
        set("retry_attempts = 2147483646");
        Claim claim = work.claim(scope.chatId()).orElseThrow();
        work.fail(claim);
        assertEquals(Integer.MAX_VALUE, state().getRetryAttempts());
        Duration delay = Duration.between(state().getGmtModified(), state().getRetryAt());
        assertTrue(delay.compareTo(Duration.ofDays(365).dividedBy(2)) >= 0);
        assertTrue(delay.compareTo(Duration.ofDays(365)) <= 0);
        assertTrue(work.due(null, 10).isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"admission", "source", "fingerprint", "cursor", "profile", "revalidation"})
    void oldFailureCannotChargeChangedWorkOrOverwriteItsRetryAndActivity(String change) {
        readySource();
        set("retry_attempts = 3");
        Claim claim = work.claim(scope.chatId()).orElseThrow();
        switch (change) {
            case "admission" -> turns.begin(scope, FINGERPRINT);
            case "source" -> source();
            case "fingerprint" -> set("fingerprint = 'new-config', retry_attempts = 0, retry_at = NULL");
            case "cursor" -> set("idle_through_id = latest_finalized_id, retry_attempts = 0, retry_at = NULL");
            case "profile" -> set("profile_id = 'new-profile', retry_attempts = 0, retry_at = NULL");
            case "revalidation" -> set("profile_revalidation_pending = 1, retry_attempts = 0, retry_at = NULL");
            default -> fail("Invalid test change");
        }
        ChatMemoryState before = state();
        assertEquals(0, before.getRetryAttempts());
        work.fail(claim);
        assertClaimFieldsOnly(before, state());
        assertNull(state().getClaimToken());
        assertEquals(0, state().getRetryAttempts());
        assertNull(state().getRetryAt());
    }

    @Test
    void expiredOwnedFailureStillRecordsRetryAndReleaseNeverClearsSource() {
        readySource();
        Claim expired = work.claim(scope.chatId()).orElseThrow();
        set("claim_lease_until = UTC_TIMESTAMP(6)");
        work.fail(expired);
        assertEquals(1, state().getRetryAttempts());
        set("retry_at = UTC_TIMESTAMP(6)");
        Claim next = work.claim(scope.chatId()).orElseThrow();
        ChatMemoryState before = state();
        work.release(next);
        assertClaimFieldsOnly(before, state());
        assertEquals(1, state().getRetryAttempts());
        assertEquals(List.of(scope.chatId()), work.due(null, 10));
    }

    @Test
    void exactScopeAndGenerationFenceAllClaimOperationsWithoutPayloadLeakage() {
        readySource();
        Claim claim = work.claim(scope.chatId()).orElseThrow();
        for (MemoryScope invalid : List.of(
                new MemoryScope(scope.chatId(), "wrong-owner", scope.characterUid(), 1, scope.storeType()),
                new MemoryScope(scope.chatId(), scope.userId(), "wrong-character", 1, scope.storeType()),
                new MemoryScope(scope.chatId(), scope.userId(), scope.characterUid(), 2, scope.storeType()),
                new MemoryScope(
                        scope.chatId(),
                        scope.userId(),
                        scope.characterUid(),
                        1,
                        EmbeddingStoreType.EN_LONG_TERM_MEMORY))) {
            Claim forged = new Claim(
                    invalid,
                    claim.token(),
                    claim.deadline(),
                    claim.throughId(),
                    claim.revalidation(),
                    claim.fingerprint(),
                    claim.idleThroughId(),
                    claim.lastActivity(),
                    claim.profileId());
            ChatMemoryState before = state();
            rejected(() -> work.check(forged));
            rejected(() -> work.renew(forged));
            rejected(() -> work.release(forged));
            rejected(() -> work.fail(forged));
            assertEquals(before, state());
        }
        assertFalse(claim.toString().contains("private-dialogue-fixture"));
        assertEquals(
                List.of(
                        "scope",
                        "token",
                        "deadline",
                        "throughId",
                        "revalidation",
                        "fingerprint",
                        "idleThroughId",
                        "lastActivity",
                        "profileId"),
                Arrays.stream(Claim.class.getRecordComponents())
                        .map(java.lang.reflect.RecordComponent::getName)
                        .toList());
        set("generation = 2");
        rejected(() -> work.release(claim));
        set("generation = 1, status = 'deleted'");
        rejected(() -> work.fail(claim));
    }

    @ParameterizedTest
    @ValueSource(strings = {"claim", "renew", "release", "fail"})
    void sqlFailureRollsBackEveryWriteAndReturnsOnlySanitizedError(String operation) {
        readySource();
        Claim claim =
                operation.equals("claim") ? null : work.claim(scope.chatId()).orElseThrow();
        ChatMemoryState before = state();
        sql.execute("""
            CREATE TRIGGER fail_work_update AFTER UPDATE ON chat_memory_state FOR EACH ROW
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'private-dialogue-fixture-credential'
            """);
        try {
            IllegalStateException failure = assertThrows(IllegalStateException.class, () -> {
                switch (operation) {
                    case "claim" -> work.claim(scope.chatId());
                    case "renew" -> work.renew(claim);
                    case "release" -> work.release(claim);
                    case "fail" -> work.fail(claim);
                    default -> fail("Invalid test operation");
                }
            });
            assertEquals(FAILURE, failure.getMessage());
            assertNull(failure.getCause());
            assertEquals(0, failure.getSuppressed().length);
            assertEquals(before, state());
        } finally {
            sql.execute("DROP TRIGGER fail_work_update");
        }
        if (claim == null) {
            work.claim(scope.chatId()).orElseThrow();
        } else {
            work.check(claim);
        }
    }

    @Test
    void requiresNewReadCommittedBoundariesSurviveOuterRollback() {
        readySource();
        ChatMemoryCoordinationMapper observing = new ChatMemoryCoordinationMapper() {
            @Override
            public int initialize(ChatMemoryState state) {
                return coordination.initialize(state);
            }

            @Override
            public Optional<ChatMemoryState> lock(String id) {
                assertEquals("READ-COMMITTED", sql.queryForObject("SELECT @@transaction_isolation", String.class));
                return coordination.lock(id);
            }

            @Override
            public int retainControl(fun.freechat.model.ChatMemoryCommit row) {
                return coordination.retainControl(row);
            }

            @Override
            public Optional<String> scopeProgress(String id) {
                return coordination.scopeProgress(id);
            }

            @Override
            public long reconciling(String chatId, long generation) {
                assertEquals("READ-COMMITTED", sql.queryForObject("SELECT @@transaction_isolation", String.class));
                return coordination.reconciling(chatId, generation);
            }

            @Override
            public List<fun.freechat.model.ChatHistory> selectHistoryPage(
                    org.mybatis.dynamic.sql.select.render.SelectStatementProvider statement) {
                throw new AssertionError("Work coordination must not load transcript payloads");
            }

            @Override
            public void conversationUsage(String chatId, org.apache.ibatis.session.ResultHandler<String> handler) {
                throw new AssertionError("Work coordination must not aggregate conversational usage");
            }

            @Override
            public LocalDateTime databaseNow() {
                assertEquals("READ-COMMITTED", sql.queryForObject("SELECT @@transaction_isolation", String.class));
                return coordination.databaseNow();
            }
        };
        MemoryWorkRepository isolated = new MemoryWorkRepository(observing, states, transactions, properties);
        TransactionTemplate outer = new TransactionTemplate(transactions);
        outer.setIsolationLevel(TransactionDefinition.ISOLATION_REPEATABLE_READ);
        Claim claim = outer.execute(status -> {
            assertEquals(List.of(scope.chatId()), isolated.due(null, 1));
            Claim selected = isolated.claim(scope.chatId()).orElseThrow();
            isolated.check(selected);
            isolated.renew(selected);
            status.setRollbackOnly();
            return selected;
        });
        assertNotNull(claim);
        assertEquals(claim.token(), state().getClaimToken());
        outer.executeWithoutResult(status -> {
            isolated.fail(claim);
            status.setRollbackOnly();
        });
        assertNull(state().getClaimToken());
        assertEquals(1, state().getRetryAttempts());
        set("retry_at = UTC_TIMESTAMP(6)");
        Claim next = isolated.claim(scope.chatId()).orElseThrow();
        outer.executeWithoutResult(status -> {
            isolated.release(next);
            status.setRollbackOnly();
        });
        assertNull(state().getClaimToken());
    }

    @Test
    void invalidInputsAndAbsentRowsHavePayloadFreeBoundedResults() {
        assertTrue(work.claim("missing-chat").isEmpty());
        assertTrue(work.due(null, 1).isEmpty());
        for (String id : List.of("", " ", "x".repeat(33), "private\nidentifier")) {
            IllegalArgumentException failure = assertThrows(IllegalArgumentException.class, () -> work.claim(id));
            assertEquals("Invalid memory chat identifier", failure.getMessage());
        }
        assertThrows(IllegalArgumentException.class, () -> work.claim(null));
        assertThrows(IllegalArgumentException.class, () -> work.due("x".repeat(33), 10));
        assertThrows(IllegalArgumentException.class, () -> work.due(null, 0));
        assertThrows(IllegalArgumentException.class, () -> work.due(null, -1));
        assertThrows(IllegalArgumentException.class, () -> work.check(null));
        assertThrows(IllegalArgumentException.class, () -> work.renew(null));
        assertThrows(IllegalArgumentException.class, () -> work.release(null));
        assertThrows(IllegalArgumentException.class, () -> work.fail(null));
    }

    private long source() {
        var turn = turns.begin(scope, FINGERPRINT);
        turns.append(
                turn,
                UserMessage.from("private-dialogue-fixture"),
                null,
                null,
                MemoryTurnRepository.Origin.USER_INPUT,
                null);
        turns.complete(turn, AiMessage.from("fake-answer"), null, null);
        return state().getLatestFinalizedId();
    }

    private long readySource() {
        long through = source();
        set("due_at = UTC_TIMESTAMP(6)");
        return through;
    }

    private ChatMemoryState state() {
        return states.selectByPrimaryKey(scope.chatId()).orElseThrow();
    }

    private void set(String assignments) {
        assertEquals(
                1, sql.update("UPDATE chat_memory_state SET " + assignments + " WHERE chat_id = ?", scope.chatId()));
    }

    private static void seedState(String id, boolean eligible) {
        sql.update("""
            INSERT INTO chat_memory_state (chat_id, user_id, character_uid, store_type, fingerprint,
                last_activity, latest_finalized_id, idle_through_id, due_at, gmt_create, gmt_modified)
            VALUES (?, 'owner', 'character', 'default_long_term_memory', ?, UTC_TIMESTAMP(6), 10, ?,
                UTC_TIMESTAMP(6), UTC_TIMESTAMP(6), UTC_TIMESTAMP(6))
            """, id, FINGERPRINT, eligible ? 0 : 10);
    }

    private void lockRow(Connection connection) throws Exception {
        try (var lock =
                connection.prepareStatement("SELECT chat_id FROM chat_memory_state WHERE chat_id = ? FOR UPDATE")) {
            lock.setString(1, scope.chatId());
            try (var result = lock.executeQuery()) {
                assertTrue(result.next());
            }
        }
    }

    private static void awaitLockWait() {
        long timeout = System.nanoTime() + TimeUnit.SECONDS.toNanos(10);
        while (System.nanoTime() < timeout) {
            if (sql.queryForObject("SELECT COUNT(*) FROM performance_schema.data_lock_waits", Integer.class) > 0) {
                return;
            }
        }
        fail("Worker must wait for the SQL state row lock");
    }

    private static void assertClaimFieldsOnly(ChatMemoryState before, ChatMemoryState after) {
        before.setClaimToken(after.getClaimToken());
        before.setClaimLeaseUntil(after.getClaimLeaseUntil());
        before.setClaimDeadline(after.getClaimDeadline());
        before.setGmtModified(after.getGmtModified());
        assertEquals(before, after, "Activity, retry, source, publication, and turn fields must be untouched");
    }

    private static void rejected(Executable action) {
        IllegalStateException failure = assertThrows(IllegalStateException.class, action);
        assertEquals(CONFLICT, failure.getMessage());
        assertNull(failure.getCause());
        assertEquals(0, failure.getSuppressed().length);
    }

    private static MemoryManifest manifest(boolean episode) {
        List<MemoryManifest.Entry> entries = new ArrayList<>();
        if (episode) {
            entries.add(entry(Kind.EPISODE_SUMMARY));
        }
        entries.add(entry(Kind.PROFILE_SNAPSHOT));
        return new MemoryManifest(entries);
    }

    private static MemoryManifest.Entry entry(Kind kind) {
        String id = UUID.randomUUID().toString();
        return new MemoryManifest.Entry(id, kind, false, MemoryDocumentCodec.hash("fake-vector-" + id));
    }
}
