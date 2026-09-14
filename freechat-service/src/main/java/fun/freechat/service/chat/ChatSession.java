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
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

@Getter
@Slf4j
public class ChatSession {
    private final AiServiceContext aiServiceContext;
    private final ChatModel imageChatModel;
    private final ChatPromptContent prompt;
    private final PromptFormat promptFormat;
    private final Map<String, Object> variables;
    private final String memoryFingerprint;

    @Setter
    private MemoryUsage memoryUsage;

    @Builder
    public ChatSession(
            ChatModel chatModel,
            StreamingChatModel streamingChatModel,
            ChatModel imageChatModel,
            ModerationModel moderationModel,
            ChatMemory chatMemory,
            ChatPromptContent prompt,
            PromptFormat promptFormat,
            Map<String, Object> variables,
            String memoryFingerprint,
            RetrievalAugmentor retriever,
            MemoryUsage memoryUsage,
            List<Object> objectsWithTools,
            Map<ToolSpecification, ToolExecutor> tools) {
        aiServiceContext = AiServiceContext.create(ChatService.class);
        aiServiceContext.chatModel = chatModel;
        aiServiceContext.streamingChatModel = streamingChatModel;
        aiServiceContext.moderationModel = moderationModel;
        aiServiceContext.retrievalAugmentor = retriever;
        aiServiceContext.initChatMemories(memoryId -> chatMemory);
        aiServiceContext.toolService.maxToolCallingRoundTrips(10);
        aiServiceContext.toolService.argumentsErrorHandler((error, context) -> {
            throw new IllegalStateException("Invalid chat tool arguments");
        });
        aiServiceContext.toolService.executionErrorHandler((error, context) -> {
            throw new IllegalStateException("Chat tool execution failed");
        });

        this.imageChatModel = imageChatModel;
        this.prompt = prompt;
        this.promptFormat = promptFormat;
        this.variables = variables;
        this.memoryUsage = memoryUsage;
        this.memoryFingerprint = memoryFingerprint;

        if (CollectionUtils.isNotEmpty(objectsWithTools)) {
            this.tools(objectsWithTools);
        }
        if (MapUtils.isNotEmpty(tools)) {
            this.tools(tools);
        }
    }

    public ChatSession forInvocation(
            ChatMemory memory,
            StreamingChatModel streaming,
            List<Object> recallTools,
            int maxToolRounds,
            Runnable guard) {
        ChatSession invocation = ChatSession.builder()
                .chatModel(getChatModel())
                .streamingChatModel(streaming)
                .imageChatModel(imageChatModel)
                .moderationModel(getModerationModel())
                .chatMemory(memory)
                .prompt(prompt)
                .promptFormat(promptFormat)
                .variables(new java.util.HashMap<>(variables))
                .memoryFingerprint(memoryFingerprint)
                .retriever(getRetriever())
                .memoryUsage(memoryUsage)
                .build();
        Map<ToolSpecification, ToolExecutor> all = new java.util.LinkedHashMap<>();
        if (getToolSpecifications() != null) {
            for (ToolSpecification specification : getToolSpecifications()) {
                all.put(specification, getToolExecutors().get(specification.name()));
            }
        }
        for (Object recall : recallTools) {
            for (var tool : dev.langchain4j.service.tool.ToolService.findTools(recall)) {
                if (all.keySet().stream()
                        .anyMatch(specification -> specification.name().equals(tool.name()))) {
                    throw new IllegalStateException("Duplicate chat tool name");
                }
                all.put(tool.toolSpecification(), tool.toolExecutor());
            }
        }
        all.replaceAll((specification, executor) -> (request, memoryId) -> {
            guard.run();
            try {
                String result = executor.execute(request, memoryId);
                guard.run();
                return result;
            } catch (RuntimeException ignored) {
                throw new IllegalStateException("Chat tool execution failed");
            }
        });
        invocation.tools(all);
        invocation.aiServiceContext.toolService.maxToolCallingRoundTrips(maxToolRounds);
        return invocation;
    }

    @SuppressWarnings("unused")
    public void tools(List<Object> objectsWithTools) {
        aiServiceContext.toolService.tools(objectsWithTools);
    }

    public void tools(Map<ToolSpecification, ToolExecutor> tools) {
        aiServiceContext.toolService.tools(tools);
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

    public int getMaxToolRounds() {
        return aiServiceContext.toolService.maxToolCallingRoundTrips();
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
}
