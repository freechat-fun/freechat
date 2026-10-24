package fun.freechat.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

public class HttpUtilsTest {
    private static final String PUBLIC_URL = "https://example.anylongsuffix/";

    @ParameterizedTest
    @ValueSource(
            strings = {
                "http://example.com",
                "HTTP://example.com",
                "hTtPs://example.anylongsuffix",
                "https://public-host",
                "https://example.com:1",
                "https://example.com:443/path",
                "https://example.com:65535?key=value",
                "https://example.com?query=无需斜杠",
                "https://example.com/目录/文件?query=内容",
                "https://example.com/\uD840\uDC00?query=\uD840\uDC00",
                "https://example.com/a%20b/%2F/%23?next=https%3A%2F%2Fother.example&value=%25"
            })
    public void acceptsHttpUrlsWithoutASuffixAllowlist(String url) {
        assertTrue(HttpUtils.isSafeUrl(url, host -> addresses("8.8.8.8", "2606:4700:4700::1111")));
    }

    @Test
    public void resolvesOnlyTheParsedHostAndAcceptsAllPublicAnswers() {
        assertTrue(HttpUtils.isSafeUrl(
                "HTTPS://Example.technology:8443/目录/%2f?q=内容",
                host -> {
                    assertEquals("Example.technology", host);
                    return new InetAddress[] {
                        InetAddress.getByName("8.8.8.8"),
                        InetAddress.getByName("2606:4700:4700::1111"),
                        mappedIpv4Address(InetAddress.getByName("1.1.1.1"))
                    };
                }));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(
            strings = {
                "example.com",
                "//example.com",
                "http:example.com",
                "https:/example.com",
                "https:///example.com",
                "https:////example.com",
                "https://",
                "https://?query=value",
                "https://:443/",
                "ftp://example.com/",
                "file://example.com/path",
                "https://example.com#fragment",
                "https://example.com#",
                "https://user:password@example.com/",
                "https://user@example.com/",
                "https://@example.com/",
                "https://example.com@127.0.0.1/",
                "https://user@@example.com/",
                "https://example.com;other.example/",
                "https://example.com:80:90/",
                "https://example.com:/",
                "https://example.com:?query=value",
                "https://[2606:4700:4700::1111]:/",
                "https://example.com:0/",
                "https://example.com:65536/",
                "https://example.com:-1/",
                "https://example.com:+80/",
                "https://example.com:abc/",
                "https://example.com:999999999999/",
                "https://%65xample.com/",
                "https://example.com%2f.other.example/",
                "https://example.com:%38%30/",
                "https://[2606:4700:4700::1111%eth0]/",
                "https://[2606:4700:4700::1111%25eth0]/",
                "https://2606:4700:4700::1111/",
                "https://[2606:::1111]/",
                "https://example.com\\@other.example/",
                "https://example.com/path\\file",
                "https://example.com?query=\\value",
                " https://example.com/",
                "https://example.com/ ",
                "https://example.com/a b",
                "https://example.com/\r\nheader:value",
                "https://example.com/?query=\tvalue",
                "https://example.com/\0",
                "https://example.com/\u0085",
                "https://example.com/\u00a0",
                "https://example.com/\u2003",
                "https://example.com/\uD800",
                "https://example.com/\uDC00",
                "https://example.com/\uD800x",
                "https://example.com/?query=\uDC00\uD800",
                "https://example.com/%",
                "https://example.com/?query=%zz"
            })
    public void rejectsInvalidUrlsBeforeResolution(String url) {
        assertFalse(HttpUtils.isSafeUrl(url, HttpUtilsTest::unexpectedResolution));
    }

    @Test
    public void boundsUrlLengthBeforeResolution() {
        String url = PUBLIC_URL + "a".repeat(2048 - PUBLIC_URL.length());
        assertTrue(HttpUtils.isSafeUrl(url, host -> addresses("8.8.8.8")));
        assertFalse(HttpUtils.isSafeUrl(url + "a", HttpUtilsTest::unexpectedResolution));
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "0.0.0.0",
                "0.1.2.3",
                "10.0.0.1",
                "127.0.0.1",
                "127.255.255.254",
                "172.16.0.1",
                "172.31.255.255",
                "192.168.1.1",
                "100.64.0.1",
                "100.127.255.255",
                "169.254.169.254",
                "198.18.0.1",
                "198.19.255.255",
                "224.0.0.1",
                "239.255.255.255",
                "240.0.0.1",
                "255.255.255.255",
                "::",
                "::1",
                "fe80::1",
                "febf::1",
                "fec0::1",
                "feff::1",
                "fc00::1",
                "fd00:ec2::254",
                "ff02::1",
                "::ffff:127.0.0.1",
                "::ffff:7f00:1",
                "::ffff:169.254.169.254"
            })
    public void rejectsNonPublicDnsAnswersAndIpLiterals(String literal) throws UnknownHostException {
        InetAddress address = InetAddress.getByName(literal);
        assertFalse(HttpUtils.isSafeUrl(PUBLIC_URL, host -> new InetAddress[] {address}));

        // Only numeric literals reach the real resolver; these checks require no network.
        String authority = literal.contains(":") ? "[" + literal + "]" : literal;
        assertFalse(HttpUtils.isSafeUrl("http://" + authority + "/"));

        if (address.getAddress().length == 4) {
            InetAddress mapped = mappedIpv4Address(address);
            assertFalse(HttpUtils.isSafeUrl(PUBLIC_URL, host -> new InetAddress[] {mapped}));
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"10.0.0.1", "fd00::1", "fe80::1", "::ffff:127.0.0.1"})
    public void rejectsMixedPublicAndNonPublicAnswersInEitherOrder(String nonPublic) {
        assertFalse(HttpUtils.isSafeUrl(PUBLIC_URL, host -> addresses("8.8.8.8", "2606:4700:4700::1111", nonPublic)));
        assertFalse(HttpUtils.isSafeUrl(PUBLIC_URL, host -> addresses(nonPublic, "2606:4700:4700::1111", "8.8.8.8")));
    }

