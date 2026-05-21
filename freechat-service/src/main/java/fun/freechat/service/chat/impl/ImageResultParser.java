package fun.freechat.service.chat.impl;

import static fun.freechat.service.util.StoreUtils.PUBLIC_DIR;
import static fun.freechat.service.util.StoreUtils.defaultFileStore;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.data.image.Image;
import dev.langchain4j.data.message.AiMessage;
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

public class ImageResultParser {
    private static final Logger log = LoggerFactory.getLogger(ImageResultParser.class);

    private static final String IMAGE_MIME_PREFIX = "image/";

    @Agent(outputKey = "imageResult")
    public ImageResult parse(
            @V("chatId") String chatId,
            @V("homeUrl") String homeUrl,
            @V("shortLinkService") ShortLinkService shortLinkService,
            @V("generatedImage") Result<AiMessage> result) {
        AiMessage message = result.content();
        String url = Optional.ofNullable(message.images())
                .filter(CollectionUtils::isNotEmpty)
                .map(List::getFirst)
                .map(Image::url)
                .map(URI::toString)
                .orElse("");

        return ImageResult.builder()
                .url(transferUrl(url, chatId, homeUrl, shortLinkService))
                .description(message.text())
                .build();
    }

    private static String transferUrl(
            String temporaryUrl,
            String chatId,
            String homeUrl,
            ShortLinkService shortLinkService) {
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

        String target = PUBLIC_DIR + "chat/files/" + chatId + "/" + IdUtils.newShortId() + "." + extension;
        FileStore fileStore = defaultFileStore();
        try {
            fileStore.write(target, downloaded.content());
            String targetUrl = fileStore.getShareUrl(target, Integer.MAX_VALUE);
            if (StringUtils.isBlank(targetUrl)) {
                String subPath = target.substring(PUBLIC_DIR.length());
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
            log.warn("Failed to save image from {} to {}", temporaryUrl, target, e);
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
