package fun.freechat.api.dto;

import fun.freechat.service.chat.MemoryUsage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "Memory usage information")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class MemoryUsageDTO extends TraceableDTO {
    @Schema(description = "Messages usage information")
    private Long messageUsage;
    @Schema(description = "Token usage information")
    private TokenUsageDTO tokenUsage;

    public static MemoryUsageDTO from(MemoryUsage memoryUsage) {
        var builder = MemoryUsageDTO.builder();
        return memoryUsage == null ?
                builder.build() :
                builder.messageUsage(memoryUsage.messageUsage())
                        .tokenUsage(TokenUsageDTO.from(memoryUsage.tokenUsage()))
                        .build();
    }
}
