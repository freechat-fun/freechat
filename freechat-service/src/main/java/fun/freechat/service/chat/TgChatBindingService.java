package fun.freechat.service.chat;

public interface TgChatBindingService {

    String getOrCreate(
            String backendId,
            Long tgChatId,
            String chatType,
            String title,
            Long tgUserId,
            String username,
            String firstName,
            String lastName);

    String findChatId(String backendId, Long tgChatId);
}
