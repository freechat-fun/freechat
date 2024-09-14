package fun.freechat.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import fun.freechat.util.TraceUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@JsonInclude(NON_NULL)
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
