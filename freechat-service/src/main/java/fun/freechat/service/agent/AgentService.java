package fun.freechat.service.agent;

import fun.freechat.model.AgentInfo;
import fun.freechat.model.InteractiveStats;
import fun.freechat.model.User;
import fun.freechat.service.enums.Visibility;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Collection;
import java.util.List;

public interface AgentService {
    @Builder(toBuilder = true)
    @Data
    class Query {
        @Builder(toBuilder = true)
        @Data
        public static class Where {
            private String visibility;
            private String userId;
            private String format;
            private List<String> tags;
            private Boolean tagsAnd;
            private List<String> aiModels;
            private Boolean aiModelsAnd;
            private String name;
            private String text;
        }

        private Query.Where where;
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

    List<Triple<AgentInfo, List<String>, List<String>>> search(AgentService.Query query, User user);
    List<Triple<AgentInfo, List<String>, List<String>>> searchDetails(AgentService.Query query, User user);
    long count(Query query, User user);
    boolean create(Triple<AgentInfo, List<String>, List<String>> AgentInfoTriple);
    List<String> create(List<Triple<AgentInfo, List<String>, List<String>>> AgentInfoList);
    boolean update(Triple<AgentInfo, List<String>, List<String>> AgentInfoTriple);
    boolean hide(String agentId, User user);
    boolean delete(String agentId, User user);
    List<String> delete(List<String> agentIds, User user);
    Triple<AgentInfo, List<String>, List<String>> summary(String agentId, User user);
    List<Triple<AgentInfo, List<String>, List<String>>> summary(Collection<String> agentIds, User user);
    Triple<AgentInfo, List<String>, List<String>> details(String agentId, User user);
    List<Triple<AgentInfo, List<String>, List<String>>> details(Collection<String> agentIds, User user);
    List<Triple<String, Integer, InteractiveStats>> listVersionsByName(String name, User user);
    String getLatestIdByName(String name, User user);
    String publish(String agentId, Visibility visibility, User user);
    String getOwner(String agentId);
}
