package fun.freechat.service.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.lang3.StringUtils;

public enum TtsSpeakerType {
    IDX,
    WAV;
    public static TtsSpeakerType of(String text) {
        if (StringUtils.isBlank(text)) {
            return IDX;
        }
        try {
            return TtsSpeakerType.valueOf(text.toUpperCase());
        } catch (IllegalArgumentException e) {
            return IDX;
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
