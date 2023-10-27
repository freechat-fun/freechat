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
    boolean hide(String characterId, User user);
    boolean delete(String characterId, User user);
    List<String> delete(User user);
    CharacterInfo summary(String characterId);
    Pair<CharacterInfo, List<String>> summary(String characterId, User user);
    List<Pair<CharacterInfo, List<String>>> summary(Collection<String> characterIds, User user);
    Triple<CharacterInfo, List<String>, List<CharacterBackend>> details(String characterId, User user);
    List<Triple<CharacterInfo, List<String>, List<CharacterBackend>>> details(Collection<String> characterIds, User user);
    List<Triple<String, Integer, InteractiveStats>> listVersionsByName(String name, User user);
    String getLatestIdByName(String name, User user);
    String publish(String characterId, Visibility visibility, User user);
    String getOwner(String characterId);

    String addBackend(CharacterBackend characterBackend);
    boolean removeBackend(String characterId, String characterBackendId);
    boolean updateBackend(CharacterBackend characterBackend);
    boolean setDefaultBackend(String characterId, String characterBackendId);
    CharacterBackend getDefaultBackend(String characterId);
    CharacterBackend getBackend(String characterBackendId);
    List<String> listBackendIds(String characterId);
    String getBackendOwner(String characterBackendId);
    String getBackendCharacterId(String characterBackendId);
}
