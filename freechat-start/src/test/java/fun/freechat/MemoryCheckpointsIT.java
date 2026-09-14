package fun.freechat;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.TokenCountEstimator;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.model.output.TokenUsage;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.filter.Filter;
import fun.freechat.mapper.ChatHistoryMapper;
import fun.freechat.mapper.ChatMemoryCommitMapper;
import fun.freechat.mapper.ChatMemoryCoordinationMapper;
import fun.freechat.mapper.ChatMemoryStateMapper;
import fun.freechat.model.ChatMemoryCommit;
import fun.freechat.model.ChatMemoryState;
import fun.freechat.service.chat.SystemPromptSnapshotStore;
import fun.freechat.service.chat.memory.*;
import fun.freechat.service.chat.memory.MemoryCheckpoints.Progress;
import fun.freechat.service.chat.memory.MemoryPublicationRepository.Operation;
import fun.freechat.service.chat.memory.MemoryPublicationRepository.Snapshot;
import fun.freechat.service.chat.memory.MemorySourceReader.Position;
import fun.freechat.service.common.impl.LocalFileStoreImpl;
import fun.freechat.service.enums.EmbeddingStoreType;
import fun.freechat.service.rag.EmbeddingModelService;
import fun.freechat.service.rag.ExactEmbeddingStoreService;
import fun.freechat.service.util.InfoUtils;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
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
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/** Real isolated SQL plus immutable, exact in-memory vectors; no provider, Spring application, or memory files. */
@Testcontainers
@Timeout(120)
class MemoryCheckpointsIT {
    @Container
    private static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.0.36")
            .withUsername("root")
            .withDatabaseName("freechat")
            .withInitScript("sql/schema.sql");

    private static final String FINGERPRINT = MemoryDocumentCodec.hash("checkpoint-baseline");
    private static final String MODEL = "deterministic-extractor";
    private static final Runnable GUARD = () -> {};
    private static HikariDataSource dataSource;
    private static JdbcTemplate sql;
    private static ChatMemoryStateMapper states;
    private static ChatHistoryMapper histories;
    private static ChatMemoryCommitMapper commits;
    private static ChatMemoryCoordinationMapper coordination;
    private static DataSourceTransactionManager transactions;

    @TempDir
    Path snapshotDirectory;

    private SystemPromptSnapshotStore snapshots;
    private LongTermMemoryProperties properties;
    private MemoryTurnRepository turns;
    private MemoryPublicationRepository publications;
    private MemoryCheckpoints checkpoints;
    private MemoryVectorRepository vectors;
    private MemoryBounds bounds;
    private FakeStores stores;
    private MemoryScope scope;
    private long sourceId;
    private long throughId;

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
        Configuration config = new Configuration();
        config.setLogImpl(NoLoggingImpl.class);
        config.addMapper(ChatMemoryStateMapper.class);
        config.addMapper(ChatHistoryMapper.class);
        config.addMapper(ChatMemoryCommitMapper.class);
        config.addMapper(ChatMemoryCoordinationMapper.class);
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setConfiguration(config);
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
    void setup() {
        properties = new LongTermMemoryProperties();
        properties.afterPropertiesSet();
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
        stores = new FakeStores();
        FakeModels models = new FakeModels();
        bounds = new MemoryBounds(models.tokenCountEstimatorForLang("default"), properties);
        vectors = new MemoryVectorRepository(
                stores,
                models,
                new MemoryBoundsFactory(properties, models, new DefaultListableBeanFactory()),
                properties);
        checkpoints = new MemoryCheckpoints(publications, vectors);
    }

