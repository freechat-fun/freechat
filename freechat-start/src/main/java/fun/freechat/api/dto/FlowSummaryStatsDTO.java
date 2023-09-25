package fun.freechat.api.dto;

import fun.freechat.api.util.AccountUtils;
import fun.freechat.api.util.AiModelUtils;
import fun.freechat.api.util.CommonUtils;
import fun.freechat.model.FlowInfo;
import fun.freechat.model.InteractiveStats;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;
import java.util.Objects;

@Schema(description = "Flow template summary content, including interactive statistical information")
@Data
public class FlowSummaryStatsDTO extends FlowSummaryDTO {
    @Schema(description = "View count")
    private Long viewCount;
    @Schema(description = "Reference count")
    private Long referCount;
    @Schema(description = "Recommendation count")
    private Long recommendCount;
    @Schema(description = "Score count")
    private Long scoreCount;
    @Schema(description = "Average score")
    private Long score;

    public static FlowSummaryStatsDTO fromFlowInfoAndStats(
            Triple<FlowInfo, List<String>, List<String>> flowInfoTriple, InteractiveStats stats) {
        if (Objects.isNull(flowInfoTriple) || Objects.isNull(stats)) {
            return null;
        }
        FlowSummaryStatsDTO flowSummaryStatsDTO =
                CommonUtils.convert(flowInfoTriple.getLeft(), FlowSummaryStatsDTO.class);
        flowSummaryStatsDTO.setUsername(AccountUtils.userIdToName(flowInfoTriple.getLeft().getUserId()));
        flowSummaryStatsDTO.setTags(flowInfoTriple.getMiddle());
        flowSummaryStatsDTO.setAiModels(flowInfoTriple.getRight()
                .stream()
                .map(AiModelUtils::getModelInfoDTO)
                .peek(aiModelInfo -> aiModelInfo.setRequestId(null))
                .toList());
        flowSummaryStatsDTO.setViewCount(stats.getViewCount());
        flowSummaryStatsDTO.setReferCount(stats.getReferCount());
        flowSummaryStatsDTO.setRecommendCount(stats.getRecommendCount());
        flowSummaryStatsDTO.setScoreCount(stats.getScoreCount());
        flowSummaryStatsDTO.setScore(stats.getScore());
        return flowSummaryStatsDTO;
    }
}
