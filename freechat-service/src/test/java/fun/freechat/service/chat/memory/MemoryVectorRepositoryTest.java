package fun.freechat.service.chat.memory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.TokenCountEstimator;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.filter.Filter;
import fun.freechat.service.enums.EmbeddingStoreType;
import fun.freechat.service.rag.EmbeddingModelService;
import fun.freechat.service.rag.ExactEmbeddingStoreService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

class MemoryVectorRepositoryTest {
    private static final String PRIVATE_TEXT = "private-memory-payload";
    private static final String COMMIT = id(1000);
    private static final Instant OBSERVED = Instant.parse("2026-09-12T10:00:00Z");
    private static final MemoryScope SCOPE =
            new MemoryScope("chat", "user", "character", 7, EmbeddingStoreType.EN_LONG_TERM_MEMORY);
    private static final Embedding VECTOR = Embedding.from(new float[] {1, 0});
    private final List<String> events = new ArrayList<>();
    private final LongTermMemoryProperties properties = new LongTermMemoryProperties();
    private final FakeModels models = new FakeModels(events);
    private final FakeStores stores = new FakeStores(events);
    private final MemoryVectorRepository repository = new MemoryVectorRepository(
            stores, models, new MemoryBoundsFactory(properties, models, new DefaultListableBeanFactory()), properties);

    @Test
    void insertsExactlyThePreparedUuidListAndVerifiesEveryRecordBeforeReturning() {
        List<MemoryDocument> documents = preparedDocuments();
        MemoryManifest manifest = manifest(documents);
        repository.insertPrepared(SCOPE, COMMIT, manifest, documents, guard("lease", 0));

        assertEquals(List.of(manifest.ids()), stores.insertedIds);
        for (String id : stores.insertedIds.getFirst()) {
            assertEquals(4, UUID.fromString(id).version());
            assertEquals(2, UUID.fromString(id).variant());
        }
        assertEquals(documents.size(), stores.insertedEmbeddings.getFirst().size());
        assertEquals(
                documents.stream().map(document -> codec().encode(document)).toList(),
                stores.insertedSegments.getFirst());
        assertEquals(manifest.ids(), stores.readIds);
        assertEquals(
                List.of(
                        "lease",
                        "embed",
                        "lease",
                        "insert",
                        "lease",
                        readEvent(documents.get(0)),
                        "lease",
                        readEvent(documents.get(1)),
                        "lease",
                        readEvent(documents.get(2)),
                        "lease",
                        readEvent(documents.get(3)),
                        "lease",
                        readEvent(documents.get(4)),
                        "lease"),
                events);
        assertEquals(List.of(new StoreSelection(SCOPE.chatId(), SCOPE.storeType())), stores.selections);
        for (MemoryDocument document : documents) {
            assertEquals(
                    document,
                    repository
                            .read(SCOPE, COMMIT, manifest.find(document.id()).orElseThrow())
                            .orElseThrow());
        }
    }

