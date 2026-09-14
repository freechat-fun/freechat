package fun.freechat.service.chat.memory;

import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;
import static org.mybatis.dynamic.sql.SqlBuilder.isGreaterThan;
import static org.mybatis.dynamic.sql.SqlBuilder.isIn;
import static org.mybatis.dynamic.sql.SqlBuilder.isLessThanOrEqualTo;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.model.output.TokenUsage;
import fun.freechat.mapper.ChatHistoryDynamicSqlSupport;
import fun.freechat.mapper.ChatHistoryMapper;
import fun.freechat.mapper.ChatMemoryCoordinationMapper;
import fun.freechat.mapper.ChatMemoryStateDynamicSqlSupport;
import fun.freechat.mapper.ChatMemoryStateMapper;
import fun.freechat.model.ChatHistory;
import fun.freechat.model.ChatMemoryState;
import fun.freechat.service.chat.SystemPromptSnapshotStore;
import fun.freechat.service.util.InfoUtils;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import org.mybatis.dynamic.sql.dsl.SelectDSLCompleter;
import org.mybatis.dynamic.sql.util.mybatis3.MyBatis3Utils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

/** SQL source history and turn fencing; never holds a transaction across provider or tool work. */
public class MemoryTurnRepository {
    public record TurnLease(MemoryScope scope, String token, LocalDateTime deadline, long startId) {
        public TurnLease {
            if (scope == null
                    || token == null
                    || token.isBlank()
                    || token.length() > 36
                    || deadline == null
                    || startId <= 0) {
                throw new IllegalArgumentException("Invalid memory turn lease");
            }
        }
    }

    public enum Origin {
        USER_INPUT("user-input"),
        ASSISTANT_OUTPUT("assistant-output"),
        TOOL("tool"),
        TEMPLATE_EXAMPLE("template-example"),
        SYSTEM("system");

        private final String text;

        Origin(String text) {
            this.text = text;
        }

        public String text() {
            return text;
        }
    }

    private final ChatMemoryCoordinationMapper coordination;
    private final ChatMemoryStateMapper states;
    private final ChatHistoryMapper histories;
    private final TransactionTemplate transactions;
    private final LongTermMemoryProperties properties;
    private final SystemPromptSnapshotStore snapshots;

    public MemoryTurnRepository(
            ChatMemoryCoordinationMapper coordination,
            ChatMemoryStateMapper states,
            ChatHistoryMapper histories,
            PlatformTransactionManager transactions,
            LongTermMemoryProperties properties,
            SystemPromptSnapshotStore snapshots) {
        this.coordination = Objects.requireNonNull(coordination);
        this.states = Objects.requireNonNull(states);
        this.histories = Objects.requireNonNull(histories);
        this.properties = Objects.requireNonNull(properties);
        this.snapshots = Objects.requireNonNull(snapshots);
        this.transactions = new TransactionTemplate(Objects.requireNonNull(transactions));
        // Public operations are short, committed boundaries even if the caller has an ambient transaction.
        this.transactions.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        this.transactions.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
    }

    /** Existing generations, fingerprints and tombstones are returned intact, never reinitialized. */
    public ChatMemoryState initialize(MemoryScope scope, String fingerprint) {
        validateScope(scope);
        validateFingerprint(fingerprint);
        return inTransaction(() -> {
            coordination.initialize(new ChatMemoryState()
                    .withChatId(scope.chatId())
                    .withUserId(scope.userId())
                    .withCharacterUid(scope.characterUid())
                    .withStoreType(scope.storeType().text())
                    .withGeneration(scope.generation())
                    .withFingerprint(fingerprint));
            ChatMemoryState state = lock(scope.chatId());
            requireOwner(state, scope);
            return state;
        });
    }

    public Optional<ChatMemoryState> read(String chatId) {
        if (chatId == null || chatId.isBlank() || chatId.length() > 32) {
            throw new IllegalArgumentException("Invalid memory chat identifier");
        }
        return inTransaction(() -> states.selectByPrimaryKey(chatId));
    }

