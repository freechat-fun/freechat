package fun.freechat.api.admin;

import fun.freechat.api.dto.AppConfigInfoDTO;
import fun.freechat.service.common.ConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Tag(name = "App Config (for admin)", description = "Application configuration information, viewable only by super administrators.")
@RequestMapping("/api/v2/admin/app")
@ResponseBody
@Validated
@SuppressWarnings("unused")
public class AppConfigApi {
    @Autowired
    private ConfigService configService;

    @Operation(
            operationId = "getAppConfigs",
            summary = "Get Configurations",
            description = "Get all configuration information of the application."
    )
    @GetMapping("/configs")
    public AppConfigInfoDTO loadConfigs() {
        return AppConfigInfoDTO.from(configService.load());
    }
}
