package fun.freechat.util;

import fun.freechat.model.Tag;
import fun.freechat.model.User;
import fun.freechat.service.account.SysUserService;
import fun.freechat.service.common.TagService;
import fun.freechat.service.enums.InfoType;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Component
public class TestTagUtils implements ApplicationContextAware {
    private static SysUserService userService;
    private static TagService tagService;

    public static void addTag(String userId, String promptId, String content) {
        User user = userService.loadByUserId(userId);
        assertNotNull(user);
        tagService.create(user, InfoType.PROMPT, promptId, content);
    }

    public static void cleanTags(String userId) {
        User user = userService.loadByUserId(userId);
        if (Objects.isNull(user)) {
            return;
        }

        for (Tag tag : tagService.list(user, InfoType.PROMPT)) {
            tagService.delete(user, InfoType.PROMPT, tag.getReferId(), tag.getContent());
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        userService = applicationContext.getBean(SysUserService.class);
        tagService = applicationContext.getBean(TagService.class);
    }
}
