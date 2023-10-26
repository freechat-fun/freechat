package fun.freechat.api.dto;

import fun.freechat.api.util.AccountUtils;
import fun.freechat.service.prompt.PromptService;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Objects;

@Schema(description = "Prompt template query request")
@Data
public class PromptQueryDTO {
    @Schema(description = "Query condition")
    @Data
    public static class Where {
        @Schema(description = "Visibility: public, public_org (search this organization), private (default)")
        private String visibility;
        @Schema(description = "Effective when searching public, public_org prompts, if not specified, search all users")
        private String username;
        @Schema(description = "Tags")
        private List<String> tags;
        @Schema(description = "Relationship between tags: and | or (default)")
        private String tagsOp;
        @Schema(description = "Model set")
        private List<String> aiModels;
        @Schema(description = "Relationship between model sets: and | or (default)")
        private String aiModelsOp;
        @Schema(description = "Name, left match")
        private String name;
        @Schema(description = "Type, exact match: string (default) | chat")
        private String type;
        @Schema(description = "Language, exact match")
        private String lang;
        @Schema(description = "Name, description, template, example, fuzzy match, any one match is sufficient; public scope + general search for all users does not guarantee real-time.")
        private String text;
    }

    @Schema(description = "Query condition")
    private Where where;
    @Schema(description = """
            Sorting condition, supported sorting fields are:
            - version
            - modifyTime
            - createTime
                       
            Sorting priority follows the list order, default is descending, if ascending is expected, it needs to be specified after the field, such as: orderBy: [\\"score\\", \\"scoreCount asc\\"] (scoreCount in ascending order)
            """)
    private List<String> orderBy;
    @Schema(description = "Page number, default is 0")
    private Long pageNum;
    @Schema(description = "Number of items per page, 1～50, default is 10")
    private Long pageSize;

    public PromptService.Query toPromptInfoQuery() {
        if (Objects.isNull(getPageSize()) || getPageSize() <= 0L) {
            setPageSize(10L);
        } else if (getPageSize() > 100L) {
            setPageSize(100L);
        }
        if (Objects.isNull(getPageNum()) || getPageNum() < 0L) {
            setPageNum(0L);
        }
        return PromptService.queryBuilder()
                .where(PromptService.Query.whereBuilder()
                        .visibility(getWhere().getVisibility())
                        .userId(AccountUtils.userNameToId(getWhere().getUsername()))
                        .tags(getWhere().getTags())
                        .tagsAnd("and".equalsIgnoreCase(getWhere().getTagsOp()))
                        .aiModels(getWhere().getAiModels())
                        .aiModelsAnd("and".equalsIgnoreCase(getWhere().getAiModelsOp()))
                        .name(getWhere().getName())
                        .lang(getWhere().getLang())
                        .type(getWhere().getType())
                        .text(getWhere().getText())
                        .build())
                .orderBy(getOrderBy())
                .limit(getPageSize())
                .offset(getPageSize() * getPageNum())
                .build();
    }
}
