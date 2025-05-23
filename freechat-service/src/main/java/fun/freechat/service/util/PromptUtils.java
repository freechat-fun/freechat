package fun.freechat.service.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import dev.langchain4j.data.message.*;
import fun.freechat.service.enums.ChatVar;
import fun.freechat.service.enums.PromptType;
import fun.freechat.service.prompt.ChatPromptContent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static fun.freechat.service.util.InfoUtils.defaultMapper;

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
            case CUSTOM -> {
                try {
                    yield defaultMapper().writeValueAsString(((CustomMessage) message).attributes());
                } catch (JsonProcessingException e) {
                    log.warn("Failed to serialize {}", ((CustomMessage) message).attributes(), e);
                    yield  "";
                }
            }
        };
    }

    public static String prefixFrom(ChatMessage message) {
        if (message instanceof AiMessage) {
            return ASSISTANT_PREFIX;
        } else if (message instanceof SystemMessage) {
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
            JsonNode rootNode = defaultMapper().readTree(draft);
            JsonNode typeNode = rootNode.get("type");
            if (typeNode != null) {
                PromptType type = PromptType.of(typeNode.asText());
                if (type == PromptType.STRING) {
                    JsonNode templateNode = rootNode.get("template");
                    if (templateNode != null && templateNode.isTextual()) {
                        template = templateNode.asText();
                    }
                } else if (type == PromptType.CHAT) {
                    JsonNode templateNode = rootNode.get("chatTemplate");
                    if (templateNode != null && templateNode.isObject()) {
                        template = templateNode.toString();
                    }
                }
            }
        } catch (JsonProcessingException e) {
            log.warn("Failed to parse draft: {}", draft);
        }
        return template;
    }

    public static Pair<String, String> getDefaultInput(Map<String, Object> variables) {
        String input = MapUtils.getString(variables, ChatVar.INPUT.text().toLowerCase());
        String dataUrl = MapUtils.getString(variables, ChatVar.ATTACHMENT.text().toLowerCase());
        return Pair.of(input, dataUrl);
    }

    private static final Pattern NORMAL_URL_PATTERN = Pattern.compile("[a-zA-Z]+://.+");
    public static Pair<String, String> parseDataMimeType(String data) {
        if (StringUtils.isBlank(data) || NORMAL_URL_PATTERN.matcher(data).matches()) {
            return Pair.of(data, null);
        }

        String mimeTypePrefix = "data:";
        String base64Prefix = ";base64,";

        if (!data.startsWith(mimeTypePrefix) || !data.contains(base64Prefix)) {
            return Pair.of(data, StoreUtils.guessMimeTypeOfBase64Data(data));
        }

        // data:image/png;base64,iVBO...
        int base64Index = data.indexOf(base64Prefix) + base64Prefix.length();
        String mimeType = data.substring(mimeTypePrefix.length(), base64Index - base64Prefix.length());
        String base64Data = data.substring(base64Index);

        return Pair.of(base64Data, mimeType);
    }

    public static List<ChatMessage> toMessages(ChatPromptContent promptTemplate) {
        List<ChatMessage> messages = new LinkedList<>();
        if (StringUtils.isNotBlank(promptTemplate.getSystem())) {
            messages.add(SystemMessage.from(promptTemplate.getSystem()));
        }
        if (CollectionUtils.isNotEmpty(promptTemplate.getMessages())) {
            messages.addAll(promptTemplate.getMessages());
        }
        if (promptTemplate.getMessageToSend() != null) {
            messages.add(promptTemplate.getMessageToSend());
        }
        return messages;
    }
}
