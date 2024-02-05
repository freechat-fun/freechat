package fun.freechat.service.ai.message;

import dev.langchain4j.internal.ValidationUtils;
import fun.freechat.service.enums.PromptRole;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static fun.freechat.service.enums.ChatContentType.TEXT;

@Data
@Slf4j
public class ChatMessage {
    private PromptRole role;
    private String name;
    private List<ChatContent> contents;
    private List<ChatToolCall> toolCalls;
    private Date gmtCreate;

    public static ChatMessage from(PromptRole role, String text) {
        ChatMessage message = new ChatMessage();
        message.setRole(role);
        message.setContents(List.of(ChatContent.fromText(text)));
        message.setGmtCreate(new Date());
        return message;
    }

    public String getContentText() {
        return ValidationUtils.ensureNotEmpty(contents, "contents")
                .stream()
                .filter(content -> content.getType() == TEXT)
                .map(ChatContent::getContent)
                .collect(Collectors.joining("\n"));

    }

    public void setContentText(String text) {
        setContents(Collections.singletonList(ChatContent.fromText(text)));
    }

    public ChatMessage withName(String name) {
        setName(name);
        return this;
    }
}
