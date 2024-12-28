package fun.freechat.service.chat.impl;

import fun.freechat.service.character.CharacterBackendEvent;
import fun.freechat.service.character.CharacterService;
import fun.freechat.service.chat.ChatContextService;
import fun.freechat.service.chat.ChatSessionService;
import fun.freechat.service.rag.RagTaskSucceededEvent;
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
public class CharacterSessionListener {
    @Autowired
    private ChatContextService chatContextService;
    @Autowired
    private ChatSessionService chatSessionService;
    @Autowired
    private CharacterService characterService;

    @EventListener
    @Async("eventExecutor")
    public void onCharacterBackendChanged(CharacterBackendEvent backendEvent) {
        String userId = backendEvent.userId();
        if (StringUtils.isBlank(userId)) {
            List<String> chatIds = chatContextService.listIdsByBackend(backendEvent.backendId());
            for (String chatId: chatIds) {
                if (StringUtils.isNotBlank(chatId)) {
                    chatSessionService.reset(chatId);
                }
            }
        } else {
            String chatId = chatContextService.getChatIdByBackend(backendEvent.userId(), backendEvent.backendId());
            if (StringUtils.isNotBlank(chatId)) {
                chatSessionService.reset(chatId);
            }
        }
    }

    @EventListener
    @Async("eventExecutor")
    public void onCharacterRagChanged(RagTaskSucceededEvent taskEvent) {
        String characterUid = taskEvent.task().getCharacterUid();
        if (StringUtils.isBlank(characterUid)) {
            return;
        }

        characterService.listBackendIds(characterUid)
                .stream()
                .flatMap(backendId -> chatContextService.listIdsByBackend(backendId).stream())
                .forEach(chatSessionService::reset);
    }
}
