package fun.freechat;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolSpecifications;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.TokenCountEstimator;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.filter.Filter;
import fun.freechat.mapper.ChatHistoryMapper;
import fun.freechat.mapper.ChatMemoryCommitMapper;
import fun.freechat.mapper.ChatMemoryCoordinationMapper;
import fun.freechat.mapper.ChatMemoryStateMapper;
import fun.freechat.model.ChatMemoryCommit;
import fun.freechat.model.ChatMemoryState;
import fun.freechat.service.chat.memory.LongTermMemoryProperties;
import fun.freechat.service.chat.memory.MemoryBounds;
import fun.freechat.service.chat.memory.MemoryBoundsFactory;
import fun.freechat.service.chat.memory.MemoryDocument;
import fun.freechat.service.chat.memory.MemoryDocument.Kind;
import fun.freechat.service.chat.memory.MemoryDocumentCodec;
import fun.freechat.service.chat.memory.MemoryManifest;
import fun.freechat.service.chat.memory.MemoryPublicationRepository;
import fun.freechat.service.chat.memory.MemoryPublisher;
import fun.freechat.service.chat.memory.MemoryRecallTools;
import fun.freechat.service.chat.memory.MemoryScope;
import fun.freechat.service.chat.memory.MemoryVectorRepository;
import fun.freechat.service.enums.EmbeddingStoreType;
import fun.freechat.service.rag.EmbeddingModelService;
import fun.freechat.service.rag.ExactEmbeddingStoreService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntConsumer;
import java.util.function.ToIntFunction;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.SimpleTransactionStatus;

/** No Spring application, network, paid providers, files, or Mockito inline-agent attachment. */
@Timeout(30)
class MemoryRecallToolsTest {
    private static final ObjectMapper JSON = new ObjectMapper();
    private static final MemoryScope SCOPE =
            new MemoryScope("chat", "owner", "character", 7, EmbeddingStoreType.EN_LONG_TERM_MEMORY);
    private static final Instant DATE = Instant.parse("2026-09-12T10:00:00Z");
    private static final Embedding VECTOR = Embedding.from(new float[] {1, 0});
    private static final String SECRET = "private-credential-and-ANN-instruction";

    @Test
    void goldenSearchExactReadAndUnicodePagination() throws Exception {
        Fixture f = new Fixture();
        String summary = "A\uD83D\uDE80中文\\\"\n".repeat(90);
        MemoryDocument doc = f.seed(1, summary);
        MemoryRecallTools tools = f.tools(5);
        JsonNode search = f.output(tools.searchMemory("remember", null));
        assertTrue(search.path("historical").asBoolean());
        assertFalse(search.path("authoritative").asBoolean());
        JsonNode hit = search.path("results").get(0);
        assertEquals(doc.id(), hit.path("recordId").asText());
        assertEquals(DATE.toString(), hit.path("observedAt").asText());
        assertEquals(prefix(summary, 256), hit.path("excerpt").asText());
        assertEquals(256, hit.path("nextOffset").asInt());
        assertTrue(hit.path("more").asBoolean());
        assertEquals(List.of("query: remember"), f.embedded);
        assertEquals(15, f.lastSearch.maxResults());
        assertTrue(f.lastSearch.filter().test(SCOPE.metadata().put("record_kind", "EPISODE_SUMMARY")));
        assertFalse(search.toString().contains("role"));
        assertFalse(search.toString().contains(SECRET));
        assertFalse(search.toString().contains(SCOPE.userId()));
        assertFalse(search.toString().contains(doc.commitId()));

        JsonNode first = f.output(tools.readMemory(doc.id(), null, null));
        assertEquals(prefix(summary, 512), first.path("text").asText());
        assertEquals(512, first.path("nextOffset").asInt());
        JsonNode second =
                f.output(tools.readMemory(doc.id(), first.path("nextOffset").asInt(), null));
        assertEquals(summary, first.path("text").asText() + second.path("text").asText());
        assertTrue(second.path("nextOffset").isNull());
        assertFalse(second.path("more").asBoolean());
        // Each operation: exact commit lookup, SQL authorization, hash-checked read, repeat authorization.
        assertEquals(
                List.of("get", "sql", "get", "sql", "get", "sql", "get", "sql", "get", "sql", "get", "sql"), f.events);
        assertTrue(f.guardCalls.get() >= 3 * 4);
        f.assertBudgets();
    }

