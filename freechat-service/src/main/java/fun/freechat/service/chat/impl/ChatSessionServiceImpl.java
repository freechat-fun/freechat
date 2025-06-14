package fun.freechat.service.chat.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.moderation.Moderation;
import dev.langchain4j.model.moderation.ModerationModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.injector.ContentInjector;
import dev.langchain4j.rag.content.injector.DefaultContentInjector;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.rag.query.transformer.QueryTransformer;
import dev.langchain4j.service.ModerationException;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import fun.freechat.langchain4j.memory.chat.SystemAlwaysOnTopMessageWindowChatMemory;
import fun.freechat.langchain4j.rag.query.transformer.DelegatedQueryTransformer;
import fun.freechat.langchain4j.rag.query.transformer.NamedCompressingQueryTransformer;
import fun.freechat.service.ai.AiModelInfo;
import fun.freechat.model.CharacterBackend;
import fun.freechat.model.CharacterInfo;
import fun.freechat.model.ChatContext;
import fun.freechat.model.PromptInfo;
import fun.freechat.model.PromptTask;
import fun.freechat.model.User;
import fun.freechat.service.account.SysUserService;
import fun.freechat.service.ai.AiApiKeyService;
import fun.freechat.service.ai.CloseableAiApiKey;
import fun.freechat.service.character.CharacterService;
import fun.freechat.service.chat.ChatContextService;
import fun.freechat.service.chat.ChatMemoryService;
import fun.freechat.service.chat.ChatSession;
import fun.freechat.service.chat.ChatSessionService;
import fun.freechat.service.chat.MemoryUsage;
import fun.freechat.service.common.ShortLinkService;
import fun.freechat.service.enums.ChatVar;
import fun.freechat.service.enums.ModelProvider;
import fun.freechat.service.enums.PromptFormat;
import fun.freechat.service.prompt.ChatPromptContent;
import fun.freechat.service.prompt.PromptService;
import fun.freechat.service.prompt.PromptTaskService;
import fun.freechat.service.rag.EmbeddingModelService;
import fun.freechat.service.rag.EmbeddingStoreService;
import fun.freechat.service.util.CacheUtils;
import fun.freechat.service.util.InfoUtils;
import fun.freechat.service.util.PromptUtils;
import fun.freechat.util.LangUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static dev.langchain4j.data.message.ChatMessageType.USER;
import static dev.langchain4j.store.embedding.filter.MetadataFilterBuilder.metadataKey;
import static fun.freechat.service.ai.LanguageModelFactory.createAzureOpenAiChatModel;
import static fun.freechat.service.ai.LanguageModelFactory.createAzureOpenAiStreamingChatModel;
import static fun.freechat.service.ai.LanguageModelFactory.createOllamaChatModel;
import static fun.freechat.service.ai.LanguageModelFactory.createOllamaStreamingChatModel;
import static fun.freechat.service.ai.LanguageModelFactory.createOpenAiChatModel;
import static fun.freechat.service.ai.LanguageModelFactory.createOpenAiModerationModel;
import static fun.freechat.service.ai.LanguageModelFactory.createOpenAiStreamingChatModel;
import static fun.freechat.service.ai.LanguageModelFactory.createQwenChatModel;
import static fun.freechat.service.ai.LanguageModelFactory.createQwenStreamingChatModel;
import static fun.freechat.service.enums.ChatVar.CHARACTER_CHAT_EXAMPLE;
import static fun.freechat.service.enums.ChatVar.CHARACTER_CHAT_STYLE;
import static fun.freechat.service.enums.ChatVar.CHARACTER_DESCRIPTION;
import static fun.freechat.service.enums.ChatVar.CHARACTER_GENDER;
import static fun.freechat.service.enums.ChatVar.CHARACTER_GREETING;
import static fun.freechat.service.enums.ChatVar.CHARACTER_LANG;
import static fun.freechat.service.enums.ChatVar.CHARACTER_NICKNAME;
import static fun.freechat.service.enums.ChatVar.CHARACTER_PROFILE;
import static fun.freechat.service.enums.ChatVar.CHAT_CONTEXT;
import static fun.freechat.service.enums.ChatVar.USER_NICKNAME;
import static fun.freechat.service.enums.ChatVar.USER_PROFILE;
import static fun.freechat.service.enums.EmbeddingRecordMeta.MEMORY_ID;
import static fun.freechat.service.enums.EmbeddingStoreType.documentTypeForLang;
import static fun.freechat.service.enums.EmbeddingStoreType.longTermMemoryTypeForLang;
import static fun.freechat.service.util.CacheUtils.IN_PROCESS_LONG_CACHE_MANAGER;
import static fun.freechat.service.util.CacheUtils.LONG_PERIOD_CACHE_NAME;
import static fun.freechat.util.ByteUtils.isTrue;

