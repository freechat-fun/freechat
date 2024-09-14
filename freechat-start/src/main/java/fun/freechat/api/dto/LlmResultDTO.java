package fun.freechat.api.dto;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.output.Response;
import fun.freechat.service.enums.PromptRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Schema(description = "Prompt service result")
@Data
@EqualsAndHashCode(callSuper = true)
public class LlmResultDTO extends TraceableDTO {
    @Schema(description = "Model response content, the complete content is included in non-streaming responses; only the delta content is included in streaming responses (the complete content of streaming responses is in the content of the last frame message field)")
    private String text;
    @Schema(description = "Chat response message (usually as assistant, sometimes tool_call)")
    private ChatMessageDTO message;
    @Schema(description = "Model end reason: stop | length | tool_execution | content_filter")
    private String finishReason;
    @Schema(description = "Token usage information")
    private TokenUsageDTO tokenUsage;

    public static LlmResultDTO from(String text,
                                    ChatMessageDTO message,
                                    String finishReason,
                                    TokenUsageDTO usage) {
        LlmResultDTO dto = new LlmResultDTO();
        dto.setText(text);
        dto.setMessage(message);
        dto.setFinishReason(finishReason);
        dto.setTokenUsage(usage);
        return dto;
    }

    public static <T> LlmResultDTO from(Response<T> response) {
        if (response == null) {
            return null;
        }

        T content = response.content();
        String text;
        ChatMessageDTO message = new ChatMessageDTO();

        switch (content) {
            case String textMessage -> {
                text = textMessage;
                message.setContents(List.of(ChatContentDTO.fromText(text)));
                message.setRole(PromptRole.ASSISTANT.text());
            }
            case AiMessage aiMessage -> {
                text = aiMessage.text();
                message.setContents(List.of(ChatContentDTO.fromText(text)));
                if (aiMessage.hasToolExecutionRequests()) {
                    message.setToolCalls(aiMessage.toolExecutionRequests().stream()
                            .map(request -> {
                                ChatToolCallDTO toolCall = new ChatToolCallDTO();
                                toolCall.setId(request.id());
                                toolCall.setName(request.name());
                                toolCall.setArguments(request.arguments());
                                return toolCall;
                            })
                            .toList()
                    );
                    message.setRole(PromptRole.FUNCTION_CALL.text());
                } else {
                    message.setRole(PromptRole.ASSISTANT.text());
                }
            }
            case null, default -> {
                text = content != null ? content.toString() : "";
                message.setContents(List.of(ChatContentDTO.fromText(text)));
                message.setRole(PromptRole.ASSISTANT.text());
            }
        }

        String finishReason = response.finishReason() != null ?
                response.finishReason().name().toLowerCase() : null;

        return LlmResultDTO.from(
                text,
                message,
                finishReason,
                TokenUsageDTO.from(response.tokenUsage()));
    }
}
