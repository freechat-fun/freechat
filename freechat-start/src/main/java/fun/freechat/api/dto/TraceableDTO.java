package fun.freechat.api.dto;

import fun.freechat.util.TraceUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class TraceableDTO {
    @Schema(description = "Request identifier")
    private String requestId;

    public TraceableDTO() {
        this.requestId = TraceUtils.getTraceId();
    }

    public TraceableDTO(String requestId) {
        this.requestId = requestId;
    }
}
