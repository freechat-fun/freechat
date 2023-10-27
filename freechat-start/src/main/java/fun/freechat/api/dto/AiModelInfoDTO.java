package fun.freechat.api.dto;

import fun.freechat.api.util.CommonUtils;
import fun.freechat.model.AiModelInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Objects;

@Schema(description = "Model information")
@Data
public class AiModelInfoDTO extends TraceableDTO {
    @Schema(description = "Model identifier: [provider]name")
    private String modelId;
    @Schema(description = "Model name")
    private String name;
    @Schema(description = "Model description")
    private String description;
    @Schema(description = "Model provider: hugging_face | open_ai | local_ai | in_process | dash_scope | unknown")
    private String provider;
    @Schema(description = "Model type: text2text | text2chat | text2image | embedding | moderation")
    private String type;

    public static AiModelInfoDTO from(AiModelInfo aiModelInfo) {
        if (Objects.isNull(aiModelInfo)) {
            return null;
        }
        return CommonUtils.convert(aiModelInfo, AiModelInfoDTO.class);
    }

    public static AiModelInfoDTO from(String modelId) {
        AiModelInfoDTO dto =  new AiModelInfoDTO();
        dto.setModelId(modelId);
        dto.setDescription("Not yet supported.");
        dto.setName("unknown");
        dto.setProvider("unknown");
        dto.setType("unknown");
        return dto;
    }

    public AiModelInfo toAiModelInfo() {
        return CommonUtils.convert(this, AiModelInfo.class);
    }
}
