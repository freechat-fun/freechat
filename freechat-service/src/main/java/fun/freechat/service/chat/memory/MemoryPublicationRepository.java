package fun.freechat.service.chat.memory;

import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;
import static org.mybatis.dynamic.sql.SqlBuilder.isGreaterThan;
import static org.mybatis.dynamic.sql.SqlBuilder.isLessThanOrEqualTo;

import com.fasterxml.jackson.databind.JsonNode;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.output.TokenUsage;
import fun.freechat.mapper.ChatHistoryDynamicSqlSupport;
import fun.freechat.mapper.ChatHistoryMapper;
import fun.freechat.mapper.ChatMemoryCommitDynamicSqlSupport;
import fun.freechat.mapper.ChatMemoryCommitMapper;
import fun.freechat.mapper.ChatMemoryCoordinationMapper;
import fun.freechat.mapper.ChatMemoryStateMapper;
import fun.freechat.model.ChatHistory;
import fun.freechat.model.ChatMemoryCommit;
import fun.freechat.model.ChatMemoryState;
import fun.freechat.service.util.InfoUtils;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

public final class MemoryPublicationRepository {
    private final ChatMemoryCoordinationMapper coordination;
    private final ChatMemoryStateMapper states;
    private final ChatMemoryCommitMapper commits;
    private final ChatHistoryMapper histories;
    private final TransactionTemplate transactions;
    private final LongTermMemoryProperties properties;

    public MemoryPublicationRepository(
            ChatMemoryCoordinationMapper coordination,
            ChatMemoryStateMapper states,
            ChatMemoryCommitMapper commits,
            ChatHistoryMapper histories,
            PlatformTransactionManager transactions,
            LongTermMemoryProperties properties) {
        this.coordination = coordination;
        this.states = states;
        this.commits = commits;
        this.histories = histories;
        this.transactions = new TransactionTemplate(transactions);
        this.transactions.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        this.transactions.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        this.properties = properties;
    }

    public Snapshot snapshot(MemoryScope scope, Operation operation, long throughId, String leaseToken) {
        return transaction(() -> {
            ChatMemoryState state = lock(scope);
            LocalDateTime now = coordination.databaseNow();
            checkLease(state, operation, leaseToken, now);
            long cursor = cursor(state, operation);
            if (operation == Operation.REVALIDATE) {
                require(state.getProfileRevalidationPending() == 1 && throughId == cursor);
            } else {
                require(throughId > cursor && throughId <= state.getLatestFinalizedId());
                requireFinalBoundary(scope, throughId);
            }
            return new Snapshot(
                    scope,
                    UUID.randomUUID().toString(),
                    operation,
                    cursor + 1,
                    throughId,
                    cursor,
                    head(state, operation),
                    leaseToken,
                    state.getFingerprint(),
                    sourceHash(scope, cursor, throughId));
        });
    }

    public void prepare(Snapshot snapshot, MemoryManifest manifest, String modelId, TokenUsage usage) {
        require(modelId != null && !modelId.isBlank() && modelId.length() <= 98);
        validateManifest(snapshot.operation(), manifest);
        inTransaction(() -> {
            ChatMemoryState state = lock(snapshot.scope());
            LocalDateTime now = coordination.databaseNow();
            validateSnapshot(state, snapshot, now);
            require(sourceHash(snapshot.scope(), snapshot.expectedCursor(), snapshot.sourceEndId())
                    .equals(snapshot.sourceHash()));
            MemoryScopeLedger.retain(coordination, state, now);
            ChatMemoryCommit attempt = new ChatMemoryCommit()
                    .withAttemptId(snapshot.attemptId())
                    .withChatId(snapshot.scope().chatId())
                    .withGeneration(snapshot.scope().generation())
                    .withOperation(snapshot.operation().name())
                    .withSourceStartId(snapshot.operation() == Operation.REVALIDATE ? 0L : snapshot.sourceStartId())
                    .withSourceEndId(snapshot.sourceEndId())
                    .withExpectedCursor(snapshot.expectedCursor())
                    .withExpectedHead(snapshot.expectedHead())
                    .withLeaseToken(snapshot.leaseToken())
                    .withStatus("prepared")
                    .withFingerprint(snapshot.fingerprint())
                    .withSchemaVersion(MemoryScope.SCHEMA_VERSION)
                    .withModelId(modelId)
                    .withManifest(MemoryDocumentCodec.encodeManifest(manifest))
                    .withProgress("{\"sourceHash\":\"" + snapshot.sourceHash() + "\"}")
                    .withTokenUsage(usage == null ? null : InfoUtils.serialize(usage))
                    .withLeaseUntil(deadline(state, snapshot.operation()))
                    .withGmtCreate(now)
                    .withGmtModified(now);
            checkLease(state, snapshot.operation(), snapshot.leaseToken(), coordination.databaseNow());
            require(commits.insertSelective(attempt) == 1);
        });
    }

    public void checkSnapshot(Snapshot snapshot) {
        inTransaction(() -> {
            ChatMemoryState state = lock(snapshot.scope());
            validateSnapshot(state, snapshot, coordination.databaseNow());
            require(sourceHash(snapshot.scope(), snapshot.expectedCursor(), snapshot.sourceEndId())
                    .equals(snapshot.sourceHash()));
            checkLease(state, snapshot.operation(), snapshot.leaseToken(), coordination.databaseNow());
        });
    }

