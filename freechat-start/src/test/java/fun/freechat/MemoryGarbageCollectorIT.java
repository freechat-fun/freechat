package fun.freechat;

import static org.junit.jupiter.api.Assertions.*;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.store.embedding.filter.Filter;
import fun.freechat.mapper.ChatContextMapper;
import fun.freechat.mapper.ChatHistoryMapper;
import fun.freechat.mapper.ChatMemoryCommitMapper;
import fun.freechat.mapper.ChatMemoryCoordinationMapper;
import fun.freechat.mapper.ChatMemoryStateMapper;
import fun.freechat.model.ChatMemoryCommit;
import fun.freechat.model.ChatMemoryState;
import fun.freechat.service.chat.SystemPromptSnapshotStore;
import fun.freechat.service.chat.memory.LongTermMemoryProperties;
import fun.freechat.service.chat.memory.MemoryDocument.Kind;
import fun.freechat.service.chat.memory.MemoryDocumentCodec;
import fun.freechat.service.chat.memory.MemoryGarbageCollector;
import fun.freechat.service.chat.memory.MemoryLifecycleRepository;
import fun.freechat.service.chat.memory.MemoryManifest;
import fun.freechat.service.chat.memory.MemoryManifest.Entry;
import fun.freechat.service.chat.memory.MemoryPublicationRepository;
import fun.freechat.service.chat.memory.MemoryPublicationRepository.Failure;
import fun.freechat.service.chat.memory.MemoryPublicationRepository.Operation;
import fun.freechat.service.chat.memory.MemoryPublicationRepository.Snapshot;
import fun.freechat.service.chat.memory.MemoryScope;
import fun.freechat.service.chat.memory.MemoryTurnRepository;
import fun.freechat.service.chat.memory.MemoryTurnRepository.TurnLease;
import fun.freechat.service.common.impl.LocalFileStoreImpl;
import fun.freechat.service.enums.EmbeddingStoreType;
import fun.freechat.service.rag.MemoryEmbeddingCleanupService;
import fun.freechat.service.util.InfoUtils;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/** Isolated MySQL, generated mappers and real transactions; vectors are deterministic metadata only. */
@Testcontainers
@Timeout(120)
class MemoryGarbageCollectorIT {
    @Container
    private static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.0.36")
            .withUsername("root")
            .withDatabaseName("freechat")
            .withInitScript("sql/schema.sql");

    private static final String FINGERPRINT = MemoryDocumentCodec.hash("gc-test-baseline");
    private static final List<EmbeddingStoreType> STORES = List.of(
            EmbeddingStoreType.EN_LONG_TERM_MEMORY,
            EmbeddingStoreType.ZH_LONG_TERM_MEMORY,
            EmbeddingStoreType.DEFAULT_LONG_TERM_MEMORY);
    private static HikariDataSource dataSource;
    private static JdbcTemplate sql;
    private static ChatMemoryStateMapper states;
    private static ChatMemoryCommitMapper commits;
    private static ChatMemoryCoordinationMapper coordination;
    private static ChatHistoryMapper histories;
    private static ChatContextMapper contexts;
    private static DataSourceTransactionManager transactions;

    @TempDir
    Path snapshotDirectory;

