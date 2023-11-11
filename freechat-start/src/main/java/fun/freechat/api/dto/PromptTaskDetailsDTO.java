package fun.freechat.api.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import fun.freechat.api.util.CommonUtils;
import fun.freechat.model.PromptTask;
import fun.freechat.service.util.InfoUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

@Schema(description = "Prompt task detailed information")
@Slf4j
@Data
@EqualsAndHashCode(callSuper = true)
public class PromptTaskDetailsDTO extends TraceableDTO {
    @Schema(description = "Prompt task identifier")
    private String taskId;
    @Schema(description = "Creation time")
    private Date gmtCreate;
    @Schema(description = "Modification time")
    private Date gmtModified;
    @Schema(description = "Latest executed time")
    private Date gmtExecuted;
    @Schema(description = "Prompt reference information")
    private PromptRefDTO promptRef;
    @Schema(description = "Model identifier")
    private String modelId;
    @Schema(description = "API-KEY name")
    private String apiKeyName;
    @Schema(description = "Model call parameters")
    private Map<String, Object> params;
    @Schema(description = "Task scheduling configuration which compatible with Quartz cron format")
    private String cron;
    @Schema(description = "Task execution status: pending | running | succeeded | failed")
    private String status;

    public static PromptTaskDetailsDTO from(PromptTask task) {
        if (Objects.isNull(task)) {
            return null;
        }
        PromptTaskDetailsDTO dto = CommonUtils.convert(task, PromptTaskDetailsDTO.class);

        if (StringUtils.isNotBlank(task.getPromptId())) {
            Map<String, Object> variables = null;
            if (StringUtils.isNotBlank(task.getVariables())) {
                try {
                    variables = InfoUtils.defaultMapper().readValue(
                            task.getVariables(), new TypeReference<>() {});
                } catch (JsonProcessingException e) {
                    log.error("Failed to parse prompt variables: {}", task.getVariables(), e);
                }
            }
            Boolean draft = (byte) 1 == task.getDraft();
            PromptRefDTO promptRef = PromptRefDTO.from(task.getPromptId(), variables, draft);

            dto.setPromptRef(promptRef);
        }

        if (StringUtils.isNotBlank(task.getParams())) {
            try {
                Map<String, Object> params = InfoUtils.defaultMapper().readValue(
                        task.getParams(), new TypeReference<>() {});
                dto.setParams(params);
            } catch (JsonProcessingException e) {
                log.error("Failed to parse model params: {}", task.getParams(), e);
            }
        }

        return dto;
    }
}