    public void checkPrepared(MemoryScope scope, String attemptId) {
        inTransaction(() -> {
            ChatMemoryState state = lock(scope);
            ChatMemoryCommit attempt = lockAttempt(scope, attemptId);
            LocalDateTime now = coordination.databaseNow();
            require("prepared".equals(attempt.getStatus()) && now.isBefore(attempt.getLeaseUntil()));
            validateSnapshot(state, fromAttempt(scope, attempt), now);
        });
    }

    public void publish(MemoryScope scope, String attemptId) {
        inTransaction(() -> {
            ChatMemoryState state = lock(scope);
            ChatMemoryCommit attempt = lockAttempt(scope, attemptId);
            LocalDateTime now = coordination.databaseNow();
            require("prepared".equals(attempt.getStatus()) && now.isBefore(attempt.getLeaseUntil()));
            Snapshot snapshot = fromAttempt(scope, attempt);
            validateSnapshot(state, snapshot, now);
            require(sourceHash(scope, snapshot.expectedCursor(), snapshot.sourceEndId())
                    .equals(snapshot.sourceHash()));
            if (snapshot.operation() != Operation.REVALIDATE) {
                requireFinalBoundary(scope, snapshot.sourceEndId());
            }
            MemoryManifest manifest = MemoryDocumentCodec.decodeManifest(attempt.getManifest());
            validateManifest(snapshot.operation(), manifest);
            if (snapshot.operation() == Operation.OVERFLOW) {
                state.setSummaryId(manifest.entries().stream()
                        .filter(entry -> entry.kind() == MemoryDocument.Kind.WINDOW_SUMMARY && !entry.archive())
                        .findFirst()
                        .orElseThrow()
                        .id());
                state.setOverflowThroughId(snapshot.sourceEndId());
            } else {
                state.setProfileId(manifest.entries().stream()
                        .filter(entry -> entry.kind() == MemoryDocument.Kind.PROFILE_SNAPSHOT)
                        .findFirst()
                        .orElseThrow()
                        .id());
                state.setProfileRevalidationPending((byte) 0);
                if (snapshot.operation() == Operation.IDLE) {
                    state.setIdleThroughId(snapshot.sourceEndId());
                }
                state.setRetryAttempts(0);
                state.setRetryAt(null);
                if (state.getTurnToken() == null && state.getIdleThroughId().equals(state.getLatestFinalizedId())) {
                    state.setDueAt(null);
                }
            }
            state.setVersion(state.getVersion() + 1);
            state.setGmtModified(now);
            attempt.setStatus("committed");
            attempt.setGmtModified(now);
            checkLease(state, snapshot.operation(), snapshot.leaseToken(), coordination.databaseNow());
            require(states.updateByPrimaryKey(state) == 1 && commits.updateByPrimaryKey(attempt) == 1);
            if (snapshot.operation() != Operation.REVALIDATE) {
                checkpointHead(snapshot)
                        .filter(row -> matchesCheckpoint(snapshot, row))
                        .ifPresent(row -> retireCheckpoint(row, now));
            }
        });
    }

    public void advanceAborted(Snapshot snapshot) {
        inTransaction(() -> {
            require(snapshot.operation() != Operation.REVALIDATE);
            ChatMemoryState state = lock(snapshot.scope());
            LocalDateTime now = coordination.databaseNow();
            validateSnapshot(state, snapshot, now);
            require(sourceHash(snapshot.scope(), snapshot.expectedCursor(), snapshot.sourceEndId())
                    .equals(snapshot.sourceHash()));
            ChatHistory terminal = histories
                    .selectByPrimaryKey(snapshot.sourceEndId())
                    .orElseThrow(MemoryPublicationRepository::conflict);
            require(snapshot.scope().chatId().equals(terminal.getMemoryId())
                    && terminal.getEnabled() == 1
                    && "turn-abort".equals(terminal.getRecordKind())
                    && MemoryDocumentCodec.canonicalUuid(terminal.getTurnId()));
            long after = snapshot.expectedCursor();
            boolean started = false;
            while (after < snapshot.sourceEndId()) {
                long cursor = after;
                List<ChatHistory> page = sourceRows(
                        snapshot.scope(),
                        query -> query.where(
                                        ChatHistoryDynamicSqlSupport.memoryId,
                                        isEqualTo(snapshot.scope().chatId()))
                                .and(ChatHistoryDynamicSqlSupport.enabled, isEqualTo((byte) 1))
                                .and(ChatHistoryDynamicSqlSupport.id, isGreaterThan(cursor))
                                .and(ChatHistoryDynamicSqlSupport.id, isLessThanOrEqualTo(snapshot.sourceEndId()))
                                .orderBy(ChatHistoryDynamicSqlSupport.id)
                                .limit(64));
                require(!page.isEmpty());
                for (ChatHistory row : page) {
                    boolean prefix = row.getTurnId() == null
                            && "message".equals(row.getRecordKind())
                            && (MemoryTurnRepository.Origin.SYSTEM.text().equals(row.getMessageOrigin())
                                    || MemoryTurnRepository.Origin.TEMPLATE_EXAMPLE
                                            .text()
                                            .equals(row.getMessageOrigin()));
                    if (!started && prefix) {
                        continue;
                    }
                    require(terminal.getTurnId().equals(row.getTurnId())
                            && Objects.equals(terminal.getEpisode(), row.getEpisode()));
                    boolean singleAbort = row.getId().equals(terminal.getId())
                            && "turn-abort".equals(row.getRecordKind())
                            && row.getMessage() != null;
                    require(
                            !started
                                    ? "turn-start".equals(row.getRecordKind()) || singleAbort
                                    : row.getId().equals(terminal.getId())
                                            ? "turn-abort".equals(row.getRecordKind())
                                            : "message".equals(row.getRecordKind()));
                    started = true;
                }
                after = page.getLast().getId();
            }
            require(started);
            if (snapshot.operation() == Operation.OVERFLOW) {
                state.setOverflowThroughId(snapshot.sourceEndId());
            } else {
                state.setIdleThroughId(snapshot.sourceEndId());
                state.setRetryAt(null);
                state.setRetryAttempts(0);
                if (state.getTurnToken() == null
                        && state.getProfileRevalidationPending() == 0
                        && state.getIdleThroughId().equals(state.getLatestFinalizedId())) {
                    state.setDueAt(null);
                }
            }
            state.setVersion(state.getVersion() + 1);
            state.setGmtModified(now);
            checkLease(state, snapshot.operation(), snapshot.leaseToken(), coordination.databaseNow());
            require(states.updateByPrimaryKey(state) == 1);
            require(commits.insertSelective(new ChatMemoryCommit()
                            .withAttemptId(snapshot.attemptId())
                            .withChatId(snapshot.scope().chatId())
                            .withGeneration(snapshot.scope().generation())
                            .withOperation("SKIP_" + snapshot.operation().name())
                            .withStatus("skipped")
                            .withManifest("{}")
                            .withSourceStartId(snapshot.sourceStartId())
                            .withSourceEndId(snapshot.sourceEndId())
                            .withExpectedCursor(snapshot.expectedCursor())
                            .withExpectedHead(snapshot.expectedHead())
                            .withLeaseToken(snapshot.leaseToken())
                            .withFingerprint(snapshot.fingerprint())
                            .withSchemaVersion(MemoryScope.SCHEMA_VERSION)
                            .withModelId("none")
                            .withProgress("{\"sourceHash\":\"" + snapshot.sourceHash() + "\"}")
                            .withLeaseUntil(deadline(state, snapshot.operation()))
                            .withGmtCreate(now)
                            .withGmtModified(now))
                    == 1);
        });
    }

