package fun.freechat.channels.telegram.command;

import fun.freechat.channels.spi.ChannelText;
import fun.freechat.channels.spi.ChannelTurnContext;
import fun.freechat.service.chat.ChatSession;
import fun.freechat.service.chat.ChatSessionService;
import fun.freechat.service.chat.TgChatBindingService;
import fun.freechat.service.enums.ChatVar;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.message.Message;

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
    public CompletionStage<Void> execute(String backendId, Update update, ChannelTurnContext turn) {
        if (!update.hasMessage()) {
            return CompletableFuture.completedFuture(null);
        }
        Message message = update.getMessage();
        Chat chat = message.getChat();
        if (chat == null) {
            return CompletableFuture.completedFuture(null);
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
            return CompletableFuture.completedFuture(null);
        }

        String greeting = resolveCharacterGreeting(chatId);
        if (StringUtils.isBlank(greeting)) {
            greeting = FALLBACK_GREETING;
        }

        return turn.outbound()
                .sendText(new ChannelText(greeting, ChannelText.Format.MARKDOWN))
                .<Void>thenApply(receipt -> null)
                .whenComplete((ignored, failure) -> {
                    if (failure != null) {
                        log.warn("/start reply failed for chat {}", tgChatId);
                    }
                });
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
            log.warn("Failed to resolve character greeting for chat {}", chatId);
            return null;
        }
    }
}
