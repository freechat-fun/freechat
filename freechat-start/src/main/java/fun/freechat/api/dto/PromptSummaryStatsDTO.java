package fun.freechat.api.dto;

import fun.freechat.api.util.AccountUtils;
import fun.freechat.api.util.AiModelUtils;
import fun.freechat.api.util.CommonUtils;
import fun.freechat.model.InteractiveStats;
import fun.freechat.model.PromptInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;
import java.util.Objects;

@Schema(description = "Prompt template summary content, including interactive statistical information")
@Data
public class PromptSummaryStatsDTO extends PromptSummaryDTO {
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

    public static PromptSummaryStatsDTO fromPromptInfoAndStats(
            Triple<PromptInfo, List<String>, List<String>> promptInfoTriple, InteractiveStats stats) {
        if (Objects.isNull(promptInfoTriple) || Objects.isNull(stats)) {
            return null;
        }
        PromptSummaryStatsDTO promptSummaryStatsDTO =
                CommonUtils.convert(promptInfoTriple.getLeft(), PromptSummaryStatsDTO.class);
        promptSummaryStatsDTO.setUsername(AccountUtils.userIdToName(promptInfoTriple.getLeft().getUserId()));
        promptSummaryStatsDTO.setTags(promptInfoTriple.getMiddle());
        promptSummaryStatsDTO.setAiModels(promptInfoTriple.getRight()
                .stream()
                .map(AiModelUtils::getModelInfoDTO)
                .peek(aiModelInfo -> aiModelInfo.setRequestId(null))
                .toList());
        promptSummaryStatsDTO.setViewCount(stats.getViewCount());
        promptSummaryStatsDTO.setReferCount(stats.getReferCount());
        promptSummaryStatsDTO.setRecommendCount(stats.getRecommendCount());
        promptSummaryStatsDTO.setScoreCount(stats.getScoreCount());
        promptSummaryStatsDTO.setScore(stats.getScore());
        return promptSummaryStatsDTO;
    }
}
