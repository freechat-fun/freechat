package fun.freechat.langchain4j.store.embedding;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.langchain4j.store.embedding.filter.Filter;
import dev.langchain4j.store.embedding.filter.comparison.IsEqualTo;
import dev.langchain4j.store.embedding.filter.logical.And;
import io.milvus.client.MilvusServiceClient;
import io.milvus.common.clientenum.ConsistencyLevelEnum;
import io.milvus.grpc.ErrorCode;
import io.milvus.grpc.MutationResult;
import io.milvus.grpc.QueryResults;
import io.milvus.grpc.Status;
import io.milvus.param.R;
import io.milvus.param.dml.DeleteParam;
import io.milvus.param.dml.QueryParam;
import io.milvus.response.QueryResultsWrapper;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/** Metadata-only cleanup borrowing an already sanitized, deadline-configured memory client. */
public final class MilvusMemoryCleanupAdapter {
    private static final int MAX_BATCH = 100;
    private static final Pattern UUID_PATTERN =
            Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
    private static final List<String> STRUCTURED_KEYS = List.of(
            "schema_version", "commit_id", "generation", "record_kind", "user_id", "character_uid", "store_type");
    private static final List<String> REQUIRED_SCOPE_KEYS =
            List.of("memory_id", "user_id", "character_uid", "generation", "store_type", "schema_version", "commit_id");
    private final Function<QueryParam, R<QueryResults>> query;
    private final Function<DeleteParam, R<MutationResult>> delete;
    private final String database;
    private final String collection;

    public MilvusMemoryCleanupAdapter(MilvusServiceClient client, String database, String collection) {
        this(Objects.requireNonNull(client, "client")::query, client::delete, database, collection);
    }

    // Package-private SDK boundaries for server-free tests; neither boundary owns client resources.
    MilvusMemoryCleanupAdapter(
            Function<QueryParam, R<QueryResults>> query,
            Function<DeleteParam, R<MutationResult>> delete,
            String database,
            String collection) {
        this.query = Objects.requireNonNull(query, "query");
        this.delete = Objects.requireNonNull(delete, "delete");
        if (database == null || database.isBlank() || collection == null || collection.isBlank()) {
            throw new IllegalArgumentException("An explicit database and collection are required");
        }
        this.database = database;
        this.collection = collection;
    }

    /** One server-side delete; the caller must supply the historical scope and original commit UUID. */
    public void removeExact(List<String> ids, Filter scope) {
        String idExpression = idsExpression(ids);
        Map<String, Object> values = new HashMap<>();
        collectScope(scope, values, 0);
        if (!values.keySet().containsAll(REQUIRED_SCOPE_KEYS)) {
            throw new IllegalArgumentException("Cleanup requires the full memory scope and original commit");
        }
        requireChatId(values.get("memory_id"));
        requireIdentifier(values.get("user_id"));
        requireIdentifier(values.get("character_uid"));
        requirePositiveInteger(values.get("generation"));
        requirePositiveInteger(values.get("schema_version"));
        if (!collection.equals(values.get("store_type"))) {
            throw new IllegalArgumentException("Cleanup scope must match the selected collection");
        }
        Object commit = values.get("commit_id");
        requireUuid(commit instanceof UUID uuid ? uuid.toString() : commit);
        String expression = MilvusTextSegmentReader.mapScope(scope);
        remove("(" + idExpression + ") and (" + expression + ")");
    }

    /** A single strong, no-offset page containing IDs and metadata only, never text or vectors. */
    public List<String> legacyIds(String chatId, int limit) {
        requireBatchSize(limit);
        String predicate = legacyPredicate(chatId);
        QueryParam request = QueryParam.newBuilder()
                .withDatabaseName(database)
                .withCollectionName(collection)
                .withExpr(predicate)
                .withOutFields(List.of("id", "metadata"))
                .withConsistencyLevel(ConsistencyLevelEnum.STRONG)
                .withLimit((long) limit)
                .build();
        try {
            R<QueryResults> response = query.apply(request);
            if (response == null || !Objects.equals(response.getStatus(), R.Status.Success.getCode())) {
                throw failure();
            }
            QueryResults results = response.getData();
            if (results == null || !results.hasStatus() || !success(results.getStatus())) {
                throw failure();
            }
            QueryResultsWrapper wrapper = new QueryResultsWrapper(results);
            if (wrapper.getRowCount() > limit) {
                throw failure();
            }
            LinkedHashSet<String> ids = new LinkedHashSet<>();
            for (QueryResultsWrapper.RowRecord row : wrapper.getRowRecords()) {
                // Skip malformed or foreign rows rather than authorizing their deletion.
                if (row.get("id") instanceof String id
                        && canonicalUuid(id)
                        && row.get("metadata") instanceof JsonObject metadata
                        && isLegacy(metadata, chatId)) {
                    ids.add(id);
                }
            }
            return List.copyOf(ids);
        } catch (RuntimeException ignored) {
            // SDK/JSON exceptions can contain record contents, expressions or credentials.
            throw failure();
        }
    }

