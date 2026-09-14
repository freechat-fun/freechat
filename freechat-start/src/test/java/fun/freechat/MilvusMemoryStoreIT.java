package fun.freechat;

import static dev.langchain4j.store.embedding.filter.MetadataFilterBuilder.metadataKey;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.read.ListAppender;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.TokenCountEstimator;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.filter.Filter;
import dev.langchain4j.store.embedding.filter.comparison.IsEqualTo;
import fun.freechat.langchain4j.store.embedding.MilvusMemoryCleanupAdapter;
import fun.freechat.langchain4j.store.embedding.MilvusTextSegmentReader;
import fun.freechat.service.chat.memory.LongTermMemoryProperties;
import fun.freechat.service.chat.memory.MemoryBounds;
import fun.freechat.service.chat.memory.MemoryDocument;
import fun.freechat.service.chat.memory.MemoryDocumentCodec;
import fun.freechat.service.chat.memory.MemoryScope;
import fun.freechat.service.enums.EmbeddingStoreType;
import fun.freechat.service.rag.EmbeddingModelService;
import fun.freechat.service.rag.impl.MilvusEmbeddingStoreServiceImpl;
import io.milvus.client.AbstractMilvusGrpcClient;
import io.milvus.client.MilvusServiceClient;
import io.milvus.common.clientenum.ConsistencyLevelEnum;
import io.milvus.param.ConnectParam;
import io.milvus.param.LogLevel;
import io.milvus.param.dml.DeleteParam;
import io.milvus.param.dml.InsertParam;
import io.milvus.param.dml.QueryParam;
import io.milvus.param.dml.UpsertParam;
import io.milvus.response.QueryResultsWrapper;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.milvus.MilvusContainer;
import org.testcontainers.utility.DockerImageName;

/** Real storage only: no Spring context, model downloads, provider credentials or application files. */
@Testcontainers
@Timeout(value = 2, unit = TimeUnit.MINUTES)
class MilvusMemoryStoreIT {
    // Match AbstractIntegrationTest, without inheriting its other containers or application context.
    @Container
    private static final MilvusContainer MILVUS = new MilvusContainer(DockerImageName.parse("milvusdb/milvus:v2.4.20"))
            .waitingFor(Wait.forHttp("/healthz").forPort(9091).forStatusCode(200))
            .withStartupTimeout(Duration.ofMinutes(3));

    private static final Embedding VECTOR = Embedding.from(new float[] {1, 0, 0, 0});
    private static final Instant OBSERVED = Instant.parse("2026-09-12T10:00:00Z");
    private static final String TRANSPORT_LOGGER = "io.grpc.netty.shaded.io.grpc.netty.NettyClientHandler";
    private static final MemoryDocumentCodec CODEC = codec();
    private static MilvusEmbeddingStoreServiceImpl service;
    private static LogCapture sdkLogs;
    private static LogCapture storeLogs;
    private static LogCapture transportLogs;

    @BeforeAll
    static void startService() {
        // SDK logging stays enabled, but no SDK payload can reach an application/file appender.
        sdkLogs = new LogCapture(Level.DEBUG, "io.milvus", AbstractMilvusGrpcClient.class.getName());
        storeLogs = new LogCapture(Level.DEBUG, "dev.langchain4j.store.embedding.milvus");
        // No Spring context is loaded: apply only the production HTTP/2 frame suppression here.
        transportLogs = new LogCapture(Level.OFF, TRANSPORT_LOGGER);
        try {
            service = newService();
        } catch (RuntimeException | Error error) {
            restoreLogs();
            throw error;
        }
    }

    @AfterAll
    static void stopService() {
        try {
            closeService(service);
        } finally {
            restoreLogs();
        }
    }

    private static void restoreLogs() {
        if (transportLogs != null) {
            transportLogs.close();
        }
        if (storeLogs != null) {
            storeLogs.close();
        }
        if (sdkLogs != null) {
            sdkLogs.close();
        }
    }