    @Test
    public void failsClosedOnResolutionFailureOrEmptyAnswers() {
        assertFalse(HttpUtils.isSafeUrl(PUBLIC_URL, host -> {
            throw new UnknownHostException("Simulated resolution failure");
        }));
        assertFalse(HttpUtils.isSafeUrl(PUBLIC_URL, host -> {
            throw new SecurityException("Resolution denied");
        }));
        assertFalse(HttpUtils.isSafeUrl(PUBLIC_URL, host -> new InetAddress[0]));
        assertFalse(HttpUtils.isSafeUrl(PUBLIC_URL, host -> null));
        assertFalse(HttpUtils.isSafeUrl(PUBLIC_URL, host -> new InetAddress[] {null}));
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "http://8.8.8.8/",
                "HTTPS://1.1.1.1:443?query=value",
                "http://[2606:4700:4700::1111]/",
                "https://[2001:4860:4860::8888]:65535/目录?query=内容",
                "http://[::ffff:8.8.8.8]/",
                "http://[::ffff:808:808]/",
                "http://100.63.255.255/",
                "http://100.128.0.1/",
                "http://172.15.255.255/",
                "http://172.32.0.1/",
                "http://198.17.255.255/",
                "http://198.20.0.1/"
            })
    public void acceptsPublicIpLiteralsWithoutNetworkAccess(String url) {
        assertTrue(HttpUtils.isSafeUrl(url));
    }

    private static InetAddress[] addresses(String... literals) throws UnknownHostException {
        InetAddress[] addresses = new InetAddress[literals.length];
        for (int i = 0; i < literals.length; i++) {
            addresses[i] = InetAddress.getByName(literals[i]);
        }
        return addresses;
    }

    private static Inet6Address mappedIpv4Address(InetAddress ipv4) throws UnknownHostException {
        byte[] mapped = new byte[16];
        mapped[10] = (byte) 0xff;
        mapped[11] = (byte) 0xff;
        System.arraycopy(ipv4.getAddress(), 0, mapped, 12, 4);
        return Inet6Address.getByAddress(null, mapped, -1);
    }

    private static InetAddress[] unexpectedResolution(String host) {
        throw new AssertionError("Invalid URL must not trigger resolution");
    }
}
