package fun.freechat.service.rag;

import dev.langchain4j.store.embedding.EmbeddingStore;
import fun.freechat.service.enums.EmbeddingStoreType;

public interface EmbeddingStoreService<Embedded> {
    EmbeddingStore<Embedded> from(Object memoryId, EmbeddingStoreType type);
    void save(Object memoryId, EmbeddingStoreType storeType, EmbeddingStore<Embedded> store);
    void delete(Object memoryId, EmbeddingStoreType storeType);
}
