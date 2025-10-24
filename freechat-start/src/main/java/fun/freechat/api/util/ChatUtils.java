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
import static dev.langchain4j.data.message.ContentType.*;

@Slf4j
@Component
public class ChatUtils implements ApplicationContextAware {
    private static ChatMemoryService chatMemoryService;

    public static ContentType contentTypeOf(String typeText) {
        return switch (typeText) {
            case "image" -> IMAGE;
            case "video" -> VIDEO;
            case "audio" -> AUDIO;
            case "pdf" -> PDF;
            case null, default -> ContentType.TEXT;
        };
    }

    public static String contentTypeText(ContentType type) {
        return switch (type) {
            case IMAGE -> "image";
            case VIDEO -> "video";
            case AUDIO -> "audio";
            case PDF -> "pdf";
            case null, default -> "text";
        };
    }

    public static String getChatMessageExt(ChatMessageRecord messageRecord) {
        if (messageRecord.getMessage().type() == USER) {
            return chatMemoryService.loadSystemMessage(messageRecord.getId());
        } else {
            return messageRecord.getExt();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        chatMemoryService = applicationContext.getBean(ChatMemoryService.class);
    }
}
