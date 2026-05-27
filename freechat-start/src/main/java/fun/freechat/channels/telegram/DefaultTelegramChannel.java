package fun.freechat.channels.telegram;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
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
@ConditionalOnProperty(name = "channels.telegram.enabled", havingValue = "true")
@RequiredArgsConstructor
public class DefaultTelegramChannel implements TelegramChannel {

    private final TelegramClient telegramClient;

    @Override
    public Message sendText(Long chatId, String text) throws TelegramApiException {
        SendMessage request = SendMessage.builder().chatId(chatId).text(text).build();
        return telegramClient.execute(request);
    }

    @Override
    public Message sendPhoto(Long chatId, InputFile photo, String caption) throws TelegramApiException {
        SendPhoto request =
                SendPhoto.builder().chatId(chatId).photo(photo).caption(caption).build();
        return telegramClient.execute(request);
    }

    @Override
    public Message sendVoice(Long chatId, InputFile voice, String caption) throws TelegramApiException {
        SendVoice request =
                SendVoice.builder().chatId(chatId).voice(voice).caption(caption).build();
        return telegramClient.execute(request);
    }

    @Override
    public Message sendVideo(Long chatId, InputFile video, String caption) throws TelegramApiException {
        SendVideo request =
                SendVideo.builder().chatId(chatId).video(video).caption(caption).build();
        return telegramClient.execute(request);
    }

    @Override
    public Message sendAudio(Long chatId, InputFile audio, String caption) throws TelegramApiException {
        SendAudio request =
                SendAudio.builder().chatId(chatId).audio(audio).caption(caption).build();
        return telegramClient.execute(request);
    }

    @Override
    public Message sendDocument(Long chatId, InputFile document, String caption) throws TelegramApiException {
        SendDocument request = SendDocument.builder()
                .chatId(chatId)
                .document(document)
                .caption(caption)
                .build();
        return telegramClient.execute(request);
    }
}
