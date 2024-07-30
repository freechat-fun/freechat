package fun.freechat.service.organization.impl;

import fun.freechat.mapper.OrgRelationshipDynamicSqlSupport;
import fun.freechat.mapper.OrgRelationshipMapper;
import fun.freechat.model.OrgRelationship;
import fun.freechat.service.organization.OrgService;
import fun.freechat.util.graph.Graph;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;
import static org.mybatis.dynamic.sql.SqlBuilder.isIn;

@Service
@Slf4j
@SuppressWarnings("unused")
public class OrgServiceImpl implements OrgService {
    @Autowired
    private SqlSessionFactory sqlSessionFactory;
    @Autowired
    OrgRelationshipRepo orgRelationshipRepo;
    @Autowired
    OrgRelationshipMapper orgRelationshipMapper;

    private boolean isEnabled(OrgRelationship relationship) {
        return relationship.getEnabled().equals((byte)1);
    }

    private boolean isVirtual(OrgRelationship relationship) {
        return relationship.getIsVirtual().equals((byte)1);
    }

    @Override
    public Collection<String> addOwners(String userId, Collection<String> owners) {
        if (StringUtils.isBlank(userId) || CollectionUtils.isEmpty(owners)) {
            return Collections.emptyList();
        }
        List<String> successfulOwners = new LinkedList<>();
        SqlSession session = sqlSessionFactory.openSession();
        try {
            Date now = new Date();
            Graph<String> solidSubordinates = getSubordinates(userId, false);
            int rows;
            for (String ownerId : new HashSet<>(owners)) {
                boolean isVirtual = solidSubordinates.contains(ownerId);
                OrgRelationship oldRelationship = orgRelationshipMapper.selectOne(c ->
                        c.where(OrgRelationshipDynamicSqlSupport.userId, isEqualTo(userId))
                                .and(OrgRelationshipDynamicSqlSupport.ownerId, isEqualTo(ownerId)))
                        .orElse(null);
                if (Objects.nonNull(oldRelationship)) {
                    if (isEnabled(oldRelationship) && isVirtual == isVirtual(oldRelationship)) {
                        continue;
                    }
                    rows = orgRelationshipMapper.updateByPrimaryKeySelective(
                            oldRelationship.withGmtModified(now)
                                    .withEnabled((byte)1)
                                    .withIsVirtual(isVirtual ? (byte)1 : (byte)0));
                } else {
                    OrgRelationship relationship = new OrgRelationship()
                            .withGmtCreate(now)
                            .withGmtModified(now)
                            .withUserId(userId)
                            .withOwnerId(ownerId)
                            .withEnabled((byte)1)
                            .withIsVirtual(isVirtual ? (byte)1 : (byte)0);
                    rows = orgRelationshipMapper.insert(relationship);
                }
                if (rows > 0) {
                    successfulOwners.add(ownerId);
                }
            }
            session.commit();
            orgRelationshipRepo.clearCaches(userId);
            for (String ownerId : successfulOwners) {
                orgRelationshipRepo.clearCaches(ownerId);
            }
        } catch (Exception e) {
            log.error("Failed to add owners for {}: {}", userId, owners, e);
            session.rollback();
        } finally {
            session.close();
        }
        return successfulOwners;
    }

    @Override
    public Collection<String> addSubordinates(String userId, Collection<String> subordinates) {
        if (StringUtils.isBlank(userId) || CollectionUtils.isEmpty(subordinates)) {
            return Collections.emptyList();
        }
        List<String> successfulSubordinates = new LinkedList<>();
        SqlSession session = sqlSessionFactory.openSession();
        try {
            Date now = new Date();
            Graph<String> solidOwners = getOwners(userId, false);
            int rows;
            for (String subordinateId : new HashSet<>(subordinates)) {
                boolean isVirtual = solidOwners.contains(subordinateId);
                OrgRelationship oldRelationship = orgRelationshipMapper.selectOne(c ->
                                c.where(OrgRelationshipDynamicSqlSupport.userId, isEqualTo(subordinateId))
                                        .and(OrgRelationshipDynamicSqlSupport.ownerId, isEqualTo(userId)))
                        .orElse(null);
                if (Objects.nonNull(oldRelationship)) {
                    if (isEnabled(oldRelationship) && isVirtual == isVirtual(oldRelationship)) {
                        continue;
                    }
                    rows = orgRelationshipMapper.updateByPrimaryKeySelective(
                            oldRelationship.withGmtModified(now)
                                    .withEnabled((byte)1)
                                    .withIsVirtual(isVirtual ? (byte)1 : (byte)0));
                } else {
                    OrgRelationship relationship = new OrgRelationship()
                            .withGmtCreate(now)
                            .withGmtModified(now)
                            .withUserId(subordinateId)
                            .withOwnerId(userId)
                            .withEnabled((byte)1)
                            .withIsVirtual(isVirtual ? (byte)1 : (byte)0);
                    rows = orgRelationshipMapper.insert(relationship);
                }
                if (rows > 0) {
                    successfulSubordinates.add(subordinateId);
                }
            }
            session.commit();
            orgRelationshipRepo.clearCaches(userId);
            for (String ownerId : successfulSubordinates) {
                orgRelationshipRepo.clearCaches(ownerId);
            }
        } catch (Exception e) {
            log.error("Failed to add subordinates for {}: {}", userId, subordinates, e);
            session.rollback();
        } finally {
            session.close();
        }
        return successfulSubordinates;
    }

