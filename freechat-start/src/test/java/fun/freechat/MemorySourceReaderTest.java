package fun.freechat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.TokenCountEstimator;
import fun.freechat.model.ChatHistory;
import fun.freechat.service.chat.memory.LongTermMemoryProperties;
import fun.freechat.service.chat.memory.MemoryBounds;
import fun.freechat.service.chat.memory.MemoryScope;
import fun.freechat.service.chat.memory.MemorySourceReader;
import fun.freechat.service.chat.memory.MemorySourceReader.Evidence;
import fun.freechat.service.chat.memory.MemorySourceReader.Position;
import fun.freechat.service.chat.memory.MemorySourceReader.Turn;
import fun.freechat.service.chat.memory.MemoryTurnRepository;
import fun.freechat.service.chat.memory.MemoryTurnRepository.Origin;
import fun.freechat.service.enums.EmbeddingStoreType;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockMakers;

/** Complete SQL turns and deterministic token accounting; no models, database or Spring context. */
@Timeout(20)
class MemorySourceReaderTest {
    private static final MemoryScope SCOPE =
            new MemoryScope("chat", "owner", "character", 7, EmbeddingStoreType.EN_LONG_TERM_MEMORY);
    private static final String TURN_ID = "00000000-0000-0000-0000-000000000001";
    private static final LocalDateTime NOW = LocalDateTime.of(2026, 9, 12, 10, 0);
    private final MemoryTurnRepository repository =
            mock(MemoryTurnRepository.class, withSettings().mockMaker(MockMakers.SUBCLASS));
    private final MemorySourceReader reader = new MemorySourceReader(repository);
    private final List<ChatHistory> rows = new ArrayList<>();
    private final MemoryBounds bounds = new MemoryBounds(
            new TokenCountEstimator() {
                @Override
                public int estimateTokenCountInText(String text) {
                    return text.codePointCount(0, text.length());
                }

                @Override
                public int estimateTokenCountInMessage(ChatMessage message) {
                    throw new AssertionError("Source reading must not estimate model requests");
                }

                @Override
                public int estimateTokenCountInMessages(Iterable<ChatMessage> messages) {
                    throw new AssertionError("Source reading must not estimate model requests");
                }
            },
            new LongTermMemoryProperties());

    @BeforeEach
    void pages() {
        when(repository.sourcePage(eq(SCOPE), anyLong(), anyLong(), anyInt()))
                .thenAnswer(call -> page(call.getArgument(1), call.getArgument(2), call.getArgument(3), false));
        when(repository.finalizedPage(eq(SCOPE), anyLong(), anyLong(), anyInt()))
                .thenAnswer(call -> page(call.getArgument(1), call.getArgument(2), call.getArgument(3), true));
    }

    @Test
    void selectsFirstTerminalWithinCursorAndNeverReadsAnActiveTurn() {
        rows.add(marker(1, "turn-start"));
        rows.add(message(2, Origin.USER_INPUT, UserMessage.from("first")));
        rows.add(marker(3, "turn-complete"));
        rows.add(marker(4, "turn-start").withTurnId(UUID.randomUUID().toString()));
        rows.add(marker(5, "turn-abort").withTurnId(rows.getLast().getTurnId()));
        rows.add(marker(6, "turn-start"));
        rows.add(message(7, Origin.USER_INPUT, UserMessage.from("unfinished")));

        assertTrue(reader.nextTurn(SCOPE, 0, 2).isEmpty());
        Turn first = reader.nextTurn(SCOPE, 0, 7).orElseThrow();
        assertEquals(new Turn(0, 3, TURN_ID, 2, NOW.toInstant(ZoneOffset.UTC), false), first);
        assertEquals(
                List.of(2L),
                collect(first, 100).stream().map(Evidence::sourceId).toList());
        Turn aborted = reader.nextTurn(SCOPE, 3, 7).orElseThrow();
        assertEquals(5, aborted.throughId());
        assertTrue(aborted.aborted());
        assertTrue(reader.nextTurn(SCOPE, 5, 7).isEmpty());
        verify(repository).finalizedPage(SCOPE, 0, 7, 1);
        verify(repository, atLeastOnce()).sourcePage(SCOPE, 0, 3, 1);
    }

