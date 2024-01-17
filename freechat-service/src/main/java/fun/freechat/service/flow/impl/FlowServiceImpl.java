package fun.freechat.service.flow.impl;

import fun.freechat.mapper.*;
import fun.freechat.model.*;
import fun.freechat.service.cache.LongPeriodCache;
import fun.freechat.service.cache.LongPeriodCacheEvict;
import fun.freechat.service.enums.FlowFormat;
import fun.freechat.service.enums.InfoType;
import fun.freechat.service.enums.StatsType;
import fun.freechat.service.enums.Visibility;
import fun.freechat.service.flow.FlowService;
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
public class FlowServiceImpl implements FlowService {
    private final static String CACHE_KEY_PREFIX = "FlowService_";

    private final static String CACHE_KEY_SPEL_PREFIX = "'" + CACHE_KEY_PREFIX + "' + ";

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Autowired
    private FlowInfoMapper flowInfoMapper;

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private AiModelMapper aiModelMapper;

    @Autowired
    private OrgService orgService;

    @Autowired
    private InteractiveStatsMapper interactiveStatsMapper;

    @Autowired
    private InteractiveStatsScoreDetailsMapper interactiveStatsScoreDetailsMapper;

    private QueryExpressionDSL<SelectModel>.QueryExpressionWhereBuilder wrapQueryExpression(
            QueryExpressionDSL<SelectModel> c, String currentUserId) {
        return c.where(Info.visibility,
                isIn(Visibility.PUBLIC.text(), Visibility.PUBLIC_ORG.text()),
                or(Info.visibility, isEqualTo(Visibility.PRIVATE.text()),
                        and(Info.userId, isEqualTo(currentUserId))));
    }

    private boolean filterVisibility(FlowInfo info, User user) {
        Visibility visibility = Visibility.of(info.getVisibility());
        return switch (visibility) {
            case PUBLIC -> true;
            case PUBLIC_ORG -> orgService.hasRelationship(user.getUserId(), info.getUserId());
            case PRIVATE, HIDDEN -> user.getUserId().equals(info.getUserId());
        };
    }

