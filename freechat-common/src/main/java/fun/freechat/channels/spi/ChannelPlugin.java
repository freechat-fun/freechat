package fun.freechat.channels.spi;

public interface ChannelPlugin<E> extends AutoCloseable {
    String id();

    ChannelTransport transport();

    ChannelInboundHandler<E> inboundHandler();

    default ChannelPolicy policy() {
        return ChannelPolicy.defaults();
    }

    void start(ChannelRuntimeContext<E> runtime) throws Exception;

    void stopReceiving();

    @Override
    void close();
}
