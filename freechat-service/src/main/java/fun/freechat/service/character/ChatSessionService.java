package fun.freechat.service.character;

import dev.langchain4j.model.moderation.Moderation;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public interface ChatSessionService {
    ChatSession get(String chatId);
    void reset(String chatId);
    CompletableFuture<Moderation> triggerModerationIfNeeded(
            ChatSession session,
            List<dev.langchain4j.data.message.ChatMessage> messages);
    void verifyModerationIfNeeded(
            ChatSession session,
            Future<dev.langchain4j.model.moderation.Moderation> moderationFuture);
}
