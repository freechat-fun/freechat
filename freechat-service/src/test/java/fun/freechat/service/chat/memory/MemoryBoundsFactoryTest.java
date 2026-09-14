package fun.freechat.service.chat.memory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dev.langchain4j.model.TokenCountEstimator;
import dev.langchain4j.model.embedding.EmbeddingModel;
import fun.freechat.service.rag.EmbeddingModelService;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

class MemoryBoundsFactoryTest {
    private final LongTermMemoryProperties properties = new LongTermMemoryProperties();
    private final DefaultListableBeanFactory beans = new DefaultListableBeanFactory();
    private final AtomicReference<String> selectedLanguage = new AtomicReference<>();
    private final EmbeddingModelService models = new EmbeddingModelService() {
        @Override
        public EmbeddingModel modelForLang(String lang) {
            throw new AssertionError("Budget resolution must not invoke an embedding model");
        }

        @Override
        public TokenCountEstimator tokenCountEstimatorForLang(String lang) {
            selectedLanguage.set(lang);
            return MemoryBoundsTest.estimator(ignored -> 4);
        }

        @Override
        public String queryPrefixForLang(String lang) {
            throw new AssertionError("Budget resolution must not prepare a search");
        }

        @Override
        public int dimensionForLang(String lang) {
            throw new AssertionError("Budget resolution must not initialize a vector store");
        }
    };

    @Test
    void defaultsToSessionLanguageEstimatorWithSafetyMargin() {
        MemoryBounds bounds = new MemoryBoundsFactory(properties, models, beans).forLanguage("zh");
        assertEquals("zh", selectedLanguage.get());
        assertEquals(5, bounds.tokens("text"));
    }

    @Test
    void namedEstimatorOverridesLanguageEstimator() {
        beans.registerSingleton("customEstimator", MemoryBoundsTest.estimator(ignored -> 8));
        properties.setTokenEstimatorBean("customEstimator");
        MemoryBounds bounds = new MemoryBoundsFactory(properties, models, beans).forLanguage("en");
        assertEquals(null, selectedLanguage.get());
        assertEquals(10, bounds.tokens("text"));
    }

    @Test
    void failsAtConstructionForMissingOrWronglyTypedOverride() {
        properties.setTokenEstimatorBean("customEstimator");
        assertThrows(NoSuchBeanDefinitionException.class, () -> new MemoryBoundsFactory(properties, models, beans));
        beans.registerSingleton("customEstimator", "not a tokenizer");
        assertThrows(BeanNotOfRequiredTypeException.class, () -> new MemoryBoundsFactory(properties, models, beans));
    }
}
