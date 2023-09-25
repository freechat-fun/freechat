package fun.freechat.api.dto;

import fun.freechat.api.util.CommonUtils;
import fun.freechat.model.InteractiveStats;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.Objects;

@Schema(description = "Interactive statistics information")
@Data
public class InteractiveStatsDTO extends TraceableDTO {
    @Schema(description = "Creation time")
    private Date gmtCreate;
    @Schema(description = "Modification time")
    private Date gmtModified;
    @Schema(description = "Resource type")
    private String referType;
    @Schema(description = "Resource identifier")
    private String referId;
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

    public static InteractiveStatsDTO fromInteractiveStats(InteractiveStats stats) {
        if (Objects.isNull(stats)) {
            return null;
        }
        return CommonUtils.convert(stats, InteractiveStatsDTO.class);
    }
}
