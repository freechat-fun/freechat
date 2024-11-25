package fun.freechat.api.dto;

import fun.freechat.model.InteractiveStats;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.tuple.Triple;

@Schema(description = "Character identifier and version information")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CharacterItemForNameDTO {
    @Schema(description = "characterId")
    private Long characterId;
    @Schema(description = "version")
    private Integer version;
    @Schema(description = "Interactive statistics information")
    InteractiveStatsDTO stats;

    public static CharacterItemForNameDTO from(Triple<Long, Integer, InteractiveStats> infoItem) {
        InteractiveStatsDTO stats = InteractiveStatsDTO.from(infoItem.getRight());
        stats.setRequestId(null);
        stats.setGmtCreate(null);
        stats.setGmtModified(null);
        stats.setReferType(null);
        stats.setReferId(null);

        return CharacterItemForNameDTO.builder()
                .characterId(infoItem.getLeft())
                .version(infoItem.getMiddle())
                .stats(stats)
                .build();
    }
}
