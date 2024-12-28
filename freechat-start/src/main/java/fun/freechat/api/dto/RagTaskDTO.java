package fun.freechat.api.dto;

import fun.freechat.api.util.AccountUtils;
import fun.freechat.api.util.CommonUtils;
import fun.freechat.api.util.FileUtils;
import fun.freechat.model.RagTask;
import fun.freechat.service.enums.SourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.AccessDeniedException;

@Schema(description = "RAG task information")
@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RagTaskDTO {
    @Schema(description = "Source type: file (default) | url")
    @Pattern(regexp = "file|url")
    private String sourceType;
    @Schema(description = "Source information, url, or a key for file")
    private String source;
    @Schema(description = "The maximum size of a segment in tokens.")
    private Integer maxSegmentSize;
    @Schema(description = "The maximum size of the overlap between segments in tokens.")
    private Integer maxOverlapSize;

    public RagTask toRagTask(String characterUid) {
        if (StringUtils.isBlank(characterUid)) {
            return null;
        }

        RagTask task = CommonUtils.convert(this, RagTask.class);
        task.setCharacterUid(characterUid);
        if (SourceType.of(getSourceType()) == SourceType.FILE) {
            try {
                task.setSource(FileUtils.getDefaultPrivatePath(
                        getSource(), AccountUtils.currentUser().getUserId()));
            } catch (AccessDeniedException e) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
            }
        }

        return task;
    }
}
