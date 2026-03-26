package fun.freechat.service.cache;

import static fun.freechat.service.util.CacheUtils.KEY_GENERATOR;
import static fun.freechat.service.util.CacheUtils.SHORT_PERIOD_CACHE_NAME;

import java.lang.annotation.*;
import org.springframework.cache.annotation.Cacheable;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Cacheable(cacheNames = SHORT_PERIOD_CACHE_NAME, keyGenerator = KEY_GENERATOR, unless = "#result == null")
@SuppressWarnings("unused")
public @interface ShortPeriodCache {
    String keyBy() default "";
}
