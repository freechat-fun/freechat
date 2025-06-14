package fun.freechat.service.prompt.impl;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.language.LanguageModel;
import dev.langchain4j.model.language.StreamingLanguageModel;
import fun.freechat.service.ai.AiModelInfo;
import fun.freechat.model.User;
import fun.freechat.service.ai.AiApiKeyService;
import fun.freechat.service.ai.CloseableAiApiKey;
import fun.freechat.service.enums.ModelProvider;
import fun.freechat.service.enums.ModelType;
import fun.freechat.service.enums.PromptType;
import fun.freechat.service.prompt.ChatPromptContent;
import fun.freechat.service.prompt.PromptAiService;
import fun.freechat.service.util.InfoUtils;
import fun.freechat.service.util.PromptUtils;
import fun.freechat.util.PojoUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static fun.freechat.service.ai.LanguageModelFactory.createAzureOpenAiChatModel;
import static fun.freechat.service.ai.LanguageModelFactory.createAzureOpenAiEmbeddingModel;
import static fun.freechat.service.ai.LanguageModelFactory.createAzureOpenAiLanguageModel;
import static fun.freechat.service.ai.LanguageModelFactory.createAzureOpenAiStreamingChatModel;
import static fun.freechat.service.ai.LanguageModelFactory.createAzureOpenAiStreamingLanguageModel;
import static fun.freechat.service.ai.LanguageModelFactory.createOllamaChatModel;
import static fun.freechat.service.ai.LanguageModelFactory.createOllamaEmbeddingModel;
import static fun.freechat.service.ai.LanguageModelFactory.createOllamaLanguageModel;
import static fun.freechat.service.ai.LanguageModelFactory.createOllamaStreamingChatModel;
import static fun.freechat.service.ai.LanguageModelFactory.createOllamaStreamingLanguageModel;
import static fun.freechat.service.ai.LanguageModelFactory.createOpenAiChatModel;
import static fun.freechat.service.ai.LanguageModelFactory.createOpenAiEmbeddingModel;
import static fun.freechat.service.ai.LanguageModelFactory.createOpenAiLanguageModel;
import static fun.freechat.service.ai.LanguageModelFactory.createOpenAiStreamingChatModel;
import static fun.freechat.service.ai.LanguageModelFactory.createOpenAiStreamingLanguageModel;
import static fun.freechat.service.ai.LanguageModelFactory.createQwenChatModel;
import static fun.freechat.service.ai.LanguageModelFactory.createQwenEmbeddingModel;
import static fun.freechat.service.ai.LanguageModelFactory.createQwenLanguageModel;
import static fun.freechat.service.ai.LanguageModelFactory.createQwenStreamingChatModel;
import static fun.freechat.service.ai.LanguageModelFactory.createQwenStreamingLanguageModel;

@Service
@SuppressWarnings("unused")
public class PromptAiServiceImpl implements PromptAiService {
    @Autowired
    private AiApiKeyService aiApiKeyService;

    private CloseableAiApiKey getCloseableAiApiKey(User user, String apiKeyInfo) {
        if (user == null) {
            return aiApiKeyService.use(apiKeyInfo);
        } else {
            return aiApiKeyService.use(user.getUserId(), apiKeyInfo);
        }
    }

