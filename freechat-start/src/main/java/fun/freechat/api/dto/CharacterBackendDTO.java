package fun.freechat.api.dto;

import fun.freechat.api.util.CommonUtils;
import fun.freechat.model.CharacterBackend;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

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
    @Schema(description = "Max rounds (a round includes a user message and a character reply) in the character's long term memory, 0 to disable")
    private Integer longTermMemoryWindowSize;
    @Schema(description = "Minutes to wait for a proactive chat")
    private Integer proactiveChatWaitingTime;
    @Schema(description = "Initial quota when opening a chat")
    private Long initQuota;
    @Schema(description = "Quota type: messages | tokens | none (not limited)")
    private String quotaType;

    public CharacterBackend toCharacterBackend(String characterUid) {
        if (StringUtils.isBlank(characterUid)) {
            return null;
        }
        CharacterBackend backend = CommonUtils.convert(this, CharacterBackend.class).withCharacterUid(characterUid);
        backend.setIsDefault(BooleanUtils.isTrue(getIsDefault()) ? (byte) 1 : (byte) 0);
        return backend;
    }

    public static CharacterBackendDTO from(CharacterBackend backend) {
        if (backend == null) {
            return null;
        }
        CharacterBackendDTO dto = CommonUtils.convert(backend, CharacterBackendDTO.class);
        dto.setIsDefault(backend.getIsDefault() == (byte) 1);
        return dto;
    }
}
