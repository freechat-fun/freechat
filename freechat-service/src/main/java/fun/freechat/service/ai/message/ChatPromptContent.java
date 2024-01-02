package fun.freechat.service.ai.message;

import lombok.Data;

import java.util.List;

@Data
public class ChatPromptContent {
    private String system;
    private ChatMessage messageToSend;
    private List<ChatMessage> messages;
    private String format;
}
