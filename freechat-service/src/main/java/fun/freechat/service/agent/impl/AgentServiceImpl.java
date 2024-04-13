package fun.freechat.service.agent.impl;

import fun.freechat.mapper.*;
import fun.freechat.model.*;
import fun.freechat.service.agent.AgentService;
import fun.freechat.service.cache.LongPeriodCache;
import fun.freechat.service.cache.LongPeriodCacheEvict;
import fun.freechat.service.enums.AgentFormat;
import fun.freechat.service.enums.InfoType;
import fun.freechat.service.enums.StatsType;
import fun.freechat.service.enums.Visibility;
import fun.freechat.service.organization.OrgService;
import fun.freechat.service.util.CacheUtils;
import fun.freechat.service.util.InfoUtils;
import fun.freechat.service.util.SortSpecificationWrapper;
import fun.freechat.util.IdUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.dynamic.sql.BasicColumn;
import org.mybatis.dynamic.sql.SortSpecification;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.select.QueryExpressionDSL;
import org.mybatis.dynamic.sql.select.SelectModel;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.mybatis.dynamic.sql.SqlBuilder.*;

@Service
@Slf4j
@SuppressWarnings({"unused", "rawtypes"})
public class AgentServiceImpl implements AgentService {
    private final static String CACHE_KEY_PREFIX = "agentservice_";
    private final static String CACHE_KEY_SPEL_PREFIX = "'" + CACHE_KEY_PREFIX + "' + ";

    @Autowired
    private SqlSessionFactory sqlSessionFactory;
    @Autowired
    private AgentInfoMapper agentInfoMapper;
    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private AiModelMapper aiModelMapper;
    @Autowired
    private OrgService orgService;
    @Autowired
    private InteractiveStatsMapper interactiveStatsMapper;

    private QueryExpressionDSL<SelectModel>.QueryExpressionWhereBuilder wrapQueryExpression(
            QueryExpressionDSL<SelectModel> c, String currentUserId) {
        return c.where(Info.visibility,
                isIn(Visibility.PUBLIC.text(), Visibility.PUBLIC_ORG.text()),
                or(Info.visibility, isEqualTo(Visibility.PRIVATE.text()),
                        and(Info.userId, isEqualTo(currentUserId))));
    }

    private boolean filterVisibility(AgentInfo info, User user) {
        Visibility visibility = Visibility.of(info.getVisibility());
        return switch (visibility) {
            case PUBLIC -> true;
            case PUBLIC_ORG -> orgService.hasRelationship(user.getUserId(), info.getUserId());
            case PRIVATE, HIDDEN -> user.getUserId().equals(info.getUserId());
        };
    }

    private boolean filterTags(Triple<AgentInfo, List<String>, List<String>> triple, Query query) {
        List<String> matchTags = query.getWhere().getTags();
        Boolean and = query.getWhere().getTagsAnd();
        if (CollectionUtils.isNotEmpty(matchTags)) {
            if (Objects.nonNull(and) && and) {
                //noinspection SlowListContainsAll
                return triple.getMiddle().containsAll(matchTags);
            } else {
                for (String matchTag : matchTags) {
                    if (triple.getMiddle().contains(matchTag)) {
                        return true;
                    }
                }
                return false;
            }
        }
        return true;
    }

    private boolean filterAiModels(Triple<AgentInfo, List<String>, List<String>> triple, Query query) {
        List<String> matchAiModels = query.getWhere().getAiModels();
        Boolean and = query.getWhere().getAiModelsAnd();
        if (CollectionUtils.isNotEmpty(matchAiModels)) {
            if (Objects.nonNull(and) && and) {
                //noinspection SlowListContainsAll
                return triple.getRight().containsAll(matchAiModels);
            } else {
                for (String aiModel : matchAiModels) {
                    if (triple.getRight().contains(aiModel)) {
                        return true;
                    }
                }
                return false;
            }
        }
        return true;
    }

    private void handleDraft(AgentInfo info, boolean hasDraft) {
        if (!hasDraft) {
            info.setDraft(null);
        }
    }

