package fun.freechat.service.tools.network;

import static fun.freechat.service.tools.network.NetworkTestSupport.*;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.JsonNode;
import fun.freechat.service.tools.ToolArguments;
import fun.freechat.service.tools.network.WebSearchTool.Provider;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

@Timeout(5)
class WebSearchResponseTest {
    private static final List<String> MEDIA_TYPES = List.of("application/json", "text/event-stream");
    private static final String SEARCH = """
            {"type":"web_search_call","status":"completed","action":{"type":"search","query":"facts","sources":[
              {"type":"url","url":"https://other.org/reference"},
              {"type":"url","url":"https://example.com/facts"}]}}
            """;
    private static final String OPEN = """
            {"type":"web_search_call","status":"completed","action":{"type":"open_page","url":"https://example.com/facts"}}
            """;
    private static final String FIND = """
            {"type":"web_search_call","status":"completed","action":{
              "type":"find_in_page","url":"https://research.org/paper","pattern":"facts"}}
            """;
    private static final String EXTRACTOR = """
            {"type":"web_extractor_call","status":"completed","urls":["https://example.com/facts"],
             "output":"<p>Extracted facts.</p><script>hidden-extractor</script>"}
            """;
    private static final String MESSAGE = """
            {"type":"message","content":[{"type":"output_text",
              "text":"<b>Found facts.</b><script>hidden-script</script> [invented](https://invented.com) https://fabricated.com",
              "annotations":[{"type":"url_citation","url":"https://annotation.com"}]}]}
            """;

    private static String completed(String... items) {
        return "{\"status\":\"completed\",\"output\":[" + String.join(",", items) + "]}";
    }

    private static String call(String action) {
        return "{\"type\":\"web_search_call\",\"status\":\"completed\",\"action\":" + action + "}";
    }

    private static String done(String item) {
        return "event: response.output_item.done\r\ndata: {\"item\":"
                + item.strip().replace("\n", "\r\ndata: ") + "}\r\n\r\n";
    }

    private static String terminal(String response) {
        return "event: response.completed\ndata: {\"type\":\"response.completed\",\ndata: \"response\":"
                + response.replace("\n", "\ndata: ") + "}\n\ndata: [DONE]\n\n";
    }

    private static JsonNode parse(Provider provider, String mediaType, String body, int maxResults) throws IOException {
        return args(WebSearchResponse.result(
                new NetworkHttp.Payload(mediaType, body), maxResults, new NetworkHttp.Deadline(context()), provider));
    }

    private static JsonNode snapshot(Provider provider, String mediaType, int maxResults, String... items)
            throws IOException {
        String json = completed(items);
        return parse(provider, mediaType, mediaType.equals("text/event-stream") ? terminal(json) : json, maxResults);
    }

    @ParameterizedTest
    @EnumSource(Provider.class)
    void parsesCompletedJsonAndSseSearchEvidenceWithTheSameSafeOutputShape(Provider provider) throws Exception {
        for (String mediaType : MEDIA_TYPES) {
            JsonNode result = snapshot(provider, mediaType, 5, SEARCH, MESSAGE);
            assertEquals(7, result.size());
            assertEquals(1, result.path("searchCount").asInt());
            assertEquals(args("""
                            [{"url":"https://other.org/reference","opened":false},
                             {"url":"https://example.com/facts","opened":false}]
                            """), result.path("sources"));
            assertTrue(result.path("answer").asText().contains("Found facts."));
            assertFalse(result.path("rawPageContent").asBoolean());
            assertFalse(result.path("truncated").asBoolean());
            assertTrue(result.path("untrusted").asBoolean());
            assertEquals(NetworkContent.NOTICE, result.path("notice").asText());
            for (String forbidden : List.of("invented.com", "fabricated.com", "annotation.com", "hidden-script")) {
                assertFalse(result.toString().contains(forbidden));
            }
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"application/json", "text/event-stream"})
    void openAiPromotesAndDeduplicatesOpenedAndFoundPagesRegardlessOfOrder(String mediaType) throws Exception {
        String normalizedOpen = OPEN.replace("https://example.com/facts", "https://EXAMPLE.COM:443/facts");
        JsonNode expected = args("""
                [{"url":"https://example.com/facts","opened":true},
                 {"url":"https://research.org/paper","opened":true},
                 {"url":"https://other.org/reference","opened":false}]
                """);
        for (String[] items : List.of(
                new String[] {SEARCH, normalizedOpen, FIND, OPEN, FIND},
                new String[] {normalizedOpen, FIND, SEARCH, OPEN, FIND})) {
            JsonNode result = snapshot(Provider.OPEN_AI, mediaType, 5, items);
            assertEquals(expected, result.path("sources"));
            assertEquals(1, result.path("searchCount").asInt());
            assertFalse(result.path("rawPageContent").asBoolean());
            JsonNode limited = snapshot(Provider.OPEN_AI, mediaType, 2, items);
            assertEquals(2, limited.path("sources").size());
            assertEquals(expected.get(0), limited.path("sources").get(0));
            assertEquals(expected.get(1), limited.path("sources").get(1));
            assertTrue(limited.path("truncated").asBoolean());
        }
    }

