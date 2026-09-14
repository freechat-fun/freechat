package fun.freechat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.TokenCountEstimator;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.model.output.TokenUsage;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.filter.Filter;
import fun.freechat.mapper.ChatHistoryMapper;
import fun.freechat.mapper.ChatMemoryCommitMapper;
import fun.freechat.mapper.ChatMemoryCoordinationMapper;
import fun.freechat.mapper.ChatMemoryStateMapper;
import fun.freechat.model.ChatHistory;
import fun.freechat.model.ChatMemoryCommit;
import fun.freechat.model.ChatMemoryState;
import fun.freechat.service.chat.memory.LongTermMemoryProperties;
import fun.freechat.service.chat.memory.MemoryBoundsFactory;
import fun.freechat.service.chat.memory.MemoryDocument;
import fun.freechat.service.chat.memory.MemoryDocumentCodec;
import fun.freechat.service.chat.memory.MemoryManifest;
import fun.freechat.service.chat.memory.MemoryPublicationRepository;
import fun.freechat.service.chat.memory.MemoryPublicationRepository.Operation;
import fun.freechat.service.chat.memory.MemoryPublicationRepository.Snapshot;
import fun.freechat.service.chat.memory.MemoryPublisher;
import fun.freechat.service.chat.memory.MemoryScope;
import fun.freechat.service.chat.memory.MemoryVectorRepository;
import fun.freechat.service.enums.EmbeddingStoreType;
import fun.freechat.service.rag.EmbeddingModelService;
import fun.freechat.service.rag.ExactEmbeddingStoreService;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockMakers;
import org.mybatis.dynamic.sql.dsl.SelectDSLCompleter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.SimpleTransactionStatus;

/** Real publisher, vector codec and SQL publication rules, with deterministic in-memory I/O boundaries. */
class MemoryPublisherTest {
    private static final MemoryScope SCOPE =
            new MemoryScope("chat", "owner", "character", 7, EmbeddingStoreType.EN_LONG_TERM_MEMORY);
    private static final String FINGERPRINT = MemoryDocumentCodec.hash("configuration");
    private static final LocalDateTime NOW = LocalDateTime.of(2026, 9, 12, 10, 0);
    private static final String LEASE = "00000000-0000-0000-0000-000000000001";
    private final Fixture f = new Fixture();

    @AfterEach
    void noAnnFlushDeletionOrImplicitRetries() {
        // Any new vector API operation needs an explicit test, especially synchronous orphan deletion.
        assertTrue(mockingDetails(f.store).getInvocations().stream()
                .allMatch(call -> call.getMethod().getName().equals("addAll")));
        assertTrue(mockingDetails(f.stores).getInvocations().stream()
                .allMatch(call -> List.of("get", "of").contains(call.getMethod().getName())));
    }

    @Test
    void preparesImmutableManifestChecksEveryLeaseAndExactRecordBeforePublishingSql() {
        TokenUsage usage = new TokenUsage(21, 8);
        f.publisher.publish(f.snapshot, f.documents, "local-test-model", usage);
        assertEquals(
                List.of(
                        "prepare", "guard", "embed", "guard", "insert", "guard", "exact", "guard", "exact", "guard",
                        "guard", "publish"),
                f.events);
        ChatMemoryCommit attempt = f.attempt();
        assertEquals("committed", attempt.getStatus());
        assertEquals("local-test-model", attempt.getModelId());
        assertNotNull(attempt.getTokenUsage());
        assertEquals("{\"sourceHash\":\"" + f.snapshot.sourceHash() + "\"}", attempt.getProgress());
        assertEquals(10L, attempt.getExpectedCursor());
        assertEquals(11L, attempt.getSourceStartId());
        assertEquals(14L, attempt.getSourceEndId());
        assertEquals(FINGERPRINT, attempt.getFingerprint());
        assertEquals(f.manifest(), MemoryDocumentCodec.decodeManifest(attempt.getManifest()));
        assertEquals(14L, f.state.getIdleThroughId());
        assertEquals(f.documents.getFirst().id(), f.state.getProfileId());
        assertEquals(10L, f.state.getOverflowThroughId());
        assertEquals(0, f.state.getRetryAttempts());
        verify(f.model).embedAll(anyList());
        verify(f.store).addAll(eq(f.manifest().ids()), anyList(), anyList());
    }

