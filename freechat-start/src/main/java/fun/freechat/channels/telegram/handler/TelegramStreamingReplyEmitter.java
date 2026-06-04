package fun.freechat.channels.telegram.handler;

import fun.freechat.channels.telegram.TelegramChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.ResponseParameters;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

/**
 * Streams an LLM reply into Telegram by editing a placeholder message in place.
 *
 * <p>Behaviour:
 *
 * <ul>
 *   <li>{@link #start()} — sends an initial placeholder message and records its id.
 *   <li>{@link #append(String)} — accumulates a streamed token. Flushes (calls {@code editText})
 *       at most once per {@link #FLUSH_INTERVAL_MS}, in plain text.
 *   <li>{@link #complete()} — final flush of the accumulated text, with Markdown parse mode applied
 *       to the most recent (still-active) message. Earlier messages frozen by length splitting
 *       remain plain text.
 *   <li>If accumulated text for the current message exceeds {@link #MAX_MESSAGE_LENGTH}, the
 *       current message is frozen with the partial content and a fresh placeholder is sent;
 *       further appends edit the new message.
 * </ul>
 *
 * <p>Single-threaded: instances are not safe for concurrent use; rely on the langchain4j
 * {@code TokenStream} contract that callbacks are delivered sequentially per stream.
 */
@Slf4j
public final class TelegramStreamingReplyEmitter {

    private static final int MAX_MESSAGE_LENGTH = 4000;
    static final long FLUSH_INTERVAL_MS = 500L;
    private static final String PLACEHOLDER = "…";

    /**
     * How often to re-send the {@code TYPING} chat action. Telegram clears the indicator after
     * ~5 seconds, so we refresh slightly faster to keep it continuously visible.
     */
    private static final long TYPING_REFRESH_MS = 4_000L;

    /**
     * Matches CommonMark image syntax {@code ![alt](url)}. The URL is captured up to the first
     * whitespace or closing paren — sufficient for the http(s) URLs the freechat album tool
     * emits ({@code https://freechat.fun/s/...}). Alt text may be empty.
     */
    private static final Pattern IMAGE_MD = Pattern.compile("!\\[([^\\]]*)\\]\\(([^)\\s]+)\\)");

    /**
     * Shared daemon-threaded scheduler for {@code TYPING} keepalive ticks across all emitter
     * instances. Each tick just submits a {@code sendChatAction} HTTP request (the actual I/O
     * happens on the OkHttp pool inside the telegrambots client), so a tiny pool is enough.
     */
    private static final ScheduledExecutorService TYPING_SCHEDULER = Executors.newScheduledThreadPool(2, r -> {
        Thread t = new Thread(r, "tg-typing-heartbeat");
        t.setDaemon(true);
        return t;
    });

    private final TelegramChannel channel;
    private final String backendId;
    private final Long tgChatId;

    private final List<Long> messageIds = new ArrayList<>();
    private final StringBuilder fullText = new StringBuilder();
    private final List<String> pendingImageUrls = new ArrayList<>();
    private final List<Pair<Long, String>> sentImages = new ArrayList<>();
    private int currentSegmentStart = 0;
    private String lastFlushed = "";
    private long nextFlushAt = 0L;
    private boolean started = false;
    private ScheduledFuture<?> typingHeartbeat;

    public TelegramStreamingReplyEmitter(TelegramChannel channel, String backendId, Long tgChatId) {
        this.channel = channel;
        this.backendId = backendId;
        this.tgChatId = tgChatId;
    }

    /** Sends the initial placeholder. Must be called once before {@link #append(String)}. */
    public void start() throws TelegramApiException {
        if (started) {
            return;
        }
        Message placeholder = channel.sendText(backendId, tgChatId, PLACEHOLDER);
        messageIds.add(placeholder.getMessageId().longValue());
        lastFlushed = PLACEHOLDER;
        nextFlushAt = System.currentTimeMillis() + FLUSH_INTERVAL_MS;
        started = true;
        startTypingHeartbeat();
    }

    /** Appends a streamed token and possibly flushes (plain text) per the debounce policy. */
    public void append(String token) {
        if (!started || token == null || token.isEmpty()) {
            return;
        }
        fullText.append(token);
        stripInlineImages();
        if (System.currentTimeMillis() >= nextFlushAt) {
            try {
                flush(false);
            } catch (Exception e) {
                // Best effort: log and keep going. The next append will retry.
                log.warn("Telegram streaming flush failed for chat {}: {}", tgChatId, e.getMessage());
            }
        }
    }

