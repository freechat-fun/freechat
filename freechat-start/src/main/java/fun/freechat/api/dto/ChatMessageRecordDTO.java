package fun.freechat.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import fun.freechat.api.util.AccountUtils;
import fun.freechat.api.util.ChatUtils;
import fun.freechat.service.chat.ChatMessageRecord;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Schema(description = "Chat message record")
@Data
@Slf4j
@JsonInclude(NON_NULL)
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
        ChatMessageRecordDTO dto = new ChatMessageRecordDTO();
        dto.setMessage(message);
        dto.setGmtCreate(record.getGmtCreate());
        dto.setMessageId(record.getId());

        String userId = AccountUtils.currentUser().getUserId();

        if (debugInfo &&
                userId.equals(record.getCharacterOwnerId()) &&
                userId.equals(record.getChatOwnerId())) {
           dto.setExt(ChatUtils.getChatMessageExt(record));
        }

        return dto;
    }
}
