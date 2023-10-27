package fun.freechat.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Schema(description = "Prompt chat template content")
@Data
@JsonInclude(NON_NULL)
public class ChatPromptContentDTO {
    @Schema(description = "Prompt system template")
    private String system;
    @Schema(description = "Chat new message template (usually as user role)")
    private ChatMessageDTO messagesToSend;
    @Schema(description = "Pre-set chat messages in the Prompt")
    private List<ChatMessageDTO> messages;
}
