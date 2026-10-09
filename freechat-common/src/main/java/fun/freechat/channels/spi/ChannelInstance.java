package fun.freechat.channels.spi;

import java.util.concurrent.CompletableFuture;

public interface ChannelInstance<E> extends AutoCloseable {
    String id();

    boolean isActive();

    CompletableFuture<Void> receive(String conversationId, String eventId, E payload);

    ChannelOutbound outbound(String conversationId);

    @Override
    void close();
}
