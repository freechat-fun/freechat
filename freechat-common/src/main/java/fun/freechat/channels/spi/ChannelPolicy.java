package fun.freechat.channels.spi;

import java.time.Duration;
import java.util.Objects;

public record ChannelPolicy(
        int receiveConcurrency,
        int sendConcurrency,
        int receiveCapacity,
        int sendCapacity,
        int conversationReceiveCapacity,
        int conversationSendCapacity,
        int maxInstances,
        Duration turnTimeout,
        Duration deliveryTimeout,
        int deliveryAttempts,
        Duration shutdownTimeout) {
    public ChannelPolicy {
        if (receiveConcurrency < 1
                || sendConcurrency < 1
                || receiveCapacity < receiveConcurrency
                || sendCapacity < sendConcurrency
                || conversationReceiveCapacity < 1
                || conversationReceiveCapacity > receiveCapacity
                || conversationSendCapacity < 1
                || conversationSendCapacity > sendCapacity
                || maxInstances < 1
                || deliveryAttempts < 1) {
            throw new IllegalArgumentException("Invalid channel capacity policy");
        }
        for (Duration duration : new Duration[] {turnTimeout, deliveryTimeout, shutdownTimeout}) {
            if (Objects.requireNonNull(duration).isNegative() || duration.isZero()) {
                throw new IllegalArgumentException("Channel deadlines must be positive");
            }
        }
    }

    public static ChannelPolicy defaults() {
        return new ChannelPolicy(
                128,
                32,
                4096,
                4096,
                32,
                64,
                1024,
                Duration.ofMinutes(10),
                Duration.ofSeconds(90),
                3,
                Duration.ofSeconds(10));
    }
}
