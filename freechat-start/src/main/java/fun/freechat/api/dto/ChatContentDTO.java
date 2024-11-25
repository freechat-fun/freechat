package fun.freechat.api.dto;

import dev.langchain4j.data.image.Image;
import dev.langchain4j.data.message.Content;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import fun.freechat.service.util.PromptUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import static dev.langchain4j.data.message.ContentType.IMAGE;
import static dev.langchain4j.data.message.ContentType.TEXT;
import static fun.freechat.api.util.ChatUtils.contentTypeOf;
import static fun.freechat.api.util.ChatUtils.contentTypeText;
import static fun.freechat.api.util.ValidationUtils.ensureNotNull;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "Chat content")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatContentDTO {
    @Schema(description = "Chat type: text (default) | image")
    private String type;
    @Schema(description = "Chat content(for image, it might be a normal url or data url)", requiredMode = REQUIRED)
    private String content;

    public static ChatContentDTO from(Content content) {
        ensureNotNull(content, "content");
        if (content.type() == IMAGE) {
            Image image = ((ImageContent) content).image();
            if (image.url() != null) {
                return ChatContentDTO.fromImage(image.url().toString());
            } else {
                String dataUrl = "data:%s;base64,%s";
                return ChatContentDTO.fromImage(dataUrl.formatted(image.mimeType(), image.base64Data()));
            }
        } else {
            return ChatContentDTO.fromText(((TextContent) content).text());
        }
    }

    public static ChatContentDTO fromText(String text) {
        ensureNotNull(text, "content");
        return ChatContentDTO.builder()
                .type(contentTypeText(TEXT))
                .content(text)
                .build();
    }

    public static ChatContentDTO fromImage(String imageInfo) {
        ensureNotNull(imageInfo, "content");
        return ChatContentDTO.builder()
                .type(contentTypeText(IMAGE))
                .content(imageInfo)
                .build();
    }

    public Content toContent() {
        if (contentTypeOf(getType()) == IMAGE) {
            Pair<String, String> imageInfo = PromptUtils.parseDataMimeType(getContent());

            return StringUtils.isBlank(imageInfo.getRight()) ?
                    ImageContent.from(imageInfo.getLeft()) :
                    ImageContent.from(imageInfo.getLeft(), imageInfo.getRight());
        } else {
            return TextContent.from(getContent());
        }
    }
}
