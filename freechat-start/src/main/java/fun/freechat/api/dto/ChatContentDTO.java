package fun.freechat.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "Chat content")
@Data
public class ChatContentDTO {
    @Schema(description = "Chat content.", requiredMode = REQUIRED)
    private String content;
    @Schema(description = "Chat attachment.")
    private String attachment;
}
