package fun.freechat.channels.spi;

import java.util.Set;

public interface ChannelMediaTransport {
    Set<ChannelMedia.Kind> mediaKinds();

    ChannelReceipt sendMedia(ChannelAddress address, ChannelMedia media) throws ChannelFailure;
}
