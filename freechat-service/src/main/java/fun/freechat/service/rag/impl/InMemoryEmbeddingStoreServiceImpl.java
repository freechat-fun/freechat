package fun.freechat.service.rag.impl;

import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import fun.freechat.service.common.FileStore;
import fun.freechat.service.enums.EmbeddingStoreType;
import fun.freechat.service.rag.EmbeddingStoreService;
import fun.freechat.service.util.StoreUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static fun.freechat.service.util.CacheUtils.IN_PROCESS_LONG_CACHE_MANAGER;
import static fun.freechat.service.util.CacheUtils.LONG_PERIOD_CACHE_NAME;

@Service("inMemoryEmbeddingStoreService")
@Slf4j
@SuppressWarnings("unused")
public class InMemoryEmbeddingStoreServiceImpl<TextSegment> implements EmbeddingStoreService<TextSegment> {
    private final static String CACHE_KEY_PREFIX = "InMemoryEmbeddingStoreService_";
    private final static String BASE_PATH = "embedding";

    @Autowired
    @Qualifier(IN_PROCESS_LONG_CACHE_MANAGER)
    private CacheManager cacheManager;
    private Cache cache;

    private String memoryDirectory(EmbeddingStoreType storeType) {
        return BASE_PATH + File.separator + storeType.text();
    }

    private String memoryIdToPath(Object memoryId, EmbeddingStoreType storeType) {
        return memoryDirectory(storeType) + File.separator + memoryId.toString() + ".json";
    }

    private Cache cache() {
        if (Objects.isNull(cache) && Objects.nonNull(cacheManager)) {
            cache = cacheManager.getCache(LONG_PERIOD_CACHE_NAME);
        }
        return cache;
    }

    private EmbeddingStore<TextSegment> getCachedStore(Object memoryId, long lastModifiedTime) {
        Cache cache = cache();
        if (Objects.isNull(cache)) {
            return null;
        }
        String timestampKey = CACHE_KEY_PREFIX + "timestamp";
        Long timestamp = cache.get(timestampKey, Long.class);
        if (Objects.isNull(timestamp) || timestamp < lastModifiedTime) {
            return null;
        }

        String dataKey = CACHE_KEY_PREFIX + "data";
        return cache.get(dataKey, EmbeddingStore.class);
    }

    private void removeCachedStore(Object memoryId) {
        Cache cache = cache();
        if (Objects.isNull(cache)) {
            return;
        }
        String timestampKey = CACHE_KEY_PREFIX + "timestamp";
        String dataKey = CACHE_KEY_PREFIX + "data";
        cache.evict(timestampKey);
        cache.evict(dataKey);
    }

    private void cacheStore(EmbeddingStore<TextSegment> store) {
        Cache cache = cache();
        if (Objects.isNull(cache)) {
            return;
        }
        String timestampKey = CACHE_KEY_PREFIX + "timestamp";
        String dataKey = CACHE_KEY_PREFIX + "data";
        cache.put(timestampKey, System.currentTimeMillis());
        cache.put(dataKey, store);
    }

    @Override
    public EmbeddingStore<TextSegment> of(Object memoryId, EmbeddingStoreType storeType) {
        if (Objects.nonNull(memoryId)) {
            String storePath = memoryIdToPath(memoryId, storeType);
            FileStore fileStore = StoreUtils.defaultFileStore();
            if (Objects.nonNull(fileStore) && fileStore.exists(storePath)) {
                long lastModifiedTime = Long.MAX_VALUE;
                try {
                    lastModifiedTime = fileStore.getLastModifiedTime(storePath);
                } catch (IOException e) {
                    log.warn("Failed to get last modified time of file {}", storePath, e);
                }
                EmbeddingStore<TextSegment> store = getCachedStore(memoryId, lastModifiedTime);
                if (Objects.isNull(store)) {
                    store = (EmbeddingStore<TextSegment>) InMemoryEmbeddingStore.fromFile(fileStore.toPath(storePath));
                    cacheStore(store);
                }
                return store;
            }
        }
        return new InMemoryEmbeddingStore<>();
    }

    @Override
    public void flush(Object memoryId, EmbeddingStoreType storeType, EmbeddingStore<TextSegment> store) {
        if (Objects.nonNull(memoryId) && store instanceof InMemoryEmbeddingStore<TextSegment> inMemoryStore) {
            String storePath = memoryIdToPath(memoryId, storeType);
            FileStore fileStore = StoreUtils.defaultFileStore();
            if (Objects.nonNull(fileStore)) {
                String dir = memoryDirectory(storeType);
                if (!fileStore.exists(dir)) {
                    try {
                        fileStore.createDirectories(dir);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                inMemoryStore.serializeToFile(fileStore.toPath(storePath));
                removeCachedStore(memoryId);
            }
        }
    }

    @Override
    public void delete(Object memoryId, EmbeddingStoreType storeType) {
        if (Objects.nonNull(memoryId)) {
            String storePath = memoryIdToPath(memoryId, storeType);
            FileStore fileStore = StoreUtils.defaultFileStore();
            if (Objects.nonNull(fileStore)) {
                fileStore.tryDelete(storePath);
                removeCachedStore(memoryId);
            }
        }
    }
}
