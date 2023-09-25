package fun.freechat.api.dto;

import fun.freechat.api.util.AccountUtils;
import fun.freechat.api.util.CommonUtils;
import fun.freechat.model.PluginInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;
import java.util.Objects;

@Schema(description = "Request data for updating plugin information")
@Data
public class PluginUpdateDTO extends PluginCreateDTO {
    public Triple<PluginInfo, List<String>, List<String>> toPluginInfo(String pluginId) {
        PluginInfo pluginInfo = CommonUtils.convert(this, PluginInfo.class);
        pluginInfo.setPluginId(pluginId);
        pluginInfo.setUserId(
                Objects.requireNonNull(AccountUtils.currentUser()).getUserId());
        return Triple.of(pluginInfo, getTags(), getAiModels());
    }
}
