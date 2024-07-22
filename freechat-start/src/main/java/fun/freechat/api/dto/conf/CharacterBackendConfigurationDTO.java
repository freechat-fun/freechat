package fun.freechat.api.dto.conf;

import fun.freechat.api.dto.CharacterBackendDTO;
import fun.freechat.api.dto.PromptCreateDTO;
import fun.freechat.api.dto.PromptTaskDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Schema(description = "Character backend configuration")
@Data
@Builder
public class CharacterBackendConfigurationDTO {
    private CharacterBackendDTO backend;
    private PromptCreateDTO chatPrompt;
    private PromptTaskDTO chatPromptTask;
    private PromptCreateDTO greetingPrompt;
    private PromptTaskDTO greetingPromptTask;
}
