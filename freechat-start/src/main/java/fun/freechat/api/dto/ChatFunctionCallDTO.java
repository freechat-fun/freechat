package fun.freechat.api.dto;

import fun.freechat.service.ai.message.ChatFunctionCall;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Objects;

@Schema(description = "Function call information during the conversation")
@Data
public class ChatFunctionCallDTO {
    @Schema(description = "Function name")
    private String name;
    @Schema(description = "Function parameters")
    private String arguments;

    public static ChatFunctionCallDTO from(ChatFunctionCall functionCall) {
        if (Objects.isNull(functionCall)) {
            return null;
        }

        ChatFunctionCallDTO dto = new ChatFunctionCallDTO();
        dto.setName(functionCall.getName());
        dto.setArguments(functionCall.getArguments());
        return dto;
    }
}
