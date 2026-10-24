package fun.freechat.service.tools.network;

import static fun.freechat.service.tools.network.NetworkTestSupport.*;
import static org.junit.jupiter.api.Assertions.*;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@Timeout(10)
class WebSearchProviderHttpTest {
    @ParameterizedTest
    @CsvSource({"open_ai,fixture-model", "dash_scope,fixture-model", "dash_scope,qwen3.8-omni-flash"})
    void actualHttpRequestsAndChunkedResponsesUseTheSelectedProviderContract(String provider, String model)
            throws Exception {
        var requestBody = new AtomicReference<String>();
        var authorization = new AtomicReference<String>();
        var method = new AtomicReference<String>();
        byte[] responseBody = """
                {"status":"completed","output":[
                  {"type":"web_search_call","status":"completed","action":{"type":"search","sources":[{"type":"url","url":"https://example.com/evidence"}]}},
                  {"type":"message","content":[{"type":"output_text","text":"Verified facts."}]}
                ]}
                """.getBytes(StandardCharsets.UTF_8);
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/responses", exchange -> {
            method.set(exchange.getRequestMethod());
            authorization.set(exchange.getRequestHeaders().getFirst("Authorization"));
            requestBody.set(new String(exchange.getRequestBody().readNBytes(16384), StandardCharsets.UTF_8));
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, 0);
            try (var output = exchange.getResponseBody()) {
                output.write(responseBody);
            }
        });
        server.start();
        try (var client = NetworkHttp.client()) {
            URI local = URI.create("http://127.0.0.1:" + server.getAddress().getPort() + "/responses");
            NetworkHttp.Transport transport = request -> {
                var forwarded = HttpRequest.newBuilder(local)
                        .timeout(request.timeout().orElseThrow())
                        .method(request.method(), request.bodyPublisher().orElseThrow());
                request.headers()
                        .map()
                        .forEach((name, values) -> values.forEach(value -> forwarded.header(name, value)));
                return client.send(forwarded.build(), HttpResponse.BodyHandlers.ofInputStream());
            };
            String baseUrl = provider.equals("open_ai")
                    ? "https://api.openai.com/v1"
                    : "https://workspace.cn-beijing.maas.aliyuncs.com/compatible-mode/v1";
            var tool = new WebSearchTool(baseUrl, "test-only-key", "[" + provider + "]" + model, transport);
            var result = args(tool.execute(args("{\"query\":\"test facts\"}"), context()));
            assertEquals("POST", method.get());
            assertEquals("Bearer test-only-key", authorization.get());
            var request = args(requestBody.get());
            assertEquals(model, request.path("model").asText());
            assertFalse(request.path("store").asBoolean());
            assertFalse(request.path("stream").asBoolean());
            assertFalse(requestBody.get().contains("test-only-key"));
            if (provider.equals("open_ai")) {
                assertEquals(1, request.path("tools").size());
                assertEquals(
                        "web_search_call.action.sources",
                        request.path("include").get(0).asText());
                assertEquals("required", request.path("tool_choice").asText());
                assertFalse(requestBody.get().contains("web_extractor"));
            } else if (model.equals("qwen3.8-omni-flash")) {
                assertEquals(1, request.path("tools").size());
                assertEquals(
                        "web_search", request.path("tools").get(0).path("type").asText());
                assertFalse(requestBody.get().contains("web_extractor"));
                assertFalse(request.has("include"));
            } else {
                assertEquals(2, request.path("tools").size());
                assertEquals(
                        "web_extractor",
                        request.path("tools").get(1).path("type").asText());
            }
            assertEquals("Verified facts.", result.path("answer").asText());
            assertEquals(
                    "https://example.com/evidence",
                    result.path("sources").get(0).path("url").asText());
            assertEquals(1, result.path("searchCount").asInt());
            assertTrue(result.path("untrusted").asBoolean());
        } finally {
            server.stop(0);
        }
    }
}
