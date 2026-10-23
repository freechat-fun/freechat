package fun.freechat.service.skills;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public record SkillBundle(String name, String description, Map<String, String> files, String digest) {
    public SkillBundle {
        files = java.util.Collections.unmodifiableMap(new TreeMap<>(files));
    }

    public Set<String> tags() {
        return Set.of("user", "prompt-only");
    }
}
