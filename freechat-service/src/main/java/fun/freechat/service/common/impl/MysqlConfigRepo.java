package fun.freechat.service.common.impl;

import fun.freechat.mapper.NonRealTimeConfigDynamicSqlSupport;
import fun.freechat.mapper.NonRealTimeConfigMapper;
import fun.freechat.model.NonRealTimeConfig;
import fun.freechat.service.cache.MiddlePeriodCache;
import fun.freechat.service.cache.MiddlePeriodCacheEvict;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import static org.mybatis.dynamic.sql.SqlBuilder.*;

@Component
@SuppressWarnings("unused")
public class MysqlConfigRepo {
    final static String CACHE_KEY = "'MysqlConfigRepo_' + #p0";

    @Autowired
    private NonRealTimeConfigMapper configMapper;

    @MiddlePeriodCacheEvict(keyBy = CACHE_KEY)
    public synchronized Integer publish(String name, String format, String content, String userId) {
        Date now = new Date();
        NonRealTimeConfig config = configMapper.selectOne(c ->
                        c.where(NonRealTimeConfigDynamicSqlSupport.name, isEqualTo(name))
                                .orderBy(NonRealTimeConfigDynamicSqlSupport.version.descending())
                                .limit(1))
                .orElse(null);
        Integer version = Objects.nonNull(config) ? config.getVersion() + 1 : 1;
        int rows = configMapper.insert(new NonRealTimeConfig()
                .withGmtCreate(now)
                .withGmtModified(now)
                .withName(name)
                .withVersion(version)
                .withFormat(format)
                .withContent(content)
                .withUserId(userId));
        return rows > 0 ? version : null;
    }

    @MiddlePeriodCache(keyBy = CACHE_KEY)
    public NonRealTimeConfig load(String name) {
        return configMapper.selectOne(c ->
                        c.where(NonRealTimeConfigDynamicSqlSupport.name, isEqualTo(name))
                                .orderBy(NonRealTimeConfigDynamicSqlSupport.version.descending(),
                                        NonRealTimeConfigDynamicSqlSupport.gmtModified.descending())
                                .limit(1))
                .orElse(null);
    }

    public NonRealTimeConfig load(String name, Integer version) {
        if (Objects.isNull(version)) {
            return load(name);
        }
        return configMapper.selectOne(c ->
                        c.where(NonRealTimeConfigDynamicSqlSupport.name, isEqualTo(name))
                                .and(NonRealTimeConfigDynamicSqlSupport.version, isEqualTo(version))
                                .orderBy(NonRealTimeConfigDynamicSqlSupport.gmtModified.descending())
                                .limit(1))
                .orElse(null);
    }

    public List<String> listNames() {
        var statement = selectDistinct(NonRealTimeConfigDynamicSqlSupport.name)
                .from(NonRealTimeConfigDynamicSqlSupport.nonRealTimeConfig)
                .build()
                .render(RenderingStrategies.MYBATIS3);

        return configMapper.selectMany(statement)
                .stream()
                .map(NonRealTimeConfig::getName)
                .toList();
    }
}
