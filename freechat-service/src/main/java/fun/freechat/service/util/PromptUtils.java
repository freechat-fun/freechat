package fun.freechat.service.util;

import dev.langchain4j.agent.tool.ToolExecutionRequest;
import fun.freechat.service.ai.message.ChatFunctionCall;
import fun.freechat.service.ai.message.ChatMessage;
import fun.freechat.service.ai.message.ChatPromptContent;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
                ChatFunctionCall functionCall = message.getFunctionCall();
                if (Objects.nonNull(functionCall) && StringUtils.isNotBlank(functionCall.getName())) {
                    yield dev.langchain4j.data.message.AiMessage.from(
                            ToolExecutionRequest.builder()
                                    .name(functionCall.getName())
                                    .arguments(functionCall.getArguments())
                                    .build());
                } else {
                    yield null;
                }
            }
            case FUNCTION_RESULT -> dev.langchain4j.data.message.ToolExecutionResultMessage.from(
                    message.getName(), message.getContent());
        };
    }

    public static List<dev.langchain4j.data.message.ChatMessage> toLangChain4jMessages(ChatPromptContent promptTemplate) {
        List<dev.langchain4j.data.message.ChatMessage> messages = new LinkedList<>();
        if (StringUtils.isNotBlank(promptTemplate.getSystem())) {
            messages.add(dev.langchain4j.data.message.SystemMessage.from(promptTemplate.getSystem()));
        }
        if (CollectionUtils.isNotEmpty(promptTemplate.getMessages())) {
            for (var message : promptTemplate.getMessages()) {
                dev.langchain4j.data.message.ChatMessage langchain4jChatMessage =
                        convertChatMessage(message);
                if (Objects.nonNull(langchain4jChatMessage)) {
                    messages.add(langchain4jChatMessage);
                }
            }
        }
        if (Objects.nonNull(promptTemplate.getMessageToBeSent())) {
            messages.add(convertChatMessage(promptTemplate.getMessageToBeSent()));
        }
        return messages;
    }
}
