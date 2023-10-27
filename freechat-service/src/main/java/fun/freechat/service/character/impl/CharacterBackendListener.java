package fun.freechat.service.character.impl;

import fun.freechat.service.character.CharacterBackendEvent;
import fun.freechat.service.character.ChatContextService;
import fun.freechat.service.character.ChatSessionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

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
        String chatId = chatContextService.getIdByBackend(backendEvent.getUserId(), backendEvent.getBackendId());
        if (StringUtils.isNotBlank(chatId)) {
            chatSessionService.reset(chatId);
        }
    }
}
