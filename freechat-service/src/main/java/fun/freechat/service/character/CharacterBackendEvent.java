package fun.freechat.service.character;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CharacterBackendEvent {
    private String userId;
    private String backendId;
}
