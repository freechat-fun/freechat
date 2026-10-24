package fun.freechat.api.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fun.freechat.service.connectors.ConnectorConfiguration;
import fun.freechat.service.connectors.ConnectorException;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonDeserialize(using = ConnectorWriteDTO.Deserializer.class)
@Schema(description = "Private remote MCP configuration. Saves validate syntax only; no network requests are made.")
public class ConnectorWriteDTO {
    @Schema(description = "Printable display name, 1–64 characters")
    private String name;

    @Schema(
            allowableValues = {"http", "sse"},
            description = "Remote HTTP or SSE only")
    private String transport;

    @Schema(description = "HTTP(S) base URL, at most 2048 characters; no credentials, query, or fragment")
    private String url;

    @Schema(description = "Timeout in milliseconds, 1000–120000", defaultValue = "60000")
    private Integer timeout;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Schema(
            description =
                    "New header values, HTTPS only. At most 16 headers and 16 KiB total. Values are never returned.")
    private Map<String, String> headers;

    @Schema(
            description =
                    "Stored header names to retain on update; cannot overlap new headers or move to another origin")
    private List<String> keepHeaderNames;

    @Schema(description = "Current revision required on update; omit on create")
    private String expectedRevision;

    @Schema(defaultValue = "true")
    private boolean active = true;

    public ConnectorConfiguration configuration() {
        return new ConnectorConfiguration(name, transport, url, timeout, headers);
    }

    @JsonAnySetter
    public void rejectUnknown(String ignoredName, Object ignoredValue) {
        throw invalid();
    }

    @Override
    public String toString() {
        return "ConnectorWriteDTO[redacted]";
    }

    private static ConnectorException invalid() {
        return new ConnectorException(ConnectorException.Code.CONNECTOR_INVALID_CONFIGURATION);
    }

    /** Reject unknown fields, duplicate keys, and scalar coercions even with the permissive global mapper. */
    public static class Deserializer extends JsonDeserializer<ConnectorWriteDTO> {
        private static final Set<String> FIELDS = Set.of(
                "name", "transport", "url", "timeout", "headers", "keepHeaderNames", "expectedRevision", "active");

        @Override
        public ConnectorWriteDTO deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            try {
                return parse(parser);
            } catch (ConnectorException | IOException ignored) {
                // Drop parser/provider causes and source locations, which may contain submitted secrets.
                throw JsonMappingException.from((JsonParser) null, "CONNECTOR_INVALID_CONFIGURATION");
            }
        }

        private ConnectorWriteDTO parse(JsonParser parser) throws IOException {
            parser.enable(JsonParser.Feature.STRICT_DUPLICATE_DETECTION);
            JsonNode root = parser.getCodec().readTree(parser);
            if (!root.isObject() || root.properties().stream().anyMatch(entry -> !FIELDS.contains(entry.getKey()))) {
                throw invalid();
            }
            ConnectorWriteDTO dto = new ConnectorWriteDTO();
            dto.name = text(root.get("name"));
            dto.transport = text(root.get("transport"));
            dto.url = text(root.get("url"));
            dto.expectedRevision = text(root.get("expectedRevision"));
            JsonNode timeout = root.get("timeout");
            if (timeout != null && !timeout.isNull()) {
                if (!timeout.isIntegralNumber() || !timeout.canConvertToInt()) {
                    throw invalid();
                }
                dto.timeout = timeout.intValue();
            }
            JsonNode active = root.get("active");
            if (active != null) {
                if (!active.isBoolean()) {
                    throw invalid();
                }
                dto.active = active.booleanValue();
            }
            JsonNode headers = root.get("headers");
            if (headers != null && !headers.isNull()) {
                if (!headers.isObject() || headers.size() > 16) {
                    throw invalid();
                }
                dto.headers = new LinkedHashMap<>();
                Set<String> seen = new HashSet<>();
                for (var entry : headers.properties()) {
                    if (!entry.getValue().isTextual()
                            || !seen.add(entry.getKey().toLowerCase(Locale.ROOT))) {
                        throw invalid();
                    }
                    dto.headers.put(entry.getKey(), entry.getValue().textValue());
                }
            }
            JsonNode kept = root.get("keepHeaderNames");
            if (kept != null && !kept.isNull()) {
                if (!kept.isArray() || kept.size() > 16) {
                    throw invalid();
                }
                dto.keepHeaderNames = new ArrayList<>();
                Set<String> seen = new HashSet<>();
                for (JsonNode name : kept) {
                    if (!name.isTextual() || !seen.add(name.textValue().toLowerCase(Locale.ROOT))) {
                        throw invalid();
                    }
                    dto.keepHeaderNames.add(name.textValue());
                }
            }
            return dto;
        }

        private static String text(JsonNode value) {
            if (value == null || value.isNull()) {
                return null;
            }
            if (!value.isTextual()) {
                throw invalid();
            }
            return value.textValue();
        }
    }
}
