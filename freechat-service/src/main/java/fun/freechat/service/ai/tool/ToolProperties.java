package fun.freechat.service.ai.tool;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
public class ToolProperties {
    private String description;
    private String example;
    private String type;

    @JsonProperty("enum")
    private List<String> enums;
}