    public TurnLease begin(MemoryScope scope, String fingerprint) {
        validateScope(scope);
        validateFingerprint(fingerprint);
        return inTransaction(() -> {
            ChatMemoryState state = lock(scope.chatId());
            LocalDateTime now = coordination.databaseNow();
            requireCurrent(state, scope);
            require(Objects.equals(fingerprint, state.getFingerprint()));
            boolean nextEpisode = !now.isBefore(state.getLastActivity().plus(properties.getIdleTimeout()));
            if (state.getTurnToken() != null) {
                require(!live(state, now));
                // Abort the old token before replacing it, retaining its original episode.
                terminate(state, "turn-abort", now);
            }
            if (nextEpisode) {
                state.setEpisode(state.getEpisode() + 1);
            }
            state.setTurnToken(UUID.randomUUID().toString());
            state.setTurnDeadline(now.plus(properties.getTurnMaxDuration()));
            state.setTurnLeaseUntil(earlier(now.plus(properties.getTurnLease()), state.getTurnDeadline()));
            state.setTurnRevision(state.getTurnRevision() + 1);
            activity(state, now);
            long startId = insertMarker(state, "turn-start", now);
            save(state, now);
            return new TurnLease(scope, state.getTurnToken(), state.getTurnDeadline(), startId);
        });
    }

    public long append(
            TurnLease lease,
            ChatMessage message,
            ChatMessage originalInput,
            SystemMessage systemSnapshot,
            Origin origin,
            TokenUsage usage) {
        validateLease(lease);
        if (message == null || origin == null) {
            throw new IllegalArgumentException("Invalid memory message");
        }
        String snapshotRef = snapshots.save(lease.scope().chatId(), systemSnapshot);
        return inTransaction(() -> {
            ChatMemoryState state = lock(lease.scope().chatId());
            LocalDateTime now = coordination.databaseNow();
            requireLease(state, lease, now, false);
            return insertMessage(state, message, originalInput, snapshotRef, origin, usage, now);
        });
    }

    /** Returns the final answer's row ID; latestFinalizedId points to the accompanying terminal marker. */
    public long complete(TurnLease lease, AiMessage finalAnswer, SystemMessage snapshot, TokenUsage usage) {
        validateLease(lease);
        if (finalAnswer == null || finalAnswer.hasToolExecutionRequests()) {
            throw new IllegalArgumentException("A final non-tool assistant answer is required");
        }
        String snapshotRef = snapshots.save(lease.scope().chatId(), snapshot);
        return inTransaction(() -> {
            ChatMemoryState state = lock(lease.scope().chatId());
            LocalDateTime now = coordination.databaseNow();
            requireLease(state, lease, now, false);
            long answerId = insertMessage(state, finalAnswer, null, snapshotRef, Origin.ASSISTANT_OUTPUT, usage, now);
            terminate(state, "turn-complete", now);
            save(state, now);
            return answerId;
        });
    }

    /** Expiry is allowed only here: a stale token can never terminate its successor. */
    public void abort(TurnLease lease) {
        validateLease(lease);
        inTransaction(() -> {
            ChatMemoryState state = lock(lease.scope().chatId());
            LocalDateTime now = coordination.databaseNow();
            requireLease(state, lease, now, true);
            terminate(state, "turn-abort", now);
            save(state, now);
            return null;
        });
    }

    /** Returns the new bounded lease expiry without extending the absolute deadline or activity. */
    public LocalDateTime renew(TurnLease lease) {
        validateLease(lease);
        return inTransaction(() -> {
            ChatMemoryState state = lock(lease.scope().chatId());
            LocalDateTime now = coordination.databaseNow();
            requireLease(state, lease, now, false);
            LocalDateTime until = earlier(now.plus(properties.getTurnLease()), state.getTurnDeadline());
            if (until.isBefore(state.getTurnLeaseUntil())) {
                until = earlier(state.getTurnLeaseUntil(), state.getTurnDeadline());
            }
            LocalDateTime renewedUntil = until;
            require(states.update(c -> c.set(ChatMemoryStateDynamicSqlSupport.turnLeaseUntil)
                            .equalTo(renewedUntil)
                            .set(ChatMemoryStateDynamicSqlSupport.turnRevision)
                            .equalTo(state.getTurnRevision() + 1)
                            .set(ChatMemoryStateDynamicSqlSupport.gmtModified)
                            .equalTo(now)
                            .where(ChatMemoryStateDynamicSqlSupport.chatId, isEqualTo(state.getChatId())))
                    == 1);
            return renewedUntil;
        });
    }

    public void check(TurnLease lease) {
        validateLease(lease);
        inTransaction(() -> {
            ChatMemoryState state = lock(lease.scope().chatId());
            requireLease(state, lease, coordination.databaseNow(), false);
            return null;
        });
    }

    public void recoverExpired(MemoryScope scope) {
        inTransaction(() -> {
            ChatMemoryState state = lock(scope.chatId());
            requireCurrent(state, scope);
            if (state.getTurnToken() != null) {
                LocalDateTime now = coordination.databaseNow();
                require(!live(state, now));
                terminate(state, "turn-abort", now);
                save(state, now);
            }
            return null;
        });
    }

