package fun.freechat.api.dto;

import dev.langchain4j.data.message.UserMessage;
import fun.freechat.service.prompt.ChatPromptContent;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Optional;

@Schema(description = "Prompt chat template content")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatPromptContentDTO {
    @Schema(description = "Prompt system template")
    private String system;
    @Schema(description = "Chat new message template (usually as user role)")
    private ChatMessageDTO messageToSend;
    @Schema(description = "Pre-set chat messages in the Prompt")
    private List<ChatMessageDTO> messages;

    public static ChatPromptContentDTO from(ChatPromptContent chatPrompt) {
        if (chatPrompt == null) {
            return null;
        }

        var builder = ChatPromptContentDTO.builder();
        builder.system(chatPrompt.getSystem());
        builder.messageToSend(ChatMessageDTO.from(chatPrompt.getMessageToSend(), null));
        if (CollectionUtils.isNotEmpty(chatPrompt.getMessages())) {
            builder.messages(chatPrompt.getMessages().stream()
                    .map(message -> ChatMessageDTO.from(message, null))
                    .toList());
        }
        return builder.build();
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
