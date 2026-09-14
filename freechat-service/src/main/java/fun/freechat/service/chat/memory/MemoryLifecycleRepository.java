package fun.freechat.service.chat.memory;

import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;
import static org.mybatis.dynamic.sql.SqlBuilder.isIn;
import static org.mybatis.dynamic.sql.SqlBuilder.isNotNull;

import fun.freechat.mapper.ChatContextMapper;
import fun.freechat.mapper.ChatHistoryDynamicSqlSupport;
import fun.freechat.mapper.ChatHistoryMapper;
import fun.freechat.mapper.ChatMemoryCoordinationMapper;
import fun.freechat.mapper.ChatMemoryStateMapper;
import fun.freechat.model.ChatContext;
import fun.freechat.model.ChatHistory;
import fun.freechat.model.ChatMemoryState;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

public final class MemoryLifecycleRepository {
    private final ChatMemoryCoordinationMapper coordination;
    private final ChatMemoryStateMapper states;
    private final ChatHistoryMapper histories;
    private final ChatContextMapper contexts;
    private final TransactionTemplate transactions;
    private final LongTermMemoryProperties properties;

    public MemoryLifecycleRepository(
            ChatMemoryCoordinationMapper coordination,
            ChatMemoryStateMapper states,
            ChatHistoryMapper histories,
            ChatContextMapper contexts,
            PlatformTransactionManager transactions,
            LongTermMemoryProperties properties) {
        this.coordination = coordination;
        this.states = states;
        this.histories = histories;
        this.contexts = contexts;
        this.properties = properties;
        this.transactions = new TransactionTemplate(transactions);
        this.transactions.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        this.transactions.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
    }

    public ChatMemoryState activate(MemoryScope configured, String fingerprint) {
        require(fingerprint != null && fingerprint.matches("[0-9a-f]{64}"));
        return transaction(() -> {
            coordination.initialize(new ChatMemoryState()
                    .withChatId(configured.chatId())
                    .withUserId(configured.userId())
                    .withCharacterUid(configured.characterUid())
                    .withStoreType(configured.storeType().text())
                    .withGeneration(1L)
                    .withFingerprint(fingerprint));
            ChatMemoryState state =
                    coordination.lock(configured.chatId()).orElseThrow(MemoryLifecycleRepository::failed);
            require(contexts.selectByPrimaryKey(configured.chatId())
                    .filter(context -> configured.userId().equals(context.getUserId()))
                    .isPresent());
            require(!"deleted".equals(state.getStatus()));
            MemoryScopeLedger.retain(coordination, state, coordination.databaseNow());
            boolean ownerChanged = !configured.userId().equals(state.getUserId());
            boolean identityChanged = ownerChanged
                    || !configured.characterUid().equals(state.getCharacterUid())
                    || !configured.storeType().text().equals(state.getStoreType());
            boolean fingerprintChanged = !fingerprint.equals(state.getFingerprint());
            if (!identityChanged && !fingerprintChanged && "active".equals(state.getStatus())) {
                return state;
            }
            LocalDateTime now = coordination.databaseNow();
            abortTurn(state, now);
            if (identityChanged) {
                if (ownerChanged) {
                    disableHistory(configured.chatId());
                    state.setLatestFinalizedId(0L);
                }
                invalidate(state);
                state.setUserId(configured.userId());
                state.setCharacterUid(configured.characterUid());
                state.setStoreType(configured.storeType().text());
            } else if (fingerprintChanged) {
                state.setProfileRevalidationPending((byte) (state.getProfileId() == null ? 0 : 1));
            }
            state.setStatus("active");
            state.setFingerprint(fingerprint);
            revokeClaim(state);
            state.setRetryAt(null);
            state.setRetryAttempts(0);
            state.setDueAt(
                    state.getLatestFinalizedId() > state.getIdleThroughId()
                                    || state.getProfileRevalidationPending() == 1
                            ? state.getLastActivity().plus(properties.getIdleTimeout())
                            : null);
            save(state, now);
            return state;
        });
    }

