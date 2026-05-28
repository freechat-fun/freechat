package fun.freechat.channels.telegram.command;

import fun.freechat.channels.telegram.TelegramChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Slf4j
public class HelpCommand implements TelegramCommandHandler {

    private static final String HELP_TEXT =
            "Available commands:\n/start - Start chatting with me\n/help  - Show this help";

    @Override
    public String name() {
        return "help";
    }

    @Override
    public void execute(String backendId, Update update, TelegramChannel channel) {
        if (!update.hasMessage()) {
            return;
        }
        Message message = update.getMessage();
        Chat chat = message.getChat();
        if (chat == null) {
            return;
        }
        try {
            channel.sendText(backendId, chat.getId(), HELP_TEXT);
        } catch (TelegramApiException e) {
            log.warn("/help reply failed for chat {}", chat.getId(), e);
        }
    }
}
