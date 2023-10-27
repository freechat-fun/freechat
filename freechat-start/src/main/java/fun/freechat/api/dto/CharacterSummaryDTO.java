package fun.freechat.api.dto;

import fun.freechat.api.util.AccountUtils;
import fun.freechat.api.util.CommonUtils;
import fun.freechat.model.CharacterInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "Character summary content")
@Data
public class CharacterSummaryDTO extends TraceableDTO {
    @Schema(description = "Character identifier")
    private String characterId;
    @Schema(description = "Creation time")
    private Date gmtCreate;
    @Schema(description = "Modification time")
    private Date gmtModified;
    @Schema(description = "Visibility: private, public, public_org, hidden")
    private String visibility;
    @Schema(description = "Version")
    private Integer version;
    @Schema(description = "Character name", requiredMode = REQUIRED)
    private String name;
    @Schema(description = "Character description")
    private String description;
    @Schema(description = "Character avatar url")
    private String avatar;
    @Schema(description = "Character picture url")
    private String picture;
    @Schema(description = "Character language: English | Chinese (Simplified) | ...")
    private String lang;
    @Schema(description = "Character owner")
    private String username;
    @Schema(description = "Tag set")
    private List<String> tags;

    public static CharacterSummaryDTO from(Pair<CharacterInfo, List<String>> characterInfoPair) {
        if (Objects.isNull(characterInfoPair)) {
            return null;
        }
        CharacterSummaryDTO dto =
                CommonUtils.convert(characterInfoPair.getLeft(), CharacterSummaryDTO.class);
        dto.setUsername(AccountUtils.userIdToName(characterInfoPair.getLeft().getUserId()));
        dto.setTags(characterInfoPair.getRight());
        return dto;
    }
}
