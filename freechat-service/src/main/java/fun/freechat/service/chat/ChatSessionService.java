package fun.freechat.service.chat;

import dev.langchain4j.model.moderation.Moderation;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import fun.freechat.model.ChatContext;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public interface ChatSessionService {
    ChatSession get(String chatId);
    ChatSession get(ChatContext chatContext);
    void reset(String chatId);
    CompletableFuture<Moderation> triggerModerationIfNeeded(
            ChatSession session,
            List<dev.langchain4j.data.message.ChatMessage> messages);
    void verifyModerationIfNeeded(Future<dev.langchain4j.model.moderation.Moderation> moderationFuture);
    ChatSession createTemporary(ChatContext chatContext, ChatMemoryStore chatMemoryStore);
}
