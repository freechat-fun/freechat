package fun.freechat.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import fun.freechat.service.ai.message.ChatMessage;
import fun.freechat.service.enums.PromptRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

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
    @Schema(description = "Creation time")
    private Date gmtCreate;
    @Schema(description = "Contextual information in this round of conversation")
    private String context;

    public static ChatMessageDTO from(ChatMessage message) {
        return from(message, null);
    }

    public static ChatMessageDTO from(ChatMessage message, String context) {
        if (Objects.isNull(message)) {
            return null;
        }

        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setRole(message.getRole().text());
        dto.setName(message.getName());
        dto.setContext(context);
        if (CollectionUtils.isNotEmpty(message.getContents())) {
            dto.setContents(message.getContents().stream()
                    .map(ChatContentDTO::from)
                    .toList());
        }
        dto.setGmtCreate(message.getGmtCreate());
        if (CollectionUtils.isNotEmpty(message.getToolCalls())) {
            dto.setToolCalls(message.getToolCalls().stream()
                    .map(ChatToolCallDTO::from)
                    .toList());
        }
        return dto;
    }

    public ChatMessage toChatMessage() {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setRole(PromptRole.of(getRole()));
        chatMessage.setName(getName());
        if (CollectionUtils.isNotEmpty(getContents())) {
            chatMessage.setContents(getContents().stream()
                    .map(ChatContentDTO::toChatContent)
                    .toList());
        }
        if (CollectionUtils.isNotEmpty(getToolCalls())) {
            chatMessage.setToolCalls(getToolCalls().stream()
                    .map(ChatToolCallDTO::toChatToolCall)
                    .toList());
        }
        chatMessage.setGmtCreate(getGmtCreate());
        return chatMessage;
    }
}
