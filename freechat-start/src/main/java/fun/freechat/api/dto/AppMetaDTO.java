package fun.freechat.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Application metadata")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppMetaDTO {
    @Schema(description = "Application name")
    private String name;
    @Schema(description = "Application version")
    private String version;
    @Schema(description = "Build time")
    private String buildTimestamp;
    @Schema(description = "Build number")
    private String buildNumber;
    @Schema(description = "Commit URL")
    private String commitUrl;
    @Schema(description = "Release notes")
    private String releaseNoteUrl;
    @Schema(description = "Running environment")
    private String runningEnv;
}
