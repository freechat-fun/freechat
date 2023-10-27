package fun.freechat.api.dto;

import dev.langchain4j.model.output.Response;
import fun.freechat.service.ai.message.ChatMessage;
import fun.freechat.service.enums.PromptRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.Objects;

@Schema(description = "Prompt service result")
@Data
public class LlmResultDTO extends TraceableDTO {
    @Schema(description = "Model response content, the complete content is included in non-streaming responses; only the delta content is included in streaming responses (the complete content of streaming responses is in the content of the last frame message field)")
    private String text;
    @Schema(description = "Chat response message (usually as assistant, sometimes function_call)")
    private ChatMessageDTO message;
    @Schema(description = "Model end reason: stop | length | tool_execution | content_filter")
    private String finishReason;
    @Schema(description = "Token usage information")
    private LlmTokenUsageDTO tokenUsage;

    public static LlmResultDTO from(String text,
                                    ChatMessageDTO message,
                                    String finishReason,
                                    LlmTokenUsageDTO usage) {
        LlmResultDTO dto = new LlmResultDTO();
        dto.setText(text);
        dto.setMessage(message);
        dto.setFinishReason(finishReason);
        dto.setTokenUsage(usage);
        return dto;
    }

    public static <T> LlmResultDTO from(Response<T> response) {
        if (Objects.isNull(response)) {
            return null;
        }

        T content = response.content();
        String text;
        ChatMessageDTO message = new ChatMessageDTO();

        switch (content) {
            case ChatMessage origMessage -> {
                text = origMessage.getContent();
                message.setContent(text);
                message.setGmtCreate(origMessage.getGmtCreate());
                message.setName(origMessage.getName());
                message.setRole(origMessage.getRole().text());
                message.setFunctionCall(
                        ChatFunctionCallDTO.from(origMessage.getFunctionCall()));
            }
            case String textMessage -> {
                text = textMessage;
                message.setContent(text);
                message.setGmtCreate(new Date());
                message.setRole(PromptRole.ASSISTANT.text());
            }
            case dev.langchain4j.data.message.AiMessage aiMessage -> {
                text = aiMessage.text();
                message.setContent(text);
                message.setGmtCreate(new Date());
                if (Objects.nonNull(aiMessage.toolExecutionRequest())) {
                    ChatFunctionCallDTO functionCall = new ChatFunctionCallDTO();
                    functionCall.setName(aiMessage.toolExecutionRequest().name());
                    functionCall.setArguments(aiMessage.toolExecutionRequest().arguments());

                    message.setFunctionCall(functionCall);
                    message.setRole(PromptRole.FUNCTION_CALL.text());
                } else {
                    message.setRole(PromptRole.ASSISTANT.text());
                }
            }
            case null, default -> {
                text = Objects.nonNull(content) ? content.toString() : "";
                message.setContent(text);
                message.setGmtCreate(new Date());
                message.setRole(PromptRole.ASSISTANT.text());
            }
        }

        String finishReason = Objects.nonNull(response.finishReason()) ?
                response.finishReason().name().toLowerCase() : null;

        return LlmResultDTO.from(
                text,
                message,
                finishReason,
                LlmTokenUsageDTO.from(response.tokenUsage()));
    }
}
