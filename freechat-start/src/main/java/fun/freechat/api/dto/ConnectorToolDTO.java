package fun.freechat.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "An untrusted tool description advertised by a remote Connector")
public class ConnectorToolDTO {
    @Schema(description = "Remote tool name")
    private String name;

    @Schema(description = "Untrusted remote tool description; display as text")
    private String description;
}
