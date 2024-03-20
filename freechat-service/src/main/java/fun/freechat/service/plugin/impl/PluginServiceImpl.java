package fun.freechat.service.plugin.impl;

import fun.freechat.mapper.*;
import fun.freechat.model.AiModel;
import fun.freechat.model.PluginInfo;
import fun.freechat.model.Tag;
import fun.freechat.model.User;
import fun.freechat.service.cache.LongPeriodCache;
import fun.freechat.service.cache.LongPeriodCacheEvict;
import fun.freechat.service.enums.*;
import fun.freechat.service.organization.OrgService;
import fun.freechat.service.plugin.PluginFetchService;
import fun.freechat.service.plugin.PluginService;
import fun.freechat.service.util.CacheUtils;
import fun.freechat.service.util.InfoUtils;
import fun.freechat.service.util.SortSpecificationWrapper;
import fun.freechat.util.HttpUtils;
import fun.freechat.util.IdUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
public class PluginServiceImpl implements PluginService {
    private final static String CACHE_KEY_PREFIX = "PluginService_";

    private final static String CACHE_KEY_SPEL_PREFIX = "'" + CACHE_KEY_PREFIX + "' + ";

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Autowired
    private PluginInfoMapper pluginInfoMapper;

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private AiModelMapper aiModelMapper;

    @Autowired
    private PluginFetchService fetchService;

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

    private boolean filterVisibility(PluginInfo info, User user) {
        Visibility visibility = Visibility.of(info.getVisibility());
        return switch (visibility) {
            case PUBLIC -> true;
            case PUBLIC_ORG -> orgService.hasRelationship(user.getUserId(), info.getUserId());
            case PRIVATE, HIDDEN -> user.getUserId().equals(info.getUserId());
        };
    }

