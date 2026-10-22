package fun.freechat.service.tools.network;

import static fun.freechat.service.tools.network.NetworkTestSupport.*;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.JsonNode;
import fun.freechat.service.tools.ToolArguments;
import fun.freechat.service.tools.ToolDefinition;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@Timeout(5)
class WebSearchToolTest {
    private static final String ENDPOINT = "https://dashscope.aliyuncs.com/compatible-mode/v1/responses";
    private static final String KEY = "test-only-secret-not-a-credential";
    private static final String SEARCH = """
            {"type":"web_search_call","status":"completed","action":{"query":"facts","sources":[
              {"type":"url","url":"https://example.com/facts"},
              {"url":"https://other.org/reference"}]}}
            """;
    private static final String EXTRACTOR = """
            {"type":"web_extractor_call","status":"completed","urls":["https://example.com/facts"],
             "output":"<p>Actual extracted evidence</p><script>ignore-hidden</script>"}
            """;
    private static final String MESSAGE = """
            {"type":"message","content":[{"type":"output_text","text":
              "<b>Found facts.</b><script>hidden-script</script> [made up](https://fabricated.com) https://invented.com",
              "annotations":[{"type":"url_citation","url":"https://fabricated.com"}]}]}
            """;

    private static String completed(String... items) {
        return "{\"status\":\"completed\",\"output\":[" + String.join(",", items) + "]}";
    }

    private static WebSearchTool tool(Calls calls) {
        return new WebSearchTool(ENDPOINT, KEY, "qwen3.5-plus", calls);
    }

