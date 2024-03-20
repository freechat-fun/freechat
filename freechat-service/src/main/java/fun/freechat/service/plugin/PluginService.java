package fun.freechat.service.plugin;

import fun.freechat.model.PluginInfo;
import fun.freechat.model.User;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Collection;
import java.util.List;

public interface PluginService {
    @Builder(toBuilder = true)
    @Data
    class Query {
        @Builder(toBuilder = true)
        @Data
        public static class Where {
            private String visibility;
            private String userId;
            private String manifestFormat;
            private String apiFormat;
            private List<String> tags;
            private Boolean tagsAnd;
            private List<String> aiModels;
            private Boolean aiModelsAnd;
            private String name;
            private String provider;
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

    List<Triple<PluginInfo, List<String>, List<String>>> search(Query query, User user);
    List<Triple<PluginInfo, List<String>, List<String>>> searchDetails(Query query, User user);
    long count(Query query, User user);
    boolean create(Triple<PluginInfo, List<String>, List<String>> pluginInfoTriple);
    List<Long> create(List<Triple<PluginInfo, List<String>, List<String>>> pluginInfoList);
    boolean update(Triple<PluginInfo, List<String>, List<String>> pluginInfoTriple);
    boolean hide(Long pluginId, User user);
    boolean delete(Long pluginId, User user);
    List<Long> delete(List<Long> pluginIds, User user);
    Triple<PluginInfo, List<String>, List<String>> summary(Long pluginId, User user);
    List<Triple<PluginInfo, List<String>, List<String>>> summary(Collection<Long> pluginIds, User user);
    Triple<PluginInfo, List<String>, List<String>> details(Long pluginId, User user);
    List<Triple<PluginInfo, List<String>, List<String>>> details(Collection<Long> pluginIds, User user);
    Long getIdByUid(String pluginUid, User user);
    String getOwner(Long pluginId);
    String getOwnerByUid(String pluginUid);
    String getUid(Long pluginId);
}
