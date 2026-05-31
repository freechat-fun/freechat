package fun.freechat.channels.telegram.handler;

import fun.freechat.channels.telegram.TelegramChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
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
     * Matches CommonMark image syntax: {@code ![alt text](url)}. The URL is captured up to the
     * first whitespace or closing paren — sufficient for the http(s) URLs the freechat album tool
     * emits ({@code https://freechat.fun/s/...}). Alt text may be empty.
     */
    private static final Pattern IMAGE_MD = Pattern.compile("!\\[([^\\]]*)\\]\\(([^)\\s]+)\\)");

    private final TelegramChannel channel;
    private final String backendId;
    private final Long tgChatId;

    private final List<Long> messageIds = new ArrayList<>();
    private final StringBuilder fullText = new StringBuilder();
    private int currentSegmentStart = 0;
    private String lastFlushed = "";
    private long nextFlushAt = 0L;
    private boolean started = false;

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
    }

    /** Appends a streamed token and possibly flushes (plain text) per the debounce policy. */
    public void append(String token) {
        if (!started || token == null || token.isEmpty()) {
            return;
        }
        fullText.append(token);
        // Split on inline images BEFORE the debounced flush so a freshly-complete `![alt](url)` is
        // freezing the current message + firing sendPhoto + opening a new placeholder
        // synchronously — no debounce wait, the image lands at the right narrative point.
        try {
            splitOnInlineImages();
        } catch (Exception e) {
            log.warn("Inline image split failed for chat {}: {}", tgChatId, e.getMessage());
        }
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
        try {
            splitOnInlineImages();
        } catch (Exception e) {
            log.warn("Final inline image split failed for chat {}: {}", tgChatId, e.getMessage());
        }
        try {
            flush(true);
        } catch (Exception e) {
            log.warn("Telegram final flush failed for chat {}: {}", tgChatId, e.getMessage());
        }
        return fullText.toString();
    }

    /** Telegram message id of the LAST message in the streamed sequence (used for outbound logging). */
    public Long lastMessageId() {
        return messageIds.isEmpty() ? null : messageIds.get(messageIds.size() - 1);
    }

    /** All message ids in send order — useful for tests / auditing. */
    public List<Long> messageIds() {
        return List.copyOf(messageIds);
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
     * Scans the current message segment for complete {@code ![alt](url)} matches. For each one
     * found, freezes the current message with the preceding text (rendered as Markdown), fires a
     * separate {@code sendPhoto} for the image, and opens a fresh placeholder for whatever text
     * follows. Partial matches (e.g. {@code ![alt](https://} still arriving token-by-token) are
     * left in place — the next {@code append} re-scans and catches them once complete.
     */
    private void splitOnInlineImages() {
        while (true) {
            String segment = fullText.substring(currentSegmentStart);
            Matcher m = IMAGE_MD.matcher(segment);
            if (!m.find()) {
                return;
            }
            int matchStart = m.start();
            int matchEnd = m.end();
            String alt = m.group(1);
            String url = m.group(2);

            // 1. Freeze the current message with the text BEFORE the image (if any non-whitespace
            //    content). Render as Markdown — this segment won't grow further.
            String beforeImage = segment.substring(0, matchStart);
            if (!beforeImage.isBlank()) {
                String displayText = balanceMarkdown(beforeImage);
                if (!editWithRetry(lastMessageId(), displayText, ParseMode.MARKDOWN)) {
                    // Throttled or other failure — bail; next flush/append will retry the same
                    // split since currentSegmentStart hasn't advanced yet.
                    return;
                }
            }
            // else: the placeholder still reads "…". Trade-off: we don't delete leading-image
            // placeholders to keep this v1 simple. The bot's text response usually has at least a
            // word of intro before an image, so this is a rare cosmetic issue.

            // 2. Send the image as a separate Telegram message.
            if (!sendPhotoWithRetry(url, alt)) {
                // Failed to send the photo. Don't advance — let the next flush try again.
                // (Acceptable to drop the image entirely if it never succeeds; the text continues.)
                return;
            }

            // 3. Advance past the image markdown and open a fresh placeholder for text that follows.
            currentSegmentStart += matchEnd;
            Message next = sendPlaceholderWithRetry();
            if (next == null) {
                return;
            }
            messageIds.add(next.getMessageId().longValue());
            lastFlushed = PLACEHOLDER;
            nextFlushAt = System.currentTimeMillis() + FLUSH_INTERVAL_MS;
            // Loop: there may be more images in the remainder of the segment.
        }
    }

    private boolean sendPhotoWithRetry(String url, String alt) {
        try {
            channel.sendPhoto(backendId, tgChatId, new InputFile(url), alt.isBlank() ? null : alt);
            return true;
        } catch (TelegramApiException e) {
            handleTelegramFailure(e, "sendPhoto (" + url + ")");
            return false;
        }
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
        for (int i = 0; i < opens - closes; i++) {
            out.append(']');
        }
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