    @Override
    public boolean removeOwners(String userId, Collection<String> owners) {
        if (StringUtils.isBlank(userId) || CollectionUtils.isEmpty(owners)) {
            return false;
        }
        int rows = orgRelationshipMapper.delete(c ->
                c.where(OrgRelationshipDynamicSqlSupport.userId, isEqualTo(userId))
                        .and(OrgRelationshipDynamicSqlSupport.ownerId, isIn(owners)));
        return rows > 0;
    }

    @Override
    public boolean removeSubordinates(String userId, Collection<String> subordinates) {
        if (StringUtils.isBlank(userId) || CollectionUtils.isEmpty(subordinates)) {
            return false;
        }
        int rows = orgRelationshipMapper.delete(c ->
                c.where(OrgRelationshipDynamicSqlSupport.ownerId, isEqualTo(userId))
                        .and(OrgRelationshipDynamicSqlSupport.userId, isIn(subordinates)));
        return rows > 0;
    }

    @Override
    public boolean removeSubordinatesTree(String userId) {
        if (StringUtils.isBlank(userId)) {
            return false;
        }
        TreeSet<String> subordinates = (TreeSet<String>) orgRelationshipRepo.getSubordinates(userId, false)
                .tailSet(userId);
        boolean successful = false;
        for (String subordinateId : subordinates.descendingSet()) {
            int rows = orgRelationshipMapper.delete(c ->
                    c.where(OrgRelationshipDynamicSqlSupport.ownerId, isEqualTo(subordinateId)));
            successful =  rows > 0 || successful;
        }
        return successful;
    }

    @Override
    public boolean updateOwners(String userId, Collection<String> owners) {
        if (StringUtils.isBlank(userId)) {
            return false;
        }
        if (owners == null) {
            owners = Collections.emptyList();
        }
        Set<String> banOwners = orgRelationshipMapper.select(c ->
                        c.where(OrgRelationshipDynamicSqlSupport.userId, isEqualTo(userId)))
                .stream()
                .map(OrgRelationship::getOwnerId)
                .collect(Collectors.toSet());
        banOwners.removeAll(owners);
        boolean successful = removeOwners(userId, banOwners);
        successful = (!CollectionUtils.isEmpty(addOwners(userId, owners))) || successful;
        return successful;
    }

    @Override
    public boolean updateSubordinates(String userId, Collection<String> subordinates) {
        if (StringUtils.isBlank(userId)) {
            return false;
        }
        if (subordinates == null) {
            subordinates = Collections.emptyList();
        }
        Set<String> banSubordinates = orgRelationshipMapper.select(c ->
                        c.where(OrgRelationshipDynamicSqlSupport.ownerId, isEqualTo(userId)))
                .stream()
                .map(OrgRelationship::getUserId)
                .collect(Collectors.toSet());
        banSubordinates.removeAll(subordinates);
        boolean successful = removeSubordinates(userId, banSubordinates);
        successful = (!CollectionUtils.isEmpty(addSubordinates(userId, subordinates))) || successful;
        return successful;
    }

    @Override
    public boolean hasRelationship(String userId, String otherUserId) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(otherUserId)) {
            return false;
        }
        return getOwners(userId, true).contains(otherUserId) ||
                getSubordinates(userId, true).contains(otherUserId);
    }

    @Override
    public Graph<String> getOwners(String userId, boolean includeVirtual) {
        if (StringUtils.isBlank(userId)) {
            return null;
        }
        return orgRelationshipRepo.getOwners(userId, includeVirtual);
    }

    @Override
    public Graph<String> getSubordinates(String userId, boolean includeVirtual) {
        if (StringUtils.isBlank(userId)) {
            return null;
        }
        return orgRelationshipRepo.getSubordinates(userId, includeVirtual);
    }
}
