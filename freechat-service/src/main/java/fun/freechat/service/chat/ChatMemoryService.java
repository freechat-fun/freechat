package fun.freechat.service.chat;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.output.TokenUsage;
import fun.freechat.langchain4j.memory.chat.TokenUsageChatMemoryStore;

import java.util.List;

public interface ChatMemoryService extends TokenUsageChatMemoryStore {
    Long updateChatMessageTokenUsage(Object memoryId, AiMessage message, TokenUsage tokenUsage);
    List<ChatMessageRecord> listAllChatMessages(Object memoryId);
    ChatMessageRecord getLatestChatMessage(Object memoryId);
    List<Long> rollback(Object memoryId, Integer count);
    MemoryUsage usage(Object memoryId);
    String getLang(Object memoryId);
    Long roughCount(Object memoryId);
    String loadSystemMessage(Long id);
    ChatMessageRecord get(Long id);
}
