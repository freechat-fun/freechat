package fun.freechat.service.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.lang3.StringUtils;

public enum ModelType {
    TEXT2TEXT,
    TEXT2CHAT,
    TEXT2IMAGE,
    EMBEDDING,
    MODERATION,
    UNKNOWN;
    public static ModelType of(String text) {
        if (StringUtils.isBlank(text)) {
            return UNKNOWN;
        }
        try {
            return ModelType.valueOf(text.toUpperCase());
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