    public Optional<AuthorizedRecord> authorize(MemoryScope scope, String attemptId, String recordId) {
        return transaction(() -> {
            lock(scope);
            Optional<ChatMemoryCommit> found = commits.selectByPrimaryKey(attemptId);
            if (found.isEmpty()) {
                return Optional.empty();
            }
            ChatMemoryCommit attempt = found.orElseThrow();
            if (!scope.chatId().equals(attempt.getChatId())
                    || scope.generation() != attempt.getGeneration()
                    || !"committed".equals(attempt.getStatus())
                    || attempt.getSchemaVersion() != MemoryScope.SCHEMA_VERSION
                    || "USAGE".equals(attempt.getOperation())
                    || "CHECKPOINT_OVERFLOW".equals(attempt.getOperation())
                    || "CHECKPOINT_IDLE".equals(attempt.getOperation())) {
                return Optional.empty();
            }
            return MemoryDocumentCodec.decodeManifest(attempt.getManifest())
                    .find(recordId)
                    .map(entry -> new AuthorizedRecord(attemptId, attempt.getFingerprint(), entry));
        });
    }

    public void terminate(MemoryScope scope, String attemptId, Failure failure) {
        inTransaction(() -> {
            coordination.lock(scope.chatId()).orElseThrow(MemoryPublicationRepository::conflict);
            ChatMemoryCommit attempt = lockAttempt(scope, attemptId);
            require(!"committed".equals(attempt.getStatus()));
            if (!"prepared".equals(attempt.getStatus())) {
                return;
            }
            LocalDateTime now = coordination.databaseNow();
            attempt.setStatus("terminal");
            attempt.setErrorCategory(failure.name());
            attempt.setGcAfter(now.plus(properties.getGcGracePeriod()));
            attempt.setGmtModified(now);
            require(commits.updateByPrimaryKey(attempt) == 1);
        });
    }

    public void checkTerminal(MemoryScope scope, String attemptId) {
        inTransaction(() -> {
            coordination.lock(scope.chatId()).orElseThrow(MemoryPublicationRepository::conflict);
            ChatMemoryCommit attempt = lockAttempt(scope, attemptId);
            require(!"USAGE".equals(attempt.getOperation())
                    && "terminal".equals(attempt.getStatus())
                    && attempt.getGcAfter() != null
                    && !coordination.databaseNow().isBefore(attempt.getGcAfter()));
        });
    }

    public void recordTurnUsage(
            MemoryTurnRepository.TurnLease turn, String fingerprint, String callId, String modelId, TokenUsage usage) {
        inTransaction(() -> {
            require(MemoryDocumentCodec.canonicalUuid(callId) && MemoryDocumentCodec.canonicalUuid(turn.token()));
            require(modelId != null && !modelId.isBlank() && modelId.length() <= 98);
            require(fingerprint != null && fingerprint.matches("[0-9a-f]{64}"));
            coordination.lock(turn.scope().chatId());
            String accounting = usage == null ? null : InfoUtils.serialize(usage);
            var existing = commits.selectByPrimaryKey(callId);
            if (existing.isPresent()) {
                ChatMemoryCommit row = existing.orElseThrow();
                require("CHAT_USAGE".equals(row.getOperation())
                        && "usage".equals(row.getStatus())
                        && turn.scope().chatId().equals(row.getChatId())
                        && turn.scope().generation() == row.getGeneration()
                        && turn.token().equals(row.getLeaseToken())
                        && turn.startId() == row.getSourceStartId()
                        && modelId.equals(row.getModelId())
                        && fingerprint.equals(row.getFingerprint())
                        && Objects.equals(jsonTree(accounting), jsonTree(row.getTokenUsage())));
                return;
            }
            LocalDateTime now = coordination.databaseNow();
            require(commits.insertSelective(new ChatMemoryCommit()
                            .withAttemptId(callId)
                            .withChatId(turn.scope().chatId())
                            .withGeneration(turn.scope().generation())
                            .withOperation("CHAT_USAGE")
                            .withStatus("usage")
                            .withManifest("{}")
                            .withSourceStartId(turn.startId())
                            .withSourceEndId(turn.startId())
                            .withExpectedCursor(0L)
                            .withLeaseToken(turn.token())
                            .withFingerprint(fingerprint)
                            .withSchemaVersion(MemoryScope.SCHEMA_VERSION)
                            .withModelId(modelId)
                            .withTokenUsage(accounting)
                            .withLeaseUntil(turn.deadline())
                            .withGmtCreate(now)
                            .withGmtModified(now))
                    == 1);
        });
    }

