package fun.freechat.service.chat;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.rag.RetrievalAugmentor;

import java.util.List;

public interface LongTermChatMemoryStore {
    List<ChatMessage> getMessages(Object memoryId,
                                  UserMessage userMessage,
                                  List<ChatMessage> chatMemory,
                                  RetrievalAugmentor retriever);
    void updateMessages(Object memoryId, List<ChatMessageRecord> messages);
    void deleteMessages(Object memoryId);
}