    @ParameterizedTest
    @EnumSource(Operation.class)
    void operationManifestsPublishOnlyTheirOwnHeadAndCursor(Operation operation) {
        f.configure(operation);
        f.publisher.publish(f.snapshot, f.documents, "local-test-model", null);
        assertEquals("committed", f.attempt().getStatus());
        assertEquals(
                f.manifest(), MemoryDocumentCodec.decodeManifest(f.attempt().getManifest()));
        if (operation == Operation.OVERFLOW) {
            assertEquals(14L, f.state.getOverflowThroughId());
            assertEquals(10L, f.state.getIdleThroughId());
            assertEquals(f.documents.getFirst().id(), f.state.getSummaryId());
            assertNull(f.state.getProfileId());
        } else {
            assertEquals(f.documents.getFirst().id(), f.state.getProfileId());
            assertEquals(14L, f.state.getIdleThroughId());
            assertEquals(0, f.state.getProfileRevalidationPending().intValue());
            assertEquals(
                    operation == Operation.REVALIDATE ? 0L : 11L, f.attempt().getSourceStartId());
        }
    }

    @ParameterizedTest
    @ValueSource(
            strings = {"chat", "owner", "character", "generation", "store", "commit", "fingerprint", "start", "end"})
    void rejectsSnapshotMismatchesBeforePrepareOrEmbedding(String field) {
        MemoryDocument d = f.documents.getFirst();
        MemoryScope changed =
                switch (field) {
                    case "chat" -> new MemoryScope("other", SCOPE.userId(), SCOPE.characterUid(), 7, SCOPE.storeType());
                    case "owner" ->
                        new MemoryScope(SCOPE.chatId(), "other", SCOPE.characterUid(), 7, SCOPE.storeType());
                    case "character" -> new MemoryScope(SCOPE.chatId(), SCOPE.userId(), "other", 7, SCOPE.storeType());
                    case "generation" ->
                        new MemoryScope(SCOPE.chatId(), SCOPE.userId(), SCOPE.characterUid(), 8, SCOPE.storeType());
                    case "store" ->
                        new MemoryScope(
                                SCOPE.chatId(),
                                SCOPE.userId(),
                                SCOPE.characterUid(),
                                7,
                                EmbeddingStoreType.ZH_LONG_TERM_MEMORY);
                    default -> SCOPE;
                };
        MemoryDocument invalid = new MemoryDocument(
                d.id(),
                changed,
                field.equals("commit") ? UUID.randomUUID().toString() : d.commitId(),
                d.kind(),
                d.archive(),
                field.equals("start") ? 10 : d.sourceStartId(),
                field.equals("end") ? 13 : d.sourceEndId(),
                d.observedAt(),
                field.equals("fingerprint") ? MemoryDocumentCodec.hash("other") : d.fingerprint(),
                d.summary(),
                d.userFacts(),
                d.characterDeltas());
        assertThrows(
                IllegalArgumentException.class,
                () -> f.publisher.publish(f.snapshot, List.of(invalid), "local-test-model", null));
        assertTrue(f.events.isEmpty());
        verifyNoInteractions(f.store, f.stores, f.model);
    }