    @Test
    void abortedTurnsAdvanceDirectlyWithoutParsingAnyMessage() {
        rows.add(marker(1, "turn-start"));
        rows.add(marker(2, "message").withMessage("private malformed aborted payload"));
        rows.add(marker(3, "turn-abort"));
        Turn turn = reader.nextTurn(SCOPE, 0, 3).orElseThrow();
        clearInvocations(repository);
        var result = reader.nextFragment(SCOPE, turn, new Position(0, 0), bounds, 0);
        assertTrue(result.complete());
        assertNull(result.evidence());
        assertEquals(new Position(3, 0), result.next());
        verifyNoInteractions(repository);
    }

    @Test
    void preservesOriginalInputProvenanceAndExcludesTemplatesSystemReasoningAndToolRequests() {
        rows.add(marker(1, "turn-start"));
        rows.add(message(2, Origin.USER_INPUT, UserMessage.from("expanded prompt, not user evidence"))
                .withSourceMessage(json(UserMessage.from(TextContent.from("original"), TextContent.from("second"))))
                .withSystemMessageRef("a".repeat(64) + "/" + "b".repeat(43) + "-" + "c".repeat(32)));
        rows.add(marker(3, "message")
                .withMessageOrigin(Origin.TEMPLATE_EXAMPLE.text())
                .withMessage("invalid template JSON must not be parsed"));
        rows.add(marker(4, "message")
                .withMessageOrigin(Origin.SYSTEM.text())
                .withSourceMessage("invalid system JSON must not be parsed"));
        rows.add(message(
                5,
                Origin.ASSISTANT_OUTPUT,
                AiMessage.builder()
                        .text("visible answer")
                        .thinking("private reasoning")
                        .build()));
        rows.add(message(
                6,
                Origin.ASSISTANT_OUTPUT,
                AiMessage.builder().thinking("reasoning only").build()));
        rows.add(message(
                7,
                Origin.TOOL,
                AiMessage.from(ToolExecutionRequest.builder()
                        .id("call-1")
                        .name("lookup")
                        .arguments("{\"not_user_evidence\":true}")
                        .build())));
        rows.add(message(8, Origin.TOOL, ToolExecutionResultMessage.from("call-1", "lookup", "tool result")));
        rows.add(message(9, Origin.USER_INPUT, UserMessage.from("   \n")));
        rows.add(marker(10, "turn-complete"));

        List<Evidence> evidence = collect(reader.nextTurn(SCOPE, 0, 10).orElseThrow(), 100);
        assertEquals(
                List.of(2L, 5L, 8L), evidence.stream().map(Evidence::sourceId).toList());
        assertTrue("original\nsecond".equals(evidence.getFirst().text()));
        assertTrue(evidence.getFirst().originalInput());
        assertEquals(Origin.USER_INPUT.text(), evidence.getFirst().origin());
        assertTrue("visible answer".equals(evidence.get(1).text()));
        assertEquals(Origin.ASSISTANT_OUTPUT.text(), evidence.get(1).origin());
        assertFalse(evidence.get(1).originalInput());
        assertEquals(Origin.TOOL.text(), evidence.get(2).origin());
        assertFalse(evidence.get(2).originalInput());
        assertTrue("tool result".equals(evidence.get(2).text()));
        assertTrue(
                evidence.stream().allMatch(item -> NOW.toInstant(ZoneOffset.UTC).equals(item.observedAt())));
    }

    @Test
    void paginatesMoreThanOneThousandRowsWithoutDuplicatesOrTruncation() {
        rows.add(marker(1, "turn-start"));
        for (long id = 2; id <= 1106; id++) {
            rows.add(message(id, Origin.USER_INPUT, UserMessage.from("row " + id)));
        }
        rows.add(marker(1107, "turn-complete"));
        rows.add(message(1108, Origin.USER_INPUT, UserMessage.from("outside boundary")));
        List<Evidence> evidence = collect(reader.nextTurn(SCOPE, 0, 1107).orElseThrow(), 100);
        assertEquals(1105, evidence.size());
        for (int index = 0; index < evidence.size(); index++) {
            assertEquals(index + 2L, evidence.get(index).sourceId());
            assertEquals(0, evidence.get(index).offset());
        }
        verify(repository).sourcePage(SCOPE, 1106, 1107, 1);
        verify(repository, never()).sourcePage(SCOPE, 1107, 1107, 1);
    }