    @Test
    void clampsReadPageAndReturnsEmptyTerminalPagesAtOrBeyondEnd() throws Exception {
        Fixture f = new Fixture();
        f.wideBudgets();
        MemoryDocument doc = f.seed(1, "\uD83D\uDE80".repeat(2048));
        MemoryRecallTools tools = f.tools(1);
        f.output(tools.searchMemory("q", 1));
        JsonNode page = f.output(tools.readMemory(doc.id(), 0, Integer.MAX_VALUE));
        String text = page.path("text").asText();
        int count = text.codePointCount(0, text.length());
        assertTrue(count > 0 && count <= 2048);
        assertEquals(count, page.path("nextOffset").asInt());
        assertEquals("\uD83D\uDE80".repeat(count), text);
        for (int offset : new int[] {2048, 2049, Integer.MAX_VALUE}) {
            JsonNode terminal = f.output(tools.readMemory(doc.id(), offset, 9));
            assertEquals("", terminal.path("text").asText());
            assertEquals(offset, terminal.path("offset").asInt());
            assertTrue(terminal.path("nextOffset").isNull());
            assertFalse(terminal.path("more").asBoolean());
        }
        f.assertBudgets();
    }

    @Test
    void exactCodePointClampIs2048WhenBytesAndTokensPermit() throws Exception {
        Fixture f = new Fixture();
        f.wideBudgets();
        MemoryDocument doc = f.seed(1, "a".repeat(3000));
        MemoryRecallTools tools = f.tools(1);
        f.output(tools.searchMemory("q", null));
        JsonNode page = f.output(tools.readMemory(doc.id(), 0, Integer.MAX_VALUE));
        assertEquals(2048, page.path("text").asText().length());
        assertEquals(2048, page.path("nextOffset").asInt());
    }

    @Test
    void ranksByScoreThenDateThenIdAndDeduplicatesCandidateIds() throws Exception {
        Fixture f = new Fixture();
        f.wideBudgets();
        f.seed(1, SCOPE, Kind.EPISODE_SUMMARY, false, "relevant old", DATE.minusSeconds(100), 0.95);
        f.seed(2, SCOPE, Kind.EPISODE_SUMMARY, false, "newer tied", DATE.plusSeconds(10), 0.8);
        f.seed(3, SCOPE, Kind.WINDOW_SUMMARY, true, "older tied", DATE, 0.8);
        f.seed(4, SCOPE, Kind.EPISODE_SUMMARY, false, "irrelevant new", DATE.plusSeconds(100), 0.4);
        f.matches.add(0, f.match(id(1), 0.5));
        f.matches.add(f.match(id(99), Double.NaN));
        f.matches.add(f.match("not-a-uuid", 1));
        JsonNode search = f.output(f.tools(4).searchMemory("q", Integer.MAX_VALUE));
        assertEquals(List.of(id(1), id(2), id(3), id(4)), ids(search));
        assertEquals(8, f.exactReads);
        assertEquals(12, f.lastSearch.maxResults());
    }

    @Test
    void enforcesBackendAndGlobalResultLimitsAndAtMostNinetyCandidates() throws Exception {
        Fixture f = new Fixture();
        f.wideBudgets();
        for (int index = 1; index <= 100; index++) {
            f.seed(index, "x");
        }
        JsonNode search = f.output(f.tools(Integer.MAX_VALUE).searchMemory("q", Integer.MAX_VALUE));
        assertEquals(30, search.path("results").size());
        assertEquals(90, f.lastSearch.maxResults());
        assertEquals(180, f.exactReads, "Do not scan an unbounded rogue ANN response");
        assertTrue(search.path("more").asBoolean());
        JsonNode smaller = f.output(f.tools(2).searchMemory("q", 100));
        assertEquals(2, smaller.path("results").size());
        assertEquals(6, f.lastSearch.maxResults());
    }

    @ParameterizedTest
    @ValueSource(strings = {"chat", "owner", "character", "generation", "store"})
    void foreignCandidateAndGuessedIdCannotEscapeAnyScopeDimension(String dimension) throws Exception {
        Fixture f = new Fixture();
        MemoryScope foreign = new MemoryScope(
                dimension.equals("chat") ? "other-chat" : SCOPE.chatId(),
                dimension.equals("owner") ? "other-owner" : SCOPE.userId(),
                dimension.equals("character") ? "other-character" : SCOPE.characterUid(),
                dimension.equals("generation") ? 8 : SCOPE.generation(),
                dimension.equals("store") ? EmbeddingStoreType.ZH_LONG_TERM_MEMORY : SCOPE.storeType());
        MemoryDocument doc = f.seed(1, foreign, Kind.EPISODE_SUMMARY, false, SECRET, DATE, 0.9);
        MemoryRecallTools tools = f.tools(5);
        assertEquals(0, f.output(tools.searchMemory("q", 5)).path("results").size());
        f.code("NOT_FOUND", tools.readMemory(doc.id(), 0, 10));
        assertTrue(f.events.stream().noneMatch("sql"::equals));
        f.assertBudgets();
    }