    @ParameterizedTest
    @EnumSource(Provider.class)
    void queryAndQueriesSearchActionsCountCallsNotQueriesAndMayOmitSources(Provider provider) throws Exception {
        String queries = SEARCH.replace("\"query\":\"facts\"", "\"queries\":[\"facts\",\"references\"]");
        String noSources = call("{\"type\":\"search\",\"queries\":[\"facts\"]}");
        String opened = provider == Provider.OPEN_AI ? OPEN : EXTRACTOR;
        for (String mediaType : MEDIA_TYPES) {
            JsonNode result = snapshot(provider, mediaType, 5, SEARCH, queries);
            assertEquals(2, result.path("searchCount").asInt());
            assertEquals(2, result.path("sources").size());
            JsonNode readResult = snapshot(provider, mediaType, 5, noSources, opened);
            assertEquals(1, readResult.path("searchCount").asInt());
            assertEquals(1, readResult.path("sources").size());
            assertTrue(readResult.path("sources").get(0).path("opened").asBoolean());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"application/json", "text/event-stream"})
    void dashScopeRetainsImplicitSearchAndExtractorFallbackButIgnoresOpenAiPageActions(String mediaType)
            throws Exception {
        String legacySearch = SEARCH.replace("\"type\":\"search\",", "").replace("\"type\":\"url\",", "");
        for (String[] items : List.of(
                new String[] {legacySearch, EXTRACTOR, OPEN, FIND},
                new String[] {EXTRACTOR, legacySearch, OPEN, FIND})) {
            JsonNode result = snapshot(Provider.DASH_SCOPE, mediaType, 1, items);
            assertEquals(1, result.path("searchCount").asInt());
            assertEquals("Extracted facts.", result.path("answer").asText());
            assertTrue(result.path("rawPageContent").asBoolean());
            assertTrue(result.path("truncated").asBoolean());
            assertEquals(args("[{\"url\":\"https://example.com/facts\",\"opened\":true}]"), result.path("sources"));
            assertEquals(
                    snapshot(Provider.DASH_SCOPE, mediaType, 5, legacySearch, EXTRACTOR),
                    snapshot(Provider.DASH_SCOPE, mediaType, 5, items));
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"application/json", "text/event-stream"})
    void openAiIgnoresExtractorItemsEntirelyIncludingMalformedOrOversizedUrls(String mediaType) throws Exception {
        String oversized = ToolArguments.result(Map.of(
                "type",
                "web_extractor_call",
                "status",
                "completed",
                "urls",
                Collections.nCopies(129, "https://extra.org")));
        for (String extractor : List.of(
                EXTRACTOR,
                EXTRACTOR.replace("example.com/facts", "extra.org/page"),
                EXTRACTOR.replace("[\"https://example.com/facts\"]", "{}"),
                oversized)) {
            JsonNode result = snapshot(Provider.OPEN_AI, mediaType, 5, SEARCH, extractor);
            assertEquals(snapshot(Provider.OPEN_AI, mediaType, 5, SEARCH), result);
            String noSources = call("{\"type\":\"search\",\"query\":\"facts\"}");
            assertThrows(IOException.class, () -> snapshot(Provider.OPEN_AI, mediaType, 5, noSources, extractor));
        }
    }

