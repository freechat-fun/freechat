package fun.freechat.service.tools;

import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.util.Set;

public final class ToolArguments {
    public static final ObjectMapper JSON = JsonMapper.builder()
            .enable(StreamReadFeature.STRICT_DUPLICATE_DETECTION)
            .build();

    static {
        JSON.getFactory()
                .setStreamReadConstraints(StreamReadConstraints.builder()
                        .maxNestingDepth(16)
                        .maxStringLength(131_072)
                        .maxNumberLength(128)
                        .build());
    }

    private ToolArguments() {}

    public static JsonNode parse(String json) {
        if (json == null || json.length() > 131_072) {
            throw invalid();
        }
        try (var parser = JSON.createParser(json)) {
            JsonNode value = JSON.readTree(parser);
            if (value == null || !value.isObject() || parser.nextToken() != null) {
                throw invalid();
            }
            return value;
        } catch (Exception ignored) {
            throw invalid();
        }
    }

    public static void only(JsonNode args, String... names) {
        Set<String> allowed = Set.of(names);
        if (args == null || !args.isObject()) {
            throw invalid();
        }
        args.fieldNames().forEachRemaining(name -> {
            if (!allowed.contains(name)) {
                throw invalid();
            }
        });
    }

    public static String text(JsonNode args, String name, int maxLength) {
        JsonNode value = args.get(name);
        if (value == null || !value.isTextual() || value.textValue().length() > maxLength) {
            throw invalid();
        }
        String text = value.textValue();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (Character.isHighSurrogate(c)) {
                if (++i == text.length() || !Character.isLowSurrogate(text.charAt(i))) {
                    throw invalid();
                }
            } else if (Character.isLowSurrogate(c)) {
                throw invalid();
            }
        }
        return text;
    }

    public static String optionalText(JsonNode args, String name, String fallback, int maxLength) {
        return !args.has(name) ? fallback : text(args, name, maxLength);
    }

    public static int integer(JsonNode args, String name, int fallback, int min, int max) {
        JsonNode value = args.get(name);
        if (value == null) {
            return fallback;
        }
        if (!value.isIntegralNumber() || !value.canConvertToInt() || value.intValue() < min || value.intValue() > max) {
            throw invalid();
        }
        return value.intValue();
    }

    public static boolean bool(JsonNode args, String name, boolean fallback) {
        JsonNode value = args.get(name);
        if (value == null) {
            return fallback;
        }
        if (!value.isBoolean()) {
            throw invalid();
        }
        return value.booleanValue();
    }

    public static String result(Object result) {
        try {
            return JSON.writeValueAsString(result);
        } catch (Exception ignored) {
            throw new IllegalStateException("Tool result serialization failed");
        }
    }

    public static IllegalArgumentException invalid() {
        return new IllegalArgumentException("Invalid tool arguments");
    }
}
