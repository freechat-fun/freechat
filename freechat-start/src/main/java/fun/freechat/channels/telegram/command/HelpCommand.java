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
public class HelpCommand extends BotCommand {

    private static final String HELP_TEXT =
            "[mock] available commands:\n/start - Start the bot\n/help  - Show this help";

    private final TelegramChannel telegramChannel;

    public HelpCommand(TelegramChannel telegramChannel) {
        super("help", "Show available commands");
        this.telegramChannel = telegramChannel;
    }

    @Override
    public void execute(TelegramClient client, User user, Chat chat, String[] arguments) {
        try {
            telegramChannel.sendText(chat.getId(), HELP_TEXT);
        } catch (TelegramApiException e) {
            log.warn("[mock] failed to handle /help for chatId={}", chat.getId(), e);
        }
    }
}
