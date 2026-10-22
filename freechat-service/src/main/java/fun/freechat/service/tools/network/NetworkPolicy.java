package fun.freechat.service.tools.network;

import java.net.IDN;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import okhttp3.Dns;
import okhttp3.HttpUrl;

/** Exact server-owned destinations; DNS is checked again at the actual connection boundary. */
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

    HttpUrl url(String value) {
        HttpUrl url = httpsUrl(value);
        if (!hosts.contains(url.host())) {
            throw new IllegalArgumentException("Network destination is not allowed");
        }
        return url;
    }

    static HttpUrl httpsUrl(String value) {
        try {
            if (value == null
                    || value.length() > 2048
                    || value.indexOf('\\') >= 0
                    || value.codePoints().anyMatch(c -> Character.isISOControl(c) || Character.isWhitespace(c))) {
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
            HttpUrl result = HttpUrl.get(value).newBuilder().host(host).build();
            if (!result.isHttps()
                    || result.port() != 443
                    || !result.username().isEmpty()
                    || !result.password().isEmpty()
                    || result.fragment() != null
                    || result.toString().length() > 2048) {
                throw new IllegalArgumentException();
            }
            return result;
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

    Dns dns(Dns resolver) {
        return hostname -> {
            if (!hosts.contains(hostname)) {
                throw new UnknownHostException("Network destination is not allowed");
            }
            List<InetAddress> addresses = resolver.lookup(hostname);
            if (addresses == null
                    || addresses.isEmpty()
                    || addresses.size() > 16
                    || addresses.stream().anyMatch(address -> !global(address))) {
                // Reject the entire mixed answer, not just its private subset.
                throw new UnknownHostException("Network address is not globally routable");
            }
            return List.copyOf(addresses);
        };
    }

    static boolean global(InetAddress address) {
        if (address == null
                || address.isAnyLocalAddress()
                || address.isLoopbackAddress()
                || address.isLinkLocalAddress()
                || address.isSiteLocalAddress()
                || address.isMulticastAddress()) {
            return false;
        }
        byte[] bytes = address.getAddress();
        if (bytes.length == 4) {
            long ip = 0;
            for (byte b : bytes) {
                ip = (ip << 8) | (b & 255);
            }
            return !(v4(ip, 0x00000000L, 8)
                    || v4(ip, 0x0a000000L, 8)
                    || v4(ip, 0x64400000L, 10)
                    || v4(ip, 0x7f000000L, 8)
                    || v4(ip, 0xa9fe0000L, 16)
                    || v4(ip, 0xac100000L, 12)
                    || v4(ip, 0xc0000000L, 24)
                    || v4(ip, 0xc0000200L, 24)
                    || v4(ip, 0xc01fc400L, 24)
                    || v4(ip, 0xc034c100L, 24)
                    || v4(ip, 0xc0586300L, 24)
                    || v4(ip, 0xc0a80000L, 16)
                    || v4(ip, 0xc0af3000L, 24)
                    || v4(ip, 0xc6120000L, 15)
                    || v4(ip, 0xc6336400L, 24)
                    || v4(ip, 0xcb007100L, 24)
                    || v4(ip, 0xe0000000L, 3));
        }
        if (bytes.length != 16 || (bytes[0] & 0xe0) != 0x20) {
            // Positive global-unicast allocation: excludes mapped/compatible IPv4, NAT64,
            // unspecified, ULA, link/site local, multicast, discard-only and future allocations.
            return false;
        }
        int first = ((bytes[0] & 255) << 8) | (bytes[1] & 255);
        int second = ((bytes[2] & 255) << 8) | (bytes[3] & 255);
        return !(first == 0x2001 && (second < 0x0200 || second == 0x0db8)
                || first == 0x2002 // 6to4 can embed non-global IPv4.
                || first == 0x3ffe // Retired 6bone allocation.
                || first == 0x3fff && (second & 0xf000) == 0 // Documentation.
                || first == 0x2620 && second == 0x004f && (bytes[4] & 255) == 0x80 && bytes[5] == 0);
    }

    private static boolean v4(long address, long network, int bits) {
        long mask = (0xffffffffL << (32 - bits)) & 0xffffffffL;
        return (address & mask) == network;
    }
}
