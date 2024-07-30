package fun.freechat.service.ai;

import fun.freechat.mapper.AiApiKeyDynamicSqlSupport;
import fun.freechat.mapper.AiApiKeyMapper;
import fun.freechat.model.AiApiKey;
import fun.freechat.service.common.EncryptionService;
import org.apache.commons.lang3.StringUtils;

import java.io.Closeable;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

public class CloseableAiApiKey implements Closeable {
    private final AiApiKeyMapper aiApiKeyMapper;
    private final Long id;
    private final String token;
    private boolean used;

    public CloseableAiApiKey(EncryptionService encryptionService,
                             AiApiKeyMapper aiApiKeyMapper,
                             Long id) {
        if (aiApiKeyMapper == null || id == null) {
            throw new IllegalArgumentException("aiApiKeyMapper and id must be defined!");
        }
        this.aiApiKeyMapper = aiApiKeyMapper;
        this.id = id;
        this.token = aiApiKeyMapper.selectByPrimaryKey(id)
                .filter(apiKey -> apiKey.getEnabled() == (byte)1)
                .map(AiApiKey::getToken)
                .map(encryptionService::decrypt)
                .orElse("");
        this.used = false;
    }

    public CloseableAiApiKey(EncryptionService encryptionService,
                             AiApiKeyMapper aiApiKeyMapper,
                             String userId,
                             String name) {
        if (aiApiKeyMapper == null || StringUtils.isBlank(userId) || StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("aiApiKeyMapper, userId and name must be defined!");
        }
        AiApiKey aiApiKey = aiApiKeyMapper.select(c ->
                        c.where(AiApiKeyDynamicSqlSupport.userId, isEqualTo(userId))
                                .and(AiApiKeyDynamicSqlSupport.name, isEqualTo(name)))
                .stream()
                .filter(apiKey -> apiKey.getEnabled() == (byte)1)
                .findAny()
                .orElse(null);
        this.aiApiKeyMapper = aiApiKeyMapper;
        this.id = aiApiKey != null ? aiApiKey.getId() : Long.MAX_VALUE;
        this.token = Optional.ofNullable(aiApiKey)
                .map(AiApiKey::getToken)
                .map(encryptionService::decrypt)
                .orElse("");
        this.used = false;
    }

    public CloseableAiApiKey(String token) {
        this.aiApiKeyMapper = null;
        this.id = null;
        this.token = token;
        this.used = false;
    }

    @Override
    public void close() throws IOException {
        if (StringUtils.isBlank(token) || id == null || !used) {
            return;
        }
        AiApiKey aiApiKey = new AiApiKey()
                .withId(id)
                .withGmtUsed(new Date());
        aiApiKeyMapper.updateByPrimaryKeySelective(
                aiApiKey.withGmtUsed(new Date()));
    }

    public String token() {
        used = true;
        return token;
    }
}
