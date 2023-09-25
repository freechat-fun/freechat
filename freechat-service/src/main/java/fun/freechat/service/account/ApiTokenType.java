package fun.freechat.service.account;

import org.apache.commons.lang3.StringUtils;

public enum ApiTokenType {
    ACCESS("access");
    private final String type;

    ApiTokenType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
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
}
