package fun.freechat.service.chat.memory;

import static fun.freechat.mapper.ChatMemoryStateDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;
import static org.mybatis.dynamic.sql.SqlBuilder.isGreaterThan;
import static org.mybatis.dynamic.sql.SqlBuilder.isGreaterThanWhenPresent;
import static org.mybatis.dynamic.sql.SqlBuilder.isLessThan;
import static org.mybatis.dynamic.sql.SqlBuilder.isLessThanOrEqualTo;
import static org.mybatis.dynamic.sql.SqlBuilder.isNull;
import static org.mybatis.dynamic.sql.SqlBuilder.notExists;
import static org.mybatis.dynamic.sql.SqlBuilder.or;
import static org.mybatis.dynamic.sql.SqlBuilder.select;

import fun.freechat.mapper.ChatMemoryCommitDynamicSqlSupport;
import fun.freechat.mapper.ChatMemoryCoordinationMapper;
import fun.freechat.mapper.ChatMemoryStateMapper;
import fun.freechat.model.ChatMemoryState;
import fun.freechat.service.enums.EmbeddingStoreType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

/** SQL discovery and fenced idle claims only; no provider, vector, Redis, or lifecycle work. */
public final class MemoryWorkRepository {
    /**
     * Metadata-only task input. throughId is an upper bound, not an instruction to extract it all.
     * The selected activity/cursor/head protect new work from an older worker's failure accounting;
     * they are deliberately NOT activity fences on renewal or release. Publication has its own CAS.
     */
    public record Claim(
            MemoryScope scope,
            String token,
            LocalDateTime deadline,
            long throughId,
            boolean revalidation,
            String fingerprint,
            long idleThroughId,
            LocalDateTime lastActivity,
            String profileId) {
        public Claim {
            if (scope == null
                    || token == null
                    || token.isBlank()
                    || token.length() > 36
                    || deadline == null
                    || throughId < 0
                    || idleThroughId < 0
                    || idleThroughId > throughId
                    || lastActivity == null
                    || fingerprint == null
                    || fingerprint.isBlank()
                    || fingerprint.length() > 64
                    || (profileId != null && profileId.length() > 36)) {
                throw new IllegalArgumentException("Invalid memory work claim");
            }
        }
    }

    private final ChatMemoryCoordinationMapper coordination;
    private final ChatMemoryStateMapper states;
    private final LongTermMemoryProperties properties;
    private final TransactionTemplate transactions;

    public MemoryWorkRepository(
            ChatMemoryCoordinationMapper coordination,
            ChatMemoryStateMapper states,
            PlatformTransactionManager transactions,
            LongTermMemoryProperties properties) {
        this.coordination = Objects.requireNonNull(coordination);
        this.states = Objects.requireNonNull(states);
        this.properties = Objects.requireNonNull(properties);
        this.transactions = new TransactionTemplate(Objects.requireNonNull(transactions));
        this.transactions.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        this.transactions.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
    }

    /**
     * Keyset page, capped before retrieval. Null/empty starts a scan; the dispatcher must start a
     * fresh scan after reaching the end. Filter ALL eligibility in SQL so blocked rows cannot
     * crowd eligible rows out of a page. Discovery is advisory; claim rechecks under the row lock.
     * Lifecycle integration must keep active state in sync with chat/backend enablement.
     */
    public List<String> due(String afterChatId, int limit) {
        String after = afterChatId == null || afterChatId.isEmpty() ? null : afterChatId;
        if (after != null) {
            validateChatId(after);
        }
        if (limit < 1) {
            throw new IllegalArgumentException("Invalid memory work page limit");
        }
        return transaction(() -> {
            LocalDateTime now = coordination.databaseNow();
            return states
                    .selectMany(select(chatId)
                            .from(chatMemoryState, "candidate")
                            .where(status, isEqualTo("active"))
                            .and(chatId, isGreaterThanWhenPresent(after))
                            .and(notExists(select(ChatMemoryCommitDynamicSqlSupport.attemptId)
                                    .from(ChatMemoryCommitDynamicSqlSupport.chatMemoryCommit, "reconciliation")
                                    .where(
                                            ChatMemoryCommitDynamicSqlSupport.chatId,
                                            isEqualTo(chatId.qualifiedWith("candidate")))
                                    .and(
                                            ChatMemoryCommitDynamicSqlSupport.generation,
                                            isEqualTo(generation.qualifiedWith("candidate")))
                                    .and(ChatMemoryCommitDynamicSqlSupport.operation, isEqualTo("RECONCILE"))
                                    .and(ChatMemoryCommitDynamicSqlSupport.status, isEqualTo("reconciling"))))
                            .and(dueAt, isLessThanOrEqualTo(now))
                            .and(retryAt, isNull(), or(retryAt, isLessThanOrEqualTo(now)))
                            .and(retryAttempts, isLessThan(properties.getMaxAttempts()))
                            .and(
                                    turnToken,
                                    isNull(),
                                    or(turnLeaseUntil, isNull()),
                                    or(turnDeadline, isNull()),
                                    or(turnLeaseUntil, isLessThanOrEqualTo(now)),
                                    or(turnDeadline, isLessThanOrEqualTo(now)))
                            .and(
                                    claimToken,
                                    isNull(),
                                    or(claimLeaseUntil, isNull()),
                                    or(claimDeadline, isNull()),
                                    or(claimLeaseUntil, isLessThanOrEqualTo(now)),
                                    or(claimDeadline, isLessThanOrEqualTo(now)))
                            .and(
                                    latestFinalizedId,
                                    isGreaterThan(idleThroughId),
                                    or(profileRevalidationPending, isEqualTo((byte) 1)))
                            .orderBy(chatId)
                            .limit(Math.min(limit, properties.getDispatchBatchSize()))
                            .build()
                            .render(RenderingStrategies.MYBATIS3))
                    .stream()
                    .map(ChatMemoryState::getChatId)
                    .toList();
        });
    }

