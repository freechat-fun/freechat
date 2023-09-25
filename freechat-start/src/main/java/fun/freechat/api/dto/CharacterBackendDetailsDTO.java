package fun.freechat.api.dto;

import fun.freechat.api.util.CommonUtils;
import fun.freechat.model.CharacterBackend;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.Objects;

@Schema(description = "Character backend detailed information")
@Data
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
    private Byte isDefault;
    @Schema(description = "Prompt task identifier for chat")
    private String chatPromptTaskId;
    @Schema(description = "Prompt task identifier for chat example")
    private String chatExamplePromptTaskId;
    @Schema(description = "Prompt task identifier for greeting")
    private String greetingPromptTaskId;
    @Schema(description = "Prompt task identifier for experience")
    private String experiencePromptTaskId;

    public static CharacterBackendDetailsDTO fromCharacterBackend(CharacterBackend backend) {
        if (Objects.isNull(backend)) {
            return null;
        }
        return CommonUtils.convert(backend, CharacterBackendDetailsDTO.class);
    }
}
