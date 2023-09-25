package fun.freechat.api;

import fun.freechat.api.dto.AiModelInfoDTO;
import fun.freechat.service.ai.AiModelInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Objects;

@Controller
@Tag(name = "AI Service")
@RequestMapping("/api/v1/ai")
@ResponseBody
@Validated
@SuppressWarnings("unused")
public class AiModelInfoApi {
    @Autowired
    private AiModelInfoService aiModelInfoService;

    @Operation(
            operationId = "listAiModelInfo",
            summary = "List Models",
            description = "Return all model information."
    )
    @GetMapping("/models")
    public List<AiModelInfoDTO> list() {
        return list(10, 0);
    }

    @Operation(
            operationId = "listAiModelInfoBySize",
            summary = "List Models by Limits",
            description = "Return model information, return up to pageSize model information."
    )
    @GetMapping("/models/{pageSize}")
    public List<AiModelInfoDTO> list(
            @Parameter(description = "Maximum quantity") @PathVariable("pageSize") Integer pageSize) {
        return list(pageSize, 0);
    }

    @Operation(
            operationId = "listAiModelInfoByPage",
            summary = "List Models by Page",
            description = "Return model information by page, return the pageNum page, up to pageSize model information."
    )
    @GetMapping("/models/{pageSize}/{pageNum}")
    public List<AiModelInfoDTO> list(
            @Parameter(description = "Maximum quantity") @PathVariable("pageSize") Integer pageSize,
            @Parameter(description = "Current page number") @PathVariable("pageNum") Integer pageNum) {
        int limit = Objects.nonNull(pageSize) && pageSize > 0 ? pageSize : 0;
        int offset = Objects.nonNull(pageSize) && Objects.nonNull(pageNum) && pageSize * pageNum > 0
                ? pageSize * pageNum
                : 0;
        return aiModelInfoService.list(limit, offset).stream()
                .map(AiModelInfoDTO::fromAiModelInfo)
                .filter(Objects::nonNull)
                .peek(modelInfo -> modelInfo.setRequestId(null))
                .toList();
    }

    @Operation(
            operationId = "getAiModelInfo",
            summary = "Get Model Information",
            description = "Return specific model information."
    )
    @GetMapping("/model/{modelId}")
    public AiModelInfoDTO info(
            @Parameter(description = "Model identifier") @PathVariable("modelId") @NotBlank
            String modelId) {
        return AiModelInfoDTO.fromAiModelInfo(aiModelInfoService.get(modelId));
    }
}
