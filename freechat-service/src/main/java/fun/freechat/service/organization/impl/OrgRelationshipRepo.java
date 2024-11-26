package fun.freechat.service.organization.impl;

import fun.freechat.mapper.OrgRelationshipDynamicSqlSupport;
import fun.freechat.mapper.OrgRelationshipMapper;
import fun.freechat.model.OrgRelationship;
import fun.freechat.service.cache.LongPeriodCache;
import fun.freechat.util.graph.DaGraph;
import fun.freechat.util.graph.DiGraph;
import fun.freechat.util.graph.Graph;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

import static fun.freechat.service.util.CacheUtils.LONG_PERIOD_CACHE_NAME;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;
import static org.mybatis.dynamic.sql.SqlBuilder.selectDistinct;

@Component
@Slf4j
@SuppressWarnings("unused")
public class OrgRelationshipRepo {
    private static final String CACHE_KEY_PREFIX = "OrgRelationshipRepo_";
    private static final String OWNERS_CACHE_KEY_SPEL_PREFIX = "'" + CACHE_KEY_PREFIX + "owners_' + ";
    private static final String SUBORDINATES_CACHE_KEY_SPEL_PREFIX = "'" + CACHE_KEY_PREFIX + "subordinates_' + ";

    @Autowired
    private OrgRelationshipMapper orgRelationshipMapper;

    private List<String> getDirectOwners(String userId, boolean includeVirtual) {
        var fields = selectDistinct(OrgRelationshipDynamicSqlSupport.ownerId)
                .from(OrgRelationshipDynamicSqlSupport.orgRelationship);
        var conditions = fields.where(OrgRelationshipDynamicSqlSupport.userId, isEqualTo(userId))
                .and(OrgRelationshipDynamicSqlSupport.enabled, isEqualTo((byte)1));
        if (!includeVirtual) {
            conditions.and(OrgRelationshipDynamicSqlSupport.isVirtual, isEqualTo((byte)0));
        }
        var statement = conditions.build().render(RenderingStrategies.MYBATIS3);
        return orgRelationshipMapper.selectMany(statement)
                .stream()
                .map(OrgRelationship::getOwnerId)
                .toList();
    }

    private void addOwners(Graph<String> graph, String userId, boolean includeVirtual) {
        List<String> owners = getDirectOwners(userId, includeVirtual);
        List<String> validOwners = new LinkedList<>();
        for (String owner : owners) {
            try {
                if (!graph.add(owner)) {
                    continue;
                }
                graph.connect(userId, owner);
                validOwners.add(owner);
            } catch (UnsupportedOperationException e) {
                log.warn("Failed to connect from {} to {}", userId, owner);
                graph.remove(owner);
            }
        }
        for (String validOwner : validOwners) {
            addOwners(graph, validOwner, includeVirtual);
        }
    }

    @LongPeriodCache(keyBy = OWNERS_CACHE_KEY_SPEL_PREFIX + "#p0 + '_' + #p1")
    public Graph<String> getOwners(String userId, Boolean includeVirtual) {
        Graph<String> graph = includeVirtual ? new DiGraph<>() : new DaGraph<>();
        graph.add(userId);
        addOwners(graph, userId, includeVirtual);
        return graph;
    }

    private List<String> getDirectSubordinates(String userId, boolean includeVirtual) {
        var fields = selectDistinct(OrgRelationshipDynamicSqlSupport.userId)
                .from(OrgRelationshipDynamicSqlSupport.orgRelationship);
        var conditions = fields.where(OrgRelationshipDynamicSqlSupport.ownerId, isEqualTo(userId))
                .and(OrgRelationshipDynamicSqlSupport.enabled, isEqualTo((byte)1));
        if (!includeVirtual) {
            conditions.and(OrgRelationshipDynamicSqlSupport.isVirtual, isEqualTo((byte)0));
        }
        var statement = conditions.build().render(RenderingStrategies.MYBATIS3);
        return orgRelationshipMapper.selectMany(statement)
                .stream()
                .map(OrgRelationship::getUserId)
                .toList();
    }

    private void addSubordinates(Graph<String> graph, String userId, boolean includeVirtual) {
        List<String> subordinates = getDirectSubordinates(userId, includeVirtual);
        List<String> validSubordinates = new LinkedList<>();
        for (String subordinate : subordinates) {
            try {
                if (!graph.add(subordinate)) {
                    continue;
                }
                graph.connect(userId, subordinate);
                validSubordinates.add(subordinate);
            } catch (UnsupportedOperationException e) {
                log.warn("Failed to connect from {} to {}", userId, subordinate);
                graph.remove(subordinate);
            }
        }
        for (String validSubordinate : validSubordinates) {
            addSubordinates(graph, validSubordinate, includeVirtual);
        }
    }

    @LongPeriodCache(keyBy = SUBORDINATES_CACHE_KEY_SPEL_PREFIX + "#p0 + '_' + #p1")
    public Graph<String> getSubordinates(String userId, Boolean includeVirtual) {
        Graph<String> graph = includeVirtual ? new DiGraph<>() : new DaGraph<>();
        graph.add(userId);
        addSubordinates(graph, userId, includeVirtual);
        return graph;
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = LONG_PERIOD_CACHE_NAME, key = OWNERS_CACHE_KEY_SPEL_PREFIX + "#p0 + '_true'"),
            @CacheEvict(cacheNames = LONG_PERIOD_CACHE_NAME, key = OWNERS_CACHE_KEY_SPEL_PREFIX + "#p0 + '_false'"),
            @CacheEvict(cacheNames = LONG_PERIOD_CACHE_NAME, key = SUBORDINATES_CACHE_KEY_SPEL_PREFIX + "#p0 + '_true'"),
            @CacheEvict(cacheNames = LONG_PERIOD_CACHE_NAME, key = SUBORDINATES_CACHE_KEY_SPEL_PREFIX + "#p0 + '_false'")
    })
    public void clearCaches(String userId) {}
}