    @ParameterizedTest
    @ValueSource(ints = {10, 100000})
    void unicodeFragmentsUseCodePointOffsetsAndBothByteAndTokenBounds(int tokenLimit) {
        String original = "A\uD83E\uDDEA中e\u0301".repeat(tokenLimit == 10 ? 10 : 4000);
        rows.add(marker(1, "turn-start"));
        rows.add(message(2, Origin.USER_INPUT, UserMessage.from("expanded text"))
                .withSourceMessage(json(UserMessage.from(original))));
        rows.add(marker(3, "turn-complete"));
        Turn turn = reader.nextTurn(SCOPE, 0, 3).orElseThrow();
        List<Evidence> fragments = collect(turn, tokenLimit);
        assertTrue(fragments.size() > 1);
        StringBuilder joined = new StringBuilder();
        int offset = 0;
        for (Evidence evidence : fragments) {
            assertEquals(2, evidence.sourceId());
            assertTrue(evidence.originalInput());
            assertEquals(offset, evidence.offset());
            assertTrue(MemoryBounds.bytes(evidence.text()) <= MemoryBounds.SOURCE_FRAGMENT_BYTES);
            assertTrue(bounds.tokens(evidence.text()) <= tokenLimit);
            assertFalse(Character.isLowSurrogate(evidence.text().charAt(0)));
            assertFalse(Character.isHighSurrogate(
                    evidence.text().charAt(evidence.text().length() - 1)));
            joined.append(evidence.text());
            offset += evidence.text().codePointCount(0, evidence.text().length());
        }
        assertTrue(original.contentEquals(joined), "Original Unicode input must round-trip without loss");
        assertEquals(original.codePointCount(0, original.length()), offset);
    }

    @ParameterizedTest
    @ValueSource(strings = {"not-json-private-input", "null", "[]", "{\"type\":\"UNKNOWN\",\"text\":\"private\"}"})
    void malformedMessagesFailWithSanitizedErrors(String malformed) {
        completeWith(
                marker(2, "message").withMessageOrigin(Origin.USER_INPUT.text()).withMessage(malformed));
        invalid(() -> collect(reader.nextTurn(SCOPE, 0, 3).orElseThrow(), 100));
    }

    @Test
    void malformedOriginalNeverFallsBackToExpandedInput() {
        completeWith(message(2, Origin.USER_INPUT, UserMessage.from("expanded input"))
                .withSourceMessage("private malformed original"));
        invalid(() -> collect(reader.nextTurn(SCOPE, 0, 3).orElseThrow(), 100));
    }

    @ParameterizedTest
    @ValueSource(strings = {"user-input", "tool", "unknown-origin"})
    void aiOutputCannotBeRelabeledAsRealUserOrToolResult(String origin) {
        completeWith(message(2, Origin.ASSISTANT_OUTPUT, AiMessage.from("assistant assertion"))
                .withMessageOrigin(origin));
        invalid(() -> collect(reader.nextTurn(SCOPE, 0, 3).orElseThrow(), 100));
    }

    @ParameterizedTest
    @ValueSource(strings = {"turn-id", "episode", "record-kind"})
    void rejectsRowsOutsideTheSelectedCompleteTurn(String mutation) {
        ChatHistory row = message(2, Origin.USER_INPUT, UserMessage.from("source"));
        switch (mutation) {
            case "turn-id" -> row.setTurnId(UUID.randomUUID().toString());
            case "episode" -> row.setEpisode(3L);
            case "record-kind" -> row.setRecordKind("invalid-marker");
            default -> fail();
        }
        completeWith(row);
        invalid(() -> collect(reader.nextTurn(SCOPE, 0, 3).orElseThrow(), 100));
    }

    @Test
    void rejectsMissingStartWrongStartTurnAndNonCanonicalTerminalId() {
        completeWith(message(2, Origin.USER_INPUT, UserMessage.from("source")));
        rows.getFirst().setRecordKind("message");
        invalid(() -> reader.nextTurn(SCOPE, 0, 3));
        rows.getFirst().setRecordKind("turn-start");
        rows.getFirst().setTurnId(UUID.randomUUID().toString());
        invalid(() -> reader.nextTurn(SCOPE, 0, 3));
        rows.getFirst().setTurnId(TURN_ID);
        rows.getLast().setTurnId("invalid-private-turn");
        invalid(() -> reader.nextTurn(SCOPE, 0, 3));
    }

