package fun.freechat;

import static org.junit.jupiter.api.Assertions.*;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.output.TokenUsage;
import fun.freechat.mapper.ChatHistoryMapper;
import fun.freechat.mapper.ChatMemoryCommitMapper;
import fun.freechat.mapper.ChatMemoryCoordinationMapper;
import fun.freechat.mapper.ChatMemoryStateMapper;
import fun.freechat.model.ChatHistory;
import fun.freechat.model.ChatMemoryCommit;
import fun.freechat.model.ChatMemoryState;
import fun.freechat.service.chat.SystemPromptSnapshotStore;
import fun.freechat.service.chat.memory.LongTermMemoryProperties;
import fun.freechat.service.chat.memory.MemoryDocument.Kind;
import fun.freechat.service.chat.memory.MemoryDocumentCodec;
import fun.freechat.service.chat.memory.MemoryManifest;
import fun.freechat.service.chat.memory.MemoryManifest.Entry;
import fun.freechat.service.chat.memory.MemoryPublicationRepository;
import fun.freechat.service.chat.memory.MemoryPublicationRepository.Failure;
import fun.freechat.service.chat.memory.MemoryPublicationRepository.Operation;
import fun.freechat.service.chat.memory.MemoryPublicationRepository.Snapshot;
import fun.freechat.service.chat.memory.MemoryScope;
import fun.freechat.service.chat.memory.MemoryTurnRepository;
import fun.freechat.service.chat.memory.MemoryTurnRepository.Origin;
import fun.freechat.service.chat.memory.MemoryTurnRepository.TurnLease;
import fun.freechat.service.common.impl.LocalFileStoreImpl;
import fun.freechat.service.enums.EmbeddingStoreType;
import fun.freechat.service.util.InfoUtils;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
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
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/** Real MySQL protocol tests with fabricated typed vector IDs/hashes; no application or model bootstrap. */
@Testcontainers
@Timeout(120)
class MemoryPublicationRepositoryIT {
    @Container
    private static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.0.36")
            // Root is needed for failure-injection triggers when binary logging is enabled.
            .withUsername("root")
            .withDatabaseName("freechat")
            .withInitScript("sql/schema.sql");

    private static final String FINGERPRINT = MemoryDocumentCodec.hash("publication-test-fingerprint");
    private static final String MODEL = "fabricated-extractor";
    private static final String CONFLICT = "Memory publication state changed or lease expired";
    private static final String TRANSACTION_FAILURE = "Memory publication transaction failed";
    private static HikariDataSource dataSource;
    private static JdbcTemplate sql;
    private static ChatMemoryStateMapper states;
    private static ChatHistoryMapper histories;
    private static ChatMemoryCommitMapper commits;
    private static ChatMemoryCoordinationMapper coordination;
    private static DataSourceTransactionManager transactions;
    private static final HistoryQueryProbe historyQueries = new HistoryQueryProbe();

    @TempDir
    Path snapshotDirectory;

    private SystemPromptSnapshotStore snapshots;
    private LongTermMemoryProperties properties;
    private MemoryTurnRepository turns;
    private MemoryPublicationRepository publication;
    private MemoryScope scope;

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
        configuration.addMapper(ChatMemoryCommitMapper.class);
        configuration.addMapper(ChatMemoryCoordinationMapper.class);
        configuration.addInterceptor(historyQueries);
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
    void freshScope() {
        properties = new LongTermMemoryProperties();
        properties.afterPropertiesSet();
        var files = new LocalFileStoreImpl();
        org.springframework.test.util.ReflectionTestUtils.setField(files, "basePathStr", snapshotDirectory.toString());
        snapshots = new SystemPromptSnapshotStore(files);
        turns = new MemoryTurnRepository(coordination, states, histories, transactions, properties, snapshots);
        publication =
                new MemoryPublicationRepository(coordination, states, commits, histories, transactions, properties);
        scope = new MemoryScope(
                UUID.randomUUID().toString().replace("-", ""),
                "owner",
                "character",
                1,
                EmbeddingStoreType.DEFAULT_LONG_TERM_MEMORY);
        turns.initialize(scope, FINGERPRINT);
    }

