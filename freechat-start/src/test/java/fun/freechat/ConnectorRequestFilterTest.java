package fun.freechat;

import static org.junit.jupiter.api.Assertions.*;

import fun.freechat.access.ConnectorRequestFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class ConnectorRequestFilterTest {
    @ParameterizedTest
    @ValueSource(
            strings = {
                "/api/v2/connector",
                "/api/v2/connector/id",
                "/api/v2/conn%65ctor/id",
                "/api/v2/connector;param=x/id",
                "/%61pi/v2/connector/id"
            })
    void boundsDeclaredRequestsOnDecodedPath(String path) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", path);
        request.setContent(new byte[64 * 1024 + 1]);
        MockHttpServletResponse response = new MockHttpServletResponse();
        new ConnectorRequestFilter().doFilter(request, response, (req, res) -> fail("Must reject before JSON parsing"));
        assertEquals(413, response.getStatus());
        assertEquals("no-store", response.getHeader("Cache-Control"));
        assertTrue(response.getContentAsString().contains("CONNECTOR_LIMIT_EXCEEDED"));
    }

    @Test
    void boundsUnknownLengthAndRepeatedReadsWithoutLeakingBody() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("PUT", "/api/v2/conn%65ctor/id") {
            @Override
            public long getContentLengthLong() {
                return -1;
            }
        };
        request.setContent(new byte[64 * 1024 + 1]);
        new ConnectorRequestFilter().doFilter(request, new MockHttpServletResponse(), (req, res) -> {
            var input = req.getInputStream();
            assertEquals(
                    "CONNECTOR_LIMIT_EXCEEDED",
                    assertThrows(IOException.class, input::readAllBytes).getMessage());
            assertThrows(IOException.class, input::read);
        });
    }

    @Test
    void acceptsExactLimitAndUtf8AndSkipsUnrelatedPaths() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v2/connector");
        request.setContent(new byte[64 * 1024]);
        new ConnectorRequestFilter()
                .doFilter(
                        request,
                        new MockHttpServletResponse(),
                        (req, res) ->
                                assertEquals(64 * 1024, req.getInputStream().readAllBytes().length));
        request = new MockHttpServletRequest("POST", "/app/api/v2/connector");
        request.setContextPath("/app");
        String json = "{\"name\":\"中文\"}";
        request.setContent(json.getBytes(StandardCharsets.UTF_8));
        new ConnectorRequestFilter()
                .doFilter(
                        request,
                        new MockHttpServletResponse(),
                        (req, res) -> assertEquals(json, req.getReader().readLine()));
        MockHttpServletRequest unrelated = new MockHttpServletRequest("POST", "/api/v2/connector-other");
        MockHttpServletResponse response = new MockHttpServletResponse();
        new ConnectorRequestFilter().doFilter(unrelated, response, (req, res) -> assertSame(unrelated, req));
        assertNull(response.getHeader("Cache-Control"));
    }
}
