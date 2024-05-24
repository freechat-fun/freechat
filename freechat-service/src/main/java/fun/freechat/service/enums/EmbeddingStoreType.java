package fun.freechat.service.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.lang3.StringUtils;

public enum EmbeddingStoreType {
    EN_CHARACTER_DOCUMENT,
    EN_LONG_TERM_MEMORY,
    ZH_CHARACTER_DOCUMENT,
    ZH_LONG_TERM_MEMORY,
    DEFAULT_CHARACTER_DOCUMENT,
    DEFAULT_LONG_TERM_MEMORY;
    public static EmbeddingStoreType of(String text) {
        if (StringUtils.isBlank(text)) {
            return DEFAULT_CHARACTER_DOCUMENT;
        }
        try {
            return EmbeddingStoreType.valueOf(text.toUpperCase());
        } catch (IllegalArgumentException e) {
            return DEFAULT_CHARACTER_DOCUMENT;
        }
    }

    public static EmbeddingStoreType documentTypeForLang(String lang) {
        if ("en".equalsIgnoreCase(lang)) {
            return EN_CHARACTER_DOCUMENT;
        } else if ("zh".equalsIgnoreCase(lang) || "zh_CN".equalsIgnoreCase(lang) || "zh_TW".equalsIgnoreCase(lang)) {
            return ZH_CHARACTER_DOCUMENT;
        } else {
            return DEFAULT_CHARACTER_DOCUMENT;
        }
    }

    public static EmbeddingStoreType longTermMemoryTypeForLang(String lang) {
        if ("en".equalsIgnoreCase(lang)) {
            return EN_LONG_TERM_MEMORY;
        } else if ("zh".equalsIgnoreCase(lang) || "zh_CN".equalsIgnoreCase(lang) || "zh_TW".equalsIgnoreCase(lang)) {
            return ZH_LONG_TERM_MEMORY;
        } else {
            return DEFAULT_LONG_TERM_MEMORY;
        }
    }

    @JsonValue
    public String text() {
        return name().toLowerCase();
    }
}
