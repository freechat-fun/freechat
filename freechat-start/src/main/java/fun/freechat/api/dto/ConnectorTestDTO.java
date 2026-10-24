package fun.freechat.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Connector discovery result; no remote tools have been executed")
public class ConnectorTestDTO {
    @Schema(description = "Bounded catalog of remote tools")
    private List<ConnectorToolDTO> tools;
}
