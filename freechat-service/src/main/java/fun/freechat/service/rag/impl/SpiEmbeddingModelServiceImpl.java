package fun.freechat.service.rag.impl;

import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.spi.model.embedding.EmbeddingModelFactory;
import fun.freechat.service.rag.EmbeddingModelService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;

import static dev.langchain4j.spi.ServiceHelper.loadFactories;

@Service("spiEmbeddingModelService")
@SuppressWarnings("unused")
@Slf4j
public class SpiEmbeddingModelServiceImpl implements EmbeddingModelService {
    private EmbeddingModel embeddingModel;

    @PostConstruct
    public void init() {
        embeddingModel = loadEmbeddingModel();
    }

    @Override
    public EmbeddingModel from(Object memoryId) {
        // ignore memoryId for now
        return embeddingModel;
    }

    private static EmbeddingModel loadEmbeddingModel() {
        Collection<EmbeddingModelFactory> factories = loadFactories(EmbeddingModelFactory.class);
        if (factories.size() > 1) {
            throw new RuntimeException("Conflict: multiple embedding models have been found in the classpath. " +
                    "Please explicitly specify the one you wish to use.");
        }

        for (EmbeddingModelFactory factory : factories) {
            EmbeddingModel embeddingModel = factory.create();
            log.debug("Loaded the following embedding model through SPI: {}", embeddingModel);
            return embeddingModel;
        }

        return null;
    }
}
