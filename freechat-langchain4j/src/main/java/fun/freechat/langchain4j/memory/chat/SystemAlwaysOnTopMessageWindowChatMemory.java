package fun.freechat.langchain4j.memory.chat;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.model.output.TokenUsage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static dev.langchain4j.data.message.ChatMessageType.SYSTEM;
import static dev.langchain4j.data.message.ChatMessageType.USER;
import static dev.langchain4j.internal.ValidationUtils.ensureGreaterThanZero;
import static dev.langchain4j.internal.ValidationUtils.ensureNotNull;

@Slf4j
public class SystemAlwaysOnTopMessageWindowChatMemory implements ChatMemory {
    private final Object id;
    private final Integer maxMessages;
    private final ChatMemoryStore store;
    private SystemMessage latestSystemMessage;

    @Builder
    public SystemAlwaysOnTopMessageWindowChatMemory(String id, Integer maxMessages, ChatMemoryStore chatMemoryStore) {
        this.id = ensureNotNull(id, "id");
        this.maxMessages = ensureGreaterThanZero(maxMessages, "maxMessages");
        this.store = ensureNotNull(chatMemoryStore, "store");
    }

    @Override
    public Object id() {
        return id;
    }

    @Override
    public void add(ChatMessage message) {
        List<ChatMessage> messages = messages();
        if (message instanceof SystemMessage) {
            latestSystemMessage = (SystemMessage) message;
            if (!messages.isEmpty()) {
                return;
            }
        }
        messages.add(message);
        ensureCapacity(messages, maxMessages);
        store.updateMessages(id, messages);
    }

    public void addAiMessage(AiMessage message, TokenUsage usage) {
        if (store instanceof TokenUsageChatMemoryStore tokenUsageChatMemoryStore) {
            List<ChatMessage> messages = messages();
            messages.add(message);
            ensureCapacity(messages, maxMessages);
            tokenUsageChatMemoryStore.addAiMessage(id, message, usage);
        } else {
            add(message);
        }
    }

    @Override
    public List<ChatMessage> messages() {
        LinkedList<ChatMessage> messages = new LinkedList<>(store.getMessages(id));
        if (Objects.nonNull(latestSystemMessage)) {
            messages.removeFirst();
            messages.addFirst(latestSystemMessage);
        }
        ensureCapacity(messages, maxMessages);
        return messages;
    }

    @Override
    public void clear() {
        store.deleteMessages(id);
    }

    private static int firstNonSystemMessageIndex(List<ChatMessage> messages) {
        return messages.getFirst().type() == SYSTEM ? 1 : 0;
    }

    private static void ensureCapacity(List<ChatMessage> messages, int maxMessages) {
        if (messages.size() <= 1) {
            return;
        }
        int firstIndex = firstNonSystemMessageIndex(messages);
        while (messages.size() > maxMessages || messages.get(firstIndex).type() != USER) {
            ChatMessage removedMessage = messages.remove(firstIndex);
            log.trace("Removing the following message to comply with the capacity requirements: {}", removedMessage);
        }
    }
}
