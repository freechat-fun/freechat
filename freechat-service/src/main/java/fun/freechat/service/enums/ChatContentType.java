package fun.freechat.service.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.lang3.StringUtils;

public enum ChatContentType {
    TEXT,
    IMAGE;
    public static ChatContentType of(String text) {
        if (StringUtils.isBlank(text)) {
            return TEXT;
        }
        try {
            return ChatContentType.valueOf(text.toUpperCase());
        } catch (IllegalArgumentException e) {
            return TEXT;
        }
    }

    @JsonValue
    public String text() {
        return name().toLowerCase();
    }
}
