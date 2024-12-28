package fun.freechat.api.dto;

import fun.freechat.api.util.CommonUtils;
import fun.freechat.model.CharacterBackend;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import static fun.freechat.util.ByteUtils.isTrue;

@Schema(description = "Character backend information")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    @Pattern(regexp = "messages|tokens|none")
    private String quotaType;
    @Schema(description = "Enable character album image retrieval tool")
    private Boolean enableAlbumTool;
    @Schema(description = "Enable character tts feature")
    private Boolean enableTts;
    @Schema(description = "Character's speaker idx for tts")
    private String ttsSpeakerIdx;
    @Schema(description = "Character's speaker sample for tts")
    private String ttsSpeakerWav;
    @Schema(description = "Character's speaker type for tts: idx | wav")
    @Pattern(regexp = "idx|wav")
    private String ttsSpeakerType;

    public CharacterBackend toCharacterBackend(String characterUid) {
        if (StringUtils.isBlank(characterUid)) {
            return null;
        }
        CharacterBackend backend = CommonUtils.convert(this, CharacterBackend.class).withCharacterUid(characterUid);
        if (getIsDefault() == Boolean.TRUE) {
            backend.setIsDefault((byte) 1);
        } else if (getIsDefault() == Boolean.FALSE) {
            backend.setIsDefault((byte) 0);
        }

        if (getEnableAlbumTool() == Boolean.TRUE) {
            backend.setEnableAlbumTool((byte) 1);
        } else if (getEnableAlbumTool() == Boolean.FALSE) {
            backend.setEnableAlbumTool((byte) 0);
        }

        if (getEnableTts() == Boolean.TRUE) {
            backend.setEnableTts((byte) 1);
        } else if (getEnableTts() == Boolean.FALSE) {
            backend.setEnableTts((byte) 0);
        }

        return backend;
    }

    public static CharacterBackendDTO from(CharacterBackend backend) {
        if (backend == null) {
            return null;
        }
        CharacterBackendDTO dto = CommonUtils.convert(backend, CharacterBackendDTO.class);
        dto.setIsDefault(isTrue(backend.getIsDefault()));
        dto.setEnableAlbumTool(isTrue(backend.getEnableAlbumTool()));
        dto.setEnableTts(isTrue(backend.getEnableTts()));
        return dto;
    }
}
