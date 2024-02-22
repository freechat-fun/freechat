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
    List<String> create(List<Triple<PromptInfo, List<String>, List<String>>> promptInfoList);
    boolean update(Triple<PromptInfo, List<String>, List<String>> promptInfoTriple);
    boolean hide(String promptId, User user);
    boolean delete(String promptId, User user);
    List<String> delete(List<String> promptIds, User user);
    List<String> delete(User user);
    List<String> deleteByName(String name, User user);
    Triple<PromptInfo, List<String>, List<String>> summary(String promptId, User user);
    List<Triple<PromptInfo, List<String>, List<String>>> summary(Collection<String> promptIds, User user);
    Triple<PromptInfo, List<String>, List<String>> details(String promptId, User user);
    List<Triple<PromptInfo, List<String>, List<String>>> details(Collection<String> promptIds, User user);
    List<Triple<String, Integer, InteractiveStats>> listVersionsByName(String name, User user);
    String getLatestIdByName(String name, User user);
    String publish(String promptId, Visibility visibility, User user);
    String getOwner(String promptId);
    String apply(String promptTemplate, Map<String, Object> variables, PromptFormat format);
    ChatMessage apply(ChatMessage original, Map<String, Object> variables, PromptFormat format);
    ChatPromptContent apply(ChatPromptContent promptContent, Map<String, Object> variables, PromptFormat format);
    Pair<String, PromptType> apply(String promptId, Map<String, Object> variables, Boolean draft);
    boolean existsName(String name, User user);
}
