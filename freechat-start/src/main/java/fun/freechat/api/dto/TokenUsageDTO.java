package fun.freechat.api.dto;

import dev.langchain4j.model.output.TokenUsage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Objects;

@Schema(description = "Token usage information")
@Data
public class TokenUsageDTO {
    @Schema(description = "Input token count")
    private Integer inputTokenCount;
    @Schema(description = "Output token count")
    private Integer outputTokenCount;
    @Schema(description = "Total token count")
    private Integer totalTokenCount;

    public static TokenUsageDTO from(TokenUsage tokenUsage) {
        if (Objects.isNull(tokenUsage)) {
            return null;
        }
        TokenUsageDTO dto = new TokenUsageDTO();
        dto.setInputTokenCount(tokenUsage.inputTokenCount());
        dto.setOutputTokenCount(tokenUsage.outputTokenCount());
        dto.setTotalTokenCount(tokenUsage.totalTokenCount());
        return dto;
    }
}
