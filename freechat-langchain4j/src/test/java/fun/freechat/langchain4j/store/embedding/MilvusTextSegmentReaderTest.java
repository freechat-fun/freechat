package fun.freechat.langchain4j.store.embedding;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.Gson;
import com.google.protobuf.ByteString;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.filter.Filter;
import dev.langchain4j.store.embedding.filter.comparison.IsEqualTo;
import dev.langchain4j.store.embedding.filter.comparison.IsGreaterThan;
import io.milvus.client.MilvusServiceClient;
import io.milvus.common.clientenum.ConsistencyLevelEnum;
import io.milvus.grpc.DataType;
import io.milvus.grpc.ErrorCode;
import io.milvus.grpc.FieldData;
import io.milvus.grpc.JSONArray;
import io.milvus.grpc.QueryResults;
import io.milvus.grpc.ScalarField;
import io.milvus.grpc.Status;
import io.milvus.grpc.StringArray;
import io.milvus.param.R;
import io.milvus.param.dml.QueryParam;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class MilvusTextSegmentReaderTest {
    private static final String ID = "12345678-1234-1234-abcd-123456789abc";
    private static final String PRIVATE_PAYLOAD = "private document and credentials";
    private static final String SCOPE_JSON = "{\"owner\":\"owner-1\",\"memoryId\":\"chat-1\",\"generation\":7}";
    private static final Filter SCOPE = new IsEqualTo("owner", "owner-1")
            .and(new IsEqualTo("memoryId", "chat-1"))
            .and(new IsEqualTo("generation", 7L));

    @Test
    void queriesExactIdAndEntireScopeWithStrongConsistency() {
        FakeQuery query = new FakeQuery();
        query.response = R.success(row(ID, "stored text", SCOPE_JSON));
        MilvusTextSegmentReader reader = reader(query);

        TextSegment result = reader.get(ID, SCOPE).orElseThrow();

        assertEquals("stored text", result.text());
        assertEquals(1, query.calls);
        assertEquals("memory_db", query.request.getDatabaseName());
        assertEquals("memory_collection", query.request.getCollectionName());
        assertEquals(
                "(id == \"" + ID + "\") and (((metadata[\"owner\"] == \"owner-1\") and "
                        + "(metadata[\"memoryId\"] == \"chat-1\")) and (metadata[\"generation\"] == 7))",
                query.request.getExpr());
        assertEquals(ConsistencyLevelEnum.STRONG, query.request.getConsistencyLevel());
        assertEquals(1L, query.request.getLimit());
        assertEquals(0L, query.request.getOffset());
        assertFalse(query.request.isIgnoreGrowing());
        assertEquals(List.of("id", "text", "metadata"), query.request.getOutFields());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(
            strings = {
                "",
                "1-1-1-1-1",
                "1234567812341234abcd123456789abc",
                "12345678-1234-1234-ABCD-123456789ABC",
                "12345678-1234-1234-abcd-123456789abz",
                " 12345678-1234-1234-abcd-123456789abc",
                "12345678-1234-1234-abcd-123456789abc ",
                "12345678-1234-1234-abcd-123456789abc0",
                "\") or id != (\""
            })
    void rejectsNoncanonicalIdsBeforeQuery(String id) {
        FakeQuery query = new FakeQuery();
        IllegalArgumentException error =
                assertThrows(IllegalArgumentException.class, () -> reader(query).get(id, SCOPE));
        assertEquals("id must be a canonical lowercase UUID", error.getMessage());
        assertEquals(0, query.calls);
    }

    @Test
    void rejectsMissingScopeBeforeQuery() {
        FakeQuery query = new FakeQuery();
        assertThrows(IllegalArgumentException.class, () -> reader(query).get(ID, null));
        assertEquals(0, query.calls);
    }

    @Test
    void rejectsUnsupportedFiltersRatherThanDroppingThem() {
        FakeQuery query = new FakeQuery();
        for (Filter filter : List.of(
                SCOPE.or(new IsEqualTo("owner", "another-owner")),
                Filter.not(SCOPE),
                SCOPE.and(new IsGreaterThan("generation", 0)),
                (Filter) ignored -> true)) {
            assertThrows(IllegalArgumentException.class, () -> reader(query).get(ID, filter));
        }
        assertEquals(0, query.calls);
    }

    @Test
    void escapesMetadataKeysAndStringValues() {
        FakeQuery query = new FakeQuery();
        Filter filter = new IsEqualTo("key\"\\]", "value\\\" or id != \"anything");
        assertTrue(reader(query).get(ID, filter).isEmpty());
        assertEquals(
                "(id == \"" + ID + "\") and (metadata[\"key\\\"\\\\]\"] == "
                        + "\"value\\\\\\\" or id != \\\"anything\")",
                query.request.getExpr());
    }

    @ParameterizedTest
    @ValueSource(strings = {"\n", "\r", "\t", "\u0000", "\u007f"})
    void rejectsControlCharactersInBothKeysAndValues(String control) {
        FakeQuery query = new FakeQuery();
        for (Filter filter :
                List.of(new IsEqualTo("owner" + control, "value"), new IsEqualTo("owner", "value" + control))) {
            assertThrows(IllegalArgumentException.class, () -> reader(query).get(ID, filter));
        }
        assertEquals(0, query.calls);
    }

    @ParameterizedTest
    @MethodSource("scalarValues")
    void mapsSupportedScalarScopeValues(Object value, String literal) {
        FakeQuery query = new FakeQuery();
        assertTrue(reader(query).get(ID, new IsEqualTo("key", value)).isEmpty());
        assertEquals("(id == \"" + ID + "\") and (metadata[\"key\"] == " + literal + ")", query.request.getExpr());
    }

    static Stream<Arguments> scalarValues() {
        return Stream.of(
                Arguments.of("string", "\"string\""),
                Arguments.of(UUID.fromString(ID), "\"" + ID + "\""),
                Arguments.of(7, "7"),
                Arguments.of(Long.MAX_VALUE, "9223372036854775807"),
                Arguments.of(1.25f, "1.25"),
                Arguments.of(-2.5d, "-2.5"));
    }

    @Test
    void rejectsUnsupportedAndNonfiniteScopeValues() {
        FakeQuery query = new FakeQuery();
        Object maliciousValue = new Object() {
            @Override
            public String toString() {
                throw new AssertionError("Unsupported scope values must not be stringified");
            }
        };
        for (Object value : List.of(
                true,
                (byte) 1,
                Map.of("nested", "value"),
                Double.NaN,
                Double.POSITIVE_INFINITY,
                Float.NEGATIVE_INFINITY,
                maliciousValue)) {
            assertThrows(IllegalArgumentException.class, () -> reader(query).get(ID, new IsEqualTo("owner", value)));
        }
        assertEquals(0, query.calls);
    }

    @Test
    void roundTripsAllSupportedMetadataScalarsThroughSdkJsonRows() {
        Metadata original = new Metadata()
                .put("owner", "owner-1")
                .put("memoryId", "chat-1")
                .put("generation", 7L)
                .put("integer", 123)
                .put("long", Long.MAX_VALUE)
                .put("negativeLong", Long.MIN_VALUE)
                .put("float", 1.25f)
                .put("double", -2.5d)
                .put("uuid", UUID.fromString(ID))
                .put("string", "Unicode 文本 and \"quotes\"\\new\nline");
        FakeQuery query = new FakeQuery();
        query.response = R.success(row(ID, "Unicode body 文本", new Gson().toJson(original.toMap())));

        TextSegment segment = reader(query).get(ID, SCOPE).orElseThrow();
        Metadata actual = segment.metadata();

        assertEquals("Unicode body 文本", segment.text());
        assertEquals(original.getString("string"), actual.getString("string"));
        assertEquals(original.getInteger("integer"), actual.getInteger("integer"));
        assertEquals(original.getLong("long"), actual.getLong("long"));
        assertEquals(original.getLong("negativeLong"), actual.getLong("negativeLong"));
        assertEquals(original.getFloat("float"), actual.getFloat("float"));
        assertEquals(original.getDouble("double"), actual.getDouble("double"));
        assertEquals(original.getUUID("uuid"), actual.getUUID("uuid"));
        assertInstanceOf(Long.class, actual.toMap().get("integer"));
        assertInstanceOf(Double.class, actual.toMap().get("float"));
        assertInstanceOf(String.class, actual.toMap().get("uuid"));
        assertEquals(original.toMap().keySet(), actual.toMap().keySet());
    }

    @Test
    void returnsEmptyWhenNoRecordMatches() {
        FakeQuery query = new FakeQuery();
        assertTrue(reader(query).get(ID, SCOPE).isEmpty());
        query.response = R.success(QueryResults.newBuilder()
                .setStatus(Status.getDefaultInstance())
                .addFieldsData(strings("id"))
                .addFieldsData(strings("text"))
                .addFieldsData(json())
                .build());
        assertTrue(reader(query).get(ID, SCOPE).isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"owner", "memoryId", "generation"})
    void neverReturnsRowsOutsideAnyScopeDimension(String key) {
        FakeQuery query = new FakeQuery();
        Metadata metadata =
                new Metadata().put("owner", "owner-1").put("memoryId", "chat-1").put("generation", 7L);
        if (key.equals("generation")) {
            metadata.put(key, 8L);
        } else {
            metadata.put(key, "another-scope");
        }
        query.response = R.success(row(ID, PRIVATE_PAYLOAD, new Gson().toJson(metadata.toMap())));
        assertTrue(reader(query).get(ID, SCOPE).isEmpty());
        metadata.remove(key);
        query.response = R.success(row(ID, PRIVATE_PAYLOAD, new Gson().toJson(metadata.toMap())));
        assertTrue(reader(query).get(ID, SCOPE).isEmpty());
    }

    @Test
    void sanitizesTransportAndBothSdkStatusLayers() {
        FakeQuery query = new FakeQuery();
        MilvusTextSegmentReader reader = reader(query);
        query.failure = new IllegalStateException(PRIVATE_PAYLOAD);
        assertSanitizedFailure(reader);
        query.failure = null;
        for (R<QueryResults> response : Arrays.<R<QueryResults>>asList(
                null,
                new R<>(),
                R.failed(new IllegalStateException(PRIVATE_PAYLOAD)),
                R.failed(R.Status.PermissionDenied, PRIVATE_PAYLOAD),
                R.success(null),
                R.success(QueryResults.getDefaultInstance()),
                R.success(QueryResults.newBuilder()
                        .setStatus(Status.newBuilder()
                                .setErrorCode(ErrorCode.UnexpectedError)
                                .setReason(PRIVATE_PAYLOAD))
                        .build()),
                R.success(QueryResults.newBuilder()
                        .setStatus(Status.newBuilder().setCode(123).setReason(PRIVATE_PAYLOAD))
                        .build()))) {
            query.response = response;
            assertSanitizedFailure(reader);
        }
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "{\"owner\":{\"nested\":\"private document and credentials\"}}",
                "{\"owner\":[\"private document and credentials\"]}",
                "{\"owner\":true}",
                "{\"owner\":null}",
                "{\"owner\":1e999}",
                "{\"\":\"private document and credentials\"}",
                "not valid json",
                "[]",
                "null"
            })
    void rejectsMalformedOrUnsupportedMetadataWithoutPayloadLeaks(String metadata) {
        FakeQuery query = new FakeQuery();
        query.response = R.success(row(ID, PRIVATE_PAYLOAD, metadata));
        assertSanitizedFailure(reader(query));
    }

    @Test
    void rejectsWrongIdMissingFieldsBlankTextAndMultipleRows() {
        FakeQuery query = new FakeQuery();
        List<FieldData> validFields = row(ID, PRIVATE_PAYLOAD, SCOPE_JSON).getFieldsDataList();
        for (String field : List.of("id", "text", "metadata")) {
            query.response = R.success(QueryResults.newBuilder()
                    .setStatus(Status.getDefaultInstance())
                    .addAllFieldsData(validFields.stream()
                            .filter(value -> !field.equals(value.getFieldName()))
                            .toList())
                    .build());
            assertSanitizedFailure(reader(query));
        }
        query.response = R.success(row("00000000-0000-0000-0000-000000000000", PRIVATE_PAYLOAD, SCOPE_JSON));
        assertSanitizedFailure(reader(query));
        query.response = R.success(row(ID, " \n ", SCOPE_JSON));
        assertSanitizedFailure(reader(query));
        query.response = R.success(QueryResults.newBuilder()
                .setStatus(Status.getDefaultInstance())
                .addFieldsData(strings("id", ID, ID))
                .addFieldsData(strings("text", PRIVATE_PAYLOAD, PRIVATE_PAYLOAD))
                .addFieldsData(json(SCOPE_JSON, SCOPE_JSON))
                .build());
        assertSanitizedFailure(reader(query));
    }

    @Test
    void validatesConstructorAndDoesNotOwnQueryResources() {
        FakeQuery query = new FakeQuery();
        assertThrows(
                NullPointerException.class,
                () -> new MilvusTextSegmentReader((MilvusServiceClient) null, "database", "collection"));
        assertThrows(IllegalArgumentException.class, () -> new MilvusTextSegmentReader(query, null, "collection"));
        assertThrows(IllegalArgumentException.class, () -> new MilvusTextSegmentReader(query, "database", " "));
        MilvusTextSegmentReader reader = reader(query);
        assertTrue(reader.get(ID, SCOPE).isEmpty());
        assertEquals(1, query.calls);
    }

    private static void assertSanitizedFailure(MilvusTextSegmentReader reader) {
        IllegalStateException failure = assertThrows(IllegalStateException.class, () -> reader.get(ID, SCOPE));
        assertTrue(failure.getMessage().startsWith("Milvus exact query"));
        assertFalse(failure.toString().contains(PRIVATE_PAYLOAD));
        assertNull(failure.getCause());
        assertEquals(0, failure.getSuppressed().length);
    }

    private static MilvusTextSegmentReader reader(FakeQuery query) {
        return new MilvusTextSegmentReader(query, "memory_db", "memory_collection");
    }

    private static QueryResults row(String id, String text, String metadata) {
        return QueryResults.newBuilder()
                .setStatus(Status.getDefaultInstance())
                .addFieldsData(strings("id", id))
                .addFieldsData(strings("text", text))
                .addFieldsData(json(metadata))
                .build();
    }

    private static FieldData strings(String name, String... values) {
        return FieldData.newBuilder()
                .setFieldName(name)
                .setType(DataType.VarChar)
                .setScalars(ScalarField.newBuilder()
                        .setStringData(StringArray.newBuilder().addAllData(List.of(values))))
                .build();
    }

    private static FieldData json(String... values) {
        return FieldData.newBuilder()
                .setFieldName("metadata")
                .setType(DataType.JSON)
                .setScalars(ScalarField.newBuilder()
                        .setJsonData(JSONArray.newBuilder()
                                .addAllData(Arrays.stream(values)
                                        .map(ByteString::copyFromUtf8)
                                        .toList())))
                .build();
    }

    private static final class FakeQuery implements Function<QueryParam, R<QueryResults>> {
        private int calls;
        private QueryParam request;
        private RuntimeException failure;
        private R<QueryResults> response = R.success(
                QueryResults.newBuilder().setStatus(Status.getDefaultInstance()).build());

        @Override
        public R<QueryResults> apply(QueryParam request) {
            calls++;
            this.request = request;
            if (failure != null) {
                throw failure;
            }
            return response;
        }
    }
}
