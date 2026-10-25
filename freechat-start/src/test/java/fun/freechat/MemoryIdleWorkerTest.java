package fun.freechat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.TokenCountEstimator;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.filter.Filter;
import fun.freechat.mapper.ChatHistoryMapper;
import fun.freechat.mapper.ChatMemoryCommitMapper;
import fun.freechat.mapper.ChatMemoryCoordinationMapper;
import fun.freechat.mapper.ChatMemoryStateMapper;
import fun.freechat.model.ChatHistory;
import fun.freechat.model.ChatMemoryCommit;
import fun.freechat.model.ChatMemoryState;
import fun.freechat.service.chat.memory.*;
import fun.freechat.service.chat.memory.MemoryWorkRepository.Claim;
import fun.freechat.service.enums.EmbeddingStoreType;
import fun.freechat.service.rag.EmbeddingModelService;
import fun.freechat.service.rag.ExactEmbeddingStoreService;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockMakers;
import org.mybatis.dynamic.sql.dsl.SelectDSLCompleter;
import org.mybatis.dynamic.sql.dsl.UpdateDSLCompleter;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.mybatis.dynamic.sql.update.render.UpdateStatementProvider;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.SimpleTransactionStatus;

/** Real final worker/repositories/consolidator; only nonfinal I/O boundaries use SUBCLASS mocks. */
@Timeout(30)
class MemoryIdleWorkerTest {
    private static final MemoryScope SCOPE =
            new MemoryScope("idle-chat", "owner", "character", 1, EmbeddingStoreType.EN_LONG_TERM_MEMORY);
    private static final String FINGERPRINT = "a".repeat(64);
    private static final String TOKEN = "00000000-0000-0000-0000-000000000001";
    private static final String PRIVATE = "private-provider-source-and-credential";
    private static final LocalDateTime NOW = LocalDateTime.of(2026, 9, 12, 10, 0);
    private static final com.fasterxml.jackson.databind.ObjectMapper JSON =
            new com.fasterxml.jackson.databind.ObjectMapper()
                    .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
    private final Fixture f = new Fixture();

    @AfterEach
    void stopOwnedWorkerAndClearTestInterrupt() {
        f.worker.close();
        Thread.interrupted();
    }

    @Test
    void initialCheckFailureStillFailsOwnedClaimWithoutScheduling() {
        f.state.setClaimLeaseUntil(NOW);
        assertDoesNotThrow(() -> f.worker.execute(f.claim()));
        f.finished(true);
        verifyNoInteractions(f.models, f.turns, f.boundsFactory);
        assertNull(f.renewalTask.get());
    }

    @Test
    void closedWorkerStillFailsClaimWithoutSchedulingOrResolving() {
        f.worker.close();
        assertDoesNotThrow(() -> f.worker.execute(f.claim()));
        f.finished(true);
        verifyNoInteractions(f.models, f.turns, f.boundsFactory);
        assertNull(f.renewalTask.get());
    }

    @Test
    void closeAfterInitialGuardRejectsRegistrationButStillCleansUp() {
        // Use the real executor: close from the first SQL check, after the guard's closed check.
        f.useRealRenewals();
        AtomicBoolean first = new AtomicBoolean(true);
        doAnswer(call -> {
                    if (first.getAndSet(false)) {
                        f.worker.close();
                    }
                    return Optional.of(f.state);
                })
                .when(f.coordination)
                .lock(SCOPE.chatId());
        assertDoesNotThrow(() -> f.worker.execute(f.claim()));
        f.finished(true);
        verifyNoInteractions(f.models, f.turns, f.boundsFactory);
    }

    @Test
    void abortedOnlyTurnAdvancesUnderGuardWithoutAnyModelOrConsolidatorPreparation() {
        f.abort();
        f.worker.execute(f.claim());
        f.finished(false);
        assertEquals(3L, f.state.getIdleThroughId());
        ChatMemoryCommit skip = f.onlyAttempt("SKIP_IDLE");
        assertEquals("skipped", skip.getStatus());
        assertEquals("none", skip.getModelId());
        assertEquals(TOKEN, skip.getLeaseToken());
        assertEquals(1L, skip.getSourceStartId());
        assertEquals(3L, skip.getSourceEndId());
        assertNull(f.state.getProfileId());
        verifyNoInteractions(f.models, f.boundsFactory, f.chat, f.stores, f.embedding);
        verify(f.turns, times(1)).finalizedPage(SCOPE, 0, 3, 1);
        verify(f.renewal).cancel(false);
    }

    @Test
    void abortSkipChecksWorkerAgainAfterReadingSnapshot() {
        f.abort();
        doAnswer(call -> {
                    f.worker.close();
                    return f.rows;
                })
                .when(f.coordination)
                .selectHistoryPage(any());
        f.worker.execute(f.claim());
        f.finished(true);
        assertEquals(0L, f.state.getIdleThroughId());
        assertTrue(f.attempts.isEmpty());
        verifyNoInteractions(f.models, f.boundsFactory);
    }

    @Test
    void mismatchedSnapshotNeverResolvesModel() {
        Claim claim = f.claim();
        f.state.setFingerprint("b".repeat(64));
        f.worker.execute(claim);
        // Changed work is released, without charging the stale claim's retry counter.
        f.finished(false);
        assertEquals(0L, f.state.getIdleThroughId());
        verifyNoInteractions(f.models, f.boundsFactory, f.chat);
    }

    @Test
    void staleResolvedFingerprintFailsBeforeOpeningConsolidatorOrBilledCalls() {
        doReturn(f.resolved("b".repeat(64))).when(f.models).resolve(SCOPE.chatId());
        f.worker.execute(f.claim());
        f.finished(true);
        verify(f.models).resolve(SCOPE.chatId());
        verifyNoInteractions(f.boundsFactory, f.chat, f.embedding, f.stores);
        assertTrue(f.attempts.isEmpty());
    }

