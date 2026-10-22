package fun.freechat.service.tools.network;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Parses provider tool evidence, never treating message annotations or model-authored links as sources. */
final class DashScopeSearchResponse {
    private DashScopeSearchResponse() {}

    private record Parsed(List<JsonNode> items, String fallback) {}

    private record Source(String url, boolean opened) {}

    static String result(NetworkHttp.Payload payload, int maxResults, NetworkHttp.Deadline deadline)
            throws IOException {
        Parsed parsed = payload.mediaType().equals("text/event-stream")
                ? stream(payload.text(), deadline)
                : completed(NetworkContent.json(payload.text()), List.of(), "");
        Map<String, Source> sources = new LinkedHashMap<>();
        StringBuilder answer = new StringBuilder();
        StringBuilder extracted = new StringBuilder();
        int searchCount = 0;
        for (JsonNode item : parsed.items()) {
            deadline.remaining();
            switch (item.path("type").asText()) {
                case "web_search_call" -> {
                    if (!item.path("status").asText().equals("completed")) {
                        continue;
                    }
                    searchCount++;
                    JsonNode candidates = item.path("action").path("sources");
                    checkArray(candidates, 128);
                    for (JsonNode source : candidates) {
                        addSource(sources, source.path("url"), false);
                    }
                }
                case "web_extractor_call" -> {
                    if (!item.path("status").asText().equals("completed")) {
                        continue;
                    }
                    JsonNode urls = item.path("urls");
                    checkArray(urls, 128);
                    boolean hasSource = false;
                    for (JsonNode url : urls) {
                        hasSource |= addSource(sources, url, true);
                    }
                    if (hasSource && item.path("output").isTextual()) {
                        append(extracted, item.path("output").textValue());
                    }
                }
                case "message" -> {
                    JsonNode content = item.path("content");
                    checkArray(content, 128);
                    for (JsonNode part : content) {
                        if (part.path("type").asText().equals("output_text")
                                && part.path("text").isTextual()) {
                            append(answer, part.path("text").textValue());
                        }
                    }
                }
                default -> {
                    /* Ignore reasoning, annotations, usage counters and unknown tools. */
                }
            }
        }
        if (searchCount == 0 || sources.isEmpty()) {
            throw new IOException("Search backend returned no actual search evidence");
        }
        boolean rawPage = false;
        if (answer.isEmpty()) {
            append(answer, parsed.fallback());
        }
        if (answer.isEmpty() && !extracted.isEmpty()) {
            rawPage = true;
            append(answer, extracted.toString());
        }
        // Links are served only through the evidence list. Strip model-authored Markdown/bare links.
        String safe = NetworkContent.text(answer.toString(), true)
                .replaceAll("\\[([^\\]]{0,512})\\]\\([^\\s)]{1,2048}\\)", "$1")
                .replaceAll("(?i)\\b(?:https?|ftp|javascript|data):\\S+", "[link omitted; see sources]");
        String bounded = NetworkContent.prefix(safe, 6000);
        List<Source> selected = sources.values().stream()
                .sorted((a, b) -> Boolean.compare(b.opened(), a.opened()))
                .limit(maxResults)
                .toList();
        return NetworkContent.result(Map.of(
                "answer",
                bounded,
                "sources",
                selected,
                "searchCount",
                searchCount,
                "rawPageContent",
                rawPage,
                "truncated",
                safe.length() > bounded.length() || answer.length() >= 16_000 || sources.size() > selected.size(),
                "untrusted",
                true,
                "notice",
                NetworkContent.NOTICE));
    }

    private static void checkArray(JsonNode array, int limit) throws IOException {
        if (!array.isMissingNode() && (!array.isArray() || array.size() > limit)) {
            throw NetworkHttp.failure();
        }
    }

