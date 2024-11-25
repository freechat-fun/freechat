package fun.freechat.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "Prompt AI service information")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromptAiParamDTO {
    @Schema(description = "Complete input content, priority: prompt > promptTemplate > promptRef")
    private String prompt;
    @Schema(description = "Prompt template content")
    private PromptTemplateDTO promptTemplate;
    @Schema(description = "Prompt reference information")
    private PromptRefDTO promptRef;
    @Schema(description = "Model call parameters, the actual supported fields are related to modelId, depending on the model provider, specific fields can refer to: OpenAiParamDTO, QwenParamDTO", requiredMode = REQUIRED)
    private Map<String, Object> params;
}
