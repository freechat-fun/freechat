package fun.freechat.service.plugin;

import fun.freechat.model.PluginInfo;

public interface PluginFetchService {
    String fetchManifestInfo(PluginInfo pluginInfo);
    String fetchApiDocsInfo(PluginInfo pluginInfo);
    void clearCaches(PluginInfo pluginInfo);
}
