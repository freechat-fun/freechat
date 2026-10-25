package fun.freechat.service.chat.memory;

import dev.langchain4j.model.output.TokenUsage;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class MemoryPublisher implements AutoCloseable {
    private static final int HEAD_READ_WORKERS = 8;
    private static final long HEAD_READ_TIMEOUT_MILLIS = 200;

    private final MemoryPublicationRepository publications;
    private final MemoryVectorRepository vectors;
    private final ExecutorService headReads = new ThreadPoolExecutor(
            0,
            HEAD_READ_WORKERS,
            60,
            TimeUnit.SECONDS,
            new SynchronousQueue<>(),
            Thread.ofPlatform().daemon(true).name("memory-head-read-", 0).factory(),
            new ThreadPoolExecutor.AbortPolicy());

    public MemoryPublisher(MemoryPublicationRepository publications, MemoryVectorRepository vectors) {
        this.publications = publications;
        this.vectors = vectors;
    }

    public void publish(
            MemoryPublicationRepository.Snapshot snapshot,
            List<MemoryDocument> documents,
            String modelId,
            TokenUsage usage) {
        for (MemoryDocument document : documents) {
            long start = snapshot.operation() == MemoryPublicationRepository.Operation.REVALIDATE
                    ? 0
                    : snapshot.sourceStartId();
            if (!snapshot.scope().equals(document.scope())
                    || !snapshot.attemptId().equals(document.commitId())
                    || !snapshot.fingerprint().equals(document.fingerprint())
                    || document.sourceStartId() != start
                    || document.sourceEndId() != snapshot.sourceEndId()) {
                throw new IllegalArgumentException("Memory records do not match the source snapshot");
            }
        }
        MemoryManifest manifest = MemoryManifest.of(documents, vectors.codec(snapshot.scope()));
        publications.prepare(snapshot, manifest, modelId, usage);
        try {
            vectors.insertPrepared(
                    snapshot.scope(),
                    snapshot.attemptId(),
                    manifest,
                    documents,
                    () -> publications.checkPrepared(snapshot.scope(), snapshot.attemptId()));
            publications.publish(snapshot.scope(), snapshot.attemptId());
        } catch (RuntimeException failure) {
            try {
                publications.terminate(
                        snapshot.scope(), snapshot.attemptId(), MemoryPublicationRepository.Failure.VECTOR_WRITE);
            } catch (RuntimeException cleanupFailure) {
                // An ambiguous SQL commit may already be published; reconciliation owns unresolved attempts.
            }
            if (failure instanceof CancellationException) {
                throw new CancellationException("Memory publication cancelled");
            }
            throw new IllegalStateException("Memory publication failed");
        }
    }

    public Optional<MemoryDocument> readCommitted(MemoryScope scope, String recordId, Runnable checkInvocation) {
        try {
            checkInvocation.run();
            Optional<String> commit = vectors.findCommit(scope, recordId);
            if (commit.isEmpty()) {
                return Optional.empty();
            }
            Optional<MemoryPublicationRepository.AuthorizedRecord> authorization =
                    publications.authorize(scope, commit.orElseThrow(), recordId);
            if (authorization.isEmpty()) {
                return Optional.empty();
            }
            MemoryPublicationRepository.AuthorizedRecord permitted = authorization.orElseThrow();
            Optional<MemoryDocument> document = vectors.read(scope, permitted.attemptId(), permitted.entry());
            checkInvocation.run();
            if (publications
                    .authorize(scope, permitted.attemptId(), recordId)
                    .filter(permitted::equals)
                    .isEmpty()) {
                return Optional.empty();
            }
            return document;
        } catch (CancellationException ignored) {
            throw new CancellationException("Committed memory read cancelled");
        } catch (RuntimeException ignored) {
            throw new IllegalStateException("Committed memory read failed");
        }
    }

    public Optional<MemoryDocument> readHead(
            MemoryScope scope,
            String recordId,
            MemoryDocument.Kind kind,
            String fingerprint,
            Runnable checkInvocation) {
        if (recordId == null) {
            return Optional.empty();
        }
        MemoryDocument document = readCommitted(scope, recordId, checkInvocation)
                .orElseThrow(() -> new IllegalStateException("Committed memory head is unavailable"));
        if (document.kind() != kind
                || document.archive()
                || document.kind() == MemoryDocument.Kind.EXTRACTION_CHECKPOINT) {
            throw new IllegalStateException("Invalid committed memory head");
        }
        if (kind == MemoryDocument.Kind.PROFILE_SNAPSHOT
                && !document.fingerprint().equals(fingerprint)) {
            return Optional.empty();
        }
        return Optional.of(document);
    }

    /** Optional derived memory must not stall a conversation or hide invocation revocation. */
    public Optional<MemoryDocument> readHeadBestEffort(
            MemoryScope scope,
            String recordId,
            MemoryDocument.Kind kind,
            String fingerprint,
            Runnable checkInvocation) {
        if (recordId == null) {
            return Optional.empty();
        }
        // The caller owns invocation state (including thread-local cancellation/authentication).
        checkInvocation.run();
        Optional<MemoryDocument> document = Optional.empty();
        Future<Optional<MemoryDocument>> pending = null;
        try {
            // Keep strict HEAD validation and both SQL authorizations, but never pass caller state to a worker.
            pending = headReads.submit(() -> readHead(scope, recordId, kind, fingerprint, () -> {}));
            document = pending.get(HEAD_READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException | TimeoutException | RejectedExecutionException | CancellationException ignored) {
            // Derived memory is optional; do not expose backend payloads or retry on the caller.
        } finally {
            if (pending != null) {
                // Also discard late results when a backend ignores interruption. Completed reads are unaffected.
                pending.cancel(true);
            }
        }
        checkInvocation.run();
        return document;
    }

    @Override
    public void close() {
        headReads.shutdownNow();
    }
}
