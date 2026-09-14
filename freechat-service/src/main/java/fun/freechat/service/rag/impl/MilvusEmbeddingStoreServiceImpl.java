package fun.freechat.service.rag.impl;

import static dev.langchain4j.store.embedding.filter.MetadataFilterBuilder.metadataKey;
import static fun.freechat.service.enums.EmbeddingRecordMeta.MEMORY_ID;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.filter.Filter;
import dev.langchain4j.store.embedding.milvus.MilvusEmbeddingStore;
import fun.freechat.langchain4j.store.embedding.DelegatedEmbeddingStore;
import fun.freechat.langchain4j.store.embedding.MilvusMemoryCleanupAdapter;
import fun.freechat.langchain4j.store.embedding.MilvusTextSegmentReader;
import fun.freechat.service.chat.memory.MemoryScope;
import fun.freechat.service.enums.EmbeddingStoreType;
import fun.freechat.service.rag.EmbeddingModelService;
import fun.freechat.service.rag.ExactEmbeddingStoreService;
import fun.freechat.service.rag.MemoryEmbeddingCleanupService;
import io.milvus.client.MilvusServiceClient;
import io.milvus.common.clientenum.ConsistencyLevelEnum;
import io.milvus.param.ConnectParam;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service("milvusEmbeddingStoreService")
@Primary
@SuppressWarnings("unused")
public class MilvusEmbeddingStoreServiceImpl implements ExactEmbeddingStoreService, MemoryEmbeddingCleanupService {
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
    private final Map<EmbeddingStoreType, MilvusTextSegmentReader> memoryReaders = new HashMap<>();
    private final Map<EmbeddingStoreType, MilvusMemoryCleanupAdapter> memoryCleaners = new HashMap<>();
    private MilvusServiceClient memoryClient;

    private int dimensionForType(EmbeddingStoreType type) {
        return switch (type) {
            case ZH_CHARACTER_DOCUMENT, ZH_LONG_TERM_MEMORY -> embeddingModelService.dimensionForLang("zh");
            case EN_CHARACTER_DOCUMENT, EN_LONG_TERM_MEMORY -> embeddingModelService.dimensionForLang("en");
            case null, default -> embeddingModelService.dimensionForLang("default");
        };
    }

    @PostConstruct
    public void init() {
        String memoryDatabase = database == null ? "default" : database;
        memoryClient = new MemoryMilvusClient(ConnectParam.newBuilder()
                .withUri(url)
                .withToken(token)
                .withAuthorization(username == null ? "" : username, password == null ? "" : password)
                .withDatabaseName(memoryDatabase)
                .withConnectTimeout(10, TimeUnit.SECONDS)
                .withRpcDeadline(30, TimeUnit.SECONDS)
                .build());
        embeddingStores = HashMap.newHashMap(EmbeddingStoreType.values().length);
        try {
            for (EmbeddingStoreType type : EmbeddingStoreType.values()) {
                MilvusEmbeddingStore.Builder builder = MilvusEmbeddingStore.builder()
                        .uri(url)
                        .username(username)
                        .password(password)
                        .token(token)
                        .databaseName(database)
                        .collectionName(type.text())
                        .dimension(dimensionForType(type))
                        .retrieveEmbeddingsOnSearch(retrieveEmbeddingsOnSearch)
                        .autoFlushOnInsert(false);
                if (MemoryScope.isMemoryStore(type)) {
                    builder.milvusClient(memoryClient).consistencyLevel(ConsistencyLevelEnum.STRONG);
                    memoryReaders.put(type, new MilvusTextSegmentReader(memoryClient, memoryDatabase, type.text()));
                    memoryCleaners.put(type, new MilvusMemoryCleanupAdapter(memoryClient, memoryDatabase, type.text()));
                }
                embeddingStores.put(
                        type,
                        DelegatedEmbeddingStore.builder()
                                .embeddingStore(builder.build())
                                .build());
            }
        } catch (RuntimeException ignored) {
            closeMemoryClient();
            throw new IllegalStateException("Milvus embedding storage initialization failed");
        }
    }

    @PreDestroy
    public void closeMemoryClient() {
        if (memoryClient != null) {
            memoryClient.close();
        }
    }

    @Override
    public Optional<TextSegment> get(EmbeddingStoreType storeType, String id, Filter scope) {
        if (!MemoryScope.isMemoryStore(storeType)) {
            throw new IllegalArgumentException("Exact memory reads require a long-term-memory collection");
        }
        return memoryReaders.get(storeType).get(id, scope);
    }

    @Override
    public void removeExact(EmbeddingStoreType type, List<String> ids, Filter scope) {
        memoryCleaner(type).removeExact(ids, scope);
    }

    @Override
    public List<String> legacyIds(EmbeddingStoreType type, String chatId, int limit) {
        return memoryCleaner(type).legacyIds(chatId, limit);
    }

    @Override
    public void removeLegacy(EmbeddingStoreType type, String chatId, List<String> ids) {
        memoryCleaner(type).removeLegacy(chatId, ids);
    }

    private MilvusMemoryCleanupAdapter memoryCleaner(EmbeddingStoreType type) {
        if (!MemoryScope.isMemoryStore(type)) {
            throw new IllegalArgumentException("Memory cleanup requires a long-term-memory collection");
        }
        return memoryCleaners.get(type);
    }

    @Override
    public EmbeddingStore<TextSegment> of(Object memoryId, EmbeddingStoreType storeType) {
        return embeddingStores.get(storeType);
    }

    @Override
    public void flush(Object memoryId, EmbeddingStoreType storeType, EmbeddingStore<TextSegment> store) {
        // ignored
    }

    @Override
    public void delete(Object memoryId, EmbeddingStoreType storeType) {
        if (memoryId == null) {
            return;
        }
        EmbeddingStore<TextSegment> embeddingStore = of(memoryId, storeType);
        embeddingStore.removeAll(metadataKey(MEMORY_ID.text()).isEqualTo(memoryId.toString()));
    }

    private static final class MemoryMilvusClient extends MilvusServiceClient {
        private MemoryMilvusClient(ConnectParam parameters) {
            super(parameters);
        }

        // The SDK has no OFF level; even its ERROR messages can include server-returned record contents.
        @Override
        protected void logDebug(String message, Object... arguments) {}

        @Override
        protected void logInfo(String message, Object... arguments) {}

        @Override
        protected void logWarning(String message, Object... arguments) {}

        @Override
        protected void logError(String message, Object... arguments) {}
    }
}
