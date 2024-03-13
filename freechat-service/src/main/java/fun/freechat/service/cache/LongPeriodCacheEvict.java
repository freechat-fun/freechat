package fun.freechat.service.cache;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

import static fun.freechat.service.util.CacheUtils.LONG_PERIOD_CACHE_NAME;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@CacheEvict(cacheNames = LONG_PERIOD_CACHE_NAME)
@SuppressWarnings("unused")
public @interface LongPeriodCacheEvict {
    @AliasFor(annotation = CacheEvict.class, attribute = "key")
    String keyBy() default "";
}
