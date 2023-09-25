package fun.freechat.api.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import fun.freechat.api.util.AccountUtils;
import fun.freechat.api.util.CommonUtils;
import fun.freechat.model.CharacterBackend;
import fun.freechat.model.CharacterInfo;
import fun.freechat.service.character.CharacterInfoDraft;
import fun.freechat.service.util.InfoUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

@Schema(description = "Character detailed content")
@Data
public class CharacterDetailsDTO extends CharacterSummaryDTO {
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
    @Schema(description = "Additional information, JSON format")
    private String ext;
    @Schema(description = "Character draft information (for prompt)")
    private CharacterInfoDraftDTO draft;
    @Schema(description = "Character backends information")
    private List<CharacterBackendDetailsDTO> backends;

    public static CharacterDetailsDTO fromCharacterInfo(
            Triple<CharacterInfo, List<String>, List<CharacterBackend>> characterInfoTriple) {
        if (Objects.isNull(characterInfoTriple)) {
            return null;
        }
        CharacterInfo info = characterInfoTriple.getLeft();
        CharacterDetailsDTO characterDetailsDTO =
                CommonUtils.convert(info, CharacterDetailsDTO.class);
        characterDetailsDTO.setUsername(AccountUtils.userIdToName(characterInfoTriple.getLeft().getUserId()));
        try {
            CharacterInfoDraft draft = InfoUtils.defaultMapper().readValue(
                    info.getDraft(), CharacterInfoDraft.class);
            characterDetailsDTO.setDraft(CharacterInfoDraftDTO.fromCharacterInfoDraft(draft));
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        characterDetailsDTO.setTags(characterInfoTriple.getMiddle());
        characterDetailsDTO.setBackends(characterInfoTriple.getRight()
                .stream()
                .map(CharacterBackendDetailsDTO::fromCharacterBackend)
                .filter(Objects::nonNull)
                .peek(backend -> backend.setRequestId(null))
                .toList());
        return characterDetailsDTO;
    }
}
