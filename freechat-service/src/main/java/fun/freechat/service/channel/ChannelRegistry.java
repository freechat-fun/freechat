package fun.freechat.service.channel;

import fun.freechat.channels.spi.ChannelPlugin;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public final class ChannelRegistry {
    private final Map<String, ChannelPlugin<?>> plugins;

    public ChannelRegistry(List<ChannelPlugin<?>> plugins) {
        Map<String, ChannelPlugin<?>> registered = new LinkedHashMap<>();
        for (ChannelPlugin<?> plugin : plugins) {
            String id = plugin.id();
            if (id == null || !id.matches("[a-z][a-z0-9-]{0,63}")) {
                throw new IllegalArgumentException("Invalid channel plugin ID");
            }
            Objects.requireNonNull(plugin.transport(), "Channel transport is required");
            Objects.requireNonNull(plugin.inboundHandler(), "Channel inbound handler is required");
            Objects.requireNonNull(plugin.policy(), "Channel policy is required");
            if (registered.putIfAbsent(id, plugin) != null) {
                throw new IllegalArgumentException("Duplicate channel plugin ID: " + id);
            }
        }
        this.plugins = Map.copyOf(registered);
    }

    public Collection<ChannelPlugin<?>> plugins() {
        return plugins.values();
    }

    public Optional<ChannelPlugin<?>> find(String id) {
        return Optional.ofNullable(plugins.get(id));
    }
}