    private boolean filterTags(Triple<PluginInfo, List<String>, List<String>> triple, Query query) {
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

    private boolean filterAiModels(Triple<PluginInfo, List<String>, List<String>> triple, Query query) {
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

    private PluginInfo fetchInfo(PluginInfo info) {
        if (HttpUtils.isValidUrl(info.getManifestInfo())) {
            info.setManifestInfo(fetchService.fetchManifestInfo(info));
        }
        if (HttpUtils.isValidUrl(info.getApiInfo())) {
            info.setApiInfo(fetchService.fetchApiDocsInfo(info));
        }
        if (Objects.isNull(info.getManifestInfo())) {
            info.setManifestInfo("");
        }
        if (Objects.isNull(info.getApiInfo())) {
            info.setApiInfo("");
        }
        return info;
    }

    private Triple<PluginInfo, List<String>, List<String>> toInfoTriple(PluginInfo info) {
        if (Objects.isNull(info)) {
            return null;
        }
        List<String> tags = tagMapper.select(c ->
                        c.where(TagDynamicSqlSupport.referType, isEqualTo(InfoType.PLUGIN.text()))
                                .and(TagDynamicSqlSupport.referId, isEqualTo(info.getPluginUid())))
                .stream()
                .map(Tag::getContent)
                .toList();
        List<String> aiModels = aiModelMapper.select(c ->
                        c.where(AiModelDynamicSqlSupport.referType, isEqualTo(InfoType.PLUGIN.text()))
                                .and(AiModelDynamicSqlSupport.referId, isEqualTo(info.getPluginUid())))
                .stream()
                .map(AiModel::getModelId)
                .toList();
        return Triple.of(info, tags, aiModels);
    }

    private static SqlColumn nameToColumn(String fieldName) {
        return switch (fieldName) {
            case "modifyTime" -> Info.gmtModified;
            case "createTime" -> Info.gmtCreate;
            case "viewCount" -> InteractiveStatsDynamicSqlSupport.viewCount;
            case "referCount" -> InteractiveStatsDynamicSqlSupport.referCount;
            case "recommendCount" -> InteractiveStatsDynamicSqlSupport.recommendCount;
            case "score" -> InteractiveStatsDynamicSqlSupport.score;
            default -> null;
        };
    }

    private void doCreate(Triple<PluginInfo, List<String>, List<String>> infoTriple) {
        PluginInfo info = infoTriple.getLeft();
        Date now = new Date();
        int rows = pluginInfoMapper.insertSelective(info
                .withGmtCreate(now)
                .withGmtModified(now));
        if (rows <= 0) {
            throw new RuntimeException("Insert plugin " + info.getName() + " failed!");
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
                        .withReferType(InfoType.PLUGIN.text())
                        .withReferId(info.getPluginUid()));
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
                        .withReferType(InfoType.PLUGIN.text())
                        .withReferId(info.getPluginUid()));
                if (rows <= 0) {
                    throw new RuntimeException("Insert aiModel " + aiModelId + " failed!");
                }
            }
        }
    }

    private SelectStatementProvider getSelectStatement(
            Query query, User user, QueryExpressionDSL.FromGatherer<SelectModel> fields) {
        // join
        var table = fields.from(Info.table, "p");
        List<String> tags = InfoUtils.trimListElements(query.getWhere().getTags());
        if (CollectionUtils.isNotEmpty(tags)) {
            table.leftJoin(TagDynamicSqlSupport.tag, "t")
                    .on(Info.pluginUid, equalTo(TagDynamicSqlSupport.referId));

        }
        List<String> modelIds = InfoUtils.trimListElements(query.getWhere().getAiModels());
        if (CollectionUtils.isNotEmpty(modelIds)) {
            table.leftJoin(AiModelDynamicSqlSupport.aiModel, "m")
                    .on(Info.pluginUid, equalTo(AiModelDynamicSqlSupport.referId));
        }
        List<String> orderByStats =  new LinkedList<>(InfoUtils.trimListElements(query.getOrderBy()));
        orderByStats.retainAll(StatsType.fieldNames());
        if (!orderByStats.isEmpty()) {
            table.leftJoin(InteractiveStatsDynamicSqlSupport.interactiveStats, "i")
                    .on(Info.pluginUid, equalTo((InteractiveStatsDynamicSqlSupport.referId)));
        }
        // conditions
        var conditions = table.where();
        // visibility
        String visibilityStr = query.getWhere().getVisibility();
        String userIdStr = query.getWhere().getUserId();
        if (StringUtils.isBlank(visibilityStr)) {
            if (StringUtils.isBlank(userIdStr) || userIdStr.equals(user.getUserId())) {
                conditions.and(Info.visibility, isNotEqualTo(Visibility.HIDDEN.text()))
                        .and(Info.userId, isEqualTo(user.getUserId()));
            } else {
                conditions.and(Info.visibility,
                                isIn(Visibility.PUBLIC.text(), Visibility.PUBLIC_ORG.text()))
                        .and(Info.userId, isEqualTo(userIdStr));
            }
        } else {
            Visibility visibility = Visibility.of(query.getWhere().getVisibility());
            conditions.and(Info.visibility, isEqualTo(visibility.text()));
            if (visibility == Visibility.PUBLIC || visibility == Visibility.PUBLIC_ORG) {
                if (StringUtils.isNotBlank(userIdStr)) {
                    conditions.and(Info.userId, isEqualTo(userIdStr));
                }
            } else {
                conditions.and(Info.userId, isEqualTo(user.getUserId()));
            }
        }
        // manifest format
        ModelProvider modelProvider = ModelProvider.of(query.getWhere().getManifestFormat());
        if (modelProvider != ModelProvider.UNKNOWN) {
            conditions.and(Info.manifestFormat, isEqualTo(modelProvider.text()));
        }
        // api format
        ApiFormat apiFormat = ApiFormat.of(query.getWhere().getApiFormat());
        if (apiFormat != ApiFormat.UNKNOWN) {
            conditions.and(Info.apiFormat, isEqualTo(apiFormat.text()));
        }
        // name
        conditions.and(Info.name,
                isLike(query.getWhere().getName()).filter(StringUtils::isNotBlank).map(s -> s + "%"));
        // text
        String commonText = query.getWhere().getText();
        if (StringUtils.isNotBlank(commonText)) {
            var commonTextCondition = isLike(commonText).map(s -> "%" + s + "%");
            conditions.and(Info.name, commonTextCondition,
                    or(Info.provider, commonTextCondition),
                    or(Info.manifestInfo, commonTextCondition));
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
                    orderByStats.contains(orderBy) ? "i" : "p", orderByField));
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

        return conditions.build().render(RenderingStrategies.MYBATIS3);
    }

    private List<Triple<PluginInfo, List<String>, List<String>>> doSearch(
            Query query, User user, QueryExpressionDSL.FromGatherer<SelectModel> fields) {
        return pluginInfoMapper.selectMany(getSelectStatement(query, user, fields))
                .stream()
                .filter(info -> filterVisibility(info, user))
                .peek(this::fetchInfo)
                .map(this::toInfoTriple)
                .filter(triple -> filterTags(triple, query))
                .filter(triple -> filterAiModels(triple, query))
                .toList();
    }

    @Override
    public List<Triple<PluginInfo, List<String>, List<String>>> search(Query query, User user) {
                /*
select distinct p.user_id, p.plugin_id, p.visibility... \
  from plugin_info p \
  left join tag t on p.plugin_id = t.refer_id \
  left join ai_model m on p.plugin_id = m.refer_id \
  where t.refer_type = 'plugin' and m.refer_type= 'plugin' \
  and ((p.visibility = 'public' and p.user_id = '{userId}') or p.user_id = '{me}') \
  and p.manifest_format = '{manifestFormat}' \
  and p.api_format = '{apiFormat}' \
  and t.content in '{tags}' \
  and m.model_id in '{modelIds}' \
  and p.name like '{name}%' \
  and p.provider like '{provider}%' \
  and (p.name like '%{text}%' or \
    p.provider like '%{text}%' or \
    p.manifest_info like '%{text}%' \
  ) \
  order by p.{orderBy[0]}, p.{orderBy[1]}... \
  limit {limit} offset {offset} \
;
         */
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
    public List<Triple<PluginInfo, List<String>, List<String>>> searchDetails(Query query, User user) {
        var fields = selectDistinct(Info.table.allColumns());
        return doSearch(query, user, fields);
    }

    @Override
    public long count(Query query, User user) {
        query.setLimit(null);
        query.setOffset(null);
        var fields = select(countDistinct(Info.pluginId));
        var statement = getSelectStatement(query, user, fields);
        return pluginInfoMapper.count(statement);
    }

    @Override
    public boolean create(Triple<PluginInfo, List<String>, List<String>> infoTriple) {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            infoTriple.getLeft().setPluginUid(IdUtils.newId());
            doCreate(infoTriple);
            session.commit();
            return true;
        } catch (Exception e) {
            log.error("Failed to create plugin for {}", infoTriple.getLeft().getName(), e);
            session.rollback();
        } finally {
            session.close();
        }
        return false;
    }

    @Override
    public List<Long> create(List<Triple<PluginInfo, List<String>, List<String>>> pluginInfoList) {
        SqlSession session = sqlSessionFactory.openSession();
        LinkedList<Long> pluginIds = new LinkedList<>();
        try {
            for (Triple<PluginInfo, List<String>, List<String>> infoTriple : pluginInfoList) {
                infoTriple.getLeft().setPluginUid(IdUtils.newId());
                doCreate(infoTriple);
                pluginIds.add(infoTriple.getLeft().getPluginId());
            }
            session.commit();
        } catch (Exception e) {
            log.error("Failed to create plugins", e);
            session.rollback();
            pluginIds.clear();
        } finally {
            session.close();
        }
        return pluginIds;
    }

    @Override
    @LongPeriodCacheEvict(keyBy = CACHE_KEY_SPEL_PREFIX + "#p0.getLeft().pluginId")
    public boolean update(Triple<PluginInfo, List<String>, List<String>> infoTriple) {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            Date now = new Date();
            PluginInfo pluginInfo = infoTriple.getLeft();
            pluginInfoMapper.updateByPrimaryKeySelective(pluginInfo.withGmtModified(now));
            int rows;
            if (CollectionUtils.isNotEmpty(infoTriple.getMiddle())) {
                tagMapper.delete(c -> c.where(TagDynamicSqlSupport.referId, isEqualTo(pluginInfo.getPluginUid()))
                        .and(TagDynamicSqlSupport.referType, isEqualTo(InfoType.PLUGIN.text()))
                        .and(TagDynamicSqlSupport.userId, isEqualTo(pluginInfo.getUserId())));
                Set<String> tagSet = new HashSet<>(infoTriple.getMiddle());
                for (String tagText : tagSet) {
                    rows = tagMapper.insert(new Tag()
                            .withGmtCreate(now)
                            .withGmtModified(now)
                            .withContent(tagText)
                            .withUserId(pluginInfo.getUserId())
                            .withReferType(InfoType.PLUGIN.text())
                            .withReferId(pluginInfo.getPluginUid()));
                    if (rows <= 0) {
                        throw new RuntimeException("Update tag " + tagText + " failed!");
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(infoTriple.getRight())) {
                aiModelMapper.delete(c -> c.where(AiModelDynamicSqlSupport.referId, isEqualTo(pluginInfo.getPluginUid()))
                        .and(AiModelDynamicSqlSupport.referType, isEqualTo(InfoType.PLUGIN.text())));
                Set<String> aiModelSet = new HashSet<>(infoTriple.getRight());
                for (String aiModelId : aiModelSet) {
                    rows = aiModelMapper.insert(new AiModel()
                            .withGmtCreate(now)
                            .withGmtModified(now)
                            .withModelId(aiModelId)
                            .withReferType(InfoType.PLUGIN.text())
                            .withReferId(pluginInfo.getPluginUid()));
                    if (rows <= 0) {
                        throw new RuntimeException("Update aiModel " + aiModelId + " failed!");
                    }
                }
            }
            session.commit();
            return true;
        } catch (Exception e) {
            log.error("Failed to update plugin for {}", infoTriple.getLeft().getName(), e);
            session.rollback();
        } finally {
            session.close();
        }
        return false;
    }

    @Override
    @LongPeriodCacheEvict(keyBy = CACHE_KEY_SPEL_PREFIX + "#p0")
    public boolean hide(Long pluginId, User user) {
        if (Objects.isNull(pluginId)) {
            return false;
        }
        PluginInfo pluginInfo = pluginInfoMapper.selectOne(c ->
                        c.where(Info.userId, isEqualTo(user.getUserId()))
                                .and(Info.pluginId, isEqualTo(pluginId)))
                .orElse(null);
        if (Objects.isNull(pluginInfo) ||
                Visibility.HIDDEN.text().equals(pluginInfo.getVisibility())) {
            return false;
        }
        Date now = new Date();
        pluginInfo.withGmtModified(new Date())
                .withVisibility(Visibility.HIDDEN.text());
        int rows = pluginInfoMapper.updateByPrimaryKeySelective(pluginInfo);
        return rows > 0;
    }

    @Override
    @LongPeriodCacheEvict(keyBy = CACHE_KEY_SPEL_PREFIX + "#p0")
    public boolean delete(Long pluginId, User user) {
        if (Objects.isNull(pluginId)) {
            return false;
        }
        int rows = pluginInfoMapper.delete(c -> c.where(Info.pluginId, isEqualTo(pluginId))
                .and(Info.userId, isEqualTo(user.getUserId())));
        return rows > 0;
    }

    @Override
    public List<Long> delete(List<Long> pluginIds, User user) {
        LinkedList<Long> deletedIds = new LinkedList<>();
        SqlSession session = sqlSessionFactory.openSession();
        try {
            for (Long pluginId : pluginIds) {
                if (delete(pluginId, user)) {
                    deletedIds.add(pluginId);
                }
            }
            session.commit();
            CacheUtils.longPeriodCacheEvict(deletedIds.stream().map(id -> CACHE_KEY_PREFIX + id).toList());
        } catch (Exception e) {
            log.error("Failed to delete plugins", e);
            deletedIds.clear();
            session.rollback();
        } finally {
            session.close();
        }
        return deletedIds;
    }

    @Override
    public Triple<PluginInfo, List<String>, List<String>> summary(Long pluginId, User user) {
        var fields = select(Info.summaryColumns())
                .from(Info.table);

        var statement = wrapQueryExpression(fields, user.getUserId())
                .and(Info.pluginId, isEqualTo(pluginId))
                .build()
                .render(RenderingStrategies.MYBATIS3);
        return pluginInfoMapper.selectOne(statement)
                .filter(info -> filterVisibility(info, user))
                .map(this::fetchInfo)
                .map(this::toInfoTriple)
                .orElse(null);
    }

    @Override
    public List<Triple<PluginInfo, List<String>, List<String>>> summary(Collection<Long> pluginIds, User user) {
        var fields = select(Info.summaryColumns())
                .from(Info.table);

        var statement = wrapQueryExpression(fields, user.getUserId())
                .and(Info.pluginId, isIn(pluginIds))
                .build()
                .render(RenderingStrategies.MYBATIS3);
        return pluginInfoMapper.selectMany(statement)
                .stream()
                .filter(info -> filterVisibility(info, user))
                .map(this::fetchInfo)
                .map(this::toInfoTriple)
                .toList();
    }

    @Override
    @LongPeriodCache(keyBy = CACHE_KEY_SPEL_PREFIX + "#p0")
    public Triple<PluginInfo, List<String>, List<String>> details(Long pluginId, User user) {
        return pluginInfoMapper.selectOne(c -> wrapQueryExpression(c, user.getUserId())
                        .and(Info.pluginId, isEqualTo(pluginId)))
                .filter(info -> filterVisibility(info, user))
                .map(this::fetchInfo)
                .map(this::toInfoTriple)
                .orElse(null);
    }

    @Override
    public List<Triple<PluginInfo, List<String>, List<String>>> details(Collection<Long> pluginIds, User user) {
        return pluginInfoMapper.select(c -> wrapQueryExpression(c, user.getUserId())
                        .and(Info.pluginId, isIn(pluginIds)))
                .stream()
                .filter(info -> filterVisibility(info, user))
                .peek(this::fetchInfo)
                .map(this::toInfoTriple)
                .toList();
    }

    @Override
    public Long getIdByUid(String pluginUid, User user) {
        var statement = select(Info.pluginId, Info.gmtCreate)
                .from(Info.table)
                .where(Info.pluginUid, isEqualTo(pluginUid))
                .and(Info.userId, isEqualTo(user.getUserId()))
                .and(Info.visibility, isNotEqualTo(Visibility.HIDDEN.text()))
                .orderBy(Info.gmtCreate.descending())
                .limit(1)
                .build()
                .render(RenderingStrategies.MYBATIS3);

        return pluginInfoMapper.selectOne(statement).map(PluginInfo::getPluginId).orElse(null);
    }

    @Override
    @LongPeriodCache
    public String getOwner(Long pluginId) {
        var statement = select(Info.userId)
                .from(Info.table)
                .where(Info.pluginId, isEqualTo(pluginId))
                .build()
                .render(RenderingStrategies.MYBATIS3);

        return pluginInfoMapper.selectOne(statement).map(PluginInfo::getUserId).orElse(null);
    }

    @Override
    public String getOwnerByUid(String pluginUid) {
        var statement = select(Info.userId)
                .from(Info.table)
                .where(Info.pluginUid, isEqualTo(pluginUid))
                .limit(1)
                .build()
                .render(RenderingStrategies.MYBATIS3);

        return pluginInfoMapper.selectOne(statement).map(PluginInfo::getUserId).orElse(null);
    }

    @Override
    public String getUid(Long pluginId) {
        var statement = select(Info.pluginUid)
                .from(Info.table)
                .where(Info.pluginId, isEqualTo(pluginId))
                .build()
                .render(RenderingStrategies.MYBATIS3);

        return pluginInfoMapper.selectOne(statement).map(PluginInfo::getPluginUid).orElse(null);
    }

    private static class Info {
        public static final PluginInfoDynamicSqlSupport.PluginInfo table = PluginInfoDynamicSqlSupport.pluginInfo;
        public static final SqlColumn<Long> pluginId = PluginInfoDynamicSqlSupport.pluginId;
        public static final SqlColumn<String> pluginUid = PluginInfoDynamicSqlSupport.pluginUid;
        public static final SqlColumn<Date> gmtCreate = PluginInfoDynamicSqlSupport.gmtCreate;
        public static final SqlColumn<Date> gmtModified = PluginInfoDynamicSqlSupport.gmtModified;
        public static final SqlColumn<String> userId = PluginInfoDynamicSqlSupport.userId;
        public static final SqlColumn<String> visibility = PluginInfoDynamicSqlSupport.visibility;
        public static final SqlColumn<String> name = PluginInfoDynamicSqlSupport.name;
        public static final SqlColumn<String> manifestFormat = PluginInfoDynamicSqlSupport.manifestFormat;
        public static final SqlColumn<String> apiFormat = PluginInfoDynamicSqlSupport.apiFormat;
        public static final SqlColumn<String> provider = PluginInfoDynamicSqlSupport.provider;
        public static final SqlColumn<String> manifestInfo = PluginInfoDynamicSqlSupport.manifestInfo;
        public static final SqlColumn<String> apiInfo = PluginInfoDynamicSqlSupport.apiInfo;
        public static final SqlColumn<String> ext = PluginInfoDynamicSqlSupport.ext;

        public static List<BasicColumn> summaryColumns() {
            return List.of(
                    Info.gmtCreate,
                    Info.gmtModified,
                    Info.userId,
                    Info.pluginId,
                    Info.pluginUid,
                    Info.visibility,
                    Info.name,
                    Info.provider,
                    Info.manifestFormat,
                    Info.apiFormat
            );
        }
    }
}
