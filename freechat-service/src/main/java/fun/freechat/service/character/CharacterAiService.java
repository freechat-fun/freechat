package fun.freechat.service.character;

import dev.langchain4j.model.output.Response;
import dev.langchain4j.service.ModerationException;
import dev.langchain4j.service.TokenStream;
import fun.freechat.model.User;
import fun.freechat.service.ai.message.ChatMessage;

public interface CharacterAiService {
    String start(User user,
                 String userNickname,
                 String userProfile,
                 String characterNickname,
                 String backendId,
                 String ext);
    boolean delete(String chatId);
    Response<ChatMessage> send(String chatId, String text) throws ModerationException;
    TokenStream streamSend(String chatId, String text);
}
