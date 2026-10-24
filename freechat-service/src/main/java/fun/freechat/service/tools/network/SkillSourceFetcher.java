package fun.freechat.service.tools.network;

import fun.freechat.service.skills.SkillException;
import fun.freechat.service.skills.SkillException.Code;
import fun.freechat.service.skills.SkillSourceAnalyzer;
import fun.freechat.service.tools.ToolContext;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public final class SkillSourceFetcher {
    private final NetworkPolicy policy;
    private final NetworkHttp.Transport transport;
    private final SkillSourceAnalyzer analyzer;
    private final String githubToken;

    @Autowired
    public SkillSourceFetcher(
            @Value("${skills.sources.allowed-hosts:github.com,clawhub.ai}") String hosts,
            @Value("${skills.sources.github-token:}") String githubToken,
            SkillSourceAnalyzer analyzer) {
        this(
                Arrays.stream(hosts.split(","))
                        .map(String::trim)
                        .filter(host -> !host.isEmpty())
                        .collect(Collectors.toSet()),
                analyzer,
                null,
                githubToken);
    }

    SkillSourceFetcher(Set<String> hosts, SkillSourceAnalyzer analyzer, NetworkHttp.Transport transport) {
        this(hosts, analyzer, transport, null);
    }

    SkillSourceFetcher(
            Set<String> hosts, SkillSourceAnalyzer analyzer, NetworkHttp.Transport transport, String githubToken) {
        policy = new NetworkPolicy(hosts);
        this.analyzer = analyzer;
        this.transport = transport;
        this.githubToken = githubToken == null || githubToken.isBlank() ? null : githubToken.strip();
    }

    public List<String> allowedHosts() {
        return policy.allowedHosts().stream().sorted().toList();
    }

    public SkillSourceBundle fetch(String ownerId, String input) {
        List<String> candidates = candidates(input);
        ToolContext context = new ToolContext(
                UUID.randomUUID().toString(), ownerId, "skill-installation", "skill-source", Duration.ofSeconds(90));
        int selected;
        try {
            selected = analyzer.select(input, candidates);
            context.check();
        } catch (SkillException failure) {
            throw failure;
        } catch (RuntimeException ignored) {
            throw new SkillException(Code.SKILL_SOURCE_ANALYSIS_UNAVAILABLE);
        }
        if (selected < 0 || selected >= candidates.size()) {
            throw new SkillException(Code.SKILL_SOURCE_AMBIGUOUS);
        }
        URI source = policy.url(candidates.get(selected));
        String host = source.getHost();
        boolean provider = host.equals("github.com") || host.equals("clawhub.ai");
        NetworkPolicy destinations = new NetworkPolicy(Set.of(host.equals("github.com") ? "api.github.com" : host));
        NetworkHttp http = new NetworkHttp(destinations, transport, provider);
        int[] calls = {0};
        long[] bytes = {0};
        SkillSourceReader reader = (uri, media, maxBytes) -> {
            if (++calls[0] > 32 || maxBytes <= 0 || maxBytes > 1024 * 1024)
                throw new SkillException(Code.SKILL_LIMIT_EXCEEDED);
            try {
                context.check();
                var payload = http.exchange(
                        destinations.url(uri.toString()),
                        null,
                        "api.github.com".equals(uri.getHost()) && host.equals("github.com") ? githubToken : null,
                        media,
                        maxBytes,
                        new NetworkHttp.Deadline(context));
                bytes[0] += payload.text().getBytes(StandardCharsets.UTF_8).length;
                if (bytes[0] > 3 * 1024 * 1024) throw new SkillException(Code.SKILL_LIMIT_EXCEEDED);
                return payload.text();
            } catch (SkillException failure) {
                throw failure;
            } catch (NetworkHttp.HttpFailure failure) {
                throw new SkillException(
                        failure.rateLimited() ? Code.SKILL_SOURCE_RATE_LIMITED : Code.SKILL_SOURCE_UNAVAILABLE);
            } catch (Exception ignored) {
                throw new SkillException(Code.SKILL_SOURCE_UNAVAILABLE);
            }
        };
        try {
            SkillSourceBundle bundle =
                    switch (host) {
                        case "github.com" -> new GithubSkillSource().fetch(source, reader);
                        case "clawhub.ai" -> new ClawHubSkillSource().fetch(source, reader);
                        default ->
                            new SkillSourceBundle(
                                    Map.of("SKILL.md", reader.get(source, Set.of(), 32 * 1024)), source, null);
                    };
            context.check();
            return bundle;
        } catch (SkillException failure) {
            throw failure;
        } catch (RuntimeException ignored) {
            throw new SkillException(Code.SKILL_SOURCE_UNAVAILABLE);
        }
    }

    List<String> candidates(String input) {
        if (input == null
                || input.isBlank()
                || input.length() > 2048
                || input.codePoints()
                        .anyMatch(c -> Character.isISOControl(c) && c != '\n' && c != '\r' && c != '\t'
                                || c >= Character.MIN_SURROGATE && c <= Character.MAX_SURROGATE)) {
            throw new SkillException(Code.SKILL_INVALID_FORMAT);
        }
        if (!policy.configured()) throw new SkillException(Code.SKILL_SOURCE_NOT_ALLOWED);
        String hosts = policy.allowedHosts().stream().map(Pattern::quote).collect(Collectors.joining("|"));
        Pattern tokens =
                Pattern.compile("(?i)[a-z][a-z0-9+.-]*://[^\\s<>\"'`\\[\\]]+|(?<![\\p{L}\\p{N}_./@%:-])(?:www\\.)?(?:"
                        + hosts + ")(?=[:/?#\\s]|$)[^\\s<>\"'`\\[\\]]*");
        List<String> found = new java.util.ArrayList<>();
        String stripped = input.strip();
        if (stripped.matches("(?i)(?:[a-z][a-z0-9+.-]*://|(?:www\\.)?(?:" + hosts + ")(?=[:/?#]|$))\\S*")) {
            found.add(stripped);
        } else {
            var matcher = tokens.matcher(input);
            while (matcher.find()) found.add(matcher.group().replaceFirst("[.,;!?)]+$", ""));
        }
        Set<String> result = new LinkedHashSet<>();
        for (String value : found) {
            String token = value;
            if (!token.contains("://")) token = "https://" + token;
            else if (token.regionMatches(true, 0, "http://", 0, 7)) token = "https://" + token.substring(7);
            try {
                URI parsed = URI.create(token);
                if ("www.github.com".equalsIgnoreCase(parsed.getHost())
                        || "www.clawhub.ai".equalsIgnoreCase(parsed.getHost())) {
                    token = token.replaceFirst("(?i)://www\\.", "://");
                }
                int fragment = token.indexOf('#');
                if (fragment >= 0) token = token.substring(0, fragment);
                URI candidate = policy.url(token);
                if (candidate.getHost().equals("github.com") && candidate.getRawQuery() != null) {
                    for (String field : candidate.getRawQuery().split("&")) {
                        String key = field.split("=", 2)[0];
                        if (!key.isEmpty() && !key.equals("tab") && !key.matches("utm_[a-z_]+"))
                            throw new SkillException(Code.SKILL_SOURCE_AMBIGUOUS);
                    }
                    candidate = URI.create(candidate.toASCIIString().split("\\?", 2)[0]);
                }
                result.add(candidate.toASCIIString());
            } catch (IllegalArgumentException ignored) {
                throw new SkillException(Code.SKILL_SOURCE_NOT_ALLOWED);
            }
            if (result.size() > 16) throw new SkillException(Code.SKILL_LIMIT_EXCEEDED);
        }
        if (result.isEmpty()) throw new SkillException(Code.SKILL_SOURCE_NOT_ALLOWED);
        return List.copyOf(result);
    }
}
