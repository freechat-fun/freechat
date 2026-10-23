package fun.freechat.service.tools.network;

import fun.freechat.service.skills.SkillException;
import fun.freechat.service.skills.SkillException.Code;
import fun.freechat.service.tools.ToolContext;
import java.net.URI;
import java.time.Duration;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public final class SkillSourceFetcher {
    private final NetworkPolicy policy;
    private final NetworkHttp http;

    @Autowired
    public SkillSourceFetcher(@Value("${skills.sources.allowed-hosts:}") String hosts) {
        this(
                Arrays.stream(hosts.split(","))
                        .map(String::trim)
                        .filter(host -> !host.isEmpty())
                        .collect(Collectors.toSet()),
                null);
    }

    SkillSourceFetcher(Set<String> hosts, NetworkHttp.Transport transport) {
        policy = new NetworkPolicy(hosts);
        http = new NetworkHttp(policy, transport);
    }

    public String fetch(String ownerId, String source) {
        URI destination;
        try {
            destination = policy.url(source);
        } catch (IllegalArgumentException ignored) {
            throw new SkillException(Code.SKILL_SOURCE_NOT_ALLOWED);
        }
        ToolContext context = new ToolContext(
                UUID.randomUUID().toString(), ownerId, "skill-installation", "skill-source", Duration.ofSeconds(15));
        try {
            return http.exchange(
                            destination,
                            null,
                            null,
                            Set.of("text/plain", "text/markdown"),
                            32 * 1024,
                            new NetworkHttp.Deadline(context))
                    .text();
        } catch (Exception ignored) {
            throw new SkillException(Code.SKILL_SOURCE_UNAVAILABLE);
        }
    }
}
