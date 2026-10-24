package fun.freechat.api.dto.conf;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import fun.freechat.api.dto.CharacterCreateDTO;
import fun.freechat.service.util.InfoUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Schema(description = "Character configuration")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Slf4j
public class CharacterConfigurationDTO {
    private CharacterCreateDTO character;
    private List<CharacterBackendConfigurationDTO> backends;

    @Schema(
            description =
                    "Optional redacted, credentialless portable connectors. Omitted by default on export; importing"
                            + " creates new inactive owner-private records without credentials.")
    private List<ConnectorConfigurationDTO> connectors;

    public String toJson() {
        try {
            return InfoUtils.defaultMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            log.error("Failed to generate character configuration");
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate character configuration!");
        }
    }

    public static CharacterConfigurationDTO fromJson(String json) {
        try {
            JsonNode root = InfoUtils.defaultMapper()
                    .reader()
                    .with(JsonParser.Feature.STRICT_DUPLICATE_DETECTION)
                    .with(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
                    .readTree(json);
            if (root == null || !root.isObject() || !root.path("character").isObject()) {
                throw new IllegalArgumentException();
            }
            JsonNode descriptors = root.get("connectors");
            if (descriptors != null
                    && !descriptors.isNull()
                    && (!descriptors.isArray() || descriptors.size() > ConnectorConfigurationDTO.MAX_DESCRIPTORS)) {
                throw new IllegalArgumentException();
            }
            CharacterConfigurationDTO configuration =
                    InfoUtils.defaultMapper().treeToValue(root, CharacterConfigurationDTO.class);
            if (configuration.connectors != null) {
                var references = new java.util.HashSet<String>();
                for (ConnectorConfigurationDTO connector : configuration.connectors) {
                    if (connector == null || !references.add(connector.reference())) {
                        throw new IllegalArgumentException();
                    }
                }
            }
            return configuration;
        } catch (JsonProcessingException | IllegalArgumentException | NullPointerException e) {
            // Jackson exception messages can contain the imported payload, including hostile credentials.
            log.error("Failed to parse character configuration");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to parse character configuration!");
        }
    }
}
