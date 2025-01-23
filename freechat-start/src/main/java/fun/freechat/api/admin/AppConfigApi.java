package fun.freechat.api.admin;

import fun.freechat.service.config.RuntimeConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "App Config (for admin)", description = "Application configuration information, viewable only by super administrators.")
@RequestMapping("/api/v2/admin/app")
@Validated
@SuppressWarnings("unused")
public class AppConfigApi {
    @Autowired
    private RuntimeConfig runtimeConfig;

    @Operation(
            operationId = "getDefaultConfig",
            summary = "Get Default Config",
            description = "Get default configuration information of the application."
    )
    @GetMapping(value = "/configs/default", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getDefaultConfig() {
        return runtimeConfig.getContent();
    }
}
