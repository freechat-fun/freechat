package fun.freechat.api.dto;

import fun.freechat.api.util.CommonUtils;
import fun.freechat.model.AiModelInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    // [provider]modelName|type
    private static final Pattern MODEL_ID_PATTERN =
            Pattern.compile("\\[(.+?)\\](.+?)(\\|([^|]*))?");

    public static AiModelInfoDTO from(AiModelInfo aiModelInfo) {
        if (aiModelInfo == null) {
            return null;
        }
        return CommonUtils.convert(aiModelInfo, AiModelInfoDTO.class);
    }

    public static AiModelInfoDTO from(String modelId) {
        Matcher m = MODEL_ID_PATTERN.matcher(modelId);
        return m.matches() ?
                AiModelInfoDTO.builder()
                        .modelId(modelId)
                        .description("Unknown model.")
                        .provider(m.group(1))
                        .name(m.group(2))
                        .type(StringUtils.isBlank(m.group(4)) ? "text2chat" : m.group(4))
                        .build() :
                AiModelInfoDTO.builder()
                        .modelId(modelId)
                        .description("Unknown model.")
                        .provider("unknown")
                        .name("unknown")
                        .type("unknown")
                        .build();
    }

    public AiModelInfo toAiModelInfo() {
        return CommonUtils.convert(this, AiModelInfo.class);
    }
}
