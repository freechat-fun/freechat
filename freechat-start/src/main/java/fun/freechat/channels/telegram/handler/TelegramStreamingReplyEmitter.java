package fun.freechat.channels.telegram.handler;

import dev.langchain4j.model.chat.response.ChatResponse;
import fun.freechat.channels.spi.ChannelFailure;
import fun.freechat.channels.spi.ChannelMedia;
import fun.freechat.channels.spi.ChannelReceipt;
import fun.freechat.channels.spi.ChannelText;
import fun.freechat.channels.spi.ChannelTurnContext;
import fun.freechat.service.chat.ChatRunEvent;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ScheduledFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Telegram presentation only: the runtime owns all delivery, retries and timers. */
public final class TelegramStreamingReplyEmitter {
    private enum State {
        NEW,
        STREAMING,
        FINALIZING,
        TERMINAL
    }

    private enum Kind {
        ERROR,
        FREEZE,
        PARTIAL,
        FINAL,
        IMAGE
    }

    private record Operation(Kind kind, String messageId, ChannelText text, int splitAt, String url) {}

    public record ImageReceipt(String messageId, String url) {}

    public record Reply(String text, String lastMessageId, List<ImageReceipt> images) {
        public Reply {
            images = List.copyOf(images);
        }
    }

    static final int MAX_MESSAGE_LENGTH = 4000;
    static final int MAX_BUFFER_LENGTH = 128_000;
    static final int MAX_IMAGES = 32;
    static final long FLUSH_INTERVAL_MS = 500L;
    private static final String SAFE_ERROR = "Sorry, I couldn't complete this reply. Please try again.";
    private static final Pattern IMAGE_MD = Pattern.compile("!\\[([^\\]]*)\\]\\(([^)\\s]+)\\)");
    private static final Pattern INCOMPLETE_IMAGE = Pattern.compile("!\\[[^\\]]*(?:\\](?:\\([^)]*)?)?$");

    private final Object monitor = new Object();
    private final ChannelTurnContext turn;
    private final CompletableFuture<Void> started = new CompletableFuture<>();
    private final CompletableFuture<Reply> completion = new CompletableFuture<>();
    private final StringBuilder fullText = new StringBuilder();
    private final List<String> images = new ArrayList<>();
    private final List<ImageReceipt> sentImages = new ArrayList<>();
    private State state = State.NEW;
    private ChannelFailure failure;
    private ChannelFailure textFailure;
    private ChannelFailure imageFailure;
    private boolean runTerminal;
    private boolean typingStopped;
    private boolean typingStarting;
    private String errorNotice;
    private boolean errorNoticeRequested;
    private boolean inFlight;
    private boolean partialReady;
    private boolean timerArmed;
    private long timerVersion;
    private ScheduledFuture<?> timer;
    private AutoCloseable typing;
    private int receivedLength;
    private int segmentStart;
    private int imageIndex;
    private String messageId;
    private String lastFlushed = "";
    private int confirmedTextEnd;
    private String confirmedTextMessageId;
    private boolean finalTextConfirmed;

    public TelegramStreamingReplyEmitter(ChannelTurnContext turn) {
        this.turn = turn;
        turn.onCancel(() -> fail(new ChannelFailure(ChannelFailure.Kind.CANCELLED)));
    }

    public CompletableFuture<Void> start() {
        synchronized (monitor) {
            if (state != State.NEW) {
                return started;
            }
            state = State.STREAMING;
        }
        // Readiness is local; no Telegram message exists until actual visible content arrives.
        started.complete(null);
        return started;
    }

