package fun.freechat.api.dto;

import fun.freechat.api.util.AccountUtils;
import fun.freechat.api.util.CommonUtils;
import fun.freechat.model.CharacterBackend;
import fun.freechat.model.CharacterInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

@Schema(description = "Character detailed content")
@Data
@EqualsAndHashCode(callSuper = true)
public class CharacterDetailsDTO extends CharacterSummaryDTO {
    @Schema(description = "Character profile")
    private String profile;
    @Schema(description = "Character chat-style")
    private String chatStyle;
    @Schema(description = "Character chat-example")
    private String chatExample;
    @Schema(description = "Additional information, JSON format")
    private String ext;
    @Schema(description = "Character draft information")
    private String draft;
    @Schema(description = "Character backends information")
    private List<CharacterBackendDetailsDTO> backends;

    public static CharacterDetailsDTO from(
            Triple<CharacterInfo, List<String>, List<CharacterBackend>> characterInfoTriple) {
        if (characterInfoTriple == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Failed to find character!");
        }
        CharacterInfo info = characterInfoTriple.getLeft();
        CharacterDetailsDTO dto =
                CommonUtils.convert(info, CharacterDetailsDTO.class);
        dto.setUsername(AccountUtils.userIdToName(characterInfoTriple.getLeft().getUserId()));
        dto.setTags(characterInfoTriple.getMiddle());
        dto.setBackends(characterInfoTriple.getRight()
                .stream()
                .map(CharacterBackendDetailsDTO::from)
                .filter(Objects::nonNull)
                .peek(backend -> backend.setRequestId(null))
                .toList());
        return dto;
    }
}
