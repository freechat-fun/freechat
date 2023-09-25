package fun.freechat.service.util;

import org.springframework.beans.BeansException;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
public class CacheUtils implements ApplicationContextAware {
    public static final String LONG_PERIOD_CACHE_NAME = "freechatLongPeriodCache";

    public static final String MIDDLE_PERIOD_CACHE_NAME = "freechatMiddlePeriodCache";

    public static final String SHORT_PERIOD_CACHE_NAME = "freechatShortPeriodCache";

    public static final String KEY_GENERATOR = "fullNameKeyGenerator";

    private static CacheManager cacheManager;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        cacheManager = applicationContext.getBean(CacheManager.class);
    }

    public static void cacheEvict(String cacheName, List<String> keys) {
        if (CollectionUtils.isEmpty(keys)) {
            return;
        }

        Optional.ofNullable(cacheManager)
                .map(manager -> manager.getCache(cacheName))
                .ifPresent(cache -> keys.forEach(cache::evict));
    }

    public static void longPeriodCacheEvict(List<String> keys) {
        cacheEvict(LONG_PERIOD_CACHE_NAME, keys);
    }

    public static void middlePeriodCacheEvict(List<String> keys) {
        cacheEvict(MIDDLE_PERIOD_CACHE_NAME, keys);
    }

    public static void shortPeriodCacheEvict(List<String> keys) {
        cacheEvict(SHORT_PERIOD_CACHE_NAME, keys);
    }
}
