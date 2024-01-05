package fun.freechat.api.dto;

import fun.freechat.service.ai.message.ChatToolCall;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Objects;

@Schema(description = "Tool call information during the conversation")
@Data
public class ChatToolCallDTO {
    @Schema(description = "Tool id")
    private String id;
    @Schema(description = "Tool name")
    private String name;
    @Schema(description = "Tool parameters")
    private String arguments;

    public static ChatToolCallDTO from(ChatToolCall toolCall) {
        if (Objects.isNull(toolCall)) {
            return null;
        }

        ChatToolCallDTO dto = new ChatToolCallDTO();
        dto.setId(toolCall.getId());
        dto.setName(toolCall.getName());
        dto.setArguments(toolCall.getArguments());
        return dto;
    }

    public ChatToolCall toChatToolCall() {
        ChatToolCall toolCall = new ChatToolCall();
        toolCall.setId(getId());
        toolCall.setName(getName());
        toolCall.setArguments(getArguments());
        return toolCall;
    }
}