    @Test
    void openAiRejectsMissingNullUnknownAndOldFindActionTypesEvenWithOpenedPages() throws Exception {
        for (String type : List.of("", "null", "\"unknown\"", "\"find\"", "17")) {
            String action = "{" + (type.isEmpty() ? "" : "\"type\":" + type + ",")
                    + "\"query\":\"facts\",\"url\":\"https://evidence.org/page\","
                    + "\"sources\":[{\"type\":\"url\",\"url\":\"https://evidence.org/page\"}]}";
            for (String mediaType : MEDIA_TYPES) {
                assertThrows(
                        IOException.class, () -> snapshot(Provider.OPEN_AI, mediaType, 5, call(action), OPEN, MESSAGE));
                assertEquals(
                        snapshot(Provider.OPEN_AI, mediaType, 5, SEARCH),
                        snapshot(Provider.OPEN_AI, mediaType, 5, SEARCH, call(action)));
            }
        }
    }

    @ParameterizedTest
    @EnumSource(Provider.class)
    void narrationAnnotationsUsageAndPageOnlyResponsesAreNotCompletedSearchEvidence(Provider provider) {
        for (String[] items : List.of(
                new String[] {MESSAGE},
                new String[] {OPEN, FIND, MESSAGE},
                new String[] {EXTRACTOR, MESSAGE},
                new String[] {SEARCH.replace("completed", "failed"), OPEN, EXTRACTOR, MESSAGE},
                new String[] {SEARCH.replace("completed", "in_progress"), OPEN, EXTRACTOR, MESSAGE},
                new String[] {"{\"type\":\"web_search_call\",\"status\":\"completed\"}", OPEN, EXTRACTOR},
                new String[] {call("null"), OPEN, EXTRACTOR},
                new String[] {call("[]"), OPEN, EXTRACTOR},
                new String[] {call("\"search\""), OPEN, EXTRACTOR},
                new String[] {call("{\"type\":\"search\",\"query\":\"facts\"}"), MESSAGE},
                new String[] {"{\"type\":\"url_citation\",\"url\":\"https://annotation.com\"}"})) {
            for (String mediaType : MEDIA_TYPES) {
                assertThrows(IOException.class, () -> snapshot(provider, mediaType, 5, items));
            }
        }
        String usage =
                "{\"status\":\"completed\",\"usage\":{\"x_tools\":{\"web_search\":{\"count\":10}}},\"output\":[]}";
        assertThrows(IOException.class, () -> parse(provider, "application/json", usage, 5));
        assertThrows(IOException.class, () -> parse(provider, "text/event-stream", terminal(usage), 5));
    }

    @ParameterizedTest
    @EnumSource(Provider.class)
    void ignoresFailedAndInProgressCallsWithoutCountingOrPromotingThem(Provider provider) throws Exception {
        for (String status : List.of("failed", "in_progress", "cancelled")) {
            for (String mediaType : MEDIA_TYPES) {
                JsonNode result = snapshot(
                        provider,
                        mediaType,
                        5,
                        SEARCH,
                        SEARCH.replace("completed", status).replace("example.com", "failed.org"),
                        OPEN.replace("completed", status),
                        FIND.replace("completed", status),
                        EXTRACTOR.replace("completed", status));
                assertEquals(snapshot(provider, mediaType, 5, SEARCH), result);
            }
        }
    }

