package fun.freechat.api;

import fun.freechat.api.dto.ConnectorDetailsDTO;
import fun.freechat.api.dto.ConnectorWriteDTO;
import fun.freechat.api.util.AccountUtils;
import fun.freechat.service.connectors.ConnectorException;
import fun.freechat.service.connectors.ConnectorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/connector")
@Tag(name = "Connector", description = "Private, user-owned remote MCP connectors")
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class ConnectorApi {
    private final ConnectorService connectors;

    @GetMapping
    @Operation(operationId = "listConnectors", summary = "List My Connectors")
    public List<ConnectorDetailsDTO> list() {
        return connectors.list(AccountUtils.currentUser().getUserId()).stream()
                .map(ConnectorDetailsDTO::from)
                .toList();
    }

    @GetMapping("/{connectorId}")
    @PreAuthorize("hasPermission(#p0, 'connectorDefaultOp')")
    @Operation(
            operationId = "getConnector",
            summary = "Get My Connector",
            description = "Returns metadata and header names, never header values.")
    public ConnectorDetailsDTO get(@PathVariable String connectorId) {
        return ConnectorDetailsDTO.from(
                connectors.get(AccountUtils.currentUser().getUserId(), connectorId));
    }

    @PostMapping
    @Operation(
            operationId = "createConnector",
            summary = "Create My Connector",
            description =
                    "Validates syntax and stores encrypted header values without contacting the endpoint. Public address checks run before network use.")
    public ConnectorDetailsDTO create(@RequestBody ConnectorWriteDTO request) {
        if (request == null
                || request.getExpectedRevision() != null
                || (request.getKeepHeaderNames() != null
                        && !request.getKeepHeaderNames().isEmpty())) {
            throw new ConnectorException(ConnectorException.Code.CONNECTOR_INVALID_CONFIGURATION);
        }
        return ConnectorDetailsDTO.from(
                connectors.create(AccountUtils.currentUser().getUserId(), request.configuration(), request.isActive()));
    }

    @PutMapping("/{connectorId}")
    @PreAuthorize("hasPermission(#p0, 'connectorDefaultOp')")
    @Operation(
            operationId = "updateConnector",
            summary = "Update My Connector",
            description =
                    "Replaces an expected revision. Explicitly retain unchanged header names; retained credentials cannot move to another origin.")
    public ConnectorDetailsDTO update(@PathVariable String connectorId, @RequestBody ConnectorWriteDTO request) {
        if (request == null) {
            throw new ConnectorException(ConnectorException.Code.CONNECTOR_INVALID_CONFIGURATION);
        }
        return ConnectorDetailsDTO.from(connectors.update(
                AccountUtils.currentUser().getUserId(),
                connectorId,
                request.getExpectedRevision(),
                request.configuration(),
                request.getKeepHeaderNames() == null
                        ? java.util.Set.of()
                        : java.util.Set.copyOf(request.getKeepHeaderNames()),
                request.isActive()));
    }

    @DeleteMapping("/{connectorId}")
    @PreAuthorize("hasPermission(#p0, 'connectorDefaultOp')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(operationId = "deleteConnector", summary = "Delete My Connector")
    public void delete(@PathVariable String connectorId, @RequestParam String expectedRevision) {
        connectors.delete(AccountUtils.currentUser().getUserId(), connectorId, expectedRevision);
    }
}
