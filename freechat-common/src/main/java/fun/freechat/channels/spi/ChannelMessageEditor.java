package fun.freechat.channels.spi;

public interface ChannelMessageEditor {
    void editText(ChannelAddress address, String messageId, ChannelText text) throws ChannelFailure;
}
