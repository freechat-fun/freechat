package fun.freechat.api.dto;

import fun.freechat.api.util.AccountUtils;
import fun.freechat.api.util.CommonUtils;
import fun.freechat.model.PluginInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;

@Schema(description = "Plugin summary information")
@Data
@EqualsAndHashCode(callSuper = true)
public class PluginSummaryDTO extends TraceableDTO {
    @Schema(description = "Plugin identifier")
    private Long pluginId;
    @Schema(description = "Plugin immutable identifier")
    private String pluginUid;
    @Schema(description = "Creation time")
    private Date gmtCreate;
    @Schema(description = "Modification time")
    private Date gmtModified;
    @Schema(description = "Visibility: private, public, public_org, hidden")
    private String visibility;
    @Schema(description = "Plugin name")
    private String name;
    @Schema(description = "Information of the provider")
    private String provider;
    @Schema(description = "Manifest format, currently supported: dash_scope, open_ai")
    private String manifestFormat;
    @Schema(description = "API description format, currently supported: openapi_v3")
    private String apiFormat;
    @Schema(description = "Plugin owner")
    private String username;
    @Schema(description = "Tag set")
    private List<String> tags;
    @Schema(description = "Supported model set")
    private List<AiModelInfoDTO> aiModels;

    public static PluginSummaryDTO from(
            Triple<PluginInfo, List<String>, List<String>> pluginInfoTriple) {
        if (pluginInfoTriple == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Failed to find plugin!");
        }
        PluginInfo pluginInfo = pluginInfoTriple.getLeft();
        PluginSummaryDTO dto = CommonUtils.convert(pluginInfo, PluginSummaryDTO.class);
        dto.setUsername(AccountUtils.userIdToName(pluginInfoTriple.getLeft().getUserId()));
        dto.setTags(pluginInfoTriple.getMiddle());
        dto.setAiModels(pluginInfoTriple.getRight()
                .stream()
                .map(AiModelInfoDTO::from)
                .peek(aiModelInfo -> aiModelInfo.setRequestId(null))
                .toList());
        return dto;
    }
}
