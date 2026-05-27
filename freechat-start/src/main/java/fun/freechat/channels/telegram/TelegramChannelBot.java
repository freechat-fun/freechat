package fun.freechat.channels.telegram;

import fun.freechat.channels.telegram.handler.TelegramMessageHandler;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.CommandLongPollingTelegramBot;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Slf4j
@Component
@ConditionalOnProperty(name = "channels.telegram.enabled", havingValue = "true")
public class TelegramChannelBot extends CommandLongPollingTelegramBot implements SpringLongPollingBot {

    private final String botToken;
    private final TelegramMessageHandler messageHandler;

    public TelegramChannelBot(
            TelegramClient telegramClient,
            @Value("${channels.telegram.bot-token}") String botToken,
            @Value("${channels.telegram.bot-username}") String botUsername,
            List<BotCommand> commands,
            TelegramMessageHandler messageHandler) {
        super(telegramClient, true, () -> botUsername);
        this.botToken = botToken;
        this.messageHandler = messageHandler;
        commands.forEach(this::register);
        log.info("Telegram channel bot initialized: username={}, commands={}", botUsername, commands.size());
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        messageHandler.handle(update);
    }
}
