package fun.freechat.service.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.lang3.StringUtils;

public enum ModelProvider {
    HUGGING_FACE,
    OPEN_AI,
    AZURE_OPEN_AI,
    LOCAL_AI,
    IN_PROCESS,
    DASH_SCOPE,
    UNKNOWN;
    public static ModelProvider of(String text) {
        if (StringUtils.isBlank(text)) {
            return UNKNOWN;
        }
        try {
            return ModelProvider.valueOf(text.toUpperCase());
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

    public static Boolean hasPrivateEndpoint(ModelProvider provider) {
        return provider == AZURE_OPEN_AI;
    }

    public static Boolean hasPublicEndpoint(ModelProvider provider) {
        return !hasPrivateEndpoint(provider);
    }
}
