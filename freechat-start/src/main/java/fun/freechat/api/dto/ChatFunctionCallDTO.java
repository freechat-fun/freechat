package fun.freechat.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "Function call information during the conversation")
@Data
public class ChatFunctionCallDTO {
    @Schema(description = "Function name")
    private String name;
    @Schema(description = "Function parameters")
    private String arguments;
}
