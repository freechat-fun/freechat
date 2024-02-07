package fun.freechat.api.dto;

import fun.freechat.api.util.AccountUtils;
import fun.freechat.api.util.AiModelUtils;
import fun.freechat.api.util.CommonUtils;
import fun.freechat.model.AgentInfo;
import fun.freechat.model.InteractiveStats;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;
import java.util.Objects;

@Schema(description = "Agent template summary content, including interactive statistical information")
@Data
@EqualsAndHashCode(callSuper = true)
public class AgentSummaryStatsDTO extends AgentSummaryDTO {
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

    public static AgentSummaryStatsDTO from(
            Triple<AgentInfo, List<String>, List<String>> agentInfoTriple, InteractiveStats stats) {
        if (Objects.isNull(agentInfoTriple) || Objects.isNull(stats)) {
            return null;
        }
        AgentSummaryStatsDTO dto =
                CommonUtils.convert(agentInfoTriple.getLeft(), AgentSummaryStatsDTO.class);
        dto.setUsername(AccountUtils.userIdToName(agentInfoTriple.getLeft().getUserId()));
        dto.setTags(agentInfoTriple.getMiddle());
        dto.setAiModels(agentInfoTriple.getRight()
                .stream()
                .map(AiModelUtils::getModelInfoDTO)
                .peek(aiModelInfo -> aiModelInfo.setRequestId(null))
                .toList());
        dto.setViewCount(stats.getViewCount());
        dto.setReferCount(stats.getReferCount());
        dto.setRecommendCount(stats.getRecommendCount());
        dto.setScoreCount(stats.getScoreCount());
        dto.setScore(stats.getScore());
        return dto;
    }
}
