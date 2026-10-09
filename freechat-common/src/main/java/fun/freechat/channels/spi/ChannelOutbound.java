package fun.freechat.channels.spi;

import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;

public interface ChannelOutbound {
    boolean supports(Class<?> capability);

    CompletableFuture<ChannelReceipt> sendText(ChannelText text);

    CompletableFuture<ChannelReceipt> sendMedia(ChannelMedia media);

    CompletableFuture<Void> editText(String messageId, ChannelText text);

    CompletableFuture<Void> sendTyping(BooleanSupplier stillNeeded);
}
