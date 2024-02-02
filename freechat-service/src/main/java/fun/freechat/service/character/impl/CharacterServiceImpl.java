package fun.freechat.service.character.impl;

import fun.freechat.mapper.*;
import fun.freechat.model.*;
import fun.freechat.service.cache.LongPeriodCache;
import fun.freechat.service.cache.LongPeriodCacheEvict;
import fun.freechat.service.character.CharacterBackendEvent;
import fun.freechat.service.character.CharacterInfoDraft;
import fun.freechat.service.character.CharacterService;
import fun.freechat.service.enums.InfoType;
import fun.freechat.service.enums.StatsType;
import fun.freechat.service.enums.Visibility;
import fun.freechat.service.organization.OrgService;
import fun.freechat.service.util.CacheUtils;
import fun.freechat.service.util.CharacterUtils;
import fun.freechat.service.util.InfoUtils;
import fun.freechat.service.util.SortSpecificationWrapper;
import fun.freechat.util.IdUtils;
import fun.freechat.util.PojoUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.dynamic.sql.BasicColumn;
import org.mybatis.dynamic.sql.SortSpecification;
import org.mybatis.dynamic.sql.SqlBuilder;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.select.QueryExpressionDSL;
import org.mybatis.dynamic.sql.select.SelectModel;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Stream;

import static fun.freechat.service.util.CacheUtils.LONG_PERIOD_CACHE_NAME;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

@Service
@Slf4j
@SuppressWarnings({"unused", "rawtypes"})
public class CharacterServiceImpl implements CharacterService {
    final static String CACHE_KEY_PREFIX = "CharacterService_";

    final static String CACHE_KEY_SPEL_PREFIX = "'" + CACHE_KEY_PREFIX + "_character_' + ";

    final static String BACKEND_CACHE_KEY_SPEL_PREFIX = "'" + CACHE_KEY_PREFIX + "_backend_' + ";

    final static String DEFAULT_BACKEND_CACHE_KEY_SPEL_PREFIX = "'" + CACHE_KEY_PREFIX + "_default_backend_' + ";

    final static String BACKEND_CHARACTER_ID_CACHE_KEY_PREFIX = CACHE_KEY_PREFIX + "_backend_character_id_";

    final static String BACKEND_CHARACTER_ID_CACHE_KEY_SPEL_PREFIX = "'" + BACKEND_CHARACTER_ID_CACHE_KEY_PREFIX + "' + ";

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Autowired
    private CharacterInfoMapper characterInfoMapper;

    @Autowired
    private CharacterBackendMapper characterBackendMapper;

    @Autowired
    private TagMapper tagMapper;

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

    private boolean filterVisibility(CharacterInfo info, User user) {
        Visibility visibility = Visibility.of(info.getVisibility());
        return switch (visibility) {
            case PUBLIC -> true;
            case PUBLIC_ORG -> orgService.hasRelationship(user.getUserId(), info.getUserId());
            case PRIVATE, HIDDEN -> user.getUserId().equals(info.getUserId());
        };
    }

    private boolean filterTags(Pair<CharacterInfo, List<String>> pair, CharacterService.Query query) {
        List<String> matchTags = query.getWhere().getTags();
        Boolean and = query.getWhere().getTagsAnd();
        if (CollectionUtils.isNotEmpty(matchTags)) {
            if (Objects.nonNull(and) && and) {
                //noinspection SlowListContainsAll
                return pair.getRight().containsAll(matchTags);
            } else {
                for (String matchTag : matchTags) {
                    if (pair.getRight().contains(matchTag)) {
                        return true;
                    }
                }
                return false;
            }
        }
        return true;
    }

    private void handleDraft(CharacterInfo info, boolean hasDraft) {
        if (!hasDraft) {
            info.setDraft(null);
        }
    }

    private Pair<CharacterInfo, List<String>> toInfoPair(CharacterInfo info) {
        if (Objects.isNull(info)) {
            return null;
        }
        List<String> tags = tagMapper.select(c ->
                        c.where(TagDynamicSqlSupport.referType, isEqualTo(InfoType.CHARACTER.text()))
                                .and(TagDynamicSqlSupport.referId, isEqualTo(info.getCharacterId())))
                .stream()
                .map(Tag::getContent)
                .toList();

        return Pair.of(info, tags);
    }

