package fun.freechat.channels.spi;

public interface ChannelStatusTransport {
    void sendTyping(ChannelAddress address) throws ChannelFailure;
}
