package fun.freechat.api;

import fun.freechat.api.dto.AiModelInfoDTO;
import fun.freechat.service.ai.AiModelInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
            description = "Return model information by page, return the pageNum page, up to pageSize model information."
    )
    @GetMapping(value = {
            "/models/{pageSize}/{pageNum}",
            "/models/{pageSize}",
            "/models"})
    public List<AiModelInfoDTO> list(
            @Parameter(description = "Maximum quantity") @PathVariable("pageSize") @Positive Optional<Long> pageSize,
            @Parameter(description = "Current page number") @PathVariable("pageNum") @PositiveOrZero Optional<Long> pageNum) {
        long limit = pageSize.filter(size -> size > 0).orElse(10L);
        long offset = pageNum.filter(num -> num >= 0).orElse(0L) * limit;
        return aiModelInfoService.list(limit, offset).stream()
                .map(AiModelInfoDTO::from)
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
        return AiModelInfoDTO.from(aiModelInfoService.get(modelId));
    }
}
