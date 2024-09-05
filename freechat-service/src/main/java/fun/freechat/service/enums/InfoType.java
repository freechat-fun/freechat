package fun.freechat.service.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.lang3.StringUtils;

@SuppressWarnings("unused")
public enum InfoType {
    PROMPT,
    AGENT,
    PLUGIN,
    CHARACTER,
    UNKNOWN;
    public static InfoType of(String text) {
        if (StringUtils.isBlank(text)) {
            return UNKNOWN;
        }
        try {
            return InfoType.valueOf(text.toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }

    @JsonValue
    public String text() {
        return name().toLowerCase();
    }

    @Override
    public String toString() {
        return text();
    }
}
