package fun.freechat.service.ai.message;

import lombok.Data;

@Data
public class ChatFunctionCall {
    private String name;
    private String arguments;
}
