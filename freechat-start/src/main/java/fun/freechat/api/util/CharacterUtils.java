package fun.freechat.api.util;

import fun.freechat.model.CharacterInfo;
import fun.freechat.service.character.CharacterService;
import fun.freechat.service.enums.Visibility;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CharacterUtils implements ApplicationContextAware {
    private static CharacterService characterService;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        characterService = applicationContext.getBean(CharacterService.class);
    }

    private static void resetCharacterInfo(CharacterInfo info, String parentUid) {
        if (StringUtils.isNotBlank(parentUid)) {
            info.setParentUid(parentUid);
            info.setVisibility(Visibility.PRIVATE.text());
        }
        info.setName(newUniqueName(info.getName()));
        info.setCharacterId(null);
        info.setVersion(1);
        info.setUserId(AccountUtils.currentUser().getUserId());
    }

    public static void resetCharacterInfoPair(
            Pair<CharacterInfo, List<String>> infoPair, String parentUid) {
        resetCharacterInfo(infoPair.getLeft(), parentUid);
    }

    public static String newUniqueName(String desired) {
        int index = 0;
        String name =desired;
        while (characterService.existsName(name, AccountUtils.currentUser())) {
            index++;
            name = desired + "-" + index;
        }
        return name;
    }
}
