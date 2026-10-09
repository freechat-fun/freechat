package fun.freechat.service.channel;

import fun.freechat.service.character.CharacterBackendEvent;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public final class ChannelBackendEvents {
    public static final String TOPIC = "freechat:channels:backend-changed";
    private final RedissonClient redisson;
    private final ChannelRuntime runtime;
    private RTopic topic;
    private int listenerId;

    @PostConstruct
    public void subscribe() {
        topic = redisson.getTopic(TOPIC);
        listenerId = topic.addListener(String.class, (name, backendId) -> runtime.backendChanged(backendId));
    }

    @EventListener
    public void onBackendChanged(CharacterBackendEvent event) {
        if (event.backendId() == null) {
            return;
        }
        runtime.backendChanged(event.backendId());
        try {
            redisson.getTopic(TOPIC).publishAsync(event.backendId()).whenComplete((ignored, error) -> {
                if (error != null) {
                    log.warn("Channel backend event publication deferred");
                }
            });
        } catch (RuntimeException ignored) {
            log.warn("Channel backend event publication deferred");
        }
    }

    @PreDestroy
    public void unsubscribe() {
        if (topic != null) {
            topic.removeListener(listenerId);
        }
    }
}
