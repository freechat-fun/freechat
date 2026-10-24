package fun.freechat.service.connectors;

import fun.freechat.service.tools.ToolContext;
import java.util.Set;

@FunctionalInterface
public interface ConnectorPolicySource {
    record Policy(String ownerId, Set<String> enabled, boolean permitted) {
        public Policy {
            enabled = Set.copyOf(enabled);
        }
    }

    Policy load(ToolContext context);
}
