package fun.freechat;

import static org.junit.jupiter.api.Assertions.*;

import fun.freechat.access.SkillRequestFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class SkillRequestFilterTest {
    @org.junit.jupiter.params.ParameterizedTest
    @org.junit.jupiter.params.provider.ValueSource(
            strings = {
                "/api/v2/skill/install",
                "/api/v2/sk%69ll/install",
                "/api/v2/skill;param=value/install",
                "/%61pi/v2/skill/install"
            })
    void rejectsOversizedDeclaredBodiesBeforeParsing(String path) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", path);
        request.setContent(new byte[2 * 1024 * 1024 + 1]);
        MockHttpServletResponse response = new MockHttpServletResponse();
        new SkillRequestFilter().doFilter(request, response, (req, res) -> fail("Must not enter controller"));
        assertEquals(413, response.getStatus());
        assertEquals("no-store", response.getHeader("Cache-Control"));
    }

    @org.junit.jupiter.params.ParameterizedTest
    @org.junit.jupiter.params.provider.ValueSource(strings = {"/api/v2/skill/id", "/api/v2/sk%69ll/id"})
    void boundsUnknownLengthStreamingRequestsBeforeJsonMaterialization(String path) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("PUT", path) {
            @Override
            public long getContentLengthLong() {
                return -1;
            }

            @Override
            public int getContentLength() {
                return -1;
            }
        };
        request.setContent(new byte[2 * 1024 * 1024 + 1]);
        new SkillRequestFilter()
                .doFilter(
                        request,
                        new MockHttpServletResponse(),
                        (req, res) -> assertThrows(
                                IOException.class, () -> req.getInputStream().readAllBytes()));
    }

    @Test
    void preservesSmallUtf8RequestsAndDoesNotAffectUnrelatedApis() throws Exception {
        String json = "{\"files\":{\"SKILL.md\":\"中文\"}}";
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v2/skill/install");
        request.setContent(json.getBytes(StandardCharsets.UTF_8));
        new SkillRequestFilter()
                .doFilter(
                        request,
                        new MockHttpServletResponse(),
                        (req, res) -> assertEquals(json, req.getReader().readLine()));
        MockHttpServletRequest unrelated = new MockHttpServletRequest("POST", "/api/v2/character");
        new SkillRequestFilter()
                .doFilter(unrelated, new MockHttpServletResponse(), (req, res) -> assertSame(unrelated, req));
    }
}
