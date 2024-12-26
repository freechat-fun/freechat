package fun.freechat.api.util;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

@Slf4j
public class CommonUtils {
    private CommonUtils() {}

    @NonNull
    public static <S, T> T convert(@NonNull S source, @NonNull Class<T> clazz) {
        try {
            Objects.requireNonNull(source);
            T response = clazz.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, response);
            return response;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            log.error("Failed to construct instance of {}", clazz.getSimpleName(), e);
            throw new IllegalStateException(e);
        }
    }
}