    @Test
    void rejectsMissingTerminalDuringPagingAndInvalidResumePositions() {
        completeWith(message(2, Origin.USER_INPUT, UserMessage.from("source")));
        Turn turn = reader.nextTurn(SCOPE, 0, 3).orElseThrow();
        for (Position position : List.of(
                new Position(-1, 0),
                new Position(4, 0),
                new Position(2, -1),
                new Position(2, 1000),
                new Position(1, 1))) {
            invalid(() -> reader.nextFragment(SCOPE, turn, position, bounds, 100));
        }
        rows.removeLast();
        invalid(() -> reader.nextFragment(SCOPE, turn, new Position(2, 0), bounds, 100));
    }

    @Test
    void disappearingStartAfterTerminalSelectionFailsWithSanitizedSourceError() {
        completeWith(message(2, Origin.USER_INPUT, UserMessage.from("source")));
        when(repository.sourcePage(SCOPE, 0, 3, 1)).thenReturn(List.of());
        invalid(() -> reader.nextTurn(SCOPE, 0, 3));
    }

    @ParameterizedTest
    @ValueSource(strings = {"\uD800", "\uDC00"})
    void malformedUnicodeCannotBecomeSourceEvidence(String malformed) {
        completeWith(message(2, Origin.USER_INPUT, UserMessage.from("expanded input"))
                .withSourceMessage(json(UserMessage.from("prefix" + malformed + "suffix"))));
        invalid(() -> collect(reader.nextTurn(SCOPE, 0, 3).orElseThrow(), 100));
    }

    @Test
    void budgetFailureDoesNotSilentlyConsumeSource() {
        completeWith(message(2, Origin.USER_INPUT, UserMessage.from("source")));
        Turn turn = reader.nextTurn(SCOPE, 0, 3).orElseThrow();
        assertThrows(
                MemoryBounds.CapacityException.class,
                () -> reader.nextFragment(SCOPE, turn, new Position(0, 0), bounds, 1));
        assertTrue("source"
                .equals(reader.nextFragment(SCOPE, turn, new Position(0, 0), bounds, 100)
                        .evidence()
                        .text()));
    }

    private List<ChatHistory> page(long after, long through, int limit, boolean terminalOnly) {
        assertTrue(limit > 0 && limit <= 1000);
        return rows.stream()
                .filter(row -> row.getId() > after && row.getId() <= through)
                .filter(row ->
                        !terminalOnly || List.of("turn-complete", "turn-abort").contains(row.getRecordKind()))
                .limit(limit)
                .toList();
    }

    private List<Evidence> collect(Turn turn, int tokens) {
        List<Evidence> result = new ArrayList<>();
        Position position = new Position(turn.afterId(), 0);
        for (int count = 0; count < 10000; count++) {
            var fragment = reader.nextFragment(SCOPE, turn, position, bounds, tokens);
            if (fragment.complete()) {
                assertNull(fragment.evidence());
                assertEquals(new Position(turn.throughId(), 0), fragment.next());
                return result;
            }
            assertNotNull(fragment.evidence());
            assertTrue(fragment.next().rowId() > position.rowId()
                    || fragment.next().rowId() == position.rowId()
                            && (fragment.next().offset() > position.offset()
                                    || fragment.next().offset() == 0));
            result.add(fragment.evidence());
            position = fragment.next();
        }
        throw new AssertionError("Source pagination did not terminate");
    }

    private void completeWith(ChatHistory message) {
        rows.add(marker(1, "turn-start"));
        rows.add(message);
        rows.add(marker(3, "turn-complete"));
    }

    private static ChatHistory marker(long id, String kind) {
        return new ChatHistory()
                .withId(id)
                .withMemoryId(SCOPE.chatId())
                .withTurnId(TURN_ID)
                .withEpisode(2L)
                .withRecordKind(kind)
                .withEnabled((byte) 1)
                .withGmtCreate(NOW);
    }

    private static ChatHistory message(long id, Origin origin, ChatMessage message) {
        return marker(id, "message").withMessageOrigin(origin.text()).withMessage(json(message));
    }

    private static String json(ChatMessage message) {
        return ChatMessageSerializer.messageToJson(message);
    }

    private static void invalid(Runnable operation) {
        IllegalStateException error = assertThrows(IllegalStateException.class, operation::run);
        assertEquals("Invalid finalized memory source", error.getMessage());
        assertNull(error.getCause());
        assertEquals(0, error.getSuppressed().length);
    }
}
