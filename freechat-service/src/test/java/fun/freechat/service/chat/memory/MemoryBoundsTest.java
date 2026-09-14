package fun.freechat.service.chat.memory;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.TokenCountEstimator;
import java.util.List;
import java.util.function.ToIntFunction;
import org.junit.jupiter.api.Test;

class MemoryBoundsTest {
    private final LongTermMemoryProperties properties = new LongTermMemoryProperties();
    private final MemoryBounds bounds = new MemoryBounds(estimator(String::length), properties);

    @Test
    void appliesRoundedUpSafetyMarginWithoutIntegerOverflow() {
        assertEquals(0, bounds.tokens(""));
        assertEquals(2, bounds.tokens("a"));
        assertEquals(3, bounds.tokens("ab"));
        assertEquals(5, bounds.tokens("abcd"));
        assertEquals(2_684_354_559L, new MemoryBounds(estimator(ignored -> Integer.MAX_VALUE), properties).tokens("a"));
    }

    @Test
    void bothTokenAndUtf8ByteBudgetsMustFit() {
        MemoryBounds byteBounds = new MemoryBounds(estimator(ignored -> 1), properties);
        assertEquals(6, MemoryBounds.bytes("中文"));
        assertFalse(byteBounds.fits("中文", 10, 5));
        assertFalse(bounds.fits("abcd", 4, 100));
        assertTrue(bounds.fits("abcd", 5, 4));
        assertThrows(MemoryBounds.CapacityException.class, () -> bounds.requireText("abcd", 4, 100));
    }

    @Test
    void countsToolSchemasAndMessagesBeforeEveryRequest() {
        List<ChatMessage> messages = List.of(SystemMessage.from("system"), UserMessage.from("question"));
        List<ToolSpecification> tools = List.of(ToolSpecification.builder()
                .name("searchMemory")
                .description("Search remembered conversations")
                .build());
        int messageTokens = estimator(String::length).estimateTokenCountInMessages(messages);
        properties.setMaxInputTokens((messageTokens * 5 + 3) / 4);
        assertDoesNotThrow(() -> bounds.requirePrompt(messages, List.of(), null));
        assertThrows(MemoryBounds.CapacityException.class, () -> bounds.requirePrompt(messages, tools, null));
    }

    @Test
    void checksResponseReserveWhenProviderContextIsKnown() {
        List<ChatMessage> messages = List.of(UserMessage.from("question"));
        properties.setResponseReserveTokens(20);
        assertThrows(MemoryBounds.CapacityException.class, () -> bounds.requirePrompt(messages, List.of(), 20));
        assertDoesNotThrow(() -> bounds.requirePrompt(messages, List.of(), 10000));
    }

    @Test
    void requestBytesIncludeSerializationAndExtractionHasItsOwnBudget() {
        MemoryBounds byteBounds = new MemoryBounds(estimator(ignored -> 1), properties);
        List<ChatMessage> messages = List.of(UserMessage.from("字".repeat(30_000)));
        assertDoesNotThrow(() -> byteBounds.requirePrompt(messages, List.of(), null));
        assertThrows(MemoryBounds.CapacityException.class, () -> byteBounds.requireExtraction(messages, null));
        List<ChatMessage> oversized = List.of(UserMessage.from("字".repeat(50_000)));
        assertThrows(MemoryBounds.CapacityException.class, () -> byteBounds.requirePrompt(oversized, List.of(), null));
        properties.setExtractionMaxInputTokens(1);
        assertThrows(
                MemoryBounds.CapacityException.class,
                () -> bounds.requireExtraction(List.of(UserMessage.from("a")), null));
    }

    @Test
    void defaultsAndClampsPageLengthsInCodePoints() {
        String text = "x".repeat(5000);
        MemoryBounds.Page first = bounds.page(text, 0, null, 10000, 10000);
        assertEquals(512, first.text().length());
        assertEquals(512, first.nextOffset());
        MemoryBounds.Page clamped = bounds.page(text, 0, Integer.MAX_VALUE, 10000, 10000);
        assertEquals(2048, clamped.text().length());
        assertEquals(2048, clamped.nextOffset());
    }

    @Test
    void neverSplitsSurrogatePairsAtCodePointOrByteBoundaries() {
        String supplementary = new String(Character.toChars(0x20000));
        String text = "a" + supplementary + "中文z";
        MemoryBounds.Page first = bounds.page(text, 0, 3, 100, 5);
        assertEquals("a" + supplementary, first.text());
        assertEquals(2, first.nextOffset());
        MemoryBounds.Page second = bounds.page(text, first.nextOffset(), 3, 100, 3);
        assertEquals("中", second.text());
        assertEquals(3, second.nextOffset());
        MemoryBounds.Page last = bounds.page(text, second.nextOffset(), 3, 100, 4);
        assertEquals("文z", last.text());
        assertNull(last.nextOffset());
        assertEquals(text, first.text() + second.text() + last.text());
    }