    @ParameterizedTest
    @EnumSource(
            value = Operation.class,
            names = {"OVERFLOW", "IDLE"})
    void unicodeIncrementalReductionResumesAcrossFreshTokensWithoutPartialPublication(Operation operation)
            throws Exception {
        String original = "中文" + new String(Character.toChars(0x20000));
        original = original.repeat(5000);
        Snapshot snapshot = seed(operation, original);
        MemorySourceReader reader = new MemorySourceReader(turns);
        var turn = reader.nextTurn(scope, snapshot.expectedCursor(), throughId).orElseThrow();
        MemoryExtractor extractor = new MemoryExtractor(properties, bounds);
        Progress previous = null;
        StringBuilder reconstructed = new StringBuilder();
        AtomicInteger calls = new AtomicInteger();
        Instant observedAt = turn.observedAt();
        while (true) {
            Snapshot current = snapshot;
            MemoryCheckpoints restarted = new MemoryCheckpoints(publications, vectors);
            assertEquals(previous, restarted.load(current, GUARD).orElse(null));
            Position position = previous == null ? new Position(current.expectedCursor(), 0) : previous.position();
            var fragment = reader.nextFragment(scope, turn, position, bounds, 6000);
            MemoryExtractor.Output output;
            if (fragment.complete()) {
                output = previous.output();
            } else {
                assertTrue(MemoryBounds.bytes(fragment.evidence().text()) <= MemoryBounds.SOURCE_FRAGMENT_BYTES);
                if (fragment.evidence().sourceId() == sourceId) {
                    reconstructed.append(fragment.evidence().text());
                }
                String prior = previous == null ? "" : previous.output().summary();
                var input = new MemoryExtractor.Input(
                        scope,
                        current.fingerprint(),
                        operation,
                        "",
                        "",
                        prior,
                        previous == null ? List.of() : previous.output().userFacts(),
                        List.of(),
                        List.of(fragment.evidence()),
                        observedAt);
                ChatModel model = model(request -> {
                    assertTrue(((UserMessage) request.messages().getLast())
                            .singleText()
                            .contains(prior));
                    int call = calls.incrementAndGet();
                    String facts = operation == Operation.OVERFLOW
                            ? "[]"
                            : "[{\"key\":\"preference\",\"value\":\"Retained source fact\",\"sourceIds\":[" + sourceId
                                    + "],\"fictional\":false}]";
                    return response("{\"summary\":\"Compact result " + call + "\",\"userFacts\":" + facts
                            + ",\"characterDeltas\":[]}");
                });
                output = extractor.extract(
                        model,
                        null,
                        input,
                        new MemoryExtractor.Budget(
                                1,
                                Duration.ofSeconds(30),
                                () -> publications.checkSnapshot(current),
                                (id, usage) -> publications.recordUsage(current, id, MODEL, usage)));
            }
            ChatMemoryState before = state();
            Progress saved = restarted.save(current, previous, fragment.next(), output, observedAt, MODEL, null, GUARD);
            assertEquals(before, state());
            assertNotEquals(current.attemptId(), saved.attemptId());
            if (previous != null) {
                assertNotEquals(previous.attemptId(), saved.attemptId());
                assertGrace(previous.attemptId());
            }
            ChatMemoryCommit row = attempt(saved.attemptId());
            assertEquals("checkpoint", row.getStatus());
            assertEquals("CHECKPOINT_" + operation, row.getOperation());
            var json = InfoUtils.defaultMapper().readTree(row.getProgress());
            assertEquals(3, json.size());
            assertEquals(current.sourceHash(), json.path("sourceHash").textValue());
            assertEquals(
                    fragment.next().rowId(), json.path("position").path("rowId").longValue());
            assertFalse(row.getProgress().contains(output.summary()));
            assertFalse(row.getManifest().contains(output.summary()));
            assertEquals(1, manifest(saved).entries().size());
            assertTrue(publications
                    .authorize(scope, saved.attemptId(), manifest(saved).ids().getFirst())
                    .isEmpty());
            assertTrue(vectors.candidates(scope, "Compact result", 10).matches().isEmpty());
            rejected(() -> publications.publish(scope, saved.attemptId()));
            rejected(() -> publications.checkPrepared(scope, saved.attemptId()));
            previous = saved;
            if (fragment.complete()) {
                break;
            }
            snapshot = freshSnapshot(current);
            checkpointRejected(() -> restarted.load(current, GUARD));
        }
        assertEquals(original, reconstructed.toString());
        assertTrue(calls.get() >= 4);
        assertEquals(calls.get(), count("usage"));
        assertEquals(1, count("checkpoint"));
        Progress complete = previous;
        Snapshot finalSnapshot = snapshot;
        publishFinal(finalSnapshot, complete.output(), observedAt);
        assertEquals(
                throughId,
                operation == Operation.OVERFLOW ? state().getOverflowThroughId() : state().getIdleThroughId());
        assertEquals(0, operation == Operation.OVERFLOW ? state().getIdleThroughId() : state().getOverflowThroughId());
        assertGrace(complete.attemptId());
        checkpointRejected(() -> checkpoints.load(finalSnapshot, GUARD));
        assertEquals(0, stores.deletes.get());
    }

    @ParameterizedTest
    @EnumSource(
            value = Operation.class,
            names = {"OVERFLOW", "IDLE"})
    void legacyBodyBearingBoundariesResumeInsideFinalAssistantRow(Operation operation) {
        String turnId = UUID.randomUUID().toString();
        String text = ("中文" + new String(Character.toChars(0x20000))).repeat(3000);
        var start = new fun.freechat.model.ChatHistory()
                .withMemoryId(scope.chatId())
                .withTurnId(turnId)
                .withRecordKind("turn-start")
                .withMessageOrigin("user-input")
                .withEpisode(0L)
                .withGmtCreate(LocalDateTime.now())
                .withGmtModified(LocalDateTime.now())
                .withEnabled((byte) 1)
                .withMessage(dev.langchain4j.data.message.ChatMessageSerializer.messageToJson(UserMessage.from(text)));
        var end = new fun.freechat.model.ChatHistory()
                .withMemoryId(scope.chatId())
                .withTurnId(turnId)
                .withRecordKind("turn-complete")
                .withMessageOrigin("assistant-output")
                .withEpisode(0L)
                .withGmtCreate(LocalDateTime.now())
                .withGmtModified(LocalDateTime.now())
                .withEnabled((byte) 1)
                .withMessage(dev.langchain4j.data.message.ChatMessageSerializer.messageToJson(AiMessage.from(text)));
        assertEquals(1, histories.insertSelective(start));
        assertEquals(1, histories.insertSelective(end));
        sourceId = start.getId();
        throughId = end.getId();
        setState("latest_finalized_id = " + throughId);
        String token = operation == Operation.OVERFLOW
                ? turns.begin(scope, FINGERPRINT).token()
                : UUID.randomUUID().toString();
        if (operation == Operation.IDLE) {
            sql.update(
                    "UPDATE chat_memory_state SET claim_token = ?, claim_lease_until = UTC_TIMESTAMP(6) + INTERVAL 5"
                            + " MINUTE, claim_deadline = UTC_TIMESTAMP(6) + INTERVAL 10 MINUTE WHERE chat_id = ?",
                    token,
                    scope.chatId());
        }
        Snapshot snapshot = publications.snapshot(scope, operation, throughId, token);
        var reader = new MemorySourceReader(turns);
        var turn = reader.nextTurn(scope, 0, throughId).orElseThrow();
        Progress progress = null;
        boolean terminalOffset = false;
        StringBuilder original = new StringBuilder();
        StringBuilder answer = new StringBuilder();
        for (int batch = 0; batch < 100; batch++) {
            Position position = progress == null ? new Position(0, 0) : progress.position();
            var fragment = reader.nextFragment(scope, turn, position, bounds, 2000);
            if (fragment.evidence() != null) {
                (fragment.evidence().sourceId() == sourceId ? original : answer)
                        .append(fragment.evidence().text());
            }
            progress = save(snapshot, progress, fragment.next());
            terminalOffset |= progress.position().rowId() == throughId
                    && progress.position().offset() > 0;
            assertEquals(0L, state().getOverflowThroughId());
            assertEquals(0L, state().getIdleThroughId());
            snapshot = freshSnapshot(snapshot);
            assertEquals(progress, checkpoints.load(snapshot, GUARD).orElseThrow());
            if (progress.position().rowId() == throughId && progress.position().offset() == 0) {
                break;
            }
        }
        assertTrue(terminalOffset, "A checkpoint must resume from inside the body-bearing terminal row");
        assertEquals(new Position(throughId, 0), progress.position());
        assertEquals(MemoryDocumentCodec.hash(text), MemoryDocumentCodec.hash(original.toString()));
        assertEquals(MemoryDocumentCodec.hash(text), MemoryDocumentCodec.hash(answer.toString()));
        publishFinal(snapshot, progress.output(), turn.observedAt());
        assertEquals(
                throughId,
                operation == Operation.OVERFLOW ? state().getOverflowThroughId() : state().getIdleThroughId());
    }

