package fun.freechat.api.dto;

import fun.freechat.service.skills.SkillService;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Owner-only contents of an installed prompt skill")
public class SkillDetailsDTO extends SkillSummaryDTO {
    @Schema(description = "Logical document names and exact UTF-8 text; never server filesystem paths")
    private Map<String, String> files;

    @Schema(description = "SHA-256 digest of the complete validated bundle")
    private String digest;

    public static SkillDetailsDTO from(SkillService.Installed value) {
        SkillDetailsDTO result = new SkillDetailsDTO();
        result.setSkillId(value.skillId());
        result.setRevision(value.revision());
        result.setName(value.bundle().name());
        result.setDescription(value.bundle().description());
        result.setTags(value.bundle().tags());
        result.setModified(value.modified());
        result.setFiles(value.bundle().files());
        result.setDigest(value.bundle().digest());
        return result;
    }
}
