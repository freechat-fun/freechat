package fun.freechat.api.dto;

import fun.freechat.api.util.AccountUtils;
import fun.freechat.api.util.ChatUtils;
import fun.freechat.service.chat.ChatMessageRecord;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Schema(description = "Chat message record")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Slf4j
public class ChatMessageRecordDTO {
    @Schema(description = "Message")
    private ChatMessageDTO message;
    @Schema(description = "Message identifier")
    private Long messageId;
    @Schema(description = "Creation time")
    private Date gmtCreate;
    @Schema(description = "Additional information")
    private String ext;

    public static ChatMessageRecordDTO from(ChatMessageRecord record, boolean debugInfo) {
        if (record == null) {
            return null;
        }

        ChatMessageDTO message = ChatMessageDTO.from(record.getMessage());
        var builder = ChatMessageRecordDTO.builder()
                .message(message)
                .gmtCreate(record.getGmtCreate())
                .messageId(record.getId());

        String userId = AccountUtils.currentUser().getUserId();

        if (debugInfo &&
                userId.equals(record.getCharacterOwnerId()) &&
                userId.equals(record.getChatOwnerId())) {
            builder.ext(ChatUtils.getChatMessageExt(record));
        }

        return builder.build();
    }
}
