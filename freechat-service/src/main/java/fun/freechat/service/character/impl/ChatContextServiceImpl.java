package fun.freechat.service.character.impl;

import fun.freechat.mapper.ChatContextDynamicSqlSupport;
import fun.freechat.mapper.ChatContextMapper;
import fun.freechat.mapper.ChatHistoryDynamicSqlSupport;
import fun.freechat.model.ChatContext;
import fun.freechat.service.cache.LongPeriodCache;
import fun.freechat.service.cache.LongPeriodCacheEvict;
import fun.freechat.service.character.ChatContextService;
import fun.freechat.util.IdUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;
import static org.mybatis.dynamic.sql.SqlBuilder.select;

@Service
@Slf4j
@SuppressWarnings("unused")
public class ChatContextServiceImpl implements ChatContextService {
    private final static String CACHE_KEY_PREFIX = "ChatContextService_";

    private final static String CACHE_KEY_SPEL_PREFIX = "'" + CACHE_KEY_PREFIX + "' + ";

    private final static String SESSION_CACHE_KEY_PREFIX = "ChatSessionService_";

    @Autowired
    private ChatContextMapper chatContextMapper;

    private List<String> getCacheKeysByBackendId(String backendId) {
        List<ChatContext> rows = chatContextMapper.select(c ->
                c.where(ChatContextDynamicSqlSupport.backendId, isEqualTo(backendId)));
        if (CollectionUtils.isEmpty(rows)) {
            return null;
        }
        List<String> keys = new ArrayList<>(rows.size() * 3);
        for (ChatContext row : rows) {
            keys.add(CACHE_KEY_PREFIX + row.getChatId());
            keys.add(CACHE_KEY_PREFIX + row.getUserId() + "_" + row.getBackendId());
            keys.add(SESSION_CACHE_KEY_PREFIX + row.getChatId());
        }
        return keys;
    }

    @Override
    public String create(@NotNull ChatContext context) {
        if (StringUtils.isBlank(context.getUserId()) || StringUtils.isBlank(context.getBackendId())) {
            return null;
        }
        Date now = new Date();
        String id = IdUtils.newId();
        int rows = chatContextMapper.insertSelective(context
                .withGmtCreate(now)
                .withGmtModified(now)
                .withChatId(id));
        return rows > 0 ? id : null;
    }

    @Override
    @LongPeriodCacheEvict(keyBy = CACHE_KEY_SPEL_PREFIX + "#p0.chatId")
    public boolean update(ChatContext context) {
        return chatContextMapper.updateByPrimaryKeySelective(context.withGmtModified(new Date())) > 0;
    }

    @Override
    @LongPeriodCacheEvict(keyBy = CACHE_KEY_SPEL_PREFIX + "#p0")
    public boolean delete(String chatId) {
        return chatContextMapper.deleteByPrimaryKey(chatId) > 0;
    }

    @Override
    @LongPeriodCache(keyBy = CACHE_KEY_SPEL_PREFIX + "#p0")
    public ChatContext get(String chatId) {
        return chatContextMapper.selectByPrimaryKey(chatId).orElse(null);
    }

    @Override
    @LongPeriodCache(keyBy = CACHE_KEY_SPEL_PREFIX + "#p0 + '_' + #p1")
    public String getIdByBackend(String userId, String backendId) {
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
    public List<String> listIdsByUser(String userId) {
        var statement = select(ChatContextDynamicSqlSupport.chatId)
                .from(ChatContextDynamicSqlSupport.chatContext)
                .where(ChatContextDynamicSqlSupport.userId, isEqualTo(userId))
                .build()
                .render(RenderingStrategies.MYBATIS3);

        return chatContextMapper.selectMany(statement).stream().map(ChatContext::getChatId).toList();
    }

    @Override
    @LongPeriodCache
    public String getOwner(String chatId) {
        var statement = select(ChatContextDynamicSqlSupport.userId)
                .from(ChatContextDynamicSqlSupport.chatContext)
                .where(ChatContextDynamicSqlSupport.chatId, isEqualTo(chatId))
                .build()
                .render(RenderingStrategies.MYBATIS3);

        return chatContextMapper.selectOne(statement).map(ChatContext::getUserId).orElse(null);
    }
}
