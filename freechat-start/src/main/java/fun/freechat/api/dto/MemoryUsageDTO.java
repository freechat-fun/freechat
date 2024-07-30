package fun.freechat.api.dto;

import fun.freechat.service.chat.MemoryUsage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Objects;

@Schema(description = "Memory usage information")
@Data
@EqualsAndHashCode(callSuper = true)
public class MemoryUsageDTO extends TraceableDTO {
    @Schema(description = "Messages usage information")
    private Long messageUsage;
    @Schema(description = "Token usage information")
    private TokenUsageDTO tokenUsage;

    public static MemoryUsageDTO from(MemoryUsage memoryUsage) {
        MemoryUsageDTO dto = new MemoryUsageDTO();

        if (memoryUsage == null) {
            return dto;
        }

        dto.setMessageUsage(memoryUsage.messageUsage());
        dto.setTokenUsage(TokenUsageDTO.from(memoryUsage.tokenUsage()));

        return dto;
    }
}
