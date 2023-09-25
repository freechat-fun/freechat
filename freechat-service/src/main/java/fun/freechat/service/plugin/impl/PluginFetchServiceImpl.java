package fun.freechat.service.plugin.impl;

import fun.freechat.model.PluginInfo;
import fun.freechat.service.cache.LongPeriodCache;
import fun.freechat.service.plugin.PluginFetchService;
import fun.freechat.util.HttpUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import static fun.freechat.service.util.CacheUtils.LONG_PERIOD_CACHE_NAME;

@Service
@SuppressWarnings("unused")
public class PluginFetchServiceImpl implements PluginFetchService {
    final static String MANIFEST_CACHE_KEY = "'PluginFetchServiceImpl_manifest_' + #p0.pluginId";
    final static String API_DOCS_CACHE_KEY = "'PluginFetchServiceImpl_apiDocs_' + #p0.pluginId";

    private String fetch(String url) {
        String info = HttpUtils.get(url);
        return StringUtils.isNotBlank(info) ? info : url;
    }

    @Override
    @LongPeriodCache(keyBy = MANIFEST_CACHE_KEY)
    public String fetchManifestInfo(PluginInfo pluginInfo) {
        return fetch(pluginInfo.getManifestInfo());
    }

    @Override
    @LongPeriodCache(keyBy = API_DOCS_CACHE_KEY)
    public String fetchApiDocsInfo(PluginInfo pluginInfo) {
        return fetch(pluginInfo.getApiInfo());
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = LONG_PERIOD_CACHE_NAME, key = MANIFEST_CACHE_KEY),
            @CacheEvict(cacheNames = LONG_PERIOD_CACHE_NAME, key = API_DOCS_CACHE_KEY)
    })
    public void clearCaches(PluginInfo pluginInfo) {}
}