    private Triple<CharacterInfo, List<String>, List<CharacterBackend>> toInfoTriple(
            Pair<CharacterInfo, List<String>> infoPair) {
        if (Objects.isNull(infoPair)) {
            return null;
        }
        List<CharacterBackend> backends = characterBackendMapper.select(c ->
                        c.where(CharacterBackendDynamicSqlSupport.characterId,
                                isEqualTo(infoPair.getLeft().getCharacterId())));

        return Triple.of(infoPair.getLeft(), infoPair.getRight(), backends);
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

    private void overrideByDraft(CharacterInfoDraft draft, CharacterInfo info) {
        if (Objects.isNull(draft)) {
            return;
        }
        PojoUtils.mapWhenExists(draft::getChatExample, info::setChatExample);
        PojoUtils.mapWhenExists(draft::getChatStyle, info::setChatStyle);
        PojoUtils.mapWhenExists(draft::getGreeting, info::setGreeting);
        PojoUtils.mapWhenExists(draft::getProfile, info::setProfile);
    }

    private void doCreate(Pair<CharacterInfo, List<String>> infoPair) {
        CharacterInfo info = infoPair.getLeft();
        Date now = new Date();
        int rows = characterInfoMapper.insertSelective(info
                .withGmtCreate(now)
                .withGmtModified(now)
                .withCharacterId(IdUtils.newId()));
        if (rows <= 0) {
            info.setCharacterId(null);
            throw new RuntimeException("Insert character " + info.getName() + " failed!");
        }
        if (CollectionUtils.isNotEmpty(infoPair.getRight())) {
            Set<String> tagSet = new HashSet<>(infoPair.getRight());
            for (String tagText : tagSet) {
                if (StringUtils.isBlank(tagText)) {
                    continue;
                }
                rows = tagMapper.insert(new Tag()
                        .withGmtCreate(now)
                        .withGmtModified(now)
                        .withContent(tagText)
                        .withUserId(info.getUserId())
                        .withReferType(InfoType.CHARACTER.text())
                        .withReferId(info.getCharacterId()));
                if (rows <= 0) {
                    info.setCharacterId(null);
                    throw new RuntimeException("Insert tag " + tagText + " failed!");
                }
            }
        }
    }

    private Pair<SelectStatementProvider, Boolean> getSelectStatement(
            Query query, User user, QueryExpressionDSL.FromGatherer<SelectModel> fields) {
        // join
        var table = fields.from(Info.table, "c");
        List<String> tags = InfoUtils.trimListElements(query.getWhere().getTags());
        if (CollectionUtils.isNotEmpty(tags)) {
            table.leftJoin(TagDynamicSqlSupport.tag, "t")
                    .on(Info.characterId, equalTo(TagDynamicSqlSupport.referId));

        }
        List<String> orderByStats =  new LinkedList<>(InfoUtils.trimListElements(query.getOrderBy()));
        orderByStats.retainAll(StatsType.fieldNames());
        if (!orderByStats.isEmpty()) {
            table.leftJoin(InteractiveStatsDynamicSqlSupport.interactiveStats, "i")
                    .on(Info.characterId, equalTo((InteractiveStatsDynamicSqlSupport.referId)));
        }

        // conditions
        var conditions = table.where(Info.priority, isGreaterThan(0));
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
        // lang
        conditions.and(Info.lang,
                isEqualTo(query.getWhere().getLang()).filter(StringUtils::isNotBlank));
        // text
        String commonText = query.getWhere().getText();
        if (StringUtils.isNotBlank(commonText)) {
            var commonTextCondition = isLike(commonText).map(s -> "%" + s + "%");
            conditions.and(Info.name, commonTextCondition,
                    or(Info.description, commonTextCondition),
                    or(Info.profile, commonTextCondition),
                    or(Info.chatStyle, commonTextCondition));
        }
        // tags
        if (CollectionUtils.isNotEmpty(tags)) {
            conditions.and(TagDynamicSqlSupport.content, isIn(tags));
        }

        // order by
        LinkedList<SortSpecification> orderByFields = new LinkedList<>();
        orderByFields.add(Info.priority.descending());
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
                    orderByStats.contains(orderBy) ? "i" : "c", orderByField));
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

