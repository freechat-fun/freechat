package fun.freechat.service.character;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.service.ModerationException;
import dev.langchain4j.service.TokenStream;
import fun.freechat.model.User;

public interface CharacterAiService {
    String start(User user,
                 String userNickname,
                 String userProfile,
                 String characterNickname,
                 String about,
                 String backendId,
                 String ext);
    boolean delete(String chatId);
    default Response<AiMessage> send(String chatId, String text) {
        return send(chatId, UserMessage.from(text), null);
    }
    Response<AiMessage> send(String chatId, ChatMessage message, String context) throws ModerationException;
    TokenStream streamSend(String chatId, ChatMessage message, String context);
    default Object asMemoryId(String chatId) {
        return chatId;
    }
}
