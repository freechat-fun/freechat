package fun.freechat.api.dto;

import fun.freechat.api.util.AccountUtils;
import fun.freechat.api.util.AiModelUtils;
import fun.freechat.api.util.CommonUtils;
import fun.freechat.model.InteractiveStats;
import fun.freechat.model.PromptInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;

@Schema(description = "Prompt template summary content, including interactive statistical information")
@Data
@EqualsAndHashCode(callSuper = true)
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

    public static PromptSummaryStatsDTO from(
            Triple<PromptInfo, List<String>, List<String>> promptInfoTriple, InteractiveStats stats) {
        if (promptInfoTriple == null || stats == null) {
            return null;
        }
        PromptSummaryStatsDTO dto =
                CommonUtils.convert(promptInfoTriple.getLeft(), PromptSummaryStatsDTO.class);
        dto.setUsername(AccountUtils.userIdToName(promptInfoTriple.getLeft().getUserId()));
        dto.setTags(promptInfoTriple.getMiddle());
        dto.setAiModels(promptInfoTriple.getRight()
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
