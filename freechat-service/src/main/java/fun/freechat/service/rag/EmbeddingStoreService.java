package fun.freechat.service.rag;

import dev.langchain4j.store.embedding.EmbeddingStore;

public interface EmbeddingStoreService<Embedded> {
    EmbeddingStore<Embedded> from(Object memoryId);
    void save(Object memoryId, EmbeddingStore<Embedded> store);
    void delete(Object memoryId);
}
