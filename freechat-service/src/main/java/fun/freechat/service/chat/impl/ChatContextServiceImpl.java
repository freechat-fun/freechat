package fun.freechat.service.chat.impl;

import fun.freechat.mapper.ChatContextDynamicSqlSupport;
import fun.freechat.mapper.ChatContextMapper;
import fun.freechat.mapper.ChatHistoryDynamicSqlSupport;
import fun.freechat.model.ChatContext;
import fun.freechat.service.cache.LongPeriodCache;
import fun.freechat.service.cache.LongPeriodCacheEvict;
import fun.freechat.service.character.CharacterService;
import fun.freechat.service.chat.ChatContextService;
import fun.freechat.service.common.EncryptionService;
import fun.freechat.util.IdUtils;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static fun.freechat.service.util.CacheUtils.IN_PROCESS_LONG_CACHE_MANAGER;
import static fun.freechat.service.util.CacheUtils.LONG_PERIOD_CACHE_NAME;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;
import static org.mybatis.dynamic.sql.SqlBuilder.select;

@Service
@Slf4j
@SuppressWarnings("unused")
public class ChatContextServiceImpl implements ChatContextService {
    private static final String CACHE_KEY_PREFIX = "ChatContextService_";
    private static final String CACHE_KEY_SPEL_PREFIX = "'" + CACHE_KEY_PREFIX + "' + ";
    private static final String SESSION_CACHE_KEY_PREFIX = "ChatSessionService_";

    @Autowired
    private ChatContextMapper chatContextMapper;
    @Autowired
    private CharacterService characterService;
    @Autowired
    private EncryptionService encryptionService;

    @Override
    public ChatContext create(@NonNull ChatContext context) {
        if (StringUtils.isBlank(context.getUserId()) || StringUtils.isBlank(context.getBackendId())) {
            return null;
        }

        String existedId = getChatIdByBackend(context.getUserId(), context.getBackendId());
        if (StringUtils.isNotBlank(existedId)) {
            boolean success = update(context.withChatId(existedId));
            return success ? context : null;
        }

        Date now = new Date();
        String id = IdUtils.newId();
        String origApiKeyValue = context.getApiKeyValue();
        int rows = chatContextMapper.insertSelective(context
                .withApiKeyValue(encryptionService.encrypt(origApiKeyValue))
                .withGmtCreate(now)
                .withGmtModified(now)
                .withChatId(id));
        return rows > 0 ? context.withApiKeyValue(origApiKeyValue) : null;
    }

    @Override
    @LongPeriodCacheEvict(keyBy = CACHE_KEY_SPEL_PREFIX + "#p0.chatId")
    @CacheEvict(cacheNames = LONG_PERIOD_CACHE_NAME,
            cacheManager = IN_PROCESS_LONG_CACHE_MANAGER,
            key = ChatSessionServiceImpl.CACHE_KEY_SPEL_PREFIX + "#p0.chatId"
    )
    public boolean update(ChatContext context) {
        String origApiKeyValue = context.getApiKeyValue();
        int rows = chatContextMapper.updateByPrimaryKeySelective(context
                .withApiKeyValue(encryptionService.encrypt(origApiKeyValue))
                .withGmtModified(new Date()));
        context.setApiKeyValue(origApiKeyValue);
        return rows > 0;
    }

    @Override
    @LongPeriodCacheEvict(keyBy = CACHE_KEY_SPEL_PREFIX + "#p0")
    @CacheEvict(cacheNames = LONG_PERIOD_CACHE_NAME,
            cacheManager = IN_PROCESS_LONG_CACHE_MANAGER,
            key = ChatSessionServiceImpl.CACHE_KEY_SPEL_PREFIX + "#p0"
    )
    public boolean delete(String chatId) {
        return chatContextMapper.deleteByPrimaryKey(chatId) > 0;
    }

