package fun.freechat.api.util;

import fun.freechat.model.PluginInfo;
import fun.freechat.service.enums.ToolSpecFormat;
import fun.freechat.service.plugin.PluginToolSpecFormatService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ToolSpecFormatUtils implements ApplicationContextAware {
    private static PluginToolSpecFormatService formatter;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        formatter = applicationContext.getBean(PluginToolSpecFormatService.class);
    }

    public static Pair<String, String> convert(PluginInfo pluginInfo) {
        Pair<ToolSpecFormat, String> toolSpecPair = formatter.convert(pluginInfo);
        return Pair.of(toolSpecPair.getLeft().text(), toolSpecPair.getRight());
    }
}
