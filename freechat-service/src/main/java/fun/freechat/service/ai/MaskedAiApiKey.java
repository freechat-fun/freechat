package fun.freechat.service.ai;

import fun.freechat.model.AiApiKey;
import fun.freechat.service.common.EncryptionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.util.Objects;

public class MaskedAiApiKey extends AiApiKey {
    private static final int VISIBLE_SIZE = 4;

    private MaskedAiApiKey() {}

    public static MaskedAiApiKey of(AiApiKey aiApiKey, EncryptionService encryptionService) {
        if (Objects.isNull(aiApiKey) || Objects.isNull(encryptionService)) {
            throw new IllegalArgumentException("aiApiKey and encryptionService must be defined!");
        }
        MaskedAiApiKey maskedAiApiKey = new MaskedAiApiKey();
        BeanUtils.copyProperties(aiApiKey, maskedAiApiKey);
        String token = encryptionService.decrypt(aiApiKey.getToken());
        int length = token.length();
        if (length > VISIBLE_SIZE * 2) {
            String prefix = token.substring(0, VISIBLE_SIZE);
            String suffix = token.substring(length - VISIBLE_SIZE, length);
            String stars = StringUtils.repeat('*', Math.min(VISIBLE_SIZE, length - VISIBLE_SIZE * 2));
            maskedAiApiKey.setToken(prefix + stars + suffix);
        } else {
            maskedAiApiKey.setToken(StringUtils.repeat('*', length));
        }
        return maskedAiApiKey;
    }
}