    public Optional<Claim> claim(String candidateId) {
        validateChatId(candidateId);
        return transaction(() -> {
            Optional<ChatMemoryState> found = coordination.lock(candidateId);
            // UTC_TIMESTAMP is statement time in MySQL: never fetch it before waiting for this lock.
            LocalDateTime now = coordination.databaseNow();
            if (found.isEmpty() || !eligible(found.orElseThrow(), now)) {
                return Optional.empty();
            }
            ChatMemoryState state = found.orElseThrow();
            if (coordination.reconciling(state.getChatId(), state.getGeneration()) != 0) {
                return Optional.empty();
            }
            MemoryScope scope = new MemoryScope(
                    state.getChatId(),
                    state.getUserId(),
                    state.getCharacterUid(),
                    state.getGeneration(),
                    EmbeddingStoreType.of(state.getStoreType()));
            LocalDateTime deadline = now.plus(properties.getJobMaxDuration());
            Claim claim = new Claim(
                    scope,
                    UUID.randomUUID().toString(),
                    deadline,
                    state.getLatestFinalizedId(),
                    state.getProfileRevalidationPending() == 1,
                    state.getFingerprint(),
                    state.getIdleThroughId(),
                    state.getLastActivity(),
                    state.getProfileId());
            require(states.update(c -> c.set(claimToken)
                            .equalTo(claim.token())
                            .set(claimDeadline)
                            .equalTo(deadline)
                            .set(claimLeaseUntil)
                            .equalTo(earlier(now.plus(properties.getExtractionClaimLease()), deadline))
                            .set(gmtModified)
                            .equalTo(now)
                            .where(chatId, isEqualTo(candidateId)))
                    == 1);
            return Optional.of(claim);
        });
    }

    public void check(Claim claim) {
        validateClaim(claim);
        transaction(() -> {
            ChatMemoryState state = lock(claim);
            requireLive(state, claim, coordination.databaseNow());
            return null;
        });
    }

    /** Heartbeats never change user activity, due/retry state, cache version, or absolute deadline. */
    public LocalDateTime renew(Claim claim) {
        validateClaim(claim);
        return transaction(() -> {
            ChatMemoryState state = lock(claim);
            LocalDateTime now = coordination.databaseNow();
            requireLive(state, claim, now);
            LocalDateTime deadline = earlier(claim.deadline(), state.getClaimDeadline());
            LocalDateTime proposed = now.plus(properties.getExtractionClaimLease());
            // Do not shorten an existing lease if configuration is changed between calls.
            LocalDateTime until = earlier(
                    proposed.isBefore(state.getClaimLeaseUntil()) ? state.getClaimLeaseUntil() : proposed, deadline);
            require(states.update(c -> c.set(claimLeaseUntil)
                            .equalTo(until)
                            .set(gmtModified)
                            .equalTo(now)
                            .where(chatId, isEqualTo(state.getChatId())))
                    == 1);
            return until;
        });
    }

    /** Expired but still owned claims may be released; a replaced token can never release its successor. */
    public void release(Claim claim) {
        finish(claim, false);
    }

    /** Retain exhausted retry counts on ACTIVE state: foreground turns must remain available. */
    public void fail(Claim claim) {
        finish(claim, true);
    }

    private void finish(Claim claim, boolean failed) {
        validateClaim(claim);
        transaction(() -> {
            ChatMemoryState state = lock(claim);
            LocalDateTime now = coordination.databaseNow();
            boolean chargeRetry = failed && sameWork(state, claim);
            int attempts = (int) Math.min((long) properties.getMaxAttempts(), (long) state.getRetryAttempts() + 1);
            LocalDateTime retry = chargeRetry ? now.plusNanos(retryDelayMillis(attempts) * 1_000_000) : null;
            require(states.update(c -> {
                        var update = c.set(claimToken)
                                .equalToNull()
                                .set(claimLeaseUntil)
                                .equalToNull()
                                .set(claimDeadline)
                                .equalToNull()
                                .set(gmtModified)
                                .equalTo(now);
                        if (chargeRetry) {
                            update.set(retryAttempts)
                                    .equalTo(attempts)
                                    .set(retryAt)
                                    .equalTo(retry);
                        }
                        return update.where(chatId, isEqualTo(state.getChatId()));
                    })
                    == 1);
            return null;
        });
    }

