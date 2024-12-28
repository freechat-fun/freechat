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
    @Schema(description = "Creation time")
    private Date gmtCreate;
    @Schema(description = "Additional information")
    private String ext;

    public static ChatMessageRecordDTO from(ChatMessageRecord messageRecord, boolean debugInfo) {
        if (messageRecord == null) {
            return null;
        }

        ChatMessageDTO message = ChatMessageDTO.from(messageRecord.getMessage(), messageRecord.getId());
        var builder = ChatMessageRecordDTO.builder()
                .message(message)
                .gmtCreate(messageRecord.getGmtCreate());

        String userId = AccountUtils.currentUser().getUserId();

        if (debugInfo &&
                userId.equals(messageRecord.getCharacterOwnerId()) &&
                userId.equals(messageRecord.getChatOwnerId())) {
            builder.ext(ChatUtils.getChatMessageExt(messageRecord));
        }

        return builder.build();
    }
}
