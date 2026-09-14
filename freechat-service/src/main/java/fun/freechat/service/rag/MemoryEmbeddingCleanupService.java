package fun.freechat.service.rag;

import dev.langchain4j.store.embedding.filter.Filter;
import fun.freechat.service.enums.EmbeddingStoreType;
import java.util.List;

/** Bounded metadata-only cleanup, separate from read-only exact embedding access. */
public interface MemoryEmbeddingCleanupService {
    /** Deletes 1..100 canonical UUIDs AND the full historical MemoryScope AND original commit_id. */
    void removeExact(EmbeddingStoreType type, List<String> ids, Filter scope);

    /** Returns a single strong, no-offset page (1..100) of validated unstructured legacy UUIDs. */
    List<String> legacyIds(EmbeddingStoreType type, String chatId, int limit);

    /** Deletes 1..100 canonical UUIDs AND the same chat-specific legacy predicate used for discovery. */
    void removeLegacy(EmbeddingStoreType type, String chatId, List<String> ids);
}
