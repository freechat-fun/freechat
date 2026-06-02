package fun.freechat.service.chat.impl;

import static fun.freechat.service.util.StoreUtils.PUBLIC_DIR;
import static fun.freechat.service.util.StoreUtils.defaultFileStore;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.data.image.Image;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.Content;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.V;
import fun.freechat.service.common.FileStore;
import fun.freechat.service.common.ShortLinkService;
import fun.freechat.util.HttpUtils;
import fun.freechat.util.IdUtils;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageContentParser {
    private static final Logger log = LoggerFactory.getLogger(ImageContentParser.class);

    private static final String IMAGE_MIME_PREFIX = "image/";

    @Agent(outputKey = "imageResult")
    public Content parse(
            @V("chatId") String chatId,
            @V("homeUrl") String homeUrl,
            @V("shortLinkService") ShortLinkService shortLinkService,
            @V("generatedImage") Result<AiMessage> result) {
        String url = Optional.ofNullable(result.content())
                .map(AiMessage::images)
                .filter(CollectionUtils::isNotEmpty)
                .map(List::getFirst)
                .map(Image::url)
                .map(URI::toString)
                .map(temporaryUrl -> transferUrl(temporaryUrl, chatId, homeUrl, shortLinkService))
                .orElse("");

        // return ImageContent.from(url);
        return TextContent.from(url);
    }

    private static String transferUrl(
            String temporaryUrl, String chatId, String homeUrl, ShortLinkService shortLinkService) {
        if (StringUtils.isBlank(temporaryUrl)) {
            return temporaryUrl;
        }

        HttpUtils.DownloadResult downloaded = HttpUtils.download(temporaryUrl);
        if (downloaded == null || downloaded.content() == null || downloaded.content().length == 0) {
            return temporaryUrl;
        }

        String extension = imageExtensionFromContentType(downloaded.contentType());
        if (extension == null) {
            // Not an image, do not save.
            return temporaryUrl;
        }

        String targetDir = PUBLIC_DIR + "chat/files/" + chatId;
        String targetFile = targetDir + "/" + IdUtils.newShortId() + "." + extension;
        FileStore fileStore = defaultFileStore();
        try {
            if (!fileStore.exists(targetDir)) {
                fileStore.createDirectories(targetDir);
            }
            fileStore.write(targetFile, downloaded.content());
            String targetUrl = fileStore.getShareUrl(targetFile, Integer.MAX_VALUE);
            if (StringUtils.isBlank(targetUrl)) {
                String subPath = targetFile.substring(PUBLIC_DIR.length());
                String key = Base64.getUrlEncoder().encodeToString(subPath.getBytes(StandardCharsets.UTF_8));
                String targetPath = "/public/image/" + key;
                try {
                    // use short link to reduce token usage
                    targetPath = shortLinkService.shorten("/public/image/" + key);
                    targetUrl = "%s/s/%s".formatted(homeUrl, targetPath);
                } catch (Exception e) {
                    log.warn("Failed to shorten path: {}", targetPath);
                    targetUrl = homeUrl + targetPath;
                }
            }
            return targetUrl;
        } catch (IOException e) {
            log.warn("Failed to save image from {} to {}", temporaryUrl, targetFile, e);
            return temporaryUrl;
        }
    }

    private static String imageExtensionFromContentType(String contentType) {
        if (StringUtils.isBlank(contentType)) {
            return null;
        }
        String type = contentType.toLowerCase();
        int sep = type.indexOf(';');
        if (sep >= 0) {
            type = type.substring(0, sep).trim();
        }
        if (!type.startsWith(IMAGE_MIME_PREFIX)) {
            return null;
        }
        String subtype = type.substring(IMAGE_MIME_PREFIX.length()).trim();
        if (subtype.isEmpty()) {
            return null;
        }
        return switch (subtype) {
            case "jpeg" -> "jpg";
            case "svg+xml" -> "svg";
            case "x-icon", "vnd.microsoft.icon" -> "ico";
            default -> subtype;
        };
    }
}
