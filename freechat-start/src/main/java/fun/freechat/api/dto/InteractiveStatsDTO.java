package fun.freechat.api.dto;

import fun.freechat.api.util.CommonUtils;
import fun.freechat.model.InteractiveStats;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Date;

@Schema(description = "Interactive statistics information")
@Data
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
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

    public InteractiveStatsDTO() {
        viewCount = 0L;
        referCount = 0L;
        recommendCount = 0L;
        scoreCount = 0L;
        score = 0L;
    }

    public static InteractiveStatsDTO from(InteractiveStats stats) {
        if (stats == null) {
            return InteractiveStatsDTO.builder().build();
        }
        return CommonUtils.convert(stats, InteractiveStatsDTO.class);
    }
}
