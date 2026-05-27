package fun.freechat.channels.telegram.handler;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface TelegramMessageHandler {

    void handle(Update update);
}