    private SystemPromptSnapshotStore snapshots;
    private LongTermMemoryProperties properties;
    private MemoryTurnRepository turns;
    private MemoryPublicationRepository publication;
    private MemoryLifecycleRepository lifecycle;
    private MemoryGarbageCollector gc;
    private FakeCleanup vectors;
    private MemoryScope scope;
    private int sequence;

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
        configuration.addMapper(ChatMemoryCommitMapper.class);
        configuration.addMapper(ChatMemoryCoordinationMapper.class);
        configuration.addMapper(ChatHistoryMapper.class);
        configuration.addMapper(ChatContextMapper.class);
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setConfiguration(configuration);
        SqlSessionTemplate sessions = new SqlSessionTemplate(factory.getObject());
        states = sessions.getMapper(ChatMemoryStateMapper.class);
        commits = sessions.getMapper(ChatMemoryCommitMapper.class);
        coordination = sessions.getMapper(ChatMemoryCoordinationMapper.class);
        histories = sessions.getMapper(ChatHistoryMapper.class);
        contexts = sessions.getMapper(ChatContextMapper.class);
        transactions = new DataSourceTransactionManager(dataSource);
    }

    @AfterAll
    static void closePool() {
        if (dataSource != null) {
            dataSource.close();
        }
    }

    @BeforeEach
    void freshSqlAndVectors() {
        // This class owns its container; clearing it makes global keyset assertions independent of test order.
        sql.update("DELETE FROM chat_memory_commit");
        sql.update("DELETE FROM chat_memory_state");
        sql.update("DELETE FROM chat_history");
        sql.update("DELETE FROM chat_context");
        properties = new LongTermMemoryProperties();
        properties.afterPropertiesSet();
        var files = new LocalFileStoreImpl();
        org.springframework.test.util.ReflectionTestUtils.setField(files, "basePathStr", snapshotDirectory.toString());
        snapshots = new SystemPromptSnapshotStore(files);
        turns = new MemoryTurnRepository(coordination, states, histories, transactions, properties, snapshots);
        publication = publication(coordination);
        lifecycle = new MemoryLifecycleRepository(coordination, states, histories, contexts, transactions, properties);
        vectors = new FakeCleanup();
        gc = collector(coordination);
        scope = new MemoryScope(
                "gc-chat", "old-owner", "old-character", 1, EmbeddingStoreType.DEFAULT_LONG_TERM_MEMORY);
        insertContext(scope.chatId(), scope.userId());
        turns.initialize(scope, FINGERPRINT);
        sequence = 0;
    }

    @ParameterizedTest
    @ValueSource(strings = {"owner", "character", "store", "all", "clear", "delete"})
    void lifecycleRetainsFullHistoricalScopeBeforeReplacingIdentityOrClearing(String mutation) throws Exception {
        Prepared old = prepare(Operation.IDLE);
        publication.publish(scope, old.id());
        assertScopeLedger(scope);
        // Model an already committed pre-ledger row: lifecycle must retain the old identity itself.
        assertEquals(
                1,
                sql.update("DELETE FROM chat_memory_commit WHERE chat_id = ? AND operation = 'SCOPE'", scope.chatId()));
        MemoryScope previous = scope;
        switch (mutation) {
            case "clear" -> lifecycle.clear(scope.chatId());
            case "delete" -> assertTrue(lifecycle.delete(scope.chatId()));
            default -> {
                String owner = mutation.equals("owner") || mutation.equals("all") ? "new-owner" : scope.userId();
                String character =
                        mutation.equals("character") || mutation.equals("all") ? "new-character" : scope.characterUid();
                EmbeddingStoreType store = mutation.equals("store") || mutation.equals("all")
                        ? EmbeddingStoreType.ZH_LONG_TERM_MEMORY
                        : scope.storeType();
                sql.update("UPDATE chat_context SET user_id = ? WHERE chat_id = ?", owner, scope.chatId());
                lifecycle.activate(new MemoryScope(scope.chatId(), owner, character, 1, store), FINGERPRINT);
            }
        }
        assertEquals(2L, state().getGeneration());
        assertScopeLedger(previous);
        assertNull(state().getProfileId());
        MemoryScope current = new MemoryScope(
                scope.chatId(),
                state().getUserId(),
                state().getCharacterUid(),
                state().getGeneration(),
                EmbeddingStoreType.of(state().getStoreType()));
        String currentId = nextId();
        vectors.put(current.storeType(), currentId, current.metadata().put("commit_id", old.id()));
        ChatMemoryState before = state();
        gc.collect(old.id());
        assertRetired(old.id());
        assertEquals(old.manifest().ids().size() + 1, vectors.size());
        assertTrue(vectors.exact.isEmpty());
        makeDue(old.id());
        gc.collect(old.id());
        assertEquals(before, state(), "Cleanup may not rewrite current lifecycle state");
        assertEquals(1, vectors.size());
        assertTrue(vectors.contains(current.storeType(), currentId));
        assertExact(vectors.exact.getFirst(), previous, old.id(), old.manifest().ids());
        assertEquals("terminal", row(old.id()).getStatus());
        assertScopeLedger(previous);
        if (mutation.equals("delete")) {
            assertTrue(contexts.selectByPrimaryKey(scope.chatId()).isEmpty());
        }
    }

    @ParameterizedTest
    @EnumSource(Operation.class)
    void preparedExpirationStartsGraceAndRepeatedTombstoneSweepCatchesLateInsert(Operation operation) throws Exception {
        Prepared prepared = prepare(operation);
        assertScopeLedger(scope); // prepare must retain scope BEFORE callers insert vectors.
        ChatMemoryCommit live = row(prepared.id());
        ChatMemoryState before = state();
        gc.collect(prepared.id());
        assertEquals(live, row(prepared.id()));
        assertTrue(vectors.exact.isEmpty());
        sql.update(
                "UPDATE chat_memory_commit SET lease_until = UTC_TIMESTAMP(6) - INTERVAL 1 SECOND WHERE attempt_id = ?",
                prepared.id());
        LocalDateTime discoveredAfter = coordination.databaseNow();
        gc.collect(prepared.id());
        ChatMemoryCommit retired = assertRetired(prepared.id());
        assertFalse(retired.getGmtModified().isBefore(discoveredAfter));
        assertEquals(before, state());
        assertFalse(gc.candidates(null, 100).contains(prepared.id()));
        gc.collect(prepared.id());
        assertEquals(retired, row(prepared.id()), "Repeated discovery must not postpone initial grace");
        assertTrue(vectors.exact.isEmpty());
        assertThrows(IllegalStateException.class, () -> publication.publish(scope, prepared.id()));
        makeDue(prepared.id());
        assertTrue(gc.candidates(null, 100).contains(prepared.id()));
        gc.collect(prepared.id());
        assertEquals(0, vectors.size());
        assertSwept(prepared.id());
        assertExact(
                vectors.exact.getFirst(),
                scope,
                prepared.id(),
                prepared.manifest().ids());
        ChatMemoryCommit firstSweep = row(prepared.id());
        vectors.put(
                scope.storeType(),
                prepared.manifest().ids().getFirst(),
                scope.metadata().put("commit_id", prepared.id()));
        gc.collect(prepared.id());
        assertEquals(firstSweep, row(prepared.id()));
        assertEquals(1, vectors.size(), "Late insertion waits until the retained tombstone is due again");
        makeDue(prepared.id());
        gc.collect(prepared.id());
        assertEquals(0, vectors.size());
        assertEquals(2, vectors.exact.size());
        assertEquals(firstSweep.getManifest(), row(prepared.id()).getManifest());
        assertSwept(prepared.id());
    }

    @ParameterizedTest
    @ValueSource(strings = {"lease", "deadline", "token"})
    void expiredOrReplacedWorkerClaimRetiresPreparedWorkEvenWithFutureAttemptDeadline(String change) {
        Prepared prepared = prepare(Operation.IDLE);
        switch (change) {
            case "lease" -> setState("claim_lease_until = UTC_TIMESTAMP(6) - INTERVAL 1 SECOND");
            case "deadline" -> setState("claim_deadline = UTC_TIMESTAMP(6) - INTERVAL 1 SECOND");
            case "token" ->
                sql.update("UPDATE chat_memory_state SET claim_token = ? WHERE chat_id = ?", nextId(), scope.chatId());
            default -> fail("Unknown fixture");
        }
        gc.collect(prepared.id());
        assertRetired(prepared.id());
        assertTrue(vectors.exact.isEmpty());
        assertEquals(prepared.manifest().ids().size(), vectors.size());
    }

    @ParameterizedTest
    @EnumSource(
            value = Operation.class,
            names = {"OVERFLOW", "IDLE"})
    void committedArchiveSurvivesWhileSupersededFixedHeadGetsDiscoveryTimeGcIds(Operation operation) throws Exception {
        Prepared first = prepare(operation);
        publication.publish(scope, first.id());
        sql.update(
                "UPDATE chat_memory_commit SET gmt_create = UTC_TIMESTAMP(6) - INTERVAL 7 DAY, "
                        + "gmt_modified = UTC_TIMESTAMP(6) - INTERVAL 7 DAY WHERE attempt_id = ?",
                first.id());
        ChatMemoryCommit original = row(first.id());
        gc.collect(first.id());
        assertTrue(operationRows("GC_IDS").isEmpty(), "The currently referenced fixed head cannot be retired");
        if (first.turn() != null) {
            turns.abort(first.turn());
        }
        Prepared second = prepare(operation);
        publication.publish(scope, second.id());
        ChatMemoryState published = state();
        LocalDateTime discovery = coordination.databaseNow();
        gc.collect(first.id());
        ChatMemoryCommit tombstone = onlyOperation("GC_IDS");
        assertEquals("terminal", tombstone.getStatus());
        assertNotEquals(first.id(), tombstone.getAttemptId());
        assertEquals(
                first.id(),
                InfoUtils.defaultMapper()
                        .readTree(tombstone.getProgress())
                        .path("commitId")
                        .textValue());
        List<Entry> fixed = first.manifest().entries().stream()
                .filter(MemoryGarbageCollectorIT::fixedHead)
                .toList();
        assertEquals(new MemoryManifest(fixed), MemoryDocumentCodec.decodeManifest(tombstone.getManifest()));
        assertFalse(tombstone.getGmtCreate().isBefore(discovery));
        assertEquals(tombstone.getGmtCreate().plus(properties.getGcGracePeriod()), tombstone.getGcAfter());
        assertTrue(tombstone.getGcAfter().isAfter(original.getGmtCreate().plus(properties.getGcGracePeriod())));
        assertEquals(original, row(first.id()), "The authorizing archive commit must remain immutable and committed");
        assertFalse(gc.candidates(null, 100).contains(tombstone.getAttemptId()));
        gc.collect(first.id());
        gc.collect(tombstone.getAttemptId());
        assertEquals(tombstone, row(tombstone.getAttemptId()));
        assertEquals(1, operationRows("GC_IDS").size());
        assertTrue(vectors.exact.isEmpty());
        makeDue(tombstone.getAttemptId());
        gc.collect(tombstone.getAttemptId());
        assertEquals(published, state());
        assertEquals(original, row(first.id()));
        assertEquals(3, vectors.size());
        for (Entry entry : first.manifest().entries()) {
            assertEquals(!fixedHead(entry), vectors.contains(scope.storeType(), entry.id()));
            if (!fixedHead(entry)) {
                assertTrue(publication.authorize(scope, first.id(), entry.id()).isPresent());
            }
        }
        second.manifest().ids().forEach(id -> assertTrue(vectors.contains(scope.storeType(), id)));
        assertExact(
                vectors.exact.getFirst(),
                scope,
                first.id(),
                fixed.stream().map(Entry::id).toList());
        assertSwept(tombstone.getAttemptId());
    }

    @Test
    void changedFingerprintPreservesStillReferencedProfileUntilRevalidationPublishesReplacement() {
        Prepared first = prepare(Operation.IDLE);
        publication.publish(scope, first.id());
        String profile = state().getProfileId();
        lifecycle.activate(scope, MemoryDocumentCodec.hash("changed-baseline"));
        assertEquals(1L, state().getGeneration());
        assertEquals((byte) 1, state().getProfileRevalidationPending());
        assertEquals(profile, state().getProfileId());
        ChatMemoryCommit original = row(first.id());
        ChatMemoryState pending = state();
        gc.collect(first.id());
        assertEquals(original, row(first.id()));
        assertEquals(pending, state());
        assertTrue(operationRows("GC_IDS").isEmpty());
        assertTrue(vectors.contains(scope.storeType(), profile));
        Prepared replacement = prepare(Operation.REVALIDATE);
        gc.collect(first.id());
        assertTrue(operationRows("GC_IDS").isEmpty(), "Preparation alone cannot retire the revalidation input");
        publication.publish(scope, replacement.id());
        gc.collect(first.id());
        ChatMemoryCommit tombstone = onlyOperation("GC_IDS");
        assertEquals(
                List.of(profile),
                MemoryDocumentCodec.decodeManifest(tombstone.getManifest()).ids());
        assertTrue(vectors.exact.isEmpty());
        makeDue(tombstone.getAttemptId());
        gc.collect(tombstone.getAttemptId());
        assertFalse(vectors.contains(scope.storeType(), profile));
        assertEquals(2, vectors.size());
        assertEquals(original, row(first.id()));
        assertEquals(replacement.manifest().ids().getFirst(), state().getProfileId());
    }

    @ParameterizedTest
    @ValueSource(strings = {"generation", "fingerprint", "idle-cursor", "overflow-cursor", "head"})
    void checkpointSurvivesOrdinaryClaimExpiryButRetiresWhenItsBaselineChanges(String change) throws Exception {
        Operation operation = change.equals("overflow-cursor") ? Operation.OVERFLOW : Operation.IDLE;
        Snapshot snapshot = snapshot(operation).snapshot();
        lifecycle.activate(scope, FINGERPRINT);
        MemoryManifest manifest = new MemoryManifest(List.of(entry(Kind.EXTRACTION_CHECKPOINT, false)));
        String id = nextId();
        // Seed the durable completed-checkpoint shape, without re-testing extraction/model orchestration.
        ChatMemoryCommit checkpoint = fixture(id, "CHECKPOINT_" + operation, "checkpoint", manifest)
                .withSourceStartId(snapshot.sourceStartId())
                .withSourceEndId(snapshot.sourceEndId())
                .withExpectedCursor(snapshot.expectedCursor())
                .withExpectedHead(snapshot.expectedHead())
                .withLeaseToken(snapshot.leaseToken())
                .withProgress(InfoUtils.defaultMapper()
                        .createObjectNode()
                        .put("sourceHash", snapshot.sourceHash())
                        .putNull("predecessor")
                        .set(
                                "position",
                                InfoUtils.defaultMapper()
                                        .createObjectNode()
                                        .put("rowId", snapshot.sourceEndId())
                                        .put("offset", 0))
                        .toString());
        assertEquals(1, commits.insertSelective(checkpoint));
        vectors.put(
                scope.storeType(), manifest.ids().getFirst(), scope.metadata().put("commit_id", id));
        String prefix = operation == Operation.OVERFLOW ? "turn_" : "claim_";
        setState(prefix + "lease_until = UTC_TIMESTAMP(6) - INTERVAL 1 HOUR, " + prefix
                + "deadline = UTC_TIMESTAMP(6) - INTERVAL 1 HOUR");
        sql.update(
                "UPDATE chat_memory_commit SET lease_until = UTC_TIMESTAMP(6) - INTERVAL 1 HOUR WHERE attempt_id = ?",
                id);
        ChatMemoryCommit saved = row(id);
        gc.collect(id);
        assertEquals(saved, row(id), "Completed checkpoint eligibility is not tied to its original claim lifetime");
        sql.update("UPDATE chat_memory_state SET " + prefix + "token = ? WHERE chat_id = ?", nextId(), scope.chatId());
        gc.collect(id);
        assertEquals(saved, row(id));
        switch (change) {
            case "generation" -> lifecycle.clear(scope.chatId());
            case "fingerprint" -> lifecycle.activate(scope, MemoryDocumentCodec.hash("checkpoint-new-config"));
            case "idle-cursor" -> setState("idle_through_id = " + snapshot.sourceEndId());
            case "overflow-cursor" -> setState("overflow_through_id = " + snapshot.sourceEndId());
            case "head" ->
                sql.update("UPDATE chat_memory_state SET profile_id = ? WHERE chat_id = ?", nextId(), scope.chatId());
            default -> fail("Unknown fixture");
        }
        ChatMemoryState changed = state();
        gc.collect(id);
        assertRetired(id);
        assertTrue(vectors.exact.isEmpty());
        makeDue(id);
        gc.collect(id);
        assertEquals(changed, state());
        assertEquals(0, vectors.size());
        assertExact(vectors.exact.getFirst(), scope, id, manifest.ids());
    }

    @Test
    void accountingReconciliationSkipAndScopeRowsNeverEnterVectorCleanupEvenWithMisleadingStatus() {
        lifecycle.activate(scope, FINGERPRINT);
        List<String> operations = List.of("USAGE", "CHAT_USAGE", "RECONCILE", "SKIP_IDLE", "SKIP_OVERFLOW", "SCOPE");
        List<String> statuses = List.of(
                "usage",
                "reconciling",
                "reconciled",
                "skipped",
                "control",
                "prepared",
                "committed",
                "checkpoint",
                "terminal");
        for (String operation : operations) {
            for (String status : statuses) {
                String id = nextId();
                ChatMemoryCommit row = fixture(id, operation, status, null)
                        .withGcAfter(coordination.databaseNow().minusDays(1));
                assertEquals(1, commits.insertSelective(row));
                ChatMemoryCommit before = row(id);
                assertDeferred(() -> gc.collect(id));
                assertEquals(before, row(id));
            }
        }
        assertTrue(gc.candidates(null, 100).isEmpty());
        assertTrue(vectors.exact.isEmpty());
        assertTrue(vectors.legacyReads.isEmpty());
        assertEquals(55, sql.queryForObject("SELECT COUNT(*) FROM chat_memory_commit", Integer.class));
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void neverActivatedClearOrDeleteRetainsBoundedLegacyGcAcrossAllThreeStores(boolean delete) {
        String chatId = "never-activated";
        insertContext(chatId, "legacy-owner");
        for (EmbeddingStoreType store : STORES) {
            for (int index = 0; index < 205; index++) {
                vectors.put(store, nextId(), new Metadata().put("memory_id", chatId));
            }
            vectors.put(store, "structured", new MemoryScope(chatId, "legacy-owner", "character", 1, store).metadata());
            vectors.put(store, "foreign", new Metadata().put("memory_id", "foreign-chat"));
        }
        if (delete) {
            assertTrue(lifecycle.delete(chatId));
            assertTrue(contexts.selectByPrimaryKey(chatId).isEmpty());
            assertEquals(
                    "deleted", states.selectByPrimaryKey(chatId).orElseThrow().getStatus());
        } else {
            lifecycle.clear(chatId);
            assertTrue(states.selectByPrimaryKey(chatId).isEmpty());
            assertTrue(contexts.selectByPrimaryKey(chatId).isPresent());
        }
        ChatMemoryCommit tombstone = onlyOperation("LEGACY_GC");
        assertEquals(chatId, tombstone.getChatId());
        assertEquals(0L, tombstone.getGeneration());
        assertEquals("terminal", tombstone.getStatus());
        assertEquals(tombstone.getGmtCreate().plus(properties.getGcGracePeriod()), tombstone.getGcAfter());
        assertEquals("{}", tombstone.getManifest());
        lifecycle.clear(chatId);
        if (delete) {
            assertFalse(lifecycle.delete(chatId));
        }
        assertEquals(
                tombstone,
                row(tombstone.getAttemptId()),
                "Repeated lifecycle calls cannot extend initial legacy grace");
        gc.collect(tombstone.getAttemptId());
        assertTrue(vectors.legacyReads.isEmpty());
        for (int remaining : List.of(105, 5, 0)) {
            makeDue(tombstone.getAttemptId());
            gc.collect(tombstone.getAttemptId());
            for (EmbeddingStoreType store : STORES) {
                assertEquals(remaining, vectors.legacyCount(store, chatId));
                assertTrue(vectors.contains(store, "structured"));
                assertTrue(vectors.contains(store, "foreign"));
            }
            assertSwept(tombstone.getAttemptId());
        }
        assertEquals(9, vectors.legacyDeletes.size());
        assertEquals(
                List.of(100, 100, 100, 100, 100, 100, 5, 5, 5),
                vectors.legacyDeletes.stream().map(call -> call.ids().size()).toList());
        for (EmbeddingStoreType store : STORES) {
            vectors.put(store, nextId(), new Metadata().put("memory_id", chatId));
        }
        makeDue(tombstone.getAttemptId());
        gc.collect(tombstone.getAttemptId());
        assertEquals(6, vectors.size());
        assertEquals(12, vectors.legacyReads.size());
        assertEquals(STORES, vectors.legacyReads.subList(9, 12));
        assertTrue(vectors.exact.isEmpty());
        assertEquals(1, operationRows("LEGACY_GC").size());
        assertSwept(tombstone.getAttemptId());
    }

    @Test
    void candidateKeysetIsBoundedOrderedAndWrapRevisitsEarlierIdsWithoutOffsetStarvation() {
        properties.setDispatchBatchSize(2);
        List<String> expected = new ArrayList<>();
        List<String> operations = List.of(
                "OVERFLOW", "IDLE", "REVALIDATE", "CHECKPOINT_IDLE", "CHECKPOINT_OVERFLOW", "GC_IDS", "LEGACY_GC");
        for (int index = 0; index < operations.size(); index++) {
            String id = orderedId(index + 10);
            String status = index < 2 ? "prepared" : index == 2 ? "committed" : index < 5 ? "checkpoint" : "terminal";
            assertEquals(
                    1,
                    commits.insertSelective(fixture(id, operations.get(index), status, null)
                            .withGcAfter(coordination.databaseNow().minusDays(1))));
            expected.add(id);
        }
        assertEquals(
                1,
                commits.insertSelective(fixture(orderedId(30), "IDLE", "terminal", null)
                        .withGcAfter(coordination.databaseNow().plusDays(1))));
        assertEquals(1, commits.insertSelective(fixture(orderedId(31), "IDLE", "terminal", null)));
        assertEquals(
                1,
                commits.insertSelective(fixture(orderedId(32), "USAGE", "terminal", null)
                        .withGcAfter(coordination.databaseNow().minusDays(1))));
        assertEquals(expected.subList(0, 2), gc.candidates(null, Integer.MAX_VALUE));
        assertEquals(expected.subList(0, 1), gc.candidates("", 1));
        List<String> visited = new ArrayList<>(gc.candidates(null, 100));
        // An earlier ID arriving after the cursor must be found when the caller wraps, not by offset paging.
        String earlier = orderedId(1);
        assertEquals(1, commits.insertSelective(fixture(earlier, "IDLE", "prepared", null)));
        String cursor = visited.getLast();
        for (int pageNumber = 0; pageNumber < 10; pageNumber++) {
            List<String> page = gc.candidates(cursor, 100);
            assertTrue(page.size() <= 2);
            if (page.isEmpty()) {
                break;
            }
            String previous = cursor;
            assertTrue(page.stream().allMatch(id -> id.compareTo(previous) > 0));
            visited.addAll(page);
            cursor = page.getLast();
        }
        assertEquals(expected, visited);
        assertTrue(gc.candidates(cursor, 100).isEmpty());
        assertEquals(List.of(earlier, expected.getFirst()), gc.candidates("", 100));
        assertDeferred(() -> gc.candidates("not-a-uuid", 1));
        assertDeferred(() -> gc.candidates(null, 0));
        assertDeferred(() -> gc.candidates(null, -1));
        assertTrue(vectors.exact.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "memory_id",
                "user_id",
                "character_uid",
                "generation",
                "store_type",
                "schema_version",
                "commit_id"
            })
    void exactDeletionRequiresEveryHistoricalScopeFieldAndOriginalCommitAsWellAsManifestIds(String mismatch) {
        Prepared prepared = prepare(Operation.IDLE);
        publication.terminate(scope, prepared.id(), Failure.VECTOR_WRITE);
        String matching = prepared.manifest().ids().getFirst();
        String mismatched = prepared.manifest().ids().getLast();
        Metadata foreign = scope.metadata().put("commit_id", prepared.id());
        switch (mismatch) {
            case "generation" -> foreign.put(mismatch, 2L);
            case "schema_version" -> foreign.put(mismatch, 2);
            case "store_type" -> foreign.put(mismatch, EmbeddingStoreType.EN_LONG_TERM_MEMORY.text());
            case "commit_id" -> foreign.put(mismatch, nextId());
            default -> foreign.put(mismatch, "foreign");
        }
        vectors.put(scope.storeType(), mismatched, foreign);
        String notInManifest = nextId();
        vectors.put(scope.storeType(), notInManifest, scope.metadata().put("commit_id", prepared.id()));
        vectors.put(
                EmbeddingStoreType.EN_LONG_TERM_MEMORY,
                matching,
                scope.metadata().put("commit_id", prepared.id()));
        makeDue(prepared.id());
        gc.collect(prepared.id());
        assertFalse(vectors.contains(scope.storeType(), matching));
        assertTrue(vectors.contains(scope.storeType(), mismatched));
        assertTrue(vectors.contains(scope.storeType(), notInManifest));
        assertTrue(vectors.contains(EmbeddingStoreType.EN_LONG_TERM_MEMORY, matching));
        assertEquals(3, vectors.size());
        assertExact(
                vectors.exact.getFirst(),
                scope,
                prepared.id(),
                prepared.manifest().ids());
        assertFalse(vectors.exact.getFirst().filter().test(foreign));
        assertTrue(vectors.legacyReads.isEmpty());
    }

    @Test
    void missingOldScopeFailsClosedWithoutSubstitutingCurrentIdentityOrGuessingAStore() throws Exception {
        Prepared old = prepare(Operation.IDLE);
        publication.terminate(scope, old.id(), Failure.EXPIRED);
        assertEquals(
                1,
                sql.update("DELETE FROM chat_memory_commit WHERE operation = 'SCOPE' AND chat_id = ?", scope.chatId()));
        // Simulate an old generation from before scope retention, not a supported lifecycle transition.
        setState("generation = 2, user_id = 'new-owner', character_uid = 'new-character', store_type ="
                + " 'zh_long_term_memory'");
        sql.update("UPDATE chat_context SET user_id = 'new-owner' WHERE chat_id = ?", scope.chatId());
        MemoryScope current = new MemoryScope(
                scope.chatId(), "new-owner", "new-character", 2, EmbeddingStoreType.ZH_LONG_TERM_MEMORY);
        lifecycle.activate(current, FINGERPRINT);
        assertScopeLedger(current);
        makeDue(old.id());
        ChatMemoryCommit unchanged = row(old.id());
        gc.collect(old.id());
        gc.collect(old.id());
        assertEquals(unchanged, row(old.id()));
        assertEquals(1, operationRows("SCOPE").size());
        assertEquals(2L, onlyOperation("SCOPE").getGeneration());
        assertEquals(old.manifest().ids().size(), vectors.size());
        assertTrue(vectors.exact.isEmpty());
        assertTrue(vectors.legacyReads.isEmpty());
        assertTrue(gc.candidates(null, 100).contains(old.id()), "Unresolved cleanup must remain retryable");
    }

    @ParameterizedTest
    @ValueSource(strings = {"\"1\"", "1.5", "1.0", "4294967297", "true", "null", "2", "0", "-1", "[]", "{}"})
    void historicalScopeVersionMustBeCanonicalAfterSqlRoundTrip(String version) throws Exception {
        Prepared prepared = prepare(Operation.IDLE);
        publication.terminate(scope, prepared.id(), Failure.EXPIRED);
        makeDue(prepared.id());
        ChatMemoryCommit ledger = onlyOperation("SCOPE");
        var progress = (com.fasterxml.jackson.databind.node.ObjectNode)
                InfoUtils.defaultMapper().readTree(ledger.getProgress());
        progress.set("version", InfoUtils.defaultMapper().readTree(version));
        sql.update(
                "UPDATE chat_memory_commit SET progress = ? WHERE attempt_id = ?",
                progress.toString(),
                ledger.getAttemptId());
        var storedVersion = InfoUtils.defaultMapper()
                .readTree(row(ledger.getAttemptId()).getProgress())
                .get("version");
        ChatMemoryCommit before = row(prepared.id());
        ChatMemoryState stateBefore = state();
        if (version.equals("1.0")) {
            // MySQL canonicalizes an exactly integral JSON double to an integer.
            assertTrue(storedVersion.isIntegralNumber());
            assertEquals(1, storedVersion.intValue());
            gc.collect(prepared.id());
            assertEquals(stateBefore, state());
            assertEquals(0, vectors.size());
            assertExact(
                    vectors.exact.getFirst(),
                    scope,
                    prepared.id(),
                    prepared.manifest().ids());
            return;
        }
        assertEquals(version, storedVersion.toString());
        assertDeferred(() -> gc.collect(prepared.id()));
        assertEquals(before, row(prepared.id()));
        assertEquals(stateBefore, state());
        assertTrue(vectors.exact.isEmpty());
        assertEquals(prepared.manifest().ids().size(), vectors.size());
        sql.update(
                "UPDATE chat_memory_commit SET progress = ? WHERE attempt_id = ?",
                ledger.getProgress(),
                ledger.getAttemptId());
        gc.collect(prepared.id());
        assertEquals(0, vectors.size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"\"1\"", "1.5", "1.0", "4294967297", "true", "null", "2", "0", "-1", "[]", "{}"})
    void headGcVersionMustBeCanonicalAfterSqlRoundTrip(String version) throws Exception {
        Prepared first = prepare(Operation.IDLE);
        publication.publish(scope, first.id());
        Prepared second = prepare(Operation.IDLE);
        publication.publish(scope, second.id());
        gc.collect(first.id());
        ChatMemoryCommit tombstone = onlyOperation("GC_IDS");
        makeDue(tombstone.getAttemptId());
        var progress = (com.fasterxml.jackson.databind.node.ObjectNode)
                InfoUtils.defaultMapper().readTree(tombstone.getProgress());
        progress.set("version", InfoUtils.defaultMapper().readTree(version));
        sql.update(
                "UPDATE chat_memory_commit SET progress = ? WHERE attempt_id = ?",
                progress.toString(),
                tombstone.getAttemptId());
        ChatMemoryCommit before = row(tombstone.getAttemptId());
        ChatMemoryState stateBefore = state();
        if (version.equals("1.0")) {
            var storedVersion =
                    InfoUtils.defaultMapper().readTree(before.getProgress()).get("version");
            assertTrue(storedVersion.isIntegralNumber());
            assertEquals(1, storedVersion.intValue());
            gc.collect(tombstone.getAttemptId());
            assertEquals(stateBefore, state());
            assertEquals(3, vectors.size());
            assertExact(
                    vectors.exact.getFirst(),
                    scope,
                    first.id(),
                    MemoryDocumentCodec.decodeManifest(tombstone.getManifest()).ids());
            second.manifest().ids().forEach(id -> assertTrue(vectors.contains(scope.storeType(), id)));
            return;
        }
        assertDeferred(() -> gc.collect(tombstone.getAttemptId()));
        assertEquals(before, row(tombstone.getAttemptId()));
        assertEquals(stateBefore, state());
        assertTrue(vectors.exact.isEmpty());
        assertEquals(4, vectors.size());
        sql.update(
                "UPDATE chat_memory_commit SET progress = ? WHERE attempt_id = ?",
                tombstone.getProgress(),
                tombstone.getAttemptId());
        gc.collect(tombstone.getAttemptId());
        assertEquals(3, vectors.size());
        second.manifest().ids().forEach(id -> assertTrue(vectors.contains(scope.storeType(), id)));
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void publicationAndGcSerializeOnTheSameMysqlStateLock(boolean gcFirst) throws Exception {
        Prepared prepared = prepare(Operation.IDLE);
        if (gcFirst) {
            sql.update(
                    "UPDATE chat_memory_commit SET lease_until = UTC_TIMESTAMP(6) - INTERVAL 1 SECOND WHERE attempt_id"
                            + " = ?",
                    prepared.id());
        }
        ChatMemoryState before = state();
        CountDownLatch locked = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        CountDownLatch followerAttempted = new CountDownLatch(1);
        ChatMemoryCoordinationMapper leaderMapper = gate(locked, release, null);
        ChatMemoryCoordinationMapper followerMapper = gate(null, null, followerAttempted);
        MemoryGarbageCollector collector = collector(gcFirst ? leaderMapper : followerMapper);
        MemoryPublicationRepository publisher = publication(gcFirst ? followerMapper : leaderMapper);
        try (var workers = Executors.newFixedThreadPool(2)) {
            var leader = workers.submit(() -> {
                if (gcFirst) {
                    collector.collect(prepared.id());
                } else {
                    publisher.publish(scope, prepared.id());
                }
            });
            try {
                assertTrue(locked.await(10, TimeUnit.SECONDS));
                var follower = workers.submit(() -> {
                    if (gcFirst) {
                        IllegalStateException error = assertThrows(
                                IllegalStateException.class, () -> publisher.publish(scope, prepared.id()));
                        assertEquals("Memory publication state changed or lease expired", error.getMessage());
                    } else {
                        collector.collect(prepared.id());
                    }
                });
                assertTrue(followerAttempted.await(10, TimeUnit.SECONDS));
                assertMysqlStateLockWait();
                assertFalse(follower.isDone(), "The competing transaction must wait for the shared state row");
                assertTrue(vectors.exact.isEmpty());
                release.countDown();
                leader.get(20, TimeUnit.SECONDS);
                follower.get(20, TimeUnit.SECONDS);
            } finally {
                release.countDown();
            }
        }
        if (gcFirst) {
            assertRetired(prepared.id());
            assertEquals(before, state());
            makeDue(prepared.id());
            gc.collect(prepared.id());
            assertEquals(0, vectors.size());
        } else {
            assertEquals("committed", row(prepared.id()).getStatus());
            assertEquals(prepared.manifest().ids().getLast(), state().getProfileId());
            assertEquals(prepared.snapshot().sourceEndId(), state().getIdleThroughId());
            assertEquals(2, vectors.size());
            assertTrue(vectors.exact.isEmpty());
            assertTrue(operationRows("GC_IDS").isEmpty());
        }
    }

    @Test
    void adapterFailureKeepsDueTombstoneAndSanitizesFailureForRetry() {
        Prepared prepared = prepare(Operation.IDLE);
        publication.terminate(scope, prepared.id(), Failure.VECTOR_WRITE);
        makeDue(prepared.id());
        ChatMemoryCommit before = row(prepared.id());
        vectors.failExact = true;
        assertDeferred(() -> gc.collect(prepared.id()));
        assertEquals(before, row(prepared.id()));
        assertEquals(2, vectors.size());
        vectors.failExact = false;
        gc.collect(prepared.id());
        assertEquals(0, vectors.size());
        assertSwept(prepared.id());
    }

    private Prepared prepare(Operation operation) {
        Prepared seed = snapshot(operation);
        MemoryManifest manifest = new MemoryManifest(
                switch (operation) {
                    case OVERFLOW -> List.of(entry(Kind.WINDOW_SUMMARY, false), entry(Kind.WINDOW_SUMMARY, true));
                    case IDLE -> List.of(entry(Kind.EPISODE_SUMMARY, false), entry(Kind.PROFILE_SNAPSHOT, false));
                    case REVALIDATE -> List.of(entry(Kind.PROFILE_SNAPSHOT, false));
                });
        publication.prepare(seed.snapshot(), manifest, "fabricated-extractor", null);
        assertEquals(1, operationRows("SCOPE").size(), "Scope retention must precede vector insertion");
        manifest.ids()
                .forEach(id ->
                        vectors.put(scope.storeType(), id, scope.metadata().put("commit_id", seed.id())));
        return new Prepared(seed.snapshot(), manifest, seed.turn());
    }

    private Prepared snapshot(Operation operation) {
        TurnLease active = null;
        long through;
        if (operation == Operation.REVALIDATE) {
            setState("profile_revalidation_pending = 1");
            through = state().getIdleThroughId();
        } else {
            TurnLease source = turns.begin(scope, state().getFingerprint());
            turns.append(
                    source,
                    UserMessage.from("Source evidence"),
                    UserMessage.from("Source evidence"),
                    null,
                    MemoryTurnRepository.Origin.USER_INPUT,
                    null);
            turns.complete(source, AiMessage.from("Final answer"), null, null);
            through = state().getLatestFinalizedId();
        }
        String token;
        if (operation == Operation.OVERFLOW) {
            active = turns.begin(scope, state().getFingerprint());
            token = active.token();
        } else {
            token = nextId();
            sql.update(
                    "UPDATE chat_memory_state SET claim_token = ?, claim_lease_until = UTC_TIMESTAMP(6) + INTERVAL 5"
                            + " MINUTE, claim_deadline = UTC_TIMESTAMP(6) + INTERVAL 10 MINUTE WHERE chat_id = ?",
                    token,
                    scope.chatId());
        }
        return new Prepared(publication.snapshot(scope, operation, through, token), null, active);
    }

    private Entry entry(Kind kind, boolean archive) {
        String id = nextId();
        return new Entry(id, kind, archive, MemoryDocumentCodec.hash("fabricated-record-" + id));
    }

    private String nextId() {
        return UUID.nameUUIDFromBytes(("gc-fixture-" + ++sequence).getBytes(StandardCharsets.UTF_8))
                .toString();
    }

    private static String orderedId(int value) {
        return "00000000-0000-0000-0000-%012d".formatted(value);
    }

    private ChatMemoryCommit fixture(String id, String operation, String status, MemoryManifest manifest) {
        LocalDateTime now = coordination.databaseNow();
        return new ChatMemoryCommit()
                .withAttemptId(id)
                .withChatId(scope.chatId())
                .withGeneration(scope.generation())
                .withOperation(operation)
                .withStatus(status)
                .withSourceStartId(0L)
                .withSourceEndId(0L)
                .withExpectedCursor(0L)
                .withLeaseToken(id)
                .withFingerprint(FINGERPRINT)
                .withSchemaVersion(MemoryScope.SCHEMA_VERSION)
                .withModelId("fabricated-extractor")
                .withManifest(manifest == null ? "{}" : MemoryDocumentCodec.encodeManifest(manifest))
                .withProgress("{}")
                .withLeaseUntil(now.plusMinutes(10))
                .withGmtCreate(now)
                .withGmtModified(now);
    }

    private static void insertContext(String chatId, String owner) {
        sql.update(
                "INSERT INTO chat_context (chat_id, user_id, backend_id, gmt_create, gmt_modified) "
                        + "VALUES (?, ?, 'synthetic-backend', UTC_TIMESTAMP(), UTC_TIMESTAMP())",
                chatId,
                owner);
    }

    private MemoryPublicationRepository publication(ChatMemoryCoordinationMapper mapper) {
        return new MemoryPublicationRepository(mapper, states, commits, histories, transactions, properties);
    }

    private MemoryGarbageCollector collector(ChatMemoryCoordinationMapper mapper) {
        return new MemoryGarbageCollector(mapper, commits, vectors, properties, transactions);
    }

    private ChatMemoryState state() {
        return states.selectByPrimaryKey(scope.chatId()).orElseThrow();
    }

    private static ChatMemoryCommit row(String id) {
        return commits.selectByPrimaryKey(id).orElseThrow();
    }

    private static List<ChatMemoryCommit> operationRows(String operation) {
        return commits.select(query -> query.where(
                fun.freechat.mapper.ChatMemoryCommitDynamicSqlSupport.operation,
                org.mybatis.dynamic.sql.SqlBuilder.isEqualTo(operation)));
    }

    private static ChatMemoryCommit onlyOperation(String operation) {
        List<ChatMemoryCommit> rows = operationRows(operation);
        assertEquals(1, rows.size(), "Expected one " + operation + " row");
        return rows.getFirst();
    }

    private static void makeDue(String id) {
        assertEquals(
                1,
                sql.update(
                        "UPDATE chat_memory_commit SET gc_after = UTC_TIMESTAMP(6) - INTERVAL 1 SECOND WHERE"
                                + " attempt_id = ?",
                        id));
    }

    private void setState(String assignments) {
        assertEquals(
                1, sql.update("UPDATE chat_memory_state SET " + assignments + " WHERE chat_id = ?", scope.chatId()));
    }

    private ChatMemoryCommit assertRetired(String id) {
        ChatMemoryCommit row = row(id);
        assertEquals("terminal", row.getStatus());
        assertEquals("RETIRED", row.getErrorCategory());
        assertEquals(row.getGmtModified().plus(properties.getGcGracePeriod()), row.getGcAfter());
        assertTrue(row.getGcAfter().isAfter(coordination.databaseNow()));
        return row;
    }

    private void assertSwept(String id) {
        ChatMemoryCommit row = row(id);
        assertEquals("terminal", row.getStatus());
        assertEquals(row.getGmtModified().plus(properties.getGcGracePeriod()), row.getGcAfter());
        assertTrue(row.getGcAfter().isAfter(coordination.databaseNow()));
        assertFalse(gc.candidates(null, 100).contains(id));
    }

    private static void assertScopeLedger(MemoryScope expected) throws Exception {
        List<ChatMemoryCommit> rows = operationRows("SCOPE").stream()
                .filter(row ->
                        row.getChatId().equals(expected.chatId()) && row.getGeneration() == expected.generation())
                .toList();
        assertEquals(1, rows.size());
        ChatMemoryCommit row = rows.getFirst();
        assertEquals("control", row.getStatus());
        assertEquals("{}", row.getManifest());
        var progress = InfoUtils.defaultMapper().readTree(row.getProgress());
        assertEquals(4, progress.size());
        assertEquals(1, progress.path("version").intValue());
        assertEquals(expected.userId(), progress.path("userId").textValue());
        assertEquals(expected.characterUid(), progress.path("characterUid").textValue());
        assertEquals(expected.storeType().text(), progress.path("storeType").textValue());
    }

    private static boolean fixedHead(Entry entry) {
        return !entry.archive() && (entry.kind() == Kind.WINDOW_SUMMARY || entry.kind() == Kind.PROFILE_SNAPSHOT);
    }

    private static void assertExact(ExactCall call, MemoryScope expected, String originalCommit, List<String> ids) {
        assertEquals(expected.storeType(), call.store());
        assertEquals(ids, call.ids());
        assertTrue(call.filter().test(expected.metadata().put("commit_id", originalCommit)));
        for (String key : List.of("memory_id", "user_id", "character_uid", "store_type", "commit_id")) {
            Metadata foreign =
                    expected.metadata().put("commit_id", originalCommit).put(key, "other");
            assertFalse(call.filter().test(foreign), "Deletion omitted predicate " + key);
        }
        assertFalse(call.filter()
                .test(expected.metadata()
                        .put("commit_id", originalCommit)
                        .put("generation", expected.generation() + 1)));
        assertFalse(call.filter()
                .test(expected.metadata().put("commit_id", originalCommit).put("schema_version", 2)));
        assertFalse(call.filter().test(new Metadata().put("memory_id", expected.chatId())));
    }

    private static void assertDeferred(Executable action) {
        IllegalStateException failure = assertThrows(IllegalStateException.class, action);
        assertEquals("Memory cleanup deferred", failure.getMessage());
        assertNull(failure.getCause());
        assertEquals(0, failure.getSuppressed().length);
    }

    /** Decorates, never replaces, the real mapper; the gate holds the actual InnoDB lock inside its transaction. */
    private static ChatMemoryCoordinationMapper gate(
            CountDownLatch locked, CountDownLatch release, CountDownLatch attempted) {
        AtomicBoolean first = new AtomicBoolean(true);
        return (ChatMemoryCoordinationMapper) Proxy.newProxyInstance(
                ChatMemoryCoordinationMapper.class.getClassLoader(),
                new Class<?>[] {ChatMemoryCoordinationMapper.class},
                (proxy, method, args) -> {
                    boolean intercept = method.getName().equals("lock") && first.compareAndSet(true, false);
                    if (intercept && attempted != null) {
                        attempted.countDown();
                    }
                    Object result;
                    try {
                        result = method.invoke(coordination, args);
                    } catch (InvocationTargetException failure) {
                        throw failure.getCause();
                    }
                    if (intercept && locked != null) {
                        assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
                        locked.countDown();
                        assertTrue(release.await(20, TimeUnit.SECONDS));
                    }
                    return result;
                });
    }

    private static void assertMysqlStateLockWait() {
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(10);
        do {
            Integer waits = sql.queryForObject("""
                SELECT COUNT(*) FROM performance_schema.data_lock_waits w
                JOIN performance_schema.data_locks l ON l.ENGINE_LOCK_ID = w.REQUESTING_ENGINE_LOCK_ID
                WHERE l.OBJECT_SCHEMA = 'freechat' AND l.OBJECT_NAME = 'chat_memory_state'
                """, Integer.class);
            if (waits != null && waits > 0) {
                return;
            }
            Thread.yield();
        } while (System.nanoTime() < deadline);
        fail("Competing transaction did not wait for the MySQL chat_memory_state lock");
    }

    private record Prepared(Snapshot snapshot, MemoryManifest manifest, TurnLease turn) {
        String id() {
            return snapshot.attemptId();
        }
    }

    private record Key(EmbeddingStoreType store, String id) {}

    private record ExactCall(EmbeddingStoreType store, List<String> ids, Filter filter) {}

    private record LegacyCall(EmbeddingStoreType store, String chatId, List<String> ids) {}

    /** Exact IDs and predicates are both applied; no model, approximate search, store serialization or broad delete. */
    private static final class FakeCleanup implements MemoryEmbeddingCleanupService {
        private final Map<Key, Metadata> records = new LinkedHashMap<>();
        private final List<ExactCall> exact = new ArrayList<>();
        private final List<EmbeddingStoreType> legacyReads = new ArrayList<>();
        private final List<LegacyCall> legacyDeletes = new ArrayList<>();
        private boolean failExact;

        synchronized void put(EmbeddingStoreType store, String id, Metadata metadata) {
            records.put(new Key(store, id), metadata);
        }

        synchronized boolean contains(EmbeddingStoreType store, String id) {
            return records.containsKey(new Key(store, id));
        }

        synchronized int size() {
            return records.size();
        }

        synchronized long legacyCount(EmbeddingStoreType store, String chatId) {
            return records.entrySet().stream()
                    .filter(entry -> entry.getKey().store() == store && legacy(entry.getValue(), chatId))
                    .count();
        }

        @Override
        public synchronized void removeExact(EmbeddingStoreType store, List<String> ids, Filter filter) {
            assertFalse(
                    TransactionSynchronizationManager.isActualTransactionActive(),
                    "Vector I/O must run outside SQL locks");
            assertTrue(MemoryScope.isMemoryStore(store));
            assertTrue(!ids.isEmpty() && ids.size() <= 100);
            assertTrue(ids.stream().allMatch(MemoryDocumentCodec::canonicalUuid));
            exact.add(new ExactCall(store, List.copyOf(ids), filter));
            if (failExact) {
                throw new IllegalStateException("private-source-credential", new RuntimeException("private-cause"));
            }
            records.entrySet()
                    .removeIf(entry -> entry.getKey().store() == store
                            && ids.contains(entry.getKey().id())
                            && filter.test(entry.getValue()));
        }

        @Override
        public synchronized List<String> legacyIds(EmbeddingStoreType store, String chatId, int limit) {
            assertFalse(TransactionSynchronizationManager.isActualTransactionActive());
            assertTrue(limit > 0 && limit <= 100);
            legacyReads.add(store);
            return records.entrySet().stream()
                    .filter(entry -> entry.getKey().store() == store && legacy(entry.getValue(), chatId))
                    .map(entry -> entry.getKey().id())
                    .sorted()
                    .limit(limit)
                    .toList();
        }

        @Override
        public synchronized void removeLegacy(EmbeddingStoreType store, String chatId, List<String> ids) {
            assertFalse(TransactionSynchronizationManager.isActualTransactionActive());
            assertTrue(!ids.isEmpty() && ids.size() <= 100);
            assertTrue(ids.stream().allMatch(MemoryDocumentCodec::canonicalUuid));
            legacyDeletes.add(new LegacyCall(store, chatId, List.copyOf(ids)));
            records.entrySet()
                    .removeIf(entry -> entry.getKey().store() == store
                            && ids.contains(entry.getKey().id())
                            && legacy(entry.getValue(), chatId));
        }

        private static boolean legacy(Metadata metadata, String chatId) {
            return chatId.equals(metadata.getString("memory_id"))
                    && !metadata.toMap().containsKey("schema_version")
                    && !metadata.toMap().containsKey("commit_id")
                    && !metadata.toMap().containsKey("generation");
        }
    }
}