    @Test
    void moreThanOneManifestPageOfReductionsKeepsOneEntryPerImmutableAttempt() {
        Snapshot snapshot = seed(Operation.OVERFLOW, "x".repeat(150));
        Progress previous = null;
        for (int offset = 1; offset <= 101; offset++) {
            previous = save(snapshot, previous, new Position(sourceId, offset));
            assertEquals(1, manifest(previous).entries().size());
        }
        assertEquals(101, stores.records.size());
        assertEquals(100, count("terminal"));
        assertEquals(1, count("checkpoint"));
        assertEquals(0L, state().getOverflowThroughId());
        assertEquals(previous, checkpoints.load(freshSnapshot(snapshot), GUARD).orElseThrow());
    }

    @Test
    void duplicateWorkersCompeteOnPredecessorAndOnlyOneChainHeadSurvives() throws Exception {
        Snapshot snapshot = seed(Operation.IDLE, "source evidence");
        Progress previous = save(snapshot, null, new Position(sourceId, 2));
        CountDownLatch inserted = new CountDownLatch(2);
        stores.afterInsert = () -> {
            inserted.countDown();
            await(inserted);
        };
        try (var workers = Executors.newFixedThreadPool(2)) {
            var first = workers.submit(() -> competingSave(snapshot, previous));
            var second = workers.submit(() -> competingSave(snapshot, previous));
            assertNotEquals(first.get(20, TimeUnit.SECONDS), second.get(20, TimeUnit.SECONDS));
        }
        assertEquals(1, count("checkpoint"));
        assertEquals(2, count("terminal"));
        assertEquals(3, stores.records.size());
        assertEquals(0L, state().getIdleThroughId());
        assertEquals(
                new Position(sourceId, 0),
                checkpoints.load(snapshot, GUARD).orElseThrow().position());
        assertGrace(previous.attemptId());
    }

    @ParameterizedTest
    @ValueSource(strings = {"source", "fingerprint", "generation", "head", "cursor"})
    void changedSourceOrBaselineOrPublishedStateHidesOldProgress(String change) {
        Snapshot original = seed(Operation.IDLE, "unchanged source");
        Progress prior = save(original, null, new Position(sourceId, 2));
        switch (change) {
            case "source" -> sql.update("UPDATE chat_history SET source_message = message WHERE id = ?", sourceId);
            case "fingerprint" -> setState("fingerprint = '" + MemoryDocumentCodec.hash("new baseline") + "'");
            case "generation" -> {
                setState("generation = 2");
                scope = new MemoryScope(scope.chatId(), scope.userId(), scope.characterUid(), 2, scope.storeType());
            }
            case "head" -> setState("profile_id = '" + UUID.randomUUID() + "'");
            case "cursor" -> setState("idle_through_id = " + (sourceId - 1));
            default -> fail("Unknown fixture");
        }
        checkpointRejected(() -> checkpoints.load(original, GUARD));
        Snapshot fresh = publications.snapshot(scope, Operation.IDLE, throughId, state().getClaimToken());
        assertTrue(checkpoints.load(fresh, GUARD).isEmpty());
        Progress replacement = save(fresh, null, new Position(sourceId, 3));
        assertNotEquals(prior.attemptId(), replacement.attemptId());
        if (change.equals("source") || change.equals("fingerprint") || change.equals("head")) {
            assertGrace(prior.attemptId());
            assertEquals(1, count("checkpoint"));
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"claim_lease_until", "claim_deadline"})
    void expiredLeaseCannotLoadOrSaveButFreshClaimCanResume(String field) {
        Snapshot snapshot = seed(Operation.IDLE, "long enough source");
        Progress prior = save(snapshot, null, new Position(sourceId, 2));
        setState(field + " = UTC_TIMESTAMP(6)");
        checkpointRejected(() -> checkpoints.load(snapshot, GUARD));
        checkpointRejected(() -> save(snapshot, prior, new Position(sourceId, 3)));
        Snapshot fresh = freshSnapshot(snapshot);
        assertEquals(prior, checkpoints.load(fresh, GUARD).orElseThrow());
        save(fresh, prior, new Position(sourceId, 0));
    }

