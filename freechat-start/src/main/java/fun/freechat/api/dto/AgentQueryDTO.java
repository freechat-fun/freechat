package fun.freechat.api.dto;

import fun.freechat.api.util.AccountUtils;
import fun.freechat.service.agent.AgentService;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.List;

@Schema(description = "Agent information query request")
@Data
public class AgentQueryDTO {
    @Schema(name = "AgentQuery.Where", description = "Query condition")
    @Data
    public static class Where {
        @Schema(description = "Visibility: public, public_org (search this organization), private (default)")
        private String visibility;
        @Schema(description = "Effective when searching public, public_org prompts, if not specified, search all users")
        private String username;
        @Schema(description = "Agent configuration format, currently supported: langflow.")
        private String format;
        @Schema(description = "Tags")
        private List<String> tags;
        @Schema(description = "Relationship between tags: and | or (default)")
        @Pattern(regexp = "and|or")
        private String tagsOp;
        @Schema(description = "Model set")
        private List<String> aiModels;
        @Schema(description = "Relationship between model sets: and | or (default)")
        @Pattern(regexp = "and|or")
        private String aiModelsOp;
        @Schema(description = "Name, left match")
        private String name;
        @Schema(description = "Name, description, example, fuzzy matching, any one match is sufficient; public scope + general search for all users does not guarantee real-time.")
        private String text;
    }

    @Schema(description = "Query condition")
    private Where where;
    @Schema(description = """
            Sorting condition, supported sorting fields are:
            - version
            - modifyTime
            - createTime
                       
            Sorting priority follows the list order, default is descending. If ascending is expected, specify after the field, such as: orderBy: [\\"score\\", \\"scoreCount asc\\"] (scoreCount in ascending order)
            """)
    private List<String> orderBy;
    @Schema(description = "Page number, default is 0")
    private Long pageNum;
    @Schema(description = "Number of items per page, 1~50, default is 10")
    private Long pageSize;

    public AgentService.Query toAgentInfoQuery() {
        if (getPageSize() == null || getPageSize() <= 0L) {
            setPageSize(10L);
        } else if (getPageSize() > 100L) {
            setPageSize(100L);
        }
        if (getPageNum() == null || getPageNum() < 0L) {
            setPageNum(0L);
        }
        return AgentService.queryBuilder()
                .where(AgentService.Query.whereBuilder()
                        .visibility(getWhere().getVisibility())
                        .userId(AccountUtils.userNameToId(getWhere().getUsername()))
                        .format(getWhere().getFormat())
                        .tags(getWhere().getTags())
                        .tagsAnd("and".equalsIgnoreCase(getWhere().getTagsOp()))
                        .aiModels(getWhere().getAiModels())
                        .aiModelsAnd("and".equalsIgnoreCase(getWhere().getAiModelsOp()))
                        .name(getWhere().getName())
                        .text(getWhere().getText())
                        .build())
                .orderBy(getOrderBy())
                .limit(getPageSize())
                .offset(getPageSize() * getPageNum())
                .build();
    }
}
