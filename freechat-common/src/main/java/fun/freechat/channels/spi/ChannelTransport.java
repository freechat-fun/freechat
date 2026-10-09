package fun.freechat.channels.spi;

public interface ChannelTransport {
    ChannelReceipt sendText(ChannelAddress address, ChannelText text) throws ChannelFailure;
}
