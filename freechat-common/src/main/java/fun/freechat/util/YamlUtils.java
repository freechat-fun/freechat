package fun.freechat.util;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.representer.Representer;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@SuppressWarnings("unused")
public class YamlUtils {
    private static final Yaml yaml = createYaml();

    public static String toYaml(Properties properties) {
        Map<String, Object> map = HashMap.newHashMap(properties.size());
        for (String name : properties.stringPropertyNames()) {
            String[] keys = name.split("\\.");
            Map<String, Object> currentMap = map;
            for (int i = 0; i < keys.length - 1; i++) {
                String key = keys[i];
                currentMap = (Map<String, Object>) currentMap.computeIfAbsent(key, k -> new HashMap<>());
            }
            currentMap.put(keys[keys.length - 1], properties.getProperty(name));
        }

        return yaml.dump(map);
    }

    public static <T> String toYaml(T map) {
        return yaml.dump(map);
    }

    public static Properties toProperties(String text) {
        Properties properties = new Properties();
        Map<String, Object> map = yaml.load(text);

        populateProperties(properties, map, "");
        return properties;
    }

    public static <T> T toObject(String text) {
        return yaml.load(text);
    }

    private static void populateProperties(Properties properties, Map<String, Object> map, String prefix) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
            if (entry.getValue() instanceof Map) {
                populateProperties(properties, (Map<String, Object>) entry.getValue(), key);
            } else if (entry.getValue() instanceof Iterable) {
                int index = 0;
                for (Object item : (Iterable<?>) entry.getValue()) {
                    properties.put(key + "[" + index++ + "]", item.toString());
                }
            } else {
                properties.put(key, entry.getValue().toString());
            }
        }
    }

    private static Yaml createYaml() {
        DumperOptions options = new DumperOptions();
        options.setIndent(2);
        options.setPrettyFlow(true);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        Representer representer = new Representer(options);
        representer.getPropertyUtils().setSkipMissingProperties(true);

        return new Yaml(representer, options);
    }
}
