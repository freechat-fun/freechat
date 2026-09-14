package fun.freechat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.data.message.SystemMessage;
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
import fun.freechat.service.chat.memory.MemoryDocument.Kind;
import fun.freechat.service.chat.memory.MemoryExtractor.Input;
import fun.freechat.service.chat.memory.MemoryPublicationRepository.Operation;
import fun.freechat.service.chat.memory.MemoryPublicationRepository.Snapshot;
import fun.freechat.service.chat.memory.MemoryTurnRepository.Origin;
import fun.freechat.service.chat.memory.MemoryTurnRepository.TurnLease;
import fun.freechat.service.common.impl.LocalFileStoreImpl;
import fun.freechat.service.enums.EmbeddingStoreType;
import fun.freechat.service.rag.EmbeddingModelService;
import fun.freechat.service.rag.ExactEmbeddingStoreService;
import fun.freechat.service.util.InfoUtils;
import fun.freechat.service.util.StoreUtils;
import java.nio.file.Path;
import java.time.Duration;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
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
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/** Exercises the public coordinator against real SQL, without application bootstrap or paid providers. */
@Testcontainers
@Timeout(120)
class MemoryConsolidatorIT {
    @Container
    private static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.0.36")
            .withUsername("root")
            .withDatabaseName("freechat")
            .withInitScript("sql/schema.sql");

    private static final String FINGERPRINT = MemoryDocumentCodec.hash("consolidator-baseline");
    private static final String MODEL = "deterministic-consolidator";
    private static final Runnable GUARD = () -> {};
    private static final TokenUsage USAGE = new TokenUsage(7, 9);
    private static final ObjectMapper JSON = new ObjectMapper().registerModule(new JavaTimeModule());
    private static HikariDataSource dataSource;
    private static JdbcTemplate sql;
    private static ChatMemoryStateMapper states;
    private static ChatHistoryMapper histories;
    private static ChatMemoryCommitMapper commits;
    private static ChatMemoryCoordinationMapper coordination;
    private static DataSourceTransactionManager transactions;
    private static String sqlFailureCategory;

    @TempDir
    Path snapshotDirectory;

    private SystemPromptSnapshotStore snapshots;
    private LongTermMemoryProperties properties;
    private MemoryTurnRepository turns;
    private MemoryPublicationRepository publications;
    private MemoryCheckpoints checkpoints;
    private MemoryPublisher publisher;
    private MemoryVectorRepository vectors;
    private MemoryBoundsFactory boundsFactory;
    private FakeStores stores;
    private MemoryScope scope;
    private TurnLease foreground;

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
        Configuration config = new Configuration();
        config.setLogImpl(NoLoggingImpl.class);
        config.addMapper(ChatMemoryStateMapper.class);
        config.addMapper(ChatHistoryMapper.class);
        config.addMapper(ChatMemoryCommitMapper.class);
        config.addMapper(ChatMemoryCoordinationMapper.class);
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setConfiguration(config);
        var translator = new org.mybatis.spring.MyBatisExceptionTranslator(dataSource, true);
        SqlSessionTemplate sessions =
                new SqlSessionTemplate(factory.getObject(), org.apache.ibatis.session.ExecutorType.SIMPLE, failure -> {
                    Throwable cause = failure;
                    while (cause.getCause() != null && cause.getCause() != cause) {
                        cause = cause.getCause();
                    }
                    sqlFailureCategory = cause instanceof java.sql.SQLException jdbc
                            ? "SQLState=" + jdbc.getSQLState() + ", vendorCode=" + jdbc.getErrorCode()
                            : cause.getClass().getSimpleName();
                    return translator.translateExceptionIfPossible(failure);
                });
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

    @org.junit.jupiter.api.AfterEach
    void sqlFailuresContainOnlySafeDiagnostics() {
        assertNull(sqlFailureCategory, "Unexpected persistence failure (category only)");
    }

    @BeforeEach
    void setup() {
        sqlFailureCategory = null;
        properties = new LongTermMemoryProperties();
        properties.setTurnLease(Duration.ofMinutes(5));
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
        boundsFactory = new MemoryBoundsFactory(properties, models, new DefaultListableBeanFactory());
        vectors = new MemoryVectorRepository(stores, models, boundsFactory, properties);
        checkpoints = new MemoryCheckpoints(publications, vectors);
        publisher = new MemoryPublisher(publications, vectors);
    }