    private Triple<AgentInfo, List<String>, List<String>> toInfoTriple(AgentInfo info) {
        if (Objects.isNull(info)) {
            return null;
        }
        List<String> tags = tagMapper.select(c ->
                        c.where(TagDynamicSqlSupport.referType, isEqualTo(InfoType.AGENT.text()))
                                .and(TagDynamicSqlSupport.referId, isEqualTo(info.getAgentUid())))
                .stream()
                .map(Tag::getContent)
                .toList();
        List<String> aiModels = aiModelMapper.select(c ->
                        c.where(AiModelDynamicSqlSupport.referType, isEqualTo(InfoType.AGENT.text()))
                                .and(AiModelDynamicSqlSupport.referId, isEqualTo(info.getAgentUid())))
                .stream()
                .map(AiModel::getModelId)
                .toList();
        return Triple.of(info, tags, aiModels);
    }

    private static SqlColumn nameToColumn(String fieldName) {
        return switch (fieldName) {
            case "version" -> Info.version;
            case "modifyTime" -> Info.gmtModified;
            case "createTime" -> Info.gmtCreate;
            case "viewCount" -> InteractiveStatsDynamicSqlSupport.viewCount;
            case "referCount" -> InteractiveStatsDynamicSqlSupport.referCount;
            case "recommendCount" -> InteractiveStatsDynamicSqlSupport.recommendCount;
            case "score" -> InteractiveStatsDynamicSqlSupport.score;
            default -> null;
        };
    }

    private void doCreate(Triple<AgentInfo, List<String>, List<String>> infoTriple) {
        AgentInfo info = infoTriple.getLeft();
        Date now = new Date();
        int rows = agentInfoMapper.insertSelective(info
                .withGmtCreate(now)
                .withGmtModified(now));
        if (rows <= 0) {
            throw new RuntimeException("Insert agent " + info.getName() + " failed!");
        }
        if (CollectionUtils.isNotEmpty(infoTriple.getMiddle())) {
            Set<String> tagSet = new HashSet<>(infoTriple.getMiddle());
            for (String tagText : tagSet) {
                if (StringUtils.isBlank(tagText)) {
                    continue;
                }
                rows = tagMapper.insert(new Tag()
                        .withGmtCreate(now)
                        .withGmtModified(now)
                        .withContent(tagText)
                        .withUserId(info.getUserId())
                        .withReferType(InfoType.AGENT.text())
                        .withReferId(info.getAgentUid()));
                if (rows <= 0) {
                    throw new RuntimeException("Insert tag " + tagText + " failed!");
                }
            }
        }
        if (CollectionUtils.isNotEmpty(infoTriple.getRight())) {
            Set<String> aiModelSet = new HashSet<>(infoTriple.getRight());
            for (String aiModelId : aiModelSet) {
                if (StringUtils.isBlank(aiModelId)) {
                    continue;
                }
                rows = aiModelMapper.insert(new AiModel()
                        .withGmtCreate(now)
                        .withGmtModified(now)
                        .withModelId(aiModelId)
                        .withReferType(InfoType.AGENT.text())
                        .withReferId(info.getAgentUid()));
                if (rows <= 0) {
                    throw new RuntimeException("Insert aiModel " + aiModelId + " failed!");
                }
            }
        }
    }

