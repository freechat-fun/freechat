package fun.freechat.service.chat.memory;

import dev.langchain4j.model.output.TokenUsage;
import java.util.List;
import java.util.Optional;

public final class MemoryPublisher {
    private final MemoryPublicationRepository publications;
    private final MemoryVectorRepository vectors;

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
        } catch (RuntimeException ignored) {
            try {
                publications.terminate(
                        snapshot.scope(), snapshot.attemptId(), MemoryPublicationRepository.Failure.VECTOR_WRITE);
            } catch (RuntimeException cleanupFailure) {
                // An ambiguous SQL commit may already be published; reconciliation owns unresolved attempts.
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
}
