package fun.freechat.service.character.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.moderation.Moderation;
import dev.langchain4j.model.moderation.ModerationModel;
import dev.langchain4j.service.ModerationException;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import fun.freechat.langchain4j.memory.chat.SystemAlwaysOnTopMessageWindowChatMemory;
import fun.freechat.model.*;
import fun.freechat.service.account.SysUserService;
import fun.freechat.service.ai.AiApiKeyService;
import fun.freechat.service.ai.AiModelInfoService;
import fun.freechat.service.ai.message.ChatMessage;
import fun.freechat.service.ai.message.ChatPromptContent;
import fun.freechat.service.character.CharacterService;
import fun.freechat.service.character.ChatContextService;
import fun.freechat.service.character.ChatSession;
import fun.freechat.service.character.ChatSessionService;
import fun.freechat.service.enums.ChatVar;
import fun.freechat.service.enums.ModelProvider;
import fun.freechat.service.enums.PromptFormat;
import fun.freechat.service.enums.PromptRole;
import fun.freechat.service.prompt.PromptService;
import fun.freechat.service.prompt.PromptTaskService;
import fun.freechat.service.util.InfoUtils;
import fun.freechat.service.util.PromptUtils;
import fun.freechat.service.util.StoreUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static fun.freechat.service.ai.LanguageModelFactory.*;
import static fun.freechat.service.enums.ChatVar.*;
import static fun.freechat.service.util.CacheUtils.IN_PROCESS_CACHE_MANAGER;
import static fun.freechat.service.util.CacheUtils.LONG_PERIOD_CACHE_NAME;
import static java.util.stream.Collectors.toList;

@Service
@Slf4j
@SuppressWarnings("unused")
public class ChatSessionServiceImpl implements ChatSessionService {
    private final static String CACHE_KEY_PREFIX = "ChatSessionService_";

    private final static String CACHE_KEY_SPEL_PREFIX = "'" + CACHE_KEY_PREFIX + "' + ";

    @Autowired
    private CharacterService characterService;

    @Autowired
    private ChatContextService chatContextService;

    @Autowired
    private PromptService promptService;

    @Autowired
    private PromptTaskService promptTaskService;

    @Autowired
    private AiApiKeyService aiApiKeyService;

    @Autowired
    private SysUserService userService;

    @Autowired
    private AiModelInfoService aiModelInfoService;