    @Test
    void memoryClientNeverEmitsSdkLogsAtDebugEvenOnErrorsButDocumentHooksStillWork() throws Exception {
        MilvusServiceClient memory = (MilvusServiceClient) ReflectionTestUtils.getField(service, "memoryClient");
        MilvusServiceClient document = (MilvusServiceClient) ReflectionTestUtils.getField(
                underlyingStore(service, EmbeddingStoreType.EN_CHARACTER_DOCUMENT), "milvusClient");
        assertTrue(Modifier.isPrivate(memory.getClass().getModifiers()));
        assertNotSame(memory, document);
        LogLevel oldMemoryLevel = (LogLevel) ReflectionTestUtils.getField(memory, "logLevel");
        LogLevel oldDocumentLevel = (LogLevel) ReflectionTestUtils.getField(document, "logLevel");
        try {
            memory.setLogLevel(LogLevel.Debug);
            document.setLogLevel(LogLevel.Debug);
            assertTrue(((Logger) LoggerFactory.getLogger("io.milvus")).isDebugEnabled());
            assertTrue(((Logger) LoggerFactory.getLogger(AbstractMilvusGrpcClient.class)).isDebugEnabled());
            sdkLogs.clear();
            MemoryScope scope = scope(EmbeddingStoreType.EN_LONG_TERM_MEMORY);
            MemoryDocument record = document(scope, MemoryDocument.Kind.EPISODE_SUMMARY, false);
            TextSegment encoded = CODEC.encode(record);
            store(scope).addAll(List.of(record.id()), List.of(VECTOR), List.of(encoded));
            assertRoundTrip(service, record, encoded);
            assertEquals(Set.of(record.id()), searchIds(scope, scope.filter()));
            store(scope).removeAll(scope.filter());
            assertTrue(
                    service.get(scope.storeType(), record.id(), scope.filter()).isEmpty());

            // Real server-side parse errors exercise SDK error paths without logging exception payloads.
            QueryParam badQuery = QueryParam.newBuilder()
                    .withCollectionName(scope.storeType().text())
                    .withExpr("memory_id == \"" + scope.chatId() + "\" and (")
                    .withOutFields(List.of("id"))
                    .build();
            assertNotEquals(0, memory.query(badQuery).getStatus());
            assertNotEquals(
                    0, memory.queryAsync(badQuery).get(30, TimeUnit.SECONDS).getStatus());
            assertNotEquals(
                    0,
                    memory.delete(DeleteParam.newBuilder()
                                    .withCollectionName(scope.storeType().text())
                                    .withExpr("memory_id == \"" + scope.chatId() + "\" and (")
                                    .build())
                            .getStatus());
            assertThrows(
                    RuntimeException.class,
                    () -> store(scope)
                            .addAll(List.of(uuid()), List.of(Embedding.from(new float[] {1, 0})), List.of(encoded)));
            assertThrows(
                    RuntimeException.class,
                    () -> store(scope)
                            .search(EmbeddingSearchRequest.builder()
                                    .queryEmbedding(Embedding.from(new float[] {1, 0}))
                                    .filter(scope.filter())
                                    .maxResults(1)
                                    .build()));

            List<String> hooks = List.of("logDebug", "logInfo", "logWarning", "logError");
            for (String hook : hooks) {
                // Check all four overridden dispatch points, including those used by async/retry paths.
                assertEquals(
                        memory.getClass(),
                        memory.getClass()
                                .getDeclaredMethod(hook, String.class, Object[].class)
                                .getDeclaringClass());
                invokeHook(memory, hook);
            }
            assertTrue(
                    sdkLogs.events().isEmpty(), "Memory client emitted SDK logs; event bodies intentionally omitted");
            for (String hook : hooks) {
                invokeHook(document, hook);
            }
            assertEquals(
                    List.of(Level.DEBUG, Level.INFO, Level.WARN, Level.ERROR),
                    sdkLogs.events().stream().map(ILoggingEvent::getLevel).toList());
            assertTrue(sdkLogs.events().stream()
                    .allMatch(event -> event.getFormattedMessage().equals("innocuous document logging diagnostic")));
        } finally {
            memory.setLogLevel(oldMemoryLevel);
            document.setLogLevel(oldDocumentLevel);
            sdkLogs.clear();
        }
    }

