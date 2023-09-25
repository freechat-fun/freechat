package fun.freechat.api.admin;

import fun.freechat.api.dto.AppConfigCreateDTO;
import fun.freechat.api.dto.AppConfigInfoDTO;
import fun.freechat.api.util.AccountUtils;
import fun.freechat.service.common.ConfigService;
import fun.freechat.service.enums.ConfigFormat;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

@Controller
@Tag(name = "App Config (for admin)", description = "Application configuration information, viewable only by super administrators.")
@RequestMapping("/api/v1/admin/app")
@ResponseBody
@Validated
@SuppressWarnings("unused")
public class AppConfigApi {
    @Autowired
    @Qualifier("mysqlConfigService")
    private ConfigService configService;

    @Operation(
            operationId = "getAppConfig",
            summary = "Get Configuration",
            description = "Get the latest configuration information of the application by name."
    )
    @GetMapping("/config/{name}")
    public AppConfigInfoDTO loadConfig(
            @Parameter(description = "Configuration name") @PathVariable("name") @NotBlank
            String name) {
        var config = configService.load(name);
        if (Objects.isNull(config)) {
            return null;
        }
        AppConfigInfoDTO configDto = new AppConfigInfoDTO();
        configDto.setName(name);
        configDto.setContent(config.getLeft());
        configDto.setFormat(config.getMiddle().text());
        configDto.setVersion(config.getRight());
        return configDto;
    }

    @Operation(
            operationId = "getAppConfigByVersion",
            summary = "Get Specified Version of Configuration",
            description = "Get the configuration information of the application by name and version."
    )
    @GetMapping("/config/{name}/{version}")
    public AppConfigInfoDTO loadConfig(
            @Parameter(description = "Configuration name") @PathVariable("name") @NotBlank
            String name,
            @Parameter(description = "Configuration version") @PathVariable("version") @Positive
            Integer version) {
        var config = configService.load(name, version);
        if (Objects.isNull(config)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Failed to find config of '" + name + "'");
        }
        AppConfigInfoDTO configDto = new AppConfigInfoDTO();
        configDto.setName(name);
        configDto.setContent(config.getLeft());
        configDto.setFormat(config.getRight().text());
        configDto.setVersion(version);
        return configDto;
    }

    @Operation(
            operationId = "publishAppConfig",
            summary = "Publish Configuration",
            description = "Publish application configuration, return configuration version."
    )
    @PostMapping("/config")
    public Integer publishConfig(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Configuration information") @RequestBody @NotNull
            AppConfigCreateDTO config) {
        return configService.publish(AccountUtils.currentUser(), config.getName(),
                ConfigFormat.of(config.getFormat()), config.getContent());
    }

    @Operation(
            operationId = "listAppConfigNames",
            summary = "List Configuration Names",
            description = "List all application configuration names."
    )
    @PostMapping("/configs")
    public List<String> listConfigNames() {
        return configService.listNames();
    }
}
