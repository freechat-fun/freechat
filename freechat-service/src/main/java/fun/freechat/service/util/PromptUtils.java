package fun.freechat.service.util;

import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.output.Response;
import fun.freechat.service.ai.message.ChatMessage;
import fun.freechat.service.ai.message.ChatPromptContent;
import fun.freechat.service.ai.message.ChatToolCall;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static fun.freechat.service.enums.PromptRole.*;

public class PromptUtils {
    private static final String SYSTEM_PREFIX = "<|system|>:";
    private static final String ASSISTANT_PREFIX = "<|assistant|>:";
    private static final String USER_PREFIX = "<|user|>:";

    /* Chat prompt for pure language models:
     * <|system|>: ...
     *
     * <|user|>: ...
     *
     * <|assistant|>: ...
     *
     * <|user|>: ...
     * ...
     */
    public static String toPrompt(List<dev.langchain4j.data.message.ChatMessage> messages) {
        return messages.stream()
                .map(PromptUtils::toMessage)
                .collect(Collectors.joining("\n\n"));
    }

    public static String toMessage(dev.langchain4j.data.message.ChatMessage message) {
        return prefixFrom(message) + message.text();
    }

    public static String prefixFrom(dev.langchain4j.data.message.ChatMessage message) {
        if (message instanceof dev.langchain4j.data.message.AiMessage) {
            return ASSISTANT_PREFIX;
        } else if (message instanceof dev.langchain4j.data.message.SystemMessage) {
            return SYSTEM_PREFIX;
        } else {
            return USER_PREFIX;
        }
    }

    public static dev.langchain4j.data.message.ChatMessage convertChatMessage(ChatMessage message) {
        return switch (message.getRole()) {
            case SYSTEM -> dev.langchain4j.data.message.SystemMessage.from(message.getContent());
            case ASSISTANT -> dev.langchain4j.data.message.AiMessage.from(message.getContent());
            case USER -> dev.langchain4j.data.message.UserMessage.from(message.getName(), message.getContent());
            case FUNCTION_CALL -> {
                List<ChatToolCall> toolCalls = message.getToolCalls();
                if (CollectionUtils.isNotEmpty(toolCalls)) {
                    yield dev.langchain4j.data.message.AiMessage.from(toolCalls.stream()
                            .map(toolCall ->
                                    ToolExecutionRequest.builder()
                                    .id(toolCall.getId())
                                    .name(toolCall.getName())
                                    .arguments(toolCall.getArguments()).build())
                            .toList());
                } else {
                    yield null;
                }
            }
            case FUNCTION_RESULT -> {
                String id = null;
                String name = message.getName();
                String result = message.getContent();
                List<ChatToolCall> toolCalls = message.getToolCalls();
                if (CollectionUtils.isNotEmpty(toolCalls)) {
                    ChatToolCall toolCall = toolCalls.getFirst();
                    id = toolCall.getId();
                    name = toolCall.getName();
                }
                yield dev.langchain4j.data.message.ToolExecutionResultMessage.from(id, name, result);
            }
        };
    }

    public static ChatMessage convertL4jMessage(
            dev.langchain4j.data.message.ChatMessage l4jMessage) {
        ChatMessage message = new ChatMessage();
        message.setGmtCreate(new Date());
        message.setContent(l4jMessage.text());
        switch (l4jMessage.type()) {
            case SYSTEM -> message.setRole(SYSTEM);
            case AI -> {
                List<ToolExecutionRequest> requests = ((AiMessage) l4jMessage).toolExecutionRequests();
                if (Objects.isNull(requests)) {
                    message.setRole(ASSISTANT);
                } else {
                    message.setRole(FUNCTION_CALL);
                    message.setToolCalls(requests.stream()
                            .map(request -> {
                                ChatToolCall toolCall = new ChatToolCall();
                                toolCall.setId(request.id());
                                toolCall.setName(request.name());
                                toolCall.setArguments(request.arguments());
                                return toolCall;
                            })
                            .toList()
                    );
                }
            }
            case USER -> {
                message.setRole(USER);
                message.setName(((UserMessage) l4jMessage).name());
            }
            case TOOL_EXECUTION_RESULT -> {
                ToolExecutionResultMessage resultMessage = (ToolExecutionResultMessage) l4jMessage;
                ChatToolCall toolCall = new ChatToolCall();
                toolCall.setId(resultMessage.id());
                toolCall.setName(resultMessage.toolName());
                message.setRole(FUNCTION_RESULT);
                message.setName((resultMessage).toolName());
                message.setContent(resultMessage.text());
                message.setToolCalls(Collections.singletonList(toolCall));
            }
        }
        return message;
    }

    public static Response<ChatMessage> convertL4jMessageResponse(
            Response<? extends dev.langchain4j.data.message.ChatMessage> response) {
        return Response.from(convertL4jMessage(response.content()), response.tokenUsage(), response.finishReason());
    }

    public static List<dev.langchain4j.data.message.ChatMessage> toL4jMessages(ChatPromptContent promptTemplate) {
        List<dev.langchain4j.data.message.ChatMessage> messages = new LinkedList<>();
        if (StringUtils.isNotBlank(promptTemplate.getSystem())) {
            messages.add(dev.langchain4j.data.message.SystemMessage.from(promptTemplate.getSystem()));
        }
        if (CollectionUtils.isNotEmpty(promptTemplate.getMessages())) {
            for (var message : promptTemplate.getMessages()) {
                dev.langchain4j.data.message.ChatMessage l4jMessage =
                        convertChatMessage(message);
                if (Objects.nonNull(l4jMessage)) {
                    messages.add(l4jMessage);
                }
            }
        }
        if (Objects.nonNull(promptTemplate.getMessageToSend())) {
            messages.add(convertChatMessage(promptTemplate.getMessageToSend()));
        }
        return messages;
    }
}
