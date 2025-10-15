package fun.freechat.util;

import fun.freechat.service.enums.ModelProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static fun.freechat.util.TestAiApiKeyUtils.baseUrlFor;

public class TestCommonUtils {
    public static void waitAWhile() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            // ignored
        }
    }

    public static String defaultModelFor(ModelProvider provider) {
        return switch (provider) {
            case OPEN_AI, AZURE_OPEN_AI -> "gpt-4o-mini";
            case DASH_SCOPE -> "qwen3-max";
            case OLLAMA -> "llama3.2:1b|text2chat";
            default -> null;
        };
    }

    public static String defaultEmbeddingModelFor(ModelProvider provider) {
        return switch (provider) {
            case OPEN_AI, AZURE_OPEN_AI -> "text-embedding-ada-002";
            case DASH_SCOPE -> "text-embedding-v2";
            case OLLAMA -> "all-minilm|embedding";
            default -> null;
        };
    }

    public static Map<String, Object> parametersFor(String modelId) {
        Map<String, Object> param = new HashMap<>();
        param.put("topP", 0.8d);
        param.put("seed", new Random().nextInt(0, Integer.MAX_VALUE));
        param.put("maxTokens", 1000);
        param.put("temperature", 0.0d);
        param.put("modelId", modelId);
        param.put("baseUrl", baseUrlFor(modelId));
        return param;
    }
}
