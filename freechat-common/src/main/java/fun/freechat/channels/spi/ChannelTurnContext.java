package fun.freechat.channels.spi;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ScheduledFuture;

public interface ChannelTurnContext {
    ChannelAddress address();

    ChannelOutbound outbound();

    Instant deadline();

    boolean isCancelled();

    void onCancel(Runnable action);

    ScheduledFuture<?> schedule(Runnable action, Duration delay);

    AutoCloseable typing(Duration interval);
}
