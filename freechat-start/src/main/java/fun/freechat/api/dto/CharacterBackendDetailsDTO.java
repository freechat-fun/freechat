package fun.freechat.api.dto;

import fun.freechat.api.util.CommonUtils;
import fun.freechat.model.CharacterBackend;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.Objects;

@Schema(description = "Character backend detailed information")
@Data
@EqualsAndHashCode(callSuper = true)
public class CharacterBackendDetailsDTO extends TraceableDTO {
    @Schema(description = "Character backend identifier")
    private String backendId;
    @Schema(description = "Creation time")
    private Date gmtCreate;
    @Schema(description = "Modification time")
    private Date gmtModified;
    @Schema(description = "Character immutable identifier")
    private String characterUid;
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
    @Schema(description = "Max rounds (a round includes a user message and a character reply) in the character's long term memory")
    private Integer longTermMemoryWindowSize;
    @Schema(description = "Minutes to wait for a proactive chat")
    private Integer proactiveChatWaitingTime;
    @Schema(description = "Initial quota when opening a chat")
    private Long initQuota;
    @Schema(description = "Quota type: messages | tokens | none (not limited)")
    private String quotaType;

    public static CharacterBackendDetailsDTO from(CharacterBackend backend) {
        if (Objects.isNull(backend)) {
            return null;
        }
        CharacterBackendDetailsDTO dto = CommonUtils.convert(backend, CharacterBackendDetailsDTO.class);
        dto.setIsDefault(backend.getIsDefault() == (byte) 1);
        return dto;
    }
}