    /** Caller holds the shared chat coordination lock; credentials must already be encrypted. */
    public boolean updateContext(ChatContext patch) {
        require(patch != null);
        validateChatId(patch.getChatId());
        return transaction(() -> {
            ChatMemoryState state = coordination.lock(patch.getChatId()).orElse(null);
            ChatContext previous =
                    contexts.selectByPrimaryKey(patch.getChatId()).orElse(null);
            if (previous == null) {
                return false;
            }
            if (state != null && !"deleted".equals(state.getStatus()) && memoryInputsChanged(previous, patch)) {
                LocalDateTime now = coordination.databaseNow();
                abortTurn(state, now);
                revokeClaim(state);
                state.setStatus("disabled");
                state.setDueAt(null);
                // Activation needs the previous identity to detect a generation change.
                save(state, now);
            }
            return contexts.updateByPrimaryKeySelective(patch) > 0;
        });
    }

    private static boolean memoryInputsChanged(ChatContext previous, ChatContext patch) {
        // Match selective-update semantics: null means unchanged, while an empty string is a write.
        // These are the context inputs consumed by MemoryModelResolver/ordinary model construction.
        // ext, timestamps, Telegram routing and quota do not change the memory identity or baseline.
        return changed(previous.getUserId(), patch.getUserId())
                || changed(previous.getBackendId(), patch.getBackendId())
                || changed(previous.getUserNickname(), patch.getUserNickname())
                || changed(previous.getCharacterNickname(), patch.getCharacterNickname())
                || changed(previous.getUserProfile(), patch.getUserProfile())
                || changed(previous.getAbout(), patch.getAbout())
                || changed(previous.getApiKeyName(), patch.getApiKeyName())
                || changed(previous.getApiKeyValue(), patch.getApiKeyValue());
    }

    private static boolean changed(Object previous, Object patch) {
        return patch != null && !Objects.equals(previous, patch);
    }

    public void disable(String chatId) {
        validateChatId(chatId);
        transaction(() -> {
            var found = coordination.lock(chatId);
            if (found.isEmpty() || !"active".equals(found.orElseThrow().getStatus())) {
                return null;
            }
            ChatMemoryState state = found.orElseThrow();
            LocalDateTime now = coordination.databaseNow();
            abortTurn(state, now);
            revokeClaim(state);
            state.setStatus("disabled");
            state.setDueAt(null);
            save(state, now);
            return null;
        });
    }

    public void clear(String chatId) {
        validateChatId(chatId);
        transaction(() -> {
            clearState(chatId, false);
            return null;
        });
    }

    public boolean delete(String chatId) {
        validateChatId(chatId);
        return transaction(() -> {
            clearState(chatId, true);
            return contexts.deleteByPrimaryKey(chatId) > 0;
        });
    }

    public List<Long> rollback(String chatId, int count) {
        validateChatId(chatId);
        require(count > 0);
        return transaction(() -> {
            ChatMemoryState state = coordination.lock(chatId).orElse(null);
            if (state != null) {
                require(!"deleted".equals(state.getStatus()));
            }
            List<ChatHistory> rows =
                    histories.select(query -> query.where(ChatHistoryDynamicSqlSupport.memoryId, isEqualTo(chatId))
                            .and(ChatHistoryDynamicSqlSupport.enabled, isEqualTo((byte) 1))
                            .and(ChatHistoryDynamicSqlSupport.message, isNotNull())
                            .orderBy(ChatHistoryDynamicSqlSupport.id.descending())
                            .limit(count));
            List<Long> ids = rows.stream().map(ChatHistory::getId).toList();
            if (ids.isEmpty()) {
                return ids;
            }
            LocalDateTime now = coordination.databaseNow();
            if (state != null) {
                abortTurn(state, now);
            }
            histories.update(query -> query.set(ChatHistoryDynamicSqlSupport.enabled)
                    .equalTo((byte) 0)
                    .where(ChatHistoryDynamicSqlSupport.memoryId, isEqualTo(chatId))
                    .and(ChatHistoryDynamicSqlSupport.id, isIn(ids)));
            List<String> turns = rows.stream()
                    .map(ChatHistory::getTurnId)
                    .filter(Objects::nonNull)
                    .distinct()
                    .toList();
            if (!turns.isEmpty()) {
                histories.update(query -> query.set(ChatHistoryDynamicSqlSupport.recordKind)
                        .equalTo("turn-abort")
                        .where(ChatHistoryDynamicSqlSupport.memoryId, isEqualTo(chatId))
                        .and(ChatHistoryDynamicSqlSupport.turnId, isIn(turns))
                        .and(ChatHistoryDynamicSqlSupport.recordKind, isIn("turn-complete", "turn-abort")));
            }
            if (state != null) {
                invalidate(state);
                state.setDueAt(
                        "active".equals(state.getStatus()) && state.getLatestFinalizedId() > 0
                                ? state.getLastActivity().plus(properties.getIdleTimeout())
                                : null);
                save(state, now);
            }
            return ids;
        });
    }

