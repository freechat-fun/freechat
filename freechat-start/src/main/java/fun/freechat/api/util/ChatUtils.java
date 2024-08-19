package fun.freechat.api.util;

import dev.langchain4j.data.message.ContentType;
import fun.freechat.service.chat.ChatMemoryService;
import fun.freechat.service.chat.ChatMessageRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import static dev.langchain4j.data.message.ChatMessageType.USER;
import static dev.langchain4j.data.message.ContentType.IMAGE;

@Slf4j
@Component
public class ChatUtils implements ApplicationContextAware {
    private static ChatMemoryService chatMemoryService;

    public static ContentType contentTypeOf(String typeText) {
        return switch (typeText) {
            case "image" -> IMAGE;
            case null, default -> ContentType.TEXT;
        };
    }

    public static String contentTypeText(ContentType type) {
        return switch (type) {
            case IMAGE -> "image";
            default -> "text";
        };
    }

    public static String getChatMessageExt(ChatMessageRecord record) {
        if (record.getMessage().type() == USER) {
            return chatMemoryService.loadSystemMessage(record.getId());
        } else {
            return record.getExt();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        chatMemoryService = applicationContext.getBean(ChatMemoryService.class);
    }
}
