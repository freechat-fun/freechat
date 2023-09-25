package fun.freechat.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "Configuration information")
@Data
public class AppConfigInfoDTO extends TraceableDTO {
    @Schema(description = "Configuration name")
    private String name;
    @Schema(description = "Configuration format: kv | json | yaml")
    private String format;
    @Schema(description = "Configuration content")
    private String content;
    @Schema(description = "Configuration version")
    private Integer version;
}
