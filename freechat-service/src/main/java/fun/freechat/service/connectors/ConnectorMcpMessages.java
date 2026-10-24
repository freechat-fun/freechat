package fun.freechat.service.connectors;

import static fun.freechat.service.connectors.ConnectorException.Code.*;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/** Bounded, payload-free validation before invoking any LC parser or logging path. */
final class ConnectorMcpMessages {
    static final int REQUEST_BYTES = 64 * 1024;
    static final int MESSAGE_BYTES = 256 * 1024;
    static final int RESULT_BYTES = 64 * 1024;
    static final String REMOTE_TOOL_ERROR = "{\"isError\":true,\"message\":\"Remote tool execution failed\"}";
    static final Set<String> VERSIONS = Set.of("2024-11-05", "2025-03-26", "2025-06-18", "2025-11-25");
    private static final Set<String> ORDINARY_HEADERS = Set.of(
            "accept",
            "accept-encoding",
            "accept-language",
            "user-agent",
            "cache-control",
            "pragma",
            "content-type",
            "content-length",
            "content-language",
            "connection",
            "host");
    private static final ObjectMapper JSON = new ObjectMapper(JsonFactory.builder()
                    .streamReadConstraints(StreamReadConstraints.builder()
                            .maxNestingDepth(48)
                            .maxStringLength(MESSAGE_BYTES)
                            .maxNumberLength(64)
                            .build())
                    .enable(StreamReadFeature.STRICT_DUPLICATE_DETECTION)
                    .build())
            .enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS);

    private ConnectorMcpMessages() {}

    static ConnectorException unavailable() {
        return new ConnectorException(CONNECTOR_UNAVAILABLE);
    }

    static ConnectorException limit() {
        return new ConnectorException(CONNECTOR_LIMIT_EXCEEDED);
    }

    static ConnectorException sanitized(Throwable error) {
        // Never retain a provider/HTTP/JSON exception as the public exception's cause.
        for (int i = 0; error != null && i < 16; i++, error = error.getCause()) {
            if (error instanceof ConnectorException connector) {
                return new ConnectorException(connector.code());
            }
            if (error instanceof java.util.concurrent.TimeoutException
                    || error instanceof java.net.http.HttpTimeoutException) {
                return new ConnectorException(CONNECTOR_TIMEOUT);
            }
        }
        return unavailable();
    }

    static byte[] bytes(String text, int maximum) {
        if (text == null || text.length() > maximum) {
            throw limit();
        }
        try {
            ByteBuffer encoded = StandardCharsets.UTF_8
                    .newEncoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT)
                    .encode(java.nio.CharBuffer.wrap(text));
            if (encoded.remaining() > maximum) {
                throw limit();
            }
            byte[] bytes = new byte[encoded.remaining()];
            encoded.get(bytes);
            return bytes;
        } catch (CharacterCodingException ignored) {
            throw unavailable();
        }
    }

    static String utf8(byte[] bytes) {
        try {
            return StandardCharsets.UTF_8
                    .newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT)
                    .decode(ByteBuffer.wrap(bytes))
                    .toString();
        } catch (CharacterCodingException ignored) {
            throw unavailable();
        }
    }

    static JsonNode parse(String text, int maximum) {
        bytes(text, maximum);
        try {
            JsonNode node = JSON.readTree(text);
            if (node == null || !node.isObject()) {
                throw unavailable();
            }
            return node;
        } catch (Exception ignored) {
            throw unavailable();
        }
    }

    static void toolName(String name) {
        if (name == null || !name.matches("[A-Za-z0-9_.-]{1,128}")) {
            throw unavailable();
        }
    }

    static final class Discovery {
        int pages;
        int schemaBytes;
        final Set<String> names = new HashSet<>();
        final Set<String> cursors = new HashSet<>();
    }

    static JsonNode initialize(JsonNode result) {
        String version = result.path("protocolVersion").asText();
        if (!VERSIONS.contains(version) || !result.path("capabilities").isObject()) {
            throw unavailable();
        }
        // LC logs initialization; only fixed metadata and the negotiated version may cross this boundary.
        return JSON.valueToTree(Map.of(
                "protocolVersion",
                version,
                "capabilities",
                Map.of(),
                "serverInfo",
                Map.of("name", "connector", "version", "1")));
    }

    static JsonNode tools(JsonNode result, Discovery discovery) {
        JsonNode tools = result.path("tools");
        if (!tools.isArray()) {
            throw unavailable();
        }
        if (tools.size() + discovery.names.size() > 32) {
            throw limit();
        }
        ObjectNode clean = JSON.createObjectNode();
        var output = clean.putArray("tools");
        for (JsonNode tool : tools) {
            if (!tool.path("name").isTextual()) {
                throw unavailable();
            }
            String name = tool.get("name").textValue();
            toolName(name);
            if (!discovery.names.add(name)) {
                throw unavailable();
            }
            JsonNode schema = tool.path("inputSchema");
            if (!schema.isObject() || !"object".equals(schema.path("type").asText()) || schema.has("$ref")) {
                throw unavailable();
            }
            discovery.schemaBytes += bytes(schema.toString(), MESSAGE_BYTES).length;
            if (discovery.schemaBytes > 128 * 1024) {
                throw limit();
            }
            validateTree(schema, 0, new int[1], true);
            ObjectNode spec = output.addObject().put("name", name);
            spec.set("inputSchema", schema);
            if (tool.has("description")) {
                if (!tool.get("description").isTextual()) {
                    throw unavailable();
                }
                bytes(tool.get("description").textValue(), 4096);
                spec.set("description", tool.get("description"));
            }
        }
        JsonNode cursor = result.get("nextCursor");
        if (cursor != null && !cursor.isNull()) {
            if (!cursor.isTextual() || cursor.textValue().isEmpty()) {
                throw unavailable();
            }
            bytes(cursor.textValue(), 2048);
            if (!discovery.cursors.add(cursor.textValue()) || discovery.pages >= 8) {
                throw limit();
            }
            clean.set("nextCursor", cursor);
        }
        return clean;
    }

    private static void validateTree(JsonNode node, int depth, int[] nodes, boolean schema) {
        if (depth > 32 || ++nodes[0] > 4096) {
            throw limit();
        }
        if (node.isObject()) {
            // LC may log server-controlled allOf/header-extension errors.
            if (schema && (node.has("allOf") || node.has("x-mcp-header"))) {
                throw unavailable();
            }
            node.elements().forEachRemaining(child -> validateTree(child, depth + 1, nodes, schema));
        } else if (node.isArray()) {
            node.forEach(child -> validateTree(child, depth + 1, nodes, schema));
        }
    }

    static JsonNode call(JsonNode result) {
        if (result.has("isError") && !result.get("isError").isBoolean()) {
            throw unavailable();
        }
        JsonNode structured = result.get("structuredContent");
        if (structured != null) {
            if (!structured.isObject()) {
                throw unavailable();
            }
            validateTree(structured, 0, new int[1], false);
        }
        JsonNode content = result.path("content");
        if ((!content.isArray() && !(content.isMissingNode() && structured != null)) || content.size() > 32) {
            throw unavailable();
        }
        StringBuilder text = new StringBuilder();
        for (JsonNode item : content) {
            if (!"text".equals(item.path("type").asText()) || !item.path("text").isTextual()) {
                throw unavailable();
            }
            appendResult(text, item.get("text").textValue());
        }
        if (structured != null) {
            appendResult(text, structured.toString());
        }
        boolean error = result.path("isError").asBoolean(false);
        // Normalize structured results and application errors before LC bypasses conversion.
        return JSON.valueToTree(Map.of(
                "content", List.of(Map.of("type", "text", "text", error ? REMOTE_TOOL_ERROR : text.toString()))));
    }

    private static void appendResult(StringBuilder text, String part) {
        bytes(part, RESULT_BYTES);
        if (!text.isEmpty()) {
            text.append('\n');
        }
        text.append(part);
        bytes(text.toString(), RESULT_BYTES);
    }

    static String response(long id, JsonNode result) {
        ObjectNode envelope = JSON.createObjectNode().put("jsonrpc", "2.0").put("id", id);
        envelope.set("result", result);
        return envelope.toString();
    }

    static String serverReply(JsonNode id, String method) {
        if (id.isTextual()) {
            bytes(id.textValue(), 128);
        } else if (!id.isIntegralNumber()) {
            throw unavailable();
        }
        Object result =
                switch (method) {
                    case "ping" -> Map.of();
                    case "roots/list" -> Map.of("roots", List.of());
                    default -> null;
                };
        ObjectNode envelope = JSON.createObjectNode().put("jsonrpc", "2.0");
        envelope.set("id", id);
        envelope.set(
                result == null ? "error" : "result",
                JSON.valueToTree(result == null ? Map.of("code", -32601, "message", "Method not found") : result));
        return envelope.toString();
    }

    // Echo detection does not change encryption or management visibility.
    static List<String> credentialValues(Map<String, String> headers) {
        Set<String> values = new LinkedHashSet<>();
        headers.forEach((name, value) -> {
            String lower = name.toLowerCase(Locale.ROOT);
            boolean credential = lower.equals("authorization")
                    || lower.equals("proxy-authorization")
                    || lower.equals("cookie")
                    || lower.equals("set-cookie")
                    || lower.contains("token")
                    || lower.contains("api-key")
                    || lower.contains("apikey")
                    || lower.contains("secret")
                    || lower.contains("password");
            if (value == null
                    || value.isBlank()
                    || (!credential
                            && (ORDINARY_HEADERS.contains(lower)
                                    || value.getBytes(StandardCharsets.UTF_8).length < 8
                                    || value.codePoints().anyMatch(Character::isWhitespace)))) {
                return;
            }
            values.add(value);
            String[] scheme = value.trim().split("\\s+", 2);
            if (scheme.length == 2 && ("bearer".equalsIgnoreCase(scheme[0]) || "basic".equalsIgnoreCase(scheme[0]))) {
                values.add(scheme[1]);
            }
        });
        return List.copyOf(values);
    }

    static void noResultSecrets(JsonNode result, Iterable<String> secrets) {
        // Inspect only remote payloads, not generated envelope keys or content type markers.
        for (JsonNode tool : result.path("tools")) {
            noSecrets(tool.path("name"), secrets);
            noSecrets(tool.path("description"), secrets);
            noSecrets(tool.path("inputSchema"), secrets);
        }
        noSecrets(result.path("nextCursor"), secrets);
        // Scan the structured tree before JSON escaping can obscure a quoted credential.
        noSecrets(result.path("structuredContent"), secrets);
        for (JsonNode item : result.path("content")) {
            noSecrets(item.path("text"), secrets);
        }
    }

    private static void noSecrets(JsonNode node, Iterable<String> secrets) {
        if (node.isTextual()) {
            for (String secret : secrets) {
                if (!secret.isEmpty() && node.textValue().contains(secret)) {
                    throw unavailable();
                }
            }
        } else if (node.isObject()) {
            node.fields().forEachRemaining(field -> {
                for (String secret : secrets) {
                    if (!secret.isEmpty() && field.getKey().contains(secret)) {
                        throw unavailable();
                    }
                }
                noSecrets(field.getValue(), secrets);
            });
        } else if (node.isArray()) {
            node.forEach(child -> noSecrets(child, secrets));
        }
    }
}
