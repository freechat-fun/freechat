package fun.freechat.service.channel;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.model.chat.response.ChatResponse;
import fun.freechat.channels.spi.ChannelFailure;
import fun.freechat.channels.spi.ChannelText;
import fun.freechat.channels.spi.ChannelTurnContext;
import fun.freechat.service.chat.ChatRunEvent;
import fun.freechat.service.chat.ChatRunHandle;
import fun.freechat.service.chat.ChatService;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public final class ChannelChatBridge {
    private static final int MAX_RESPONSE_CHARS = 65_536;
    private final ChatService chats;

    public CompletableFuture<Void> reply(
            String authorizedChatId, ChatMessage message, ChannelTurnContext turn, int messageLimit) {
        if (messageLimit < 2 || messageLimit > MAX_RESPONSE_CHARS) {
            throw new IllegalArgumentException("Channel message limit must be between 2 and 65536");
        }
        Reply reply = new Reply(turn, messageLimit);
        CompletableFuture<Void> settlement = new CompletableFuture<>();
        try {
            turn.onCancel(() -> reply.fail(new ChannelFailure(ChannelFailure.Kind.CANCELLED)));
            if (turn.isCancelled()) {
                reply.fail(new ChannelFailure(ChannelFailure.Kind.CANCELLED));
            }
            if (reply.terminal.get()) {
                return reply.delivered;
            }
            ChatRunHandle handle = chats.startRun(authorizedChatId, message, null, reply::onRunEvent);
            if (handle == null) {
                throw new IllegalStateException("Chat run handle unavailable");
            }
            reply.handle.set(handle);
            handle.eventsDelivered().whenComplete((ignored, error) -> {
                if (error != null) reply.fail(error);
            });
            handle.settled().whenComplete((ignored, error) -> {
                if (error == null) {
                    settlement.complete(null);
                } else {
                    reply.fail(error);
                    settlement.completeExceptionally(ChannelTaskScheduler.failure(error));
                }
            });
            if (reply.cancellationRequested.get()) {
                reply.cancel();
            }
        } catch (Throwable error) {
            reply.fail(error);
            if (reply.handle.get() == null) {
                settlement.complete(null);
            }
        }
        // A settled run may still have its terminal observer queued, or delivery in flight.
        return CompletableFuture.allOf(settlement, reply.delivered);
    }

    private static final class Reply {
        final AtomicReference<ChatRunHandle> handle = new AtomicReference<>();
        final AtomicBoolean cancellationRequested = new AtomicBoolean();
        final AtomicBoolean cancellationForwarded = new AtomicBoolean();
        final ChannelTurnContext turn;
        final int messageLimit;
        final StringBuilder text = new StringBuilder();
        final CompletableFuture<Void> delivered = new CompletableFuture<>();
        final AtomicBoolean terminal = new AtomicBoolean();
        AutoCloseable typing;
        boolean typingStopped;
        boolean typingStarting;

        Reply(ChannelTurnContext turn, int messageLimit) {
            this.turn = turn;
            this.messageLimit = messageLimit;
        }

        void onRunEvent(ChatRunEvent event) {
            if (terminal.get()) {
                return;
            }
            switch (event.event()) {
                case "run.queued", "run.started" -> startTyping();
                case "run.status" -> {
                    Object phase = event.data().get("phase");
                    if ("finalizing".equals(phase) || "cancelling".equals(phase)) {
                        stopTyping();
                    } else {
                        startTyping();
                    }
                }
                case "message.delta" -> {
                    if (event.data().get("delta") instanceof String delta) {
                        append(delta);
                    }
                }
                case "run.completed" -> {
                    boolean needsBody;
                    synchronized (text) {
                        needsBody = text.toString().isBlank();
                        if (needsBody) text.setLength(0);
                    }
                    if (needsBody
                            && event.data().get("response") instanceof ChatResponse response
                            && response.aiMessage() != null) {
                        append(response.aiMessage().text());
                    }
                    finish();
                }
                case "run.failed" -> fail(new ChannelFailure(ChannelFailure.Kind.FAILED));
                case "run.cancelled" -> fail(new ChannelFailure(ChannelFailure.Kind.CANCELLED));
                default -> {
                    // Only assistant body deltas are public reply content.
                }
            }
        }

        void startTyping() {
            synchronized (text) {
                if (terminal.get() || typingStopped || typing != null || typingStarting) {
                    return;
                }
                typingStarting = true;
            }
            try {
                AutoCloseable heartbeat = turn.typing(Duration.ofSeconds(4));
                boolean keep;
                synchronized (text) {
                    keep = !terminal.get() && !typingStopped;
                    if (keep) {
                        typing = heartbeat;
                    }
                }
                if (!keep) {
                    close(heartbeat);
                }
            } catch (RuntimeException ignored) {
                // An optional indicator must not prevent text delivery.
            } finally {
                synchronized (text) {
                    typingStarting = false;
                }
            }
        }

        void append(String token) {
            boolean exceeded = false;
            synchronized (text) {
                if (terminal.get() || token == null) {
                    return;
                }
                if (token.length() > MAX_RESPONSE_CHARS - text.length()) {
                    exceeded = true;
                } else {
                    text.append(token);
                }
            }
            if (exceeded) {
                fail(new ChannelFailure(ChannelFailure.Kind.REJECTED));
            }
        }

        void finish() {
            if (!terminal.compareAndSet(false, true)) {
                return;
            }
            stopTyping();
            String answer;
            synchronized (text) {
                answer = text.toString();
            }
            CompletableFuture<Void> output = CompletableFuture.completedFuture(null);
            for (int start = 0; start < answer.length(); ) {
                int end = Math.min(answer.length(), start + messageLimit);
                if (end < answer.length() && Character.isHighSurrogate(answer.charAt(end - 1))) {
                    end--;
                }
                String part = answer.substring(start, end);
                if (!part.isBlank()) {
                    output = output.thenCompose(ignored -> turn.outbound().sendText(ChannelText.plain(part)))
                            .thenApply(receipt -> null);
                }
                start = end;
            }
            output.whenComplete((ignored, error) -> {
                if (error == null) {
                    delivered.complete(null);
                } else {
                    cancel();
                    delivered.completeExceptionally(ChannelTaskScheduler.failure(error));
                }
            });
        }

        void fail(Throwable error) {
            cancel();
            if (!terminal.compareAndSet(false, true)) {
                return;
            }
            stopTyping();
            delivered.completeExceptionally(ChannelTaskScheduler.failure(error));
        }

        void cancel() {
            cancellationRequested.set(true);
            ChatRunHandle current = handle.get();
            if (current != null && cancellationForwarded.compareAndSet(false, true)) {
                try {
                    current.cancel();
                } catch (RuntimeException ignored) {
                    // Cancellation must not skip delivery completion or service settlement.
                }
            }
        }

        void stopTyping() {
            AutoCloseable heartbeat;
            synchronized (text) {
                typingStopped = true;
                heartbeat = typing;
                typing = null;
            }
            close(heartbeat);
        }

        static void close(AutoCloseable heartbeat) {
            if (heartbeat == null) {
                return;
            }
            try {
                heartbeat.close();
            } catch (Exception ignored) {
                // Status cleanup is best-effort and cannot change a completed model response.
            }
        }
    }
}
