package fun.freechat.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.redisson.api.RedissonClient;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static fun.freechat.service.util.CacheUtils.*;

@Configuration
@SuppressWarnings("unused")
public class CacheConfig {
    @Bean
    @Primary
    CacheManager distributedCacheManager(RedissonClient redissonClient) {
        /* 10 seconds cache
        NOTE: Because the cache is often invalidated,
        it is recommended to set "sync" to true to prevent cache breakdown as
        @Cacheable(cacheNames = "freechatShortPeriodCache", sync = true...)
        */
        org.redisson.spring.cache.CacheConfig shortPeriodCache =
                new org.redisson.spring.cache.CacheConfig(10_000, 5_000);

        // 5 minutes cache
        org.redisson.spring.cache.CacheConfig middlePeriodCache =
                new org.redisson.spring.cache.CacheConfig(300_000, 150_000);

        // 1 hour cache
        org.redisson.spring.cache.CacheConfig longPeriodCache =
                new org.redisson.spring.cache.CacheConfig(3600_000, 1800_000);

        RedissonSpringCacheManager cacheManager = new RedissonSpringCacheManager(redissonClient, Map.of(
                SHORT_PERIOD_CACHE_NAME, shortPeriodCache,
                MIDDLE_PERIOD_CACHE_NAME, middlePeriodCache,
                LONG_PERIOD_CACHE_NAME, longPeriodCache));

        cacheManager.setAllowNullValues(false);
        return cacheManager;
    }

    @Bean(name = IN_PROCESS_LONG_CACHE_MANAGER)
    public CacheManager inProcessLongCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(LONG_PERIOD_CACHE_NAME);
        cacheManager.setAllowNullValues(false);
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(100_000)
                .expireAfterWrite(60, TimeUnit.MINUTES)
                .expireAfterAccess(30, TimeUnit.MINUTES));
        return cacheManager;
    }

    @Bean(name = IN_PROCESS_SHORT_CACHE_MANAGER)
    public CacheManager inProcessShortCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(SHORT_PERIOD_CACHE_NAME);
        cacheManager.setAllowNullValues(false);
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(100_000)
                .expireAfterWrite(10, TimeUnit.SECONDS)
                .expireAfterAccess(5, TimeUnit.SECONDS));
        return cacheManager;
    }
}
