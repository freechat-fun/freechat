package fun.freechat.api.dto;

import fun.freechat.service.skills.SkillService;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.Map;
import lombok.Data;

@Data
@Schema(description = "Reviewed installation preview; confirmation is bound to its exact digest")
public class SkillDraftDTO {
    @Schema(description = "Owner-scoped single-use draft identifier")
    private String draftId;

    @Schema(description = "Server-assigned skill identifier")
    private String skillId;

    @Schema(description = "Revision being replaced, absent for a new skill")
    private String expectedRevision;

    @Schema(description = "Validated skill name")
    private String name;

    @Schema(description = "Validated skill description")
    private String description;

    @Schema(description = "Complete staged document set for confirmation")
    private Map<String, String> files;

    @Schema(description = "Digest to supply when confirming this preview")
    private String digest;

    @Schema(description = "Confirmation expiry time")
    private Instant expiresAt;

    @Schema(description = "Resolved source URL of the imported skill, when installed from a link")
    private String sourceUrl;

    @Schema(description = "Pinned repository commit or registry version of the imported bundle")
    private String sourceRevision;

    public static SkillDraftDTO from(SkillService.Draft value) {
        SkillDraftDTO result = new SkillDraftDTO();
        result.setDraftId(value.draftId());
        result.setSkillId(value.skillId());
        result.setExpectedRevision(value.expectedRevision());
        result.setName(value.bundle().name());
        result.setDescription(value.bundle().description());
        result.setFiles(value.bundle().files());
        result.setDigest(value.bundle().digest());
        result.setExpiresAt(value.expiresAt());
        return result;
    }
}
