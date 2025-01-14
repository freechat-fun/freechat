package fun.freechat.service.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public interface RuntimeConfig {
    String getContent();
    Properties getProperties();
    default Map<String, Object> getMap() {
        Properties properties = getProperties();
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
        return map;
    }
    default <T> T getObject() {
        throw new UnsupportedOperationException("Not implemented");
    }
    default String get(String key) {
        return getProperties().getProperty(key);
    }
}