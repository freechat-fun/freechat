package fun.freechat.api.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.nimbusds.oauth2.sdk.util.MapUtils;
import fun.freechat.api.util.CommonUtils;
import fun.freechat.api.util.PromptUtils;
import fun.freechat.model.PromptTask;
import fun.freechat.service.util.InfoUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

        if (MapUtils.isNotEmpty(params)) {
            try {
                task.setParams(InfoUtils.defaultMapper().writeValueAsString(params));
            } catch (JsonProcessingException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
            }
        }

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
        task.setPromptUid(PromptUtils.idToUid(promptRef.getPromptId()));
        task.setVariables(variables);
        if (Objects.nonNull(promptRef.getDraft())) {
            task.setDraft(promptRef.getDraft() ? (byte) 0 : (byte) 1);
        }

        return task;
    }

    public static PromptTaskDTO from(PromptTask task) {
        if (Objects.isNull(task)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Prompt task should not be null.");
        }
        PromptTaskDTO dto = CommonUtils.convert(task, PromptTaskDTO.class);

        if (StringUtils.isNotBlank(task.getPromptUid())) {
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
            PromptRefDTO promptRef = PromptRefDTO.from(
                    PromptUtils.uidToLatestId(task.getPromptUid()), variables, draft);

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
