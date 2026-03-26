package fun.freechat.api.dto;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Prompt reference information")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromptRefDTO {
    @Schema(description = "Prompt identifier", requiredMode = REQUIRED)
    private Long promptId;

    @Schema(description = "Variables applied to the template, can be empty")
    private Map<String, Object> variables;

    @Schema(description = "Whether to use draft content")
    private Boolean draft;

    public static PromptRefDTO from(Long promptId, Map<String, Object> variables, Boolean draft) {
        return PromptRefDTO.builder()
                .promptId(promptId)
                .variables(variables)
                .draft(draft)
                .build();
    }
}
