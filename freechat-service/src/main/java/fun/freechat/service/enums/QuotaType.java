package fun.freechat.service.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.lang3.StringUtils;

public enum QuotaType {
    MESSAGES,
    TOKENS,
    NONE;
    public static QuotaType of(String text) {
        if (StringUtils.isBlank(text)) {
            return NONE;
        }
        try {
            return QuotaType.valueOf(text.toUpperCase());
        } catch (IllegalArgumentException e) {
            return NONE;
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
