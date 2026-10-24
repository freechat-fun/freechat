package fun.freechat.service.tools.network;

import java.net.URI;
import java.util.Set;

@FunctionalInterface
interface SkillSourceReader {
    String get(URI uri, Set<String> mediaTypes, int maxBytes);
}
