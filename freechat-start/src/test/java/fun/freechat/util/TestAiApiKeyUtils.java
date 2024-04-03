package fun.freechat.util;

import fun.freechat.model.User;
import fun.freechat.service.account.SysUserService;
import fun.freechat.service.ai.AiApiKeyService;
import fun.freechat.service.ai.MaskedAiApiKey;
import fun.freechat.service.enums.ModelProvider;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Component
public class TestAiApiKeyUtils implements ApplicationContextAware {
    private static SysUserService userService;
    private static AiApiKeyService aiApiKeyService;

    public static void addAiApiKey(String userId,
                                   String name,
                                   ModelProvider provider,
                                   String token,
                                   boolean enabled) {
        User user = userService.loadByUserId(userId);
        assertNotNull(user);
        aiApiKeyService.create(user, name, provider, token, enabled);
    }

    public static void cleanAiApiKeys(String userId) {
        User user = userService.loadByUserId(userId);
        if (Objects.isNull(user)) {
            return;
        }

        for (var provider : ModelProvider.values()) {
            List<MaskedAiApiKey> apiKeys = aiApiKeyService.list(user, provider);
            if (CollectionUtils.isEmpty(apiKeys)) {
                continue;
            }
            for (var apiKey : apiKeys) {
                aiApiKeyService.delete(apiKey.getId());
            }
        }
    }

    public static String apiKeyOfDashScope() {
        return System.getenv("DASHSCOPE_API_KEY");
    }

    public static String apiKeyOfOpenAI() {
        return System.getenv("OPENAI_API_KEY");
    }

    public static String apiKeyFor(String modelId) {
        if (modelId.startsWith("[dash_scope]")) {
            return apiKeyOfDashScope();
        } else if (modelId.startsWith("[open_ai]")) {
            return apiKeyOfOpenAI();
        } else {
            return null;
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        userService = applicationContext.getBean(SysUserService.class);
        aiApiKeyService = applicationContext.getBean(AiApiKeyService.class);
    }
}
