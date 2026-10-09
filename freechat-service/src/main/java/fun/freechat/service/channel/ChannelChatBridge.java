package fun.freechat.service.channel;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.service.TokenStream;
import fun.freechat.channels.spi.ChannelFailure;
import fun.freechat.channels.spi.ChannelText;
import fun.freechat.channels.spi.ChannelTurnContext;
import fun.freechat.service.chat.ChatService;
import fun.freechat.service.chat.ChatStreamHandle;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
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
        ChatStreamHandle handle;
        try {
            handle = chats.streamSendManaged(authorizedChatId, message, null);
        } catch (RuntimeException error) {
            return CompletableFuture.failedFuture(ChannelTaskScheduler.failure(error));
        }
        Reply reply = new Reply(handle, turn, messageLimit);
        try {
            turn.onCancel(handle::cancel);
            handle.ready().whenComplete((stream, error) -> {
                if (error != null) {
                    reply.fail(error);
                } else if (stream == null) {
                    reply.finish();
                } else {
                    reply.start(stream);
                }
            });
        } catch (Throwable error) {
            reply.fail(error);
        }
        return CompletableFuture.allOf(handle.settled(), reply.delivered);
    }

    private static final class Reply {
        final ChatStreamHandle handle;
        final ChannelTurnContext turn;
        final int messageLimit;
        final StringBuilder text = new StringBuilder();
        final CompletableFuture<Void> delivered = new CompletableFuture<>();
        final AtomicBoolean terminal = new AtomicBoolean();
        AutoCloseable typing = () -> {};

        Reply(ChatStreamHandle handle, ChannelTurnContext turn, int messageLimit) {
            this.handle = handle;
            this.turn = turn;
            this.messageLimit = messageLimit;
        }

        void start(TokenStream stream) {
            try {
                if (turn.isCancelled()) {
                    fail(new ChannelFailure(ChannelFailure.Kind.CANCELLED));
                    return;
                }
                try {
                    typing = turn.typing(Duration.ofSeconds(4));
                } catch (RuntimeException ignored) {
                    // An optional indicator must not prevent text delivery.
                }
                stream.onPartialResponse(this::append)
                        .onCompleteResponse(response -> {
                            if (response != null && response.aiMessage() != null) {
                                synchronized (text) {
                                    if (text.isEmpty()) {
                                        append(response.aiMessage().text());
                                    }
                                }
                            }
                            finish();
                        })
                        .onError(this::fail)
                        .start();
            } catch (Throwable error) {
                fail(error);
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
                output = output.thenCompose(ignored -> turn.outbound().sendText(ChannelText.plain(part)))
                        .thenApply(receipt -> null);
                start = end;
            }
            output.whenComplete((ignored, error) -> {
                if (error == null) {
                    delivered.complete(null);
                } else {
                    delivered.completeExceptionally(ChannelTaskScheduler.failure(error));
                }
            });
        }

        void fail(Throwable error) {
            if (!terminal.compareAndSet(false, true)) {
                return;
            }
            stopTyping();
            handle.cancel();
            delivered.completeExceptionally(ChannelTaskScheduler.failure(error));
        }

        void stopTyping() {
            try {
                typing.close();
            } catch (Exception ignored) {
                // Status cleanup is best-effort and cannot change a completed model response.
            }
        }
    }
}
