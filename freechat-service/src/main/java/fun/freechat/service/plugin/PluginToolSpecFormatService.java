package fun.freechat.service.plugin;

import fun.freechat.model.PluginInfo;
import fun.freechat.service.enums.ToolSpecFormat;
import org.apache.commons.lang3.tuple.Pair;

public interface PluginToolSpecFormatService {
    Pair<ToolSpecFormat, String> convert(PluginInfo pluginInfo);
}
