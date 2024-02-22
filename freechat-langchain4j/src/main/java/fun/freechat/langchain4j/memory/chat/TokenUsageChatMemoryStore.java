package fun.freechat.langchain4j.memory.chat;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.output.TokenUsage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;

public interface TokenUsageChatMemoryStore extends ChatMemoryStore {
    void addAiMessage(Object memoryId, AiMessage message, TokenUsage usage);
}
