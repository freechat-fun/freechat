package fun.freechat.access.auth.customizer;

import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

@Component
@SuppressWarnings("unused")
public class GoogleOAuth2AuthorizationRequestCustomizer implements BiConsumer<OAuth2AuthorizationRequest.Builder, Boolean> {
    @Override
    public void accept(OAuth2AuthorizationRequest.Builder builder, Boolean isBinding) {
        Map<String, Object> extraParams = HashMap.newHashMap(3);
        extraParams.put("include_granted_scopes", true);
        if (Boolean.TRUE.equals(isBinding)) {
            extraParams.put("access_type", "offline");
            extraParams.put("prompt", "consent");
        } else {
            extraParams.put("access_type", "online");
        }
        builder.additionalParameters(extraParams);
    }
}
