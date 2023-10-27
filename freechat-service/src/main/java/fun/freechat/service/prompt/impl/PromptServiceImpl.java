package fun.freechat.service.prompt.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.langchain4j.model.input.PromptTemplate;
import fun.freechat.langchain4j.model.input.FStringPromptTemplate;
import fun.freechat.mapper.*;
import fun.freechat.model.*;
import fun.freechat.service.ai.message.ChatMessage;
import fun.freechat.service.ai.message.ChatPromptContent;
import fun.freechat.service.cache.LongPeriodCache;
import fun.freechat.service.cache.LongPeriodCacheEvict;
import fun.freechat.service.enums.InfoType;
import fun.freechat.service.enums.PromptFormat;
import fun.freechat.service.enums.PromptType;
import fun.freechat.service.enums.Visibility;
import fun.freechat.service.organization.OrgService;
import fun.freechat.service.prompt.PromptService;
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
import org.apache.poi.util.StringUtil;
import org.mybatis.dynamic.sql.BasicColumn;
import org.mybatis.dynamic.sql.SortSpecification;
import org.mybatis.dynamic.sql.SqlBuilder;
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

    @Autowired
    private InteractiveStatsScoreDetailsMapper interactiveStatsScoreDetailsMapper;

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
        if (CollectionUtils.isNotEmpty(matchTags) && Objects.nonNull(and) && and) {
            //noinspection SlowListContainsAll
            return triple.getMiddle().containsAll(matchTags);
        }
        return true;
    }

    private boolean filterAiModels(Triple<PromptInfo, List<String>, List<String>> triple, Query query) {
        List<String> matchAiModels = query.getWhere().getAiModels();
        Boolean and = query.getWhere().getAiModelsAnd();
        if (CollectionUtils.isNotEmpty(matchAiModels) && Objects.nonNull(and) && and) {
            //noinspection SlowListContainsAll
            return triple.getRight().containsAll(matchAiModels);
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
                                .and(TagDynamicSqlSupport.referId, isEqualTo(info.getPromptId())))
                .stream()
                .map(Tag::getContent)
                .toList();
        List<String> aiModels = aiModelMapper.select(c ->
                        c.where(AiModelDynamicSqlSupport.referType, isEqualTo(InfoType.PROMPT.text()))
                                .and(AiModelDynamicSqlSupport.referId, isEqualTo(info.getPromptId())))
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
            default -> null;
        };
    }

    private Pair<Integer, InteractiveStats> getLastVersionAndStatsByName(String name, User user) {
        var statement = select(Info.promptId, Info.version)
                .from(Info.table)
                .where(Info.name, isEqualTo(name))
                .and(Info.userId, isEqualTo(user.getUserId()))
                .orderBy(Info.version.descending())
                .limit(1)
                .build()
                .render(RenderingStrategies.MYBATIS3);

        return promptInfoMapper.selectOne(statement)
                .map(info -> {
                    InteractiveStats stats = interactiveStatsMapper.selectOne(c ->
                                    c.where(InteractiveStatsDynamicSqlSupport.referType, isEqualTo(InfoType.PROMPT.text()))
                                            .and(InteractiveStatsDynamicSqlSupport.referId, isEqualTo(info.getPromptId())))
                            .orElse(null);
                    return Pair.of(info.getVersion(), stats);
                })
                .orElse(null);
    }

    private void doCreate(Triple<PromptInfo, List<String>, List<String>> infoTriple) {
        PromptInfo info = infoTriple.getLeft();
        Date now = new Date();
        int rows = promptInfoMapper.insertSelective(info
                .withGmtCreate(now)
                .withGmtModified(now)
                .withPromptId(IdUtils.newId()));
        if (rows <= 0) {
            info.setPromptId(null);
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
                        .withReferId(info.getPromptId()));
                if (rows <= 0) {
                    info.setPromptId(null);
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
                        .withReferId(info.getPromptId()));
                if (rows <= 0) {
                    info.setPromptId(null);
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
                    .on(Info.promptId, equalTo(TagDynamicSqlSupport.referId));

        }
        List<String> modelIds = InfoUtils.trimListElements(query.getWhere().getAiModels());
        if (CollectionUtils.isNotEmpty(modelIds)) {
            table.leftJoin(AiModelDynamicSqlSupport.aiModel, "m")
                    .on(Info.promptId, equalTo(AiModelDynamicSqlSupport.referId));
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
        // tags
        if (CollectionUtils.isNotEmpty(tags)) {
            conditions.and(TagDynamicSqlSupport.referType, isEqualTo(InfoType.PROMPT.text()))
                    .and(TagDynamicSqlSupport.content, isIn(tags));
        }
        // model ids.
        if (CollectionUtils.isNotEmpty(modelIds)) {
            conditions.and(AiModelDynamicSqlSupport.referType, isEqualTo(InfoType.PROMPT.text()))
                    .and(AiModelDynamicSqlSupport.modelId, isIn(modelIds));
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
        // text
        String commonText = query.getWhere().getText();
        if (StringUtils.isNotBlank(commonText)) {
            var commonTextCondition = isLike(commonText).map(s -> "%" + commonText + "%");
            conditions.and(Info.name, commonTextCondition,
                    or(Info.description, commonTextCondition),
                    or(Info.template, commonTextCondition),
                    or(Info.example, commonTextCondition));
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
            orderByFields.add(SortSpecificationWrapper.of("p", orderByField));
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
        var fields = selectDistinct(Info.summaryColumns());
        return doSearch(query, user, fields);
    }

    @Override
    public List<Triple<PromptInfo, List<String>, List<String>>> searchDetails(Query query, User user) {
        var fields = selectDistinct(Info.table.allColumns());
        return doSearch(query, user, fields);
    }

    @Override
    public long count(Query query, User user) {
        var fields = selectDistinct(SqlBuilder.count());
        var statement = getSelectStatement(query, user, fields);
        return promptInfoMapper.count(statement.getLeft());
    }


    @Override
    public boolean create(Triple<PromptInfo, List<String>, List<String>> infoTriple) {
        SqlSession session = sqlSessionFactory.openSession();
        try {
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
    public List<String> create(List<Triple<PromptInfo, List<String>, List<String>>> promptInfoList) {
        SqlSession session = sqlSessionFactory.openSession();
        LinkedList<String> promptIds = new LinkedList<>();
        try {
            for (Triple<PromptInfo, List<String>, List<String>> infoTriple : promptInfoList) {
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
                tagMapper.delete(c -> c.where(TagDynamicSqlSupport.referId, isEqualTo(promptInfo.getPromptId()))
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
                            .withReferId(promptInfo.getPromptId()));
                    if (rows <= 0) {
                        promptInfo.setPromptId(null);
                        throw new RuntimeException("Update tag " + tagText + " failed!");
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(infoTriple.getRight())) {
                aiModelMapper.delete(c -> c.where(AiModelDynamicSqlSupport.referId, isEqualTo(promptInfo.getPromptId()))
                        .and(AiModelDynamicSqlSupport.referType, isEqualTo(InfoType.PROMPT.text())));
                Set<String> aiModelSet = new HashSet<>(infoTriple.getRight());
                for (String aiModelId : aiModelSet) {
                    rows = aiModelMapper.insert(new AiModel()
                            .withGmtCreate(now)
                            .withGmtModified(now)
                            .withModelId(aiModelId)
                            .withReferType(InfoType.PROMPT.text())
                            .withReferId(promptInfo.getPromptId()));
                    if (rows <= 0) {
                        promptInfo.setPromptId(null);
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
    public boolean hide(String promptId, User user) {
        if (StringUtils.isBlank(promptId)) {
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
    public boolean delete(String promptId, User user) {
        if (StringUtils.isBlank(promptId)) {
            return false;
        }
        int rows = promptInfoMapper.delete(c -> c.where(Info.promptId, isEqualTo(promptId))
                    .and(Info.userId, isEqualTo(user.getUserId())));
        if (rows > 0) {
            tagMapper.delete(c -> c.where(TagDynamicSqlSupport.referId, isEqualTo(promptId))
                    .and(TagDynamicSqlSupport.referType, isEqualTo(InfoType.PROMPT.text())));
            aiModelMapper.delete(c -> c.where(AiModelDynamicSqlSupport.referId, isEqualTo(promptId))
                    .and(AiModelDynamicSqlSupport.referType, isEqualTo(InfoType.PROMPT.text())));
            interactiveStatsMapper.delete(c ->
                    c.where(InteractiveStatsDynamicSqlSupport.referId, isEqualTo(promptId))
                            .and(InteractiveStatsDynamicSqlSupport.referType, isEqualTo(InfoType.PROMPT.text())));
            interactiveStatsScoreDetailsMapper.delete(c ->
                    c.where(InteractiveStatsScoreDetailsDynamicSqlSupport.referId, isEqualTo(promptId))
                            .and(InteractiveStatsScoreDetailsDynamicSqlSupport.referType, isEqualTo(InfoType.PROMPT.text())));
        }
        return rows > 0;
    }

    @Override
    public List<String> delete(List<String> promptIds, User user) {
        LinkedList<String> deletedIds = new LinkedList<>();
        SqlSession session = sqlSessionFactory.openSession();
        try {
            for (String promptId : promptIds) {
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
    public List<String> delete(User user) {
        var statement = select(Info.promptId)
                .from(Info.table)
                .where(Info.userId, isEqualTo(user.getUserId()))
                .build()
                .render(RenderingStrategies.MYBATIS3);
        List<String> ids = promptInfoMapper.selectMany(statement)
                .stream()
                .map(PromptInfo::getPromptId)
                .toList();

        return delete(ids, user);
    }

    @Override
    public Triple<PromptInfo, List<String>, List<String>> summary(String promptId, User user) {
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
    public List<Triple<PromptInfo, List<String>, List<String>>> summary(Collection<String> promptIds, User user) {
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
    public Triple<PromptInfo, List<String>, List<String>> details(String promptId, User user) {
        return promptInfoMapper.selectOne(c -> wrapQueryExpression(c, user.getUserId())
                        .and(Info.promptId, isEqualTo(promptId)))
                .filter(info -> filterVisibility(info, user))
                .map(this::toInfoTriple)
                .orElse(null);
    }

    @Override
    public List<Triple<PromptInfo, List<String>, List<String>>> details(Collection<String> promptIds, User user) {
        return promptInfoMapper.select(c -> wrapQueryExpression(c, user.getUserId())
                        .and(Info.promptId, isIn(promptIds)))
                .stream()
                .filter(info -> filterVisibility(info, user))
                .map(this::toInfoTriple)
                .toList();
    }

    @Override
    public List<Triple<String, Integer, InteractiveStats>> listVersionsByName(String name, User user) {
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
                                            .and(InteractiveStatsDynamicSqlSupport.referId, isEqualTo(info.getPromptId())))
                            .orElse(null);
                    return Triple.of(info.getPromptId(), info.getVersion(), stats);
                })
                .toList();
    }

    @Override
    public String getLatestIdByName(String name, User user) {
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
    @LongPeriodCacheEvict(keyBy = CACHE_KEY_SPEL_PREFIX + "#p0")
    public String publish(String promptId, Visibility visibility, User user) {
        String publishedInfoId;
        SqlSession session = sqlSessionFactory.openSession();
        try {
            var infoTriple = details(promptId, user);
            if (Objects.isNull(infoTriple)) {
                return null;
            }
            PromptInfo info = infoTriple.getLeft();

            Pair<Integer, InteractiveStats> versionInfo = getLastVersionAndStatsByName(info.getName(), user);
            Integer version = Objects.nonNull(versionInfo) ? versionInfo.getLeft() : info.getVersion();

            info.setVisibility(visibility.text());
            info.setPromptId(null);
            info.setVersion(version + 1);

            if (StringUtil.isNotBlank(info.getDraft())) {
                info.setTemplate(info.getDraft());
                info.setDraft(null);
            }

            doCreate(infoTriple);
            publishedInfoId = info.getPromptId();

            info = new PromptInfo()
                    .withPromptId(promptId)
                    .withVisibility(Visibility.HIDDEN.text())
                    .withGmtModified(new Date());

            promptInfoMapper.updateByPrimaryKeySelective(info);

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
    public String getOwner(String promptId) {
        var statement = select(Info.userId)
                .from(Info.table)
                .where(Info.promptId, isEqualTo(promptId))
                .build()
                .render(RenderingStrategies.MYBATIS3);

        return promptInfoMapper.selectOne(statement).map(PromptInfo::getUserId).orElse(null);
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

    @Override
    public ChatMessage apply(ChatMessage original, Map<String, Object> variables, PromptFormat format) {
        ChatMessage message = new ChatMessage();
        message.setRole(original.getRole());
        message.setName(original.getName());
        message.setFunctionCall(original.getFunctionCall());
        message.setContent(apply(original.getContent(), variables, format));
        message.setGmtCreate(original.getGmtCreate());
        return message;
    }

    @Override
    public ChatPromptContent apply(ChatPromptContent promptContent, Map<String, Object> variables, PromptFormat format) {
        ChatPromptContent applied = new ChatPromptContent();
        applied.setFormat(format.text());
        applied.setSystem(apply(promptContent.getSystem(), variables, format));

        if (Objects.nonNull(promptContent.getMessagesToSend())) {
            ChatMessage original = promptContent.getMessagesToSend();
            applied.setMessagesToSend(apply(original, variables, format));
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
    public Pair<String, PromptType> apply(String promptId, Map<String, Object> variables, Boolean draft) {
        PromptInfo promptInfo = promptInfoMapper.selectOne(c -> c.where(Info.promptId, isEqualTo(promptId)))
                .orElse(null);
        if (Objects.isNull(promptInfo)) {
            return null;
        }

        String promptTemplate = draft ? promptInfo.getDraft() : promptInfo.getTemplate();
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

    private static class Info {
        public static final PromptInfoDynamicSqlSupport.PromptInfo table = PromptInfoDynamicSqlSupport.promptInfo;
        public static final SqlColumn<String> promptId = PromptInfoDynamicSqlSupport.promptId;
        public static final SqlColumn<Date> gmtCreate = PromptInfoDynamicSqlSupport.gmtCreate;
        public static final SqlColumn<Date> gmtModified = PromptInfoDynamicSqlSupport.gmtModified;
        public static final SqlColumn<String> userId = PromptInfoDynamicSqlSupport.userId;
        public static final SqlColumn<String> parentId = PromptInfoDynamicSqlSupport.parentId;
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
