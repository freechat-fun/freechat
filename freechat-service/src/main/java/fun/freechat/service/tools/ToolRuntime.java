package fun.freechat.service.tools;

import com.fasterxml.jackson.databind.JsonNode;

public interface ToolRuntime {
    String id();

    boolean isolated();

    boolean supports(ToolDefinition tool, ToolContext context);

    String execute(ToolDefinition tool, JsonNode arguments, ToolContext context) throws Exception;
}
