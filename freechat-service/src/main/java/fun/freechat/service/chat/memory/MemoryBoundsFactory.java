package fun.freechat.service.chat.memory;

import dev.langchain4j.model.TokenCountEstimator;
import fun.freechat.service.rag.EmbeddingModelService;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.stereotype.Component;

@Component
public class MemoryBoundsFactory {
    private final LongTermMemoryProperties properties;
    private final EmbeddingModelService models;
    private final TokenCountEstimator override;

    public MemoryBoundsFactory(LongTermMemoryProperties properties, EmbeddingModelService models, BeanFactory beans) {
        this.properties = properties;
        this.models = models;
        String name = properties.getTokenEstimatorBean();
        override = name.isEmpty() ? null : beans.getBean(name, TokenCountEstimator.class);
    }

    public MemoryBounds forLanguage(String language) {
        return new MemoryBounds(override == null ? models.tokenCountEstimatorForLang(language) : override, properties);
    }
}
