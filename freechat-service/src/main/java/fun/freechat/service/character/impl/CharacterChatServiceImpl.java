package fun.freechat.service.character.impl;

import fun.freechat.mapper.ChatContextDynamicSqlSupport;
import fun.freechat.mapper.ChatContextMapper;
import fun.freechat.model.ChatContext;
import fun.freechat.service.cache.LongPeriodCache;
import fun.freechat.service.cache.LongPeriodCacheEvict;
import fun.freechat.service.character.CharacterChatService;
import fun.freechat.service.util.CacheUtils;
import fun.freechat.util.IdUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;
import static org.mybatis.dynamic.sql.SqlBuilder.select;

@Service
@Slf4j
@SuppressWarnings("unused")
public class CharacterChatServiceImpl implements CharacterChatService {
    private final static String CACHE_KEY_PREFIX = "CharacterChatService_";

    private final static String CACHE_KEY_SPEL_PREFIX = "'" + CACHE_KEY_PREFIX + "' + ";

    @Autowired
    private ChatContextMapper chatContextMapper;

    private List<String> getCacheKeysByCharacterId(String characterId) {
        List<ChatContext> rows = chatContextMapper.select(c ->
                c.where(ChatContextDynamicSqlSupport.characterId, isEqualTo(characterId)));
        List<String> keys = new ArrayList<>(rows.size() * 2);
        for (ChatContext row : rows) {
            keys.add(CACHE_KEY_PREFIX + row.getChatId());
            keys.add(CACHE_KEY_PREFIX + row.getUserId() + "_" + row.getCharacterId());
        }
        return keys;
    }

    @Override
    public String create(String userId, String characterId, String context) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(characterId)) {
            return null;
        }
        Date now = new Date();
        String id = IdUtils.newId();
        int rows = chatContextMapper.insertSelective(new ChatContext()
                .withUserId(userId)
                .withCharacterId(characterId)
                .withContext(context)
                .withGmtCreate(now)
                .withGmtModified(now)
                .withChatId(id));
        return rows > 0 ? id : null;
    }

    @Override
    @LongPeriodCacheEvict(keyBy = CACHE_KEY_SPEL_PREFIX + "#p0")
    public boolean update(String chatId, String context) {
        return chatContextMapper.updateByPrimaryKeySelective(new ChatContext()
                .withChatId(chatId)
                .withContext(context)) > 0;
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
    public String getChatId(String userId, String characterId) {
        var statement = select(ChatContextDynamicSqlSupport.chatId)
                .from(ChatContextDynamicSqlSupport.chatContext)
                .where(ChatContextDynamicSqlSupport.userId, isEqualTo(userId))
                .and(ChatContextDynamicSqlSupport.characterId, isEqualTo(characterId))
                .build()
                .render(RenderingStrategies.MYBATIS3);

        return chatContextMapper.selectOne(statement).map(ChatContext::getChatId).orElse(null);
    }

    @Override
    public boolean replaceCharacter(String oldCharacterId, String newCharacterId) {
        if (newCharacterId.equals(oldCharacterId)) {
            return false;
        }

        List<String> cacheKeys = getCacheKeysByCharacterId(oldCharacterId);
        int rows = chatContextMapper.update(c ->
                c.set(ChatContextDynamicSqlSupport.characterId).equalTo(newCharacterId)
                        .where(ChatContextDynamicSqlSupport.characterId, isEqualTo(oldCharacterId)));
        if (rows > 0) {
            CacheUtils.longPeriodCacheEvict(cacheKeys);
            return true;
        }

        return false;
    }
}
