package fun.freechat.channels.spi;

public interface ChannelBackendListener {
    void backendChanged(String backendId);

    void reconcile();
}