    private boolean filterTags(Triple<FlowInfo, List<String>, List<String>> triple, Query query) {
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

    private boolean filterAiModels(Triple<FlowInfo, List<String>, List<String>> triple, Query query) {
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

    private void handleDraft(FlowInfo info, boolean hasDraft) {
        if (!hasDraft) {
            info.setDraft(null);
        }
    }

    private Triple<FlowInfo, List<String>, List<String>> toInfoTriple(FlowInfo info) {
        if (Objects.isNull(info)) {
            return null;
        }
        List<String> tags = tagMapper.select(c ->
                        c.where(TagDynamicSqlSupport.referType, isEqualTo(InfoType.FLOW.text()))
                                .and(TagDynamicSqlSupport.referId, isEqualTo(info.getFlowId())))
                .stream()
                .map(Tag::getContent)
                .toList();
        List<String> aiModels = aiModelMapper.select(c ->
                        c.where(AiModelDynamicSqlSupport.referType, isEqualTo(InfoType.FLOW.text()))
                                .and(AiModelDynamicSqlSupport.referId, isEqualTo(info.getFlowId())))
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

    private Pair<Integer, InteractiveStats> getLastVersionAndStatsByName(String name, User user) {
        var statement = select(Info.flowId, Info.version)
                .from(Info.table)
                .where(Info.name, isEqualTo(name))
                .and(Info.userId, isEqualTo(user.getUserId()))
                .orderBy(Info.version.descending())
                .limit(1)
                .build()
                .render(RenderingStrategies.MYBATIS3);

        return flowInfoMapper.selectOne(statement)
                .map(info -> {
                    InteractiveStats stats = interactiveStatsMapper.selectOne(c ->
                                    c.where(InteractiveStatsDynamicSqlSupport.referType, isEqualTo(InfoType.FLOW.text()))
                                            .and(InteractiveStatsDynamicSqlSupport.referId, isEqualTo(info.getFlowId())))
                            .orElse(null);
                    return Pair.of(info.getVersion(), stats);
                }).orElse(null);
    }

    private void doCreate(Triple<FlowInfo, List<String>, List<String>> infoTriple) {
        FlowInfo info = infoTriple.getLeft();
        Date now = new Date();
        int rows = flowInfoMapper.insertSelective(info
                .withGmtCreate(now)
                .withGmtModified(now)
                .withFlowId(IdUtils.newId()));
        if (rows <= 0) {
            info.setFlowId(null);
            throw new RuntimeException("Insert flow " + info.getName() + " failed!");
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
                        .withReferType(InfoType.FLOW.text())
                        .withReferId(info.getFlowId()));
                if (rows <= 0) {
                    info.setFlowId(null);
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
                        .withReferType(InfoType.FLOW.text())
                        .withReferId(info.getFlowId()));
                if (rows <= 0) {
                    info.setFlowId(null);
                    throw new RuntimeException("Insert aiModel " + aiModelId + " failed!");
                }
            }
        }
    }

    private Pair<SelectStatementProvider, Boolean> getSelectStatement(
            Query query, User user, QueryExpressionDSL.FromGatherer<SelectModel> fields) {
        // join
        var table = fields.from(Info.table, "f");
        List<String> tags = InfoUtils.trimListElements(query.getWhere().getTags());
        if (CollectionUtils.isNotEmpty(tags)) {
            table.leftJoin(TagDynamicSqlSupport.tag, "t")
                    .on(Info.flowId, equalTo(TagDynamicSqlSupport.referId));

        }
        List<String> modelIds = InfoUtils.trimListElements(query.getWhere().getAiModels());
        if (CollectionUtils.isNotEmpty(modelIds)) {
            table.leftJoin(AiModelDynamicSqlSupport.aiModel, "m")
                    .on(Info.flowId, equalTo(AiModelDynamicSqlSupport.referId));
        }
        List<String> orderByStats =  new LinkedList<>(InfoUtils.trimListElements(query.getOrderBy()));
        orderByStats.retainAll(StatsType.fieldNames());
        if (!orderByStats.isEmpty()) {
            table.leftJoin(InteractiveStatsDynamicSqlSupport.interactiveStats, "i")
                    .on(Info.flowId, equalTo((InteractiveStatsDynamicSqlSupport.referId)));
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
        FlowFormat format = FlowFormat.of(query.getWhere().getFormat());
        if (format != FlowFormat.UNKNOWN) {
            conditions.and(Info.format, isEqualTo(format.text()));
        }
        // name
        conditions.and(Info.name,
                isLike(query.getWhere().getName()).filter(StringUtils::isNotBlank).map(s -> s + "%"));
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
                    orderByStats.contains(orderBy) ? "i" : "f", orderByField));
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

    private List<Triple<FlowInfo, List<String>, List<String>>> doSearch(
            Query query, User user, QueryExpressionDSL.FromGatherer<SelectModel> fields) {
        var statement = getSelectStatement(query, user, fields);
        return flowInfoMapper.selectMany(statement.getLeft())
                .stream()
                .filter(info -> filterVisibility(info, user))
                .peek(info -> handleDraft(info, statement.getRight()))
                .map(this::toInfoTriple)
                .filter(triple -> filterTags(triple, query))
                .filter(triple -> filterAiModels(triple, query))
                .toList();
    }

    @Override
    public List<Triple<FlowInfo, List<String>, List<String>>> search(Query query, User user) {
        /*
select distinct f.user_id, f.flow_id, f.visibility... \
  from flow_info f \
  left join tag t on f.flow_id = t.refer_id \
  left join ai_model m on f.flow_id = m.refer_id \
  where t.refer_type = 'flow' and m.refer_type= 'flow' \
  and ((f.visibility = 'public' and f.user_id = '{userId}') or f.user_id = '{me}') \
  and f.format = '{format}' \
  and t.content in '{tags}' \
  and m.model_id in '{modelIds}' \
  and f.name like '{name}%' \
  and (f.name like '%{text}%' or \
    f.description like '%{text}%' or \
    f.example like '%{text}%' \
  ) \
  order by f.{orderBy[0]}, f.{orderBy[1]}... \
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
    public List<Triple<FlowInfo, List<String>, List<String>>> searchDetails(Query query, User user) {
        // fields
        var fields = selectDistinct(Info.table.allColumns());
        return doSearch(query, user, fields);
    }

    @Override
    public long count(Query query, User user) {
        query.setLimit(null);
        query.setOffset(null);
        var fields = select(countDistinct(Info.flowId));
        var statement = getSelectStatement(query, user, fields);
        return flowInfoMapper.count(statement.getLeft());
    }
    
    @Override
    public boolean create(Triple<FlowInfo, List<String>, List<String>> infoTriple) {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            doCreate(infoTriple);
            session.commit();
            return true;
        } catch (Exception e) {
            log.error("Failed to create flow for {}", infoTriple.getLeft().getName(), e);
            session.rollback();
        } finally {
            session.close();
        }
        return false;
    }

    @Override
    public List<String> create(List<Triple<FlowInfo, List<String>, List<String>>> flowInfoList) {
        SqlSession session = sqlSessionFactory.openSession();
        LinkedList<String> flowIds = new LinkedList<>();
        try {
            for (Triple<FlowInfo, List<String>, List<String>> infoTriple : flowInfoList) {
                doCreate(infoTriple);
                flowIds.add(infoTriple.getLeft().getFlowId());
            }
            session.commit();
        } catch (Exception e) {
            log.error("Failed to create flows", e);
            session.rollback();
            flowIds.clear();
        } finally {
            session.close();
        }
        return flowIds;
    }

    @Override
    @LongPeriodCacheEvict(keyBy = CACHE_KEY_SPEL_PREFIX + "#p0.getLeft().flowId")
    public boolean update(Triple<FlowInfo, List<String>, List<String>> infoTriple) {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            Date now = new Date();
            FlowInfo flowInfo = infoTriple.getLeft();
            flowInfoMapper.updateByPrimaryKeySelective(flowInfo.withGmtModified(now));
            int rows;
            if (CollectionUtils.isNotEmpty(infoTriple.getMiddle())) {
                tagMapper.delete(c -> c.where(TagDynamicSqlSupport.referId, isEqualTo(flowInfo.getFlowId()))
                        .and(TagDynamicSqlSupport.referType, isEqualTo(InfoType.FLOW.text()))
                        .and(TagDynamicSqlSupport.userId, isEqualTo(flowInfo.getUserId())));
                Set<String> tagSet = new HashSet<>(infoTriple.getMiddle());
                for (String tagText : tagSet) {
                    rows = tagMapper.insert(new Tag()
                            .withGmtCreate(now)
                            .withGmtModified(now)
                            .withContent(tagText)
                            .withUserId(flowInfo.getUserId())
                            .withReferType(InfoType.FLOW.text())
                            .withReferId(flowInfo.getFlowId()));
                    if (rows <= 0) {
                        flowInfo.setFlowId(null);
                        throw new RuntimeException("Update tag " + tagText + " failed!");
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(infoTriple.getRight())) {
                aiModelMapper.delete(c -> c.where(AiModelDynamicSqlSupport.referId, isEqualTo(flowInfo.getFlowId()))
                        .and(AiModelDynamicSqlSupport.referType, isEqualTo(InfoType.FLOW.text())));
                Set<String> aiModelSet = new HashSet<>(infoTriple.getRight());
                for (String aiModelId : aiModelSet) {
                    rows = aiModelMapper.insert(new AiModel()
                            .withGmtCreate(now)
                            .withGmtModified(now)
                            .withModelId(aiModelId)
                            .withReferType(InfoType.FLOW.text())
                            .withReferId(flowInfo.getFlowId()));
                    if (rows <= 0) {
                        flowInfo.setFlowId(null);
                        throw new RuntimeException("Update aiModel " + aiModelId + " failed!");
                    }
                }
            }
            session.commit();
            return true;
        } catch (Exception e) {
            log.error("Failed to update flow for {}", infoTriple.getLeft().getName(), e);
            session.rollback();
        } finally {
            session.close();
        }
        return false;
    }

    @Override
    @LongPeriodCacheEvict(keyBy = CACHE_KEY_SPEL_PREFIX + "#p0")
    public boolean hide(String flowId, User user) {
        if (StringUtils.isBlank(flowId)) {
            return false;
        }
        FlowInfo flowInfo = flowInfoMapper.selectOne(c ->
                        c.where(Info.userId, isEqualTo(user.getUserId()))
                                .and(Info.flowId, isEqualTo(flowId)))
                .orElse(null);
        if (Objects.isNull(flowInfo) ||
                Visibility.HIDDEN.text().equals(flowInfo.getVisibility())) {
            return false;
        }
        Date now = new Date();
        flowInfo.withGmtModified(new Date())
                .withVisibility(Visibility.HIDDEN.text());
        int rows = flowInfoMapper.updateByPrimaryKeySelective(flowInfo);
        return rows > 0;
    }

    @Override
    @LongPeriodCacheEvict(keyBy = CACHE_KEY_SPEL_PREFIX + "#p0")
    public boolean delete(String flowId, User user) {
        if (StringUtils.isBlank(flowId)) {
            return false;
        }
        int rows = flowInfoMapper.delete(c -> c.where(Info.flowId, isEqualTo(flowId))
                .and(Info.userId, isEqualTo(user.getUserId())));
        if (rows > 0) {
            tagMapper.delete(c -> c.where(TagDynamicSqlSupport.referId, isEqualTo(flowId))
                    .and(TagDynamicSqlSupport.referType, isEqualTo(InfoType.FLOW.text())));
            aiModelMapper.delete(c -> c.where(AiModelDynamicSqlSupport.referId, isEqualTo(flowId))
                    .and(AiModelDynamicSqlSupport.referType, isEqualTo(InfoType.FLOW.text())));
            interactiveStatsMapper.delete(c ->
                    c.where(InteractiveStatsDynamicSqlSupport.referId, isEqualTo(flowId))
                            .and(InteractiveStatsDynamicSqlSupport.referType, isEqualTo(InfoType.FLOW.text())));
            interactiveStatsScoreDetailsMapper.delete(c ->
                    c.where(InteractiveStatsScoreDetailsDynamicSqlSupport.referId, isEqualTo(flowId))
                            .and(InteractiveStatsScoreDetailsDynamicSqlSupport.referType, isEqualTo(InfoType.FLOW.text())));
        }
        return rows > 0;
    }

    @Override
    public List<String> delete(List<String> flowIds, User user) {
        LinkedList<String> deletedIds = new LinkedList<>();
        SqlSession session = sqlSessionFactory.openSession();
        try {
            for (String flowId : flowIds) {
                if (delete(flowId, user)) {
                    deletedIds.add(flowId);
                }
            }
            session.commit();
            CacheUtils.longPeriodCacheEvict(deletedIds.stream().map(id -> CACHE_KEY_PREFIX + id).toList());
        } catch (Exception e) {
            log.error("Failed to delete flows", e);
            deletedIds.clear();
            session.rollback();
        } finally {
            session.close();
        }
        return deletedIds;
    }

    @Override
    public Triple<FlowInfo, List<String>, List<String>> summary(String flowId, User user) {
        var fields = select(Info.summaryColumns())
                .from(Info.table);

        var statement = wrapQueryExpression(fields, user.getUserId())
                .and(Info.flowId, isEqualTo(flowId))
                .build()
                .render(RenderingStrategies.MYBATIS3);
        return flowInfoMapper.selectOne(statement)
                .filter(info -> filterVisibility(info, user))
                .map(this::toInfoTriple)
                .orElse(null);
    }

    @Override
    public List<Triple<FlowInfo, List<String>, List<String>>> summary(Collection<String> flowIds, User user) {
        var fields = select(Info.summaryColumns())
                .from(Info.table);

        var statement = wrapQueryExpression(fields, user.getUserId())
                .and(Info.flowId, isIn(flowIds))
                .build()
                .render(RenderingStrategies.MYBATIS3);
        return flowInfoMapper.selectMany(statement)
                .stream()
                .filter(info -> filterVisibility(info, user))
                .map(this::toInfoTriple)
                .toList();
    }

    @Override
    @LongPeriodCache(keyBy = CACHE_KEY_SPEL_PREFIX + "#p0")
    public Triple<FlowInfo, List<String>, List<String>> details(String flowId, User user) {
        return flowInfoMapper.selectOne(c -> wrapQueryExpression(c, user.getUserId())
                        .and(Info.flowId, isEqualTo(flowId)))
                .filter(info -> filterVisibility(info, user))
                .map(this::toInfoTriple)
                .orElse(null);
    }

    @Override
    public List<Triple<FlowInfo, List<String>, List<String>>> details(Collection<String> flowIds, User user) {
        return flowInfoMapper.select(c -> wrapQueryExpression(c, user.getUserId())
                        .and(Info.flowId, isIn(flowIds)))
                .stream()
                .filter(info -> filterVisibility(info, user))
                .map(this::toInfoTriple)
                .toList();
    }

    @Override
    public List<Triple<String, Integer, InteractiveStats>> listVersionsByName(String name, User user) {
        var statement = select(Info.flowId, Info.version)
                .from(Info.table)
                .where(Info.name, isEqualTo(name))
                .and(Info.userId, isEqualTo(user.getUserId()))
                .orderBy(Info.version.descending())
                .build()
                .render(RenderingStrategies.MYBATIS3);

        return flowInfoMapper.selectMany(statement)
                .stream()
                .map(info -> {
                    InteractiveStats stats = interactiveStatsMapper.selectOne(c ->
                                    c.where(InteractiveStatsDynamicSqlSupport.referType, isEqualTo(InfoType.FLOW.text()))
                                            .and(InteractiveStatsDynamicSqlSupport.referId, isEqualTo(info.getFlowId())))
                            .orElse(null);
                    return Triple.of(info.getFlowId(), info.getVersion(), stats);
                })
                .toList();
    }

    @Override
    public String getLatestIdByName(String name, User user) {
        var statement = select(Info.flowId, Info.version)
                .from(Info.table)
                .where(Info.name, isEqualTo(name))
                .and(Info.userId, isEqualTo(user.getUserId()))
                .and(Info.visibility, isNotEqualTo(Visibility.HIDDEN.text()))
                .orderBy(Info.version.descending())
                .limit(1)
                .build()
                .render(RenderingStrategies.MYBATIS3);

        return flowInfoMapper.selectOne(statement).map(FlowInfo::getFlowId).orElse(null);
    }

    @Override
    @LongPeriodCacheEvict(keyBy = CACHE_KEY_SPEL_PREFIX + "#p0")
    public String publish(String flowId, Visibility visibility, User user) {
        String publishedInfoId;
        SqlSession session = sqlSessionFactory.openSession();
        try {
            var infoTriple = details(flowId, user);
            if (Objects.isNull(infoTriple)) {
                return null;
            }
            FlowInfo info = infoTriple.getLeft();

            Pair<Integer, InteractiveStats> versionInfo = getLastVersionAndStatsByName(info.getName(), user);
            Integer version = Objects.nonNull(versionInfo) ? versionInfo.getLeft() : info.getVersion();

            info.setVisibility(visibility.text());
            info.setFlowId(null);
            info.setVersion(version + 1);

            if (StringUtils.isNotBlank(info.getDraft())) {
                info.setConfig(info.getDraft());
                info.setDraft(null);
            }

            doCreate(infoTriple);
            publishedInfoId = info.getFlowId();

            info = new FlowInfo()
                    .withFlowId(flowId)
                    .withVisibility(Visibility.HIDDEN.text())
                    .withGmtModified(new Date());

            flowInfoMapper.updateByPrimaryKeySelective(info);

            if (Objects.nonNull(versionInfo) && Objects.nonNull(versionInfo.getRight())) {
                Date now = new Date();
                InteractiveStats stats = versionInfo.getRight();
                interactiveStatsMapper.insertSelective(stats
                        .withId(null)
                        .withGmtCreate(now)
                        .withGmtModified(now)
                        .withReferId(publishedInfoId)
                );
            }
            session.commit();
        } catch (Exception e) {
            log.error("Failed to publish flow", e);
            publishedInfoId = null;
            session.rollback();
        } finally {
            session.close();
        }
        return publishedInfoId;
    }

    @Override
    @LongPeriodCache
    public String getOwner(String flowId) {
        var statement = select(Info.userId)
                .from(Info.table)
                .where(Info.flowId, isEqualTo(flowId))
                .build()
                .render(RenderingStrategies.MYBATIS3);

        return flowInfoMapper.selectOne(statement).map(FlowInfo::getUserId).orElse(null);
    }

    private static class Info {
        public static final FlowInfoDynamicSqlSupport.FlowInfo table = FlowInfoDynamicSqlSupport.flowInfo;
        public static final SqlColumn<String> flowId = FlowInfoDynamicSqlSupport.flowId;
        public static final SqlColumn<Date> gmtCreate = FlowInfoDynamicSqlSupport.gmtCreate;
        public static final SqlColumn<Date> gmtModified = FlowInfoDynamicSqlSupport.gmtModified;
        public static final SqlColumn<String> userId = FlowInfoDynamicSqlSupport.userId;
        public static final SqlColumn<String> parentId = FlowInfoDynamicSqlSupport.parentId;
        public static final SqlColumn<String> visibility = FlowInfoDynamicSqlSupport.visibility;
        public static final SqlColumn<String> format = FlowInfoDynamicSqlSupport.format;
        public static final SqlColumn<Integer> version = FlowInfoDynamicSqlSupport.version;
        public static final SqlColumn<String> name = FlowInfoDynamicSqlSupport.name;
        public static final SqlColumn<String> description = FlowInfoDynamicSqlSupport.description;
        public static final SqlColumn<String> config = FlowInfoDynamicSqlSupport.config;
        public static final SqlColumn<String> example = FlowInfoDynamicSqlSupport.example;
        public static final SqlColumn<String> parameters = FlowInfoDynamicSqlSupport.parameters;
        public static final SqlColumn<String> ext = FlowInfoDynamicSqlSupport.ext;

        public static List<BasicColumn> summaryColumns () {
            return List.of(
                    Info.gmtCreate,
                    Info.gmtModified,
                    Info.userId,
                    Info.flowId,
                    Info.visibility,
                    Info.format,
                    Info.version,
                    Info.name,
                    Info.description);
        }

    }
}