    @ParameterizedTest
    @EnumSource(Operation.class)
    void snapshotPrepareCheckPublishAuthorizesOnlyExactManifestAndChangesOnlyOperationState(Operation operation)
            throws Exception {
        Snapshot snapshot = eligibleSnapshot(operation);
        ChatMemoryState before = state();
        assertTrue(commits.selectByPrimaryKey(snapshot.attemptId()).isEmpty());
        assertEquals(before.getFingerprint(), snapshot.fingerprint());
        assertEquals(
                operation == Operation.OVERFLOW ? before.getOverflowThroughId() : before.getIdleThroughId(),
                snapshot.expectedCursor());
        // The extraction result is deliberately created only after the source snapshot exists.
        MemoryManifest manifest = manifest(operation);
        TokenUsage usage = new TokenUsage(17, 23);
        assertUnauthorized(snapshot, manifest);
        publication.prepare(snapshot, manifest, MODEL, usage);
        ChatMemoryCommit prepared = attempt(snapshot);
        assertEquals(before, state(), "Preparing cannot expose a head or advance a cursor");
        assertEquals("prepared", prepared.getStatus());
        assertEquals(scope.chatId(), prepared.getChatId());
        assertEquals(scope.generation(), prepared.getGeneration());
        assertEquals(MemoryScope.SCHEMA_VERSION, prepared.getSchemaVersion());
        assertEquals(operation.name(), prepared.getOperation());
        assertEquals(snapshot.sourceEndId(), prepared.getSourceEndId());
        assertEquals(operation == Operation.REVALIDATE ? 0L : snapshot.sourceStartId(), prepared.getSourceStartId());
        assertEquals(snapshot.expectedHead(), prepared.getExpectedHead());
        assertEquals(snapshot.expectedCursor(), prepared.getExpectedCursor());
        assertEquals(snapshot.leaseToken(), prepared.getLeaseToken());
        assertEquals(FINGERPRINT, prepared.getFingerprint());
        assertEquals(MODEL, prepared.getModelId());
        assertEquals(
                operation == Operation.OVERFLOW ? before.getTurnDeadline() : before.getClaimDeadline(),
                prepared.getLeaseUntil());
        assertEquals(usage, InfoUtils.deserialize(prepared.getTokenUsage()));
        assertEquals(manifest, MemoryDocumentCodec.decodeManifest(prepared.getManifest()));
        assertEquals(
                snapshot.sourceHash(),
                InfoUtils.defaultMapper()
                        .readTree(prepared.getProgress())
                        .get("sourceHash")
                        .textValue());
        assertEquals(
                1, InfoUtils.defaultMapper().readTree(prepared.getProgress()).size());
        assertFalse(prepared.getManifest().contains("tokenUsage"));
        assertFalse(prepared.getProgress().contains("tokenUsage"));
        assertEquals(
                "OBJECT",
                sql.queryForObject(
                        "SELECT JSON_TYPE(manifest) FROM chat_memory_commit WHERE chat_id = ? AND attempt_id = ?",
                        String.class,
                        scope.chatId(),
                        snapshot.attemptId()));
        // MySQL's JSON column rewrites whitespace/key order, so the SQL representation is not the input text.
        assertNotEquals(MemoryDocumentCodec.encodeManifest(manifest), prepared.getManifest());
        assertNotEquals("{\"sourceHash\":\"" + snapshot.sourceHash() + "\"}", prepared.getProgress());
        publication.checkPrepared(scope, snapshot.attemptId());
        assertUnauthorized(snapshot, manifest);
        publication.publish(scope, snapshot.attemptId());
        assertPublishedState(before, state(), snapshot, manifest);
        assertEquals("committed", attempt(snapshot).getStatus());
        assertEquals(usage, InfoUtils.deserialize(attempt(snapshot).getTokenUsage()));
        for (Entry entry : manifest.entries()) {
            assertEquals(
                    new MemoryPublicationRepository.AuthorizedRecord(snapshot.attemptId(), FINGERPRINT, entry),
                    publication
                            .authorize(scope, snapshot.attemptId(), entry.id())
                            .orElseThrow());
        }
        assertTrue(publication
                .authorize(scope, snapshot.attemptId(), UUID.randomUUID().toString())
                .isEmpty());
        assertTrue(publication
                .authorize(scope, UUID.randomUUID().toString(), manifest.ids().getFirst())
                .isEmpty());
        assertRejected(() -> publication.publish(scope, snapshot.attemptId()));
        assertRejected(() -> publication.checkPrepared(scope, snapshot.attemptId()));
        assertRejected(() -> publication.terminate(scope, snapshot.attemptId(), Failure.CONFLICT));
        assertEquals("committed", attempt(snapshot).getStatus());
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void idlePublicationOfOriginalPrefixPreservesNewActivityAndLiveTurnLease(boolean newerFinalizedTurn) {
        Snapshot snapshot = eligibleSnapshot(Operation.IDLE);
        MemoryManifest manifest = manifest(Operation.IDLE);
        publication.prepare(snapshot, manifest, MODEL, null);
        long latestFinal = newerFinalizedTurn ? seedFinalizedTurn(1) : snapshot.sourceEndId();
        TurnLease active = turns.begin(scope, FINGERPRINT);
        append(active, "New activity after the extraction snapshot");
        ChatMemoryState before = state();
        assertTrue(before.getLastActivity().isAfter(attempt(snapshot).getGmtCreate()));
        publication.checkPrepared(scope, snapshot.attemptId());
        publication.publish(scope, snapshot.attemptId());
        ChatMemoryState after = state();
        assertEquals(latestFinal, after.getLatestFinalizedId());
        assertEquals(snapshot.sourceEndId(), after.getIdleThroughId());
        assertEquals(before.getLastActivity(), after.getLastActivity());
        assertEquals(before.getTurnToken(), after.getTurnToken());
        assertEquals(before.getTurnLeaseUntil(), after.getTurnLeaseUntil());
        assertEquals(before.getTurnDeadline(), after.getTurnDeadline());
        assertEquals(before.getTurnRevision(), after.getTurnRevision());
        turns.check(active);
        assertEquals(
                before.getDueAt(),
                after.getDueAt(),
                "Publishing an older eligible prefix must preserve the new turn's activity deadline");
        assertNotNull(after.getDueAt());
        assertPublishedState(before, after, snapshot, manifest);
        append(active, "Still admitted after publication");
    }

    @Test
    void revalidationNeedsNoNewSourceAndPreservesBothExistingCursors() {
        Snapshot idle = eligibleSnapshot(Operation.IDLE);
        publication.prepare(idle, manifest(Operation.IDLE), MODEL, null);
        publication.publish(scope, idle.attemptId());
        TurnLease lease = turns.begin(scope, FINGERPRINT);
        Snapshot overflow = publication.snapshot(scope, Operation.OVERFLOW, idle.sourceEndId(), lease.token());
        publication.prepare(overflow, manifest(Operation.OVERFLOW), MODEL, null);
        publication.publish(scope, overflow.attemptId());
        // Ending this turn would manufacture a new source boundary, so retain the existing live turn instead.
        setState("profile_revalidation_pending = 1");
        String claim = claim();
        Snapshot baseline = publication.snapshot(scope, Operation.REVALIDATE, state().getIdleThroughId(), claim);
        assertEquals(MemoryDocumentCodec.hash(""), baseline.sourceHash());
        ChatMemoryState before = state();
        MemoryManifest manifest = manifest(Operation.REVALIDATE);
        publication.prepare(baseline, manifest, MODEL, null);
        assertEquals(0L, attempt(baseline).getSourceStartId());
        publication.checkPrepared(scope, baseline.attemptId());
        publication.publish(scope, baseline.attemptId());
        assertPublishedState(before, state(), baseline, manifest);
        assertEquals(before.getIdleThroughId(), state().getIdleThroughId());
        assertEquals(before.getOverflowThroughId(), state().getOverflowThroughId());
        assertEquals(before.getLatestFinalizedId(), state().getLatestFinalizedId());
        turns.check(lease);
        assertRejected(() -> publication.snapshot(scope, Operation.REVALIDATE, baseline.sourceEndId(), claim));
    }

    @ParameterizedTest
    @EnumSource(Operation.class)
    void competingPreparedPublicationLosesExpectedCursorOrHeadCas(Operation operation) throws Exception {
        Snapshot first = eligibleSnapshot(operation);
        Snapshot second = publication.snapshot(scope, operation, first.sourceEndId(), first.leaseToken());
        MemoryManifest firstManifest = manifest(operation);
        MemoryManifest secondManifest = manifest(operation);
        publication.prepare(first, firstManifest, MODEL, null);
        publication.prepare(second, secondManifest, MODEL, null);
        ChatMemoryState before = state();
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch go = new CountDownLatch(1);
        boolean firstWon;
        try (var workers = Executors.newFixedThreadPool(2)) {
            var firstResult = workers.submit(() -> compete(first, ready, go));
            var secondResult = workers.submit(() -> compete(second, ready, go));
            try {
                assertTrue(ready.await(10, TimeUnit.SECONDS));
            } finally {
                go.countDown();
            }
            firstWon = firstResult.get(20, TimeUnit.SECONDS);
            assertNotEquals(firstWon, secondResult.get(20, TimeUnit.SECONDS));
        }
        Snapshot winner = firstWon ? first : second;
        Snapshot loser = firstWon ? second : first;
        MemoryManifest winnerManifest = firstWon ? firstManifest : secondManifest;
        MemoryManifest loserManifest = firstWon ? secondManifest : firstManifest;
        ChatMemoryState committed = state();
        assertPublishedState(before, committed, winner, winnerManifest);
        assertEquals("committed", attempt(winner).getStatus());
        assertRejected(() -> publication.checkPrepared(scope, loser.attemptId()));
        assertRejected(() -> publication.publish(scope, loser.attemptId()));
        assertEquals(committed, state());
        assertEquals("prepared", attempt(loser).getStatus());
        assertUnauthorized(loser, loserManifest);
    }

    @ParameterizedTest
    @EnumSource(
            value = Operation.class,
            names = {"OVERFLOW", "IDLE"})
    void overflowAndIdlePreparedTogetherAdvanceIndependentHeadsAndCursors(Operation firstOperation) {
        long through = seedFinalizedTurn(1);
        String claim = claim();
        TurnLease turn = turns.begin(scope, FINGERPRINT);
        Snapshot overflow = publication.snapshot(scope, Operation.OVERFLOW, through, turn.token());
        Snapshot idle = publication.snapshot(scope, Operation.IDLE, through, claim);
        MemoryManifest overflowManifest = manifest(Operation.OVERFLOW);
        MemoryManifest idleManifest = manifest(Operation.IDLE);
        publication.prepare(overflow, overflowManifest, MODEL, null);
        publication.prepare(idle, idleManifest, MODEL, null);
        Snapshot first = firstOperation == Operation.OVERFLOW ? overflow : idle;
        Snapshot second = firstOperation == Operation.OVERFLOW ? idle : overflow;
        ChatMemoryState before = state();
        publication.publish(scope, first.attemptId());
        assertPublishedState(
                before, state(), first, firstOperation == Operation.OVERFLOW ? overflowManifest : idleManifest);
        publication.checkPrepared(scope, second.attemptId());
        before = state();
        publication.publish(scope, second.attemptId());
        assertPublishedState(
                before, state(), second, firstOperation == Operation.OVERFLOW ? idleManifest : overflowManifest);
        assertEquals(through, state().getOverflowThroughId());
        assertEquals(through, state().getIdleThroughId());
        assertEquals(overflowManifest.entries().getFirst().id(), state().getSummaryId());
        assertEquals(idleManifest.entries().getLast().id(), state().getProfileId());
        turns.check(turn);
    }

    @ParameterizedTest
    @EnumSource(Operation.class)
    void headAndCursorAreIndependentlyFenced(Operation operation) {
        Snapshot snapshot = eligibleSnapshot(operation);
        MemoryManifest manifest = manifest(operation);
        publication.prepare(snapshot, manifest, MODEL, null);
        String headColumn = operation == Operation.OVERFLOW ? "summary_id" : "profile_id";
        setState(headColumn + " = '" + UUID.randomUUID() + "'");
        assertRejected(() -> publication.checkPrepared(scope, snapshot.attemptId()));
        assertRejected(() -> publication.publish(scope, snapshot.attemptId()));
        setState(headColumn + " = NULL");
        String cursorColumn = operation == Operation.OVERFLOW ? "overflow_through_id" : "idle_through_id";
        setState(cursorColumn + " = " + (snapshot.expectedCursor() + 1));
        assertRejected(() -> publication.checkPrepared(scope, snapshot.attemptId()));
        assertRejected(() -> publication.publish(scope, snapshot.attemptId()));
        assertUnauthorized(snapshot, manifest);
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "fingerprint = 'changed-fingerprint'",
                "generation = 2",
                "status = 'deleted'",
                "claim_token = 'replacement-claim'",
                "profile_revalidation_pending = 0"
            })
    void stateChangesAfterSnapshotRejectPrepareAndAfterPrepareRejectPublication(String mutation) {
        for (boolean prepared : List.of(false, true)) {
            freshScope();
            Snapshot snapshot = eligibleSnapshot(Operation.REVALIDATE);
            MemoryManifest manifest = manifest(Operation.REVALIDATE);
            if (prepared) {
                publication.prepare(snapshot, manifest, MODEL, null);
            }
            setState(mutation);
            ChatMemoryState before = state();
            if (prepared) {
                assertRejected(() -> publication.checkPrepared(scope, snapshot.attemptId()));
                assertRejected(() -> publication.publish(scope, snapshot.attemptId()));
                assertEquals("prepared", attempt(snapshot).getStatus());
            } else {
                assertRejected(() -> publication.prepare(snapshot, manifest, MODEL, null));
                assertTrue(commits.selectByPrimaryKey(snapshot.attemptId()).isEmpty());
            }
            assertEquals(before, state());
        }
    }

