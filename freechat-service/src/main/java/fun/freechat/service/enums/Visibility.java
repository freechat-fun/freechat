package fun.freechat.service.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.lang3.StringUtils;

@SuppressWarnings("unused")
public enum Visibility {
    HIDDEN,
    PRIVATE,
    PUBLIC_ORG,  // public in an organization
    PUBLIC;

    public static Visibility of(String text) {
        if (StringUtils.isBlank(text)) {
            return PUBLIC;
        }
        try {
            return Visibility.valueOf(text.toUpperCase());
        } catch (IllegalArgumentException e) {
            return PUBLIC;
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
