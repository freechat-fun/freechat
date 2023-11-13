package fun.freechat.service.ai.message;

import lombok.Data;

@Data
public class ChatToolCall {
    private String name;
    private String arguments;
}