    /** Accounting is independent of publication eligibility, including after lease/generation expiry. */
    public void recordUsage(Snapshot snapshot, String callId, String modelId, TokenUsage usage) {
        inTransaction(() -> {
            require(MemoryDocumentCodec.canonicalUuid(callId)
                    && MemoryDocumentCodec.canonicalUuid(snapshot.attemptId()));
            require(modelId != null && !modelId.isBlank() && modelId.length() <= 98);
            // Serialize duplicate callbacks, but deliberately do not require an active generation or lease.
            coordination.lock(snapshot.scope().chatId());
            var progress = InfoUtils.defaultMapper()
                    .createObjectNode()
                    .put("sourceHash", snapshot.sourceHash())
                    .put("snapshotAttemptId", snapshot.attemptId())
                    .put("sourceOperation", snapshot.operation().name())
                    .put(
                            "scopeHash",
                            MemoryDocumentCodec.hash(InfoUtils.defaultMapper()
                                    .valueToTree(snapshot.scope())
                                    .toString()));
            String accounting = usage == null ? null : InfoUtils.serialize(usage);
            Optional<ChatMemoryCommit> existing = commits.selectByPrimaryKey(callId);
            if (existing.isPresent()) {
                ChatMemoryCommit row = existing.orElseThrow();
                require("USAGE".equals(row.getOperation())
                        && "usage".equals(row.getStatus())
                        && snapshot.scope().chatId().equals(row.getChatId())
                        && snapshot.scope().generation() == row.getGeneration()
                        && modelId.equals(row.getModelId())
                        && row.getSchemaVersion() == MemoryScope.SCHEMA_VERSION
                        && snapshot.sourceStartId() == row.getSourceStartId()
                        && snapshot.sourceEndId() == row.getSourceEndId()
                        && snapshot.expectedCursor() == row.getExpectedCursor()
                        && Objects.equals(snapshot.expectedHead(), row.getExpectedHead())
                        && Objects.equals(snapshot.leaseToken(), row.getLeaseToken())
                        && Objects.equals(snapshot.fingerprint(), row.getFingerprint())
                        && progress.equals(jsonTree(row.getProgress()))
                        && Objects.equals(jsonTree(accounting), jsonTree(row.getTokenUsage())));
                return;
            }
            LocalDateTime now = coordination.databaseNow();
            require(commits.insertSelective(new ChatMemoryCommit()
                            .withAttemptId(callId)
                            .withChatId(snapshot.scope().chatId())
                            .withGeneration(snapshot.scope().generation())
                            .withOperation("USAGE")
                            .withStatus("usage")
                            .withManifest("{}")
                            .withSourceStartId(snapshot.sourceStartId())
                            .withSourceEndId(snapshot.sourceEndId())
                            .withExpectedCursor(snapshot.expectedCursor())
                            .withExpectedHead(snapshot.expectedHead())
                            .withLeaseToken(snapshot.leaseToken())
                            .withFingerprint(snapshot.fingerprint())
                            .withSchemaVersion(MemoryScope.SCHEMA_VERSION)
                            .withModelId(modelId)
                            .withProgress(progress.toString())
                            .withTokenUsage(accounting)
                            // Required SQL column; usage rows have no publication lease and never enter vector GC.
                            .withLeaseUntil(now)
                            .withGmtCreate(now)
                            .withGmtModified(now))
                    == 1);
        });
    }

    // These entry points each own a short isolated transaction. Never call them while holding the state lock.
    Optional<Checkpoint> loadCheckpoint(Snapshot snapshot) {
        return transaction(() -> {
            validateCheckpointSnapshot(snapshot);
            Optional<Checkpoint> result = checkpointHead(snapshot)
                    .filter(row -> matchesCheckpoint(snapshot, row))
                    .map(row -> checkpoint(snapshot, row));
            checkLease(lock(snapshot.scope()), snapshot.operation(), snapshot.leaseToken(), coordination.databaseNow());
            return result;
        });
    }