    @Test
    void authorizationIsFencedByOwnerCharacterStoreChatGenerationAndSchema() {
        Snapshot snapshot = eligibleSnapshot(Operation.IDLE);
        MemoryManifest manifest = manifest(Operation.IDLE);
        publication.prepare(snapshot, manifest, MODEL, null);
        publication.publish(scope, snapshot.attemptId());
        String recordId = manifest.ids().getFirst();
        for (MemoryScope invalid : List.of(
                new MemoryScope(scope.chatId(), "other-owner", scope.characterUid(), 1, scope.storeType()),
                new MemoryScope(scope.chatId(), scope.userId(), "other-character", 1, scope.storeType()),
                new MemoryScope(
                        scope.chatId(),
                        scope.userId(),
                        scope.characterUid(),
                        1,
                        EmbeddingStoreType.EN_LONG_TERM_MEMORY),
                new MemoryScope(scope.chatId(), scope.userId(), scope.characterUid(), 2, scope.storeType()))) {
            assertRejected(() -> publication.authorize(invalid, snapshot.attemptId(), recordId));
        }
        MemoryScope otherChat = new MemoryScope(
                UUID.randomUUID().toString().replace("-", ""),
                scope.userId(),
                scope.characterUid(),
                1,
                scope.storeType());
        turns.initialize(otherChat, FINGERPRINT);
        assertTrue(
                publication.authorize(otherChat, snapshot.attemptId(), recordId).isEmpty());
        setAttempt(snapshot, "schema_version = 2");
        assertTrue(publication.authorize(scope, snapshot.attemptId(), recordId).isEmpty());
        setAttempt(snapshot, "schema_version = 1");
        setState("generation = 2");
        assertRejected(() -> publication.authorize(scope, snapshot.attemptId(), recordId));
        MemoryScope current =
                new MemoryScope(scope.chatId(), scope.userId(), scope.characterUid(), 2, scope.storeType());
        assertTrue(
                publication.authorize(current, snapshot.attemptId(), recordId).isEmpty());
    }

    @Test
    void snapshotsRequireEnabledFinalBoundaryWithinCurrentChatAndCursor() {
        TurnLease first = turns.begin(scope, FINGERPRINT);
        long messageId = append(first, "Not a final boundary");
        long answerId = turns.complete(first, AiMessage.from("Final"), null, null);
        long terminal = state().getLatestFinalizedId();
        String claim = claim();
        for (long invalid : List.of(0L, first.startId(), messageId, answerId, terminal + 1)) {
            assertRejected(() -> publication.snapshot(scope, Operation.IDLE, invalid, claim));
        }
        MemoryScope other = new MemoryScope(
                UUID.randomUUID().toString().replace("-", ""),
                scope.userId(),
                scope.characterUid(),
                1,
                scope.storeType());
        turns.initialize(other, FINGERPRINT);
        TurnLease otherLease = turns.begin(other, FINGERPRINT);
        turns.abort(otherLease);
        long foreignBoundary = turns.read(other.chatId()).orElseThrow().getLatestFinalizedId();
        TurnLease aborted = turns.begin(scope, FINGERPRINT);
        turns.abort(aborted);
        long abortedBoundary = state().getLatestFinalizedId();
        assertTrue(foreignBoundary < abortedBoundary);
        assertRejected(() -> publication.snapshot(scope, Operation.IDLE, foreignBoundary, claim));
        sql.update("UPDATE chat_history SET enabled = 0 WHERE memory_id = ? AND id = ?", scope.chatId(), terminal);
        assertRejected(() -> publication.snapshot(scope, Operation.IDLE, terminal, claim));
        Snapshot snapshot = publication.snapshot(scope, Operation.IDLE, abortedBoundary, claim);
        publication.prepare(snapshot, manifest(Operation.IDLE), MODEL, null);
        publication.publish(scope, snapshot.attemptId());
        assertEquals(abortedBoundary, state().getIdleThroughId());
        assertRejected(() -> publication.snapshot(scope, Operation.IDLE, abortedBoundary, claim));
    }