    @Override
    // ChatSession cannot be serialized. Use the in-process cache.
    @Cacheable(cacheNames = LONG_PERIOD_CACHE_NAME,
            cacheManager = IN_PROCESS_CACHE_MANAGER,
            key = CACHE_KEY_SPEL_PREFIX + "#p0",
            unless="#result == null")
    public ChatSession get(String chatId) {
        try {
            ChatContext context = chatContextService.get(chatId);
            CharacterBackend backend = characterService.getBackend(context.getBackendId());
            String characterId = backend.getCharacterId();
            String ownerId = characterService.getOwner(characterId);
            User owner = userService.loadByUserId(ownerId);
            CharacterInfo characterInfo = characterService.details(characterId, owner).getLeft();
            PromptTask promptTask = promptTaskService.get(backend.getChatPromptTaskId());
            AiModelInfo modelInfo = aiModelInfoService.get(promptTask.getModelId());
            ModelProvider provider = ModelProvider.of(modelInfo.getProvider());
            Map<String, Object> parameters = StringUtils.isNotBlank(promptTask.getParams()) ?
                    InfoUtils.defaultMapper().readValue(promptTask.getParams(), new TypeReference<>() {}) :
                    Collections.emptyMap();

            ChatLanguageModel chatModel;
            StreamingChatLanguageModel streamingChatModel;
            try (var apiKeyClient = aiApiKeyService.use(ownerId, promptTask.getApiKeyName())) {
                switch (provider) {
                    case OPEN_AI -> {
                        chatModel = createOpenAiChatModel(apiKeyClient.token(), modelInfo.getName(), parameters);
                        streamingChatModel = createOpenAiStreamingChatModel(apiKeyClient.token(), modelInfo.getName(), parameters);
                    }
                    case DASH_SCOPE -> {
                        chatModel = createQwenChatModel(apiKeyClient.token(), modelInfo.getName(), parameters);
                        streamingChatModel = createQwenStreamingChatModel(apiKeyClient.token(), modelInfo.getName(), parameters);
                    }
                    default -> throw new NotImplementedException("Not implemented.");
                }
            }

            ModerationModel moderationModel = null;
            String moderationModelId = backend.getModerationModelId();
            String moderationApiKeyName = backend.getModerationApiKeyName();
            if (StringUtils.isNotBlank(moderationModelId) && StringUtils.isNotBlank(moderationApiKeyName)) {
                modelInfo = aiModelInfoService.get(moderationModelId);
                provider = ModelProvider.of(modelInfo.getProvider());
                parameters = StringUtils.isNotBlank(backend.getModerationParams()) ?
                        InfoUtils.defaultMapper().readValue(backend.getModerationParams(), new TypeReference<>() {}) :
                        Collections.emptyMap();
                try (var apiKeyClient = aiApiKeyService.use(ownerId, moderationApiKeyName)) {
                    switch (provider) {
                        case OPEN_AI -> {
                            moderationModel = createOpenAiModerationModel(apiKeyClient.token(), modelInfo.getName(), parameters);
                        }
                        default -> throw new NotImplementedException("Not implemented.");
                    }
                }
            }

            PromptInfo promptInfo = promptService.details(promptTask.getPromptId(), owner).getLeft();
            String promptTemplate = promptTask.getDraft() == (byte) 1 && StringUtils.isNotBlank(promptInfo.getDraft()) ?
                    PromptUtils.getDraftTemplate(promptInfo.getDraft()) :
                    promptInfo.getTemplate();
            ChatPromptContent prompt = InfoUtils.defaultMapper().readValue(promptTemplate, ChatPromptContent.class);

            String characterNickname = context.getCharacterNickname();
            if (StringUtils.isBlank(characterNickname)) {
                characterNickname = characterInfo.getName();
            }

            User user = userService.loadByUserId(context.getUserId());
            String userNickname = context.getUserNickname();
            if (StringUtils.isBlank(userNickname)) {
                userNickname = user.getNickname();
            }
            if (StringUtils.isBlank(userNickname)) {
                userNickname = user.getPreferredUsername();
            }
            if (StringUtils.isBlank(userNickname)) {
                userNickname = user.getUsername();
            }

            String userProfile = context.getUserProfile();
            if (StringUtils.isBlank(userProfile)) {
                userProfile = user.getProfile();
            }

            Map<String, Object> variables = new HashMap<>(ChatVar.values().length);

            if (StringUtils.isNotBlank(promptInfo.getInputs())) {
                Map<String, Object> inputs = InfoUtils.defaultMapper().readValue(
                        promptInfo.getInputs(), new TypeReference<>() {});
                for (Map.Entry<String, Object> input : inputs.entrySet()) {
                    Object value = input.getValue();
                    if  (Objects.isNull(value) ||
                            (value instanceof String strValue && StringUtils.isBlank(strValue))) {
                        continue;
                    }
                    variables.put(input.getKey(), value);
                }
            }

            if (StringUtils.isNotBlank(promptTask.getVariables())) {
                Map<String, Object> predefinedVariables = InfoUtils.defaultMapper().readValue(
                        promptTask.getVariables(), new TypeReference<>() {});
                variables.putAll(predefinedVariables);
            }

            variables.put(CHARACTER_NICKNAME.text(), characterNickname);
            variables.put(CHARACTER_GENDER.text(), characterInfo.getGender());
            variables.put(CHARACTER_LANG.text(), characterInfo.getLang());
            variables.put(CHARACTER_CHAT_STYLE.text(), getOrBlank(characterInfo.getChatStyle()));
            variables.put(CHARACTER_CHAT_EXAMPLE.text(), getOrBlank(characterInfo.getChatExample()));
            variables.put(CHARACTER_GREETING.text(), getOrBlank(characterInfo.getChatStyle()));
            variables.put(CHARACTER_EXPERIENCE.text(), getOrBlank(characterInfo.getExperience()));
            variables.put(CHARACTER_PROFILE.text(), getOrBlank(characterInfo.getProfile()));
            variables.put(USER_PROFILE.text(), getOrBlank(userProfile));
            variables.put(USER_NICKNAME.text(), userNickname);

            ChatMemoryStore chatMemoryStore = StoreUtils.defaultMemoryStore();
            Integer windowSize = backend.getMessageWindowSize();
            ChatMemory chatMemory = SystemAlwaysOnTopMessageWindowChatMemory.builder()
                    .id(chatId)
                    .maxMessages(windowSize)
                    .chatMemoryStore(chatMemoryStore)
                    .build();

            if (CollectionUtils.isEmpty(chatMemory.messages())) {
                Optional.of(prompt.getSystem())
                        .map(system -> ChatMessage.from(
                                PromptRole.SYSTEM,
                                promptService.apply(system, variables, PromptFormat.of(prompt.getFormat()))))
                        .map(PromptUtils::convertChatMessage)
                        .ifPresent(chatMemory::add);

                Optional.ofNullable(prompt.getMessages())
                        .orElse(Collections.emptyList())
                        .stream()
                        .map(PromptUtils::convertChatMessage)
                        .forEach(chatMemory::add);

                Optional.ofNullable(characterInfo.getGreeting())
                        .map(greeting -> ChatMessage.from(
                                PromptRole.ASSISTANT,
                                promptService.apply(greeting, variables, PromptFormat.of(prompt.getFormat()))))
                        .map(PromptUtils::convertChatMessage)
                        .ifPresent(chatMemory::add);
            }

            return ChatSession.builder()
                    .chatModel(chatModel)
                    .streamingChatModel(streamingChatModel)
                    .moderationModel(moderationModel)
                    .chatMemory(chatMemory)
                    .prompt(prompt)
                    .variables(variables)
                    .build();
        } catch (NotImplementedException | NoSuchElementException | NullPointerException | IOException e) {
            log.warn("Failed to build chat session of {}", chatId, e);
            return null;
        }
    }

