package fun.freechat.service.connectors;

import fun.freechat.mapper.CharacterInfoMapper;
import fun.freechat.model.CharacterInfo;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConnectorCharacterPolicy {
    private final ConnectorService connectors;
    private final CharacterInfoMapper characters;

    public void validate(String ownerId, String ext, String draft) {
        Set<String> enabled =
                new HashSet<>(CharacterConnectorConfiguration.parse(ext).enabled());
        enabled.addAll(CharacterConnectorConfiguration.parse(CharacterConnectorConfiguration.draftExt(draft))
                .enabled());
        if (enabled.isEmpty()) {
            return;
        }
        if (ownerId == null || ownerId.isBlank()) {
            throw new ConnectorException(ConnectorException.Code.CONNECTOR_ACCESS_DENIED);
        }
        Set<String> owned = connectors.list(ownerId).stream()
                .map(ConnectorService.Summary::connectorId)
                .collect(Collectors.toSet());
        if (!owned.containsAll(enabled)) {
            throw new ConnectorException(ConnectorException.Code.CONNECTOR_ACCESS_DENIED);
        }
    }

    public String validateUpdate(Long characterId, String ext, String draft) {
        CharacterInfo character = character(characterId);
        validate(character.getUserId(), ext, draft);
        return character.getUserId();
    }

    public void validatePublication(Long characterId) {
        CharacterInfo character = character(characterId);
        String draftExt = CharacterConnectorConfiguration.draftExt(character.getDraft());
        validate(character.getUserId(), draftExt == null ? character.getExt() : draftExt, null);
    }

    private CharacterInfo character(Long characterId) {
        return characters
                .selectByPrimaryKey(characterId)
                .orElseThrow(() -> new ConnectorException(ConnectorException.Code.CONNECTOR_NOT_FOUND));
    }
}
