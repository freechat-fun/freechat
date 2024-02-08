package fun.freechat.service.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import dev.langchain4j.data.message.*;
import fun.freechat.service.ai.message.ChatPromptContent;
import fun.freechat.service.enums.ChatVar;
import fun.freechat.service.enums.PromptType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
    public static String toPrompt(List<ChatMessage> messages) {
        return messages.stream()
                .map(PromptUtils::toSingleBlock)
                .collect(Collectors.joining("\n\n"));
    }

    public static String toSingleBlock(ChatMessage message) {
        return prefixFrom(message) + toSingleText(message);
    }

    public static String toSingleText(ChatMessage message) {
        return switch (message.type()) {
            case USER -> ((UserMessage) message).contents()
                    .stream()
                    .filter(TextContent.class::isInstance)
                    .map(TextContent.class::cast)
                    .map(TextContent::text)
                    .collect(Collectors.joining("\n"));
            case AI -> ((AiMessage) message).text();
            case SYSTEM -> ((SystemMessage) message).text();
            case TOOL_EXECUTION_RESULT -> ((ToolExecutionResultMessage) message).text();
        };
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

    public static String getDefaultInput(Map<String, Object> variables) {
        return MapUtils.getString(variables, ChatVar.INPUT.text().toLowerCase());
    }

    public static List<ChatMessage> toMessages(ChatPromptContent promptTemplate) {
        List<ChatMessage> messages = new LinkedList<>();
        if (StringUtils.isNotBlank(promptTemplate.getSystem())) {
            messages.add(SystemMessage.from(promptTemplate.getSystem()));
        }
        if (CollectionUtils.isNotEmpty(promptTemplate.getMessages())) {
            messages.addAll(promptTemplate.getMessages());
        }
        if (Objects.nonNull(promptTemplate.getMessageToSend())) {
            messages.add(promptTemplate.getMessageToSend());
        }
        return messages;
    }
}
