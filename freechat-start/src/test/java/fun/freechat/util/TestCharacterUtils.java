package fun.freechat.util;

import fun.freechat.model.CharacterBackend;
import fun.freechat.model.CharacterInfo;
import fun.freechat.model.User;
import fun.freechat.service.character.CharacterService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Component
public class TestCharacterUtils implements ApplicationContextAware {
    private static CharacterService characterService;

    public static Long createCharacter(String userId) {
        return createCharacter(userId, "en");
    }

    public static Long createCharacter(String userId, String lang) {
        CharacterInfo info = new CharacterInfo()
                .withName("test-bot")
                .withUserId(userId)
                .withLang(lang)
                .withVisibility("public");
        assertTrue(characterService.create(Pair.of(info, null)));
        return info.getCharacterId();
    }

    public static String createCharacterBackend(String characterUid, String promptTaskId) {
        CharacterBackend backend = new CharacterBackend()
                .withCharacterUid(characterUid)
                .withChatPromptTaskId(promptTaskId)
                .withIsDefault((byte) 1)
                .withMessageWindowSize(50);
        String backendId = characterService.addBackend(backend);
        assertNotNull(backendId);
        return backendId;
    }

    public static void prioritizeCharacter(Long characterId) {
        CharacterInfo info = new CharacterInfo()
                .withCharacterId(characterId)
                .withPriority(2);

        characterService.update(Pair.of(info, null));
    }

    public static void deleteCharacters(String userId) {
        if (StringUtils.isBlank(userId)) {
            return;
        }
        User user = new User().withUserId(userId);
        characterService.removeBackendsByUser(user);
        characterService.delete(user);
    }

    public static String idToUid(Long characterId) {
        return characterService.getUid(characterId);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        characterService = applicationContext.getBean(CharacterService.class);
    }
}
