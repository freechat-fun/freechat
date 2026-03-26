package fun.freechat.service.cache;

import static fun.freechat.service.util.CacheUtils.MIDDLE_PERIOD_CACHE_NAME;

import java.lang.annotation.*;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.core.annotation.AliasFor;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@CacheEvict(cacheNames = MIDDLE_PERIOD_CACHE_NAME)
@SuppressWarnings("unused")
public @interface MiddlePeriodCacheEvict {
    @AliasFor(annotation = CacheEvict.class, attribute = "key")
    String keyBy() default "";
}
