package fun.freechat.service.chat.impl;

import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.message.*;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.AugmentationRequest;
import dev.langchain4j.rag.AugmentationResult;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.filter.Filter;
import fun.freechat.service.chat.ChatMemoryReducedEvent;
import fun.freechat.service.chat.ChatMemoryService;
import fun.freechat.service.chat.ChatMessageRecord;
import fun.freechat.service.chat.LongTermChatMemoryStore;
import fun.freechat.service.rag.EmbeddingModelService;
import fun.freechat.service.rag.EmbeddingStoreService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static dev.langchain4j.data.message.ChatMessageType.AI;
import static dev.langchain4j.data.message.ChatMessageType.USER;
import static dev.langchain4j.store.embedding.filter.MetadataFilterBuilder.metadataKey;
import static fun.freechat.service.enums.EmbeddingRecordMeta.*;
import static fun.freechat.service.enums.EmbeddingStoreType.longTermMemoryTypeForLang;
import static fun.freechat.service.util.PromptUtils.toSingleText;

@Service
@Slf4j
@SuppressWarnings("unused")
public class LongTermChatMemoryStoreImpl implements LongTermChatMemoryStore {
    private static final String LOCK_PREFIX = "LongTermChatMemoryStoreLock-";

    @Autowired
    private RedissonClient redisson;
    @Autowired
    private ChatMemoryService chatMemoryService;
    @Autowired
    private EmbeddingStoreService<TextSegment> embeddingStoreService;
    @Autowired
    private EmbeddingModelService embeddingModelService;

    @Override
    public List<ChatMessage> getMessages(Object memoryId,
                                         UserMessage userMessage,
                                         List<ChatMessage> chatMemory,
                                         RetrievalAugmentor retriever) {
        dev.langchain4j.rag.query.Metadata metadata = dev.langchain4j.rag.query.Metadata.from(
                userMessage, memoryId, chatMemory);

        return Optional.ofNullable(retriever.augment(new AugmentationRequest(userMessage, metadata)))
                .map(AugmentationResult::contents)
                .orElse(Collections.emptyList())
                .stream()
                .map(Content::textSegment)
                .map(TextSegment::text)
                .map(ChatMessageDeserializer::messagesFromJson)
                .filter(messages -> messages.size() == 2 &&
                        messages.getFirst().type() == USER &&
                        messages.getLast().type() == AI)
                .flatMap(List::stream)
                .toList();
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessageRecord> messages) {
        RLock lock = redisson.getLock(LOCK_PREFIX + memoryId);
        boolean locked = false;

        try {
            locked = lock.tryLock(30, 60, TimeUnit.SECONDS);
            String lang = chatMemoryService.getLang(memoryId);
            EmbeddingModel embeddingModel = embeddingModelService.modelForLang(lang);
            EmbeddingStore<TextSegment> embeddingStore =
                    embeddingStoreService.of(memoryId, longTermMemoryTypeForLang(lang));

            ChatMessageRecord userRecord = null;
            for (ChatMessageRecord record: messages) {
                ChatMessage message = record.getMessage();
                if (message.type() == USER) {
                    userRecord = record;
                } else if (message.type() == AI && !((AiMessage) message).hasToolExecutionRequests()) {
                    if (userRecord == null) {
                        continue;
                    }
                    UserMessage userMessage = (UserMessage) userRecord.getMessage();
                    AiMessage aiMessage = (AiMessage) message;
                    String text = text(userMessage, aiMessage);
                    Metadata metadata = new Metadata();
                    metadata.put(USER_MESSAGE_ID.text(), userRecord.getId());
                    metadata.put(AI_MESSAGE_ID.text(), record.getId());
                    metadata.put(MEMORY_ID.text(), memoryId.toString());
                    TextSegment segment = TextSegment.from(text, metadata);

                    Embedding embedding = embed(embeddingModel, userMessage, aiMessage);
                    Filter filter = metadataKey(USER_MESSAGE_ID.text()).isEqualTo(userRecord.getId())
                            .and(metadataKey(AI_MESSAGE_ID.text()).isEqualTo(record.getId()))
                            .and(metadataKey(MEMORY_ID.text()).isEqualTo(memoryId.toString()));

                    EmbeddingSearchRequest request = EmbeddingSearchRequest.builder()
                            .queryEmbedding(embedding)
                            .filter(filter)
                            .minScore(0.8)
                            .maxResults(1)
                            .build();
                    EmbeddingSearchResult<TextSegment> result = embeddingStore.search(request);
                    if (result.matches().isEmpty()) {
                        embeddingStore.add(embedding, segment);
                    }
                    userRecord = null;
                }
            }
            embeddingStoreService.flush(memoryId, longTermMemoryTypeForLang(lang), embeddingStore);
        } catch (Exception ex) {
            log.error("Failed to update long term memory for [{}]", memoryId, ex);
        } finally {
            if (locked) {
                try {
                    lock.unlock();
                } catch (Throwable unlockEx) {
                    log.warn("Unlock failed!", unlockEx);
                }
            }
        }
    }

    @Override
    public void deleteMessages(Object memoryId) {
        if (memoryId == null) {
            return;
        }
        String lang = chatMemoryService.getLang(memoryId);
        embeddingStoreService.delete(memoryId, longTermMemoryTypeForLang(lang));
    }

    @EventListener
    public void onChatMemoryReduced(ChatMemoryReducedEvent event) {
        updateMessages(event.memoryId(), event.records());
    }

    private static String text(UserMessage userMessage, AiMessage aiMessage) {
        return ChatMessageSerializer.messagesToJson(List.of(userMessage, aiMessage));
    }

    private static Embedding embed(EmbeddingModel embeddingModel, UserMessage userMessage, AiMessage aiMessage) {
        return embeddingModel.embed(
                String.format("%s\n\n%s", toSingleText(userMessage), toSingleText(aiMessage))
        ).content();
    }
}
