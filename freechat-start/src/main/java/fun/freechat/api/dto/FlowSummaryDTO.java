package fun.freechat.api.dto;

import fun.freechat.api.util.AiModelUtils;
import fun.freechat.api.util.AccountUtils;
import fun.freechat.api.util.CommonUtils;
import fun.freechat.model.FlowInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Schema(description = "Flow summary information")
@Data
public class FlowSummaryDTO extends TraceableDTO {
    @Schema(description = "Flow identifier")
    private String flowId;
    @Schema(description = "Creation time")
    private Date gmtCreate;
    @Schema(description = "Modification time")
    private Date gmtModified;
    @Schema(description = "Referenced flow")
    private String parentId;
    @Schema(description = "Visibility: private, public, public_org, hidden")
    private String visibility;
    @Schema(description = "Flow format, currently supported: langflow")
    private String format;
    @Schema(description = "Version")
    private Integer version;
    @Schema(description = "Flow name")
    private String name;
    @Schema(description = "Flow description")
    private String description;
    @Schema(description = "Flow owner")
    private String username;
    @Schema(description = "Tag set")
    private List<String> tags;
    @Schema(description = "Supported model set")
    private List<AiModelInfoDTO> aiModels;

    public static FlowSummaryDTO fromFlowInfo(Triple<FlowInfo, List<String>, List<String>> flowInfoTriple) {
        if (Objects.isNull(flowInfoTriple)) {
            return null;
        }
        FlowSummaryDTO flowSummaryDTO =
                CommonUtils.convert(flowInfoTriple.getLeft(), FlowSummaryDTO.class);
        flowSummaryDTO.setUsername(AccountUtils.userIdToName(flowInfoTriple.getLeft().getUserId()));
        flowSummaryDTO.setTags(flowInfoTriple.getMiddle());
        flowSummaryDTO.setAiModels(flowInfoTriple.getRight()
                .stream()
                .map(AiModelUtils::getModelInfoDTO)
                .peek(aiModelInfo -> aiModelInfo.setRequestId(null))
                .toList());
        return flowSummaryDTO;
    }
}
