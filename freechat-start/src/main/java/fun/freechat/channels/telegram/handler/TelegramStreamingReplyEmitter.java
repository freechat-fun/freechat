package fun.freechat.channels.telegram.handler;

import fun.freechat.channels.spi.ChannelFailure;
import fun.freechat.channels.spi.ChannelMedia;
import fun.freechat.channels.spi.ChannelReceipt;
import fun.freechat.channels.spi.ChannelText;
import fun.freechat.channels.spi.ChannelTurnContext;
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
        PLACEHOLDER,
        REPLACEMENT,
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
    private static final String PLACEHOLDER = "…";
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
    private boolean initialRequested;
    private boolean inFlight;
    private boolean partialReady;
    private boolean timerArmed;
    private long timerVersion;
    private ScheduledFuture<?> timer;
    private AutoCloseable typing;
    private int receivedLength;
    private int segmentStart;
    private int pendingSplitAt = -1;
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
            initialRequested = true;
        }
        try {
            AutoCloseable heartbeat = turn.typing(Duration.ofSeconds(4));
            boolean keep;
            synchronized (monitor) {
                keep = state == State.STREAMING;
                if (keep) {
                    typing = heartbeat;
                }
            }
            if (!keep) {
                close(heartbeat);
            }
        } catch (Exception ignored) {
            // Typing is optional and best effort, never a reason to lose a reply.
        }
        drive();
        return started;
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
            operation = failure == null ? nextOperation() : null;
            if (operation != null) {
                inFlight = true;
            } else if (state == State.FINALIZING) {
                state = State.TERMINAL;
                finish = true;
            } else if (!timerArmed
                    && textFailure == null
                    && visibleEnd() > segmentStart
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

    // Called only under monitor. Offsets change in accepted(), never when a send is merely queued.
    private Operation nextOperation() {
        if (textFailure != null) {
            return state == State.FINALIZING && imageIndex < images.size()
                    ? new Operation(Kind.IMAGE, null, null, -1, images.get(imageIndex))
                    : null;
        }
        if (messageId == null && initialRequested) {
            return new Operation(Kind.PLACEHOLDER, null, ChannelText.plain(PLACEHOLDER), -1, null);
        }
        if (pendingSplitAt >= 0) {
            return new Operation(Kind.REPLACEMENT, null, ChannelText.plain(PLACEHOLDER), pendingSplitAt, null);
        }
        boolean isFinal = state == State.FINALIZING;
        if (!isFinal && !partialReady) {
            return null;
        }
        int end = isFinal ? fullText.length() : visibleEnd();
        int remaining = end - segmentStart;
        if (remaining > MAX_MESSAGE_LENGTH) {
            int splitAt = segmentStart + MAX_MESSAGE_LENGTH;
            int space = fullText.lastIndexOf(" ", splitAt);
            if (space > segmentStart + MAX_MESSAGE_LENGTH * 3 / 4) {
                splitAt = space;
            }
            if (Character.isHighSurrogate(fullText.charAt(splitAt - 1))) {
                splitAt--;
            }
            return freeze(splitAt);
        }
        String segment = fullText.substring(segmentStart, end);
        if (isFinal && !finalTextConfirmed) {
            if (segment.isEmpty()) {
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
        if (!segment.isEmpty() && !segment.equals(lastFlushed)) {
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
                        case PLACEHOLDER, REPLACEMENT -> turn.outbound().sendText(operation.text());
                        case FREEZE, PARTIAL, FINAL ->
                            turn.outbound().editText(operation.messageId(), operation.text());
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
                    } else if (operation.kind() != Kind.PLACEHOLDER && failure == null && !turn.isCancelled()) {
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
                            } else if (operation.kind() == Kind.PARTIAL
                                    || (operation.kind() == Kind.FREEZE && state == State.STREAMING)) {
                                partialReady = false;
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
                    if (operation.kind() == Kind.PLACEHOLDER) {
                        started.complete(null);
                    }
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
        switch (operation.kind()) {
            case PLACEHOLDER, REPLACEMENT -> {
                if (receipt == null) {
                    failure = new ChannelFailure(ChannelFailure.Kind.AMBIGUOUS);
                    state = State.FINALIZING;
                    return;
                }
                messageId = receipt.messageId();
                lastFlushed = PLACEHOLDER;
                if (operation.kind() == Kind.REPLACEMENT) {
                    segmentStart = operation.splitAt();
                    pendingSplitAt = -1;
                }
            }
            case FREEZE -> {
                lastFlushed = operation.text().text();
                pendingSplitAt = operation.splitAt();
                confirmedTextEnd = operation.splitAt();
                confirmedTextMessageId = operation.messageId();
            }
            case PARTIAL -> {
                lastFlushed = operation.text().text();
                partialReady = false;
                confirmedTextEnd = segmentStart + operation.text().text().length();
                confirmedTextMessageId = operation.messageId();
            }
            case FINAL -> {
                finalTextConfirmed = true;
                confirmedTextEnd = fullText.length();
                confirmedTextMessageId = operation.messageId();
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
        AutoCloseable oldTyping;
        synchronized (monitor) {
            timerArmed = false;
            timerVersion++;
            oldTimer = timer;
            timer = null;
            oldTyping = typing;
            typing = null;
        }
        if (oldTimer != null) {
            oldTimer.cancel(false);
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
