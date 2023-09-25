package fun.freechat.service.cache;

import fun.freechat.util.SpELUtils;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.ParseException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.SimpleEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;

import static fun.freechat.service.util.CacheUtils.*;

@Component(KEY_GENERATOR)
@Slf4j
@SuppressWarnings("unused")
public class FullNameKeyGenerator implements KeyGenerator {
    private static final String[] SHORT_PERIOD_CACHE_NAMES = new String[]{SHORT_PERIOD_CACHE_NAME};
    private static final String[] MIDDLE_PERIOD_CACHE_NAMES = new String[]{MIDDLE_PERIOD_CACHE_NAME};
    private static final String[] LONG_PERIOD_CACHE_NAMES = new String[]{LONG_PERIOD_CACHE_NAME};

    @Override
    public @NonNull Object generate(@NonNull Object target, @NonNull Method method, @Nullable Object... params) {
        Pair<String[], String> cachesKeyExpr = getCachesKeyExpr(method);
        String key = null;
        if (Objects.nonNull(cachesKeyExpr) && !ObjectUtils.isEmpty(cachesKeyExpr.getRight())) {
            key = generateSpELKey(target, method, params, cachesKeyExpr);
        }
        if (ObjectUtils.isEmpty(key)) {
            key = generateDefaultKey(target, method, params);
        }
        return key;
    }

    private Pair<String[], String> getCachesKeyExpr(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof ShortPeriodCache) {
                return Pair.of(SHORT_PERIOD_CACHE_NAMES, ((ShortPeriodCache) annotation).keyBy());
            } else if (annotation instanceof MiddlePeriodCache) {
                return Pair.of(MIDDLE_PERIOD_CACHE_NAMES, ((MiddlePeriodCache) annotation).keyBy());
            } else if (annotation instanceof LongPeriodCache) {
                return Pair.of(LONG_PERIOD_CACHE_NAMES, ((LongPeriodCache) annotation).keyBy());
            } else if (annotation instanceof Cacheable) {
                return Pair.of(((Cacheable) annotation).cacheNames(), ((Cacheable) annotation).key());
            }
        }

        return null;
    }

    private Pair<String[], String> getCachesKeyExpr(Method method) {
        Pair<String[], String> cachesKeyExpr = getCachesKeyExpr(method.getAnnotations());
        return Objects.nonNull(cachesKeyExpr) ? cachesKeyExpr
                : getCachesKeyExpr(method.getDeclaringClass().getAnnotations());
    }

    private String generateDefaultKey(Object target, Method method, Object[] params) {
        String key = target.getClass().getName() + "::" + method.getName() + "_";
        if (Objects.nonNull(params) && params.length > 0) {
            key += StringUtils.arrayToDelimitedString(params, "_");
        }
        return key;
    }

    private String generateSpELKey(Object target, Method method, Object[] params,
                                   Pair<String[], String> cachesKeyExpr) {
        SpelExpressionParser parser = new SpelExpressionParser();
        SimpleEvaluationContext context = SimpleEvaluationContext.forReadOnlyDataBinding().build();
        Map<String, Object> contextInfo = SpELUtils.extractInfo(target, method, params);
        contextInfo.forEach(context::setVariable);
        context.setVariable("caches", cachesKeyExpr.getLeft());
        try {
            Expression expr = parser.parseExpression(cachesKeyExpr.getRight());
            return expr.getValue(context, String.class);
        } catch (ParseException | EvaluationException e) {
            log.error("Failed to generate SpEL key!", e);
        }
        return null;
    }

    static class Ada {
        public String hi = "Hello ";

        public String sayHello(String somebody) {
            return hi + somebody;
        }
    }

    private static Pair<String[], String> testPair(String expr) {
        return Pair.of(LONG_PERIOD_CACHE_NAMES, expr);
    }

    public static void main(String[] args) throws NoSuchMethodException {
        Ada ada = new Ada();
        Method hello = Ada.class.getMethod("sayHello", String.class);
        String bob = "bob";

        FullNameKeyGenerator gen = new FullNameKeyGenerator();
        Object[] params = new Object[]{bob};
        System.out.println(gen.generateSpELKey(ada, hello, params, testPair("#methodName")));
        System.out.println(gen.generateSpELKey(ada, hello, params, testPair("#method.name")));
        System.out.println(gen.generateSpELKey(ada, hello, params, testPair("#args")));
        System.out.println(gen.generateSpELKey(ada, hello, params, testPair("#p0")));
        System.out.println(gen.generateSpELKey(ada, hello, params, testPair("#somebody")));
        System.out.println(gen.generateSpELKey(ada, hello, params, testPair("#target.hi")));
        System.out.println(gen.generateSpELKey(ada, hello, params, testPair("'Bye ' + #p0")));
    }
}
