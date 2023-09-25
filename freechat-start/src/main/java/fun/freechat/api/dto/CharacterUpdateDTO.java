package fun.freechat.api.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import fun.freechat.api.util.AccountUtils;
import fun.freechat.api.util.CommonUtils;
import fun.freechat.model.CharacterInfo;
import fun.freechat.service.character.CharacterInfoDraft;
import fun.freechat.service.util.InfoUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

@Schema(description = "Request data for updating character information")
@Data
public class CharacterUpdateDTO extends CharacterCreateDTO {
    public Pair<CharacterInfo, List<String>> toCharacterInfo(String characterId) {
        CharacterInfo characterInfo = CommonUtils.convert(this, CharacterInfo.class);
        characterInfo.setCharacterId(characterId);
        characterInfo.setUserId(
                Objects.requireNonNull(AccountUtils.currentUser()).getUserId());
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
