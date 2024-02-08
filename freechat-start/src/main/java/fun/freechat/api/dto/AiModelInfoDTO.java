package fun.freechat.api.dto;

import fun.freechat.api.util.CommonUtils;
import fun.freechat.model.AiModelInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Schema(description = "Model information")
@Data
@EqualsAndHashCode(callSuper = true)
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

    // [provider]modelName|type
    private static final Pattern MODEL_ID_PATTERN = Pattern.compile("\\[(.+?)\\](.+?)(\\|([^|]*))?");

    public static AiModelInfoDTO from(AiModelInfo aiModelInfo) {
        if (Objects.isNull(aiModelInfo)) {
            return null;
        }
        return CommonUtils.convert(aiModelInfo, AiModelInfoDTO.class);
    }

    public static AiModelInfoDTO from(String modelId) {
        AiModelInfoDTO dto =  new AiModelInfoDTO();
        dto.setModelId(modelId);
        dto.setDescription("Unknown model.");
        Matcher m = MODEL_ID_PATTERN.matcher(modelId);
        if (m.matches()) {
            dto.setProvider(m.group(1));
            dto.setName(m.group(2));
            dto.setType(StringUtils.isBlank(m.group(4)) ? "text2chat" : m.group(4));
        } else {
            dto.setProvider("unknown");
            dto.setName("unknown");
            dto.setType("unknown");
        }
        return dto;
    }

    public AiModelInfo toAiModelInfo() {
        return CommonUtils.convert(this, AiModelInfo.class);
    }
}
