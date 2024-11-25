package fun.freechat.api.dto;

import dev.langchain4j.agent.tool.ToolExecutionRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static fun.freechat.api.util.ValidationUtils.ensureNotNull;


@Schema(description = "Tool call information during the conversation")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatToolCallDTO {
    @Schema(description = "Tool id")
    private String id;
    @Schema(description = "Tool name")
    private String name;
    @Schema(description = "Tool parameters")
    private String arguments;

    public static ChatToolCallDTO from(ToolExecutionRequest request) {
        ensureNotNull(request, "tool");
        return ChatToolCallDTO.builder()
                .id(request.id())
                .name(request.name())
                .arguments(request.arguments())
                .build();
    }

    public ToolExecutionRequest toToolExecutionRequest() {
        return ToolExecutionRequest.builder()
                .id(getId())
                .name(getName())
                .arguments(getArguments())
                .build();
    }
}
