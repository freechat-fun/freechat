package fun.freechat.service.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.lang3.StringUtils;

public enum EmbeddingStoreType {
    DOCUMENT,
    LONG_TERM_MEMORY;
    public static EmbeddingStoreType of(String text) {
        if (StringUtils.isBlank(text)) {
            return DOCUMENT;
        }
        try {
            return EmbeddingStoreType.valueOf(text.toUpperCase());
        } catch (IllegalArgumentException e) {
            return DOCUMENT;
        }
    }

    @JsonValue
    public String text() {
        return name().toLowerCase();
    }
}