    @ParameterizedTest
    @ValueSource(strings = {"prepared", "terminal", "missing", "manifest", "generation", "chat"})
    void onlyExactCommittedManifestIdsAreVisible(String fault) throws Exception {
        Fixture f = new Fixture();
        MemoryDocument doc = f.seed(1, SECRET);
        ChatMemoryCommit commit = f.attempts.get(doc.commitId());
        switch (fault) {
            case "prepared", "terminal" -> commit.setStatus(fault);
            case "missing" -> f.attempts.clear();
            case "manifest" ->
                commit.setManifest(MemoryDocumentCodec.encodeManifest(new MemoryManifest(
                        List.of(new MemoryManifest.Entry(id(2), Kind.EPISODE_SUMMARY, false, "0".repeat(64))))));
            case "generation" -> commit.setGeneration(8L);
            case "chat" -> commit.setChatId("other-chat");
            default -> throw new AssertionError(fault);
        }
        MemoryRecallTools tools = f.tools(5);
        assertEquals(0, f.output(tools.searchMemory("q", 5)).path("results").size());
        f.code("NOT_FOUND", tools.readMemory(doc.id(), null, null));
        assertEquals(2, f.exactReads, "Uncommitted candidates must never reach content decoding");
    }

    @Test
    void checkpointsProfilesAndRollingHeadsRemainPrivateEvenWhenAnnReturnsThem() throws Exception {
        Fixture f = new Fixture();
        f.seed(1, SCOPE, Kind.EXTRACTION_CHECKPOINT, false, SECRET, DATE, 1);
        f.seed(2, SCOPE, Kind.PROFILE_SNAPSHOT, false, "", DATE, 1);
        f.seed(3, SCOPE, Kind.WINDOW_SUMMARY, false, SECRET, DATE, 1);
        f.seed(4, SCOPE, Kind.WINDOW_SUMMARY, true, "archive", DATE, 0.9);
        MemoryRecallTools tools = f.tools(5);
        assertEquals(List.of(id(4)), ids(f.output(tools.searchMemory("q", 5))));
        for (int id = 1; id <= 3; id++) {
            f.code("NOT_FOUND", tools.readMemory(id(id), 0, 30));
        }
    }

    @Test
    void guessedEvenValidSameScopeIdsMustFirstBeReturnedBySearch() throws Exception {
        Fixture f = new Fixture();
        MemoryDocument doc = f.seed(1, "known only to server");
        MemoryRecallTools tools = f.tools(1);
        f.code("NOT_FOUND", tools.readMemory(doc.id(), 0, 10));
        f.code("NOT_FOUND", tools.readMemory(id(999), 0, 10));
        f.output(tools.searchMemory("q", 1));
        assertEquals(
                "known only",
                f.output(tools.readMemory(doc.id(), 0, 10)).path("text").asText());
        f.code("NOT_FOUND", f.tools(1).readMemory(doc.id(), 0, 10));
    }

    @Test
    void readAndDuplicateReauthorizeAndDoNotReplayAfterRevocation() throws Exception {
        Fixture f = new Fixture();
        MemoryDocument doc = f.seed(1, "saved summary");
        MemoryRecallTools tools = f.tools(1);
        f.output(tools.searchMemory("q", 1));
        f.output(tools.readMemory(doc.id(), 0, 4));
        f.code("DUPLICATE", tools.readMemory(doc.id(), 0, 4));
        f.attempts.get(doc.commitId()).setStatus("terminal");
        f.code("NOT_FOUND", tools.readMemory(doc.id(), 0, 4));
        assertEquals(7, f.exactReads);
    }

    @Test
    void repeatAuthorizationDeniesPublicationRevokedDuringExactRead() throws Exception {
        Fixture f = new Fixture();
        MemoryDocument doc = f.seed(1, "summary");
        MemoryRecallTools tools = f.tools(1);
        f.output(tools.searchMemory("q", 1));
        f.onExactRead = count -> {
            if (count == 4) {
                f.attempts.get(doc.commitId()).setStatus("terminal");
            }
        };
        f.code("NOT_FOUND", tools.readMemory(doc.id(), 0, 7));
        assertEquals(4, f.events.stream().filter("sql"::equals).count());
    }

    @Test
    void hashMismatchIsSanitizedAndNeverReturnsAnnOrCanonicalPayload() throws Exception {
        Fixture f = new Fixture();
        MemoryDocument doc = f.seed(1, "summary");
        TextSegment segment = f.records.get(doc.id());
        f.records.put(doc.id(), TextSegment.from(segment.text() + " ", segment.metadata()));
        f.code("BACKEND_UNAVAILABLE", f.tools(1).searchMemory("q", 1));
    }

