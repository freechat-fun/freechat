package fun.freechat.api.dto;

import fun.freechat.api.util.AccountUtils;
import fun.freechat.api.util.CommonUtils;
import fun.freechat.api.util.FileUtils;
import fun.freechat.model.RagTask;
import fun.freechat.service.enums.SourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Schema(description = "RAG task information")
@Slf4j
@Data
public class RagTaskDTO {
    @Schema(description = "Source type: file (default) | url")
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
            task.setSource(FileUtils.getDefaultPrivatePath(
                    getSource(), AccountUtils.currentUser().getUserId()));
        }

        return task;
    }
}
