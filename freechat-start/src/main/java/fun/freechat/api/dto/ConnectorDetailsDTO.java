package fun.freechat.api.dto;

import fun.freechat.service.connectors.ConnectorService;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Private connector metadata. Header names only; secret values are never returned.")
public class ConnectorDetailsDTO {
    private String connectorId;
    private String revision;
    private String name;
    private String transport;
    private String url;
    private Integer timeout;
    private boolean active;
    private List<String> headerNames;
    private Instant modified;

    public static ConnectorDetailsDTO from(ConnectorService.Summary summary) {
        ConnectorDetailsDTO dto = new ConnectorDetailsDTO();
        dto.connectorId = summary.connectorId();
        dto.revision = summary.revision();
        dto.name = summary.name();
        dto.transport = summary.transport();
        dto.url = summary.url();
        dto.timeout = summary.timeout();
        dto.active = summary.active();
        dto.headerNames = summary.headerNames().stream().sorted().toList();
        dto.modified = summary.modified();
        return dto;
    }

    @Override
    public String toString() {
        return "ConnectorDetailsDTO[redacted]";
    }
}
