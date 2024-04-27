package fun.freechat.service.character;

import fun.freechat.model.CharacterBackend;
import fun.freechat.model.CharacterInfo;
import fun.freechat.model.InteractiveStats;
import fun.freechat.model.User;
import fun.freechat.service.enums.Visibility;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Collection;
import java.util.List;

public interface CharacterService {
    @Builder(toBuilder = true)
    @Data
    class Query {
        @Builder(toBuilder = true)
        @Data
        public static class Where {
            private String visibility;
            private String userId;
            private List<String> tags;
            private Boolean tagsAnd;
            private String name;
            private String lang;
            private String text;
            private Integer priority;
        }

        private Where where;
        private List<String> orderBy;
        private Long limit;
        private Long offset;

        public static Query.Where.WhereBuilder whereBuilder() {
            return Query.Where.builder();
        }
    }

    static Query.QueryBuilder queryBuilder() {
        return Query.builder();
    }
    List<Pair<CharacterInfo, List<String>>> search(Query query, User user);
    List<Triple<CharacterInfo, List<String>, List<CharacterBackend>>> searchDetails(Query query, User user);
    long count(Query query, User user);
    boolean create(Pair<CharacterInfo, List<String>> characterInfoPair);
    boolean update(Pair<CharacterInfo, List<String>> characterInfoPair);
    boolean hide(Long characterId, User user);
    boolean delete(Long characterId, User user);
    List<Long> delete(User user);
    List<Long> deleteByName(String name, User user);
    CharacterInfo summary(Long characterId);
    Pair<CharacterInfo, List<String>> summary(Long characterId, User user);
    List<Pair<CharacterInfo, List<String>>> summary(Collection<Long> characterIds, User user);
    Triple<CharacterInfo, List<String>, List<CharacterBackend>> details(Long characterId, User user);
    List<Triple<CharacterInfo, List<String>, List<CharacterBackend>>> details(Collection<Long> characterIds, User user);
    List<Triple<Long, Integer, InteractiveStats>> listVersionsByName(String name, User user);
    Long getLatestIdByName(String name, User user);
    Long getLatestIdByUid(String characterUid, User user);
    default Long getLatestIdByUid(String characterUid) {
        return getLatestIdByUid(characterUid, null);
    }
    Long publish(Long characterId, Visibility visibility, User user);
    String getOwner(Long characterId);
    String getOwnerByUid(String characterUid);
    String getUid(Long characterId);
    boolean existsName(String name, User user);

    String addBackend(CharacterBackend characterBackend);
    boolean removeBackend(String characterUid, String characterBackendId);
    boolean updateBackend(CharacterBackend characterBackend);
    boolean setDefaultBackend(String characterUid, String characterBackendId);
    CharacterBackend getDefaultBackend(String characterUid);
    CharacterBackend getBackend(String characterBackendId);
    List<String> listBackendIds(String characterUid);
    List<CharacterBackend> listBackends(String characterUid);
    int removeBackendsByUser(User user);
    String getBackendOwner(String characterBackendId);
    String getBackendCharacterUid(String characterBackendId);
}