    void prepareCheckpoint(
            Snapshot snapshot,
            String attemptId,
            String predecessor,
            MemorySourceReader.Position position,
            MemoryManifest manifest,
            String modelId,
            TokenUsage usage) {
        inTransaction(() -> {
            ChatMemoryState state = validateCheckpointSnapshot(snapshot);
            require(MemoryDocumentCodec.canonicalUuid(attemptId)
                    && !snapshot.attemptId().equals(attemptId));
            require(modelId != null && !modelId.isBlank() && modelId.length() <= 98);
            checkpointEntry(manifest);
            Optional<ChatMemoryCommit> current =
                    checkpointHead(snapshot).filter(row -> matchesCheckpoint(snapshot, row));
            require(Objects.equals(
                    predecessor, current.map(ChatMemoryCommit::getAttemptId).orElse(null)));
            MemorySourceReader.Position previous = current.map(
                            row -> checkpoint(snapshot, row).position())
                    .orElse(new MemorySourceReader.Position(snapshot.expectedCursor(), 0));
            validatePosition(snapshot, position);
            require(advances(previous, position));
            var progress = InfoUtils.defaultMapper().createObjectNode().put("sourceHash", snapshot.sourceHash());
            progress.putObject("position").put("rowId", position.rowId()).put("offset", position.offset());
            progress.put("predecessor", predecessor);
            LocalDateTime now = coordination.databaseNow();
            checkLease(state, snapshot.operation(), snapshot.leaseToken(), now);
            MemoryScopeLedger.retain(coordination, state, now);
            require(commits.insertSelective(new ChatMemoryCommit()
                            .withAttemptId(attemptId)
                            .withChatId(snapshot.scope().chatId())
                            .withGeneration(snapshot.scope().generation())
                            .withOperation(checkpointOperation(snapshot))
                            .withStatus("prepared")
                            .withSourceStartId(snapshot.sourceStartId())
                            .withSourceEndId(snapshot.sourceEndId())
                            .withExpectedCursor(snapshot.expectedCursor())
                            .withExpectedHead(snapshot.expectedHead())
                            .withLeaseToken(snapshot.leaseToken())
                            .withFingerprint(snapshot.fingerprint())
                            .withSchemaVersion(MemoryScope.SCHEMA_VERSION)
                            .withModelId(modelId)
                            .withManifest(MemoryDocumentCodec.encodeManifest(manifest))
                            .withProgress(progress.toString())
                            .withTokenUsage(usage == null ? null : InfoUtils.serialize(usage))
                            .withLeaseUntil(deadline(state, snapshot.operation()))
                            .withGmtCreate(now)
                            .withGmtModified(now))
                    == 1);
        });
    }

    void checkPreparedCheckpoint(Snapshot snapshot, String attemptId) {
        inTransaction(() -> preparedCheckpoint(snapshot, attemptId));
    }

    void publishCheckpoint(Snapshot snapshot, String attemptId) {
        inTransaction(() -> {
            ChatMemoryCommit attempt = preparedCheckpoint(snapshot, attemptId);
            Checkpoint prepared = checkpoint(snapshot, attempt);
            Optional<ChatMemoryCommit> current = checkpointHead(snapshot);
            Optional<ChatMemoryCommit> matching = current.filter(row -> matchesCheckpoint(snapshot, row));
            require(Objects.equals(
                    prepared.predecessor(),
                    matching.map(ChatMemoryCommit::getAttemptId).orElse(null)));
            LocalDateTime now = coordination.databaseNow();
            current.ifPresent(row -> retireCheckpoint(row, now));
            attempt.setStatus("checkpoint");
            attempt.setGmtModified(now);
            checkLease(lock(snapshot.scope()), snapshot.operation(), snapshot.leaseToken(), coordination.databaseNow());
            require(commits.updateByPrimaryKey(attempt) == 1);
        });
    }

    private ChatMemoryCommit preparedCheckpoint(Snapshot snapshot, String attemptId) {
        ChatMemoryState state = validateCheckpointSnapshot(snapshot);
        ChatMemoryCommit attempt = lockAttempt(snapshot.scope(), attemptId);
        require("prepared".equals(attempt.getStatus())
                && matchesCheckpoint(snapshot, attempt)
                && snapshot.leaseToken().equals(attempt.getLeaseToken())
                && attempt.getLeaseUntil() != null
                && coordination.databaseNow().isBefore(attempt.getLeaseUntil()));
        Checkpoint prepared = checkpoint(snapshot, attempt);
        Optional<ChatMemoryCommit> current = checkpointHead(snapshot).filter(row -> matchesCheckpoint(snapshot, row));
        require(Objects.equals(
                prepared.predecessor(),
                current.map(ChatMemoryCommit::getAttemptId).orElse(null)));
        require(advances(
                current.map(row -> checkpoint(snapshot, row).position())
                        .orElse(new MemorySourceReader.Position(snapshot.expectedCursor(), 0)),
                prepared.position()));
        checkLease(state, snapshot.operation(), snapshot.leaseToken(), coordination.databaseNow());
        return attempt;
    }

    private static boolean advances(MemorySourceReader.Position previous, MemorySourceReader.Position next) {
        return next.rowId() > previous.rowId()
                || next.rowId() == previous.rowId()
                        && previous.offset() > 0
                        && (next.offset() == 0 || next.offset() > previous.offset());
    }

    private ChatMemoryState validateCheckpointSnapshot(Snapshot snapshot) {
        require(snapshot.operation() != Operation.REVALIDATE);
        ChatMemoryState state = lock(snapshot.scope());
        validateSnapshot(state, snapshot, coordination.databaseNow());
        require(snapshot.sourceStartId() == snapshot.expectedCursor() + 1
                && snapshot.sourceEndId() > snapshot.expectedCursor()
                && snapshot.sourceEndId() <= state.getLatestFinalizedId());
        requireFinalBoundary(snapshot.scope(), snapshot.sourceEndId());
        require(sourceHash(snapshot.scope(), snapshot.expectedCursor(), snapshot.sourceEndId())
                .equals(snapshot.sourceHash()));
        checkLease(state, snapshot.operation(), snapshot.leaseToken(), coordination.databaseNow());
        return state;
    }

