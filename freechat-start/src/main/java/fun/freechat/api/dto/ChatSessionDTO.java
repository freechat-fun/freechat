package fun.freechat.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import fun.freechat.model.CharacterInfo;
import fun.freechat.model.ChatContext;
import fun.freechat.service.chat.ChatMessageRecord;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Objects;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Schema(description = "Chat session")
@Data
@JsonInclude(NON_NULL)
public class ChatSessionDTO {
    @Schema(description = "Chat context")
    private ChatContextDTO context;
    @Schema(description = "Character summary info")
    private CharacterSummaryDTO character;
    @Schema(description = "Latest message record")
    private ChatMessageRecordDTO latestMessageRecord;
    @Schema(description = "Sender status: online | offline | invisible")
    private String senderStatus;
    @Schema(description = "Is it possible to debug")
    private Boolean isDebugEnabled;

    public static ChatSessionDTO from(
            Triple<ChatContext, CharacterInfo, ChatMessageRecord> chatItem,
            String senderStatus,
            Boolean isDebugEnabled) {
        if (Objects.isNull(chatItem) || Objects.isNull(chatItem.getLeft())) {
            return null;
        }

        ChatSessionDTO dto = new ChatSessionDTO();
        ChatContextDTO context = ChatContextDTO.from(chatItem.getLeft());
        Objects.requireNonNull(context).setRequestId(null);
        dto.setContext(context);
        dto.setCharacter(CharacterSummaryDTO.from(Pair.of(chatItem.getMiddle(), null)));
        dto.setLatestMessageRecord(ChatMessageRecordDTO.from(chatItem.getRight()));
        dto.setSenderStatus(senderStatus);
        dto.setIsDebugEnabled(isDebugEnabled);
        return dto;
    }
}
