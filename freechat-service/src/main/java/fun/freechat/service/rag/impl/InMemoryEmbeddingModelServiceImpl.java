package fun.freechat.service.rag.impl;

import dev.langchain4j.model.Tokenizer;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.HuggingFaceTokenizer;
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
import java.util.Objects;

import static java.util.Collections.singletonMap;

@Service("enZhEmbeddingModelService")
@SuppressWarnings("unused")
@Slf4j
public class InMemoryEmbeddingModelServiceImpl implements EmbeddingModelService {
    private EmbeddingModel enEmbeddingModel;
    private Tokenizer enTokenizer;
    private EmbeddingModel zhEmbeddingModel;
    private Tokenizer zhTokenizer;
    private EmbeddingModel defaultEmbeddingModel;
    private Tokenizer defaultTokenizer;

    private boolean isEn(String lang) {
        return "en".equalsIgnoreCase(lang);
    }

    private boolean isZh(String lang) {
        return "zh".equalsIgnoreCase(lang) || "zh_CN".equalsIgnoreCase(lang) || "zh_TW".equalsIgnoreCase(lang);
    }

    private Tokenizer tokenizerFromResource(String resourceName, Class<? extends EmbeddingModel> clazz) {
        try {
            URI uri = Objects.requireNonNull(clazz.getResource(resourceName)).toURI();

            if ("jar".equals(uri.getScheme())) {
                try (FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap())) {
                    Path resourcePath = fileSystem.getPath(resourceName);
                    new HuggingFaceTokenizer(resourcePath, singletonMap("padding", "false"));
                }
            } else {
                Path resourcePath = Path.of(uri);
                new HuggingFaceTokenizer(resourcePath, singletonMap("padding", "false"));
            }
        } catch (Exception e) {
            log.error("Failed to find resource {}", resourceName, e);
        }
        return new HuggingFaceTokenizer();
    }

    @PostConstruct
    public void init() {
        enEmbeddingModel = new BgeSmallEnV15QuantizedEmbeddingModel();
        zhEmbeddingModel = new BgeSmallZhV15QuantizedEmbeddingModel();
        defaultEmbeddingModel = new AllMiniLmL6V2QuantizedEmbeddingModel();

        enTokenizer = tokenizerFromResource(
                "/bge-small-en-v1.5-q-tokenizer.json",
                BgeSmallEnV15QuantizedEmbeddingModel.class);

        zhTokenizer = tokenizerFromResource(
                "/bge-small-zh-v1.5-q-tokenizer.json",
                BgeSmallZhV15QuantizedEmbeddingModel.class);

        defaultTokenizer = tokenizerFromResource(
                "/all-minilm-l6-v2-q-tokenizer.json",
                AllMiniLmL6V2QuantizedEmbeddingModel.class);
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
    public Tokenizer tokenizerForLang(String lang) {
        if (isEn(lang)) {
            return enTokenizer;
        } else if (isZh(lang)) {
            return zhTokenizer;
        } else {
            return defaultTokenizer;
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