    @Test
    void idlePublishesDatedOriginalEvidenceAndCorrectsBothProfilesWithoutFiles() {
        TurnLease turn = turns.begin(scope, FINGERPRINT);
        long source = append(turn, "I prefer tea.");
        turns.append(turn, UserMessage.from("Excluded example"), null, null, Origin.TEMPLATE_EXAMPLE, null);
        turns.append(turn, SystemMessage.from("Excluded policy"), null, null, Origin.SYSTEM, null);
        long answer = turns.complete(turn, AiMessage.from("A fictional guide promises to help."), null, null);
        long through = state().getLatestFinalizedId();
        Snapshot first = snapshot(Operation.IDLE, through);
        ScriptedModel model = new ScriptedModel(input -> output(
                "First episode", fact("drink", "Tea", source, false), fact("promise", "Will help", answer, true)));
        var resolved = resolved(model);
        try (var files = mockStatic(StoreUtils.class)) {
            open(resolved, 1).consolidate(first);
            files.verifyNoInteractions();
        }
        Input input = model.inputs.getFirst();
        assertEquals(
                List.of(source, answer),
                input.evidence().stream()
                        .map(MemorySourceReader.Evidence::sourceId)
                        .toList());
        assertTrue(input.evidence().getFirst().originalInput());
        assertEquals("user-input", input.evidence().getFirst().origin());
        sameText("I prefer tea.", input.evidence().getFirst().text());
        assertTrue(input.previousUserFacts().isEmpty());
        sameText(resolved.userBaseline(), input.userBaseline());
        sameText(resolved.characterBaseline(), input.characterBaseline());
        MemoryDocument profile = profile();
        assertEquals(List.of(source), profile.userFacts().getFirst().sourceIds());
        assertFalse(profile.userFacts().getFirst().fictional());
        assertTrue(profile.characterDeltas().getFirst().fictional());
        assertEquals(
                histories
                        .selectByPrimaryKey(source)
                        .orElseThrow()
                        .getGmtCreate()
                        .toInstant(ZoneOffset.UTC),
                profile.userFacts().getFirst().observedAt());
        MemoryDocument episode = published(first).stream()
                .filter(doc -> doc.kind() == Kind.EPISODE_SUMMARY)
                .findFirst()
                .orElseThrow();
        assertEquals(
                histories
                        .selectByPrimaryKey(through)
                        .orElseThrow()
                        .getGmtCreate()
                        .toInstant(ZoneOffset.UTC),
                episode.observedAt());
        assertEquals(first.sourceStartId(), episode.sourceStartId());
        assertEquals(through, episode.sourceEndId());
        assertTrue(episode.userFacts().isEmpty() && episode.characterDeltas().isEmpty());
        assertEquals(through, state().getIdleThroughId());
        assertEquals(0, state().getOverflowThroughId());
        assertNull(state().getDueAt());
        assertRetiredCheckpoints();
        assertUsage(first, 1);

        Source correction = seed("Correction: I now prefer coffee.");
        Snapshot next = snapshot(Operation.IDLE, correction.through());
        ScriptedModel correct = new ScriptedModel(
                data -> output("Corrected episode", fact("drink", "Coffee", correction.id(), false), "[]"));
        open(resolved(correct), 1).consolidate(next);
        assertTrue(profile.userFacts().equals(correct.inputs.getFirst().previousUserFacts()));
        assertTrue(profile.characterDeltas().equals(correct.inputs.getFirst().previousCharacterDeltas()));
        assertTrue(
                correct.inputs.getFirst().previousSummary().isEmpty(), "Idle does not reuse a lossy episode summary");
        assertEquals(1, profile().userFacts().size());
        sameText("Coffee", profile().userFacts().getFirst().value());
        assertEquals(List.of(correction.id()), profile().userFacts().getFirst().sourceIds());
        assertTrue(profile().characterDeltas().isEmpty());
        sameText(
                "Tea",
                publisher
                        .readCommitted(scope, profile.id(), GUARD)
                        .orElseThrow()
                        .userFacts()
                        .getFirst()
                        .value());
        assertEquals(0, state().getOverflowThroughId());
        assertEquals(2, count("committed"));
        assertUsage(next, 1);
        assertRetiredCheckpoints();
    }

