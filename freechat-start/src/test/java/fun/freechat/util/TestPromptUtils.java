package fun.freechat.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import fun.freechat.model.PromptInfo;
import fun.freechat.model.PromptTask;
import fun.freechat.model.User;
import fun.freechat.service.enums.PromptFormat;
import fun.freechat.service.enums.PromptType;
import fun.freechat.service.enums.Visibility;
import fun.freechat.service.prompt.ChatPromptContent;
import fun.freechat.service.prompt.PromptService;
import fun.freechat.service.prompt.PromptTaskService;
import fun.freechat.service.util.InfoUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import static fun.freechat.util.TestChatUtils.modelParams;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Component
@Slf4j
public class TestPromptUtils implements ApplicationContextAware {
    private static PromptService promptService;
    private static PromptTaskService promptTaskService;

    public static Long createPrompt(String userId, String template, String draft) {
        PromptInfo promptInfo = new PromptInfo()
                .withVersion(0)
                .withVisibility(Visibility.PUBLIC.text())
                .withUserId(userId)
                .withName("Integration-test Prompt for " + userId)
                .withDescription("Prompt for integration-test.")
                .withTemplate(template)
                .withType(PromptType.STRING.text())
                .withDraft(draft);
        boolean successful = promptService.create(Triple.of(promptInfo, null, null));
        assertTrue(successful);
        assertNotNull(promptInfo.getPromptId());
        return promptInfo.getPromptId();
    }
    
    public static Long createChatPrompt(String userId, ChatPromptContent promptContent) throws JsonProcessingException {
        String template = InfoUtils.defaultMapper().writeValueAsString(promptContent);
        PromptInfo promptInfo = new PromptInfo()
                .withVersion(0)
                .withVisibility(Visibility.PUBLIC.text())
                .withUserId(userId)
                .withName("Integration-test Prompt for " + userId)
                .withDescription("Prompt for integration-test.")
                .withTemplate(template)
                .withType(PromptType.CHAT.text())
                .withFormat(PromptFormat.MUSTACHE.text());
        assertTrue(promptService.create(Triple.of(promptInfo, null, null)));
        return promptInfo.getPromptId();
    }

    public static String createChatPromptTask(Long promptId, String modelId, String apiKey) throws JsonProcessingException {
        String params = InfoUtils.defaultMapper().writeValueAsString(modelParams(modelId));
        PromptTask task = new PromptTask()
                .withApiKeyValue(apiKey)
                .withModelId(modelId)
                .withParams(params)
                .withPromptUid(promptService.getUid(promptId));
        assertTrue(promptTaskService.create(task));
        assertNotNull(task.getTaskId());
        return task.getTaskId();
    }

    public static void deletePrompt(String userId, Long promptId) {
        if (StringUtils.isBlank(userId)) {
            return;
        }
        promptTaskService.deleteByPromptUid(promptService.getUid(promptId));
        User user = new User().withUserId(userId);
        promptService.delete(promptId, user);
    }

    public static void deletePrompts(String userId) {
        if (StringUtils.isBlank(userId)) {
            return;
        }
        User user = new User().withUserId(userId);
        promptTaskService.deleteByUser(user);
        promptService.deleteByUser(user);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        promptService = applicationContext.getBean(PromptService.class);
        promptTaskService = applicationContext.getBean(PromptTaskService.class);
    }
}
