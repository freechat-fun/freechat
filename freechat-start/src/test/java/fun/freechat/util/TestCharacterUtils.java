package fun.freechat.util;

import fun.freechat.model.CharacterInfo;
import fun.freechat.model.User;
import fun.freechat.service.character.CharacterService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Component
public class TestCharacterUtils implements ApplicationContextAware {
    private static CharacterService characterService;

    public static Long createCharacter(String userId) {
        CharacterInfo info = new CharacterInfo()
                .withName("test-bot")
                .withUserId(userId)
                .withVisibility("public");
        assertTrue(characterService.create(Pair.of(info, null)));
        return info.getCharacterId();
    }

    public static void deleteCharacters(String userId) {
        if (StringUtils.isBlank(userId)) {
            return;
        }
        User user = new User().withUserId(userId);
        characterService.delete(user);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        characterService = applicationContext.getBean(CharacterService.class);

    }
}
