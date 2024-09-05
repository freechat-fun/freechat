package fun.freechat.service.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.lang3.StringUtils;

public enum GenderType {
    MALE,
    FEMALE,
    OTHER;
    public static GenderType of(String text) {
        if (StringUtils.isBlank(text)) {
            return OTHER;
        }
        try {
            return GenderType.valueOf(text.toUpperCase());
        } catch (IllegalArgumentException e) {
            return OTHER;
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