    @Test
    void embedsOnlyNaturalLanguageSummaryAndFactValuesWithoutJsonOrMetadata() {
        List<MemoryDocument> documents = preparedDocuments();
        repository.insertPrepared(SCOPE, COMMIT, manifest(documents), documents, () -> {});
        assertEquals(
                List.of(
                        "A remembered conversation 中文",
                        "Prefers green tea\nArrived at the observatory",
                        "Fragment evidence\nPrefers green tea\nArrived at the observatory",
                        "Session profile has no retained facts.",
                        "An archived conversation"),
                models.inputs.stream().map(TextSegment::text).toList());
        for (TextSegment input : models.inputs) {
            assertTrue(input.metadata().toMap().isEmpty());
            assertFalse(input.text().contains(COMMIT));
            assertFalse(input.text().contains("userFacts"));
            assertFalse(input.text().contains("tea_preference"));
            assertFalse(input.text().contains("record_kind"));
        }
        assertEquals(List.of("en"), models.modelLanguages);
        assertTrue(models.prefixLanguages.isEmpty());
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"scope", "commit", "id", "hash", "kind", "archive", "missing", "extra", "order"})
    void rejectsRecordsThatDoNotExactlyMatchThePreparedScopeCommitAndManifestBeforeIo(String mismatch) {
        List<MemoryDocument> documents = List.of(window(1, false), episode(2));
        MemoryManifest original = manifest(documents);
        List<MemoryManifest.Entry> entries = new ArrayList<>(original.entries());
        MemoryScope scope = SCOPE;
        String commit = COMMIT;
        MemoryManifest.Entry first = entries.getFirst();
        switch (mismatch) {
            case "scope" -> scope = otherScope();
            case "commit" -> commit = id(1001);
            case "id" -> entries.set(0, new MemoryManifest.Entry(id(3), first.kind(), false, first.hash()));
            case "hash" -> entries.set(0, new MemoryManifest.Entry(first.id(), first.kind(), false, "0".repeat(64)));
            case "kind" ->
                entries.set(
                        0,
                        new MemoryManifest.Entry(
                                first.id(), MemoryDocument.Kind.PROFILE_SNAPSHOT, false, first.hash()));
            case "archive" ->
                entries.set(
                        0,
                        new MemoryManifest.Entry(first.id(), MemoryDocument.Kind.WINDOW_SUMMARY, true, first.hash()));
            case "missing" -> entries.removeLast();
            case "extra" -> entries.add(manifest(List.of(episode(3))).entries().getFirst());
            case "order" -> entries = new ArrayList<>(entries.reversed());
            default -> throw new AssertionError("Unknown test case");
        }
        MemoryScope requestedScope = scope;
        String requestedCommit = commit;
        MemoryManifest supplied = new MemoryManifest(entries);
        assertSafeFailure(
                IllegalArgumentException.class,
                () -> repository.insertPrepared(
                        requestedScope, requestedCommit, supplied, documents, guard("lease", 0)));
        assertTrue(events.isEmpty());
        assertTrue(models.modelLanguages.isEmpty());
        assertTrue(stores.selections.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5})
    void losingLeaseAtEveryEmbeddingWriteAndReadBoundaryStopsAllFurtherOperations(int failureAt) {
        List<MemoryDocument> documents = List.of(episode(1), episode(2));
        List<String> successful = List.of(
                "lease",
                "embed",
                "lease",
                "insert",
                "lease",
                readEvent(documents.getFirst()),
                "lease",
                readEvent(documents.getLast()),
                "lease");
        int finalIndex = IntStream.range(0, successful.size())
                .filter(index -> successful.get(index).equals("lease"))
                .skip(failureAt - 1L)
                .findFirst()
                .orElseThrow();
        assertSafeFailure(
                IllegalStateException.class,
                () -> repository.insertPrepared(
                        SCOPE, COMMIT, manifest(documents), documents, guard("lease", failureAt)));
        assertEquals(successful.subList(0, finalIndex + 1), events);
        assertEquals(failureAt >= 3 ? 1 : 0, stores.insertedIds.size());
        assertTrue(stores.deletedIds.isEmpty());
    }

    @ParameterizedTest
    @EnumSource(ReadbackFault.class)
    void preparedWriteFailsOnMissingMalformedOrMismatchedExactReadbackAndDoesNotReadNextRecord(ReadbackFault fault) {
        List<MemoryDocument> documents = List.of(episode(1), episode(2));
        MemoryManifest manifest = manifest(documents);
        stores.readback = fault::apply;
        assertSafeFailure(
                IllegalStateException.class,
                () -> repository.insertPrepared(SCOPE, COMMIT, manifest, documents, guard("lease", 0)));
        assertEquals(List.of("lease", "embed", "lease", "insert", "lease", readEvent(documents.getFirst())), events);
        assertEquals(List.of(manifest.ids()), stores.insertedIds);
        assertTrue(stores.deletedIds.isEmpty());
    }

    @Test
    void anAmbiguousPartialInsertNeverRetriesGeneratesReplacementIdsOrDeletesAutomatically() {
        List<MemoryDocument> documents = List.of(episode(1), episode(2));
        MemoryManifest manifest = manifest(documents);
        stores.failAfterInsertedCount = 1;
        assertSafeFailure(
                IllegalStateException.class,
                () -> repository.insertPrepared(SCOPE, COMMIT, manifest, documents, guard("lease", 0)));
        assertEquals(List.of("lease", "embed", "lease", "insert"), events);
        assertEquals(List.of(manifest.ids()), stores.insertedIds);
        assertEquals(1, stores.records.size());
        assertTrue(stores.records.containsKey(
                new RecordKey(SCOPE.storeType(), documents.getFirst().id())));
        assertTrue(stores.readIds.isEmpty());
        assertTrue(stores.deletedIds.isEmpty());
        assertEquals(1, models.modelLanguages.size());
    }

    @Test
    void readReturnsEmptyOnlyForAbsentScopedIdAndVerifiesThePresentDocument() {
        MemoryDocument document = episode(1);
        MemoryManifest.Entry entry = manifest(List.of(document)).entries().getFirst();
        assertTrue(repository.read(SCOPE, COMMIT, entry).isEmpty());
        stores.seed(document, codec(), 1);
        assertEquals(document, repository.read(SCOPE, COMMIT, entry).orElseThrow());
        assertTrue(repository.read(otherScope(), COMMIT, entry).isEmpty());
        assertTrue(models.modelLanguages.isEmpty());
        assertTrue(stores.selections.isEmpty());
        assertEquals(List.of(document.id(), document.id(), document.id()), stores.readIds);
        assertEquals(List.of(SCOPE.storeType(), SCOPE.storeType(), SCOPE.storeType()), stores.readTypes);
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"commit", "kind", "archive", "id", "scope", "hash", "malformed", "metadata"})
    void readRejectsWrongManifestIdentityAndEvenCorrectlyHashedInvalidReadback(String mismatch) {
        MemoryDocument document = window(1, true);
        TextSegment encoded = codec().encode(document);
        MemoryManifest.Entry original = manifest(List.of(document)).entries().getFirst();
        MemoryManifest.Entry entry = original;
        String commit = COMMIT;
        TextSegment readback = encoded;
        switch (mismatch) {
            case "commit" -> commit = id(1001);
            case "kind" ->
                entry = new MemoryManifest.Entry(
                        document.id(), MemoryDocument.Kind.EPISODE_SUMMARY, false, original.hash());
            case "archive" -> entry = new MemoryManifest.Entry(document.id(), document.kind(), false, original.hash());
            case "hash" -> readback = TextSegment.from(encoded.text() + " ", encoded.metadata());
            case "metadata" ->
                readback = TextSegment.from(
                        encoded.text(), encoded.metadata().copy().put("commit_id", id(1001)));
            case "id" -> readback = codec().encode(window(2, true));
            case "scope" ->
                readback = codec().encode(document(
                        1, otherScope(), COMMIT, document.kind(), true, document.summary(), List.of(), List.of()));
            case "malformed" ->
                readback = TextSegment.from("{\"summary\":\"" + PRIVATE_TEXT + "\"", encoded.metadata());
            default -> throw new AssertionError("Unknown test case");
        }
        // These cases must reach decoding/identity validation rather than fail only the hash gate.
        if (List.of("id", "scope", "malformed").contains(mismatch)) {
            entry = new MemoryManifest.Entry(
                    document.id(), document.kind(), true, MemoryDocumentCodec.hash(readback.text()));
        }
        stores.seed(document, codec(), 1);
        TextSegment returned = readback;
        stores.readback = ignored -> returned;
        MemoryManifest.Entry expected = entry;
        String expectedCommit = commit;
        assertSafeFailure(IllegalStateException.class, () -> repository.read(SCOPE, expectedCommit, expected));
        assertEquals(List.of(readEvent(document)), events);
    }

    @Test
    void candidateFilterAndsEveryScopeDimensionWithEpisodeOrArchivedWindowOnly() {
        List<MemoryDocument> documents = List.of(
                episode(1),
                window(2, true),
                window(3, false),
                document(4, SCOPE, COMMIT, MemoryDocument.Kind.PROFILE_SNAPSHOT, false, "", List.of(), List.of()),
                document(
                        5,
                        SCOPE,
                        COMMIT,
                        MemoryDocument.Kind.EXTRACTION_CHECKPOINT,
                        false,
                        "Checkpoint",
                        List.of(),
                        List.of()));
        documents.forEach(document -> stores.seed(document, codec(), 1));
        EmbeddingSearchResult<TextSegment> result = repository.candidates(SCOPE, "remember", 90);
        assertEquals(
                List.of(id(1), id(2)),
                result.matches().stream().map(EmbeddingMatch::embeddingId).toList());
        Filter filter = stores.lastSearch.filter();
        Metadata episode = codec().encode(documents.getFirst()).metadata();
        Metadata archive = codec().encode(documents.get(1)).metadata();
        assertTrue(filter.test(episode));
        assertTrue(filter.test(archive));
        for (Metadata valid : List.of(episode, archive)) {
            for (String key : SCOPE.metadata().toMap().keySet()) {
                Metadata missing = valid.copy();
                missing.remove(key);
                assertFalse(filter.test(missing), "Missing scope dimension must not match: " + key);
                Metadata different = changedScopeMetadata(valid, key);
                assertFalse(filter.test(different), "Different scope dimension must not match: " + key);
            }
        }
        assertFalse(filter.test(SCOPE.metadata()));
        assertFalse(filter.test(episode.copy().put("record_kind", "UNKNOWN")));
        assertFalse(filter.test(archive.copy().put("archive", 0)));
        assertEquals(List.of("embed", "search"), events);
    }

    @ParameterizedTest
    @EnumSource(
            value = EmbeddingStoreType.class,
            names = {"EN_LONG_TERM_MEMORY", "ZH_LONG_TERM_MEMORY", "DEFAULT_LONG_TERM_MEMORY"})
    void candidatesUseTheCollectionLanguageAndModelQueryPrefix(EmbeddingStoreType type) {
        String language =
                switch (type) {
                    case EN_LONG_TERM_MEMORY -> "en";
                    case ZH_LONG_TERM_MEMORY -> "zh";
                    default -> "default";
                };
        MemoryScope scope = new MemoryScope("chat", "user", "character", 7, type);
        repository.candidates(scope, "What was remembered?", 1);
        assertEquals(List.of(language), models.modelLanguages);
        assertEquals(List.of(language), models.prefixLanguages);
        assertEquals(
                List.of("query[" + language + "]: What was remembered?"),
                models.inputs.stream().map(TextSegment::text).toList());
        assertEquals(List.of(new StoreSelection(scope.chatId(), type)), stores.selections);
        assertEquals(VECTOR, stores.lastSearch.queryEmbedding());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidSearches")
    void rejectsUnboundedOrEmptySearchArgumentsBeforeEmbeddingOrStoreAccess(String name, String query, int limit) {
        assertSafeFailure(IllegalArgumentException.class, () -> repository.candidates(SCOPE, query, limit));
        assertTrue(events.isEmpty());
        assertTrue(models.modelLanguages.isEmpty());
        assertTrue(stores.selections.isEmpty());
    }

    @Test
    void searchHonorsCodePointQueryBoundaryResultCapAndConfiguredFiniteScoreThreshold() {
        properties.setSearchQueryMaxChars(4);
        properties.setSearchMinScore(0.75);
        for (int index = 1; index <= 92; index++) {
            stores.seed(episode(index), codec(), index == 1 ? 0.5 : 0.9);
        }
        String query = new String(Character.toChars(0x20000)).repeat(4);
        EmbeddingSearchResult<TextSegment> maximum = repository.candidates(SCOPE, query, 90);
        assertEquals(90, maximum.matches().size());
        assertFalse(
                maximum.matches().stream().anyMatch(match -> match.embeddingId().equals(id(1))));
        assertEquals(90, stores.lastSearch.maxResults());
        assertTrue(Double.isFinite(stores.lastSearch.minScore()));
        assertEquals(0.75, stores.lastSearch.minScore());
        assertEquals(1, repository.candidates(SCOPE, query, 1).matches().size());
        int operations = events.size();
        assertSafeFailure(IllegalArgumentException.class, () -> repository.candidates(SCOPE, query + "x", 1));
        assertEquals(operations, events.size());
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(
            strings = {
                "insert-model",
                "insert-embedding",
                "insert-open",
                "insert-read",
                "read",
                "search-model",
                "search-prefix",
                "search-embedding",
                "search-open",
                "search-store"
            })
    void sanitizesDependencyFailuresWithoutPayloadBearingCauseOrSuppressedException(String stage) {
        MemoryDocument document = episode(1);
        MemoryManifest manifest = manifest(List.of(document));
        if (!stage.startsWith("insert")) {
            stores.seed(document, codec(), 1);
        }
        if (stage.endsWith("model")) {
            models.failureAt = "model";
        } else if (stage.endsWith("embedding")) {
            models.failureAt = "embed";
        } else if (stage.endsWith("prefix")) {
            models.failureAt = "prefix";
        } else if (stage.endsWith("open")) {
            stores.failureAt = "open";
        } else if (stage.endsWith("read")) {
            stores.failureAt = "read";
        } else {
            stores.failureAt = "search";
        }
        Executable operation;
        if (stage.startsWith("insert")) {
            operation = () -> repository.insertPrepared(SCOPE, COMMIT, manifest, List.of(document), () -> {});
        } else if (stage.startsWith("search")) {
            operation = () -> repository.candidates(SCOPE, "remember", 1);
        } else {
            operation = () -> repository.read(SCOPE, COMMIT, manifest.entries().getFirst());
        }
        assertSafeFailure(IllegalStateException.class, operation);
        assertTrue(stores.insertedIds.size() <= 1);
        assertTrue(stores.deletedIds.size() <= 1);
    }

    @Test
    void preparedWriteAlsoSanitizesFailureWhileResolvingTheBoundsEstimator() {
        MemoryDocument document = episode(1);
        MemoryManifest manifest = manifest(List.of(document));
        models.failureAt = "estimator";
        assertSafeFailure(
                IllegalStateException.class,
                () -> repository.insertPrepared(SCOPE, COMMIT, manifest, List.of(document), () -> {}));
        assertTrue(events.isEmpty());
    }

    private MemoryDocumentCodec codec() {
        return repository.codec(SCOPE);
    }

    private MemoryManifest manifest(List<MemoryDocument> documents) {
        return MemoryManifest.of(documents, codec());
    }

    private Runnable guard(String name, int failAt) {
        int[] calls = {0};
        return () -> {
            events.add(name);
            if (++calls[0] == failAt) {
                throw payloadFailure();
            }
        };
    }

    private static List<MemoryDocument> preparedDocuments() {
        List<MemoryDocument.Fact> user =
                List.of(new MemoryDocument.Fact("tea_preference", "Prefers green tea", List.of(1L), OBSERVED, false));
        List<MemoryDocument.Fact> character = List.of(
                new MemoryDocument.Fact("story_state", "Arrived at the observatory", List.of(2L), OBSERVED, true));
        return List.of(
                episode(1),
                document(2, SCOPE, COMMIT, MemoryDocument.Kind.PROFILE_SNAPSHOT, false, "", user, character),
                document(
                        3,
                        SCOPE,
                        COMMIT,
                        MemoryDocument.Kind.EXTRACTION_CHECKPOINT,
                        false,
                        "Fragment evidence",
                        user,
                        character),
                document(4, SCOPE, COMMIT, MemoryDocument.Kind.PROFILE_SNAPSHOT, false, "", List.of(), List.of()),
                window(5, true));
    }

    private static MemoryDocument episode(int number) {
        return document(
                number,
                SCOPE,
                COMMIT,
                MemoryDocument.Kind.EPISODE_SUMMARY,
                false,
                "A remembered conversation 中文",
                List.of(),
                List.of());
    }

    private static MemoryDocument window(int number, boolean archive) {
        return document(
                number,
                SCOPE,
                COMMIT,
                MemoryDocument.Kind.WINDOW_SUMMARY,
                archive,
                "An archived conversation",
                List.of(),
                List.of());
    }

    private static MemoryDocument document(
            int number,
            MemoryScope scope,
            String commit,
            MemoryDocument.Kind kind,
            boolean archive,
            String summary,
            List<MemoryDocument.Fact> user,
            List<MemoryDocument.Fact> character) {
        return new MemoryDocument(
                id(number),
                scope,
                commit,
                kind,
                archive,
                1,
                3,
                OBSERVED,
                MemoryDocumentCodec.hash("source fingerprint"),
                summary,
                user,
                character);
    }

    private static MemoryScope otherScope() {
        return new MemoryScope(
                "other-chat", SCOPE.userId(), SCOPE.characterUid(), SCOPE.generation(), SCOPE.storeType());
    }

    private static String id(int number) {
        // Fixed RFC 4122 version-4 UUID fixtures keep the insert identity assertions reproducible.
        return new UUID(0x1234567812344000L, 0x8000000000000000L + number).toString();
    }

    private static String readEvent(MemoryDocument document) {
        return "read:" + document.id();
    }

    private static Metadata changedScopeMetadata(Metadata metadata, String key) {
        Metadata changed = metadata.copy();
        return switch (key) {
            case "generation", "schema_version" -> changed.put(key, 99L);
            default -> changed.put(key, "other");
        };
    }

    private static Stream<Arguments> invalidSearches() {
        return Stream.of(
                Arguments.of("null query", null, 1),
                Arguments.of("empty query", "", 1),
                Arguments.of("blank query", " \t\n", 1),
                Arguments.of("overlong query", "x".repeat(2001), 1),
                Arguments.of("zero limit", "remember", 0),
                Arguments.of("negative limit", "remember", -1),
                Arguments.of("over maximum limit", "remember", 91),
                Arguments.of("unbounded limit", "remember", Integer.MAX_VALUE));
    }

    private static IllegalStateException payloadFailure() {
        IllegalStateException failure =
                new IllegalStateException(PRIVATE_TEXT, new IllegalArgumentException(PRIVATE_TEXT));
        failure.addSuppressed(new IllegalStateException(PRIVATE_TEXT));
        return failure;
    }

    private static void assertSafeFailure(Class<? extends RuntimeException> type, Executable operation) {
        RuntimeException failure = assertThrows(type, operation);
        assertNotNull(failure.getMessage());
        assertFalse(failure.getMessage().contains(PRIVATE_TEXT), "Exception message must not expose memory payload");
        assertNull(failure.getCause(), "Exception must not retain a payload-bearing cause");
        assertEquals(0, failure.getSuppressed().length, "Exception must not retain payload-bearing suppressed errors");
    }

    private enum ReadbackFault {
        MISSING,
        MALFORMED,
        CHANGED_HASH,
        WRONG_ID,
        WRONG_SCOPE,
        WRONG_COMMIT,
        WRONG_KIND,
        WRONG_ARCHIVE,
        WRONG_METADATA;

        TextSegment apply(TextSegment segment) {
            return switch (this) {
                case MISSING -> null;
                case MALFORMED -> TextSegment.from("{\"payload\":\"" + PRIVATE_TEXT + "\"", segment.metadata());
                case CHANGED_HASH -> TextSegment.from(segment.text() + " ", segment.metadata());
                case WRONG_ID -> TextSegment.from(segment.text().replace(id(1), id(999)), segment.metadata());
                case WRONG_SCOPE ->
                    TextSegment.from(segment.text().replace("\"chat\"", "\"other-chat\""), segment.metadata());
                case WRONG_COMMIT -> TextSegment.from(segment.text().replace(COMMIT, id(1001)), segment.metadata());
                case WRONG_KIND ->
                    TextSegment.from(segment.text().replace("EPISODE_SUMMARY", "WINDOW_SUMMARY"), segment.metadata());
                case WRONG_ARCHIVE ->
                    TextSegment.from(
                            segment.text().replace("\"archive\":false", "\"archive\":true"), segment.metadata());
                case WRONG_METADATA ->
                    TextSegment.from(segment.text(), segment.metadata().copy().put("fingerprint", "0".repeat(64)));
            };
        }
    }

    private record StoreSelection(Object memoryId, EmbeddingStoreType type) {}

    private record RecordKey(EmbeddingStoreType type, String id) {}

    private static final class FakeModels implements EmbeddingModelService {
        private final List<String> events;
        private final List<TextSegment> inputs = new ArrayList<>();
        private final List<String> modelLanguages = new ArrayList<>();
        private final List<String> prefixLanguages = new ArrayList<>();
        private String failureAt = "";

        private FakeModels(List<String> events) {
            this.events = events;
        }

        @Override
        public EmbeddingModel modelForLang(String lang) {
            modelLanguages.add(lang);
            fail("model");
            return new EmbeddingModel() {
                @Override
                public Response<List<Embedding>> embedAll(List<TextSegment> segments) {
                    events.add("embed");
                    inputs.addAll(segments);
                    fail("embed");
                    return Response.from(
                            segments.stream().map(ignored -> VECTOR).toList());
                }
            };
        }

        @Override
        public TokenCountEstimator tokenCountEstimatorForLang(String lang) {
            fail("estimator");
            return MemoryBoundsTest.estimator(ignored -> 1);
        }

        @Override
        public String queryPrefixForLang(String lang) {
            prefixLanguages.add(lang);
            fail("prefix");
            return "query[" + lang + "]: ";
        }

        @Override
        public int dimensionForLang(String lang) {
            return 2;
        }

        private void fail(String stage) {
            if (failureAt.equals(stage)) {
                throw payloadFailure();
            }
        }
    }

    private static final class FakeStores implements ExactEmbeddingStoreService {
        private final List<String> events;
        private final Map<RecordKey, TextSegment> records = new LinkedHashMap<>();
        private final Map<RecordKey, Double> scores = new LinkedHashMap<>();
        private final List<StoreSelection> selections = new ArrayList<>();
        private final List<List<String>> insertedIds = new ArrayList<>();
        private final List<List<Embedding>> insertedEmbeddings = new ArrayList<>();
        private final List<List<TextSegment>> insertedSegments = new ArrayList<>();
        private final List<List<String>> deletedIds = new ArrayList<>();
        private final List<String> readIds = new ArrayList<>();
        private final List<EmbeddingStoreType> readTypes = new ArrayList<>();
        private final List<Filter> readFilters = new ArrayList<>();
        private UnaryOperator<TextSegment> readback = UnaryOperator.identity();
        private String failureAt = "";
        private int failAfterInsertedCount;
        private EmbeddingSearchRequest lastSearch;

        private FakeStores(List<String> events) {
            this.events = events;
        }

        private void seed(MemoryDocument document, MemoryDocumentCodec codec, double score) {
            RecordKey key = new RecordKey(document.scope().storeType(), document.id());
            records.put(key, codec.encode(document));
            scores.put(key, score);
        }

        @Override
        public Optional<TextSegment> get(EmbeddingStoreType type, String id, Filter scope) {
            events.add("read:" + id);
            readIds.add(id);
            readTypes.add(type);
            readFilters.add(scope);
            fail("read");
            return Optional.ofNullable(records.get(new RecordKey(type, id)))
                    .filter(segment -> scope.test(segment.metadata()))
                    .map(readback);
        }

        @Override
        public EmbeddingStore<TextSegment> of(Object memoryId, EmbeddingStoreType type) {
            selections.add(new StoreSelection(memoryId, type));
            fail("open");
            // Deliberately shared by all chats: only the repository's explicit filters/IDs isolate data.
            return new EmbeddingStore<>() {
                @Override
                public void addAll(List<String> ids, List<Embedding> embeddings, List<TextSegment> segments) {
                    events.add("insert");
                    insertedIds.add(List.copyOf(ids));
                    insertedEmbeddings.add(List.copyOf(embeddings));
                    insertedSegments.add(List.copyOf(segments));
                    assertEquals(ids.size(), embeddings.size());
                    assertEquals(ids.size(), segments.size());
                    for (int index = 0; index < ids.size(); index++) {
                        RecordKey key = new RecordKey(type, ids.get(index));
                        assertFalse(records.containsKey(key), "An insert must never silently reuse an existing UUID");
                        records.put(key, segments.get(index));
                        scores.put(key, 1.0);
                        if (index + 1 == failAfterInsertedCount) {
                            throw payloadFailure();
                        }
                    }
                }

                @Override
                public EmbeddingSearchResult<TextSegment> search(EmbeddingSearchRequest request) {
                    events.add("search");
                    lastSearch = request;
                    fail("search");
                    return new EmbeddingSearchResult<>(records.entrySet().stream()
                            .filter(entry -> entry.getKey().type() == type)
                            .filter(entry ->
                                    request.filter().test(entry.getValue().metadata()))
                            .filter(entry -> scores.get(entry.getKey()) >= request.minScore())
                            .sorted(Comparator.comparingDouble(entry -> -scores.get(entry.getKey())))
                            .limit(request.maxResults())
                            .map(entry -> new EmbeddingMatch<>(
                                    scores.get(entry.getKey()), entry.getKey().id(), VECTOR, entry.getValue()))
                            .toList());
                }

                @Override
                public void removeAll(Collection<String> ids) {
                    deletedIds.add(List.copyOf(ids));
                    throw new AssertionError("Memory deletion must include its scope");
                }

                @Override
                public List<String> generateIds(int count) {
                    throw new AssertionError("Prepared inserts must never generate replacement IDs");
                }

                @Override
                public String add(Embedding embedding) {
                    throw unsupportedInsert();
                }

                @Override
                public void add(String id, Embedding embedding) {
                    throw unsupportedInsert();
                }

                @Override
                public String add(Embedding embedding, TextSegment segment) {
                    throw unsupportedInsert();
                }

                @Override
                public List<String> addAll(List<Embedding> embeddings) {
                    throw unsupportedInsert();
                }

                @Override
                public List<String> addAll(List<Embedding> embeddings, List<TextSegment> segments) {
                    throw unsupportedInsert();
                }

                @Override
                public void remove(String id) {
                    throw new AssertionError("Cleanup must delete the explicit matched ID list");
                }

                @Override
                public void removeAll(Filter filter) {
                    throw new AssertionError("Cleanup must not use filter-wide deletion");
                }

                @Override
                public void removeAll() {
                    throw new AssertionError("Cleanup must not delete an entire collection");
                }
            };
        }

        @Override
        public void flush(Object memoryId, EmbeddingStoreType type, EmbeddingStore<TextSegment> store) {
            throw new AssertionError("Memory repository tests must never flush or serialize a store");
        }

        @Override
        public void delete(Object memoryId, EmbeddingStoreType type) {
            throw new AssertionError("Cleanup must never delete a chat-wide store");
        }

        private void fail(String stage) {
            if (failureAt.equals(stage)) {
                throw payloadFailure();
            }
        }

        private static AssertionError unsupportedInsert() {
            return new AssertionError("Prepared insertion requires the explicit UUID, embedding and segment lists");
        }
    }
}
