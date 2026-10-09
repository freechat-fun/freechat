package fun.freechat.channels.spi;

import java.time.Duration;
import java.util.Objects;

public final class ChannelFailure extends RuntimeException {
    public enum Kind {
        REJECTED,
        UNSUPPORTED,
        CANCELLED,
        FAILED,
        RETRYABLE,
        RATE_LIMITED,
        AMBIGUOUS
    }

    private final Kind kind;
    private final Duration retryAfter;

    public ChannelFailure(Kind kind) {
        this(kind, Duration.ZERO);
    }

    public ChannelFailure(Kind kind, Duration retryAfter) {
        super("Channel operation " + Objects.requireNonNull(kind).name().toLowerCase());
        this.kind = kind;
        this.retryAfter = Objects.requireNonNull(retryAfter);
        if (retryAfter.isNegative()) {
            throw new IllegalArgumentException("Negative channel retry delay");
        }
    }

    public Kind kind() {
        return kind;
    }

    public Duration retryAfter() {
        return retryAfter;
    }

    public boolean retryable() {
        return kind == Kind.RETRYABLE || kind == Kind.RATE_LIMITED;
    }
}
