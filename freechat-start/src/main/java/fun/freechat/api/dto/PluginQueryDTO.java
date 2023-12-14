package fun.freechat.api.dto;

import fun.freechat.api.util.AccountUtils;
import fun.freechat.service.plugin.PluginService;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Objects;

@Schema(description = "Plugin information query request")
@Data
public class PluginQueryDTO {
    @Schema(name = "PluginQuery.Where", description = "Query condition")
    @Data
    public static class Where {
        @Schema(description = "Visibility: public, public_org (search this organization), private (default)")
        private String visibility;
        @Schema(description = "Effective when searching public, public_org prompts, if not specified, search all users")
        private String username;
        @Schema(description = "Manifest configuration format, currently supported: dash_scope, open_ai.")
        private String manifestFormat;
        @Schema(description = "API description format, currently supported: openapi_v3.")
        private String apiFormat;
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
        @Schema(description = "Provider, left match")
        private String provider;
        @Schema(description = "Name, provider Information, manifest (real-time pulling is not supported at the moment), fuzzy matching, any one match is sufficient; public scope + general search for all users does not guarantee real-time.")
        private String text;
    }

    @Schema(description = "Query condition")
    private Where where;
    @Schema(description = """
            Ordering condition, supported sorting fields are:
            - modifyTime
            - createTime
                       
            Sorting priority follows the list order, default is descending, if ascending is expected, it needs to be specified after the field, such as: orderBy: [\\"score\\", \\"scoreCount asc\\"] (scoreCount in ascending order)
            """)
    private List<String> orderBy;
    @Schema(description = "Page number, default is 0")
    private Long pageNum;
    @Schema(description = "Page item count, 1～50, default is 10")
    private Long pageSize;

    public PluginService.Query toPluginInfoQuery() {
        if (Objects.isNull(getPageSize()) || getPageSize() <= 0L) {
            setPageSize(10L);
        } else if (getPageSize() > 100L) {
            setPageSize(100L);
        }
        if (Objects.isNull(getPageNum()) || getPageNum() < 0L) {
            setPageNum(0L);
        }
        return PluginService.queryBuilder()
                .where(PluginService.Query.whereBuilder()
                        .visibility(getWhere().getVisibility())
                        .userId(AccountUtils.userNameToId(getWhere().getUsername()))
                        .manifestFormat(getWhere().getManifestFormat())
                        .apiFormat(getWhere().getApiFormat())
                        .tags(getWhere().getTags())
                        .tagsAnd("and".equalsIgnoreCase(getWhere().getTagsOp()))
                        .aiModels(getWhere().getAiModels())
                        .aiModelsAnd("and".equalsIgnoreCase(getWhere().getAiModelsOp()))
                        .name(getWhere().getName())
                        .provider(getWhere().getProvider())
                        .text(getWhere().getText())
                        .build())
                .orderBy(getOrderBy())
                .limit(getPageSize())
                .offset(getPageSize() * getPageNum())
                .build();
    }
}
