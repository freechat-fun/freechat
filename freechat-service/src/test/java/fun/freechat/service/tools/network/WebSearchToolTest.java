package fun.freechat.service.tools.network;

import static fun.freechat.service.tools.network.NetworkTestSupport.*;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.JsonNode;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.output.FinishReason;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.MemoryId;
import fun.freechat.service.tools.InProcessToolRuntime;
import fun.freechat.service.tools.ToolArguments;
import fun.freechat.service.tools.ToolDefinition;
import fun.freechat.service.tools.ToolPolicySource;
import fun.freechat.service.tools.ToolRegistry;
import fun.freechat.service.tools.ToolRuntimeBroker;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

@Timeout(5)
class WebSearchToolTest {
    private static final String ENDPOINT = "https://dashscope.aliyuncs.com/compatible-mode/v1/responses";
    private static final String OPEN_AI_ENDPOINT = "https://api.openai.com/v1/responses";
    private static final String KEY = "test-only-secret-not-a-credential";
    private static final String SEARCH = """
            {"type":"web_search_call","status":"completed","action":{"type":"search","query":"facts","sources":[
              {"type":"url","url":"https://example.com/facts"},
              {"type":"url","url":"https://other.org/reference"}]}}
            """;
    private static final String EXTRACTOR = """
            {"type":"web_extractor_call","status":"completed","urls":["https://example.com/facts"],
             "output":"<p>Actual extracted evidence</p><script>ignore-hidden</script>"}
            """;
    private static final String OPEN_PAGE = """
            {"type":"web_search_call","status":"completed","action":{
              "type":"open_page","url":"https://example.com/facts"}}
            """;
    private static final String MESSAGE = """
            {"type":"message","content":[{"type":"output_text","text":
              "<b>Found facts.</b><script>hidden-script</script> [made up](https://fabricated.com) https://invented.com",
              "annotations":[{"type":"url_citation","url":"https://fabricated.com"}]}]}
            """;

    private enum Provider {
        DASH_SCOPE(ENDPOINT, "[dash_scope]qwen3.5-plus", "qwen3.5-plus", EXTRACTOR),
        OPEN_AI(OPEN_AI_ENDPOINT, "[open_ai]gpt-4.1-mini", "gpt-4.1-mini", OPEN_PAGE);

        private final String endpoint;
        private final String modelId;
        private final String wireModel;
        private final String opened;

        Provider(String endpoint, String modelId, String wireModel, String opened) {
            this.endpoint = endpoint;
            this.modelId = modelId;
            this.wireModel = wireModel;
            this.opened = opened;
        }
    }

    private static String completed(String... items) {
        return "{\"status\":\"completed\",\"output\":[" + String.join(",", items) + "]}";
    }

    private static WebSearchTool tool(Calls calls) {
        return tool(Provider.DASH_SCOPE, calls);
    }

    private static WebSearchTool tool(Provider provider, Calls calls) {
        return new WebSearchTool(provider.endpoint, KEY, provider.modelId, calls);
    }

    private static void assertDisabled(String endpoint, String key, String modelId) {
        Calls calls = new Calls(request -> {
            throw new AssertionError("Unconfigured search must not make HTTP requests");
        });
        var disabled = new WebSearchTool(endpoint, key, modelId, calls);
        assertFalse(disabled.configured(), () -> endpoint + " / " + modelId);
        assertThrows(IllegalStateException.class, () -> disabled.execute(args("{\"query\":\"facts\"}"), context()));
        assertEquals(0, calls.count.get());
    }

    @ParameterizedTest
    @EnumSource(Provider.class)
    void disabledWithoutExplicitPrerequisitesAndDefinitionNeedsNoNetwork(Provider provider) {
        assertFalse(new WebSearchTool(null, null, null).configured());
        for (String endpoint : new String[] {null, "", " \t "}) {
            assertDisabled(endpoint, KEY, provider.modelId);
        }
        for (String key : new String[] {null, "", " ", "secret\r\nHeader:bad"}) {
            assertDisabled(provider.endpoint, key, provider.modelId);
        }
        assertDisabled(provider.endpoint, KEY, null);
        var disabled = new WebSearchTool(null, KEY, provider.modelId);
        assertEquals("web_search", disabled.definition().name());
        assertEquals(ToolDefinition.Boundary.NETWORK, disabled.definition().boundary());
        assertFalse(disabled.definition().sideEffects());
        assertThrows(IllegalStateException.class, () -> disabled.execute(args("{\"query\":\"facts\"}"), context()));
    }

