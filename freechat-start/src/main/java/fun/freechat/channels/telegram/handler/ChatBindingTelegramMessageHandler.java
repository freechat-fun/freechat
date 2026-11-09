package fun.freechat.channels.telegram.handler;

import dev.langchain4j.data.message.UserMessage;
import fun.freechat.channels.spi.ChannelFailure;
import fun.freechat.channels.spi.ChannelTurnContext;
import fun.freechat.service.channel.ChannelTaskScheduler;
import fun.freechat.service.chat.ChatRunHandle;
import fun.freechat.service.chat.ChatService;
import fun.freechat.service.chat.TgChatBindingService;
import fun.freechat.service.chat.TgMessageService;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatBindingTelegramMessageHandler implements TelegramMessageHandler {

    private final TgChatBindingService tgChatBindingService;
    private final TgMessageService tgMessageService;
    private final ChatService chatService;

    @Override
    public CompletionStage<Void> handle(String backendId, Update update, ChannelTurnContext turn) {
        if (!update.hasMessage() || turn.isCancelled()) {
            return CompletableFuture.completedFuture(null);
        }
        Message message = update.getMessage();
        Chat chat = message.getChat();
        if (chat == null) {
            return CompletableFuture.completedFuture(null);
        }
        User from = message.getFrom();
        Long tgChatId = chat.getId();
        Long tgUserId = from == null ? null : from.getId();
        String chatType = chat.getType();
        String title = chat.getTitle();

        String chatId = tgChatBindingService.getOrCreate(
                backendId,
                tgChatId,
                chatType,
                title,
                tgUserId,
                from == null ? null : from.getUserName(),
                from == null ? null : from.getFirstName(),
                from == null ? null : from.getLastName());
        if (chatId == null) {
            log.warn("Failed to bind chat for backend={} tgChatId={}", backendId, tgChatId);
            return CompletableFuture.completedFuture(null);
        }

        String text = message.hasText() ? message.getText() : null;
        String kind = detectKind(message);
        tgMessageService.record(chatId, message.getMessageId().longValue(), tgUserId, "in", kind, text, null);

        if (!message.hasText()) {
            return CompletableFuture.completedFuture(null);
        }

        TelegramStreamingReplyEmitter emitter;
        try {
            emitter = new TelegramStreamingReplyEmitter(turn);
        } catch (Exception failure) {
            return CompletableFuture.failedFuture(ChannelTaskScheduler.failure(failure));
        }
        CompletableFuture<Void> settlement = new CompletableFuture<>();
        // Register exactly once, not once per model terminal callback. Persistence never runs on
        // a token callback, even when a fake/cached outbound future completes synchronously.
        CompletableFuture<Void> recorded = emitter.completion()
                .handleAsync(
                        (reply, failure) -> {
                            recordConfirmed(chatId, emitter.confirmedReply());
                            if (failure != null) {
                                log.warn("Telegram reply delivery failed for chat {}", chatId);
                                throw ChannelTaskScheduler.failure(failure);
                            }
                            return (Void) null;
                        },
                        action -> Thread.startVirtualThread(action));

        AtomicReference<ChatRunHandle> run = new AtomicReference<>();
        AtomicBoolean cancellationRequested = new AtomicBoolean();
        AtomicBoolean cancellationForwarded = new AtomicBoolean();
        Runnable requestCancel = () -> {
            cancellationRequested.set(true);
            ChatRunHandle handle = run.get();
            if (handle != null && cancellationForwarded.compareAndSet(false, true)) {
                cancel(handle, chatId);
            }
        };
        emitter.completion().whenComplete((reply, failure) -> {
            if (failure != null) {
                requestCancel.run();
            }
        });
        try {
            // startRun may invoke the observer or cancellation before returning its handle.
            turn.onCancel(requestCancel);
            emitter.start();
            if (turn.isCancelled() || emitter.completion().isDone()) {
                emitter.fail(new ChannelFailure(ChannelFailure.Kind.CANCELLED));
                settlement.complete(null);
            } else {
                ChatRunHandle handle = chatService.startRun(chatId, UserMessage.from(text), null, emitter::onRunEvent);
                if (handle == null) {
                    throw new IllegalStateException("Chat run handle unavailable");
                }
                run.set(handle);
                handle.eventsDelivered().whenComplete((unused, failure) -> {
                    if (failure != null) emitter.fail(failure);
                });
                handle.settled().whenComplete((unused, failure) -> {
                    if (failure == null) {
                        settlement.complete(null);
                    } else {
                        emitter.fail(failure);
                        settlement.completeExceptionally(ChannelTaskScheduler.failure(failure));
                    }
                });
                if (cancellationRequested.get() || turn.isCancelled()) {
                    requestCancel.run();
                }
            }
        } catch (Exception failure) {
            log.warn("Failed to construct chat run for chat {}", chatId);
            requestCancel.run();
            emitter.fail(failure, "Sorry, I couldn't start this reply. Please try again.");
            if (run.get() == null) {
                settlement.complete(null);
            }
        }
        // allOf waits for both, including exceptional completion: delivery cannot release the
        // receive lane while ChatService still owns its memory/coordination task (or vice versa).
        return CompletableFuture.allOf(settlement, recorded);
    }

    private static void cancel(ChatRunHandle handle, String chatId) {
        try {
            handle.cancel();
        } catch (Exception ignored) {
            log.warn("Telegram chat stream cancellation failed for chat {}", chatId);
        }
    }

    private void recordConfirmed(String chatId, TelegramStreamingReplyEmitter.Reply reply) {
        boolean failed = false;
        if (reply.lastMessageId() != null) {
            try {
                tgMessageService.record(
                        chatId, Long.valueOf(reply.lastMessageId()), null, "out", "text", reply.text(), null);
            } catch (Exception ignored) {
                failed = true;
            }
        }
        for (var image : reply.images()) {
            try {
                tgMessageService.record(
                        chatId, Long.valueOf(image.messageId()), null, "out", "photo", image.url(), null);
            } catch (Exception ignored) {
                failed = true;
            }
        }
        if (failed) {
            log.warn("Telegram outbound recording failed for chat {}", chatId);
            throw new ChannelFailure(ChannelFailure.Kind.FAILED);
        }
    }

    private static String detectKind(Message m) {
        if (m.hasText()) {
            return "text";
        }
        if (m.hasPhoto()) {
            return "photo";
        }
        if (m.hasVoice()) {
            return "voice";
        }
        if (m.hasVideo()) {
            return "video";
        }
        if (m.hasAudio()) {
            return "audio";
        }
        if (m.hasDocument()) {
            return "document";
        }
        if (m.isCommand()) {
            return "command";
        }
        return "unsupported";
    }
}
