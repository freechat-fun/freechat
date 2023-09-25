package fun.freechat.service.character;

import fun.freechat.model.ChatContext;

public interface CharacterChatService {
    String create(String userId, String characterId, String context);
    boolean update(String chatId, String context);
    boolean delete(String chatId);
    ChatContext get(String chatId);
    String getChatId(String userId, String characterId);
    boolean replaceCharacter(String oldCharacterId, String newCharacterId);
}
