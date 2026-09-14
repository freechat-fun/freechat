package fun.freechat.service.chat.memory;

import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.internal.Json;
import dev.langchain4j.model.TokenCountEstimator;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

public final class MemoryBounds {
    public static final int PROMPT_BYTES = 128 * 1024;
    public static final int EXTRACTION_BYTES = 64 * 1024;
    public static final int SOURCE_FRAGMENT_BYTES = 16 * 1024;
    public static final int COMPACT_TEXT_BYTES = 8 * 1024;
    public static final int VECTOR_RECORD_BYTES = 48 * 1024;
    public static final int TOOL_RESULT_BYTES = 8 * 1024;
    public static final int TOOL_TOTAL_BYTES = 32 * 1024;
    public static final int DEFAULT_PAGE_CODE_POINTS = 512;
    public static final int MAX_PAGE_CODE_POINTS = 2048;
    public static final int MAX_MANIFEST_RECORDS = 100;

    private final TokenCountEstimator estimator;
    private final LongTermMemoryProperties properties;

    public MemoryBounds(TokenCountEstimator estimator, LongTermMemoryProperties properties) {
        this.estimator = Objects.requireNonNull(estimator);
        this.properties = Objects.requireNonNull(properties);
    }

    public long tokens(String text) {
        try {
            return withMargin(estimator.estimateTokenCountInText(text));
        } catch (RuntimeException ignored) {
            throw new CapacityException("Memory text could not be measured");
        }
    }

    public static int bytes(String text) {
        return text.getBytes(StandardCharsets.UTF_8).length;
    }

    public boolean fits(String text, int tokenLimit, int byteLimit) {
        return bytes(text) <= byteLimit && tokens(text) <= tokenLimit;
    }

    public void requireText(String text, int tokenLimit, int byteLimit) {
        if (!fits(text, tokenLimit, byteLimit)) {
            throw new CapacityException("Memory text exceeds its configured budget");
        }
    }

    public void requirePrompt(List<ChatMessage> messages, List<ToolSpecification> tools, Integer providerContextLimit) {
        requireMessages(messages, tools, properties.getMaxInputTokens(), PROMPT_BYTES, providerContextLimit);
    }

    public void requireExtraction(List<ChatMessage> messages, Integer providerContextLimit) {
        requireMessages(
                messages, List.of(), properties.getExtractionMaxInputTokens(), EXTRACTION_BYTES, providerContextLimit);
    }

    public Page page(String text, int offset, Integer requestedLimit, int tokenLimit, int byteLimit) {
        int limit = requestedLimit == null ? DEFAULT_PAGE_CODE_POINTS : requestedLimit;
        if (offset < 0 || limit <= 0) {
            throw new IllegalArgumentException("Memory offsets must be nonnegative and limits positive");
        }
        return slice(
                text,
                offset,
                Math.min(limit, MAX_PAGE_CODE_POINTS),
                tokenLimit,
                Math.min(byteLimit, TOOL_RESULT_BYTES));
    }

    public Page fragment(String text, int offset, int tokenLimit) {
        if (offset < 0) {
            throw new IllegalArgumentException("Memory offsets must be nonnegative");
        }
        return slice(text, offset, Integer.MAX_VALUE, tokenLimit, SOURCE_FRAGMENT_BYTES);
    }

    private Page slice(String text, int offset, int limit, int tokenLimit, int byteLimit) {
        int total = text.codePointCount(0, text.length());
        if (offset >= total) {
            return new Page("", offset, null);
        }
        if (tokenLimit <= 0 || byteLimit <= 0) {
            throw new CapacityException("Memory output budget exhausted");
        }
        int start = text.offsetByCodePoints(0, offset);
        int end = start;
        int usedBytes = 0;
        int count = 0;
        while (end < text.length() && count < limit) {
            int codePoint = text.codePointAt(end);
            int width = codePoint <= 0x7f ? 1 : codePoint <= 0x7ff ? 2 : codePoint <= 0xffff ? 3 : 4;
            if ((long) usedBytes + width > byteLimit) {
                break;
            }
            usedBytes += width;
            end += Character.charCount(codePoint);
            count++;
        }
        String result = text.substring(start, end);
        // Token counts are not monotonic in prefix length, so validate every chosen endpoint.
        while (count > 0 && tokens(result) > tokenLimit) {
            count /= 2;
            end = text.offsetByCodePoints(start, count);
            result = text.substring(start, end);
        }
        if (count == 0) {
            throw new CapacityException("Memory output budget cannot fit the next code point");
        }
        int next = offset + count;
        return new Page(result, offset, next < total ? next : null);
    }

    private void requireMessages(
            List<ChatMessage> messages,
            List<ToolSpecification> tools,
            int tokenLimit,
            int byteLimit,
            Integer providerContextLimit) {
        try {
            String schemas = tools.isEmpty() ? "" : Json.toJson(tools);
            long byteCount = (long) bytes(ChatMessageSerializer.messagesToJson(messages)) + bytes(schemas);
            if (byteCount > byteLimit) {
                throw new CapacityException("Memory request exceeds its byte budget");
            }
            int messageTokens = estimator.estimateTokenCountInMessages(messages);
            int toolTokens = schemas.isEmpty() ? 0 : estimator.estimateTokenCountInText(schemas);
            if (messageTokens < 0 || toolTokens < 0) {
                throw new CapacityException("Memory token estimator returned an invalid count");
            }
            long tokenCount = withMargin((long) messageTokens + toolTokens);
            if (tokenCount > tokenLimit
                    || providerContextLimit != null
                            && (long) tokenLimit + properties.getResponseReserveTokens() > providerContextLimit) {
                throw new CapacityException("Memory request exceeds its token budget");
            }
        } catch (CapacityException error) {
            throw error;
        } catch (RuntimeException ignored) {
            throw new CapacityException("Memory request could not be measured");
        }
    }

    private static long withMargin(long count) {
        if (count < 0) {
            throw new CapacityException("Memory token estimator returned an invalid count");
        }
        return (count * 5 + 3) / 4;
    }

    public record Page(String text, int offset, Integer nextOffset) {}

    public static final class CapacityException extends IllegalStateException {
        public CapacityException(String message) {
            super(message);
        }
    }
}
