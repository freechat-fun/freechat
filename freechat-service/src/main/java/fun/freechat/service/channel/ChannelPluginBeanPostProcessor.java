package fun.freechat.service.channel;

import fun.freechat.channels.spi.ChannelPlugin;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;

public final class ChannelPluginBeanPostProcessor implements BeanPostProcessor {
    private final ConfigurableListableBeanFactory beans;

    public ChannelPluginBeanPostProcessor(ConfigurableListableBeanFactory beans) {
        this.beans = beans;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String name) {
        if (bean instanceof ChannelPlugin<?> && beans.containsBeanDefinition(name)) {
            // Only the runtime may close plugins after their active work has drained.
            ((AbstractBeanDefinition) beans.getMergedBeanDefinition(name)).setDestroyMethodName("");
        }
        return bean;
    }
}
