package fun.freechat.service.rag.impl;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.milvus.MilvusEmbeddingStore;
import fun.freechat.langchain4j.store.embedding.DelegatedEmbeddingStore;
import fun.freechat.service.enums.EmbeddingStoreType;
import fun.freechat.service.rag.EmbeddingStoreService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import static fun.freechat.service.enums.EmbeddingStoreType.CHARACTER_DOCUMENT;
import static fun.freechat.service.enums.EmbeddingStoreType.LONG_TERM_MEMORY;

@Service("milvusEmbeddingStoreService")
@Primary
@SuppressWarnings("unused")
public class MilvusEmbeddingStoreServiceImpl implements EmbeddingStoreService<TextSegment> {
    @Value("${embedding.milvus.database:#{null}}")
    private String database;
    @Value("${embedding.milvus.dimension:384}")
    private Integer dimension;
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

    private DelegatedEmbeddingStore documentEmbeddingStore;
    private DelegatedEmbeddingStore longTermMemoryEmbeddingStore;

    @PostConstruct
    public void init() {
        MilvusEmbeddingStore documentMilvusEmbeddingStore = MilvusEmbeddingStore.builder()
                .uri(url)
                .username(username)
                .password(password)
                .token(token)
                .databaseName(database)
                .collectionName(CHARACTER_DOCUMENT.text())
                .dimension(dimension)
                .retrieveEmbeddingsOnSearch(retrieveEmbeddingsOnSearch)
                .build();

        MilvusEmbeddingStore longTermMemoryMilvusEmbeddingStore = MilvusEmbeddingStore.builder()
                .uri(url)
                .username(username)
                .password(password)
                .token(token)
                .databaseName(database)
                .collectionName(LONG_TERM_MEMORY.text())
                .dimension(dimension)
                .retrieveEmbeddingsOnSearch(retrieveEmbeddingsOnSearch)
                .build();

        documentEmbeddingStore = DelegatedEmbeddingStore.builder()
                .embeddingStore(documentMilvusEmbeddingStore)
                .build();

        longTermMemoryEmbeddingStore = DelegatedEmbeddingStore.builder()
                .embeddingStore(longTermMemoryMilvusEmbeddingStore)
                .build();
    }

    @Override
    public EmbeddingStore<TextSegment> of(Object memoryId, EmbeddingStoreType type) {
        return type == LONG_TERM_MEMORY ? longTermMemoryEmbeddingStore : documentEmbeddingStore;
    }

    @Override
    public void flush(Object memoryId, EmbeddingStoreType storeType, EmbeddingStore<TextSegment> store) {
        // ignore
    }

    @Override
    public void delete(Object memoryId, EmbeddingStoreType storeType) {
        // ignore
    }
}
