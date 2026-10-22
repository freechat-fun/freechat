package fun.freechat.service.tools;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.LinkedHashSet;
import java.util.Set;

public record CharacterToolConfiguration(Set<String> enabled) {
    public static final Set<String> DEFAULTS =
            Set.of("get_current_time", "calculate", "roll_dice", "search_memory", "read_memory", "find_image");

    public CharacterToolConfiguration {
        enabled = Set.copyOf(enabled);
    }

    public static CharacterToolConfiguration parse(String ext) {
        if (ext == null || ext.isBlank()) {
            return new CharacterToolConfiguration(DEFAULTS);
        }
        JsonNode root = ToolArguments.parse(ext);
        JsonNode tools = root.get("tools");
        if (tools == null || tools.isNull()) {
            return new CharacterToolConfiguration(DEFAULTS);
        }
        ToolArguments.only(tools, "enabled");
        JsonNode selection = tools.get("enabled");
        if (selection == null || selection.isNull()) {
            return new CharacterToolConfiguration(DEFAULTS);
        }
        if (!selection.isArray() || selection.size() > 64) {
            throw new IllegalArgumentException("Invalid character tool configuration");
        }
        Set<String> names = new LinkedHashSet<>();
        for (JsonNode name : selection) {
            if (!name.isTextual()
                    || !name.textValue().matches("[A-Za-z][A-Za-z0-9_]{0,63}")
                    || !names.add(name.textValue())) {
                throw new IllegalArgumentException("Invalid character tool configuration");
            }
        }
        return new CharacterToolConfiguration(names);
    }
}
