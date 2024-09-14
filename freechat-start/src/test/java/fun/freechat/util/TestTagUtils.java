package fun.freechat.util;

import fun.freechat.model.Tag;
import fun.freechat.model.User;
import fun.freechat.service.account.SysUserService;
import fun.freechat.service.common.TagService;
import fun.freechat.service.enums.InfoType;
import fun.freechat.service.prompt.PromptService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Component
public class TestTagUtils implements ApplicationContextAware {
    private static SysUserService userService;
    private static TagService tagService;
    private static PromptService promptService;

    public static void addTag(String userId, Long promptId, String content) {
        User user = userService.loadByUserId(userId);
        assertNotNull(user);
        tagService.create(user, InfoType.PROMPT, promptService.getUid(promptId), content);
    }

    public static void cleanTags(String userId) {
        User user = userService.loadByUserId(userId);
        if (user == null) {
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
        promptService = applicationContext.getBean(PromptService.class);
    }
}