    private Stream<Pair<CharacterInfo, List<String>>> doSearch(
            Query query, User user, QueryExpressionDSL.FromGatherer<SelectModel> fields) {
        var statement = getSelectStatement(query, user, fields);
        return characterInfoMapper.selectMany(statement.getLeft())
                .stream()
                .filter(info -> filterVisibility(info, user))
                .peek(info -> handleDraft(info, statement.getRight()))
                .map(this::toInfoPair)
                .filter(pair -> filterTags(pair, query));
    }

    private List<Pair<CharacterInfo, List<String>>> doSearchSummaries(
            Query query, User user, QueryExpressionDSL.FromGatherer<SelectModel> fields) {
        return doSearch(query, user, fields).toList();
    }

    private List<Triple<CharacterInfo, List<String>, List<CharacterBackend>>> doSearchDetails(
            Query query, User user, QueryExpressionDSL.FromGatherer<SelectModel> fields) {
        return doSearch(query, user, fields).map(this::toInfoTriple).toList();
    }

    @Override
    public List<Pair<CharacterInfo, List<String>>> search(Query query, User user) {
        /*
select distinct c.user_id, c.character_id, c.visibility... \
  from character_info c \
  left join tag t on c.character_id = t.refer_id \
  where t.refer_type = 'character' \
  and ((c.visibility = 'public' and c.user_id = '{userId}') or c.user_id = '{me}') \
  and t.content in '{tags}' \
  and c.name like '{name}%' \
  and c.lang = '{lang}' \
  and (c.name like '%{text}%' or \
    c.description like '%{text}%' or \
    c.profile like '%{text}%' or \
    c.chat_style like '%{text}%' \
  ) \
  order by c.{orderBy[0]}, p.{orderBy[1]}... \
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
        return doSearchSummaries(query, user, fields);
    }

    @Override
    public List<Triple<CharacterInfo, List<String>, List<CharacterBackend>>> searchDetails(Query query, User user) {
        var fields = selectDistinct(Info.table.allColumns());
        return doSearchDetails(query, user, fields);
    }

    @Override
    public long count(Query query, User user) {
        query.setLimit(null);
        query.setOffset(null);
        var fields = select(SqlBuilder.countDistinct(Info.characterId));
        var statement = getSelectStatement(query, user, fields);
        return characterInfoMapper.count(statement.getLeft());
    }

    @Override
    public boolean create(Pair<CharacterInfo, List<String>> infoPair) {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            doCreate(infoPair);
            session.commit();
            return true;
        } catch (Exception e) {
            log.error("Failed to create character for {}", infoPair.getLeft().getName(), e);
            session.rollback();
        } finally {
            session.close();
        }
        return false;
    }

    @Override
    @LongPeriodCacheEvict(keyBy = CACHE_KEY_SPEL_PREFIX + "#p0.getLeft().characterId")
    public boolean update(Pair<CharacterInfo, List<String>> infoPair) {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            Date now = new Date();
            CharacterInfo characterInfo = infoPair.getLeft();
            characterInfoMapper.updateByPrimaryKeySelective(characterInfo.withGmtModified(now));
            int rows;
            if (CollectionUtils.isNotEmpty(infoPair.getRight())) {
                tagMapper.delete(c -> c.where(TagDynamicSqlSupport.referId, isEqualTo(characterInfo.getCharacterId()))
                        .and(TagDynamicSqlSupport.referType, isEqualTo(InfoType.CHARACTER.text()))
                        .and(TagDynamicSqlSupport.userId, isEqualTo(characterInfo.getUserId())));
                Set<String> tagSet = new HashSet<>(infoPair.getRight());
                for (String tagText : tagSet) {
                    rows = tagMapper.insert(new Tag()
                            .withGmtCreate(now)
                            .withGmtModified(now)
                            .withContent(tagText)
                            .withUserId(characterInfo.getUserId())
                            .withReferType(InfoType.CHARACTER.text())
                            .withReferId(characterInfo.getCharacterId()));
                    if (rows <= 0) {
                        characterInfo.setCharacterId(null);
                        throw new RuntimeException("Update tag " + tagText + " failed!");
                    }
                }
            }
            session.commit();
            return true;
        } catch (Exception e) {
            log.error("Failed to update character for {}", infoPair.getLeft().getName(), e);
            session.rollback();
        } finally {
            session.close();
        }
        return false;
    }

    @Override
    @LongPeriodCacheEvict(keyBy = CACHE_KEY_SPEL_PREFIX + "#p0")
    public boolean hide(String characterId, User user) {
        if (StringUtils.isBlank(characterId)) {
            return false;
        }
        CharacterInfo characterInfo = characterInfoMapper.selectOne(c ->
                        c.where(Info.userId, isEqualTo(user.getUserId()))
                                .and(Info.characterId, isEqualTo(characterId)))
                .orElse(null);
        if (Objects.isNull(characterInfo) ||
                Visibility.HIDDEN.text().equals(characterInfo.getVisibility())) {
            return false;
        }
        Date now = new Date();
        characterInfo.withGmtModified(new Date())
                .withVisibility(Visibility.HIDDEN.text());
        int rows = characterInfoMapper.updateByPrimaryKeySelective(characterInfo);
        return rows > 0;
    }

    @Override
    @LongPeriodCacheEvict(keyBy = CACHE_KEY_SPEL_PREFIX + "#p0")
    public boolean delete(String characterId, User user) {
        if (StringUtils.isBlank(characterId)) {
            return false;
        }
        int rows = characterInfoMapper.delete(c -> c.where(Info.characterId, isEqualTo(characterId))
                .and(Info.userId, isEqualTo(user.getUserId())));
        if (rows > 0) {
            characterBackendMapper.delete(c ->
                    c.where(CharacterBackendDynamicSqlSupport.characterId, isEqualTo(characterId)));
            tagMapper.delete(c -> c.where(TagDynamicSqlSupport.referId, isEqualTo(characterId))
                    .and(TagDynamicSqlSupport.referType, isEqualTo(InfoType.CHARACTER.text())));
            interactiveStatsMapper.delete(c ->
                    c.where(InteractiveStatsDynamicSqlSupport.referId, isEqualTo(characterId))
                            .and(InteractiveStatsDynamicSqlSupport.referType, isEqualTo(InfoType.CHARACTER.text())));
            interactiveStatsScoreDetailsMapper.delete(c ->
                    c.where(InteractiveStatsScoreDetailsDynamicSqlSupport.referId, isEqualTo(characterId))
                            .and(InteractiveStatsScoreDetailsDynamicSqlSupport.referType, isEqualTo(InfoType.CHARACTER.text())));
        }
        return rows > 0;
    }

    @Override
    public List<String> delete(User user) {
        var statement = select(Info.characterId)
                .from(Info.table)
                .where(Info.userId, isEqualTo(user.getUserId()))
                .build()
                .render(RenderingStrategies.MYBATIS3);
        List<String> ids = characterInfoMapper.selectMany(statement)
                .stream()
                .map(CharacterInfo::getCharacterId)
                .toList();

        return delete(ids, user);
    }

    @Override
    public List<String> deleteByName(String name, User user) {
        var statement = select(Info.characterId)
                .from(Info.table)
                .where(Info.userId, isEqualTo(user.getUserId()))
                .and(Info.name, isEqualTo(name))
                .build()
                .render(RenderingStrategies.MYBATIS3);
        List<String> ids = characterInfoMapper.selectMany(statement)
                .stream()
                .map(CharacterInfo::getCharacterId)
                .toList();

        return delete(ids, user);
    }

    private List<String> delete(List<String> ids, User user) {
        LinkedList<String> deletedIds = new LinkedList<>();
        SqlSession session = sqlSessionFactory.openSession();
        try {
            for (String id : ids) {
                if (delete(id, user)) {
                    deletedIds.add(id);
                }
            }
            session.commit();
            CacheUtils.longPeriodCacheEvict(deletedIds.stream().map(id -> CACHE_KEY_PREFIX + id).toList());
        } catch (Exception e) {
            log.error("Failed to delete characters", e);
            deletedIds.clear();
            session.rollback();
        } finally {
            session.close();
        }

        return deletedIds;
    }

    @Override
    @LongPeriodCache
    public CharacterInfo summary(String characterId) {
        var statement = select(Info.summaryColumns())
                .from(Info.table)
                .where(Info.characterId, isEqualTo(characterId))
                .build()
                .render(RenderingStrategies.MYBATIS3);
        return characterInfoMapper.selectOne(statement).orElse(null);
    }

    @Override
    public Pair<CharacterInfo, List<String>> summary(String characterId, User user) {
        var fields = select(Info.summaryColumns())
                .from(Info.table);

        var statement = wrapQueryExpression(fields, user.getUserId())
                .and(Info.characterId, isEqualTo(characterId))
                .build()
                .render(RenderingStrategies.MYBATIS3);
        return characterInfoMapper.selectOne(statement)
                .filter(info -> filterVisibility(info, user))
                .map(this::toInfoPair)
                .orElse(null);
    }

    @Override
    public List<Pair<CharacterInfo, List<String>>> summary(Collection<String> characterIds, User user) {
        var fields = select(Info.summaryColumns())
                .from(Info.table);

        var statement = wrapQueryExpression(fields, user.getUserId())
                .and(Info.characterId, isIn(characterIds))
                .build()
                .render(RenderingStrategies.MYBATIS3);
        return characterInfoMapper.selectMany(statement)
                .stream()
                .filter(info -> filterVisibility(info, user))
                .map(this::toInfoPair)
                .toList();
    }

    @Override
    @LongPeriodCache(keyBy = CACHE_KEY_SPEL_PREFIX + "#p0")
    public Triple<CharacterInfo, List<String>, List<CharacterBackend>> details(String characterId, User user) {
        return characterInfoMapper.selectOne(c -> wrapQueryExpression(c, user.getUserId())
                        .and(Info.characterId, isEqualTo(characterId)))
                .filter(info -> filterVisibility(info, user))
                .map(this::toInfoPair)
                .map(this::toInfoTriple)
                .orElse(null);
    }

    @Override
    public List<Triple<CharacterInfo, List<String>, List<CharacterBackend>>> details(Collection<String> characterIds, User user) {
        return characterInfoMapper.select(c -> wrapQueryExpression(c, user.getUserId())
                        .and(Info.characterId, isIn(characterIds)))
                .stream()
                .filter(info -> filterVisibility(info, user))
                .map(this::toInfoPair)
                .map(this::toInfoTriple)
                .toList();
    }

    @Override
    public List<Triple<String, Integer, InteractiveStats>> listVersionsByName(String name, User user) {
        var statement = select(Info.characterId, Info.version)
                .from(Info.table)
                .where(Info.name, isEqualTo(name))
                .and(Info.userId, isEqualTo(user.getUserId()))
                .orderBy(Info.version.descending())
                .build()
                .render(RenderingStrategies.MYBATIS3);

        return characterInfoMapper.selectMany(statement)
                .stream()
                .map(info -> {
                    InteractiveStats stats = interactiveStatsMapper.selectOne(c ->
                                    c.where(InteractiveStatsDynamicSqlSupport.referType, isEqualTo(InfoType.CHARACTER.text()))
                                            .and(InteractiveStatsDynamicSqlSupport.referId, isEqualTo(info.getCharacterId())))
                            .orElse(null);
                    return Triple.of(info.getCharacterId(), info.getVersion(), stats);
                })
                .toList();
    }

    @Override
    public String getLatestIdByName(String name, User user) {
        var statement = select(Info.characterId, Info.version)
                .from(Info.table)
                .where(Info.name, isEqualTo(name))
                .and(Info.userId, isEqualTo(user.getUserId()))
                .and(Info.visibility, isNotEqualTo(Visibility.HIDDEN.text()))
                .orderBy(Info.version.descending())
                .limit(1)
                .build()
                .render(RenderingStrategies.MYBATIS3);

        return characterInfoMapper.selectOne(statement).map(CharacterInfo::getCharacterId).orElse(null);
    }

    @Override
    @LongPeriodCacheEvict(keyBy = CACHE_KEY_SPEL_PREFIX + "#p0")
    public String publish(String characterId, Visibility visibility, User user) {
        String publishedInfoId;
        SqlSession session = sqlSessionFactory.openSession();
        try {
            var infoTriple = details(characterId, user);
            if (Objects.isNull(infoTriple)) {
                return null;
            }
            CharacterInfo info = infoTriple.getLeft();

            List<Triple<String, Integer, InteractiveStats>> versionInfoList = listVersionsByName(info.getName(), user);
            Triple<String, Integer, InteractiveStats> versionInfo =
                    CollectionUtils.isNotEmpty(versionInfoList) ? versionInfoList.getFirst() : null;
            Integer version = Objects.nonNull(versionInfo) ? versionInfo.getMiddle() : info.getVersion();

            info.setVisibility(visibility.text());
            info.setVersion(version + 1);

            if (StringUtils.isNotBlank(info.getDraft())) {
                CharacterInfoDraft draft = CharacterUtils.getDraftInfo(info.getDraft());
                overrideByDraft(draft, info);
                info.setDraft(null);
            }

            info.setCharacterId(null);
            doCreate(Pair.of(info, infoTriple.getMiddle()));
            publishedInfoId = info.getCharacterId();

            if (CollectionUtils.isNotEmpty(infoTriple.getRight())) {
                Set<CharacterBackend> backendSet = new HashSet<>(infoTriple.getRight());
                List<String> cacheKeys = new ArrayList<>(backendSet.size());
                for (CharacterBackend backend : backendSet) {
                    int rows = characterBackendMapper.updateByPrimaryKeySelective(backend
                            .withCharacterId(publishedInfoId)
                            .withGmtModified(info.getGmtModified()));
                    cacheKeys.add(BACKEND_CHARACTER_ID_CACHE_KEY_PREFIX + backend.getBackendId());
                    if (rows <= 0) {
                        info.setCharacterId(null);
                        throw new RuntimeException("Publish backend from " + backend.getBackendId() + " failed!");
                    }
                    eventPublisher.publishEvent(
                            new CharacterBackendEvent(user.getUserId(), backend.getBackendId()));
                }
                CacheUtils.longPeriodCacheEvict(cacheKeys);
            }

            for (var prevVersionInfo : versionInfoList) {
                info = new CharacterInfo()
                        .withCharacterId(prevVersionInfo.getLeft())
                        .withVisibility(Visibility.HIDDEN.text())
                        .withGmtModified(new Date());
                characterInfoMapper.updateByPrimaryKeySelective(info);
            }

            characterInfoMapper.updateByPrimaryKeySelective(info);

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
            log.error("Failed to publish character", e);
            publishedInfoId = null;
            session.rollback();
        } finally {
            session.close();
        }
        return publishedInfoId;
    }

    @Override
    @LongPeriodCache
    public String getOwner(String characterId) {
        var statement = select(Info.userId)
                .from(Info.table)
                .where(Info.characterId, isEqualTo(characterId))
                .build()
                .render(RenderingStrategies.MYBATIS3);

        return characterInfoMapper.selectOne(statement).map(CharacterInfo::getUserId).orElse(null);
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

        return !characterInfoMapper.selectMany(statement).isEmpty();
    }

    private void ensureDefaultBackend(CharacterBackend characterBackend, Date now) {
        String characterId = characterBackend.getCharacterId();
        if (StringUtils.isBlank(characterId)) {
            return;
        }

        if (characterBackend.getIsDefault() != (byte) 1) {
            ensureDefaultBackend(characterId, now);
            return;
        }

        List<CharacterBackend> defaultBackends = characterBackendMapper.select(c ->
                c.where(CharacterBackendDynamicSqlSupport.backendId, isNotEqualTo(characterBackend.getBackendId()))
                        .and(CharacterBackendDynamicSqlSupport.characterId, isEqualTo(characterId))
                        .and(CharacterBackendDynamicSqlSupport.isDefault, isEqualTo((byte) 1)));

        for (CharacterBackend defaultBackend : defaultBackends) {
            characterBackendMapper.updateByPrimaryKeySelective(new CharacterBackend()
                    .withBackendId(defaultBackend.getBackendId())
                    .withGmtModified(now)
                    .withIsDefault((byte) 0));
        }
    }

    private void ensureDefaultBackend(String characterId, Date now) {
        List<CharacterBackend> defaultBackends = characterBackendMapper.select(c ->
                c.where(CharacterBackendDynamicSqlSupport.characterId, isEqualTo(characterId))
                        .and(CharacterBackendDynamicSqlSupport.isDefault, isEqualTo((byte) 1)));

        if (CollectionUtils.isEmpty(defaultBackends)) {
            characterBackendMapper.select(c -> c.where(CharacterBackendDynamicSqlSupport.characterId, isEqualTo(characterId)))
                    .stream()
                    .findAny()
                    .ifPresent(backend -> characterBackendMapper.updateByPrimaryKeySelective(
                            backend.withGmtModified(now).withIsDefault((byte) 1)));
        }
    }

    @Override
    @LongPeriodCacheEvict(keyBy = DEFAULT_BACKEND_CACHE_KEY_SPEL_PREFIX + "#p0.characterId")
    public String addBackend(CharacterBackend characterBackend) {
        if (StringUtils.isBlank(characterBackend.getCharacterId())) {
            return null;
        }

        SqlSession session = sqlSessionFactory.openSession();
        try {
            Date now = new Date();
            int rows = characterBackendMapper.insertSelective(characterBackend
                    .withGmtCreate(now)
                    .withGmtModified(now)
                    .withBackendId(IdUtils.newId()));

            if (rows > 0) {
                ensureDefaultBackend(characterBackend, now);
            } else {
                characterBackend.setBackendId(null);
            }
        } catch (Exception e) {
            log.error("Failed to create character backend", e);
            characterBackend.setBackendId(null);
            session.rollback();
        } finally {
            session.close();
        }
        return characterBackend.getBackendId();
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = LONG_PERIOD_CACHE_NAME, key = DEFAULT_BACKEND_CACHE_KEY_SPEL_PREFIX + "#p0"),
            @CacheEvict(cacheNames = LONG_PERIOD_CACHE_NAME, key = BACKEND_CACHE_KEY_SPEL_PREFIX + "#p1")
    })
    public boolean removeBackend(String characterId, String characterBackendId) {
        int rows = characterBackendMapper.deleteByPrimaryKey(characterBackendId);
        if (rows > 0) {
            ensureDefaultBackend(characterId, new Date());
            return true;
        }
        return false;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = LONG_PERIOD_CACHE_NAME, key = DEFAULT_BACKEND_CACHE_KEY_SPEL_PREFIX + "#p0.characterId"),
            @CacheEvict(cacheNames = LONG_PERIOD_CACHE_NAME, key = BACKEND_CACHE_KEY_SPEL_PREFIX + "#p0.backendId")
    })
    public boolean updateBackend(CharacterBackend characterBackend) {
        String characterId = characterBackend.getCharacterId();
        if (StringUtils.isBlank(characterId)) {
            return false;
        }

        int rows = 0;
        SqlSession session = sqlSessionFactory.openSession();
        try {
            Date now = new Date();
            rows = characterBackendMapper.updateByPrimaryKeySelective(characterBackend
                    .withGmtModified(now));

            if (rows > 0) {
                ensureDefaultBackend(characterBackend, now);
            }
        } catch (Exception e) {
            log.error("Failed to update character backend", e);
            session.rollback();
        } finally {
            session.close();
        }
        return rows > 0;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = LONG_PERIOD_CACHE_NAME, key = DEFAULT_BACKEND_CACHE_KEY_SPEL_PREFIX + "#p0"),
            @CacheEvict(cacheNames = LONG_PERIOD_CACHE_NAME, key = BACKEND_CACHE_KEY_SPEL_PREFIX + "#p1")
    })
    public boolean setDefaultBackend(String characterId, String characterBackendId) {
        return updateBackend(new CharacterBackend()
                .withCharacterId(characterId)
                .withBackendId(characterBackendId)
                .withIsDefault((byte) 1));
    }

    @Override
    @LongPeriodCache(keyBy = DEFAULT_BACKEND_CACHE_KEY_SPEL_PREFIX + "#p0")
    public CharacterBackend getDefaultBackend(String characterId) {
        return characterBackendMapper.selectOne(c ->
                c.where(CharacterBackendDynamicSqlSupport.characterId, isEqualTo(characterId))
                        .and(CharacterBackendDynamicSqlSupport.isDefault, isEqualTo((byte) 1)))
                .orElse(null);
    }

    @Override
    @LongPeriodCache(keyBy = BACKEND_CACHE_KEY_SPEL_PREFIX + "#p0")
    public CharacterBackend getBackend(String characterBackendId) {
        return characterBackendMapper.selectByPrimaryKey(characterBackendId).orElse(null);
    }

    @Override
    public List<String> listBackendIds(String characterId) {
        var statement = select(CharacterBackendDynamicSqlSupport.backendId)
                .from(CharacterBackendDynamicSqlSupport.characterBackend)
                .where(CharacterBackendDynamicSqlSupport.characterId, isEqualTo(characterId))
                .build()
                .render(RenderingStrategies.MYBATIS3);
        return characterBackendMapper.selectMany(statement)
                .stream()
                .map(CharacterBackend::getBackendId)
                .toList();
    }

    @Override
    @LongPeriodCache
    public String getBackendOwner(String characterBackendId) {
        String characterId = getBackendCharacterId(characterBackendId);
        if (StringUtils.isNotBlank(characterId)) {
            return getOwner(characterId);
        }
        return null;
    }

    @Override
    @LongPeriodCache(keyBy = BACKEND_CHARACTER_ID_CACHE_KEY_SPEL_PREFIX + "#p0")
    public String getBackendCharacterId(String characterBackendId) {
        var statement = select(CharacterBackendDynamicSqlSupport.characterId)
                .from(CharacterBackendDynamicSqlSupport.characterBackend)
                .where(CharacterBackendDynamicSqlSupport.backendId, isEqualTo(characterBackendId))
                .build()
                .render(RenderingStrategies.MYBATIS3);

        return characterBackendMapper.selectOne(statement).map(CharacterBackend::getCharacterId).orElse(null);
    }

    private static class Info {
        public static final CharacterInfoDynamicSqlSupport.CharacterInfo table = CharacterInfoDynamicSqlSupport.characterInfo;
        public static final SqlColumn<String> characterId = CharacterInfoDynamicSqlSupport.characterId;
        public static final SqlColumn<Date> gmtCreate = CharacterInfoDynamicSqlSupport.gmtCreate;
        public static final SqlColumn<Date> gmtModified = CharacterInfoDynamicSqlSupport.gmtModified;
        public static final SqlColumn<String> userId = CharacterInfoDynamicSqlSupport.userId;
        public static final SqlColumn<String> parentId = CharacterInfoDynamicSqlSupport.parentId;
        public static final SqlColumn<String> visibility = CharacterInfoDynamicSqlSupport.visibility;
        public static final SqlColumn<String> name = CharacterInfoDynamicSqlSupport.name;
        public static final SqlColumn<String> avatar = CharacterInfoDynamicSqlSupport.avatar;
        public static final SqlColumn<String> picture = CharacterInfoDynamicSqlSupport.picture;
        public static final SqlColumn<String> lang = CharacterInfoDynamicSqlSupport.lang;
        public static final SqlColumn<Integer> version = CharacterInfoDynamicSqlSupport.version;
        public static final SqlColumn<String> description = CharacterInfoDynamicSqlSupport.description;
        public static final SqlColumn<String> gender = CharacterInfoDynamicSqlSupport.gender;
        public static final SqlColumn<String> profile = CharacterInfoDynamicSqlSupport.profile;
        public static final SqlColumn<String> greeting = CharacterInfoDynamicSqlSupport.greeting;
        public static final SqlColumn<String> chatStyle = CharacterInfoDynamicSqlSupport.chatStyle;
        public static final SqlColumn<String> chatExample = CharacterInfoDynamicSqlSupport.chatExample;
        public static final SqlColumn<String> ext = CharacterInfoDynamicSqlSupport.ext;
        public static final SqlColumn<String> draft = CharacterInfoDynamicSqlSupport.draft;
        public static final SqlColumn<Integer> priority = CharacterInfoDynamicSqlSupport.priority;

        public static List<BasicColumn> summaryColumns() {
            return List.of(
                    Info.gmtCreate,
                    Info.gmtModified,
                    Info.userId,
                    Info.characterId,
                    Info.visibility,
                    Info.version,
                    Info.name,
                    Info.avatar,
                    Info.picture,
                    Info.gender,
                    Info.description,
                    Info.lang,
                    Info.priority
            );
        }
    }
}
