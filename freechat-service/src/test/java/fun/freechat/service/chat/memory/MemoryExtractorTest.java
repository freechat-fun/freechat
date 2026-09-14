package fun.freechat.service.chat.memory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.output.TokenUsage;
import fun.freechat.service.enums.EmbeddingStoreType;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class MemoryExtractorTest {
    private static final Instant OBSERVED = Instant.parse("2026-09-12T10:00:00Z");
    private static final MemoryScope SCOPE =
            new MemoryScope("chat", "user", "character", 1, EmbeddingStoreType.EN_LONG_TERM_MEMORY);
    private static final String HASH = "a".repeat(64);
    private static final String PRIVATE = "private-extraction-payload";
    private static final String VALID = "{\"summary\":\"The user prefers tea.\",\"userFacts\":[{\"key\":\"drink\","
            + "\"value\":\"Prefers tea\",\"sourceIds\":[2],\"fictional\":false}],\"characterDeltas\":[]}";
    private final LongTermMemoryProperties properties = new LongTermMemoryProperties();
    private final MemoryBounds bounds =
            new MemoryBounds(MemoryBoundsTest.estimator(text -> Math.max(1, text.length() / 4)), properties);
    private final MemoryExtractor extractor = new MemoryExtractor(properties, bounds);
    private final List<String> callIds = new ArrayList<>();
    private final List<TokenUsage> recorded = new ArrayList<>();

    @Test
    void usesSeparateToolFreeRequestWithOriginalEvidenceAndServerDates() {
        FakeModel model = new FakeModel(VALID);
        MemoryExtractor.Output output =
                extractor.extract(model, null, input(MemoryPublicationRepository.Operation.IDLE), budget(2));
        assertEquals("The user prefers tea.", output.summary());
        assertEquals(
                List.of(new MemoryDocument.Fact("drink", "Prefers tea", List.of(2L), OBSERVED, false)),
                output.userFacts());
        assertTrue(output.characterDeltas().isEmpty());
        assertEquals(1, model.requests.size());
        ChatRequest request = model.requests.getFirst();
        assertEquals(2, request.messages().size());
        assertTrue(request.messages().getFirst() instanceof SystemMessage);
        assertTrue(request.messages().getLast() instanceof UserMessage);
        assertTrue(request.toolSpecifications() == null
                || request.toolSpecifications().isEmpty());
        String system = ((SystemMessage) request.messages().getFirst()).text();
        assertTrue(system.contains("untrusted"));
        assertTrue(system.contains("credentials"));
        assertTrue(system.contains("fictional"));
        assertTrue(((UserMessage) request.messages().getLast()).singleText().contains(PRIVATE));
        assertEquals(List.of(new TokenUsage(10, 5)), recorded);
        assertEquals(1, callIds.size());
        assertEquals(callIds.getFirst(), UUID.fromString(callIds.getFirst()).toString());
    }

    @Test
    void repairsOnceWithoutEchoingTheInvalidResponseAndAccountsBothCalls() {
        FakeModel model = new FakeModel(PRIVATE, VALID);
        extractor.extract(model, null, input(MemoryPublicationRepository.Operation.IDLE), budget(2));
        assertEquals(2, model.requests.size());
        assertEquals(2, recorded.size());
        assertEquals(2, callIds.stream().distinct().count());
        assertEquals(2, model.requests.getLast().messages().size());
        assertFalse(((SystemMessage) model.requests.getLast().messages().getFirst())
                .text()
                .contains(PRIVATE));
        assertEquals(
                model.requests.getFirst().messages().getLast(),
                model.requests.getLast().messages().getLast());
    }

    @ParameterizedTest
    @MethodSource("invalidOutputs")
    void rejectsNoncanonicalSchemaAndUnbackedFactsWithNoPayloadBearingErrors(String output) {
        FakeModel model = new FakeModel(output, output, VALID);
        RuntimeException error = assertThrows(
                IllegalArgumentException.class,
                () -> extractor.extract(model, null, input(MemoryPublicationRepository.Operation.IDLE), budget(3)));
        assertSafe(error);
        assertEquals(2, model.requests.size());
        assertEquals(2, recorded.size());
    }

    private static Stream<String> invalidOutputs() {
        return Stream.of(
                "null",
                "[]",
                "{}",
                VALID + "{}",
                "```json\n" + VALID + "\n```",
                VALID.replace("\"summary\":", "\"summary\":\"duplicate\",\"summary\":"),
                VALID.replace("\"summary\":", "\"scope\":\"" + PRIVATE + "\",\"summary\":"),
                VALID.replace("[2]", "[999]"),
                VALID.replace("[2]", "[3]"),
                VALID.replace("[2]", "[]"),
                VALID.replace("[2]", "[2,2]"),
                VALID.replace("[2]", "[2.1]"),
                VALID.replace("[2]", "[\"2\"]"),
                VALID.replace("false", "\"false\""),
                VALID.replace("\"drink\"", "\"invalid key\""),
                VALID.replace("\"Prefers tea\"", "null"),
                VALID.replace("\"characterDeltas\":[]", "\"characterDeltas\":null"));
    }

    @Test
    void assistantEvidenceCanDescribeFictionButCannotEstablishRealUserBiography() {
        FakeModel model = new FakeModel(VALID.replace("[2]", "[3]").replace("false", "true"));
        MemoryExtractor.Output output =
                extractor.extract(model, null, input(MemoryPublicationRepository.Operation.IDLE), budget(1));
        assertTrue(output.userFacts().getFirst().fictional());
        assertEquals(List.of(3L), output.userFacts().getFirst().sourceIds());
    }

    @Test
    void revalidationAllowsExplicitFactRemovalWithoutInventingAnEpisodeOrSource() {
        MemoryDocument.Fact previous =
                new MemoryDocument.Fact("drink", "Prefers tea", List.of(1L), OBSERVED.minusSeconds(10), false);
        MemoryExtractor.Input input = new MemoryExtractor.Input(
                SCOPE,
                HASH,
                MemoryPublicationRepository.Operation.REVALIDATE,
                "Prefers tea",
                "Baseline character",
                "",
                List.of(previous),
                List.of(),
                List.of(),
                OBSERVED);
        FakeModel model = new FakeModel("{\"summary\":\"\",\"userFacts\":[],\"characterDeltas\":[]}");
        MemoryExtractor.Output output = extractor.extract(model, null, input, budget(1));
        assertEquals("", output.summary());
        assertTrue(output.userFacts().isEmpty());
    }

    @Test
    void previousFactsKeepTheirOriginalObservationDateDuringRevalidation() {
        MemoryDocument.Fact previous =
                new MemoryDocument.Fact("drink", "Prefers tea", List.of(2L), OBSERVED.minusSeconds(10), false);
        MemoryExtractor.Input input = new MemoryExtractor.Input(
                SCOPE,
                HASH,
                MemoryPublicationRepository.Operation.REVALIDATE,
                "",
                "",
                "",
                List.of(previous),
                List.of(),
                List.of(),
                OBSERVED);
        String json = VALID.replace("The user prefers tea.", "");
        MemoryExtractor.Output result = extractor.extract(new FakeModel(json), null, input, budget(1));
        assertEquals(previous, result.userFacts().getFirst());
    }

    @Test
    void overflowCannotUpdateProfilesAndRepairCountsTowardForegroundBudget() {
        FakeModel model = new FakeModel(VALID, VALID);
        RuntimeException error = assertThrows(
                MemoryBounds.CapacityException.class,
                () -> extractor.extract(model, null, input(MemoryPublicationRepository.Operation.OVERFLOW), budget(1)));
        assertSafe(error);
        assertEquals(1, model.requests.size());
        assertEquals(1, recorded.size());
    }

    @Test
    void providerContextAndByteLimitsRejectBeforeAnyPaidCall() {
        FakeModel model = new FakeModel(VALID);
        assertThrows(
                MemoryBounds.CapacityException.class,
                () -> extractor.extract(model, 1, input(MemoryPublicationRepository.Operation.IDLE), budget(2)));
        MemoryExtractor.Input large = new MemoryExtractor.Input(
                SCOPE,
                HASH,
                MemoryPublicationRepository.Operation.IDLE,
                PRIVATE.repeat(MemoryBounds.EXTRACTION_BYTES),
                "",
                "",
                List.of(),
                List.of(),
                List.of(),
                OBSERVED);
        assertThrows(MemoryBounds.CapacityException.class, () -> extractor.extract(model, null, large, budget(2)));
        assertTrue(model.requests.isEmpty());
        assertTrue(recorded.isEmpty());
    }

    @Test
    void recordsUsageEvenWhenLeaseIsLostImmediatelyAfterProviderReturns() {
        AtomicInteger checks = new AtomicInteger();
        MemoryExtractor.Budget budget = new MemoryExtractor.Budget(
                2,
                Duration.ofSeconds(5),
                () -> {
                    if (checks.incrementAndGet() > 1) {
                        throw new IllegalStateException("lease expired");
                    }
                },
                (id, usage) -> recorded.add(usage));
        FakeModel model = new FakeModel(VALID);
        assertThrows(
                IllegalStateException.class,
                () -> extractor.extract(model, null, input(MemoryPublicationRepository.Operation.IDLE), budget));
        assertEquals(List.of(new TokenUsage(10, 5)), recorded);
        assertEquals(1, model.requests.size());
    }

    @Test
    void providerFailuresAreNotRepairedOrLeaked() {
        ChatModel failing = new ChatModel() {
            @Override
            public ChatResponse chat(ChatRequest request) {
                throw new IllegalStateException(PRIVATE, new IllegalArgumentException(PRIVATE));
            }
        };
        assertSafe(assertThrows(
                IllegalStateException.class,
                () -> extractor.extract(failing, null, input(MemoryPublicationRepository.Operation.IDLE), budget(2))));
        assertTrue(recorded.isEmpty());
    }

    private MemoryExtractor.Budget budget(int limit) {
        return new MemoryExtractor.Budget(limit, Duration.ofSeconds(10), () -> {}, (id, usage) -> {
            callIds.add(id);
            recorded.add(usage);
        });
    }

    private static MemoryExtractor.Input input(MemoryPublicationRepository.Operation operation) {
        return new MemoryExtractor.Input(
                SCOPE,
                HASH,
                operation,
                "User baseline",
                "Character baseline",
                "Previous summary",
                List.of(),
                List.of(),
                List.of(
                        new MemorySourceReader.Evidence(
                                2,
                                MemoryTurnRepository.Origin.USER_INPUT.text(),
                                true,
                                OBSERVED,
                                0,
                                "I prefer tea. " + PRIVATE),
                        new MemorySourceReader.Evidence(
                                3,
                                MemoryTurnRepository.Origin.ASSISTANT_OUTPUT.text(),
                                false,
                                OBSERVED,
                                0,
                                "Story: you are a captain.")),
                OBSERVED);
    }

    private static void assertSafe(RuntimeException failure) {
        assertNull(failure.getCause());
        assertEquals(0, failure.getSuppressed().length);
        assertFalse(failure.toString().contains(PRIVATE));
    }

    private static final class FakeModel implements ChatModel {
        private final List<String> responses;
        private final List<ChatRequest> requests = new ArrayList<>();

        private FakeModel(String... responses) {
            this.responses = List.of(responses);
        }

        @Override
        public ChatResponse chat(ChatRequest request) {
            requests.add(request);
            return ChatResponse.builder()
                    .aiMessage(AiMessage.from(responses.get(requests.size() - 1)))
                    .tokenUsage(new TokenUsage(10, 5))
                    .build();
        }
    }
}