    @Test
    void rejectsEveryInvalidArgumentBeforeAnyBackendOrGuardIo() throws Exception {
        Fixture f = new Fixture();
        MemoryRecallTools tools = f.tools(5);
        for (String query : new String[] {null, "", " \n\t", "x".repeat(2001), "\uD800", "\uDC00"}) {
            f.code("INVALID_QUERY", tools.searchMemory(query, 1));
        }
        for (int limit : new int[] {0, -1, Integer.MIN_VALUE}) {
            f.code("INVALID_LIMIT", tools.searchMemory("q", limit));
            f.code("INVALID_LIMIT", tools.readMemory(id(1), 0, limit));
        }
        for (int offset : new int[] {-1, Integer.MIN_VALUE}) {
            f.code("INVALID_OFFSET", tools.readMemory(id(1), offset, 1));
        }
        for (String id : new String[] {null, "", "../secret", "x".repeat(2000), "1-1-1-1-1"}) {
            f.code("INVALID_RECORD_ID", tools.readMemory(id, 0, 1));
        }
        assertTrue(f.events.isEmpty());
        assertTrue(f.embedded.isEmpty());
        assertEquals(0, f.guardCalls.get());
        f.assertBudgets();
    }

    @Test
    void acceptsQueryLimitInCodePointsRatherThanUtf16Units() throws Exception {
        Fixture f = new Fixture();
        f.properties.setSearchQueryMaxChars(2);
        MemoryRecallTools tools = f.tools(1);
        f.output(tools.searchMemory("\uD83D\uDE80\uD83D\uDE80", null));
        f.code("INVALID_QUERY", tools.searchMemory("\uD83D\uDE80\uD83D\uDE80x", null));
        assertEquals(1, f.embedded.size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"x", "中", "\uD83D\uDE80", "\"\\\n\u0001"})
    void completeEscapedJsonFitsByteAndTokenCapsWithoutSurrogateSplits(String unit) throws Exception {
        Fixture f = new Fixture();
        f.wideBudgets();
        int repeat = MemoryBounds.COMPACT_TEXT_BYTES / MemoryBounds.bytes(unit);
        MemoryDocument doc = f.seed(1, unit.repeat(repeat));
        MemoryRecallTools tools = f.tools(1);
        f.output(tools.searchMemory("q", null));
        JsonNode page = f.output(tools.readMemory(doc.id(), 0, Integer.MAX_VALUE));
        String text = page.path("text").asText();
        assertFalse(text.isEmpty());
        assertTrue(doc.summary().startsWith(text));
        assertValidUnicode(text);
        assertEquals(
                text.codePointCount(0, text.length()), page.path("nextOffset").asInt());
        f.assertBudgets();
    }

    @Test
    void shrinksExcerptsAndResultListAndOnlyCachesActuallyEmittedIds() throws Exception {
        Fixture f = new Fixture();
        f.counter = text -> text.codePointCount(0, text.length());
        f.properties.setToolResultMaxTokens(330);
        f.properties.setToolTotalMaxTokens(4000);
        f.seed(1, "a".repeat(1000));
        f.seed(2, "b".repeat(1000));
        MemoryRecallTools tools = f.tools(2);
        JsonNode first = f.output(tools.searchMemory("q", 2));
        assertEquals(List.of(id(1)), ids(first));
        assertTrue(first.path("more").asBoolean());
        assertTrue(first.path("results").get(0).path("excerpt").asText().length() < 256);
        JsonNode second = f.output(tools.searchMemory("q", 2));
        assertEquals(List.of(id(2)), ids(second));
        f.code("DUPLICATE", tools.searchMemory("q", 2));
        f.assertBudgets();
    }

    @Test
    void exactRequestsAndEquivalentClampedPagesDeduplicateWithoutReplayedText() throws Exception {
        Fixture f = new Fixture();
        f.wideBudgets();
        MemoryDocument doc = f.seed(1, "short");
        MemoryRecallTools tools = f.tools(1);
        f.output(tools.searchMemory("q", 1));
        f.code("DUPLICATE", tools.searchMemory("another query", 1));
        f.output(tools.readMemory(doc.id(), null, null));
        f.code("DUPLICATE", tools.readMemory(doc.id(), 0, 512));
        f.code("DUPLICATE", tools.readMemory(doc.id(), 0, Integer.MAX_VALUE));
        assertEquals(
                "hort",
                f.output(tools.readMemory(doc.id(), 1, null)).path("text").asText());
        f.assertBudgets();
    }

