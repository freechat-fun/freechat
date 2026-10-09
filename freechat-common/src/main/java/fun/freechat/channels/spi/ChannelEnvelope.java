package fun.freechat.channels.spi;

import java.util.Objects;

public record ChannelEnvelope<E>(ChannelAddress address, String eventId, E payload) {
    public ChannelEnvelope {
        Objects.requireNonNull(address);
        Objects.requireNonNull(payload);
    }

    @Override
    public String toString() {
        return "ChannelEnvelope[address=" + address + "]";
    }
}
