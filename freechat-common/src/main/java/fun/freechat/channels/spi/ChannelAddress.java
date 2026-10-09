package fun.freechat.channels.spi;

public record ChannelAddress(String pluginId, String instanceId, String conversationId) {
    public ChannelAddress {
        if (pluginId == null || !pluginId.matches("[a-z][a-z0-9-]{0,63}")) {
            throw new IllegalArgumentException("Invalid channel plugin ID");
        }
        if (instanceId == null || instanceId.isBlank() || instanceId.length() > 256) {
            throw new IllegalArgumentException("Invalid channel instance ID");
        }
        if (conversationId == null || conversationId.isBlank() || conversationId.length() > 1024) {
            throw new IllegalArgumentException("Invalid channel conversation ID");
        }
    }
}
