package fun.freechat.service.plugin;

import fun.freechat.model.PluginInfo;
import fun.freechat.service.enums.FunctionFormat;
import org.apache.commons.lang3.tuple.Pair;

public interface PluginFormatService {
    Pair<FunctionFormat, String> convert(PluginInfo pluginInfo);
}
