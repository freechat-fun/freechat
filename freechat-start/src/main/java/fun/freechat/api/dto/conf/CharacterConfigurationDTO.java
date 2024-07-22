package fun.freechat.api.dto.conf;

import com.fasterxml.jackson.core.JsonProcessingException;
import fun.freechat.api.dto.CharacterCreateDTO;
import fun.freechat.service.util.InfoUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Schema(description = "Character configuration")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Slf4j
public class CharacterConfigurationDTO {
    private CharacterCreateDTO character;
    private List<CharacterBackendConfigurationDTO> backends;

    public String toJson() {
        try {
            return InfoUtils.defaultMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse character configuration: {}", this, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to generate character configuration!");
        }
    }

    public static CharacterConfigurationDTO fromJson(String json) {
        try {
            return InfoUtils.defaultMapper().readValue(json, CharacterConfigurationDTO.class);
        } catch (JsonProcessingException | NullPointerException e) {
            log.error("Failed to parse character configuration: {}", json, e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Failed to parse character configuration!");
        }
    }
}
