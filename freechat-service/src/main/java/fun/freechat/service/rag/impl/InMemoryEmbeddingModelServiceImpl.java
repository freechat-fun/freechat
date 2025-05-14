package fun.freechat.service.rag.impl;

import dev.langchain4j.model.TokenCountEstimator;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.HuggingFaceTokenCountEstimator;
import dev.langchain4j.model.embedding.onnx.allminilml6v2q.AllMiniLmL6V2QuantizedEmbeddingModel;
import dev.langchain4j.model.embedding.onnx.bgesmallenv15q.BgeSmallEnV15QuantizedEmbeddingModel;
import dev.langchain4j.model.embedding.onnx.bgesmallzhv15q.BgeSmallZhV15QuantizedEmbeddingModel;
import fun.freechat.service.rag.EmbeddingModelService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@Service("enZhEmbeddingModelService")
@SuppressWarnings("unused")
@Slf4j
public class InMemoryEmbeddingModelServiceImpl implements EmbeddingModelService {
    private EmbeddingModel enEmbeddingModel;
    private TokenCountEstimator enTokenCountEstimator;
    private EmbeddingModel zhEmbeddingModel;
    private TokenCountEstimator zhTokenCountEstimator;
    private EmbeddingModel defaultEmbeddingModel;
    private TokenCountEstimator defaultTokenCountEstimator;

    private boolean isEn(String lang) {
        return "en".equalsIgnoreCase(lang);
    }

    private boolean isZh(String lang) {
        return "zh".equalsIgnoreCase(lang) || "zh_CN".equalsIgnoreCase(lang) || "zh_TW".equalsIgnoreCase(lang);
    }

    private TokenCountEstimator tokenCountEstimatorFromResource(String resourceName, EmbeddingModel model) {
        Class<? extends EmbeddingModel> clazz = model.getClass();
        Map<String, String> options = Map.of(
                "padding", "false",
                "modelMaxLength", String.valueOf(model.dimension()));

        try {
            URI uri = Objects.requireNonNull(clazz.getResource(resourceName)).toURI();

            if ("jar".equals(uri.getScheme())) {
                try (FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap())) {
                    Path resourcePath = fileSystem.getPath(resourceName);
                    new HuggingFaceTokenCountEstimator(resourcePath, options);
                }
            } else {
                Path resourcePath = Path.of(uri);
                new HuggingFaceTokenCountEstimator(resourcePath, options);
            }
        } catch (Exception e) {
            log.error("Failed to find resource {}", resourceName, e);
        }
        return new HuggingFaceTokenCountEstimator();
    }

    @PostConstruct
    public void init() {
        enEmbeddingModel = new BgeSmallEnV15QuantizedEmbeddingModel();
        zhEmbeddingModel = new BgeSmallZhV15QuantizedEmbeddingModel();
        defaultEmbeddingModel = new AllMiniLmL6V2QuantizedEmbeddingModel();

        enTokenCountEstimator = tokenCountEstimatorFromResource(
                "/bge-small-en-v1.5-q-tokenizer.json",
                enEmbeddingModel);

        zhTokenCountEstimator = tokenCountEstimatorFromResource(
                "/bge-small-zh-v1.5-q-tokenizer.json",
                zhEmbeddingModel);

        defaultTokenCountEstimator = tokenCountEstimatorFromResource(
                "/all-minilm-l6-v2-q-tokenizer.json",
                defaultEmbeddingModel);
    }

    @Override
    public EmbeddingModel modelForLang(String lang) {
        if (isEn(lang)) {
            return enEmbeddingModel;
        } else if (isZh(lang)) {
            return zhEmbeddingModel;
        } else {
            return defaultEmbeddingModel;
        }
    }

    @Override
    public TokenCountEstimator tokenCountEstimatorForLang(String lang) {
        if (isEn(lang)) {
            return enTokenCountEstimator;
        } else if (isZh(lang)) {
            return zhTokenCountEstimator;
        } else {
            return defaultTokenCountEstimator;
        }
    }

    @Override
    public String queryPrefixForLang(String lang) {
        if (isEn(lang)) {
            // https://github.com/langchain4j/langchain4j-embeddings/blob/main/langchain4j-embeddings-bge-small-en-v15-q/src/main/java/dev/langchain4j/model/embedding/bge/small/en/v15/BgeSmallEnV15QuantizedEmbeddingModel.java#L16
            return "Represent this sentence for searching relevant passages:";
        } else if (isZh(lang)) {
            // https://github.com/langchain4j/langchain4j-embeddings/blob/main/langchain4j-embeddings-bge-small-zh-v15-q/src/main/java/dev/langchain4j/model/embedding/bge/small/zh/v15/BgeSmallZhV15QuantizedEmbeddingModel.java#L16
            return "为这个句子生成表示以用于检索相关文章：";
        } else {
            return "";
        }
    }

    @Override
    public int dimensionForLang(String lang) {
        if (isEn(lang)) {
            return enEmbeddingModel.dimension();
        } else if (isZh(lang)) {
            return zhEmbeddingModel.dimension();
        } else {
            return defaultEmbeddingModel.dimension();
        }
    }
}