    @Test
    void overflowKeepsDistinctArchiveChunkAndCumulativeHeadAndNeverUpdatesProfiles() {
        Source first = seed("First original source");
        Source second = seed("Second original source");
        Snapshot idle = snapshot(Operation.IDLE, first.through());
        open(
                        resolved(new ScriptedModel(input ->
                                output("Historical episode", fact("preference", "Retained", first.id(), false), "[]"))),
                        1)
                .consolidate(idle);
        String profile = state().getProfileId();
        ScriptedModel model = new ScriptedModel(input -> summary(
                input.previousSummary().isEmpty()
                        ? input.evidence().getFirst().sourceId() == first.id() ? "Chunk A" : "Chunk B"
                        : "Rolling A plus B"));
        var work = open(resolved(model), 3);
        Snapshot a = snapshot(Operation.OVERFLOW, first.through());
        work.consolidate(a);
        Snapshot b = snapshot(Operation.OVERFLOW, second.through());
        work.consolidate(b);
        assertEquals(3, model.inputs.size());
        sameText(
                "First original source",
                model.inputs.get(0).evidence().getFirst().text());
        sameText(
                "Second original source",
                model.inputs.get(1).evidence().getFirst().text());
        Input merge = model.inputs.get(2);
        sameText("Chunk A", merge.previousSummary());
        assertEquals(1, merge.evidence().size());
        assertEquals("derived-summary", merge.evidence().getFirst().origin());
        assertEquals(second.through(), merge.evidence().getFirst().sourceId());
        sameText("Chunk B", merge.evidence().getFirst().text());
        assertTrue(model.inputs.stream()
                .allMatch(input -> input.userBaseline().isEmpty()
                        && input.characterBaseline().isEmpty()
                        && input.previousUserFacts().isEmpty()
                        && input.previousCharacterDeltas().isEmpty()));
        List<MemoryDocument> documents = published(b);
        sameText(
                "Chunk B",
                documents.stream()
                        .filter(MemoryDocument::archive)
                        .findFirst()
                        .orElseThrow()
                        .summary());
        sameText(
                "Rolling A plus B",
                publisher
                        .readCommitted(scope, state().getSummaryId(), GUARD)
                        .orElseThrow()
                        .summary());
        assertEquals(profile, state().getProfileId());
        assertEquals(first.through(), state().getIdleThroughId());
        assertEquals(second.through(), state().getOverflowThroughId());
        assertEquals(
                3,
                vectors.candidates(scope, "fixture", 10).matches().size(),
                "Only episode and distinct archives are searchable");
        assertUsage(a, 1);
        assertUsage(b, 2);
        assertRetiredCheckpoints();
    }

    @Test
    void oneWorkSharesMapAndRollingBudgetAcrossSuccessiveSnapshotsAndResumesWithoutRemapping() {
        Source first = seed("First turn");
        Source second = seed("Second turn");
        ScriptedModel model = new ScriptedModel(input -> summary("Compact turn"));
        var work = open(resolved(model), 2);
        Snapshot a = snapshot(Operation.OVERFLOW, first.through());
        work.consolidate(a);
        Snapshot b = snapshot(Operation.OVERFLOW, second.through());
        exhausted(() -> work.consolidate(b));
        assertEquals(2, model.inputs.size());
        assertEquals(first.through(), state().getOverflowThroughId());
        var progress = checkpoints.load(b, GUARD).orElseThrow();
        assertEquals(second.through(), progress.position().rowId());
        assertEquals(0, progress.position().offset());
        assertUsage(a, 1);
        assertUsage(b, 1);
        Snapshot resumed = freshSnapshot(b);
        ScriptedModel merge = new ScriptedModel(input -> summary("Resumed cumulative head"));
        open(resolved(merge), 1).consolidate(resumed);
        assertEquals(1, merge.inputs.size());
        assertEquals(
                "derived-summary", merge.inputs.getFirst().evidence().getFirst().origin());
        assertEquals(second.through(), state().getOverflowThroughId());
        assertUsage(resumed, 1);
        assertRetiredCheckpoints();
    }

    @Test
    void baselineRevalidationReadsIncompatiblePairedProfileAndRemovesFactsWithoutEpisodeOrCursorChanges() {
        Source source = seed("Supported profile source");
        Snapshot idle = snapshot(Operation.IDLE, source.through());
        open(
                        resolved(new ScriptedModel(input -> output(
                                "Episode",
                                fact("preference", "Now a default", source.id(), false),
                                fact("story", "Now baseline", source.id(), true)))),
                        1)
                .consolidate(idle);
        String oldProfile = state().getProfileId();
        String fingerprint = MemoryDocumentCodec.hash("changed baseline");
        sql.update(
                "UPDATE chat_memory_state SET fingerprint = ?, profile_revalidation_pending = 1 WHERE chat_id = ?",
                fingerprint,
                scope.chatId());
        assertTrue(publisher
                .readHead(scope, oldProfile, Kind.PROFILE_SNAPSHOT, fingerprint, GUARD)
                .isEmpty());
        Snapshot revalidate = snapshot(Operation.REVALIDATE, source.through());
        ChatMemoryState before = state();
        int historyCount = historyCount();
        ScriptedModel model = new ScriptedModel(input -> output("", "[]", "[]"));
        var changed = new MemoryModelResolver.Resolved(
                model,
                MODEL,
                "default",
                "Now a default",
                "Now baseline",
                fingerprint,
                scope.characterUid(),
                scope.userId(),
                10,
                10,
                null);
        open(changed, 1).consolidate(revalidate);
        Input input = model.inputs.getFirst();
        assertEquals(Operation.REVALIDATE, input.operation());
        assertTrue(input.evidence().isEmpty() && input.previousSummary().isEmpty());
        assertEquals(1, input.previousUserFacts().size());
        assertEquals(1, input.previousCharacterDeltas().size());
        sameText(changed.userBaseline(), input.userBaseline());
        sameText(changed.characterBaseline(), input.characterBaseline());
        assertTrue(
                profile().userFacts().isEmpty() && profile().characterDeltas().isEmpty());
        assertEquals(fingerprint, profile().fingerprint());
        assertEquals(0, profile().sourceStartId());
        assertEquals(before.getIdleThroughId(), state().getIdleThroughId());
        assertEquals(before.getOverflowThroughId(), state().getOverflowThroughId());
        assertEquals(before.getLatestFinalizedId(), state().getLatestFinalizedId());
        assertEquals(historyCount, historyCount());
        assertEquals(1, published(revalidate).size());
        assertEquals((byte) 0, state().getProfileRevalidationPending());
        assertEquals(1, vectors.candidates(scope, "fixture", 10).matches().size());
        assertUsage(revalidate, 1);
    }

