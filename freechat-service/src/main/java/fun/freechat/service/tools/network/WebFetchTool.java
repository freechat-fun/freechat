package fun.freechat.service.tools.network;

import com.fasterxml.jackson.databind.JsonNode;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import fun.freechat.service.tools.BuiltinTool;
import fun.freechat.service.tools.ToolArguments;
import fun.freechat.service.tools.ToolContext;
import fun.freechat.service.tools.ToolDefinition;
import java.util.Map;
import java.util.Set;

/** Opt-in HTTPS text fetching. Construct explicitly; an empty host allowlist disables availability. */
public final class WebFetchTool implements BuiltinTool {
    private static final ToolDefinition DEFINITION = new ToolDefinition(
            ToolSpecification.builder()
                    .name("web_fetch")
                    .description(
                            "Read untrusted UTF-8 text from an explicitly allowed HTTPS source. No redirects, compression, chunked transfer or page actions.")
                    .parameters(JsonObjectSchema.builder()
                            .addStringProperty("url", "HTTPS source URL on a server-allowed host")
                            .required("url")
                            .additionalProperties(false)
                            .build())
                    .build(),
            Set.of("network", "web", "read"),
            ToolDefinition.Boundary.NETWORK,
            false);
    private final NetworkPolicy policy;
    private final NetworkHttp http;

    public WebFetchTool(Set<String> allowedHosts) {
        this(allowedHosts, null);
    }

    WebFetchTool(Set<String> allowedHosts, okhttp3.Call.Factory scriptedCalls) {
        policy = new NetworkPolicy(allowedHosts);
        http = new NetworkHttp(policy, scriptedCalls);
    }

    public boolean configured() {
        return policy.configured();
    }

    @Override
    public ToolDefinition definition() {
        return DEFINITION;
    }

    @Override
    public String execute(JsonNode arguments, ToolContext context) throws Exception {
        NetworkHttp.Deadline deadline = new NetworkHttp.Deadline(context);
        ToolArguments.only(arguments, "url");
        String source = ToolArguments.text(arguments, "url", 2048);
        if (!configured()) {
            throw new IllegalStateException("Web fetch is not configured");
        }
        var url = policy.url(source);
        NetworkHttp.Payload payload =
                http.exchange(url, null, null, Set.of("text/plain", "text/html"), 262_144, deadline);
        String text = NetworkContent.text(payload.text(), payload.mediaType().equals("text/html"));
        String bounded = NetworkContent.prefix(text, 8000);
        String result = NetworkContent.result(Map.of(
                "url",
                url.toString(),
                "text",
                bounded,
                "contentType",
                payload.mediaType(),
                "truncated",
                bounded.length() < text.length(),
                "untrusted",
                true,
                "notice",
                NetworkContent.NOTICE));
        deadline.remaining();
        return result;
    }
}