    @Test
    void tokenLimitedPagesReportActualEndpointAndMakeProgress() {
        String text = "abcdefghijklmnop";
        MemoryBounds.Page page = bounds.page(text, 2, 10, 5, 100);
        assertEquals(page.text().length() + 2, page.nextOffset());
        assertTrue(bounds.tokens(page.text()) <= 5);
        assertFalse(page.text().isEmpty());
    }

    @Test
    void nonmonotonicTokenizerCannotProduceAnOverBudgetPage() {
        MemoryBounds nonmonotonic = new MemoryBounds(estimator(text -> text.length() == 4 ? 1 : 100), properties);
        MemoryBounds.Page page = nonmonotonic.page("abcdefgh", 0, 8, 2, 100);
        assertEquals("abcd", page.text());
        assertEquals(4, page.nextOffset());
    }

    @Test
    void rejectsInvalidOffsetsAndExhaustedBudgetsWithoutLooping() {
        assertThrows(IllegalArgumentException.class, () -> bounds.page("abc", -1, 1, 100, 100));
        assertThrows(IllegalArgumentException.class, () -> bounds.page("abc", 0, 0, 100, 100));
        assertThrows(IllegalArgumentException.class, () -> bounds.page("abc", 0, -1, 100, 100));
        assertThrows(MemoryBounds.CapacityException.class, () -> bounds.page("abc", 0, 1, 0, 100));
        assertThrows(MemoryBounds.CapacityException.class, () -> bounds.page("abc", 0, 1, 100, 0));
        assertThrows(MemoryBounds.CapacityException.class, () -> bounds.page("中", 0, 1, 100, 2));
        assertThrows(MemoryBounds.CapacityException.class, () -> bounds.page("a", 0, 1, 1, 100));
        assertEquals(new MemoryBounds.Page("", 3, null), bounds.page("abc", 3, 1, 0, 0));
        assertEquals(
                new MemoryBounds.Page("", Integer.MAX_VALUE, null), bounds.page("abc", Integer.MAX_VALUE, 1, 0, 0));
    }

    @Test
    void fragmentsAreBoundedAndReconstructLargeCompletedSource() {
        String text = ("文" + new String(Character.toChars(0x20000))).repeat(20_000);
        MemoryBounds fragmentBounds = new MemoryBounds(estimator(ignored -> 1), properties);
        StringBuilder rebuilt = new StringBuilder();
        Integer offset = 0;
        int fragments = 0;
        while (offset != null) {
            MemoryBounds.Page page = fragmentBounds.fragment(text, offset, 100);
            assertTrue(MemoryBounds.bytes(page.text()) <= MemoryBounds.SOURCE_FRAGMENT_BYTES);
            assertFalse(page.text().isEmpty());
            rebuilt.append(page.text());
            offset = page.nextOffset();
            fragments++;
        }
        assertTrue(fragments > 1);
        assertEquals(text, rebuilt.toString());
    }

    @Test
    void sanitizesEstimatorExceptionsAndRejectsInvalidCounts() {
        MemoryBounds broken = new MemoryBounds(
                estimator(ignored -> {
                    throw new IllegalStateException("private transcript");
                }),
                properties);
        MemoryBounds.CapacityException failure =
                assertThrows(MemoryBounds.CapacityException.class, () -> broken.tokens("private transcript"));
        assertFalse(failure.toString().contains("private transcript"));
        assertNull(failure.getCause());
        assertThrows(
                MemoryBounds.CapacityException.class,
                () -> broken.requirePrompt(List.of(UserMessage.from("private transcript")), List.of(), null));
        MemoryBounds negative = new MemoryBounds(estimator(ignored -> -1), properties);
        assertThrows(MemoryBounds.CapacityException.class, () -> negative.tokens("a"));
    }

    static TokenCountEstimator estimator(ToIntFunction<String> counter) {
        return new TokenCountEstimator() {
            @Override
            public int estimateTokenCountInText(String text) {
                return counter.applyAsInt(text);
            }

            @Override
            public int estimateTokenCountInMessage(ChatMessage message) {
                return counter.applyAsInt(ChatMessageSerializer.messageToJson(message));
            }

            @Override
            public int estimateTokenCountInMessages(Iterable<ChatMessage> messages) {
                int sum = 0;
                for (ChatMessage message : messages) {
                    sum += estimateTokenCountInMessage(message);
                }
                return sum;
            }
        };
    }
}
