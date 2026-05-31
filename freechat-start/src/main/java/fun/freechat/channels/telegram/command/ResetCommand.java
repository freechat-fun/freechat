package fun.freechat.channels.telegram.command;

import fun.freechat.channels.telegram.TelegramChannel;
import fun.freechat.service.chat.ChatService;
import fun.freechat.service.chat.TgChatBindingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * /reset — clears the conversation memory for the bound FreeChat chat session, mirroring
 * {@link fun.freechat.api.ChatApi#clearMemory}. Does NOT create a session if none exists; the user
 * is told to send a message to start one.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ResetCommand implements TelegramCommandHandler {

    private static final String CONFIRMATION = "Memory cleared. Say something to start fresh.";
    private static final String NOTHING_TO_RESET =
            "There's no active conversation to reset yet — just send me a message to start one.";

    private final TgChatBindingService tgChatBindingService;
    private final ChatService chatService;

    @Override
    public String name() {
        return "reset";
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
        Long tgChatId = chat.getId();

        String chatId = tgChatBindingService.findChatId(backendId, tgChatId);
        String reply;
        if (StringUtils.isBlank(chatId)) {
            reply = NOTHING_TO_RESET;
        } else {
            try {
                chatService.clearMemory(chatId);
                reply = CONFIRMATION;
            } catch (Exception e) {
                log.warn("/reset clearMemory failed for chat {}", chatId, e);
                reply = "Sorry — couldn't reset the conversation just now. Please try again.";
            }
        }

        try {
            channel.sendText(backendId, tgChatId, reply);
        } catch (TelegramApiException e) {
            log.warn("/reset reply failed for chat {}", tgChatId, e);
        }
    }
}
