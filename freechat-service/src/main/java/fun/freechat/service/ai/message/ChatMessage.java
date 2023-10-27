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

    public static ChatMessage from(PromptRole role, String text) {
        ChatMessage message = new ChatMessage();
        message.setRole(role);
        message.setContent(text);
        message.setGmtCreate(new Date());
        return message;
    }

    public ChatMessage withName(String name) {
        setName(name);
        return this;
    }
}
