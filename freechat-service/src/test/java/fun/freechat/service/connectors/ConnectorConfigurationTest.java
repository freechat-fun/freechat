package fun.freechat.service.connectors;

import static org.junit.jupiter.api.Assertions.*;

import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ConnectorConfigurationTest {
    @Test
    void immutableDefaultsAndRedactedOutput() {
        Map<String, String> headers = new LinkedHashMap<>(Map.of("Authorization", "Bearer super-secret"));
        ConnectorConfiguration config = config("https://unlisted.example/mcp", headers);
        headers.clear();
        assertEquals(60_000, config.timeout());
        assertEquals("Bearer super-secret", config.headers().get("Authorization"));
        assertThrows(UnsupportedOperationException.class, () -> config.headers().clear());
        assertEquals("ConnectorConfiguration[redacted]", config.toString());
        assertFalse(new ConnectorService.Resolved("id", "owner", "revision", config, true)
                .toString()
                .contains("secret"));
        assertEquals("中文 connector / 1", config.name());
    }

    @ParameterizedTest
    @ValueSource(strings = {"stdio", "STDIO", "HTTP", "SSE", "websocket", "", "http ", "https"})
    void onlyExactRemoteTransports(String transport) {
        invalid(() -> new ConnectorConfiguration("name", transport, "https://example.com", null, null));
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "file:///tmp/mcp",
                "ftp://example.com/mcp",
                "ws://example.com/mcp",
                "https:example.com",
                "//example.com/mcp",
                "https://user:secret@example.com/mcp",
                "https://example.com?key=secret",
                "https://example.com?",
                "https://example.com#fragment",
                "https://example.com#",
                "https://example.com\\evil",
                "https://example.com/a b",
                "https://example.com/\n",
                "https://example.com/%0d%0aInjected",
                "https://example.com/%5cpath",
                "https://example.com/%20path",
                "https://example.com:\u0030/mcp",
                "https://example.com:65536/mcp",
                "https://example.com:-1/mcp",
                "https://example.com:abc/mcp",
                "https://example.com:/mcp",
                "https:///mcp",
                "https://bad_host/mcp",
                "https://exa%6dple.com/mcp",
                "https://[fe80::1%25eth0]/mcp"
            })
    void rejectsMalformedAndCredentialBearingBaseUrls(String url) {
        invalid(() -> config(url, Map.of()));
    }

    @Test
    void syntaxChecksDoNotResolveOrApplyAllowlistOrPublicAddressPolicy() {
        for (String url : new String[] {
            "https://nonexistent.invalid/mcp",
            "http://127.0.0.1:8080/mcp",
            "https://[::1]:443/sse",
            "https://any-domain.example/mcp",
            "https://example.com:65535/mcp"
        }) {
            assertDoesNotThrow(() -> config(url, null));
        }
        invalid(() -> config("https://example.com/" + "a".repeat(2048), null));
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "Host",
                "Accept",
                "Content-Type",
                "Content-Length",
                "Connection",
                "Transfer-Encoding",
                "Upgrade",
                "Proxy-Authorization",
                "proxy-custom",
                "Origin",
                "Referer",
                "Forwarded",
                "X-Forwarded-For",
                "Mcp-Session-Id",
                "MCP-Protocol-Version",
                "Last-Event-ID",
                "Accept-Encoding",
                "Keep-Alive",
                "TE",
                "Trailer",
                "Expect",
                "Via",
                "HTTP2-Settings",
                "Bad Name",
                "Injected\r\nName",
                "Ünicode",
                ""
            })
    void routingAndTransportHeadersAndInvalidTokensAreDenied(String header) {
        invalid(() -> config("https://example.com", Map.of(header, "value")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"value\r\nX-Evil: yes", "value\n", "\t", "\u007f", "\u0000", "é"})
    void valuesMustBePrintableAscii(String value) {
        invalid(() -> config("https://example.com", Map.of("X-Key", value)));
    }

    @Test
    void allConfiguredHeadersRequireHttpsIncludingNonSecrets() {
        invalid(() -> config("http://example.com", Map.of("X-Custom", "anything")));
        assertDoesNotThrow(
                () -> config("https://example.com", Map.of("Authorization", "Bearer secret", "X-Api-Key", "key")));
        invalid(() -> config("https://example.com", Map.of("X-Key", "a", "x-key", "b")));
    }

    @Test
    void strictHeaderCountAndTotalSize() {
        Map<String, String> headers = new LinkedHashMap<>();
        for (int i = 0; i < 16; i++) {
            headers.put("X-" + i, "v");
        }
        assertDoesNotThrow(() -> config("https://example.com", headers));
        headers.put("X-17", "v");
        invalid(() -> config("https://example.com", headers));
        assertDoesNotThrow(() -> config("https://example.com", Map.of("X", "v".repeat(16383))));
        invalid(() -> config("https://example.com", Map.of("X", "v".repeat(16384))));
        invalid(() -> config("https://example.com", Map.of("X", "v".repeat(8192), "Y", "v".repeat(8192))));
    }

    @Test
    void boundedPrintableNamesAndTimeouts() {
        for (String name : new String[] {"", " ", "x".repeat(65), "name\n", "name\u202e", "name\u0000"}) {
            invalid(() -> new ConnectorConfiguration(name, "sse", "https://example.com", null, null));
        }
        for (int timeout : new int[] {0, 999, 120001, Integer.MAX_VALUE}) {
            invalid(() -> new ConnectorConfiguration("name", "sse", "https://example.com", timeout, null));
        }
        for (int timeout : new int[] {1000, 60000, 120000}) {
            assertDoesNotThrow(
                    () -> new ConnectorConfiguration("x".repeat(64), "sse", "https://example.com", timeout, null));
        }
    }

    @Test
    void originsMatchCaseAndDefaultPortsButNotSchemeHostOrPortChanges() {
        assertTrue(
                ConnectorConfigurationValidator.sameOrigin("https://EXAMPLE.com/one", "https://example.com:443/two"));
        for (String changed :
                new String[] {"https://other.example/one", "http://example.com/one", "https://example.com:8443/one"}) {
            assertFalse(ConnectorConfigurationValidator.sameOrigin("https://example.com/one", changed));
        }
    }

    private static ConnectorConfiguration config(String url, Map<String, String> headers) {
        return new ConnectorConfiguration("中文 connector / 1", "http", url, null, headers);
    }

    private static void invalid(org.junit.jupiter.api.function.Executable operation) {
        ConnectorException failure = assertThrows(ConnectorException.class, operation);
        assertEquals(ConnectorException.Code.CONNECTOR_INVALID_CONFIGURATION, failure.code());
        assertEquals("CONNECTOR_INVALID_CONFIGURATION", failure.getMessage());
        assertNull(failure.getCause());
    }
}
