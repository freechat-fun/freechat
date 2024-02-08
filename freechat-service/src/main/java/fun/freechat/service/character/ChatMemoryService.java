package fun.freechat.service.character;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;

import java.util.List;

public interface ChatMemoryService extends ChatMemoryStore {
    List<ChatMessage> listChatMessages(Object memoryId);
}
