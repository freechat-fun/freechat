package fun.freechat.service.ai;

import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.community.model.dashscope.QwenEmbeddingModel;
import dev.langchain4j.community.model.dashscope.QwenLanguageModel;
import dev.langchain4j.community.model.dashscope.QwenStreamingChatModel;
import dev.langchain4j.community.model.dashscope.QwenStreamingLanguageModel;
import dev.langchain4j.model.azure.AzureOpenAiChatModel;
import dev.langchain4j.model.azure.AzureOpenAiEmbeddingModel;
import dev.langchain4j.model.azure.AzureOpenAiLanguageModel;
import dev.langchain4j.model.azure.AzureOpenAiStreamingChatModel;
import dev.langchain4j.model.azure.AzureOpenAiStreamingLanguageModel;
import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.model.ollama.OllamaLanguageModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import dev.langchain4j.model.ollama.OllamaStreamingLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiLanguageModel;
import dev.langchain4j.model.openai.OpenAiModerationModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingLanguageModel;
import dev.langchain4j.model.openai.OpenAiTokenCountEstimator;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class LanguageModelFactory {
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(60L);

    private LanguageModelFactory() {}

    public static OpenAiLanguageModel createOpenAiLanguageModel(
            String apiKey, String modelName, Map<String, Object> parameters) {
        return OpenAiLanguageModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .timeout(DEFAULT_TIMEOUT)
                .baseUrl(getString(parameters, "baseUrl"))
                .temperature(getDouble(parameters, "temperature"))
                .build();
    }

    public static OpenAiStreamingLanguageModel createOpenAiStreamingLanguageModel(
            String apiKey, String modelName, Map<String, Object> parameters) {
        return OpenAiStreamingLanguageModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .timeout(DEFAULT_TIMEOUT)
                .baseUrl(getString(parameters, "baseUrl"))
                .temperature(getDouble(parameters, "temperature"))
                .build();
    }

    public static OpenAiChatModel createOpenAiChatModel(
            String apiKey, String modelName, Map<String, Object> parameters) {
        return OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .timeout(DEFAULT_TIMEOUT)
                .baseUrl(getString(parameters, "baseUrl"))
                .temperature(getDouble(parameters, "temperature"))
                .topP(getDouble(parameters, "topP"))
                .maxTokens(getInteger(parameters, "maxTokens"))
                .presencePenalty(getDouble(parameters, "presencePenalty"))
                .frequencyPenalty(getDouble(parameters, "frequencyPenalty"))
                .stop((List<String>) parameters.get("stop"))
                .seed(getInteger(parameters, "seed"))
                .responseFormat(getString(parameters, "responseFormat"))
                .build();
    }

    public static OpenAiStreamingChatModel createOpenAiStreamingChatModel(
            String apiKey, String modelName, Map<String, Object> parameters) {
        return OpenAiStreamingChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .timeout(DEFAULT_TIMEOUT)
                .baseUrl((String) parameters.get("baseUrl"))
                .temperature(getDouble(parameters, "temperature"))
                .topP(getDouble(parameters, "topP"))
                .maxTokens(getInteger(parameters, "maxTokens"))
                .presencePenalty(getDouble(parameters, "presencePenalty"))
                .frequencyPenalty(getDouble(parameters, "frequencyPenalty"))
                .stop((List<String>) parameters.get("stop"))
                .seed(getInteger(parameters, "seed"))
                .responseFormat(getString(parameters, "responseFormat"))
                .build();
    }

    public static OpenAiEmbeddingModel createOpenAiEmbeddingModel(
            String apiKey, String modelName, Map<String, Object> parameters) {
        return OpenAiEmbeddingModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .baseUrl(getString(parameters, "baseUrl"))
                .build();
    }

    public static OpenAiModerationModel createOpenAiModerationModel(
            String apiKey, String modelName, Map<String, Object> parameters) {
        return OpenAiModerationModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .timeout(DEFAULT_TIMEOUT)
                .baseUrl(getString(parameters, "baseUrl"))
                .build();
    }

    public static AzureOpenAiLanguageModel createAzureOpenAiLanguageModel(
            String apiKey, String modelName, Map<String, Object> parameters) {
        return AzureOpenAiLanguageModel.builder()
                .apiKey(apiKey)
                .endpoint(getString(parameters, "baseUrl"))
                .deploymentName(modelName)
                .maxTokens(getInteger(parameters, "maxTokens"))
                .temperature(getDouble(parameters, "temperature"))
                .topP(getDouble(parameters, "topP"))
                .user(getString(parameters, "user"))
                .bestOf(getInteger(parameters, "bestOf"))
                .echo(false)
                .stop((List<String>) parameters.get("stop"))
                .presencePenalty(getDouble(parameters, "presencePenalty"))
                .frequencyPenalty(getDouble(parameters, "frequencyPenalty"))
                .timeout(DEFAULT_TIMEOUT)
                .build();
    }

    public static AzureOpenAiStreamingLanguageModel createAzureOpenAiStreamingLanguageModel(
            String apiKey, String modelName, Map<String, Object> parameters) {
        return AzureOpenAiStreamingLanguageModel.builder()
                .apiKey(apiKey)
                .endpoint(getString(parameters, "baseUrl"))
                .deploymentName(modelName)
                .maxTokens(getInteger(parameters, "maxTokens"))
                .temperature(getDouble(parameters, "temperature"))
                .topP(getDouble(parameters, "topP"))
                .user(getString(parameters, "user"))
                .echo(false)
                .stop((List<String>) parameters.get("stop"))
                .presencePenalty(getDouble(parameters, "presencePenalty"))
                .frequencyPenalty(getDouble(parameters, "frequencyPenalty"))
                .tokenCountEstimator(new OpenAiTokenCountEstimator(modelName))
                .timeout(DEFAULT_TIMEOUT)
                .build();
    }

    public static AzureOpenAiChatModel createAzureOpenAiChatModel(
            String apiKey, String modelName, Map<String, Object> parameters) {
        return AzureOpenAiChatModel.builder()
                .apiKey(apiKey)
                .endpoint(getString(parameters, "baseUrl"))
                .deploymentName(modelName)
                .maxTokens(getInteger(parameters, "maxTokens"))
                .temperature(getDouble(parameters, "temperature"))
                .topP(getDouble(parameters, "topP"))
                .user(getString(parameters, "user"))
                .stop((List<String>) parameters.get("stop"))
                .seed(getLong(parameters, "seed"))
                .responseFormat("json_object".equals(parameters.get("responseFormat")) ?
                        ResponseFormat.JSON : ResponseFormat.TEXT)
                .presencePenalty(getDouble(parameters, "presencePenalty"))
                .frequencyPenalty(getDouble(parameters, "frequencyPenalty"))
                .timeout(DEFAULT_TIMEOUT)
                .build();
    }

    public static AzureOpenAiStreamingChatModel createAzureOpenAiStreamingChatModel(
            String apiKey, String modelName, Map<String, Object> parameters) {
        return AzureOpenAiStreamingChatModel.builder()
                .apiKey(apiKey)
                .endpoint(getString(parameters, "baseUrl"))
                .deploymentName(modelName)
                .maxTokens(getInteger(parameters, "maxTokens"))
                .temperature(getDouble(parameters, "temperature"))
                .topP(getDouble(parameters, "topP"))
                .user(getString(parameters, "user"))
                .stop((List<String>) parameters.get("stop"))
                .seed(getLong(parameters, "seed"))
                .responseFormat("json_object".equals(parameters.get("responseFormat")) ?
                        ResponseFormat.JSON : ResponseFormat.TEXT)
                .presencePenalty(getDouble(parameters, "presencePenalty"))
                .frequencyPenalty(getDouble(parameters, "frequencyPenalty"))
                .tokenCountEstimator(new OpenAiTokenCountEstimator(modelName))
                .timeout(DEFAULT_TIMEOUT)
                .build();
    }

    public static AzureOpenAiEmbeddingModel createAzureOpenAiEmbeddingModel(
            String apiKey, String modelName, Map<String, Object> parameters) {
        return AzureOpenAiEmbeddingModel.builder()
                .apiKey(apiKey)
                .endpoint(getString(parameters, "baseUrl"))
                .deploymentName(modelName)
                .build();
    }

    public static OllamaLanguageModel createOllamaLanguageModel(
            String modelName, Map<String, Object> parameters) {
        return OllamaLanguageModel.builder()
                .baseUrl(getString(parameters, "baseUrl"))
                .modelName(modelName)
                .temperature(getDouble(parameters, "temperature"))
                .topK(getInteger(parameters, "topK"))
                .topP(getDouble(parameters, "topP"))
                .repeatPenalty(getDouble(parameters, "repeatPenalty"))
                .seed(getInteger(parameters, "seed"))
                .numPredict(getInteger(parameters, "numPredict"))
                .numCtx(getInteger(parameters, "numCtx"))
                .stop((List<String>) parameters.get("stop"))
                .responseFormat("json".equals(getString(parameters, "format")) ?
                        ResponseFormat.JSON : ResponseFormat.TEXT)
                .build();
    }

    public static OllamaStreamingLanguageModel createOllamaStreamingLanguageModel (
            String modelName, Map<String, Object> parameters) {
        return OllamaStreamingLanguageModel.builder()
                .baseUrl(getString(parameters, "baseUrl"))
                .modelName(modelName)
                .temperature(getDouble(parameters, "temperature"))
                .topK(getInteger(parameters, "topK"))
                .topP(getDouble(parameters, "topP"))
                .repeatPenalty(getDouble(parameters, "repeatPenalty"))
                .seed(getInteger(parameters, "seed"))
                .numPredict(getInteger(parameters, "numPredict"))
                .numCtx(getInteger(parameters, "numCtx"))
                .stop((List<String>) parameters.get("stop"))
                .responseFormat("json".equals(getString(parameters, "format")) ?
                        ResponseFormat.JSON : ResponseFormat.TEXT)
                .build();
    }

    public static OllamaChatModel createOllamaChatModel(
            String modelName, Map<String, Object> parameters) {
        return OllamaChatModel.builder()
                .baseUrl(getString(parameters, "baseUrl"))
                .modelName(modelName)
                .temperature(getDouble(parameters, "temperature"))
                .topK(getInteger(parameters, "topK"))
                .topP(getDouble(parameters, "topP"))
                .repeatPenalty(getDouble(parameters, "repeatPenalty"))
                .seed(getInteger(parameters, "seed"))
                .numPredict(getInteger(parameters, "numPredict"))
                .numCtx(getInteger(parameters, "numCtx"))
                .stop((List<String>) parameters.get("stop"))
                .responseFormat("json".equals(getString(parameters, "format")) ?
                        ResponseFormat.JSON : ResponseFormat.TEXT)
                .build();
    }

    public static OllamaStreamingChatModel createOllamaStreamingChatModel(
            String modelName, Map<String, Object> parameters) {
        return OllamaStreamingChatModel.builder()
                .baseUrl(getString(parameters, "baseUrl"))
                .modelName(modelName)
                .temperature(getDouble(parameters, "temperature"))
                .topK(getInteger(parameters, "topK"))
                .topP(getDouble(parameters, "topP"))
                .repeatPenalty(getDouble(parameters, "repeatPenalty"))
                .seed(getInteger(parameters, "seed"))
                .numPredict(getInteger(parameters, "numPredict"))
                .numCtx(getInteger(parameters, "numCtx"))
                .stop((List<String>) parameters.get("stop"))
                .responseFormat("json".equals(getString(parameters, "format")) ?
                        ResponseFormat.JSON : ResponseFormat.TEXT)
                .build();
    }

    public static OllamaEmbeddingModel createOllamaEmbeddingModel (
            String modelName, Map<String, Object> parameters) {
        return OllamaEmbeddingModel.builder()
                .baseUrl(getString(parameters, "baseUrl"))
                .modelName(modelName)
                .build();
    }

    public static QwenLanguageModel createQwenLanguageModel(
            String apiKey, String modelName, Map<String, Object> parameters) {
        return QwenLanguageModel.builder()
                .apiKey(apiKey)
                .baseUrl(getString(parameters, "baseUrl"))
                .modelName(modelName)
                .topP(getDouble(parameters, "topP"))
                .topK(getInteger(parameters, "topK"))
                .enableSearch(getBoolean(parameters, "enableSearch"))
                .seed(getInteger(parameters, "seed"))
                .repetitionPenalty(getFloat(parameters, "repetitionPenalty"))
                .temperature(getFloat(parameters, "temperature"))
                .maxTokens(getInteger(parameters, "maxTokens"))
                .stops((List<String>) parameters.get("stops"))
                .build();
    }

    public static QwenStreamingLanguageModel createQwenStreamingLanguageModel (
            String apiKey, String modelName, Map<String, Object> parameters) {
        return QwenStreamingLanguageModel.builder()
                .apiKey(apiKey)
                .baseUrl(getString(parameters, "baseUrl"))
                .modelName(modelName)
                .topP(getDouble(parameters, "topP"))
                .topK(getInteger(parameters, "topK"))
                .enableSearch(getBoolean(parameters, "enableSearch"))
                .seed(getInteger(parameters, "seed"))
                .repetitionPenalty(getFloat(parameters, "repetitionPenalty"))
                .temperature(getFloat(parameters, "temperature"))
                .maxTokens(getInteger(parameters, "maxTokens"))
                .stops((List<String>) parameters.get("stops"))
                .build();
    }

    public static QwenChatModel createQwenChatModel(
            String apiKey, String modelName, Map<String, Object> parameters) {
        return QwenChatModel.builder()
                .apiKey(apiKey)
                .baseUrl(getString(parameters, "baseUrl"))
                .modelName(modelName)
                .topP(getDouble(parameters, "topP"))
                .topK(getInteger(parameters, "topK"))
                .enableSearch(getBoolean(parameters, "enableSearch"))
                .seed(getInteger(parameters, "seed"))
                .repetitionPenalty(getFloat(parameters, "repetitionPenalty"))
                .temperature(getFloat(parameters, "temperature"))
                .maxTokens(getInteger(parameters, "maxTokens"))
                .stops((List<String>) parameters.get("stops"))
                .build();
    }

    public static QwenStreamingChatModel createQwenStreamingChatModel(
            String apiKey, String modelName, Map<String, Object> parameters) {
        return QwenStreamingChatModel.builder()
                .apiKey(apiKey)
                .baseUrl(getString(parameters, "baseUrl"))
                .modelName(modelName)
                .topP(getDouble(parameters, "topP"))
                .topK(getInteger(parameters, "topK"))
                .enableSearch(getBoolean(parameters, "enableSearch"))
                .seed(getInteger(parameters, "seed"))
                .repetitionPenalty(getFloat(parameters, "repetitionPenalty"))
                .temperature(getFloat(parameters, "temperature"))
                .maxTokens(getInteger(parameters, "maxTokens"))
                .stops((List<String>) parameters.get("stops"))
                .build();
    }

    public static QwenEmbeddingModel createQwenEmbeddingModel (
            String apiKey, String modelName, Map<String, Object> parameters) {
        return QwenEmbeddingModel.builder()
                .apiKey(apiKey)
                .baseUrl(getString(parameters, "baseUrl"))
                .modelName(modelName)
                .build();
    }

    private static Float getFloat(Map<String, Object> parameters, String key) {
        Object value = parameters.get(key);
        return switch (value) {
            case Float floatValue -> floatValue;
            case Number number -> number.floatValue();
            case null, default -> null;
        };
    }

    private static Double getDouble(Map<String, Object> parameters, String key) {
        Object value = parameters.get(key);
        return switch (value) {
            case Double doubleValue -> doubleValue;
            case Number number -> number.doubleValue();
            case null, default -> null;
        };
    }

    private static Long getLong(Map<String, Object> parameters, String key) {
        Object value = parameters.get(key);
        return switch (value) {
            case Long longValue -> longValue;
            case Number number -> number.longValue();
            case null, default -> null;
        };
    }

    private static Integer getInteger(Map<String, Object> parameters, String key) {
        Object value = parameters.get(key);
        return switch (value) {
            case Integer intValue -> intValue;
            case Number number -> number.intValue();
            case null, default -> null;
        };
    }

    private static String getString(Map<String, Object> parameters, String key) {
        Object value = parameters.get(key);
        if (value instanceof String strValue) {
            return strValue;
        } else if (value != null) {
            return value.toString();
        } else {
            return null;
        }
    }

    private static Boolean getBoolean(Map<String, Object> parameters, String key) {
        Object value = parameters.get(key);
        return switch (value) {
            case Boolean boolValue -> boolValue;
            case Number number -> number.intValue() != 0;
            case String str -> "true".equalsIgnoreCase(str);
            case null, default -> null;
        };
    }
}
