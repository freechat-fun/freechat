package fun.freechat.api.util;

import dev.langchain4j.data.message.ContentType;
import lombok.extern.slf4j.Slf4j;

import static dev.langchain4j.data.message.ContentType.IMAGE;

@Slf4j
public class ChatUtils {
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
}