    /** Final flush. The most recent message is rendered with {@link ParseMode#MARKDOWN}. */
    public String complete() {
        if (!started) {
            return fullText.toString();
        }
        stopTypingHeartbeat();
        stripInlineImages();
        try {
            flush(true);
        } catch (Exception e) {
            log.warn("Telegram final flush failed for chat {}: {}", tgChatId, e.getMessage());
        }
        sendPendingImages();
        return fullText.toString();
    }

    /** Telegram message id of the LAST message in the streamed sequence (used for outbound logging). */
    public Long lastMessageId() {
        return messageIds.isEmpty() ? null : messageIds.getLast();
    }

    /** All message ids in send order — useful for tests / auditing. */
    public List<Long> messageIds() {
        return List.copyOf(messageIds);
    }

    /**
     * {@code (telegramMessageId, url)} pairs for each photo successfully delivered by
     * {@link #sendPendingImages()}. Empty until {@link #complete()} runs. Caller-facing,
     * used by {@code ChatBindingTelegramMessageHandler} to persist one outbound row per photo.
     */
    public List<Pair<Long, String>> sentImages() {
        return List.copyOf(sentImages);
    }

    private void flush(boolean isFinal) {
        // First handle length splitting: if the in-progress segment exceeded the limit,
        // freeze the current message with as much as fits and start a new placeholder.
        while (fullText.length() - currentSegmentStart > MAX_MESSAGE_LENGTH) {
            int splitAt = currentSegmentStart + MAX_MESSAGE_LENGTH;
            int lastSpace = fullText.lastIndexOf(" ", splitAt);
            if (lastSpace > currentSegmentStart + (MAX_MESSAGE_LENGTH * 3 / 4)) {
                splitAt = lastSpace;
            }
            String frozen = fullText.substring(currentSegmentStart, splitAt);
            if (!editWithRetry(lastMessageId(), frozen, null)) {
                // Rate-limited or other failure mid-split — bail; next flush will retry.
                return;
            }
            currentSegmentStart = splitAt;

            Message next = sendPlaceholderWithRetry();
            if (next == null) {
                return;
            }
            messageIds.add(next.getMessageId().longValue());
            lastFlushed = PLACEHOLDER;
        }

        String currentSegment = fullText.substring(currentSegmentStart);
        if (currentSegment.isEmpty()) {
            // nothing to render yet (e.g. final flush right after a length split)
            nextFlushAt = System.currentTimeMillis() + FLUSH_INTERVAL_MS;
            return;
        }

        String parseMode = null;
        String displayText = currentSegment;
        if (isFinal) {
            // Final flush: render as Markdown, balancing any unclosed delimiters mid-stream cuts may have left open.
            displayText = balanceMarkdown(currentSegment);
            parseMode = ParseMode.MARKDOWN;
        }
        if (!displayText.equals(lastFlushed)) {
            if (editWithRetry(lastMessageId(), displayText, parseMode)) {
                lastFlushed = displayText;
            } else {
                // Edit failed/throttled — leave nextFlushAt as set by the helper and exit.
                return;
            }
        }
        nextFlushAt = System.currentTimeMillis() + FLUSH_INTERVAL_MS;
    }

    /**
     * Scans the unsent portion of {@link #fullText} for complete {@code ![alt](url)} matches.
     * For each match: stashes the URL into {@link #pendingImageUrls} and removes the whole
     * markdown (alt included) from the displayed text. The alt is dropped because the album tool
     * emits a literal {@code img} placeholder that would otherwise leak into the chat as inline
     * filler and as a redundant photo caption. Partial matches still being streamed (e.g.
     * {@code ![alt](https://}) are left untouched and re-scanned on the next append.
     */
    private void stripInlineImages() {
        String segment = fullText.substring(currentSegmentStart);
        Matcher m = IMAGE_MD.matcher(segment);
        if (!m.find()) {
            return;
        }
        StringBuilder rewritten = new StringBuilder(segment.length());
        int last = 0;
        do {
            rewritten.append(segment, last, m.start());
            pendingImageUrls.add(m.group(2));
            last = m.end();
        } while (m.find());
        rewritten.append(segment, last, segment.length());
        fullText.setLength(currentSegmentStart);
        fullText.append(rewritten);
    }

