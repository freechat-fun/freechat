package fun.freechat.service.ai.tool;

import lombok.Data;

@Data
public class ToolSpecification {
    private String name;
    private String description;
    private ToolParameters parameters;
}

