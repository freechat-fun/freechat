package fun.freechat.api.dto;

import dev.langchain4j.agent.tool.ToolExecutionRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import static fun.freechat.api.util.ValidationUtils.ensureNotNull;


@Schema(description = "Tool call information during the conversation")
@Data
public class ChatToolCallDTO {
    @Schema(description = "Tool id")
    private String id;
    @Schema(description = "Tool name")
    private String name;
    @Schema(description = "Tool parameters")
    private String arguments;

    public static ChatToolCallDTO from(ToolExecutionRequest request) {
        ensureNotNull(request, "tool");
        ChatToolCallDTO dto = new ChatToolCallDTO();
        dto.setId(request.id());
        dto.setName(request.name());
        dto.setArguments(request.arguments());
        return dto;
    }

    public ToolExecutionRequest toToolExecutionRequest() {
        return ToolExecutionRequest.builder()
                .id(getId())
                .name(getName())
                .arguments(getArguments())
                .build();
    }
}
