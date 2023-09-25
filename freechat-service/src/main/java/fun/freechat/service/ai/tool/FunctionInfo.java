package fun.freechat.service.ai.tool;

import lombok.Data;

@Data
public class FunctionInfo {
    private String name;
    private String description;
    private FunctionParameters parameters;
}