    @Override
    @LongPeriodCache(keyBy = CACHE_KEY_SPEL_PREFIX + "#p0")
    public ChatContext get(String chatId) {
        return chatContextMapper.selectByPrimaryKey(chatId)
                .map(context -> context.withApiKeyValue(encryptionService.decrypt(context.getApiKeyValue())))
                .orElse(null);
    }

    @Override
    public List<ChatContext> list(String userId) {
        return chatContextMapper.select(c -> c.where(ChatContextDynamicSqlSupport.userId, isEqualTo(userId))
                .orderBy(ChatContextDynamicSqlSupport.gmtRead.descending(),
                        ChatContextDynamicSqlSupport.gmtCreate.descending()))
                .stream()
                .peek(context -> context.setApiKeyValue(encryptionService.decrypt(context.getApiKeyValue())))
                .toList();
    }

    @Override
    public String getChatIdByBackend(String userId, String backendId) {
        var statement = select(ChatContextDynamicSqlSupport.chatId)
                .from(ChatContextDynamicSqlSupport.chatContext)
                .where(ChatContextDynamicSqlSupport.userId, isEqualTo(userId))
                .and(ChatContextDynamicSqlSupport.backendId, isEqualTo(backendId))
                .orderBy(ChatHistoryDynamicSqlSupport.gmtCreate.descending())
                .limit(1)
                .build()
                .render(RenderingStrategies.MYBATIS3);

        return chatContextMapper.selectOne(statement).map(ChatContext::getChatId).orElse(null);
    }

    @Override
    public List<String> listIds(String userId) {
        var statement = select(ChatContextDynamicSqlSupport.chatId)
                .from(ChatContextDynamicSqlSupport.chatContext)
                .where(ChatContextDynamicSqlSupport.userId, isEqualTo(userId))
                .build()
                .render(RenderingStrategies.MYBATIS3);

        return chatContextMapper.selectMany(statement).stream().map(ChatContext::getChatId).toList();
    }

    @Override
    public List<String> listIdsByBackend(String backendId) {
        var statement = select(ChatContextDynamicSqlSupport.chatId)
                .from(ChatContextDynamicSqlSupport.chatContext)
                .where(ChatContextDynamicSqlSupport.backendId, isEqualTo(backendId))
                .build()
                .render(RenderingStrategies.MYBATIS3);

        return chatContextMapper.selectMany(statement).stream().map(ChatContext::getChatId).toList();
    }

    @Override
    @LongPeriodCache
    public String getChatOwner(String chatId) {
        var statement = select(ChatContextDynamicSqlSupport.userId)
                .from(ChatContextDynamicSqlSupport.chatContext)
                .where(ChatContextDynamicSqlSupport.chatId, isEqualTo(chatId))
                .build()
                .render(RenderingStrategies.MYBATIS3);

        return chatContextMapper.selectOne(statement).map(ChatContext::getUserId).orElse(null);
    }

    @Override
    @LongPeriodCache
    public String getCharacterOwner(String chatId) {
        var statement = select(ChatContextDynamicSqlSupport.backendId)
                .from(ChatContextDynamicSqlSupport.chatContext)
                .where(ChatContextDynamicSqlSupport.chatId, isEqualTo(chatId))
                .build()
                .render(RenderingStrategies.MYBATIS3);

        String backendId = chatContextMapper.selectOne(statement).map(ChatContext::getBackendId).orElse(null);

        if (backendId == null) {
            return null;
        }

        return characterService.getBackendOwner(backendId);
    }

    @Override
    @LongPeriodCache
    public String getCharacterUid(String chatId) {
        var statement = select(ChatContextDynamicSqlSupport.backendId)
                .from(ChatContextDynamicSqlSupport.chatContext)
                .where(ChatContextDynamicSqlSupport.chatId, isEqualTo(chatId))
                .build()
                .render(RenderingStrategies.MYBATIS3);

        String backendId = chatContextMapper.selectOne(statement).map(ChatContext::getBackendId).orElse(null);

        if (backendId == null) {
            return null;
        }

        return characterService.getBackendCharacterUid(backendId);
    }
}
