package fun.freechat.service.rag;

import dev.langchain4j.model.Tokenizer;
import dev.langchain4j.model.embedding.EmbeddingModel;

public interface EmbeddingModelService {
    EmbeddingModel modelForLang(String lang);
    Tokenizer tokenizerForLang(String lang);
    String queryPrefixForLang(String lang);
}
