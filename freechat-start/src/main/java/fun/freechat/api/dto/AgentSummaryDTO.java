package fun.freechat.api.dto;

import fun.freechat.api.util.AiModelUtils;
import fun.freechat.api.util.AccountUtils;
import fun.freechat.api.util.CommonUtils;
import fun.freechat.model.AgentInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Schema(description = "Agent summary information")
@Data
@EqualsAndHashCode(callSuper = true)
public class AgentSummaryDTO extends TraceableDTO {
    @Schema(description = "Agent identifier, will change after publish")
    private Long agentId;
    @Schema(description = "Agent immutable identifier")
    private String agentUid;
    @Schema(description = "Creation time")
    private Date gmtCreate;
    @Schema(description = "Modification time")
    private Date gmtModified;
    @Schema(description = "Referenced agent")
    private String parentUid;
    @Schema(description = "Visibility: private, public, public_org, hidden")
    private String visibility;
    @Schema(description = "Agent format, currently supported: langflow")
    private String format;
    @Schema(description = "Version")
    private Integer version;
    @Schema(description = "Agent name")
    private String name;
    @Schema(description = "Agent description")
    private String description;
    @Schema(description = "Agent owner")
    private String username;
    @Schema(description = "Tag set")
    private List<String> tags;
    @Schema(description = "Supported model set")
    private List<AiModelInfoDTO> aiModels;

    public static AgentSummaryDTO from(Triple<AgentInfo, List<String>, List<String>> agentInfoTriple) {
        if (Objects.isNull(agentInfoTriple)) {
            return null;
        }
        AgentSummaryDTO dto =
                CommonUtils.convert(agentInfoTriple.getLeft(), AgentSummaryDTO.class);
        dto.setUsername(AccountUtils.userIdToName(agentInfoTriple.getLeft().getUserId()));
        dto.setTags(agentInfoTriple.getMiddle());
        dto.setAiModels(agentInfoTriple.getRight()
                .stream()
                .map(AiModelUtils::getModelInfoDTO)
                .peek(aiModelInfo -> aiModelInfo.setRequestId(null))
                .toList());
        return dto;
    }
}
