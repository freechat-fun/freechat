package fun.freechat.service.character;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolExecutor;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.moderation.ModerationModel;
import dev.langchain4j.retriever.Retriever;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import fun.freechat.service.ai.message.ChatPromptContent;
import lombok.Builder;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.langchain4j.agent.tool.ToolSpecifications.toolSpecificationFrom;

@Data
@Builder
public class ChatSession {
    private ChatLanguageModel chatModel;
    private StreamingChatLanguageModel streamingChatModel;
    private ModerationModel moderationModel;
    private ChatMemoryStore chatMemoryStore;
    private ChatMemory chatMemory;
    private List<ToolSpecification> toolSpecifications;
    private Map<String, ToolExecutor> toolExecutors;
    private Retriever<TextSegment> retriever;
    private ChatPromptContent prompt;
    private Map<String, Object> variables;

    @SuppressWarnings("unused")
    public ChatSession tools(List<Object> objectsWithTools) {
        toolSpecifications = new ArrayList<>();
        toolExecutors = new HashMap<>();

        for (Object objectWithTool : objectsWithTools) {
            for (Method method : objectWithTool.getClass().getDeclaredMethods()) {
                if (method.isAnnotationPresent(Tool.class)) {
                    ToolSpecification toolSpecification = toolSpecificationFrom(method);
                    toolSpecifications.add(toolSpecification);
                    toolExecutors.put(toolSpecification.name(), new ToolExecutor(objectWithTool, method));
                }
            }
        }

        return this;
    }

    @SuppressWarnings("unused")
    public ChatSession retriever(Retriever<TextSegment> retriever) {
        this.retriever = retriever;
        return this;
    }
}
