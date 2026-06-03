package fun.freechat.api.dto;

import fun.freechat.api.util.AccountUtils;
import fun.freechat.api.util.CharacterUtils;
import fun.freechat.api.util.CommonUtils;
import fun.freechat.model.CharacterInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Character summary content")
@Data
@EqualsAndHashCode(callSuper = true)
public class CharacterSummaryDTO extends TraceableDTO {
    @Schema(description = "Character identifier, will change after publish")
    private Long characterId;

    @Schema(description = "Character immutable identifier")
    private String characterUid;

    @Schema(description = "Creation time")
    private LocalDateTime gmtCreate;

    @Schema(description = "Modification time")
    private LocalDateTime gmtModified;

    @Schema(description = "Referenced character")
    private String parentUid;

    @Schema(description = "Visibility: private, public, public_org, hidden")
    private String visibility;

    @Schema(description = "Version")
    private Integer version;

    @Schema(description = "Character name")
    private String name;

    @Schema(description = "Character description")
    private String description;

    @Schema(description = "Character nickname")
    private String nickname;

    @Schema(description = "Character avatar url")
    private String avatar;

    @Schema(description = "Character picture url")
    private String picture;

    @Schema(description = "Character video url")
    private String video;

    @Schema(description = "Character gender: male | female | other")
    private String gender;

    @Schema(description = "Character language: en (default) | zh | ...")
    private String lang;

    @Schema(description = "Character greeting")
    private String greeting;

    @Schema(
            description =
                    "Default scene, which will be set as the default conversation background information when creating a new chat")
    private String defaultScene;

    @Schema(description = "Character priority")
    private Integer priority;

    @Schema(description = "Character owner")
    private String username;

    @Schema(description = "Tag set")
    private List<String> tags;

    @Schema(description = "Telegram URL")
    private String telegramUrl;

    public static CharacterSummaryDTO from(Pair<CharacterInfo, List<String>> characterInfoPair) {
        if (characterInfoPair == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Failed to find character!");
        }
        CharacterInfo info = characterInfoPair.getLeft();
        CharacterSummaryDTO dto = CommonUtils.convert(info, CharacterSummaryDTO.class);
        dto.setUsername(AccountUtils.userIdToName(info.getUserId()));
        dto.setTags(characterInfoPair.getRight());
        dto.setTelegramUrl(CharacterUtils.telegramUrl(info.getCharacterUid()));
        return dto;
    }
}
