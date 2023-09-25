package fun.freechat.service.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.lang3.StringUtils;

public enum TaskStatus {
    PENDING,
    RUNNING,
    SUCCEEDED,
    FAILED;
    public static TaskStatus of(String text) {
        if (StringUtils.isBlank(text)) {
            return PENDING;
        }
        try {
            return TaskStatus.valueOf(text.toUpperCase());
        } catch (IllegalArgumentException e) {
            return PENDING;
        }
    }

    @JsonValue
    public String text() {
        return name().toLowerCase();
    }
}
