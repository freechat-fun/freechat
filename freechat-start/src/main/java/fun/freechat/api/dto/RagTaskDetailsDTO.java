package fun.freechat.api.dto;

import fun.freechat.api.util.AccountUtils;
import fun.freechat.api.util.CommonUtils;
import fun.freechat.api.util.FileUtils;
import fun.freechat.model.RagTask;
import fun.freechat.service.enums.SourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.Objects;

@Schema(description = "Prompt task detailed information")
@Slf4j
@Data
@EqualsAndHashCode(callSuper = true)
public class RagTaskDetailsDTO extends TraceableDTO {
    @Schema(description = "RAG task identifier")
    private Long id;
    @Schema(description = "Creation time")
    private Date gmtCreate;
    @Schema(description = "Modification time")
    private Date gmtModified;
    @Schema(description = "Task start execution time")
    private Date gmtStart;
    @Schema(description = "Task end execution time")
    private Date gmtEnd;
    @Schema(description = "Character identifier")
    private String characterUid;
    @Schema(description = "Source type: file (default) | url")
    private String sourceType;
    @Schema(description = "Source information, url, or a key for file")
    private String source;
    @Schema(description = "The maximum size of a segment in tokens.")
    private Integer maxSegmentSize;
    @Schema(description = "The maximum size of the overlap between segments in tokens.")
    private Integer maxOverlapSize;
    @Schema(description = "Task execution status: pending | running | succeeded | failed | canceled")
    private String status;
    @Schema(description = "Additional information, JSON format")
    private String ext;

    public static RagTaskDetailsDTO from(RagTask task) {
        if (task == null) {
            return null;
        }

        RagTaskDetailsDTO dto = CommonUtils.convert(task, RagTaskDetailsDTO.class);
        if (SourceType.of(task.getSourceType()) == SourceType.FILE) {
            dto.setSource(FileUtils.getDefaultPrivateKey(
                    task.getSource(), AccountUtils.currentUser().getUserId()));
        }

        return dto;
    }
}
