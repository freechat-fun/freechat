package fun.freechat.channels.telegram;

import fun.freechat.channels.spi.ChannelBackendListener;
import fun.freechat.channels.spi.ChannelInboundHandler;
import fun.freechat.channels.spi.ChannelPlugin;
import fun.freechat.channels.spi.ChannelRuntimeContext;
import fun.freechat.channels.spi.ChannelTransport;
import fun.freechat.channels.telegram.handler.TelegramUpdateDispatcher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public final class TelegramChannelPlugin implements ChannelPlugin<Update>, ChannelBackendListener {
    private final TelegramChannelManager manager;
    private final DefaultTelegramChannel transport;
    private final TelegramUpdateDispatcher dispatcher;

    @Override
    public String id() {
        return "telegram";
    }

    @Override
    public ChannelTransport transport() {
        return transport;
    }

    @Override
    public ChannelInboundHandler<Update> inboundHandler() {
        return dispatcher;
    }

    @Override
    public void start(ChannelRuntimeContext<Update> runtime) {
        manager.start(runtime);
    }

    @Override
    public void backendChanged(String backendId) {
        manager.activate(backendId);
    }

    @Override
    public void reconcile() {
        manager.reconcile();
    }

    @Override
    public void stopReceiving() {
        manager.stopReceiving();
    }

    @Override
    public void close() {
        manager.shutdown();
    }
}
