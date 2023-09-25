package fun.freechat.api.dto;

import fun.freechat.api.util.AccountUtils;
import fun.freechat.api.util.CommonUtils;
import fun.freechat.model.CharacterInfo;
import fun.freechat.model.InteractiveStats;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Objects;

@Schema(description = "Character summary content, including interactive statistical information")
@Data
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

    public static CharacterSummaryStatsDTO fromCharacterInfoAndStats(
            Pair<CharacterInfo, List<String>> characterInfoPair, InteractiveStats stats) {
        if (Objects.isNull(characterInfoPair) || Objects.isNull(stats)) {
            return null;
        }
        CharacterSummaryStatsDTO characterSummaryStatsDTO =
                CommonUtils.convert(characterInfoPair.getLeft(), CharacterSummaryStatsDTO.class);
        characterSummaryStatsDTO.setUsername(AccountUtils.userIdToName(characterInfoPair.getLeft().getUserId()));
        characterSummaryStatsDTO.setTags(characterInfoPair.getRight());
        characterSummaryStatsDTO.setViewCount(stats.getViewCount());
        characterSummaryStatsDTO.setReferCount(stats.getReferCount());
        characterSummaryStatsDTO.setRecommendCount(stats.getRecommendCount());
        characterSummaryStatsDTO.setScoreCount(stats.getScoreCount());
        characterSummaryStatsDTO.setScore(stats.getScore());
        return characterSummaryStatsDTO;
    }
}
