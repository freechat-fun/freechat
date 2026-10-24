package fun.freechat.service.connectors;

import java.time.Instant;
import java.util.List;
import java.util.Set;

public interface ConnectorService {
    record Summary(
            String connectorId,
            String revision,
            String name,
            String transport,
            String url,
            Integer timeout,
            boolean active,
            Set<String> headerNames,
            Instant modified) {
        public Summary {
            headerNames = Set.copyOf(headerNames);
        }

        @Override
        public String toString() {
            return "ConnectorSummary[redacted]";
        }
    }

    record Resolved(
            String connectorId, String ownerId, String revision, ConnectorConfiguration configuration, boolean active) {
        @Override
        public String toString() {
            return "ResolvedConnector[redacted]";
        }
    }

    List<Summary> list(String ownerId);

    Summary get(String ownerId, String connectorId);

    Resolved resolve(String ownerId, String connectorId);

    Summary create(String ownerId, ConnectorConfiguration configuration, boolean active);

    Summary update(
            String ownerId,
            String connectorId,
            String expectedRevision,
            ConnectorConfiguration configuration,
            Set<String> keepHeaderNames,
            boolean active);

    void delete(String ownerId, String connectorId, String expectedRevision);
}
