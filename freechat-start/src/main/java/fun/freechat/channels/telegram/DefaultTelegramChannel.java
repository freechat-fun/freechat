package fun.freechat.channels.telegram;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultTelegramChannel implements TelegramChannel {

    private final TelegramChannelManager manager;

    private TelegramClient client(String backendId) {
        TelegramClient c = manager.getClient(backendId);
        if (c == null) {
            throw new IllegalStateException("No active Telegram bot for backendId=" + backendId);
        }
        return c;
    }

    @Override
    public Message sendText(String backendId, Long chatId, String text) throws TelegramApiException {
        return client(backendId)
                .execute(SendMessage.builder()
                        .chatId(chatId)
                        .text(text)
                        .parseMode(ParseMode.MARKDOWN)
                        .build());
    }

    @Override
    public Message sendPhoto(String backendId, Long chatId, InputFile photo, String caption)
            throws TelegramApiException {
        return client(backendId)
                .execute(SendPhoto.builder()
                        .chatId(chatId)
                        .photo(photo)
                        .caption(caption)
                        .build());
    }

    @Override
    public Message sendVoice(String backendId, Long chatId, InputFile voice, String caption)
            throws TelegramApiException {
        return client(backendId)
                .execute(SendVoice.builder()
                        .chatId(chatId)
                        .voice(voice)
                        .caption(caption)
                        .build());
    }

    @Override
    public Message sendVideo(String backendId, Long chatId, InputFile video, String caption)
            throws TelegramApiException {
        return client(backendId)
                .execute(SendVideo.builder()
                        .chatId(chatId)
                        .video(video)
                        .caption(caption)
                        .build());
    }

    @Override
    public Message sendAudio(String backendId, Long chatId, InputFile audio, String caption)
            throws TelegramApiException {
        return client(backendId)
                .execute(SendAudio.builder()
                        .chatId(chatId)
                        .audio(audio)
                        .caption(caption)
                        .build());
    }

    @Override
    public Message sendDocument(String backendId, Long chatId, InputFile document, String caption)
            throws TelegramApiException {
        return client(backendId)
                .execute(SendDocument.builder()
                        .chatId(chatId)
                        .document(document)
                        .caption(caption)
                        .build());
    }

    @Override
    public String getInviteLink(String backendId) {
        return manager.getInviteLink(backendId);
    }

    @Override
    public String getBotUsername(String backendId) {
        return manager.getUsername(backendId);
    }
}
