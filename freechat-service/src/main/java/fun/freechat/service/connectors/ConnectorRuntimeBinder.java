package fun.freechat.service.connectors;

import fun.freechat.service.tools.ToolContext;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class ConnectorRuntimeBinder {
    private final ConnectorPolicySource policies;
    private final ConnectorService connectors;
    private final ConnectorClientFactory clients;

    public ConnectorBinder binder(String chatId, String userId, String characterUid, String backendId) {
        return () -> bind(new ToolContext(chatId, userId, characterUid, backendId, Duration.ofMinutes(10)));
    }

    ConnectorInvocation bind(ToolContext context) {
        if (context.chatId().endsWith("-assist")) return null;
        var policy = policies.load(context);
        if (!policy.permitted() || policy.enabled().isEmpty()) return null;
        Map<String, String> revisions = connectors.list(policy.ownerId()).stream()
                .filter(connector -> connector.active() && policy.enabled().contains(connector.connectorId()))
                .sorted(java.util.Comparator.comparing(ConnectorService.Summary::connectorId))
                .collect(Collectors.toMap(
                        ConnectorService.Summary::connectorId,
                        ConnectorService.Summary::revision,
                        (first, second) -> {
                            throw unavailable();
                        },
                        LinkedHashMap::new));
        if (revisions.isEmpty()) return null;
        ConnectorInvocation invocation = new ConnectorInvocation(context, () -> {
            if (!policy.equals(policies.load(context))) throw unavailable();
            Map<String, String> current = connectors.list(policy.ownerId()).stream()
                    .filter(connector -> connector.active() && revisions.containsKey(connector.connectorId()))
                    .collect(Collectors.toMap(
                            ConnectorService.Summary::connectorId, ConnectorService.Summary::revision));
            if (!revisions.equals(current)) throw unavailable();
        });
        try {
            for (var entry : revisions.entrySet()) {
                var connector = connectors.resolve(policy.ownerId(), entry.getKey());
                if (!connector.active()
                        || !policy.ownerId().equals(connector.ownerId())
                        || !entry.getKey().equals(connector.connectorId())
                        || !entry.getValue().equals(connector.revision())) throw unavailable();
                invocation.add(connector, clients);
            }
            invocation.check();
            return invocation;
        } catch (RuntimeException | Error failure) {
            invocation.close();
            throw unavailable();
        }
    }

    private static IllegalStateException unavailable() {
        return new IllegalStateException("Chat connector authorization unavailable");
    }
}