    /**
     * Fires {@code sendChatAction(TYPING)} immediately, then re-fires every
     * {@link #TYPING_REFRESH_MS}ms until {@link #stopTypingHeartbeat()} cancels it. Telegram
     * clears the indicator after ~5s, so re-firing at 4s keeps it continuously visible.
     */
    private void startTypingHeartbeat() {
        sendTypingTick();
        typingHeartbeat = TYPING_SCHEDULER.scheduleAtFixedRate(
                this::sendTypingTick, TYPING_REFRESH_MS, TYPING_REFRESH_MS, TimeUnit.MILLISECONDS);
    }

    private void stopTypingHeartbeat() {
        if (typingHeartbeat != null) {
            typingHeartbeat.cancel(false);
            typingHeartbeat = null;
        }
    }

    private void sendTypingTick() {
        try {
            channel.sendChatAction(backendId, tgChatId, ActionType.TYPING);
        } catch (Exception e) {
            // Best-effort indicator. Log at debug to avoid spamming on transient failures or
            // bot-blocked scenarios — the actual response still streams via editText.
            log.debug("Telegram sendChatAction(TYPING) failed for chat {}: {}", tgChatId, e.getMessage());
        }
    }

    /**
     * Fires one {@code sendPhoto} per stashed image. Failures are logged but never thrown.
     * Successful sends are recorded into {@link #sentImages} so callers can persist them.
     */
    private void sendPendingImages() {
        for (String url : pendingImageUrls) {
            try {
                Message sent = channel.sendPhoto(backendId, tgChatId, new InputFile(url), null);
                if (sent != null && sent.getMessageId() != null) {
                    sentImages.add(Pair.of(sent.getMessageId().longValue(), url));
                }
            } catch (TelegramApiException e) {
                log.warn("Telegram sendPhoto failed for chat {} (url={}): {}", tgChatId, url, e.getMessage());
            }
        }
        pendingImageUrls.clear();
    }

    private boolean editWithRetry(Long messageId, String text, String parseMode) {
        try {
            channel.editText(backendId, tgChatId, messageId, text, parseMode);
            return true;
        } catch (TelegramApiException e) {
            handleTelegramFailure(e, "editText");
            return false;
        }
    }

    private Message sendPlaceholderWithRetry() {
        try {
            return channel.sendText(backendId, tgChatId, PLACEHOLDER);
        } catch (TelegramApiException e) {
            handleTelegramFailure(e, "sendText (placeholder for length-split)");
            return null;
        }
    }

    private void handleTelegramFailure(TelegramApiException e, String op) {
        long deferMs = retryAfterMillis(e);
        if (deferMs > 0) {
            // Defer the next flush attempt by at least retry_after; never pull it earlier.
            nextFlushAt = Math.max(nextFlushAt, System.currentTimeMillis() + deferMs);
            log.info("Telegram throttled {} for chat {} — deferring next flush by {}ms", op, tgChatId, deferMs);
        } else {
            log.warn("Telegram {} failed for chat {}: {}", op, tgChatId, e.getMessage());
        }
    }

    private static long retryAfterMillis(TelegramApiException e) {
        if (e instanceof TelegramApiRequestException req) {
            ResponseParameters params = req.getParameters();
            if (params != null && params.getRetryAfter() != null && params.getRetryAfter() > 0) {
                return params.getRetryAfter() * 1000L;
            }
        }
        return 0L;
    }

    /**
     * Best-effort balancer for Telegram legacy Markdown delimiters: closes any unclosed
     * single-char emphasis ({@code *}, {@code _}, {@code `}) and unmatched {@code [...]} brackets
     * by appending the missing closer. Doesn't handle escaped delimiters (rare in LLM output) or
     * triple-backtick code fences (those are uncommon mid-cut and Telegram tolerates them).
     */
    static String balanceMarkdown(String s) {
        int stars = count(s, '*');
        int unders = count(s, '_');
        int ticks = count(s, '`');
        int opens = count(s, '[');
        int closes = count(s, ']');
        if ((stars % 2 == 0) && (unders % 2 == 0) && (ticks % 2 == 0) && opens == closes) {
            return s;
        }
        StringBuilder out = new StringBuilder(s.length() + 8).append(s);
        if (stars % 2 != 0) {
            out.append('*');
        }
        if (unders % 2 != 0) {
            out.append('_');
        }
        if (ticks % 2 != 0) {
            out.append('`');
        }
        out.repeat("]", Math.max(0, opens - closes));
        return out.toString();
    }

    private static int count(CharSequence s, char c) {
        int n = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == c) {
                n++;
            }
        }
        return n;
    }
}
