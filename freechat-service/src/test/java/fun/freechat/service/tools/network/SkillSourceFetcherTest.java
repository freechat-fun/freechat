package fun.freechat.service.tools.network;

import static fun.freechat.service.tools.network.NetworkTestSupport.*;
import static org.junit.jupiter.api.Assertions.*;

import fun.freechat.service.skills.SkillException;
import fun.freechat.service.skills.SkillSourceAnalyzer;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@Timeout(5)
class SkillSourceFetcherTest {
    private static final SkillSourceAnalyzer FIRST = (input, candidates) -> 0;

    @Test
    void defaultAndOverriddenHostListsMatchTheEffectivePolicy() {
        for (var settings : List.of(
                Map.<String, Object>of(),
                Map.<String, Object>of("skills.sources.allowedHosts", " GITHUB.COM.,clawhub.ai,github.com "),
                Map.<String, Object>of("skills.sources.allowedHosts", "example.com"),
                Map.<String, Object>of("skills.sources.allowedHosts", ""))) {
            try (var application = new org.springframework.context.annotation.AnnotationConfigApplicationContext()) {
                application
                        .getEnvironment()
                        .getPropertySources()
                        .addFirst(new org.springframework.core.env.MapPropertySource("skill-hosts", settings));
                application.registerBean(SkillSourceAnalyzer.class, () -> FIRST);
                application.register(SkillSourceFetcher.class);
                application.refresh();
                var hosts = application.getBean(SkillSourceFetcher.class).allowedHosts();
                String configured = (String) settings.get("skills.sources.allowedHosts");
                if ("".equals(configured)) assertTrue(hosts.isEmpty());
                else if ("example.com".equals(configured)) assertEquals(List.of("example.com"), hosts);
                else assertEquals(List.of("clawhub.ai", "github.com"), hosts);
                assertThrows(UnsupportedOperationException.class, () -> hosts.add("other.example"));
            }
        }
    }

    @Test
    void analyzesImperfectLinksAndDownloadsExactRawTextWithoutIdentityOrCredentials() {
        String text = "---\nname: story\ndescription: Narration\n---\nKeep {{literal}} markup unchanged.";
        Calls calls = new Calls(request -> response(request, 200, "text/plain", text));
        AtomicInteger analyses = new AtomicInteger();
        String input = "Please install [this](example.com/SKILL.md).";
        SkillSourceAnalyzer analyzer = (value, candidates) -> {
            analyses.incrementAndGet();
            assertEquals(input, value);
            assertEquals(List.of("https://example.com/SKILL.md"), candidates);
            return 0;
        };
        var bundle =
                new SkillSourceFetcher(Set.of("example.com"), analyzer, calls).fetch("private-owner-sentinel", input);
        assertEquals(Map.of("SKILL.md", text), bundle.files());
        assertEquals("https://example.com/SKILL.md", bundle.source().toString());
        assertEquals(1, analyses.get());
        assertEquals(1, calls.count.get());
        assertTrue(calls.request.headers().firstValue("Authorization").isEmpty());
        assertTrue(calls.request.headers().firstValue("Cookie").isEmpty());
        assertTrue(calls.request.bodyPublisher().isEmpty());
        assertFalse(calls.request.toString().contains("private-owner-sentinel"));
    }

    @Test
    void modelSelectsOnlyIndependentlyValidatedCandidates() {
        Calls calls = new Calls(request -> response(request, 200, "text/markdown", "document"));
        String input = "Use the second link: https://example.com/one.md or https://other.example/two.md";
        SkillSourceFetcher fetcher =
                new SkillSourceFetcher(Set.of("example.com", "other.example"), (value, candidates) -> 1, calls);
        assertEquals(
                "https://other.example/two.md",
                fetcher.fetch("owner", input).source().toString());
        for (int choice : new int[] {-1, 1, 100}) {
            assertEquals(
                    SkillException.Code.SKILL_SOURCE_AMBIGUOUS,
                    assertThrows(
                                    SkillException.class,
                                    () -> new SkillSourceFetcher(
                                                    Set.of("example.com"), (value, candidates) -> choice, calls)
                                            .fetch("owner", "https://example.com/"))
                            .code());
        }
        assertEquals(1, calls.count.get());
    }

