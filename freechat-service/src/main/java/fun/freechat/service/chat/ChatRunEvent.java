package fun.freechat.service.chat;

import java.time.Instant;
import java.util.Map;

/** Ephemeral, ordered run notification. Tool payloads and provider failures never belong here. */
public record ChatRunEvent(
        String event, String runId, String sessionId, long seq, Instant timestamp, Map<String, Object> data) {
    public ChatRunEvent {
        data = Map.copyOf(data);
    }
}
