package fun.freechat.api.dto;

import dev.langchain4j.model.output.TokenUsage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Token usage information")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenUsageDTO {
    @Schema(description = "Input token count")
    private Integer inputTokenCount;
    @Schema(description = "Output token count")
    private Integer outputTokenCount;
    @Schema(description = "Total token count")
    private Integer totalTokenCount;

    public static TokenUsageDTO from(TokenUsage tokenUsage) {
        if (tokenUsage == null) {
            return null;
        }

        return TokenUsageDTO.builder()
                .inputTokenCount(tokenUsage.inputTokenCount())
                .outputTokenCount(tokenUsage.outputTokenCount())
                .totalTokenCount(tokenUsage.totalTokenCount())
                .build();
    }
}
