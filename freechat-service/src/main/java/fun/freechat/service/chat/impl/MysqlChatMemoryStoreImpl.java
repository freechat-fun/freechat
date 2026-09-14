package fun.freechat.service.chat.impl;

import static dev.langchain4j.data.message.ChatMessageType.*;
import static fun.freechat.service.util.CacheUtils.IN_PROCESS_LONG_CACHE_MANAGER;
import static fun.freechat.service.util.CacheUtils.LONG_PERIOD_CACHE_NAME;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

import dev.langchain4j.data.message.*;
import dev.langchain4j.model.output.TokenUsage;
import fun.freechat.mapper.ChatHistoryDynamicSqlSupport;
import fun.freechat.mapper.ChatHistoryMapper;
import fun.freechat.mapper.ChatMemoryCoordinationMapper;
import fun.freechat.model.CharacterBackend;
import fun.freechat.model.CharacterInfo;
import fun.freechat.model.ChatContext;
import fun.freechat.model.ChatHistory;
import fun.freechat.service.cache.MiddlePeriodCache;
import fun.freechat.service.character.CharacterService;
import fun.freechat.service.chat.*;
import fun.freechat.service.chat.memory.MemoryLifecycleRepository;
import fun.freechat.service.enums.TtsSpeakerType;
import fun.freechat.service.util.CacheUtils;
import fun.freechat.service.util.InfoUtils;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.dynamic.sql.BasicColumn;
import org.mybatis.dynamic.sql.dsl.SelectDSLCompleter;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.util.mybatis3.MyBatis3Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service("mysqlChatMemoryStore")
@Slf4j
@SuppressWarnings("unused")
public class MysqlChatMemoryStoreImpl implements ChatMemoryService {
    private static final String CACHE_KEY_PREFIX = "MysqlChatMemoryStoreImpl_";
    private static final String CACHE_KEY_SPEL_PREFIX = "'" + CACHE_KEY_PREFIX + "' + ";
    private static final String CACHE_KEY_FOR_MESSAGE_RECORD_SPEL_PREFIX = "'" + CACHE_KEY_PREFIX + "record_' + ";

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
    private SystemPromptSnapshotStore snapshots;

    @Autowired
    private ChatMemoryCoordinationMapper chatMemoryCoordinationMapper;

    @Autowired
    private MemoryLifecycleRepository lifecycle;

    @Autowired
    private ChatTaskQueueManager queueManager;

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
        ChatHistory history = messageToHistory(memoryId, lastMessage, null)
                .withSystemMessageRef(snapshots.save((String) memoryId, systemMessage));
        chatHistoryMapper.insertSelective(history);
        if (history.getId() != null) {
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
        String chatId = (String) memoryId;
        var lock = queueManager.coordinationLock(chatId);
        lock.lock(); // Watchdog lease, with same-thread reentry from queue work. Never drain here.
        try {
            lifecycle.clear(chatId);
            evictMemoryCaches(chatId, List.of());
        } finally {
            lock.unlock();
        }
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
        while (it.hasNext()) {
            ChatMessageRecord messageRecord = it.next();
            if (message.equals(messageRecord.getMessage())) {
                // Canonical invocation rows own per-call accounting. Equality is only a legacy fallback;
                // guard in SQL as reconciliation can tag a row after this cache was populated.
                int updated = chatHistoryMapper.update(c -> c.set(ChatHistoryDynamicSqlSupport.ext)
                        .equalTo(InfoUtils.serialize(tokenUsage))
                        .set(ChatHistoryDynamicSqlSupport.gmtModified)
                        .equalTo(LocalDateTime.now())
                        .where(ChatHistoryDynamicSqlSupport.id, isEqualTo(messageRecord.getId()))
                        .and(ChatHistoryDynamicSqlSupport.memoryId, isEqualTo((String) memoryId))
                        .and(ChatHistoryDynamicSqlSupport.turnId, isNull()));
                return updated > 0 ? messageRecord.getId() : null;
            }
        }

        return null;
    }

    @Override
    public List<ChatMessageRecord> listAllChatMessages(Object memoryId) {
        return loadHistories(memoryId).stream()
                .map(this::historyToFullMessageRecord)
                .filter(Objects::nonNull)
                .reduce(new LinkedList<>(), messageRecordAccumulator(), messageRecordCombiner());
    }

