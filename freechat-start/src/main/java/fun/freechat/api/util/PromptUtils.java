package fun.freechat.api.util;

import fun.freechat.model.PromptInfo;
import fun.freechat.service.enums.Visibility;
import fun.freechat.service.prompt.PromptService;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
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

    private static void resetPromptInfo(PromptInfo info, String parentUid) {
        if (StringUtils.isNotBlank(parentUid)) {
            info.setParentUid(parentUid);
            info.setVisibility(Visibility.PRIVATE.text());
        }
        info.setName(newUniqueName(info.getName()));
        info.setPromptId(null);
        info.setVersion(1);
        info.setUserId(AccountUtils.currentUser().getUserId());
    }

    public static void resetPromptInfoTriple(
            Triple<PromptInfo, List<String>, List<String>> infoTriple, String parentUid) {
        resetPromptInfo(infoTriple.getLeft(), parentUid);
    }

    public static String newUniqueName(String desired) {
        int index = 0;
        String name = desired;
        while (promptService.existsName(name, AccountUtils.currentUser())) {
            index++;
            name = desired + "-" + index;
        }
        return name;
    }
}
