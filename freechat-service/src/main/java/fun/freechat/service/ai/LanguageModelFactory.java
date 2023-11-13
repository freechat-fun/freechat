package fun.freechat.service.ai;

import dev.langchain4j.model.dashscope.*;
import dev.langchain4j.model.openai.*;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class LanguageModelFactory {
    static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(60L);

    public static OpenAiLanguageModel createOpenAiLanguageModel(
            String apiKey, String modelName, Map<String, Object> parameters) {
        return OpenAiLanguageModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .timeout(DEFAULT_TIMEOUT)
                .baseUrl((String) parameters.get("baseUrl"))
                .temperature((Double) parameters.get("temperature"))
                .build();
    }

    public static OpenAiStreamingLanguageModel createOpenAiStreamingLanguageModel(
            String apiKey, String modelName, Map<String, Object> parameters) {
        return OpenAiStreamingLanguageModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .timeout(DEFAULT_TIMEOUT)
                .baseUrl((String) parameters.get("baseUrl"))
                .temperature((Double) parameters.get("temperature"))
                .build();
    }

    public static OpenAiChatModel createOpenAiChatModel(
            String apiKey, String modelName, Map<String, Object> parameters) {
        return OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .timeout(DEFAULT_TIMEOUT)
                .baseUrl((String) parameters.get("baseUrl"))
                .temperature((Double) parameters.get("temperature"))
                .topP((Double) parameters.get("topP"))
                .maxTokens((Integer) parameters.get("maxTokens"))
                .presencePenalty((Double) parameters.get("presencePenalty"))
                .frequencyPenalty((Double) parameters.get("frequencyPenalty"))
                .stop((List<String>) parameters.get("stop"))
                .build();
    }

    public static OpenAiStreamingChatModel createOpenAiStreamingChatModel(
            String apiKey, String modelName, Map<String, Object> parameters) {
        return OpenAiStreamingChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .timeout(DEFAULT_TIMEOUT)
                .baseUrl((String) parameters.get("baseUrl"))
                .temperature((Double) parameters.get("temperature"))
                .topP((Double) parameters.get("topP"))
                .maxTokens((Integer) parameters.get("maxTokens"))
                .presencePenalty((Double) parameters.get("presencePenalty"))
                .frequencyPenalty((Double) parameters.get("frequencyPenalty"))
                .stop((List<String>) parameters.get("stop"))
                .build();
    }

    public static OpenAiEmbeddingModel createOpenAiEmbeddingModel(
            String apiKey, String modelName, Map<String, Object> parameters) {
        return OpenAiEmbeddingModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .baseUrl((String) parameters.get("baseUrl"))
                .build();
    }

    public static OpenAiModerationModel createOpenAiModerationModel(
            String apiKey, String modelName, Map<String, Object> parameters) {
        return OpenAiModerationModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .timeout(DEFAULT_TIMEOUT)
                .baseUrl((String) parameters.get("baseUrl"))
                .build();
    }

    public static QwenLanguageModel createQwenLanguageModel(
            String apiKey, String modelName, Map<String, Object> parameters) {
        return QwenLanguageModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .topP((Double) parameters.get("topP"))
                .topK((Integer) parameters.get("topK"))
                .enableSearch((Boolean) parameters.get("enableSearch"))
                .seed((Integer) parameters.get("seed"))
                .build();
    }

    public static QwenStreamingLanguageModel createQwenStreamingLanguageModel (
            String apiKey, String modelName, Map<String, Object> parameters) {
        return QwenStreamingLanguageModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .topP((Double) parameters.get("topP"))
                .topK((Integer) parameters.get("topK"))
                .enableSearch((Boolean) parameters.get("enableSearch"))
                .seed((Integer) parameters.get("seed"))
                .build();
    }

    public static QwenChatModel createQwenChatModel(
            String apiKey, String modelName, Map<String, Object> parameters) {
        return QwenChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .topP((Double) parameters.get("topP"))
                .topK((Integer) parameters.get("topK"))
                .enableSearch((Boolean) parameters.get("enableSearch"))
                .seed((Integer) parameters.get("seed"))
                .build();
    }

    public static QwenStreamingChatModel createQwenStreamingChatModel(
            String apiKey, String modelName, Map<String, Object> parameters) {
        return QwenStreamingChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .topP((Double) parameters.get("topP"))
                .topK((Integer) parameters.get("topK"))
                .enableSearch((Boolean) parameters.get("enableSearch"))
                .seed((Integer) parameters.get("seed"))
                .build();
    }

    public static QwenEmbeddingModel createQwenEmbeddingModel (
            String apiKey, String modelName, Map<String, Object> ignoredParameters) {
        return QwenEmbeddingModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .build();
    }
}