    @Test
    void successfulWorkPublishesOneTurnThenReleasesAndCancelsRenewal() {
        f.worker.execute(f.claim());
        f.finished(false);
        assertEquals(3L, f.state.getIdleThroughId());
        assertNotNull(f.state.getProfileId());
        assertEquals("committed", f.onlyAttempt("IDLE").getStatus());
        assertEquals("terminal", f.onlyAttempt("CHECKPOINT_IDLE").getStatus());
        assertEquals("usage", f.onlyAttempt("USAGE").getStatus());
        verify(f.chat).chat(any(ChatRequest.class));
        verify(f.models).resolve(SCOPE.chatId());
        verify(f.renewal).cancel(false);
        int locks = mockingDetails(f.coordination).getInvocations().size();
        f.renewalTask.get().run(); // A queued heartbeat after completion must be a no-op.
        assertEquals(locks, mockingDetails(f.coordination).getInvocations().size());
        assertFalse(Thread.currentThread().isInterrupted());
    }

    @Test
    void fatalProviderFailureIsFailedAndPropagatedWithoutPrivateCauseOrSuppressedErrors() {
        Error original = new AssertionError(PRIVATE, new IllegalStateException(PRIVATE));
        original.addSuppressed(new IllegalStateException(PRIVATE));
        doAnswer(call -> {
                    Thread.currentThread().interrupt();
                    throw original;
                })
                .when(f.chat)
                .chat(any(ChatRequest.class));
        Error failure = assertThrows(Error.class, () -> f.worker.execute(f.claim()));
        assertEquals("Memory consolidation failed", failure.getMessage());
        assertNotSame(original, failure);
        assertNull(failure.getCause());
        assertEquals(0, failure.getSuppressed().length);
        f.finished(true);
        assertTrue(Thread.currentThread().isInterrupted());
        verify(f.renewal).cancel(false);
        assertTrue(f.attempts.isEmpty());
    }

