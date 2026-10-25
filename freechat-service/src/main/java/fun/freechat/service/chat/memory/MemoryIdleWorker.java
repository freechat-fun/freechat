package fun.freechat.service.chat.memory;

import fun.freechat.service.chat.memory.MemoryPublicationRepository.Operation;
import fun.freechat.service.chat.memory.MemoryPublicationRepository.Snapshot;
import fun.freechat.service.chat.memory.MemoryWorkRepository.Claim;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class MemoryIdleWorker implements AutoCloseable {
    private final MemoryWorkRepository work;
    private final MemorySourceReader sources;
    private final MemoryPublicationRepository publications;
    private final MemoryModelResolver models;
    private final MemoryConsolidator consolidator;
    private final LongTermMemoryProperties properties;
    private final ScheduledExecutorService renewals;
    private final AtomicBoolean closed = new AtomicBoolean();

    public MemoryIdleWorker(
            MemoryWorkRepository work,
            MemorySourceReader sources,
            MemoryPublicationRepository publications,
            MemoryModelResolver models,
            MemoryConsolidator consolidator,
            LongTermMemoryProperties properties) {
        this.work = work;
        this.sources = sources;
        this.publications = publications;
        this.models = models;
        this.consolidator = consolidator;
        this.properties = properties;
        renewals = Executors.newScheduledThreadPool(
                properties.getWorkersPerNode(),
                Thread.ofPlatform().daemon().name("memory-claim-renewal-", 0).factory());
    }

    public void execute(Claim claim) {
        AtomicBoolean leaseLost = new AtomicBoolean();
        AtomicBoolean finished = new AtomicBoolean();
        Thread worker = Thread.currentThread();
        Runnable guard = () -> {
            if (closed.get() || leaseLost.get() || Thread.currentThread().isInterrupted()) {
                throw new IllegalStateException("Memory extraction worker is no longer active");
            }
            try {
                work.check(claim);
            } catch (RuntimeException rejected) {
                leaseLost.set(true);
                throw rejected;
            }
        };
        ScheduledFuture<?> renewal = null;
        MemoryConsolidator.Work consolidation = null;
        Snapshot snapshot = null;
        boolean failed = false;
        try {
            guard.run();
            renewal = renewals.scheduleWithFixedDelay(
                    () -> {
                        synchronized (finished) {
                            if (finished.get() || leaseLost.get()) {
                                return;
                            }
                            try {
                                if (closed.get()) {
                                    throw new IllegalStateException("Memory worker stopped");
                                }
                                work.renew(claim);
                            } catch (RuntimeException ignored) {
                                leaseLost.set(true);
                                worker.interrupt();
                            }
                        }
                    },
                    properties.getLeaseRenewInterval().toMillis(),
                    properties.getLeaseRenewInterval().toMillis(),
                    TimeUnit.MILLISECONDS);
            Operation operation = claim.revalidation() ? Operation.REVALIDATE : Operation.IDLE;
            long through = claim.idleThroughId();
            boolean aborted = false;
            if (operation == Operation.IDLE) {
                var turn = sources.nextTurn(claim.scope(), through, claim.throughId())
                        .orElseThrow(() -> new IllegalStateException("Memory work source is unavailable"));
                through = turn.throughId();
                aborted = turn.aborted();
            }
            var candidate = publications.snapshot(claim.scope(), operation, through, claim.token());
            if (!candidate.fingerprint().equals(claim.fingerprint())
                    || candidate.expectedCursor() != claim.idleThroughId()
                    || !Objects.equals(candidate.expectedHead(), claim.profileId())
                    || through > claim.throughId()) {
                throw new IllegalStateException("Memory work snapshot changed");
            }
            guard.run();
            if (aborted) {
                publications.advanceAborted(candidate);
                return;
            }
            // Only a validated, selected range may be abandoned after optional work fails.
            snapshot = candidate;
            var model = models.resolve(claim.scope().chatId());
            if (!claim.fingerprint().equals(model.fingerprint())) {
                snapshot = null;
                throw new IllegalStateException("Memory work model changed");
            }
            guard.run();
            // Bound even fast providers; elapsed-time limits alone do not cap the number of billed calls.
            int maxCalls = (int) Math.max(
                    1,
                    Math.min(
                            100,
                            properties.getJobMaxDuration().toMillis()
                                    / properties.getExtractionTimeout().toMillis()));
            consolidation = consolidator.open(model, maxCalls, properties.getJobMaxDuration(), guard);
            consolidation.consolidate(snapshot);
        } catch (MemoryBounds.CapacityException exhausted) {
            failed = consolidation == null || !consolidation.progressed();
            if (failed && !(exhausted instanceof MemoryBounds.PreparationDeferredException)) {
                failed = !abandon(snapshot, guard, finished);
            }
        } catch (CancellationException ignored) {
            failed = true;
        } catch (RuntimeException ignored) {
            failed = true;
            if (abandon(snapshot, guard, finished)) {
                failed = false;
            }
        } catch (Error ignored) {
            failed = true;
            // Provider/source failures may carry private payloads in messages, causes or suppressed errors.
            throw new Error("Memory consolidation failed");
        } finally {
            synchronized (finished) {
                finished.set(true);
            }
            if (renewal != null) {
                renewal.cancel(false);
            }
            // Quiesce renewal before clearing interruption, and restore it only after SQL cleanup.
            boolean interrupted = Thread.interrupted();
            try {
                if (failed) {
                    work.fail(claim);
                    log.warn(
                            "Memory consolidation deferred for chat {}",
                            claim.scope().chatId());
                } else {
                    work.release(claim);
                }
            } catch (RuntimeException ignored) {
                log.warn(
                        "Memory claim release deferred for chat {}",
                        claim.scope().chatId());
            } finally {
                if (interrupted) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private boolean abandon(Snapshot snapshot, Runnable guard, AtomicBoolean finished) {
        if (snapshot == null) {
            return false;
        }
        // Wait for an in-flight heartbeat before checking cancellation; never clear interruption to skip work.
        synchronized (finished) {
            try {
                guard.run();
                publications.abandon(snapshot);
                log.warn("memory work skipped");
                return true;
            } catch (RuntimeException ignored) {
                // Publication may have committed already, or ownership/source changed. Fenced cleanup still applies.
                return false;
            }
        }
    }

    @Override
    public void close() {
        closed.set(true);
        renewals.shutdownNow();
    }
}