    @ParameterizedTest
    @ValueSource(strings = {"empty", "duplicate", "wrong-kind", "cursor", "head", "fingerprint", "source", "lease"})
    void invalidManifestOrStaleSnapshotCannotPrepare(String change) {
        switch (change) {
            case "empty" -> f.documents = List.of();
            case "duplicate" -> f.documents = List.of(f.documents.getFirst(), f.documents.getFirst());
            case "wrong-kind" -> f.documents = List.of(f.documents.getFirst());
            case "cursor" -> f.state.setIdleThroughId(11L);
            case "head" -> f.state.setProfileId(UUID.randomUUID().toString());
            case "fingerprint" -> f.state.setFingerprint(MemoryDocumentCodec.hash("changed"));
            case "source" -> f.source.getFirst().setMessage("changed source");
            case "lease" -> f.expire();
            default -> fail();
        }
        assertThrows(
                RuntimeException.class, () -> f.publisher.publish(f.snapshot, f.documents, "local-test-model", null));
        assertTrue(f.attempts.isEmpty());
        verifyNoInteractions(f.store, f.stores, f.model);
    }

    @ParameterizedTest
    @ValueSource(strings = {"prepare", "embed", "insert", "exact", "missing", "corrupt", "publish"})
    void failuresNeverRetryAndPreparedAttemptsAreTerminalizedWithoutDeletingVectors(String stage) {
        f.failure = stage;
        IllegalStateException failure = assertThrows(
                IllegalStateException.class,
                () -> f.publisher.publish(f.snapshot, f.documents, "local-test-model", null));
        assertNull(failure.getCause());
        assertEquals(0, failure.getSuppressed().length);
        if (stage.equals("prepare")) {
            assertTrue(f.attempts.isEmpty());
            assertEquals(List.of("prepare"), f.events);
            verifyNoInteractions(f.store, f.model);
        } else {
            assertEquals("Memory publication failed", failure.getMessage());
            assertEquals("terminal", f.attempt().getStatus());
            assertEquals("VECTOR_WRITE", f.attempt().getErrorCategory());
            assertNotNull(f.attempt().getGcAfter());
            assertEquals(1, Collections.frequency(f.events, "terminal"));
            assertEquals(1, Collections.frequency(f.events, "embed"));
            assertTrue(Collections.frequency(f.events, "insert") <= 1);
            assertEquals(10L, f.state.getIdleThroughId());
            assertNull(f.state.getProfileId());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"prepare", "embed", "insert", "exact"})
    void revokedLeaseAtEveryExternalBoundaryStopsBeforeTheNextSideEffect(String stage) {
        f.expireAfter = stage;
        sanitized(
                () -> f.publisher.publish(f.snapshot, f.documents, "local-test-model", null),
                "Memory publication failed");
        assertEquals("terminal", f.attempt().getStatus());
        assertFalse(f.events.contains("publish"));
        if (stage.equals("prepare")) {
            verifyNoInteractions(f.model, f.store);
        } else if (stage.equals("embed")) {
            verifyNoInteractions(f.store);
        } else if (stage.equals("insert")) {
            assertFalse(f.events.contains("exact"));
        } else {
            assertEquals(1, Collections.frequency(f.events, "exact"));
        }
    }

    @Test
    void cleanupFailureCannotExposePayloadOrTriggerRemoval() {
        f.failure = "insert";
        doThrow(new IllegalStateException("private cleanup payload"))
                .when(f.commits)
                .updateByPrimaryKey(any(ChatMemoryCommit.class));
        sanitized(
                () -> f.publisher.publish(f.snapshot, f.documents, "local-test-model", null),
                "Memory publication failed");
        assertEquals(1, Collections.frequency(f.events, "insert"));
        assertFalse(f.events.contains("publish"));
    }

    @Test
    void explicitRetryRequiresFreshAttemptAndRetainsFailedVectorsForReconciliation() {
        f.failure = "insert";
        sanitized(
                () -> f.publisher.publish(f.snapshot, f.documents, "local-test-model", null),
                "Memory publication failed");
        String oldAttempt = f.snapshot.attemptId();
        String orphan = f.documents.getFirst().id();
        assertTrue(f.segments.containsKey(orphan));
        f.failure = "";
        f.configure(Operation.IDLE);
        assertNotEquals(oldAttempt, f.snapshot.attemptId());
        f.publisher.publish(f.snapshot, f.documents, "local-test-model", null);
        assertEquals("terminal", f.attempts.get(oldAttempt).getStatus());
        assertTrue(f.segments.containsKey(orphan));
        assertTrue(f.publisher.readCommitted(SCOPE, orphan, () -> {}).isEmpty());
        assertEquals("committed", f.attempt().getStatus());
    }