    private Pair<SelectStatementProvider, Boolean> getSelectStatement(
            Query query, User user, QueryExpressionDSL.FromGatherer<SelectModel> fields) {
        // join
        var table = fields.from(Info.table, "a");
        List<String> tags = InfoUtils.trimListElements(query.getWhere().getTags());
        if (CollectionUtils.isNotEmpty(tags)) {
            table.leftJoin(TagDynamicSqlSupport.tag, "t")
                    .on(Info.agentUid, equalTo(TagDynamicSqlSupport.referId));

        }
        List<String> modelIds = InfoUtils.trimListElements(query.getWhere().getAiModels());
        if (CollectionUtils.isNotEmpty(modelIds)) {
            table.leftJoin(AiModelDynamicSqlSupport.aiModel, "m")
                    .on(Info.agentUid, equalTo(AiModelDynamicSqlSupport.referId));
        }
        List<String> orderByStats =  new LinkedList<>(InfoUtils.trimListElements(query.getOrderBy()));
        orderByStats.retainAll(StatsType.fieldNames());
        if (!orderByStats.isEmpty()) {
            table.leftJoin(InteractiveStatsDynamicSqlSupport.interactiveStats, "i")
                    .on(Info.agentUid, equalTo((InteractiveStatsDynamicSqlSupport.referId)));
        }
        // conditions
        var conditions = table.where();
        // visibility
        String visibilityStr = query.getWhere().getVisibility();
        String userIdStr = query.getWhere().getUserId();
        boolean hasDraft;
        if (StringUtils.isBlank(visibilityStr)) {
            if (StringUtils.isBlank(userIdStr) || userIdStr.equals(user.getUserId())) {
                conditions.and(Info.visibility, isNotEqualTo(Visibility.HIDDEN.text()))
                        .and(Info.userId, isEqualTo(user.getUserId()));
                hasDraft = true;
            } else {
                conditions.and(Info.visibility,
                                isIn(Visibility.PUBLIC.text(), Visibility.PUBLIC_ORG.text()))
                        .and(Info.userId, isEqualTo(userIdStr));
                hasDraft = false;
            }
        } else {
            Visibility visibility = Visibility.of(query.getWhere().getVisibility());
            conditions.and(Info.visibility, isEqualTo(visibility.text()));
            if (visibility == Visibility.PUBLIC || visibility == Visibility.PUBLIC_ORG) {
                if (StringUtils.isNotBlank(userIdStr)) {
                    conditions.and(Info.userId, isEqualTo(userIdStr));
                }
                hasDraft = false;
            } else {
                conditions.and(Info.userId, isEqualTo(user.getUserId()));
                hasDraft = true;
            }
        }
        // format
        AgentFormat format = AgentFormat.of(query.getWhere().getFormat());
        if (format != AgentFormat.UNKNOWN) {
            conditions.and(Info.format, isEqualTo(format.text()));
        }
        // name
        conditions.and(Info.name,
                isLike(query.getWhere().getName()).filter(StringUtils::isNotBlank).map(s -> s + "%"));
        // version
        conditions.and(Info.version, isGreaterThan(0));
        // text
        String commonText = query.getWhere().getText();
        if (StringUtils.isNotBlank(commonText)) {
            var commonTextCondition = isLike(commonText).map(s -> "%" + s + "%");
            conditions.and(Info.name, commonTextCondition,
                    or(Info.description, commonTextCondition),
                    or(Info.example, commonTextCondition));
        }
        // tags
        if (CollectionUtils.isNotEmpty(tags)) {
            conditions.and(TagDynamicSqlSupport.content, isIn(tags));
        }
        // models
        if (CollectionUtils.isNotEmpty(modelIds)) {
            conditions.and(AiModelDynamicSqlSupport.modelId, isIn(modelIds));
        }

        // order by
        LinkedList<SortSpecification> orderByFields = new LinkedList<>();
        for (String orderBy : InfoUtils.trimListElements(query.getOrderBy())) {
            String[] orderByInfo = orderBy.split(" ");
            SortSpecification orderByField = nameToColumn(orderByInfo[0]);
            if (Objects.isNull(orderByField)) {
                continue;
            }
            if (orderByInfo.length < 2 || !"asc".equalsIgnoreCase(orderByInfo[1])) {
                orderByField = orderByField.descending();
            }
            orderByFields.add(SortSpecificationWrapper.of(
                    orderByStats.contains(orderBy) ? "i" : "a", orderByField));
        }
        if (CollectionUtils.isNotEmpty(orderByFields)) {
            conditions.orderBy(orderByFields);
        }

        // limits
        Optional.ofNullable(query.getLimit())
                .filter(limit -> limit > 0)
                .ifPresent(conditions::limit);
        Optional.ofNullable(query.getOffset())
                .filter(offset -> offset > 0)
                .ifPresent(conditions::offset);

        return Pair.of(conditions.build().render(RenderingStrategies.MYBATIS3), hasDraft);
    }

    private List<Triple<AgentInfo, List<String>, List<String>>> doSearch(
            Query query, User user, QueryExpressionDSL.FromGatherer<SelectModel> fields) {
        var statement = getSelectStatement(query, user, fields);
        return agentInfoMapper.selectMany(statement.getLeft())
                .stream()
                .filter(info -> filterVisibility(info, user))
                .peek(info -> handleDraft(info, statement.getRight()))
                .map(this::toInfoTriple)
                .filter(triple -> filterTags(triple, query))
                .filter(triple -> filterAiModels(triple, query))
                .toList();
    }