    @Test
    void sourceHashCoversBeyondFirstThousandRowsAndDetectsDisablingBeforePrepareAndPublish() throws Exception {
        long through = seedFinalizedTurn(1005);
        String claim = claim();
        Snapshot snapshot = publication.snapshot(scope, Operation.IDLE, through, claim);
        List<ChatHistory> rows = new ArrayList<>();
        long cursor = 0;
        while (cursor < through) {
            List<ChatHistory> page = turns.sourcePage(scope, cursor, through, 127);
            assertFalse(page.isEmpty());
            rows.addAll(page);
            cursor = page.getLast().getId();
        }
        assertEquals(1008, rows.size());
        assertEquals(sourceHash(rows), snapshot.sourceHash());
        assertNotEquals(sourceHash(rows.subList(0, 1000)), snapshot.sourceHash());
        long lateSource = rows.get(1002).getId();
        MemoryManifest manifest = manifest(Operation.IDLE);
        sql.update("UPDATE chat_history SET enabled = 0 WHERE memory_id = ? AND id = ?", scope.chatId(), lateSource);
        assertRejected(() -> publication.prepare(snapshot, manifest, MODEL, null));
        assertTrue(commits.selectByPrimaryKey(snapshot.attemptId()).isEmpty());
        sql.update("UPDATE chat_history SET enabled = 1 WHERE memory_id = ? AND id = ?", scope.chatId(), lateSource);
        publication.prepare(snapshot, manifest, MODEL, null);
        ChatMemoryState before = state();
        sql.update("UPDATE chat_history SET enabled = 0 WHERE memory_id = ? AND id = ?", scope.chatId(), lateSource);
        assertRejected(() -> publication.publish(scope, snapshot.attemptId()));
        assertEquals(before, state());
        assertUnauthorized(snapshot, manifest);
        sql.update("UPDATE chat_history SET enabled = 1 WHERE memory_id = ? AND id = ?", scope.chatId(), lateSource);
        publication.publish(scope, snapshot.attemptId());
        assertEquals(through, state().getIdleThroughId());
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void oversizedSourceCanBeHashedAndPublishedOrSkippedWithoutSortingItsJson(boolean aborted) throws Exception {
        sql.update("""
            INSERT INTO chat_history (memory_id, gmt_create, gmt_modified)
            WITH RECURSIVE ids AS (SELECT 1 AS n UNION ALL SELECT n + 1 FROM ids WHERE n < 1000)
            SELECT ?, UTC_TIMESTAMP(), UTC_TIMESTAMP() FROM ids
            """, UUID.randomUUID().toString().replace("-", ""));
        TurnLease turn = turns.begin(scope, FINGERPRINT);
        String original = "fabricated oversized legacy source 中文 ".repeat(15000);
        long messageId = turns.append(
                turn, UserMessage.from("transformed input"), UserMessage.from(original), null, Origin.USER_INPUT, null);
        assertTrue(sql.queryForObject(
                        "SELECT OCTET_LENGTH(source_message) FROM chat_history WHERE id = ?", Long.class, messageId)
                > 500_000);
        if (aborted) {
            turns.abort(turn);
        } else {
            turns.complete(turn, AiMessage.from("Final"), null, null);
        }
        long through = state().getLatestFinalizedId();
        Snapshot snapshot = publication.snapshot(scope, Operation.IDLE, through, claim());
        assertEquals(sourceHash(turns.sourcePage(scope, 0, through, 20)), snapshot.sourceHash());
        publication.checkSnapshot(snapshot);
        if (aborted) {
            publication.advanceAborted(snapshot);
            assertEquals("skipped", attempt(snapshot).getStatus());
        } else {
            MemoryManifest manifest = manifest(Operation.IDLE);
            publication.prepare(snapshot, manifest, MODEL, null);
            String source =
                    histories.selectByPrimaryKey(messageId).orElseThrow().getSourceMessage();
            sql.update("UPDATE chat_history SET source_message = message WHERE id = ?", messageId);
            assertRejected(() -> publication.publish(scope, snapshot.attemptId()));
            sql.update("UPDATE chat_history SET source_message = ? WHERE id = ?", source, messageId);
            publication.publish(scope, snapshot.attemptId());
            assertEquals("committed", attempt(snapshot).getStatus());
        }
        assertEquals(through, state().getIdleThroughId());
    }

    @ParameterizedTest
    @ValueSource(strings = {"enabled = 0", "record_kind = 'message'"})
    void finalBoundaryChangedAfterPrepareCannotPublish(String mutation) {
        Snapshot snapshot = eligibleSnapshot(Operation.IDLE);
        MemoryManifest manifest = manifest(Operation.IDLE);
        publication.prepare(snapshot, manifest, MODEL, null);
        sql.update(
                "UPDATE chat_history SET " + mutation + " WHERE memory_id = ? AND id = ?",
                scope.chatId(),
                snapshot.sourceEndId());
        ChatMemoryState before = state();
        assertRejected(() -> publication.publish(scope, snapshot.attemptId()));
        assertEquals(before, state());
        assertUnauthorized(snapshot, manifest);
    }

    @ParameterizedTest
    @EnumSource(Operation.class)
    void expiredLeaseAndAbsoluteDeadlineRejectEveryPublicationPhase(Operation operation) {
        String prefix = operation == Operation.OVERFLOW ? "turn_" : "claim_";
        for (String expiry : List.of(prefix + "lease_until", prefix + "deadline")) {
            for (boolean prepared : List.of(false, true)) {
                freshScope();
                Snapshot snapshot = eligibleSnapshot(operation);
                MemoryManifest manifest = manifest(operation);
                if (prepared) {
                    publication.prepare(snapshot, manifest, MODEL, null);
                }
                setState(expiry + " = UTC_TIMESTAMP(6)");
                ChatMemoryState before = state();
                assertRejected(
                        () -> publication.snapshot(scope, operation, snapshot.sourceEndId(), snapshot.leaseToken()));
                if (prepared) {
                    assertRejected(() -> publication.checkPrepared(scope, snapshot.attemptId()));
                    assertRejected(() -> publication.publish(scope, snapshot.attemptId()));
                    assertUnauthorized(snapshot, manifest);
                } else {
                    assertRejected(() -> publication.prepare(snapshot, manifest, MODEL, null));
                    assertTrue(commits.selectByPrimaryKey(snapshot.attemptId()).isEmpty());
                }
                assertEquals(before, state());
            }
        }
    }

    @ParameterizedTest
    @EnumSource(Operation.class)
    void expiredPreparedAttemptCannotBeRevivedByCurrentLiveLease(Operation operation) {
        Snapshot snapshot = eligibleSnapshot(operation);
        MemoryManifest manifest = manifest(operation);
        publication.prepare(snapshot, manifest, MODEL, null);
        setAttempt(snapshot, "lease_until = UTC_TIMESTAMP(6)");
        ChatMemoryState before = state();
        assertRejected(() -> publication.checkPrepared(scope, snapshot.attemptId()));
        assertRejected(() -> publication.publish(scope, snapshot.attemptId()));
        assertEquals(before, state());
        assertUnauthorized(snapshot, manifest);
    }

    @ParameterizedTest
    @EnumSource(Operation.class)
    void terminalizationIsImmediateButGcWaitsForGraceAndCannotResurrect(Operation operation) {
        Snapshot snapshot = eligibleSnapshot(operation);
        MemoryManifest manifest = manifest(operation);
        publication.prepare(snapshot, manifest, MODEL, null);
        ChatMemoryState before = state();
        publication.terminate(scope, snapshot.attemptId(), Failure.VECTOR_WRITE);
        ChatMemoryCommit terminal = attempt(snapshot);
        assertEquals("terminal", terminal.getStatus());
        assertEquals("VECTOR_WRITE", terminal.getErrorCategory());
        assertEquals(terminal.getGmtModified().plus(properties.getGcGracePeriod()), terminal.getGcAfter());
        assertEquals(before, state());
        assertUnauthorized(snapshot, manifest);
        assertRejected(() -> publication.checkTerminal(scope, snapshot.attemptId()));
        assertRejected(() -> publication.checkPrepared(scope, snapshot.attemptId()));
        assertRejected(() -> publication.publish(scope, snapshot.attemptId()));
        assertRejected(() -> publication.prepare(snapshot, manifest, MODEL, null));
        publication.terminate(scope, snapshot.attemptId(), Failure.EXPIRED);
        assertEquals(terminal, attempt(snapshot), "Repeated terminalization cannot extend grace or replace the reason");
        setAttempt(snapshot, "gc_after = UTC_TIMESTAMP(6)");
        publication.checkTerminal(scope, snapshot.attemptId());
        assertRejected(() -> publication.publish(scope, snapshot.attemptId()));
        assertUnauthorized(snapshot, manifest);
        // A replaced generation must not make its already terminal vector IDs impossible to collect.
        setState("generation = 2, status = 'deleted'");
        publication.checkTerminal(scope, snapshot.attemptId());
        publication.terminate(scope, snapshot.attemptId(), Failure.CONFLICT);
        assertEquals("terminal", attempt(snapshot).getStatus());
    }

    @ParameterizedTest
    @EnumSource(Operation.class)
    void commitUpdateFailureRollsBackStateHeadCursorAndVersionAndSanitizesSqlError(Operation operation) {
        Snapshot snapshot = eligibleSnapshot(operation);
        MemoryManifest manifest = manifest(operation);
        publication.prepare(snapshot, manifest, MODEL, null);
        ChatMemoryState before = state();
        ChatMemoryCommit prepared = attempt(snapshot);
        sql.execute("""
            CREATE TRIGGER fail_publication_commit BEFORE UPDATE ON chat_memory_commit FOR EACH ROW
            BEGIN
              IF NEW.attempt_id = '%s' AND NEW.status = 'committed' THEN
                SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'private-source-text-and-credential-fixture';
              END IF;
            END
            """.formatted(snapshot.attemptId()));
        try {
            IllegalStateException failure =
                    assertThrows(IllegalStateException.class, () -> publication.publish(scope, snapshot.attemptId()));
            assertEquals(TRANSACTION_FAILURE, failure.getMessage());
            assertNull(failure.getCause());
            assertEquals(0, failure.getSuppressed().length);
            assertEquals(before, state(), "A commit-row failure must roll back the preceding state-row update");
            assertEquals(prepared, attempt(snapshot));
            assertUnauthorized(snapshot, manifest);
        } finally {
            sql.execute("DROP TRIGGER fail_publication_commit");
        }
        publication.checkPrepared(scope, snapshot.attemptId());
        publication.publish(scope, snapshot.attemptId());
        assertPublishedState(before, state(), snapshot, manifest);
    }

    @ParameterizedTest
    @MethodSource("invalidManifests")
    void partialOrExtraTypedManifestCannotBePrepared(Operation operation, MemoryManifest invalid) {
        Snapshot snapshot = eligibleSnapshot(operation);
        ChatMemoryState before = state();
        assertRejected(() -> publication.prepare(snapshot, invalid, MODEL, null));
        assertTrue(commits.selectByPrimaryKey(snapshot.attemptId()).isEmpty());
        assertEquals(before, state());
    }

    private static Stream<Arguments> invalidManifests() {
        Entry head = entry(Kind.WINDOW_SUMMARY, false);
        Entry archive = entry(Kind.WINDOW_SUMMARY, true);
        Entry profile = entry(Kind.PROFILE_SNAPSHOT, false);
        Entry episode = entry(Kind.EPISODE_SUMMARY, false);
        return Stream.of(
                Arguments.of(Operation.OVERFLOW, new MemoryManifest(List.of(head))),
                Arguments.of(Operation.OVERFLOW, new MemoryManifest(List.of(archive))),
                Arguments.of(Operation.OVERFLOW, new MemoryManifest(List.of(head, archive, profile))),
                Arguments.of(
                        Operation.OVERFLOW,
                        new MemoryManifest(List.of(head, archive, entry(Kind.WINDOW_SUMMARY, false)))),
                Arguments.of(Operation.IDLE, new MemoryManifest(List.of(profile))),
                Arguments.of(Operation.IDLE, new MemoryManifest(List.of(episode))),
                Arguments.of(Operation.IDLE, new MemoryManifest(List.of(episode, profile, archive))),
                Arguments.of(
                        Operation.IDLE,
                        new MemoryManifest(List.of(episode, profile, entry(Kind.PROFILE_SNAPSHOT, false)))),
                Arguments.of(Operation.REVALIDATE, new MemoryManifest(List.of(episode))),
                Arguments.of(Operation.REVALIDATE, new MemoryManifest(List.of(profile, episode))));
    }

    @ParameterizedTest
    @ValueSource(
            strings = {"unknown-field", "unknown-kind", "missing-hash", "missing-archive", "empty", "partial", "extra"})
    void malformedSqlManifestCannotPublishAndDoesNotLeakContents(String corruption) throws Exception {
        Snapshot snapshot = eligibleSnapshot(Operation.IDLE);
        MemoryManifest manifest = manifest(Operation.IDLE);
        publication.prepare(snapshot, manifest, MODEL, null);
        var json = InfoUtils.defaultMapper().readTree(attempt(snapshot).getManifest());
        var entries = (com.fasterxml.jackson.databind.node.ArrayNode) json.get("entries");
        var first = (com.fasterxml.jackson.databind.node.ObjectNode) entries.get(0);
        switch (corruption) {
            case "unknown-field" -> first.put("private-source-text", "credential-fixture");
            case "unknown-kind" -> first.put("kind", "UNKNOWN_PRIVATE_KIND");
            case "missing-hash" -> first.remove("hash");
            case "missing-archive" -> first.remove("archive");
            case "empty" -> entries.removeAll();
            case "partial" -> entries.remove(0);
            case "extra" ->
                entries.add(InfoUtils.defaultMapper()
                        .readTree(MemoryDocumentCodec.encodeManifest(manifest(Operation.REVALIDATE)))
                        .get("entries")
                        .get(0));
            default -> fail("Unexpected corruption fixture");
        }
        sql.update(
                "UPDATE chat_memory_commit SET manifest = ? WHERE chat_id = ? AND attempt_id = ?",
                json.toString(),
                scope.chatId(),
                snapshot.attemptId());
        ChatMemoryState before = state();
        assertRejected(() -> publication.publish(scope, snapshot.attemptId()));
        assertEquals(before, state());
        assertEquals("prepared", attempt(snapshot).getStatus());
        assertUnauthorized(snapshot, manifest);
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "{}",
                "{\"sourceHash\":123}",
                "{\"sourceHash\":\"private-not-a-hash\"}",
                "{\"sourceHash\":\"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\",\"extra\":1}"
            })
    void malformedSqlProgressIsRejectedWithoutLeakingJson(String progress) {
        Snapshot snapshot = eligibleSnapshot(Operation.IDLE);
        publication.prepare(snapshot, manifest(Operation.IDLE), MODEL, null);
        sql.update(
                "UPDATE chat_memory_commit SET progress = ? WHERE chat_id = ? AND attempt_id = ?",
                progress,
                scope.chatId(),
                snapshot.attemptId());
        ChatMemoryState before = state();
        assertRejected(() -> publication.checkPrepared(scope, snapshot.attemptId()));
        assertRejected(() -> publication.publish(scope, snapshot.attemptId()));
        assertEquals(before, state());
    }

