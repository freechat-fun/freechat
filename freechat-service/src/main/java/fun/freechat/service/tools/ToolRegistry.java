package fun.freechat.service.tools;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class ToolRegistry {
    private volatile Map<String, ToolDefinition> definitions = Map.of();

    public ToolRegistry(Collection<ToolDefinition> tools) {
        tools.forEach(this::register);
    }

    public synchronized void register(ToolDefinition definition) {
        Map<String, ToolDefinition> updated = new LinkedHashMap<>(definitions);
        if (updated.putIfAbsent(definition.name(), definition) != null) {
            throw new IllegalArgumentException("Duplicate tool name");
        }
        definitions = Map.copyOf(updated);
    }

    public synchronized boolean unregister(String name) {
        Map<String, ToolDefinition> updated = new LinkedHashMap<>(definitions);
        boolean removed = updated.remove(name) != null;
        definitions = Map.copyOf(updated);
        return removed;
    }

    public Optional<ToolDefinition> find(String name) {
        return Optional.ofNullable(definitions.get(name));
    }

    public List<ToolDefinition> list() {
        return list(Set.of());
    }

    public List<ToolDefinition> list(Set<String> tags) {
        return definitions.values().stream()
                .filter(tool -> tool.tags().containsAll(tags))
                .sorted(java.util.Comparator.comparing(ToolDefinition::name))
                .toList();
    }

    public void validateConfiguration(String ext) {
        CharacterToolConfiguration configuration = CharacterToolConfiguration.parse(ext);
        if (ext == null
                || ext.isBlank()
                || !ToolArguments.parse(ext).path("tools").path("enabled").isArray()) {
            return;
        }
        for (String name : configuration.enabled()) {
            if (!definitions.containsKey(name)) {
                throw new IllegalArgumentException("Unknown character tool");
            }
        }
    }
}