    @Test
    void capacityWithoutResumableProgressSkipsValidatedRange() {
        doThrow(new MemoryBounds.CapacityException("Test capacity"))
                .when(f.boundsFactory)
                .forLanguage("en");
        f.worker.execute(f.claim());
        f.finished(false);
        verifyNoInteractions(f.chat);
        assertEquals(3L, f.state.getIdleThroughId());
        assertEquals("EXTRACTION", f.onlyAttempt("SKIP_IDLE").getErrorCategory());
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "resolution",
                "provider",
                "vector-lookup",
                "vector-read",
                "checkpoint-vector",
                "publication-vector"
            })
    void optionalFailureSkipsOnlySelectedTurnRetainsLastProfileAndAllowsNextTurn(String failure) {
        f.worker.execute(f.claim());
        String lastGoodProfile = f.state.getProfileId();
        assertNotNull(lastGoodProfile);
        f.nextTurn();
        String history = MemoryDocumentCodec.hash(JSON.valueToTree(f.rows).toString());
        switch (failure) {
            case "resolution" ->
                doThrow(new IllegalStateException(PRIVATE)).when(f.models).resolve(SCOPE.chatId());
            case "provider" ->
                doThrow(new IllegalStateException(PRIVATE)).when(f.chat).chat(any(ChatRequest.class));
            case "vector-lookup", "vector-read" -> f.failExternalIo(failure, new IllegalStateException(PRIVATE));
            case "checkpoint-vector" ->
                doThrow(new IllegalStateException(PRIVATE)).when(f.store).addAll(anyList(), anyList(), anyList());
            case "publication-vector" ->
                doAnswer(call -> {
                            List<String> ids = call.getArgument(0);
                            if (ids.size() > 1) {
                                throw new IllegalStateException(PRIVATE);
                            }
                            f.insertSegments(ids, call.getArgument(2));
                            return null;
                        })
                        .when(f.store)
                        .addAll(anyList(), anyList(), anyList());
            default -> fail("Unknown fixture");
        }
        f.worker.execute(f.claim());
        f.finished(false);
        assertEquals(6L, f.state.getIdleThroughId());
        assertEquals(lastGoodProfile, f.state.getProfileId());
        assertEquals(history, MemoryDocumentCodec.hash(JSON.valueToTree(f.rows).toString()));
        ChatMemoryCommit skip = f.onlyAttempt("SKIP_IDLE");
        assertEquals("EXTRACTION", skip.getErrorCategory());
        assertEquals("skipped", skip.getStatus());
        assertEquals("{}", skip.getManifest());
        assertEquals(4L, skip.getSourceStartId());
        assertEquals(6L, skip.getSourceEndId());
        assertTrue(f.attempts.values().stream().noneMatch(row -> "checkpoint".equals(row.getStatus())));
        assertTrue(mockingDetails(f.store).getInvocations().stream()
                .noneMatch(call -> call.getMethod().getName().startsWith("remove")));
        if (failure.equals("publication-vector")) {
            ChatMemoryCommit failed = f.attempts.values().stream()
                    .filter(row -> "IDLE".equals(row.getOperation()) && "terminal".equals(row.getStatus()))
                    .findFirst()
                    .orElseThrow();
            assertNotEquals(failed.getAttemptId(), skip.getAttemptId());
        }
        doAnswer(call -> f.resolved(FINGERPRINT)).when(f.models).resolve(SCOPE.chatId());
        doAnswer(call -> f.response()).when(f.chat).chat(any(ChatRequest.class));
        doAnswer(call -> f.readSegment(call.getArgument(1), call.getArgument(2)))
                .when(f.stores)
                .get(eq(SCOPE.storeType()), anyString(), any(Filter.class));
        doAnswer(call -> {
                    f.insertSegments(call.getArgument(0), call.getArgument(2));
                    return null;
                })
                .when(f.store)
                .addAll(anyList(), anyList(), anyList());
        f.nextTurn();
        f.worker.execute(f.claim());
        f.finished(false);
        assertEquals(9L, f.state.getIdleThroughId());
        assertNotEquals(lastGoodProfile, f.state.getProfileId());
        assertEquals(
                1,
                f.attempts.values().stream()
                        .filter(row -> "SKIP_IDLE".equals(row.getOperation()))
                        .count());
    }

    @Test
    void cancelledPublicationTransactionRollsBackAndDoesNotBecomeASkippableFailure() {
        doThrow(new java.util.concurrent.CancellationException(PRIVATE))
                .when(f.coordination)
                .databaseNow();
        var failure = assertThrows(
                java.util.concurrent.CancellationException.class,
                () -> f.publications.snapshot(SCOPE, MemoryPublicationRepository.Operation.IDLE, 3, TOKEN));
        assertEquals("Memory publication transaction cancelled", failure.getMessage());
        assertNull(failure.getCause());
        assertEquals(0, failure.getSuppressed().length);
        verify(f.transactions).rollback(any());
        assertEquals(0L, f.state.getIdleThroughId());
    }

    @Test
    void failedRevalidationDropsIncompatibleProfileWithoutAdvancingCursor() {
        f.worker.execute(f.claim());
        f.resetCleanup();
        f.state.setFingerprint("b".repeat(64));
        f.state.setProfileRevalidationPending((byte) 1);
        String history = MemoryDocumentCodec.hash(JSON.valueToTree(f.rows).toString());
        doThrow(new IllegalStateException(PRIVATE)).when(f.models).resolve(SCOPE.chatId());
        f.worker.execute(f.claim());
        f.finished(false);
        assertNull(f.state.getProfileId());
        assertEquals((byte) 0, f.state.getProfileRevalidationPending());
        assertEquals(3L, f.state.getIdleThroughId());
        assertEquals(0L, f.state.getOverflowThroughId());
        assertEquals(history, MemoryDocumentCodec.hash(JSON.valueToTree(f.rows).toString()));
        assertEquals("EXTRACTION", f.onlyAttempt("SKIP_REVALIDATE").getErrorCategory());
    }

    @ParameterizedTest
    @ValueSource(strings = {"cancelled", "provider-cancelled", "interrupted", "stopped"})
    void cancellationAfterValidatedSnapshotNeverSkips(String cancellation) {
        if (cancellation.equals("cancelled")) {
            doThrow(new java.util.concurrent.CancellationException(PRIVATE))
                    .when(f.models)
                    .resolve(SCOPE.chatId());
        } else if (cancellation.equals("provider-cancelled")) {
            doThrow(new java.util.concurrent.CancellationException(PRIVATE))
                    .when(f.chat)
                    .chat(any(ChatRequest.class));
        } else {
            doAnswer(call -> {
                        if (cancellation.equals("interrupted")) {
                            Thread.currentThread().interrupt();
                        } else {
                            f.worker.close();
                        }
                        throw new IllegalStateException(PRIVATE);
                    })
                    .when(f.chat)
                    .chat(any(ChatRequest.class));
        }
        f.worker.execute(f.claim());
        f.finished(true);
        assertEquals(0L, f.state.getIdleThroughId());
        assertTrue(
                f.attempts.values().stream().noneMatch(row -> row.getOperation().startsWith("SKIP_")));
        assertEquals(cancellation.equals("interrupted"), Thread.currentThread().isInterrupted());
    }

    @ParameterizedTest
    @CsvSource({
        "vector-lookup, false",
        "vector-read, false",
        "checkpoint-vector, false",
        "checkpoint-read, false",
        "publication-vector, false",
        "publication-read, false",
        "vector-lookup, true",
        "vector-read, true",
        "publication-vector, true",
        "publication-read, true"
    })
    void cancelledVectorIoNeverConsumesSourceOrClearsProfile(String boundary, boolean revalidation) {
        f.worker.execute(f.claim());
        String lastGoodProfile = f.state.getProfileId();
        assertNotNull(lastGoodProfile);
        if (revalidation) {
            f.resetCleanup();
            f.state.setFingerprint("b".repeat(64));
            f.state.setProfileRevalidationPending((byte) 1);
            doAnswer(call -> f.resolved(f.state.getFingerprint()))
                    .when(f.models)
                    .resolve(SCOPE.chatId());
        } else {
            f.nextTurn();
        }
        String history = MemoryDocumentCodec.hash(JSON.valueToTree(f.rows).toString());
        List<String> priorAttempts = List.copyOf(f.attempts.keySet());
        f.failExternalIo(boundary, privateCancellation());

        assertDoesNotThrow(() -> f.worker.execute(f.claim()));

        f.finished(true);
        assertEquals(3L, f.state.getIdleThroughId());
        assertEquals(0L, f.state.getOverflowThroughId());
        assertEquals(lastGoodProfile, f.state.getProfileId());
        assertEquals((byte) (revalidation ? 1 : 0), f.state.getProfileRevalidationPending());
        assertEquals(history, MemoryDocumentCodec.hash(JSON.valueToTree(f.rows).toString()));
        assertTrue(
                f.attempts.values().stream().noneMatch(row -> row.getOperation().startsWith("SKIP_")));
        assertFalse(Thread.currentThread().isInterrupted(), "Cancellation must survive without an interrupt flag");
        List<ChatMemoryCommit> newAttempts = f.attempts.values().stream()
                .filter(row -> !priorAttempts.contains(row.getAttemptId()))
                .toList();
        assertTrue(newAttempts.stream().noneMatch(row -> "prepared".equals(row.getStatus())));
        if (boundary.startsWith("checkpoint") || boundary.startsWith("publication")) {
            String operation =
                    boundary.startsWith("checkpoint") ? "CHECKPOINT_IDLE" : revalidation ? "REVALIDATE" : "IDLE";
            ChatMemoryCommit terminated = newAttempts.stream()
                    .filter(row -> operation.equals(row.getOperation()))
                    .findFirst()
                    .orElseThrow();
            assertEquals("terminal", terminated.getStatus());
            assertEquals("VECTOR_WRITE", terminated.getErrorCategory());
        }
        if (!revalidation && boundary.startsWith("publication")) {
            assertTrue(
                    newAttempts.stream().anyMatch(row -> "checkpoint".equals(row.getStatus())),
                    "Cancellation must retain resumable checkpoint progress");
        }
        assertFalse(JSON.valueToTree(f.attempts).toString().contains(PRIVATE));
        assertFalse(JSON.valueToTree(f.state).toString().contains(PRIVATE));
        assertTrue(mockingDetails(f.store).getInvocations().stream()
                .noneMatch(call -> call.getMethod().getName().startsWith("remove")));
    }

    @ParameterizedTest
    @CsvSource({
        "provider, Memory extraction request cancelled",
        "vector-lookup, Committed memory read cancelled",
        "vector-read, Committed memory read cancelled",
        "checkpoint-vector, Memory checkpoint operation cancelled",
        "checkpoint-read, Memory checkpoint operation cancelled",
        "publication-vector, Memory publication cancelled",
        "publication-read, Memory publication cancelled"
    })
    void externalCancellationKeepsItsTypeWithoutPrivateCauseOrSuppressedErrors(String boundary, String message) {
        f.worker.execute(f.claim());
        f.nextTurn();
        var snapshot = f.publications.snapshot(SCOPE, MemoryPublicationRepository.Operation.IDLE, 6, TOKEN);
        CancellationException original = privateCancellation();
        f.failExternalIo(boundary, original);

        CancellationException failure = assertThrows(
                CancellationException.class,
                () -> f.consolidator
                        .open(f.resolved(FINGERPRINT), 2, Duration.ofMinutes(1), () -> {})
                        .consolidate(snapshot));

        assertSanitizedCancellation(original, failure, message);
        assertEquals(3L, f.state.getIdleThroughId());
        assertTrue(
                f.attempts.values().stream().noneMatch(row -> row.getOperation().startsWith("SKIP_")));
    }

    @Test
    void cancelledCheckpointResumeReadRetainsProgressWithoutConsumingSource() {
        f.properties.setJobMaxDuration(Duration.ofMinutes(1));
        f.properties.setExtractionTimeout(Duration.ofMinutes(1));
        f.rows.get(1).setMessage(ChatMessageSerializer.messageToJson(UserMessage.from("evidence ".repeat(40000))));
        f.worker.execute(f.claim());
        f.finished(false);
        ChatMemoryCommit checkpoint = f.onlyAttempt("CHECKPOINT_IDLE");
        assertEquals("checkpoint", checkpoint.getStatus());
        f.resetCleanup();
        CancellationException original = privateCancellation();
        f.failExternalIo("vector-lookup", original);
        var snapshot = f.publications.snapshot(SCOPE, MemoryPublicationRepository.Operation.IDLE, 3, TOKEN);

        CancellationException failure = assertThrows(
                CancellationException.class,
                () -> f.consolidator
                        .open(f.resolved(FINGERPRINT), 1, Duration.ofMinutes(1), () -> {})
                        .consolidate(snapshot));
        assertSanitizedCancellation(original, failure, "Memory checkpoint operation cancelled");
        f.worker.execute(f.claim());

        f.finished(true);
        assertEquals(0L, f.state.getIdleThroughId());
        assertEquals(0L, f.state.getOverflowThroughId());
        assertNull(f.state.getProfileId());
        assertEquals("checkpoint", checkpoint.getStatus());
        assertTrue(
                f.attempts.values().stream().noneMatch(row -> row.getOperation().startsWith("SKIP_")));
        assertFalse(JSON.valueToTree(f.attempts).toString().contains(PRIVATE));
        verify(f.chat, times(1)).chat(any(ChatRequest.class));
    }

    @Test
    void ambiguousPublicationAlreadyCommittedIsNotAbandonedOrChargedToNewWork() {
        AtomicBoolean ambiguous = new AtomicBoolean(true);
        doAnswer(call -> {
                    f.transactionFinished();
                    if (f.attempts.values().stream()
                                    .anyMatch(row ->
                                            "IDLE".equals(row.getOperation()) && "committed".equals(row.getStatus()))
                            && ambiguous.getAndSet(false)) {
                        throw new IllegalStateException(PRIVATE);
                    }
                    return null;
                })
                .when(f.transactions)
                .commit(any());
        f.worker.execute(f.claim());
        f.finished(false);
        assertEquals(3L, f.state.getIdleThroughId());
        assertNotNull(f.state.getProfileId());
        assertEquals("committed", f.onlyAttempt("IDLE").getStatus());
        assertTrue(
                f.attempts.values().stream().noneMatch(row -> row.getOperation().startsWith("SKIP_")));
    }

    @Test
    void capacityWithoutCheckpointProgressChargesRetryAndNeverRepairsBeyondCallBudget() {
        f.properties.setJobMaxDuration(Duration.ofMinutes(1));
        f.properties.setExtractionTimeout(Duration.ofMinutes(1));
        doReturn(ChatResponse.builder().aiMessage(AiMessage.from("not-json")).build())
                .when(f.chat)
                .chat(any(ChatRequest.class));
        f.worker.execute(f.claim());
        f.finished(true);
        verify(f.chat, times(1)).chat(any(ChatRequest.class));
        assertEquals(0L, f.state.getIdleThroughId());
        assertEquals(1, f.attempts.size());
        assertEquals("usage", f.onlyAttempt("USAGE").getStatus());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3})
    void capacityAfterDurableProgressReleasesWithoutRetryAndHonorsCallBound(int calls) {
        f.properties.setJobMaxDuration(Duration.ofMinutes(calls));
        f.properties.setExtractionTimeout(Duration.ofMinutes(1));
        // More than one batch of evidence; each bounded model result is checkpointed by real services.
        f.rows.get(1).setMessage(ChatMessageSerializer.messageToJson(UserMessage.from("evidence ".repeat(40000))));
        f.worker.execute(f.claim());
        f.finished(false);
        verify(f.chat, times(calls)).chat(any(ChatRequest.class));
        assertEquals(0L, f.state.getIdleThroughId());
        assertNull(f.state.getProfileId());
        assertEquals(
                calls,
                f.attempts.values().stream()
                        .filter(row -> "USAGE".equals(row.getOperation()))
                        .count());
        List<ChatMemoryCommit> checkpoints = f.attempts.values().stream()
                .filter(row -> "checkpoint".equals(row.getStatus()))
                .toList();
        assertEquals(1, checkpoints.size());
        assertEquals("CHECKPOINT_IDLE", checkpoints.getFirst().getOperation());
        assertFalse(f.segments.isEmpty());
        assertTrue(f.attempts.values().stream().noneMatch(row -> "IDLE".equals(row.getOperation())));
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void capacityOrCancellationAfterCheckpointPreservesResumableProgress(boolean cancelled) {
        f.rows.get(1).setMessage(ChatMessageSerializer.messageToJson(UserMessage.from("evidence ".repeat(40000))));
        if (cancelled) {
            AtomicBoolean first = new AtomicBoolean(true);
            doAnswer(call -> {
                        if (first.getAndSet(false)) {
                            return f.response();
                        }
                        throw new java.util.concurrent.CancellationException(PRIVATE);
                    })
                    .when(f.chat)
                    .chat(any(ChatRequest.class));
        } else {
            doAnswer(call -> {
                        if (f.attempts.values().stream().anyMatch(row -> "checkpoint".equals(row.getStatus()))) {
                            throw new MemoryBounds.CapacityException("Test capacity after progress");
                        }
                        return f.page(call.getArgument(1), call.getArgument(2), call.getArgument(3), false);
                    })
                    .when(f.turns)
                    .sourcePage(eq(SCOPE), anyLong(), anyLong(), anyInt());
        }
        f.worker.execute(f.claim());
        f.finished(cancelled);
        assertEquals(0L, f.state.getIdleThroughId());
        assertEquals("checkpoint", f.onlyAttempt("CHECKPOINT_IDLE").getStatus());
        assertTrue(
                f.attempts.values().stream().noneMatch(row -> row.getOperation().startsWith("SKIP_")));
    }

    @Test
    void lostRenewalStopsBeforeConsolidationEvenIfProviderResolutionClearsInterrupt() {
        doAnswer(call -> {
                    f.state.setClaimLeaseUntil(NOW);
                    f.renewalTask.get().run();
                    assertTrue(Thread.interrupted()); // An I/O boundary consumes the interruption.
                    f.state.setClaimLeaseUntil(NOW.plusMinutes(5));
                    return f.resolved(FINGERPRINT);
                })
                .when(f.models)
                .resolve(SCOPE.chatId());
        f.worker.execute(f.claim());
        f.finished(true);
        verifyNoInteractions(f.boundsFactory, f.chat);
        verify(f.renewal).cancel(false);
    }

    @Test
    void renewalLossDuringConsolidationRestoresInterruptOnlyAfterSqlCleanup() {
        doAnswer(call -> {
                    f.state.setClaimLeaseUntil(NOW);
                    f.renewalTask.get().run();
                    return f.response();
                })
                .when(f.chat)
                .chat(any(ChatRequest.class));
        f.worker.execute(f.claim());
        f.finished(true);
        assertTrue(Thread.currentThread().isInterrupted());
        assertEquals(0L, f.state.getIdleThroughId());
        assertEquals("usage", f.onlyAttempt("USAGE").getStatus());
        verifyNoInteractions(f.stores, f.embedding);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void cleanupFailureIsContainedAndInterruptRestoredAfterTransactionFinishes(boolean failurePath) {
        f.cleanupFailure = true;
        if (failurePath) {
            Thread.currentThread().interrupt();
        } else {
            f.abort();
            doAnswer(call -> {
                        Thread.currentThread().interrupt();
                        return 1;
                    })
                    .when(f.states)
                    .updateByPrimaryKey(any(ChatMemoryState.class));
        }
        assertDoesNotThrow(() -> f.worker.execute(f.claim()));
        f.finished(failurePath);
        assertTrue(Thread.currentThread().isInterrupted());
        verify(f.transactions).rollback(any());
        if (failurePath) {
            verifyNoInteractions(f.models, f.turns);
        }
    }

    @Test
    void inFlightRenewalCannotInterruptSqlCleanupOrRunAgainAfterFinish() throws Exception {
        CountDownLatch renewalEntered = new CountDownLatch(1);
        CountDownLatch allowRenewalFailure = new CountDownLatch(1);
        CountDownLatch consolidationReturning = new CountDownLatch(1);
        AtomicReference<Thread> heartbeat = new AtomicReference<>();
        doAnswer(call -> {
                    if (Thread.currentThread() == heartbeat.get()) {
                        renewalEntered.countDown();
                        await(allowRenewalFailure);
                        throw new IllegalStateException(PRIVATE);
                    }
                    return Optional.of(f.state);
                })
                .when(f.coordination)
                .lock(SCOPE.chatId());
        doAnswer(call -> {
                    Thread thread = Thread.ofPlatform().unstarted(f.renewalTask.get());
                    heartbeat.set(thread);
                    thread.start();
                    await(renewalEntered);
                    consolidationReturning.countDown();
                    throw new IllegalStateException("Test resolution stopped");
                })
                .when(f.models)
                .resolve(SCOPE.chatId());
        FutureTask<Boolean> execution = new FutureTask<>(() -> {
            f.worker.execute(f.claim());
            return Thread.currentThread().isInterrupted();
        });
        Thread worker = Thread.ofPlatform().start(execution);
        try {
            await(consolidationReturning);
            long until = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
            while (worker.getState() != Thread.State.BLOCKED && !execution.isDone() && System.nanoTime() < until) {
                Thread.onSpinWait();
            }
            assertEquals(
                    Thread.State.BLOCKED, worker.getState(), "Cleanup must wait for the in-flight heartbeat monitor");
            assertTrue(f.finishes.isEmpty());
        } finally {
            allowRenewalFailure.countDown();
        }
        assertTrue(execution.get(5, TimeUnit.SECONDS));
        heartbeat.get().join(5000);
        assertFalse(heartbeat.get().isAlive());
        f.finished(true);
        f.renewalTask.get().run();
        assertFalse(Thread.currentThread().isInterrupted());
        verify(f.renewal).cancel(false);
    }

    private static void await(CountDownLatch latch) {
        try {
            assertTrue(latch.await(5, TimeUnit.SECONDS), "Expected controlled worker event");
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
            throw new AssertionError("Unexpected test interruption");
        }
    }

    private static <T> T fake(Class<T> type) {
        return mock(type, withSettings().mockMaker(MockMakers.SUBCLASS));
    }

    private static CancellationException privateCancellation() {
        CancellationException failure = new CancellationException(PRIVATE);
        failure.initCause(new IllegalStateException(PRIVATE));
        failure.addSuppressed(new IllegalStateException(PRIVATE));
        return failure;
    }

    private static void assertSanitizedCancellation(
            CancellationException original, CancellationException failure, String message) {
        assertEquals(CancellationException.class, failure.getClass());
        assertEquals(message, failure.getMessage());
        assertNotSame(original, failure);
        assertNull(failure.getCause());
        assertEquals(0, failure.getSuppressed().length);
    }

    private static final class Fixture {
        final LongTermMemoryProperties properties = new LongTermMemoryProperties();
        final ChatMemoryCoordinationMapper coordination = fake(ChatMemoryCoordinationMapper.class);
        final ChatMemoryStateMapper states = fake(ChatMemoryStateMapper.class);
        final ChatMemoryCommitMapper commits = fake(ChatMemoryCommitMapper.class);
        final ChatHistoryMapper histories = fake(ChatHistoryMapper.class);
        final PlatformTransactionManager transactions = fake(PlatformTransactionManager.class);
        final MemoryTurnRepository turns = fake(MemoryTurnRepository.class);
        final MemoryModelResolver models = fake(MemoryModelResolver.class);
        final ChatModel chat = fake(ChatModel.class);
        final EmbeddingModelService embeddingModels = fake(EmbeddingModelService.class);
        final EmbeddingModel embedding = fake(EmbeddingModel.class);
        final ExactEmbeddingStoreService stores = fake(ExactEmbeddingStoreService.class);

        @SuppressWarnings("unchecked")
        final EmbeddingStore<TextSegment> store = fake(EmbeddingStore.class);

        final ScheduledExecutorService scheduler = fake(ScheduledExecutorService.class);
        final ScheduledFuture<?> renewal = fake(ScheduledFuture.class);
        final AtomicReference<Runnable> renewalTask = new AtomicReference<>();
        final List<UpdateStatementProvider> finishes = new ArrayList<>();
        final Map<String, ChatMemoryCommit> attempts = new HashMap<>();
        final Map<String, TextSegment> segments = new HashMap<>();
        final List<ChatHistory> rows = new ArrayList<>(List.of(
                row(1, "turn-start"),
                row(2, "message")
                        .withMessageOrigin("user-input")
                        .withMessage(ChatMessageSerializer.messageToJson(UserMessage.from("A remembered preference"))),
                row(3, "turn-complete")));
        final ChatMemoryState state = new ChatMemoryState()
                .withChatId(SCOPE.chatId())
                .withUserId(SCOPE.userId())
                .withCharacterUid(SCOPE.characterUid())
                .withGeneration(SCOPE.generation())
                .withStoreType(SCOPE.storeType().text())
                .withStatus("active")
                .withFingerprint(FINGERPRINT)
                .withVersion(0L)
                .withLatestFinalizedId(3L)
                .withIdleThroughId(0L)
                .withOverflowThroughId(0L)
                .withProfileRevalidationPending((byte) 0)
                .withClaimToken(TOKEN)
                .withClaimLeaseUntil(NOW.plusMinutes(5))
                .withClaimDeadline(NOW.plusHours(1))
                .withRetryAttempts(0)
                .withLastActivity(NOW);
        final MemoryBoundsFactory boundsFactory;
        final MemoryPublicationRepository publications;
        final MemoryConsolidator consolidator;
        final MemoryIdleWorker worker;
        final ScheduledExecutorService realRenewals;
        boolean cleanupFailure;
        boolean cleaning;
        int cleanupTransactionsFinished;

        Fixture() {
            properties.afterPropertiesSet();
            when(transactions.getTransaction(any())).thenAnswer(call -> {
                if (cleaning) {
                    assertFalse(Thread.currentThread().isInterrupted());
                }
                return new SimpleTransactionStatus();
            });
            doAnswer(call -> {
                        transactionFinished();
                        return null;
                    })
                    .when(transactions)
                    .commit(any());
            doAnswer(call -> {
                        transactionFinished();
                        return null;
                    })
                    .when(transactions)
                    .rollback(any());
            when(coordination.lock(SCOPE.chatId())).thenReturn(Optional.of(state));
            when(coordination.databaseNow()).thenReturn(NOW);
            when(coordination.retainControl(any())).thenAnswer(call -> {
                ChatMemoryCommit row = call.getArgument(0);
                doReturn(Optional.of(row.getProgress())).when(coordination).scopeProgress(row.getAttemptId());
                return 1;
            });
            doCallRealMethod().when(states).update(any(UpdateDSLCompleter.class));
            when(states.update(any(UpdateStatementProvider.class))).thenAnswer(call -> {
                UpdateStatementProvider update = call.getArgument(0);
                if (update.getUpdateStatement().contains("claim_token = null")) {
                    cleaning = true;
                    assertFalse(
                            Thread.currentThread().isInterrupted(),
                            "SQL claim cleanup must run with interrupt cleared");
                    finishes.add(update);
                    if (cleanupFailure) {
                        throw new IllegalStateException(PRIVATE);
                    }
                }
                return 1;
            });
            when(states.updateByPrimaryKey(any(ChatMemoryState.class))).thenReturn(1);
            when(turns.sourcePage(eq(SCOPE), anyLong(), anyLong(), anyInt()))
                    .thenAnswer(call -> page(call.getArgument(1), call.getArgument(2), call.getArgument(3), false));
            when(turns.finalizedPage(eq(SCOPE), anyLong(), anyLong(), anyInt()))
                    .thenAnswer(call -> page(call.getArgument(1), call.getArgument(2), call.getArgument(3), true));
            when(histories.selectMany(any(SelectStatementProvider.class)))
                    .thenAnswer(call -> rows.stream()
                            .map(row -> new ChatHistory().withId(row.getId()))
                            .toList());
            when(coordination.selectHistoryPage(any())).thenAnswer(call -> rows);
            when(histories.selectByPrimaryKey(anyLong()))
                    .thenAnswer(call -> rows.stream()
                            .filter(row -> row.getId().equals(call.getArgument(0)))
                            .findFirst());
            when(commits.insertSelective(any(ChatMemoryCommit.class))).thenAnswer(call -> {
                ChatMemoryCommit row = call.getArgument(0);
                assertNull(attempts.putIfAbsent(row.getAttemptId(), row));
                return 1;
            });
            when(commits.selectByPrimaryKey(anyString()))
                    .thenAnswer(call -> Optional.ofNullable(attempts.get(call.getArgument(0))));
            doCallRealMethod().when(commits).selectOne(any(SelectDSLCompleter.class));
            when(commits.selectOne(any(SelectStatementProvider.class))).thenAnswer(call -> {
                SelectStatementProvider query = call.getArgument(0);
                return attempts.values().stream()
                        .filter(row -> query.getParameters().containsValue(row.getAttemptId()))
                        .findFirst();
            });
            when(commits.select(any(SelectDSLCompleter.class)))
                    .thenAnswer(call -> attempts.values().stream()
                            .filter(row -> "checkpoint".equals(row.getStatus()))
                            .toList());
            when(commits.updateByPrimaryKey(any(ChatMemoryCommit.class))).thenReturn(1);
            when(chat.chat(any(ChatRequest.class))).thenAnswer(call -> response());
            when(models.resolve(SCOPE.chatId())).thenAnswer(call -> resolved(FINGERPRINT));
            when(embeddingModels.tokenCountEstimatorForLang("en")).thenReturn(new TokenCountEstimator() {
                public int estimateTokenCountInText(String text) {
                    return Math.max(1, (text.length() + 3) / 4);
                }

                public int estimateTokenCountInMessage(ChatMessage message) {
                    return estimateTokenCountInText(ChatMessageSerializer.messageToJson(message));
                }

                public int estimateTokenCountInMessages(Iterable<ChatMessage> messages) {
                    int count = 0;
                    for (ChatMessage message : messages) {
                        count += estimateTokenCountInMessage(message);
                    }
                    return count;
                }
            });
            MemoryBoundsFactory realBounds =
                    new MemoryBoundsFactory(properties, embeddingModels, new DefaultListableBeanFactory());
            boundsFactory = mock(
                    MemoryBoundsFactory.class,
                    withSettings()
                            .mockMaker(MockMakers.SUBCLASS)
                            .defaultAnswer(org.mockito.AdditionalAnswers.delegatesTo(realBounds)));
            when(embeddingModels.modelForLang("en")).thenReturn(embedding);
            when(embedding.embedAll(anyList())).thenAnswer(call -> {
                List<TextSegment> input = call.getArgument(0);
                return Response.from(input.stream()
                        .map(ignored -> Embedding.from(new float[] {1, 0}))
                        .toList());
            });
            when(stores.of(SCOPE.chatId(), SCOPE.storeType())).thenReturn(store);
            doAnswer(call -> {
                        insertSegments(call.getArgument(0), call.getArgument(2));
                        return null;
                    })
                    .when(store)
                    .addAll(anyList(), anyList(), anyList());
            when(stores.get(eq(SCOPE.storeType()), anyString(), any(Filter.class)))
                    .thenAnswer(call -> readSegment(call.getArgument(1), call.getArgument(2)));
            publications =
                    new MemoryPublicationRepository(coordination, states, commits, histories, transactions, properties);
            MemorySourceReader sources = new MemorySourceReader(turns);
            MemoryVectorRepository vectors =
                    new MemoryVectorRepository(stores, embeddingModels, boundsFactory, properties);
            consolidator = new MemoryConsolidator(
                    sources,
                    publications,
                    new MemoryPublisher(publications, vectors),
                    new MemoryCheckpoints(publications, vectors),
                    boundsFactory,
                    properties);
            worker = new MemoryIdleWorker(
                    new MemoryWorkRepository(coordination, states, transactions, properties),
                    sources,
                    publications,
                    models,
                    consolidator,
                    properties);
            realRenewals = (ScheduledExecutorService) ReflectionTestUtils.getField(worker, "renewals");
            realRenewals.shutdownNow();
            ReflectionTestUtils.setField(worker, "renewals", scheduler);
            doAnswer(call -> {
                        assertEquals(properties.getLeaseRenewInterval().toMillis(), (long) call.getArgument(1));
                        assertEquals(properties.getLeaseRenewInterval().toMillis(), (long) call.getArgument(2));
                        renewalTask.set(call.getArgument(0));
                        return renewal;
                    })
                    .when(scheduler)
                    .scheduleWithFixedDelay(any(Runnable.class), anyLong(), anyLong(), eq(TimeUnit.MILLISECONDS));
        }

        void useRealRenewals() {
            // Already shut down: the close/check race must reach and reject real scheduling.
            ReflectionTestUtils.setField(worker, "renewals", realRenewals);
        }

        void transactionFinished() {
            if (cleaning) {
                assertFalse(Thread.currentThread().isInterrupted(), "Restore interrupt only after SQL commit/rollback");
                cleanupTransactionsFinished++;
                cleaning = false;
            }
        }

        void finished(boolean failed) {
            assertEquals(1, finishes.size(), "Exactly one fenced SQL claim cleanup");
            UpdateStatementProvider update = finishes.getFirst();
            assertEquals(failed, update.getUpdateStatement().contains("retry_attempts ="));
            assertTrue(update.getUpdateStatement().contains("claim_lease_until = null"));
            assertTrue(update.getUpdateStatement().contains("claim_deadline = null"));
            assertTrue(update.getParameters().containsValue(SCOPE.chatId()));
            if (failed) {
                assertTrue(update.getParameters().containsValue(1));
            }
            assertEquals(1, cleanupTransactionsFinished);
        }

        Claim claim() {
            return new Claim(
                    SCOPE,
                    TOKEN,
                    NOW.plusHours(1),
                    state.getLatestFinalizedId(),
                    state.getProfileRevalidationPending() == 1,
                    state.getFingerprint(),
                    state.getIdleThroughId(),
                    NOW,
                    state.getProfileId());
        }

        void resetCleanup() {
            finishes.clear();
            cleanupTransactionsFinished = 0;
        }

        void nextTurn() {
            resetCleanup();
            long start = rows.getLast().getId() + 1;
            rows.add(row(start, "turn-start"));
            rows.add(row(start + 1, "message")
                    .withMessageOrigin("user-input")
                    .withMessage(ChatMessageSerializer.messageToJson(UserMessage.from("Next remembered preference"))));
            rows.add(row(start + 2, "turn-complete"));
            state.setLatestFinalizedId(start + 2);
        }

        void insertSegments(List<String> ids, List<TextSegment> values) {
            for (int i = 0; i < ids.size(); i++) {
                segments.put(ids.get(i), values.get(i));
            }
        }

        Optional<TextSegment> readSegment(String id, Filter filter) {
            return Optional.ofNullable(segments.get(id)).filter(segment -> filter.test(segment.metadata()));
        }

        void failExternalIo(String boundary, RuntimeException failure) {
            switch (boundary) {
                case "provider" -> doThrow(failure).when(chat).chat(any(ChatRequest.class));
                case "vector-lookup" ->
                    doThrow(failure).when(stores).get(eq(SCOPE.storeType()), anyString(), any(Filter.class));
                case "vector-read" -> {
                    AtomicBoolean lookup = new AtomicBoolean(true);
                    doAnswer(call -> {
                                if (!lookup.getAndSet(false)) {
                                    throw failure;
                                }
                                return readSegment(call.getArgument(1), call.getArgument(2));
                            })
                            .when(stores)
                            .get(eq(SCOPE.storeType()), anyString(), any(Filter.class));
                }
                case "checkpoint-vector", "publication-vector" ->
                    doAnswer(call -> {
                                List<TextSegment> values = call.getArgument(2);
                                boolean checkpoint = values.getFirst()
                                        .metadata()
                                        .getString("record_kind")
                                        .equals(MemoryDocument.Kind.EXTRACTION_CHECKPOINT.name());
                                if (checkpoint == boundary.equals("checkpoint-vector")) {
                                    throw failure;
                                }
                                insertSegments(call.getArgument(0), values);
                                return null;
                            })
                            .when(store)
                            .addAll(anyList(), anyList(), anyList());
                case "checkpoint-read", "publication-read" ->
                    doAnswer(call -> {
                                Optional<TextSegment> found = readSegment(call.getArgument(1), call.getArgument(2));
                                if (found.isPresent()) {
                                    var metadata = found.orElseThrow().metadata();
                                    boolean checkpoint = metadata.getString("record_kind")
                                            .equals(MemoryDocument.Kind.EXTRACTION_CHECKPOINT.name());
                                    if (checkpoint == boundary.equals("checkpoint-read")
                                            && "prepared"
                                                    .equals(attempts.get(metadata.getString("commit_id"))
                                                            .getStatus())) {
                                        throw failure;
                                    }
                                }
                                return found;
                            })
                            .when(stores)
                            .get(eq(SCOPE.storeType()), anyString(), any(Filter.class));
                default -> fail("Unknown external I/O boundary");
            }
        }

        MemoryModelResolver.Resolved resolved(String fingerprint) {
            return new MemoryModelResolver.Resolved(
                    chat,
                    "deterministic-test",
                    "en",
                    "",
                    "",
                    fingerprint,
                    SCOPE.characterUid(),
                    SCOPE.userId(),
                    10,
                    10,
                    null);
        }

        ChatResponse response() {
            return ChatResponse.builder()
                    .aiMessage(AiMessage.from(
                            "{\"summary\":\"A remembered preference\",\"userFacts\":[],\"characterDeltas\":[]}"))
                    .build();
        }

        void abort() {
            rows.getLast().setRecordKind("turn-abort");
        }

        ChatMemoryCommit onlyAttempt(String operation) {
            List<ChatMemoryCommit> found = attempts.values().stream()
                    .filter(row -> operation.equals(row.getOperation()))
                    .toList();
            assertEquals(1, found.size());
            return found.getFirst();
        }

        List<ChatHistory> page(long after, long through, int limit, boolean terminal) {
            return rows.stream()
                    .filter(row -> row.getId() > after && row.getId() <= through)
                    .filter(row ->
                            !terminal || List.of("turn-complete", "turn-abort").contains(row.getRecordKind()))
                    .limit(limit)
                    .toList();
        }

        static ChatHistory row(long id, String kind) {
            return new ChatHistory()
                    .withId(id)
                    .withMemoryId(SCOPE.chatId())
                    .withEnabled((byte) 1)
                    .withTurnId(TOKEN)
                    .withEpisode(1L)
                    .withGmtCreate(NOW)
                    .withRecordKind(kind);
        }
    }
}
