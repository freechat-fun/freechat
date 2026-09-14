package fun.freechat.langchain4j.store.embedding;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.filter.Filter;
import dev.langchain4j.store.embedding.filter.comparison.IsEqualTo;
import dev.langchain4j.store.embedding.filter.logical.And;
import io.milvus.client.MilvusServiceClient;
import io.milvus.common.clientenum.ConsistencyLevelEnum;
import io.milvus.grpc.ErrorCode;
import io.milvus.grpc.QueryResults;
import io.milvus.param.R;
import io.milvus.param.dml.QueryParam;
import io.milvus.response.QueryResultsWrapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.regex.Pattern;

public final class MilvusTextSegmentReader {
    private static final Pattern CANONICAL_UUID =
            Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
    private static final List<String> OUTPUT_FIELDS = List.of("id", "text", "metadata");

    private final Function<QueryParam, R<QueryResults>> query;
    private final String database;
    private final String collection;

    /** Creates a reader borrowing the client and targeting an explicit, nonblank database/collection. */
    public MilvusTextSegmentReader(MilvusServiceClient client, String database, String collection) {
        this(Objects.requireNonNull(client, "client")::query, database, collection);
    }

    // Package-private SDK query boundary for server-free tests; no alternate public client lifecycle.
    MilvusTextSegmentReader(Function<QueryParam, R<QueryResults>> query, String database, String collection) {
        this.query = Objects.requireNonNull(query, "query");
        this.database = requireName(database, "database");
        this.collection = requireName(collection, "collection");
    }

    public Optional<TextSegment> get(String id, Filter filter) {
        if (id == null || id.length() != 36 || !CANONICAL_UUID.matcher(id).matches()) {
            throw new IllegalArgumentException("id must be a canonical lowercase UUID");
        }
        if (filter == null) {
            throw new IllegalArgumentException("A scope filter is required");
        }
        String scope = mapScope(filter);
        QueryParam request = QueryParam.newBuilder()
                .withDatabaseName(database)
                .withCollectionName(collection)
                .withExpr("(id == \"" + id + "\") and (" + scope + ")")
                .withConsistencyLevel(ConsistencyLevelEnum.STRONG)
                .withOutFields(OUTPUT_FIELDS)
                .withLimit(1L)
                .build();

        R<QueryResults> response;
        try {
            response = query.apply(request);
        } catch (RuntimeException ignored) {
            // SDK exceptions can contain the expression or document. Do not retain their causes.
            throw new IllegalStateException("Milvus exact query failed");
        }
        if (response == null || !Objects.equals(response.getStatus(), R.Status.Success.getCode())) {
            throw new IllegalStateException("Milvus exact query failed");
        }
        QueryResults results = response.getData();
        if (results == null || !results.hasStatus()) {
            throw invalidResponse();
        }
        if (results.getStatus().getErrorCode() != ErrorCode.Success
                || results.getStatus().getCode() != 0) {
            throw new IllegalStateException("Milvus exact query failed");
        }
        try {
            QueryResultsWrapper wrapper = new QueryResultsWrapper(results);
            if (wrapper.getRowCount() > 1) {
                throw invalidResponse();
            }
            List<QueryResultsWrapper.RowRecord> rows = wrapper.getRowRecords();
            if (rows.isEmpty()) {
                return Optional.empty();
            }
            QueryResultsWrapper.RowRecord row = rows.getFirst();
            if (!id.equals(row.get("id"))
                    || !(row.get("text") instanceof String text)
                    || text.isBlank()
                    || !(row.get("metadata") instanceof JsonObject json)) {
                throw invalidResponse();
            }
            Metadata metadata = toMetadata(json);
            // Defense in depth: never return a record outside the supplied scope.
            if (!filter.test(metadata)) {
                return Optional.empty();
            }
            return Optional.of(TextSegment.from(text, metadata));
        } catch (RuntimeException ignored) {
            // SDK and Metadata validation errors may include field values or document contents.
            throw invalidResponse();
        }
    }

    private static String requireName(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return value;
    }

    static String mapScope(Filter filter) {
        if (filter instanceof And and) {
            return "(" + mapScope(and.left()) + ") and (" + mapScope(and.right()) + ")";
        }
        if (filter instanceof IsEqualTo equal) {
            return "metadata[" + quote(equal.key()) + "] == " + literal(equal.comparisonValue());
        }
        throw new IllegalArgumentException("Scope filters support only equality and AND");
    }

    private static String literal(Object value) {
        if (value instanceof String string) {
            return quote(string);
        }
        if (value instanceof UUID uuid) {
            return quote(uuid.toString());
        }
        if (value instanceof Integer || value instanceof Long) {
            return value.toString();
        }
        if (value instanceof Float number && Float.isFinite(number)) {
            return number.toString();
        }
        if (value instanceof Double number && Double.isFinite(number)) {
            return number.toString();
        }
        throw new IllegalArgumentException("Unsupported scope value type");
    }

    private static String quote(String value) {
        if (value.chars().anyMatch(Character::isISOControl)) {
            throw new IllegalArgumentException("Scope strings must not contain control characters");
        }
        return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }

    private static Metadata toMetadata(JsonObject json) {
        Map<String, Object> values = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            if (!entry.getValue().isJsonPrimitive()) {
                throw invalidResponse();
            }
            JsonPrimitive value = entry.getValue().getAsJsonPrimitive();
            if (value.isString()) {
                values.put(entry.getKey(), value.getAsString());
            } else if (value.isNumber()) {
                // Match LangChain4j's LONG_OR_DOUBLE JSON number policy without losing long precision.
                String number = value.getAsString();
                try {
                    values.put(entry.getKey(), Long.parseLong(number));
                } catch (NumberFormatException ignored) {
                    double decimal = Double.parseDouble(number);
                    if (!Double.isFinite(decimal)) {
                        throw invalidResponse();
                    }
                    values.put(entry.getKey(), decimal);
                }
            } else {
                throw invalidResponse();
            }
        }
        return Metadata.from(values);
    }

    private static IllegalStateException invalidResponse() {
        return new IllegalStateException("Milvus exact query returned an invalid response");
    }
}