    @ParameterizedTest
    @EnumSource(
            value = Operation.class,
            names = {"OVERFLOW", "IDLE"})
    void abortedTurnsAdvanceWithoutModelEmbeddingOrVectorReads(Operation operation) {
        TurnLease turn = turns.begin(scope, FINGERPRINT);
        append(turn, "Aborted content must not be extracted");
        turns.abort(turn);
        long through = state().getLatestFinalizedId();
        Snapshot snapshot = snapshot(operation, through);
        ScriptedModel model = new ScriptedModel(input -> {
            throw new AssertionError("Aborted source reached model");
        });
        open(resolved(model), 1).consolidate(snapshot);
        assertTrue(model.inputs.isEmpty());
        assertTrue(stores.records.isEmpty());
        assertEquals(0, stores.reads);
        assertEquals(0, count("usage"));
        assertEquals("skipped", attempt(snapshot.attemptId()).getStatus());
        assertEquals("{}", attempt(snapshot.attemptId()).getManifest());
        assertEquals(
                through, operation == Operation.OVERFLOW ? state().getOverflowThroughId() : state().getIdleThroughId());
        assertEquals(0, operation == Operation.OVERFLOW ? state().getIdleThroughId() : state().getOverflowThroughId());
        assertNull(state().getProfileId());
        assertNull(state().getSummaryId());
    }

    @ParameterizedTest
    @EnumSource(
            value = Operation.class,
            names = {"OVERFLOW", "IDLE"})
    void oversizedUnicodeResumesAcrossFreshTurnOrClaimTokensWithOriginalCodePointOffsets(Operation operation) {
        properties.setExtractionMaxInputTokens(2200);
        properties.afterPropertiesSet();
        String original = ("汉" + new String(Character.toChars(0x20000))).repeat(12000);
        Source source = seed(original);
        Snapshot snapshot = snapshot(operation, source.through());
        StringBuilder reconstructed = new StringBuilder();
        ScriptedModel model = new ScriptedModel(input -> {
            for (var evidence : input.evidence()) {
                if (evidence.sourceId() == source.id()) {
                    assertEquals(reconstructed.codePointCount(0, reconstructed.length()), evidence.offset());
                    assertTrue(evidence.originalInput());
                    reconstructed.append(evidence.text());
                }
            }
            return summary("Bounded compact progress");
        });
        int retries = 0;
        while (true) {
            int callsBefore = model.inputs.size();
            try {
                open(resolved(model), 1).consolidate(snapshot);
                assertUsage(snapshot, model.inputs.size() - callsBefore);
                break;
            } catch (MemoryBounds.CapacityException exhausted) {
                assertEquals("Memory extraction call budget exhausted", exhausted.getMessage());
                assertEquals(1, model.inputs.size() - callsBefore);
                assertUsage(snapshot, 1);
                assertEquals(0, state().getOverflowThroughId());
                assertEquals(0, state().getIdleThroughId());
                var progress = checkpoints.load(snapshot, GUARD).orElseThrow();
                assertTrue(progress.position().rowId() < source.through());
                assertEquals(1, count("checkpoint"));
                assertTrue(vectors.candidates(scope, "fixture", 10).matches().isEmpty());
                Snapshot old = snapshot;
                snapshot = freshSnapshot(snapshot);
                assertNotEquals(old.leaseToken(), snapshot.leaseToken());
                var restored = new MemoryCheckpoints(publications, vectors)
                        .load(snapshot, GUARD)
                        .orElseThrow();
                assertTrue(
                        progress.equals(restored), "Fresh worker must recover the exact compact output and position");
                assertThrows(IllegalStateException.class, () -> checkpoints.load(old, GUARD));
                assertTrue(++retries < 30, "Bounded progress must converge");
            }
        }
        assertTrue(retries > 1);
        sameText(original, reconstructed.toString());
        assertEquals(
                source.through(),
                operation == Operation.OVERFLOW ? state().getOverflowThroughId() : state().getIdleThroughId());
        assertRetiredCheckpoints();
    }

