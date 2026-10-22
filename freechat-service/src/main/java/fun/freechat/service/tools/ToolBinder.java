package fun.freechat.service.tools;

import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.service.tool.ToolExecutor;
import java.util.Map;

@FunctionalInterface
public interface ToolBinder {
    Map<ToolSpecification, ToolExecutor> bind(Map<ToolSpecification, ToolExecutor> candidates);
}
