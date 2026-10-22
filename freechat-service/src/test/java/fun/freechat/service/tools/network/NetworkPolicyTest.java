package fun.freechat.service.tools.network;

import static fun.freechat.service.tools.network.NetworkTestSupport.context;
import static org.junit.jupiter.api.Assertions.*;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.Proxy;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import okhttp3.Authenticator;
import okhttp3.CookieJar;
import okhttp3.Protocol;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@Timeout(5)
class NetworkPolicyTest {
    @ParameterizedTest
    @ValueSource(
            strings = {
                "0.0.0.0",
                "0.1.2.3",
                "10.1.2.3",
                "100.64.0.1",
                "100.127.255.255",
                "127.0.0.1",
                "169.254.169.254",
                "169.254.170.2",
                "172.16.0.1",
                "172.31.255.255",
                "192.0.0.9",
                "192.0.2.1",
                "192.31.196.1",
                "192.52.193.1",
                "192.88.99.1",
                "192.168.1.1",
                "192.175.48.1",
                "198.18.0.1",
                "198.19.255.255",
                "198.51.100.1",
                "203.0.113.1",
                "224.0.0.1",
                "239.255.255.255",
                "240.0.0.1",
                "255.255.255.255",
                "::",
                "::1",
                "::8.8.8.8",
                "64:ff9b::808:808",
                "64:ff9b:1::1",
                "100::1",
                "2001::1",
                "2001:2::1",
                "2001:10::1",
                "2001:20::1",
                "2001:db8::1",
                "2002:0808:0808::1",
                "2620:4f:8000::1",
                "3ffe::1",
                "3fff::1",
                "3fff:fff::1",
                "5f00::1",
                "fc00::1",
                "fd00:ec2::254",
                "fe80::1",
                "fec0::1",
                "ff02::1"
            })
    void rejectsNonGlobalAndSpecialAddresses(String literal) throws Exception {
        assertFalse(NetworkPolicy.global(InetAddress.getByName(literal)), literal);
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "8.8.8.8",
                "1.1.1.1",
                "93.184.216.34",
                "100.63.255.255",
                "100.128.0.1",
                "172.15.255.255",
                "172.32.0.1",
                "198.17.255.255",
                "198.20.0.1",
                "2001:4860:4860::8888",
                "2606:4700:4700::1111",
                "2a00:1450::1"
            })
    void acceptsGlobalUnicastAddresses(String literal) throws Exception {
        assertTrue(NetworkPolicy.global(InetAddress.getByName(literal)), literal);
    }

    @Test
    void rejectsMappedIpv6EvenWithPublicEmbeddedIpv4() throws Exception {
        byte[] bytes = new byte[16];
        bytes[10] = (byte) 255;
        bytes[11] = (byte) 255;
        bytes[12] = 8;
        bytes[13] = 8;
        bytes[14] = 8;
        bytes[15] = 8;
        assertFalse(NetworkPolicy.global(Inet6Address.getByAddress(null, bytes, -1)));
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "http://example.com",
                "ftp://example.com",
                "file:///etc/passwd",
                "//example.com",
                "https://user@example.com",
                "https://user:password@example.com",
                "https://@example.com",
                "https://example.com/#",
                "https://example.com/#fragment",
                "https://example.com:80/",
                "https://example.com:444/",
                "https://example.com:/",
                "https://example.com:0443/",
                "https://example.com.evil.com/",
                "https://sub.example.com/",
                "https://example.com%00.evil.com/",
                "https://example.com\\@evil.com/",
                "https://example.com/\r\nHeader: x",
                " https://example.com/",
                "https://127.0.0.1/",
                "https://2130706433/",
                "https://0177.0.0.1/",
                "https://0x7f000001/",
                "https://[::1]/",
                "https://[::ffff:8.8.8.8]/",
                "https://[fe80::1%25lo0]/",
                "https://8.8.8.8/"
            })
    void rejectsAmbiguousOrUnapprovedDestinations(String url) {
        assertThrows(IllegalArgumentException.class, () -> new NetworkPolicy(Set.of("example.com")).url(url));
    }

    @Test
    void normalizesExactHostAllowlistWithoutWildcardMatching() {
        NetworkPolicy policy = new NetworkPolicy(Set.of("EXAMPLE.COM.", "xn--bcher-kva.de"));
        assertEquals("example.com", policy.url("HTTPS://EXAMPLE.COM.:443/a?q=1").host());
        assertEquals("xn--bcher-kva.de", policy.url("https://xn--bcher-kva.de/").host());
        assertThrows(IllegalArgumentException.class, () -> new NetworkPolicy(Set.of("*.example.com")));
        assertThrows(IllegalArgumentException.class, () -> new NetworkPolicy(Set.of("127.0.0.1")));
        assertThrows(IllegalArgumentException.class, () -> new NetworkPolicy(Set.of("https://example.com")));
        assertThrows(IllegalArgumentException.class, () -> policy.url("https://other.example.com/"));
        assertFalse(new NetworkPolicy(null).configured());
        assertFalse(new NetworkPolicy(Set.of()).configured());
    }

    @Test
    void dnsChecksEveryActualLookupAndRejectsMixedRebindingAndLargeAnswers() throws Exception {
        NetworkPolicy policy = new NetworkPolicy(Set.of("example.com"));
        InetAddress publicIp = InetAddress.getByName("8.8.8.8");
        InetAddress privateIp = InetAddress.getByName("10.0.0.1");
        AtomicInteger lookups = new AtomicInteger();
        var rebinding = policy.dns(host -> lookups.incrementAndGet() == 1 ? List.of(publicIp) : List.of(privateIp));
        assertEquals(List.of(publicIp), rebinding.lookup("example.com"));
        assertThrows(UnknownHostException.class, () -> rebinding.lookup("example.com"));
        assertThrows(
                UnknownHostException.class,
                () -> policy.dns(host -> List.of(publicIp, privateIp)).lookup("example.com"));
        assertThrows(
                UnknownHostException.class, () -> policy.dns(host -> List.of()).lookup("example.com"));
        assertThrows(
                UnknownHostException.class,
                () -> policy.dns(host -> Collections.nCopies(17, publicIp)).lookup("example.com"));
        assertThrows(
                UnknownHostException.class,
                () -> policy.dns(host -> {
                            fail("Unapproved hosts must not reach the resolver");
                            return List.of(publicIp);
                        })
                        .lookup("evil.com"));
    }

    @Test
    void productionClientHasNoAmbientProxyCookiesRedirectsRetriesOrSharedConnections() {
        var client = NetworkHttp.client(
                new NetworkPolicy(Set.of("example.com")), new NetworkHttp.Deadline(context(Duration.ofSeconds(10))));
        assertEquals(Proxy.NO_PROXY, client.proxy());
        assertSame(CookieJar.NO_COOKIES, client.cookieJar());
        assertSame(Authenticator.NONE, client.authenticator());
        assertSame(Authenticator.NONE, client.proxyAuthenticator());
        assertFalse(client.followRedirects());
        assertFalse(client.followSslRedirects());
        assertFalse(client.retryOnConnectionFailure());
        assertNull(client.cache());
        assertEquals(List.of(Protocol.HTTP_1_1), client.protocols());
        assertTrue(client.connectTimeoutMillis() > 0 && client.connectTimeoutMillis() <= 3000);
        assertTrue(client.readTimeoutMillis() > 0 && client.readTimeoutMillis() <= 4000);
        assertTrue(client.writeTimeoutMillis() > 0 && client.writeTimeoutMillis() <= 3000);
        assertTrue(client.callTimeoutMillis() > 0 && client.callTimeoutMillis() <= 10_000);
        assertEquals(1, client.networkInterceptors().size());
        assertNotNull(client.sslSocketFactory());
        assertNotNull(client.hostnameVerifier());
    }
}
