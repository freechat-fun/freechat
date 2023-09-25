package fun.freechat.service.flow;

import fun.freechat.model.FlowInfo;
import fun.freechat.model.InteractiveStats;
import fun.freechat.model.User;
import fun.freechat.service.enums.Visibility;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Collection;
import java.util.List;

public interface FlowService {
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

    List<Triple<FlowInfo, List<String>, List<String>>> search(FlowService.Query query, User user);
    List<Triple<FlowInfo, List<String>, List<String>>> searchDetails(FlowService.Query query, User user);
    long count(Query query, User user);
    boolean create(Triple<FlowInfo, List<String>, List<String>> flowInfoTriple);
    List<String> create(List<Triple<FlowInfo, List<String>, List<String>>> flowInfoList);
    boolean update(Triple<FlowInfo, List<String>, List<String>> flowInfoTriple);
    boolean hide(String flowId, User user);
    boolean delete(String flowId, User user);
    List<String> delete(List<String> flowIds, User user);
    Triple<FlowInfo, List<String>, List<String>> summary(String flowId, User user);
    List<Triple<FlowInfo, List<String>, List<String>>> summary(Collection<String> flowIds, User user);
    Triple<FlowInfo, List<String>, List<String>> details(String flowId, User user);
    List<Triple<FlowInfo, List<String>, List<String>>> details(Collection<String> flowIds, User user);
    List<Triple<String, Integer, InteractiveStats>> listVersionsByName(String name, User user);
    String getLatestIdByName(String name, User user);
    String publish(String flowId, Visibility visibility, User user);
    String getOwner(String flowId);
}
