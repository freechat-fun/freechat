package fun.freechat.service.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.lang3.StringUtils;

public enum EmbeddingRecordMeta {
    MEMORY_ID,
    TASK_ID,
    USER_MESSAGE_ID,
    AI_MESSAGE_ID,
    UNKNOWN;
    public static EmbeddingRecordMeta of(String text) {
        if (StringUtils.isBlank(text)) {
            return UNKNOWN;
        }
        try {
            return EmbeddingRecordMeta.valueOf(text.toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }

    @JsonValue
    public String text() {
        return name().toLowerCase();
    }
}
