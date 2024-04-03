package fun.freechat.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import fun.freechat.model.InteractiveStats;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.tuple.Triple;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Schema(description = "Prompt identifier and version information")
@Data
@JsonInclude(NON_NULL)
public class PromptItemForNameDTO {
    @Schema(description = "promptId")
    private Long promptId;
    @Schema(description = "version")
    private Integer version;
    @Schema(description = "Interactive statistics information")
    InteractiveStatsDTO stats;

    public static PromptItemForNameDTO from(Triple<Long, Integer, InteractiveStats> infoItem) {
        PromptItemForNameDTO dto = new PromptItemForNameDTO();
        dto.setPromptId(infoItem.getLeft());
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
