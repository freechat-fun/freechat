package fun.freechat.service.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public enum ApiTokenType {
    ACCESS("access");
    private final String type;

    ApiTokenType(String type) {
        this.type = type;
    }

    public ApiTokenType fromType(String type) {
        if (StringUtils.isBlank(type)) {
            return ACCESS;
        }

        for (ApiTokenType tokenType : values()) {
            if (type.equalsIgnoreCase(tokenType.getType())) {
                return tokenType;
            }
        }

        return ACCESS;
    }

    @JsonValue
    public String text() {
        return type;
    }
    @Override
    public String toString() {
        return text();
    }
}
