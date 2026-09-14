package fun.freechat.service.chat.memory;

import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;
import static org.mybatis.dynamic.sql.SqlBuilder.isGreaterThanWhenPresent;
import static org.mybatis.dynamic.sql.SqlBuilder.isIn;
import static org.mybatis.dynamic.sql.SqlBuilder.isLessThanOrEqualTo;
import static org.mybatis.dynamic.sql.SqlBuilder.or;
import static org.mybatis.dynamic.sql.SqlBuilder.select;

import fun.freechat.mapper.ChatMemoryCommitDynamicSqlSupport;
import fun.freechat.mapper.ChatMemoryCommitMapper;
import fun.freechat.mapper.ChatMemoryCoordinationMapper;
import fun.freechat.model.ChatMemoryCommit;
import fun.freechat.model.ChatMemoryState;
import fun.freechat.service.enums.EmbeddingStoreType;
import fun.freechat.service.rag.MemoryEmbeddingCleanupService;
import fun.freechat.service.util.InfoUtils;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

public final class MemoryGarbageCollector {
    private static final List<String> VECTOR_OPERATIONS =
            List.of("OVERFLOW", "IDLE", "REVALIDATE", "CHECKPOINT_OVERFLOW", "CHECKPOINT_IDLE");
    private static final List<String> SCANNED_OPERATIONS =
            List.of("OVERFLOW", "IDLE", "REVALIDATE", "CHECKPOINT_OVERFLOW", "CHECKPOINT_IDLE", "GC_IDS", "LEGACY_GC");
    private static final List<EmbeddingStoreType> STORES = List.of(
            EmbeddingStoreType.EN_LONG_TERM_MEMORY,
            EmbeddingStoreType.ZH_LONG_TERM_MEMORY,
            EmbeddingStoreType.DEFAULT_LONG_TERM_MEMORY);
    private final ChatMemoryCoordinationMapper coordination;
    private final ChatMemoryCommitMapper commits;
    private final MemoryEmbeddingCleanupService vectors;
    private final LongTermMemoryProperties properties;
    private final TransactionTemplate transactions;

    public MemoryGarbageCollector(
            ChatMemoryCoordinationMapper coordination,
            ChatMemoryCommitMapper commits,
            MemoryEmbeddingCleanupService vectors,
            LongTermMemoryProperties properties,
            PlatformTransactionManager transactions) {
        this.coordination = Objects.requireNonNull(coordination);
        this.commits = Objects.requireNonNull(commits);
        this.vectors = Objects.requireNonNull(vectors);
        this.properties = Objects.requireNonNull(properties);
        this.transactions = new TransactionTemplate(Objects.requireNonNull(transactions));
        this.transactions.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        this.transactions.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
    }

    public List<String> candidates(String afterId, int limit) {
        require(limit > 0 && (afterId == null || afterId.isEmpty() || MemoryDocumentCodec.canonicalUuid(afterId)));
        String after = afterId == null || afterId.isEmpty() ? null : afterId;
        return transaction(() -> commits
                .selectMany(select(ChatMemoryCommitDynamicSqlSupport.attemptId)
                        .from(ChatMemoryCommitDynamicSqlSupport.chatMemoryCommit)
                        .where(ChatMemoryCommitDynamicSqlSupport.attemptId, isGreaterThanWhenPresent(after))
                        .and(ChatMemoryCommitDynamicSqlSupport.operation, isIn(SCANNED_OPERATIONS))
                        .and(
                                ChatMemoryCommitDynamicSqlSupport.status,
                                isIn("prepared", "committed", "checkpoint"),
                                or(
                                        ChatMemoryCommitDynamicSqlSupport.status,
                                        isEqualTo("terminal"),
                                        org.mybatis.dynamic.sql.SqlBuilder.and(
                                                ChatMemoryCommitDynamicSqlSupport.gcAfter,
                                                isLessThanOrEqualTo(coordination.databaseNow()))))
                        .orderBy(ChatMemoryCommitDynamicSqlSupport.attemptId)
                        .limit(Math.min(limit, properties.getDispatchBatchSize()))
                        .build()
                        .render(RenderingStrategies.MYBATIS3))
                .stream()
                .map(ChatMemoryCommit::getAttemptId)
                .toList());
    }

    public void collect(String attemptId) {
        require(MemoryDocumentCodec.canonicalUuid(attemptId));
        Optional<Deletion> selected = transaction(() -> prepare(attemptId));
        if (selected.isEmpty()) {
            return;
        }
        Deletion deletion = selected.orElseThrow();
        try {
            if (deletion.scope() == null) {
                for (EmbeddingStoreType store : STORES) {
                    if (Thread.currentThread().isInterrupted() || !ready(deletion)) {
                        return;
                    }
                    List<String> ids = vectors.legacyIds(store, deletion.chatId(), 100);
                    if (!ids.isEmpty() && ready(deletion)) {
                        vectors.removeLegacy(store, deletion.chatId(), ids);
                    }
                }
            } else if (ready(deletion)) {
                vectors.removeExact(
                        deletion.scope().storeType(),
                        deletion.manifest().ids(),
                        deletion.scope()
                                .filter()
                                .and(dev.langchain4j.store.embedding.filter.MetadataFilterBuilder.metadataKey(
                                                "commit_id")
                                        .isEqualTo(deletion.commitId())));
            }
            transaction(() -> {
                coordination.lock(deletion.chatId());
                ChatMemoryCommit row = lockAttempt(attemptId);
                require("terminal".equals(row.getStatus()));
                LocalDateTime now = coordination.databaseNow();
                // Keep the tombstone and repeat exact deletion: a timed-out insert may arrive later.
                row.setGcAfter(now.plus(properties.getGcGracePeriod()));
                row.setGmtModified(now);
                require(commits.updateByPrimaryKey(row) == 1);
                return null;
            });
        } catch (RuntimeException ignored) {
            throw failed();
        }
    }

