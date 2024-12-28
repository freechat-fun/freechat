package fun.freechat.service.chat.impl;

import dev.langchain4j.data.message.*;
import dev.langchain4j.model.output.TokenUsage;
import fun.freechat.mapper.ChatHistoryDynamicSqlSupport;
import fun.freechat.mapper.ChatHistoryMapper;
import fun.freechat.model.CharacterBackend;
import fun.freechat.model.CharacterInfo;
import fun.freechat.model.ChatContext;
import fun.freechat.model.ChatHistory;
import fun.freechat.service.cache.MiddlePeriodCache;
import fun.freechat.service.character.CharacterService;
import fun.freechat.service.chat.*;
import fun.freechat.service.common.FileStore;
import fun.freechat.service.enums.TtsSpeakerType;
import fun.freechat.service.util.InfoUtils;
import fun.freechat.service.util.StoreUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

import static dev.langchain4j.data.message.ChatMessageType.*;
import static fun.freechat.service.util.CacheUtils.IN_PROCESS_LONG_CACHE_MANAGER;
import static fun.freechat.service.util.CacheUtils.LONG_PERIOD_CACHE_NAME;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

@Service("mysqlChatMemoryStore")
@Slf4j
@SuppressWarnings("unused")
public class MysqlChatMemoryStoreImpl implements ChatMemoryService {
    private static final String CACHE_KEY_PREFIX = "MysqlChatMemoryStoreImpl_";
    private static final String CACHE_KEY_SPEL_PREFIX = "'" + CACHE_KEY_PREFIX + "' + ";
    private static final String CACHE_KEY_FOR_MESSAGE_RECORD_SPEL_PREFIX = "'" + CACHE_KEY_PREFIX + "record_' + ";
    private static final String SYSTEM_MESSAGE_HOME = "private/messages/";

    @Value("${chat.memory.maxMessageSize:1000}")
    private Integer maxSize;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private CharacterService characterService;
    @Autowired
    private ChatHistoryMapper chatHistoryMapper;
    @Autowired
    private ChatContextService chatContextService;
    private Cache cache;

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        return getMessageRecords(memoryId).stream()
                .map(ChatMessageRecord::getMessage)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        if (StringUtils.isBlank((String) memoryId) || CollectionUtils.isEmpty(messages)) {
            return;
        }

        var firstMessage = messages.getFirst();
        var lastMessage = messages.getLast();
        LinkedList<ChatMessageRecord> cachedList = getMessageRecords(memoryId);