    /** The state-row lock serializes every chain writer; terminal IDs are never removed or reused. */
    private Optional<ChatMemoryCommit> checkpointHead(Snapshot snapshot) {
        List<ChatMemoryCommit> heads = commits.select(query -> query.where(
                        ChatMemoryCommitDynamicSqlSupport.chatId,
                        isEqualTo(snapshot.scope().chatId()))
                .and(
                        ChatMemoryCommitDynamicSqlSupport.generation,
                        isEqualTo(snapshot.scope().generation()))
                .and(ChatMemoryCommitDynamicSqlSupport.operation, isEqualTo(checkpointOperation(snapshot)))
                .and(ChatMemoryCommitDynamicSqlSupport.sourceStartId, isEqualTo(snapshot.sourceStartId()))
                .and(ChatMemoryCommitDynamicSqlSupport.sourceEndId, isEqualTo(snapshot.sourceEndId()))
                .and(ChatMemoryCommitDynamicSqlSupport.status, isEqualTo("checkpoint"))
                .limit(2));
        require(heads.size() <= 1);
        return heads.stream().findFirst();
    }

    private static boolean matchesCheckpoint(Snapshot snapshot, ChatMemoryCommit row) {
        return checkpointOperation(snapshot).equals(row.getOperation())
                && snapshot.scope().chatId().equals(row.getChatId())
                && snapshot.scope().generation() == row.getGeneration()
                && row.getSchemaVersion() == MemoryScope.SCHEMA_VERSION
                && snapshot.sourceStartId() == row.getSourceStartId()
                && snapshot.sourceEndId() == row.getSourceEndId()
                && snapshot.expectedCursor() == row.getExpectedCursor()
                && Objects.equals(snapshot.expectedHead(), row.getExpectedHead())
                && snapshot.fingerprint().equals(row.getFingerprint())
                && snapshot.sourceHash()
                        .equals(checkpointProgress(row).path("sourceHash").textValue());
    }

    private Checkpoint checkpoint(Snapshot snapshot, ChatMemoryCommit row) {
        var progress = checkpointProgress(row);
        var location = progress.get("position");
        MemorySourceReader.Position position = new MemorySourceReader.Position(
                location.get("rowId").longValue(), location.get("offset").intValue());
        validatePosition(snapshot, position);
        return new Checkpoint(
                row.getAttemptId(),
                position,
                progress.get("predecessor").textValue(),
                checkpointEntry(MemoryDocumentCodec.decodeManifest(row.getManifest())));
    }

    private static JsonNode checkpointProgress(ChatMemoryCommit row) {
        require(row.getProgress() != null && MemoryBounds.bytes(row.getProgress()) <= 1024);
        var progress = jsonTree(row.getProgress());
        require(progress != null
                && progress.isObject()
                && progress.size() == 3
                && progress.path("sourceHash").isTextual()
                && progress.path("sourceHash").textValue().matches("[0-9a-f]{64}"));
        var position = progress.path("position");
        require(position.isObject()
                && position.size() == 2
                && position.path("rowId").isIntegralNumber()
                && position.path("rowId").canConvertToLong()
                && position.path("offset").isIntegralNumber()
                && position.path("offset").canConvertToInt());
        var predecessor = progress.get("predecessor");
        require(predecessor != null
                && (predecessor.isNull()
                        || predecessor.isTextual() && MemoryDocumentCodec.canonicalUuid(predecessor.textValue())));
        return progress;
    }

    private void validatePosition(Snapshot snapshot, MemorySourceReader.Position position) {
        require(position != null
                && position.rowId() > snapshot.expectedCursor()
                && position.rowId() <= snapshot.sourceEndId()
                && position.offset() >= 0);
        ChatHistory row =
                histories.selectByPrimaryKey(position.rowId()).orElseThrow(MemoryPublicationRepository::conflict);
        require(snapshot.scope().chatId().equals(row.getMemoryId()) && row.getEnabled() == 1);
        if (position.offset() > 0) {
            require(row.getMessage() != null
                    && ("message".equals(row.getRecordKind())
                            || "turn-start".equals(row.getRecordKind())
                            || "turn-complete".equals(row.getRecordKind())));
            // Legacy boundaries carry evidence; the final row may still have unread code points.
            var message = ChatMessageDeserializer.messageFromJson(
                    row.getSourceMessage() == null ? row.getMessage() : row.getSourceMessage());
            require(!"turn-start".equals(row.getRecordKind())
                    || message instanceof UserMessage
                            && MemoryTurnRepository.Origin.USER_INPUT.text().equals(row.getMessageOrigin()));
            require(!"turn-complete".equals(row.getRecordKind())
                    || message instanceof AiMessage ai
                            && !ai.hasToolExecutionRequests()
                            && MemoryTurnRepository.Origin.ASSISTANT_OUTPUT
                                    .text()
                                    .equals(row.getMessageOrigin()));
            String text;
            if (message instanceof UserMessage user) {
                text = user.contents().stream()
                        .filter(TextContent.class::isInstance)
                        .map(TextContent.class::cast)
                        .map(TextContent::text)
                        .collect(java.util.stream.Collectors.joining("\n"));
            } else if (message instanceof AiMessage ai) {
                text = ai.text();
            } else if (message instanceof ToolExecutionResultMessage tool) {
                text = tool.text();
            } else {
                throw conflict();
            }
            require(text != null && position.offset() < text.codePointCount(0, text.length()));
        }
    }

    private static MemoryManifest.Entry checkpointEntry(MemoryManifest manifest) {
        require(manifest.entries().size() == 1);
        MemoryManifest.Entry entry = manifest.entries().getFirst();
        require(entry.kind() == MemoryDocument.Kind.EXTRACTION_CHECKPOINT && !entry.archive());
        return entry;
    }

