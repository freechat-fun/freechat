package fun.freechat.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Map;
import lombok.Data;

@Data
@Schema(description = "Replace a skill only after the complete resulting bundle is validated and reviewed again")
public class SkillUpdateDTO {
    @Schema(description = "Current revision returned by getSkill; stale saves are rejected")
    @NotBlank
    @Size(max = 64)
    private String expectedRevision;

    @Schema(description = "Complete resulting document set, including unchanged files")
    @NotNull
    @Size(min = 1, max = 16)
    private Map<String, String> files;
}
