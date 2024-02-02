package fun.freechat.api.dto;

import fun.freechat.api.util.AccountUtils;
import fun.freechat.api.util.CommonUtils;
import fun.freechat.model.CharacterInfo;
import fun.freechat.service.enums.GenderType;
import fun.freechat.service.enums.Visibility;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Objects;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "Request data for creating new character information")
@Data
public class CharacterCreateDTO {
    @Schema(description = "Referenced character")
    private String parentId;
    @Schema(description = "Visibility: private (default), public, public_org, hidden")
    private String visibility;
    @Schema(description = "Character name", requiredMode = REQUIRED)
    private String name;
    @Schema(description = "Character description")
    private String description;
    @Schema(description = "Character nickname")
    private String nickname;
    @Schema(description = "Character avatar url")
    private String avatar;
    @Schema(description = "Character picture url")
    private String picture;
    @Schema(description = "Character gender: male | female | other")
    private String gender;
    @Schema(description = "Character profile")
    private String profile;
    @Schema(description = "Character greeting")
    private String greeting;
    @Schema(description = "Character chat-style")
    private String chatStyle;
    @Schema(description = "Character chat-example")
    private String chatExample;
    @Schema(description = "Character language: English | Chinese (Simplified) | ...")
    private String lang;
    @Schema(description = "Additional information, JSON format")
    private String ext;
    @Schema(description = "Character draft information")
    private String draft;
    @Schema(description = "Tag set")
    private List<String> tags;

    public Pair<CharacterInfo, List<String>> toCharacterInfo() {
        CharacterInfo characterInfo = CommonUtils.convert(this, CharacterInfo.class);
        characterInfo.setCharacterId(null);
        characterInfo.setVersion(0);
        characterInfo.setUserId(
                Objects.requireNonNull(AccountUtils.currentUser()).getUserId());
        characterInfo.setGender(GenderType.of(getGender()).text());
        characterInfo.setVisibility(Visibility.of(getVisibility()).text());
        characterInfo.setDraft(getDraft());
        return Pair.of(characterInfo, getTags());
    }
}