    @Override
    public ChatMessageRecord getLatestChatMessage(Object memoryId) {
        return selectHistories(c -> c.where(ChatHistoryDynamicSqlSupport.memoryId, isEqualTo((String) memoryId))
                        .and(ChatHistoryDynamicSqlSupport.enabled, isEqualTo((byte) 1))
                        .and(ChatHistoryDynamicSqlSupport.message, isNotNull())
                        .orderBy(ChatHistoryDynamicSqlSupport.id.descending())
                        .limit(1))
                .stream()
                .findFirst()
                .map(this::historyToFullMessageRecord)
                .orElse(null);
    }

    @Override
    public List<Long> rollback(Object memoryId, Integer count) {
        if (StringUtils.isBlank((String) memoryId) || count == null || count <= 0) {
            return Collections.emptyList();
        }

        String chatId = (String) memoryId;
        var lock = queueManager.coordinationLock(chatId);
        lock.lock();
        try {
            List<Long> ids = lifecycle.rollback(chatId, count);
            evictMemoryCaches(chatId, ids);
            return ids;
        } finally {
            lock.unlock();
        }
    }

    private void evictMemoryCaches(String chatId, List<Long> ids) {
        cache().evict(CACHE_KEY_PREFIX + chatId);
        CacheUtils.middlePeriodCacheEvict(List.of(MysqlChatMemoryStoreImpl.class.getName() + "::roughCount_" + chatId));
        var local = CacheUtils.inProcessLongPeriodCache();
        if (local != null) {
            local.evict("ChatSessionService_" + chatId);
            ids.forEach(id -> local.evict(CACHE_KEY_PREFIX + "record_" + id));
        }
    }

