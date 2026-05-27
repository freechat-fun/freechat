package fun.freechat.channels.telegram;

import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface TelegramChannel {

    Message sendText(Long chatId, String text) throws TelegramApiException;

    Message sendPhoto(Long chatId, InputFile photo, String caption) throws TelegramApiException;

    Message sendVoice(Long chatId, InputFile voice, String caption) throws TelegramApiException;

    Message sendVideo(Long chatId, InputFile video, String caption) throws TelegramApiException;

    Message sendAudio(Long chatId, InputFile audio, String caption) throws TelegramApiException;

    Message sendDocument(Long chatId, InputFile document, String caption) throws TelegramApiException;
}
