package fun.freechat.channels.telegram.command;

import fun.freechat.channels.telegram.TelegramChannel;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface TelegramCommandHandler {

    String name();

    void execute(String backendId, Update update, TelegramChannel channel);
}