    @Override
    public List<Triple<AgentInfo, List<String>, List<String>>> search(Query query, User user) {
        /*
select distinct a.user_id, a.agent_id, a.visibility... \
  from agent_info f \
  left join tag t on a.agent_id = t.refer_id \
  left join ai_model m on a.agent_id = m.refer_id \
  where t.refer_type = 'agent' and m.refer_type= 'agent' \
  and ((a.visibility = 'public' and a.user_id = '{userId}') or a.user_id = '{me}') \
  and a.format = '{format}' \
  and t.content in '{tags}' \
  and m.model_id in '{modelIds}' \
  and a.name like '{name}%' \
  and a.version > 0 \
  and (a.name like '%{text}%' or \
    a.description like '%{text}%' or \
    a.example like '%{text}%' \
  ) \
  order by a.{orderBy[0]}, a.{orderBy[1]}... \
  limit {limit} offset {offset} \
;
         */

        // fields
        List<BasicColumn> columns = new LinkedList<>(Info.summaryColumns());
        if (CollectionUtils.isNotEmpty(query.getOrderBy())) {
            for (String orderBy : query.getOrderBy()) {
                if (StatsType.fieldNames().contains(orderBy)) {
                    columns.add(nameToColumn(orderBy));
                }
            }
        }
        var fields = selectDistinct(columns);
        return doSearch(query, user, fields);
    }

    @Override
    public List<Triple<AgentInfo, List<String>, List<String>>> searchDetails(Query query, User user) {
        // fields
        var fields = selectDistinct(Info.table.allColumns());
        return doSearch(query, user, fields);
    }

    @Override
    public long count(Query query, User user) {
        query.setLimit(null);
        query.setOffset(null);
        var fields = select(countDistinct(Info.agentId));
        var statement = getSelectStatement(query, user, fields);
        return agentInfoMapper.count(statement.getLeft());
    }
    
