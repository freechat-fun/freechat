package fun.freechat.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Schema(description = "Chat message")
@Data
@JsonInclude(NON_NULL)
public class ChatMessageDTO {
    @Schema(description = "Chat role: system | assistant | user | function_call | function_result")
    private String role;
    @Schema(description = "user: Name of the user role; function_call: Name of the called tool")
    private String name;
    @Schema(description = "default: Dialogue content; function_result: function call result, serialized as json")
    private String content;
    @Schema(description = "Function call information during the conversation")
    private ChatFunctionCallDTO functionCall;
    @Schema(description = "Creation time")
    private Date gmtCreate;
}
