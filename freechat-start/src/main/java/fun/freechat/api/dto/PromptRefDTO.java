package fun.freechat.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "Prompt reference information")
@Data
public class PromptRefDTO {
    @Schema(description = "Prompt identifier", requiredMode = REQUIRED)
    private Long promptId;
    @Schema(description = "Variables applied to the template, can be empty")
    private Map<String, Object> variables;
    @Schema(description = "Whether to use draft content")
    private Boolean draft;

    public static PromptRefDTO from(Long promptId, Map<String, Object> variables, Boolean draft) {
        PromptRefDTO dto = new PromptRefDTO();
        dto.setPromptId(promptId);
        dto.setVariables(variables);
        dto.setDraft(draft);
        return dto;
    }
}
