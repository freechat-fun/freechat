package fun.freechat.api.dto;

import dev.langchain4j.model.chat.response.ChatResponse;
import fun.freechat.service.chat.ChatRunEvent;
import java.util.LinkedHashMap;
import java.util.Map;

/** Versioned, flat SSE envelope. Internal response objects never cross the HTTP boundary. */
public final class ChatRunEventDTO {
    private ChatRunEventDTO() {}

    public static Map<String, Object> from(ChatRunEvent event) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("v", 1);
        result.put("event", event.event());
        result.put("run_id", event.runId());
        result.put("session_id", event.sessionId());
        result.put(
                "timestamp",
                event.timestamp().getEpochSecond() + event.timestamp().getNano() / 1_000_000_000.0);
        result.put("seq", event.seq());
        Map<String, Object> data = new LinkedHashMap<>(event.data());
        if (event.event().equals("run.completed")) {
            ChatResponse response = (ChatResponse) data.remove("response");
            Long messageId = (Long) data.remove("messageId");
            LlmResultDTO llm = LlmResultDTO.from(response, messageId);
            if (llm.getFinishReason() == null) llm.setFinishReason("stop");
            llm.setText(null);
            llm.setRequestId(null);
            data.put("result", llm);
        } else if (event.event().equals("run.failed")) {
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("code", data.remove("errorCode"));
            error.put("message", data.remove("message"));
            Object retry = data.remove("retryAfter");
            if (retry != null) error.put("retry_after", retry);
            data.put("error", error);
        }
        result.putAll(data);
        return result;
    }
}
