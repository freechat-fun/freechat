package fun.freechat.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "Request data for starting a chat session")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ChatCreateDTO {
    @Schema(description = "User nickname for this session")
    private String userNickname;
    @Schema(description = "User profile for this session")
    private String userProfile;
    @Schema(description = "Character nickname for this session")
    private String characterNickname;
    @Schema(description = "Anything about this session")
    private String about;
    @Schema(description = "Character uid for this session", requiredMode = REQUIRED)
    private String characterUid;
    @Schema(description = "Character backend for this session")
    private String backendId;
    @Schema(description = "API-KEY name, priority: apiKeyName > apiKeyValue")
    private String apiKeyName;
    @Schema(description = "API-KEY value")
    private String apiKeyValue;
    @Schema(description = "Extra info for this session")
    private String ext;
}
