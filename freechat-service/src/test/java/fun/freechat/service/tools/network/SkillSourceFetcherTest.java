package fun.freechat.service.tools.network;

import static fun.freechat.service.tools.network.NetworkTestSupport.*;
import static org.junit.jupiter.api.Assertions.*;

import fun.freechat.service.skills.SkillException;
import java.io.IOException;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@Timeout(5)
class SkillSourceFetcherTest {
    @Test
    void downloadsOnlyExactRawTextWithoutForwardingIdentityOrCredentials() {
        String text =
                "---\nname: story\ndescription: Narration\n---\nKeep {{literal}} markup unchanged.\n[Reference](https://other.example/resource)";
        Calls calls = new Calls(request -> response(request, 200, "text/plain", text));
        assertEquals(
                text,
                new SkillSourceFetcher(Set.of("example.com"), calls)
                        .fetch("private-owner-sentinel", "https://example.com/SKILL.md"));
        assertEquals(1, calls.count.get());
        assertTrue(calls.request.headers().firstValue("Authorization").isEmpty());
        assertTrue(calls.request.headers().firstValue("Cookie").isEmpty());
        assertTrue(calls.request.bodyPublisher().isEmpty());
        assertFalse(calls.request.toString().contains("private-owner-sentinel"));
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "https://example.com.evil.com/SKILL.md",
                "https://sub.example.com/SKILL.md",
                "https://127.0.0.1/",
                "file:///etc/passwd",
                "http://example.com/",
                "https://user@example.com/",
                "https://example.com:444/"
            })
    void unsafeOrUnapprovedUrlsNeverReachTransport(String url) {
        Calls calls = new Calls(request -> {
            throw new AssertionError("No request expected");
        });
        assertEquals(
                SkillException.Code.SKILL_SOURCE_NOT_ALLOWED,
                assertThrows(
                                SkillException.class,
                                () -> new SkillSourceFetcher(Set.of("example.com"), calls).fetch("owner", url))
                        .code());
        assertEquals(0, calls.count.get());
    }

    @Test
    void refusesRedirectsHtmlOversizedBodiesAndProviderSecretErrors() {
        for (Calls calls : new Calls[] {
            new Calls(request ->
                    response(request, 302, "text/plain", "secret").withHeader("Location", "https://example.com/other")),
            new Calls(request -> response(request, 200, "text/html", "secret")),
            new Calls(request -> response(request, 200, "text/plain", "x".repeat(32 * 1024 + 1))),
            new Calls(request -> {
                throw new IOException("private-key-secret");
            })
        }) {
            SkillException error = assertThrows(
                    SkillException.class,
                    () -> new SkillSourceFetcher(Set.of("example.com"), calls)
                            .fetch("owner", "https://example.com/SKILL.md"));
            assertEquals(SkillException.Code.SKILL_SOURCE_UNAVAILABLE, error.code());
            assertFalse(error.toString().contains("secret"));
            assertNull(error.getCause());
            assertEquals(1, calls.count.get());
        }
    }
}
