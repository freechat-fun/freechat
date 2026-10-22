package fun.freechat.service.tools;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public final class InProcessToolRuntime implements ToolRuntime {
    private final Map<String, BuiltinTool> tools;

    public InProcessToolRuntime(Collection<BuiltinTool> tools) {
        Map<String, BuiltinTool> handlers = new LinkedHashMap<>();
        for (BuiltinTool tool : tools) {
            if (tool.definition().boundary() == ToolDefinition.Boundary.ISOLATED
                    || handlers.putIfAbsent(tool.definition().name(), tool) != null) {
                throw new IllegalArgumentException("Invalid in-process tool registration");
            }
        }
        this.tools = Map.copyOf(handlers);
    }

    @Override
    public String id() {
        return "in-process";
    }

    @Override
    public boolean isolated() {
        return false;
    }

    @Override
    public boolean supports(ToolDefinition tool, ToolContext context) {
        BuiltinTool handler = tools.get(tool.name());
        return handler != null
                && handler.definition().equals(tool)
                && tool.boundary() != ToolDefinition.Boundary.ISOLATED;
    }

    @Override
    public String execute(ToolDefinition tool, JsonNode arguments, ToolContext context) throws Exception {
        if (!supports(tool, context)) {
            throw new IllegalStateException("Tool runtime unavailable");
        }
        context.check();
        return tools.get(tool.name()).execute(arguments, context);
    }
}
