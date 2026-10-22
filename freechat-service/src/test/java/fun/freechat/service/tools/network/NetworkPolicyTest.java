package fun.freechat.service.tools.network;

import static org.junit.jupiter.api.Assertions.*;

import java.net.Proxy;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

@Timeout(5)
class NetworkPolicyTest {
    @ParameterizedTest
    @ValueSource(
            strings = {
                "",
                "http://example.com",
                "ftp://example.com",
                "file:///etc/passwd",
                "//example.com",
                "https:example.com",
                "https:///example.com",
                "https://user@example.com",
                "https://user:password@example.com",
                "https://@example.com",
                "https://example.com/#",
                "https://example.com/#fragment",
                "https://example.com:80/",
                "https://example.com:444/",
                "https://example.com:/",
                "https://example.com:0443/",
                "https://example.com:+443/",
                "https://example.com:443:443/",
                "https://example.com.evil.com/",
                "https://sub.example.com/",
                "https://example.com../",
                "https://example.com%00.evil.com/",
                "https://example%2ecom/",
                "https://%65xample.com/",
                "https://example.com%3a443/",
                "https://example.com\\@evil.com/",
                "https://example.com/a\\b",
                "https://example.com/\r\nHeader: x",
                "https://example.com/\tpath",
                "https://example.com/?q=\u007f",
                "https://example.com/\u0085",
                "https://example.com/a b",
                "https://example.com/\u2003",
                " https://example.com/",
                "https://example.com/ ",
                "https://example.com/%",
                "https://example.com/%GG",
                "https://example.com/?q=%2",
                "https://127.0.0.1/",
                "https://2130706433/",
                "https://127.1/",
                "https://0177.0.0.1/",
                "https://0x7f000001/",
                "https://0x7f.0x0.0x0.0x1/",
                "https://[::1]/",
                "https://[::ffff:8.8.8.8]/",
                "https://[fe80::1%25lo0]/",
                "https://8.8.8.8/"
            })
    void rejectsAmbiguousOrUnapprovedDestinations(String url) {
        assertThrows(IllegalArgumentException.class, () -> new NetworkPolicy(Set.of("example.com")).url(url));
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "",
                "localhost",
                "*.example.com",
                "https://example.com",
                "example.com:443",
                "example.com/path",
                "user@example.com",
                " example.com",
                "example.com ",
                "example..com",
                "example.com..",
                ".example.com",
                "-example.com",
                "example-.com",
                "example.c-m-",
                "example.123",
                "127.0.0.1",
                "8.8.8.8",
                "2130706433",
                "127.1",
                "0177.0.0.1",
                "0x7f000001",
                "0x7f.0x0.0x0.0x1",
                "[::1]",
                "::1"
            })
    void rejectsInvalidAllowedHosts(String host) {
        assertThrows(IllegalArgumentException.class, () -> new NetworkPolicy(Set.of(host)));
    }

    @Test
    void normalizesExactHostAllowlistWithoutWildcardMatching() {
        NetworkPolicy policy = new NetworkPolicy(Set.of("EXAMPLE.COM.", "bücher.de"));
        assertTrue(policy.configured());
        URI normalized = policy.url("HTTPS://EXAMPLE.COM.:443/a?q=1");
        assertEquals(URI.create("https://example.com/a?q=1"), normalized);
        assertEquals("https", normalized.getScheme());
        assertEquals("example.com", normalized.getHost());
        assertEquals(-1, normalized.getPort());
        assertEquals("xn--bcher-kva.de", policy.url("https://xn--bcher-kva.de/").getHost());
        assertThrows(IllegalArgumentException.class, () -> policy.url("https://bücher.de/"));
        assertThrows(IllegalArgumentException.class, () -> policy.url("https://other.example.com/"));
        assertThrows(IllegalArgumentException.class, () -> policy.url("https://example.com.evil.com/"));
        assertFalse(new NetworkPolicy(null).configured());
        assertFalse(new NetworkPolicy(Set.of()).configured());
        assertThrows(IllegalArgumentException.class, () -> new NetworkPolicy(Set.of()).url("https://example.com/"));
    }

    @ParameterizedTest
    @CsvSource(
            delimiter = '|',
            value = {
                "HTTPS://EXAMPLE.COM.:443/a?q=1|https://example.com/a?q=1",
                "https://EXAMPLE.COM:443|https://example.com",
                "https://EXAMPLE.COM.:443?|https://example.com?",
                "https://EXAMPLE.COM.:443/a?|https://example.com/a?",
                "https://EXAMPLE.COM./a//b/../c/./|https://example.com/a//b/../c/./",
                "https://EXAMPLE.COM./%2e%2E/%2f/%25?q=a+b%20c&x=%26%3D%23|https://example.com/%2e%2E/%2f/%25?q=a+b%20c&x=%26%3D%23",
                "https://EXAMPLE.COM.:443/%e4%b9%a6?q=%E4%B8%AD&x=%252F|https://example.com/%e4%b9%a6?q=%E4%B8%AD&x=%252F",
                "https://EXAMPLE.COM.:443/%E4%B9%A6/书?q=中文&escaped=%2f%25|https://example.com/%E4%B9%A6/%E4%B9%A6?q=%E4%B8%AD%E6%96%87&escaped=%2f%25",
                "https://EXAMPLE.COM./\uD834\uDD1E?q=é|https://example.com/%F0%9D%84%9E?q=%C3%A9"
            })
    void preservesRawComponentsAndEncodesUnicodeWithoutDoubleEncoding(String input, String expected) {
        NetworkPolicy policy = new NetworkPolicy(Set.of("example.com"));
        URI uri = policy.url(input);
        assertEquals(expected, uri.toString());
        assertEquals(expected, uri.toASCIIString());
        assertEquals(URI.create(expected).getRawPath(), uri.getRawPath());
        assertEquals(URI.create(expected).getRawQuery(), uri.getRawQuery());
        assertEquals(uri, policy.url(uri.toASCIIString()));
        assertEquals(uri, HttpRequest.newBuilder(uri).build().uri());
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "https://example.com/\uD800",
                "https://example.com/\uDC00",
                "https://example.com/\uD800x",
                "https://example.com/?q=\uD800",
                "https://example.com/?q=\uDC00"
            })
    void rejectsMalformedUnicodeIncludingProviderSourceUrls(String url) {
        assertThrows(IllegalArgumentException.class, () -> NetworkPolicy.httpsUrl(url));
    }

    @Test
    void capsInputAndAsciiDestinationLengths() {
        NetworkPolicy policy = new NetworkPolicy(Set.of("example.com"));
        String prefix = "https://example.com/";
        String limit = prefix + "a".repeat(2048 - prefix.length());
        assertEquals(limit, policy.url(limit).toASCIIString());
        assertThrows(IllegalArgumentException.class, () -> policy.url(limit + "a"));
        assertThrows(IllegalArgumentException.class, () -> policy.url(null));

        int remaining = 2048 - prefix.length();
        String unicodeLimit = prefix + "书".repeat(remaining / 9) + "a".repeat(remaining % 9);
        assertEquals(2048, policy.url(unicodeLimit).toASCIIString().length());
        assertThrows(IllegalArgumentException.class, () -> policy.url(unicodeLimit + "e"));
    }

    @Test
    void capsAllowlistAndHostLengths() {
        Set<String> hosts = new HashSet<>();
        for (int i = 0; i < 128; i++) {
            hosts.add("host" + i + ".example.com");
        }
        assertTrue(new NetworkPolicy(hosts).configured());
        hosts.add("extra.example.com");
        assertThrows(IllegalArgumentException.class, () -> new NetworkPolicy(hosts));
        assertThrows(IllegalArgumentException.class, () -> new NetworkPolicy(Set.of("a".repeat(64) + ".com")));
        String hostLimit = ("a".repeat(63) + ".").repeat(3) + "b".repeat(61);
        assertEquals(253, hostLimit.length());
        assertTrue(new NetworkPolicy(Set.of(hostLimit + ".")).configured());
        assertThrows(IllegalArgumentException.class, () -> new NetworkPolicy(Set.of(hostLimit + "b")));
    }

    @Test
    void acceptsUnresolvableTrustedHostWithoutSendingRequests() {
        String host = "trusted-does-not-resolve.invalid";
        NetworkPolicy policy = new NetworkPolicy(Set.of(host));
        assertEquals(URI.create("https://" + host + "/a"), policy.url("https://" + host + "/a"));
        AtomicInteger sends = new AtomicInteger();
        WebFetchTool tool = new WebFetchTool(Set.of(host), request -> {
            sends.incrementAndGet();
            throw new AssertionError("Configuration must not send a request");
        });
        assertTrue(tool.configured());
        assertEquals(0, sends.get());
    }

    @Test
    void productionClientUsesHttp1WithBoundedConnectTimeoutAndNoAmbientProxyCookiesOrAuthentication() {
        try (HttpClient client = NetworkHttp.client()) {
            assertEquals(HttpClient.Version.HTTP_1_1, client.version());
            assertEquals(Duration.ofSeconds(3), client.connectTimeout().orElseThrow());
            assertEquals(HttpClient.Redirect.NEVER, client.followRedirects());
            assertSame(HttpClient.Builder.NO_PROXY, client.proxy().orElseThrow());
            assertEquals(
                    List.of(Proxy.NO_PROXY), client.proxy().orElseThrow().select(URI.create("https://example.com/")));
            assertTrue(client.cookieHandler().isEmpty());
            assertTrue(client.authenticator().isEmpty());
        }
    }
}
