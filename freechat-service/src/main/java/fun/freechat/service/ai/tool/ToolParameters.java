package fun.freechat.service.ai.tool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class ToolParameters {
    private String type = "object";
    private Map<String, ToolProperties> properties;
    private List<String> required = new ArrayList<>();
    private String description;
    private String example;
}
