package fun.freechat.service.ai.message;

import fun.freechat.service.enums.PromptRole;
import lombok.Data;

import java.util.Date;

@Data
public class ChatMessage {
    private PromptRole role;
    private String name;
    private String content;
    private ChatFunctionCall functionCall;
    private Date gmtCreate;
}
