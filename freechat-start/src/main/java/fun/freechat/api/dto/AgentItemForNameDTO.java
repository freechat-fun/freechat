package fun.freechat.api.dto;

import fun.freechat.model.InteractiveStats;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.tuple.Triple;

@Schema(description = "Agent identifier and version information")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgentItemForNameDTO {
    @Schema(description = "agentId")
    private Long agentId;
    @Schema(description = "version")
    private Integer version;
    @Schema(description = "Interactive statistics information")
    InteractiveStatsDTO stats;

    public static AgentItemForNameDTO from(Triple<Long, Integer, InteractiveStats> infoItem) {
        InteractiveStatsDTO stats = InteractiveStatsDTO.from(infoItem.getRight());
        stats.setRequestId(null);
        stats.setGmtCreate(null);
        stats.setGmtModified(null);
        stats.setReferType(null);
        stats.setReferId(null);

        return AgentItemForNameDTO.builder()
                .agentId(infoItem.getLeft())
                .version(infoItem.getMiddle())
                .stats(stats)
                .build();
    }
}
