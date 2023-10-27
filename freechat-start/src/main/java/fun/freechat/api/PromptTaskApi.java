package fun.freechat.api;

import fun.freechat.api.dto.PromptTaskDTO;
import fun.freechat.api.dto.PromptTaskDetailsDTO;
import fun.freechat.model.PromptTask;
import fun.freechat.service.prompt.PromptTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
@Tag(name = "Prompt Task")
@RequestMapping("/api/v1/prompt/task")
@ResponseBody
@Validated
@SuppressWarnings("unused")
public class PromptTaskApi {
    @Autowired
    private PromptTaskService promptTaskService;

    @Operation(
            operationId = "createPromptTask",
            summary = "Add Prompt Task",
            description = "Add a prompt task."
    )
    @PostMapping(produces = MediaType.TEXT_PLAIN_VALUE)
    @PreAuthorize("hasPermission(#p0.promptRef.promptId, 'promptDefaultOp')")
    public String create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The prompt task to be added") @RequestBody @NotNull
            PromptTaskDTO task) {
        PromptTask promptTask = task.toPromptTask();
        if (!promptTaskService.create(promptTask)) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create prompt task.");
        }
        return promptTask.getTaskId();
    }

    @Operation(
            operationId = "updatePromptTask",
            summary = "Update Prompt Task",
            description = "Update a prompt task."
    )
    @PutMapping("/{promptTaskId}")
    @PreAuthorize("hasPermission(#p0 + '|' + #p1.promptRef.promptId, 'promptTaskUpdateOp')")
    public Boolean update(
            @Parameter(description = "The promptTaskId to be updated") @PathVariable("promptTaskId") @NotBlank
            String promptTaskId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The prompt task info to be updated") @RequestBody @NotNull
            PromptTaskDTO task) {
        PromptTask promptTask = task.toPromptTask();
        promptTask.setTaskId(promptTaskId);
        return promptTaskService.update(promptTask);
    }

    @Operation(
            operationId = "deletePromptTask",
            summary = "Delete Prompt Task",
            description = "Delete a prompt task."
    )
    @DeleteMapping("/{promptTaskId}")
    @PreAuthorize("hasPermission(#p0, 'promptTaskDefaultOp')")
    public Boolean delete(
            @Parameter(description = "The promptTaskId to be deleted") @PathVariable("promptTaskId") @NotBlank
            String promptTaskId) {
        return promptTaskService.delete(promptTaskId);
    }

    @Operation(
            operationId = "getPromptTask",
            summary = "Get Prompt Task",
            description = "Get the prompt task details."
    )
    @GetMapping("/{promptTaskId}")
    @PreAuthorize("hasPermission(#p0, 'promptTaskDefaultOp')")
    public PromptTaskDetailsDTO get(
            @Parameter(description = "The promptTaskId to be queried") @PathVariable("promptTaskId") @NotBlank
            String promptTaskId) {
        return PromptTaskDetailsDTO.from(promptTaskService.get(promptTaskId));
    }
}