    private boolean eligible(ChatMemoryState state, LocalDateTime now) {
        return "active".equals(state.getStatus())
                && state.getDueAt() != null
                && !now.isBefore(state.getDueAt())
                && (state.getRetryAt() == null || !now.isBefore(state.getRetryAt()))
                && state.getRetryAttempts() < properties.getMaxAttempts()
                && !live(state.getTurnToken(), state.getTurnLeaseUntil(), state.getTurnDeadline(), now)
                && !live(state.getClaimToken(), state.getClaimLeaseUntil(), state.getClaimDeadline(), now)
                && (state.getLatestFinalizedId() > state.getIdleThroughId()
                        || state.getProfileRevalidationPending() == 1);
    }

    private ChatMemoryState lock(Claim claim) {
        ChatMemoryState state = coordination.lock(claim.scope().chatId()).orElseThrow(Rejected::new);
        MemoryScope scope = claim.scope();
        require("active".equals(state.getStatus())
                && Objects.equals(state.getChatId(), scope.chatId())
                && Objects.equals(state.getUserId(), scope.userId())
                && Objects.equals(state.getCharacterUid(), scope.characterUid())
                && Objects.equals(state.getStoreType(), scope.storeType().text())
                && Objects.equals(state.getGeneration(), scope.generation())
                && claim.token().equals(state.getClaimToken()));
        return state;
    }

    private static boolean sameWork(ChatMemoryState state, Claim claim) {
        return claim.fingerprint().equals(state.getFingerprint())
                && claim.throughId() == state.getLatestFinalizedId()
                && claim.idleThroughId() == state.getIdleThroughId()
                && claim.lastActivity().equals(state.getLastActivity())
                && claim.revalidation() == (state.getProfileRevalidationPending() == 1)
                && Objects.equals(claim.profileId(), state.getProfileId());
    }

    /** Equal jitter in [ceil(capped exponential / 2), capped exponential], in milliseconds. */
    private long retryDelayMillis(int attempts) {
        long cap = properties.getRetryMaxDelay().toMillis();
        long delay = Math.min(properties.getRetryInitialDelay().toMillis(), cap);
        // Saturate BEFORE multiplying; stop at the cap even if max-attempts is Integer.MAX_VALUE.
        for (int remaining = attempts - 1; remaining > 0 && delay < cap; --remaining) {
            delay = delay > cap / 2 ? cap : delay * 2;
        }
        long lower = Math.max(1, delay / 2 + delay % 2);
        return ThreadLocalRandom.current().nextLong(lower, delay + 1);
    }

    private static void requireLive(ChatMemoryState state, Claim claim, LocalDateTime now) {
        require(now.isBefore(claim.deadline())
                && live(state.getClaimToken(), state.getClaimLeaseUntil(), state.getClaimDeadline(), now));
    }

    private static boolean live(String token, LocalDateTime lease, LocalDateTime deadline, LocalDateTime now) {
        return token != null && lease != null && deadline != null && now.isBefore(lease) && now.isBefore(deadline);
    }

    private static LocalDateTime earlier(LocalDateTime left, LocalDateTime right) {
        return left.isBefore(right) ? left : right;
    }

    private static void validateChatId(String id) {
        if (id == null || id.isBlank() || id.length() > 32 || id.chars().anyMatch(Character::isISOControl)) {
            throw new IllegalArgumentException("Invalid memory chat identifier");
        }
    }

    private static void validateClaim(Claim claim) {
        if (claim == null) {
            throw new IllegalArgumentException("Invalid memory work claim");
        }
    }

    private static void require(boolean valid) {
        if (!valid) {
            throw new Rejected();
        }
    }

    private <T> T transaction(Supplier<T> action) {
        boolean[] fatal = {false};
        try {
            return transactions.execute(ignored -> {
                try {
                    return action.get();
                } catch (Rejected rejected) {
                    throw new Rejected();
                } catch (Error failure) {
                    fatal[0] = true;
                    throw new Error("Memory work transaction failed");
                } catch (Throwable failure) {
                    // Sanitize before TransactionTemplate logs callback failures, including sneaky checked ones.
                    throw new IllegalStateException("Memory work transaction failed");
                }
            });
        } catch (Throwable failure) {
            // Begin/commit/rollback can also expose payloads. A failed rollback must not downgrade a fatal error.
            if (fatal[0] || failure instanceof Error) {
                throw new Error("Memory work transaction failed");
            }
            if (failure instanceof Rejected) {
                throw new Rejected();
            }
            throw new IllegalStateException("Memory work transaction failed");
        }
    }

    private static final class Rejected extends IllegalStateException {
        private Rejected() {
            super("Memory work claim or scope is no longer valid");
        }
    }
}
