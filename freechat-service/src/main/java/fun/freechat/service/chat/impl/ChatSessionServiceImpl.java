package fun.freechat.service.chat.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.moderation.Moderation;
import dev.langchain4j.model.moderation.ModerationModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.injector.ContentInjector;
import dev.langchain4j.rag.content.injector.DefaultContentInjector;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.rag.query.transformer.CompressingQueryTransformer;
import dev.langchain4j.rag.query.transformer.QueryTransformer;
import dev.langchain4j.service.ModerationException;
import dev.langchain4j.store.embedding.EmbeddingStore;
import fun.freechat.langchain4j.memory.chat.SystemAlwaysOnTopMessageWindowChatMemory;
import fun.freechat.langchain4j.rag.query.transformer.DelegatedQueryTransformer;
import fun.freechat.model.*;
import fun.freechat.service.account.SysUserService;
import fun.freechat.service.ai.AiApiKeyService;
import fun.freechat.service.ai.AiModelInfoService;
import fun.freechat.service.ai.CloseableAiApiKey;
import fun.freechat.service.character.CharacterService;
import fun.freechat.service.chat.*;
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
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static dev.langchain4j.data.message.ChatMessageType.USER;
import static dev.langchain4j.store.embedding.filter.MetadataFilterBuilder.metadataKey;
import static fun.freechat.service.ai.LanguageModelFactory.*;
import static fun.freechat.service.enums.ChatVar.*;
import static fun.freechat.service.enums.EmbeddingRecordMeta.MEMORY_ID;
import static fun.freechat.service.enums.EmbeddingStoreType.*;
import static fun.freechat.service.util.CacheUtils.IN_PROCESS_CACHE_MANAGER;
import static fun.freechat.service.util.CacheUtils.LONG_PERIOD_CACHE_NAME;
import static java.util.stream.Collectors.toList;

@Service
@Slf4j
@SuppressWarnings("unused")
public class ChatSessionServiceImpl implements ChatSessionService {
    final static String CACHE_KEY_PREFIX = "ChatSessionService_";
    final static String CACHE_KEY_SPEL_PREFIX = "'" + CACHE_KEY_PREFIX + "' + ";
    final static PromptTemplate DEFAULT_PROMPT_TEMPLATE_ZH = PromptTemplate.from(
            """
                    阅读并理解用户与AI之间的对话。\
                    然后，分析用户的新查询。从对话和新查询中识别所有相关的细节、术语和上下文。\
                    将这个查询重新表述为一个清晰、简洁、自包含的格式，适合信息检索。

                    对话：
                    {{chatMemory}}

                    用户查询：{{query}}

                    非常重要的是，你只提供重新表述过的查询，不要添加其他任何内容！查询前不要加任何东西！"""
    );

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
    private AiModelInfoService aiModelInfoService;
    @Autowired
    private ChatMemoryService chatMemoryService;
    @Autowired
    private EmbeddingStoreService<TextSegment> embeddingStoreService;
    @Autowired
    private EmbeddingModelService embeddingModelService;
    @Autowired
    @Qualifier("defaultExecutor")
    private ThreadPoolTaskExecutor executor;

    private synchronized Lock getLock(Cache cache, String chatId) {
        if (Objects.isNull(cache)) {
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
            cacheManager = IN_PROCESS_CACHE_MANAGER,
            key = CACHE_KEY_SPEL_PREFIX + "#p0",
            unless="#result == null")
    public ChatSession get(String chatId) {
        ChatContext context = chatContextService.get(chatId);
        if (Objects.isNull(context)) {
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
            if (Objects.nonNull(cache)) {
                ChatSession session = cache.get(CACHE_KEY_PREFIX + chatId, ChatSession.class);
                if (Objects.nonNull(session)) {
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
            AiModelInfo modelInfo = aiModelInfoService.get(promptTask.getModelId());
            ModelProvider provider = ModelProvider.of(modelInfo.getProvider());
            Map<String, Object> parameters = StringUtils.isNotBlank(promptTask.getParams()) ?
                    InfoUtils.defaultMapper().readValue(promptTask.getParams(), new TypeReference<>() {}) :
                    Collections.emptyMap();

            ChatLanguageModel chatModel;
            StreamingChatLanguageModel streamingChatModel;
            try (var apiKeyClient = getCloseableAiApiKey(context, ownerId, promptTask)) {
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
                        case OPEN_AI -> moderationModel = createOpenAiModerationModel(apiKeyClient.token(), modelInfo.getName(), parameters);
                        default -> throw new NotImplementedException("Not implemented.");
                    }
                }
            }

            Long promptId = promptService.getLatestIdByUid(promptTask.getPromptUid(), owner);
            PromptInfo promptInfo = promptService.details(promptId, owner).getLeft();
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

            String lang = characterInfo.getLang();
            variables.put(CHARACTER_LANG.text(), LangUtils.codeToLabel(lang));
            variables.put(CHARACTER_NICKNAME.text(), characterNickname);
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
                Response<AiMessage> response = chatModel.generate(messages);
                chatMemory.addAiMessage(response.content(), response.tokenUsage());
            }

            // knowledge
            EmbeddingModel embeddingModel = embeddingModelService.modelForLang(lang);
            EmbeddingStore<TextSegment> embeddingStore =
                    embeddingStoreService.of(characterUid, documentTypeForLang(lang));

            QueryTransformer origQueryTransformer = CompressingQueryTransformer.builder()
                    .chatLanguageModel(chatModel)
                    .promptTemplate("zh".equalsIgnoreCase(lang) ?
                            DEFAULT_PROMPT_TEMPLATE_ZH :
                            CompressingQueryTransformer.DEFAULT_PROMPT_TEMPLATE)
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

            if (Objects.nonNull(longTermMemoryWindowSize) && longTermMemoryWindowSize > 0L) {
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
                    .build();
        } catch (NotImplementedException | NoSuchElementException | NullPointerException | IOException e) {
            log.warn("Failed to build chat session of {}", chatId, e);
            return null;
        } finally {
            try {
                lock.unlock();
            } catch (Throwable unlockEx) {
                log.warn("Unlock failed!", unlockEx);
            }
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
        List<ChatMessage> messagesToModerate = removeToolMessages(messages);
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

    private List<ChatMessage> removeToolMessages(List<ChatMessage> messages) {
        return messages.stream()
                .filter(it -> !(it instanceof ToolExecutionResultMessage))
                .filter(it -> !(it instanceof AiMessage aiMessage && CollectionUtils.isNotEmpty(aiMessage.toolExecutionRequests())))
                .collect(toList());
    }

    private String getOrBlank(String content) {
        return StringUtils.isBlank(content) ? "" : content;
    }
}
