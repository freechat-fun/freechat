package fun.freechat.service.chat;

import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.moderation.ModerationModel;
import dev.langchain4j.model.output.TokenUsage;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.service.AiServiceContext;
import dev.langchain4j.service.tool.ToolArgumentsErrorHandler;
import dev.langchain4j.service.tool.ToolExecutionErrorHandler;
import dev.langchain4j.service.tool.ToolExecutor;
import fun.freechat.service.enums.PromptFormat;
import fun.freechat.service.prompt.ChatPromptContent;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Getter
@Slf4j
public class ChatSession {
    private final AiServiceContext aiServiceContext;
    private final ChatPromptContent prompt;
    private final PromptFormat promptFormat;
    private final Map<String, Object> variables;
    private final RetrievalAugmentor longTermMemoryRetriever;
    private final AtomicBoolean processing = new AtomicBoolean(false);

    @Setter
    private MemoryUsage memoryUsage;

    @Builder
    public ChatSession(ChatModel chatModel,
                       StreamingChatModel streamingChatModel,
                       ModerationModel moderationModel,
                       ChatMemory chatMemory,
                       ChatPromptContent prompt,
                       PromptFormat promptFormat,
                       Map<String, Object> variables,
                       RetrievalAugmentor retriever,
                       RetrievalAugmentor longTermMemoryRetriever,
                       MemoryUsage memoryUsage,
                       List<Object> objectsWithTools) {
        aiServiceContext = AiServiceContext.create(ChatService.class);
        aiServiceContext.chatModel = chatModel;
        aiServiceContext.streamingChatModel = streamingChatModel;
        aiServiceContext.moderationModel = moderationModel;
        aiServiceContext.retrievalAugmentor = retriever;
        aiServiceContext.initChatMemories(memoryId -> chatMemory);

        this.prompt = prompt;
        this.promptFormat = promptFormat;
        this.variables = variables;
        this.longTermMemoryRetriever = longTermMemoryRetriever;
        this.memoryUsage = memoryUsage;

        if (CollectionUtils.isNotEmpty(objectsWithTools)) {
            this.tools(objectsWithTools);
        }
    }

    @SuppressWarnings("unused")
    public void tools(List<Object> objectsWithTools) {
        aiServiceContext.toolService.tools(objectsWithTools);
    }

    public ChatModel getChatModel() {
        return aiServiceContext.chatModel;
    }

    public StreamingChatModel getStreamingChatModel() {
        return aiServiceContext.streamingChatModel;
    }

    public ModerationModel getModerationModel() {
        return aiServiceContext.moderationModel;
    }

    public ChatMemory getChatMemory(Object memoryId) {
        return aiServiceContext.chatMemoryService.getOrCreateChatMemory(memoryId);
    }

    public List<ToolSpecification> getToolSpecifications() {
        return aiServiceContext.toolService.toolSpecifications();
    }

    public Map<String, ToolExecutor> getToolExecutors() {
        return aiServiceContext.toolService.toolExecutors();
    }

    public ToolArgumentsErrorHandler getToolArgumentsErrorHandler() {
        return aiServiceContext.toolService.argumentsErrorHandler();
    }

    public ToolExecutionErrorHandler getToolExecutionErrorHandler() {
        return aiServiceContext.toolService.executionErrorHandler();
    }

    public RetrievalAugmentor getRetriever() {
        return aiServiceContext.retrievalAugmentor;
    }

    public void addMemoryUsage(Long messageUsage, TokenUsage tokenUsage) {
        if (memoryUsage == null) {
            memoryUsage = new MemoryUsage(messageUsage, tokenUsage);
        } else {
            memoryUsage = memoryUsage.add(messageUsage, tokenUsage);
        }
    }

    public boolean acquire() {
        return !processing.compareAndExchangeAcquire(false, true);
    }

    public void release() {
        processing.compareAndExchangeRelease(true, false);
    }

    public boolean isProcessing() {
        return processing.get();
    }
}
