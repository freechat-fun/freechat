package fun.freechat.api.dto;

import fun.freechat.api.util.AccountUtils;
import fun.freechat.api.util.CommonUtils;
import fun.freechat.model.CharacterInfo;
import fun.freechat.service.enums.GenderType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Objects;

@Schema(description = "Request data for updating character information")
@Data
@EqualsAndHashCode(callSuper = true)
public class CharacterUpdateDTO extends CharacterCreateDTO {
    public Pair<CharacterInfo, List<String>> toCharacterInfo(Long characterId) {
        CharacterInfo characterInfo = CommonUtils.convert(this, CharacterInfo.class);
        characterInfo.setCharacterId(characterId);
        characterInfo.setUserId(
                Objects.requireNonNull(AccountUtils.currentUser()).getUserId());
        if (getGender() != null) {
            characterInfo.setGender(GenderType.of(getGender()).text());
        }
        return Pair.of(characterInfo, getTags());
    }
}
