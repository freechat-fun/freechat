package fun.freechat.service.ai.message;

import dev.langchain4j.internal.ValidationUtils;
import fun.freechat.service.enums.ChatContentType;
import fun.freechat.service.util.StoreUtils;
import fun.freechat.util.HttpUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import static fun.freechat.service.enums.ChatContentType.IMAGE;
import static fun.freechat.service.enums.ChatContentType.TEXT;

@Data
public class ChatContent {
    private ChatContentType type;
    private String content;
    private String mimeType;

    public static ChatContent fromText(String text, String mimeType) {
        ValidationUtils.ensureNotBlank(text, "content");
        ChatContent content = new ChatContent();
        content.setType(TEXT);
        content.setContent(text);
        content.setMimeType(mimeType);

        return content;
    }

    public static ChatContent fromText(String text) {
        return fromText(text, null);
    }

    public static ChatContent fromImage(String imageInfo, String mimeType) {
        ValidationUtils.ensureNotBlank(imageInfo, "content");
        ChatContent content = new ChatContent();
        content.setType(IMAGE);
        content.setContent(imageInfo);
        if (StringUtils.isNotBlank(mimeType)) {
            content.setMimeType(mimeType);
        } else if (!HttpUtils.isValidUrl(imageInfo)) {
            // base64 data
            content.setMimeType(StoreUtils.guessMimeTypeOfBase64Data(imageInfo));
        }

        return content;
    }
}
