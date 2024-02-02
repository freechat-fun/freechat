package fun.freechat.service.character;

import dev.langchain4j.model.output.Response;
import dev.langchain4j.service.ModerationException;
import dev.langchain4j.service.TokenStream;
import fun.freechat.model.User;
import fun.freechat.service.ai.message.ChatMessage;
import fun.freechat.service.enums.PromptRole;

public interface CharacterAiService {
    String start(User user,
                 String userNickname,
                 String userProfile,
                 String characterNickname,
                 String about,
                 String backendId,
                 String ext);
    boolean delete(String chatId);
    default Response<ChatMessage> send(String chatId, String text) {
        return send(chatId, ChatMessage.from(PromptRole.USER, text), null);
    }
    Response<ChatMessage> send(String chatId, ChatMessage message, String context) throws ModerationException;
    TokenStream streamSend(String chatId, ChatMessage message, String context);
    default Object asMemoryId(String chatId) {
        return chatId;
    }
}
