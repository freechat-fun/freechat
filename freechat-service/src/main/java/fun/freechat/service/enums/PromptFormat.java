package fun.freechat.service.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.lang3.StringUtils;

public enum PromptFormat {
    F_STRING,
    MUSTACHE;
    public static PromptFormat of(String text) {
        if (StringUtils.isBlank(text)) {
            return MUSTACHE;
        }
        try {
            return PromptFormat.valueOf(text.toUpperCase());
        } catch (IllegalArgumentException e) {
            return MUSTACHE;
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
