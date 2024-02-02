package fun.freechat.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "Request data for starting a chat session")
@Data
public class ChatCreateDTO {
    @Schema(description = "User nickname for this session.")
    private String userNickname;
    @Schema(description = "User profile for this session.")
    private String userProfile;
    @Schema(description = "Character nickname for this session.")
    private String characterNickname;
    @Schema(description = "Anything about this session.")
    private String about;
    @Schema(description = "Character backend for this session.", requiredMode = REQUIRED)
    private String backendId;
    @Schema(description = "Extra info for this session.")
    private String ext;
}
