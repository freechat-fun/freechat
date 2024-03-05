package fun.freechat.service.prompt.impl;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.language.LanguageModel;
import dev.langchain4j.model.language.StreamingLanguageModel;
import dev.langchain4j.model.output.Response;
import fun.freechat.annotation.Trace;
import fun.freechat.model.AiModelInfo;
import fun.freechat.model.User;
import fun.freechat.service.ai.AiApiKeyService;
import fun.freechat.service.ai.CloseableAiApiKey;
import fun.freechat.service.prompt.ChatPromptContent;
import fun.freechat.service.enums.ModelProvider;
import fun.freechat.service.enums.ModelType;
import fun.freechat.service.enums.PromptType;
import fun.freechat.service.prompt.PromptAiService;
import fun.freechat.service.util.InfoUtils;
import fun.freechat.service.util.PromptUtils;
import fun.freechat.util.PojoUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static fun.freechat.service.ai.LanguageModelFactory.*;

@Service
@SuppressWarnings("unused")
public class PromptAiServiceImpl implements PromptAiService {

    @Autowired
    private AiApiKeyService aiApiKeyService;

    private CloseableAiApiKey getCloseableAiApiKey(User user, String apiKeyInfo) {
        if (Objects.isNull(user)) {
            return aiApiKeyService.use(apiKeyInfo);
        } else {
            return aiApiKeyService.use(user.getUserId(), apiKeyInfo);
        }
    }

    @Override
    @Trace(ignoreArgs = true, extInfo = "'prompt:' + #p0 + ',model:' + #p4.modelId + ',parameters:' + #p5")
    public Response<AiMessage> send(String prompt,
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
                    case DASH_SCOPE -> createQwenLanguageModel(apiKeyClient.token(), modelInfo.getName(), parameters);
                    default -> throw new NotImplementedException("Not implemented.");
                };
                if (promptType == PromptType.CHAT) {
                    var promptTemplate = InfoUtils.defaultMapper().readValue(prompt, ChatPromptContent.class);
                    var messages = PromptUtils.toMessages(promptTemplate);
                    prompt = PromptUtils.toPrompt(messages);
                }
                return Optional.ofNullable(model.generate(prompt))
                        .map(resp -> new Response<>(
                                AiMessage.from(resp.content()),
                                resp.tokenUsage(),
                                resp.finishReason()))
                        .orElse(null);
            } else if (type == ModelType.TEXT2CHAT) {
                ChatLanguageModel model = switch (provider) {
                    case OPEN_AI -> createOpenAiChatModel(apiKeyClient.token(), modelInfo.getName(), parameters);
                    case DASH_SCOPE -> createQwenChatModel(apiKeyClient.token(), modelInfo.getName(), parameters);
                    default -> throw new NotImplementedException("Not implemented.");
                };
                if (promptType == PromptType.CHAT) {
                    var promptTemplate = InfoUtils.defaultMapper().readValue(prompt, ChatPromptContent.class);
                    var messages = PromptUtils.toMessages(promptTemplate);
                    return model.generate(messages);
                } else {
                    return model.generate(UserMessage.from(prompt));
                }
            } else if (type == ModelType.EMBEDDING) {
                EmbeddingModel model = switch (provider) {
                    case OPEN_AI -> createOpenAiEmbeddingModel(apiKeyClient.token(), modelInfo.getName(), parameters);
                    case DASH_SCOPE -> createQwenEmbeddingModel(apiKeyClient.token(), modelInfo.getName(), parameters);
                    default -> throw new NotImplementedException("Not implemented.");
                };
                return Optional.ofNullable(model.embed(prompt))
                        .map(resp -> new Response<>(
                                AiMessage.from(PojoUtils.object2JsonString(resp.content().vector())),
                                resp.tokenUsage(),
                                resp.finishReason()))
                        .orElse(null);
            } else {
                throw new NotImplementedException("Not implemented.");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Trace(ignoreArgs = true, extInfo = "'prompt:' + #p0 + ',model:' + #p4.modelId + ',parameters:' + #p5")
    public <T> void streamSend(String prompt,
                               PromptType promptType,
                               User user,
                               String apiKeyInfo,
                               AiModelInfo modelInfo,
                               Map<String, Object> parameters,
                               StreamingResponseHandler<T> handler) {
        ModelProvider provider = ModelProvider.of(modelInfo.getProvider());
        ModelType type  = ModelType.of(modelInfo.getType());
        try (var apiKeyClient = getCloseableAiApiKey(user, apiKeyInfo)) {
            if (type == ModelType.TEXT2TEXT) {
                StreamingLanguageModel model = switch (provider) {
                    case OPEN_AI -> createOpenAiStreamingLanguageModel(apiKeyClient.token(), modelInfo.getName(), parameters);
                    case DASH_SCOPE -> createQwenStreamingLanguageModel(apiKeyClient.token(), modelInfo.getName(), parameters);
                    default -> throw new NotImplementedException("Not implemented.");
                };
                if (promptType == PromptType.CHAT) {
                    var promptTemplate = InfoUtils.defaultMapper().readValue(prompt, ChatPromptContent.class);
                    var messages = PromptUtils.toMessages(promptTemplate);
                    prompt = PromptUtils.toPrompt(messages);
                }
                model.generate(prompt, (StreamingResponseHandler<String>) handler);
            } else if (type == ModelType.TEXT2CHAT) {
                StreamingChatLanguageModel model = switch (provider) {
                    case OPEN_AI -> createOpenAiStreamingChatModel(apiKeyClient.token(), modelInfo.getName(), parameters);
                    case DASH_SCOPE -> createQwenStreamingChatModel(apiKeyClient.token(), modelInfo.getName(), parameters);
                    default -> throw new NotImplementedException("Not implemented.");
                };
                if (promptType == PromptType.CHAT) {
                    var promptTemplate = InfoUtils.defaultMapper().readValue(prompt, ChatPromptContent.class);
                    var messages = PromptUtils.toMessages(promptTemplate);
                    model.generate(messages, (StreamingResponseHandler<AiMessage>) handler);
                } else {
                    model.generate(prompt, (StreamingResponseHandler<AiMessage>) handler);
                }
            } else if (type == ModelType.EMBEDDING) {
                var result = send(prompt, promptType, user, apiKeyInfo, modelInfo, parameters);
                handler.onNext(result.content().text());
                handler.onComplete((Response<T>) result);
            } else {
                throw new NotImplementedException("Not implemented.");
            }
        } catch (Exception e) {
            handler.onError(e);
        }
    }
}
