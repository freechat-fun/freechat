package fun.freechat.service.connectors;

import static fun.freechat.service.connectors.ConnectorException.Code.CONNECTOR_INVALID_CONFIGURATION;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/** Pure bounded syntax validation; intentionally performs no DNS, network, or domain allowlist checks. */
final class ConnectorConfigurationValidator {
    static final int MAX_HEADERS = 16;
    static final int MAX_HEADER_BYTES = 16 * 1024;
    private static final Pattern TOKEN = Pattern.compile("[!#$%&'*+.^_`|~0-9A-Za-z-]+");
    private static final Set<String> RESERVED = Set.of(
            "host",
            "accept",
            "content-type",
            "content-length",
            "connection",
            "transfer-encoding",
            "upgrade",
            "origin",
            "referer",
            "forwarded",
            "last-event-id",
            "accept-encoding",
            "keep-alive",
            "te",
            "trailer",
            "expect",
            "via",
            "http2-settings");

    private ConnectorConfigurationValidator() {}

    static Map<String, String> validate(
            String name, String transport, String url, int timeout, Map<String, String> headers) {
        if (name == null
                || name.isBlank()
                || name.length() > 64
                || !name.codePoints().allMatch(code -> switch (Character.getType(code)) {
                    case Character.CONTROL,
                            Character.FORMAT,
                            Character.SURROGATE,
                            Character.UNASSIGNED,
                            Character.LINE_SEPARATOR,
                            Character.PARAGRAPH_SEPARATOR -> false;
                    default -> true;
                })) {
            throw invalid();
        }
        if (!("http".equals(transport) || "sse".equals(transport)) || timeout < 1_000 || timeout > 120_000) {
            throw invalid();
        }
        URI endpoint = endpoint(url);
        if (headers == null || headers.isEmpty()) {
            return Map.of();
        }
        if (headers.size() > MAX_HEADERS || !"https".equalsIgnoreCase(endpoint.getScheme())) {
            throw invalid();
        }
        Map<String, String> copy = new LinkedHashMap<>();
        Set<String> names = new HashSet<>();
        int bytes = 0;
        for (var entry : headers.entrySet()) {
            if (copy.size() >= MAX_HEADERS) {
                throw invalid();
            }
            String key = headerName(entry.getKey());
            String value = entry.getValue();
            if (!names.add(key)
                    || value == null
                    || value.length() > MAX_HEADER_BYTES
                    || !value.chars().allMatch(ch -> ch >= 0x20 && ch <= 0x7e)) {
                throw invalid();
            }
            bytes += entry.getKey().length() + value.length();
            if (bytes > MAX_HEADER_BYTES) {
                throw invalid();
            }
            copy.put(entry.getKey(), value);
        }
        return Collections.unmodifiableMap(copy);
    }

    static String headerName(String name) {
        if (name == null
                || name.isEmpty()
                || name.length() > 128
                || name.equals("__proto__")
                || !TOKEN.matcher(name).matches()) {
            throw invalid();
        }
        String lower = name.toLowerCase(Locale.ROOT);
        if (RESERVED.contains(lower)
                || lower.startsWith("proxy-")
                || lower.startsWith("x-forwarded-")
                || lower.startsWith("mcp-")) {
            throw invalid();
        }
        return lower;
    }

    static URI endpoint(String url) {
        if (url == null || url.isEmpty() || url.length() > 2048 || unsafeCharacters(url)) {
            throw invalid();
        }
        try {
            URI uri = new URI(url).parseServerAuthority();
            if (!("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme()))
                    || uri.isOpaque()
                    || uri.getHost() == null
                    || uri.getRawUserInfo() != null
                    || uri.getRawFragment() != null
                    || uri.getRawQuery() != null
                    || uri.getRawAuthority() == null
                    || uri.getRawAuthority().contains("%")
                    || uri.getRawAuthority().endsWith(":")
                    || uri.getPort() == 0
                    || uri.getPort() > 65535
                    || unsafeCharacters(uri.getPath())) {
                throw invalid();
            }
            return uri;
        } catch (URISyntaxException ignored) {
            throw invalid();
        }
    }

    static boolean sameOrigin(String first, String second) {
        URI a = endpoint(first);
        URI b = endpoint(second);
        return a.getScheme().equalsIgnoreCase(b.getScheme())
                && a.getHost().equalsIgnoreCase(b.getHost())
                && port(a) == port(b);
    }

    private static int port(URI uri) {
        return uri.getPort() == -1 ? ("https".equalsIgnoreCase(uri.getScheme()) ? 443 : 80) : uri.getPort();
    }

    private static boolean unsafeCharacters(String text) {
        return text != null
                && text.codePoints()
                        .anyMatch(ch -> ch == '\\'
                                || Character.isISOControl(ch)
                                || Character.isWhitespace(ch)
                                || Character.isSpaceChar(ch)
                                || Character.getType(ch) == Character.SURROGATE
                                || Character.getType(ch) == Character.FORMAT);
    }

    static ConnectorException invalid() {
        return new ConnectorException(CONNECTOR_INVALID_CONFIGURATION);
    }
}
