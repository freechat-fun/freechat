package fun.freechat.util;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@SuppressWarnings({"rawtypes", "unused"})
public class PojoUtils {
    private static final Logger log = LoggerFactory.getLogger(PojoUtils.class);

    private static final int MAX_DEPTH = 5;

    private static final ThreadLocal<WeakHashMap<Object, Object>> REGISTRY = new ThreadLocal<>();
    private static final ThreadLocal<Integer> DEPTH = new ThreadLocal<>();

    private static int getDepth() {
        Integer depth = DEPTH.get();
        return Objects.nonNull(depth) ? depth : 0;
    }

    private static boolean isTooDeep() {
        return getDepth() > MAX_DEPTH;
    }

    private static void increaseDepth() {
        DEPTH.set(getDepth() + 1);
    }

    private static void decreaseDepth() {
        DEPTH.set(getDepth() - 1);
    }

    private static Map<Object, Object> getRegistry() {
        return REGISTRY.get();
    }

    private static boolean isRegistered(Object value) {
        Map<Object, Object> m = getRegistry();
        return Objects.nonNull(m) && m.containsKey(value);
    }

    private static void register(Object value) {
        if (Objects.isNull(value)) {
            return;
        }
        Map<Object, Object> m = getRegistry();
        if (Objects.isNull(m)) {
            REGISTRY.set(new WeakHashMap());
        }
        getRegistry().put(value, null);
    }

    private static void unregister(Object value) {
        if (Objects.isNull(value)) {
            return;
        }
        Map<Object, Object> m = getRegistry();
        if (Objects.isNull(m)) {
            return;
        }
        m.remove(value);
        if (m.isEmpty()) {
            REGISTRY.remove();
        }
    }

    public static String object2JsonString(Object value, boolean recursive) {
        if (Objects.isNull(value)) {
            return "null";
        } else if (value instanceof Number || value instanceof Boolean) {
            return value.toString();
        } else if (value instanceof Character) {
            return "\"" + value +"\"";
        } else if (value instanceof String) {
            return "\"" + escape((String) value) + "\"";
        } else if (value instanceof Map) {
            return map2Json((Map) value, recursive);
        } else if (value instanceof Collection) {
            return coll2Json((Collection) value, recursive);
        } else if (value.getClass().isArray()) {
            return coll2Json(arrayToList(value), recursive);
        } else {
            return customObject2Json(value, recursive);
        }
    }

    public static String object2JsonString(Object value) {
        return object2JsonString(value, false);
    }

    private static String coll2Json(Collection coll, boolean recursive) {
        if (Objects.isNull(coll)) {
            return "null";
        }
        StringBuilder builder = new StringBuilder();
        builder.append('[');
        for (Iterator it = coll.iterator(); it.hasNext(); ) {
            builder.append(object2JsonString(it.next(), recursive));
            if (it.hasNext()) {
                builder.append(",");
            }
        }
        builder.append(']');
        return builder.toString();
    }

    private static boolean accept(Field field) {
        if (field.getName().indexOf(36) != -1) {
            return false;
        }
        int modifiers = field.getModifiers();
        return !(Modifier.isTransient(modifiers) || Modifier.isStatic(modifiers));
    }

    private static List<Field> getFields(Class type, boolean recursive) {
        List<Field> fieldList;
        if (!recursive) {
            fieldList = List.of(type.getDeclaredFields());
        } else {
            fieldList = new LinkedList<>();
            while (Objects.nonNull(type)) {
                fieldList.addAll(List.of(type.getDeclaredFields()));
                type = type.getSuperclass();
            }
        }

        return fieldList.stream().filter(PojoUtils::accept).collect(Collectors.toList());
    }

    private static String objSimpleInfo(Object o) {
        return "\"" + o.getClass().getSimpleName() + "@" + Integer.toHexString(o.hashCode()) +"\"";
    }

    private static String objToString(Object o)
    {
        try {
            if(Object.class != o.getClass().getMethod("toString").getDeclaringClass()) {
                return "\"" + o + "\"";
            }
        } catch (NoSuchMethodException ignored) {
        }
        return objSimpleInfo(o);
    }

