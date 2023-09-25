package fun.freechat.api.dto;

import fun.freechat.api.util.AccountUtils;
import fun.freechat.api.util.CommonUtils;
import fun.freechat.model.PluginInfo;
import fun.freechat.service.enums.ApiFormat;
import fun.freechat.service.enums.ModelProvider;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;
import java.util.Objects;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "Request data for creating new plugin information")
@Data
public class PluginCreateDTO {
    @Schema(description = "Visibility: private (default), public, public_org, hidden")
    private String visibility;
    @Schema(description = "Plugin name", requiredMode = REQUIRED)
    private String name;
    @Schema(description = "Manifest format, currently supported: dash_scope (default), open_ai")
    private String manifestFormat;
    @Schema(description = "Manifest content, can be full content or a URL")
    private String manifestInfo;
    @Schema(description = "API description format, currently supported: openapi_v3 (default)")
    private String apiFormat;
    @Schema(description = "API description content, can be full content or a URL")
    private String apiInfo;
    @Schema(description = "Provider information, default is the current user's username")
    private String provider;
    @Schema(description = "Additional information, JSON format")
    private String ext;
    @Schema(description = "Tag set")
    private List<String> tags;
    @Schema(description = "Supported model set, empty means no model limit")
    private List<String> aiModels;

    public Triple<PluginInfo, List<String>, List<String>> toPluginInfo() {
        PluginInfo pluginInfo = CommonUtils.convert(this, PluginInfo.class);
        pluginInfo.setPluginId(null);
        pluginInfo.setUserId(
                Objects.requireNonNull(AccountUtils.currentUser()).getUserId());
        if (StringUtils.isBlank(pluginInfo.getProvider())) {
            pluginInfo.setProvider(AccountUtils.currentUser().getUsername());
        }
        if (StringUtils.isBlank(pluginInfo.getManifestFormat())) {
            pluginInfo.setManifestFormat(ModelProvider.DASH_SCOPE.text());
        }
        if (StringUtils.isBlank(pluginInfo.getApiFormat())) {
            pluginInfo.setApiFormat(ApiFormat.OPENAPI_V3.text());
        }
        return Triple.of(pluginInfo, getTags(), getAiModels());
    }
}
