package fun.freechat.service.tools;

import java.util.Set;

@FunctionalInterface
public interface ToolPolicySource {
    Policy load(String chatId);

    record Policy(
            String userId, String characterUid, String backendId, Set<String> enabled, boolean memory, boolean album) {
        public Policy {
            enabled = Set.copyOf(enabled);
        }

        public boolean permits(String tool) {
            return enabled.contains(tool)
                    && switch (tool) {
                        case "search_memory", "read_memory" -> memory;
                        case "find_image" -> album;
                        default -> true;
                    };
        }

        public boolean matches(ToolContext context) {
            return context.userId().equals(userId)
                    && context.characterUid().equals(characterUid)
                    && context.backendId().equals(backendId);
        }
    }
}