    @Test
    @Timeout(300)
    void moreThanThousandSourceRowsAreReadAndBatchedAtOneHundredEvidenceItems() {
        TurnLease turn = turns.begin(scope, FINGERPRINT);
        List<Long> ids = new ArrayList<>();
        for (int index = 0; index < 1005; index++) {
            ids.add(append(turn, "Evidence " + index));
        }
        ids.add(turns.complete(turn, AiMessage.from("Final answer"), null, null));
        Snapshot snapshot = snapshot(Operation.IDLE, state().getLatestFinalizedId());
        int before = historyCount();
        ScriptedModel model = new ScriptedModel(input -> summary("Compact page"));
        open(resolved(model), 11).consolidate(snapshot);
        assertEquals(11, model.inputs.size());
        assertEquals(100, model.inputs.getFirst().evidence().size());
        assertEquals(
                ids,
                model.inputs.stream()
                        .flatMap(input -> input.evidence().stream())
                        .map(MemorySourceReader.Evidence::sourceId)
                        .toList());
        assertEquals(before, historyCount(), "Extraction must never rewrite or append transcript rows");
        assertEquals(snapshot.sourceEndId(), state().getIdleThroughId());
        assertUsage(snapshot, 11);
        assertRetiredCheckpoints();
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2})
    void formatRepairConsumesSameBudgetAndNeverRepeatsInvalidResponse(int maxCalls) {
        Source source = seed("Original evidence");
        Snapshot snapshot = snapshot(Operation.IDLE, source.through());
        ScriptedModel model = new ScriptedModel(input -> summary("Repaired summary"));
        model.malformedCalls = 1;
        var work = open(resolved(model), maxCalls);
        if (maxCalls == 1) {
            exhausted(() -> work.consolidate(snapshot));
            assertEquals(0, state().getIdleThroughId());
            assertTrue(stores.records.isEmpty());
        } else {
            work.consolidate(snapshot);
            assertTrue(model.repairs.getLast());
            assertTrue(
                    model.inputs.getFirst().equals(model.inputs.getLast()),
                    "Repair repeats bounded input, not invalid output");
            assertEquals(source.through(), state().getIdleThroughId());
        }
        assertEquals(maxCalls, model.inputs.size());
        assertUsage(snapshot, maxCalls);
    }

    @Test
    void twoMalformedResponsesAreAccountedButCannotPublishOrRetryIndefinitely() {
        Source source = seed("Original evidence");
        Snapshot snapshot = snapshot(Operation.IDLE, source.through());
        ScriptedModel model = new ScriptedModel(input -> summary("Unused"));
        model.malformedCalls = 3;
        IllegalArgumentException failure = assertThrows(
                IllegalArgumentException.class, () -> open(resolved(model), 5).consolidate(snapshot));
        assertEquals("Invalid memory extraction output", failure.getMessage());
        assertNull(failure.getCause());
        assertEquals(2, model.inputs.size());
        assertUsage(snapshot, 2);
        assertTrue(stores.records.isEmpty());
        assertEquals(0, state().getIdleThroughId());
    }

    @ParameterizedTest
    @ValueSource(strings = {"source", "fingerprint", "generation", "claim_lease_until", "claim_deadline"})
    void returnedUsageSurvivesSourceOrLeaseInvalidationDuringPaidCall(String change) {
        Source source = seed("Source before invalidation");
        Snapshot snapshot = snapshot(Operation.IDLE, source.through());
        ScriptedModel model = new ScriptedModel(input -> {
            switch (change) {
                case "source" ->
                    sql.update("UPDATE chat_history SET source_message = message WHERE id = ?", source.id());
                case "fingerprint" -> setState("fingerprint = 'changed'");
                case "generation" -> setState("generation = 2");
                default -> setState(change + " = UTC_TIMESTAMP(6)");
            }
            return summary("Discarded result");
        });
        IllegalStateException error = assertThrows(
                IllegalStateException.class, () -> open(resolved(model), 1).consolidate(snapshot));
        assertEquals("Memory publication state changed or lease expired", error.getMessage());
        assertNull(error.getCause());
        assertEquals(0, error.getSuppressed().length);
        assertUsage(snapshot, 1);
        assertEquals(0, state().getIdleThroughId());
        assertTrue(stores.records.isEmpty());
        assertEquals(0, count("checkpoint"));
    }

    @Test
    void failedPublicationLeavesCompletedCheckpointForFreshWorkerAndTerminalIdsStayUnauthorized() {
        Source source = seed("Original evidence");
        Snapshot snapshot = snapshot(Operation.IDLE, source.through());
        ScriptedModel model = new ScriptedModel(input -> summary("Completed extraction"));
        stores.failPublication = true;
        IllegalStateException error = assertThrows(
                IllegalStateException.class, () -> open(resolved(model), 1).consolidate(snapshot));
        assertEquals("Memory publication failed", error.getMessage());
        assertNull(error.getCause());
        assertEquals(0, state().getIdleThroughId());
        assertEquals("terminal", attempt(snapshot.attemptId()).getStatus());
        var progress = checkpoints.load(snapshot, GUARD).orElseThrow();
        assertEquals(source.through(), progress.position().rowId());
        assertEquals(0, progress.position().offset());
        assertUsage(snapshot, 1);
        for (var entry : MemoryDocumentCodec.decodeManifest(
                        attempt(snapshot.attemptId()).getManifest())
                .entries()) {
            assertTrue(publications
                    .authorize(scope, snapshot.attemptId(), entry.id())
                    .isEmpty());
            assertTrue(publisher.readCommitted(scope, entry.id(), GUARD).isEmpty());
        }
        stores.failPublication = false;
        Snapshot fresh = freshSnapshot(snapshot);
        ScriptedModel unused = new ScriptedModel(input -> {
            throw new AssertionError("Completed source was remapped");
        });
        open(resolved(unused), 1).consolidate(fresh);
        assertTrue(unused.inputs.isEmpty());
        assertEquals(source.through(), state().getIdleThroughId());
        assertEquals(1, count("committed"));
        assertRetiredCheckpoints();
        assertEquals(
                5, stores.records.size(), "Failed immutable IDs are retained for grace-period GC, not overwritten");
    }

    @Test
    void disabledOrMismatchedModelAndImpossibleBaselineFailBeforePaidOrVectorWork() {
        Source source = seed("Original evidence");
        Snapshot snapshot = snapshot(Operation.IDLE, source.through());
        ScriptedModel model = new ScriptedModel(input -> {
            throw new AssertionError("Invalid configuration reached model");
        });
        var resolved = resolved(model);
        var disabled = new MemoryModelResolver.Resolved(
                model, MODEL, "default", "", "", FINGERPRINT, scope.characterUid(), scope.userId(), 10, 0, null);
        assertThrows(IllegalStateException.class, () -> open(disabled, 1));
        var mismatched = new MemoryModelResolver.Resolved(
                model, MODEL, "default", "", "", FINGERPRINT, scope.characterUid(), "foreign-owner", 10, 1, null);
        assertThrows(IllegalStateException.class, () -> open(mismatched, 1).consolidate(snapshot));
        var oversized = new MemoryModelResolver.Resolved(
                model,
                MODEL,
                "default",
                "x".repeat(70000),
                resolved.characterBaseline(),
                FINGERPRINT,
                scope.characterUid(),
                scope.userId(),
                10,
                1,
                null);
        assertThrows(
                MemoryBounds.CapacityException.class, () -> open(oversized, 1).consolidate(snapshot));
        assertTrue(model.inputs.isEmpty());
        assertTrue(stores.records.isEmpty());
        assertEquals(0, count("usage"));
    }

    private MemoryConsolidator.Work open(MemoryModelResolver.Resolved model, int calls) {
        return new MemoryConsolidator(
                        new MemorySourceReader(turns),
                        publications,
                        publisher,
                        new MemoryCheckpoints(publications, vectors),
                        boundsFactory,
                        properties)
                .open(model, calls, Duration.ofMinutes(4), GUARD);
    }

    private MemoryModelResolver.Resolved resolved(ChatModel model) {
        return new MemoryModelResolver.Resolved(
                model,
                MODEL,
                "default",
                "Configured user baseline",
                "Configured character baseline",
                FINGERPRINT,
                scope.characterUid(),
                scope.userId(),
                10,
                10,
                null);
    }

    private Source seed(String text) {
        TurnLease turn = turns.begin(scope, FINGERPRINT);
        long id = append(turn, text);
        turns.complete(turn, AiMessage.from("Final answer"), null, null);
        return new Source(id, state().getLatestFinalizedId());
    }

    private long append(TurnLease turn, String original) {
        return turns.append(
                turn,
                UserMessage.from("Transformed prompt; never source evidence"),
                UserMessage.from(original),
                null,
                Origin.USER_INPUT,
                null);
    }

    private Snapshot snapshot(Operation operation, long through) {
        String token;
        if (operation == Operation.OVERFLOW) {
            if (foreground == null) {
                foreground = turns.begin(scope, state().getFingerprint());
            }
            token = foreground.token();
        } else {
            token = UUID.randomUUID().toString();
            sql.update(
                    "UPDATE chat_memory_state SET claim_token = ?, claim_lease_until = UTC_TIMESTAMP(6) + INTERVAL 5"
                            + " MINUTE, claim_deadline = UTC_TIMESTAMP(6) + INTERVAL 10 MINUTE WHERE chat_id = ?",
                    token,
                    scope.chatId());
        }
        return publications.snapshot(scope, operation, through, token);
    }

    private Snapshot freshSnapshot(Snapshot previous) {
        if (previous.operation() == Operation.OVERFLOW) {
            turns.abort(foreground);
            foreground = null;
        }
        return snapshot(previous.operation(), previous.sourceEndId());
    }

    private MemoryDocument profile() {
        return publisher.readCommitted(scope, state().getProfileId(), GUARD).orElseThrow();
    }

    private List<MemoryDocument> published(Snapshot snapshot) {
        ChatMemoryCommit row = attempt(snapshot.attemptId());
        assertEquals("committed", row.getStatus());
        return MemoryDocumentCodec.decodeManifest(row.getManifest()).ids().stream()
                .map(id -> publisher.readCommitted(scope, id, GUARD).orElseThrow())
                .toList();
    }

    private void assertUsage(Snapshot snapshot, int expected) {
        var rows = sql.queryForList(
                "SELECT attempt_id FROM chat_memory_commit WHERE chat_id = ? AND operation = 'USAGE' "
                        + "AND JSON_UNQUOTE(JSON_EXTRACT(progress, '$.snapshotAttemptId')) = ?",
                String.class,
                scope.chatId(),
                snapshot.attemptId());
        assertEquals(expected, rows.size());
        for (String id : rows) {
            ChatMemoryCommit row = attempt(id);
            assertEquals("usage", row.getStatus());
            assertEquals("{}", row.getManifest());
            assertEquals(USAGE, InfoUtils.deserialize(row.getTokenUsage()));
            assertEquals(snapshot.sourceStartId(), row.getSourceStartId());
            assertEquals(snapshot.sourceEndId(), row.getSourceEndId());
            assertEquals(snapshot.leaseToken(), row.getLeaseToken());
            assertEquals(snapshot.fingerprint(), row.getFingerprint());
            assertEquals(snapshot.scope().generation(), row.getGeneration());
            assertEquals(MODEL, row.getModelId());
        }
    }

    private void assertRetiredCheckpoints() {
        assertEquals(0, count("checkpoint"));
        List<String> retired = sql.queryForList(
                "SELECT attempt_id FROM chat_memory_commit WHERE chat_id = ? AND operation LIKE 'CHECKPOINT_%'",
                String.class, scope.chatId());
        assertFalse(retired.isEmpty(), "Successful extraction must leave durable checkpoint retirement evidence");
        for (String id : retired) {
            ChatMemoryCommit row = attempt(id);
            assertEquals("terminal", row.getStatus());
            assertEquals("SUPERSEDED", row.getErrorCategory());
            assertEquals(row.getGmtModified().plus(properties.getGcGracePeriod()), row.getGcAfter());
            for (String record :
                    MemoryDocumentCodec.decodeManifest(row.getManifest()).ids()) {
                assertTrue(publications.authorize(scope, id, record).isEmpty());
            }
        }
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

    private int historyCount() {
        return sql.queryForObject(
                "SELECT COUNT(*) FROM chat_history WHERE memory_id = ?", Integer.class, scope.chatId());
    }

    // Compare digests so failed assertions never print source, model input, or profile bodies.
    private static void sameText(String expected, String actual) {
        assertEquals(MemoryDocumentCodec.hash(expected), MemoryDocumentCodec.hash(actual));
    }

    private static void exhausted(org.junit.jupiter.api.function.Executable action) {
        var error = assertThrows(MemoryBounds.CapacityException.class, action);
        assertEquals("Memory extraction call budget exhausted", error.getMessage());
    }

    private static String summary(String summary) {
        return output(summary, "[]", "[]");
    }

    private static String output(String summary, String users, String characters) {
        return "{\"summary\":\"" + summary + "\",\"userFacts\":" + users + ",\"characterDeltas\":" + characters + "}";
    }

    private static String fact(String key, String value, long source, boolean fictional) {
        return "[{\"key\":\"" + key + "\",\"value\":\"" + value + "\",\"sourceIds\":[" + source + "],\"fictional\":"
                + fictional + "}]";
    }

    private record Source(long id, long through) {}

    private final class ScriptedModel implements ChatModel {
        private final List<Input> inputs = new ArrayList<>();
        private final List<Boolean> repairs = new ArrayList<>();
        private final Function<Input, String> response;
        private int malformedCalls;

        private ScriptedModel(Function<Input, String> response) {
            this.response = response;
        }

        @Override
        public ChatResponse doChat(ChatRequest request) {
            assertFalse(
                    TransactionSynchronizationManager.isActualTransactionActive(),
                    "No SQL transaction across model IO");
            assertEquals(2, request.messages().size());
            assertTrue(request.messages().getFirst() instanceof SystemMessage);
            assertTrue(request.toolSpecifications() == null
                    || request.toolSpecifications().isEmpty());
            boundsFactory.forLanguage("default").requireExtraction(request.messages(), null);
            String data = ((UserMessage) request.messages().getLast()).singleText();
            Input input;
            try {
                input = JSON.readValue(data, Input.class);
            } catch (Exception ignored) {
                throw new AssertionError("Extractor request is not valid structured data");
            }
            new MemoryExtractor(properties, boundsFactory.forLanguage("default")).requireInput(input, null);
            assertTrue(input.evidence().size() <= 100);
            for (var evidence : input.evidence()) {
                assertTrue(MemoryBounds.bytes(evidence.text()) <= MemoryBounds.SOURCE_FRAGMENT_BYTES);
                assertFalse(evidence.text().contains("Transformed prompt"));
            }
            String instructions = ((SystemMessage) request.messages().getFirst()).text();
            assertFalse(instructions.contains("invalid-private-response"));
            inputs.add(input);
            repairs.add(instructions.contains("last output failed"));
            String text = inputs.size() <= malformedCalls ? "invalid-private-response" : response.apply(input);
            return ChatResponse.builder()
                    .aiMessage(AiMessage.from(text))
                    .tokenUsage(USAGE)
                    .build();
        }
    }

    private final class FakeModels implements EmbeddingModelService {
        @Override
        public EmbeddingModel modelForLang(String lang) {
            return new EmbeddingModel() {
                @Override
                public Response<List<Embedding>> embedAll(List<TextSegment> segments) {
                    assertFalse(TransactionSynchronizationManager.isActualTransactionActive());
                    if (segments.stream().noneMatch(segment -> segment.text().startsWith("query:"))) {
                        assertTrue(count("prepared") > 0, "Prepare SQL manifest before embedding");
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
                    return Math.max(1, (text.length() + 7) / 8);
                }

                public int estimateTokenCountInMessage(ChatMessage message) {
                    return estimateTokenCountInText(ChatMessageSerializer.messageToJson(message));
                }

                public int estimateTokenCountInMessages(Iterable<ChatMessage> messages) {
                    int total = 0;
                    for (ChatMessage message : messages) {
                        total += estimateTokenCountInMessage(message);
                    }
                    return total;
                }
            };
        }

        public String queryPrefixForLang(String lang) {
            return "query:";
        }

        public int dimensionForLang(String lang) {
            return 2;
        }
    }

    private final class FakeStores implements ExactEmbeddingStoreService {
        private final Map<String, TextSegment> records = new HashMap<>();
        private int reads;
        private boolean failPublication;

        @Override
        public Optional<TextSegment> get(EmbeddingStoreType type, String id, Filter filter) {
            assertFalse(TransactionSynchronizationManager.isActualTransactionActive());
            reads++;
            return Optional.ofNullable(records.get(id)).filter(segment -> filter.test(segment.metadata()));
        }

        @Override
        public EmbeddingStore<TextSegment> of(Object memoryId, EmbeddingStoreType type) {
            return new EmbeddingStore<>() {
                @Override
                public void addAll(List<String> ids, List<Embedding> embeddings, List<TextSegment> segments) {
                    assertFalse(TransactionSynchronizationManager.isActualTransactionActive());
                    assertEquals(ids.size(), embeddings.size());
                    assertEquals(ids.size(), segments.size());
                    for (int index = 0; index < ids.size(); index++) {
                        TextSegment segment = segments.get(index);
                        ChatMemoryCommit row = attempt(segment.metadata().getString("commit_id"));
                        assertEquals("prepared", row.getStatus());
                        assertTrue(MemoryDocumentCodec.decodeManifest(row.getManifest())
                                .ids()
                                .contains(ids.get(index)));
                        assertFalse(records.containsKey(ids.get(index)), "Immutable IDs must never be reused");
                        records.put(ids.get(index), segment);
                    }
                    if (failPublication
                            && !Kind.EXTRACTION_CHECKPOINT
                                    .name()
                                    .equals(segments.getFirst().metadata().getString("record_kind"))) {
                        throw new IllegalStateException("Injected ambiguous vector write");
                    }
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

                public String add(Embedding embedding) {
                    throw new AssertionError("Use prepared immutable IDs");
                }

                public void add(String id, Embedding embedding) {
                    throw new AssertionError("Use prepared immutable IDs");
                }

                public String add(Embedding embedding, TextSegment segment) {
                    throw new AssertionError("Use prepared immutable IDs");
                }

                public List<String> addAll(List<Embedding> embeddings) {
                    throw new AssertionError("Use prepared immutable IDs");
                }

                public void removeAll(Collection<String> ids) {
                    throw new AssertionError("Coordinator must not perform vector GC");
                }
            };
        }

        public void flush(Object id, EmbeddingStoreType type, EmbeddingStore<TextSegment> store) {
            throw new AssertionError("No file serialization");
        }

        public void delete(Object id, EmbeddingStoreType type) {
            throw new AssertionError("No whole-store deletion");
        }
    }
}
