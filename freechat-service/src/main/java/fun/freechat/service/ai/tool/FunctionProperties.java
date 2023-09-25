package fun.freechat.service.ai.tool;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class FunctionProperties {
    private String description;
    private String example;
    private String type;
    @JsonProperty("enum")
    private List<String> enums;
}
