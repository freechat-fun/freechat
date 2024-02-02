package fun.freechat.api.dto;

import fun.freechat.service.ai.message.ChatContent;
import fun.freechat.service.enums.ChatContentType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.http.MediaType;

import java.util.Objects;

import static fun.freechat.service.enums.ChatContentType.IMAGE;
import static fun.freechat.service.enums.ChatContentType.TEXT;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "Chat content")
@Data
public class ChatContentDTO {
    @Schema(description = "Chat type: text (default) | image")
    private String type;
    @Schema(description = "Chat content(for image, it might be an url or base64 encoded string)", requiredMode = REQUIRED)
    private String content;
    @Schema(description = "Mime-type of content")
    private String mimeType;

    public static ChatContentDTO from(ChatContent content) {
        if (Objects.isNull(content)) {
            return null;
        }
        ChatContentDTO dto = new ChatContentDTO();
        dto.setType(content.getType().text());
        dto.setContent(content.getContent());
        dto.setMimeType(content.getMimeType());

        return dto;
    }

    public static ChatContentDTO fromText(String text) {
        if (Objects.isNull(text)) {
            return null;
        }

        ChatContentDTO dto = new ChatContentDTO();
        dto.setType(TEXT.text());
        dto.setContent(text);
        dto.setMimeType(MediaType.TEXT_PLAIN_VALUE);

        return dto;
    }

    public ChatContent toChatContent() {
        if (ChatContentType.of(getType()) == IMAGE) {
            return ChatContent.fromImage(getContent(), getMimeType());
        } else {
            return ChatContent.fromText(getContent(), getMimeType());
        }
    }
}
