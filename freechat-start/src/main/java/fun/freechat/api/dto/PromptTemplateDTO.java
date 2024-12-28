package fun.freechat.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Schema(description = "Prompt template content")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromptTemplateDTO {
    @Schema(description = "Prompt text template content, choose one between this and chatTemplate field, priority: template > chatTemplate")
    private String template;
    @Schema(description = "Prompt chat template content")
    private ChatPromptContentDTO chatTemplate;
    @Schema(description = "Variables applied to the template, can be empty")
    private Map<String, Object> variables;
    @Schema(description = "Prompt format: mustache (default) | f_string")
    @Pattern(regexp = "mustache|f_string")
    private String format;
}
