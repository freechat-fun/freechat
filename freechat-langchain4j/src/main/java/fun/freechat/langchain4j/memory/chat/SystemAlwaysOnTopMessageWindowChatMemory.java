package fun.freechat.langchain4j.memory.chat;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import lombok.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static dev.langchain4j.internal.ValidationUtils.ensureGreaterThanZero;
import static dev.langchain4j.internal.ValidationUtils.ensureNotNull;

public class SystemAlwaysOnTopMessageWindowChatMemory implements ChatMemory {
    private static final Logger log = LoggerFactory.getLogger(SystemAlwaysOnTopMessageWindowChatMemory.class);

    private final Object id;
    private final Integer maxMessages;
    private final ChatMemoryStore store;

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
            Optional<SystemMessage> systemMessage = findSystemMessage(messages);
            if (systemMessage.isPresent()) {
                if (systemMessage.get().equals(message)) {
                    return; // do not add the same system message
                } else {
                    messages.remove(systemMessage.get()); // need to replace existing system message
                }
            }
            messages.add(0, message);
        } else {
            messages.add(message);
        }
        ensureCapacity(messages, maxMessages);
        store.updateMessages(id, messages);
    }

    private static Optional<SystemMessage> findSystemMessage(List<ChatMessage> messages) {
        return messages.stream()
                .filter(message -> message instanceof SystemMessage)
                .map(message -> (SystemMessage) message)
                .findAny();
    }

    @Override
    public List<ChatMessage> messages() {
        List<ChatMessage> messages = new LinkedList<>(store.getMessages(id));
        ensureCapacity(messages, maxMessages);
        return messages;
    }

    @Override
    public void clear() {
        store.deleteMessages(id);
    }


    private static void ensureCapacity(List<ChatMessage> messages, int maxMessages) {
        while (messages.size() > maxMessages) {
            int messageToRemove = 0;
            if (messages.get(0) instanceof SystemMessage) {
                messageToRemove = 1;
            }
            ChatMessage removedMessage = messages.remove(messageToRemove);
            log.trace("Removing the following message to comply with the capacity requirements: {}", removedMessage);
        }
    }
}