    public long remainingMillis(TurnLease lease) {
        validateLease(lease);
        return inTransaction(() -> {
            ChatMemoryState state = lock(lease.scope().chatId());
            LocalDateTime now = coordination.databaseNow();
            requireLease(state, lease, now, false);
            return Math.max(
                    1, java.time.Duration.between(now, state.getTurnDeadline()).toMillis());
        });
    }

    public List<ChatHistory> finalizedPage(MemoryScope scope, long afterExclusive, long throughInclusive, int limit) {
        validateScope(scope);
        if (afterExclusive < 0 || throughInclusive < afterExclusive || limit < 1 || limit > 1000) {
            throw new IllegalArgumentException("Invalid memory source page bounds");
        }
        return inTransaction(() -> {
            requireCurrent(lock(scope.chatId()), scope);
            return sourceRows(
                    scope,
                    c -> c.where(ChatHistoryDynamicSqlSupport.memoryId, isEqualTo(scope.chatId()))
                            .and(ChatHistoryDynamicSqlSupport.enabled, isEqualTo((byte) 1))
                            .and(ChatHistoryDynamicSqlSupport.id, isGreaterThan(afterExclusive))
                            .and(ChatHistoryDynamicSqlSupport.id, isLessThanOrEqualTo(throughInclusive))
                            .and(ChatHistoryDynamicSqlSupport.recordKind, isIn("turn-complete", "turn-abort"))
                            .orderBy(ChatHistoryDynamicSqlSupport.id)
                            .limit(limit));
        });
    }

    /** Includes bodyless boundaries and every message origin; callers reconstruct whole turns. */
    public List<ChatHistory> sourcePage(MemoryScope scope, long afterExclusive, long throughInclusive, int limit) {
        validateScope(scope);
        if (afterExclusive < 0 || throughInclusive < afterExclusive || limit < 1 || limit > 1000) {
            throw new IllegalArgumentException("Invalid memory source page bounds");
        }
        return inTransaction(() -> {
            requireCurrent(lock(scope.chatId()), scope);
            return sourceRows(
                    scope,
                    c -> c.where(ChatHistoryDynamicSqlSupport.memoryId, isEqualTo(scope.chatId()))
                            .and(ChatHistoryDynamicSqlSupport.enabled, isEqualTo((byte) 1))
                            .and(ChatHistoryDynamicSqlSupport.id, isGreaterThan(afterExclusive))
                            .and(ChatHistoryDynamicSqlSupport.id, isLessThanOrEqualTo(throughInclusive))
                            .orderBy(ChatHistoryDynamicSqlSupport.id)
                            .limit(limit));
        });
    }

    private List<ChatHistory> sourceRows(MemoryScope scope, SelectDSLCompleter selection) {
        // Sort only IDs: MySQL filesort can exhaust its buffer on a single oversized JSON message.
        List<Long> ids = MyBatis3Utils.selectList(
                        histories::selectMany,
                        new org.mybatis.dynamic.sql.BasicColumn[] {ChatHistoryDynamicSqlSupport.id},
                        ChatHistoryDynamicSqlSupport.chatHistory,
                        selection)
                .stream()
                .map(ChatHistory::getId)
                .toList();
        if (ids.isEmpty()) {
            return List.of();
        }
        List<ChatHistory> rows =
                histories.select(query -> query.where(ChatHistoryDynamicSqlSupport.memoryId, isEqualTo(scope.chatId()))
                        .and(ChatHistoryDynamicSqlSupport.enabled, isEqualTo((byte) 1))
                        .and(ChatHistoryDynamicSqlSupport.id, isIn(ids)));
        require(rows.size() == ids.size());
        return rows.stream()
                .sorted(Comparator.comparingLong(ChatHistory::getId))
                .toList();
    }

    private long insertMessage(
            ChatMemoryState state,
            ChatMessage message,
            ChatMessage original,
            String snapshotRef,
            Origin origin,
            TokenUsage usage,
            LocalDateTime now) {
        ChatHistory row = row(state, "message", now)
                .withMessage(ChatMessageSerializer.messageToJson(message))
                .withSourceMessage(original == null ? null : ChatMessageSerializer.messageToJson(original))
                .withSystemMessageRef(snapshotRef)
                .withMessageOrigin(origin.text())
                .withExt(usage == null ? null : InfoUtils.serialize(usage));
        return insert(row);
    }

