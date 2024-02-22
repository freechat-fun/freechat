package fun.freechat.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.data.message.*;
import dev.langchain4j.internal.ValidationUtils;
import fun.freechat.service.enums.PromptRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static dev.langchain4j.data.message.ContentType.TEXT;
import static fun.freechat.api.util.ChatUtils.contentTypeOf;
import static fun.freechat.service.enums.PromptRole.*;

@Schema(description = "Chat message")
@Data
@JsonInclude(NON_NULL)
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

    private void setContentText(String text) {
        setContents(List.of(ChatContentDTO.fromText(text)));
    }

    public static ChatMessageDTO from(ChatMessage message) {
        ChatMessageDTO dto = new ChatMessageDTO();
        switch (message.type()) {
            case SYSTEM -> {
                dto.setRole(SYSTEM.text());
                dto.setContentText(((SystemMessage) message).text());
            }
            case AI -> {
                AiMessage aiMessage = (AiMessage) message;
                dto.setContentText(aiMessage.text());
                List<ToolExecutionRequest> requests = aiMessage.toolExecutionRequests();
                if (Objects.isNull(requests)) {
                    dto.setRole(ASSISTANT.text());
                } else {
                    dto.setRole(FUNCTION_CALL.text());
                    dto.setToolCalls(requests.stream()
                            .map(ChatToolCallDTO::from)
                            .toList()
                    );
                }
            }
            case USER -> {
                UserMessage userMessage = (UserMessage) message;
                dto.setRole(USER.text());
                dto.setName(userMessage.name());
                dto.setContents(userMessage.contents().stream()
                        .map(ChatContentDTO::from)
                        .toList());
            }
            case TOOL_EXECUTION_RESULT -> {
                ToolExecutionResultMessage resultMessage = (ToolExecutionResultMessage) message;
                ChatToolCallDTO toolCall = new ChatToolCallDTO();
                toolCall.setId(resultMessage.id());
                toolCall.setName(resultMessage.toolName());
                dto.setRole(FUNCTION_RESULT.text());
                dto.setName((resultMessage).toolName());
                dto.setContentText(resultMessage.text());
                dto.setToolCalls(List.of(toolCall));
            }
        }
        return dto;
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
