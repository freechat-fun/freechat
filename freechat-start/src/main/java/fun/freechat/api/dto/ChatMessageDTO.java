package fun.freechat.api.dto;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.Content;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.internal.ValidationUtils;
import dev.langchain4j.model.chat.response.PartialThinking;
import fun.freechat.service.enums.PromptRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

import static dev.langchain4j.data.message.ContentType.TEXT;
import static fun.freechat.api.util.ChatUtils.contentTypeOf;
import static fun.freechat.service.enums.PromptRole.ASSISTANT;
import static fun.freechat.service.enums.PromptRole.FUNCTION_CALL;
import static fun.freechat.service.enums.PromptRole.FUNCTION_RESULT;
import static fun.freechat.service.enums.PromptRole.SYSTEM;
import static fun.freechat.service.enums.PromptRole.USER;

@Schema(description = "Chat message")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageDTO {
    @Schema(description = "Chat role: system | assistant | user | tool_call | tool_result")
    @Pattern(regexp = "system|assistant|user|tool_call|tool_result")
    private String role;
    @Schema(description = "user: name of the user role; tool_call: name of the called tool")
    private String name;
    @Schema(description = "default: dialogue content; tool_result: tool call result, serialized as json")
    private List<ChatContentDTO> contents;
    @Schema(description = "Tool calls information during the conversation")
    private List<ChatToolCallDTO> toolCalls;
    @Schema(description = "Thinking information")
    private String thinking;
    @Schema(description = "Contextual information in this round of conversation (the external RAG result can be passed in through this parameter)")
    private String context;
    @Schema(description = "Message identifier")
    private Long messageId;

    private String getContentText() {
        return ValidationUtils.ensureNotEmpty(contents, "contents")
                .stream()
                .filter(content -> contentTypeOf(content.getType()) == TEXT)
                .map(ChatContentDTO::getContent)
                .collect(Collectors.joining("\n"));
    }

    public static ChatMessageDTO from(ChatMessage message, Long messageId) {
        var builder = ChatMessageDTO.builder().messageId(messageId);
        switch (message.type()) {
            case SYSTEM -> {
                builder.role(SYSTEM.text());
                builder.contents(List.of(ChatContentDTO.from(TEXT, ((SystemMessage) message).text())));
            }
            case AI -> {
                AiMessage aiMessage = (AiMessage) message;
                if (StringUtils.isNotBlank(aiMessage.text())) {
                    builder.contents(List.of(ChatContentDTO.from(TEXT, aiMessage.text())));
                }

                builder.thinking(aiMessage.thinking());

                if (!aiMessage.hasToolExecutionRequests()) {
                    builder.role(ASSISTANT.text());
                } else {
                    builder.role(FUNCTION_CALL.text());
                    builder.toolCalls(aiMessage.toolExecutionRequests()
                            .stream()
                            .map(ChatToolCallDTO::from)
                            .toList()
                    );
                }
            }
            case USER -> {
                UserMessage userMessage = (UserMessage) message;
                builder.role(USER.text());
                builder.name(userMessage.name());
                builder.contents(userMessage.contents().stream()
                        .map(ChatContentDTO::from)
                        .toList());
            }
            case TOOL_EXECUTION_RESULT -> {
                ToolExecutionResultMessage resultMessage = (ToolExecutionResultMessage) message;
                ChatToolCallDTO toolCall = ChatToolCallDTO.builder()
                        .id(resultMessage.id())
                        .name(resultMessage.toolName())
                        .build();
                builder.role(FUNCTION_RESULT.text());
                builder.name((resultMessage).toolName());
                builder.contents(List.of(ChatContentDTO.from(TEXT, resultMessage.text())));
                builder.toolCalls(List.of(toolCall));
            }
        }
        return builder.build();
    }

    public static ChatMessageDTO fromPartialResult(String partialResult) {
        return from(AiMessage.from(partialResult), null);
    }

    public static ChatMessageDTO fromPartialThinking(PartialThinking partialThinking) {
        return from(AiMessage.builder().thinking(partialThinking.text()).build(), null);
    }

    public ChatMessage toChatMessage() {
        return switch (PromptRole.of(getRole())) {
            case SYSTEM -> SystemMessage.from(getContentText());
            case ASSISTANT -> AiMessage.builder().text(getContentText()).thinking(getThinking()).build();
            case USER -> {
                List<Content> messageContents = getContents().stream()
                        .map(ChatContentDTO::toContent)
                        .toList();
                yield StringUtils.isBlank(getName()) ?
                        UserMessage.from(messageContents) :
                        UserMessage.from(getName(), messageContents);

            }
            case FUNCTION_CALL -> AiMessage.from(getToolCalls().stream()
                    .map(ChatToolCallDTO::toToolExecutionRequest)
                    .toList());
            case FUNCTION_RESULT -> {
                String toolId = null;
                String toolName =getName();
                String result =getContentText();
                List<ChatToolCallDTO> messageToolCalls = getToolCalls();
                if (CollectionUtils.isNotEmpty(messageToolCalls)) {
                    ChatToolCallDTO toolCall = messageToolCalls.getFirst();
                    toolId = toolCall.getId();
                    toolName = toolCall.getName();
                }
                yield ToolExecutionResultMessage.from(toolId, toolName, result);
            }
        };
    }
}
