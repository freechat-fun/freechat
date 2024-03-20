package fun.freechat.api.dto;

import fun.freechat.api.util.CommonUtils;
import fun.freechat.model.CharacterBackend;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

@Schema(description = "Character backend information")
@Data
public class CharacterBackendDTO {
    @Schema(description = "Whether it is the default backend")
    private Boolean isDefault;
    @Schema(description = "Prompt task identifier for chat")
    private String chatPromptTaskId;
    @Schema(description = "Prompt task identifier for greeting")
    private String greetingPromptTaskId;
    @Schema(description = "Moderation model for the character's response")
    private String moderationModelId;
    @Schema(description = "Api key name for moderation model")
    private String moderationApiKeyName;
    @Schema(description = "Parameters for moderation model")
    private String moderationParams;
    @Schema(description = "Max messages in the character's memory")
    private Integer messageWindowSize;
    @Schema(description = "Weather to forward messages to the character owner")
    private Boolean forwardToUser;

    public CharacterBackend toCharacterBackend(String characterUid) {
        if (StringUtils.isBlank(characterUid)) {
            return null;
        }
        CharacterBackend backend = CommonUtils.convert(this, CharacterBackend.class).withCharacterUid(characterUid);
        backend.setIsDefault(BooleanUtils.isTrue(getIsDefault()) ? (byte) 1 : (byte) 0);
        backend.setForwardToUser(BooleanUtils.isTrue(getForwardToUser()) ? (byte) 1 : (byte) 0);
        return backend;
    }
}