    @ParameterizedTest
    @EnumSource(Provider.class)
    void sseFallsBackToDoneItemsAndBoundedTextDeltas(Provider provider) throws Exception {
        String opened = provider == Provider.OPEN_AI ? OPEN : EXTRACTOR;
        String stream = done(SEARCH) + done(opened)
                + "data: {\"type\":\"response.output_text.delta\",\"delta\":\"Streamed facts\"}\n\n"
                + terminal(completed());
        JsonNode result = parse(provider, "text/event-stream", stream, 5);
        assertEquals(1, result.path("searchCount").asInt());
        assertEquals(2, result.path("sources").size());
        assertEquals(
                "https://example.com/facts",
                result.path("sources").get(0).path("url").asText());
        assertTrue(result.path("sources").get(0).path("opened").asBoolean());
        assertEquals("Streamed facts", result.path("answer").asText());
        assertFalse(result.path("rawPageContent").asBoolean());
    }

    @ParameterizedTest
    @EnumSource(Provider.class)
    void completedSseSnapshotReplacesTransientItemsAndPreventsDoubleCounting(Provider provider) throws Exception {
        String transientItems = done(SEARCH) + done(SEARCH.replace("example.com/facts", "transient.org/page"))
                + done(provider == Provider.OPEN_AI ? OPEN : EXTRACTOR)
                + "data: {\"type\":\"response.output_text.delta\",\"delta\":\"Transient answer\"}\n\n";
        JsonNode result =
                parse(provider, "text/event-stream", transientItems + terminal(completed(SEARCH, MESSAGE)), 5);
        assertEquals(snapshot(provider, "application/json", 5, SEARCH, MESSAGE), result);
        assertThrows(
                IOException.class,
                () -> parse(provider, "text/event-stream", transientItems + terminal(completed(MESSAGE)), 5));
    }

    @ParameterizedTest
    @EnumSource(Provider.class)
    void rejectsMalformedAndOversizedArraysEvenAlongsideValidEvidence(Provider provider) throws Exception {
        for (String shape : List.of("null", "{}", "1", "\"not-an-array\"", "[" + "{},".repeat(128) + "{}]")) {
            String badSources = call("{\"type\":\"search\",\"sources\":" + shape + "}");
            String badContent = "{\"type\":\"message\",\"content\":" + shape + "}";
            for (String mediaType : MEDIA_TYPES) {
                assertThrows(IOException.class, () -> snapshot(provider, mediaType, 5, SEARCH, badSources));
                assertThrows(IOException.class, () -> snapshot(provider, mediaType, 5, SEARCH, badContent));
                if (provider == Provider.DASH_SCOPE) {
                    String extractor =
                            "{\"type\":\"web_extractor_call\",\"status\":\"completed\",\"urls\":" + shape + "}";
                    assertThrows(IOException.class, () -> snapshot(provider, mediaType, 5, SEARCH, extractor));
                }
            }
            String badOutput = "{\"status\":\"completed\",\"output\":" + shape + "}";
            assertThrows(IOException.class, () -> parse(provider, "application/json", badOutput, 5));
            assertThrows(
                    IOException.class,
                    () -> parse(provider, "text/event-stream", done(SEARCH) + terminal(badOutput), 5));
        }
        String atLimit = call(ToolArguments.result(Map.of(
                "type",
                "search",
                "sources",
                Collections.nCopies(128, Map.of("type", "url", "url", "https://example.com")))));
        assertEquals(
                1,
                snapshot(provider, "application/json", 5, atLimit)
                        .path("sources")
                        .size());
    }

