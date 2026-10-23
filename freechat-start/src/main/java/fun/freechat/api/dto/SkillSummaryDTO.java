package fun.freechat.api.dto;

import fun.freechat.service.skills.SkillService;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.Set;
import lombok.Data;

@Data
@Schema(description = "An installed, private, prompt-only skill")
public class SkillSummaryDTO {
    @Schema(description = "Opaque skill identifier")
    private String skillId;

    @Schema(description = "Current immutable revision identifier")
    private String revision;

    @Schema(description = "Skill name")
    private String name;

    @Schema(description = "Skill description")
    private String description;

    @Schema(description = "Descriptive labels, not permission grants")
    private Set<String> tags;

    @Schema(description = "Last publication time")
    private Instant modified;

    public static SkillSummaryDTO from(SkillService.Summary value) {
        SkillSummaryDTO result = new SkillSummaryDTO();
        result.setSkillId(value.skillId());
        result.setRevision(value.revision());
        result.setName(value.name());
        result.setDescription(value.description());
        result.setTags(Set.of("user", "prompt-only"));
        result.setModified(value.modified());
        return result;
    }
}
