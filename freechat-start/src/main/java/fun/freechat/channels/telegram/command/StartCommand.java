package fun.freechat.channels.telegram.command;

import fun.freechat.channels.telegram.TelegramChannel;
import fun.freechat.service.chat.TelegramChatBindingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@RequiredArgsConstructor
@Slf4j
public class StartCommand implements TelegramCommandHandler {

    private static final String GREETING = "Hi! I'm ready when you are — just send a message.";

    private final TelegramChatBindingService telegramChatBindingService;

    @Override
    public String name() {
        return "start";
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
        User from = message.getFrom();
        Long tgChatId = chat.getId();
        Long tgUserId = from == null ? null : from.getId();

        String chatId = telegramChatBindingService.getOrCreate(
                backendId,
                tgChatId,
                chat.getType(),
                chat.getTitle(),
                tgUserId,
                from == null ? null : from.getUserName(),
                from == null ? null : from.getFirstName(),
                from == null ? null : from.getLastName());
        if (chatId == null) {
            log.warn("/start could not bind chat for backend={} tgChatId={}", backendId, tgChatId);
            return;
        }

        try {
            channel.sendText(backendId, tgChatId, GREETING);
        } catch (TelegramApiException e) {
            log.warn("/start reply failed for chat {}", tgChatId, e);
        }
    }
}