    @ParameterizedTest
    @ValueSource(strings = {"source", "lease", "generation", "head"})
    void exactReadIsRecheckedAfterVectorIo(String mutation) {
        Snapshot snapshot = seed(Operation.IDLE, "original evidence");
        save(snapshot, null, new Position(sourceId, 2));
        stores.afterRead = () -> {
            switch (mutation) {
                case "source" -> sql.update("UPDATE chat_history SET enabled = 0 WHERE id = ?", sourceId);
                case "lease" -> setState("claim_lease_until = UTC_TIMESTAMP(6)");
                case "generation" -> setState("generation = 2");
                case "head" -> setState("profile_id = '" + UUID.randomUUID() + "'");
                default -> fail("Unknown fixture");
            }
        };
        checkpointRejected(() -> checkpoints.load(snapshot, GUARD));
        assertEquals(0, stores.deletes.get());
    }

    @Test
    void guardsRunBeforeAndAfterExactReadsAndMissingOrCorruptVectorsFailClosed() {
        Snapshot snapshot = seed(Operation.IDLE, "original evidence");
        Progress prior = save(snapshot, null, new Position(sourceId, 2));
        for (int failAt : List.of(1, 2, 3)) {
            AtomicInteger guards = new AtomicInteger();
            int reads = stores.reads.get();
            checkpointRejected(() -> checkpoints.load(snapshot, () -> {
                if (guards.incrementAndGet() == failAt) {
                    throw new IllegalStateException("private payload");
                }
            }));
            assertEquals(reads + (failAt == 3 ? 1 : 0), stores.reads.get());
        }
        String id = manifest(prior).ids().getFirst();
        TextSegment original = stores.records.get(id);
        stores.records.put(id, TextSegment.from(original.text() + " ", original.metadata()));
        checkpointRejected(() -> checkpoints.load(snapshot, GUARD));
        stores.records.remove(id);
        checkpointRejected(() -> checkpoints.load(snapshot, GUARD));
    }

    @ParameterizedTest
    @ValueSource(strings = {"ambiguous-insert", "sql-publish", "lease-after-insert", "source-after-insert"})
    void failedAttemptsTerminalizeWithGraceAndRetryUsesFreshIds(String failure) {
        Snapshot snapshot = seed(Operation.IDLE, "original evidence");
        if (failure.equals("sql-publish")) {
            sql.execute("""
                CREATE TRIGGER fail_checkpoint BEFORE UPDATE ON chat_memory_commit FOR EACH ROW
                BEGIN
                  IF NEW.chat_id = '%s' AND NEW.status = 'checkpoint' THEN
                    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'private-source-and-credential';
                  END IF;
                END
                """.formatted(scope.chatId()));
        }
        stores.afterInsert = () -> {
            switch (failure) {
                case "ambiguous-insert" ->
                    throw new IllegalStateException("private source", new RuntimeException("credential"));
                case "lease-after-insert" -> setState("claim_lease_until = UTC_TIMESTAMP(6)");
                case "source-after-insert" ->
                    sql.update("UPDATE chat_history SET source_message = message WHERE id = ?", sourceId);
                default -> {}
            }
        };
        try {
            checkpointRejected(() -> save(snapshot, null, new Position(sourceId, 2)));
        } finally {
            if (failure.equals("sql-publish")) {
                sql.execute("DROP TRIGGER fail_checkpoint");
            }
        }
        assertEquals(1, stores.records.size());
        assertEquals(0, count("checkpoint"));
        assertEquals(1, count("terminal"));
        String old = sql.queryForObject(
                "SELECT attempt_id FROM chat_memory_commit WHERE chat_id = ? AND status = 'terminal'",
                String.class,
                scope.chatId());
        assertGrace(old);
        assertEquals(0L, state().getIdleThroughId());
        stores.afterInsert = GUARD;
        Snapshot fresh = freshSnapshot(snapshot);
        save(fresh, null, new Position(sourceId, 3));
        assertEquals(2, stores.records.size());
        assertEquals(0, stores.deletes.get());
        sql.update("UPDATE chat_memory_commit SET gc_after = UTC_TIMESTAMP(6) WHERE attempt_id = ?", old);
        publications.checkTerminal(scope, old);
        new MemoryGarbageCollector(coordination, commits, stores, properties, transactions).collect(old);
        assertEquals(1, stores.records.size());
        assertEquals("terminal", attempt(old).getStatus(), "The compact ID tombstone is retained after deletion");
    }

