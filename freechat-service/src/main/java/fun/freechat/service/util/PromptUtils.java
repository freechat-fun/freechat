package fun.freechat.service.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.data.image.Image;
import dev.langchain4j.data.message.*;
import dev.langchain4j.model.output.Response;
import fun.freechat.service.ai.message.ChatContent;
import fun.freechat.service.ai.message.ChatMessage;
import fun.freechat.service.ai.message.ChatPromptContent;
import fun.freechat.service.ai.message.ChatToolCall;
import fun.freechat.service.enums.PromptType;
import fun.freechat.util.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static fun.freechat.service.enums.ChatContentType.IMAGE;
import static fun.freechat.service.enums.PromptRole.*;

@Slf4j
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
            case SYSTEM -> dev.langchain4j.data.message.SystemMessage.from(message.getContentText());
            case ASSISTANT -> dev.langchain4j.data.message.AiMessage.from(message.getContentText());
            case USER -> {
                List<Content> contents = message.getContents().stream()
                        .map(PromptUtils::convertChatContent)
                        .toList();
                yield StringUtils.isBlank(message.getName()) ?
                        UserMessage.from(contents) :
                        UserMessage.from(message.getName(), contents);

            }
            case FUNCTION_CALL -> AiMessage.from( message.getToolCalls().stream()
                    .map(toolCall ->
                            ToolExecutionRequest.builder()
                            .id(toolCall.getId())
                            .name(toolCall.getName())
                            .arguments(toolCall.getArguments()).build())
                    .toList());
            case FUNCTION_RESULT -> {
                String id = null;
                String name = message.getName();
                String result = message.getContentText();
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
        switch (l4jMessage.type()) {
            case SYSTEM -> {
                message.setRole(SYSTEM);
                message.setContentText(((SystemMessage) l4jMessage).text());
            }
            case AI -> {
                AiMessage aiMessage = (AiMessage) l4jMessage;
                message.setContentText(aiMessage.text());
                List<ToolExecutionRequest> requests = aiMessage.toolExecutionRequests();
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
                UserMessage userMessage = (UserMessage) l4jMessage;
                message.setRole(USER);
                message.setName(userMessage.name());
                message.setContents(userMessage.contents().stream()
                        .map(PromptUtils::convertL4jContent)
                        .toList());
            }
            case TOOL_EXECUTION_RESULT -> {
                ToolExecutionResultMessage resultMessage = (ToolExecutionResultMessage) l4jMessage;
                ChatToolCall toolCall = new ChatToolCall();
                toolCall.setId(resultMessage.id());
                toolCall.setName(resultMessage.toolName());
                message.setRole(FUNCTION_RESULT);
                message.setName((resultMessage).toolName());
                message.setContentText(resultMessage.text());
                message.setToolCalls(Collections.singletonList(toolCall));
            }
        }
        return message;
    }

    public static Response<ChatMessage> convertL4jMessageResponse(
            Response<? extends dev.langchain4j.data.message.ChatMessage> response) {
        return Response.from(convertL4jMessage(response.content()), response.tokenUsage(), response.finishReason());
    }

    public static dev.langchain4j.data.message.Content convertChatContent(ChatContent content) {
        if (content.getType() == IMAGE) {
            String imageInfo = content.getContent();
            if (HttpUtils.isValidUrl(imageInfo)) {
                return ImageContent.from(imageInfo);
            } else {
                return ImageContent.from(imageInfo, content.getMimeType());
            }
        } else {
            return TextContent.from(content.getContent());
        }
    }

    public static ChatContent convertL4jContent(dev.langchain4j.data.message.Content l4jContent) {
        if (l4jContent.type() == ContentType.IMAGE) {
            Image l4jImage = ((ImageContent) l4jContent).image();
            if (Objects.nonNull(l4jImage.url())) {
                return ChatContent.fromImage(l4jImage.url().toString(), l4jImage.mimeType());
            } else {
                return ChatContent.fromImage(l4jImage.base64Data(), l4jImage.mimeType());
            }
        } else {
            return ChatContent.fromText(((TextContent) l4jContent).text());
        }
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

    public static String getDraftTemplate(String draft) {
        if (StringUtils.isBlank(draft)) {
            return null;
        }
        String template = null;
        try {
            JsonNode rootNode = InfoUtils.defaultMapper().readTree(draft);
            JsonNode typeNode = rootNode.get("type");
            if (Objects.nonNull(typeNode)) {
                PromptType type = PromptType.of(typeNode.asText());
                if (type == PromptType.STRING) {
                    JsonNode templateNode = rootNode.get("template");
                    if (Objects.nonNull(templateNode) && templateNode.isTextual()) {
                        template = templateNode.asText();
                    }
                } else if (type == PromptType.CHAT) {
                    JsonNode templateNode = rootNode.get("chatTemplate");
                    if (Objects.nonNull(templateNode) && templateNode.isObject()) {
                        template = templateNode.toString();
                    }
                }
            }
        } catch (JsonProcessingException e) {
            log.warn("Failed to parse draft: {}", draft);
        }
        return template;
    }
}
