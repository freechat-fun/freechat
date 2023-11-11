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
    @Schema(description = "Character identifier")
    private String characterId;
    @Schema(description = "Whether it is the default backend")
    private Boolean isDefault;
    @Schema(description = "Prompt task identifier for greeting")
    private String greetingPromptTaskId;
    @Schema(description = "Prompt task identifier for experience")
    private String experiencePromptTaskId;
    @Schema(description = "Max messages in the character's memory")
    private Integer messageWindowSize;
    @Schema(description = "Whether to forward messages to the character owner")
    private Boolean forwardToUser;

    public static CharacterBackendDetailsDTO from(CharacterBackend backend) {
        if (Objects.isNull(backend)) {
            return null;
        }
        CharacterBackendDetailsDTO dto = CommonUtils.convert(backend, CharacterBackendDetailsDTO.class);
        dto.setIsDefault(backend.getIsDefault() == (byte) 1);
        dto.setForwardToUser(backend.getForwardToUser() == (byte) 1);
        return dto;
    }
}
