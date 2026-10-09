package fun.freechat.channels.spi;

import java.util.concurrent.CompletionStage;

@FunctionalInterface
public interface ChannelInboundHandler<E> {
    CompletionStage<Void> handle(ChannelEnvelope<E> envelope, ChannelTurnContext turn) throws Exception;
}