    /** Project the service's ordered run events, never infer model status from UI text. */
    public void onRunEvent(ChatRunEvent event) {
        synchronized (monitor) {
            if (runTerminal || state != State.STREAMING) {
                return;
            }
            runTerminal = switch (event.event()) {
                case "run.completed", "run.failed", "run.cancelled" -> true;
                default -> false;
            };
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
                synchronized (monitor) {
                    needsBody = images.isEmpty() && fullText.toString().isBlank();
                    if (needsBody) {
                        fullText.setLength(0);
                        receivedLength = 0;
                    }
                }
                if (needsBody
                        && event.data().get("response") instanceof ChatResponse response
                        && response.aiMessage() != null) {
                    append(response.aiMessage().text());
                }
                complete();
            }
            case "run.failed" ->
                fail(
                        new ChannelFailure(ChannelFailure.Kind.FAILED),
                        event.data().get("message") instanceof String message ? message : SAFE_ERROR);
            case "run.cancelled" -> fail(new ChannelFailure(ChannelFailure.Kind.CANCELLED));
            default -> {
                // Reasoning, tool arguments and metadata are not Telegram reply content.
            }
        }
    }

    private void startTyping() {
        synchronized (monitor) {
            if (state != State.STREAMING || typingStopped || typing != null || typingStarting) {
                return;
            }
            typingStarting = true;
        }
        try {
            AutoCloseable heartbeat = turn.typing(Duration.ofSeconds(4));
            boolean keep;
            synchronized (monitor) {
                keep = state == State.STREAMING && !typingStopped;
                if (keep) {
                    typing = heartbeat;
                }
            }
            if (!keep) {
                close(heartbeat);
            }
        } catch (Exception ignored) {
            // Typing is optional and best effort, never a reason to lose a reply.
        } finally {
            synchronized (monitor) {
                typingStarting = false;
            }
        }
    }

    /** Token callbacks only mutate a bounded buffer and enqueue coalesced work. */
    public void append(String token) {
        if (token == null || token.isEmpty()) {
            return;
        }
        boolean overflow;
        synchronized (monitor) {
            if (state != State.STREAMING) {
                return;
            }
            overflow = token.length() > MAX_BUFFER_LENGTH - receivedLength;
            if (!overflow) {
                receivedLength += token.length();
                fullText.append(token);
                overflow = !stripInlineImages();
            }
        }
        if (overflow) {
            fail(new ChannelFailure(ChannelFailure.Kind.REJECTED));
        } else {
            drive();
        }
    }

    /** Idempotent: freezes the plan, including image order, and always returns the same future. */
    public CompletableFuture<Reply> complete() {
        synchronized (monitor) {
            if (state == State.TERMINAL || state == State.FINALIZING) {
                return completion;
            }
            state = State.FINALIZING;
        }
        stopTimers();
        drive();
        return completion;
    }

    /** Observe delivery without requesting finalization. */
    public CompletableFuture<Reply> completion() {
        return completion;
    }

    /** Abort setup/delivery without exposing a provider exception or its cause. */
    public CompletableFuture<Reply> fail(Throwable cause) {
        return fail(cause, null);
    }

    /** A service-approved safe message is a separate notice, not successful assistant content. */
    public CompletableFuture<Reply> fail(Throwable cause, String safeMessage) {
        synchronized (monitor) {
            if (state == State.TERMINAL) {
                return completion;
            }
            if (failure == null) {
                while (cause instanceof CompletionException && cause.getCause() != null) {
                    cause = cause.getCause();
                }
                failure = cause instanceof ChannelFailure safe
                        ? new ChannelFailure(safe.kind(), safe.retryAfter())
                        : new ChannelFailure(ChannelFailure.Kind.FAILED);
                if (safeMessage != null && failure.kind() != ChannelFailure.Kind.CANCELLED) {
                    String notice = safeMessage.isBlank() ? SAFE_ERROR : safeMessage;
                    int end = Math.min(notice.length(), MAX_MESSAGE_LENGTH);
                    if (end < notice.length() && Character.isHighSurrogate(notice.charAt(end - 1))) {
                        end--;
                    }
                    errorNotice = notice.substring(0, end);
                }
            }
            state = State.FINALIZING;
        }
        stopTimers();
        drive();
        return completion;
    }

    /** Confirmed deliveries remain recordable even if a later photo or edit fails. */
    public Reply confirmedReply() {
        synchronized (monitor) {
            return snapshot();
        }
    }

    private Reply snapshot() {
        return new Reply(fullText.substring(0, confirmedTextEnd), confirmedTextMessageId, sentImages);
    }

    // Reserve a single operation under the monitor; invoke all external code outside it.
    private void drive() {
        Operation operation;
        long scheduleVersion = -1;
        boolean finish = false;
        synchronized (monitor) {
            if (state == State.NEW || state == State.TERMINAL || inFlight) {
                return;
            }
            if (failure == null) {
                operation = nextOperation();
            } else if (errorNotice != null && !errorNoticeRequested && !turn.isCancelled()) {
                errorNoticeRequested = true;
                operation = new Operation(Kind.ERROR, null, ChannelText.plain(errorNotice), -1, null);
            } else {
                operation = null;
            }
            if (operation != null) {
                inFlight = true;
            } else if (state == State.FINALIZING) {
                state = State.TERMINAL;
                finish = true;
            } else if (!timerArmed
                    && textFailure == null
                    && visibleEnd() > segmentStart
                    && !fullText.substring(segmentStart, visibleEnd()).isBlank()
                    && !fullText.substring(segmentStart, visibleEnd()).equals(lastFlushed)) {
                timerArmed = true;
                scheduleVersion = ++timerVersion;
            }
        }
        if (finish) {
            stopTimers();
            ChannelFailure terminalFailure;
            Reply reply;
            synchronized (monitor) {
                terminalFailure = failure != null ? failure : textFailure != null ? textFailure : imageFailure;
                reply = snapshot();
            }
            if (terminalFailure == null) {
                started.complete(null);
                completion.complete(reply);
            } else {
                started.completeExceptionally(terminalFailure);
                completion.completeExceptionally(terminalFailure);
            }
        } else if (operation != null) {
            submit(operation);
        } else if (scheduleVersion >= 0) {
            schedulePartial(scheduleVersion);
        }
    }

    // Called only under monitor. Only skip blank segments; sent offsets advance in accepted().
    private Operation nextOperation() {
        if (textFailure != null) {
            return state == State.FINALIZING && imageIndex < images.size()
                    ? new Operation(Kind.IMAGE, null, null, -1, images.get(imageIndex))
                    : null;
        }
        boolean isFinal = state == State.FINALIZING;
        if (!isFinal && !partialReady) {
            return null;
        }
        int end = isFinal ? fullText.length() : visibleEnd();
        int remaining = end - segmentStart;
        while (remaining > MAX_MESSAGE_LENGTH
                && !fullText.substring(segmentStart, end).isBlank()) {
            int splitAt = segmentStart + MAX_MESSAGE_LENGTH;
            int space = fullText.lastIndexOf(" ", splitAt);
            if (space > segmentStart + MAX_MESSAGE_LENGTH * 3 / 4) {
                splitAt = space;
            }
            if (Character.isHighSurrogate(fullText.charAt(splitAt - 1))) {
                splitAt--;
            }
            if (messageId == null && fullText.substring(segmentStart, splitAt).isBlank()) {
                fullText.delete(segmentStart, splitAt);
                end -= splitAt - segmentStart;
                remaining = end - segmentStart;
            } else {
                return freeze(splitAt);
            }
        }
        String segment = fullText.substring(segmentStart, end);
        if (isFinal && !finalTextConfirmed) {
            if (segment.isBlank()) {
                finalTextConfirmed = true;
            } else {
                String markdown = balanceMarkdown(segment);
                if (markdown.length() > MAX_MESSAGE_LENGTH) {
                    return freeze(segmentStart + Math.max(1, remaining / 2));
                }
                // Formatting itself is part of the final plan, even if the visible text is unchanged.
                return new Operation(
                        Kind.FINAL, messageId, new ChannelText(markdown, ChannelText.Format.MARKDOWN), -1, null);
            }
        }
        if (isFinal) {
            return imageIndex < images.size()
                    ? new Operation(Kind.IMAGE, null, null, -1, images.get(imageIndex))
                    : null;
        }
        if (!segment.isBlank() && !segment.equals(lastFlushed)) {
            return new Operation(Kind.PARTIAL, messageId, ChannelText.plain(segment), -1, null);
        }
        partialReady = false;
        return null;
    }

    private Operation freeze(int splitAt) {
        if (splitAt > segmentStart + 1 && Character.isHighSurrogate(fullText.charAt(splitAt - 1))) {
            splitAt--;
        }
        return new Operation(
                Kind.FREEZE, messageId, ChannelText.plain(fullText.substring(segmentStart, splitAt)), splitAt, null);
    }

    private void submit(Operation operation) {
        try {
            CompletableFuture<?> delivery =
                    switch (operation.kind()) {
                        case ERROR -> turn.outbound().sendText(operation.text());
                        case FREEZE, PARTIAL, FINAL ->
                            operation.messageId() == null
                                    ? turn.outbound().sendText(operation.text())
                                    : turn.outbound().editText(operation.messageId(), operation.text());
                        case IMAGE ->
                            turn.outbound()
                                    .sendMedia(ChannelMedia.reference(ChannelMedia.Kind.IMAGE, operation.url(), null));
                    };
            delivery.whenComplete((receipt, error) -> {
                ChannelFailure currentFailure;
                boolean continueReply = false;
                synchronized (monitor) {
                    if (error == null) {
                        accepted(operation, (receipt instanceof ChannelReceipt r) ? r : null);
                    } else if (failure == null && !turn.isCancelled()) {
                        Throwable unwrapped = error;
                        while (unwrapped instanceof CompletionException && unwrapped.getCause() != null) {
                            unwrapped = unwrapped.getCause();
                        }
                        ChannelFailure safe = unwrapped instanceof ChannelFailure value
                                ? new ChannelFailure(value.kind(), value.retryAfter())
                                : new ChannelFailure(ChannelFailure.Kind.FAILED);
                        if (safe.kind() != ChannelFailure.Kind.CANCELLED) {
                            if (operation.kind() == Kind.IMAGE) {
                                imageFailure = imageFailure == null ? safe : imageFailure;
                                imageIndex++;
                            } else if (operation.messageId() != null
                                    && (operation.kind() == Kind.PARTIAL
                                            || (operation.kind() == Kind.FREEZE && state == State.STREAMING))) {
                                partialReady = false;
                            } else if (operation.messageId() == null && state == State.STREAMING) {
                                // An unconfirmed send cannot safely be replayed as a later edit/send.
                                failure = safe;
                                state = State.FINALIZING;
                            } else {
                                textFailure = textFailure == null ? safe : textFailure;
                            }
                            continueReply = true;
                        }
                    }
                    inFlight = false;
                    currentFailure = failure;
                }
                if (continueReply) {
                    drive();
                } else if (error != null || currentFailure != null) {
                    fail(error != null ? error : currentFailure);
                } else {
                    drive();
                }
            });
        } catch (Exception error) {
            synchronized (monitor) {
                inFlight = false;
            }
            fail(error);
        }
    }

    private void accepted(Operation operation, ChannelReceipt receipt) {
        if (operation.kind() != Kind.IMAGE && operation.kind() != Kind.ERROR && operation.messageId() == null) {
            if (receipt == null || receipt.messageId() == null) {
                failure = new ChannelFailure(ChannelFailure.Kind.AMBIGUOUS);
                state = State.FINALIZING;
                return;
            }
            messageId = receipt.messageId();
        }
        switch (operation.kind()) {
            case ERROR -> {
                // Failure notices never replace or become confirmed assistant content.
            }
            case FREEZE -> {
                confirmedTextEnd = operation.splitAt();
                confirmedTextMessageId = messageId;
                segmentStart = operation.splitAt();
                messageId = null;
                lastFlushed = "";
            }
            case PARTIAL -> {
                lastFlushed = operation.text().text();
                partialReady = false;
                confirmedTextEnd = segmentStart + operation.text().text().length();
                confirmedTextMessageId = messageId;
            }
            case FINAL -> {
                finalTextConfirmed = true;
                confirmedTextEnd = fullText.length();
                confirmedTextMessageId = messageId;
            }
            case IMAGE -> {
                if (receipt == null) {
                    failure = new ChannelFailure(ChannelFailure.Kind.AMBIGUOUS);
                    state = State.FINALIZING;
                    return;
                }
                sentImages.add(new ImageReceipt(receipt.messageId(), operation.url()));
                imageIndex++;
            }
        }
    }

    private void schedulePartial(long version) {
        try {
            ScheduledFuture<?> scheduled = turn.schedule(
                    () -> {
                        synchronized (monitor) {
                            if (state != State.STREAMING || !timerArmed || version != timerVersion) {
                                return;
                            }
                            timerArmed = false;
                            timer = null;
                            partialReady = true;
                        }
                        drive();
                    },
                    Duration.ofMillis(FLUSH_INTERVAL_MS));
            boolean keep;
            synchronized (monitor) {
                keep = state == State.STREAMING && timerArmed && version == timerVersion;
                if (keep) {
                    timer = scheduled;
                }
            }
            if (!keep) {
                scheduled.cancel(false);
            }
        } catch (Exception error) {
            fail(error);
        }
    }

    private void stopTimers() {
        ScheduledFuture<?> oldTimer;
        synchronized (monitor) {
            timerArmed = false;
            timerVersion++;
            oldTimer = timer;
            timer = null;
        }
        if (oldTimer != null) {
            oldTimer.cancel(false);
        }
        stopTyping();
    }

    private void stopTyping() {
        AutoCloseable oldTyping;
        synchronized (monitor) {
            typingStopped = true;
            oldTyping = typing;
            typing = null;
        }
        close(oldTyping);
    }

    private static void close(AutoCloseable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (Exception ignored) {
                // Status cleanup is best effort; never expose provider diagnostics.
            }
        }
    }

    private boolean stripInlineImages() {
        Matcher matcher = IMAGE_MD.matcher(fullText);
        int scanFrom = segmentStart;
        while (matcher.find(scanFrom)) {
            if (images.size() == MAX_IMAGES) {
                return false;
            }
            images.add(matcher.group(2));
            scanFrom = matcher.start();
            fullText.delete(matcher.start(), matcher.end());
            matcher.reset(fullText);
        }
        return true;
    }

    // Do not freeze a possible image prefix; it may become a photo when the next token arrives.
    private int visibleEnd() {
        Matcher incomplete = INCOMPLETE_IMAGE.matcher(fullText);
        if (incomplete.find(segmentStart)) {
            return incomplete.start();
        }
        int length = fullText.length();
        return length > segmentStart && fullText.charAt(length - 1) == '!' ? length - 1 : length;
    }

    static String balanceMarkdown(String text) {
        StringBuilder out = new StringBuilder(text);
        for (char delimiter : new char[] {'*', '_', '`'}) {
            if (count(text, delimiter) % 2 != 0) {
                out.append(delimiter);
            }
        }
        out.repeat("]", Math.max(0, count(text, '[') - count(text, ']')));
        return out.toString();
    }

    private static int count(String text, char character) {
        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == character) {
                count++;
            }
        }
        return count;
    }
}
