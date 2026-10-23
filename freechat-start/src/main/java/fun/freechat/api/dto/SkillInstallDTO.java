package fun.freechat.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import java.util.Map;
import lombok.Data;

@Data
@Schema(description = "Install a prompt-only skill from either text documents or an operator-approved HTTPS source")
public class SkillInstallDTO {
    @Schema(description = "Uploaded or pasted text documents including SKILL.md; mutually exclusive with sourceUrl")
    @Size(max = 16)
    private Map<String, String> files;

    @Schema(description = "Approved HTTPS URL of a raw SKILL.md document; no recursive fetching or redirects")
    @Size(max = 2048)
    private String sourceUrl;
}
