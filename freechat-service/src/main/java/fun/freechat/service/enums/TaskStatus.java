package fun.freechat.service.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.lang3.StringUtils;

public enum TaskStatus {
    PENDING,
    RUNNING,
    SUCCEEDED,
    FAILED,
    CANCELED,
    UNKNOWN;
    public static TaskStatus of(String text) {
        if (StringUtils.isBlank(text)) {
            return UNKNOWN;
        }
        try {
            return TaskStatus.valueOf(text.toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
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
