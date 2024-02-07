package fun.freechat.service.stats.impl;

import fun.freechat.mapper.*;
import fun.freechat.model.InteractiveStats;
import fun.freechat.model.InteractiveStatsScoreDetails;
import fun.freechat.service.enums.InfoType;
import fun.freechat.service.enums.StatsType;
import fun.freechat.service.enums.Visibility;
import fun.freechat.service.stats.InteractiveStatsService;
import fun.freechat.service.util.SortSpecificationWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.dynamic.sql.SortSpecification;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.select.QueryExpressionDSL;
import org.mybatis.dynamic.sql.select.SelectModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.mybatis.dynamic.sql.SqlBuilder.*;

@Service
@Slf4j
@SuppressWarnings("unused")
public class InteractiveStatsServiceImpl implements InteractiveStatsService {
    @Autowired
    private InteractiveStatsMapper interactiveStatsMapper;

    @Autowired
    private InteractiveStatsScoreDetailsMapper interactiveStatsScoreDetailsMapper;

    private SqlColumn<Long> getStatsColumn(StatsType statsType) {
        return  switch (statsType) {
            case VIEW_COUNT -> InteractiveStatsDynamicSqlSupport.viewCount;
            case REFER_COUNT -> InteractiveStatsDynamicSqlSupport.referCount;
            case RECOMMEND_COUNT -> InteractiveStatsDynamicSqlSupport.recommendCount;
            case SCORE -> InteractiveStatsDynamicSqlSupport.score;
            case UNKNOWN -> null;
        };
    }

    private Long getStatsValue(InteractiveStats stats, StatsType statsType) {
        return switch (statsType) {
            case VIEW_COUNT -> stats.getViewCount();
            case REFER_COUNT -> stats.getReferCount();
            case RECOMMEND_COUNT -> stats.getRecommendCount();
            case SCORE -> stats.getScore();
            default -> throw new IllegalStateException("Unexpected value: " + statsType);
        };
    }

    private void setStatsValue(InteractiveStats stats, StatsType statsType, Long v) {
        switch (statsType) {
            case VIEW_COUNT -> stats.setViewCount(v);
            case REFER_COUNT -> stats.setReferCount(v);
            case RECOMMEND_COUNT -> stats.setRecommendCount(v);
            case SCORE -> stats.setScore(v);
            default -> throw new IllegalStateException("Unexpected value: " + statsType);
        }
    }

    private long mark(String userId, InfoType infoType, String infoId, long v) {
        if (StringUtils.isBlank(userId)) {
            return -1;
        }
        Date now = new Date();
        InteractiveStatsScoreDetails scoreDetails = getScore(userId, infoType, infoId);
        if (Objects.isNull(scoreDetails)) {
            scoreDetails = new InteractiveStatsScoreDetails()
                    .withScore(v)
                    .withGmtCreate(now)
                    .withGmtModified(now)
                    .withReferType(infoType.text())
                    .withReferId(infoId)
                    .withUserId(userId);
            int rows = interactiveStatsScoreDetailsMapper.insertSelective(scoreDetails);
            return rows > 0 ? v : -1;
        }
        long oldScore = scoreDetails.getScore();
        if (oldScore == v) {
            return -1;
        }
        int rows = interactiveStatsScoreDetailsMapper.updateByPrimaryKeySelective(
                scoreDetails.withGmtModified(now).withScore(v));
        return rows > 0 ? oldScore : -1;
    }

    @Override
    public long add(String userId, InfoType infoType, String infoId, StatsType statsType, long delta) {
        SqlColumn<Long> statsColumn = getStatsColumn(statsType);
        if (Objects.isNull(statsColumn) || infoType == InfoType.UNKNOWN || StringUtils.isBlank(infoId)) {
            return -1;
        }
        Date now = new Date();
        QueryExpressionDSL.FromGatherer<SelectModel> fields;
        if (statsType == StatsType.SCORE) {
            fields = select(InteractiveStatsDynamicSqlSupport.id,
                    InteractiveStatsDynamicSqlSupport.score,
                    InteractiveStatsDynamicSqlSupport.scoreCount);
        } else {
            fields = select(InteractiveStatsDynamicSqlSupport.id, statsColumn);
        }
        var statement = fields.from(InteractiveStatsDynamicSqlSupport.interactiveStats)
                .where(InteractiveStatsDynamicSqlSupport.referType, isEqualTo(infoType.text()))
                .and(InteractiveStatsDynamicSqlSupport.referId, isEqualTo(infoId))
                .build()
                .render(RenderingStrategies.MYBATIS3);
        InteractiveStats stats = interactiveStatsMapper.selectOne(statement).orElse(null);
        boolean exists = true;
        if (Objects.isNull(stats)) {
            stats = new InteractiveStats()
                    .withGmtCreate(now)
                    .withGmtModified(now)
                    .withRecommendCount(0L)
                    .withReferCount(0L)
                    .withViewCount(0L)
                    .withScoreCount(0L)
                    .withScore(0L)
                    .withReferType(infoType.text())
                    .withReferId(infoId);
            exists = false;
        }
        Long v = getStatsValue(stats, statsType);
        if (delta == 0L) {
            return v;
        }
        Long orig = v;
        if (statsType == StatsType.SCORE) {
            long oldScore = mark(userId, infoType, infoId, delta);
            if (oldScore < 0) {
                return orig;
            } else if (oldScore == delta) {
                long count = stats.getScoreCount();
                long total = orig * count;
                count += 1;
                v = (total + delta) / count;
                stats.setScore(v);
                stats.setScoreCount(count);
            } else {
                long count = stats.getScoreCount();
                long total = orig * count;
                if (count == 0) {
                    // something wrong, reset
                    count = 1;
                    total = oldScore;
                }
                v = (total + delta - oldScore) / count;
                stats.setScore(v);
                stats.setScoreCount(count);
            }
        } else {
            v += delta;
            setStatsValue(stats, statsType, v);
        }
        int rows;
        if (exists) {
            rows = interactiveStatsMapper.updateByPrimaryKeySelective(stats.withGmtModified(now));
        } else {
            rows = interactiveStatsMapper.insertSelective(stats);
        }
        return rows > 0 ? v : orig;
    }

