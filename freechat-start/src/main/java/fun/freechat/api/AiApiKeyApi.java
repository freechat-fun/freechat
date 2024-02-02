package fun.freechat.api;

import fun.freechat.api.dto.AiApiKeyCreateDTO;
import fun.freechat.api.dto.AiApiKeyInfoDTO;
import fun.freechat.api.util.AccountUtils;
import fun.freechat.service.ai.AiApiKeyService;
import fun.freechat.service.enums.ModelProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

@Controller
@Tag(name = "AI Service")
@RequestMapping("/api/v1/ai")
@ResponseBody
@Validated
@SuppressWarnings("unused")
public class AiApiKeyApi {
    @Autowired
    private AiApiKeyService aiApiKeyService;

    @Operation(
            operationId = "addAiApiKey",
            summary = "Add Model Provider Credential",
            description = "Add a credential for the model service."
    )
    @PostMapping("/apikey")
    public Long add(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Model call credential information"
            )
            @RequestBody
            @NotNull
            AiApiKeyCreateDTO aiApiKey) {
        Long id = aiApiKeyService.create(AccountUtils.currentUser(),
                aiApiKey.getName(), ModelProvider.of(aiApiKey.getProvider()),
                aiApiKey.getToken(), aiApiKey.getEnabled());
        if (Objects.isNull(id)) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many api keys.");
        }
        return id;
    }

    @Operation(
            operationId = "disableAiApiKey",
            summary = "Disable Model Provider Credential",
            description = "Disable the credential information of the model provider."
    )
    @PutMapping("/apikey/disable/{id}")
    @PreAuthorize("hasPermission(#p0, 'aiApiKeyOp')")
    Boolean disable(
            @Parameter(description = "Credential identifier") @PathVariable("id") @Positive Long id) {
        return aiApiKeyService.disable(id);
    }

    @Operation(
            operationId = "enableAiApiKey",
            summary = "Enable Model Provider Credential",
            description = "Enable the credential information of the model provider."
    )
    @PutMapping("/apikey/enable/{id}")
    @PreAuthorize("hasPermission(#p0, 'aiApiKeyOp')")
    Boolean enable(
            @Parameter(description = "Credential identifier") @PathVariable("id") @Positive Long id) {
        return aiApiKeyService.enable(id);
    }

    @Operation(
            operationId = "deleteAiApiKey",
            summary = "Delete Credential of Model Provider",
            description = "Delete the credential information of the model provider."
    )
    @DeleteMapping("/apikey/{id}")
    @PreAuthorize("hasPermission(#p0, 'aiApiKeyOp')")
    Boolean delete(
            @Parameter(description = "Credential identifier") @PathVariable("id") @Positive Long id) {
        return aiApiKeyService.delete(id);
    }

    @Operation(
            operationId = "getAiApiKey",
            summary = "Get credential of Model Provider",
            description = "Get the credential information of the model provider."
    )
    @GetMapping("/apikey/{id}")
    @PreAuthorize("hasPermission(#p0, 'aiApiKeyOp')")
    AiApiKeyInfoDTO get(
            @Parameter(description = "Credential identifier") @PathVariable("id") @Positive Long id) {
        return AiApiKeyInfoDTO.from(aiApiKeyService.get(id));
    }


    @Operation(
            operationId = "listAiApiKeys",
            summary = "List Credentials of Model Provider",
            description = "List all credential information of the model provider."
    )
    @GetMapping("/apikeys/{provider}")
    List<AiApiKeyInfoDTO> list(
            @Parameter(description = "Model provider") @PathVariable("provider") @NotBlank String provider) {
        return aiApiKeyService.list(AccountUtils.currentUser(), ModelProvider.of(provider))
                .stream()
                .map(AiApiKeyInfoDTO::from)
                .toList();
    }
}
