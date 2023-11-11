package fun.freechat.api.dto;

import fun.freechat.api.util.AccountUtils;
import fun.freechat.api.util.CommonUtils;
import fun.freechat.model.CharacterInfo;
import fun.freechat.model.InteractiveStats;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Objects;

@Schema(description = "Character summary content, including interactive statistical information")
@Data
@EqualsAndHashCode(callSuper = true)
public class CharacterSummaryStatsDTO extends CharacterSummaryDTO {
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

    public static CharacterSummaryStatsDTO from(
            Pair<CharacterInfo, List<String>> characterInfoPair, InteractiveStats stats) {
        if (Objects.isNull(characterInfoPair) || Objects.isNull(stats)) {
            return null;
        }
        CharacterSummaryStatsDTO dto =
                CommonUtils.convert(characterInfoPair.getLeft(), CharacterSummaryStatsDTO.class);
        dto.setUsername(AccountUtils.userIdToName(characterInfoPair.getLeft().getUserId()));
        dto.setTags(characterInfoPair.getRight());
        dto.setViewCount(stats.getViewCount());
        dto.setReferCount(stats.getReferCount());
        dto.setRecommendCount(stats.getRecommendCount());
        dto.setScoreCount(stats.getScoreCount());
        dto.setScore(stats.getScore());
        return dto;
    }
}
