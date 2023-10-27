package fun.freechat.service.character;

import fun.freechat.model.ChatContext;

import java.util.List;

public interface ChatContextService {
    String create(ChatContext context);
    boolean update(ChatContext context);
    boolean delete(String chatId);
    ChatContext get(String chatId);
    String getIdByBackend(String userId, String backendId);
    List<String> listIdsByUser(String userId);
    String getOwner(String chatId);
}
