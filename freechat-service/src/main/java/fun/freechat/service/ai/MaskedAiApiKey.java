package fun.freechat.service.ai;

import fun.freechat.model.AiApiKey;
import fun.freechat.service.common.EncryptionService;
import fun.freechat.service.util.InfoUtils;
import org.springframework.beans.BeanUtils;

public class MaskedAiApiKey extends AiApiKey {
    private MaskedAiApiKey() {}

    public static MaskedAiApiKey of(AiApiKey aiApiKey, EncryptionService encryptionService) {
        if (aiApiKey == null || encryptionService == null) {
            throw new IllegalArgumentException("aiApiKey and encryptionService must be defined!");
        }
        MaskedAiApiKey maskedAiApiKey = new MaskedAiApiKey();
        BeanUtils.copyProperties(aiApiKey, maskedAiApiKey);
        String token = encryptionService.decrypt(aiApiKey.getToken());
        maskedAiApiKey.setToken(InfoUtils.desensitize(token));
        return maskedAiApiKey;
    }
}