    @Test
    void normalizesPresentationNotRepositoryIdentity() {
        var fetcher = new SkillSourceFetcher("github.com,clawhub.ai", "", FIRST);
        assertEquals(List.of("https://github.com/owner/repo"), fetcher.candidates("github.com/owner/repo"));
        assertEquals(
                List.of("https://github.com/owner/repo.git"),
                fetcher.candidates("git clone https://github.com/owner/repo.git"));
        assertEquals(
                List.of("https://github.com/owner/repo"),
                fetcher.candidates("'https://github.com/owner/repo?tab=readme-ov-file&utm_source=share'"));
        assertEquals(
                List.of("https://github.com/owner/repo"),
                fetcher.candidates("<HTTP://WWW.GITHUB.COM/owner/repo#readme>"));
        assertEquals(
                List.of("https://clawhub.ai/owner/skills/story"),
                fetcher.candidates("Install [story](https://clawhub.ai/owner/skills/story)."));
        assertEquals(
                List.of("https://github.com/owner/repo"),
                fetcher.candidates("https://github.com/owner/repo https://github.com/owner/repo"));
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "https://example.com.evil.com/SKILL.md",
                "https://sub.example.com/SKILL.md",
                "https://127.0.0.1/",
                "file:///etc/passwd",
                "https://user@example.com/",
                "https://example.com:444/",
                "https://evil.example/?url=https://example.com/SKILL.md"
            })
    void unsafeOrUnapprovedSourcesNeverReachModelOrTransport(String input) {
        Calls calls = new Calls(request -> {
            throw new AssertionError("No request expected");
        });
        SkillSourceAnalyzer analyzer = (value, candidates) -> {
            throw new AssertionError("No analysis expected");
        };
        assertEquals(
                SkillException.Code.SKILL_SOURCE_NOT_ALLOWED,
                assertThrows(
                                SkillException.class,
                                () -> new SkillSourceFetcher(Set.of("example.com"), analyzer, calls)
                                        .fetch("owner", input))
                        .code());
        assertEquals(0, calls.count.get());
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "https://example.com/SKILL.md?variant=release(v2)",
                "https://example.com/SKILL.md?name=O'Reilly",
                "https://example.com/SKILL.md?values=[one,two]",
                "https://example.com/SKILL.md?",
                "https://example.com/file."
            })
    void preservesValidStandaloneRawUrlsBeforeProseExtraction(String source) {
        Calls calls = new Calls(request -> response(request, 200, "text/plain", "document"));
        var fetcher = new SkillSourceFetcher(Set.of("example.com"), FIRST, calls);
        assertEquals(List.of(source), fetcher.candidates(source));
        assertEquals(source, fetcher.fetch("owner", source).source().toString());
        assertEquals(source, calls.request.uri().toString());
    }

    @Test
    void analysisFailuresAreSanitizedWithoutHeuristicFallback() {
        Calls calls = new Calls(request -> {
            throw new AssertionError("No request expected");
        });
        SkillSourceAnalyzer analyzer = (value, candidates) -> {
            throw new IllegalStateException("private-key-secret");
        };
        SkillException error = assertThrows(
                SkillException.class,
                () -> new SkillSourceFetcher(Set.of("example.com"), analyzer, calls)
                        .fetch("owner", "https://example.com/"));
        assertEquals(SkillException.Code.SKILL_SOURCE_ANALYSIS_UNAVAILABLE, error.code());
        assertNull(error.getCause());
        assertFalse(error.toString().contains("secret"));
        assertEquals(0, calls.count.get());
    }

    @ParameterizedTest
    @ValueSource(strings = {"text/html", "application/json", "application/octet-stream", "text/x-config"})
    void rawTextSourcesHaveNoDocumentMimeGate(String media) {
        Calls calls = new Calls(request -> response(request, 200, media, "Plain instructions"));
        var fetched =
                new SkillSourceFetcher(Set.of("example.com"), FIRST, calls).fetch("owner", "https://example.com/guide");
        assertEquals(Map.of("SKILL.md", "Plain instructions"), fetched.files());
        assertEquals("*/*", calls.request.headers().firstValue("Accept").orElseThrow());
    }

    @Test
    void refusesRedirectsOversizeBodiesAndProviderSecrets() {
        for (Calls calls : new Calls[] {
            new Calls(request ->
                    response(request, 302, "text/plain", "secret").withHeader("Location", "https://example.com/other")),
            new Calls(request -> response(request, 200, "text/plain", "x".repeat(32769))),
            new Calls(request -> {
                throw new IOException("private-key-secret");
            })
        }) {
            SkillException error = assertThrows(
                    SkillException.class,
                    () -> new SkillSourceFetcher(Set.of("example.com"), FIRST, calls)
                            .fetch("owner", "https://example.com/SKILL.md"));
            assertEquals(SkillException.Code.SKILL_SOURCE_UNAVAILABLE, error.code());
            assertNull(error.getCause());
            assertFalse(error.toString().contains("secret"));
            assertEquals(1, calls.count.get());
        }
    }

    @Test
    void clawHubOwnerSurvivesAnalysisAndEveryPinnedDownload() throws Exception {
        String content = "Use the supplied weather information.";
        String digest = java.util.HexFormat.of()
                .formatHex(java.security.MessageDigest.getInstance("SHA-256")
                        .digest(content.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
        String api = "/api/v1/skills/weather";
        var responses = Map.of(
                api + "?owner=steipete",
                "{\"skill\":{\"slug\":\"weather\"},\"owner\":{\"handle\":\"steipete\"},\"latestVersion\":{\"version\":\"1.0.0\"}}",
                api + "/versions/1.0.0?owner=steipete",
                "{\"skill\":{\"slug\":\"weather\"},\"version\":{\"version\":\"1.0.0\",\"files\":[{\"path\":\"SKILL.md\",\"size\":"
                        + content.length() + ",\"sha256\":\"" + digest + "\"},{\"path\":\"notes.txt\",\"size\":"
                        + content.length() + ",\"sha256\":\"" + digest + "\"}]}}",
                api + "/file?path=SKILL.md&version=1.0.0&owner=steipete",
                content,
                api + "/file?path=notes.txt&version=1.0.0&owner=steipete",
                content);
        Calls calls = new Calls(request -> {
            assertEquals("clawhub.ai", request.uri().getHost());
            assertTrue(request.headers().firstValue("Authorization").isEmpty());
            String key = request.uri().getRawPath() + "?" + request.uri().getRawQuery();
            assertTrue(responses.containsKey(key), key);
            return response(
                    request,
                    200,
                    request.uri().getPath().endsWith("/file") ? "text/plain" : "application/json",
                    responses.get(key));
        });
        var fetched = new SkillSourceFetcher(Set.of("clawhub.ai"), FIRST, calls, "github-only-secret")
                .fetch("owner", "https://clawhub.ai/steipete/skills/weather");
        assertEquals(Map.of("SKILL.md", content, "notes.txt", content), fetched.files());
        assertEquals("1.0.0", fetched.revision());
        assertEquals(4, calls.count.get());
        assertDoesNotThrow(() -> new fun.freechat.service.skills.SkillBundleValidator().validate(fetched.files()));
    }

    @Test
    void upstreamRateLimitsHaveTheirOwnCodeAndSafeDiagnostics() {
        var logger = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(NetworkHttp.class);
        var appender = new ch.qos.logback.core.read.ListAppender<ch.qos.logback.classic.spi.ILoggingEvent>();
        appender.start();
        logger.addAppender(appender);
        try {
            for (int status : new int[] {403, 429}) {
                Calls calls = new Calls(request -> response(request, status, "application/json", "private-body-secret")
                        .withHeader("X-RateLimit-Remaining", "0")
                        .withHeader("X-RateLimit-Reset", "1792763055")
                        .withHeader("Retry-After", "private-header-secret"));
                var fetcher = new SkillSourceFetcher(Set.of("github.com"), FIRST, calls, "private-token-secret");
                SkillException error = assertThrows(
                        SkillException.class,
                        () -> fetcher.fetch("private-owner-secret", "https://github.com/acme/repo"));
                assertEquals(SkillException.Code.SKILL_SOURCE_RATE_LIMITED, error.code());
                assertNull(error.getCause());
                assertEquals(1, calls.count.get());
                var event = appender.list.getLast();
                assertTrue(event.getFormattedMessage().contains("host=api.github.com, status=" + status));
                assertTrue(event.getFormattedMessage().contains("rateLimitRemaining=0, rateLimitReset=1792763055"));
                assertTrue(event.getFormattedMessage().contains("retryAfter=unknown"));
                assertFalse(event.getFormattedMessage().contains("secret"));
                assertFalse(event.getFormattedMessage().contains("/acme/repo"));
                assertNull(event.getThrowableProxy());
            }
        } finally {
            logger.detachAppender(appender);
            appender.stop();
        }
    }

    @Test
    void secondaryRateLimitsAreDistinctFromOrdinaryForbiddenResponses() {
        for (String retryAfter : new String[] {"60", "untrusted-value"}) {
            Calls calls = new Calls(request -> response(request, 403, "application/json", "private-body-secret")
                    .withHeader("X-RateLimit-Remaining", "42")
                    .withHeader("Retry-After", retryAfter));
            SkillException error = assertThrows(
                    SkillException.class,
                    () -> new SkillSourceFetcher(Set.of("github.com"), FIRST, calls)
                            .fetch("owner", "https://github.com/acme/repo"));
            assertEquals(
                    retryAfter.equals("60")
                            ? SkillException.Code.SKILL_SOURCE_RATE_LIMITED
                            : SkillException.Code.SKILL_SOURCE_UNAVAILABLE,
                    error.code());
            assertEquals(1, calls.count.get());
        }
    }

    @ParameterizedTest
    @ValueSource(
            strings = {"https://github.com/acme/repo", "https://clawhub.ai/acme/story", "https://example.com/SKILL.md"})
    void dedicatedGithubCredentialsNeverReachOtherProvidersOrRedirects(String source) {
        Calls calls = new Calls(request -> response(request, 302, "application/json", "private-body-secret")
                .withHeader("Location", "https://attacker.invalid/"));
        var fetcher = new SkillSourceFetcher(
                Set.of("github.com", "clawhub.ai", "example.com"), FIRST, calls, "private-token-secret");
        SkillException error = assertThrows(SkillException.class, () -> fetcher.fetch("owner", source));
        assertEquals(SkillException.Code.SKILL_SOURCE_UNAVAILABLE, error.code());
        assertEquals(1, calls.count.get());
        assertEquals(
                source.startsWith("https://github.com/") ? "Bearer private-token-secret" : null,
                calls.request.headers().firstValue("Authorization").orElse(null));
        assertFalse(error.toString().contains("secret"));
    }

    @Test
    void sharedCredentialsDoNotRevealPrivateRepositoryExistence() {
        var privateRepository = new Calls(request ->
                response(request, 200, "application/json", "{\"private\":true,\"default_branch\":\"main\"}"));
        var missingRepository = new Calls(request -> response(request, 404, "application/json", "not found"));
        for (Calls calls : List.of(privateRepository, missingRepository)) {
            SkillException error = assertThrows(
                    SkillException.class,
                    () -> new SkillSourceFetcher(Set.of("github.com"), FIRST, calls, "private-token-secret")
                            .fetch("owner", "https://github.com/acme/repo"));
            assertEquals(SkillException.Code.SKILL_SOURCE_UNAVAILABLE, error.code());
            assertEquals("SKILL_SOURCE_UNAVAILABLE", error.getMessage());
            assertNull(error.getCause());
            assertEquals(1, calls.count.get());
        }
    }

    @Test
    void absentGithubCredentialsLeaveRequestsAnonymous() {
        Calls calls = new Calls(request -> response(request, 403, "application/json", "denied"));
        assertThrows(
                SkillException.class,
                () -> new SkillSourceFetcher(Set.of("github.com"), FIRST, calls, " ")
                        .fetch("owner", "https://github.com/acme/repo"));
        assertTrue(calls.request.headers().firstValue("Authorization").isEmpty());
    }

    @Test
    void transportDiagnosticsExposeOnlyTheExceptionType() {
        var logger = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(NetworkHttp.class);
        var appender = new ch.qos.logback.core.read.ListAppender<ch.qos.logback.classic.spi.ILoggingEvent>();
        appender.start();
        logger.addAppender(appender);
        try {
            Calls calls = new Calls(request -> {
                throw new javax.net.ssl.SSLHandshakeException("private-error-secret");
            });
            SkillException error = assertThrows(
                    SkillException.class,
                    () -> new SkillSourceFetcher(Set.of("github.com"), FIRST, calls, "private-token-secret")
                            .fetch("owner", "https://github.com/acme/repo"));
            assertEquals(SkillException.Code.SKILL_SOURCE_UNAVAILABLE, error.code());
            assertNull(error.getCause());
            var event = appender.list.getLast();
            assertTrue(event.getFormattedMessage().contains("exception=SSLHandshakeException"));
            assertFalse(event.getFormattedMessage().contains("secret"));
            assertNull(event.getThrowableProxy());
        } finally {
            logger.detachAppender(appender);
            appender.stop();
        }
    }

    @Test
    void canonicalProviderHostNeverGrantsUserAccessToItsSubdomains() {
        var fetcher = new SkillSourceFetcher("github.com,clawhub.ai", "", FIRST);
        for (String host :
                List.of("api.github.com", "raw.githubusercontent.com", "sub.clawhub.ai", "github.com.evil.com")) {
            assertThrows(SkillException.class, () -> fetcher.candidates("https://" + host + "/SKILL.md"));
        }
        assertTrue(new SkillSourceFetcher("", "", FIRST).allowedHosts().isEmpty());
        assertThrows(
                SkillException.class,
                () -> new SkillSourceFetcher("", "", FIRST).fetch("owner", "https://github.com/owner/repo"));
    }
}
