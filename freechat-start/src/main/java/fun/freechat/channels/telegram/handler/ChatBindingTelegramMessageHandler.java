package fun.freechat.channels.telegram.handler;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.service.TokenStream;
import fun.freechat.channels.telegram.TelegramChannel;
import fun.freechat.service.chat.ChatService;
import fun.freechat.service.chat.TgChatBindingService;
import fun.freechat.service.chat.TgMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatBindingTelegramMessageHandler implements TelegramMessageHandler {

    private final TgChatBindingService tgChatBindingService;
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

        String chatId = tgChatBindingService.getOrCreate(
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

        TelegramStreamingReplyEmitter emitter = new TelegramStreamingReplyEmitter(telegramChannel, backendId, tgChatId);
        try {
            emitter.start();
        } catch (Exception e) {
            log.warn("Failed to send streaming placeholder for chat {}", chatId, e);
            return;
        }

        try {
            assert text != null;
            TokenStream stream = chatService.streamSend(chatId, UserMessage.from(text), null);
            if (stream == null) {
                log.warn("ChatService.streamSend returned null for chat {}", chatId);
                emitter.complete();
                return;
            }
            stream.onPartialResponse(emitter::append)
                    .onCompleteResponse(response -> {
                        String finalText = emitter.complete();
                        Long lastId = emitter.lastMessageId();
                        if (lastId != null) {
                            tgMessageService.record(chatId, lastId, null, "out", "text", finalText, null);
                        }
                    })
                    .onError(err -> {
                        log.warn("Streaming reply errored for chat {}", chatId, err);
                        String partial = emitter.complete();
                        Long lastId = emitter.lastMessageId();
                        if (lastId != null && !partial.isBlank()) {
                            tgMessageService.record(chatId, lastId, null, "out", "text", partial, null);
                        }
                    })
                    .start();
        } catch (Exception e) {
            log.error("Failed to start streaming reply for chat {}", chatId, e);
            emitter.complete();
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