    @Override
    public boolean create(Triple<AgentInfo, List<String>, List<String>> infoTriple) {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            infoTriple.getLeft().setAgentUid(IdUtils.newId());
            doCreate(infoTriple);
            session.commit();
            return true;
        } catch (Exception e) {
            log.error("Failed to create agent for {}", infoTriple.getLeft().getName(), e);
            session.rollback();
        } finally {
            session.close();
        }
        return false;
    }

    @Override
    public List<Long> create(List<Triple<AgentInfo, List<String>, List<String>>> AgentInfoList) {
        SqlSession session = sqlSessionFactory.openSession();
        LinkedList<Long> agentIds = new LinkedList<>();
        try {
            for (Triple<AgentInfo, List<String>, List<String>> infoTriple : AgentInfoList) {
                infoTriple.getLeft().setAgentUid(IdUtils.newId());
                doCreate(infoTriple);
                agentIds.add(infoTriple.getLeft().getAgentId());
            }
            session.commit();
        } catch (Exception e) {
            log.error("Failed to create agents", e);
            session.rollback();
            agentIds.clear();
        } finally {
            session.close();
        }
        return agentIds;
    }

    @Override
    @LongPeriodCacheEvict(keyBy = CACHE_KEY_SPEL_PREFIX + "#p0.getLeft().agentId")
    public boolean update(Triple<AgentInfo, List<String>, List<String>> infoTriple) {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            Date now = new Date();
            AgentInfo agentInfo = infoTriple.getLeft();
            agentInfoMapper.updateByPrimaryKeySelective(agentInfo.withGmtModified(now));
            int rows;
            if (CollectionUtils.isNotEmpty(infoTriple.getMiddle())) {
                tagMapper.delete(c -> c.where(TagDynamicSqlSupport.referId, isEqualTo(agentInfo.getAgentUid()))
                        .and(TagDynamicSqlSupport.referType, isEqualTo(InfoType.AGENT.text()))
                        .and(TagDynamicSqlSupport.userId, isEqualTo(agentInfo.getUserId())));
                Set<String> tagSet = new HashSet<>(infoTriple.getMiddle());
                for (String tagText : tagSet) {
                    rows = tagMapper.insert(new Tag()
                            .withGmtCreate(now)
                            .withGmtModified(now)
                            .withContent(tagText)
                            .withUserId(agentInfo.getUserId())
                            .withReferType(InfoType.AGENT.text())
                            .withReferId(agentInfo.getAgentUid()));
                    if (rows <= 0) {
                        throw new RuntimeException("Update tag " + tagText + " failed!");
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(infoTriple.getRight())) {
                aiModelMapper.delete(c -> c.where(AiModelDynamicSqlSupport.referId, isEqualTo(agentInfo.getAgentUid()))
                        .and(AiModelDynamicSqlSupport.referType, isEqualTo(InfoType.AGENT.text())));
                Set<String> aiModelSet = new HashSet<>(infoTriple.getRight());
                for (String aiModelId : aiModelSet) {
                    rows = aiModelMapper.insert(new AiModel()
                            .withGmtCreate(now)
                            .withGmtModified(now)
                            .withModelId(aiModelId)
                            .withReferType(InfoType.AGENT.text())
                            .withReferId(agentInfo.getAgentUid()));
                    if (rows <= 0) {
                        throw new RuntimeException("Update aiModel " + aiModelId + " failed!");
                    }
                }
            }
            session.commit();
            return true;
        } catch (Exception e) {
            log.error("Failed to update agent for {}", infoTriple.getLeft().getName(), e);
            session.rollback();
        } finally {
            session.close();
        }
        return false;
    }

    @Override
    @LongPeriodCacheEvict(keyBy = CACHE_KEY_SPEL_PREFIX + "#p0")
    public boolean hide(Long agentId, User user) {
        if (Objects.isNull(agentId)) {
            return false;
        }
        AgentInfo AgentInfo = agentInfoMapper.selectOne(c ->
                        c.where(Info.userId, isEqualTo(user.getUserId()))
                                .and(Info.agentId, isEqualTo(agentId)))
                .orElse(null);
        if (Objects.isNull(AgentInfo) ||
                Visibility.HIDDEN.text().equals(AgentInfo.getVisibility())) {
            return false;
        }
        Date now = new Date();
        AgentInfo.withGmtModified(new Date())
                .withVisibility(Visibility.HIDDEN.text());
        int rows = agentInfoMapper.updateByPrimaryKeySelective(AgentInfo);
        return rows > 0;
    }

    @Override
    @LongPeriodCacheEvict(keyBy = CACHE_KEY_SPEL_PREFIX + "#p0")
    public boolean delete(Long agentId, User user) {
        if (Objects.isNull(agentId)) {
            return false;
        }
        int rows = agentInfoMapper.delete(c -> c.where(Info.agentId, isEqualTo(agentId))
                .and(Info.userId, isEqualTo(user.getUserId())));
        return rows > 0;
    }

    @Override
    public List<Long> delete(List<Long> agentIds, User user) {
        LinkedList<Long> deletedIds = new LinkedList<>();
        SqlSession session = sqlSessionFactory.openSession();
        try {
            for (Long agentId : agentIds) {
                if (delete(agentId, user)) {
                    deletedIds.add(agentId);
                }
            }
            session.commit();
            CacheUtils.longPeriodCacheEvict(deletedIds.stream().map(id -> CACHE_KEY_PREFIX + id).toList());
        } catch (Exception e) {
            log.error("Failed to delete agents", e);
            deletedIds.clear();
            session.rollback();
        } finally {
            session.close();
        }
        return deletedIds;
    }

    @Override
    public Triple<AgentInfo, List<String>, List<String>> summary(Long agentId, User user) {
        var fields = select(Info.summaryColumns())
                .from(Info.table);

        var statement = wrapQueryExpression(fields, user.getUserId())
                .and(Info.agentId, isEqualTo(agentId))
                .build()
                .render(RenderingStrategies.MYBATIS3);
        return agentInfoMapper.selectOne(statement)
                .filter(info -> filterVisibility(info, user))
                .map(this::toInfoTriple)
                .orElse(null);
    }

    @Override
    public List<Triple<AgentInfo, List<String>, List<String>>> summary(Collection<Long> agentIds, User user) {
        var fields = select(Info.summaryColumns())
                .from(Info.table);

        var statement = wrapQueryExpression(fields, user.getUserId())
                .and(Info.agentId, isIn(agentIds))
                .build()
                .render(RenderingStrategies.MYBATIS3);
        return agentInfoMapper.selectMany(statement)
                .stream()
                .filter(info -> filterVisibility(info, user))
                .map(this::toInfoTriple)
                .toList();
    }

    @Override
    @LongPeriodCache(keyBy = CACHE_KEY_SPEL_PREFIX + "#p0")
    public Triple<AgentInfo, List<String>, List<String>> details(Long agentId, User user) {
        return agentInfoMapper.selectOne(c -> wrapQueryExpression(c, user.getUserId())
                        .and(Info.agentId, isEqualTo(agentId)))
                .filter(info -> filterVisibility(info, user))
                .map(this::toInfoTriple)
                .orElse(null);
    }

    @Override
    public List<Triple<AgentInfo, List<String>, List<String>>> details(Collection<Long> agentIds, User user) {
        return agentInfoMapper.select(c -> wrapQueryExpression(c, user.getUserId())
                        .and(Info.agentId, isIn(agentIds)))
                .stream()
                .filter(info -> filterVisibility(info, user))
                .map(this::toInfoTriple)
                .toList();
    }

    @Override
    public List<Triple<Long, Integer, InteractiveStats>> listVersionsByName(String name, User user) {
        var statement = select(Info.agentId, Info.version)
                .from(Info.table)
                .where(Info.name, isEqualTo(name))
                .and(Info.userId, isEqualTo(user.getUserId()))
                .orderBy(Info.version.descending())
                .build()
                .render(RenderingStrategies.MYBATIS3);

        return agentInfoMapper.selectMany(statement)
                .stream()
                .map(info -> {
                    InteractiveStats stats = interactiveStatsMapper.selectOne(c ->
                                    c.where(InteractiveStatsDynamicSqlSupport.referType, isEqualTo(InfoType.AGENT.text()))
                                            .and(InteractiveStatsDynamicSqlSupport.referId, isEqualTo(info.getAgentUid())))
                            .orElse(null);
                    return Triple.of(info.getAgentId(), info.getVersion(), stats);
                })
                .toList();
    }

    @Override
    public Long getLatestIdByName(String name, User user) {
        var statement = select(Info.agentId, Info.version)
                .from(Info.table)
                .where(Info.name, isEqualTo(name))
                .and(Info.userId, isEqualTo(user.getUserId()))
                .and(Info.visibility, isNotEqualTo(Visibility.HIDDEN.text()))
                .orderBy(Info.version.descending())
                .limit(1)
                .build()
                .render(RenderingStrategies.MYBATIS3);

        return agentInfoMapper.selectOne(statement).map(AgentInfo::getAgentId).orElse(null);
    }

    @Override
    public Long getLatestIdByUid(String agentUid, User user) {
        var statement = select(Info.agentId, Info.version)
                .from(Info.table)
                .where(Info.agentUid, isEqualTo(agentUid))
                .and(Info.userId, isEqualTo(user.getUserId()))
                .and(Info.visibility, isNotEqualTo(Visibility.HIDDEN.text()))
                .orderBy(Info.version.descending())
                .limit(1)
                .build()
                .render(RenderingStrategies.MYBATIS3);

        return agentInfoMapper.selectOne(statement).map(AgentInfo::getAgentId).orElse(null);
    }

    @Override
    @LongPeriodCacheEvict(keyBy = CACHE_KEY_SPEL_PREFIX + "#p0")
    public Long publish(Long agentId, Visibility visibility, User user) {
        Long publishedInfoId;
        SqlSession session = sqlSessionFactory.openSession();
        try {
            var infoTriple = details(agentId, user);
            if (Objects.isNull(infoTriple)) {
                return null;
            }
            AgentInfo info = infoTriple.getLeft();

            List<Triple<Long, Integer, InteractiveStats>> versionInfoList = listVersionsByName(info.getName(), user);
            Triple<Long, Integer, InteractiveStats> versionInfo =
                    CollectionUtils.isNotEmpty(versionInfoList) ? versionInfoList.getFirst() : null;
            Integer version = Objects.nonNull(versionInfo) ? versionInfo.getMiddle() : info.getVersion();

            info.setVisibility(visibility.text());
            info.setAgentId(null);
            info.setVersion(version + 1);

            if (StringUtils.isNotBlank(info.getDraft())) {
                info.setConfig(info.getDraft());
                info.setDraft(null);
            }

            doCreate(Triple.of(infoTriple.getLeft(), null, null));
            publishedInfoId = info.getAgentId();

            for (var prevVersionInfo : versionInfoList) {
                info = new AgentInfo()
                        .withAgentId(prevVersionInfo.getLeft())
                        .withVisibility(Visibility.HIDDEN.text())
                        .withGmtModified(new Date());
                agentInfoMapper.updateByPrimaryKeySelective(info);
            }
            session.commit();
        } catch (Exception e) {
            log.error("Failed to publish agent", e);
            publishedInfoId = null;
            session.rollback();
        } finally {
            session.close();
        }
        return publishedInfoId;
    }

    @Override
    @LongPeriodCache
    public String getOwner(Long agentId) {
        var statement = select(Info.userId)
                .from(Info.table)
                .where(Info.agentId, isEqualTo(agentId))
                .build()
                .render(RenderingStrategies.MYBATIS3);

        return agentInfoMapper.selectOne(statement).map(AgentInfo::getUserId).orElse(null);
    }

    @Override
    public String getOwnerByUid(String agentUid) {
        var statement = select(Info.userId)
                .from(Info.table)
                .where(Info.agentUid, isEqualTo(agentUid))
                .limit(1)
                .build()
                .render(RenderingStrategies.MYBATIS3);

        return agentInfoMapper.selectOne(statement).map(AgentInfo::getUserId).orElse(null);
    }

    @Override
    public String getUid(Long agentId) {
        var statement = select(Info.agentUid)
                .from(Info.table)
                .where(Info.agentId, isEqualTo(agentId))
                .build()
                .render(RenderingStrategies.MYBATIS3);

        return agentInfoMapper.selectOne(statement).map(AgentInfo::getAgentUid).orElse(null);
    }

    private static class Info {
        public static final AgentInfoDynamicSqlSupport.AgentInfo table = AgentInfoDynamicSqlSupport.agentInfo;
        public static final SqlColumn<Long> agentId = AgentInfoDynamicSqlSupport.agentId;
        public static final SqlColumn<String> agentUid = AgentInfoDynamicSqlSupport.agentUid;

        public static final SqlColumn<Date> gmtCreate = AgentInfoDynamicSqlSupport.gmtCreate;
        public static final SqlColumn<Date> gmtModified = AgentInfoDynamicSqlSupport.gmtModified;
        public static final SqlColumn<String> userId = AgentInfoDynamicSqlSupport.userId;
        public static final SqlColumn<String> parentUid = AgentInfoDynamicSqlSupport.parentUid;
        public static final SqlColumn<String> visibility = AgentInfoDynamicSqlSupport.visibility;
        public static final SqlColumn<String> format = AgentInfoDynamicSqlSupport.format;
        public static final SqlColumn<Integer> version = AgentInfoDynamicSqlSupport.version;
        public static final SqlColumn<String> name = AgentInfoDynamicSqlSupport.name;
        public static final SqlColumn<String> description = AgentInfoDynamicSqlSupport.description;
        public static final SqlColumn<String> config = AgentInfoDynamicSqlSupport.config;
        public static final SqlColumn<String> example = AgentInfoDynamicSqlSupport.example;
        public static final SqlColumn<String> parameters = AgentInfoDynamicSqlSupport.parameters;
        public static final SqlColumn<String> ext = AgentInfoDynamicSqlSupport.ext;

        public static List<BasicColumn> summaryColumns () {
            return List.of(
                    Info.gmtCreate,
                    Info.gmtModified,
                    Info.userId,
                    Info.agentId,
                    Info.agentUid,
                    Info.visibility,
                    Info.format,
                    Info.version,
                    Info.name,
                    Info.description);
        }

    }
}
