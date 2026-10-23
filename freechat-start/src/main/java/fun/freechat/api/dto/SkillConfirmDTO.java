package fun.freechat.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "Confirm publication of the exact reviewed installation preview")
public class SkillConfirmDTO {
    @Schema(description = "SHA-256 digest returned with the preview")
    @NotBlank
    @Pattern(regexp = "[0-9a-f]{64}")
    private String digest;
}