    @Test
    void committedReadsCheckInvocationAndSqlAuthorizationBeforeAndAfterExactRead() {
        MemoryDocument document = f.committedDocument();
        assertEquals(
                Optional.of(document),
                f.publisher.readCommitted(SCOPE, document.id(), () -> f.events.add("invocation")));
        assertEquals(List.of("invocation", "exact", "authorize", "exact", "invocation", "authorize"), f.events);
        verifyNoInteractions(f.model, f.store);
    }

    @ParameterizedTest
    @ValueSource(
            strings = {"absent", "prepared", "terminal", "foreign-chat", "foreign-generation", "schema", "manifest"})
    void uncommittedOrUnauthorizedRecordsStayPrivateAndAreNeverDecoded(String status) {
        MemoryDocument document = f.committedDocument();
        // A malformed body proves commit discovery reads metadata only, not untrusted/private JSON.
        TextSegment encoded = f.segments.get(document.id());
        f.segments.put(document.id(), TextSegment.from("private malformed body", encoded.metadata()));
        switch (status) {
            case "absent" -> f.attempts.clear();
            case "prepared", "terminal" -> f.attempt().setStatus(status);
            case "foreign-chat" -> f.attempt().setChatId("other");
            case "foreign-generation" -> f.attempt().setGeneration(8L);
            case "schema" -> f.attempt().setSchemaVersion(MemoryScope.SCHEMA_VERSION + 1);
            case "manifest" ->
                f.attempt()
                        .setManifest(MemoryDocumentCodec.encodeManifest(new MemoryManifest(
                                List.of(f.manifest().entries().getLast()))));
            default -> fail();
        }
        assertTrue(f.publisher
                .readCommitted(SCOPE, document.id(), () -> f.events.add("invocation"))
                .isEmpty());
        assertEquals(List.of("invocation", "exact", "authorize"), f.events);
        verifyNoInteractions(f.model, f.store);
    }