    @Override
    // @Trace(ignoreArgs = true, extInfo = "'prompt:' + #p0 + ',model:' + #p4.modelId + ',parameters:' + #p5")
    public ChatResponse send(String prompt,
                             PromptType promptType,
                             User user,
                             String apiKeyInfo,
                             AiModelInfo modelInfo,
                             Map<String, Object> parameters) {
        ModelProvider provider = ModelProvider.of(modelInfo.getProvider());
        ModelType type  = ModelType.of(modelInfo.getType());
        try (var apiKeyClient = getCloseableAiApiKey(user, apiKeyInfo)) {
            if (type == ModelType.TEXT2TEXT) {
                LanguageModel model = switch (provider) {
                    case OPEN_AI -> createOpenAiLanguageModel(apiKeyClient.token(), modelInfo.getName(), parameters);
                    case AZURE_OPEN_AI -> createAzureOpenAiLanguageModel(apiKeyClient.token(), modelInfo.getName(), parameters);
                    case DASH_SCOPE -> createQwenLanguageModel(apiKeyClient.token(), modelInfo.getName(), parameters);
                    case OLLAMA -> createOllamaLanguageModel(modelInfo.getName(), parameters);
                    default -> throw new NotImplementedException("Not implemented.");
                };
                if (promptType == PromptType.CHAT) {
                    var promptTemplate = InfoUtils.defaultMapper().readValue(prompt, ChatPromptContent.class);
                    var messages = PromptUtils.toMessages(promptTemplate);
                    prompt = PromptUtils.toPrompt(messages);
                }
                return Optional.ofNullable(model.generate(prompt))
                        .map(resp -> ChatResponse.builder()
                                .aiMessage(AiMessage.from(resp.content()))
                                .tokenUsage(resp.tokenUsage())
                                .finishReason(resp.finishReason())
                                .build())
                        .orElse(null);
            } else if (type == ModelType.TEXT2CHAT) {
                ChatModel model = switch (provider) {
                    case OPEN_AI -> createOpenAiChatModel(apiKeyClient.token(), modelInfo.getName(), parameters);
                    case AZURE_OPEN_AI -> createAzureOpenAiChatModel(apiKeyClient.token(), modelInfo.getName(), parameters);
                    case DASH_SCOPE -> createQwenChatModel(apiKeyClient.token(), modelInfo.getName(), parameters);
                    case OLLAMA -> createOllamaChatModel(modelInfo.getName(), parameters);
                    default -> throw new NotImplementedException("Not implemented.");
                };
                if (promptType == PromptType.CHAT) {
                    var promptTemplate = InfoUtils.defaultMapper().readValue(prompt, ChatPromptContent.class);
                    var messages = PromptUtils.toMessages(promptTemplate);
                    return model.chat(messages);
                } else {
                    return model.chat(UserMessage.from(prompt));
                }
            } else if (type == ModelType.EMBEDDING) {
                EmbeddingModel model = switch (provider) {
                    case OPEN_AI -> createOpenAiEmbeddingModel(apiKeyClient.token(), modelInfo.getName(), parameters);
                    case AZURE_OPEN_AI -> createAzureOpenAiEmbeddingModel(apiKeyClient.token(), modelInfo.getName(), parameters);
                    case DASH_SCOPE -> createQwenEmbeddingModel(apiKeyClient.token(), modelInfo.getName(), parameters);
                    case OLLAMA -> createOllamaEmbeddingModel(modelInfo.getName(), parameters);
                    default -> throw new NotImplementedException("Not implemented.");
                };
                return Optional.ofNullable(model.embed(prompt))
                        .map(resp -> ChatResponse.builder()
                                .aiMessage(AiMessage.from(PojoUtils.object2JsonString(resp.content().vector())))
                                .tokenUsage(resp.tokenUsage())
                                .finishReason(resp.finishReason())
                                .build())
                        .orElse(null);
            } else {
                throw new NotImplementedException("Not implemented.");
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    // @Trace(ignoreArgs = true, extInfo = "'prompt:' + #p0 + ',model:' + #p4.modelId + ',parameters:' + #p5")
    public void streamSend(String prompt,
                           PromptType promptType,
                           User user,
                           String apiKeyInfo,
                           AiModelInfo modelInfo,
                           Map<String, Object> parameters,
                           StreamingChatResponseHandler handler) {
        ModelProvider provider = ModelProvider.of(modelInfo.getProvider());
        ModelType type  = ModelType.of(modelInfo.getType());
        try (var apiKeyClient = getCloseableAiApiKey(user, apiKeyInfo)) {
            if (type == ModelType.TEXT2TEXT) {
                StreamingLanguageModel model = switch (provider) {
                    case OPEN_AI -> createOpenAiStreamingLanguageModel(apiKeyClient.token(), modelInfo.getName(), parameters);
                    case AZURE_OPEN_AI -> createAzureOpenAiStreamingLanguageModel(apiKeyClient.token(), modelInfo.getName(), parameters);
                    case DASH_SCOPE -> createQwenStreamingLanguageModel(apiKeyClient.token(), modelInfo.getName(), parameters);
                    case OLLAMA -> createOllamaStreamingLanguageModel(modelInfo.getName(), parameters);
                    default -> throw new NotImplementedException("Not implemented.");
                };
                if (promptType == PromptType.CHAT) {
                    var promptTemplate = InfoUtils.defaultMapper().readValue(prompt, ChatPromptContent.class);
                    var messages = PromptUtils.toMessages(promptTemplate);
                    prompt = PromptUtils.toPrompt(messages);
                }
                model.generate(prompt, (StreamingResponseHandler<String>) handler);
            } else if (type == ModelType.TEXT2CHAT) {
                StreamingChatModel model = switch (provider) {
                    case OPEN_AI -> createOpenAiStreamingChatModel(apiKeyClient.token(), modelInfo.getName(), parameters);
                    case AZURE_OPEN_AI -> createAzureOpenAiStreamingChatModel(apiKeyClient.token(), modelInfo.getName(), parameters);
                    case DASH_SCOPE -> createQwenStreamingChatModel(apiKeyClient.token(), modelInfo.getName(), parameters);
                    case OLLAMA -> createOllamaStreamingChatModel(modelInfo.getName(), parameters);
                    default -> throw new NotImplementedException("Not implemented.");
                };
                if (promptType == PromptType.CHAT) {
                    var promptTemplate = InfoUtils.defaultMapper().readValue(prompt, ChatPromptContent.class);
                    var messages = PromptUtils.toMessages(promptTemplate);
                    model.chat(messages, handler);
                } else {
                    model.chat(prompt, handler);
                }
            } else if (type == ModelType.EMBEDDING) {
                var result = send(prompt, promptType, user, apiKeyInfo, modelInfo, parameters);
                handler.onPartialResponse(result.aiMessage().text());
                handler.onCompleteResponse(result);
            } else {
                throw new NotImplementedException("Not implemented.");
            }
        } catch (Exception e) {
            handler.onError(e);
        }
    }
}
