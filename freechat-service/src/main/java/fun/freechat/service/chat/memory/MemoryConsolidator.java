package fun.freechat.service.chat.memory;

import static fun.freechat.service.enums.EmbeddingStoreType.longTermMemoryTypeForLang;

import fun.freechat.service.chat.memory.MemoryDocument.Kind;
import fun.freechat.service.chat.memory.MemoryExtractor.Input;
import fun.freechat.service.chat.memory.MemoryExtractor.Output;
import fun.freechat.service.chat.memory.MemoryPublicationRepository.Operation;
import fun.freechat.service.chat.memory.MemoryPublicationRepository.Snapshot;
import fun.freechat.service.chat.memory.MemorySourceReader.Evidence;
import fun.freechat.service.chat.memory.MemorySourceReader.Position;
import fun.freechat.service.chat.memory.MemorySourceReader.Turn;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class MemoryConsolidator {
    private static final int MAX_EVIDENCE_PER_CALL = 100;
    private final MemorySourceReader sources;
    private final MemoryPublicationRepository publications;
    private final MemoryPublisher publisher;
    private final MemoryCheckpoints checkpoints;
    private final MemoryBoundsFactory boundsFactory;
    private final LongTermMemoryProperties properties;

    public MemoryConsolidator(
            MemorySourceReader sources,
            MemoryPublicationRepository publications,
            MemoryPublisher publisher,
            MemoryCheckpoints checkpoints,
            MemoryBoundsFactory boundsFactory,
            LongTermMemoryProperties properties) {
        this.sources = sources;
        this.publications = publications;
        this.publisher = publisher;
        this.checkpoints = checkpoints;
        this.boundsFactory = boundsFactory;
        this.properties = properties;
    }

    public Work open(MemoryModelResolver.Resolved model, int maxCalls, Duration duration, Runnable guard) {
        if (model.recallLimit() <= 0) {
            throw new IllegalStateException("Long-term memory is disabled");
        }
        return new Work(model, maxCalls, duration, guard);
    }

    public final class Work {
        private final MemoryModelResolver.Resolved model;
        private final MemoryBounds bounds;
        private final MemoryExtractor extractor;
        private final MemoryExtractor.Budget budget;
        private final Runnable guard;
        private Snapshot current;
        private boolean progressed;

        public boolean progressed() {
            return progressed;
        }

        private Work(MemoryModelResolver.Resolved model, int maxCalls, Duration duration, Runnable guard) {
            this.model = model;
            this.guard = guard;
            bounds = boundsFactory.forLanguage(model.language());
            extractor = new MemoryExtractor(properties, bounds);
            budget = new MemoryExtractor.Budget(
                    maxCalls,
                    duration,
                    this::check,
                    (id, usage) -> publications.recordUsage(current, id, model.modelId(), usage));
        }

        // One Work spans a provider preparation, so fragment, reduction and repair calls share its budget.
        public synchronized void consolidate(Snapshot snapshot) {
            require(snapshot.scope().userId().equals(model.userId())
                    && snapshot.scope().characterUid().equals(model.characterUid())
                    && snapshot.scope().storeType() == longTermMemoryTypeForLang(model.language())
                    && snapshot.fingerprint().equals(model.fingerprint()));
            current = snapshot;
            budget.check();
            if (snapshot.operation() == Operation.REVALIDATE) {
                revalidate();
                return;
            }
            Turn turn = sources.nextTurn(snapshot.scope(), snapshot.expectedCursor(), snapshot.sourceEndId())
                    .orElseThrow(MemoryConsolidator::invalid);
            require(turn.throughId() == snapshot.sourceEndId());
            if (turn.aborted()) {
                publications.advanceAborted(snapshot);
                return;
            }
            MemoryDocument previous = head();
            if (snapshot.operation() == Operation.IDLE && previous != null) {
                require(previous.fingerprint().equals(snapshot.fingerprint()));
            }
            MemoryCheckpoints.Progress progress =
                    checkpoints.load(snapshot, this::check).orElse(null);
            Output compact = progress == null
                    ? new Output(
                            "",
                            previous != null && snapshot.operation() == Operation.IDLE
                                    ? previous.userFacts()
                                    : List.of(),
                            previous != null && snapshot.operation() == Operation.IDLE
                                    ? previous.characterDeltas()
                                    : List.of())
                    : progress.output();
            Position position = progress == null ? new Position(snapshot.expectedCursor(), 0) : progress.position();
            while (position.rowId() < turn.throughId() || position.offset() > 0) {
                budget.check();
                Batch batch = batch(turn, position, compact);
                if (batch.input().evidence().isEmpty()) {
                    position = batch.next();
                    break;
                }
                compact = extractor.extract(model.model(), model.providerContextLimit(), batch.input(), budget);
                progress = checkpoints.save(
                        snapshot,
                        progress,
                        batch.next(),
                        compact,
                        turn.observedAt(),
                        model.modelId(),
                        null,
                        this::check);
                progressed = true;
                position = batch.next();
            }
            require(position.rowId() == turn.throughId() && position.offset() == 0);
            if (compact.summary().isBlank()) {
                compact = new Output(
                        "This completed turn contained no textual memory evidence.",
                        compact.userFacts(),
                        compact.characterDeltas());
            }
            if (snapshot.operation() == Operation.OVERFLOW) {
                Output rolling = compact;
                if (previous != null) {
                    Input merge = input(
                            new Output(previous.summary(), List.of(), List.of()),
                            List.of(new Evidence(
                                    turn.throughId(),
                                    "derived-summary",
                                    false,
                                    turn.observedAt(),
                                    0,
                                    compact.summary())),
                            turn.observedAt());
                    rolling = extractor.extract(model.model(), model.providerContextLimit(), merge, budget);
                }
                budget.check();
                publisher.publish(
                        snapshot,
                        List.of(
                                document(Kind.WINDOW_SUMMARY, false, rolling, turn.observedAt()),
                                document(Kind.WINDOW_SUMMARY, true, compact, turn.observedAt())),
                        model.modelId(),
                        null);
            } else {
                budget.check();
                publisher.publish(
                        snapshot,
                        List.of(
                                document(
                                        Kind.EPISODE_SUMMARY,
                                        false,
                                        new Output(compact.summary(), List.of(), List.of()),
                                        turn.observedAt()),
                                document(
                                        Kind.PROFILE_SNAPSHOT,
                                        false,
                                        new Output("", compact.userFacts(), compact.characterDeltas()),
                                        turn.observedAt())),
                        model.modelId(),
                        null);
            }
        }

        private void revalidate() {
            MemoryDocument previous = head();
            Instant now = Instant.now();
            Output output = new Output("", List.of(), List.of());
            if (previous != null
                    && (!previous.userFacts().isEmpty()
                            || !previous.characterDeltas().isEmpty())) {
                output = extractor.extract(
                        model.model(),
                        model.providerContextLimit(),
                        input(new Output("", previous.userFacts(), previous.characterDeltas()), List.of(), now),
                        budget);
            }
            budget.check();
            publisher.publish(
                    current, List.of(document(Kind.PROFILE_SNAPSHOT, false, output, now)), model.modelId(), null);
        }

        private MemoryDocument head() {
            if (current.expectedHead() == null) {
                return null;
            }
            MemoryDocument document = publisher
                    .readCommitted(current.scope(), current.expectedHead(), this::check)
                    .orElseThrow(MemoryConsolidator::invalid);
            Kind expected = current.operation() == Operation.OVERFLOW ? Kind.WINDOW_SUMMARY : Kind.PROFILE_SNAPSHOT;
            require(document.kind() == expected && !document.archive());
            return document;
        }

        private Batch batch(Turn turn, Position start, Output previous) {
            List<Evidence> evidence = new ArrayList<>();
            Position position = start;
            int fragmentTokens = properties.getExtractionMaxInputTokens();
            while (evidence.size() < MAX_EVIDENCE_PER_CALL) {
                budget.check();
                var fragment = sources.nextFragment(current.scope(), turn, position, bounds, fragmentTokens);
                if (fragment.complete()) {
                    return new Batch(input(previous, evidence, turn.observedAt()), fragment.next());
                }
                List<Evidence> candidate = new ArrayList<>(evidence);
                candidate.add(fragment.evidence());
                Input proposed = input(previous, candidate, turn.observedAt());
                try {
                    extractor.requireInput(proposed, model.providerContextLimit());
                } catch (MemoryBounds.CapacityException tooLarge) {
                    if (!evidence.isEmpty()) {
                        break;
                    }
                    fragmentTokens /= 2;
                    if (fragmentTokens == 0) {
                        throw new MemoryBounds.CapacityException(
                                "Memory extraction cannot fit baseline and source evidence");
                    }
                    continue;
                }
                evidence = candidate;
                position = fragment.next();
                fragmentTokens = properties.getExtractionMaxInputTokens();
            }
            return new Batch(input(previous, evidence, turn.observedAt()), position);
        }

        private Input input(Output previous, List<Evidence> evidence, Instant observedAt) {
            return new Input(
                    current.scope(),
                    current.fingerprint(),
                    current.operation(),
                    current.operation() == Operation.OVERFLOW ? "" : model.userBaseline(),
                    current.operation() == Operation.OVERFLOW ? "" : model.characterBaseline(),
                    previous.summary(),
                    previous.userFacts(),
                    previous.characterDeltas(),
                    evidence,
                    observedAt);
        }

        private MemoryDocument document(Kind kind, boolean archive, Output output, Instant observedAt) {
            return new MemoryDocument(
                    UUID.randomUUID().toString(),
                    current.scope(),
                    current.attemptId(),
                    kind,
                    archive,
                    current.operation() == Operation.REVALIDATE ? 0 : current.sourceStartId(),
                    current.sourceEndId(),
                    observedAt,
                    current.fingerprint(),
                    output.summary(),
                    output.userFacts(),
                    output.characterDeltas());
        }

        private void check() {
            guard.run();
            publications.checkSnapshot(current);
        }
    }

    private static void require(boolean valid) {
        if (!valid) {
            throw invalid();
        }
    }

    private static IllegalStateException invalid() {
        return new IllegalStateException("Memory consolidation source or configuration changed");
    }

    private record Batch(Input input, Position next) {}
}
