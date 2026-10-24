package fun.freechat.service.connectors;

import dev.langchain4j.agent.tool.ToolSpecification;
import java.time.Duration;
import java.util.List;

public interface ConnectorClientFactory {
    Connection open(String ownerId, ConnectorConfiguration configuration, Duration lifetime, Runnable authorize);

    interface Connection extends AutoCloseable {
        List<ToolSpecification> listTools();

        String execute(String name, String arguments);

        @Override
        void close();
    }
}
