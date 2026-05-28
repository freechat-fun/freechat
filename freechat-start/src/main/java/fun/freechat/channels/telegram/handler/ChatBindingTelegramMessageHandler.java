package fun.freechat.channels.telegram.handler;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.response.ChatResponse;
import fun.freechat.channels.telegram.TelegramChannel;
import fun.freechat.service.chat.ChatService;
import fun.freechat.service.chat.TelegramChatBindingService;
import fun.freechat.service.chat.TgMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatBindingTelegramMessageHandler implements TelegramMessageHandler {

    private final TelegramChatBindingService telegramChatBindingService;
    private final TgMessageService tgMessageService;
    private final ChatService chatService;
    private final TelegramChannel telegramChannel;

    @Override
    public void handle(String backendId, Update update) {
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
        String chatType = chat.getType();
        String title = chat.getTitle();

        String chatId = telegramChatBindingService.getOrCreate(
                backendId,
                tgChatId,
                chatType,
                title,
                tgUserId,
                from == null ? null : from.getUserName(),
                from == null ? null : from.getFirstName(),
                from == null ? null : from.getLastName());
        if (chatId == null) {
            log.warn("Failed to bind chat for backend={} tgChatId={}", backendId, tgChatId);
            return;
        }

        String text = message.hasText() ? message.getText() : null;
        String kind = detectKind(message);
        tgMessageService.record(chatId, message.getMessageId().longValue(), tgUserId, "in", kind, text, null);

        if (!message.hasText()) {
            return;
        }

        try {
            Pair<ChatResponse, Long> result = chatService.send(chatId, UserMessage.from(text), null);
            if (result == null || result.getLeft() == null) {
                log.warn("ChatService.send returned null for chat {}", chatId);
                return;
            }
            String replyText = result.getLeft().aiMessage().text();
            if (StringUtils.isBlank(replyText)) {
                return;
            }
            Message sent = telegramChannel.sendText(backendId, tgChatId, replyText);
            tgMessageService.record(chatId, sent.getMessageId().longValue(), null, "out", "text", replyText, null);
        } catch (Exception e) {
            log.error("Failed to handle inbound telegram message for chat {}", chatId, e);
        }
    }

    private static String detectKind(Message m) {
        if (m.hasText()) {
            return "text";
        }
        if (m.hasPhoto()) {
            return "photo";
        }
        if (m.hasVoice()) {
            return "voice";
        }
        if (m.hasVideo()) {
            return "video";
        }
        if (m.hasAudio()) {
            return "audio";
        }
        if (m.hasDocument()) {
            return "document";
        }
        if (m.isCommand()) {
            return "command";
        }
        return "unsupported";
    }
}
