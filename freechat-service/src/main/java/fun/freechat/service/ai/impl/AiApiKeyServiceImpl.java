package fun.freechat.service.ai.impl;

import fun.freechat.mapper.AiApiKeyDynamicSqlSupport;
import fun.freechat.mapper.AiApiKeyMapper;
import fun.freechat.model.AiApiKey;
import fun.freechat.model.User;
import fun.freechat.service.ai.AiApiKeyService;
import fun.freechat.service.ai.CloseableAiApiKey;
import fun.freechat.service.ai.MaskedAiApiKey;
import fun.freechat.service.cache.LongPeriodCache;
import fun.freechat.service.common.EncryptionService;
import fun.freechat.service.enums.ModelProvider;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;
import static org.mybatis.dynamic.sql.SqlBuilder.select;

@Service
@SuppressWarnings("unused")
public class AiApiKeyServiceImpl implements AiApiKeyService {
    @Value("${auth.aiApiKey.limits:#{null}}")
    private Integer maxCount;

    @Autowired
    private AiApiKeyMapper aiApiKeyMapper;

    @Autowired
    private EncryptionService encryptionService;

    @Override
    public Long create(User user, String name, ModelProvider provider,
                       String token, boolean enabled) {
        if (StringUtils.isBlank(name) ||
                StringUtils.isBlank(token) ||
                (Objects.nonNull(maxCount) && list(user, provider).size() >= maxCount)) {
            return null;
        }
        Date now = new Date();
        AiApiKey aiApiKey = new AiApiKey()
                .withGmtCreate(now)
                .withGmtModified(now)
                .withName(name)
                .withToken(encryptionService.encrypt(token))
                .withProvider(provider.text())
                .withUserId(user.getUserId())
                .withEnabled(enabled ? (byte)1 : (byte)0);
        aiApiKeyMapper.insert(aiApiKey);
        return aiApiKey.getId();
    }

    @Override
    public boolean disable(Long id) {
        AiApiKey aiApiKey = new AiApiKey()
                .withId(id)
                .withEnabled((byte)0)
                .withGmtModified(new Date());
        int rows = aiApiKeyMapper.updateByPrimaryKeySelective(aiApiKey);
        return rows > 0;
    }

    @Override
    public boolean enable(Long id) {
        AiApiKey aiApiKey = new AiApiKey()
                .withId(id)
                .withEnabled((byte)1)
                .withGmtModified(new Date());
        int rows = aiApiKeyMapper.updateByPrimaryKeySelective(aiApiKey);
        return rows > 0;
    }

    @Override
    public boolean delete(Long id) {
        int rows = aiApiKeyMapper.deleteByPrimaryKey(id);
        return rows > 0;
    }

    @Override
    public CloseableAiApiKey use(Long id) {
        return new CloseableAiApiKey(encryptionService, aiApiKeyMapper, id);
    }

    @Override
    public CloseableAiApiKey use(String userId, String name) {
        return new CloseableAiApiKey(encryptionService, aiApiKeyMapper, userId, name);
    }

    @Override
    public CloseableAiApiKey use(String token) {
        return new CloseableAiApiKey(token);
    }

    @Override
    public MaskedAiApiKey get(Long id) {
        return aiApiKeyMapper.selectByPrimaryKey(id)
                .map(aiApiKey -> MaskedAiApiKey.of(aiApiKey, encryptionService))
                .orElse(null);
    }

    @Override
    public List<MaskedAiApiKey> list(User user, ModelProvider provider) {
        return aiApiKeyMapper.select(c ->
                        c.where(AiApiKeyDynamicSqlSupport.userId, isEqualTo(user.getUserId()))
                                .and(AiApiKeyDynamicSqlSupport.provider, isEqualTo(provider.text())))
                .stream()
                .map(aiApiKey -> MaskedAiApiKey.of(aiApiKey, encryptionService))
                .toList();
    }

    @LongPeriodCache
    @Override
    public String getOwner(Long id) {
        var statement = select(AiApiKeyDynamicSqlSupport.userId)
                .from(AiApiKeyDynamicSqlSupport.aiApiKey)
                .where(AiApiKeyDynamicSqlSupport.id, isEqualTo(id))
                .build()
                .render(RenderingStrategies.MYBATIS3);

        return aiApiKeyMapper.selectOne(statement).map(AiApiKey::getUserId).orElse(null);
    }

    @LongPeriodCache
    @Override
    public String getOwner(String userId, String name) {
        var statement = select(AiApiKeyDynamicSqlSupport.userId)
                .from(AiApiKeyDynamicSqlSupport.aiApiKey)
                .where(AiApiKeyDynamicSqlSupport.userId, isEqualTo(userId))
                .and(AiApiKeyDynamicSqlSupport.name, isEqualTo(name))
                .build()
                .render(RenderingStrategies.MYBATIS3);

        return aiApiKeyMapper.selectOne(statement).map(AiApiKey::getUserId).orElse(null);
    }
}
