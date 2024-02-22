package fun.freechat.util;

import fun.freechat.service.chat.ChatContextService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class TestChatUtils  implements ApplicationContextAware {
    private static ChatContextService chatContextService;

    public static void deleteChats(String userId) {
        List<String> ids = chatContextService.listIds(userId);
        Optional.ofNullable(ids)
                .orElse(Collections.emptyList())
                .forEach(chatContextService::delete);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        chatContextService = applicationContext.getBean(ChatContextService.class);
    }
}
