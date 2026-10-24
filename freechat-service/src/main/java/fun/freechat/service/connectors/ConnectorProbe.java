package fun.freechat.service.connectors;

import dev.langchain4j.agent.tool.ToolSpecification;
import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConnectorProbe {
    private final ConnectorService connectors;
    private final ConnectorClientFactory clients;

    public List<ToolSpecification> tools(String ownerId, String connectorId, String expectedRevision) {
        var connector = connectors.resolve(ownerId, connectorId);
        if (!connector.revision().equals(expectedRevision)) throw conflict();
        Runnable authorize = () -> {
            if (!connectors.get(ownerId, connectorId).revision().equals(expectedRevision)) throw conflict();
        };
        try (var client = clients.open(ownerId, connector.configuration(), Duration.ofMinutes(2), authorize)) {
            var tools = client.listTools();
            authorize.run();
            return tools;
        }
    }

    private static ConnectorException conflict() {
        return new ConnectorException(ConnectorException.Code.CONNECTOR_VERSION_CONFLICT);
    }
}
