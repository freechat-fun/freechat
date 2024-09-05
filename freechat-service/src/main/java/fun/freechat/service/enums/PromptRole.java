package fun.freechat.service.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.lang3.StringUtils;

public enum PromptRole {
    SYSTEM,
    ASSISTANT,
    USER,
    FUNCTION_CALL,
    FUNCTION_RESULT;
    public static PromptRole of(String text) {
        if (StringUtils.isBlank(text)) {
            return USER;
        }
        try {
            return PromptRole.valueOf(text.toUpperCase());
        } catch (IllegalArgumentException e) {
            return USER;
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
