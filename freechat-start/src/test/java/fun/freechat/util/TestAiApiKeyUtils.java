package fun.freechat.util;

import fun.freechat.api.dto.AiModelInfoDTO;
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

import static fun.freechat.AbstractIntegrationTest.ollama;
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
        if (user == null) {
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

    public static String apiKeyFor(ModelProvider provider) {
        return switch (provider) {
            case OPEN_AI -> System.getenv("OPENAI_API_KEY");
            case AZURE_OPEN_AI -> System.getenv("AZURE_OPENAI_KEY");
            case DASH_SCOPE -> System.getenv("DASHSCOPE_API_KEY");
            case OLLAMA -> "placeholder";
            default -> null;
        };
    }

    public static String apiKeyFor(String modelId) {
        AiModelInfoDTO aiModelInfo = AiModelInfoDTO.from(modelId);
        return apiKeyFor(ModelProvider.of(aiModelInfo.getProvider()));
    }

    public static String baseUrlFor(ModelProvider provider) {
        return switch (provider) {
            case OPEN_AI -> System.getenv("OPENAI_BASE_URL");
            case AZURE_OPEN_AI -> System.getenv("AZURE_OPENAI_ENDPOINT");
            case DASH_SCOPE -> System.getenv("DASHSCOPE_BASE_URL");
            case OLLAMA -> ollama().getEndpoint();
            default -> null;
        };
    }

    public static String baseUrlFor(String modelId) {
        AiModelInfoDTO aiModelInfo = AiModelInfoDTO.from(modelId);
        return baseUrlFor(ModelProvider.of(aiModelInfo.getProvider()));
    }

    public static String keyNameFor(ModelProvider provider) {
        return "test_api_key_" + provider.text();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        userService = applicationContext.getBean(SysUserService.class);
        aiApiKeyService = applicationContext.getBean(AiApiKeyService.class);
    }
}
