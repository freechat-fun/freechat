package fun.freechat.service.chat;

import fun.freechat.service.chat.memory.MemoryInvocation;
import fun.freechat.service.chat.memory.MemoryTurnLifetime;
import java.util.Optional;

public interface LongTermChatMemoryStore {
    boolean enabled(String chatId);

    Optional<Binding> open(String chatId, ChatSession session);

    record Binding(ChatSession session, MemoryInvocation memory, MemoryTurnLifetime lifetime) implements AutoCloseable {
        @Override
        public void close() {
            lifetime.close();
        }
    }
}
