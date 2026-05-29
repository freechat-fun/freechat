package fun.freechat.channels.telegram;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

/**
 * Cluster-aware listener: subscribes to the Redis topic that {@link TelegramChannelEventBridge}
 * publishes to whenever a {@code CharacterBackendEvent} fires anywhere in the cluster. Every
 * replica receives every backend-changed message and reconciles its own outbound state +
 * contests for polling leadership via {@link TelegramChannelManager#activate(String)}.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TelegramChannelEventListener {

    private final TelegramChannelManager manager;
    private final RedissonClient redisson;

    @PostConstruct
    void subscribe() {
        RTopic topic = redisson.getTopic(TelegramChannelTopics.BACKEND_CHANGED);
        topic.addListener(String.class, (channel, backendId) -> {
            if (backendId == null) {
                return;
            }
            try {
                manager.activate(backendId);
            } catch (Exception e) {
                log.warn("Telegram (re)activation failed for backend {}", backendId, e);
            }
        });
        log.info("Subscribed to telegram backend-change topic '{}'", TelegramChannelTopics.BACKEND_CHANGED);
    }
}