    @Test
    void productionXmlDisablesOnlyTransportFramesNotMilvusSdkDiagnostics() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        try (var input = MilvusMemoryStoreIT.class.getResourceAsStream("/logback-spring.xml")) {
            assertTrue(input != null, "Production logging configuration must be on the test classpath");
            var loggers = factory.newDocumentBuilder().parse(input).getElementsByTagName("logger");
            int transport = 0;
            for (int index = 0; index < loggers.getLength(); index++) {
                var logger = (org.w3c.dom.Element) loggers.item(index);
                String name = logger.getAttribute("name");
                if (name.equals(TRANSPORT_LOGGER)) {
                    transport++;
                    assertEquals("off", logger.getAttribute("level").toLowerCase(java.util.Locale.ROOT));
                }
                assertTrue(
                        !name.startsWith("io.milvus")
                                || !logger.getAttribute("level").equalsIgnoreCase("off"),
                        "Memory privacy must not globally disable independent document SDK logging");
            }
            assertEquals(1, transport);
        }
    }

    private static void invokeHook(MilvusServiceClient client, String hook) throws Exception {
        Method method = AbstractMilvusGrpcClient.class.getDeclaredMethod(hook, String.class, Object[].class);
        method.setAccessible(true);
        method.invoke(client, "innocuous document logging diagnostic", new Object[0]);
    }

    @Test
    void memoryStoresShareStrongClientWithoutChangingDocumentDefaults() {
        Object sharedClient = ReflectionTestUtils.getField(service, "memoryClient");
        for (EmbeddingStoreType type : EmbeddingStoreType.values()) {
            Object store = underlyingStore(service, type);
            assertEquals(false, ReflectionTestUtils.getField(store, "autoFlushOnInsert"));
            assertEquals(false, ReflectionTestUtils.getField(store, "retrieveEmbeddingsOnSearch"));
            assertEquals(
                    MemoryScope.isMemoryStore(type) ? ConsistencyLevelEnum.STRONG : ConsistencyLevelEnum.EVENTUALLY,
                    ReflectionTestUtils.getField(store, "consistencyLevel"));
            if (MemoryScope.isMemoryStore(type)) {
                assertSame(sharedClient, ReflectionTestUtils.getField(store, "milvusClient"));
            } else {
                assertNotSame(sharedClient, ReflectionTestUtils.getField(store, "milvusClient"));
                assertThrows(
                        IllegalArgumentException.class,
                        () -> service.get(
                                type,
                                uuid(),
                                scope(EmbeddingStoreType.EN_LONG_TERM_MEMORY).filter()));
            }
        }
    }

    @ParameterizedTest
    @EnumSource(
            value = EmbeddingStoreType.class,
            names = {"EN_LONG_TERM_MEMORY", "ZH_LONG_TERM_MEMORY", "DEFAULT_LONG_TERM_MEMORY"})
    void immediateExactReadsRoundTripStructuredRecordsWithoutFlushOrAnn(EmbeddingStoreType type) {
        MemoryScope scope = scope(type);
        List<MemoryDocument> documents = new ArrayList<>();
        for (MemoryDocument.Kind kind : MemoryDocument.Kind.values()) {
            documents.add(document(scope, kind, false));
        }
        documents.add(document(scope, MemoryDocument.Kind.WINDOW_SUMMARY, true));
        List<TextSegment> segments = documents.stream().map(CODEC::encode).toList();
        store(scope)
                .addAll(
                        documents.stream().map(MemoryDocument::id).toList(),
                        Collections.nCopies(documents.size(), VECTOR),
                        segments);

        // The very next operation is a known-UUID scalar query, not search, flush, sleep or retry.
        for (int index = 0; index < documents.size(); index++) {
            assertRoundTrip(service, documents.get(index), segments.get(index));
        }
        assertTrue(service.get(type, uuid(), scope.filter()).isEmpty());
    }

    @Test
    void everyScopeDimensionAndLegacyMetadataAreIsolatedEvenWithIdenticalVectors() {
        MemoryScope scope = scope(EmbeddingStoreType.EN_LONG_TERM_MEMORY);
        MemoryDocument first = document(scope, MemoryDocument.Kind.EPISODE_SUMMARY, false);
        MemoryDocument second = document(scope, MemoryDocument.Kind.WINDOW_SUMMARY, true);
        List<String> ids = new ArrayList<>(List.of(first.id(), second.id()));
        List<TextSegment> segments = new ArrayList<>(List.of(CODEC.encode(first), CODEC.encode(second)));
        Map<String, Object> otherValues = Map.of(
                "memory_id",
                "other-chat",
                "user_id",
                "other-owner",
                "character_uid",
                "other-character",
                "generation",
                scope.generation() + 1,
                "store_type",
                EmbeddingStoreType.ZH_LONG_TERM_MEMORY.text(),
                "schema_version",
                MemoryScope.SCHEMA_VERSION + 1);
        List<Filter> otherScopes = new ArrayList<>();
        for (var entry : otherValues.entrySet()) {
            MemoryDocument foreign = document(scope, MemoryDocument.Kind.EPISODE_SUMMARY, false);
            TextSegment encoded = CODEC.encode(foreign);
            Map<String, Object> metadata = new HashMap<>(encoded.metadata().toMap());
            metadata.put(entry.getKey(), entry.getValue());
            ids.add(foreign.id());
            segments.add(TextSegment.from(encoded.text(), Metadata.from(metadata)));
            Map<String, Object> changedScope = new HashMap<>(scope.metadata().toMap());
            changedScope.put(entry.getKey(), entry.getValue());
            otherScopes.add(equalities(changedScope));
        }
        String legacyId = uuid();
        ids.add(legacyId);
        segments.add(TextSegment.from("[]", new Metadata().put("memory_id", scope.chatId())));
        store(scope).addAll(ids, Collections.nCopies(ids.size(), VECTOR), segments);

        assertRoundTrip(service, first, segments.getFirst());
        assertEquals(Set.of(first.id(), second.id()), searchIds(scope, scope.filter()));
        for (int index = 0; index < otherScopes.size(); index++) {
            String foreignId = ids.get(index + 2);
            Filter foreignScope = otherScopes.get(index);
            assertTrue(service.get(scope.storeType(), foreignId, scope.filter()).isEmpty());
            assertTrue(service.get(scope.storeType(), first.id(), foreignScope).isEmpty());
            // A positive read/search proves the mismatched fixture really exists in Milvus.
            assertTrue(service.get(scope.storeType(), foreignId, foreignScope).isPresent());
            assertEquals(Set.of(foreignId), searchIds(scope, foreignScope));
        }
        assertTrue(service.get(scope.storeType(), legacyId, scope.filter()).isEmpty());
        assertTrue(service.get(
                        scope.storeType(), legacyId, metadataKey("memory_id").isEqualTo(scope.chatId()))
                .isPresent());
    }

    @Test
    void scopedDeletionIsImmediatelyVisibleAndPreservesOtherChatsAndGenerations() {
        MemoryScope scope = scope(EmbeddingStoreType.EN_LONG_TERM_MEMORY);
        MemoryScope otherChat = new MemoryScope(
                "other-" + scope.chatId().substring(0, 20),
                scope.userId(),
                scope.characterUid(),
                scope.generation(),
                scope.storeType());
        MemoryScope otherGeneration = new MemoryScope(
                scope.chatId(), scope.userId(), scope.characterUid(), scope.generation() + 1, scope.storeType());
        List<MemoryDocument> documents = List.of(
                document(scope, MemoryDocument.Kind.EPISODE_SUMMARY, false),
                document(scope, MemoryDocument.Kind.WINDOW_SUMMARY, true),
                document(otherChat, MemoryDocument.Kind.EPISODE_SUMMARY, false),
                document(otherGeneration, MemoryDocument.Kind.EPISODE_SUMMARY, false));
        List<TextSegment> segments = documents.stream().map(CODEC::encode).toList();
        store(scope)
                .addAll(
                        documents.stream().map(MemoryDocument::id).toList(),
                        Collections.nCopies(documents.size(), VECTOR),
                        segments);
        for (int index = 0; index < documents.size(); index++) {
            assertRoundTrip(service, documents.get(index), segments.get(index));
        }

        // Each fixture has a unique immutable commit; first delete exactly that record within its scope.
        store(scope)
                .removeAll(scope.filter()
                        .and(metadataKey("commit_id")
                                .isEqualTo(documents.getFirst().commitId())));
        assertTrue(service.get(scope.storeType(), documents.getFirst().id(), scope.filter())
                .isEmpty());
        assertRoundTrip(service, documents.get(1), segments.get(1));
        store(scope).removeAll(scope.filter());
        assertTrue(service.get(scope.storeType(), documents.get(1).id(), scope.filter())
                .isEmpty());
        assertTrue(searchIds(scope, scope.filter()).isEmpty());
        for (int index = 2; index < documents.size(); index++) {
            assertRoundTrip(service, documents.get(index), segments.get(index));
            assertEquals(
                    Set.of(documents.get(index).id()),
                    searchIds(
                            documents.get(index).scope(),
                            documents.get(index).scope().filter()));
        }
    }

    @Test
    void freshReaderAndServiceReloadImmutableUuidsAfterWriterCloses() {
        MemoryScope scope = scope(EmbeddingStoreType.DEFAULT_LONG_TERM_MEMORY);
        MemoryDocument document = document(scope, MemoryDocument.Kind.PROFILE_SNAPSHOT, false);
        TextSegment encoded = CODEC.encode(document);
        MilvusEmbeddingStoreServiceImpl writer = newService();
        try {
            writer.of(scope.chatId(), scope.storeType())
                    .addAll(List.of(document.id()), List.of(VECTOR), List.of(encoded));
            assertRoundTrip(writer, document, encoded);
        } finally {
            closeService(writer);
        }

        // Independent client and reader: no writer cache or ANN query is involved in UUID recovery.
        MilvusServiceClient client = new MilvusServiceClient(ConnectParam.newBuilder()
                .withUri(MILVUS.getEndpoint())
                .withDatabaseName("default")
                .withConnectTimeout(10, TimeUnit.SECONDS)
                .withRpcDeadline(30, TimeUnit.SECONDS)
                .build());
        try {
            MilvusTextSegmentReader reader = new MilvusTextSegmentReader(
                    client, "default", scope.storeType().text());
            assertSegment(
                    document, encoded, reader.get(document.id(), scope.filter()).orElseThrow());
        } finally {
            client.close();
        }
        MilvusEmbeddingStoreServiceImpl reloaded = newService();
        try {
            assertRoundTrip(reloaded, document, encoded);
        } finally {
            closeService(reloaded);
        }
    }

    @Test
    void maximumSerializedRecordBytesRoundTripAndOneExtraByteIsRejected() {
        MemoryScope scope = scope(EmbeddingStoreType.ZH_LONG_TERM_MEMORY);
        MemoryDocument seed = document(scope, MemoryDocument.Kind.EPISODE_SUMMARY, false);
        MemoryDocument minimum = withSummary(seed, "a");
        int envelopeBytes = MemoryBounds.bytes(CODEC.encode(minimum).text()) - 1;
        int remaining = MemoryBounds.VECTOR_RECORD_BYTES - envelopeBytes;
        // JSON expands each control character to six ASCII bytes, while the raw summary stays < 8 KiB.
        String summary = "\u0001".repeat(remaining / 6) + "a".repeat(remaining % 6);
        MemoryDocument maximum = withSummary(seed, summary);
        TextSegment encoded = CODEC.encode(maximum);
        assertEquals(MemoryBounds.VECTOR_RECORD_BYTES, MemoryBounds.bytes(encoded.text()));
        assertTrue(MemoryBounds.bytes(summary) < MemoryBounds.COMPACT_TEXT_BYTES);
        assertThrows(IllegalArgumentException.class, () -> CODEC.encode(withSummary(seed, summary + "a")));
        store(scope).addAll(List.of(maximum.id()), List.of(VECTOR), List.of(encoded));
        assertRoundTrip(service, maximum, encoded);
    }

    @ParameterizedTest
    @EnumSource(
            value = EmbeddingStoreType.class,
            names = {"EN_LONG_TERM_MEMORY", "ZH_LONG_TERM_MEMORY", "DEFAULT_LONG_TERM_MEMORY"})
    void cleanupDeletesOnlySuppliedIdsWithinHistoricalScopeAndOriginalCommit(EmbeddingStoreType type) {
        MemoryScope scope = scope(type);
        MemoryDocument target = document(scope, MemoryDocument.Kind.EPISODE_SUMMARY, false);
        MemoryDocument sameScopeDifferentCommit = document(scope, MemoryDocument.Kind.WINDOW_SUMMARY, false);
        TextSegment encoded = CODEC.encode(target);
        List<String> ids = new ArrayList<>(List.of(target.id(), sameScopeDifferentCommit.id()));
        List<TextSegment> segments = new ArrayList<>(List.of(encoded, CODEC.encode(sameScopeDifferentCommit)));
        Map<String, Object> changed = Map.of(
                "memory_id",
                "another-chat",
                "user_id",
                "another-user",
                "character_uid",
                "another-character",
                "generation",
                scope.generation() + 1,
                "schema_version",
                MemoryScope.SCHEMA_VERSION + 1,
                "store_type",
                type == EmbeddingStoreType.ZH_LONG_TERM_MEMORY
                        ? EmbeddingStoreType.EN_LONG_TERM_MEMORY.text()
                        : EmbeddingStoreType.ZH_LONG_TERM_MEMORY.text());
        for (var entry : changed.entrySet()) {
            Map<String, Object> metadata = new HashMap<>(encoded.metadata().toMap());
            metadata.put(entry.getKey(), entry.getValue());
            ids.add(uuid());
            segments.add(TextSegment.from(encoded.text(), Metadata.from(metadata)));
        }
        String unlistedId = uuid();
        ids.add(unlistedId);
        segments.add(encoded); // Same scope and commit but not a requested UUID must survive.
        store(scope).addAll(ids, Collections.nCopies(ids.size(), VECTOR), segments);
        Filter originalCommit = scope.filter().and(metadataKey("commit_id").isEqualTo(target.commitId()));
        service.removeExact(type, ids.subList(0, ids.size() - 1), originalCommit);
        assertTrue(service.get(type, target.id(), scope.filter()).isEmpty());
        assertTrue(service.get(type, unlistedId, originalCommit).isPresent());
        for (int index = 1; index < ids.size() - 1; index++) {
            assertTrue(service.get(
                            type,
                            ids.get(index),
                            equalities(segments.get(index).metadata().toMap()))
                    .isPresent());
        }
        service.removeExact(type, List.of(target.id()), originalCommit); // Idempotent, no naked-ID fallback.
    }

    @ParameterizedTest
    @EnumSource(
            value = EmbeddingStoreType.class,
            names = {"EN_LONG_TERM_MEMORY", "ZH_LONG_TERM_MEMORY", "DEFAULT_LONG_TERM_MEMORY"})
    void legacyCleanupUsesActualMilvus24JsonTypesAndExcludesMixedStructuredAndForeignRows(EmbeddingStoreType type) {
        MemoryScope scope = scope(type);
        List<JsonObject> fixtures = new ArrayList<>();
        // More malformed rows than a maximum page: these must be excluded before applying LIMIT.
        for (int index = 0; index < 110; index++) {
            JsonObject metadata = legacyMetadata(scope.chatId());
            metadata.addProperty("user_message_id", "1");
            fixtures.add(rawRow(uuid(), metadata));
        }
        for (String field : List.of("user_message_id", "ai_message_id")) {
            for (String value : List.of("null", "true", "false", "\"2\"", "0", "-1", "{}", "[]")) {
                JsonObject metadata = legacyMetadata(scope.chatId());
                metadata.add(field, JsonParser.parseString(value));
                fixtures.add(rawRow(uuid(), metadata));
            }
            JsonObject missing = legacyMetadata(scope.chatId());
            missing.remove(field);
            fixtures.add(rawRow(uuid(), missing));
        }
        for (String key : List.of(
                "schema_version", "commit_id", "generation", "record_kind", "user_id", "character_uid", "store_type")) {
            for (String value : List.of("null", "1", "\"foreign\"")) {
                JsonObject metadata = legacyMetadata(scope.chatId());
                metadata.add(key, JsonParser.parseString(value));
                fixtures.add(rawRow(uuid(), metadata));
            }
        }
        fixtures.add(rawRow(uuid(), legacyMetadata("another-chat")));
        insertRaw(type, fixtures);
        // Exercise the actual old writer: LC add generates UUIDs and writes exactly these three metadata keys.
        String legacy = store(scope)
                .add(
                        VECTOR,
                        TextSegment.from(
                                "[]",
                                new Metadata()
                                        .put("memory_id", scope.chatId())
                                        .put("user_message_id", Long.MAX_VALUE - 1)
                                        .put("ai_message_id", Long.MAX_VALUE)));
        JsonObject decimal = legacyMetadata(scope.chatId());
        decimal.addProperty("user_message_id", 1.5);
        decimal.addProperty("ai_message_id", 2.5);
        String decimalId = uuid();
        insertRaw(type, List.of(rawRow(decimalId, decimal)));
        assertEquals(2, service.legacyIds(type, scope.chatId(), 100).size());
        assertEquals(Set.of(legacy, decimalId), Set.copyOf(service.legacyIds(type, scope.chatId(), 100)));
        assertEquals(1, service.legacyIds(type, scope.chatId(), 1).size());

        // Even explicitly supplied malformed/structured/foreign UUIDs must not be deleted.
        List<String> protectedIds =
                fixtures.stream().map(row -> row.get("id").getAsString()).toList();
        for (int start = 0; start < protectedIds.size(); start += 100) {
            List<String> batch = protectedIds.subList(start, Math.min(start + 100, protectedIds.size()));
            service.removeLegacy(type, scope.chatId(), batch);
            assertEquals(Set.copyOf(batch), existingIds(type, batch));
        }
        service.removeLegacy(type, scope.chatId(), List.of(legacy, decimalId));
        assertTrue(service.legacyIds(type, scope.chatId(), 100).isEmpty());
        assertTrue(existingIds(type, List.of(legacy, decimalId)).isEmpty());
    }

    @Test
    void legacyCleanupDrainsBoundedNoOffsetPagesAndRechecksMetadataChangedAfterDiscovery() {
        MemoryScope scope = scope(EmbeddingStoreType.EN_LONG_TERM_MEMORY);
        List<JsonObject> fixtures = new ArrayList<>();
        for (int index = 0; index < 103; index++) {
            fixtures.add(rawRow(uuid(), legacyMetadata(scope.chatId())));
        }
        insertRaw(scope.storeType(), fixtures);
        assertEquals(
                100, service.legacyIds(scope.storeType(), scope.chatId(), 100).size());
        List<String> firstPage = service.legacyIds(scope.storeType(), scope.chatId(), 37);
        String changedId = firstPage.getFirst();
        JsonObject nowStructured = legacyMetadata(scope.chatId());
        nowStructured.addProperty("commit_id", uuid());
        var result = memoryClient()
                .upsert(UpsertParam.newBuilder()
                        .withDatabaseName("default")
                        .withCollectionName(scope.storeType().text())
                        .withRows(List.of(rawRow(changedId, nowStructured)))
                        .build());
        assertEquals(0, result.getStatus());
        service.removeLegacy(scope.storeType(), scope.chatId(), firstPage);
        assertEquals(Set.of(changedId), existingIds(scope.storeType(), firstPage));
        int removed = firstPage.size() - 1;
        for (int pageNumber = 0; pageNumber < 3; pageNumber++) {
            List<String> page = service.legacyIds(scope.storeType(), scope.chatId(), 37);
            if (page.isEmpty()) {
                break;
            }
            assertTrue(page.size() <= 37);
            service.removeLegacy(scope.storeType(), scope.chatId(), page);
            removed += page.size();
        }
        assertEquals(102, removed);
        assertTrue(service.legacyIds(scope.storeType(), scope.chatId(), 37).isEmpty());
        assertEquals(Set.of(changedId), existingIds(scope.storeType(), List.of(changedId)));
    }

    @Test
    void cleanupRejectsInvalidInputsAndEscapesIdentifiersWithoutSdkPayloadLogs() {
        MemoryScope scope = new MemoryScope(
                "x\\\" or id != \"", "owner\\\"", "角色\\\"", 8, EmbeddingStoreType.DEFAULT_LONG_TERM_MEMORY);
        MemoryDocument record = document(scope, MemoryDocument.Kind.EPISODE_SUMMARY, false);
        MemoryDocument foreign = document(
                scope(EmbeddingStoreType.DEFAULT_LONG_TERM_MEMORY), MemoryDocument.Kind.EPISODE_SUMMARY, false);
        store(scope)
                .addAll(
                        List.of(record.id(), foreign.id()),
                        List.of(VECTOR, VECTOR),
                        List.of(CODEC.encode(record), CODEC.encode(foreign)));
        String legacy = store(scope)
                .add(
                        VECTOR,
                        TextSegment.from(
                                "[]",
                                new Metadata()
                                        .put("memory_id", scope.chatId())
                                        .put("user_message_id", 1L)
                                        .put("ai_message_id", 2L)));
        sdkLogs.clear();
        assertEquals(List.of(legacy), service.legacyIds(scope.storeType(), scope.chatId(), 1));
        Filter scopeAndCommit = scope.filter().and(metadataKey("commit_id").isEqualTo(record.commitId()));
        for (EmbeddingStoreType type : EmbeddingStoreType.values()) {
            if (!MemoryScope.isMemoryStore(type)) {
                assertThrows(
                        IllegalArgumentException.class,
                        () -> service.removeExact(type, List.of(record.id()), scopeAndCommit));
                assertThrows(IllegalArgumentException.class, () -> service.legacyIds(type, scope.chatId(), 1));
                assertThrows(
                        IllegalArgumentException.class,
                        () -> service.removeLegacy(type, scope.chatId(), List.of(legacy)));
            }
        }
        assertThrows(
                IllegalArgumentException.class, () -> service.removeExact(null, List.of(record.id()), scopeAndCommit));
        assertThrows(IllegalArgumentException.class, () -> service.legacyIds(null, scope.chatId(), 1));
        assertThrows(IllegalArgumentException.class, () -> service.removeLegacy(null, scope.chatId(), List.of(legacy)));
        assertThrows(
                IllegalArgumentException.class,
                () -> service.removeExact(scope.storeType(), List.of(record.id()), scope.filter()));
        assertThrows(
                IllegalArgumentException.class,
                () -> service.removeLegacy(scope.storeType(), scope.chatId(), List.of("\") or id != (\"")));
        assertThrows(IllegalArgumentException.class, () -> service.legacyIds(scope.storeType(), scope.chatId(), 101));
        service.removeExact(scope.storeType(), List.of(record.id(), foreign.id(), legacy), scopeAndCommit);
        assertTrue(service.get(scope.storeType(), record.id(), scope.filter()).isEmpty());
        assertTrue(service.get(scope.storeType(), foreign.id(), foreign.scope().filter())
                .isPresent());
        service.removeLegacy(scope.storeType(), scope.chatId(), List.of(legacy, foreign.id()));
        assertTrue(service.legacyIds(scope.storeType(), scope.chatId(), 1).isEmpty());
        assertTrue(service.get(scope.storeType(), foreign.id(), foreign.scope().filter())
                .isPresent());
        assertTrue(sdkLogs.events().isEmpty(), "Cleanup emitted SDK logs; event bodies intentionally omitted");
    }

    @Test
    void cleanupSanitizesRealServerErrorsWithoutClosingOrLoggingFromBorrowedClient() {
        MemoryScope scope = scope(EmbeddingStoreType.EN_LONG_TERM_MEMORY);
        MilvusMemoryCleanupAdapter missingDatabase = new MilvusMemoryCleanupAdapter(
                memoryClient(),
                "missing_" + uuid().replace("-", ""),
                scope.storeType().text());
        Filter scopeAndCommit = scope.filter().and(metadataKey("commit_id").isEqualTo(uuid()));
        sdkLogs.clear();
        List<Runnable> operations = List.of(
                () -> missingDatabase.legacyIds(scope.chatId(), 1),
                () -> missingDatabase.removeLegacy(scope.chatId(), List.of(uuid())),
                () -> missingDatabase.removeExact(List.of(uuid()), scopeAndCommit));
        for (Runnable operation : operations) {
            IllegalStateException error = assertThrows(IllegalStateException.class, operation::run);
            assertEquals("Milvus memory cleanup failed", error.getMessage());
            assertTrue(error.getCause() == null);
            assertEquals(0, error.getSuppressed().length);
        }
        assertTrue(service.legacyIds(scope.storeType(), scope.chatId(), 1).isEmpty());
        assertTrue(sdkLogs.events().isEmpty(), "Cleanup errors emitted SDK logs; event bodies intentionally omitted");
    }

    private static MilvusServiceClient memoryClient() {
        return (MilvusServiceClient) ReflectionTestUtils.getField(service, "memoryClient");
    }

    private static JsonObject legacyMetadata(String chatId) {
        JsonObject metadata = new JsonObject();
        metadata.addProperty("memory_id", chatId);
        metadata.addProperty("user_message_id", 1L);
        metadata.addProperty("ai_message_id", 2L);
        return metadata;
    }

    private static JsonObject rawRow(String id, JsonObject metadata) {
        JsonObject row = new JsonObject();
        row.addProperty("id", id);
        row.addProperty("text", "[]");
        row.add("vector", JsonParser.parseString("[1,0,0,0]"));
        row.add("metadata", metadata);
        return row;
    }

    private static void insertRaw(EmbeddingStoreType type, List<JsonObject> rows) {
        var result = memoryClient()
                .insert(InsertParam.newBuilder()
                        .withDatabaseName("default")
                        .withCollectionName(type.text())
                        .withRows(rows)
                        .build());
        assertEquals(0, result.getStatus());
    }

    private static Set<String> existingIds(EmbeddingStoreType type, List<String> ids) {
        String expression = "id in [" + ids.stream().map(id -> "\"" + id + "\"").collect(Collectors.joining(",")) + "]";
        var result = memoryClient()
                .query(QueryParam.newBuilder()
                        .withDatabaseName("default")
                        .withCollectionName(type.text())
                        .withExpr(expression)
                        .withOutFields(List.of("id", "metadata"))
                        .withLimit(100L)
                        .withConsistencyLevel(ConsistencyLevelEnum.STRONG)
                        .build());
        assertEquals(0, result.getStatus());
        return new QueryResultsWrapper(result.getData())
                .getRowRecords().stream().map(row -> (String) row.get("id")).collect(Collectors.toSet());
    }

    private static MemoryDocumentCodec codec() {
        LongTermMemoryProperties properties = new LongTermMemoryProperties();
        properties.setSummaryMaxTokens(4096);
        properties.afterPropertiesSet();
        TokenCountEstimator estimator = new TokenCountEstimator() {
            @Override
            public int estimateTokenCountInText(String text) {
                return (text.codePointCount(0, text.length()) + 3) / 4;
            }

            @Override
            public int estimateTokenCountInMessage(ChatMessage message) {
                throw new AssertionError("Storage tests must not estimate chat messages");
            }

            @Override
            public int estimateTokenCountInMessages(Iterable<ChatMessage> messages) {
                throw new AssertionError("Storage tests must not estimate chat messages");
            }
        };
        return new MemoryDocumentCodec(new MemoryBounds(estimator, properties), properties);
    }

    private static MilvusEmbeddingStoreServiceImpl newService() {
        MilvusEmbeddingStoreServiceImpl result = new MilvusEmbeddingStoreServiceImpl();
        ReflectionTestUtils.setField(result, "url", MILVUS.getEndpoint());
        ReflectionTestUtils.setField(result, "retrieveEmbeddingsOnSearch", false);
        ReflectionTestUtils.setField(result, "embeddingModelService", new EmbeddingModelService() {
            @Override
            public EmbeddingModel modelForLang(String lang) {
                throw new AssertionError("Storage tests must not load embedding models");
            }

            @Override
            public TokenCountEstimator tokenCountEstimatorForLang(String lang) {
                throw new AssertionError("Storage tests must not load tokenizers");
            }

            @Override
            public String queryPrefixForLang(String lang) {
                throw new AssertionError("Exact UUID reads must not construct model queries");
            }

            @Override
            public int dimensionForLang(String lang) {
                return 4;
            }
        });
        result.init();
        return result;
    }

    private static void closeService(MilvusEmbeddingStoreServiceImpl instance) {
        if (instance != null) {
            instance.closeMemoryClient();
            // Document stores retain their independently owned default clients; also release those in this test.
            for (EmbeddingStoreType type : EmbeddingStoreType.values()) {
                if (!MemoryScope.isMemoryStore(type)) {
                    ((MilvusServiceClient)
                                    ReflectionTestUtils.getField(underlyingStore(instance, type), "milvusClient"))
                            .close();
                }
            }
        }
    }

    private static Object underlyingStore(MilvusEmbeddingStoreServiceImpl instance, EmbeddingStoreType type) {
        return ReflectionTestUtils.getField(instance.of(null, type), "embeddingStore");
    }

    private static EmbeddingStore<TextSegment> store(MemoryScope scope) {
        return service.of(scope.chatId(), scope.storeType());
    }

    private static Set<String> searchIds(MemoryScope scope, Filter filter) {
        List<EmbeddingMatch<TextSegment>> matches = store(scope)
                .search(EmbeddingSearchRequest.builder()
                        .queryEmbedding(VECTOR)
                        .filter(filter)
                        .maxResults(20)
                        .minScore(0.99)
                        .build())
                .matches();
        assertTrue(
                matches.stream().allMatch(match -> filter.test(match.embedded().metadata())),
                "ANN returned metadata outside the requested scope");
        Set<String> ids = matches.stream().map(EmbeddingMatch::embeddingId).collect(Collectors.toSet());
        assertEquals(ids.size(), matches.size(), "ANN returned duplicate immutable IDs");
        return ids;
    }

    private static Filter equalities(Map<String, Object> metadata) {
        return metadata.entrySet().stream()
                .<Filter>map(entry -> new IsEqualTo(entry.getKey(), entry.getValue()))
                .reduce((left, right) -> left.and(right))
                .orElseThrow();
    }

    private static void assertRoundTrip(
            MilvusEmbeddingStoreServiceImpl reader, MemoryDocument expected, TextSegment encoded) {
        TextSegment actual = reader.get(
                        expected.scope().storeType(),
                        expected.id(),
                        expected.scope().filter())
                .orElseThrow(() -> new AssertionError("Inserted immutable UUID was not immediately visible"));
        assertSegment(expected, encoded, actual);
    }

    private static void assertSegment(MemoryDocument expected, TextSegment encoded, TextSegment actual) {
        // Boolean equality prevents JUnit failure reports from dumping bodies or fact values.
        assertTrue(encoded.text().equals(actual.text()), "Serialized record changed in storage");
        String hash = MemoryDocumentCodec.hash(encoded.text());
        assertEquals(hash, MemoryDocumentCodec.hash(actual.text()));
        assertEquals(Long.MAX_VALUE - 1, actual.metadata().getLong("source_start_id"));
        assertEquals(Long.MAX_VALUE, actual.metadata().getLong("source_end_id"));
        assertTrue(
                expected.equals(CODEC.decode(actual, expected.scope(), expected.id(), hash)),
                "Structured record, scope or provenance changed in storage");
        assertThrows(
                IllegalArgumentException.class,
                () -> CODEC.decode(actual, expected.scope(), expected.id(), "0".repeat(64)));
    }

    private static MemoryScope scope(EmbeddingStoreType type) {
        return new MemoryScope(uuid().replace("-", ""), "用户9007199254740993", "角色-é", 7, type);
    }

    private static MemoryDocument document(MemoryScope scope, MemoryDocument.Kind kind, boolean archive) {
        boolean facts =
                kind == MemoryDocument.Kind.PROFILE_SNAPSHOT || kind == MemoryDocument.Kind.EXTRACTION_CHECKPOINT;
        return new MemoryDocument(
                uuid(),
                scope,
                uuid(),
                kind,
                archive,
                Long.MAX_VALUE - 1,
                Long.MAX_VALUE,
                OBSERVED,
                MemoryDocumentCodec.hash("deterministic-fixture"),
                kind == MemoryDocument.Kind.PROFILE_SNAPSHOT ? "" : "Résumé 中文 \"quoted\" \\ line\nnext",
                facts
                        ? List.of(new MemoryDocument.Fact(
                                "tea_preference", "绿茶 café", List.of(Long.MAX_VALUE - 1), OBSERVED, false))
                        : List.of(),
                facts
                        ? List.of(new MemoryDocument.Fact(
                                "story_state", "抵达天文台", List.of(Long.MAX_VALUE), OBSERVED, true))
                        : List.of());
    }

    private static MemoryDocument withSummary(MemoryDocument document, String summary) {
        return new MemoryDocument(
                document.id(),
                document.scope(),
                document.commitId(),
                document.kind(),
                document.archive(),
                document.sourceStartId(),
                document.sourceEndId(),
                document.observedAt(),
                document.fingerprint(),
                summary,
                document.userFacts(),
                document.characterDeltas());
    }

    private static String uuid() {
        return UUID.randomUUID().toString();
    }

    /** Detach (never stop) existing appenders and restore everything even after a failed assertion. */
    private static final class LogCapture implements AutoCloseable {
        private final ListAppender<ILoggingEvent> appender = new ListAppender<>();
        private final Map<Logger, LoggerState> saved = new HashMap<>();

        private LogCapture(Level level, String... names) {
            appender.list = Collections.synchronizedList(new ArrayList<>());
            appender.start();
            for (String name : names) {
                Logger logger = (Logger) LoggerFactory.getLogger(name);
                List<Appender<ILoggingEvent>> appenders = new ArrayList<>();
                logger.iteratorForAppenders().forEachRemaining(appenders::add);
                saved.put(logger, new LoggerState(logger.getLevel(), logger.isAdditive(), appenders));
                appenders.forEach(logger::detachAppender);
                logger.setAdditive(false);
                logger.setLevel(level);
                logger.addAppender(appender);
            }
        }

        private List<ILoggingEvent> events() {
            synchronized (appender.list) {
                return List.copyOf(appender.list);
            }
        }

        private void clear() {
            appender.list.clear();
        }

        @Override
        public void close() {
            saved.forEach((logger, state) -> {
                logger.detachAppender(appender);
                state.appenders().forEach(logger::addAppender);
                logger.setLevel(state.level());
                logger.setAdditive(state.additive());
            });
            saved.clear();
            appender.stop();
            clear();
        }

        private record LoggerState(Level level, boolean additive, List<Appender<ILoggingEvent>> appenders) {}
    }
}
