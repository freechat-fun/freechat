package fun.freechat.service.chat.memory;

import dev.langchain4j.model.output.TokenUsage;
import fun.freechat.service.chat.memory.MemoryPublicationRepository.Snapshot;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/** Exact-read-only incremental reduction state. No source cursor advances until ordinary publication. */
public final class MemoryCheckpoints {
    private final MemoryPublicationRepository publications;
    private final MemoryVectorRepository vectors;

    public MemoryCheckpoints(MemoryPublicationRepository publications, MemoryVectorRepository vectors) {
        this.publications = Objects.requireNonNull(publications);
        this.vectors = Objects.requireNonNull(vectors);
    }

    /** Resume using a NEW valid snapshot/lease, never the completed checkpoint's old lease. */
    public Optional<Progress> load(Snapshot snapshot, Runnable guard) {
        try {
            guard.run();
            var found = publications.loadCheckpoint(snapshot);
            if (found.isEmpty()) {
                return Optional.empty();
            }
            var checkpoint = found.orElseThrow();
            guard.run();
            MemoryDocument document = vectors.read(snapshot.scope(), checkpoint.attemptId(), checkpoint.entry())
                    .orElseThrow(MemoryCheckpoints::failed);
            guard.run();
            if (!publications.loadCheckpoint(snapshot).equals(found)) {
                throw failed();
            }
            if (!snapshot.fingerprint().equals(document.fingerprint())
                    || snapshot.sourceStartId() != document.sourceStartId()
                    || snapshot.sourceEndId() != document.sourceEndId()) {
                throw failed();
            }
            return Optional.of(new Progress(
                    checkpoint.attemptId(),
                    checkpoint.position(),
                    new MemoryExtractor.Output(document.summary(), document.userFacts(), document.characterDeltas())));
        } catch (RuntimeException ignored) {
            throw failed();
        }
    }

    /**
     * Save the compact result after one bounded model call. The caller supplies the previous compact
     * output and next fragment to the extractor, and records EVERY call through recordUsage separately.
     * Each save owns one fresh attempt and vector ID; ambiguous writes are never retried or deleted here.
     * Final ordinary publication atomically retires the matching checkpoint head with GC grace.
     */
    public Progress save(
            Snapshot snapshot,
            Progress previous,
            MemorySourceReader.Position next,
            MemoryExtractor.Output output,
            Instant observedAt,
            String modelId,
            TokenUsage usage,
            Runnable guard) {
        String attemptId = UUID.randomUUID().toString();
        try {
            if (!Objects.equals(previous, load(snapshot, guard).orElse(null))) {
                throw failed();
            }
            MemoryDocument document = new MemoryDocument(
                    UUID.randomUUID().toString(),
                    snapshot.scope(),
                    attemptId,
                    MemoryDocument.Kind.EXTRACTION_CHECKPOINT,
                    false,
                    snapshot.sourceStartId(),
                    snapshot.sourceEndId(),
                    observedAt,
                    snapshot.fingerprint(),
                    output.summary(),
                    output.userFacts(),
                    output.characterDeltas());
            if (snapshot.operation() == MemoryPublicationRepository.Operation.OVERFLOW
                    && (!document.userFacts().isEmpty()
                            || !document.characterDeltas().isEmpty())) {
                throw failed();
            }
            MemoryManifest manifest = MemoryManifest.of(List.of(document), vectors.codec(snapshot.scope()));
            guard.run();
            publications.prepareCheckpoint(
                    snapshot,
                    attemptId,
                    previous == null ? null : previous.attemptId(),
                    next,
                    manifest,
                    modelId,
                    usage);
            vectors.insertPrepared(snapshot.scope(), attemptId, manifest, List.of(document), () -> {
                guard.run();
                publications.checkPreparedCheckpoint(snapshot, attemptId);
            });
            guard.run();
            publications.publishCheckpoint(snapshot, attemptId);
            return new Progress(
                    attemptId,
                    next,
                    new MemoryExtractor.Output(document.summary(), document.userFacts(), document.characterDeltas()));
        } catch (RuntimeException ignored) {
            try {
                publications.terminate(snapshot.scope(), attemptId, MemoryPublicationRepository.Failure.VECTOR_WRITE);
            } catch (RuntimeException cleanupFailure) {
                // No prepared row, or an ambiguous SQL result. Reconciliation owns unresolved attempts.
            }
            throw failed();
        }
    }

    private static IllegalStateException failed() {
        return new IllegalStateException("Memory checkpoint operation failed");
    }

    public record Progress(String attemptId, MemorySourceReader.Position position, MemoryExtractor.Output output) {}
}
