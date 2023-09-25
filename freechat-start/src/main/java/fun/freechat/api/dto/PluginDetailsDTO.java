package fun.freechat.api.dto;

import fun.freechat.api.util.AiModelUtils;
import fun.freechat.api.util.AccountUtils;
import fun.freechat.api.util.CommonUtils;
import fun.freechat.api.util.FunctionFormatUtils;
import fun.freechat.model.PluginInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;
import java.util.Objects;

@Schema(description = "Plugin detailed content")
@Data
public class PluginDetailsDTO extends PluginSummaryDTO {
    @Schema(description = "Manifest content, different formats may have differences")
    private String manifestInfo;
    @Schema(description = "API description content, different formats may have content differences")
    private String apiInfo;
    @Schema(description = "Function description format, currently supported: open_ai")
    private String functionFormat;
    @Schema(description = "Function description content")
    private String functionInfo;
    @Schema(description = "Additional information, JSON format")
    private String ext;

    public static PluginDetailsDTO fromPluginInfo(
            Triple<PluginInfo, List<String>, List<String>> pluginInfoTriple) {
        if (Objects.isNull(pluginInfoTriple)) {
            return null;
        }
        PluginInfo pluginInfo = pluginInfoTriple.getLeft();
        Pair<String, String> functionInfo = FunctionFormatUtils.convert(pluginInfo);
        PluginDetailsDTO pluginDetailsDTO = CommonUtils.convert(pluginInfo, PluginDetailsDTO.class);
        pluginDetailsDTO.setUsername(AccountUtils.userIdToName(pluginInfoTriple.getLeft().getUserId()));
        pluginDetailsDTO.setFunctionFormat(functionInfo.getLeft());
        pluginDetailsDTO.setFunctionInfo(functionInfo.getRight());
        pluginDetailsDTO.setTags(pluginInfoTriple.getMiddle());
        pluginDetailsDTO.setAiModels(pluginInfoTriple.getRight()
                .stream()
                .map(AiModelUtils::getModelInfoDTO)
                .peek(aiModelInfo -> aiModelInfo.setRequestId(null))
                .toList());
        return pluginDetailsDTO;
    }
}