    private void retireCheckpoint(ChatMemoryCommit row, LocalDateTime now) {
        require("checkpoint".equals(row.getStatus()));
        row.setStatus("terminal");
        row.setErrorCategory("SUPERSEDED");
        row.setGcAfter(now.plus(properties.getGcGracePeriod()));
        row.setGmtModified(now);
        require(commits.updateByPrimaryKey(row) == 1);
    }

    private static String checkpointOperation(Snapshot snapshot) {
        require(snapshot.operation() != Operation.REVALIDATE);
        return "CHECKPOINT_" + snapshot.operation().name();
    }

    private static JsonNode jsonTree(String value) {
        try {
            return value == null ? null : InfoUtils.defaultMapper().readTree(value);
        } catch (Exception ignored) {
            throw conflict();
        }
    }

    record Checkpoint(
            String attemptId, MemorySourceReader.Position position, String predecessor, MemoryManifest.Entry entry) {}

    private ChatMemoryState lock(MemoryScope scope) {
        ChatMemoryState state = coordination.lock(scope.chatId()).orElseThrow(MemoryPublicationRepository::conflict);
        require("active".equals(state.getStatus())
                && scope.generation() == state.getGeneration()
                && scope.userId().equals(state.getUserId())
                && scope.characterUid().equals(state.getCharacterUid())
                && scope.storeType().text().equals(state.getStoreType()));
        return state;
    }

    private ChatMemoryCommit lockAttempt(MemoryScope scope, String attemptId) {
        ChatMemoryCommit attempt = commits.selectOne(
                        query -> query.where(ChatMemoryCommitDynamicSqlSupport.attemptId, isEqualTo(attemptId))
                                .forUpdate())
                .orElseThrow(MemoryPublicationRepository::conflict);
        require(scope.chatId().equals(attempt.getChatId()) && scope.generation() == attempt.getGeneration());
        return attempt;
    }

    private void validateSnapshot(ChatMemoryState state, Snapshot snapshot, LocalDateTime now) {
        require(snapshot.fingerprint().equals(state.getFingerprint()));
        require(snapshot.expectedCursor() == cursor(state, snapshot.operation())
                && Objects.equals(snapshot.expectedHead(), head(state, snapshot.operation())));
        if (snapshot.operation() == Operation.REVALIDATE) {
            require(state.getProfileRevalidationPending() == 1);
        }
        checkLease(state, snapshot.operation(), snapshot.leaseToken(), now);
    }

    private static void checkLease(ChatMemoryState state, Operation operation, String token, LocalDateTime now) {
        String current = operation == Operation.OVERFLOW ? state.getTurnToken() : state.getClaimToken();
        LocalDateTime lease = operation == Operation.OVERFLOW ? state.getTurnLeaseUntil() : state.getClaimLeaseUntil();
        LocalDateTime deadline = deadline(state, operation);
        require(token != null
                && token.equals(current)
                && lease != null
                && deadline != null
                && now.isBefore(lease)
                && now.isBefore(deadline));
    }

    private static LocalDateTime deadline(ChatMemoryState state, Operation operation) {
        return operation == Operation.OVERFLOW ? state.getTurnDeadline() : state.getClaimDeadline();
    }

    private static long cursor(ChatMemoryState state, Operation operation) {
        return operation == Operation.OVERFLOW ? state.getOverflowThroughId() : state.getIdleThroughId();
    }

    private static String head(ChatMemoryState state, Operation operation) {
        return operation == Operation.OVERFLOW ? state.getSummaryId() : state.getProfileId();
    }

    private void requireFinalBoundary(MemoryScope scope, long id) {
        ChatHistory terminal = histories.selectByPrimaryKey(id).orElseThrow(MemoryPublicationRepository::conflict);
        require(scope.chatId().equals(terminal.getMemoryId())
                && terminal.getEnabled() == 1
                && ("turn-complete".equals(terminal.getRecordKind()) || "turn-abort".equals(terminal.getRecordKind())));
    }

