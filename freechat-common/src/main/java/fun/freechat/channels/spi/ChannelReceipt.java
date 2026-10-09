package fun.freechat.channels.spi;

import java.util.Objects;

public record ChannelReceipt(String messageId) {
    public ChannelReceipt {
        Objects.requireNonNull(messageId);
    }
}