    private long insertMarker(ChatMemoryState state, String kind, LocalDateTime now) {
        return insert(row(state, kind, now));
    }

    private ChatHistory row(ChatMemoryState state, String kind, LocalDateTime now) {
        return new ChatHistory()
                .withMemoryId(state.getChatId())
                .withTurnId(state.getTurnToken())
                .withRecordKind(kind)
                .withEpisode(state.getEpisode())
                .withGmtCreate(now)
                .withGmtModified(now);
    }

    private long insert(ChatHistory row) {
        require(histories.insertSelective(row) == 1 && row.getId() != null);
        return row.getId();
    }

    private void terminate(ChatMemoryState state, String kind, LocalDateTime now) {
        state.setLatestFinalizedId(insertMarker(state, kind, now));
        state.setTurnToken(null);
        state.setTurnLeaseUntil(null);
        state.setTurnDeadline(null);
        state.setTurnRevision(state.getTurnRevision() + 1);
        activity(state, now);
        state.setVersion(state.getVersion() + 1);
    }

    private void activity(ChatMemoryState state, LocalDateTime now) {
        state.setLastActivity(now);
        state.setDueAt(now.plus(properties.getIdleTimeout()));
        state.setRetryAt(null);
        state.setRetryAttempts(0);
    }

    private void save(ChatMemoryState state, LocalDateTime now) {
        state.setGmtModified(now);
        // Full update is intentional: selective updates cannot revoke nullable lease/retry fields.
        require(states.updateByPrimaryKey(state) == 1);
    }

    private ChatMemoryState lock(String chatId) {
        return coordination.lock(chatId).orElseThrow(Rejected::new);
    }

    private static void requireOwner(ChatMemoryState state, MemoryScope scope) {
        require(Objects.equals(state.getChatId(), scope.chatId())
                && Objects.equals(state.getUserId(), scope.userId())
                && Objects.equals(state.getCharacterUid(), scope.characterUid())
                && Objects.equals(state.getStoreType(), scope.storeType().text()));
    }

    private static void requireCurrent(ChatMemoryState state, MemoryScope scope) {
        requireOwner(state, scope);
        require("active".equals(state.getStatus()) && Objects.equals(state.getGeneration(), scope.generation()));
    }

    private static void requireLease(ChatMemoryState state, TurnLease lease, LocalDateTime now, boolean allowExpired) {
        requireCurrent(state, lease.scope());
        require(lease.token().equals(state.getTurnToken()));
        require(allowExpired || live(state, now));
    }

    private static boolean live(ChatMemoryState state, LocalDateTime now) {
        return state.getTurnLeaseUntil() != null
                && state.getTurnDeadline() != null
                && now.isBefore(state.getTurnLeaseUntil())
                && now.isBefore(state.getTurnDeadline());
    }

    private static LocalDateTime earlier(LocalDateTime left, LocalDateTime right) {
        return left.isBefore(right) ? left : right;
    }

    private static void validateScope(MemoryScope scope) {
        if (scope == null) {
            throw new IllegalArgumentException("Invalid memory scope");
        }
    }

    private static void validateLease(TurnLease lease) {
        if (lease == null) {
            throw new IllegalArgumentException("Invalid memory turn lease");
        }
    }

    private static void validateFingerprint(String fingerprint) {
        if (fingerprint == null || fingerprint.isBlank() || fingerprint.length() > 64) {
            throw new IllegalArgumentException("Invalid memory fingerprint");
        }
    }

    private static void require(boolean condition) {
        if (!condition) {
            throw new Rejected();
        }
    }

    private <T> T inTransaction(Supplier<T> action) {
        boolean[] fatal = {false};
        try {
            return transactions.execute(status -> {
                try {
                    return action.get();
                } catch (Rejected rejected) {
                    throw new Rejected();
                } catch (Error failure) {
                    fatal[0] = true;
                    throw new Error("Memory transaction failed");
                } catch (Throwable failure) {
                    // Sanitize before TransactionTemplate logs callback failures, including sneaky checked ones.
                    throw new IllegalStateException("Memory transaction failed");
                }
            });
        } catch (Throwable failure) {
            // Begin/commit/rollback can also expose payloads. A failed rollback must not downgrade a fatal error.
            if (fatal[0] || failure instanceof Error) {
                throw new Error("Memory transaction failed");
            }
            if (failure instanceof Rejected) {
                throw new Rejected();
            }
            throw new IllegalStateException("Memory transaction failed");
        }
    }

    private static final class Rejected extends IllegalStateException {
        private Rejected() {
            super("Memory turn or scope is no longer valid");
        }
    }
}
