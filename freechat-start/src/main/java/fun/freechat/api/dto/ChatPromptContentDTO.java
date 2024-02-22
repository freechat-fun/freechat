package fun.freechat.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.langchain4j.data.message.UserMessage;
import fun.freechat.service.prompt.ChatPromptContent;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Schema(description = "Prompt chat template content")
@Data
@JsonInclude(NON_NULL)
public class ChatPromptContentDTO {
    @Schema(description = "Prompt system template")
    private String system;
    @Schema(description = "Chat new message template (usually as user role)")
    private ChatMessageDTO messageToSend;
    @Schema(description = "Pre-set chat messages in the Prompt")
    private List<ChatMessageDTO> messages;

    public static ChatPromptContentDTO from(ChatPromptContent chatPrompt) {
        if (Objects.isNull(chatPrompt)) {
            return null;
        }

        ChatPromptContentDTO dto = new ChatPromptContentDTO();
        dto.setSystem(chatPrompt.getSystem());
        dto.setMessageToSend(ChatMessageDTO.from(chatPrompt.getMessageToSend()));
        if (CollectionUtils.isNotEmpty(chatPrompt.getMessages())) {
            dto.setMessages(chatPrompt.getMessages().stream()
                    .map(ChatMessageDTO::from)
                    .toList());
        }
        return dto;
    }

    public ChatPromptContent toChatPromptContent() {
        ChatPromptContent chatPrompt = new ChatPromptContent();
        chatPrompt.setSystem(getSystem());

        Optional.ofNullable(getMessageToSend())
                .map(ChatMessageDTO::toChatMessage)
                .filter(UserMessage.class::isInstance)
                .map(UserMessage.class::cast)
                .ifPresent(chatPrompt::setMessageToSend);

        if (CollectionUtils.isNotEmpty(getMessages())) {
            chatPrompt.setMessages(getMessages().stream()
                    .map(ChatMessageDTO::toChatMessage)
                    .toList());
        }
        return chatPrompt;
    }
}
