package fun.freechat.api.dto;

import fun.freechat.api.util.CommonUtils;
import fun.freechat.service.ai.AiModelInfo;
import fun.freechat.service.util.InfoUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Schema(description = "Model information")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class AiModelInfoDTO extends TraceableDTO {
    @Schema(description = "Model identifier: [provider]name")
    private String modelId;
    @Schema(description = "Model name")
    private String name;
    @Schema(description = "Model description")
    private String description;
    @Schema(description = "Model provider: hugging_face | open_ai | azure_open_ai | dash_scope | ollama | unknown")
    private String provider;
    @Schema(description = "Model type: text2text | text2chat | text2image | embedding | moderation")
    private String type;

    public static AiModelInfoDTO from(AiModelInfo aiModelInfo) {
        if (aiModelInfo == null) {
            return null;
        }
        return CommonUtils.convert(aiModelInfo, AiModelInfoDTO.class);
    }

    public static AiModelInfoDTO from(String modelId) {
        return from(InfoUtils.toAiModelInfo(modelId));
    }

    public AiModelInfo toAiModelInfo() {
        return CommonUtils.convert(this, AiModelInfo.class);
    }
}
