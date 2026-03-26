package fun.freechat.api.dto;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "Model parameters")
@Data
public class AiModelParamDTO {
    @Schema(description = "API-KEY, higher priority than apiKeyName. Either apiKey or apiKeyName must be specified.")
    private String apiKey;

    @Schema(description = "API-KEY name")
    private String apiKeyName;

    @Schema(description = "Model identifier", requiredMode = REQUIRED)
    private String modelId;
}
