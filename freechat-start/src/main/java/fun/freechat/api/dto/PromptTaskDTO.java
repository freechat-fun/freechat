package fun.freechat.api.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.oauth2.sdk.util.MapUtils;
import fun.freechat.api.util.CommonUtils;
import fun.freechat.model.PromptTask;
import fun.freechat.service.util.InfoUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Objects;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "Prompt task information")
@Slf4j
@Data
public class PromptTaskDTO {
    @Schema(description = "Prompt reference information", requiredMode = REQUIRED)
    private PromptRefDTO promptRef;
    @Schema(description = "Model identifier")
    private String modelId;
    @Schema(description = "API-KEY name, priority: apiKeyName > apiKeyValue")
    private String apiKeyName;
    @Schema(description = "API-KEY value")
    private String apiKeyValue;
    @Schema(description = "Model call parameters")
    private Map<String, Object> params;
    @Schema(description = "Task scheduling configuration which compatible with Quartz cron format")
    private String cron;
    @Schema(description = "Task execution status: pending | running | succeeded | failed")
    private String status;

    public PromptTask toPromptTask() {
        PromptTask task = CommonUtils.convert(this, PromptTask.class);

        if (Objects.isNull(promptRef)) {
            return task;
        }

        String variables = null;
        if (MapUtils.isNotEmpty(promptRef.getVariables())) {
            try {
                variables = InfoUtils.defaultMapper().writeValueAsString(promptRef.getVariables());
            } catch (JsonProcessingException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
            }
        }
        task.setPromptId(promptRef.getPromptId());
        task.setVariables(variables);
        if (Objects.nonNull(promptRef.getDraft())) {
            task.setDraft(promptRef.getDraft() ? (byte) 0 : (byte) 1);
        }

        if (MapUtils.isNotEmpty(params)) {
            try {
                task.setParams(InfoUtils.defaultMapper().writeValueAsString(params));
            } catch (JsonProcessingException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
            }
        }

        return task;
    }
}