    private Optional<Deletion> prepare(String id) {
        ChatMemoryCommit observed = commits.selectByPrimaryKey(id).orElseThrow(MemoryGarbageCollector::failed);
        ChatMemoryState state = coordination.lock(observed.getChatId()).orElse(null);
        ChatMemoryCommit row = lockAttempt(id);
        require(observed.getChatId().equals(row.getChatId()) && SCANNED_OPERATIONS.contains(row.getOperation()));
        LocalDateTime now = coordination.databaseNow();
        if ("LEGACY_GC".equals(row.getOperation())) {
            return due(row, now) ? Optional.of(new Deletion(id, row.getChatId(), null, null, null)) : Optional.empty();
        }
        Optional<MemoryScope> found = historicalScope(row, state, now);
        if (found.isEmpty()) {
            return Optional.empty();
        }
        MemoryScope scope = found.orElseThrow();
        if (VECTOR_OPERATIONS.contains(row.getOperation())) {
            if ("committed".equals(row.getStatus()) && sameGeneration(scope, state)) {
                retireHeads(row, state, now);
                return Optional.empty();
            }
            if ("prepared".equals(row.getStatus()) && validWork(row, scope, state, now, true)
                    || "checkpoint".equals(row.getStatus()) && validWork(row, scope, state, now, false)) {
                return Optional.empty();
            }
            if (!"terminal".equals(row.getStatus())) {
                require(List.of("prepared", "committed", "checkpoint").contains(row.getStatus()));
                row.setStatus("terminal");
                row.setErrorCategory("RETIRED");
                row.setGcAfter(now.plus(properties.getGcGracePeriod()));
                row.setGmtModified(now);
                require(commits.updateByPrimaryKey(row) == 1);
            }
        }
        if (!due(row, now)) {
            return Optional.empty();
        }
        MemoryManifest manifest = MemoryDocumentCodec.decodeManifest(row.getManifest());
        String originalId = row.getAttemptId();
        if ("GC_IDS".equals(row.getOperation())) {
            originalId = originalCommit(row);
            ChatMemoryCommit original =
                    commits.selectByPrimaryKey(originalId).orElseThrow(MemoryGarbageCollector::failed);
            require(original.getChatId().equals(row.getChatId())
                    && original.getGeneration().equals(row.getGeneration())
                    && VECTOR_OPERATIONS.contains(original.getOperation()));
            MemoryManifest all = MemoryDocumentCodec.decodeManifest(original.getManifest());
            require(manifest.entries().stream()
                    .allMatch(entry -> fixedHead(entry) && all.entries().contains(entry)));
        }
        require(!sameGeneration(scope, state)
                || manifest.entries().stream().noneMatch(entry -> referenced(entry, state)));
        return Optional.of(new Deletion(id, row.getChatId(), scope, originalId, manifest));
    }

    private boolean ready(Deletion deletion) {
        return transaction(
                () -> prepare(deletion.attemptId()).filter(deletion::equals).isPresent());
    }

    private Optional<MemoryScope> historicalScope(ChatMemoryCommit row, ChatMemoryState state, LocalDateTime now) {
        require(row.getSchemaVersion() == MemoryScope.SCHEMA_VERSION);
        String id = MemoryScopeLedger.id(row.getChatId(), row.getGeneration());
        Optional<String> progress = coordination.scopeProgress(id);
        if (progress.isEmpty() && state != null && row.getGeneration().equals(state.getGeneration())) {
            MemoryScopeLedger.retain(coordination, state, now);
            progress = coordination.scopeProgress(id);
        }
        return progress.map(json -> MemoryScopeLedger.decode(row.getChatId(), row.getGeneration(), json));
    }

