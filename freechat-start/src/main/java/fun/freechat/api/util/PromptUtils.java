package fun.freechat.api.util;

import fun.freechat.service.prompt.PromptService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class PromptUtils implements ApplicationContextAware {
    private static PromptService promptService;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        promptService = applicationContext.getBean(PromptService.class);
    }

    public static Long uidToLatestId(String promptUid) {
        return promptService.getLatestIdByUid(promptUid, AccountUtils.currentUser());
    }

    public static String idToUid(Long promptId) {
        return promptService.getUid(promptId);
    }
}
