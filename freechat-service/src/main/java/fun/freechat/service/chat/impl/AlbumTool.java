package fun.freechat.service.chat.impl;

import fun.freechat.model.CharacterInfo;
import fun.freechat.service.character.CharacterService;
import fun.freechat.service.chat.ChatContextService;
import fun.freechat.service.chat.ChatService;
import fun.freechat.service.common.FileStore;
import fun.freechat.service.common.ShortLinkService;
import fun.freechat.service.util.StoreUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static dev.langchain4j.internal.ValidationUtils.ensureNotNull;
import static fun.freechat.service.util.StoreUtils.PUBLIC_DIR;

@Slf4j
public class AlbumTool {
    private static final String DEFAULT_PICTURE = "<NOT_FOUND>";

    private final String homeUrl;
    private final CharacterService characterService;
    private final ChatContextService chatContextService;
    private final ShortLinkService shortLinkService;

    public AlbumTool(String homeUrl,
                     CharacterService characterService,
                     ChatContextService chatContextService,
                     ShortLinkService shortLinkService) {
        this.homeUrl = ensureNotNull(homeUrl, "homeUrl");
        this.characterService = ensureNotNull(characterService, "characterService");
        this.chatContextService = ensureNotNull(chatContextService, "chatContextService");
        this.shortLinkService = ensureNotNull(shortLinkService, "shortLinkService");
    }

    public String findAnImage(Object memoryId, String description) {
        CharacterInfo characterInfo = Optional.ofNullable(memoryId)
                .map(ChatService::asChatId)
                .filter(StringUtils::isNotBlank)
                .map(chatContextService::getCharacterUid)
                .filter(StringUtils::isNotBlank)
                .map(characterService::summaryByUid)
                .orElse(null);

        if (characterInfo == null) {
            return DEFAULT_PICTURE;
        }

        String characterPicture = characterInfo.getPicture();
        String characterAvatar = characterInfo.getAvatar();

        FileStore fileStore = StoreUtils.defaultFileStore();
        try {
            String dstDir = PUBLIC_DIR + characterInfo.getUserId() + "/character/picture/" + characterInfo.getCharacterUid();
            List<String> pictures = fileStore.list(dstDir, null, false)
                    .stream()
                    // order by last modified time
                    .sorted(Comparator.comparing(path -> {
                        try {
                            return fileStore.getLastModifiedTime(path);
                        } catch (IOException e) {
                            return Long.MAX_VALUE;
                        }
                    }, Long::compareTo))
                    .toList();

            if (pictures.isEmpty()) {
                return defaultPicture(characterAvatar, characterPicture);
            }

            String picturePath = pictures.getFirst();
            String pictureUrl = fileStore.getShareUrl(picturePath, Integer.MAX_VALUE);
            if (StringUtils.isBlank(pictureUrl)) {
                String subPath = picturePath.substring(PUBLIC_DIR.length());
                String key =  Base64.getUrlEncoder().encodeToString(subPath.getBytes(StandardCharsets.UTF_8));
                // use short link to reduce token usage
                String shortPath = shortLinkService.shorten("/public/image/" + key);
                pictureUrl = "%s/s/%s".formatted(homeUrl, shortPath);
            }

            if (StringUtils.isNotBlank(picturePath)) {
                // touch file so that it is at the end of list next time
                fileStore.setLastModifiedTime(picturePath, System.currentTimeMillis());
            }

            log.info("Found a picture for character {}, description: {}, path: {}, url: {}",
                    characterInfo.getName(), description, picturePath, pictureUrl);

            return pictureUrl;
        } catch (IOException e) {
            log.warn("Failed to list album of character: {} ({})",
                    characterInfo.getName(),
                    characterInfo.getCharacterUid());
            return defaultPicture(characterAvatar, characterPicture);
        }
    }

    private String defaultPicture(String avatar, String picture) {
        return StringUtils.isNotBlank(avatar) ?
                shortenPictureUrl(avatar) :
                StringUtils.isNotBlank(picture) ? shortenPictureUrl(picture) : DEFAULT_PICTURE;
    }

    private String shortenPictureUrl(String pictureUrl) {
        if (!pictureUrl.startsWith(homeUrl)) {
            return pictureUrl;
        }
        String urlPath = pictureUrl.substring(homeUrl.length());
        String shortPath = shortLinkService.shorten(urlPath);
        return "%s/s/%s".formatted(homeUrl, shortPath);
    }
}
