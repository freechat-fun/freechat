package fun.freechat.service.tools;

import com.fasterxml.jackson.databind.JsonNode;

public interface BuiltinTool {
    ToolDefinition definition();

    String execute(JsonNode arguments, ToolContext context) throws Exception;
}
