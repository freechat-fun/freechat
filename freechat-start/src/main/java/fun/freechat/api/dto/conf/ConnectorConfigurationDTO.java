package fun.freechat.api.dto.conf;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.JsonNode;
import fun.freechat.service.connectors.ConnectorConfiguration;
import fun.freechat.service.connectors.ConnectorException;
import fun.freechat.service.connectors.ConnectorService;
import fun.freechat.service.tools.ToolArguments;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Schema(
        description =
                "Credentialless portable connector. Reference is an archive-local remapping key, never an imported"
                        + " registry ID. Required header names carry no values; imports are always inactive.")
public record ConnectorConfigurationDTO(
        String reference,
        String name,
        String transport,
        String url,
        Integer timeout,
        List<String> requiredHeaderNames) {
    public static final int MAX_DESCRIPTORS = 16;

    public ConnectorConfigurationDTO {
        if (reference == null || !reference.matches("[0-9a-f]{32}")) {
            throw invalid();
        }
        requiredHeaderNames = requiredHeaderNames == null ? List.of() : new ArrayList<>(requiredHeaderNames);
        // Reuse the registry's pure syntax validator before any import writes.
        new ConnectorConfiguration(name, transport, url, timeout, placeholders(requiredHeaderNames));
        requiredHeaderNames = List.copyOf(requiredHeaderNames);
    }

    public static ConnectorConfigurationDTO from(ConnectorService.Summary summary) {
        return new ConnectorConfigurationDTO(
                summary.connectorId(),
                summary.name(),
                summary.transport(),
                summary.url(),
                summary.timeout(),
                summary.headerNames().stream().sorted().toList());
    }

    public ConnectorConfiguration toConfiguration() {
        return new ConnectorConfiguration(name, transport, url, timeout, placeholders(requiredHeaderNames));
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static ConnectorConfigurationDTO fromJson(JsonNode node) {
        try {
            ToolArguments.only(node, "reference", "name", "transport", "url", "timeout", "requiredHeaderNames");
            List<String> names = new ArrayList<>();
            JsonNode headerNames = node.get("requiredHeaderNames");
            if (headerNames != null && !headerNames.isNull()) {
                if (!headerNames.isArray() || headerNames.size() > 16) {
                    throw invalid();
                }
                for (JsonNode headerName : headerNames) {
                    if (!headerName.isTextual()) {
                        throw invalid();
                    }
                    names.add(headerName.textValue());
                }
            }
            Integer timeout =
                    !node.hasNonNull("timeout") ? null : ToolArguments.integer(node, "timeout", 60_000, 1_000, 120_000);
            return new ConnectorConfigurationDTO(
                    ToolArguments.text(node, "reference", 32),
                    ToolArguments.text(node, "name", 64),
                    ToolArguments.text(node, "transport", 4),
                    ToolArguments.text(node, "url", 2048),
                    timeout,
                    names);
        } catch (IllegalArgumentException ignored) {
            throw invalid();
        }
    }

    private static Map<String, String> placeholders(List<String> names) {
        if (names.size() > 16) {
            throw invalid();
        }
        Map<String, String> headers = new LinkedHashMap<>();
        for (String name : names) {
            if (name == null || headers.putIfAbsent(name, "") != null) {
                throw invalid();
            }
        }
        return headers;
    }

    private static ConnectorException invalid() {
        return new ConnectorException(ConnectorException.Code.CONNECTOR_INVALID_CONFIGURATION);
    }

    @Override
    public String toString() {
        return "ConnectorConfigurationDTO[redacted]";
    }
}
