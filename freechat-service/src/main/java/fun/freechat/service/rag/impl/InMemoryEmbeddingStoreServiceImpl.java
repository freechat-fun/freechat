package fun.freechat.service.rag.impl;

import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import fun.freechat.service.cache.LongPeriodCache;
import fun.freechat.service.cache.LongPeriodCacheEvict;
import fun.freechat.service.common.FileStore;
import fun.freechat.service.enums.EmbeddingStoreType;
import fun.freechat.service.rag.EmbeddingStoreService;
import fun.freechat.service.util.StoreUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

@Service("inMemoryEmbeddingStoreService")
@SuppressWarnings("unused")
public class InMemoryEmbeddingStoreServiceImpl<TextSegment> implements EmbeddingStoreService<TextSegment> {
    private final static String CACHE_KEY_PREFIX = "InMemoryEmbeddingStoreService_";
    private final static String CACHE_KEY_SPEL_PREFIX = "'" + CACHE_KEY_PREFIX + "' + ";
    private final static String BASE_PATH = "embedding";

    private String memoryIdToPath(Object memoryId, EmbeddingStoreType storeType) {
        return BASE_PATH + File.separator + storeType.text() + File.separator + memoryId.toString();
    }

    @Override
    @LongPeriodCache(keyBy = CACHE_KEY_SPEL_PREFIX + "#p0")
    public EmbeddingStore<TextSegment> from(Object memoryId, EmbeddingStoreType storeType) {
        if (Objects.nonNull(memoryId)) {
            String storePath = memoryIdToPath(memoryId, storeType);
            FileStore fileStore = StoreUtils.defaultFileStore();
            if (Objects.nonNull(fileStore) && fileStore.exists(storePath)) {
                return (EmbeddingStore<TextSegment>) InMemoryEmbeddingStore.fromFile(fileStore.toPath(storePath));
            }
        }
        return new InMemoryEmbeddingStore<>();
    }

    @Override
    @LongPeriodCacheEvict(keyBy = CACHE_KEY_SPEL_PREFIX + "#p0")
    public void save(Object memoryId, EmbeddingStoreType storeType, EmbeddingStore<TextSegment> store) {
        if (Objects.nonNull(memoryId) && store instanceof InMemoryEmbeddingStore<TextSegment> inMemoryStore) {
            String storePath = memoryIdToPath(memoryId, storeType);
            FileStore fileStore = StoreUtils.defaultFileStore();
            if (Objects.nonNull(fileStore)) {
                if (!fileStore.exists(BASE_PATH)) {
                    try {
                        fileStore.createDirectories(BASE_PATH);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                inMemoryStore.serializeToFile(fileStore.toPath(storePath));
            }
        }
    }

    @Override
    @LongPeriodCacheEvict(keyBy = CACHE_KEY_SPEL_PREFIX + "#p0")
    public void delete(Object memoryId, EmbeddingStoreType storeType) {
        if (Objects.nonNull(memoryId)) {
            String storePath = memoryIdToPath(memoryId, storeType);
            FileStore fileStore = StoreUtils.defaultFileStore();
            if (Objects.nonNull(fileStore)) {
                fileStore.tryDelete(storePath);
            }
        }
    }
}
