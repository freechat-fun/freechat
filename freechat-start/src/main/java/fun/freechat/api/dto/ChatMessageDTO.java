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
    @Schema(description = "Chat role: system | assistant | user | function_call | function_result")
    private String role;
    @Schema(description = "user: Name of the user role; function_call: Name of the called tool")
    private String name;
    @Schema(description = "default: Dialogue content; function_result: function call result, serialized as json")
    private String content;
    @Schema(description = "Tool calls information during the conversation")
    private List<ChatToolCallDTO> toolCalls;
    @Schema(description = "Creation time")
    private Date gmtCreate;

    public static ChatMessageDTO from(ChatMessage message) {
        if (Objects.isNull(message)) {
            return null;
        }

        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setRole(message.getRole().text());
        dto.setName(message.getName());
        dto.setContent(message.getContent());
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
        chatMessage.setContent(getContent());
        if (CollectionUtils.isNotEmpty(getToolCalls())) {
            chatMessage.setToolCalls(getToolCalls().stream()
                    .map(ChatToolCallDTO::toChatToolCall)
                    .toList());
        }
        chatMessage.setGmtCreate(getGmtCreate());
        return chatMessage;
    }
}
