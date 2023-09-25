package fun.freechat.api.dto;

import fun.freechat.api.util.CommonUtils;
import fun.freechat.model.CharacterBackend;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Schema(description = "Character backend information")
@Data
public class CharacterBackendDTO {
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

    public CharacterBackend toCharacterBackend(String characterId) {
        if (StringUtils.isBlank(characterId)) {
            return null;
        }
        return CommonUtils.convert(this, CharacterBackend.class).withCharacterId(characterId);
    }
}
