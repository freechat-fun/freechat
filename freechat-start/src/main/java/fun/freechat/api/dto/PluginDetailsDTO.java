package fun.freechat.api.dto;

import fun.freechat.api.util.AccountUtils;
import fun.freechat.api.util.AiModelUtils;
import fun.freechat.api.util.CommonUtils;
import fun.freechat.api.util.ToolSpecFormatUtils;
import fun.freechat.model.PluginInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Schema(description = "Plugin detailed content")
@Data
@EqualsAndHashCode(callSuper = true)
public class PluginDetailsDTO extends PluginSummaryDTO {
    @Schema(description = "Manifest content, different formats may have differences")
    private String manifestInfo;
    @Schema(description = "API description content, different formats may have content differences")
    private String apiInfo;
    @Schema(description = "Tool specification format, currently supported: open_ai")
    private String toolSpecFormat;
    @Schema(description = "Tool specification content")
    private String toolSpecInfo;
    @Schema(description = "Additional information, JSON format")
    private String ext;

    public static PluginDetailsDTO from(
            Triple<PluginInfo, List<String>, List<String>> pluginInfoTriple) {
        if (pluginInfoTriple == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Failed to find plugin!");
        }
        PluginInfo pluginInfo = pluginInfoTriple.getLeft();
        Pair<String, String> toolSpecPair = ToolSpecFormatUtils.convert(pluginInfo);
        PluginDetailsDTO dto = CommonUtils.convert(pluginInfo, PluginDetailsDTO.class);
        dto.setUsername(AccountUtils.userIdToName(pluginInfoTriple.getLeft().getUserId()));
        dto.setToolSpecFormat(toolSpecPair.getLeft());
        dto.setToolSpecInfo(toolSpecPair.getRight());
        dto.setTags(pluginInfoTriple.getMiddle());
        dto.setAiModels(pluginInfoTriple.getRight()
                .stream()
                .map(AiModelUtils::getModelInfoDTO)
                .peek(aiModelInfo -> aiModelInfo.setRequestId(null))
                .toList());
        return dto;
    }
}
