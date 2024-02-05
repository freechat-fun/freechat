package fun.freechat.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import fun.freechat.model.InteractiveStats;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.tuple.Triple;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Schema(description = "Character identifier and version information")
@Data
@JsonInclude(NON_NULL)
public class CharacterItemForNameDTO {
    @Schema(description = "characterId")
    private String characterId;
    @Schema(description = "version")
    private Integer version;
    @Schema(description = "Interactive statistics information")
    InteractiveStatsDTO stats;

    public static CharacterItemForNameDTO from(Triple<String, Integer, InteractiveStats> infoItem) {
        CharacterItemForNameDTO dto = new CharacterItemForNameDTO();
        dto.setCharacterId(infoItem.getLeft());
        dto.setVersion(infoItem.getMiddle());
        InteractiveStatsDTO stats = InteractiveStatsDTO.from(infoItem.getRight());
        stats.setRequestId(null);
        stats.setGmtCreate(null);
        stats.setGmtModified(null);
        stats.setReferType(null);
        stats.setReferId(null);
        dto.setStats(stats);

        return dto;
    }
}