    @Test
    void accountsForErrorNotFoundDedupAndSuccessfulPayloadsUntilTokenExhaustion() throws Exception {
        Fixture f = new Fixture();
        f.counter = ignored -> 4; // MemoryBounds charges five including its 25% margin.
        f.properties.setToolResultMaxTokens(5);
        f.properties.setToolTotalMaxTokens(30);
        MemoryDocument doc = f.seed(1, "summary");
        MemoryRecallTools tools = f.tools(1);
        f.code("INVALID_QUERY", tools.searchMemory("", 1));
        f.code("NOT_FOUND", tools.readMemory(id(999), 0, 2));
        f.output(tools.searchMemory("q", 1));
        f.code("DUPLICATE", tools.searchMemory("q", 1));
        f.output(tools.readMemory(doc.id(), 0, 2));
        f.code("DUPLICATE", tools.readMemory(doc.id(), 0, 2));
        int reads = f.exactReads;
        assertEquals("", tools.readMemory(doc.id(), 2, 2));
        assertEquals("", tools.searchMemory("", 1));
        assertEquals(reads, f.exactReads);
        f.assertBudgets();
    }

    @Test
    void cumulativeBytesIncludeEnvelopesAndErrorsAndNeverExceed32KiB() throws Exception {
        Fixture f = new Fixture();
        f.wideBudgets();
        f.counter = ignored -> 0;
        for (int index = 1; index <= 12; index++) {
            f.seed(index, "\uD83D\uDE80".repeat(2048));
        }
        MemoryRecallTools tools = f.tools(12);
        JsonNode search = f.output(tools.searchMemory("q", 12));
        assertTrue(search.path("results").size() < 12);
        for (String id : ids(search)) {
            f.output(tools.readMemory(id, 0, 2048));
        }
        for (int attempt = 0; attempt < 140; attempt++) {
            f.output(tools.searchMemory("", 1));
        }
        assertEquals("", tools.searchMemory("", 1));
        f.assertBudgets();
        assertTrue(f.outputs.stream().mapToInt(MemoryBounds::bytes).sum() > 32000);
    }

    @Test
    void nonmonotonicTokenizerMeasuresEverySelectedCompleteEndpointIncludingEnvelope() throws Exception {
        Fixture f = new Fixture();
        f.counter = text -> {
            if (!text.startsWith("{")) {
                return 1;
            }
            try {
                JsonNode value = JSON.readTree(text);
                if (value.has("text")) {
                    int length = value.path("text").asText().length();
                    return length == 7 || length == 3 ? 10 : 10000;
                }
                return 1;
            } catch (Exception error) {
                throw new AssertionError(error);
            }
        };
        f.properties.setToolResultMaxTokens(20);
        MemoryDocument doc = f.seed(1, "abcdefghij");
        MemoryRecallTools tools = f.tools(1);
        f.output(tools.searchMemory("q", 1));
        JsonNode page = f.output(tools.readMemory(doc.id(), 0, 10));
        assertEquals("abcdefg", page.path("text").asText());
        assertEquals(7, page.path("nextOffset").asInt());
        assertEquals(
                "hij", f.output(tools.readMemory(doc.id(), 7, 10)).path("text").asText());
        f.assertBudgets();
    }

    @Test
    void neverEmitsNonterminalZeroProgressPageWhenEvenOneCodePointCannotFit() throws Exception {
        Fixture f = new Fixture();
        MemoryDocument doc = f.seed(1, "summary");
        MemoryRecallTools tools = f.tools(1);
        f.output(tools.searchMemory("q", 1));
        f.counter = text -> text.contains("\"text\"") ? 100000 : 1;
        f.code("BUDGET_EXHAUSTED", tools.readMemory(doc.id(), 0, 5));
        f.counter = ignored -> 1;
        assertEquals(
                "summa", f.output(tools.readMemory(doc.id(), 0, 5)).path("text").asText());
    }

    @Test
    void boundedCallCountStopsBackendLoopsEvenWithZeroTokenCounter() throws Exception {
        Fixture f = new Fixture();
        f.counter = ignored -> 0;
        MemoryRecallTools tools = f.tools(1);
        for (int index = 0; index < 128; index++) {
            assertEquals(
                    "ok", f.output(tools.searchMemory("q", 1)).path("status").asText());
        }
        for (int index = 0; index < 10; index++) {
            f.code("CALL_LIMIT", tools.searchMemory("q", 1));
        }
        assertEquals(128, f.embedded.size());
        f.assertBudgets();
    }

