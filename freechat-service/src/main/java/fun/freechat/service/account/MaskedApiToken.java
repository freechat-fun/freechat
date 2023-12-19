package fun.freechat.service.account;

import fun.freechat.model.ApiToken;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.util.Objects;

public class MaskedApiToken extends ApiToken {
    private static final int VISIBLE_SIZE = 4;

    private MaskedApiToken() {}

    public static MaskedApiToken of(ApiToken apiToken) {
        if (Objects.isNull(apiToken)) {
            throw new IllegalArgumentException("apiToken must be defined!");
        }
        MaskedApiToken maskedApiToken = new MaskedApiToken();
        BeanUtils.copyProperties(apiToken, maskedApiToken);
        String token = apiToken.getToken();
        int length =token.length();
        if (length > VISIBLE_SIZE * 2) {
            String prefix = token.substring(0, VISIBLE_SIZE);
            String suffix = token.substring(length - VISIBLE_SIZE, length);
            String stars = StringUtils.repeat('*', Math.min(VISIBLE_SIZE, length - VISIBLE_SIZE * 2));
            maskedApiToken.setToken(prefix + stars + suffix);
        } else {
            maskedApiToken.setToken(StringUtils.repeat('*', length));
        }
        return maskedApiToken;
    }
}
