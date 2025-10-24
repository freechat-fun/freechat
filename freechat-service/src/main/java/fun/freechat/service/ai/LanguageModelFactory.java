package fun.freechat.service.ai;

import dev.langchain4j.community.model.dashscope.*;
import dev.langchain4j.model.azure.*;
import dev.langchain4j.model.chat.request.ChatRequestParameters;
import dev.langchain4j.model.chat.request.DefaultChatRequestParameters;
import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.ollama.*;
import dev.langchain4j.model.openai.*;

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
        OpenAiChatRequestParameters modelParameters = OpenAiChatRequestParameters.builder()
                .modelName(modelName)
                .temperature(getDouble(parameters, "temperature"))
                .topP(getDouble(parameters, "topP"))
                .maxOutputTokens(getInteger(parameters, "maxTokens"))
                .presencePenalty(getDouble(parameters, "presencePenalty"))
                .frequencyPenalty(getDouble(parameters, "frequencyPenalty"))
                .stopSequences((List<String>) parameters.get("stop"))
                .seed(getInteger(parameters, "seed"))
                .responseFormat("json_object".equals(parameters.get("responseFormat")) ?
                        ResponseFormat.JSON : ResponseFormat.TEXT)
                .reasoningEffort(getString(parameters, "reasoningEffort"))
                .build();

        return OpenAiChatModel.builder()
                .apiKey(apiKey)
                .baseUrl((String) parameters.get("baseUrl"))
                .timeout(DEFAULT_TIMEOUT)
                .defaultRequestParameters(modelParameters)
                .build();
    }

    public static OpenAiStreamingChatModel createOpenAiStreamingChatModel(
            String apiKey, String modelName, Map<String, Object> parameters) {
        OpenAiChatRequestParameters modelParameters = OpenAiChatRequestParameters.builder()
                .modelName(modelName)
                .temperature(getDouble(parameters, "temperature"))
                .topP(getDouble(parameters, "topP"))
                .maxOutputTokens(getInteger(parameters, "maxTokens"))
                .presencePenalty(getDouble(parameters, "presencePenalty"))
                .frequencyPenalty(getDouble(parameters, "frequencyPenalty"))
                .stopSequences((List<String>) parameters.get("stop"))
                .seed(getInteger(parameters, "seed"))
                .responseFormat("json_object".equals(parameters.get("responseFormat")) ?
                        ResponseFormat.JSON : ResponseFormat.TEXT)
                .reasoningEffort(getString(parameters, "reasoningEffort"))
                .build();

        return OpenAiStreamingChatModel.builder()
                .apiKey(apiKey)
                .baseUrl((String) parameters.get("baseUrl"))
                .timeout(DEFAULT_TIMEOUT)
                .defaultRequestParameters(modelParameters)
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
                .timeout(DEFAULT_TIMEOUT)
                .build();
    }

    public static AzureOpenAiChatModel createAzureOpenAiChatModel(
            String apiKey, String modelName, Map<String, Object> parameters) {
        ChatRequestParameters modelParameters = DefaultChatRequestParameters.builder()
                .modelName(modelName)
                .maxOutputTokens(getInteger(parameters, "maxTokens"))
                .temperature(getDouble(parameters, "temperature"))
                .topP(getDouble(parameters, "topP"))
                .stopSequences((List<String>) parameters.get("stop"))
                .responseFormat("json_object".equals(parameters.get("responseFormat")) ?
                        ResponseFormat.JSON : ResponseFormat.TEXT)
                .presencePenalty(getDouble(parameters, "presencePenalty"))
                .frequencyPenalty(getDouble(parameters, "frequencyPenalty"))
                .build();

        return AzureOpenAiChatModel.builder()
                .apiKey(apiKey)
                .endpoint(getString(parameters, "baseUrl"))
                .deploymentName(modelName)
                .user(getString(parameters, "user"))
                .seed(getLong(parameters, "seed"))
                .defaultRequestParameters(modelParameters)
                .timeout(DEFAULT_TIMEOUT)
                .build();
    }

    public static AzureOpenAiStreamingChatModel createAzureOpenAiStreamingChatModel(
            String apiKey, String modelName, Map<String, Object> parameters) {
        ChatRequestParameters modelParameters = DefaultChatRequestParameters.builder()
                .modelName(modelName)
                .maxOutputTokens(getInteger(parameters, "maxTokens"))
                .temperature(getDouble(parameters, "temperature"))
                .topP(getDouble(parameters, "topP"))
                .stopSequences((List<String>) parameters.get("stop"))
                .responseFormat("json_object".equals(parameters.get("responseFormat")) ?
                        ResponseFormat.JSON : ResponseFormat.TEXT)
                .presencePenalty(getDouble(parameters, "presencePenalty"))
                .frequencyPenalty(getDouble(parameters, "frequencyPenalty"))
                .build();

        return AzureOpenAiStreamingChatModel.builder()
                .apiKey(apiKey)
                .endpoint(getString(parameters, "baseUrl"))
                .deploymentName(modelName)
                .user(getString(parameters, "user"))
                .seed(getLong(parameters, "seed"))
                .defaultRequestParameters(modelParameters)
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
        OllamaChatRequestParameters modelParameters = OllamaChatRequestParameters.builder()
                .modelName(modelName)
                .temperature(getDouble(parameters, "temperature"))
                .topK(getInteger(parameters, "topK"))
                .topP(getDouble(parameters, "topP"))
                .repeatPenalty(getDouble(parameters, "repeatPenalty"))
                .seed(getInteger(parameters, "seed"))
                .maxOutputTokens(getInteger(parameters, "numPredict"))
                .numCtx(getInteger(parameters, "numCtx"))
                .stopSequences((List<String>) parameters.get("stop"))
                .responseFormat("json".equals(getString(parameters, "format")) ?
                        ResponseFormat.JSON : ResponseFormat.TEXT)
                .think(getBoolean(parameters, "think"))
                .build();

        return OllamaChatModel.builder()
                .baseUrl(getString(parameters, "baseUrl"))
                .defaultRequestParameters(modelParameters)
                .build();
    }

    public static OllamaStreamingChatModel createOllamaStreamingChatModel(
            String modelName, Map<String, Object> parameters) {
        OllamaChatRequestParameters modelParameters = OllamaChatRequestParameters.builder()
                .modelName(modelName)
                .temperature(getDouble(parameters, "temperature"))
                .topK(getInteger(parameters, "topK"))
                .topP(getDouble(parameters, "topP"))
                .repeatPenalty(getDouble(parameters, "repeatPenalty"))
                .seed(getInteger(parameters, "seed"))
                .maxOutputTokens(getInteger(parameters, "numPredict"))
                .numCtx(getInteger(parameters, "numCtx"))
                .stopSequences((List<String>) parameters.get("stop"))
                .responseFormat("json".equals(getString(parameters, "format")) ?
                        ResponseFormat.JSON : ResponseFormat.TEXT)
                .think(getBoolean(parameters, "think"))
                .build();

        return OllamaStreamingChatModel.builder()
                .baseUrl(getString(parameters, "baseUrl"))
                .defaultRequestParameters(modelParameters)
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
        QwenChatRequestParameters modelParameters = QwenChatRequestParameters.builder()
                .modelName(modelName)
                .topP(getDouble(parameters, "topP"))
                .topK(getInteger(parameters, "topK"))
                .enableSearch(getBoolean(parameters, "enableSearch"))
                .seed(getInteger(parameters, "seed"))
                .temperature(getDouble(parameters, "temperature"))
                .maxOutputTokens(getInteger(parameters, "maxTokens"))
                .stopSequences((List<String>) parameters.get("stops"))
                .enableThinking(getBoolean(parameters, "enableThinking"))
                .build();

        return QwenChatModel.builder()
                .apiKey(apiKey)
                .baseUrl(getString(parameters, "baseUrl"))
                .repetitionPenalty(getFloat(parameters, "repetitionPenalty"))
                .defaultRequestParameters(modelParameters)
                .build();
    }

    public static QwenStreamingChatModel createQwenStreamingChatModel(
            String apiKey, String modelName, Map<String, Object> parameters) {
        QwenChatRequestParameters modelParameters = QwenChatRequestParameters.builder()
                .modelName(modelName)
                .topP(getDouble(parameters, "topP"))
                .topK(getInteger(parameters, "topK"))
                .enableSearch(getBoolean(parameters, "enableSearch"))
                .seed(getInteger(parameters, "seed"))
                .temperature(getDouble(parameters, "temperature"))
                .maxOutputTokens(getInteger(parameters, "maxTokens"))
                .stopSequences((List<String>) parameters.get("stops"))
                .enableThinking(getBoolean(parameters, "enableThinking"))
                .build();

        return QwenStreamingChatModel.builder()
                .apiKey(apiKey)
                .baseUrl(getString(parameters, "baseUrl"))
                .repetitionPenalty(getFloat(parameters, "repetitionPenalty"))
                .defaultRequestParameters(modelParameters)
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
