package fun.freechat.service.character;

import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import fun.freechat.service.ai.message.ChatMessage;

import java.util.List;

public interface ChatMemoryService extends ChatMemoryStore {
    List<ChatMessage> listChatMessages(Object memoryId);
}
