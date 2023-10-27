package fun.freechat.service.organization;

import fun.freechat.util.graph.Graph;

import java.util.Collection;

public interface OrgService {
    default Graph<String> getOwners(String userId) {
        return getOwners(userId, false);
    }
    default Graph<String> getSubordinates(String userId) {
        return getSubordinates(userId, false);
    }
    Graph<String> getOwners(String userId, boolean includeVirtual);
    Graph<String> getSubordinates(String userId, boolean includeVirtual);
    Collection<String> addOwners(String userId, Collection<String> owners);
    Collection<String> addSubordinates(String userId, Collection<String> subordinates);
    boolean removeOwners(String userId, Collection<String> owners);
    boolean removeSubordinates(String userId, Collection<String> subordinates);
    boolean removeSubordinatesTree(String userId);
    boolean updateOwners(String userId, Collection<String> owners);
    boolean updateSubordinates(String userId, Collection<String> subordinates);
    boolean hasRelationship(String userId, String otherUserId);
}
