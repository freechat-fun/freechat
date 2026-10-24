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
import fun.freechat.service.tools.ToolBinder;
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
    private final ToolBinder toolBinder;
    private final fun.freechat.service.skills.SkillBinder skillBinder;
    private final fun.freechat.service.skills.SkillInvocation skillInvocation;
    private final fun.freechat.service.connectors.ConnectorBinder connectorBinder;
    private final fun.freechat.service.connectors.ConnectorInvocation connectorInvocation;

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
            Map<ToolSpecification, ToolExecutor> tools,
            ToolBinder toolBinder,
            fun.freechat.service.skills.SkillBinder skillBinder,
            fun.freechat.service.skills.SkillInvocation skillInvocation,
            fun.freechat.service.connectors.ConnectorBinder connectorBinder,
            fun.freechat.service.connectors.ConnectorInvocation connectorInvocation) {
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
        this.toolBinder = toolBinder;
        this.skillBinder = skillBinder;
        this.skillInvocation = skillInvocation;
        this.connectorBinder = connectorBinder;
        this.connectorInvocation = connectorInvocation;

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
        var skills = skillBinder == null ? null : skillBinder.bind();
        if (skills != null && memory instanceof fun.freechat.service.chat.memory.MemoryInvocation preparedMemory) {
            preparedMemory.requestOverlay(skills::messages, skills::validateRequest);
        }
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
        if (skills != null) {
            all.putAll(skills.executors());
        }
        if (toolBinder != null) {
            all = new java.util.LinkedHashMap<>(toolBinder.bind(all));
        }
        if (skills != null) {
            all = new java.util.LinkedHashMap<>(skills.restrict(all));
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
        var connectors = skills != null || connectorBinder == null ? null : connectorBinder.bind();
        try {
            if (connectors != null) {
                for (var entry : connectors.tools().entrySet()) {
                    var executor = entry.getValue();
                    putTool(all, entry.getKey(), (request, memoryId) -> {
                        guard.run();
                        String result = executor.execute(request, memoryId);
                        guard.run();
                        return result;
                    });
                }
            }
            ChatSession invocation = ChatSession.builder()
                    .skillInvocation(skills)
                    .connectorInvocation(connectors)
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
                    .tools(all)
                    .build();
            invocation.aiServiceContext.toolService.maxToolCallingRoundTrips(maxToolRounds);
            return invocation;
        } catch (RuntimeException | Error failure) {
            if (connectors != null) connectors.close();
            throw failure;
        }
    }

    public record InvocationTools(
            Map<ToolSpecification, ToolExecutor> tools,
            fun.freechat.service.skills.SkillInvocation skills,
            fun.freechat.service.connectors.ConnectorInvocation connectors)
            implements AutoCloseable {
        @Override
        public void close() {
            if (connectors != null) connectors.close();
        }
    }

    public InvocationTools bindInvocation() {
        var skills = skillInvocation != null ? skillInvocation : skillBinder == null ? null : skillBinder.bind();
        Map<ToolSpecification, ToolExecutor> candidates = new java.util.LinkedHashMap<>();
        if (getToolSpecifications() != null) {
            for (ToolSpecification specification : getToolSpecifications()) {
                candidates.put(specification, getToolExecutors().get(specification.name()));
            }
        }
        if (skills != null && skillInvocation == null) {
            candidates.putAll(skills.executors());
        }
        Map<ToolSpecification, ToolExecutor> bound = toolBinder == null ? candidates : toolBinder.bind(candidates);
        if (skills != null && skillInvocation == null) {
            bound = skills.restrict(bound);
        }
        var connectors = connectorInvocation != null
                ? connectorInvocation
                : skills != null || connectorBinder == null ? null : connectorBinder.bind();
        try {
            if (connectors != null) {
                connectors.check();
                if (connectorInvocation == null) {
                    bound = new java.util.LinkedHashMap<>(bound);
                    for (var entry : connectors.tools().entrySet()) putTool(bound, entry.getKey(), entry.getValue());
                }
            }
            return new InvocationTools(java.util.Collections.unmodifiableMap(bound), skills, connectors);
        } catch (RuntimeException | Error failure) {
            if (connectors != null) connectors.close();
            throw failure;
        }
    }

    private static void putTool(
            Map<ToolSpecification, ToolExecutor> tools, ToolSpecification specification, ToolExecutor executor) {
        if (tools.keySet().stream().anyMatch(existing -> existing.name().equals(specification.name()))) {
            throw new IllegalStateException("Duplicate chat tool name");
        }
        tools.put(specification, executor);
    }

    public void closeConnectors() {
        if (connectorInvocation != null) connectorInvocation.close();
    }

    public Map<ToolSpecification, ToolExecutor> bindToolsForInvocation() {
        return bindInvocation().tools();
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
