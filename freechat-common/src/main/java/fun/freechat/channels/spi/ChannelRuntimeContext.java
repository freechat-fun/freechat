package fun.freechat.channels.spi;

public interface ChannelRuntimeContext<E> {
    ChannelInstance<E> openInstance(String instanceId);
}
