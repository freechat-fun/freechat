package fun.freechat.service.chat;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.output.TokenUsage;
import fun.freechat.langchain4j.memory.chat.TokenUsageChatMemoryStore;

import java.util.List;

public interface ChatMemoryService extends TokenUsageChatMemoryStore {
    void updateChatMessageTokenUsage(Object memoryId, AiMessage message, TokenUsage tokenUsage);
    List<ChatMessageRecord> listChatMessages(Object memoryId);
    ChatMessageRecord getLatestChatMessage(Object memoryId);
    List<Long> rollback(Object memoryId, Integer count);
    MemoryUsage usage(Object memoryId);
}
