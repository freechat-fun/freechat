package fun.freechat.service.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.lang3.StringUtils;

public enum ChatVar {
    CHARACTER_NICKNAME,
    CHARACTER_DESCRIPTION,
    CHARACTER_GENDER,
    CHARACTER_LANG,
    CHARACTER_PROFILE,
    CHARACTER_CHAT_STYLE,
    CHARACTER_CHAT_EXAMPLE,
    CHARACTER_GREETING,
    USER_NICKNAME,
    USER_PROFILE,
    RELEVANT_INFORMATION,
    CHAT_CONTEXT,
    MESSAGE_CONTEXT,
    CURRENT_TIME,
    PROACTIVE_CHAT_PROMPT,
    INPUT,
    ATTACHMENT,
    UNKNOWN;
    public static ChatVar of(String text) {
        if (StringUtils.isBlank(text)) {
            return UNKNOWN;
        }
        try {
            return ChatVar.valueOf(text.toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }

    @JsonValue
    public String text() {
        return name();
    }

    @Override
    public String toString() {
        return text();
    }

}