    @ParameterizedTest
    @EnumSource(
            value = Operation.class,
            names = {"OVERFLOW", "IDLE"})
    void abortedAdvanceIsAtomicMetadataOnlyAndPreservesBothHeadsAndUnrelatedCursor(Operation operation)
            throws Exception {
        TurnLease aborted = turns.begin(scope, FINGERPRINT);
        append(aborted, "Aborted evidence must never become memory");
        turns.abort(aborted);
        long through = state().getLatestFinalizedId();
        setState("summary_id = '" + UUID.randomUUID() + "', profile_id = '" + UUID.randomUUID()
                + "', retry_attempts = 3, retry_at = UTC_TIMESTAMP(6)");
        String token = operation == Operation.OVERFLOW
                ? turns.begin(scope, FINGERPRINT).token()
                : claim();
        Snapshot snapshot = publication.snapshot(scope, operation, through, token);
        ChatMemoryState before = state();
        publication.advanceAborted(snapshot);
        ChatMemoryState after = state();
        ChatMemoryCommit row = attempt(snapshot);
        assertEquals("SKIP_" + operation, row.getOperation());
        assertEquals("skipped", row.getStatus());
        assertEquals("{}", row.getManifest());
        assertNull(row.getTokenUsage());
        assertNull(row.getGcAfter());
        assertEquals(snapshot.sourceStartId(), row.getSourceStartId());
        assertEquals(snapshot.sourceEndId(), row.getSourceEndId());
        assertEquals(snapshot.expectedCursor(), row.getExpectedCursor());
        assertEquals(snapshot.expectedHead(), row.getExpectedHead());
        assertEquals(snapshot.leaseToken(), row.getLeaseToken());
        assertEquals(snapshot.fingerprint(), row.getFingerprint());
        var progress = InfoUtils.defaultMapper().readTree(row.getProgress());
        assertEquals(1, progress.size());
        assertEquals(snapshot.sourceHash(), progress.path("sourceHash").textValue());
        if (operation == Operation.OVERFLOW) {
            before.setOverflowThroughId(through);
        } else {
            before.setIdleThroughId(through);
            before.setRetryAt(null);
            before.setRetryAttempts(0);
            before.setDueAt(null);
        }
        before.setVersion(before.getVersion() + 1);
        before.setGmtModified(after.getGmtModified());
        assertEquals(before, after);
        assertTrue(publication
                .authorize(scope, snapshot.attemptId(), UUID.randomUUID().toString())
                .isEmpty());
        assertRejected(() -> publication.checkPrepared(scope, snapshot.attemptId()));
        assertRejected(() -> publication.publish(scope, snapshot.attemptId()));
        assertRejected(() -> publication.checkTerminal(scope, snapshot.attemptId()));
        assertRejected(() -> publication.advanceAborted(snapshot));
        assertEquals(after, state());
        assertEquals(row, attempt(snapshot));
    }

