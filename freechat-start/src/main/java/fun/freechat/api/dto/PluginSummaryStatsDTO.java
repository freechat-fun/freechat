package fun.freechat.api.dto;

import fun.freechat.api.util.AccountUtils;
import fun.freechat.api.util.AiModelUtils;
import fun.freechat.api.util.CommonUtils;
import fun.freechat.model.InteractiveStats;
import fun.freechat.model.PluginInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;
import java.util.Objects;

@Schema(description = "Plugin template summary content, including interactive statistical information")
@Data
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

    public static PluginSummaryStatsDTO fromPluginInfoAndStats(
            Triple<PluginInfo, List<String>, List<String>> pluginInfoTriple, InteractiveStats stats) {
        if (Objects.isNull(pluginInfoTriple) || Objects.isNull(stats)) {
            return null;
        }
        PluginSummaryStatsDTO pluginSummaryStatsDTO =
                CommonUtils.convert(pluginInfoTriple.getLeft(), PluginSummaryStatsDTO.class);
        pluginSummaryStatsDTO.setUsername(AccountUtils.userIdToName(pluginInfoTriple.getLeft().getUserId()));
        pluginSummaryStatsDTO.setTags(pluginInfoTriple.getMiddle());
        pluginSummaryStatsDTO.setAiModels(pluginInfoTriple.getRight()
                .stream()
                .map(AiModelUtils::getModelInfoDTO)
                .peek(aiModelInfo -> aiModelInfo.setRequestId(null))
                .toList());
        pluginSummaryStatsDTO.setViewCount(stats.getViewCount());
        pluginSummaryStatsDTO.setReferCount(stats.getReferCount());
        pluginSummaryStatsDTO.setRecommendCount(stats.getRecommendCount());
        pluginSummaryStatsDTO.setScoreCount(stats.getScoreCount());
        pluginSummaryStatsDTO.setScore(stats.getScore());
        return pluginSummaryStatsDTO;
    }
}
