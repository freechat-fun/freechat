package fun.freechat.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "Request data for creating/updating model information")
@Data
public class AiModelInfoUpdateDTO {
    @Schema(description = "Model name", requiredMode = REQUIRED)
    private String name;
    @Schema(description = "Model description")
    private String description;
    @Schema(description = "Model provider: hugging_face | open_ai | azure_open_ai | local_ai | in_process | dash_scope | unknown")
    private String provider;
    @Schema(description = "Model type: text2text | text2chat | text2image | embedding | moderation")
    private String type;
}
