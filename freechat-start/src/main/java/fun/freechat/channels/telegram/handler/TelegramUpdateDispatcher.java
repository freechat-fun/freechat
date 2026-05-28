package fun.freechat.channels.telegram.handler;

import fun.freechat.channels.telegram.TelegramChannel;
import fun.freechat.channels.telegram.command.TelegramCommandHandler;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@Component
@Slf4j
public class TelegramUpdateDispatcher {

    private final Map<String, TelegramCommandHandler> commandsByName;
    private final TelegramMessageHandler messageHandler;
    private final TelegramChannel channel;

    public TelegramUpdateDispatcher(
            List<TelegramCommandHandler> commandHandlers,
            TelegramMessageHandler messageHandler,
            TelegramChannel channel) {
        this.commandsByName =
                commandHandlers.stream().collect(Collectors.toMap(TelegramCommandHandler::name, h -> h, (a, b) -> a));
        this.messageHandler = messageHandler;
        this.channel = channel;
        log.info("Telegram dispatcher initialized with commands: {}", commandsByName.keySet());
    }

    public void dispatch(String backendId, Update update) {
        if (!update.hasMessage()) {
            return;
        }
        Message message = update.getMessage();
        if (message.isCommand()) {
            String cmd = parseCommand(message.getText());
            if (cmd != null) {
                TelegramCommandHandler handler = commandsByName.get(cmd);
                if (handler != null) {
                    handler.execute(backendId, update, channel);
                    return;
                }
            }
        }
        messageHandler.handle(backendId, update);
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
