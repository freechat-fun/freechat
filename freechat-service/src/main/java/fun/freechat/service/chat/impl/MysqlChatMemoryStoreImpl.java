package fun.freechat.service.chat.impl;

import dev.langchain4j.data.message.*;
import dev.langchain4j.model.output.TokenUsage;
import fun.freechat.mapper.ChatHistoryDynamicSqlSupport;
import fun.freechat.mapper.ChatHistoryMapper;
import fun.freechat.model.ChatHistory;
import fun.freechat.service.chat.ChatContextService;
import fun.freechat.service.chat.ChatMemoryService;
import fun.freechat.service.chat.ChatMessageRecord;
import fun.freechat.service.util.InfoUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.*;

import static dev.langchain4j.data.message.ChatMessageType.*;
import static fun.freechat.service.util.CacheUtils.LONG_PERIOD_CACHE_NAME;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

@Service("mysqlChatMemoryStore")
@Slf4j
@SuppressWarnings("unused")
public class MysqlChatMemoryStoreImpl implements ChatMemoryService {
    private final static String CACHE_KEY_PREFIX = "MysqlChatMemoryStoreImpl_";

    @Value("${chat.memory.maxMessageSize:10000}")
    private Integer maxSize;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private ChatHistoryMapper chatHistoryMapper;

    @Autowired
    private ChatContextService chatContextService;

    private Cache cache;

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        if (StringUtils.isBlank((String) memoryId)) {
            return Collections.emptyList();
        }

        List<ChatMessage> messages = cache().get(CACHE_KEY_PREFIX + memoryId, List.class);
        if (CollectionUtils.isEmpty(messages)) {
            messages = loadHistories(memoryId)
                    .stream()
                    .map(MysqlChatMemoryStoreImpl::historyToMessage)
                    .filter(Objects::nonNull)
                    .reduce(new LinkedList<>(), (acc, message) -> {
                        if (!acc.isEmpty() && acc.getLast().type() == message.type()) {
                            acc.removeLast();
                        }
                        acc.add(message);
                        return acc;
                    }, (acc1, acc2) -> {
                        throw new UnsupportedOperationException("Parallel Stream not supported");
                    });
            cache().put(CACHE_KEY_PREFIX + memoryId, messages);
        }
        return messages;
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        if (StringUtils.isBlank((String) memoryId) || CollectionUtils.isEmpty(messages)) {
            return;
        }

        var firstMessage = messages.getFirst();
        var lastMessage = messages.getLast();

        SystemMessage systemMessage = firstMessage.type() == SYSTEM ? (SystemMessage) firstMessage : null;

        if (lastMessage.type() == SYSTEM && Objects.nonNull(loadSystemHistory(memoryId))) {
            // No need to update the system message record.
            cache().put(CACHE_KEY_PREFIX + memoryId, messages);
        } else {
            int rows = chatHistoryMapper.insertSelective(
                    messageToHistory(memoryId, lastMessage, systemMessage, null));
            if (rows > 0) {
                cache().put(CACHE_KEY_PREFIX + memoryId, messages);
            }
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
    public void addAiMessage(Object memoryId, AiMessage message, TokenUsage usage) {
        if (StringUtils.isBlank((String) memoryId) || Objects.isNull(message)) {
            return;
        }

        var cachedMessages = new LinkedList<>(getMessages(memoryId));
        int rows = chatHistoryMapper.insertSelective(
                messageToHistory(memoryId, message, null, usage));
        if (rows > 0) {
            cachedMessages.addLast(message);
        }

        cache().put(CACHE_KEY_PREFIX + memoryId, cachedMessages);
    }

    @Override
    public void updateChatMessageTokenUsage(Object memoryId, AiMessage message, TokenUsage tokenUsage) {
        if (Objects.isNull(tokenUsage)) {
            return;
        }

        Optional<ChatHistory> history = chatHistoryMapper.selectOne(c ->
                c.where(ChatHistoryDynamicSqlSupport.memoryId, isEqualTo((String) memoryId))
                        .and(ChatHistoryDynamicSqlSupport.enabled, isEqualTo((byte) 1))
                        .and(ChatHistoryDynamicSqlSupport.message, isNotNull())
                        .and(ChatHistoryDynamicSqlSupport.ext, isNull())
                        .orderBy(ChatHistoryDynamicSqlSupport.id.descending())
                        .limit(1));

        if (history.isPresent()) {
            ChatHistory newHistory = new ChatHistory()
                    .withId(history.get().getId())
                    .withExt(InfoUtils.serialize(tokenUsage))
                    .withGmtModified(new Date());
            chatHistoryMapper.updateByPrimaryKeySelective(newHistory);
        }
    }

    @Override
    public List<ChatMessageRecord> listChatMessages(Object memoryId) {
        return loadHistories(memoryId)
                .stream()
                .map(this::historyToMessageRecord)
                .reduce(new LinkedList<>(), (acc, record) -> {
                    if (!acc.isEmpty() &&
                            acc.getLast().getMessage().type() == record.getMessage().type()) {
                        acc.removeLast();
                    }
                    acc.add(record);
                    return acc;
                }, (acc1, acc2) -> {
                    throw new UnsupportedOperationException("Parallel Stream not supported");
                });
    }

    @Override
    public ChatMessageRecord getLatestChatMessage(Object memoryId) {
        return chatHistoryMapper.selectOne(c ->
                        c.where(ChatHistoryDynamicSqlSupport.memoryId, isEqualTo((String) memoryId))
                                .and(ChatHistoryDynamicSqlSupport.enabled, isEqualTo((byte) 1))
                                .orderBy(ChatHistoryDynamicSqlSupport.id.descending())
                                .limit(1))
                .map(this::historyToMessageRecord)
                .orElse(null);
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

        // We need a mutable list.
        List<ChatHistory> loadedHistories = new LinkedList<>(histories);

        if (loadedHistories.size() >= maxSize) {
            // There are too many histories. Query the system message (known as the first history).
            ChatHistory systemHistory = loadSystemHistory(memoryId);
            if (Objects.nonNull(systemHistory)) {
                loadedHistories.addFirst(systemHistory);
            }
        }
        return loadedHistories;
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

    private ChatMessageRecord historyToMessageRecord(ChatHistory history) {
        return Optional.ofNullable(history)
                .map(row -> {
                    ChatMessage message = historyToMessage(history);
                    if (Objects.isNull(message)) {
                        return null;
                    }
                    String chatOwner = chatContextService.getChatOwner(history.getMemoryId());
                    String characterOwner = chatContextService.getCharacterOwner(history.getMemoryId());

                    ChatMessageRecord record = new ChatMessageRecord();
                    record.setMessage(message);
                    record.setId(history.getId());
                    record.setGmtCreate(history.getGmtCreate());
                    record.setExt(history.getExt());
                    record.setChatOwnerId(chatOwner);
                    record.setCharacterOwnerId(characterOwner);

                    return record;
                })
                .orElse(null);
    }

    private static ChatHistory messageToHistory(
            Object memoryId, ChatMessage message, ChatMessage systemMessage, TokenUsage tokenUsage) {
        String ext = null;
        if (message.type() == USER && Objects.nonNull(systemMessage)) {
            ext = ChatMessageSerializer.messageToJson(systemMessage);
        } else if (message.type() == AI && Objects.nonNull(tokenUsage)) {
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

    private Cache cache() {
        if (Objects.isNull(cache) && Objects.nonNull(cacheManager)) {
            cache = cacheManager.getCache(LONG_PERIOD_CACHE_NAME);
        }
        return cache;
    }
}
