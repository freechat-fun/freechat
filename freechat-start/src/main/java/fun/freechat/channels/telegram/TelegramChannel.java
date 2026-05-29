package fun.freechat.channels.telegram;

import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface TelegramChannel {

    Message sendText(String backendId, Long chatId, String text) throws TelegramApiException;

    Message sendPhoto(String backendId, Long chatId, InputFile photo, String caption) throws TelegramApiException;

    Message sendVoice(String backendId, Long chatId, InputFile voice, String caption) throws TelegramApiException;

    Message sendVideo(String backendId, Long chatId, InputFile video, String caption) throws TelegramApiException;

    Message sendAudio(String backendId, Long chatId, InputFile audio, String caption) throws TelegramApiException;

    Message sendDocument(String backendId, Long chatId, InputFile document, String caption) throws TelegramApiException;

    /**
     * Edits a previously sent text message in place. {@code parseMode} may be null for plain text
     * (recommended for partial / mid-stream edits) or {@code "Markdown"} for the final flush.
     */
    void editText(String backendId, Long chatId, Long messageId, String text, String parseMode)
            throws TelegramApiException;

    String getInviteLink(String backendId);

    String getBotUsername(String backendId);
}
