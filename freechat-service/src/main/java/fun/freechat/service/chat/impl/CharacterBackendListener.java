package fun.freechat.service.chat.impl;

import fun.freechat.service.character.CharacterBackendEvent;
import fun.freechat.service.chat.ChatContextService;
import fun.freechat.service.chat.ChatSessionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@SuppressWarnings("unused")
public class CharacterBackendListener {
    @Autowired
    private ChatContextService chatContextService;
    @Autowired
    private ChatSessionService chatSessionService;

    @EventListener
    @Async("eventExecutor")
    public void onCharacterBackendChanged(CharacterBackendEvent backendEvent) {
        String userId = backendEvent.getUserId();
        if (StringUtils.isBlank(userId)) {
            List<String> chatIds = chatContextService.listIdsByBackend(backendEvent.getBackendId());
            for (String chatId: chatIds) {
                if (StringUtils.isNotBlank(chatId)) {
                    chatSessionService.reset(chatId);
                }
            }
        } else {
            String chatId = chatContextService.getIdByBackend(backendEvent.getUserId(), backendEvent.getBackendId());
            if (StringUtils.isNotBlank(chatId)) {
                chatSessionService.reset(chatId);
            }
        }
    }
}
