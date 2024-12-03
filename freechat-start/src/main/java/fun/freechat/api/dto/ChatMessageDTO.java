package fun.freechat.api.dto;

import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.data.message.*;
import dev.langchain4j.internal.ValidationUtils;
import fun.freechat.service.enums.PromptRole;
import io.swagger.v3.oas.annotations.media.Schema;
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
import static fun.freechat.service.enums.PromptRole.*;

@Schema(description = "Chat message")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageDTO {
    @Schema(description = "Chat role: system | assistant | user | tool_call | tool_result")
    private String role;
    @Schema(description = "user: Name of the user role; tool_call: Name of the called tool")
    private String name;
    @Schema(description = "default: Dialogue content; tool_result: tool call result, serialized as json")
    private List<ChatContentDTO> contents;
    @Schema(description = "Tool calls information during the conversation")
    private List<ChatToolCallDTO> toolCalls;
    @Schema(description = "Contextual information in this round of conversation (the external RAG result can be passed in through this parameter)")
    private String context;

    private String getContentText() {
        return ValidationUtils.ensureNotEmpty(contents, "contents")
                .stream()
                .filter(content -> contentTypeOf(content.getType()) == TEXT)
                .map(ChatContentDTO::getContent)
                .collect(Collectors.joining("\n"));
    }

    public static ChatMessageDTO from(ChatMessage message) {
        var builder = ChatMessageDTO.builder();
        switch (message.type()) {
            case SYSTEM -> {
                builder.role(SYSTEM.text());
                builder.contents(List.of(ChatContentDTO.fromText(((SystemMessage) message).text())));
            }
            case AI -> {
                AiMessage aiMessage = (AiMessage) message;
                if (StringUtils.isNotBlank(aiMessage.text())) {
                    builder.contents(List.of(ChatContentDTO.fromText(aiMessage.text())));
                }
                List<ToolExecutionRequest> requests = aiMessage.toolExecutionRequests();
                if (requests == null) {
                    builder.role(ASSISTANT.text());
                } else {
                    builder.role(FUNCTION_CALL.text());
                    builder.toolCalls(requests.stream()
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
                builder.contents(List.of(ChatContentDTO.fromText(resultMessage.text())));
                builder.toolCalls(List.of(toolCall));
            }
        }
        return builder.build();
    }

    public ChatMessage toChatMessage() {
        return switch (PromptRole.of(getRole())) {
            case SYSTEM -> SystemMessage.from(getContentText());
            case ASSISTANT -> AiMessage.from(getContentText());
            case USER -> {
                List<Content> contents = getContents().stream()
                        .map(ChatContentDTO::toContent)
                        .toList();
                yield StringUtils.isBlank(getName()) ?
                        UserMessage.from(contents) :
                        UserMessage.from(getName(), contents);

            }
            case FUNCTION_CALL -> AiMessage.from(getToolCalls().stream()
                    .map(ChatToolCallDTO::toToolExecutionRequest)
                    .toList());
            case FUNCTION_RESULT -> {
                String id = null;
                String name =getName();
                String result =getContentText();
                List<ChatToolCallDTO> toolCalls = getToolCalls();
                if (CollectionUtils.isNotEmpty(toolCalls)) {
                    ChatToolCallDTO toolCall = toolCalls.getFirst();
                    id = toolCall.getId();
                    name = toolCall.getName();
                }
                yield ToolExecutionResultMessage.from(id, name, result);
            }
        };
    }
}