    private void retireHeads(ChatMemoryCommit original, ChatMemoryState state, LocalDateTime now) {
        List<MemoryManifest.Entry> entries =
                MemoryDocumentCodec.decodeManifest(original.getManifest()).entries().stream()
                        .filter(MemoryGarbageCollector::fixedHead)
                        .filter(entry -> !referenced(entry, state))
                        .toList();
        if (entries.isEmpty()) {
            return;
        }
        String id = UUID.nameUUIDFromBytes(
                        ("freechat-memory-head-gc-v1\0" + original.getAttemptId()).getBytes(StandardCharsets.UTF_8))
                .toString();
        if (commits.selectByPrimaryKey(id).isPresent()) {
            return;
        }
        require(commits.insertSelective(new ChatMemoryCommit()
                        .withAttemptId(id)
                        .withChatId(original.getChatId())
                        .withGeneration(original.getGeneration())
                        .withOperation("GC_IDS")
                        .withStatus("terminal")
                        .withSourceStartId(0L)
                        .withSourceEndId(0L)
                        .withExpectedCursor(0L)
                        .withLeaseToken(id)
                        .withFingerprint(original.getFingerprint())
                        .withSchemaVersion(MemoryScope.SCHEMA_VERSION)
                        .withModelId("none")
                        .withManifest(MemoryDocumentCodec.encodeManifest(new MemoryManifest(entries)))
                        .withProgress(InfoUtils.defaultMapper()
                                .createObjectNode()
                                .put("version", 1)
                                .put("commitId", original.getAttemptId())
                                .toString())
                        .withLeaseUntil(now)
                        .withGcAfter(now.plus(properties.getGcGracePeriod()))
                        .withGmtCreate(now)
                        .withGmtModified(now))
                == 1);
    }

    private static boolean validWork(
            ChatMemoryCommit row, MemoryScope scope, ChatMemoryState state, LocalDateTime now, boolean prepared) {
        if (!sameGeneration(scope, state)
                || !"active".equals(state.getStatus())
                || !row.getFingerprint().equals(state.getFingerprint())) {
            return false;
        }
        boolean overflow = row.getOperation().endsWith("OVERFLOW");
        long cursor = overflow ? state.getOverflowThroughId() : state.getIdleThroughId();
        String head = overflow ? state.getSummaryId() : state.getProfileId();
        if (row.getExpectedCursor() != cursor
                || !Objects.equals(row.getExpectedHead(), head)
                || "REVALIDATE".equals(row.getOperation()) && state.getProfileRevalidationPending() != 1) {
            return false;
        }
        if (!prepared) {
            return true;
        }
        String token = overflow ? state.getTurnToken() : state.getClaimToken();
        LocalDateTime lease = overflow ? state.getTurnLeaseUntil() : state.getClaimLeaseUntil();
        LocalDateTime deadline = overflow ? state.getTurnDeadline() : state.getClaimDeadline();
        return row.getLeaseToken().equals(token)
                && lease != null
                && deadline != null
                && row.getLeaseUntil() != null
                && now.isBefore(lease)
                && now.isBefore(deadline)
                && now.isBefore(row.getLeaseUntil());
    }

    private static boolean sameGeneration(MemoryScope scope, ChatMemoryState state) {
        return state != null && !"deleted".equals(state.getStatus()) && scope.equals(MemoryScopeLedger.scope(state));
    }

    private static boolean fixedHead(MemoryManifest.Entry entry) {
        return !entry.archive()
                && (entry.kind() == MemoryDocument.Kind.PROFILE_SNAPSHOT
                        || entry.kind() == MemoryDocument.Kind.WINDOW_SUMMARY);
    }

    private static boolean referenced(MemoryManifest.Entry entry, ChatMemoryState state) {
        return entry.id().equals(state.getSummaryId()) || entry.id().equals(state.getProfileId());
    }

    private static boolean due(ChatMemoryCommit row, LocalDateTime now) {
        return "terminal".equals(row.getStatus()) && row.getGcAfter() != null && !now.isBefore(row.getGcAfter());
    }

    private static String originalCommit(ChatMemoryCommit row) {
        try {
            require(row.getProgress() != null && MemoryBounds.bytes(row.getProgress()) <= 128);
            var value = InfoUtils.defaultMapper().readTree(row.getProgress());
            require(value.isObject()
                    && value.size() == 2
                    && value.path("version").isIntegralNumber()
                    && value.path("version").canConvertToInt()
                    && value.path("version").intValue() == 1
                    && MemoryDocumentCodec.canonicalUuid(value.path("commitId").textValue()));
            return value.get("commitId").textValue();
        } catch (Exception ignored) {
            throw failed();
        }
    }

    private ChatMemoryCommit lockAttempt(String id) {
        return commits.selectOne(query -> query.where(ChatMemoryCommitDynamicSqlSupport.attemptId, isEqualTo(id))
                        .forUpdate())
                .orElseThrow(MemoryGarbageCollector::failed);
    }

    private static void require(boolean condition) {
        if (!condition) {
            throw failed();
        }
    }

    private static IllegalStateException failed() {
        return new IllegalStateException("Memory cleanup deferred");
    }

    private <T> T transaction(Supplier<T> action) {
        boolean[] fatal = {false};
        try {
            return transactions.execute(status -> {
                try {
                    return action.get();
                } catch (Error ignored) {
                    fatal[0] = true;
                    throw new Error("Memory cleanup failed");
                } catch (Throwable ignored) {
                    throw failed();
                }
            });
        } catch (Throwable failure) {
            if (fatal[0] || failure instanceof Error) {
                throw new Error("Memory cleanup failed");
            }
            throw failed();
        }
    }

    private record Deletion(
            String attemptId, String chatId, MemoryScope scope, String commitId, MemoryManifest manifest) {}
}