    @Override
    public InteractiveStats get(InfoType infoType, String infoId) {
        if (infoType == InfoType.UNKNOWN || StringUtils.isBlank(infoId)) {
            return null;
        }
        return interactiveStatsMapper.selectOne(c ->
                c.where(InteractiveStatsDynamicSqlSupport.referType, isEqualTo(infoType.text()))
                        .and(InteractiveStatsDynamicSqlSupport.referId, isEqualTo(infoId)))
                .orElse(null);
    }

    @Override
    public InteractiveStatsScoreDetails getScore(String userId, InfoType infoType, String infoId) {
        if (infoType == InfoType.UNKNOWN || StringUtils.isAnyBlank(userId, infoId)) {
            return null;
        }
        return interactiveStatsScoreDetailsMapper.selectOne(c ->
                c.where(InteractiveStatsScoreDetailsDynamicSqlSupport.referType, isEqualTo(infoType.text()))
                        .and(InteractiveStatsScoreDetailsDynamicSqlSupport.referId, isEqualTo(infoId))
                        .and(InteractiveStatsScoreDetailsDynamicSqlSupport.userId, isEqualTo(userId)))
                .orElse(null);
    }

    @Override
    public List<InteractiveStats> list(
            InfoType infoType, StatsType statsType, Long limit, Long offset, boolean desc) {
        if (infoType == InfoType.UNKNOWN || statsType == StatsType.UNKNOWN) {
            return null;
        }
        var fields = selectDistinct(InteractiveStatsDynamicSqlSupport.interactiveStats.allColumns())
                .from(InteractiveStatsDynamicSqlSupport.interactiveStats, "s");
        var table = switch (infoType) {
            case PROMPT -> fields.join(PromptInfoDynamicSqlSupport.promptInfo, "i")
                        .on(InteractiveStatsDynamicSqlSupport.referId, equalTo(PromptInfoDynamicSqlSupport.promptId));
            case AGENT -> fields.join(AgentInfoDynamicSqlSupport.agentInfo, "i")
                    .on(InteractiveStatsDynamicSqlSupport.referId, equalTo(AgentInfoDynamicSqlSupport.agentId));
            case PLUGIN -> fields.join(PluginInfoDynamicSqlSupport.pluginInfo, "i")
                    .on(InteractiveStatsDynamicSqlSupport.referId, equalTo(PluginInfoDynamicSqlSupport.pluginId));
            default -> throw new IllegalStateException("Unexpected value: " + infoType);
        };
        if (Objects.isNull(table)) {
            return Collections.emptyList();
        }
        // conditions
        var conditions = table.where();
        conditions = switch (infoType) {
            case PROMPT -> conditions.and(InteractiveStatsDynamicSqlSupport.referType, isEqualTo(InfoType.PROMPT.text()))
                    .and(PromptInfoDynamicSqlSupport.visibility, isEqualTo(Visibility.PUBLIC.text()));
            case AGENT ->  conditions.and(InteractiveStatsDynamicSqlSupport.referType, isEqualTo(InfoType.AGENT.text()))
                    .and(AgentInfoDynamicSqlSupport.visibility, isEqualTo(Visibility.PUBLIC.text()));
            case PLUGIN ->  conditions.and(InteractiveStatsDynamicSqlSupport.referType, isEqualTo(InfoType.PLUGIN.text()))
                    .and(PluginInfoDynamicSqlSupport.visibility, isEqualTo(Visibility.PUBLIC.text()));
            default -> throw new IllegalStateException("Unexpected value: " + infoType);
        };
        SortSpecification orderByField = getStatsColumn(statsType);
        if (desc) {
            orderByField = orderByField.descending();
        }
        conditions.orderBy(SortSpecificationWrapper.of("s", orderByField));
        Optional.ofNullable(limit).filter(l -> l > 0).ifPresent(conditions::limit);
        Optional.ofNullable(offset).filter(o -> o > 0).ifPresent(conditions::offset);

        var statement = conditions.build().render(RenderingStrategies.MYBATIS3);
        return interactiveStatsMapper.selectMany(statement).stream().toList();
    }
}