@Service
@Slf4j
@SuppressWarnings("unused")
public class ChatSessionServiceImpl implements ChatSessionService {
    static final String CACHE_KEY_PREFIX = "ChatSessionService_";
    static final String CACHE_KEY_SPEL_PREFIX = "'" + CACHE_KEY_PREFIX + "' + ";

    @Value("${app.homeUrl}")
    private String homeUrl;
    @Value("${chat.rag.maxResults}")
    private Integer maxResults;
    @Value("${chat.rag.minScore}")
    private Double minScore;
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
    private ChatMemoryService chatMemoryService;
    @Autowired
    private EmbeddingStoreService<TextSegment> embeddingStoreService;
    @Autowired
    private EmbeddingModelService embeddingModelService;
    @Autowired
    private ShortLinkService shortLinkService;
    @Autowired
    @Qualifier("taskExecutor")
    private ThreadPoolTaskExecutor executor;

    private synchronized Lock getLock(Cache cache, String chatId) {
        if (cache == null) {
            return new ReentrantLock();
        }
        return cache.get(CACHE_KEY_PREFIX + "_lock_" + chatId, ReentrantLock::new);
    }

    private CloseableAiApiKey getCloseableAiApiKey(ChatContext context, String userId, PromptTask promptTask) {
        String apiKeyOwner = context.getUserId();
        String apiKeyName = context.getApiKeyName();
        String apiKeyValue = context.getApiKeyValue();
        if (StringUtils.isBlank(apiKeyName) && StringUtils.isBlank(apiKeyValue)) {
            apiKeyOwner = userId;
            apiKeyName = promptTask.getApiKeyName();
            apiKeyValue = promptTask.getApiKeyValue();
        }

        return StringUtils.isBlank(apiKeyName) ?
                aiApiKeyService.use(apiKeyValue) :
                aiApiKeyService.use(apiKeyOwner, apiKeyName);
    }

    @Override
    // ChatSession cannot be serialized. Use the in-process cache.
    @Cacheable(cacheNames = LONG_PERIOD_CACHE_NAME,
            cacheManager = IN_PROCESS_LONG_CACHE_MANAGER,
            key = CACHE_KEY_SPEL_PREFIX + "#p0",
            unless="#result == null")
    public ChatSession get(String chatId) {
        ChatContext context = chatContextService.get(chatId);
        if (context == null) {
            return null;
        }
        return get(context);
    }

