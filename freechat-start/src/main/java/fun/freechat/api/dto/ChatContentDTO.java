package fun.freechat.api.dto;

import dev.langchain4j.data.audio.Audio;
import dev.langchain4j.data.image.Image;
import dev.langchain4j.data.message.*;
import dev.langchain4j.data.pdf.PdfFile;
import dev.langchain4j.data.video.Video;
import fun.freechat.service.util.PromptUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import static dev.langchain4j.data.message.ContentType.*;
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
    @Schema(description = "Chat type: text (default) | image | video | audio | pdf | ...")
    @Pattern(regexp = "text|image|video|audio|pdf|...")
    private String type;
    @Schema(description = "Chat content(for image, it might be a normal url or data url)", requiredMode = REQUIRED)
    private String content;

    public static ChatContentDTO from(Content content) {
        ensureNotNull(content, "content");
        if (content.type() == IMAGE) {
            Image image = ((ImageContent) content).image();
            if (image.url() != null) {
                return ChatContentDTO.from(IMAGE, image.url().toString());
            } else {
                String dataUrl = "data:%s;base64,%s";
                return ChatContentDTO.from(IMAGE, dataUrl.formatted(image.mimeType(), image.base64Data()));
            }
        } else if (content.type() == VIDEO) {
            Video video = ((VideoContent) content).video();
            if (video.url() != null) {
                return ChatContentDTO.from(VIDEO, video.url().toString());
            } else {
                String dataUrl = "data:%s;base64,%s";
                return ChatContentDTO.from(VIDEO, dataUrl.formatted(video.mimeType(), video.base64Data()));
            }
        } else if (content.type() == AUDIO) {
            Audio audio = ((AudioContent) content).audio();
            if (audio.url() != null) {
                return ChatContentDTO.from(AUDIO, audio.url().toString());
            } else {
                String dataUrl = "data:%s;base64,%s";
                return ChatContentDTO.from(AUDIO, dataUrl.formatted(audio.mimeType(), audio.base64Data()));
            }
        } else if (content.type() == PDF) {
            PdfFile pdf = ((PdfFileContent) content).pdfFile();
            if (pdf.url() != null) {
                return ChatContentDTO.from(PDF, pdf.url().toString());
            } else {
                String dataUrl = "data:%s;base64,%s";
                return ChatContentDTO.from(PDF, dataUrl.formatted(pdf.mimeType(), pdf.base64Data()));
            }
        } else {
            return ChatContentDTO.from(TEXT, ((TextContent) content).text());
        }
    }

    public static ChatContentDTO from(ContentType type, String info) {
        ensureNotNull(info, "content");
        return ChatContentDTO.builder()
                .type(contentTypeText(type))
                .content(info)
                .build();
    }

    public Content toContent() {
        ContentType contentType = contentTypeOf(getType());
        return switch (contentType) {
            case IMAGE -> {
                Pair<String, String> imageInfo = PromptUtils.parseDataMimeType(getContent());
                yield StringUtils.isBlank(imageInfo.getRight()) ?
                        ImageContent.from(imageInfo.getLeft()) :
                        ImageContent.from(imageInfo.getLeft(), imageInfo.getRight());
            }
            case VIDEO -> {
                Pair<String, String> videoInfo = PromptUtils.parseDataMimeType(getContent());
                yield StringUtils.isBlank(videoInfo.getRight()) ?
                        VideoContent.from(videoInfo.getLeft()) :
                        VideoContent.from(videoInfo.getLeft(), videoInfo.getRight());
            }
            case AUDIO -> {
                Pair<String, String> audioInfo = PromptUtils.parseDataMimeType(getContent());
                yield StringUtils.isBlank(audioInfo.getRight()) ?
                        AudioContent.from(audioInfo.getLeft()) :
                        AudioContent.from(audioInfo.getLeft(), audioInfo.getRight());
            }
            case PDF -> {
                Pair<String, String> pdfInfo = PromptUtils.parseDataMimeType(getContent());
                yield StringUtils.isBlank(pdfInfo.getRight()) ?
                        PdfFileContent.from(pdfInfo.getLeft()) :
                        PdfFileContent.from(pdfInfo.getLeft(), pdfInfo.getRight());
            }
            default -> TextContent.from(getContent());
        };
    }
}