    @Override
    public MemoryUsage usage(Object memoryId) {
        MemoryUsage[] total = {new MemoryUsage(null, null)};
        chatMemoryCoordinationMapper.conversationUsage(
                (String) memoryId, row -> total[0] = total[0].add(1L, InfoUtils.deserialize(row.getResultObject())));
        return total[0];
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
    @Cacheable(
            cacheNames = LONG_PERIOD_CACHE_NAME,
            cacheManager = IN_PROCESS_LONG_CACHE_MANAGER,
            key = CACHE_KEY_SPEL_PREFIX + "#p0",
            unless = "#result == null")
    public String loadSystemMessage(Long id) {
        var statement = select(ChatHistoryDynamicSqlSupport.memoryId, ChatHistoryDynamicSqlSupport.systemMessageRef)
                .from(ChatHistoryDynamicSqlSupport.chatHistory)
                .where(ChatHistoryDynamicSqlSupport.id, isEqualTo(id));

        ChatHistory history = chatHistoryMapper
                .selectOne(statement.build().render(RenderingStrategies.MYBATIS3))
                .orElse(null);
        if (history == null || history.getSystemMessageRef() == null) {
            return null;
        }
        try {
            return snapshots.read(history.getMemoryId(), history.getSystemMessageRef());
        } catch (IllegalStateException ignored) {
            log.warn("Failed to load system prompt snapshot of {}", id);
            return null;
        }
    }

    @Override
    @Cacheable(
            cacheNames = LONG_PERIOD_CACHE_NAME,
            cacheManager = IN_PROCESS_LONG_CACHE_MANAGER,
            key = CACHE_KEY_FOR_MESSAGE_RECORD_SPEL_PREFIX + "#p0",
            unless = "#result == null")
    public ChatMessageRecord get(Long id) {
        return selectHistories(c -> c.where(ChatHistoryDynamicSqlSupport.id, isEqualTo(id))).stream()
                .findFirst()
                .map(this::historyToFullMessageRecord)
                .orElse(null);
    }

    private LinkedList<ChatMessageRecord> getMessageRecords(Object memoryId) {
        if (StringUtils.isBlank((String) memoryId)) {
            return new LinkedList<>();
        }

        boolean canonical = !chatHistoryMapper
                .selectMany(select(ChatHistoryDynamicSqlSupport.id)
                        .from(ChatHistoryDynamicSqlSupport.chatHistory)
                        .where(ChatHistoryDynamicSqlSupport.memoryId, isEqualTo((String) memoryId))
                        .and(ChatHistoryDynamicSqlSupport.turnId, isNotNull())
                        .limit(1)
                        .build()
                        .render(RenderingStrategies.MYBATIS3))
                .isEmpty();
        // Fenced writes and other nodes do not update the legacy transcript cache.
        List<ChatMessageRecord> messageRecords =
                canonical ? null : cache().get(CACHE_KEY_PREFIX + memoryId, List.class);
        LinkedList<ChatMessageRecord> filteredMessageRecords = Optional.ofNullable(messageRecords)
                .orElseGet(() -> loadHistories(memoryId).stream()
                        .map(this::historyToBasicMessageRecord)
                        .filter(Objects::nonNull)
                        .toList())
                .stream()
                .reduce(new LinkedList<>(), messageRecordAccumulator(), messageRecordCombiner());

        if (!canonical
                && CollectionUtils.isEmpty(messageRecords)
                && CollectionUtils.isNotEmpty(filteredMessageRecords)) {
            cache().put(CACHE_KEY_PREFIX + memoryId, filteredMessageRecords);
        }
        return filteredMessageRecords;
    }

    private List<ChatHistory> loadHistories(Object memoryId) {
        List<ChatHistory> histories = selectHistories(
                        c -> c.where(ChatHistoryDynamicSqlSupport.memoryId, isEqualTo((String) memoryId))
                                .and(ChatHistoryDynamicSqlSupport.enabled, isEqualTo((byte) 1))
                                .and(ChatHistoryDynamicSqlSupport.message, isNotNull())
                                .orderBy(ChatHistoryDynamicSqlSupport.id.descending())
                                .limit(maxSize))
                .reversed();

        if (histories.size() >= maxSize) {
            // There are too many histories. Query the system message (known as the first history).
            ChatHistory systemHistory = loadSystemHistory(memoryId);
            if (systemHistory != null
                    && !Objects.equals(
                            systemHistory.getId(), histories.getFirst().getId())) {
                histories = new LinkedList<>(histories);
                histories.addFirst(systemHistory);
            }
        }
        return histories;
    }

    private ChatHistory loadSystemHistory(Object memoryId) {
        return selectHistories(c -> c.where(ChatHistoryDynamicSqlSupport.memoryId, isEqualTo((String) memoryId))
                        .and(ChatHistoryDynamicSqlSupport.enabled, isEqualTo((byte) 1))
                        .and(ChatHistoryDynamicSqlSupport.message, isNotNull())
                        .orderBy(ChatHistoryDynamicSqlSupport.id)
                        .limit(1))
                .stream()
                .findFirst()
                .orElse(null);
    }

    private List<ChatHistory> selectHistories(SelectDSLCompleter selection) {
        return MyBatis3Utils.selectList(
                chatHistoryMapper::selectMany,
                new BasicColumn[] {
                    ChatHistoryDynamicSqlSupport.id,
                    ChatHistoryDynamicSqlSupport.memoryId,
                    ChatHistoryDynamicSqlSupport.gmtCreate,
                    ChatHistoryDynamicSqlSupport.message,
                    ChatHistoryDynamicSqlSupport.ext
                },
                ChatHistoryDynamicSqlSupport.chatHistory,
                selection);
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

    private static ChatHistory messageToHistory(Object memoryId, ChatMessage message, TokenUsage tokenUsage) {
        String ext = null;
        if (message.type() == AI && tokenUsage != null) {
            ext = InfoUtils.serialize(tokenUsage);
        }
        String messageText = ChatMessageSerializer.messageToJson(message);
        LocalDateTime now = LocalDateTime.now();

        return new ChatHistory()
                .withMemoryId((String) memoryId)
                .withGmtCreate(now)
                .withGmtModified(now)
                .withMessage(messageText)
                .withExt(ext)
                .withEnabled((byte) 1);
    }

    private Cache cache() {
        if (cache == null && cacheManager != null) {
            cache = cacheManager.getCache(LONG_PERIOD_CACHE_NAME);
        }
        return Objects.requireNonNull(cache);
    }

    private static BiFunction<LinkedList<ChatMessageRecord>, ChatMessageRecord, LinkedList<ChatMessageRecord>>
            messageRecordAccumulator() {
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
                while (acc.getLast().getMessage().type() != SYSTEM
                        && !isNormalAiType(acc.getLast().getMessage())) {
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
        while (records.size() > maxRecords
                || (records.size() > firstIndex
                        && records.get(firstIndex).getMessage().type() != USER)) {
            removedRecords.add(records.remove(firstIndex));
        }
        return removedRecords;
    }
}