    @Test
    void sqlFailureRollsBackPredecessorRetirement() {
        Snapshot snapshot = seed(Operation.IDLE, "source evidence");
        Progress prior = save(snapshot, null, new Position(sourceId, 2));
        ChatMemoryCommit before = attempt(prior.attemptId());
        sql.execute("""
            CREATE TRIGGER fail_checkpoint_successor BEFORE UPDATE ON chat_memory_commit FOR EACH ROW
            BEGIN
              IF NEW.chat_id = '%s' AND NEW.status = 'checkpoint' THEN
                SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'private-source-and-credential';
              END IF;
            END
            """.formatted(scope.chatId()));
        try {
            checkpointRejected(() -> save(snapshot, prior, new Position(sourceId, 3)));
            assertEquals(before, attempt(prior.attemptId()));
            assertEquals(prior, checkpoints.load(snapshot, GUARD).orElseThrow());
            assertEquals(1, count("checkpoint"));
            assertEquals(1, count("terminal"));
            assertEquals(2, stores.records.size());
            assertEquals(0, stores.deletes.get());
        } finally {
            sql.execute("DROP TRIGGER fail_checkpoint_successor");
        }
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "missing",
                "extra",
                "float",
                "string",
                "overflow",
                "negative",
                "past-row",
                "predecessor",
                "manifest"
            })
    void malformedProgressAndManifestNeverLeakOrResume(String corruption) throws Exception {
        Snapshot snapshot = seed(Operation.IDLE, "original evidence");
        Progress prior = save(snapshot, null, new Position(sourceId, 2));
        ChatMemoryCommit row = attempt(prior.attemptId());
        ObjectNode progress = (ObjectNode) InfoUtils.defaultMapper().readTree(row.getProgress());
        ObjectNode position = (ObjectNode) progress.get("position");
        switch (corruption) {
            case "missing" -> progress.remove("predecessor");
            case "extra" -> progress.put("summary", "private source");
            case "float" -> position.put("offset", 2.5);
            case "string" -> position.put("rowId", Long.toString(sourceId));
            case "overflow" -> position.put("offset", Long.MAX_VALUE);
            case "negative" -> position.put("offset", -1);
            case "past-row" -> position.put("offset", 100000);
            case "predecessor" -> progress.put("predecessor", "private source");
            case "manifest" ->
                sql.update("UPDATE chat_memory_commit SET manifest = '{}' WHERE attempt_id = ?", prior.attemptId());
            default -> fail("Unknown corruption");
        }
        sql.update(
                "UPDATE chat_memory_commit SET progress = ? WHERE attempt_id = ?",
                progress.toString(),
                prior.attemptId());
        int reads = stores.reads.get();
        checkpointRejected(() -> checkpoints.load(snapshot, GUARD));
        assertEquals(reads, stores.reads.get());
        assertEquals(0L, state().getIdleThroughId());
    }

    @Test
    void invalidPositionsOutputsAndRevalidationAreRejectedBeforeEmbedding() {
        Snapshot snapshot = seed(Operation.IDLE, "short text");
        for (Position invalid : List.of(
                new Position(0, 0),
                new Position(sourceId, -1),
                new Position(sourceId, 100),
                new Position(throughId, 1),
                new Position(throughId + 1, 0))) {
            checkpointRejected(() -> save(snapshot, null, invalid));
        }
        checkpointRejected(() -> checkpoints.save(
                snapshot,
                null,
                new Position(sourceId, 2),
                new MemoryExtractor.Output(" ", List.of(), List.of()),
                Instant.now(),
                MODEL,
                null,
                GUARD));
        assertEquals(0, stores.records.size());
        Progress previous = save(snapshot, null, new Position(sourceId, 3));
        checkpointRejected(() -> save(snapshot, previous, new Position(sourceId, 2)));
        checkpointRejected(() -> save(snapshot, previous, new Position(sourceId, 3)));
        Progress completedRow = save(snapshot, previous, new Position(sourceId, 0));
        checkpointRejected(() -> save(snapshot, completedRow, new Position(sourceId, 4)));
        setState("profile_revalidation_pending = 1");
        Snapshot revalidate = publications.snapshot(scope, Operation.REVALIDATE, 0, state().getClaimToken());
        checkpointRejected(() -> checkpoints.load(revalidate, GUARD));
        checkpointRejected(() -> save(revalidate, null, new Position(sourceId, 1)));
        assertEquals(2, stores.records.size());
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void usageIsIdempotentMetadataOnlyAndAcceptedAfterLeaseOrGenerationExpiry(boolean nullUsage) throws Exception {
        Snapshot snapshot = seed(Operation.IDLE, "source text");
        String callId = UUID.randomUUID().toString();
        TokenUsage usage = nullUsage ? null : new TokenUsage(7, 11);
        ChatMemoryState before = state();
        String ext = histories.selectByPrimaryKey(sourceId).orElseThrow().getExt();
        publications.recordUsage(snapshot, callId, MODEL, usage);
        ChatMemoryCommit first = attempt(callId);
        assertEquals(before, state());
        assertEquals("USAGE", first.getOperation());
        assertEquals("usage", first.getStatus());
        assertEquals("{}", first.getManifest());
        assertNull(first.getGcAfter());
        assertEquals(
                snapshot.attemptId(),
                InfoUtils.defaultMapper()
                        .readTree(first.getProgress())
                        .path("snapshotAttemptId")
                        .textValue());
        assertEquals(usage, first.getTokenUsage() == null ? null : InfoUtils.deserialize(first.getTokenUsage()));
        publications.recordUsage(snapshot, callId, MODEL, usage);
        assertEquals(first, attempt(callId));
        assertTrue(publications
                .authorize(scope, callId, UUID.randomUUID().toString())
                .isEmpty());
        publications.terminate(scope, callId, MemoryPublicationRepository.Failure.EXPIRED);
        assertEquals(first, attempt(callId));
        rejected(() -> publications.checkTerminal(scope, callId));
        rejected(() -> publications.checkPrepared(scope, callId));
        rejected(() -> publications.publish(scope, callId));
        setState("claim_lease_until = UTC_TIMESTAMP(6), generation = 2, status = 'deleted'");
        ChatMemoryState expired = state();
        publications.recordUsage(snapshot, callId, MODEL, usage);
        publications.recordUsage(snapshot, UUID.randomUUID().toString(), MODEL, usage);
        assertEquals(expired, state());
        assertEquals(ext, histories.selectByPrimaryKey(sourceId).orElseThrow().getExt());
        assertEquals(2, count("usage"));
        assertEquals(0, stores.records.size());
    }

    @Test
    void duplicateUsageCallbacksRaceWithoutDoubleChargingAndCollisionsNeverOverwrite() throws Exception {
        Snapshot snapshot = seed(Operation.IDLE, "source");
        String callId = UUID.randomUUID().toString();
        TokenUsage usage = new TokenUsage(3, 5);
        try (var workers = Executors.newFixedThreadPool(2)) {
            var first = workers.submit(() -> publications.recordUsage(snapshot, callId, MODEL, usage));
            var second = workers.submit(() -> publications.recordUsage(snapshot, callId, MODEL, usage));
            first.get(20, TimeUnit.SECONDS);
            second.get(20, TimeUnit.SECONDS);
        }
        ChatMemoryCommit original = attempt(callId);
        rejected(() -> publications.recordUsage(snapshot, callId, "other-model", usage));
        rejected(() -> publications.recordUsage(snapshot, callId, MODEL, new TokenUsage(1, 1)));
        Snapshot otherAttempt = publications.snapshot(scope, Operation.IDLE, throughId, snapshot.leaseToken());
        rejected(() -> publications.recordUsage(otherAttempt, callId, MODEL, usage));
        MemoryScope foreign =
                new MemoryScope(scope.chatId(), "other-owner", scope.characterUid(), 1, scope.storeType());
        Snapshot otherScope = new Snapshot(
                foreign,
                snapshot.attemptId(),
                snapshot.operation(),
                snapshot.sourceStartId(),
                snapshot.sourceEndId(),
                snapshot.expectedCursor(),
                snapshot.expectedHead(),
                snapshot.leaseToken(),
                snapshot.fingerprint(),
                snapshot.sourceHash());
        rejected(() -> publications.recordUsage(otherScope, callId, MODEL, usage));
        Progress checkpoint = save(snapshot, null, new Position(sourceId, 1));
        ChatMemoryCommit existing = attempt(checkpoint.attemptId());
        rejected(() -> publications.recordUsage(snapshot, checkpoint.attemptId(), MODEL, usage));
        assertEquals(existing, attempt(checkpoint.attemptId()));
        assertEquals(original, attempt(callId));
        assertEquals(1, count("usage"));
    }

    @Test
    void malformedDiscardedAndLateModelResponsesAreAccountedAndSqlErrorsAreSanitized() {
        Snapshot snapshot = seed(Operation.IDLE, "source");
        MemoryExtractor extractor = new MemoryExtractor(properties, bounds);
        var input = new MemoryExtractor.Input(
                scope,
                snapshot.fingerprint(),
                Operation.IDLE,
                "",
                "",
                "",
                List.of(),
                List.of(),
                List.of(),
                Instant.now());
        var budget = new MemoryExtractor.Budget(
                2,
                Duration.ofSeconds(30),
                () -> publications.checkSnapshot(snapshot),
                (id, usage) -> publications.recordUsage(snapshot, id, MODEL, usage));
        assertThrows(
                IllegalArgumentException.class,
                () -> extractor.extract(model(request -> response("malformed")), null, input, budget));
        assertEquals(2, count("usage"));
        var lateBudget = new MemoryExtractor.Budget(
                1,
                Duration.ofSeconds(30),
                () -> publications.checkSnapshot(snapshot),
                (id, usage) -> publications.recordUsage(snapshot, id, MODEL, usage));
        assertThrows(
                IllegalStateException.class,
                () -> extractor.extract(
                        model(request -> {
                            setState("claim_deadline = UTC_TIMESTAMP(6), generation = 2, status = 'deleted'");
                            return response("{\"summary\":\"discarded\",\"userFacts\":[],\"characterDeltas\":[]}");
                        }),
                        null,
                        input,
                        lateBudget));
        assertEquals(3, count("usage"));
        assertEquals(0, stores.records.size());
        sql.execute("""
            CREATE TRIGGER fail_usage BEFORE INSERT ON chat_memory_commit FOR EACH ROW
            BEGIN
              IF NEW.chat_id = '%s' AND NEW.operation = 'USAGE' THEN
                SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'private-source-and-credential';
              END IF;
            END
            """.formatted(scope.chatId()));
        String failedCall = UUID.randomUUID().toString();
        try {
            IllegalStateException error =
                    rejected(() -> publications.recordUsage(snapshot, failedCall, MODEL, new TokenUsage(2, 3)));
            assertEquals("Memory publication transaction failed", error.getMessage());
            assertTrue(commits.selectByPrimaryKey(failedCall).isEmpty());
        } finally {
            sql.execute("DROP TRIGGER fail_usage");
        }
        publications.recordUsage(snapshot, failedCall, MODEL, new TokenUsage(2, 3));
        assertEquals(4, count("usage"));
    }

    private Snapshot seed(Operation operation, String text) {
        var turn = turns.begin(scope, FINGERPRINT);
        sourceId = turns.append(
                turn,
                UserMessage.from("transformed model input"),
                UserMessage.from(text),
                null,
                MemoryTurnRepository.Origin.USER_INPUT,
                new TokenUsage(1, 2));
        turns.complete(turn, AiMessage.from("Final answer"), null, new TokenUsage(2, 3));
        throughId = state().getLatestFinalizedId();
        String token;
        if (operation == Operation.OVERFLOW) {
            token = turns.begin(scope, FINGERPRINT).token();
        } else {
            token = UUID.randomUUID().toString();
            sql.update(
                    "UPDATE chat_memory_state SET claim_token = ?, claim_lease_until = UTC_TIMESTAMP(6) + INTERVAL 5"
                            + " MINUTE, claim_deadline = UTC_TIMESTAMP(6) + INTERVAL 10 MINUTE WHERE chat_id = ?",
                    token,
                    scope.chatId());
        }
        return publications.snapshot(scope, operation, throughId, token);
    }

    private Snapshot freshSnapshot(Snapshot previous) {
        String prefix = previous.operation() == Operation.OVERFLOW ? "turn_" : "claim_";
        String token = UUID.randomUUID().toString();
        sql.update(
                "UPDATE chat_memory_state SET " + prefix + "token = ?, " + prefix
                        + "lease_until = UTC_TIMESTAMP(6) + INTERVAL 5 MINUTE, " + prefix
                        + "deadline = UTC_TIMESTAMP(6) + INTERVAL 10 MINUTE WHERE chat_id = ?",
                token,
                scope.chatId());
        return publications.snapshot(scope, previous.operation(), throughId, token);
    }

    private Progress save(Snapshot snapshot, Progress previous, Position next) {
        return checkpoints.save(
                snapshot,
                previous,
                next,
                new MemoryExtractor.Output("Compact summary", List.of(), List.of()),
                Instant.now(),
                MODEL,
                null,
                GUARD);
    }

    private boolean competingSave(Snapshot snapshot, Progress previous) {
        try {
            save(snapshot, previous, new Position(sourceId, 0));
            return true;
        } catch (IllegalStateException error) {
            assertEquals("Memory checkpoint operation failed", error.getMessage());
            return false;
        }
    }

    private void publishFinal(Snapshot snapshot, MemoryExtractor.Output output, Instant observedAt) {
        List<MemoryDocument> documents = new ArrayList<>();
        if (snapshot.operation() == Operation.OVERFLOW) {
            documents.add(document(
                    snapshot, MemoryDocument.Kind.WINDOW_SUMMARY, false, output.summary(), List.of(), observedAt));
            documents.add(document(
                    snapshot, MemoryDocument.Kind.WINDOW_SUMMARY, true, output.summary(), List.of(), observedAt));
        } else {
            documents.add(document(
                    snapshot, MemoryDocument.Kind.EPISODE_SUMMARY, false, output.summary(), List.of(), observedAt));
            documents.add(document(
                    snapshot, MemoryDocument.Kind.PROFILE_SNAPSHOT, false, "", output.userFacts(), observedAt));
        }
        new MemoryPublisher(publications, vectors).publish(snapshot, documents, MODEL, null);
    }

    private MemoryDocument document(
            Snapshot snapshot,
            MemoryDocument.Kind kind,
            boolean archive,
            String summary,
            List<MemoryDocument.Fact> facts,
            Instant observedAt) {
        return new MemoryDocument(
                UUID.randomUUID().toString(),
                scope,
                snapshot.attemptId(),
                kind,
                archive,
                snapshot.sourceStartId(),
                snapshot.sourceEndId(),
                observedAt,
                snapshot.fingerprint(),
                summary,
                facts,
                List.of());
    }

    private MemoryManifest manifest(Progress progress) {
        return MemoryDocumentCodec.decodeManifest(attempt(progress.attemptId()).getManifest());
    }

    private ChatMemoryState state() {
        return states.selectByPrimaryKey(scope.chatId()).orElseThrow();
    }

    private ChatMemoryCommit attempt(String id) {
        return commits.selectByPrimaryKey(id).orElseThrow();
    }

    private void setState(String assignments) {
        assertEquals(
                1, sql.update("UPDATE chat_memory_state SET " + assignments + " WHERE chat_id = ?", scope.chatId()));
    }

    private int count(String status) {
        return sql.queryForObject(
                "SELECT COUNT(*) FROM chat_memory_commit WHERE chat_id = ? AND status = ?",
                Integer.class,
                scope.chatId(),
                status);
    }

    private void assertGrace(String id) {
        ChatMemoryCommit row = attempt(id);
        assertEquals("terminal", row.getStatus());
        assertEquals(row.getGmtModified().plus(properties.getGcGracePeriod()), row.getGcAfter());
        rejected(() -> publications.checkTerminal(scope, id));
    }

    private static IllegalStateException rejected(Executable action) {
        IllegalStateException error = assertThrows(IllegalStateException.class, action);
        assertTrue(List.of("Memory publication transaction failed", "Memory publication state changed or lease expired")
                .contains(error.getMessage()));
        assertNull(error.getCause());
        assertEquals(0, error.getSuppressed().length);
        return error;
    }

    private static void checkpointRejected(Executable action) {
        IllegalStateException error = assertThrows(IllegalStateException.class, action);
        assertEquals("Memory checkpoint operation failed", error.getMessage());
        assertNull(error.getCause());
        assertEquals(0, error.getSuppressed().length);
    }

    private static ChatResponse response(String text) {
        return ChatResponse.builder()
                .aiMessage(AiMessage.from(text))
                .tokenUsage(new TokenUsage(7, 9))
                .build();
    }

    private static ChatModel model(Function<ChatRequest, ChatResponse> response) {
        return new ChatModel() {
            @Override
            public ChatResponse doChat(ChatRequest request) {
                return response.apply(request);
            }
        };
    }

    private static void await(CountDownLatch latch) {
        try {
            assertTrue(latch.await(10, TimeUnit.SECONDS));
        } catch (InterruptedException error) {
            Thread.currentThread().interrupt();
            throw new AssertionError(error);
        }
    }

    private final class FakeModels implements EmbeddingModelService {
        @Override
        public EmbeddingModel modelForLang(String lang) {
            return new EmbeddingModel() {
                @Override
                public Response<List<Embedding>> embedAll(List<TextSegment> segments) {
                    if (segments.stream().noneMatch(segment -> segment.text().startsWith("query:"))) {
                        assertTrue(count("prepared") > 0, "SQL manifest must be durable BEFORE embedding");
                    }
                    return Response.from(segments.stream()
                            .map(ignored -> Embedding.from(new float[] {1, 0}))
                            .toList());
                }
            };
        }

        @Override
        public TokenCountEstimator tokenCountEstimatorForLang(String lang) {
            return new TokenCountEstimator() {
                public int estimateTokenCountInText(String text) {
                    return Math.max(1, text.length() / 8);
                }

                public int estimateTokenCountInMessage(ChatMessage message) {
                    return 1;
                }

                public int estimateTokenCountInMessages(Iterable<ChatMessage> messages) {
                    return 1;
                }
            };
        }

        @Override
        public String queryPrefixForLang(String lang) {
            return "query:";
        }

        @Override
        public int dimensionForLang(String lang) {
            return 2;
        }
    }

    private final class FakeStores
            implements ExactEmbeddingStoreService, fun.freechat.service.rag.MemoryEmbeddingCleanupService {
        private final Map<String, TextSegment> records = new ConcurrentHashMap<>();
        private final AtomicInteger reads = new AtomicInteger();
        private final AtomicInteger deletes = new AtomicInteger();
        private volatile Runnable afterInsert = GUARD;
        private volatile Runnable afterRead = GUARD;

        @Override
        public Optional<TextSegment> get(EmbeddingStoreType type, String id, Filter filter) {
            reads.incrementAndGet();
            Optional<TextSegment> result =
                    Optional.ofNullable(records.get(id)).filter(segment -> filter.test(segment.metadata()));
            afterRead.run();
            return result;
        }

        @Override
        public EmbeddingStore<TextSegment> of(Object memoryId, EmbeddingStoreType type) {
            return new EmbeddingStore<>() {
                @Override
                public void addAll(List<String> ids, List<Embedding> embeddings, List<TextSegment> segments) {
                    assertEquals(ids.size(), segments.size());
                    for (int index = 0; index < ids.size(); index++) {
                        TextSegment segment = segments.get(index);
                        ChatMemoryCommit row = attempt(segment.metadata().getString("commit_id"));
                        assertEquals("prepared", row.getStatus());
                        assertTrue(MemoryDocumentCodec.decodeManifest(row.getManifest())
                                .ids()
                                .contains(ids.get(index)));
                        assertNull(
                                records.putIfAbsent(ids.get(index), segment),
                                "Immutable vector IDs must never be reused");
                    }
                    afterInsert.run();
                }

                @Override
                public EmbeddingSearchResult<TextSegment> search(EmbeddingSearchRequest request) {
                    return new EmbeddingSearchResult<>(records.entrySet().stream()
                            .filter(entry ->
                                    request.filter().test(entry.getValue().metadata()))
                            .limit(request.maxResults())
                            .map(entry -> new EmbeddingMatch<>(
                                    1.0, entry.getKey(), Embedding.from(new float[] {1, 0}), entry.getValue()))
                            .toList());
                }

                @Override
                public void removeAll(Collection<String> ids) {
                    throw new AssertionError("Memory deletion must include its scope");
                }

                public String add(Embedding embedding) {
                    throw new AssertionError();
                }

                public void add(String id, Embedding embedding) {
                    throw new AssertionError();
                }

                public String add(Embedding embedding, TextSegment segment) {
                    throw new AssertionError();
                }

                public List<String> addAll(List<Embedding> embeddings) {
                    throw new AssertionError();
                }
            };
        }

        @Override
        public void removeExact(EmbeddingStoreType type, List<String> ids, Filter filter) {
            assertEquals(scope.storeType(), type);
            assertFalse(ids.isEmpty());
            assertTrue(ids.size() <= 100);
            deletes.incrementAndGet();
            records.entrySet()
                    .removeIf(entry -> ids.contains(entry.getKey())
                            && filter.test(entry.getValue().metadata()));
        }

        @Override
        public List<String> legacyIds(EmbeddingStoreType type, String chatId, int limit) {
            throw new AssertionError("No legacy records");
        }

        @Override
        public void removeLegacy(EmbeddingStoreType type, String chatId, List<String> ids) {
            throw new AssertionError("No legacy records");
        }

        @Override
        public void flush(Object id, EmbeddingStoreType type, EmbeddingStore<TextSegment> store) {
            throw new AssertionError("No files");
        }

        @Override
        public void delete(Object id, EmbeddingStoreType type) {
            throw new AssertionError("No whole-store deletion");
        }
    }
}
