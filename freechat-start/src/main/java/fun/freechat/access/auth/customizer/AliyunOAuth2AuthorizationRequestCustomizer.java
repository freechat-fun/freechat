package fun.freechat.access.auth.customizer;

import fun.freechat.service.account.SysBindService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

@Component
@SuppressWarnings("unused")
public class AliyunOAuth2AuthorizationRequestCustomizer implements BiConsumer<OAuth2AuthorizationRequest.Builder, Boolean> {
    @Autowired
    private SysBindService bindService;

    @Override
    public void accept(OAuth2AuthorizationRequest.Builder builder, Boolean isBinding) {
        Map<String, Object> extraParams = new HashMap<>(2);
        if (isBinding) {
            extraParams.put("access_type", "offline");
            extraParams.put("prompt", "admin_consent");
        } else {
            extraParams.put("access_type", "online");
        }
        builder.additionalParameters(extraParams);
    }
}
