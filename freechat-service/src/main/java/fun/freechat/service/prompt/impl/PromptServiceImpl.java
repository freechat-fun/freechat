package fun.freechat.service.prompt.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import dev.langchain4j.data.message.*;
import dev.langchain4j.model.input.PromptTemplate;
import fun.freechat.langchain4j.model.input.FStringPromptTemplate;
import fun.freechat.mapper.*;
import fun.freechat.model.*;
import fun.freechat.service.cache.LongPeriodCache;
import fun.freechat.service.cache.LongPeriodCacheEvict;
import fun.freechat.service.enums.*;
import fun.freechat.service.organization.OrgService;
import fun.freechat.service.prompt.ChatPromptContent;
import fun.freechat.service.prompt.PromptService;
import fun.freechat.service.util.CacheUtils;
import fun.freechat.service.util.InfoUtils;
import fun.freechat.service.util.PromptUtils;
import fun.freechat.service.util.SortSpecificationWrapper;
import fun.freechat.util.IdUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
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

import static dev.langchain4j.data.message.ContentType.TEXT;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

@Service
@Slf4j
@SuppressWarnings({"unused", "rawtypes"})
public class PromptServiceImpl implements PromptService {
    private final static String CACHE_KEY_PREFIX = "PromptService_";
    private final static String CACHE_KEY_SPEL_PREFIX = "'" + CACHE_KEY_PREFIX + "' + ";

