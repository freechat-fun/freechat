package fun.freechat.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "Request data for adding new model credential information")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiApiKeyCreateDTO {
    @Schema(description = "Credential name", requiredMode = REQUIRED)
    private String name;
    @Schema(description = "Model provider: hugging_face | open_ai | azure_open_ai | dash_scope | ollama | unknown",
            requiredMode = REQUIRED)
    private String provider;
    @Schema(description = "Credential content", requiredMode = REQUIRED)
    private String token;
    @Schema(description = "Whether to enable (enabled by default)")
    private Boolean enabled;
}