    @Test
    void absentVectorsDoNotQuerySqlAndInvalidIdsDoNotReachStorage() {
        assertTrue(f.publisher
                .readCommitted(SCOPE, UUID.randomUUID().toString(), () -> {})
                .isEmpty());
        assertEquals(List.of("exact"), f.events);
        f.events.clear();
        sanitized(
                () -> f.publisher.readCommitted(SCOPE, "private invalid id", () -> {}), "Committed memory read failed");
        assertTrue(f.events.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"delete-vector", "delete-commit", "terminal", "fingerprint", "manifest"})
    void disappearingOrChangedAuthorizationCannotReturnStaleMemory(String change) {
        MemoryDocument document = f.committedDocument();
        AtomicInteger reads = new AtomicInteger();
        f.onExact = () -> {
            if (reads.incrementAndGet() == 2) {
                switch (change) {
                    case "delete-vector" -> f.segments.remove(document.id());
                    case "delete-commit" -> f.attempts.clear();
                    case "terminal" -> f.attempt().setStatus("terminal");
                    case "fingerprint" -> f.attempt().setFingerprint(MemoryDocumentCodec.hash("changed"));
                    case "manifest" ->
                        f.attempt()
                                .setManifest(MemoryDocumentCodec.encodeManifest(
                                        new MemoryManifest(List.of(new MemoryManifest.Entry(
                                                document.id(), document.kind(), false, "0".repeat(64))))));
                    default -> fail();
                }
            }
        };
        assertTrue(f.publisher
                .readCommitted(SCOPE, document.id(), () -> f.events.add("invocation"))
                .isEmpty());
        assertEquals(List.of("invocation", "exact", "authorize", "exact", "invocation", "authorize"), f.events);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2})
    void invocationRevocationStopsReadsAtEitherGuard(int failAt) {
        MemoryDocument document = f.committedDocument();
        AtomicInteger checks = new AtomicInteger();
        sanitized(
                () -> f.publisher.readCommitted(SCOPE, document.id(), () -> {
                    f.events.add("invocation");
                    if (checks.incrementAndGet() == failAt) {
                        throw new IllegalStateException("private invocation payload");
                    }
                }),
                "Committed memory read failed");
        assertEquals(failAt, checks.get());
        assertEquals(
                failAt == 1
                        ? List.of("invocation")
                        : List.of("invocation", "exact", "authorize", "exact", "invocation"),
                f.events);
    }

    @ParameterizedTest
    @ValueSource(strings = {"hash", "commit", "kind", "archive", "metadata"})
    void exactReadsEnforceAuthorizedManifestAndMetadata(String corruption) {
        MemoryDocument document = f.committedDocument();
        TextSegment encoded = f.segments.get(document.id());
        if (corruption.equals("hash")) {
            f.segments.put(document.id(), TextSegment.from(encoded.text() + " ", encoded.metadata()));
        } else if (corruption.equals("metadata")) {
            Metadata metadata =
                    Metadata.from(new HashMap<>(encoded.metadata().toMap())).put("fingerprint", "0".repeat(64));
            f.segments.put(document.id(), TextSegment.from(encoded.text(), metadata));
        } else {
            MemoryManifest.Entry entry = f.manifest().entries().getFirst();
            String commit = corruption.equals("commit") ? UUID.randomUUID().toString() : document.commitId();
            MemoryManifest.Entry wrong = new MemoryManifest.Entry(
                    entry.id(),
                    corruption.equals("kind") || corruption.equals("archive")
                            ? MemoryDocument.Kind.WINDOW_SUMMARY
                            : entry.kind(),
                    corruption.equals("archive"),
                    entry.hash());
            sanitized(() -> f.vectors.read(SCOPE, commit, wrong), "Memory vector read failed");
            return;
        }
        sanitized(() -> f.publisher.readCommitted(SCOPE, document.id(), () -> {}), "Committed memory read failed");
    }

    @ParameterizedTest
    @ValueSource(strings = {"memory_id", "user_id", "character_uid", "generation", "store_type", "schema_version"})
    void commitLookupUsesEveryScopeDimensionWithoutEmbeddingOrBodyDecoding(String dimension) {
        MemoryDocument document = f.committedDocument();
        TextSegment encoded = f.segments.get(document.id());
        Map<String, Object> metadata = new HashMap<>(encoded.metadata().toMap());
        metadata.put(dimension, dimension.equals("generation") || dimension.equals("schema_version") ? 99L : "other");
        f.segments.put(document.id(), TextSegment.from("private malformed body", Metadata.from(metadata)));
        assertTrue(f.vectors.findCommit(SCOPE, document.id()).isEmpty());
        assertEquals(List.of("exact"), f.events);
        verifyNoInteractions(f.model, f.store);
    }

    @Test
    void commitLookupRejectsMalformedCommitMetadataWithoutParsingBody() {
        MemoryDocument document = f.committedDocument();
        f.segments.put(
                document.id(),
                TextSegment.from(
                        "private malformed body", SCOPE.metadata().put("commit_id", "private invalid commit")));
        sanitized(() -> f.vectors.findCommit(SCOPE, document.id()), "Memory commit lookup failed");
        assertEquals(List.of("exact"), f.events);
    }

    @Test
    void profileHeadRequiresCompatibleFingerprintWhileHistoricalSummaryRemainsReadable() {
        MemoryDocument profile = f.committedDocument();
        assertEquals(
                Optional.of(profile), f.publisher.readHead(SCOPE, profile.id(), profile.kind(), FINGERPRINT, () -> {}));
        assertTrue(f.publisher
                .readHead(SCOPE, profile.id(), profile.kind(), "0".repeat(64), () -> {})
                .isEmpty());
        MemoryDocument summary = f.documents.getLast();
        assertEquals(
                Optional.of(summary),
                f.publisher.readHead(SCOPE, summary.id(), summary.kind(), "0".repeat(64), () -> {}));
        f.events.clear();
        assertTrue(f.publisher
                .readHead(SCOPE, null, profile.kind(), FINGERPRINT, () -> fail("Null head must not read"))
                .isEmpty());
        assertTrue(f.events.isEmpty());
    }

    @Test
    void missingWrongKindArchivedAndCheckpointHeadsFailClosed() {
        MemoryDocument profile = f.committedDocument();
        sanitized(
                () -> f.publisher.readHead(SCOPE, UUID.randomUUID().toString(), profile.kind(), FINGERPRINT, () -> {}),
                "Committed memory head is unavailable");
        sanitized(
                () -> f.publisher.readHead(
                        SCOPE, profile.id(), MemoryDocument.Kind.WINDOW_SUMMARY, FINGERPRINT, () -> {}),
                "Invalid committed memory head");
        for (MemoryDocument.Kind kind :
                List.of(MemoryDocument.Kind.WINDOW_SUMMARY, MemoryDocument.Kind.EXTRACTION_CHECKPOINT)) {
            MemoryDocument invalid = f.document(kind, kind == MemoryDocument.Kind.WINDOW_SUMMARY);
            f.documents = List.of(invalid);
            f.authorizeDocuments();
            sanitized(
                    () -> f.publisher.readHead(SCOPE, invalid.id(), kind, FINGERPRINT, () -> {}),
                    "Invalid committed memory head");
        }
    }

    private static void sanitized(Runnable action, String message) {
        IllegalStateException error = assertThrows(IllegalStateException.class, action::run);
        assertEquals(message, error.getMessage());
        assertNull(error.getCause());
        assertEquals(0, error.getSuppressed().length);
    }

    private static <T> T fake(Class<T> type) {
        return mock(type, withSettings().mockMaker(MockMakers.SUBCLASS));
    }

    private static final class Fixture {
        final ChatMemoryCoordinationMapper coordination = fake(ChatMemoryCoordinationMapper.class);
        final ChatMemoryStateMapper states = fake(ChatMemoryStateMapper.class);
        final ChatMemoryCommitMapper commits = fake(ChatMemoryCommitMapper.class);
        final ChatHistoryMapper histories = fake(ChatHistoryMapper.class);
        final ExactEmbeddingStoreService stores = fake(ExactEmbeddingStoreService.class);
        final EmbeddingModelService models = fake(EmbeddingModelService.class);
        final EmbeddingModel model = fake(EmbeddingModel.class);

        @SuppressWarnings("unchecked")
        final EmbeddingStore<TextSegment> store = fake(EmbeddingStore.class);

        final List<String> events = new ArrayList<>();
        final Map<String, ChatMemoryCommit> attempts = new HashMap<>();
        final Map<String, TextSegment> segments = new HashMap<>();
        final List<ChatHistory> source = List.of(new ChatHistory()
                .withId(14L)
                .withMemoryId(SCOPE.chatId())
                .withRecordKind("turn-complete")
                .withEnabled((byte) 1)
                .withTurnId(LEASE));
        final ChatMemoryState state = new ChatMemoryState()
                .withChatId(SCOPE.chatId())
                .withUserId(SCOPE.userId())
                .withCharacterUid(SCOPE.characterUid())
                .withStoreType(SCOPE.storeType().text())
                .withGeneration(7L)
                .withStatus("active")
                .withFingerprint(FINGERPRINT)
                .withVersion(0L)
                .withLatestFinalizedId(14L)
                .withIdleThroughId(10L)
                .withOverflowThroughId(10L)
                .withProfileRevalidationPending((byte) 0)
                .withClaimToken(LEASE)
                .withClaimLeaseUntil(NOW.plusMinutes(1))
                .withClaimDeadline(NOW.plusMinutes(2))
                .withTurnToken(LEASE)
                .withTurnLeaseUntil(NOW.plusMinutes(1))
                .withTurnDeadline(NOW.plusMinutes(2))
                .withRetryAttempts(2);
        final MemoryPublicationRepository publications;
        final MemoryVectorRepository vectors;
        final MemoryPublisher publisher;
        Snapshot snapshot;
        List<MemoryDocument> documents;
        String failure = "";
        String expireAfter = "";
        Runnable onExact = () -> {};

        Fixture() {
            LongTermMemoryProperties properties = new LongTermMemoryProperties();
            properties.afterPropertiesSet();
            PlatformTransactionManager transactions = fake(PlatformTransactionManager.class);
            when(transactions.getTransaction(any())).thenAnswer(ignored -> new SimpleTransactionStatus());
            when(coordination.lock(SCOPE.chatId())).thenReturn(Optional.of(state));
            when(coordination.databaseNow()).thenReturn(NOW);
            when(coordination.retainControl(any())).thenAnswer(call -> {
                ChatMemoryCommit row = call.getArgument(0);
                doReturn(Optional.of(row.getProgress())).when(coordination).scopeProgress(row.getAttemptId());
                return 1;
            });
            when(histories.selectMany(any(org.mybatis.dynamic.sql.select.render.SelectStatementProvider.class)))
                    .thenReturn(source.stream()
                            .map(row -> new ChatHistory().withId(row.getId()))
                            .toList());
            when(coordination.selectHistoryPage(any())).thenReturn(source);
            when(histories.selectByPrimaryKey(14L)).thenReturn(Optional.of(source.getFirst()));
            when(commits.insertSelective(any(ChatMemoryCommit.class))).thenAnswer(call -> {
                event("prepare");
                ChatMemoryCommit attempt = call.getArgument(0);
                assertNull(attempts.putIfAbsent(attempt.getAttemptId(), attempt));
                return 1;
            });
            when(commits.selectOne(any(SelectDSLCompleter.class))).thenAnswer(call -> {
                events.add("guard");
                // The last guard belongs to SQL publication, after all immutable records were verified.
                if (failure.equals("publish") && Collections.frequency(events, "guard") == documents.size() + 4) {
                    throw new IllegalStateException("private SQL publication payload");
                }
                return Optional.ofNullable(attempts.get(snapshot.attemptId()));
            });
            when(commits.selectByPrimaryKey(anyString())).thenAnswer(call -> {
                events.add("authorize");
                return Optional.ofNullable(attempts.get(call.getArgument(0)));
            });
            when(commits.updateByPrimaryKey(any(ChatMemoryCommit.class))).thenAnswer(call -> {
                ChatMemoryCommit attempt = call.getArgument(0);
                if (attempt.getStatus().equals("terminal")) {
                    events.add("terminal");
                }
                attempts.put(attempt.getAttemptId(), attempt);
                return 1;
            });
            when(states.updateByPrimaryKey(any(ChatMemoryState.class))).thenAnswer(call -> {
                events.add("publish");
                return 1;
            });
            TokenCountEstimator estimator = fake(TokenCountEstimator.class);
            when(estimator.estimateTokenCountInText(anyString())).thenAnswer(call -> {
                String text = call.getArgument(0);
                return text.codePointCount(0, text.length());
            });
            when(models.tokenCountEstimatorForLang("en")).thenReturn(estimator);
            when(models.modelForLang("en")).thenReturn(model);
            when(model.embedAll(anyList())).thenAnswer(call -> {
                event("embed");
                List<TextSegment> inputs = call.getArgument(0);
                assertTrue(inputs.stream()
                        .allMatch(input -> input.metadata().toMap().isEmpty()));
                return Response.from(Collections.nCopies(inputs.size(), Embedding.from(new float[] {1, 0})));
            });
            when(stores.of(SCOPE.chatId(), SCOPE.storeType())).thenReturn(store);
            doAnswer(call -> {
                        List<String> ids = call.getArgument(0);
                        List<TextSegment> records = call.getArgument(2);
                        // Persist first, then fail: publication must tolerate partial or ambiguous vector writes.
                        for (int index = 0; index < ids.size(); index++) {
                            segments.put(ids.get(index), records.get(index));
                        }
                        event("insert");
                        return null;
                    })
                    .when(store)
                    .addAll(anyList(), anyList(), anyList());
            when(stores.get(eq(SCOPE.storeType()), anyString(), any(Filter.class)))
                    .thenAnswer(call -> {
                        event("exact");
                        onExact.run();
                        TextSegment segment = segments.get(call.getArgument(1));
                        Filter filter = call.getArgument(2);
                        if (segment == null || !filter.test(segment.metadata()) || failure.equals("missing")) {
                            return Optional.empty();
                        }
                        return Optional.of(
                                failure.equals("corrupt")
                                        ? TextSegment.from(segment.text() + " ", segment.metadata())
                                        : segment);
                    });
            publications =
                    new MemoryPublicationRepository(coordination, states, commits, histories, transactions, properties);
            vectors = new MemoryVectorRepository(
                    stores, models, new MemoryBoundsFactory(properties, models, fake(BeanFactory.class)), properties);
            publisher = new MemoryPublisher(publications, vectors);
            configure(Operation.IDLE);
        }

        void configure(Operation operation) {
            if (operation == Operation.REVALIDATE) {
                state.setIdleThroughId(14L);
                state.setProfileRevalidationPending((byte) 1);
            }
            snapshot = publications.snapshot(SCOPE, operation, 14, LEASE);
            documents = switch (operation) {
                case IDLE ->
                    List.of(
                            document(MemoryDocument.Kind.PROFILE_SNAPSHOT, false),
                            document(MemoryDocument.Kind.EPISODE_SUMMARY, false));
                case OVERFLOW ->
                    List.of(
                            document(MemoryDocument.Kind.WINDOW_SUMMARY, false),
                            document(MemoryDocument.Kind.WINDOW_SUMMARY, true));
                case REVALIDATE -> List.of(document(MemoryDocument.Kind.PROFILE_SNAPSHOT, false));
            };
            events.clear();
        }

        MemoryDocument document(MemoryDocument.Kind kind, boolean archive) {
            return new MemoryDocument(
                    UUID.randomUUID().toString(),
                    SCOPE,
                    snapshot.attemptId(),
                    kind,
                    archive,
                    snapshot.operation() == Operation.REVALIDATE ? 0 : snapshot.sourceStartId(),
                    snapshot.sourceEndId(),
                    NOW.toInstant(ZoneOffset.UTC),
                    FINGERPRINT,
                    kind == MemoryDocument.Kind.PROFILE_SNAPSHOT ? "" : "deterministic summary",
                    List.of(),
                    List.of());
        }

        MemoryManifest manifest() {
            return MemoryManifest.of(documents, vectors.codec(SCOPE));
        }

        ChatMemoryCommit attempt() {
            return attempts.get(snapshot.attemptId());
        }

        MemoryDocument committedDocument() {
            authorizeDocuments();
            events.clear();
            return documents.getFirst();
        }

        void authorizeDocuments() {
            attempts.put(
                    snapshot.attemptId(),
                    new ChatMemoryCommit()
                            .withAttemptId(snapshot.attemptId())
                            .withChatId(SCOPE.chatId())
                            .withGeneration(SCOPE.generation())
                            .withSchemaVersion(MemoryScope.SCHEMA_VERSION)
                            .withStatus("committed")
                            .withFingerprint(FINGERPRINT)
                            .withManifest(MemoryDocumentCodec.encodeManifest(manifest())));
            documents.forEach(
                    document -> segments.put(document.id(), vectors.codec(SCOPE).encode(document)));
        }

        void event(String stage) {
            events.add(stage);
            if (expireAfter.equals(stage)) {
                expire();
            }
            if (failure.equals(stage)) {
                throw new IllegalStateException("private " + stage + " payload");
            }
        }

        void expire() {
            state.setClaimLeaseUntil(NOW);
            state.setTurnLeaseUntil(NOW);
        }
    }
}
