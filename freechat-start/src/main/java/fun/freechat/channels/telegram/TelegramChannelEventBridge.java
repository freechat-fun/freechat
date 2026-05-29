package fun.freechat.channels.telegram;

import fun.freechat.service.character.CharacterBackendEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Forwards local Spring {@link CharacterBackendEvent}s onto a cluster-wide Redis topic so every
 * FreeChat replica reacts to a backend change, not just the JVM that originated it. The publish
 * is asynchronous and best-effort — failure to broadcast is logged but does not block the local
 * write path.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TelegramChannelEventBridge {

    private final RedissonClient redisson;

    @EventListener
    public void onBackendChanged(CharacterBackendEvent event) {
        String backendId = event.backendId();
        if (backendId == null) {
            return;
        }
        try {
            redisson.getTopic(TelegramChannelTopics.BACKEND_CHANGED).publishAsync(backendId);
        } catch (Exception e) {
            log.warn("Failed to broadcast CharacterBackendEvent for backend {}: {}", backendId, e.getMessage());
        }
    }
}