        SystemMessage systemMessage = firstMessage.type() == SYSTEM ? (SystemMessage) firstMessage : null;
        ChatHistory history = messageToHistory(memoryId, lastMessage, null);
        chatHistoryMapper.insertSelective(history);
        if (history.getId() != null) {
            // the system message may be too large
            // store it as a file instead of db record
            saveSystemMessage(memoryId, history.getId(), systemMessage);

            cachedList.add(ChatMessageRecord.builder()
                    .id(history.getId())
                    .message(lastMessage)
                    .build());
            List<ChatMessageRecord> removedRecords = ensureCapacity(cachedList, messages.size());
            if (CollectionUtils.isNotEmpty(removedRecords)) {
                eventPublisher.publishEvent(new ChatMemoryReducedEvent(memoryId, removedRecords));
            }
            cache().put(CACHE_KEY_PREFIX + memoryId, cachedList);
        }
    }

    @Override
    public void deleteMessages(Object memoryId) {
        chatHistoryMapper.update(c ->
                c.set(ChatHistoryDynamicSqlSupport.enabled).equalTo((byte) 0)
                        .where(ChatHistoryDynamicSqlSupport.memoryId, isEqualTo((String) memoryId))
                        .and(ChatHistoryDynamicSqlSupport.enabled, isEqualTo((byte) 1)));

        cache().evict(CACHE_KEY_PREFIX + memoryId);
    }

    @Override
    public Long addAiMessage(Object memoryId, AiMessage message, TokenUsage usage) {
        if (StringUtils.isBlank((String) memoryId) || message == null) {
            return null;
        }

        ChatHistory history = messageToHistory(memoryId, message, usage);
        chatHistoryMapper.insertSelective(history);
        if (history.getId() != null) {
            List<ChatMessageRecord> cachedList = getMessageRecords(memoryId);
            cachedList.add(ChatMessageRecord.builder()
                    .id(history.getId())
                    .message(message)
                    .build());
           cache().put(CACHE_KEY_PREFIX + memoryId, cachedList);
           return history.getId();
        }

        return null;
    }

    @Override
    public Long updateChatMessageTokenUsage(Object memoryId, AiMessage message, TokenUsage tokenUsage) {
        if (tokenUsage == null || message == null) {
            return null;
        }

        LinkedList<ChatMessageRecord> cachedList = getMessageRecords(memoryId);
        var it = cachedList.descendingIterator();
        while(it.hasNext()) {
            ChatMessageRecord messageRecord = it.next();
            if (message.equals(messageRecord.getMessage())) {
                ChatHistory newHistory = new ChatHistory()
                        .withId(messageRecord.getId())
                        .withExt(InfoUtils.serialize(tokenUsage))
                        .withGmtModified(new Date());
                chatHistoryMapper.updateByPrimaryKeySelective(newHistory);
                return newHistory.getId();
            }
        }

        return null;
    }

    @Override
    public List<ChatMessageRecord> listAllChatMessages(Object memoryId) {
        return loadHistories(memoryId)
                .stream()
                .map(this::historyToFullMessageRecord)
                .filter(Objects::nonNull)
                .reduce(new LinkedList<>(), messageRecordAccumulator(), messageRecordCombiner());
    }

    @Override
    public ChatMessageRecord getLatestChatMessage(Object memoryId) {
        return chatHistoryMapper.selectOne(c ->
                        c.where(ChatHistoryDynamicSqlSupport.memoryId, isEqualTo((String) memoryId))
                                .and(ChatHistoryDynamicSqlSupport.enabled, isEqualTo((byte) 1))
                                .orderBy(ChatHistoryDynamicSqlSupport.id.descending())
                                .limit(1))
                .map(this::historyToFullMessageRecord)
                .orElse(null);
    }

    @Override
    public List<Long> rollback(Object memoryId, Integer count) {
        if (StringUtils.isBlank((String) memoryId) || count == null || count <= 0) {
            return Collections.emptyList();
        }

        var statement = select(ChatHistoryDynamicSqlSupport.id)
                .from(ChatHistoryDynamicSqlSupport.chatHistory)
                .where(ChatHistoryDynamicSqlSupport.memoryId, isEqualTo((String) memoryId))
                .and(ChatHistoryDynamicSqlSupport.enabled, isEqualTo((byte) 1))
                .orderBy(ChatHistoryDynamicSqlSupport.id.descending())
                .limit(count);

        List<Long> ids = chatHistoryMapper.selectMany(statement.build().render(RenderingStrategies.MYBATIS3))
                .stream()
                .map(ChatHistory::getId)
                .toList();

        if (CollectionUtils.isNotEmpty(ids)) {
            chatHistoryMapper.update(c ->
                    c.set(ChatHistoryDynamicSqlSupport.enabled)
                            .equalTo((byte) 0)
                            .where(ChatHistoryDynamicSqlSupport.id, isIn(ids)));
        }

        cache().evict(CACHE_KEY_PREFIX + memoryId);
        return ids;
    }

    @Override
    public MemoryUsage usage(Object memoryId) {
        return chatHistoryMapper.select(c -> c.where(ChatHistoryDynamicSqlSupport.memoryId, isEqualTo((String) memoryId))
                        .and(ChatHistoryDynamicSqlSupport.message, isNotNull()))
                .stream()
                .filter(history -> {
                    ChatMessage message = historyToMessage(history);
                    return message != null && message.type() == AI;
                })
                .map(ChatHistory::getExt)
                .map(InfoUtils::deserialize)
                .reduce(new MemoryUsage(null, null),
                        (acc, tokenUsage) -> acc.add(1L, tokenUsage),
                        MemoryUsage::add);
    }

    @Override
    @MiddlePeriodCache
    public String getLang(Object memoryId) {
        return Optional.ofNullable(memoryId)
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .map(chatContextService::get)
                .map(ChatContext::getBackendId)
                .map(characterService::getBackendCharacterUid)
                .map(characterService::getLatestIdByUid)
                .map(characterService::summary)
                .map(CharacterInfo::getLang)
                .orElse("en");
    }

    @Override
    @MiddlePeriodCache
    public Long roughCount(Object memoryId) {
        var statement = select(count(ChatHistoryDynamicSqlSupport.id))
                .from(ChatHistoryDynamicSqlSupport.chatHistory)
                .where(ChatHistoryDynamicSqlSupport.memoryId, isEqualTo((String) memoryId))
                .and(ChatHistoryDynamicSqlSupport.enabled, isEqualTo((byte) 1))
                .and(ChatHistoryDynamicSqlSupport.message, isNotNull())
                .build()
                .render(RenderingStrategies.MYBATIS3);

        return chatHistoryMapper.count(statement);
    }

    @Override
    @Cacheable(cacheNames = LONG_PERIOD_CACHE_NAME,
            cacheManager = IN_PROCESS_LONG_CACHE_MANAGER,
            key = CACHE_KEY_SPEL_PREFIX + "#p0",
            unless="#result == null")
    public String loadSystemMessage(Long id) {
        var statement = select(ChatHistoryDynamicSqlSupport.memoryId)
                .from(ChatHistoryDynamicSqlSupport.chatHistory)
                .where(ChatHistoryDynamicSqlSupport.id, isEqualTo(id));

        String memoryId = chatHistoryMapper.selectOne(statement.build().render(RenderingStrategies.MYBATIS3))
                .map(ChatHistory::getMemoryId)
                .orElse(null);

        if (StringUtils.isBlank(memoryId)) {
            return null;
        }

        try {
            FileStore fileStore = StoreUtils.defaultFileStore();
            if (fileStore != null) {
                return fileStore.readString(messagePath(memoryId, id));
            }
        } catch (Exception e) {
            log.warn("Failed to load system message of {}", id, e);
        }

        return null;
    }

    @Override
    @Cacheable(cacheNames = LONG_PERIOD_CACHE_NAME,
            cacheManager = IN_PROCESS_LONG_CACHE_MANAGER,
            key = CACHE_KEY_FOR_MESSAGE_RECORD_SPEL_PREFIX + "#p0",
            unless="#result == null")
    public ChatMessageRecord get(Long id) {
        return chatHistoryMapper.selectByPrimaryKey(id)
                .map(this::historyToFullMessageRecord)
                .orElse(null);
    }

    private LinkedList<ChatMessageRecord> getMessageRecords(Object memoryId) {
        if (StringUtils.isBlank((String) memoryId)) {
            return new LinkedList<>();
        }

        List<ChatMessageRecord> messageRecords = cache().get(CACHE_KEY_PREFIX + memoryId, List.class);
        LinkedList<ChatMessageRecord> filteredMessageRecords = Optional.ofNullable(messageRecords)
                .orElseGet(() -> loadHistories(memoryId)
                        .stream()
                        .map(this::historyToBasicMessageRecord)
                        .filter(Objects::nonNull)
                        .toList())
                .stream()
                .reduce(new LinkedList<>(), messageRecordAccumulator(), messageRecordCombiner());

        if (CollectionUtils.isEmpty(messageRecords) && CollectionUtils.isNotEmpty(filteredMessageRecords)) {
            cache().put(CACHE_KEY_PREFIX + memoryId, filteredMessageRecords);
        }
        return filteredMessageRecords;
    }

    private List<ChatHistory> loadHistories(Object memoryId) {
        List<ChatHistory> histories =
                chatHistoryMapper.select(c ->
                        c.where(ChatHistoryDynamicSqlSupport.memoryId, isEqualTo((String) memoryId))
                                .and(ChatHistoryDynamicSqlSupport.enabled, isEqualTo((byte) 1))
                                .and(ChatHistoryDynamicSqlSupport.message, isNotNull())
                                .orderBy(ChatHistoryDynamicSqlSupport.id.descending())
                                .limit(maxSize))
                .reversed();

        if (histories.size() >= maxSize) {
            // There are too many histories. Query the system message (known as the first history).
            ChatHistory systemHistory = loadSystemHistory(memoryId);
            if (systemHistory != null &&
                    !Objects.equals(systemHistory.getId(), histories.getFirst().getId())) {
                histories = new LinkedList<>(histories);
                histories.addFirst(systemHistory);
            }
        }
        return histories;
    }

    private ChatHistory loadSystemHistory(Object memoryId) {
        return chatHistoryMapper.selectOne(c ->
                c.where(ChatHistoryDynamicSqlSupport.memoryId, isEqualTo((String) memoryId))
                        .and(ChatHistoryDynamicSqlSupport.enabled, isEqualTo((byte) 1))
                        .orderBy(ChatHistoryDynamicSqlSupport.id)
                        .limit(1))
                .orElse(null);
    }

    private static ChatMessage historyToMessage(ChatHistory history) {
        return Optional.ofNullable(history)
                .map(ChatHistory::getMessage)
                .filter(StringUtils::isNotBlank)
                .map(ChatMessageDeserializer::messageFromJson)
                .orElse(null);
    }

    private ChatMessageRecord historyToFullMessageRecord(ChatHistory history) {
        return historyToMessageRecord(history, false);
    }

    private ChatMessageRecord historyToBasicMessageRecord(ChatHistory history) {
        return historyToMessageRecord(history, true);
    }

    private ChatMessageRecord historyToMessageRecord(ChatHistory history, boolean basic) {
        if (history == null) {
            return null;
        }

        ChatMessage message = historyToMessage(history);
        if (message == null) {
            return null;
        }

        if (basic) {
            return ChatMessageRecord.builder()
                    .id(history.getId())
                    .message(message)
                    .build();
        }

        String chatId = history.getMemoryId();
        String chatOwner = chatContextService.getChatOwner(chatId);
        String characterOwner = chatContextService.getCharacterOwner(chatId);
        String backendId = chatContextService.get(chatId).getBackendId();
        CharacterBackend backend = characterService.getBackend(backendId);
        TtsSpeakerType speakerType = TtsSpeakerType.of(backend.getTtsSpeakerType());
        String speaker = speakerType == TtsSpeakerType.IDX ? backend.getTtsSpeakerIdx() : backend.getTtsSpeakerWav();

        return ChatMessageRecord.builder()
                .id(history.getId())
                .message(message)
                .gmtCreate(history.getGmtCreate())
                .chatId(chatId)
                .chatOwnerId(chatOwner)
                .characterOwnerId(characterOwner)
                .speaker(speaker)
                .speakerType(speakerType)
                .ext(history.getExt())
                .build();
    }

    private static ChatHistory messageToHistory(
            Object memoryId, ChatMessage message, TokenUsage tokenUsage) {
        String ext = null;
        if (message.type() == AI && tokenUsage != null) {
            ext =InfoUtils.serialize(tokenUsage);
        }
        String messageText = ChatMessageSerializer.messageToJson(message);
        Date now = new Date();

        return new ChatHistory()
                .withMemoryId((String) memoryId)
                .withGmtCreate(now)
                .withGmtModified(now)
                .withMessage(messageText)
                .withExt(ext)
                .withEnabled((byte) 1);
    }

    private static void saveSystemMessage(Object memoryId, Long id, SystemMessage systemMessage) {
        if (systemMessage == null) {
            return;
        }

        try {
            FileStore fileStore = StoreUtils.defaultFileStore();
            if (fileStore != null) {
                String dir = messageDirectory((String) memoryId);
                if (!fileStore.exists(dir)) {
                   fileStore.createDirectories(dir);

                }
                fileStore.write(messagePath((String) memoryId, id), ChatMessageSerializer.messageToJson(systemMessage));
            }
        } catch (Exception e) {
            log.warn("Failed to save system message of {}", id, e);
        }
    }

    private static String messageDirectory(String memoryId) {
        return SYSTEM_MESSAGE_HOME + File.separator + memoryId;
    }

    private static String messagePath(String memoryId, Long id) {
        return messageDirectory(memoryId) + File.separator + id + ".json";
    }

    private Cache cache() {
        if (cache == null && cacheManager != null) {
            cache = cacheManager.getCache(LONG_PERIOD_CACHE_NAME);
        }
        return  Objects.requireNonNull(cache);
    }

    private static BiFunction<LinkedList<ChatMessageRecord>, ChatMessageRecord, LinkedList<ChatMessageRecord>> messageRecordAccumulator() {
        return (acc, messageRecord) -> {
            ChatMessageType type = messageRecord.getMessage().type();
            if (acc.isEmpty()) {
                if (type == SYSTEM) {
                    acc.add(messageRecord);
                }
                return acc;
            }

            ChatMessageType lastType = acc.getLast().getMessage().type();
            if (lastType == SYSTEM && type != USER) {
                // First non-system message must be a user message.
                return acc;
            }

            if (type == USER) {
                // User message should follow a normal system/AI message.
                while (acc.getLast().getMessage().type() != SYSTEM && !isNormalAiType(acc.getLast().getMessage())) {
                    acc.removeLast();
                }
            } else if (type == TOOL_EXECUTION_RESULT) {
                // Tool execution result should follow a tool execution request message.
                while (!isToolExecutionRequestsAiType(acc.getLast().getMessage())) {
                    acc.removeLast();
                }
            } else if (type == AI) {
                // AI message should follow a user/tool_execution_result message.
                while (!isInputMessageType(acc.getLast().getMessage())) {
                    acc.removeLast();
                }
            }

            acc.add(messageRecord);
            return acc;
        };
    }

    private static boolean isInputMessageType(ChatMessage message) {
        ChatMessageType type = message.type();
        return type == USER || type == TOOL_EXECUTION_RESULT;
    }

    private static boolean isNormalAiType(ChatMessage message) {
        return message.type() == AI && !((AiMessage) message).hasToolExecutionRequests();
    }

    private static boolean isToolExecutionRequestsAiType(ChatMessage message) {
        return message.type() == AI && ((AiMessage) message).hasToolExecutionRequests();
    }

    private static BinaryOperator<LinkedList<ChatMessageRecord>> messageRecordCombiner() {
        return (acc1, acc2) -> {
            throw new UnsupportedOperationException("Parallel Stream not supported");
        };
    }

    private static int firstNonSystemMessageRecordIndex(List<ChatMessageRecord> records) {
        return records.getFirst().getMessage().type() == SYSTEM ? 1 : 0;
    }

    private static List<ChatMessageRecord> ensureCapacity(List<ChatMessageRecord> records, int maxRecords) {
        if (maxRecords < 1 || records.size() <= maxRecords) {
            return Collections.emptyList();
        }

        List<ChatMessageRecord> removedRecords = new LinkedList<>();
        int firstIndex = firstNonSystemMessageRecordIndex(records);
        while (records.size() > maxRecords ||
                (records.size() > firstIndex && records.get(firstIndex).getMessage().type() != USER)) {
            removedRecords.add(records.remove(firstIndex));
        }
        return removedRecords;
    }
}
