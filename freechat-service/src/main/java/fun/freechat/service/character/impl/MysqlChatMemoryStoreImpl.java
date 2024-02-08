package fun.freechat.service.character.impl;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.data.message.SystemMessage;
import fun.freechat.mapper.ChatHistoryDynamicSqlSupport;
import fun.freechat.mapper.ChatHistoryMapper;
import fun.freechat.model.ChatHistory;
import fun.freechat.service.character.ChatMemoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.*;

import static dev.langchain4j.data.message.ChatMessageType.SYSTEM;
import static fun.freechat.service.util.CacheUtils.LONG_PERIOD_CACHE_NAME;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;
import static org.mybatis.dynamic.sql.SqlBuilder.isNotNull;

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

    private Cache cache;

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        if (StringUtils.isBlank((String) memoryId)) {
            return Collections.emptyList();
        }

        var messages = cache().get(CACHE_KEY_PREFIX + memoryId, List.class);
        if (CollectionUtils.isEmpty(messages)) {
            messages = loadHistories(memoryId)
                    .stream()
                    .map(MysqlChatMemoryStoreImpl::historyToMessage)
                    .filter(Objects::nonNull)
                    .toList();
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
        var cachedMessages = new LinkedList<>(getMessages(memoryId));

        SystemMessage systemMessage = firstMessage.type() == SYSTEM ? (SystemMessage) firstMessage : null;

        int rows = chatHistoryMapper.insertSelective(messageToHistory(memoryId, lastMessage, systemMessage));
        if (rows > 0) {
            cachedMessages.addLast(lastMessage);
        }

        cache().put(CACHE_KEY_PREFIX + memoryId, cachedMessages);
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
    public List<ChatMessage> listChatMessages(Object memoryId) {
        return loadHistories(memoryId)
                .stream()
                .map(MysqlChatMemoryStoreImpl::historyToMessage)
                .toList();
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

    private static ChatHistory messageToHistory(
            Object memoryId, ChatMessage message, ChatMessage systemMessage) {
        String systemMessageText = Objects.nonNull(systemMessage) && systemMessage != message ?
                ChatMessageSerializer.messageToJson(systemMessage) : null;
        String messageText = ChatMessageSerializer.messageToJson(message);
        Date now = new Date();

        return new ChatHistory()
                .withMemoryId((String) memoryId)
                .withGmtCreate(now)
                .withGmtModified(now)
                .withMessage(messageText)
                .withSystemMessage(systemMessageText)
                .withEnabled((byte) 1);
    }

    private Cache cache() {
        if (Objects.isNull(cache) && Objects.nonNull(cacheManager)) {
            cache = cacheManager.getCache(LONG_PERIOD_CACHE_NAME);
        }
        return cache;
    }
}
