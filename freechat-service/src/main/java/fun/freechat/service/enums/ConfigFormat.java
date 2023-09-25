package fun.freechat.service.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.lang3.StringUtils;

public enum ConfigFormat {
    KV,
    JSON,
    YAML;
    public static ConfigFormat of(String text) {
        if (StringUtils.isBlank(text)) {
            return KV;
        }
        try {
            return ConfigFormat.valueOf(text.toUpperCase());
        } catch (IllegalArgumentException e) {
            return KV;
        }
    }

    @JsonValue
    public String text() {
        return name().toLowerCase();
    }
}
