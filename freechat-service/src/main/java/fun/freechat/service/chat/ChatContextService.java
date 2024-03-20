package fun.freechat.service.chat;

import fun.freechat.model.ChatContext;

import java.util.List;

public interface ChatContextService {
    ChatContext create(ChatContext context);
    boolean update(ChatContext context);
    boolean delete(String chatId);
    ChatContext get(String chatId);
    List<ChatContext> list(String userId);
    String getIdByBackend(String userId, String backendId);
    List<String> listIds(String userId);
    List<String> listIdsByBackend(String backendId);
    String getChatOwner(String chatId);
    String getCharacterOwner(String chatId);
}
