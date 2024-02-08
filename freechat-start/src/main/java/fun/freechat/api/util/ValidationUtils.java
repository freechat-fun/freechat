package fun.freechat.api.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.Map;

public class ValidationUtils {
    public static <T> T ensureNotNull(T object, String name) {
        return ensureNotNull(object, "%s cannot be null", name);
    }

    public static <T> T ensureNotNull(T object, String format, Object... args) {
        if (object == null) {
            throw badRequest(format, args);
        }
        return object;
    }

    public static <T extends Collection<?>> T ensureNotEmpty(T collection, String name) {
        if (collection == null || collection.isEmpty()) {
            throw badRequest("%s cannot be null or empty", name);
        }

        return collection;
    }

    public static <K, V> Map<K, V> ensureNotEmpty(Map<K, V> map, String name) {
        if (map == null || map.isEmpty()) {
            throw badRequest("%s cannot be null or empty", name);
        }

        return map;
    }

    public static String ensureNotBlank(String string, String name) {
        if (string == null || string.trim().isEmpty()) {
            throw badRequest("%s cannot be null or blank", name);
        }

        return string;
    }

    private static ResponseStatusException badRequest(String format, Object... args) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format(format, args));
    }
}
