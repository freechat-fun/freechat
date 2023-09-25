package fun.freechat.service.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.lang3.StringUtils;

public enum PromptType {
    STRING,
    CHAT;
    public static PromptType of(String text) {
        if (StringUtils.isBlank(text)) {
            return STRING;
        }
        try {
            return PromptType.valueOf(text.toUpperCase());
        } catch (IllegalArgumentException e) {
            return STRING;
        }
    }

    @JsonValue
    public String text() {
        return name().toLowerCase();
    }
}