    private String sourceHash(MemoryScope scope, long cursor, long throughId) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            long after = cursor;
            while (after < throughId) {
                long pageStart = after;
                List<ChatHistory> page = sourceRows(
                        scope,
                        query -> query.where(ChatHistoryDynamicSqlSupport.memoryId, isEqualTo(scope.chatId()))
                                .and(ChatHistoryDynamicSqlSupport.id, isGreaterThan(pageStart))
                                .and(ChatHistoryDynamicSqlSupport.id, isLessThanOrEqualTo(throughId))
                                .orderBy(ChatHistoryDynamicSqlSupport.id)
                                .limit(64));
                if (page.isEmpty()) {
                    break;
                }
                for (ChatHistory row : page) {
                    for (Object field : new Object[] {
                        row.getId(),
                        row.getEnabled(),
                        row.getTurnId(),
                        row.getRecordKind(),
                        row.getMessageOrigin(),
                        row.getMessage(),
                        row.getSourceMessage()
                    }) {
                        byte[] value = Objects.toString(field, "").getBytes(StandardCharsets.UTF_8);
                        digest.update(java.nio.ByteBuffer.allocate(4)
                                .putInt(value.length)
                                .array());
                        digest.update(value);
                    }
                }
                after = page.getLast().getId();
            }
            return HexFormat.of().formatHex(digest.digest());
        } catch (NoSuchAlgorithmException error) {
            throw new IllegalStateException("SHA-256 is unavailable");
        }
    }

    private List<ChatHistory> sourceRows(MemoryScope scope, org.mybatis.dynamic.sql.dsl.SelectDSLCompleter selection) {
        // MySQL can filesort entire JSON values; order IDs before retrieving payloads.
        List<Long> ids = org.mybatis.dynamic.sql.util.mybatis3.MyBatis3Utils.selectList(
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
        List<ChatHistory> rows = org.mybatis.dynamic.sql.util.mybatis3.MyBatis3Utils.selectList(
                coordination::selectHistoryPage,
                org.mybatis.dynamic.sql.BasicColumn.columnList(
                        ChatHistoryDynamicSqlSupport.id,
                        ChatHistoryDynamicSqlSupport.memoryId,
                        ChatHistoryDynamicSqlSupport.enabled,
                        ChatHistoryDynamicSqlSupport.turnId,
                        ChatHistoryDynamicSqlSupport.recordKind,
                        ChatHistoryDynamicSqlSupport.messageOrigin,
                        ChatHistoryDynamicSqlSupport.episode,
                        ChatHistoryDynamicSqlSupport.message,
                        ChatHistoryDynamicSqlSupport.sourceMessage),
                ChatHistoryDynamicSqlSupport.chatHistory,
                (org.mybatis.dynamic.sql.dsl.SelectDSLCompleter)
                        query -> query.where(ChatHistoryDynamicSqlSupport.memoryId, isEqualTo(scope.chatId()))
                                .and(ChatHistoryDynamicSqlSupport.id, org.mybatis.dynamic.sql.SqlBuilder.isIn(ids)));
        require(rows.size() == ids.size());
        return rows.stream()
                .sorted(java.util.Comparator.comparingLong(ChatHistory::getId))
                .toList();
    }

    private static Snapshot fromAttempt(MemoryScope scope, ChatMemoryCommit attempt) {
        try {
            var progress = InfoUtils.defaultMapper().readTree(attempt.getProgress());
            require(progress.isObject()
                    && progress.size() == 1
                    && progress.path("sourceHash").isTextual());
            String hash = progress.get("sourceHash").textValue();
            require(hash.matches("[0-9a-f]{64}"));
            return new Snapshot(
                    scope,
                    attempt.getAttemptId(),
                    Operation.valueOf(attempt.getOperation()),
                    attempt.getSourceStartId(),
                    attempt.getSourceEndId(),
                    attempt.getExpectedCursor(),
                    attempt.getExpectedHead(),
                    attempt.getLeaseToken(),
                    attempt.getFingerprint(),
                    hash);
        } catch (Exception ignored) {
            throw conflict();
        }
    }

    private static void validateManifest(Operation operation, MemoryManifest manifest) {
        long profiles = manifest.entries().stream()
                .filter(entry -> entry.kind() == MemoryDocument.Kind.PROFILE_SNAPSHOT)
                .count();
        long episodes = manifest.entries().stream()
                .filter(entry -> entry.kind() == MemoryDocument.Kind.EPISODE_SUMMARY)
                .count();
        long heads = manifest.entries().stream()
                .filter(entry -> entry.kind() == MemoryDocument.Kind.WINDOW_SUMMARY && !entry.archive())
                .count();
        long chunks = manifest.entries().stream()
                .filter(entry -> entry.kind() == MemoryDocument.Kind.WINDOW_SUMMARY && entry.archive())
                .count();
        require(
                switch (operation) {
                    case OVERFLOW ->
                        heads == 1
                                && chunks >= 1
                                && heads + chunks == manifest.entries().size();
                    case IDLE ->
                        profiles == 1 && episodes == 1 && manifest.entries().size() == 2;
                    case REVALIDATE -> profiles == 1 && manifest.entries().size() == 1;
                });
    }

    private static void require(boolean valid) {
        if (!valid) {
            throw conflict();
        }
    }

    private void inTransaction(Runnable action) {
        transaction(() -> {
            action.run();
            return null;
        });
    }

    private <T> T transaction(Supplier<T> action) {
        boolean[] fatal = {false};
        try {
            return transactions.execute(ignored -> {
                try {
                    return action.get();
                } catch (Conflict failure) {
                    throw new Conflict();
                } catch (Error failure) {
                    fatal[0] = true;
                    throw new Error("Memory publication transaction failed");
                } catch (Throwable failure) {
                    // Sanitize before TransactionTemplate logs callback failures, including sneaky checked ones.
                    throw new IllegalStateException("Memory publication transaction failed");
                }
            });
        } catch (Throwable failure) {
            // Begin/commit/rollback can also expose payloads. A failed rollback must not downgrade a fatal error.
            if (fatal[0] || failure instanceof Error) {
                throw new Error("Memory publication transaction failed");
            }
            if (failure instanceof Conflict) {
                throw new Conflict();
            }
            throw new IllegalStateException("Memory publication transaction failed");
        }
    }

    private static Conflict conflict() {
        return new Conflict();
    }

    private static final class Conflict extends IllegalStateException {
        private Conflict() {
            super("Memory publication state changed or lease expired");
        }
    }

    public enum Operation {
        OVERFLOW,
        IDLE,
        REVALIDATE
    }

    public enum Failure {
        EXTRACTION,
        EMBEDDING,
        VECTOR_WRITE,
        VERIFICATION,
        CONFLICT,
        EXPIRED
    }

    public record Snapshot(
            MemoryScope scope,
            String attemptId,
            Operation operation,
            long sourceStartId,
            long sourceEndId,
            long expectedCursor,
            String expectedHead,
            String leaseToken,
            String fingerprint,
            String sourceHash) {}

    public record AuthorizedRecord(String attemptId, String fingerprint, MemoryManifest.Entry entry) {}
}
