package fun.freechat.api.admin;

import fun.freechat.api.dto.AppMetaDTO;
import fun.freechat.api.dto.OpenAiParamDTO;
import fun.freechat.api.dto.QwenParamDTO;
import fun.freechat.util.AppMetaUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Tag(name = "App Meta (for admin)", description = "Application metadata, viewable only by super administrators.")
@RequestMapping("/api/v2/admin/app")
@ResponseBody
@Validated
@SuppressWarnings("unused")
public class AppMetaApi {
    private static final int BUILD_NUM_LEN = 8;

    @Operation(
            operationId = "getAppMeta",
            summary = "Get Application Information",
            description = "Get application information to accurately locate the corresponding project version."
    )
    @GetMapping("/meta")
    public AppMetaDTO meta() {
        String buildNumber = AppMetaUtils.getBuildNumber().substring(0, BUILD_NUM_LEN);
        return AppMetaDTO.builder()
                .name(AppMetaUtils.getName())
                .version(AppMetaUtils.getVersion())
                .buildTimestamp(AppMetaUtils.getBuildTimestamp())
                .buildNumber(buildNumber)
                .commitUrl(AppMetaUtils.getScmUrl() + "/commit/" + buildNumber)
                .releaseNoteUrl(AppMetaUtils.getUrl())
                .runningEnv(AppMetaUtils.getRunningEnv())
                .build();
    }

    @Operation(
            operationId = "expose",
            summary = "Expose DTO definitions",
            description = "This method does nothing."
    )
    @GetMapping("/expose")
    public String expose(
            @RequestParam OpenAiParamDTO openAiParam,
            @RequestParam QwenParamDTO qwenParam) {
        return "Oops...";
    }
}