    /** Recheck the exact legacy predicate on the server, including when records changed after discovery. */
    public void removeLegacy(String chatId, List<String> ids) {
        String idExpression = idsExpression(ids);
        remove("(" + idExpression + ") and (" + legacyPredicate(chatId) + ")");
    }

    private void remove(String expression) {
        DeleteParam request = DeleteParam.newBuilder()
                .withDatabaseName(database)
                .withCollectionName(collection)
                .withExpr(expression)
                .build();
        try {
            R<MutationResult> response = delete.apply(request);
            if (response == null || !Objects.equals(response.getStatus(), R.Status.Success.getCode())) {
                throw failure();
            }
            MutationResult result = response.getData();
            if (result == null
                    || !result.hasStatus()
                    || !success(result.getStatus())
                    || result.getErrIndexCount() != 0) {
                throw failure();
            }
        } catch (RuntimeException ignored) {
            throw failure();
        }
    }

    private static String legacyPredicate(String chatId) {
        requireChatId(chatId);
        StringBuilder predicate =
                new StringBuilder(MilvusTextSegmentReader.mapScope(new IsEqualTo("memory_id", chatId)));
        for (String key : STRUCTURED_KEYS) {
            predicate.append(" and (not exists metadata[\"").append(key).append("\"])");
        }
        // Milvus 2.4 JSON numeric comparisons exclude strings, booleans, nulls and missing keys.
        // Filtering here (not after LIMIT) prevents invalid message IDs from starving real legacy rows.
        return predicate
                .append(" and (metadata[\"user_message_id\"] > 0)")
                .append(" and (metadata[\"ai_message_id\"] > 0)")
                .toString();
    }

    private static boolean isLegacy(JsonObject metadata, String chatId) {
        JsonElement memoryId = metadata.get("memory_id");
        return memoryId != null
                && memoryId.isJsonPrimitive()
                && memoryId.getAsJsonPrimitive().isString()
                && chatId.equals(memoryId.getAsString())
                && STRUCTURED_KEYS.stream().noneMatch(metadata::has)
                && positiveNumber(metadata.get("user_message_id"))
                && positiveNumber(metadata.get("ai_message_id"));
    }

    private static boolean positiveNumber(JsonElement value) {
        return value != null
                && value.isJsonPrimitive()
                && value.getAsJsonPrimitive().isNumber()
                && Double.isFinite(value.getAsDouble())
                && value.getAsDouble() > 0;
    }

    private static void collectScope(Filter filter, Map<String, Object> values, int depth) {
        if (depth >= MAX_BATCH || values.size() >= MAX_BATCH) {
            throw new IllegalArgumentException("Cleanup scope is too large");
        }
        if (filter instanceof And and) {
            collectScope(and.left(), values, depth + 1);
            collectScope(and.right(), values, depth + 1);
        } else if (filter instanceof IsEqualTo equal
                && equal.key() != null
                && !equal.key().isBlank()
                && equal.comparisonValue() != null) {
            if (values.putIfAbsent(equal.key(), equal.comparisonValue()) != null) {
                throw new IllegalArgumentException("Cleanup scope must not repeat keys");
            }
        } else {
            throw new IllegalArgumentException("Cleanup scope supports only equality and AND");
        }
    }

    private static String idsExpression(List<String> ids) {
        if (ids == null) {
            throw new IllegalArgumentException("Cleanup IDs are required");
        }
        requireBatchSize(ids.size());
        for (String id : ids) {
            requireUuid(id);
        }
        return "id in [" + ids.stream().map(id -> "\"" + id + "\"").collect(Collectors.joining(", ")) + "]";
    }

    private static void requireBatchSize(int size) {
        if (size < 1 || size > MAX_BATCH) {
            throw new IllegalArgumentException("Cleanup batches must contain 1 to 100 entries");
        }
    }

    private static boolean canonicalUuid(String value) {
        return value.length() == 36 && UUID_PATTERN.matcher(value).matches();
    }

    private static void requireUuid(Object value) {
        if (!(value instanceof String id) || !canonicalUuid(id)) {
            throw new IllegalArgumentException("Cleanup IDs must be canonical lowercase UUIDs");
        }
    }

    private static void requireChatId(Object value) {
        requireIdentifier(value);
        if (((String) value).endsWith("-assist")) {
            throw new IllegalArgumentException("Cleanup requires a persistent memory chat ID");
        }
    }

    private static void requireIdentifier(Object value) {
        if (!(value instanceof String id)
                || id.isBlank()
                || id.length() > 32
                || id.chars().anyMatch(Character::isISOControl)) {
            throw new IllegalArgumentException("Invalid cleanup scope identifier");
        }
    }

    private static void requirePositiveInteger(Object value) {
        if (!(value instanceof Integer || value instanceof Long) || ((Number) value).longValue() <= 0) {
            throw new IllegalArgumentException("Cleanup scope requires positive integer versions");
        }
    }

    private static boolean success(Status status) {
        return status.getErrorCode() == ErrorCode.Success && status.getCode() == 0;
    }

    private static IllegalStateException failure() {
        return new IllegalStateException("Milvus memory cleanup failed");
    }
}
