package fun.freechat.api.dto;

import fun.freechat.api.util.TagUtils;
import fun.freechat.model.CharacterInfo;
import fun.freechat.model.ChatContext;
import fun.freechat.service.chat.ChatMessageRecord;
import fun.freechat.service.enums.InfoType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;
import java.util.Objects;

@Schema(description = "Chat session")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatSessionDTO {
    @Schema(description = "Chat context")
    private ChatContextDTO context;
    @Schema(description = "Character summary info")
    private CharacterSummaryDTO character;
    @Schema(description = "Model provider: hugging_face | open_ai | azure_open_ai | dash_scope | ollama | unknown")
    @Pattern(regexp = "hugging_face|open_ai|azure_open_ai|dash_scope|ollama|unknown")
    private String provider;
    @Schema(description = "Latest message record")
    private ChatMessageRecordDTO latestMessageRecord;
    @Schema(description = "Minutes to wait for a proactive chat")
    private Integer proactiveChatWaitingTime;
    @Schema(description = "Sender status: online | offline | invisible")
    @Pattern(regexp = "online|offline|invisible")
    private String senderStatus;
    @Schema(description = "Is it possible to debug")
    private Boolean isDebugEnabled;
    @Schema(description = "Is it possible to customize api-key")
    private Boolean isCustomizedApiKeyEnabled;

    public static ChatSessionDTO from(
            Triple<ChatContext, CharacterInfo, ChatMessageRecord> chatItem,
            String provider,
            Integer proactiveChatWaitingTime,
            String senderStatus,
            Boolean isDebugEnabled,
            Boolean isCustomizedApiKeyEnabled) {
        if (chatItem == null || chatItem.getLeft() == null) {
            return null;
        }

        ChatContextDTO context = ChatContextDTO.from(chatItem.getLeft());
        Objects.requireNonNull(context).setRequestId(null);
        CharacterInfo characterInfo = chatItem.getMiddle();
        List<String> tags = TagUtils.getTags(InfoType.CHARACTER, characterInfo.getCharacterUid());

        return ChatSessionDTO.builder()
                .context(context)
                .character(CharacterSummaryDTO.from(Pair.of(characterInfo, tags)))
                .latestMessageRecord(ChatMessageRecordDTO.from(chatItem.getRight(), true))
                .provider(provider)
                .proactiveChatWaitingTime(proactiveChatWaitingTime)
                .senderStatus(senderStatus)
                .isDebugEnabled(isDebugEnabled)
                .isCustomizedApiKeyEnabled(isCustomizedApiKeyEnabled)
                .build();
    }
}
