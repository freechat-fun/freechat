package fun.freechat.api.dto;

import dev.langchain4j.data.image.Image;
import dev.langchain4j.data.message.Content;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import fun.freechat.service.util.StoreUtils;
import fun.freechat.util.HttpUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;

import java.util.Objects;

import static dev.langchain4j.data.message.ContentType.IMAGE;
import static dev.langchain4j.data.message.ContentType.TEXT;
import static fun.freechat.api.util.ChatUtils.contentTypeOf;
import static fun.freechat.api.util.ChatUtils.contentTypeText;
import static fun.freechat.api.util.ValidationUtils.ensureNotNull;
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

    public static ChatContentDTO from(Content content) {
        ensureNotNull(content, "content");
        if (content.type() == IMAGE) {
            Image image = ((ImageContent) content).image();
            if (Objects.nonNull(image.url())) {
                return ChatContentDTO.fromImage(image.url().toString(), image.mimeType());
            } else {
                return ChatContentDTO.fromImage(image.base64Data(), image.mimeType());
            }
        } else {
            return ChatContentDTO.fromText(((TextContent) content).text());
        }
    }

    public static ChatContentDTO fromText(String text) {
        return fromText(text, MediaType.TEXT_PLAIN_VALUE);
    }

    public static ChatContentDTO fromText(String text, String mimeType) {
        ensureNotNull(text, "content");
        ChatContentDTO dto = new ChatContentDTO();
        dto.setType(contentTypeText(TEXT));
        dto.setContent(text);
        dto.setMimeType(mimeType);

        return dto;
    }

    public static ChatContentDTO fromImage(String imageInfo, String mimeType) {
        ensureNotNull(imageInfo, "content");
        ChatContentDTO content = new ChatContentDTO();
        content.setType(contentTypeText(IMAGE));
        content.setContent(imageInfo);
        if (StringUtils.isNotBlank(mimeType)) {
            content.setMimeType(mimeType);
        } else if (!HttpUtils.isValidUrl(imageInfo)) {
            // base64 data
            content.setMimeType(StoreUtils.guessMimeTypeOfBase64Data(imageInfo));
        }

        return content;
    }

    public Content toContent() {
        if (contentTypeOf(getType()) == IMAGE) {
            String imageInfo = getContent();
            if (HttpUtils.isValidUrl(imageInfo)) {
                return ImageContent.from(imageInfo);
            } else {
                return ImageContent.from(imageInfo, getMimeType());
            }
        } else {
            return TextContent.from(getContent());
        }
    }
}
