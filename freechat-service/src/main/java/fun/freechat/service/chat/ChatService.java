package fun.freechat.service.chat;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.service.ModerationException;
import dev.langchain4j.service.TokenStream;
import fun.freechat.model.CharacterInfo;
import fun.freechat.model.ChatContext;
import fun.freechat.model.User;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;

public interface ChatService {
    String start(User user,
                 String userNickname,
                 String userProfile,
                 String characterNickname,
                 String about,
                 String backendId,
                 String apiKeyName,
                 String apiKeyValue,
                 String ext);
    boolean delete(String chatId);
    List<Triple<ChatContext, CharacterInfo, ChatMessageRecord>> list(User user);
    String getDefaultChatId(User user, String characterUid);
    Pair<ChatResponse, Long> send(String chatId, ChatMessage message, String context) throws ModerationException;
    TokenStream streamSend(String chatId, ChatMessage message, String context);
    static Object asMemoryId(String chatId) {
        return chatId;
    }
    static String asChatId(Object memoryId) {
        return (String) memoryId;
    }
    void clearMemory(String chatId);
    ChatResponse sendAssistant(String chatId, String assistantUid);
    TokenStream streamSendAssistant(String chatId, String assistantUid);
}
