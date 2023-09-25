package fun.freechat.api.dto;

import fun.freechat.model.InteractiveStats;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Objects;

@Schema(description = "Flow identifier and version information")
@Data
public class FlowItemForNameDTO {
    @Schema(description = "flowId")
    private String flowId;
    @Schema(description = "version")
    private Integer version;
    @Schema(description = "Interactive statistics information")
    InteractiveStatsDTO stats;

    public static FlowItemForNameDTO fromInfoItem(Triple<String, Integer, InteractiveStats> infoItem) {
        FlowItemForNameDTO dto = new FlowItemForNameDTO();
        dto.setFlowId(infoItem.getLeft());
        dto.setVersion(infoItem.getMiddle());
        InteractiveStatsDTO stats = InteractiveStatsDTO.fromInteractiveStats(infoItem.getRight());
        if (Objects.nonNull(stats)) {
            stats.setRequestId(null);
            stats.setGmtCreate(null);
            stats.setGmtModified(null);
            stats.setReferType(null);
            stats.setReferId(null);
        }
        dto.setStats(stats);

        return dto;
    }
}
