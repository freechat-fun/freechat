package fun.freechat.api;

import fun.freechat.api.dto.RagTaskDTO;
import fun.freechat.api.dto.RagTaskDetailsDTO;
import fun.freechat.model.RagTask;
import fun.freechat.service.character.CharacterService;
import fun.freechat.service.rag.RagTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
@Tag(name = "Rag")
@RequestMapping("/api/v1/rag")
@ResponseBody
@Validated
@SuppressWarnings("unused")
public class RagApi {
    @Autowired
    private CharacterService characterService;
    @Autowired
    private RagTaskService ragTaskService;

    @Operation(
            operationId = "createRagTask",
            summary = "Create RAG Task",
            description = "Create a RAG task."
    )
    @PostMapping("/task/{characterId}")
    @PreAuthorize("hasPermission(#p0, 'characterDefaultOp')")
    public Long create(
            @Parameter(description = "The characterId to be added a RAG task") @PathVariable("characterId") @Positive
            Long characterId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The RAG task to be added") @RequestBody @NotNull
            RagTaskDTO task) {
        String characterUid = characterService.getUid(characterId);
        if (StringUtils.isBlank(characterUid)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No character found.");
        }
        RagTask ragTask = task.toRagTask(characterUid);
        if (!ragTaskService.create(ragTask)) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create RAG task.");
        }
        return ragTask.getId();
    }

    @Operation(
            operationId = "updateRagTask",
            summary = "Update RAG Task",
            description = "Update a RAG task."
    )
    @PutMapping("/task/{taskId}")
    @PreAuthorize("hasPermission(#p0, 'ragDefaultOp')")
    public Boolean update(
            @Parameter(description = "The taskId to be updated") @PathVariable("taskId") @Positive
            Long taskId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The prompt task info to be updated") @RequestBody @NotNull
            RagTaskDTO task) {
        String characterUid = ragTaskService.getCharacterUid(taskId);
        if (StringUtils.isBlank(characterUid)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No task found.");
        }
        RagTask ragTask = task.toRagTask(characterUid);
        return ragTaskService.update(ragTask);
    }

    @Operation(
            operationId = "deleteRagTask",
            summary = "Delete RAG Task",
            description = "Delete a RAG task."
    )
    @DeleteMapping("/task/{taskId}")
    @PreAuthorize("hasPermission(#p0, 'ragDefaultOp')")
    public Boolean delete(
            @Parameter(description = "The taskId to be deleted") @PathVariable("taskId") @Positive
            Long taskId) {
        return ragTaskService.delete(taskId);
    }

    @Operation(
            operationId = "getRagTask",
            summary = "Get RAG Task",
            description = "Get the RAG task details."
    )
    @GetMapping("/task/{taskId}")
    @PreAuthorize("hasPermission(#p0, 'ragDefaultOp')")
    public RagTaskDetailsDTO get(
            @Parameter(description = "The taskId to be queried") @PathVariable("taskId") @Positive
            Long taskId) {
        return RagTaskDetailsDTO.from(ragTaskService.get(taskId));
    }

    @Operation(
            operationId = "listRagTasks",
            summary = "List RAG Tasks",
            description = "List the RAG tasks by characterId."
    )
    @GetMapping("/tasks/{characterId}")
    @PreAuthorize("hasPermission(#p0, 'characterDefaultOp')")
    public List<RagTaskDetailsDTO> list(
            @Parameter(description = "The characterId to be queried") @PathVariable("characterId") @Positive
            Long characterId) {
        String characterUid = characterService.getUid(characterId);
        if (StringUtils.isBlank(characterUid)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No character found.");
        }
        return ragTaskService.list(characterUid)
                .stream()
                .map(RagTaskDetailsDTO::from)
                .toList();
    }

    @Operation(
            operationId = "getRagTaskStatus",
            summary = "Get RAG Task Status",
            description = "Get the RAG task execution status: pending | running | succeeded | failed | canceled."
    )
    @GetMapping(value = "/task/status/{taskId}", produces = MediaType.TEXT_PLAIN_VALUE)
    @PreAuthorize("hasPermission(#p0, 'ragDefaultOp')")
    public String getStatus(
            @Parameter(description = "The taskId to be queried status") @PathVariable("taskId") @Positive
            Long taskId) {
        return ragTaskService.getStatus(taskId).text();
    }

    @Operation(
            operationId = "startRagTask",
            summary = "Start RAG Task",
            description = "Start a RAG task."
    )
    @PostMapping("/task/start/{taskId}")
    @PreAuthorize("hasPermission(#p0, 'ragDefaultOp')")
    public Boolean start(
            @Parameter(description = "The taskId to be started") @PathVariable("taskId") @Positive
            Long taskId) {
        return ragTaskService.start(taskId);
    }

    @Operation(
            operationId = "cancelRagTask",
            summary = "Cancel RAG Task",
            description = "Cancel a RAG task."
    )
    @PostMapping("/task/cancel/{taskId}")
    @PreAuthorize("hasPermission(#p0, 'ragDefaultOp')")
    public Boolean cancel(
            @Parameter(description = "The taskId to be canceled") @PathVariable("taskId") @Positive
            Long taskId) {
        return ragTaskService.cancel(taskId);
    }
}
