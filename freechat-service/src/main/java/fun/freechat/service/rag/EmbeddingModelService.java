package fun.freechat.service.rag;

import dev.langchain4j.model.TokenCountEstimator;
import dev.langchain4j.model.embedding.EmbeddingModel;

public interface EmbeddingModelService {
    EmbeddingModel modelForLang(String lang);
    TokenCountEstimator tokenCountEstimatorForLang(String lang);
    String queryPrefixForLang(String lang);
    int dimensionForLang(String lang);
}
