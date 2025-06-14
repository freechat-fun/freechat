package fun.freechat.api.dto;

import fun.freechat.api.util.AccountUtils;
import fun.freechat.api.util.CommonUtils;
import fun.freechat.model.InteractiveStats;
import fun.freechat.model.PluginInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;

@Schema(description = "Plugin template summary content, including interactive statistical information")
@Data
@EqualsAndHashCode(callSuper = true)
public class PluginSummaryStatsDTO extends PluginSummaryDTO {
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

    public static PluginSummaryStatsDTO from(
            Triple<PluginInfo, List<String>, List<String>> pluginInfoTriple, InteractiveStats stats) {
        if (pluginInfoTriple == null || stats == null) {
            return null;
        }
        PluginSummaryStatsDTO dto =
                CommonUtils.convert(pluginInfoTriple.getLeft(), PluginSummaryStatsDTO.class);
        dto.setUsername(AccountUtils.userIdToName(pluginInfoTriple.getLeft().getUserId()));
        dto.setTags(pluginInfoTriple.getMiddle());
        dto.setAiModels(pluginInfoTriple.getRight()
                .stream()
                .map(AiModelInfoDTO::from)
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
