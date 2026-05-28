package fun.freechat.api.dto.tg;

import fun.freechat.api.util.CommonUtils;
import fun.freechat.model.TgMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

@Schema(description = "Telegram message information")
@Data
public class TgMessageDTO {

    @Schema(description = "Record identifier")
    private Long id;

    @Schema(description = "Creation time")
    private LocalDateTime gmtCreate;

    @Schema(description = "Modification time")
    private LocalDateTime gmtModified;

    @Schema(description = "Related tg_chat.chat_id")
    private String chatId;

    @Schema(description = "Telegram message id")
    private Long tgMessageId;

    @Schema(description = "Sender telegram user id")
    private Long tgUserId;

    @Schema(description = "Direction: in | out")
    private String direction;

    @Schema(description = "Message type")
    private String messageType;

    @Schema(description = "Plain text content")
    private String content;

    @Schema(description = "Raw telegram update payload (JSON)")
    private String payload;

    public static TgMessageDTO from(TgMessage message) {
        if (message == null) {
            return null;
        }
        return CommonUtils.convert(message, TgMessageDTO.class);
    }
}