    @Override
    public ChatSession get(ChatContext context) {
        String chatId = context.getChatId();
        Cache cache = CacheUtils.inProcessLongPeriodCache();
        Lock lock = getLock(cache, chatId);

        lock.lock();
        try {
            // double-checked locking
            if (cache != null) {
                ChatSession session = cache.get(CACHE_KEY_PREFIX + chatId, ChatSession.class);
                if (session != null) {
                    return session;
                }
            }

            CharacterBackend backend = characterService.getBackend(context.getBackendId());
            String characterUid = backend.getCharacterUid();
            String ownerId = characterService.getOwnerByUid(characterUid);
            User owner = userService.loadByUserId(ownerId);
            Long characterId = characterService.getLatestIdByUid(characterUid, owner);
            CharacterInfo characterInfo = characterService.details(characterId, owner).getLeft();
            PromptTask promptTask = promptTaskService.get(backend.getChatPromptTaskId());
            AiModelInfo modelInfo = InfoUtils.toAiModelInfo(promptTask.getModelId());
            ModelProvider provider = ModelProvider.of(modelInfo.getProvider());
            Map<String, Object> parameters = StringUtils.isNotBlank(promptTask.getParams()) ?
                    InfoUtils.defaultMapper().readValue(promptTask.getParams(), new TypeReference<>() {}) :
                    Collections.emptyMap();

            ChatModel chatModel;
            StreamingChatModel streamingChatModel;
            try (var apiKeyClient = getCloseableAiApiKey(context, ownerId, promptTask)) {
                switch (provider) {
                    case OPEN_AI -> {
                        chatModel = createOpenAiChatModel(apiKeyClient.token(), modelInfo.getName(), parameters);
                        streamingChatModel = createOpenAiStreamingChatModel(apiKeyClient.token(), modelInfo.getName(), parameters);
                    }
                    case AZURE_OPEN_AI -> {
                        chatModel = createAzureOpenAiChatModel(apiKeyClient.token(), modelInfo.getName(), parameters);
                        streamingChatModel = createAzureOpenAiStreamingChatModel(apiKeyClient.token(), modelInfo.getName(), parameters);
                    }
                    case DASH_SCOPE -> {
                        chatModel = createQwenChatModel(apiKeyClient.token(), modelInfo.getName(), parameters);
                        streamingChatModel = createQwenStreamingChatModel(apiKeyClient.token(), modelInfo.getName(), parameters);
                    }
                    case OLLAMA -> {
                        chatModel = createOllamaChatModel(modelInfo.getName(), parameters);
                        streamingChatModel = createOllamaStreamingChatModel(modelInfo.getName(), parameters);
                    }
                    default -> throw new NotImplementedException("Not implemented.");
                }
            }

            ModerationModel moderationModel = null;
            String moderationModelId = backend.getModerationModelId();
            String moderationApiKeyName = backend.getModerationApiKeyName();
            if (StringUtils.isNotBlank(moderationModelId) && StringUtils.isNotBlank(moderationApiKeyName)) {
                modelInfo = InfoUtils.toAiModelInfo(moderationModelId);
                provider = ModelProvider.of(modelInfo.getProvider());
                parameters = StringUtils.isNotBlank(backend.getModerationParams()) ?
                        InfoUtils.defaultMapper().readValue(backend.getModerationParams(), new TypeReference<>() {}) :
                        Collections.emptyMap();
                try (var apiKeyClient = aiApiKeyService.use(ownerId, moderationApiKeyName)) {
                    switch (provider) {
                        case OPEN_AI -> moderationModel = createOpenAiModerationModel(apiKeyClient.token(), modelInfo.getName(), parameters);
                        default -> throw new NotImplementedException("Not implemented.");
                    }
                }
            }

            Long promptId = promptService.getLatestIdByUid(promptTask.getPromptUid(), owner);
            PromptInfo promptInfo = promptService.details(promptId, owner).getLeft();
            String promptTemplate = isTrue(promptTask.getDraft()) && StringUtils.isNotBlank(promptInfo.getDraft()) ?
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

            Map<String, Object> variables = HashMap.newHashMap(ChatVar.values().length);

            if (StringUtils.isNotBlank(promptInfo.getInputs())) {
                Map<String, Object> inputs = InfoUtils.defaultMapper().readValue(
                        promptInfo.getInputs(), new TypeReference<>() {});
                for (Map.Entry<String, Object> input : inputs.entrySet()) {
                    Object value = input.getValue();
                    if  (value == null ||
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

            String lang = characterInfo.getLang();
            variables.put(CHARACTER_LANG.text(), LangUtils.codeToLabel(lang));
            variables.put(CHARACTER_NICKNAME.text(), characterNickname);
            variables.put(CHARACTER_DESCRIPTION.text(), characterInfo.getDescription());
            variables.put(CHARACTER_GENDER.text(), characterInfo.getGender());
            variables.put(CHARACTER_CHAT_STYLE.text(), getOrBlank(characterInfo.getChatStyle()));
            variables.put(CHARACTER_CHAT_EXAMPLE.text(), getOrBlank(characterInfo.getChatExample()));
            variables.put(CHARACTER_GREETING.text(), getOrBlank(characterInfo.getGreeting()));
            variables.put(CHARACTER_PROFILE.text(), getOrBlank(characterInfo.getProfile()));
            variables.put(USER_PROFILE.text(), getOrBlank(userProfile));
            variables.put(USER_NICKNAME.text(), userNickname);
            variables.put(CHAT_CONTEXT.text(), getOrBlank(context.getAbout()));

            Integer windowSize = backend.getMessageWindowSize();
            SystemAlwaysOnTopMessageWindowChatMemory chatMemory = SystemAlwaysOnTopMessageWindowChatMemory.builder()
                    .id(chatId)
                    .maxMessages(windowSize)
                    .chatMemoryStore(chatMemoryService)
                    .build();

            PromptFormat promptFormat = PromptFormat.of(promptInfo.getFormat());
            List<ChatMessage> messages = chatMemory.messages();
            if (CollectionUtils.isEmpty(messages)) {
                chatMemory.add(SystemMessage.from(promptService.apply(
                        prompt.getSystem(), variables, promptFormat)));

                if (CollectionUtils.isNotEmpty(prompt.getMessages())) {
                    prompt.getMessages().forEach(chatMemory::add);
                }
            } else if (messages.getLast().type() == USER) {
                ChatResponse response = chatModel.chat(messages);
                chatMemory.addAiMessage(response.aiMessage(), response.tokenUsage());
            }

            // knowledge
            EmbeddingModel embeddingModel = embeddingModelService.modelForLang(lang);
            EmbeddingStore<TextSegment> embeddingStore =
                    embeddingStoreService.of(characterUid, documentTypeForLang(lang));

            QueryTransformer origQueryTransformer = NamedCompressingQueryTransformer.extraBuilder()
                    .aiName(characterNickname)
                    .chatModel(chatModel)
                    .promptTemplate("zh".equalsIgnoreCase(lang) ?
                            NamedCompressingQueryTransformer.DEFAULT_PROMPT_TEMPLATE_ZH :
                            NamedCompressingQueryTransformer.DEFAULT_PROMPT_TEMPLATE_EN)
                    .build();

            QueryTransformer delegatedQueryTransformer = DelegatedQueryTransformer.builder()
                    .queryTransformer(origQueryTransformer)
                    .postProcessor(queries -> queries.stream()
                            .map(query -> Query.from(embeddingModelService.queryPrefixForLang(lang) + query.text()))
                            .toList())
                    .build();

            ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                    .embeddingModel(embeddingModel)
                    .embeddingStore(embeddingStore)
                    .maxResults(maxResults)
                    .minScore(minScore)
                    .filter(metadataKey(MEMORY_ID.text()).isEqualTo(characterUid))
                    .build();

            ContentInjector contentInjector = DefaultContentInjector.builder()
                    .promptTemplate(PromptTemplate.from("{{{contents}}}"))
                    .build();

            RetrievalAugmentor retrievalAugmentor = DefaultRetrievalAugmentor.builder()
                    .queryTransformer(delegatedQueryTransformer)
                    .contentRetriever(contentRetriever)
                    .contentInjector(contentInjector)
                    .executor(executor)
                    .build();

            // long-term memory
            RetrievalAugmentor longTermMemoryRetrievalAugmentor = null;
            Integer longTermMemoryWindowSize = backend.getLongTermMemoryWindowSize();

            if (longTermMemoryWindowSize != null && longTermMemoryWindowSize > 0L) {
                EmbeddingStore<TextSegment> longTermMemoryEmbeddingStore =
                        embeddingStoreService.of(chatId, longTermMemoryTypeForLang(lang));

                ContentRetriever longTermMemoryContentRetriever = EmbeddingStoreContentRetriever.builder()
                        .embeddingModel(embeddingModel)
                        .embeddingStore(longTermMemoryEmbeddingStore)
                        .maxResults(longTermMemoryWindowSize)
                        .minScore(minScore)
                        .filter(metadataKey(MEMORY_ID.text()).isEqualTo(chatId))
                        .build();

                longTermMemoryRetrievalAugmentor = DefaultRetrievalAugmentor.builder()
                        .queryTransformer(delegatedQueryTransformer)
                        .contentRetriever(longTermMemoryContentRetriever)
                        .contentInjector(contentInjector)
                        .executor(executor)
                        .build();
            }

            MemoryUsage memoryUsage = chatMemoryService.usage(chatId);

            // tools
            List<Object> tools = new LinkedList<>();
            if (isTrue(backend.getEnableAlbumTool())) {
                tools.add("zh".equalsIgnoreCase(lang) ?
                        ZhAlbumTool.builder()
                                .homeUrl(homeUrl)
                                .characterService(characterService)
                                .chatContextService(chatContextService)
                                .shortLinkService(shortLinkService)
                                .build() :
                        EnAlbumTool.builder()
                                .homeUrl(homeUrl)
                                .characterService(characterService)
                                .chatContextService(chatContextService)
                                .shortLinkService(shortLinkService)
                                .build()
                );
            }

            return ChatSession.builder()
                    .chatModel(chatModel)
                    .streamingChatModel(streamingChatModel)
                    .moderationModel(moderationModel)
                    .chatMemory(chatMemory)
                    .prompt(prompt)
                    .promptFormat(promptFormat)
                    .variables(variables)
                    .retriever(retrievalAugmentor)
                    .longTermMemoryRetriever(longTermMemoryRetrievalAugmentor)
                    .memoryUsage(memoryUsage)
                    .objectsWithTools(tools)
                    .build();
        } catch (NotImplementedException | NoSuchElementException | NullPointerException | IOException e) {
            log.warn("Failed to build chat session of {}", chatId, e);
            return null;
        } finally {
            try {
                lock.unlock();
            } catch (Exception unlockEx) {
                log.warn("Unlock failed!", unlockEx);
            }
        }
    }

    @Override
    @CacheEvict(cacheNames = LONG_PERIOD_CACHE_NAME,
            cacheManager = IN_PROCESS_LONG_CACHE_MANAGER,
            key = CACHE_KEY_SPEL_PREFIX + "#p0")
    public void reset(String chatId) {
        // ignored
    }

    @Override
    @Async
    public CompletableFuture<Moderation> triggerModerationIfNeeded(
            ChatSession session,
            List<dev.langchain4j.data.message.ChatMessage> messages) {
        ModerationModel moderationModel = session.getModerationModel();
        if (moderationModel == null) {
            return null;
        }
        List<ChatMessage> messagesToModerate = removeToolMessages(messages);
        return CompletableFuture.completedFuture(moderationModel.moderate(messagesToModerate).content());
    }

    @Override
    public void verifyModerationIfNeeded(Future<Moderation> moderationFuture) {
        if (moderationFuture != null) {
            try {
                Moderation moderation = moderationFuture.get();
                if (moderation != null && moderation.flagged()) {
                    throw new ModerationException(
                            String.format("Text \"%s\" violates content policy", moderation.flaggedText()));
                }
            } catch (InterruptedException | ExecutionException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    @Override
    public ChatSession createTemporary(ChatContext context, ChatMemoryStore memoryStore) {
        String chatId = context.getChatId();
        try {
            CharacterBackend backend = characterService.getBackend(context.getBackendId());
            String characterUid = backend.getCharacterUid();
            String ownerId = characterService.getOwnerByUid(characterUid);
            User owner = userService.loadByUserId(ownerId);
            Long characterId = characterService.getLatestIdByUid(characterUid, owner);
            CharacterInfo characterInfo = characterService.details(characterId, owner).getLeft();
            PromptTask promptTask = promptTaskService.get(backend.getChatPromptTaskId());
            AiModelInfo modelInfo = InfoUtils.toAiModelInfo(promptTask.getModelId());
            ModelProvider provider = ModelProvider.of(modelInfo.getProvider());
            Map<String, Object> parameters = StringUtils.isNotBlank(promptTask.getParams()) ?
                    InfoUtils.defaultMapper().readValue(promptTask.getParams(), new TypeReference<>() {}) :
                    Collections.emptyMap();

            ChatModel chatModel;
            StreamingChatModel streamingChatModel;
            try (var apiKeyClient = getCloseableAiApiKey(context, ownerId, promptTask)) {
                switch (provider) {
                    case OPEN_AI -> {
                        chatModel = createOpenAiChatModel(apiKeyClient.token(), modelInfo.getName(), parameters);
                        streamingChatModel = createOpenAiStreamingChatModel(apiKeyClient.token(), modelInfo.getName(), parameters);
                    }
                    case AZURE_OPEN_AI -> {
                        chatModel = createAzureOpenAiChatModel(apiKeyClient.token(), modelInfo.getName(), parameters);
                        streamingChatModel = createAzureOpenAiStreamingChatModel(apiKeyClient.token(), modelInfo.getName(), parameters);
                    }
                    case DASH_SCOPE -> {
                        chatModel = createQwenChatModel(apiKeyClient.token(), modelInfo.getName(), parameters);
                        streamingChatModel = createQwenStreamingChatModel(apiKeyClient.token(), modelInfo.getName(), parameters);
                    }
                    case OLLAMA -> {
                        chatModel = createOllamaChatModel(modelInfo.getName(), parameters);
                        streamingChatModel = createOllamaStreamingChatModel(modelInfo.getName(), parameters);
                    }
                    default -> throw new NotImplementedException("Not implemented.");
                }
            }

            ModerationModel moderationModel = null;
            String moderationModelId = backend.getModerationModelId();
            String moderationApiKeyName = backend.getModerationApiKeyName();
            if (StringUtils.isNotBlank(moderationModelId) && StringUtils.isNotBlank(moderationApiKeyName)) {
                modelInfo = InfoUtils.toAiModelInfo(moderationModelId);
                provider = ModelProvider.of(modelInfo.getProvider());
                parameters = StringUtils.isNotBlank(backend.getModerationParams()) ?
                        InfoUtils.defaultMapper().readValue(backend.getModerationParams(), new TypeReference<>() {}) :
                        Collections.emptyMap();
                try (var apiKeyClient = aiApiKeyService.use(ownerId, moderationApiKeyName)) {
                    switch (provider) {
                        case OPEN_AI -> moderationModel = createOpenAiModerationModel(apiKeyClient.token(), modelInfo.getName(), parameters);
                        default -> throw new NotImplementedException("Not implemented.");
                    }
                }
            }

            Long promptId = promptService.getLatestIdByUid(promptTask.getPromptUid(), owner);
            PromptInfo promptInfo = promptService.details(promptId, owner).getLeft();
            String promptTemplate = isTrue(promptTask.getDraft()) && StringUtils.isNotBlank(promptInfo.getDraft()) ?
                    PromptUtils.getDraftTemplate(promptInfo.getDraft()) :
                    promptInfo.getTemplate();
            ChatPromptContent prompt = InfoUtils.defaultMapper().readValue(promptTemplate, ChatPromptContent.class);

            String characterNickname = context.getCharacterNickname();
            if (StringUtils.isBlank(characterNickname)) {
                characterNickname = characterInfo.getName();
            }

            String userNickname = context.getUserNickname();
            String userProfile = context.getUserProfile();

            Map<String, Object> variables = HashMap.newHashMap(ChatVar.values().length);

            if (StringUtils.isNotBlank(promptInfo.getInputs())) {
                Map<String, Object> inputs = InfoUtils.defaultMapper().readValue(
                        promptInfo.getInputs(), new TypeReference<>() {});
                for (Map.Entry<String, Object> input : inputs.entrySet()) {
                    Object value = input.getValue();
                    if  (value == null ||
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

            String lang = characterInfo.getLang();
            variables.put(CHARACTER_LANG.text(), LangUtils.codeToLabel(lang));
            variables.put(CHARACTER_NICKNAME.text(), characterNickname);
            variables.put(CHARACTER_DESCRIPTION.text(), characterInfo.getDescription());
            variables.put(CHARACTER_GENDER.text(), characterInfo.getGender());
            variables.put(CHARACTER_CHAT_STYLE.text(), getOrBlank(characterInfo.getChatStyle()));
            variables.put(CHARACTER_CHAT_EXAMPLE.text(), getOrBlank(characterInfo.getChatExample()));
            variables.put(CHARACTER_GREETING.text(), getOrBlank(characterInfo.getGreeting()));
            variables.put(CHARACTER_PROFILE.text(), getOrBlank(characterInfo.getProfile()));
            variables.put(USER_PROFILE.text(), getOrBlank(userProfile));
            variables.put(USER_NICKNAME.text(), userNickname);
            variables.put(CHAT_CONTEXT.text(), getOrBlank(context.getAbout()));

            Integer windowSize = backend.getMessageWindowSize();
            SystemAlwaysOnTopMessageWindowChatMemory chatMemory = SystemAlwaysOnTopMessageWindowChatMemory.builder()
                    .id(chatId)
                    .maxMessages(windowSize)
                    .chatMemoryStore(memoryStore)
                    .build();

            PromptFormat promptFormat = PromptFormat.of(promptInfo.getFormat());
            List<ChatMessage> messages = chatMemory.messages();
            if (CollectionUtils.isEmpty(messages)) {
                chatMemory.add(SystemMessage.from(promptService.apply(
                        prompt.getSystem(), variables, promptFormat)));

                if (CollectionUtils.isNotEmpty(prompt.getMessages())) {
                    prompt.getMessages().forEach(chatMemory::add);
                }
            }

            // knowledge
            EmbeddingModel embeddingModel = embeddingModelService.modelForLang(lang);
            EmbeddingStore<TextSegment> embeddingStore =
                    embeddingStoreService.of(characterUid, documentTypeForLang(lang));

            QueryTransformer origQueryTransformer = NamedCompressingQueryTransformer.extraBuilder()
                    .aiName(characterNickname)
                    .chatModel(chatModel)
                    .promptTemplate("zh".equalsIgnoreCase(lang) ?
                            NamedCompressingQueryTransformer.DEFAULT_PROMPT_TEMPLATE_ZH :
                            NamedCompressingQueryTransformer.DEFAULT_PROMPT_TEMPLATE_EN)
                    .build();

            QueryTransformer delegatedQueryTransformer = DelegatedQueryTransformer.builder()
                    .queryTransformer(origQueryTransformer)
                    .postProcessor(queries -> queries.stream()
                            .map(query -> Query.from(embeddingModelService.queryPrefixForLang(lang) + query.text()))
                            .toList())
                    .build();

            ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                    .embeddingModel(embeddingModel)
                    .embeddingStore(embeddingStore)
                    .maxResults(maxResults)
                    .minScore(minScore)
                    .filter(metadataKey(MEMORY_ID.text()).isEqualTo(characterUid))
                    .build();

            ContentInjector contentInjector = DefaultContentInjector.builder()
                    .promptTemplate(PromptTemplate.from("{{{contents}}}"))
                    .build();

            RetrievalAugmentor retrievalAugmentor = DefaultRetrievalAugmentor.builder()
                    .queryTransformer(delegatedQueryTransformer)
                    .contentRetriever(contentRetriever)
                    .contentInjector(contentInjector)
                    .executor(executor)
                    .build();

            // long-term memory
            RetrievalAugmentor longTermMemoryRetrievalAugmentor = null;
            Integer longTermMemoryWindowSize = backend.getLongTermMemoryWindowSize();

            if (longTermMemoryWindowSize != null && longTermMemoryWindowSize > 0L) {
                EmbeddingStore<TextSegment> longTermMemoryEmbeddingStore =
                        embeddingStoreService.of(chatId, longTermMemoryTypeForLang(lang));

                ContentRetriever longTermMemoryContentRetriever = EmbeddingStoreContentRetriever.builder()
                        .embeddingModel(embeddingModel)
                        .embeddingStore(longTermMemoryEmbeddingStore)
                        .maxResults(longTermMemoryWindowSize)
                        .minScore(minScore)
                        .filter(metadataKey(MEMORY_ID.text()).isEqualTo(chatId))
                        .build();

                longTermMemoryRetrievalAugmentor = DefaultRetrievalAugmentor.builder()
                        .queryTransformer(delegatedQueryTransformer)
                        .contentRetriever(longTermMemoryContentRetriever)
                        .contentInjector(contentInjector)
                        .executor(executor)
                        .build();
            }

            return ChatSession.builder()
                    .chatModel(chatModel)
                    .streamingChatModel(streamingChatModel)
                    .moderationModel(moderationModel)
                    .chatMemory(chatMemory)
                    .prompt(prompt)
                    .promptFormat(promptFormat)
                    .variables(variables)
                    .retriever(retrievalAugmentor)
                    .longTermMemoryRetriever(longTermMemoryRetrievalAugmentor)
                    .build();
        } catch (NotImplementedException | NoSuchElementException | NullPointerException | IOException e) {
            log.warn("Failed to build chat session of {}", chatId, e);
            return null;
        }
    }

    private List<ChatMessage> removeToolMessages(List<ChatMessage> messages) {
        return messages.stream()
                .filter(it -> !(it instanceof ToolExecutionResultMessage))
                .filter(it -> !(it instanceof AiMessage aiMessage && aiMessage.hasToolExecutionRequests()))
                .toList();
    }

    private String getOrBlank(String content) {
        return StringUtils.isBlank(content) ? "" : content;
    }
}