    @Test
    void fallsBackToCountedErrorCodeThenEmptyWhenNoEnvelopeFits() throws Exception {
        Fixture f = new Fixture();
        f.counter = String::length;
        String minimal = JSON.writeValueAsString(Map.of("code", "INVALID_QUERY"));
        int budget = Math.toIntExact(f.bounds.tokens(minimal));
        f.properties.setToolResultMaxTokens(budget);
        f.properties.setToolTotalMaxTokens(budget);
        MemoryRecallTools tools = f.tools(1);
        assertEquals(minimal, tools.searchMemory("", 1));
        assertEquals("", tools.searchMemory("", 1));
        f.properties.setToolResultMaxTokens(1);
        f.properties.setToolTotalMaxTokens(1);
        assertEquals("", f.tools(1).searchMemory("", 1));
        assertTrue(f.embedded.isEmpty());
        assertTrue(f.events.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"embedding", "search", "exact", "sql"})
    void backendFailuresHaveActionableSanitizedCodes(String failure) throws Exception {
        Fixture f = new Fixture();
        f.seed(1, "summary");
        f.failure = failure;
        f.code("BACKEND_UNAVAILABLE", f.tools(1).searchMemory("q", 1));
        f.assertBudgets();
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7})
    void invocationGuardFailureAtEverySearchBoundaryFailsClosedAndStaysRevoked(int boundary) throws Exception {
        Fixture f = new Fixture();
        f.seed(1, "summary");
        f.failGuardAt = boundary;
        MemoryRecallTools tools = f.tools(1);
        f.code("INVOCATION_INVALID", tools.searchMemory("q", 1));
        int reads = f.exactReads;
        f.code("INVOCATION_INVALID", tools.readMemory(id(1), 0, 5));
        assertEquals(reads, f.exactReads);
        f.assertBudgets();
    }

    @Test
    void readGuardFailureAndTokenMeasurementFailureNeverLeakPayload() throws Exception {
        Fixture f = new Fixture();
        MemoryDocument doc = f.seed(1, "summary");
        MemoryRecallTools tools = f.tools(1);
        f.output(tools.searchMemory("q", 1));
        f.failGuardAt = f.guardCalls.get() + 4;
        f.code("INVOCATION_INVALID", tools.readMemory(doc.id(), 0, 5));
        f.counter = ignored -> {
            throw new IllegalStateException(SECRET);
        };
        assertEquals("", tools.readMemory(doc.id(), 0, 5));
    }

    @Test
    void concurrentCallsShareAtomicBudgetsAndDedupState() throws Exception {
        Fixture f = new Fixture();
        f.counter = ignored -> 4;
        f.properties.setToolResultMaxTokens(5);
        f.properties.setToolTotalMaxTokens(50);
        MemoryDocument doc = f.seed(1, "summary");
        MemoryRecallTools tools = f.tools(1);
        f.output(tools.searchMemory("q", 1));
        try (var pool = Executors.newFixedThreadPool(8)) {
            var tasks = IntStream.range(0, 40)
                    .<java.util.concurrent.Callable<String>>mapToObj(ignored -> () -> tools.readMemory(doc.id(), 0, 5))
                    .toList();
            int payloads = 0;
            for (var result : pool.invokeAll(tasks)) {
                JsonNode output = f.output(result.get());
                if (output.has("text")) {
                    payloads++;
                }
            }
            assertEquals(1, payloads);
        }
        assertEquals(10, f.outputs.stream().filter(output -> !output.isEmpty()).count());
        f.assertBudgets();
    }

    @Test
    void descriptionsAndSchemasExposeNoTrustedScopeAndZeroRecallDisablesIo() throws Exception {
        Fixture f = new Fixture();
        MemoryRecallTools tools = f.tools(0);
        f.code("DISABLED", tools.searchMemory("q", null));
        f.code("DISABLED", tools.readMemory(id(1), null, null));
        assertTrue(f.events.isEmpty());
        assertTrue(f.embedded.isEmpty());
        assertThrows(IllegalArgumentException.class, () -> f.tools(-1));
        var schemas = ToolSpecifications.toolSpecificationsFrom(tools);
        assertEquals(2, schemas.size());
        var search = schemas.stream()
                .filter(schema -> schema.name().equals("searchMemory"))
                .findFirst()
                .orElseThrow();
        var read = schemas.stream()
                .filter(schema -> schema.name().equals("readMemory"))
                .findFirst()
                .orElseThrow();
        assertEquals(
                java.util.Set.of("query", "limit"),
                search.parameters().properties().keySet());
        assertEquals(
                java.util.Set.of("recordId", "offset", "limit"),
                read.parameters().properties().keySet());
        for (var method : MemoryRecallTools.class.getDeclaredMethods()) {
            Tool annotation = method.getAnnotation(Tool.class);
            if (annotation != null) {
                assertEquals(1, annotation.value().length);
                String description = annotation.value()[0];
                assertFalse(description.contains("\n"));
                assertTrue(description.contains("non-authoritative"));
                assertTrue(description.contains("implicit session scope"));
                assertTrue(description.contains("Unicode code-point"));
            }
        }
    }

    private static List<String> ids(JsonNode search) {
        List<String> ids = new ArrayList<>();
        search.path("results").forEach(result -> ids.add(result.path("recordId").asText()));
        return ids;
    }

    private static String id(int value) {
        return new UUID(0, value).toString();
    }

    private static String prefix(String text, int count) {
        return text.substring(0, text.offsetByCodePoints(0, count));
    }

    private static void assertValidUnicode(String text) {
        for (int index = 0; index < text.length(); index++) {
            char c = text.charAt(index);
            if (Character.isHighSurrogate(c)) {
                assertTrue(++index < text.length() && Character.isLowSurrogate(text.charAt(index)));
            } else {
                assertFalse(Character.isLowSurrogate(c));
            }
        }
    }

    private static <T> T fake(Class<T> type, java.util.function.BiFunction<String, Object[], Object> calls) {
        return type.cast(java.lang.reflect.Proxy.newProxyInstance(
                type.getClassLoader(), new Class<?>[] {type}, (proxy, method, arguments) -> switch (method.getName()) {
                    case "equals" -> proxy == arguments[0];
                    case "hashCode" -> System.identityHashCode(proxy);
                    case "toString" -> type.getSimpleName() + " in-process test fake";
                    default -> calls.apply(method.getName(), arguments == null ? new Object[0] : arguments);
                }));
    }

    private static <T> T unused(Class<T> type) {
        return fake(type, (method, arguments) -> {
            throw new AssertionError("Unexpected access: " + type.getSimpleName() + "." + method);
        });
    }

    private static final class Fixture {
        final LongTermMemoryProperties properties = new LongTermMemoryProperties();
        final Map<String, TextSegment> records = new HashMap<>();
        final Map<String, ChatMemoryCommit> attempts = new HashMap<>();
        final List<EmbeddingMatch<TextSegment>> matches = new ArrayList<>();
        final List<String> embedded = new ArrayList<>();
        final List<String> events = new ArrayList<>();
        final List<String> outputs = new ArrayList<>();
        final AtomicInteger guardCalls = new AtomicInteger();
        final MemoryBounds bounds;
        final MemoryVectorRepository vectors;
        final MemoryPublisher publisher;
        final MemoryDocumentCodec codec;
        ToIntFunction<String> counter = text -> (text.codePointCount(0, text.length()) + 3) / 4;
        IntConsumer onExactRead = ignored -> {};
        EmbeddingSearchRequest lastSearch;
        int exactReads;
        int failGuardAt;
        String failure = "";

        @SuppressWarnings("unchecked")
        Fixture() {
            properties.setSummaryMaxTokens(100000);
            TokenCountEstimator estimator = fake(TokenCountEstimator.class, (method, arguments) -> {
                assertEquals("estimateTokenCountInText", method);
                return counter.applyAsInt((String) arguments[0]);
            });
            bounds = new MemoryBounds(estimator, properties);
            EmbeddingModel embeddingModel = new EmbeddingModel() {
                @Override
                public Response<List<Embedding>> embedAll(List<TextSegment> segments) {
                    fail("embedding");
                    embedded.addAll(segments.stream().map(TextSegment::text).toList());
                    return Response.from(
                            segments.stream().map(ignored -> VECTOR).toList());
                }
            };
            EmbeddingModelService models = fake(EmbeddingModelService.class, (method, arguments) -> switch (method) {
                case "modelForLang" -> embeddingModel;
                case "tokenCountEstimatorForLang" -> estimator;
                case "queryPrefixForLang" -> "query: ";
                default -> throw new AssertionError("Unexpected model operation: " + method);
            });
            EmbeddingStore<TextSegment> ann = fake(EmbeddingStore.class, (method, arguments) -> {
                assertEquals("search", method, "Recall must never write memory");
                fail("search");
                lastSearch = (EmbeddingSearchRequest) arguments[0];
                // Deliberately violate ANN filtering and maxResults: the tool must still bound/authorize results.
                return new EmbeddingSearchResult<>(List.copyOf(matches));
            });
            ExactEmbeddingStoreService stores = fake(ExactEmbeddingStoreService.class, (method, arguments) -> {
                if (method.equals("of")) {
                    assertEquals(SCOPE.chatId(), arguments[0]);
                    assertEquals(SCOPE.storeType(), arguments[1]);
                    return ann;
                }
                assertEquals("get", method, "Recall must never write memory");
                fail("exact");
                events.add("get");
                onExactRead.accept(++exactReads);
                EmbeddingStoreType type = (EmbeddingStoreType) arguments[0];
                Filter filter = (Filter) arguments[2];
                return Optional.ofNullable(records.get((String) arguments[1]))
                        .filter(segment -> type.text().equals(segment.metadata().getString("store_type")))
                        .filter(segment -> filter.test(segment.metadata()));
            });
            ChatMemoryState state = new ChatMemoryState()
                    .withChatId(SCOPE.chatId())
                    .withUserId(SCOPE.userId())
                    .withCharacterUid(SCOPE.characterUid())
                    .withGeneration(SCOPE.generation())
                    .withStatus("active")
                    .withStoreType(SCOPE.storeType().text());
            ChatMemoryCoordinationMapper coordination =
                    fake(ChatMemoryCoordinationMapper.class, (method, arguments) -> {
                        assertEquals("lock", method);
                        assertEquals(SCOPE.chatId(), arguments[0]);
                        return Optional.of(state);
                    });
            ChatMemoryCommitMapper commits = fake(ChatMemoryCommitMapper.class, (method, arguments) -> {
                assertEquals("selectByPrimaryKey", method);
                fail("sql");
                events.add("sql");
                return Optional.ofNullable(attempts.get((String) arguments[0]));
            });
            PlatformTransactionManager transactions =
                    fake(PlatformTransactionManager.class, (method, arguments) -> switch (method) {
                        case "getTransaction" -> new SimpleTransactionStatus();
                        case "commit", "rollback" -> null;
                        default -> throw new AssertionError("Unexpected transaction operation: " + method);
                    });
            vectors = new MemoryVectorRepository(
                    stores,
                    models,
                    new MemoryBoundsFactory(properties, models, new DefaultListableBeanFactory()),
                    properties);
            publisher = new MemoryPublisher(
                    new MemoryPublicationRepository(
                            coordination,
                            unused(ChatMemoryStateMapper.class),
                            commits,
                            unused(ChatHistoryMapper.class),
                            transactions,
                            properties),
                    vectors);
            codec = new MemoryDocumentCodec(bounds, properties);
        }

        void wideBudgets() {
            properties.setToolResultMaxTokens(100000);
            properties.setToolTotalMaxTokens(1000000);
        }

        MemoryRecallTools tools(int recallLimit) {
            return new MemoryRecallTools(SCOPE, recallLimit, vectors, publisher, bounds, properties, () -> {
                if (guardCalls.incrementAndGet() == failGuardAt) {
                    throw new IllegalStateException(SECRET, new IllegalArgumentException(SECRET));
                }
            });
        }

        MemoryDocument seed(int number, String summary) {
            return seed(number, SCOPE, Kind.EPISODE_SUMMARY, false, summary, DATE, 0.9);
        }

        MemoryDocument seed(
                int number, MemoryScope scope, Kind kind, boolean archive, String summary, Instant date, double score) {
            MemoryDocument doc = new MemoryDocument(
                    id(number),
                    scope,
                    id(number + 10000),
                    kind,
                    archive,
                    1,
                    2,
                    date,
                    "0".repeat(64),
                    summary,
                    List.of(),
                    List.of());
            records.put(doc.id(), codec.encode(doc));
            attempts.put(
                    doc.commitId(),
                    new ChatMemoryCommit()
                            .withAttemptId(doc.commitId())
                            .withChatId(scope.chatId())
                            .withGeneration(scope.generation())
                            .withStatus("committed")
                            .withOperation(
                                    switch (kind) {
                                        case WINDOW_SUMMARY -> "OVERFLOW";
                                        case EPISODE_SUMMARY -> "IDLE";
                                        case PROFILE_SNAPSHOT -> "REVALIDATE";
                                        case EXTRACTION_CHECKPOINT -> "CHECKPOINT";
                                    })
                            .withSchemaVersion(MemoryScope.SCHEMA_VERSION)
                            .withFingerprint(doc.fingerprint())
                            .withManifest(MemoryDocumentCodec.encodeManifest(MemoryManifest.of(List.of(doc), codec))));
            matches.add(match(doc.id(), score));
            return doc;
        }

        EmbeddingMatch<TextSegment> match(String id, double score) {
            return new EmbeddingMatch<>(score, id, VECTOR, TextSegment.from(SECRET));
        }

        JsonNode output(String value) throws Exception {
            outputs.add(value);
            assertFalse(value.contains(SECRET));
            assertTrue(MemoryBounds.bytes(value) <= MemoryBounds.TOOL_RESULT_BYTES);
            if (value.isEmpty()) {
                return JSON.createObjectNode();
            }
            assertTrue(bounds.tokens(value) <= properties.getToolResultMaxTokens());
            return JSON.readTree(value);
        }

        void code(String expected, String value) throws Exception {
            JsonNode result = output(value);
            assertEquals(expected, result.path("code").asText(), value);
            assertFalse(result.has("text"));
            assertFalse(result.has("results"));
        }

        void assertBudgets() {
            assertTrue(outputs.stream().mapToInt(MemoryBounds::bytes).sum() <= MemoryBounds.TOOL_TOTAL_BYTES);
            assertTrue(outputs.stream()
                            .filter(value -> !value.isEmpty())
                            .mapToLong(bounds::tokens)
                            .sum()
                    <= properties.getToolTotalMaxTokens());
        }

        void fail(String at) {
            if (failure.equals(at)) {
                throw new IllegalStateException(SECRET, new IllegalArgumentException(SECRET));
            }
        }
    }
}