    @ParameterizedTest
    @EnumSource(Provider.class)
    void dropsUnsafeUrlsAndMalformedSourceEntriesWithoutUsingActionOrAnnotationLinks(Provider provider)
            throws Exception {
        String unsafe = call("""
                {"type":"search","query":"facts","url":"https://not-a-source.org",
                 "sources":[null,1,"https://not-a-source.org",[],{},
                   {"url":null},{"url":{}},{"url":1},{"url":"javascript:alert(1)"},
                   {"url":"https://user:password@evil.com/"},{"url":"http://example.com/"},
                   {"url":"https://127.0.0.1/"},{"url":"https://[::1]/"},
                   {"url":"https://service.internal/"},{"url":"https://host.localhost/"},
                   {"url":"https://host.local/"},{"url":"https://host.invalid/"},{"url":"https://host.test/"}]}
                """);
        JsonNode result = snapshot(provider, "application/json", 5, SEARCH, unsafe, MESSAGE);
        assertEquals(2, result.path("searchCount").asInt());
        assertEquals(snapshot(provider, "application/json", 5, SEARCH).path("sources"), result.path("sources"));
        for (String url : List.of("http://example.com/", "https://127.0.0.1/", "https://service.internal/")) {
            JsonNode pages = snapshot(
                    provider,
                    "application/json",
                    5,
                    SEARCH,
                    OPEN.replace("https://example.com/facts", url),
                    FIND.replace("https://research.org/paper", url));
            assertEquals(snapshot(provider, "application/json", 5, SEARCH), pages);
        }
    }

    @ParameterizedTest
    @EnumSource(Provider.class)
    void rejectsMalformedJsonAndNonCompletedOrErrorResponses(Provider provider) {
        for (String json : List.of(
                "not json",
                "[]",
                completed(SEARCH) + "{}",
                "{\"status\":\"completed\",\"status\":\"completed\"}",
                completed(SEARCH).replace("\"status\":\"completed\"", "\"status\":\"failed\""),
                "{\"status\":\"completed\",\"error\":{\"message\":\"provider-private\"},\"output\":[" + SEARCH
                        + "]}")) {
            IOException error = assertThrows(IOException.class, () -> parse(provider, "application/json", json, 5));
            assertFalse(error.toString().contains("provider-private"));
        }
    }

    @ParameterizedTest
    @EnumSource(Provider.class)
    void rejectsSseErrorsMissingCompletionAndExceededEventBudgets(Provider provider) {
        for (String sse : List.of(
                "data: [DONE]\n\n",
                done(SEARCH),
                "event: error\ndata: {\"message\":\"provider-private\"}\n\n",
                "data: {\"code\":\"provider-private\"}\n\n",
                "data: {\"error\":{\"message\":\"provider-private\"}}\n\n",
                "data: {\"type\":\"response.failed\"}\n\n",
                "data: {\"type\":\"response.incomplete\"}\n\n",
                "data: {\"type\":\"response.cancelled\"}\n\n",
                "data: not-json\n\n",
                "data: {\"type\":\"response.output_item.done\",\"item\":[]}\n\n",
                "data: {\"type\":\"response.output_text.delta\",\"delta\":\"" + "x".repeat(65_536) + "\"}\n\n",
                "data: {\"type\":\"heartbeat\"}\n\n".repeat(513),
                ": comment\n".repeat(4097),
                done("{}").repeat(129) + terminal(completed(SEARCH)),
                terminal(completed(SEARCH)) + "data: {\"type\":\"heartbeat\"}\n\n")) {
            IOException error = assertThrows(IOException.class, () -> parse(provider, "text/event-stream", sse, 5));
            assertFalse(error.toString().contains("provider-private"));
        }
    }

    @ParameterizedTest
    @EnumSource(Provider.class)
    void boundsAnswersAndSerializedUtf8WithEscapedUnicode(Provider provider) throws Exception {
        String message = ToolArguments.result(Map.of(
                "type",
                "message",
                "content",
                List.of(Map.of("type", "output_text", "text", "\"\\中文\uD834\uDD1E\u202e".repeat(9000)))));
        String output = WebSearchResponse.result(
                new NetworkHttp.Payload("application/json", completed(SEARCH, message)),
                1,
                new NetworkHttp.Deadline(context()),
                provider);
        JsonNode result = args(output);
        assertTrue(output.getBytes(StandardCharsets.UTF_8).length <= 65_536);
        assertEquals(1, result.path("sources").size());
        assertTrue(result.path("answer").asText().length() <= 6000);
        assertFalse(result.path("answer").asText().contains("\u202e"));
        assertTrue(result.path("truncated").asBoolean());
    }
}
