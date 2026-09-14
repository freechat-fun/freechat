package fun.freechat.langchain4j.store.embedding;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.protobuf.ByteString;
import dev.langchain4j.store.embedding.filter.Filter;
import dev.langchain4j.store.embedding.filter.comparison.IsEqualTo;
import dev.langchain4j.store.embedding.filter.comparison.IsGreaterThan;
import io.milvus.common.clientenum.ConsistencyLevelEnum;
import io.milvus.grpc.DataType;
import io.milvus.grpc.ErrorCode;
import io.milvus.grpc.FieldData;
import io.milvus.grpc.JSONArray;
import io.milvus.grpc.MutationResult;
import io.milvus.grpc.QueryResults;
import io.milvus.grpc.ScalarField;
import io.milvus.grpc.Status;
import io.milvus.grpc.StringArray;
import io.milvus.param.R;
import io.milvus.param.dml.DeleteParam;
import io.milvus.param.dml.QueryParam;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class MilvusMemoryCleanupAdapterTest {
    private static final String ID = "12345678-1234-1234-abcd-123456789abc";
    private static final String OTHER_ID = "00000000-0000-0000-0000-000000000001";
    private static final String COLLECTION = "en_long_term_memory";
    private static final String PRIVATE = "private payload and credentials";
    private static final Map<String, Object> SCOPE = Map.of(
            "memory_id",
            "chat",
            "user_id",
            "owner",
            "character_uid",
            "character",
            "generation",
            7L,
            "store_type",
            COLLECTION,
            "schema_version",
            1,
            "commit_id",
            ID);
    private static final String LEGACY = "{\"memory_id\":\"chat\",\"user_message_id\":1,\"ai_message_id\":2}";

    @Test
    void exactDeleteUsesOneServerExpressionContainingAllIdsScopeAndOriginalCommitWithoutQuery() {
        Boundary boundary = new Boundary();
        boundary.adapter().removeExact(List.of(ID, OTHER_ID), filter(SCOPE));
        assertEquals(0, boundary.queries);
        assertEquals(1, boundary.deletes);
        assertEquals("memory_db", boundary.deletion.getDatabaseName());
        assertEquals(COLLECTION, boundary.deletion.getCollectionName());
        assertEquals(
                "(id in [\"" + ID + "\", \"" + OTHER_ID + "\"]) and (" + MilvusTextSegmentReader.mapScope(filter(SCOPE))
                        + ")",
                boundary.deletion.getExpr());
        // Idempotent zero-match mutations are successful too; never fall back to naked IDs.
        boundary.adapter().removeExact(List.of(ID), filter(SCOPE));
        assertEquals(0, boundary.queries);
        assertEquals(2, boundary.deletes);
    }

    @Test
    void legacyDiscoveryRequestsOnlyMetadataAndIdsWithStrongBoundedNoOffsetPage() {
        Boundary boundary = new Boundary();
        boundary.rows = R.success(rows(List.of(ID), List.of(LEGACY)));
        assertEquals(List.of(ID), boundary.adapter().legacyIds("chat", 100));
        assertEquals(1, boundary.queries);
        assertEquals(0, boundary.deletes);
        assertEquals("memory_db", boundary.query.getDatabaseName());
        assertEquals(COLLECTION, boundary.query.getCollectionName());
        assertEquals(List.of("id", "metadata"), boundary.query.getOutFields());
        assertEquals(ConsistencyLevelEnum.STRONG, boundary.query.getConsistencyLevel());
        assertEquals(100L, boundary.query.getLimit());
        assertEquals(0L, boundary.query.getOffset());
        assertFalse(boundary.query.isIgnoreGrowing());
        String predicate = boundary.query.getExpr();
        assertTrue(predicate.startsWith("metadata[\"memory_id\"] == \"chat\""));
        for (String key : List.of(
                "schema_version", "commit_id", "generation", "record_kind", "user_id", "character_uid", "store_type")) {
            assertTrue(predicate.contains("and (not exists metadata[\"" + key + "\"])"));
        }
        assertTrue(predicate.contains("and (metadata[\"user_message_id\"] > 0)"));
        assertTrue(predicate.contains("and (metadata[\"ai_message_id\"] > 0)"));
        boundary.adapter().removeLegacy("chat", List.of(ID));
        assertEquals(1, boundary.queries);
        assertEquals(1, boundary.deletes);
        assertEquals("(id in [\"" + ID + "\"]) and (" + predicate + ")", boundary.deletion.getExpr());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(
            strings = {
                "",
                "1-1-1-1-1",
                "12345678-1234-1234-ABCD-123456789ABC",
                "12345678-1234-1234-abcd-123456789abz",
                "\") or id != (\""
            })
    void rejectsNoncanonicalUuidBatchesBeforeNetwork(String id) {
        Boundary boundary = new Boundary();
        List<String> ids = Arrays.asList(ID, id);
        assertThrows(IllegalArgumentException.class, () -> boundary.adapter().removeExact(ids, filter(SCOPE)));
        assertThrows(IllegalArgumentException.class, () -> boundary.adapter().removeLegacy("chat", ids));
        assertNoNetwork(boundary);
    }

    @Test
    void rejectsEmptyNullOversizedBatchesAndInvalidLimitsBeforeNetwork() {
        Boundary boundary = new Boundary();
        for (List<String> ids : Arrays.<List<String>>asList(null, List.of(), Collections.nCopies(101, ID))) {
            assertThrows(
                    IllegalArgumentException.class, () -> boundary.adapter().removeExact(ids, filter(SCOPE)));
            assertThrows(
                    IllegalArgumentException.class, () -> boundary.adapter().removeLegacy("chat", ids));
        }
        for (int limit : List.of(-1, 0, 101, Integer.MAX_VALUE)) {
            assertThrows(
                    IllegalArgumentException.class, () -> boundary.adapter().legacyIds("chat", limit));
        }
        assertNoNetwork(boundary);
        boundary.adapter().removeExact(Collections.nCopies(100, ID), filter(SCOPE));
        boundary.adapter().removeLegacy("chat", Collections.nCopies(100, ID));
        assertEquals(2, boundary.deletes);
    }

    @Test
    void rejectsEveryIncompleteScopeAndUnsupportedFilterBeforeNetwork() {
        Boundary boundary = new Boundary();
        for (String key : SCOPE.keySet()) {
            Map<String, Object> missing = new HashMap<>(SCOPE);
            missing.remove(key);
            assertThrows(
                    IllegalArgumentException.class, () -> boundary.adapter().removeExact(List.of(ID), filter(missing)));
        }
        Filter full = filter(SCOPE);
        for (Filter scope : Arrays.asList(
                null,
                full.or(full),
                Filter.not(full),
                full.and(new IsGreaterThan("generation", 0)),
                full.and(new IsEqualTo("generation", 7L)),
                (Filter) ignored -> true)) {
            assertThrows(
                    IllegalArgumentException.class, () -> boundary.adapter().removeExact(List.of(ID), scope));
        }
        assertNoNetwork(boundary);
    }

    @Test
    void rejectsInvalidScopeValuesAndCollectionMismatchBeforeNetwork() {
        Boundary boundary = new Boundary();
        Map<String, List<Object>> invalid = Map.of(
                "memory_id", List.of("", "chat-assist", "a".repeat(33), "chat\n", 1),
                "user_id", List.of(" ", "owner\t", 1),
                "character_uid", List.of("", "character\u0000", 1),
                "store_type", List.of("zh_long_term_memory", "en_character_document"),
                "generation", List.of(0, -1L, 1.5, "7"),
                "schema_version", List.of(0, -1L, 1.0, "1"),
                "commit_id", List.of("", ID.toUpperCase(java.util.Locale.ROOT), "1-1-1-1-1", 1));
        invalid.forEach((key, values) -> values.forEach(value -> {
            Map<String, Object> scope = new HashMap<>(SCOPE);
            scope.put(key, value);
            assertThrows(
                    IllegalArgumentException.class, () -> boundary.adapter().removeExact(List.of(ID), filter(scope)));
        }));
        assertNoNetwork(boundary);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "chat-assist", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", "chat\n", "chat\u0000"})
    void rejectsInvalidLegacyChatBeforeNetwork(String chat) {
        Boundary boundary = new Boundary();
        assertThrows(IllegalArgumentException.class, () -> boundary.adapter().legacyIds(chat, 1));
        assertThrows(IllegalArgumentException.class, () -> boundary.adapter().removeLegacy(chat, List.of(ID)));
        assertNoNetwork(boundary);
    }

    @Test
    void quotesIdentifierInjectionAndRetainsAdditionalEqualityConstraints() {
        Boundary boundary = new Boundary();
        String chat = "x\\\" or id != \"";
        boundary.adapter().legacyIds(chat, 1);
        assertTrue(boundary.query.getExpr().startsWith("metadata[\"memory_id\"] == \"x\\\\\\\" or id != \\\"\" and"));
        Map<String, Object> scope = new HashMap<>(SCOPE);
        scope.put("memory_id", chat);
        scope.put("user_id", "owner\\\"");
        scope.put("character_uid", "角色\\\"");
        Filter extended = filter(scope).and(new IsEqualTo("key\"\\]", "extra\\\""));
        boundary.adapter().removeExact(List.of(ID), extended);
        assertTrue(boundary.deletion.getExpr().endsWith("(" + MilvusTextSegmentReader.mapScope(extended) + ")"));
    }

    @Test
    void validatesEveryReturnedLegacyDiscriminatorIncludingNullBeforeReturningIds() {
        Boundary boundary = new Boundary();
        for (String key : List.of(
                "schema_version", "commit_id", "generation", "record_kind", "user_id", "character_uid", "store_type")) {
            for (String value : List.of("null", "1", "\"foreign\"")) {
                boundary.rows = R.success(rows(
                        List.of(OTHER_ID, ID),
                        List.of(LEGACY.replace("}", ",\"" + key + "\":" + value + "}"), LEGACY)));
                assertEquals(List.of(ID), boundary.adapter().legacyIds("chat", 2));
            }
        }
    }

    @Test
    void skipsForeignMalformedIdsAndNonNumericMessageIdsButKeepsValidRowsInPage() {
        Boundary boundary = new Boundary();
        for (String invalid : List.of("null", "true", "false", "\"1\"", "0", "-1", "{}", "[]", "1e999")) {
            for (String key : List.of("user_message_id", "ai_message_id")) {
                String bad = key.equals("user_message_id")
                        ? "{\"memory_id\":\"chat\",\"user_message_id\":" + invalid + ",\"ai_message_id\":2}"
                        : "{\"memory_id\":\"chat\",\"user_message_id\":1,\"ai_message_id\":" + invalid + "}";
                boundary.rows = R.success(rows(List.of(OTHER_ID, ID), List.of(bad, LEGACY)));
                assertEquals(List.of(ID), boundary.adapter().legacyIds("chat", 2));
            }
        }
        for (String metadata : List.of("{}", "{\"memory_id\":\"chat\"}", LEGACY.replace("chat", "foreign"))) {
            boundary.rows = R.success(rows(List.of(OTHER_ID, ID), List.of(metadata, LEGACY)));
            assertEquals(List.of(ID), boundary.adapter().legacyIds("chat", 2));
        }
        boundary.rows = R.success(rows(List.of("bad-id", ID, ID), List.of(LEGACY, LEGACY, LEGACY)));
        assertEquals(List.of(ID), boundary.adapter().legacyIds("chat", 3));
        boundary.rows = R.success(rows(List.of(ID), List.of(LEGACY.replace(":1,", ":1.5,"))));
        assertEquals(List.of(ID), boundary.adapter().legacyIds("chat", 1));
    }

    @Test
    void rejectsOversizedMalformedSdkResultsAndSanitizesQueryFailures() {
        Boundary boundary = new Boundary();
        for (R<QueryResults> response : Arrays.<R<QueryResults>>asList(
                null,
                new R<>(),
                R.failed(new IllegalStateException(PRIVATE)),
                R.failed(R.Status.PermissionDenied, PRIVATE),
                R.success(null),
                R.success(QueryResults.getDefaultInstance()),
                R.success(QueryResults.newBuilder().setStatus(badStatus()).build()),
                R.success(QueryResults.newBuilder()
                        .setStatus(Status.newBuilder().setCode(123).setReason(PRIVATE))
                        .build()),
                R.success(rows(List.of(ID, ID), List.of(LEGACY, LEGACY))),
                R.success(rows(List.of(ID), List.of("not valid json"))))) {
            boundary.rows = response;
            assertSanitized(() -> boundary.adapter().legacyIds("chat", 1));
        }
        boundary.failure = new IllegalStateException(PRIVATE);
        assertSanitized(() -> boundary.adapter().legacyIds("chat", 1));
    }

    @Test
    void sanitizesTransportAndBothDeleteStatusLayersAndPartialFailures() {
        Boundary boundary = new Boundary();
        for (R<MutationResult> response : Arrays.<R<MutationResult>>asList(
                null,
                new R<>(),
                R.failed(new IllegalStateException(PRIVATE)),
                R.failed(R.Status.PermissionDenied, PRIVATE),
                R.success(null),
                R.success(MutationResult.getDefaultInstance()),
                R.success(MutationResult.newBuilder().setStatus(badStatus()).build()),
                R.success(MutationResult.newBuilder()
                        .setStatus(Status.newBuilder().setCode(123).setReason(PRIVATE))
                        .build()),
                R.success(MutationResult.newBuilder()
                        .setStatus(Status.getDefaultInstance())
                        .addErrIndex(0)
                        .build()))) {
            boundary.mutation = response;
            assertSanitized(() -> boundary.adapter().removeExact(List.of(ID), filter(SCOPE)));
            assertSanitized(() -> boundary.adapter().removeLegacy("chat", List.of(ID)));
        }
        boundary.failure = new IllegalStateException(PRIVATE);
        assertSanitized(() -> boundary.adapter().removeExact(List.of(ID), filter(SCOPE)));
        assertSanitized(() -> boundary.adapter().removeLegacy("chat", List.of(ID)));
        assertEquals(0, boundary.queries);
    }

    private static void assertNoNetwork(Boundary boundary) {
        assertEquals(0, boundary.queries);
        assertEquals(0, boundary.deletes);
    }

    private static void assertSanitized(Runnable action) {
        IllegalStateException error = assertThrows(IllegalStateException.class, action::run);
        assertEquals("Milvus memory cleanup failed", error.getMessage());
        assertNull(error.getCause());
        assertEquals(0, error.getSuppressed().length);
    }

    private static Status badStatus() {
        return Status.newBuilder()
                .setErrorCode(ErrorCode.UnexpectedError)
                .setReason(PRIVATE)
                .build();
    }

    private static Filter filter(Map<String, Object> values) {
        return values.entrySet().stream()
                .<Filter>map(entry -> new IsEqualTo(entry.getKey(), entry.getValue()))
                .reduce((left, right) -> left.and(right))
                .orElseThrow();
    }

    private static QueryResults rows(List<String> ids, List<String> metadata) {
        return QueryResults.newBuilder()
                .setStatus(Status.getDefaultInstance())
                .addFieldsData(FieldData.newBuilder()
                        .setFieldName("id")
                        .setType(DataType.VarChar)
                        .setScalars(ScalarField.newBuilder()
                                .setStringData(StringArray.newBuilder().addAllData(ids))))
                .addFieldsData(FieldData.newBuilder()
                        .setFieldName("metadata")
                        .setType(DataType.JSON)
                        .setScalars(ScalarField.newBuilder()
                                .setJsonData(JSONArray.newBuilder()
                                        .addAllData(metadata.stream()
                                                .map(ByteString::copyFromUtf8)
                                                .toList()))))
                .build();
    }

    private static final class Boundary {
        private int queries;
        private int deletes;
        private QueryParam query;
        private DeleteParam deletion;
        private RuntimeException failure;
        private R<QueryResults> rows = R.success(
                QueryResults.newBuilder().setStatus(Status.getDefaultInstance()).build());
        private R<MutationResult> mutation = R.success(MutationResult.newBuilder()
                .setStatus(Status.getDefaultInstance())
                .build());

        private MilvusMemoryCleanupAdapter adapter() {
            return new MilvusMemoryCleanupAdapter(
                    request -> {
                        queries++;
                        query = request;
                        if (failure != null) {
                            throw failure;
                        }
                        return rows;
                    },
                    request -> {
                        deletes++;
                        deletion = request;
                        if (failure != null) {
                            throw failure;
                        }
                        return mutation;
                    },
                    "memory_db",
                    COLLECTION);
        }
    }
}
