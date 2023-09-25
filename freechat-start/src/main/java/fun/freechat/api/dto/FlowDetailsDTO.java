package fun.freechat.api.dto;

import fun.freechat.api.util.AiModelUtils;
import fun.freechat.api.util.AccountUtils;
import fun.freechat.api.util.CommonUtils;
import fun.freechat.model.FlowInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;
import java.util.Objects;

@Schema(description = "Flow detailed content")
@Data
public class FlowDetailsDTO extends FlowSummaryDTO {
    @Schema(description = "Flow configuration")
    private String config;
    @Schema(description = "Flow example")
    private String example;
    @Schema(description = "Flow parameters, JSON format")
    private String parameters;
    @Schema(description = "Additional information, JSON format")
    private String ext;
    @Schema(description = "Draft content")
    private String draft;

    public static FlowDetailsDTO fromFlowInfo(
            Triple<FlowInfo, List<String>, List<String>> flowInfoTriple) {
        if (Objects.isNull(flowInfoTriple)) {
            return null;
        }
        FlowDetailsDTO flowDetailsDTO =
                CommonUtils.convert(flowInfoTriple.getLeft(), FlowDetailsDTO.class);
        flowDetailsDTO.setUsername(AccountUtils.userIdToName(flowInfoTriple.getLeft().getUserId()));
        flowDetailsDTO.setTags(flowInfoTriple.getMiddle());
        flowDetailsDTO.setAiModels(flowInfoTriple.getRight()
                .stream()
                .map(AiModelUtils::getModelInfoDTO)
                .peek(aiModelInfo -> aiModelInfo.setRequestId(null))
                .toList());
        return flowDetailsDTO;
    }
}