    private static String customObject2Json(Object obj, boolean recursive) {
        if (isTooDeep() || isRegistered(obj)) {
            return objSimpleInfo(obj);
        }
        increaseDepth();
        register(obj);
        String info;

        try {
            Class type = obj.getClass();
            List<Field> fields = getFields(type, recursive);
            if (CollectionUtils.isEmpty(fields)) {
                return objToString(obj);
            }
            Map<String, Object> map = new LinkedHashMap<>();
            for (Field f : fields) {
                String name = f.getName();
                String getter = "get" + name.substring(0, 1).toUpperCase() + name.substring(1);
                try {
                    Method method = type.getMethod(getter);
                    Object value = method.invoke(obj);
                    map.put(name, value);
                } catch (NoSuchMethodException e) {
                    try {
                        f.setAccessible(true);
                        Object value = f.get(obj);
                        map.put(name, value);
                    } catch (IllegalAccessException ex) {
                        log.error("Failed to get {}.{}", type.getSimpleName(), name, ex);
                    }
                } catch (InvocationTargetException | IllegalAccessException e) {
                    log.error("Failed to invoke {}.{}", type.getSimpleName(), getter, e);
                }
            }
            info = map2Json(map, recursive);
        } finally {
            unregister(obj);
            decreaseDepth();
        }
        return info;
    }

    private static String map2Json(Map map, boolean recursive) {
        if (Objects.isNull(map)) {
            return "null";
        }
        StringBuilder builder = new StringBuilder();
        builder.append('{');
        for (Iterator it = map.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            String key = (String) entry.getKey();
            if (Objects.isNull(key))  {
                continue;
            }
            builder.append('\"');
            escape(key, builder);
            builder.append('\"').append(':').append(object2JsonString(entry.getValue(), recursive));
            if (it.hasNext()) {
                builder.append(",");
            }
        }
        builder.append('}');
        return builder.toString();
    }

    /**
     * Escape quotes, \, /, \r, \n, \b, \f, \t and other control characters (U+0000 through U+001F).
     */
    private static String escape(String s) {
        if (Objects.isNull(s)) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        escape(s, builder);
        return builder.toString();
    }

    private static void escape(String s, StringBuilder builder) {
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            switch (ch) {
                case '"':
                    builder.append("\\\"");
                    break;
                case '\\':
                    builder.append("\\\\");
                    break;
                case '\b':
                    builder.append("\\b");
                    break;
                case '\f':
                    builder.append("\\f");
                    break;
                case '\n':
                    builder.append("\\n");
                    break;
                case '\r':
                    builder.append("\\r");
                    break;
                case '\t':
                    builder.append("\\t");
                    break;
                case '/':
                    builder.append("\\/");
                    break;
                default:
                    if (ch <= '\u001F' || ch >= '\u007F' && ch <= '\u009F' || ch >= '\u2000' && ch <= '\u20FF') {
                        String ss = Integer.toHexString(ch);
                        builder.append("\\u");
                        builder.append("0".repeat(4 - ss.length()));
                        builder.append(ss.toUpperCase());
                    } else {
                        builder.append(ch);
                    }
            }
        }
    }

    private static List<Object> arrayToList(Object value) {
        List<Object> list = new LinkedList<>();
        if (value instanceof boolean[]) {
            for (boolean v : (boolean[]) value) {
                list.add(v);
            }
        } else if (value instanceof float[]) {
            for (float v : (float[]) value) {
                list.add(v);
            }
        } else if (value instanceof long[]) {
            for (long v : (long[]) value) {
                list.add(v);
            }
        } else if (value instanceof int[]) {
            for (int v : (int[]) value) {
                list.add(v);
            }
        } else if (value instanceof short[]) {
            for (short v : (short[]) value) {
                list.add(v);
            }
        } else if (value instanceof byte[]) {
            for (byte v : (byte[]) value) {
                list.add(v);
            }
        } else if (value instanceof double[]) {
            for (double v : (double[]) value) {
                list.add(v);
            }
        } else if (value instanceof char[]) {
            for (char v : (char[]) value) {
                list.add(v);
            }
        } else {
            list.addAll(List.of((Object[]) value));
        }
        return list;
    }

    public static <S, T> void mapWhenExists(Supplier<S> from, Consumer<T> to, Function<S, T> converter) {
        S value = from.get();
        boolean isValid;

        if (value instanceof CharSequence) {
            isValid = StringUtils.isNotBlank((String) value);
        } else if (value instanceof Collection<?>){
            isValid = CollectionUtils.isNotEmpty((Collection<?>) value);
        } else if (value instanceof Map<?,?>) {
            isValid = MapUtils.isNotEmpty((Map<?, ?>) value);
        } else {
            isValid = Objects.nonNull(value);
        }

        if (isValid) {
            if (Objects.isNull(converter)) {
                converter = s -> (T) s;
            }
            to.accept(converter.apply(value));
        }
    }

    public static <T> void mapWhenExists(Supplier<T> from, Consumer<T> to) {
        mapWhenExists(from, to, null);
    }
}