    @ParameterizedTest
    @MethodSource("acceptedEndpoints")
    void acceptsDocumentedEndpointsAndPinsExactlyOneHostWithoutDiscovery(
            Provider provider, String endpoint, String expected) throws Exception {
        Calls calls = new Calls(request -> response(request, 200, "application/json", completed(SEARCH, MESSAGE)));
        var tool = new WebSearchTool(endpoint, KEY, provider.modelId, calls);
        assertTrue(tool.configured());
        assertEquals(0, calls.count.get());
        // Inspect the security boundary, not just the request: other approved provider hosts must not be allowed.
        var httpField = WebSearchTool.class.getDeclaredField("http");
        httpField.setAccessible(true);
        var validatorField = NetworkHttp.class.getDeclaredField("validateUrl");
        validatorField.setAccessible(true);
        @SuppressWarnings("unchecked")
        var validateUrl = (java.util.function.Function<String, URI>) validatorField.get(httpField.get(tool));
        assertEquals(URI.create(expected), validateUrl.apply(expected));
        for (String other : List.of(
                OPEN_AI_ENDPOINT,
                ENDPOINT,
                "https://workspace-other.cn-beijing.maas.aliyuncs.com/compatible-mode/v1/responses")) {
            if (!URI.create(other).getHost().equals(URI.create(expected).getHost())) {
                assertThrows(IllegalArgumentException.class, () -> validateUrl.apply(other));
            }
        }
        tool.execute(args("{\"query\":\"facts\"}"), context());
        assertEquals(expected, calls.request.uri().toString());
        assertEquals(1, calls.count.get());
    }

    static Stream<Arguments> acceptedEndpoints() {
        return Stream.of(
                        "api.openai.com",
                        "dashscope.aliyuncs.com",
                        "dashscope-intl.aliyuncs.com",
                        "dashscope-us.aliyuncs.com",
                        "workspace-123.cn-beijing.maas.aliyuncs.com",
                        "workspace-123.ap-southeast-1.maas.aliyuncs.com",
                        "workspace-123.us-east-1.maas.aliyuncs.com",
                        "workspace-123.eu-central-1.maas.aliyuncs.com",
                        "workspace-123.ap-northeast-1.maas.aliyuncs.com",
                        "workspace-123.cn-hongkong.maas.aliyuncs.com")
                .flatMap(host -> {
                    Provider provider = host.equals("api.openai.com") ? Provider.OPEN_AI : Provider.DASH_SCOPE;
                    List<String> paths = provider == Provider.OPEN_AI
                            ? List.of("/v1")
                            : host.endsWith(".maas.aliyuncs.com")
                                    ? List.of("/compatible-mode/v1")
                                    : List.of("/compatible-mode/v1", "/api/v2/apps/protocols/compatible-mode/v1");
                    return paths.stream()
                            .flatMap(path -> Stream.of("", "/", "/responses", "/responses/")
                                    .flatMap(suffix -> Stream.of("", ":443")
                                            .map(port -> Arguments.of(
                                                    provider,
                                                    "https://" + host + port + path + suffix,
                                                    "https://" + host + path + "/responses"))));
                });
    }

    @ParameterizedTest
    @EnumSource(Provider.class)
    void unsafeOrUnapprovedServerEndpointsStayUnconfigured(Provider provider) {
        URI endpoint = URI.create(provider.endpoint);
        String host = endpoint.getHost();
        String path = endpoint.getPath();
        String base = "https://" + host;
        String apiPath = path.substring(0, path.length() - "/responses".length());
        for (String invalid : List.of(
                "http://" + host + path,
                "https://evil.com" + path,
                "https://" + host + ".evil.com" + path,
                "https://sub." + host + path,
                "https://" + host + ":444" + path,
                "https://" + host + ":80" + path,
                "https://" + host + ":" + path,
                "https://key@" + host + path,
                "https://user:secret@" + host + path,
                provider.endpoint + "?key=secret",
                provider.endpoint + "?",
                provider.endpoint + "#fragment",
                provider.endpoint + "#",
                "https://127.0.0.1" + path,
                "https://[::1]" + path,
                "https://2130706433" + path,
                base,
                base + "/unrelated",
                base + apiPath + "/chat/completions",
                base + apiPath + "/%72esponses",
                base + apiPath + "%2fresponses",
                base + apiPath.replace("/v1", "/%761") + "/responses",
                base + apiPath + "/./responses",
                base + apiPath + "/other/../responses",
                base + apiPath + "/%2e/responses",
                base + apiPath + "//responses",
                provider.endpoint + "//")) {
            assertDisabled(invalid, KEY, provider.modelId);
        }
    }

