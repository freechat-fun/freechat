package fun.freechat.service.chat;

import dev.langchain4j.agent.tool.DefaultToolExecutor;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolExecutor;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.moderation.ModerationModel;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.service.AiServiceContext;
import fun.freechat.service.enums.PromptFormat;
import fun.freechat.service.prompt.ChatPromptContent;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import static dev.langchain4j.agent.tool.ToolSpecifications.toolSpecificationFrom;

@Getter
@Slf4j
public class ChatSession {
    private final AiServiceContext aiServiceContext;
    private final ChatPromptContent prompt;
    private final PromptFormat promptFormat;
    private final Map<String, Object> variables;
    private final AtomicBoolean processing = new AtomicBoolean(false);

    @Builder
    public ChatSession(ChatLanguageModel chatModel,
                       StreamingChatLanguageModel streamingChatModel,
                       ModerationModel moderationModel,
                       ChatMemory chatMemory,
                       ChatPromptContent prompt,
                       PromptFormat promptFormat,
                       Map<String, Object> variables) {
        aiServiceContext = new AiServiceContext(null);
        aiServiceContext.chatModel = chatModel;
        aiServiceContext.streamingChatModel = streamingChatModel;
        aiServiceContext.moderationModel = moderationModel;
        aiServiceContext.chatMemories = new ConcurrentHashMap<>();
        aiServiceContext.chatMemoryProvider = memoryId -> chatMemory;

        this.prompt = prompt;
        this.promptFormat = promptFormat;
        this.variables = variables;
    }

    @SuppressWarnings("unused")
    public ChatSession tools(List<Object> objectsWithTools) {
        aiServiceContext.toolSpecifications = new ArrayList<>();
        aiServiceContext.toolExecutors = new HashMap<>();

        for (Object objectWithTool : objectsWithTools) {
            for (Method method : objectWithTool.getClass().getDeclaredMethods()) {
                if (method.isAnnotationPresent(Tool.class)) {
                    ToolSpecification toolSpecification = toolSpecificationFrom(method);
                    aiServiceContext.toolSpecifications.add(toolSpecification);
                    aiServiceContext.toolExecutors.put(
                            toolSpecification.name(), new DefaultToolExecutor(objectWithTool, method));
                }
            }
        }

        return this;
    }

    @SuppressWarnings("unused")
    public ChatSession retrievalAugmentor(RetrievalAugmentor retriever) {
        aiServiceContext.retrievalAugmentor = retriever;
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

    public RetrievalAugmentor getRetrieverAugmentor() {
        return aiServiceContext.retrievalAugmentor;
    }

    public boolean acquire() {
        return !processing.compareAndExchangeAcquire(false, true);
    }

    public void release() {
        processing.compareAndExchangeRelease(true, false);
    }
}