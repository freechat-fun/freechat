package fun.freechat.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "Configuration creation information")
@Data
public class AppConfigCreateDTO {
    @Schema(description = "Configuration name", requiredMode = REQUIRED)
    private String name;
    @Schema(description = "Configuration format: kv | json | yaml")
    @Pattern(regexp = "kv|json|yaml")
    private String format;
    @Schema(description = "Configuration content")
    private String content;
}