    @Autowired
    private SqlSessionFactory sqlSessionFactory;
    @Autowired
    private PromptInfoMapper promptInfoMapper;
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
                or(Info.userId, isEqualTo(currentUserId)));
    }

    private boolean filterVisibility(PromptInfo info, User user) {
        Visibility visibility = Visibility.of(info.getVisibility());
        return switch (visibility) {
            case PUBLIC -> true;
            case PUBLIC_ORG -> orgService.hasRelationship(user.getUserId(), info.getUserId());
            case PRIVATE, HIDDEN -> user.getUserId().equals(info.getUserId());
        };
    }

    private boolean filterTags(Triple<PromptInfo, List<String>, List<String>> triple, Query query) {
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

    private boolean filterAiModels(Triple<PromptInfo, List<String>, List<String>> triple, Query query) {
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

    private void handleDraft(PromptInfo info, boolean hasDraft) {
        if (!hasDraft) {
            info.setDraft(null);
        }
    }

    private Triple<PromptInfo, List<String>, List<String>> toInfoTriple(PromptInfo info) {
        if (Objects.isNull(info)) {
            return null;
        }
        List<String> tags = tagMapper.select(c ->
                        c.where(TagDynamicSqlSupport.referType, isEqualTo(InfoType.PROMPT.text()))
                                .and(TagDynamicSqlSupport.referId, isEqualTo(info.getPromptUid())))
                .stream()
                .map(Tag::getContent)
                .toList();
        List<String> aiModels = aiModelMapper.select(c ->
                        c.where(AiModelDynamicSqlSupport.referType, isEqualTo(InfoType.PROMPT.text()))
                                .and(AiModelDynamicSqlSupport.referId, isEqualTo(info.getPromptUid())))
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

    private void doCreate(Triple<PromptInfo, List<String>, List<String>> infoTriple) {
        PromptInfo info = infoTriple.getLeft();
        Date now = new Date();
        int rows = promptInfoMapper.insertSelective(info
                .withGmtCreate(now)
                .withGmtModified(now));
        if (rows <= 0) {
            throw new RuntimeException("Insert prompt " + info.getName() + " failed!");
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
                        .withReferType(InfoType.PROMPT.text())
                        .withReferId(info.getPromptUid()));
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
                        .withReferType(InfoType.PROMPT.text())
                        .withReferId(info.getPromptUid()));
                if (rows <= 0) {
                    throw new RuntimeException("Insert aiModel " + aiModelId + " failed!");
                }
            }
        }
    }

    private Pair<SelectStatementProvider, Boolean> getSelectStatement(
            Query query, User user, QueryExpressionDSL.FromGatherer<SelectModel> fields) {
        // join
        var table = fields.from(Info.table, "p");
        List<String> tags = InfoUtils.trimListElements(query.getWhere().getTags());
        if (CollectionUtils.isNotEmpty(tags)) {
            table.leftJoin(TagDynamicSqlSupport.tag, "t")
                    .on(Info.promptUid, equalTo(TagDynamicSqlSupport.referId));

        }
        List<String> modelIds = InfoUtils.trimListElements(query.getWhere().getAiModels());
        if (CollectionUtils.isNotEmpty(modelIds)) {
            table.leftJoin(AiModelDynamicSqlSupport.aiModel, "m")
                    .on(Info.promptUid, equalTo(AiModelDynamicSqlSupport.referId));
        }
        List<String> orderByStats =  new LinkedList<>(InfoUtils.trimListElements(query.getOrderBy()));
        orderByStats.retainAll(StatsType.fieldNames());
        if (!orderByStats.isEmpty()) {
            table.leftJoin(InteractiveStatsDynamicSqlSupport.interactiveStats, "i")
                    .on(Info.promptUid, equalTo((InteractiveStatsDynamicSqlSupport.referId)));
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
        // name
        conditions.and(Info.name,
                isLike(query.getWhere().getName()).filter(StringUtils::isNotBlank).map(s -> s + "%"));
        // type
        conditions.and(Info.type,
                isEqualTo(query.getWhere().getType()).filter(StringUtils::isNotBlank));
        // lang
        conditions.and(Info.lang,
                isEqualTo(query.getWhere().getLang()).filter(StringUtils::isNotBlank));
        // version
        if (!hasDraft) {
            conditions.and(Info.version, isGreaterThan(0));
        }
        // text
        String commonText = query.getWhere().getText();
        if (StringUtils.isNotBlank(commonText)) {
            var commonTextCondition = isLike(commonText).map(s -> "%" + commonText + "%");
            conditions.and(Info.name, commonTextCondition,
                    or(Info.description, commonTextCondition),
                    or(Info.template, commonTextCondition),
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

        return Pair.of(conditions.build().render(RenderingStrategies.MYBATIS3), hasDraft);
    }

    private List<Triple<PromptInfo, List<String>, List<String>>> doSearch(
            Query query, User user, QueryExpressionDSL.FromGatherer<SelectModel> fields) {
        var statement = getSelectStatement(query, user, fields);
        return promptInfoMapper.selectMany(statement.getLeft())
                .stream()
                .filter(info -> filterVisibility(info, user))
                .peek(info -> handleDraft(info, statement.getRight()))
                .map(this::toInfoTriple)
                .filter(triple -> filterTags(triple, query))
                .filter(triple -> filterAiModels(triple, query))
                .toList();
    }

    @Override
    public List<Triple<PromptInfo, List<String>, List<String>>> search(Query query, User user) {
        /*
select distinct p.user_id, p.prompt_id, p.visibility... \
  from prompt_info p \
  left join tag t on p.prompt_id = t.refer_id \
  left join ai_model m on p.prompt_id = m.refer_id \
  where t.refer_type = 'prompt' and m.refer_type= 'prompt' \
  and ((p.visibility = 'public' and p.user_id = '{userId}') or p.user_id = '{me}') \
  and t.content in '{tags}' \
  and m.model_id in '{modelIds}' \
  and p.name like '{name}%' \
  and p.lang = '{lang}' \
  and p.version > 0 \
  and (p.name like '%{text}%' or \
    p.description like '%{text}%' or \
    p.template like '%{text}%' or \
    p.example like '%{text}%' \
  ) \
  order by p.{orderBy[0]}, p.{orderBy[1]}... \
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
    public List<Triple<PromptInfo, List<String>, List<String>>> searchDetails(Query query, User user) {
        var fields = selectDistinct(Info.table.allColumns());
        return doSearch(query, user, fields);
    }

    @Override
    public long count(Query query, User user) {
        query.setLimit(null);
        query.setOffset(null);
        var fields = select(countDistinct(Info.promptId));
        var statement = getSelectStatement(query, user, fields);
        return promptInfoMapper.count(statement.getLeft());
    }

    @Override
    public boolean create(Triple<PromptInfo, List<String>, List<String>> infoTriple) {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            infoTriple.getLeft().setPromptUid(IdUtils.newId());
            doCreate(infoTriple);
            session.commit();
            return true;
        } catch (Exception e) {
            log.error("Failed to create prompt for {}", infoTriple.getLeft().getName(), e);
            session.rollback();
        } finally {
            session.close();
        }
        return false;
    }

    @Override
    public List<Long> create(List<Triple<PromptInfo, List<String>, List<String>>> promptInfoList) {
        SqlSession session = sqlSessionFactory.openSession();
        LinkedList<Long> promptIds = new LinkedList<>();
        try {
            for (Triple<PromptInfo, List<String>, List<String>> infoTriple : promptInfoList) {
                infoTriple.getLeft().setPromptUid(IdUtils.newId());
                doCreate(infoTriple);
                promptIds.add(infoTriple.getLeft().getPromptId());
            }
            session.commit();
        } catch (Exception e) {
            log.error("Failed to create prompts", e);
            session.rollback();
            promptIds.clear();
        } finally {
            session.close();
        }
        return promptIds;
    }

    @Override
    @LongPeriodCacheEvict(keyBy = CACHE_KEY_SPEL_PREFIX + "#p0.getLeft().promptId")
    public boolean update(Triple<PromptInfo, List<String>, List<String>> infoTriple) {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            Date now = new Date();
            PromptInfo promptInfo = infoTriple.getLeft();
            promptInfoMapper.updateByPrimaryKeySelective(promptInfo.withGmtModified(now));
            int rows;
            if (CollectionUtils.isNotEmpty(infoTriple.getMiddle())) {
                tagMapper.delete(c -> c.where(TagDynamicSqlSupport.referId, isEqualTo(promptInfo.getPromptUid()))
                        .and(TagDynamicSqlSupport.referType, isEqualTo(InfoType.PROMPT.text()))
                        .and(TagDynamicSqlSupport.userId, isEqualTo(promptInfo.getUserId())));
                Set<String> tagSet = new HashSet<>(infoTriple.getMiddle());
                for (String tagText : tagSet) {
                    rows = tagMapper.insert(new Tag()
                            .withGmtCreate(now)
                            .withGmtModified(now)
                            .withContent(tagText)
                            .withUserId(promptInfo.getUserId())
                            .withReferType(InfoType.PROMPT.text())
                            .withReferId(promptInfo.getPromptUid()));
                    if (rows <= 0) {
                        throw new RuntimeException("Update tag " + tagText + " failed!");
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(infoTriple.getRight())) {
                aiModelMapper.delete(c -> c.where(AiModelDynamicSqlSupport.referId, isEqualTo(promptInfo.getPromptUid()))
                        .and(AiModelDynamicSqlSupport.referType, isEqualTo(InfoType.PROMPT.text())));
                Set<String> aiModelSet = new HashSet<>(infoTriple.getRight());
                for (String aiModelId : aiModelSet) {
                    rows = aiModelMapper.insert(new AiModel()
                            .withGmtCreate(now)
                            .withGmtModified(now)
                            .withModelId(aiModelId)
                            .withReferType(InfoType.PROMPT.text())
                            .withReferId(promptInfo.getPromptUid()));
                    if (rows <= 0) {
                        throw new RuntimeException("Update aiModel " + aiModelId + " failed!");
                    }
                }
            }
            session.commit();
            return true;
        } catch (Exception e) {
            log.error("Failed to update prompt for {}", infoTriple.getLeft().getName(), e);
            session.rollback();
        } finally {
            session.close();
        }
        return false;
    }

    @Override
    @LongPeriodCacheEvict(keyBy = CACHE_KEY_SPEL_PREFIX + "#p0")
    public boolean hide(Long promptId, User user) {
        if (Objects.isNull(promptId)) {
            return false;
        }
        PromptInfo promptInfo = promptInfoMapper.selectOne(c ->
                c.where(Info.userId, isEqualTo(user.getUserId()))
                        .and(Info.promptId, isEqualTo(promptId)))
                .orElse(null);
        if (Objects.isNull(promptInfo) ||
                Visibility.HIDDEN.text().equals(promptInfo.getVisibility())) {
            return false;
        }
        Date now = new Date();
        promptInfo.withGmtModified(new Date())
                .withVisibility(Visibility.HIDDEN.text());
        int rows = promptInfoMapper.updateByPrimaryKeySelective(promptInfo);
        return rows > 0;
    }

    @Override
    @LongPeriodCacheEvict(keyBy = CACHE_KEY_SPEL_PREFIX + "#p0")
    public boolean delete(Long promptId, User user) {
        if (Objects.isNull(promptId)) {
            return false;
        }
        int rows = promptInfoMapper.delete(c -> c.where(Info.promptId, isEqualTo(promptId))
                    .and(Info.userId, isEqualTo(user.getUserId())));
        return rows > 0;
    }

    @Override
    public List<Long> delete(List<Long> promptIds, User user) {
        LinkedList<Long> deletedIds = new LinkedList<>();
        SqlSession session = sqlSessionFactory.openSession();
        try {
            for (Long promptId : promptIds) {
                if (delete(promptId, user)) {
                    deletedIds.add(promptId);
                }
            }
            session.commit();
            CacheUtils.longPeriodCacheEvict(deletedIds.stream().map(id -> CACHE_KEY_PREFIX + id).toList());
        } catch (Exception e) {
            log.error("Failed to delete prompts", e);
            deletedIds.clear();
            session.rollback();
        } finally {
            session.close();
        }

        return deletedIds;
    }

    @Override
    public List<Long> deleteByName(String name, User user) {
        var statement = select(Info.promptId)
                .from(Info.table)
                .where(Info.userId, isEqualTo(user.getUserId()))
                .and(Info.name, isEqualTo(name))
                .build()
                .render(RenderingStrategies.MYBATIS3);
        List<Long> ids = promptInfoMapper.selectMany(statement)
                .stream()
                .map(PromptInfo::getPromptId)
                .toList();

        return delete(ids, user);
    }

    @Override
    public void deleteByUser(User user) {
        var statement = select(Info.promptId)
                .from(Info.table)
                .where(Info.userId, isEqualTo(user.getUserId()))
                .build()
                .render(RenderingStrategies.MYBATIS3);
        List<Long> ids = promptInfoMapper.selectMany(statement)
                .stream()
                .map(PromptInfo::getPromptId)
                .toList();

        delete(ids, user);
    }

    @Override
    public Triple<PromptInfo, List<String>, List<String>> summary(Long promptId, User user) {
        var fields = select(Info.summaryColumns())
                .from(Info.table);

        var statement = wrapQueryExpression(fields, user.getUserId())
                .and(Info.promptId, isEqualTo(promptId))
                .build()
                .render(RenderingStrategies.MYBATIS3);
        return promptInfoMapper.selectOne(statement)
                .filter(info -> filterVisibility(info, user))
                .map(this::toInfoTriple)
                .orElse(null);
    }

    @Override
    public List<Triple<PromptInfo, List<String>, List<String>>> summary(Collection<Long> promptIds, User user) {
        var fields = select(Info.summaryColumns())
                .from(Info.table);

        var statement = wrapQueryExpression(fields, user.getUserId())
                .and(Info.promptId, isIn(promptIds))
                .build()
                .render(RenderingStrategies.MYBATIS3);
        return promptInfoMapper.selectMany(statement)
                .stream()
                .filter(info -> filterVisibility(info, user))
                .map(this::toInfoTriple)
                .toList();
    }

    @Override
    @LongPeriodCache(keyBy = CACHE_KEY_SPEL_PREFIX + "#p0")
    public Triple<PromptInfo, List<String>, List<String>> details(Long promptId, User user) {
        return promptInfoMapper.selectOne(c -> wrapQueryExpression(c, user.getUserId())
                        .and(Info.promptId, isEqualTo(promptId)))
                .filter(info -> filterVisibility(info, user))
                .map(this::toInfoTriple)
                .orElse(null);
    }

    @Override
    public List<Triple<PromptInfo, List<String>, List<String>>> details(Collection<Long> promptIds, User user) {
        return promptInfoMapper.select(c -> wrapQueryExpression(c, user.getUserId())
                        .and(Info.promptId, isIn(promptIds)))
                .stream()
                .filter(info -> filterVisibility(info, user))
                .map(this::toInfoTriple)
                .toList();
    }

    @Override
    public List<Triple<Long, Integer, InteractiveStats>> listVersionsByName(String name, User user) {
        var statement = select(Info.promptId, Info.version)
                .from(Info.table)
                .where(Info.name, isEqualTo(name))
                .and(Info.userId, isEqualTo(user.getUserId()))
                .orderBy(Info.version.descending())
                .build()
                .render(RenderingStrategies.MYBATIS3);

        return promptInfoMapper.selectMany(statement)
                .stream()
                .map(info -> {
                    InteractiveStats stats = interactiveStatsMapper.selectOne(c ->
                                    c.where(InteractiveStatsDynamicSqlSupport.referType, isEqualTo(InfoType.PROMPT.text()))
                                            .and(InteractiveStatsDynamicSqlSupport.referId, isEqualTo(info.getPromptUid())))
                            .orElse(null);
                    return Triple.of(info.getPromptId(), info.getVersion(), stats);
                })
                .toList();
    }

    @Override
    public Long getLatestIdByName(String name, User user) {
        var statement = select(Info.promptId, Info.version)
                .from(Info.table)
                .where(Info.name, isEqualTo(name))
                .and(Info.userId, isEqualTo(user.getUserId()))
                .and(Info.visibility, isNotEqualTo(Visibility.HIDDEN.text()))
                .orderBy(Info.version.descending())
                .limit(1)
                .build()
                .render(RenderingStrategies.MYBATIS3);

        return promptInfoMapper.selectOne(statement).map(PromptInfo::getPromptId).orElse(null);
    }

    @Override
    public Long getLatestIdByUid(String promptUid, User user) {
        var statement = select(Info.promptId, Info.version)
                .from(Info.table)
                .where(Info.promptUid, isEqualTo(promptUid))
                .and(Info.userId, isEqualTo(user.getUserId()))
                .and(Info.visibility, isNotEqualTo(Visibility.HIDDEN.text()))
                .orderBy(Info.version.descending())
                .limit(1)
                .build()
                .render(RenderingStrategies.MYBATIS3);

        return promptInfoMapper.selectOne(statement).map(PromptInfo::getPromptId).orElse(null);
    }

    @Override
    @LongPeriodCacheEvict(keyBy = CACHE_KEY_SPEL_PREFIX + "#p0")
    public Long publish(Long promptId, Visibility visibility, User user) {
        Long publishedInfoId;
        SqlSession session = sqlSessionFactory.openSession();
        try {
            var infoTriple = details(promptId, user);
            if (Objects.isNull(infoTriple)) {
                return null;
            }
            PromptInfo info = infoTriple.getLeft();

            List<Triple<Long, Integer, InteractiveStats>> versionInfoList = listVersionsByName(info.getName(), user);
            Triple<Long, Integer, InteractiveStats> versionInfo =
                    CollectionUtils.isNotEmpty(versionInfoList) ? versionInfoList.getFirst() : null;
            Integer version = Objects.nonNull(versionInfo) ? versionInfo.getMiddle() : info.getVersion();

            info.setVisibility(visibility.text());
            info.setPromptId(null);
            info.setVersion(version + 1);

            if (StringUtils.isNotBlank(info.getDraft())) {
                info.setTemplate(PromptUtils.getDraftTemplate(info.getDraft()));
                info.setDraft(null);
            }

            doCreate(Triple.of(infoTriple.getLeft(), null, null));
            publishedInfoId = info.getPromptId();

            final PromptInfo updatedInfo = info;
            List<String> cacheKeys = new LinkedList<>();
            for (var prevVersionInfo : versionInfoList) {
                info = new PromptInfo()
                        .withPromptId(prevVersionInfo.getLeft())
                        .withVisibility(Visibility.HIDDEN.text())
                        .withGmtModified(new Date());
                promptInfoMapper.updateByPrimaryKeySelective(info);
            }

            CacheUtils.longPeriodCacheEvict(cacheKeys);
            session.commit();
        } catch (Exception e) {
            log.error("Failed to publish prompt", e);
            publishedInfoId = null;
            session.rollback();
        } finally {
            session.close();
        }
        return publishedInfoId;
    }

    @Override
    @LongPeriodCache
    public String getOwner(Long promptId) {
        var statement = select(Info.userId)
                .from(Info.table)
                .where(Info.promptId, isEqualTo(promptId))
                .build()
                .render(RenderingStrategies.MYBATIS3);

        return promptInfoMapper.selectOne(statement).map(PromptInfo::getUserId).orElse(null);
    }

    @Override
    @LongPeriodCache
    public String getOwnerByUid(String promptUid) {
        var statement = select(Info.userId)
                .from(Info.table)
                .where(Info.promptUid, isEqualTo(promptUid))
                .limit(1)
                .build()
                .render(RenderingStrategies.MYBATIS3);

        return promptInfoMapper.selectOne(statement).map(PromptInfo::getUserId).orElse(null);
    }

    @Override
    @LongPeriodCache
    public String getUid(Long promptId) {
        var statement = select(Info.promptUid)
                .from(Info.table)
                .where(Info.promptId, isEqualTo(promptId))
                .build()
                .render(RenderingStrategies.MYBATIS3);

        return promptInfoMapper.selectOne(statement).map(PromptInfo::getPromptUid).orElse(null);
    }

    @Override
    public String apply(String promptTemplate, Map<String, Object> variables, PromptFormat format) {
        if (StringUtils.isBlank(promptTemplate) || Objects.isNull(variables)) {
            return promptTemplate;
        }

        return switch (format) {
            case MUSTACHE -> PromptTemplate.from(promptTemplate).apply(variables).text();
            case F_STRING -> FStringPromptTemplate.from(promptTemplate).apply(variables).text();
        };
    }

    private AiMessage apply(AiMessage original, Map<String, Object> variables, PromptFormat format) {
        String text = original.text();
        if (StringUtils.isBlank(text)) {
            return original;
        }
        return AiMessage.from(apply(text, variables, format));
    }

    private SystemMessage apply(SystemMessage original, Map<String, Object> variables, PromptFormat format) {
        String text = original.text();
        if (StringUtils.isBlank(text)) {
            return original;
        }
        return SystemMessage.from(apply(text, variables, format));
    }

    private UserMessage apply(UserMessage original, Map<String, Object> variables, PromptFormat format) {
        List<Content> contents = original.contents().stream()
                .map(content -> content.type() == TEXT ?
                        TextContent.from(apply(((TextContent) content).text(), variables, format)) :
                        content)
                .toList();

        return StringUtils.isBlank(original.name()) ?
                UserMessage.from(contents) :
                UserMessage.from(original.name(), contents);
    }

    @Override
    public ChatMessage apply(ChatMessage original, Map<String, Object> variables, PromptFormat format) {
        return switch (original.type()) {
            case SYSTEM -> apply((SystemMessage) original, variables, format);
            case AI -> apply((AiMessage) original, variables, format);
            case USER -> apply((UserMessage) original, variables, format);
            default -> original;
        };
    }

    @Override
    public ChatPromptContent apply(ChatPromptContent promptContent, Map<String, Object> variables, PromptFormat format) {
        ChatPromptContent applied = new ChatPromptContent();
        applied.setSystem(apply(promptContent.getSystem(), variables, format));

        if (hasValidUserMessagePrompt(promptContent, format)) {
            UserMessage original = promptContent.getMessageToSend();
            applied.setMessageToSend(apply(original, variables, format));
        } else {
            Pair<String, String> inputs = PromptUtils.getDefaultInput(variables);
            UserMessage messageToSend;
            if (Objects.nonNull(inputs.getLeft())) {
                String textInput = inputs.getLeft();
                if (Objects.nonNull(inputs.getRight())) {
                    Pair<String, String> imageInfo = PromptUtils.parseDataMimeType(inputs.getRight());
                    ImageContent imageContent = StringUtils.isBlank(imageInfo.getRight()) ?
                            ImageContent.from(imageInfo.getLeft()) :
                            ImageContent.from(imageInfo.getLeft(), imageInfo.getRight());
                    messageToSend = UserMessage.from(List.of(TextContent.from(textInput), imageContent));
                } else {
                    messageToSend = UserMessage.from(textInput);
                }

                applied.setMessageToSend(messageToSend);
            }
        }

        if (CollectionUtils.isNotEmpty(promptContent.getMessages())) {
            List<ChatMessage> messages = promptContent.getMessages().stream()
                    .map(original -> apply(original, variables, format))
                    .toList();
            applied.setMessages(messages);
        }

        return applied;
    }

    @Override
    public Pair<String, PromptType> apply(Long promptId, Map<String, Object> variables, Boolean draft) {
        PromptInfo promptInfo = promptInfoMapper.selectOne(c -> c.where(Info.promptId, isEqualTo(promptId)))
                .orElse(null);
        if (Objects.isNull(promptInfo)) {
            return null;
        }

        if (StringUtils.isNotBlank(promptInfo.getInputs())) {
            try {
                Map<String, Object> inputs = InfoUtils.defaultMapper().readValue(
                        promptInfo.getInputs(), new TypeReference<>() {});
                for (Map.Entry<String, Object> input : inputs.entrySet()) {
                    String key = input.getKey();
                    Object value = input.getValue();
                    if (variables.containsKey(key) ||
                            Objects.isNull(value) ||
                            (value instanceof String strValue && StringUtils.isBlank(strValue))) {
                        continue;
                    }
                    variables.put(input.getKey(), value);
                }
            } catch (JsonProcessingException e) {
                log.warn("Failed to parse inputs from prompt {}", promptId);
            }

        }
        String promptTemplate = BooleanUtils.isTrue(draft) && StringUtils.isNotBlank(promptInfo.getDraft()) ?
                PromptUtils.getDraftTemplate(promptInfo.getDraft()) : promptInfo.getTemplate();
        PromptFormat format = PromptFormat.of(promptInfo.getFormat());
        PromptType type = PromptType.of(promptInfo.getType());
        if (type == PromptType.CHAT) {
            try {
                ChatPromptContent promptContent =
                        InfoUtils.defaultMapper().readValue(promptTemplate, ChatPromptContent.class);
                ChatPromptContent applied = apply(promptContent, variables, format);
                return Pair.of(InfoUtils.defaultMapper().writeValueAsString(applied), type);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } else {
            return Pair.of(apply(promptTemplate, variables, format), type);
        }
    }

    @Override
    public boolean existsName(String name, User user) {
        var statement = select(Info.name)
                .from(Info.table)
                .where(Info.name, isEqualTo(name))
                .and(Info.userId, isEqualTo(user.getUserId()))
                .limit(1)
                .build()
                .render(RenderingStrategies.MYBATIS3);

        return !promptInfoMapper.selectMany(statement).isEmpty();
    }

    private boolean hasValidUserMessagePrompt(ChatPromptContent promptContent, PromptFormat format) {
        if (Objects.nonNull(promptContent.getMessageToSend())) {
            String textTemplate = PromptUtils.toSingleText(promptContent.getMessageToSend());
            return switch (format) {
                case F_STRING -> !textTemplate.equals("{input}");
                case MUSTACHE -> !textTemplate.equals("{{input}}");
                case null -> false;
            };
        }
        return false;
    }

    private static class Info {
        public static final PromptInfoDynamicSqlSupport.PromptInfo table = PromptInfoDynamicSqlSupport.promptInfo;
        public static final SqlColumn<Long> promptId = PromptInfoDynamicSqlSupport.promptId;
        public static final SqlColumn<String> promptUid = PromptInfoDynamicSqlSupport.promptUid;
        public static final SqlColumn<Date> gmtCreate = PromptInfoDynamicSqlSupport.gmtCreate;
        public static final SqlColumn<Date> gmtModified = PromptInfoDynamicSqlSupport.gmtModified;
        public static final SqlColumn<String> userId = PromptInfoDynamicSqlSupport.userId;
        public static final SqlColumn<String> parentUid = PromptInfoDynamicSqlSupport.parentUid;
        public static final SqlColumn<String> visibility = PromptInfoDynamicSqlSupport.visibility;
        public static final SqlColumn<Integer> version = PromptInfoDynamicSqlSupport.version;
        public static final SqlColumn<String> name = PromptInfoDynamicSqlSupport.name;
        public static final SqlColumn<String> type = PromptInfoDynamicSqlSupport.type;
        public static final SqlColumn<String> description = PromptInfoDynamicSqlSupport.description;
        public static final SqlColumn<String> template = PromptInfoDynamicSqlSupport.template;
        public static final SqlColumn<String> format = PromptInfoDynamicSqlSupport.format;
        public static final SqlColumn<String> lang = PromptInfoDynamicSqlSupport.lang;
        public static final SqlColumn<String> example = PromptInfoDynamicSqlSupport.example;
        public static final SqlColumn<String> inputs = PromptInfoDynamicSqlSupport.inputs;
        public static final SqlColumn<String> ext = PromptInfoDynamicSqlSupport.ext;

        public static List<BasicColumn> summaryColumns() {
            return List.of(
                    Info.gmtCreate,
                    Info.gmtModified,
                    Info.userId,
                    Info.promptId,
                    Info.promptUid,
                    Info.visibility,
                    Info.version,
                    Info.name,
                    Info.type,
                    Info.description,
                    Info.format,
                    Info.lang);
        }
    }
}
