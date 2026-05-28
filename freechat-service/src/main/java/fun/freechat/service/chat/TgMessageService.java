package fun.freechat.service.chat;

import fun.freechat.model.TgMessage;
import java.util.List;

public interface TgMessageService {

    Long record(
            String chatId,
            Long tgMessageId,
            Long tgUserId,
            String direction,
            String messageType,
            String content,
            String payload);

    List<TgMessage> listByChat(String chatId, Integer limit, Integer offset);
}
