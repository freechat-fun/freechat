package fun.freechat.api.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import fun.freechat.api.util.AccountUtils;
import fun.freechat.api.util.CommonUtils;
import fun.freechat.model.CharacterInfo;
import fun.freechat.service.character.CharacterInfoDraft;
import fun.freechat.service.enums.GenderType;
import fun.freechat.service.enums.Visibility;
import fun.freechat.service.util.InfoUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

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
    @Schema(description = "Character avatar url")
    private String avatar;
    @Schema(description = "Character picture url")
    private String picture;
    @Schema(description = "Character gender: male | female | non_human | unknown")
    private String gender;
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
    @Schema(description = "Character language: English | Chinese (Simplified) | ...")
    private String lang;
    @Schema(description = "Additional information, JSON format")
    private String ext;
    @Schema(description = "Character draft information (for prompt)")
    private CharacterInfoDraftDTO draft;
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
        if (Objects.nonNull(getDraft())) {
            CharacterInfoDraft draft = getDraft().toCharacterInfoDraft();
            try {
                String draftText = InfoUtils.defaultMapper().writeValueAsString(draft);
                characterInfo.setDraft(draftText);
            } catch (JsonProcessingException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
            }
        }
        return Pair.of(characterInfo, getTags());
    }
}
