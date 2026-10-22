package fun.freechat.service.tools;

import dev.langchain4j.agent.tool.ToolSpecification;
import java.util.Objects;
import java.util.Set;

public record ToolDefinition(
        ToolSpecification specification, Set<String> tags, Boundary boundary, boolean sideEffects) {
    public enum Boundary {
        IN_PROCESS,
        NETWORK,
        ISOLATED
    }

    public ToolDefinition {
        Objects.requireNonNull(specification);
        Objects.requireNonNull(boundary);
        if (specification.name() == null || !specification.name().matches("[A-Za-z][A-Za-z0-9_]{0,63}")) {
            throw new IllegalArgumentException("Invalid tool name");
        }
        tags = Set.copyOf(tags);
    }

    public String name() {
        return specification.name();
    }
}
