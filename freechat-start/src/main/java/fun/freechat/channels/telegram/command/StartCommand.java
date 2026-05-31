package fun.freechat.channels.telegram.command;

import fun.freechat.channels.telegram.TelegramChannel;
import fun.freechat.service.chat.ChatSession;
import fun.freechat.service.chat.ChatSessionService;
import fun.freechat.service.chat.TgChatBindingService;
import fun.freechat.service.enums.ChatVar;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

    /** Used when the character has no personality greeting configured (or it resolved to blank). */
    private static final String FALLBACK_GREETING = "Hi! I'm ready when you are — just send a message.";

    private final TgChatBindingService tgChatBindingService;
    private final ChatSessionService chatSessionService;

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

        String chatId = tgChatBindingService.getOrCreate(
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

        String greeting = resolveCharacterGreeting(chatId);
        if (StringUtils.isBlank(greeting)) {
            greeting = FALLBACK_GREETING;
        }

        try {
            channel.sendText(backendId, tgChatId, greeting);
        } catch (TelegramApiException e) {
            log.warn("/start reply failed for chat {}", tgChatId, e);
        }
    }

    /**
     * Returns the character's personality greeting (CharacterInfo#greeting) resolved against the
     * chat session's variables, mirroring how {@code ChatApi.list()} renders greetings. Returns
     * null when no session is loaded or the greeting variable wasn't populated.
     */
    private String resolveCharacterGreeting(String chatId) {
        try {
            ChatSession session = chatSessionService.get(chatId);
            if (session == null) {
                return null;
            }
            Object greeting = session.getVariables().get(ChatVar.CHARACTER_GREETING.text());
            return greeting instanceof String s ? s : null;
        } catch (Exception e) {
            log.warn("Failed to resolve character greeting for chat {}", chatId, e);
            return null;
        }
    }
}