    @ParameterizedTest
    @ValueSource(strings = {"full-prefix", "active-turn", "newer-finalized", "revalidation-pending"})
    void abortedIdleClearsDueOnlyForFullPrefixWithoutActiveTurnOrPendingRevalidation(String condition) {
        TurnLease aborted = turns.begin(scope, FINGERPRINT);
        turns.abort(aborted);
        long through = state().getLatestFinalizedId();
        Snapshot snapshot = publication.snapshot(scope, Operation.IDLE, through, claim());
        switch (condition) {
            case "active-turn" -> turns.begin(scope, FINGERPRINT);
            case "newer-finalized" -> seedFinalizedTurn(1);
            case "revalidation-pending" -> setState("profile_revalidation_pending = 1");
            default -> {}
        }
        ChatMemoryState before = state();
        publication.advanceAborted(snapshot);
        assertEquals(through, state().getIdleThroughId());
        assertEquals(before.getProfileRevalidationPending(), state().getProfileRevalidationPending());
        assertEquals(before.getLastActivity(), state().getLastActivity());
        assertEquals(before.getTurnToken(), state().getTurnToken());
        assertEquals(before.getLatestFinalizedId(), state().getLatestFinalizedId());
        if (condition.equals("full-prefix")) {
            assertNull(state().getDueAt());
        } else {
            assertNotNull(state().getDueAt());
            assertEquals(before.getDueAt(), state().getDueAt());
        }
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "complete",
                "two-aborts",
                "complete-then-abort",
                "missing-start",
                "foreign-start",
                "foreign-message",
                "second-start",
                "complete-message",
                "noncanonical-terminal"
            })
    void abortedAdvanceRequiresExactlyOneEnabledCanonicalStartMessagesAbortSpan(String shape) {
        if (shape.equals("complete-then-abort")) {
            seedFinalizedTurn(1);
        }
        TurnLease turn = turns.begin(scope, FINGERPRINT);
        long message = append(turn, "Source fixture");
        if (shape.equals("complete")) {
            turns.complete(turn, AiMessage.from("Final"), null, null);
        } else {
            turns.abort(turn);
        }
        if (shape.equals("two-aborts")) {
            turns.abort(turns.begin(scope, FINGERPRINT));
        }
        long through = state().getLatestFinalizedId();
        switch (shape) {
            case "missing-start" -> sql.update("UPDATE chat_history SET enabled = 0 WHERE id = ?", turn.startId());
            case "foreign-start" ->
                sql.update(
                        "UPDATE chat_history SET turn_id = ? WHERE id = ?",
                        UUID.randomUUID().toString(),
                        turn.startId());
            case "foreign-message" ->
                sql.update(
                        "UPDATE chat_history SET turn_id = ? WHERE id = ?",
                        UUID.randomUUID().toString(),
                        message);
            case "second-start" ->
                sql.update("UPDATE chat_history SET record_kind = 'turn-start' WHERE id = ?", message);
            case "complete-message" ->
                sql.update("UPDATE chat_history SET record_kind = 'turn-complete' WHERE id = ?", message);
            case "noncanonical-terminal" ->
                sql.update("UPDATE chat_history SET turn_id = 'invalid-id' WHERE id = ?", through);
            default -> {}
        }
        // Snapshot after mutation: the source hash is current, so this tests span validation rather than only hashing.
        Snapshot snapshot = publication.snapshot(scope, Operation.IDLE, through, claim());
        ChatMemoryState before = state();
        assertConflict(() -> publication.advanceAborted(snapshot));
        assertEquals(before, state());
        assertTrue(commits.selectByPrimaryKey(snapshot.attemptId()).isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"source", "fingerprint", "generation", "head", "cursor", "lease", "deadline", "token"})
    void abortedAdvanceRechecksSourceFingerprintGenerationCursorHeadAndLease(String change) {
        TurnLease turn = turns.begin(scope, FINGERPRINT);
        long message = append(turn, "Source fixture");
        turns.abort(turn);
        Snapshot snapshot = publication.snapshot(scope, Operation.IDLE, state().getLatestFinalizedId(), claim());
        switch (change) {
            case "source" -> sql.update("UPDATE chat_history SET enabled = 0 WHERE id = ?", message);
            case "fingerprint" -> setState("fingerprint = 'changed'");
            case "generation" -> setState("generation = 2");
            case "head" -> setState("profile_id = '" + UUID.randomUUID() + "'");
            case "cursor" -> setState("idle_through_id = " + turn.startId());
            case "lease" -> setState("claim_lease_until = UTC_TIMESTAMP(6)");
            case "deadline" -> setState("claim_deadline = UTC_TIMESTAMP(6)");
            case "token" -> setState("claim_token = '" + UUID.randomUUID() + "'");
            default -> fail("Unknown fixture");
        }
        ChatMemoryState before = state();
        assertConflict(() -> publication.advanceAborted(snapshot));
        assertEquals(before, state());
        assertTrue(commits.selectByPrimaryKey(snapshot.attemptId()).isEmpty());
    }

    @Test
    void abortedAdvanceRejectsRevalidationAndRollsBackStateWhenSkipInsertFails() {
        Snapshot revalidate = eligibleSnapshot(Operation.REVALIDATE);
        ChatMemoryState unchanged = state();
        assertConflict(() -> publication.advanceAborted(revalidate));
        assertEquals(unchanged, state());
        TurnLease turn = turns.begin(scope, FINGERPRINT);
        turns.abort(turn);
        Snapshot snapshot = publication.snapshot(scope, Operation.IDLE, state().getLatestFinalizedId(), claim());
        ChatMemoryState before = state();
        sql.execute("""
            CREATE TRIGGER fail_skip BEFORE INSERT ON chat_memory_commit FOR EACH ROW
            BEGIN
              IF NEW.attempt_id = '%s' THEN
                SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'private-source-and-credential-fixture';
              END IF;
            END
            """.formatted(snapshot.attemptId()));
        try {
            assertRejected(() -> publication.advanceAborted(snapshot));
            assertEquals(before, state(), "Skip insert failure must roll back the preceding cursor/version update");
            assertTrue(commits.selectByPrimaryKey(snapshot.attemptId()).isEmpty());
        } finally {
            sql.execute("DROP TRIGGER fail_skip");
        }
        publication.advanceAborted(snapshot);
        assertEquals(snapshot.sourceEndId(), state().getIdleThroughId());
    }

    @Test
    void abortedAdvanceValidatesMessagesBeyondFirstThousandRows() {
        TurnLease turn = turns.begin(scope, FINGERPRINT);
        long last = 0;
        for (int index = 0; index < 1005; index++) {
            last = append(turn, "Aborted fixture " + index);
        }
        turns.abort(turn);
        long through = state().getLatestFinalizedId();
        sql.update(
                "UPDATE chat_history SET turn_id = ? WHERE id = ?",
                UUID.randomUUID().toString(),
                last);
        Snapshot invalid = publication.snapshot(scope, Operation.IDLE, through, claim());
        assertConflict(() -> publication.advanceAborted(invalid));
        assertEquals(0, state().getIdleThroughId());
        sql.update("UPDATE chat_history SET turn_id = ? WHERE id = ?", turn.token(), last);
        Snapshot valid = publication.snapshot(scope, Operation.IDLE, through, claim());
        publication.advanceAborted(valid);
        assertEquals(through, state().getIdleThroughId());
        assertEquals("{}", attempt(valid).getManifest());
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void publicationPagesNeverSortPayloadsOrRetainEarlierPages(boolean aborted) {
        TurnLease turn = turns.begin(scope, FINGERPRINT);
        for (int index = 0; index < 130; index++) {
            append(turn, "Page fixture " + index);
        }
        if (aborted) {
            turns.abort(turn);
        } else {
            turns.complete(turn, AiMessage.from("Final"), null, null);
        }
        long through = state().getLatestFinalizedId();
        String claim = claim();
        historyQueries.start();
        try {
            Snapshot snapshot = publication.snapshot(scope, Operation.IDLE, through, claim);
            if (aborted) {
                publication.advanceAborted(snapshot);
            } else {
                publication.prepare(snapshot, manifest(Operation.IDLE), MODEL, null);
                publication.publish(scope, snapshot.attemptId());
            }
            assertEquals(through, state().getIdleThroughId());
            assertTrue(historyQueries.pages >= 9);
            assertEquals(64, historyQueries.maxRows);
            assertFalse(historyQueries.sortedPayloads, "SQL must order IDs, not JSON payload rows");
            assertFalse(historyQueries.retainedPreviousPage, "Transaction-local cache must release previous pages");
        } finally {
            historyQueries.active = false;
        }
    }

    @org.apache.ibatis.plugin.Intercepts(
            @org.apache.ibatis.plugin.Signature(
                    type = org.apache.ibatis.executor.Executor.class,
                    method = "query",
                    args = {
                        org.apache.ibatis.mapping.MappedStatement.class, Object.class,
                        org.apache.ibatis.session.RowBounds.class, org.apache.ibatis.session.ResultHandler.class
                    }))
    private static final class HistoryQueryProbe implements org.apache.ibatis.plugin.Interceptor {
        private boolean active;
        private boolean sortedPayloads;
        private boolean retainedPreviousPage;
        private int pages;
        private int maxRows;
        private org.apache.ibatis.cache.CacheKey previousPage;

        void start() {
            active = true;
            sortedPayloads = false;
            retainedPreviousPage = false;
            pages = 0;
            maxRows = 0;
            previousPage = null;
        }

        @Override
        public Object intercept(org.apache.ibatis.plugin.Invocation invocation) throws Throwable {
            Object result = invocation.proceed();
            if (active) {
                var statement =
                        (org.apache.ibatis.mapping.MappedStatement) invocation.getArgs()[0];
                Object parameters = invocation.getArgs()[1];
                var bound = statement.getBoundSql(parameters);
                String query = bound.getSql().toLowerCase(java.util.Locale.ROOT);
                if (query.contains("from chat_history") && query.contains("source_message")) {
                    sortedPayloads |= query.contains("order by");
                    if (query.contains(" in (")) {
                        var executor = (org.apache.ibatis.executor.Executor) invocation.getTarget();
                        if (previousPage != null) {
                            retainedPreviousPage |= executor.isCached(statement, previousPage);
                        }
                        previousPage = executor.createCacheKey(
                                statement,
                                parameters,
                                (org.apache.ibatis.session.RowBounds) invocation.getArgs()[2],
                                bound);
                        pages++;
                        maxRows = Math.max(maxRows, ((List<?>) result).size());
                    }
                }
            }
            return result;
        }
    }

    private static void assertConflict(Executable operation) {
        IllegalStateException failure = assertThrows(IllegalStateException.class, operation);
        assertEquals(CONFLICT, failure.getMessage());
        assertNull(failure.getCause());
        assertEquals(0, failure.getSuppressed().length);
    }

    private Snapshot eligibleSnapshot(Operation operation) {
        if (operation == Operation.REVALIDATE) {
            setState("profile_revalidation_pending = 1");
            return publication.snapshot(scope, operation, state().getIdleThroughId(), claim());
        }
        long through = seedFinalizedTurn(1);
        String token = operation == Operation.OVERFLOW
                ? turns.begin(scope, FINGERPRINT).token()
                : claim();
        return publication.snapshot(scope, operation, through, token);
    }

    private long seedFinalizedTurn(int messages) {
        TurnLease lease = turns.begin(scope, FINGERPRINT);
        for (int index = 0; index < messages; index++) {
            append(lease, "Source " + index + ": café 中文");
        }
        turns.complete(lease, AiMessage.from("Final answer"), null, new TokenUsage(2, 3));
        return state().getLatestFinalizedId();
    }

    private long append(TurnLease lease, String text) {
        return turns.append(lease, UserMessage.from(text), UserMessage.from(text), null, Origin.USER_INPUT, null);
    }

    /** Claim admission belongs to the worker; install only a scoped, DB-clock-based eligible claim here. */
    private String claim() {
        String token = UUID.randomUUID().toString();
        assertEquals(
                1,
                sql.update(
                        """
                        UPDATE chat_memory_state SET claim_token = ?,
                            claim_lease_until = UTC_TIMESTAMP(6) + INTERVAL 5 MINUTE,
                            claim_deadline = UTC_TIMESTAMP(6) + INTERVAL 10 MINUTE,
                            last_activity = UTC_TIMESTAMP(6) - INTERVAL 2 HOUR,
                            due_at = UTC_TIMESTAMP(6) - INTERVAL 1 HOUR
                        WHERE chat_id = ? AND generation = ? AND user_id = ? AND character_uid = ? AND store_type = ?
                        """,
                        token,
                        scope.chatId(),
                        scope.generation(),
                        scope.userId(),
                        scope.characterUid(),
                        scope.storeType().text()));
        return token;
    }

    private ChatMemoryState state() {
        return states.selectByPrimaryKey(scope.chatId()).orElseThrow();
    }

    private ChatMemoryCommit attempt(Snapshot snapshot) {
        return commits.selectByPrimaryKey(snapshot.attemptId()).orElseThrow();
    }

    private void setState(String assignments) {
        assertEquals(
                1, sql.update("UPDATE chat_memory_state SET " + assignments + " WHERE chat_id = ?", scope.chatId()));
    }

    private void setAttempt(Snapshot snapshot, String assignments) {
        assertEquals(
                1,
                sql.update(
                        "UPDATE chat_memory_commit SET " + assignments + " WHERE chat_id = ? AND attempt_id = ?",
                        scope.chatId(),
                        snapshot.attemptId()));
    }

    private static MemoryManifest manifest(Operation operation) {
        return new MemoryManifest(
                switch (operation) {
                    case OVERFLOW -> List.of(entry(Kind.WINDOW_SUMMARY, false), entry(Kind.WINDOW_SUMMARY, true));
                    case IDLE -> List.of(entry(Kind.EPISODE_SUMMARY, false), entry(Kind.PROFILE_SNAPSHOT, false));
                    case REVALIDATE -> List.of(entry(Kind.PROFILE_SNAPSHOT, false));
                });
    }

    private static Entry entry(Kind kind, boolean archive) {
        String id = UUID.randomUUID().toString();
        return new Entry(id, kind, archive, MemoryDocumentCodec.hash("fabricated-vector-record-" + id));
    }

    private void assertUnauthorized(Snapshot snapshot, MemoryManifest manifest) {
        for (String id : manifest.ids()) {
            assertTrue(publication.authorize(scope, snapshot.attemptId(), id).isEmpty());
        }
    }

    private boolean compete(Snapshot snapshot, CountDownLatch ready, CountDownLatch go) throws InterruptedException {
        ready.countDown();
        assertTrue(go.await(10, TimeUnit.SECONDS));
        try {
            publication.publish(scope, snapshot.attemptId());
            return true;
        } catch (IllegalStateException rejected) {
            assertEquals(CONFLICT, rejected.getMessage());
            assertNull(rejected.getCause());
            return false;
        }
    }

    private static void assertRejected(Executable operation) {
        IllegalStateException failure = assertThrows(IllegalStateException.class, operation);
        assertTrue(
                List.of(CONFLICT, TRANSACTION_FAILURE).contains(failure.getMessage()),
                "Only a sanitized, fixed publication error may cross the repository boundary");
        assertNull(failure.getCause());
        assertEquals(0, failure.getSuppressed().length);
    }

    private static void assertPublishedState(
            ChatMemoryState before, ChatMemoryState after, Snapshot snapshot, MemoryManifest manifest) {
        // Full model equality also detects accidental overwrites of activity, live leases, unrelated heads and cursors.
        if (snapshot.operation() == Operation.OVERFLOW) {
            before.setSummaryId(manifest.entries().stream()
                    .filter(entry -> !entry.archive())
                    .findFirst()
                    .orElseThrow()
                    .id());
            before.setOverflowThroughId(snapshot.sourceEndId());
        } else {
            before.setProfileId(manifest.entries().stream()
                    .filter(entry -> entry.kind() == Kind.PROFILE_SNAPSHOT)
                    .findFirst()
                    .orElseThrow()
                    .id());
            before.setProfileRevalidationPending((byte) 0);
            if (snapshot.operation() == Operation.IDLE) {
                before.setIdleThroughId(snapshot.sourceEndId());
            }
            before.setRetryAttempts(0);
            before.setRetryAt(null);
            // Catch-up may clear idle scheduling only when there is no live foreground activity to preserve.
            if (before.getTurnToken() == null && before.getIdleThroughId().equals(before.getLatestFinalizedId())) {
                before.setDueAt(null);
            }
        }
        before.setVersion(before.getVersion() + 1);
        before.setGmtModified(after.getGmtModified());
        assertEquals(before, after);
    }

    private static String sourceHash(List<ChatHistory> rows) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        for (ChatHistory row : rows) {
            for (Object field : new Object[] {
                row.getId(),
                row.getEnabled(),
                row.getTurnId(),
                row.getRecordKind(),
                row.getMessageOrigin(),
                row.getMessage(),
                row.getSourceMessage()
            }) {
                byte[] bytes = Objects.toString(field, "").getBytes(StandardCharsets.UTF_8);
                digest.update(ByteBuffer.allocate(4).putInt(bytes.length).array());
                digest.update(bytes);
            }
        }
        return HexFormat.of().formatHex(digest.digest());
    }
}
