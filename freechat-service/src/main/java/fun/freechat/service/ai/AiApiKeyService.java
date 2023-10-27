package fun.freechat.service.ai;

import fun.freechat.model.User;
import fun.freechat.service.enums.ModelProvider;

import java.util.List;

public interface AiApiKeyService {
    Long create(User user, String name, ModelProvider provider, String token, boolean enabled);

    boolean disable(Long id);
    boolean enable(Long id);
    boolean delete(Long id);
    CloseableAiApiKey use(Long id);
    CloseableAiApiKey use(String userId, String name);
    CloseableAiApiKey use(String token);
    MaskedAiApiKey get(Long id);
    List<MaskedAiApiKey> list(User user, ModelProvider provider);
    String getOwner(Long id);
    String getOwner(String userId, String name);
}
