package fun.freechat.service.tools.network;

import java.net.URI;
import java.util.Map;
import java.util.TreeMap;

public record SkillSourceBundle(Map<String, String> files, URI source, String revision) {
    public SkillSourceBundle {
        files = java.util.Collections.unmodifiableMap(new TreeMap<>(files));
    }
}
