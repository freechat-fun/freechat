package fun.freechat.api.biz;

import fun.freechat.api.dto.AiModelInfoUpdateDTO;
import fun.freechat.service.ai.AiModelInfoService;
import fun.freechat.service.enums.ModelProvider;
import fun.freechat.service.enums.ModelType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "AI Manager (for biz, admin)", description = "Manage model information, callable only by super administrators and business administrators.")
@RequestMapping("/api/v2/biz/admin/ai/model")
@Validated
@SuppressWarnings("unused")
public class AiModelInfoManagerApi {
    @Autowired
    private AiModelInfoService aiModelInfoService;

    @Operation(
            operationId = "createOrUpdateAiModelInfo",
            summary = "Create or Update Model Information",
            description = "Create or update model information. If no modelId is passed or the modelId does not exist in the database, create a new one (keep the same modelId); otherwise update. Return modelId if successful."
    )
    @PutMapping(value = "", produces = MediaType.TEXT_PLAIN_VALUE)
    public String createOrUpdate(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Model information")
            @RequestBody
            @NotNull
            AiModelInfoUpdateDTO aiModelInfo) {
        return aiModelInfoService.createOrUpdate(aiModelInfo.getName(),
                aiModelInfo.getDescription(),
                ModelProvider.of(aiModelInfo.getProvider()),
                ModelType.of(aiModelInfo.getType()));
    }

    @Operation(
            operationId = "deleteAiModelInfo",
            summary = "Delete Model Information",
            description = "Delete model information based on modelId."
    )
    @DeleteMapping("/{modelId}")
    public Boolean delete(
            @Parameter(description = "Model identifier") @PathVariable("modelId") @NotBlank
            String modelId) {
        return aiModelInfoService.delete(modelId);
    }
}
