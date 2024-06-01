package fun.freechat.service.ai.impl;

import fun.freechat.mapper.AiApiKeyDynamicSqlSupport;
import fun.freechat.mapper.AiModelInfoDynamicSqlSupport;
import fun.freechat.mapper.AiModelInfoMapper;
import fun.freechat.model.AiModelInfo;
import fun.freechat.service.ai.AiModelInfoService;
import fun.freechat.service.cache.LongPeriodCache;
import fun.freechat.service.cache.LongPeriodCacheEvict;
import fun.freechat.service.enums.ModelProvider;
import fun.freechat.service.enums.ModelType;
import fun.freechat.util.IdUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

@Service
@SuppressWarnings("unused")
public class AiModelInfoServiceImpl implements AiModelInfoService {
    private static final String CACHE_KEY = "'AiModelInfoServiceImpl_' + #p0";
    private static final Pattern MODEL_ID_PATTERN = Pattern.compile("\\[(.+?)\\](.+?)(\\|([^|]*))?");


    @Autowired
    private AiModelInfoMapper aiModelInfoMapper;

    @Override
    @LongPeriodCacheEvict(keyBy = CACHE_KEY)
    public String createOrUpdate(String name, String description,
                                 ModelProvider provider, ModelType type) {
        Date now = new Date();
        if (StringUtils.isBlank(name)) {
            name = IdUtils.newId();
        }
        String modelId = "[" + provider.text() + "]" + name;
        AiModelInfo modelInfo = new AiModelInfo()
                .withModelId(modelId)
                .withGmtCreate(now)
                .withGmtModified(now)
                .withName(name)
                .withDescription(description)
                .withProvider(provider.text());

        AiModelInfo oldModelInfo = aiModelInfoMapper.selectOne(c ->
                    c.where(AiModelInfoDynamicSqlSupport.modelId, isEqualTo(modelId)))
            .orElse(null);

        int rows;
        if (Objects.nonNull(oldModelInfo)) {
            modelInfo.setGmtCreate(oldModelInfo.getGmtCreate());
            rows = aiModelInfoMapper.updateByPrimaryKeySelective(modelInfo);
        } else {
            rows = aiModelInfoMapper.insertSelective(modelInfo);
        }

        return rows > 0 ? modelInfo.getModelId() : null;
    }

    @Override
    @LongPeriodCacheEvict(keyBy = CACHE_KEY)
    public boolean delete(String modelId) {
        int rows = aiModelInfoMapper.delete(c -> c.where(AiModelInfoDynamicSqlSupport.modelId, isEqualTo(modelId)));
        return rows > 0;
    }

    @Override
    @LongPeriodCache(keyBy = CACHE_KEY)
    public AiModelInfo get(String modelId) {
        return aiModelInfoMapper.selectOne(c -> c.where(AiModelInfoDynamicSqlSupport.modelId, isEqualTo(modelId)))
                .orElseGet(() -> parse(modelId));
    }

    @Override
    public List<AiModelInfo> list(long limit, long offset) {
        return aiModelInfoMapper.select(c -> {
            c.orderBy(AiApiKeyDynamicSqlSupport.name);
            if (limit > 0) {
                c.limit(limit);
            }
            if (offset > 0) {
                c.offset(offset);
            }
            return c;
        });
    }

    private static AiModelInfo parse(String modelId) {
        Matcher m = MODEL_ID_PATTERN.matcher(modelId);
        if (m.matches()) {
            AiModelInfo modelInfo =  new AiModelInfo();
            modelInfo.setModelId(modelId);
            modelInfo.setDescription("Unknown model.");
            modelInfo.setProvider(m.group(1));
            modelInfo.setName(m.group(2));
            modelInfo.setType(StringUtils.isBlank(m.group(4)) ? "text2chat" : m.group(4));
            return modelInfo;
        } else {
           return null;
        }
    }
}
