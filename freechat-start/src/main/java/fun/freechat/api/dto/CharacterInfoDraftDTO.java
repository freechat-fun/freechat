package fun.freechat.api.dto;

import fun.freechat.api.util.CommonUtils;
import fun.freechat.service.character.CharacterInfoDraft;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Objects;

@Schema(description = "Character draft information (for prompt)")
@Data
public class CharacterInfoDraftDTO {
    @Schema(description = "Character profile")
    private String profile;
    @Schema(description = "Character greeting")
    private String greeting;
    @Schema(description = "Character chat-style")
    private String chatStyle;
    @Schema(description = "Character chat-example")
    private String chatExample;
    @Schema(description = "Character experience")
    private String experience;

    public static CharacterInfoDraftDTO fromCharacterInfoDraft(CharacterInfoDraft draft) {
        if (Objects.isNull(draft)) {
            return null;
        }
        return CommonUtils.convert(draft, CharacterInfoDraftDTO.class);
    }

    public CharacterInfoDraft toCharacterInfoDraft() {
        return CommonUtils.convert(this, CharacterInfoDraft.class);
    }
}
