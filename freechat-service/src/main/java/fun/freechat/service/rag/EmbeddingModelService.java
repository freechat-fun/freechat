package fun.freechat.service.rag;

import dev.langchain4j.model.embedding.EmbeddingModel;

public interface EmbeddingModelService {
    EmbeddingModel from(Object memoryId);
}
