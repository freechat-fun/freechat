package fun.freechat.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "模型调用参数")
@Data
public class AiModelParamDTO {
    @Schema(description = "API-KEY, higher priority than apiKeyName. Either apiKey or apiKeyName must be specified.")
    private String apiKey;
    @Schema(description = "API-KEY name")
    private String apiKeyName;
    @Schema(description = "Model identifier", requiredMode = REQUIRED)
    private String modelId;
}
