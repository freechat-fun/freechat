package fun.freechat.service.character.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import fun.freechat.mapper.ChatHistoryDynamicSqlSupport;
import fun.freechat.mapper.ChatHistoryMapper;
import fun.freechat.model.ChatHistory;
import fun.freechat.service.ai.message.ChatMessage;
import fun.freechat.service.character.ChatMemoryService;
import fun.freechat.service.util.InfoUtils;
import fun.freechat.service.util.PromptUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.*;

import static fun.freechat.service.util.CacheUtils.LONG_PERIOD_CACHE_NAME;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

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
    public List<dev.langchain4j.data.message.ChatMessage> getMessages(Object memoryId) {
        if (StringUtils.isBlank((String) memoryId)) {
            return Collections.emptyList();
        }

        var messages = cache().get(CACHE_KEY_PREFIX + memoryId, List.class);
        if (CollectionUtils.isEmpty(messages)) {
            messages = loadHistories(memoryId)
                    .stream()
                    .map(MysqlChatMemoryStoreImpl::historyToL4jMessage)
                    .toList();
            cache().put(CACHE_KEY_PREFIX + memoryId, messages);
        }
        return messages;
    }

    @Override
    public void updateMessages(Object memoryId, List<dev.langchain4j.data.message.ChatMessage> messages) {
        if (StringUtils.isBlank((String) memoryId) || CollectionUtils.isEmpty(messages)) {
            return;
        }

        var firstMessage = messages.getFirst();
        var lastMessage = messages.getLast();
        var cachedMessages = new LinkedList<>(getMessages(memoryId));

        if (firstMessage instanceof dev.langchain4j.data.message.SystemMessage l4jSystemMessage &&
                Objects.nonNull(firstMessage.text())) {
            boolean succeeded = updateSystemMessageIfNecessary(memoryId, cachedMessages, l4jSystemMessage);
            if (lastMessage == firstMessage && succeeded) {
                lastMessage = null;
            }
        }

        if (Objects.nonNull(lastMessage)) {
            int rows = chatHistoryMapper.insertSelective(l4jMessageToHistory(memoryId, lastMessage));
            if (rows > 0) {
                cachedMessages.addLast(lastMessage);
            }
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
                .map(MysqlChatMemoryStoreImpl::historyToChatMessage)
                .toList();
    }

    private boolean updateSystemMessageIfNecessary(
            Object memoryId,
            LinkedList<dev.langchain4j.data.message.ChatMessage> cachedMessages,
            dev.langchain4j.data.message.SystemMessage l4jSystemMessage) {

        if (CollectionUtils.isNotEmpty(cachedMessages)) {
            var existedSystemMessage = cachedMessages.getFirst();
            if (Objects.nonNull(existedSystemMessage) && !l4jSystemMessage.equals(existedSystemMessage)) {
                try {
                    ChatMessage systemMessage = PromptUtils.convertL4jMessage(l4jSystemMessage);
                    String messageStr = InfoUtils.defaultMapper().writeValueAsString(systemMessage);
                    ChatHistory systemHistory = loadSystemHistory(memoryId);
                    if (Objects.nonNull(systemHistory)) {
                        int rows = chatHistoryMapper.updateByPrimaryKeySelective(systemHistory
                                .withMessage(messageStr)
                                .withGmtModified(systemMessage.getGmtCreate()));
                        if (rows > 0) {
                            cachedMessages.removeFirst();
                            cachedMessages.addFirst(PromptUtils.convertChatMessage(systemMessage));
                        }
                        return true;
                    }
                } catch (JsonProcessingException e) {
                    log.warn("Message deserialize error: {}", l4jSystemMessage, e);
                }

            }
        }
        return false;
    }

    private List<ChatHistory> loadHistories(Object memoryId) {
        List<ChatHistory> histories =
                chatHistoryMapper.select(c ->
                        c.where(ChatHistoryDynamicSqlSupport.memoryId, isEqualTo((String) memoryId))
                                .and(ChatHistoryDynamicSqlSupport.enabled, isEqualTo((byte) 1))
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

    private static ChatMessage historyToChatMessage(ChatHistory history) {
        return Optional.ofNullable(history)
                .map(ChatHistory::getMessage)
                .map(message -> {
                    try {
                        return InfoUtils.defaultMapper().readValue(message, ChatMessage.class);
                    } catch (JsonProcessingException e) {
                        log.warn("Message deserialize error: {}", message, e);
                        return null;
                    }
                })
                .orElse(null);
    }

    private static dev.langchain4j.data.message.ChatMessage historyToL4jMessage(ChatHistory history) {
        return Optional.ofNullable(history)
                .map(ChatHistory::getMessage)
                .map(message -> {
                    try {
                        return InfoUtils.defaultMapper().readValue(message, ChatMessage.class);
                    } catch (JsonProcessingException e) {
                        log.warn("Message deserialize error: {}", message, e);
                        return null;
                    }
                })
                .map(PromptUtils::convertChatMessage)
                .orElse(null);
    }

    private static ChatHistory l4jMessageToHistory(
            Object memoryId, dev.langchain4j.data.message.ChatMessage l4jMessage) {
        return Optional.ofNullable(l4jMessage)
                .map(PromptUtils::convertL4jMessage)
                .map(message -> {
                    String messageStr;
                    try {
                        messageStr = InfoUtils.defaultMapper().writeValueAsString(message);
                    } catch (JsonProcessingException e) {
                        log.warn("Message serialize error: {}", message, e);
                        return null;
                    }
                    return new ChatHistory()
                            .withMemoryId((String) memoryId)
                            .withGmtCreate(message.getGmtCreate())
                            .withGmtModified(message.getGmtCreate())
                            .withMessage(messageStr)
                            .withEnabled((byte) 1);
                })
                .orElse(null);
    }

    private Cache cache() {
        if (Objects.isNull(cache) && Objects.nonNull(cacheManager)) {
            cache = cacheManager.getCache(LONG_PERIOD_CACHE_NAME);
        }
        return cache;
    }
}
