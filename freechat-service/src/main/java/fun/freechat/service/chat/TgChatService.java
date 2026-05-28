package fun.freechat.service.chat;

import fun.freechat.model.TgChat;
import java.util.Optional;

public interface TgChatService {

    TgChat getOrCreate(String backendId, Long tgChatId, String chatType, String title);

    Optional<TgChat> findByBackendAndChat(String backendId, Long tgChatId);

    boolean delete(String chatId);
}
