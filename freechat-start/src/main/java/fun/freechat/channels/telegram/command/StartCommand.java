package fun.freechat.channels.telegram.command;

import fun.freechat.channels.telegram.TelegramChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Slf4j
@Component
@ConditionalOnProperty(name = "channels.telegram.enabled", havingValue = "true")
public class StartCommand extends BotCommand {

    private final TelegramChannel telegramChannel;

    public StartCommand(TelegramChannel telegramChannel) {
        super("start", "Start the bot");
        this.telegramChannel = telegramChannel;
    }

    @Override
    public void execute(TelegramClient client, User user, Chat chat, String[] arguments) {
        try {
            telegramChannel.sendText(chat.getId(), "[mock] welcome");
        } catch (TelegramApiException e) {
            log.warn("[mock] failed to handle /start for chatId={}", chat.getId(), e);
        }
    }
}
