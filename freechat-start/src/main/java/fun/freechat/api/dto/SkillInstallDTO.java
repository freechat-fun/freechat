package fun.freechat.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import java.util.Map;
import lombok.Data;

@Data
@Schema(description = "Install a prompt-only skill bundle from text documents or an LLM-analyzed approved source link")
public class SkillInstallDTO {
    @Schema(description = "Uploaded or pasted text documents including SKILL.md; mutually exclusive with sourceUrl")
    @Size(max = 16)
    private Map<String, String> files;

    @Schema(
            description =
                    "GitHub repository/skill-directory URL, ClawHub skill-page URL, or raw document URL from an allowed host. May include surrounding text or omit the HTTPS scheme; the LLM selects only independently validated source candidates. Provider bundles are pinned and fully validated, not recursively crawled.")
    @Size(max = 2048)
    private String sourceUrl;
}
