package fun.freechat.api;

import fun.freechat.api.dto.ConnectorTestDTO;
import fun.freechat.api.dto.ConnectorToolDTO;
import fun.freechat.api.util.AccountUtils;
import fun.freechat.service.connectors.ConnectorProbe;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/connector")
@Tag(name = "Connector", description = "Private, user-owned remote MCP connectors")
@Validated
@RequiredArgsConstructor
public class ConnectorTestApi {
    private final ConnectorProbe probe;

    @PostMapping("/{connectorId}/test")
    @PreAuthorize("hasPermission(#p0, 'connectorDefaultOp')")
    @Operation(
            operationId = "testConnector",
            summary = "Test My Connector",
            description = "Initialize an owned HTTP or SSE MCP server and list its tools without executing any tool. "
                    + "Private addresses and redirects are rejected. The stored configuration revision must match.")
    public ConnectorTestDTO test(
            @PathVariable @NotBlank String connectorId, @RequestParam @NotBlank String expectedRevision) {
        return new ConnectorTestDTO(
                probe.tools(AccountUtils.currentUser().getUserId(), connectorId, expectedRevision).stream()
                        .map(tool -> new ConnectorToolDTO(tool.name(), tool.description()))
                        .toList());
    }
}