    private void clearState(String chatId, boolean deleted) {
        if (deleted) {
            // An unactivated chat still needs a durable barrier against reuse of its identifier.
            coordination.initialize(new ChatMemoryState()
                    .withChatId(chatId)
                    .withUserId("deleted")
                    .withCharacterUid("deleted")
                    .withStoreType("default_long_term_memory")
                    .withGeneration(1L)
                    .withFingerprint(MemoryDocumentCodec.hash("deleted")));
        }
        ChatMemoryState state = coordination.lock(chatId).orElse(null);
        LocalDateTime now = coordination.databaseNow();
        MemoryScopeLedger.markLegacy(coordination, chatId, now, properties.getGcGracePeriod());
        disableHistory(chatId);
        if (state == null) {
            return;
        }
        invalidate(state);
        state.setLatestFinalizedId(0L);
        state.setTurnToken(null);
        state.setTurnLeaseUntil(null);
        state.setTurnDeadline(null);
        state.setTurnRevision(state.getTurnRevision() + 1);
        state.setLastActivity(now);
        state.setDueAt(null);
        if (deleted) {
            state.setStatus("deleted");
        }
        save(state, now);
    }

    private void invalidate(ChatMemoryState state) {
        MemoryScopeLedger.retain(coordination, state, coordination.databaseNow());
        state.setGeneration(Math.addExact(state.getGeneration(), 1));
        state.setOverflowThroughId(0L);
        state.setIdleThroughId(0L);
        state.setSummaryId(null);
        state.setProfileId(null);
        state.setProfileRevalidationPending((byte) 0);
        state.setReconciledThroughId(0L);
        state.setRetryAt(null);
        state.setRetryAttempts(0);
        revokeClaim(state);
    }

    private static void revokeClaim(ChatMemoryState state) {
        state.setClaimToken(null);
        state.setClaimLeaseUntil(null);
        state.setClaimDeadline(null);
    }

    private void abortTurn(ChatMemoryState state, LocalDateTime now) {
        if (state.getTurnToken() == null) {
            return;
        }
        ChatHistory marker = new ChatHistory()
                .withMemoryId(state.getChatId())
                .withTurnId(state.getTurnToken())
                .withRecordKind("turn-abort")
                .withEpisode(state.getEpisode())
                .withGmtCreate(now)
                .withGmtModified(now);
        require(histories.insertSelective(marker) == 1 && marker.getId() != null);
        state.setLatestFinalizedId(marker.getId());
        state.setTurnToken(null);
        state.setTurnLeaseUntil(null);
        state.setTurnDeadline(null);
        state.setTurnRevision(state.getTurnRevision() + 1);
        state.setLastActivity(now);
    }

    private void disableHistory(String chatId) {
        histories.update(query -> query.set(ChatHistoryDynamicSqlSupport.enabled)
                .equalTo((byte) 0)
                .where(ChatHistoryDynamicSqlSupport.memoryId, isEqualTo(chatId))
                .and(ChatHistoryDynamicSqlSupport.enabled, isEqualTo((byte) 1)));
    }

    private void save(ChatMemoryState state, LocalDateTime now) {
        state.setVersion(state.getVersion() + 1);
        state.setGmtModified(now);
        require(states.updateByPrimaryKey(state) == 1);
    }

    private static void validateChatId(String chatId) {
        require(chatId != null
                && !chatId.isBlank()
                && chatId.length() <= 32
                && chatId.chars().noneMatch(Character::isISOControl));
    }

    private static void require(boolean valid) {
        if (!valid) {
            throw failed();
        }
    }

    private static IllegalStateException failed() {
        return new IllegalStateException("Memory lifecycle transaction failed");
    }

    private <T> T transaction(Supplier<T> action) {
        boolean[] fatal = {false};
        try {
            return transactions.execute(status -> {
                try {
                    return action.get();
                } catch (Error ignored) {
                    fatal[0] = true;
                    throw new Error("Memory lifecycle transaction failed");
                } catch (Throwable ignored) {
                    throw failed();
                }
            });
        } catch (Throwable failure) {
            if (fatal[0] || failure instanceof Error) {
                throw new Error("Memory lifecycle transaction failed");
            }
            throw failed();
        }
    }
}
