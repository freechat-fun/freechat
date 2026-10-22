package fun.freechat.service.tools.network;

import java.net.IDN;
import java.net.URI;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/** Exact server-owned HTTPS destinations; validation does not resolve hostnames. */
final class NetworkPolicy {
    private final Set<String> hosts;

    NetworkPolicy(Set<String> allowedHosts) {
        Set<String> normalized = new HashSet<>();
        if (allowedHosts != null) {
            if (allowedHosts.size() > 128) {
                throw new IllegalArgumentException("Too many allowed network hosts");
            }
            for (String host : allowedHosts) {
                normalized.add(normalizeHost(host));
            }
        }
        hosts = Set.copyOf(normalized);
    }

    boolean configured() {
        return !hosts.isEmpty();
    }

    URI url(String value) {
        URI url = httpsUrl(value);
        if (!hosts.contains(url.getHost())) {
            throw new IllegalArgumentException("Network destination is not allowed");
        }
        return url;
    }

    static URI httpsUrl(String value) {
        try {
            if (value == null
                    || value.length() > 2048
                    || value.indexOf('\\') >= 0
                    || value.codePoints()
                            .anyMatch(c -> Character.isISOControl(c)
                                    || Character.isWhitespace(c)
                                    || c >= Character.MIN_SURROGATE && c <= Character.MAX_SURROGATE)) {
                throw new IllegalArgumentException();
            }
            URI uri = new URI(value);
            if (!"https".equalsIgnoreCase(uri.getScheme())
                    || uri.getRawUserInfo() != null
                    || uri.getRawFragment() != null
                    || uri.getHost() == null
                    || (uri.getPort() != -1 && uri.getPort() != 443)) {
                throw new IllegalArgumentException();
            }
            String host = normalizeHost(uri.getHost());
            // Reject malformed/empty ports, encoded authorities and alternate numeric host spellings.
            String authority = uri.getRawAuthority();
            if (!authority.equalsIgnoreCase(uri.getHost()) && !authority.equalsIgnoreCase(uri.getHost() + ":443")) {
                throw new IllegalArgumentException();
            }
            // Build from raw components: multi-argument URI constructors would double-encode '%' escapes.
            URI normalized = URI.create(
                    "https://" + host + uri.getRawPath() + (uri.getRawQuery() == null ? "" : "?" + uri.getRawQuery()));
            String ascii = normalized.toASCIIString();
            if (ascii.length() > 2048) {
                throw new IllegalArgumentException();
            }
            return URI.create(ascii);
        } catch (Exception ignored) {
            throw new IllegalArgumentException("Invalid HTTPS destination");
        }
    }

    private static String normalizeHost(String value) {
        if (value == null || !value.equals(value.trim()) || value.length() > 254) {
            throw new IllegalArgumentException("Invalid allowed host");
        }
        String host = value.endsWith(".") ? value.substring(0, value.length() - 1) : value;
        host = IDN.toASCII(host, IDN.USE_STD3_ASCII_RULES).toLowerCase(Locale.ROOT);
        // Only DNS names, never IP literals (including shorthand/hex/octal IPv4).
        if (host.length() > 253
                || !host.contains(".")
                || !host.matches("[a-z0-9.-]+")
                || !host.substring(host.lastIndexOf('.') + 1).matches("[a-z][a-z0-9-]*")) {
            throw new IllegalArgumentException("Invalid allowed host");
        }
        for (String label : host.split("\\.", -1)) {
            if (label.isEmpty() || label.length() > 63 || label.startsWith("-") || label.endsWith("-")) {
                throw new IllegalArgumentException("Invalid allowed host");
            }
        }
        return host;
    }
}
