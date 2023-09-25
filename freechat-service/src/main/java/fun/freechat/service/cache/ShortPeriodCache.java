package fun.freechat.service.cache;

import org.springframework.cache.annotation.Cacheable;

import java.lang.annotation.*;

import static fun.freechat.service.util.CacheUtils.KEY_GENERATOR;
import static fun.freechat.service.util.CacheUtils.SHORT_PERIOD_CACHE_NAME;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Cacheable(cacheNames = SHORT_PERIOD_CACHE_NAME, keyGenerator=KEY_GENERATOR, unless="#result == null")
@SuppressWarnings("unused")
public @interface ShortPeriodCache {
    String keyBy() default "";
}