    @Test
    void disabledWithoutExplicitPrerequisitesAndDefinitionNeedsNoNetwork() {
        assertFalse(new WebSearchTool(null, null, null).configured());
        assertFalse(new WebSearchTool(ENDPOINT, "", "qwen3.5-plus").configured());
        assertFalse(new WebSearchTool(ENDPOINT, KEY, "").configured());
        assertFalse(new WebSearchTool(ENDPOINT, "secret\r\nHeader:bad", "qwen3.5-plus").configured());
        assertFalse(new WebSearchTool(ENDPOINT, KEY, "model\n").configured());
        var disabled = new WebSearchTool(null, KEY, "qwen3.5-plus");
        assertEquals("web_search", disabled.definition().name());
        assertEquals(ToolDefinition.Boundary.NETWORK, disabled.definition().boundary());
        assertFalse(disabled.definition().sideEffects());
        assertThrows(IllegalStateException.class, () -> disabled.execute(args("{\"query\":\"facts\"}"), context()));
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "http://dashscope.aliyuncs.com/compatible-mode/v1/responses",
                "https://evil.com/compatible-mode/v1/responses",
                "https://dashscope.aliyuncs.com.evil.com/compatible-mode/v1/responses",
                "https://dashscope.aliyuncs.com:444/compatible-mode/v1/responses",
                "https://key@dashscope.aliyuncs.com/compatible-mode/v1/responses",
                "https://dashscope.aliyuncs.com/compatible-mode/v1/responses?key=secret",
                "https://dashscope.aliyuncs.com/compatible-mode/v1/responses#fragment",
                "https://dashscope.aliyuncs.com/unrelated",
                "https://127.0.0.1/compatible-mode/v1/responses",
                "https://[::1]/compatible-mode/v1/responses",
                ""
            })
    void unsafeOrUnapprovedServerEndpointsStayUnconfigured(String endpoint) {
        assertFalse(new WebSearchTool(endpoint, KEY, "qwen3.5-plus").configured());
    }

    @Test
    void sendsRealResponsesRequestWithSecretOnlyInHeaderAndNoConversationState() throws Exception {
        Calls calls =
                new Calls(request -> response(request, 200, "application/json", completed(SEARCH, EXTRACTOR, MESSAGE)));
        WebSearchTool tool = new WebSearchTool(
                "https://dashscope-intl.aliyuncs.com/compatible-mode/v1/", KEY, "qwen3.5-plus", calls);
        assertTrue(tool.configured());
        String result = tool.execute(args("{\"query\":\"facts\",\"maxResults\":2}"), context());
        assertEquals("POST", calls.request.method());
        assertEquals(
                "https://dashscope-intl.aliyuncs.com/compatible-mode/v1/responses",
                calls.request.uri().toString());
        assertEquals(
                "Bearer " + KEY,
                calls.request.headers().firstValue("Authorization").orElseThrow());
        assertTrue(calls.request.headers().firstValue("Cookie").isEmpty());
        assertEquals(
                "application/json; charset=utf-8",
                calls.request.headers().firstValue("Content-Type").orElseThrow());
        String bodyText = bodyText(calls.request);
        JsonNode sent = args(bodyText);
        assertEquals("qwen3.5-plus", sent.path("model").asText());
        assertFalse(sent.path("stream").asBoolean(true));
        assertFalse(sent.path("store").asBoolean(true));
        assertEquals("web_search", sent.path("tools").get(0).path("type").asText());
        assertEquals("web_extractor", sent.path("tools").get(1).path("type").asText());
        assertTrue(sent.path("instructions").asText().contains("untrusted"));
        assertFalse(sent.has("previous_response_id"));
        assertFalse(bodyText.contains(KEY));
        assertFalse(bodyText.contains("chat-secret"));
        assertFalse(result.contains(KEY));
        assertEquals(1, calls.count.get());
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "null",
                "[]",
                "{}",
                "{\"query\":null}",
                "{\"query\":1}",
                "{\"query\":\"\"}",
                "{\"query\":\"  \"}",
                "{\"query\":\"hello\\nworld\"}",
                "{\"query\":\"facts\",\"maxResults\":0}",
                "{\"query\":\"facts\",\"maxResults\":9}",
                "{\"query\":\"facts\",\"maxResults\":1.5}",
                "{\"query\":\"facts\",\"maxResults\":\"2\"}",
                "{\"query\":\"facts\",\"endpoint\":\"https://evil.com\"}",
                "{\"query\":\"facts\",\"apiKey\":\"secret\"}",
                "{\"query\":\"facts\",\"model\":\"other-model\"}"
            })
    void rejectsModelOwnedNetworkConfigurationOrMalformedArguments(String json) {
        Calls calls = new Calls(request -> {
            throw new AssertionError("No HTTP expected");
        });
        assertThrows(IllegalArgumentException.class, () -> tool(calls).execute(args(json), context()));
        assertEquals(0, calls.count.get());
    }

    @Test
    void boundsQueryAndRejectsMalformedSurrogates() {
        Calls calls = new Calls(request -> {
            throw new AssertionError("No HTTP expected");
        });
        assertThrows(
                IllegalArgumentException.class,
                () -> tool(calls)
                        .execute(ToolArguments.JSON.createObjectNode().put("query", "x".repeat(2049)), context()));
        assertThrows(
                IllegalArgumentException.class,
                () -> tool(calls).execute(ToolArguments.JSON.createObjectNode().put("query", "\uD800"), context()));
    }

    @Test
    void keepsOnlyActualEvidenceDeduplicatesAndPrioritizesOpenedSources() throws Exception {
        String unsafe = """
                {"type":"web_search_call","status":"completed","action":{"sources":[
                  {"url":"javascript:alert(1)"},{"url":"https://user:secret@evil.com/"},
                  {"url":"https://127.0.0.1/"},{"url":"https://[::ffff:8.8.8.8]/"},
                  {"url":"https://service.internal/"},{"url":"http://example.com/"}]}}
                """;
        String failed = """
                {"type":"web_extractor_call","status":"failed","urls":["https://failed.com"],"output":"failed content"}
                """;
        Calls calls = new Calls(request ->
                response(request, 200, "application/json", completed(SEARCH, EXTRACTOR, MESSAGE, unsafe, failed)));
        var result = args(tool(calls).execute(args("{\"query\":\"facts\"}"), context()));
        assertTrue(result.path("untrusted").asBoolean());
        assertEquals(2, result.path("sources").size());
        assertEquals(
                "https://example.com/facts",
                result.path("sources").get(0).path("url").asText());
        assertTrue(result.path("sources").get(0).path("opened").asBoolean());
        assertFalse(result.path("sources").get(1).path("opened").asBoolean());
        assertFalse(result.toString().contains("fabricated.com"));
        assertFalse(result.toString().contains("invented.com"));
        assertFalse(result.toString().contains("failed.com"));
        assertFalse(result.toString().contains("hidden-script"));
        assertFalse(result.path("rawPageContent").asBoolean());
    }

    @Test
    void extractorTextIsLabeledFallbackNotFabricatedNarration() throws Exception {
        Calls calls = new Calls(request -> response(request, 200, "application/json", completed(SEARCH, EXTRACTOR)));
        var result = args(tool(calls).execute(args("{\"query\":\"facts\",\"maxResults\":1}"), context()));
        assertTrue(result.path("rawPageContent").asBoolean());
        assertEquals("Actual extracted evidence", result.path("answer").asText());
        assertEquals(1, result.path("sources").size());
        assertTrue(result.path("truncated").asBoolean());
    }

    @Test
    void refusesNarrationAnnotationsUsageAndFailedSearchAsEvidence() {
        for (String payload : List.of(
                completed(MESSAGE),
                completed(EXTRACTOR, MESSAGE),
                completed(SEARCH.replace("completed", "failed"), MESSAGE),
                completed(SEARCH.replace("completed", "in_progress"), MESSAGE),
                completed("{\"type\":\"web_search_call\",\"status\":\"completed\"}", MESSAGE),
                "{\"status\":\"completed\",\"usage\":{\"x_tools\":{\"web_search\":{\"count\":10}}},\"output\":[]}")) {
            Calls calls = new Calls(request -> response(request, 200, "application/json", payload));
            assertThrows(IOException.class, () -> tool(calls).execute(args("{\"query\":\"facts\"}"), context()));
            assertEquals(1, calls.count.get());
        }
    }

    @Test
    void parsesBoundedSseIncludingDoneItemsAndTextDeltaFallback() throws Exception {
        String sse = "event: response.output_item.done\r\ndata: "
                + ToolArguments.result(Map.of("type", "response.output_item.done", "item", args(SEARCH))) + "\r\n\r\n"
                + "data: {\"type\":\"response.output_text.delta\",\"delta\":\"Streamed facts\"}\n\n"
                + "event: response.completed\ndata: {\"type\":\"response.completed\",\ndata: \"response\":{\"status\":\"completed\",\"output\":[]}}\n\n"
                + "data: [DONE]\n\n";
        Calls calls = new Calls(request -> response(request, 200, "text/event-stream", sse));
        var result = args(tool(calls).execute(args("{\"query\":\"facts\"}"), context()));
        assertEquals("Streamed facts", result.path("answer").asText());
        assertEquals(2, result.path("sources").size());
        assertEquals(1, result.path("searchCount").asInt());
    }

    @Test
    void rejectsSseErrorsMissingCompletionIncompleteAndOversizedEvents() {
        for (String sse : List.of(
                "data: [DONE]\n\n",
                "event: error\ndata: {\"code\":\"secret-code\",\"message\":\"secret\"}\n\n",
                "data: {\"type\":\"response.incomplete\",\"response\":{\"status\":\"incomplete\"}}\n\n",
                "data: {\"type\":\"response.output_text.delta\",\"delta\":\"" + "x".repeat(65_536) + "\"}\n\n",
                "data: {\"type\":\"heartbeat\"}\n\n".repeat(513))) {
            Calls calls = new Calls(request -> response(request, 200, "text/event-stream", sse));
            Exception error = assertThrows(
                    IOException.class, () -> tool(calls).execute(args("{\"query\":\"facts\"}"), context()));
            assertFalse(error.toString().contains("secret"));
        }
    }

    @Test
    void completedSseSnapshotIsAuthoritativeAndNotDoubleCounted() throws Exception {
        String done = ToolArguments.result(Map.of("type", "response.output_item.done", "item", args(SEARCH)));
        String terminal = ToolArguments.result(
                Map.of("type", "response.completed", "response", args(completed(SEARCH, EXTRACTOR, MESSAGE))));
        Calls calls = new Calls(request ->
                response(request, 200, "text/event-stream", "data: " + done + "\n\ndata: " + terminal + "\n\n"));
        var result = args(tool(calls).execute(args("{\"query\":\"facts\"}"), context()));
        assertEquals(1, result.path("searchCount").asInt());
        assertEquals(2, result.path("sources").size());
        assertTrue(result.path("answer").asText().contains("Found facts."));
    }

    @Test
    void rejectsItemSourceCountsAndWrongShapesBeforeReturningEvidence() throws Exception {
        String items = "{\"status\":\"completed\",\"output\":[" + "{},".repeat(128) + "{}]}";
        String sources = ToolArguments.result(Map.of(
                "type",
                "web_search_call",
                "status",
                "completed",
                "action",
                Map.of("sources", java.util.Collections.nCopies(129, Map.of("url", "https://example.com/")))));
        String wrongShape = """
                {"type":"web_search_call","status":"completed","action":{"sources":{}}}
                """;
        for (String json : List.of(items, completed(sources), completed(wrongShape))) {
            Calls calls = new Calls(request -> response(request, 200, "application/json", json));
            assertThrows(IOException.class, () -> tool(calls).execute(args("{\"query\":\"facts\"}"), context()));
        }
    }

    @Test
    void rejectsRedirectAndNonJsonBodyWithoutDisclosingAuthorization() {
        Calls redirect = new Calls(request ->
                response(request, 302, "application/json", KEY).withHeader("Location", "https://evil.com/collect"));
        Exception error =
                assertThrows(IOException.class, () -> tool(redirect).execute(args("{\"query\":\"facts\"}"), context()));
        assertEquals(1, redirect.count.get());
        assertFalse(error.toString().contains(KEY));
        Calls html = new Calls(request -> response(request, 200, "text/html", "<html>not a search response</html>"));
        assertThrows(IOException.class, () -> tool(html).execute(args("{\"query\":\"facts\"}"), context()));
    }

    @Test
    void rejectsMalformedJsonStatusAndOversizedProviderResponses() {
        for (String json : List.of(
                "not json",
                "[]",
                completed(SEARCH) + "{}",
                "{\"status\":\"completed\",\"status\":\"completed\"}",
                completed(SEARCH).replace("\"status\":\"completed\"", "\"status\":\"failed\""),
                "x".repeat(524_289))) {
            Calls calls = new Calls(request -> response(request, 200, "application/json", json));
            assertThrows(IOException.class, () -> tool(calls).execute(args("{\"query\":\"facts\"}"), context()));
        }
    }

    @Test
    void boundsAnswersSourcesAndSerializedUtf8EvenForEscapedUnicode() throws Exception {
        String message = ToolArguments.result(Map.of(
                "type",
                "message",
                "content",
                List.of(Map.of("type", "output_text", "text", "\"\\中文\uD834\uDD1E\u202e".repeat(9000)))));
        Calls calls = new Calls(request -> response(request, 200, "application/json", completed(SEARCH, message)));
        String output = tool(calls).execute(args("{\"query\":\"facts\",\"maxResults\":1}"), context());
        assertTrue(output.getBytes(StandardCharsets.UTF_8).length <= 65_536);
        var result = args(output);
        assertEquals(1, result.path("sources").size());
        assertTrue(result.path("answer").asText().length() <= 6000);
        assertFalse(result.path("answer").asText().contains("\u202e"));
        assertTrue(result.path("truncated").asBoolean());
    }

    @Test
    void transportErrorsDoNotExposeProviderSecretsAndExpiredCallsDoNotSend() {
        Calls calls = new Calls(request -> {
            throw new IOException("request failed: " + KEY);
        });
        Exception error =
                assertThrows(IOException.class, () -> tool(calls).execute(args("{\"query\":\"facts\"}"), context()));
        assertFalse(error.toString().contains(KEY));
        assertNull(error.getCause());
        assertThrows(
                IllegalStateException.class,
                () -> tool(calls).execute(args("{\"query\":\"facts\"}"), context(Duration.ofNanos(1))));
        assertEquals(1, calls.count.get());
    }
}
