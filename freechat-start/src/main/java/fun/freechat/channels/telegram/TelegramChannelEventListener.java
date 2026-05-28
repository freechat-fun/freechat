package fun.freechat.channels.telegram;

import fun.freechat.service.character.CharacterBackendEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TelegramChannelEventListener {

    private final TelegramChannelManager manager;

    @EventListener
    @Async
    public void onBackendChanged(CharacterBackendEvent event) {
        String backendId = event.backendId();
        if (backendId == null) {
            return;
        }
        try {
            manager.activate(backendId);
        } catch (Exception e) {
            log.warn("Telegram (re)activation failed for backend {}", backendId, e);
        }
    }
}
