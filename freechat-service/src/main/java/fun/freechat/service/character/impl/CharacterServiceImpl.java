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
                                .and(TagDynamicSqlSupport.referId, isEqualTo(info.getCharacterUid())))
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
                        c.where(CharacterBackendDynamicSqlSupport.characterUid,
                                isEqualTo(infoPair.getLeft().getCharacterUid())));

        return Triple.of(infoPair.getLeft(), infoPair.getRight(), backends);
    }

    private static SqlColumn nameToColumn(String fieldName) {
        return switch (fieldName) {
            case "version" -> Info.version;
            case "modifyTime" -> Info.gmtModified;
            case "createTime" -> Info.gmtCreate;
            case "priority" -> Info.priority;
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
                .withGmtModified(now));
        if (rows <= 0) {
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
                        .withReferId(info.getCharacterUid()));
                if (rows <= 0) {
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
                    .on(Info.characterUid, equalTo(TagDynamicSqlSupport.referId));

        }
        List<String> orderByStats =  new LinkedList<>(InfoUtils.trimListElements(query.getOrderBy()));
        orderByStats.retainAll(StatsType.fieldNames());
        if (!orderByStats.isEmpty()) {
            table.leftJoin(InteractiveStatsDynamicSqlSupport.interactiveStats, "i")
                    .on(Info.characterUid, equalTo((InteractiveStatsDynamicSqlSupport.referId)));
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
        // priority
        Boolean highPriority = query.getWhere().getHighPriority();
        if (Objects.nonNull(highPriority)) {
            if (highPriority) {
                conditions.and(Info.priority, isGreaterThan(1));
            } else {
                conditions.and(Info.priority, isEqualTo(1));
            }
        }
        // version
        if (!hasDraft) {
            conditions.and(Info.version, isGreaterThan(0));
        }
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
  and c.priority = {priority} \
  and c.version > 0 \
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
            infoPair.getLeft().setCharacterUid(IdUtils.newId());
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
                tagMapper.delete(c -> c.where(TagDynamicSqlSupport.referId, isEqualTo(characterInfo.getCharacterUid()))
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
                            .withReferId(characterInfo.getCharacterUid()));
                    if (rows <= 0) {
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
    public boolean hide(Long characterId, User user) {
        if (Objects.isNull(characterId)) {
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
    public boolean delete(Long characterId, User user) {
        if (Objects.isNull(characterId)) {
            return false;
        }
        int rows = characterInfoMapper.delete(c -> c.where(Info.characterId, isEqualTo(characterId))
                .and(Info.userId, isEqualTo(user.getUserId())));
        return rows > 0;
    }

    @Override
    public List<Long> delete(User user) {
        var statement = select(Info.characterId)
                .from(Info.table)
                .where(Info.userId, isEqualTo(user.getUserId()))
                .build()
                .render(RenderingStrategies.MYBATIS3);
        List<Long> ids = characterInfoMapper.selectMany(statement)
                .stream()
                .map(CharacterInfo::getCharacterId)
                .toList();

        return delete(ids, user);
    }

    @Override
    public List<Long> deleteByName(String name, User user) {
        var statement = select(Info.characterId)
                .from(Info.table)
                .where(Info.userId, isEqualTo(user.getUserId()))
                .and(Info.name, isEqualTo(name))
                .build()
                .render(RenderingStrategies.MYBATIS3);
        List<Long> ids = characterInfoMapper.selectMany(statement)
                .stream()
                .map(CharacterInfo::getCharacterId)
                .toList();

        return delete(ids, user);
    }

    private List<Long> delete(List<Long> ids, User user) {
        LinkedList<Long> deletedIds = new LinkedList<>();
        SqlSession session = sqlSessionFactory.openSession();
        try {
            for (Long id : ids) {
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
    public CharacterInfo summary(Long characterId) {
        var statement = select(Info.summaryColumns())
                .from(Info.table)
                .where(Info.characterId, isEqualTo(characterId))
                .build()
                .render(RenderingStrategies.MYBATIS3);
        return characterInfoMapper.selectOne(statement).orElse(null);
    }

    @Override
    public Pair<CharacterInfo, List<String>> summary(Long characterId, User user) {
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
    public List<Pair<CharacterInfo, List<String>>> summary(Collection<Long> characterIds, User user) {
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
    public Triple<CharacterInfo, List<String>, List<CharacterBackend>> details(Long characterId, User user) {
        return characterInfoMapper.selectOne(c -> wrapQueryExpression(c, user.getUserId())
                        .and(Info.characterId, isEqualTo(characterId)))
                .filter(info -> filterVisibility(info, user))
                .map(this::toInfoPair)
                .map(this::toInfoTriple)
                .orElse(null);
    }

    @Override
    public List<Triple<CharacterInfo, List<String>, List<CharacterBackend>>> details(Collection<Long> characterIds, User user) {
        return characterInfoMapper.select(c -> wrapQueryExpression(c, user.getUserId())
                        .and(Info.characterId, isIn(characterIds)))
                .stream()
                .filter(info -> filterVisibility(info, user))
                .map(this::toInfoPair)
                .map(this::toInfoTriple)
                .toList();
    }

    @Override
    public List<Triple<Long, Integer, InteractiveStats>> listVersionsByName(String name, User user) {
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
                                            .and(InteractiveStatsDynamicSqlSupport.referId, isEqualTo(info.getCharacterUid()))
                                            .orderBy(InteractiveStatsDynamicSqlSupport.gmtModified.descending())
                                            .limit(1))
                            .orElse(null);
                    return Triple.of(info.getCharacterId(), info.getVersion(), stats);
                })
                .toList();
    }

    @Override
    public Long getLatestIdByName(String name, User user) {
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
    public Long getLatestIdByUid(String characterUid, User user) {
        String userId = Objects.nonNull(user) ? user.getUserId() : null;
        var statement = select(Info.characterId, Info.version)
                .from(Info.table)
                .where(Info.characterUid, isEqualTo(characterUid))
                .and(Info.userId, isEqualTo(userId).filter(Objects::nonNull))
                .and(Info.visibility, isNotEqualTo(Visibility.HIDDEN.text()))
                .orderBy(Info.version.descending())
                .limit(1)
                .build()
                .render(RenderingStrategies.MYBATIS3);

        return characterInfoMapper.selectOne(statement).map(CharacterInfo::getCharacterId).orElse(null);
    }

    @Override
    @LongPeriodCacheEvict(keyBy = CACHE_KEY_SPEL_PREFIX + "#p0")
    public Long publish(Long characterId, Visibility visibility, User user) {
        Long publishedInfoId;
        SqlSession session = sqlSessionFactory.openSession();
        try {
            var infoTriple = details(characterId, user);
            if (Objects.isNull(infoTriple)) {
                return null;
            }
            CharacterInfo info = infoTriple.getLeft();

            List<Triple<Long, Integer, InteractiveStats>> versionInfoList = listVersionsByName(info.getName(), user);
            Triple<Long, Integer, InteractiveStats> versionInfo =
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
            doCreate(Pair.of(info, null));
            publishedInfoId = info.getCharacterId();

            for (var prevVersionInfo : versionInfoList) {
                info = new CharacterInfo()
                        .withCharacterId(prevVersionInfo.getLeft())
                        .withVisibility(Visibility.HIDDEN.text())
                        .withGmtModified(new Date());
                characterInfoMapper.updateByPrimaryKeySelective(info);
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
    public String getOwner(Long characterId) {
        var statement = select(Info.userId)
                .from(Info.table)
                .where(Info.characterId, isEqualTo(characterId))
                .build()
                .render(RenderingStrategies.MYBATIS3);

        return characterInfoMapper.selectOne(statement).map(CharacterInfo::getUserId).orElse(null);
    }

    @Override
    @LongPeriodCache
    public String getOwnerByUid(String characterUid) {
        var statement = select(Info.userId)
                .from(Info.table)
                .where(Info.characterUid, isEqualTo(characterUid))
                .limit(1)
                .build()
                .render(RenderingStrategies.MYBATIS3);

        return characterInfoMapper.selectOne(statement).map(CharacterInfo::getUserId).orElse(null);
    }

    @Override
    @LongPeriodCache
    public String getUid(Long characterId) {
        var statement = select(Info.characterUid)
                .from(Info.table)
                .where(Info.characterId, isEqualTo(characterId))
                .build()
                .render(RenderingStrategies.MYBATIS3);

        return characterInfoMapper.selectOne(statement).map(CharacterInfo::getCharacterUid).orElse(null);
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
        String characterUid = characterBackend.getCharacterUid();
        if (StringUtils.isBlank(characterUid)) {
            return;
        }

        if (characterBackend.getIsDefault() != (byte) 1) {
            ensureDefaultBackend(characterUid, now);
            return;
        }

        List<CharacterBackend> defaultBackends = characterBackendMapper.select(c ->
                c.where(CharacterBackendDynamicSqlSupport.backendId, isNotEqualTo(characterBackend.getBackendId()))
                        .and(CharacterBackendDynamicSqlSupport.characterUid, isEqualTo(characterUid))
                        .and(CharacterBackendDynamicSqlSupport.isDefault, isEqualTo((byte) 1)));

        for (CharacterBackend defaultBackend : defaultBackends) {
            characterBackendMapper.updateByPrimaryKeySelective(new CharacterBackend()
                    .withBackendId(defaultBackend.getBackendId())
                    .withGmtModified(now)
                    .withIsDefault((byte) 0));
        }
    }

    private void ensureDefaultBackend(String characterUid, Date now) {
        List<CharacterBackend> defaultBackends = characterBackendMapper.select(c ->
                c.where(CharacterBackendDynamicSqlSupport.characterUid, isEqualTo(characterUid))
                        .and(CharacterBackendDynamicSqlSupport.isDefault, isEqualTo((byte) 1)));

        if (CollectionUtils.isEmpty(defaultBackends)) {
            characterBackendMapper.select(c -> c.where(CharacterBackendDynamicSqlSupport.characterUid, isEqualTo(characterUid)))
                    .stream()
                    .findAny()
                    .ifPresent(backend -> characterBackendMapper.updateByPrimaryKeySelective(
                            backend.withGmtModified(now).withIsDefault((byte) 1)));
        }
    }

    @Override
    @LongPeriodCacheEvict(keyBy = DEFAULT_BACKEND_CACHE_KEY_SPEL_PREFIX + "#p0.characterUid")
    public String addBackend(CharacterBackend characterBackend) {
        if (StringUtils.isBlank(characterBackend.getCharacterUid())) {
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
    public boolean removeBackend(String characterUid, String characterBackendId) {
        int rows = characterBackendMapper.deleteByPrimaryKey(characterBackendId);
        if (rows > 0) {
            ensureDefaultBackend(characterUid, new Date());
            return true;
        }
        return false;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = LONG_PERIOD_CACHE_NAME, key = DEFAULT_BACKEND_CACHE_KEY_SPEL_PREFIX + "#p0.characterUid"),
            @CacheEvict(cacheNames = LONG_PERIOD_CACHE_NAME, key = BACKEND_CACHE_KEY_SPEL_PREFIX + "#p0.backendId")
    })
    public boolean updateBackend(CharacterBackend characterBackend) {
        int rows = 0;
        SqlSession session = sqlSessionFactory.openSession();
        try {
            Date now = new Date();
            rows = characterBackendMapper.updateByPrimaryKeySelective(characterBackend
                    .withGmtModified(now)
                    .withCharacterUid(null));

            if (rows > 0) {
                ensureDefaultBackend(characterBackend, now);

                CharacterBackendEvent event = new CharacterBackendEvent(
                        null, characterBackend.getBackendId());
                eventPublisher.publishEvent(event);
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
    public boolean setDefaultBackend(String characterUid, String characterBackendId) {
        return updateBackend(new CharacterBackend()
                .withCharacterUid(characterUid)
                .withBackendId(characterBackendId)
                .withIsDefault((byte) 1));
    }

    @Override
    @LongPeriodCache(keyBy = DEFAULT_BACKEND_CACHE_KEY_SPEL_PREFIX + "#p0")
    public CharacterBackend getDefaultBackend(String characterUid) {
        return characterBackendMapper.selectOne(c ->
                c.where(CharacterBackendDynamicSqlSupport.characterUid, isEqualTo(characterUid))
                        .and(CharacterBackendDynamicSqlSupport.isDefault, isEqualTo((byte) 1)))
                .orElse(null);
    }

    @Override
    @LongPeriodCache(keyBy = BACKEND_CACHE_KEY_SPEL_PREFIX + "#p0")
    public CharacterBackend getBackend(String characterBackendId) {
        return characterBackendMapper.selectByPrimaryKey(characterBackendId).orElse(null);
    }

    @Override
    public List<String> listBackendIds(String characterUid) {
        var statement = select(CharacterBackendDynamicSqlSupport.backendId)
                .from(CharacterBackendDynamicSqlSupport.characterBackend)
                .where(CharacterBackendDynamicSqlSupport.characterUid, isEqualTo(characterUid))
                .build()
                .render(RenderingStrategies.MYBATIS3);
        return characterBackendMapper.selectMany(statement)
                .stream()
                .map(CharacterBackend::getBackendId)
                .toList();
    }

    @Override
    public List<CharacterBackend> listBackends(String characterUid) {
        return characterBackendMapper.select(c ->
                c.where(CharacterBackendDynamicSqlSupport.characterUid, isEqualTo(characterUid)));
    }

    @Override
    public int removeBackendsByUser(User user) {
        var statement = selectDistinct(Info.characterUid)
                .from(Info.table)
                .where(Info.userId, isEqualTo(user.getUserId()))
                .build()
                .render(RenderingStrategies.MYBATIS3);

        List<String> characterUidList = characterInfoMapper.selectMany(statement)
                .stream()
                .map(CharacterInfo::getCharacterUid)
                .toList();

        return characterBackendMapper.delete(c ->
                c.where(CharacterBackendDynamicSqlSupport.characterUid, isIn(characterUidList)));
    }

    @Override
    @LongPeriodCache
    public String getBackendOwner(String characterBackendId) {
        String characterUid = getBackendCharacterUid(characterBackendId);
        if (StringUtils.isNotBlank(characterUid)) {
            return getOwnerByUid(characterUid);
        }
        return null;
    }

    @Override
    @LongPeriodCache(keyBy = BACKEND_CHARACTER_ID_CACHE_KEY_SPEL_PREFIX + "#p0")
    public String getBackendCharacterUid(String characterBackendId) {
        var statement = select(CharacterBackendDynamicSqlSupport.characterUid)
                .from(CharacterBackendDynamicSqlSupport.characterBackend)
                .where(CharacterBackendDynamicSqlSupport.backendId, isEqualTo(characterBackendId))
                .build()
                .render(RenderingStrategies.MYBATIS3);

        return characterBackendMapper.selectOne(statement).map(CharacterBackend::getCharacterUid).orElse(null);
    }

    private static class Info {
        public static final CharacterInfoDynamicSqlSupport.CharacterInfo table = CharacterInfoDynamicSqlSupport.characterInfo;
        public static final SqlColumn<Long> characterId = CharacterInfoDynamicSqlSupport.characterId;
        public static final SqlColumn<String> characterUid = CharacterInfoDynamicSqlSupport.characterUid;
        public static final SqlColumn<Date> gmtCreate = CharacterInfoDynamicSqlSupport.gmtCreate;
        public static final SqlColumn<Date> gmtModified = CharacterInfoDynamicSqlSupport.gmtModified;
        public static final SqlColumn<String> userId = CharacterInfoDynamicSqlSupport.userId;
        public static final SqlColumn<String> parentUid = CharacterInfoDynamicSqlSupport.parentUid;
        public static final SqlColumn<String> visibility = CharacterInfoDynamicSqlSupport.visibility;
        public static final SqlColumn<String> name = CharacterInfoDynamicSqlSupport.name;
        public static final SqlColumn<String> nickname = CharacterInfoDynamicSqlSupport.nickname;
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
        public static final SqlColumn<String> defaultScene = CharacterInfoDynamicSqlSupport.defaultScene;
        public static final SqlColumn<String> ext = CharacterInfoDynamicSqlSupport.ext;
        public static final SqlColumn<String> draft = CharacterInfoDynamicSqlSupport.draft;
        public static final SqlColumn<Integer> priority = CharacterInfoDynamicSqlSupport.priority;

        public static List<BasicColumn> summaryColumns() {
            return List.of(
                    Info.gmtCreate,
                    Info.gmtModified,
                    Info.userId,
                    Info.characterId,
                    Info.characterUid,
                    Info.visibility,
                    Info.version,
                    Info.name,
                    Info.nickname,
                    Info.avatar,
                    Info.picture,
                    Info.gender,
                    Info.greeting,
                    Info.defaultScene,
                    Info.description,
                    Info.lang,
                    Info.priority
            );
        }
    }
}