    private static boolean addSource(Map<String, Source> sources, JsonNode value, boolean opened) {
        if (!value.isTextual()) {
            return false;
        }
        try {
            var url = NetworkPolicy.httpsUrl(value.textValue());
            String host = url.getHost();
            if (host.endsWith(".localhost")
                    || host.endsWith(".local")
                    || host.endsWith(".internal")
                    || host.endsWith(".invalid")
                    || host.endsWith(".test")) {
                return false;
            }
            String normalized = url.toString();
            Source previous = sources.get(normalized);
            if (previous == null && sources.size() < 64 || previous != null && opened && !previous.opened()) {
                sources.put(normalized, new Source(normalized, opened));
            }
            return true;
        } catch (IllegalArgumentException ignored) {
            return false;
        }
    }

    private static void append(StringBuilder target, String value) {
        if (!value.isEmpty() && target.length() < 16_000) {
            if (!target.isEmpty()) {
                target.append('\n');
            }
            target.append(NetworkContent.prefix(value, Math.max(0, 16_000 - target.length())));
        }
    }

    private static Parsed completed(JsonNode response, List<JsonNode> fallbackItems, String fallbackText)
            throws IOException {
        if (!response.isObject()
                || !response.path("status").asText().equals("completed")
                || response.hasNonNull("error")) {
            throw NetworkHttp.failure();
        }
        JsonNode output = response.path("output");
        checkArray(output, 128);
        List<JsonNode> items = new ArrayList<>();
        output.forEach(items::add);
        return new Parsed(items.isEmpty() ? fallbackItems : items, fallbackText);
    }

    private static Parsed stream(String text, NetworkHttp.Deadline deadline) throws IOException {
        StreamState state = new StreamState();
        try (var reader = new BufferedReader(new StringReader(text))) {
            StringBuilder data = new StringBuilder();
            String event = "";
            String line;
            int lines = 0;
            while ((line = reader.readLine()) != null) {
                deadline.remaining();
                if (++lines > 4096 || line.length() > 65_536) {
                    throw NetworkHttp.failure();
                }
                if (line.isEmpty()) {
                    state.accept(event, data.toString());
                    data.setLength(0);
                    event = "";
                } else if (line.startsWith("data:")) {
                    String part = line.substring(5);
                    if (part.startsWith(" ")) {
                        part = part.substring(1);
                    }
                    if (data.length() + part.length() + 1 > 65_536) {
                        throw NetworkHttp.failure();
                    }
                    data.append(part).append('\n');
                } else if (line.startsWith("event:")) {
                    event = line.substring(6).trim();
                }
            }
            state.accept(event, data.toString());
        }
        if (state.response == null) {
            throw NetworkHttp.failure();
        }
        return completed(state.response, state.items, state.delta.toString());
    }

    private static final class StreamState {
        private int count;
        private JsonNode response;
        private final List<JsonNode> items = new ArrayList<>();
        private final StringBuilder delta = new StringBuilder();

        private void accept(String event, String data) throws IOException {
            if (event.equals("error")) {
                throw NetworkHttp.failure();
            }
            if (data.isBlank() || data.trim().equals("[DONE]")) {
                return;
            }
            if (++count > 512) {
                throw NetworkHttp.failure();
            }
            JsonNode node = NetworkContent.json(data);
            if (node.hasNonNull("error") || node.has("code")) {
                throw NetworkHttp.failure();
            }
            String type = node.path("type").asText(event);
            if (response != null) {
                throw NetworkHttp.failure();
            }
            switch (type) {
                case "response.completed" -> response = node.path("response");
                case "response.failed", "response.incomplete", "response.cancelled", "error" ->
                    throw NetworkHttp.failure();
                case "response.output_item.done" -> {
                    if (!node.path("item").isObject() || items.size() >= 128) {
                        throw NetworkHttp.failure();
                    }
                    items.add(node.path("item"));
                }
                case "response.output_text.delta" -> {
                    if (node.path("delta").isTextual() && delta.length() < 16_000) {
                        delta.append(NetworkContent.prefix(node.path("delta").textValue(), 16_000 - delta.length()));
                    }
                }
                default -> {
                    /* Added/in-progress/usage events carry no completed evidence. */
                }
            }
        }
    }
}
