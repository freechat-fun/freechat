package fun.freechat.util;

import fun.freechat.model.PromptInfo;
import fun.freechat.model.User;
import fun.freechat.service.enums.Visibility;
import fun.freechat.service.prompt.PromptService;
import fun.freechat.service.prompt.PromptTaskService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Component
@Slf4j
public class TestPromptUtils implements ApplicationContextAware {
    private static PromptService promptService;
    private static PromptTaskService promptTaskService;

    public static Long createPrompt(String userId, String template, String draft) {
        Date now = new Date();
        PromptInfo promptInfo = new PromptInfo()
                .withGmtCreate(now)
                .withGmtModified(now)
                .withVersion(0)
                .withVisibility(Visibility.PUBLIC.text())
                .withUserId(userId)
                .withName("Unit-test Prompt for " + userId)
                .withDescription("Prompt for unit-test.")
                .withTemplate(template)
                .withDraft(draft);
        boolean successful = promptService.create(Triple.of(promptInfo, null, null));
        assertTrue(successful);
        assertNotNull(promptInfo.getPromptId());
        return promptInfo.getPromptId();
    }

    public static void deletePrompt(String userId, Long promptId) {
        if (StringUtils.isBlank(userId)) {
            return;
        }
        User user = new User().withUserId(userId);
        promptService.delete(promptId, user);
    }

    public static void deletePrompts(String userId) {
        if (StringUtils.isBlank(userId)) {
            return;
        }
        User user = new User().withUserId(userId);
        List<Long> promptIds = promptService.delete(user);
        Optional.ofNullable(promptIds)
                .orElse(Collections.emptyList())
                .stream()
                .map(promptService::getUid)
                .forEach(promptTaskService::deleteByPromptUid);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        promptService = applicationContext.getBean(PromptService.class);
        promptTaskService = applicationContext.getBean(PromptTaskService.class);
    }
}
