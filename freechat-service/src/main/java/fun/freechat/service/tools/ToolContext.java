package fun.freechat.service.tools;

import java.time.Duration;
import java.util.Objects;
import java.util.UUID;

public final class ToolContext {
    private final String chatId;
    private final String userId;
    private final String characterUid;
    private final String backendId;
    private final String invocationId = UUID.randomUUID().toString();
    private final long deadline;
    private int calls;
    private int resultBytes;

    public ToolContext(String chatId, String userId, String characterUid, String backendId, Duration timeout) {
        this.chatId = Objects.requireNonNull(chatId);
        this.userId = Objects.requireNonNull(userId);
        this.characterUid = Objects.requireNonNull(characterUid);
        this.backendId = Objects.requireNonNull(backendId);
        if (timeout.isNegative() || timeout.isZero() || timeout.compareTo(Duration.ofMinutes(10)) > 0) {
            throw new IllegalArgumentException("Invalid tool deadline");
        }
        deadline = System.nanoTime() + timeout.toNanos();
    }

    public String chatId() {
        return chatId;
    }

    public String userId() {
        return userId;
    }

    public String characterUid() {
        return characterUid;
    }

    public String backendId() {
        return backendId;
    }

    public String invocationId() {
        return invocationId;
    }

    public void check() {
        if (Thread.currentThread().isInterrupted() || deadline - System.nanoTime() <= 0) {
            throw new IllegalStateException("Tool invocation expired");
        }
    }

    public Duration remaining() {
        check();
        return Duration.ofNanos(Math.max(1, deadline - System.nanoTime()));
    }

    public synchronized void admit() {
        check();
        if (++calls > 64) {
            throw new IllegalStateException("Tool call budget exceeded");
        }
    }

    public synchronized void recordResult(String result) {
        if (result == null || result.length() > 65_536) {
            throw new IllegalStateException("Tool result budget exceeded");
        }
        int bytes = result.getBytes(java.nio.charset.StandardCharsets.UTF_8).length;
        if (bytes > 65_536 || bytes > 262_144 - resultBytes) {
            throw new IllegalStateException("Tool result budget exceeded");
        }
        resultBytes += bytes;
    }
}
