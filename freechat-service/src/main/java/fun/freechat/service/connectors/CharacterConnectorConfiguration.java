package fun.freechat.service.connectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fun.freechat.service.tools.ToolArguments;
import java.util.LinkedHashSet;
import java.util.Set;

/** Owner-scoped registry references only; never contains connector definitions or credentials. */
public record CharacterConnectorConfiguration(Set<String> enabled) {
    public static final int MAX_ENABLED = 8;

    public CharacterConnectorConfiguration {
        if (enabled == null
                || enabled.size() > MAX_ENABLED
                || enabled.stream().anyMatch(id -> id == null || !id.matches("[0-9a-f]{32}"))) {
            throw invalid();
        }
        enabled = Set.copyOf(enabled);
    }

    public static CharacterConnectorConfiguration parse(String ext) {
        if (ext == null || ext.isBlank()) {
            return new CharacterConnectorConfiguration(Set.of());
        }
        try {
            JsonNode connectors = ToolArguments.parse(ext).get("connectors");
            if (connectors == null || connectors.isNull()) {
                return new CharacterConnectorConfiguration(Set.of());
            }
            ToolArguments.only(connectors, "enabled");
            JsonNode selection = connectors.get("enabled");
            if (selection == null || selection.isNull()) {
                return new CharacterConnectorConfiguration(Set.of());
            }
            if (!selection.isArray() || selection.size() > MAX_ENABLED) {
                throw invalid();
            }
            Set<String> ids = new LinkedHashSet<>();
            for (JsonNode value : selection) {
                if (!value.isTextual() || !value.textValue().matches("[0-9a-f]{32}") || !ids.add(value.textValue())) {
                    throw invalid();
                }
            }
            return new CharacterConnectorConfiguration(ids);
        } catch (IllegalArgumentException ignored) {
            throw invalid();
        }
    }

    public static String withoutConnectors(String ext) {
        if (ext == null || ext.isBlank()) {
            return ext;
        }
        try {
            ObjectNode root = (ObjectNode) ToolArguments.parse(ext);
            if (root.remove("connectors") == null) {
                return ext;
            }
            return ToolArguments.result(root);
        } catch (IllegalArgumentException ignored) {
            throw invalid();
        }
    }

    public static String draftExt(String draft) {
        if (draft == null || draft.isBlank()) {
            return null;
        }
        try {
            JsonNode ext = ToolArguments.parse(draft).get("ext");
            if (ext == null || ext.isNull()) {
                return null;
            }
            if (!ext.isTextual()) {
                throw invalid();
            }
            return ext.textValue();
        } catch (IllegalArgumentException ignored) {
            throw invalid();
        }
    }

    public static String withoutDraftConnectors(String draft) {
        String ext = draftExt(draft);
        if (ext == null) {
            return draft;
        }
        ObjectNode root = (ObjectNode) ToolArguments.parse(draft);
        root.put("ext", withoutConnectors(ext));
        return ToolArguments.result(root);
    }

    public static String withEnabled(String ext, Set<String> enabled) {
        CharacterConnectorConfiguration configuration = new CharacterConnectorConfiguration(enabled);
        String clean = withoutConnectors(ext);
        if (configuration.enabled().isEmpty()) {
            return clean;
        }
        ObjectNode root = clean == null || clean.isBlank()
                ? ToolArguments.JSON.createObjectNode()
                : (ObjectNode) ToolArguments.parse(clean);
        var selection = root.putObject("connectors").putArray("enabled");
        configuration.enabled().stream().sorted().forEach(selection::add);
        return ToolArguments.result(root);
    }

    private static ConnectorException invalid() {
        return new ConnectorException(ConnectorException.Code.CONNECTOR_INVALID_CONFIGURATION);
    }
}
