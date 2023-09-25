package fun.freechat.api.util;

import fun.freechat.model.PluginInfo;
import fun.freechat.service.enums.FunctionFormat;
import fun.freechat.service.plugin.PluginFormatService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class FunctionFormatUtils implements ApplicationContextAware {
    private static PluginFormatService formatter;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        formatter = applicationContext.getBean(PluginFormatService.class);
    }

    public static Pair<String, String> convert(PluginInfo pluginInfo) {
        Pair<FunctionFormat, String> functionInfo = formatter.convert(pluginInfo);
        return Pair.of(functionInfo.getLeft().text(), functionInfo.getRight());
    }
}
