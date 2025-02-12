package fun.freechat.service.rag.impl;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.milvus.MilvusEmbeddingStore;
import fun.freechat.langchain4j.store.embedding.DelegatedEmbeddingStore;
import fun.freechat.service.enums.EmbeddingStoreType;
import fun.freechat.service.rag.EmbeddingModelService;
import fun.freechat.service.rag.EmbeddingStoreService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static dev.langchain4j.store.embedding.filter.MetadataFilterBuilder.metadataKey;
import static fun.freechat.service.enums.EmbeddingRecordMeta.MEMORY_ID;

@Service("milvusEmbeddingStoreService")
@Primary
@SuppressWarnings("unused")
public class MilvusEmbeddingStoreServiceImpl implements EmbeddingStoreService<TextSegment> {
    @Value("${embedding.milvus.database:#{null}}")
    private String database;
    @Value("${embedding.milvus.retrieveEmbeddingsOnSearch:false}")
    private Boolean retrieveEmbeddingsOnSearch;
    @Value("${embedding.milvus.url}")
    private String url;
    @Value("${embedding.milvus.username:#{null}}")
    private String username;
    @Value("${embedding.milvus.password:#{null}}")
    private String password;
    @Value("${embedding.milvus.token:#{null}}")
    private String token;
    @Autowired
    private EmbeddingModelService embeddingModelService;
    private Map<EmbeddingStoreType, DelegatedEmbeddingStore> embeddingStores;

    private int dimensionForType(EmbeddingStoreType type) {
        return switch (type) {
            case ZH_CHARACTER_DOCUMENT, ZH_LONG_TERM_MEMORY -> embeddingModelService.dimensionForLang("zh");
            case EN_CHARACTER_DOCUMENT, EN_LONG_TERM_MEMORY -> embeddingModelService.dimensionForLang("en");
            case null, default -> embeddingModelService.dimensionForLang("default");
        };
    }

    @PostConstruct
    public void init() {
        embeddingStores = HashMap.newHashMap(EmbeddingStoreType.values().length);
        for (EmbeddingStoreType type : EmbeddingStoreType.values()) {
            MilvusEmbeddingStore origEmbeddingStore = MilvusEmbeddingStore.builder()
                    .uri(url)
                    .username(username)
                    .password(password)
                    .token(token)
                    .databaseName(database)
                    .collectionName(type.text())
                    .dimension(dimensionForType(type))
                    .retrieveEmbeddingsOnSearch(retrieveEmbeddingsOnSearch)
                    .build();

            DelegatedEmbeddingStore delegatedEmbeddingStore = DelegatedEmbeddingStore.builder()
                    .embeddingStore(origEmbeddingStore)
                    .build();

            embeddingStores.put(type, delegatedEmbeddingStore);
        }
    }

    @Override
    public EmbeddingStore<TextSegment> of(Object memoryId, EmbeddingStoreType storeType) {
        return embeddingStores.get(storeType);
    }

    @Override
    public void flush(Object memoryId, EmbeddingStoreType storeType, EmbeddingStore<TextSegment> store) {
        // ignore
    }

    @Override
    public void delete(Object memoryId, EmbeddingStoreType storeType) {
        if (memoryId == null) {
            return;
        }
        EmbeddingStore<TextSegment> embeddingStore = of(memoryId, storeType);
        embeddingStore.removeAll(metadataKey(MEMORY_ID.text()).isEqualTo(memoryId.toString()));
    }
}
