package fun.freechat.api.dto;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.output.Response;
import fun.freechat.service.enums.PromptRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Schema(description = "Prompt service result")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
        return LlmResultDTO.builder()
                .text(text)
                .message(message)
                .finishReason(finishReason)
                .tokenUsage(usage)
                .build();
    }

    public static <T> LlmResultDTO from(Response<T> response) {
        if (response == null) {
            return null;
        }

        T content = response.content();
        String text;
        var messageBuilder = ChatMessageDTO.builder();

        switch (content) {
            case String textMessage -> {
                text = textMessage;
                messageBuilder.contents(List.of(ChatContentDTO.fromText(text)));
                messageBuilder.role(PromptRole.ASSISTANT.text());
            }
            case AiMessage aiMessage -> {
                text = aiMessage.text();
                messageBuilder.contents(List.of(ChatContentDTO.fromText(text)));
                if (aiMessage.hasToolExecutionRequests()) {
                    messageBuilder.toolCalls(aiMessage.toolExecutionRequests().stream()
                            .map(request -> ChatToolCallDTO.builder()
                                    .id((request.id()))
                                    .name(request.name())
                                    .arguments(request.arguments())
                                    .build())
                            .toList()
                    );
                    messageBuilder.role(PromptRole.FUNCTION_CALL.text());
                } else {
                    messageBuilder.role(PromptRole.ASSISTANT.text());
                }
            }
            case null, default -> {
                text = content != null ? content.toString() : "";
                messageBuilder.contents(List.of(ChatContentDTO.fromText(text)));
                messageBuilder.role(PromptRole.ASSISTANT.text());
            }
        }

        String finishReason = response.finishReason() != null ?
                response.finishReason().name().toLowerCase() : null;

        return LlmResultDTO.from(
                text,
                messageBuilder.build(),
                finishReason,
                TokenUsageDTO.from(response.tokenUsage()));
    }
}