    @Test
    void rejectsProviderHostMismatchAndUndocumentedWorkspaceHostsOrLegacyWorkspacePaths() {
        assertDisabled(OPEN_AI_ENDPOINT, KEY, Provider.DASH_SCOPE.modelId);
        for (String host : List.of(
                "dashscope.aliyuncs.com",
                "dashscope-intl.aliyuncs.com",
                "dashscope-us.aliyuncs.com",
                "workspace-123.cn-beijing.maas.aliyuncs.com")) {
            assertDisabled("https://" + host + "/compatible-mode/v1", KEY, Provider.OPEN_AI.modelId);
            assertDisabled("https://" + host + "/v1/responses", KEY, Provider.OPEN_AI.modelId);
            assertDisabled("https://" + host + "/v1/responses", KEY, Provider.DASH_SCOPE.modelId);
        }
        for (String host : List.of(
                "api.openai.com",
                "cn-beijing.maas.aliyuncs.com",
                "maas.aliyuncs.com",
                "workspace-123.eu-west-1.maas.aliyuncs.com",
                "workspace-123.cn-shanghai.maas.aliyuncs.com",
                "nested.workspace-123.cn-beijing.maas.aliyuncs.com",
                "workspace-123.cn-beijing.maas.aliyuncs.com.evil.com",
                "-workspace.cn-beijing.maas.aliyuncs.com",
                "workspace-.cn-beijing.maas.aliyuncs.com",
                "work_space.cn-beijing.maas.aliyuncs.com",
                "x".repeat(64) + ".cn-beijing.maas.aliyuncs.com")) {
            assertDisabled("https://" + host + "/compatible-mode/v1/responses", KEY, Provider.DASH_SCOPE.modelId);
        }
        for (String suffix : List.of("", "/", "/responses", "/responses/")) {
            assertDisabled(
                    "https://workspace-123.cn-beijing.maas.aliyuncs.com/api/v2/apps/protocols/compatible-mode/v1"
                            + suffix,
                    KEY,
                    Provider.DASH_SCOPE.modelId);
            assertDisabled(
                    "https://api.openai.com/api/v2/apps/protocols/compatible-mode/v1" + suffix,
                    KEY,
                    Provider.OPEN_AI.modelId);
            assertDisabled("https://api.openai.com/compatible-mode/v1" + suffix, KEY, Provider.OPEN_AI.modelId);
        }
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "",
                " ",
                "qwen3.5-plus",
                "gpt-4.1-mini",
                "model\n",
                "open_ai:gpt-4.1-mini",
                "[open_ai]",
                "[dash_scope]",
                "[]gpt-4.1-mini",
                "[unknown]gpt-4.1-mini",
                "[ollama]qwen3.5-plus",
                "[openai]gpt-4.1-mini",
                "[dashscope]qwen3.5-plus",
                "[open_ai]gpt-4.1-mini|image2text",
                "[dash_scope]qwen3.5-plus|embedding",
                "[open_ai]gpt-4.1-mini|text2image",
                "[dash_scope]qwen3.5-plus|unknown",
                "[open_ai]gpt-4.1-mini|text2chat|text2chat",
                "[dash_scope]qwen3.5-plus|text2chat|other",
                "[open_ai]gpt 4.1-mini",
                "[dash_scope]qwen\r\nHeader:bad",
                "[open_ai]gpt-4.1-mini\n"
            })
    void invalidOrUnsupportedModelIdsNeverReachTransport(String modelId) {
        for (Provider provider : Provider.values()) {
            assertDisabled(provider.endpoint, KEY, modelId);
        }
    }

    @ParameterizedTest
    @EnumSource(Provider.class)
    void sendsRealResponsesRequestWithSecretOnlyInHeaderAndNoConversationState(Provider provider) throws Exception {
        for (String suffix : List.of("", "|text2chat")) {
            Calls calls = new Calls(
                    request -> response(request, 200, "application/json", completed(SEARCH, provider.opened, MESSAGE)));
            String base = provider == Provider.DASH_SCOPE
                    ? "https://dashscope-intl.aliyuncs.com/compatible-mode/v1/"
                    : "https://api.openai.com/v1/";
            var tool = new WebSearchTool(base, KEY, provider.modelId + suffix, calls);
            assertTrue(tool.configured());
            String result = tool.execute(args("{\"query\":\"facts\",\"maxResults\":2}"), context());
            assertEquals(base + "responses", calls.request.uri().toString());
            assertProviderRequest(provider, calls);
            assertFalse(result.contains(KEY));
            assertEquals(1, calls.count.get());
        }
    }

    private static void assertProviderRequest(Provider provider, Calls calls) throws IOException {
        assertEquals("POST", calls.request.method());
        assertEquals(List.of("Bearer " + KEY), calls.request.headers().allValues("Authorization"));
        assertTrue(calls.request.headers().firstValue("Cookie").isEmpty());
        assertEquals(
                "application/json; charset=utf-8",
                calls.request.headers().firstValue("Content-Type").orElseThrow());
        assertEquals(
                "identity",
                calls.request.headers().firstValue("Accept-Encoding").orElseThrow());
        calls.request.headers().map().forEach((name, values) -> {
            if (!name.equalsIgnoreCase("Authorization")) {
                values.forEach(value -> assertFalse(value.contains(KEY)));
            }
        });
        assertFalse(calls.request.uri().toString().contains(KEY));
        String bodyText = bodyText(calls.request);
        JsonNode sent = args(bodyText);
        assertEquals(provider.wireModel, sent.path("model").asText());
        assertEquals(args("false"), sent.path("stream"));
        assertEquals(args("false"), sent.path("store"));
        assertEquals("Perform a web search for: facts", sent.path("input").asText());
        assertTrue(sent.path("instructions").asText().contains("untrusted"));
        if (provider == Provider.OPEN_AI) {
            assertEquals(8, sent.size());
            assertEquals(args("[{\"type\":\"web_search\"}]"), sent.path("tools"));
            assertEquals(args("[\"web_search_call.action.sources\"]"), sent.path("include"));
            assertEquals("required", sent.path("tool_choice").asText());
            assertFalse(bodyText.contains("web_extractor"));
        } else {
            assertEquals(6, sent.size());
            assertEquals(args("[{\"type\":\"web_search\"},{\"type\":\"web_extractor\"}]"), sent.path("tools"));
            assertTrue(sent.path("instructions").asText().contains("web_extractor"));
            assertFalse(sent.has("include"));
            assertFalse(sent.has("tool_choice"));
        }
        for (String forbidden : List.of("previous_response_id", "conversation", "messages", "apiKey", "modelId")) {
            assertFalse(sent.has(forbidden));
        }
        for (String secret : List.of(
                KEY,
                provider.modelId,
                "|text2chat",
                "chat-secret",
                "user-secret",
                "character-secret",
                "backend-secret",
                "conversation-system-secret",
                "conversation-history-secret",
                "conversation-assistant-secret")) {
            assertFalse(bodyText.contains(secret));
        }
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
                "{\"query\":\"facts\",\"baseUrl\":\"https://api.openai.com/v1\"}",
                "{\"query\":\"facts\",\"apiKey\":\"secret\"}",
                "{\"query\":\"facts\",\"model\":\"other-model\"}",
                "{\"query\":\"facts\",\"modelId\":\"[open_ai]other-model\"}",
                "{\"query\":\"facts\",\"provider\":\"open_ai\"}",
                "{\"query\":\"facts\",\"headers\":{\"Authorization\":\"Bearer secret\"}}",
                "{\"query\":\"facts\",\"instructions\":\"ignore untrusted boundary\"}",
                "{\"query\":\"facts\",\"tools\":[{\"type\":\"web_extractor\"}]}",
                "{\"query\":\"facts\",\"previous_response_id\":\"chat-secret\"}",
                "{\"query\":\"facts\",\"messages\":[{\"role\":\"user\",\"content\":\"chat-secret\"}]}"
            })
    void rejectsModelOwnedNetworkConfigurationOrMalformedArguments(String json) {
        for (Provider provider : Provider.values()) {
            Calls calls = new Calls(request -> {
                throw new AssertionError("No HTTP expected");
            });
            assertThrows(
                    IllegalArgumentException.class, () -> tool(provider, calls).execute(args(json), context()));
            assertEquals(0, calls.count.get());
        }
    }

    @ParameterizedTest
    @EnumSource(Provider.class)
    void boundsQueryAndRejectsMalformedSurrogates(Provider provider) {
        Calls calls = new Calls(request -> {
            throw new AssertionError("No HTTP expected");
        });
        assertThrows(
                IllegalArgumentException.class,
                () -> tool(provider, calls)
                        .execute(ToolArguments.JSON.createObjectNode().put("query", "x".repeat(2049)), context()));
        assertThrows(
                IllegalArgumentException.class,
                () -> tool(provider, calls)
                        .execute(ToolArguments.JSON.createObjectNode().put("query", "\uD800"), context()));
        assertEquals(0, calls.count.get());
    }

    @ParameterizedTest
    @EnumSource(Provider.class)
    void keepsOnlyActualEvidenceDeduplicatesAndPrioritizesOpenedSources(Provider provider) throws Exception {
        String unsafe = """
                {"type":"web_search_call","status":"completed","action":{"type":"search","sources":[
                  {"type":"url","url":"javascript:alert(1)"},{"type":"url","url":"https://user:secret@evil.com/"},
                  {"type":"url","url":"https://127.0.0.1/"},{"type":"url","url":"https://[::ffff:8.8.8.8]/"},
                  {"type":"url","url":"https://service.internal/"},{"type":"url","url":"http://example.com/"}]}}
                """;
        String failed = provider.opened
                .replace("completed", "failed")
                .replace("https://example.com/facts", "https://failed.com");
        Calls calls = new Calls(request -> response(
                request, 200, "application/json", completed(SEARCH, provider.opened, MESSAGE, unsafe, failed)));
        var result = args(tool(provider, calls).execute(args("{\"query\":\"facts\"}"), context()));
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
        String legacySearch = SEARCH.replace("\"type\":\"search\",", "").replace("\"type\":\"url\",", "");
        Calls calls =
                new Calls(request -> response(request, 200, "application/json", completed(legacySearch, EXTRACTOR)));
        var result = args(tool(calls).execute(args("{\"query\":\"facts\",\"maxResults\":1}"), context()));
        assertTrue(result.path("rawPageContent").asBoolean());
        assertEquals("Actual extracted evidence", result.path("answer").asText());
        assertEquals(1, result.path("sources").size());
        assertTrue(result.path("truncated").asBoolean());
    }

    @ParameterizedTest
    @EnumSource(Provider.class)
    void refusesNarrationAnnotationsUsageAndFailedSearchAsEvidence(Provider provider) {
        for (String payload : List.of(
                completed(MESSAGE),
                completed(provider.opened, MESSAGE),
                completed(SEARCH.replace("completed", "failed"), MESSAGE),
                completed(SEARCH.replace("completed", "in_progress"), MESSAGE),
                completed("{\"type\":\"web_search_call\",\"status\":\"completed\"}", MESSAGE),
                "{\"status\":\"completed\",\"usage\":{\"x_tools\":{\"web_search\":{\"count\":10}}},\"output\":[]}")) {
            Calls calls = new Calls(request -> response(request, 200, "application/json", payload));
            assertThrows(
                    IOException.class, () -> tool(provider, calls).execute(args("{\"query\":\"facts\"}"), context()));
            assertEquals(1, calls.count.get());
        }
    }

    @ParameterizedTest
    @EnumSource(Provider.class)
    void parsesBoundedSseIncludingDoneItemsAndTextDeltaFallback(Provider provider) throws Exception {
        String sse = "event: response.output_item.done\r\ndata: "
                + ToolArguments.result(Map.of("type", "response.output_item.done", "item", args(SEARCH))) + "\r\n\r\n"
                + "data: {\"type\":\"response.output_text.delta\",\"delta\":\"Streamed facts\"}\n\n"
                + "event: response.completed\ndata: {\"type\":\"response.completed\",\ndata: \"response\":{\"status\":\"completed\",\"output\":[]}}\n\n"
                + "data: [DONE]\n\n";
        Calls calls = new Calls(request -> response(request, 200, "text/event-stream", sse));
        var result = args(tool(provider, calls).execute(args("{\"query\":\"facts\"}"), context()));
        assertEquals("Streamed facts", result.path("answer").asText());
        assertEquals(2, result.path("sources").size());
        assertEquals(1, result.path("searchCount").asInt());
    }

    @ParameterizedTest
    @EnumSource(Provider.class)
    void rejectsSseErrorsMissingCompletionIncompleteAndOversizedEvents(Provider provider) {
        for (String sse : List.of(
                "data: [DONE]\n\n",
                "event: error\ndata: {\"code\":\"secret-code\",\"message\":\"secret\"}\n\n",
                "data: {\"type\":\"response.incomplete\",\"response\":{\"status\":\"incomplete\"}}\n\n",
                "data: {\"type\":\"response.output_text.delta\",\"delta\":\"" + "x".repeat(65_536) + "\"}\n\n",
                "data: {\"type\":\"heartbeat\"}\n\n".repeat(513))) {
            Calls calls = new Calls(request -> response(request, 200, "text/event-stream", sse));
            Exception error = assertThrows(
                    IOException.class, () -> tool(provider, calls).execute(args("{\"query\":\"facts\"}"), context()));
            assertFalse(error.toString().contains("secret"));
            assertNull(error.getCause());
        }
    }

    @ParameterizedTest
    @EnumSource(Provider.class)
    void completedSseSnapshotIsAuthoritativeAndNotDoubleCounted(Provider provider) throws Exception {
        String done = ToolArguments.result(Map.of("type", "response.output_item.done", "item", args(SEARCH)));
        String terminal = ToolArguments.result(
                Map.of("type", "response.completed", "response", args(completed(SEARCH, provider.opened, MESSAGE))));
        Calls calls = new Calls(request ->
                response(request, 200, "text/event-stream", "data: " + done + "\n\ndata: " + terminal + "\n\n"));
        var result = args(tool(provider, calls).execute(args("{\"query\":\"facts\"}"), context()));
        assertEquals(1, result.path("searchCount").asInt());
        assertEquals(2, result.path("sources").size());
        assertTrue(result.path("answer").asText().contains("Found facts."));
    }

    @ParameterizedTest
    @EnumSource(Provider.class)
    void rejectsItemSourceCountsAndWrongShapesBeforeReturningEvidence(Provider provider) throws Exception {
        String items = "{\"status\":\"completed\",\"output\":[" + "{},".repeat(128) + "{}]}";
        String sources = ToolArguments.result(Map.of(
                "type",
                "web_search_call",
                "status",
                "completed",
                "action",
                Map.of(
                        "type",
                        "search",
                        "sources",
                        java.util.Collections.nCopies(129, Map.of("type", "url", "url", "https://example.com/")))));
        String wrongShape = """
                {"type":"web_search_call","status":"completed","action":{"type":"search","sources":{}}}
                """;
        for (String json : List.of(items, completed(sources), completed(wrongShape))) {
            Calls calls = new Calls(request -> response(request, 200, "application/json", json));
            assertThrows(
                    IOException.class, () -> tool(provider, calls).execute(args("{\"query\":\"facts\"}"), context()));
        }
    }

    @ParameterizedTest
    @EnumSource(Provider.class)
    void rejectsRedirectAndNonJsonBodyWithoutDisclosingAuthorization(Provider provider) {
        Calls redirect = new Calls(request ->
                response(request, 302, "application/json", KEY).withHeader("Location", "https://evil.com/collect"));
        Exception error = assertThrows(
                IOException.class, () -> tool(provider, redirect).execute(args("{\"query\":\"facts\"}"), context()));
        assertEquals(1, redirect.count.get());
        assertFalse(error.toString().contains(KEY));
        Calls html = new Calls(request -> response(request, 200, "text/html", "<html>not a search response</html>"));
        assertThrows(IOException.class, () -> tool(provider, html).execute(args("{\"query\":\"facts\"}"), context()));
    }

    @ParameterizedTest
    @EnumSource(Provider.class)
    void rejectsMalformedJsonStatusAndOversizedProviderResponses(Provider provider) {
        for (String json : List.of(
                "not json",
                "[]",
                completed(SEARCH) + "{}",
                "{\"status\":\"completed\",\"status\":\"completed\"}",
                completed(SEARCH).replace("\"status\":\"completed\"", "\"status\":\"failed\""),
                "x".repeat(524_289))) {
            Calls calls = new Calls(request -> response(request, 200, "application/json", json));
            assertThrows(
                    IOException.class, () -> tool(provider, calls).execute(args("{\"query\":\"facts\"}"), context()));
        }
    }

    @ParameterizedTest
    @EnumSource(Provider.class)
    void boundsAnswersSourcesAndSerializedUtf8EvenForEscapedUnicode(Provider provider) throws Exception {
        String message = ToolArguments.result(Map.of(
                "type",
                "message",
                "content",
                List.of(Map.of("type", "output_text", "text", "\"\\中文\uD834\uDD1E\u202e".repeat(9000)))));
        Calls calls = new Calls(request -> response(request, 200, "application/json", completed(SEARCH, message)));
        String output = tool(provider, calls).execute(args("{\"query\":\"facts\",\"maxResults\":1}"), context());
        assertTrue(output.getBytes(StandardCharsets.UTF_8).length <= 65_536);
        var result = args(output);
        assertEquals(1, result.path("sources").size());
        assertTrue(result.path("answer").asText().length() <= 6000);
        assertFalse(result.path("answer").asText().contains("\u202e"));
        assertTrue(result.path("truncated").asBoolean());
    }

    @ParameterizedTest
    @EnumSource(Provider.class)
    void fakeLlmCompletesBrokeredSearchWithoutSendingConversationOrCredentials(Provider provider) throws Exception {
        Calls calls = new Calls(
                request -> response(request, 200, "application/json", completed(SEARCH, provider.opened, MESSAGE)));
        WebSearchTool search = tool(provider, calls);
        var registry = new ToolRegistry(List.of(search.definition()));
        var broker = new ToolRuntimeBroker(
                registry,
                ignored -> new ToolPolicySource.Policy(
                        "user-secret", "character-secret", "backend-secret", Set.of("web_search"), false, false),
                List.of(new InProcessToolRuntime(List.of(search))),
                Set.of("web_search"));
        var bound = broker.bind(context(), Map.of());
        assertEquals(1, bound.size());
        assertEquals(
                Set.of("query", "maxResults"),
                search.definition().specification().parameters().properties().keySet());
        AtomicInteger modelCalls = new AtomicInteger();
        ChatModel fake = new ChatModel() {
            @Override
            public ChatResponse chat(ChatRequest request) {
                assertEquals(List.of(search.definition().specification()), request.toolSpecifications());
                int round = modelCalls.incrementAndGet();
                if (round == 1) {
                    assertTrue(request.messages().stream()
                            .anyMatch(message -> message instanceof UserMessage user
                                    && user.singleText().equals("conversation-history-secret")));
                    var call = ToolExecutionRequest.builder()
                            .id("search-call")
                            .name("web_search")
                            .arguments("{\"query\":\"facts\",\"maxResults\":2}")
                            .build();
                    return ChatResponse.builder()
                            .aiMessage(AiMessage.from(List.of(call)))
                            .finishReason(FinishReason.TOOL_EXECUTION)
                            .build();
                }
                assertEquals(2, round);
                var evidence = assertInstanceOf(
                        ToolExecutionResultMessage.class, request.messages().getLast());
                assertEquals("search-call", evidence.id());
                assertEquals("web_search", evidence.toolName());
                JsonNode result = assertDoesNotThrow(() -> args(evidence.text()));
                assertTrue(result.path("untrusted").asBoolean());
                assertTrue(result.path("answer").asText().contains("Found facts."));
                assertEquals(1, result.path("searchCount").asInt());
                assertEquals(2, result.path("sources").size());
                assertEquals(
                        "https://example.com/facts",
                        result.path("sources").get(0).path("url").asText());
                assertTrue(result.path("sources").get(0).path("opened").asBoolean());
                for (String forbidden : List.of(KEY, "fabricated.com", "invented.com", "hidden-script")) {
                    assertFalse(evidence.text().contains(forbidden));
                }
                return ChatResponse.builder()
                        .aiMessage(AiMessage.from("Verified facts: https://example.com/facts"))
                        .finishReason(FinishReason.STOP)
                        .build();
            }
        };
        var memory = MessageWindowChatMemory.withMaxMessages(10);
        memory.add(SystemMessage.from("conversation-system-secret"));
        memory.add(UserMessage.from("conversation-history-secret"));
        memory.add(AiMessage.from("conversation-assistant-secret"));
        SearchAssistant assistant = AiServices.builder(SearchAssistant.class)
                .chatModel(fake)
                .chatMemoryProvider(ignored -> memory)
                .tools(bound)
                .maxToolCallingRoundTrips(2)
                .build();
        assertEquals(
                "Verified facts: https://example.com/facts",
                assistant.search("chat-secret", "Please look up current facts."));
        assertEquals(2, modelCalls.get());
        assertEquals(1, calls.count.get());
        assertEquals(provider.endpoint, calls.request.uri().toString());
        assertProviderRequest(provider, calls);
    }

    interface SearchAssistant {
        String search(@MemoryId String chatId, @dev.langchain4j.service.UserMessage String question);
    }

    @ParameterizedTest
    @EnumSource(Provider.class)
    void acceptsChunkedJsonAndSseAndClosesConsumedBodies(Provider provider) throws Exception {
        String json = ToolArguments.result(args(completed(SEARCH, provider.opened, MESSAGE)));
        String sse = "event: response.completed\ndata: {\"type\":\"response.completed\",\"response\":" + json
                + "}\n\ndata: [DONE]\n\n";
        // JDK HTTP transport exposes de-chunked bytes, but retains the Transfer-Encoding response header.
        for (var fixture :
                Map.of("application/json", json, "text/event-stream", sse).entrySet()) {
            var body = new TrackedBody(fixture.getValue());
            Calls calls = new Calls(request -> response(request, 200, fixture.getKey(), "")
                    .withHeader("Content-Length", null)
                    .withHeader("Transfer-Encoding", "chunked")
                    .withBody(body));
            JsonNode result = args(tool(provider, calls).execute(args("{\"query\":\"facts\"}"), context()));
            assertTrue(result.path("answer").asText().contains("Found facts."));
            assertEquals(2, result.path("sources").size());
            assertTrue(body.closed);
            assertEquals(fixture.getValue().getBytes(StandardCharsets.UTF_8).length, body.consumed());
            assertEquals(1, calls.count.get());
        }
    }

    @ParameterizedTest
    @EnumSource(Provider.class)
    void chunkedResponsesAcceptExactByteLimitButStopAndCloseAtLimitPlusOne(Provider provider) throws Exception {
        String json = completed(SEARCH, MESSAGE);
        String atLimit = json + " ".repeat(524_288 - json.getBytes(StandardCharsets.UTF_8).length);
        for (boolean oversized : List.of(false, true)) {
            var body = new TrackedBody(oversized ? atLimit + " ".repeat(8192) : atLimit);
            Calls calls = new Calls(request -> response(request, 200, "application/json", "")
                    .withHeader("Content-Length", null)
                    .withHeader("Transfer-Encoding", "chunked")
                    .withBody(body));
            if (oversized) {
                assertThrows(
                        IOException.class,
                        () -> tool(provider, calls).execute(args("{\"query\":\"facts\"}"), context()));
                assertEquals(524_289, body.consumed());
            } else {
                JsonNode result = args(tool(provider, calls).execute(args("{\"query\":\"facts\"}"), context()));
                assertEquals(2, result.path("sources").size());
                assertEquals(524_288, body.consumed());
            }
            assertTrue(body.closed);
            assertEquals(1, calls.count.get());
        }
    }

    @ParameterizedTest
    @EnumSource(Provider.class)
    void rejectsChunkedWithContentLengthWithoutReadingAndClosesBody(Provider provider) {
        var body = new TrackedBody(completed(SEARCH, MESSAGE));
        Calls calls = new Calls(request -> response(request, 200, "application/json", completed(SEARCH, MESSAGE))
                .withHeader("Transfer-Encoding", "chunked")
                .withBody(body));
        assertThrows(IOException.class, () -> tool(provider, calls).execute(args("{\"query\":\"facts\"}"), context()));
        assertEquals(0, body.consumed());
        assertTrue(body.closed);
        assertEquals(1, calls.count.get());
    }

    @Test
    void chunkedSearchOptInDoesNotChangeWebFetchDefaults() {
        var body = new TrackedBody("public page");
        Calls calls = new Calls(request -> response(request, 200, "text/plain", "")
                .withHeader("Content-Length", null)
                .withHeader("Transfer-Encoding", "chunked")
                .withBody(body));
        assertThrows(
                IOException.class,
                () -> new WebFetchTool(calls, url -> true)
                        .execute(args("{\"url\":\"https://example.com/page\"}"), context()));
        assertEquals(0, body.consumed());
        assertTrue(body.closed);
        assertEquals(1, calls.count.get());
    }

    @ParameterizedTest
    @EnumSource(Provider.class)
    void providerHttpErrorsAreSanitizedNotRetriedAndCloseUnreadBodies(Provider provider) {
        for (int status : new int[] {201, 204, 206, 301, 302, 303, 307, 308, 400, 401, 403, 407, 408, 429, 500, 503}) {
            var body = new TrackedBody("{\"error\":{\"message\":\"" + KEY + " provider-body-secret\"}}");
            Calls calls = new Calls(request -> response(request, status, "application/json", "")
                    .withHeader("Content-Length", null)
                    .withHeader("Transfer-Encoding", "chunked")
                    .withHeader("Location", "https://evil.com/provider-header-secret")
                    .withHeader("Retry-After", KEY)
                    .withHeader("X-RateLimit-Remaining", "provider-header-secret")
                    .withHeader("X-RateLimit-Reset", KEY)
                    .withBody(body));
            Exception error = assertThrows(
                    IOException.class, () -> tool(provider, calls).execute(args("{\"query\":\"facts\"}"), context()));
            assertTrue(error.getMessage().contains(Integer.toString(status)));
            for (String secret : List.of(KEY, "provider-body-secret", "provider-header-secret", "evil.com")) {
                assertFalse(error.toString().contains(secret));
            }
            assertNull(error.getCause());
            assertEquals(0, error.getSuppressed().length);
            assertEquals(0, body.consumed());
            assertTrue(body.closed);
            assertEquals(1, calls.count.get());
        }
    }

    @ParameterizedTest
    @EnumSource(Provider.class)
    void providerErrorEnvelopesAreSanitizedAndCloseBodies(Provider provider) {
        var body = new TrackedBody("{\"error\":{\"code\":\"invalid_api_key\",\"message\":\"" + KEY + "\"}}");
        Calls calls = new Calls(request -> response(request, 200, "application/json", "")
                .withHeader("Content-Length", null)
                .withBody(body));
        Exception error = assertThrows(
                IOException.class, () -> tool(provider, calls).execute(args("{\"query\":\"facts\"}"), context()));
        assertFalse(error.toString().contains(KEY));
        assertFalse(error.toString().contains("invalid_api_key"));
        assertNull(error.getCause());
        assertTrue(body.closed);
        assertEquals(1, calls.count.get());
    }

    private static final class TrackedBody extends ByteArrayInputStream {
        private volatile boolean closed;

        private TrackedBody(String content) {
            super(content.getBytes(StandardCharsets.UTF_8));
        }

        @Override
        public synchronized int read(byte[] buffer, int offset, int length) {
            return super.read(buffer, offset, Math.min(length, 257));
        }

        @Override
        public void close() {
            closed = true;
        }

        private int consumed() {
            return pos;
        }
    }

    @ParameterizedTest
    @EnumSource(Provider.class)
    void transportErrorsDoNotExposeProviderSecretsAndExpiredCallsDoNotSend(Provider provider) {
        Calls calls = new Calls(request -> {
            throw new IOException("request failed: " + KEY);
        });
        Exception error = assertThrows(
                IOException.class, () -> tool(provider, calls).execute(args("{\"query\":\"facts\"}"), context()));
        assertFalse(error.toString().contains(KEY));
        assertNull(error.getCause());
        assertThrows(
                IllegalStateException.class,
                () -> tool(provider, calls).execute(args("{\"query\":\"facts\"}"), context(Duration.ofNanos(1))));
        assertEquals(1, calls.count.get());
    }
}
