package fun.freechat.api.admin;

import fun.freechat.service.common.EncryptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Tag(name = "Encryption Manager (for admin)", description = "System encryption service, callable only by super administrators.")
@RequestMapping("/api/v2/admin/encryption")
@ResponseBody
@Validated
@SuppressWarnings("unused")
public class EncryptionManagerApi {
    @Autowired
    private EncryptionService encryptionService;

    @Operation(
            operationId = "encryptText",
            summary = "Encrypt Text",
            description = "Encrypt a piece of text with the built-in key."
    )
    @GetMapping(value = "/encrypt/{text}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String encrypt(
            @Parameter(description = "Text to be encrypted") @PathVariable("text") @NotBlank
            String plainText) {
        return encryptionService.encrypt(plainText);
    }
}