    @Override
    @CacheEvict(cacheNames = LONG_PERIOD_CACHE_NAME,
            cacheManager = IN_PROCESS_CACHE_MANAGER,
            key = CACHE_KEY_SPEL_PREFIX + "#p0")
    public void reset(String chatId) {}

    @Override
    @Async("defaultExecutor")
    public CompletableFuture<Moderation> triggerModerationIfNeeded(
            ChatSession session,
            List<dev.langchain4j.data.message.ChatMessage> messages) {
        ModerationModel moderationModel = session.getModerationModel();
        if (Objects.isNull(moderationModel)) {
            return null;
        }
        List<dev.langchain4j.data.message.ChatMessage> messagesToModerate = removeToolMessages(messages);
        return CompletableFuture.completedFuture(moderationModel.moderate(messagesToModerate).content());
    }

    @Override
    public void verifyModerationIfNeeded(ChatSession session, Future<Moderation> moderationFuture) {
        if (Objects.nonNull(moderationFuture)) {
            try {
                Moderation moderation = moderationFuture.get();
                if (Objects.nonNull(moderation) && moderation.flagged()) {
                    throw new ModerationException(
                            String.format("Text \"%s\" violates content policy", moderation.flaggedText()));
                }
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private List<dev.langchain4j.data.message.ChatMessage> removeToolMessages(
            List<dev.langchain4j.data.message.ChatMessage> messages) {
        return messages.stream()
                .filter(it -> !(it instanceof ToolExecutionResultMessage))
                .filter(it -> !(it instanceof AiMessage aiMessage && CollectionUtils.isNotEmpty(aiMessage.toolExecutionRequests())))
                .collect(toList());
    }

    private String getOrBlank(String content) {
        return StringUtils.isBlank(content) ? "" : content;
    }
}
