package fun.freechat.service.chat.impl;

import fun.freechat.model.CharacterInfo;
import fun.freechat.service.character.CharacterService;
import fun.freechat.service.chat.ChatContextService;
import fun.freechat.service.chat.ChatService;
import fun.freechat.service.common.FileStore;
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

    public AlbumTool(String homeUrl,
                     CharacterService characterService,
                     ChatContextService chatContextService) {
        this.homeUrl = ensureNotNull(homeUrl, "homeUrl");
        this.characterService = ensureNotNull(characterService, "characterService");
        this.chatContextService = ensureNotNull(chatContextService, "chatContextService");
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

            String pictureUrl = null;
            String picturePath = null;
            for (String path : pictures) {
                picturePath = path;
                pictureUrl = fileStore.getShareUrl(picturePath, Integer.MAX_VALUE);
                if (StringUtils.isBlank(pictureUrl)) {
                    String subPath = path.substring(PUBLIC_DIR.length());
                    String key =  Base64.getUrlEncoder().encodeToString(subPath.getBytes(StandardCharsets.UTF_8));
                    pictureUrl = "%s/public/image/%s".formatted(homeUrl, key);
                }

                if (pictureUrl.equals(characterAvatar) || pictureUrl.equals(characterPicture)) {
                    // character avatar & picture are already showed on UI
                    picturePath = null;
                    pictureUrl = null;
                }
            }

            if (StringUtils.isBlank(pictureUrl)) {
                return defaultPicture(characterAvatar, characterPicture);
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

    private static String defaultPicture(String avatar, String picture) {
        return StringUtils.isNotBlank(avatar) ?
                avatar :
                StringUtils.isNotBlank(picture) ? picture : DEFAULT_PICTURE;
    }
}
