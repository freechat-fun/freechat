package fun.freechat.service.account.impl;

import fun.freechat.mapper.ApiTokenDynamicSqlSupport;
import fun.freechat.mapper.ApiTokenMapper;
import fun.freechat.mapper.UserDynamicSqlSupport;
import fun.freechat.mapper.UserMapper;
import fun.freechat.model.ApiToken;
import fun.freechat.model.User;
import fun.freechat.service.cache.LongPeriodCache;
import fun.freechat.service.cache.LongPeriodCacheEvict;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Component;

import static fun.freechat.service.util.CacheUtils.LONG_PERIOD_CACHE_NAME;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

@Component
@SuppressWarnings("unused")
public class SysApiTokenRepo {
    final static String TOKEN_CACHE_KEY = "'SysApiTokenRepo_token_' + #p0";
    final static String USER_CACHE_KEY = "'SysApiTokenRepo_user_' + #p0";
    final static String TOKEN_BY_ID_CACHE_KEY = "'SysApiTokenRepo_token_byId_' + #p0";
    @Autowired
    private ApiTokenMapper apiTokenMapper;

    @Autowired
    private UserMapper userMapper;

    @LongPeriodCache(keyBy = TOKEN_CACHE_KEY)
    public ApiToken getApiToken(String token) {
        return apiTokenMapper.selectOne(c -> c.where(ApiTokenDynamicSqlSupport.token, isEqualTo(token)))
                .orElse(null);
    }

    @LongPeriodCache(keyBy = USER_CACHE_KEY)
    public User getUser(String token) {
        String userId = apiTokenMapper.selectOne(c ->
                        c.where(ApiTokenDynamicSqlSupport.token, isEqualTo(token)))
                .map(ApiToken::getUserId)
                .orElse(null);
        if (StringUtils.isBlank(userId)) {
            return null;
        }
        return userMapper.selectOne(c -> c.where(UserDynamicSqlSupport.userId, isEqualTo(userId)))
                .orElse(null);
    }

    @LongPeriodCache(keyBy = TOKEN_BY_ID_CACHE_KEY)
    public ApiToken getApiTokenById(Long id) {
        return apiTokenMapper.selectByPrimaryKey(id)
                .orElse(null);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = LONG_PERIOD_CACHE_NAME, key = TOKEN_CACHE_KEY),
            @CacheEvict(cacheNames = LONG_PERIOD_CACHE_NAME, key = USER_CACHE_KEY)
    })
    public void onDelete(String token) {}

    @Caching(evict = {
            @CacheEvict(cacheNames = LONG_PERIOD_CACHE_NAME, key = TOKEN_CACHE_KEY),
            @CacheEvict(cacheNames = LONG_PERIOD_CACHE_NAME, key = USER_CACHE_KEY)
    })
    public void onDisable(String token) {}

    @LongPeriodCacheEvict(keyBy = TOKEN_BY_ID_CACHE_KEY)
    public void onDeleteById(Long id) {}

    @LongPeriodCacheEvict(keyBy = TOKEN_BY_ID_CACHE_KEY)
    public void onDisableById(Long id) {}
}
