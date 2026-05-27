package fun.freechat.channels.telegram.handler;

import fun.freechat.channels.telegram.TelegramChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
@ConditionalOnProperty(name = "channels.telegram.enabled", havingValue = "true")
@RequiredArgsConstructor
public class MockTelegramMessageHandler implements TelegramMessageHandler {

    private final TelegramChannel telegramChannel;

    @Override
    public void handle(Update update) {
        if (!update.hasMessage()) {
            return;
        }
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        String kind = detectKind(message);
        log.info("[mock] telegram update from chatId={} kind={}", chatId, kind);
        try {
            telegramChannel.sendText(chatId, "[mock] received " + kind);
        } catch (TelegramApiException e) {
            log.warn("[mock] failed to reply to chatId={}", chatId, e);
        }
    }

    private String detectKind(Message message) {
        if (message.hasText()) {
            return "text";
        }
        if (message.hasPhoto()) {
            return "photo";
        }
        if (message.hasVoice()) {
            return "voice";
        }
        if (message.hasVideo()) {
            return "video";
        }
        if (message.hasAudio()) {
            return "audio";
        }
        if (message.hasDocument()) {
            return "document";
        }
        return "unsupported";
    }
}
