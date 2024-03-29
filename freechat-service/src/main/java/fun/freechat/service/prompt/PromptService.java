package fun.freechat.service.prompt;

import dev.langchain4j.data.message.ChatMessage;
import fun.freechat.model.InteractiveStats;
import fun.freechat.model.PromptInfo;
import fun.freechat.model.User;
import fun.freechat.service.enums.PromptFormat;
import fun.freechat.service.enums.PromptType;
import fun.freechat.service.enums.Visibility;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface PromptService {
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
            private List<String> aiModels;
            private Boolean aiModelsAnd;
            private String name;
            private String type;
            private String lang;
            private String text;
        }

        private Where where;
        private List<String> orderBy;
        private Long limit;
        private Long offset;

        public static Where.WhereBuilder whereBuilder() {
            return Where.builder();
        }
    }

    static Query.QueryBuilder queryBuilder() {
        return Query.builder();
    }
    List<Triple<PromptInfo, List<String>, List<String>>> search(Query query, User user);
    List<Triple<PromptInfo, List<String>, List<String>>> searchDetails(Query query, User user);
    long count(Query query, User user);
    boolean create(Triple<PromptInfo, List<String>, List<String>> promptInfoTriple);
    List<Long> create(List<Triple<PromptInfo, List<String>, List<String>>> promptInfoList);
    boolean update(Triple<PromptInfo, List<String>, List<String>> promptInfoTriple);
    boolean hide(Long promptId, User user);
    boolean delete(Long promptId, User user);
    List<Long> delete(List<Long> promptIds, User user);
    List<Long> delete(User user);
    List<Long> deleteByName(String name, User user);
    Triple<PromptInfo, List<String>, List<String>> summary(Long promptId, User user);
    List<Triple<PromptInfo, List<String>, List<String>>> summary(Collection<Long> promptIds, User user);
    Triple<PromptInfo, List<String>, List<String>> details(Long promptId, User user);
    List<Triple<PromptInfo, List<String>, List<String>>> details(Collection<Long> promptIds, User user);
    List<Triple<Long, Integer, InteractiveStats>> listVersionsByName(String name, User user);
    Long getLatestIdByName(String name, User user);
    Long getLatestIdByUid(String promptUid, User user);
    Long publish(Long promptId, Visibility visibility, User user);
    String getOwner(Long promptId);
    String getOwnerByUid(String promptUid);
    String getUid(Long promptId);
    String apply(String promptTemplate, Map<String, Object> variables, PromptFormat format);
    ChatMessage apply(ChatMessage original, Map<String, Object> variables, PromptFormat format);
    ChatPromptContent apply(ChatPromptContent promptContent, Map<String, Object> variables, PromptFormat format);
    Pair<String, PromptType> apply(Long promptId, Map<String, Object> variables, Boolean draft);
    boolean existsName(String name, User user);
}
