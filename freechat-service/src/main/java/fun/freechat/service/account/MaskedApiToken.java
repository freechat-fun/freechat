package fun.freechat.service.account;

import fun.freechat.model.ApiToken;
import fun.freechat.service.util.InfoUtils;
import org.springframework.beans.BeanUtils;

import java.util.Objects;

public class MaskedApiToken extends ApiToken {
    private MaskedApiToken() {}

    public static MaskedApiToken of(ApiToken apiToken) {
        if (Objects.isNull(apiToken)) {
            throw new IllegalArgumentException("apiToken must be defined!");
        }
        MaskedApiToken maskedApiToken = new MaskedApiToken();
        BeanUtils.copyProperties(apiToken, maskedApiToken);
        String token = apiToken.getToken();
        maskedApiToken.setToken(InfoUtils.desensitize(token));
        return maskedApiToken;
    }
}
