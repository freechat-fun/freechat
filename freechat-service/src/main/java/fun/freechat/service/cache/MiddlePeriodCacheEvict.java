package fun.freechat.service.cache;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

import static fun.freechat.service.util.CacheUtils.MIDDLE_PERIOD_CACHE_NAME;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@CacheEvict(cacheNames = MIDDLE_PERIOD_CACHE_NAME)
@SuppressWarnings("unused")
public @interface MiddlePeriodCacheEvict {
    @AliasFor("keyBy")
    String key() default "";

    @AliasFor("key")
    String keyBy() default "";
}
