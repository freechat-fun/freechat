package fun.freechat.service.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.lang3.StringUtils;

public enum PromptFormat {
    F_STRING,
    JINJA2,
    MUSTACHE;
    public static PromptFormat of(String text) {
        if (StringUtils.isBlank(text)) {
            return F_STRING;
        }
        try {
            return PromptFormat.valueOf(text.toUpperCase());
        } catch (IllegalArgumentException e) {
            return F_STRING;
        }
    }

    @JsonValue
    public String text() {
        return name().toLowerCase();
    }
}
