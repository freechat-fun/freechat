package fun.freechat.service.tools.network;

import com.fasterxml.jackson.databind.JsonNode;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import fun.freechat.service.tools.BuiltinTool;
import fun.freechat.service.tools.ToolArguments;
import fun.freechat.service.tools.ToolContext;
import fun.freechat.service.tools.ToolDefinition;
import fun.freechat.util.HttpUtils;
import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public final class WebFetchTool implements BuiltinTool {
    private static final ToolDefinition DEFINITION = new ToolDefinition(
            ToolSpecification.builder()
                    .name("web_fetch")
                    .description(
                            "Read untrusted UTF-8 text from a safety-checked public HTTP or HTTPS URL. No redirects, compression, chunked transfer or page actions.")
                    .parameters(JsonObjectSchema.builder()
                            .addStringProperty("url", "Public HTTP or HTTPS source URL")
                            .required("url")
                            .additionalProperties(false)
                            .build())
                    .build(),
            Set.of("network", "web", "read"),
            ToolDefinition.Boundary.NETWORK,
            false);
    private final NetworkHttp http;

    public WebFetchTool() {
        this(null);
    }

    WebFetchTool(NetworkHttp.Transport transport) {
        this(transport, HttpUtils::isSafeUrl);
    }

    WebFetchTool(NetworkHttp.Transport transport, Predicate<String> safetyCheck) {
        http = new NetworkHttp(
                source -> {
                    if (!safetyCheck.test(source)) {
                        throw new IllegalArgumentException("Unsafe web fetch URL");
                    }
                    return URI.create(source);
                },
                transport);
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
        URI url;
        try {
            url = URI.create(source);
        } catch (IllegalArgumentException ignored) {
            throw ToolArguments.invalid();
        }
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
