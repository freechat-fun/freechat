package fun.freechat.service.rag;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.filter.Filter;
import fun.freechat.service.enums.EmbeddingStoreType;
import java.util.Optional;

public interface ExactEmbeddingStoreService extends EmbeddingStoreService<TextSegment> {
    Optional<TextSegment> get(EmbeddingStoreType storeType, String id, Filter scope);
}
