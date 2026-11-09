package fun.freechat.service.chat;

import java.util.concurrent.CompletableFuture;

public interface ChatRunHandle {
    String runId();

    /** Request cancellation without waiting for model callbacks, cleanup or delivery. */
    void cancel();

    /** Underlying work has stopped and coordination has been released, not a delivery acknowledgement. */
    CompletableFuture<Void> settled();

    /** Event observer delivery is independent of execution settlement and channel acknowledgement. */
    CompletableFuture<Void> eventsDelivered();
}
