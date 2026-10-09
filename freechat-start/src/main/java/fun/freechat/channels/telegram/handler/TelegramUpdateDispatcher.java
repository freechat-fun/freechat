package fun.freechat.channels.telegram.handler;

import fun.freechat.channels.spi.ChannelEnvelope;
import fun.freechat.channels.spi.ChannelInboundHandler;
import fun.freechat.channels.spi.ChannelTurnContext;
import fun.freechat.channels.telegram.command.TelegramCommandHandler;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@Component
@Slf4j
public class TelegramUpdateDispatcher implements ChannelInboundHandler<Update> {

    private final Map<String, TelegramCommandHandler> commandsByName;
    private final TelegramMessageHandler messageHandler;

    public TelegramUpdateDispatcher(
            List<TelegramCommandHandler> commandHandlers, TelegramMessageHandler messageHandler) {
        this.commandsByName =
                commandHandlers.stream().collect(Collectors.toMap(TelegramCommandHandler::name, h -> h, (a, b) -> a));
        this.messageHandler = messageHandler;
        log.info("Telegram dispatcher initialized with commands: {}", commandsByName.keySet());
    }

    @Override
    public CompletionStage<Void> handle(ChannelEnvelope<Update> envelope, ChannelTurnContext turn) {
        Update update = envelope.payload();
        String backendId = envelope.address().instanceId();
        if (!update.hasMessage() || turn.isCancelled()) {
            return CompletableFuture.completedFuture(null);
        }
        Message message = update.getMessage();
        if (message.isCommand()) {
            String cmd = parseCommand(message.getText());
            if (cmd != null) {
                TelegramCommandHandler handler = commandsByName.get(cmd);
                if (handler != null) {
                    return handler.execute(backendId, update, turn);
                }
            }
        }
        return messageHandler.handle(backendId, update, turn);
    }

    private static String parseCommand(String text) {
        if (text == null) {
            return null;
        }
        String first = text.split("\\s+", 2)[0];
        if (!first.startsWith("/") || first.length() < 2) {
            return null;
        }
        String name = first.substring(1);
        int at = name.indexOf('@');
        return at > 0 ? name.substring(0, at) : name;
    }
}
