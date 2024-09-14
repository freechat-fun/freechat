package fun.freechat.service.chat;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.moderation.ModerationModel;
import dev.langchain4j.model.output.TokenUsage;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.service.AiServiceContext;
import dev.langchain4j.service.tool.DefaultToolExecutor;
import dev.langchain4j.service.tool.ToolExecutor;
import fun.freechat.service.enums.PromptFormat;
import fun.freechat.service.prompt.ChatPromptContent;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import static dev.langchain4j.agent.tool.ToolSpecifications.toolSpecificationFrom;
import static dev.langchain4j.exception.IllegalConfigurationException.illegalConfiguration;

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
    public ChatSession(ChatLanguageModel chatModel,
                       StreamingChatLanguageModel streamingChatModel,
                       ModerationModel moderationModel,
                       ChatMemory chatMemory,
                       ChatPromptContent prompt,
                       PromptFormat promptFormat,
                       Map<String, Object> variables,
                       RetrievalAugmentor retriever,
                       RetrievalAugmentor longTermMemoryRetriever,
                       MemoryUsage memoryUsage) {
        aiServiceContext = new AiServiceContext(null);
        aiServiceContext.chatModel = chatModel;
        aiServiceContext.streamingChatModel = streamingChatModel;
        aiServiceContext.moderationModel = moderationModel;
        aiServiceContext.chatMemories = new ConcurrentHashMap<>();
        aiServiceContext.chatMemoryProvider = memoryId -> chatMemory;
        aiServiceContext.retrievalAugmentor = retriever;

        this.prompt = prompt;
        this.promptFormat = promptFormat;
        this.variables = variables;
        this.longTermMemoryRetriever = longTermMemoryRetriever;
        this.memoryUsage = memoryUsage;
    }

    @SuppressWarnings("unused")
    public ChatSession tools(List<Object> objectsWithTools) {
        if (aiServiceContext.toolSpecifications == null) {
            aiServiceContext.toolSpecifications = new ArrayList<>();
        }
        if (aiServiceContext.toolExecutors == null) {
            aiServiceContext.toolExecutors = new HashMap<>();
        }

        for (Object objectWithTool : objectsWithTools) {
            if (objectWithTool instanceof Class) {
                throw illegalConfiguration("Tool '%s' must be an object, not a class", objectWithTool);
            }

            for (Method method : objectWithTool.getClass().getDeclaredMethods()) {
                if (method.isAnnotationPresent(Tool.class)) {
                    ToolSpecification toolSpecification = toolSpecificationFrom(method);
                    aiServiceContext.toolSpecifications.add(toolSpecification);
                    aiServiceContext.toolExecutors.put(toolSpecification.name(), new DefaultToolExecutor(objectWithTool, method));
                }
            }
        }

        return this;
    }

    public ChatLanguageModel getChatModel() {
        return aiServiceContext.chatModel;
    }

    public StreamingChatLanguageModel getStreamingChatModel() {
        return aiServiceContext.streamingChatModel;
    }

    public ModerationModel getModerationModel() {
        return aiServiceContext.moderationModel;
    }

    public ChatMemory getChatMemory(Object memoryId) {
        return aiServiceContext.chatMemory(memoryId);
    }

    public List<ToolSpecification> getToolSpecifications() {
        return aiServiceContext.toolSpecifications;
    }

    public Map<String, ToolExecutor> getToolExecutors() {
        return aiServiceContext.toolExecutors;
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
