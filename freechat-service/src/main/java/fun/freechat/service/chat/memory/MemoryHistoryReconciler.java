package fun.freechat.service.chat.memory;

import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;
import static org.mybatis.dynamic.sql.SqlBuilder.isGreaterThan;
import static org.mybatis.dynamic.sql.SqlBuilder.select;

import com.fasterxml.jackson.databind.JsonNode;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.data.message.UserMessage;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

/** In-place legacy boundaries. Call under chat coordination, before admitting a new turn. */
public final class MemoryHistoryReconciler {
    private static final int PAGE_SIZE = 1000;
    private final ChatMemoryCoordinationMapper coordination;
    private final ChatMemoryStateMapper states;
    private final ChatHistoryMapper histories;
    private final ChatMemoryCommitMapper commits;
    private final TransactionTemplate transactions;
    private final LongTermMemoryProperties properties;

    public MemoryHistoryReconciler(
            ChatMemoryCoordinationMapper coordination,
            ChatMemoryStateMapper states,
            ChatHistoryMapper histories,
            ChatMemoryCommitMapper commits,
            PlatformTransactionManager transactions,
            LongTermMemoryProperties properties) {
        this.coordination = Objects.requireNonNull(coordination);
        this.states = Objects.requireNonNull(states);
        this.histories = Objects.requireNonNull(histories);
        this.commits = Objects.requireNonNull(commits);
        this.properties = Objects.requireNonNull(properties);
        this.transactions = new TransactionTemplate(Objects.requireNonNull(transactions));
        this.transactions.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        this.transactions.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
    }

    public Result reconcile(MemoryScope scope, String fingerprint, List<ChatMessage> examples) {
        return transaction(() -> {
            require(scope != null
                    && fingerprint != null
                    && !fingerprint.isBlank()
                    && fingerprint.length() <= 64
                    && examples != null
                    && examples.stream().noneMatch(Objects::isNull));
            ChatMemoryState state = coordination.lock(scope.chatId()).orElseThrow(Rejected::new);
            LocalDateTime now = coordination.databaseNow();
            require("active".equals(state.getStatus())
                    && scope.generation() == state.getGeneration()
                    && scope.userId().equals(state.getUserId())
                    && scope.characterUid().equals(state.getCharacterUid())
                    && scope.storeType().text().equals(state.getStoreType())
                    && fingerprint.equals(state.getFingerprint())
                    // Even expired tokens must first be recovered by the lifecycle owner.
                    && state.getTurnToken() == null);
            List<ChatMemoryCommit> found = commits.select(
                    query -> query.where(ChatMemoryCommitDynamicSqlSupport.chatId, isEqualTo(scope.chatId()))
                            .and(ChatMemoryCommitDynamicSqlSupport.generation, isEqualTo(scope.generation()))
                            .and(ChatMemoryCommitDynamicSqlSupport.operation, isEqualTo("RECONCILE"))
                            .limit(2));
            require(found.size() <= 1);
            ChatMemoryCommit attempt = found.isEmpty() ? null : found.getFirst();
            boolean reset = attempt == null
                    || !fingerprint.equals(attempt.getFingerprint())
                    || !Objects.equals(attempt.getSourceEndId(), state.getReconciledThroughId());
            if (!reset && "reconciled".equals(attempt.getStatus()) && !hasMore(scope, state.getReconciledThroughId())) {
                return new Result(true, state.getReconciledThroughId());
            }
            // Never resume metadata from a different configuration or a lifecycle cursor reset.
            if (reset) {
                state.setReconciledThroughId(0L);
                // A rollback may have disabled the former terminal. Rebuild only from enabled boundaries.
                state.setLatestFinalizedId(0L);
            }
            List<ChatMessage> prefix = examples.stream()
                    .filter(message -> !(message instanceof SystemMessage))
                    .toList();
            Progress progress = reset ? new Progress(!prefix.isEmpty()) : decode(attempt.getProgress());
            require(progress.templatePosition <= prefix.size());
            if (attempt == null) {
                String id = UUID.randomUUID().toString();
                attempt = new ChatMemoryCommit()
                        .withAttemptId(id)
                        .withChatId(scope.chatId())
                        .withGeneration(scope.generation())
                        .withOperation("RECONCILE")
                        .withSourceStartId(0L)
                        .withExpectedCursor(0L)
                        .withLeaseToken(id)
                        .withSchemaVersion(MemoryScope.SCHEMA_VERSION)
                        .withModelId("none")
                        .withManifest("{}")
                        .withGmtCreate(now);
            }
            int remaining = PAGE_SIZE;
            // Verify the ENTIRE configured prefix before trusting any matching user text as a template.
            // Large prefixes can themselves resume, using only a position and the last inspected ID.
            if (progress.templatePrefix) {
                List<ChatHistory> page = page(scope, progress.lastRowId, remaining);
                for (ChatHistory row : page) {
                    remaining--;
                    progress.lastRowId = row.getId();
                    if (row.getTurnId() != null
                            || !"message".equals(row.getRecordKind())
                            || row.getMessageOrigin() != null) {
                        progress.templatePosition = 0;
                        progress.templatePrefix = false;
                        break;
                    }
                    ChatMessage message = parse(row);
                    if (message instanceof SystemMessage) {
                        continue;
                    }
                    if (!message.equals(prefix.get(progress.templatePosition))) {
                        progress.templatePosition = 0;
                        progress.templatePrefix = false;
                        break;
                    }
                    if (++progress.templatePosition == prefix.size()) {
                        progress.templatePrefix = false;
                        break;
                    }
                }
                if (progress.templatePrefix && !hasMore(scope, progress.lastRowId)) {
                    progress.templatePrefix = false;
                    progress.templatePosition = 0;
                }
                if (!progress.templatePrefix) {
                    progress.lastRowId = state.getReconciledThroughId();
                }
            }
            if (!progress.templatePrefix && remaining > 0) {
                require(progress.lastRowId == state.getReconciledThroughId());
                for (ChatHistory row : page(scope, state.getReconciledThroughId(), remaining)) {
                    classify(scope, state, progress, row, prefix);
                    progress.lastRowId = row.getId();
                    state.setReconciledThroughId(row.getId());
                }
            }
            boolean complete = !progress.templatePrefix && !hasMore(scope, state.getReconciledThroughId());
            if (complete && progress.pendingTurnId != null) {
                abortPending(scope, state, progress);
            }
            if (progress.lastTimestamp != null) {
                state.setLastActivity(progress.lastTimestamp);
                state.setEpisode(Math.max(state.getEpisode(), progress.episode));
                if (state.getLatestFinalizedId() > state.getIdleThroughId()) {
                    state.setDueAt(progress.lastTimestamp.plus(properties.getIdleTimeout()));
                }
            }
            state.setVersion(state.getVersion() + 1);
            state.setGmtModified(now);
            attempt.setFingerprint(fingerprint);
            attempt.setSourceEndId(state.getReconciledThroughId());
            attempt.setStatus(complete ? "reconciled" : "reconciling");
            attempt.setProgress(encode(progress));
            attempt.setLeaseUntil(now);
            attempt.setGmtModified(now);
            require(states.updateByPrimaryKey(state) == 1);
            require((found.isEmpty() ? commits.insertSelective(attempt) : commits.updateByPrimaryKey(attempt)) == 1);
            if (complete) {
                MemoryScopeLedger.markLegacy(coordination, scope.chatId(), now, properties.getGcGracePeriod());
            }
            return new Result(complete, state.getReconciledThroughId());
        });
    }

    private void classify(
            MemoryScope scope, ChatMemoryState state, Progress progress, ChatHistory row, List<ChatMessage> examples) {
        require(row.getGmtCreate() != null && row.getEpisode() != null);
        if (row.getTurnId() != null) {
            tagged(scope, state, progress, row);
            return;
        }
        require("message".equals(row.getRecordKind()));
        if ("system".equals(row.getMessageOrigin()) || "template-example".equals(row.getMessageOrigin())) {
            require(progress.pendingTurnId == null);
            return; // Already trusted by an earlier generation; never re-interpret its payload.
        }
        ChatMessage message = parse(row);
        String origin;
        String kind = "message";
        if (message instanceof SystemMessage) {
            origin = "system";
        } else if (progress.templatePosition > 0) {
            require(progress.pendingTurnId == null
                    && message.equals(examples.get(examples.size() - progress.templatePosition)));
            progress.templatePosition--;
            origin = "template-example";
        } else {
            if (message instanceof UserMessage) {
                if (progress.pendingTurnId != null) {
                    abortPending(scope, state, progress);
                }
                start(progress, row);
                kind = "turn-start";
                origin = "user-input";
            } else {
                origin = message instanceof ToolExecutionResultMessage
                                || message instanceof AiMessage ai && ai.hasToolExecutionRequests()
                        ? "tool"
                        : "assistant-output";
                if (progress.pendingTurnId == null) {
                    start(progress, row);
                    kind = "turn-abort"; // A standalone orphan is never successful profile evidence.
                } else if (message instanceof AiMessage ai && !ai.hasToolExecutionRequests()) {
                    kind = "turn-complete";
                }
            }
            progress.lastTimestamp = row.getGmtCreate();
        }
        annotate(row, progress.pendingTurnId, kind, origin, progress.episode);
        if ("turn-complete".equals(kind) || "turn-abort".equals(kind)) {
            finalized(state, progress, row.getId());
        }
    }

    private void tagged(MemoryScope scope, ChatMemoryState state, Progress progress, ChatHistory row) {
        require(MemoryDocumentCodec.canonicalUuid(row.getTurnId()) && row.getEpisode() >= 0);
        if ("turn-start".equals(row.getRecordKind())) {
            if (progress.pendingTurnId != null) {
                abortPending(scope, state, progress);
            }
            progress.pendingTurnId = row.getTurnId();
            progress.pendingStartId = row.getId();
            progress.episode = row.getEpisode();
        } else {
            boolean singleAbort = progress.pendingTurnId == null
                    && "turn-abort".equals(row.getRecordKind())
                    && row.getMessage() != null;
            require(singleAbort
                    || row.getTurnId().equals(progress.pendingTurnId) && row.getEpisode() == progress.episode);
            require("message".equals(row.getRecordKind())
                    || "turn-complete".equals(row.getRecordKind())
                    || "turn-abort".equals(row.getRecordKind()));
            if ("turn-complete".equals(row.getRecordKind()) || "turn-abort".equals(row.getRecordKind())) {
                finalized(state, progress, row.getId());
            }
            progress.episode = row.getEpisode();
        }
        // Tagged aborted content deliberately need not parse; successful content is validated by the source reader.
        progress.lastTimestamp = row.getGmtCreate();
    }

    private void start(Progress progress, ChatHistory row) {
        if (progress.lastTimestamp != null
                && !row.getGmtCreate().isBefore(progress.lastTimestamp.plus(properties.getIdleTimeout()))) {
            progress.episode++;
        }
        progress.pendingTurnId = UUID.randomUUID().toString();
        progress.pendingStartId = row.getId();
    }

    private void abortPending(MemoryScope scope, ChatMemoryState state, Progress progress) {
        ChatHistory start =
                histories.selectByPrimaryKey(progress.pendingStartId).orElseThrow(Rejected::new);
        ChatHistory last = histories.selectByPrimaryKey(progress.lastRowId).orElseThrow(Rejected::new);
        require(scope.chatId().equals(start.getMemoryId())
                && scope.chatId().equals(last.getMemoryId())
                && start.getEnabled() == 1
                && last.getEnabled() == 1
                && progress.pendingTurnId.equals(start.getTurnId())
                && progress.pendingTurnId.equals(last.getTurnId())
                && "turn-start".equals(start.getRecordKind())
                && start.getMessage() != null
                && "user-input".equals(start.getMessageOrigin())
                && last.getMessage() != null
                && ("message".equals(last.getRecordKind()) || start.getId().equals(last.getId())));
        // Only an unfinished legacy turn may be repaired. New bodyless turns belong to lease recovery.
        annotate(last, progress.pendingTurnId, "turn-abort", last.getMessageOrigin(), last.getEpisode());
        finalized(state, progress, last.getId());
    }

    private static void finalized(ChatMemoryState state, Progress progress, long id) {
        state.setLatestFinalizedId(Math.max(state.getLatestFinalizedId(), id));
        progress.pendingTurnId = null;
        progress.pendingStartId = 0;
    }

    private void annotate(ChatHistory row, String turn, String kind, String origin, long episode) {
        // Do not round-trip JSON, timestamps, enabled flags, snapshots, ext or any existing source IDs.
        require(histories.update(query -> query.set(ChatHistoryDynamicSqlSupport.turnId)
                        .equalTo(turn)
                        .set(ChatHistoryDynamicSqlSupport.recordKind)
                        .equalTo(kind)
                        .set(ChatHistoryDynamicSqlSupport.messageOrigin)
                        .equalTo(origin)
                        .set(ChatHistoryDynamicSqlSupport.episode)
                        .equalTo(episode)
                        .where(ChatHistoryDynamicSqlSupport.id, isEqualTo(row.getId()))
                        .and(ChatHistoryDynamicSqlSupport.memoryId, isEqualTo(row.getMemoryId()))
                        .and(ChatHistoryDynamicSqlSupport.enabled, isEqualTo((byte) 1)))
                == 1);
    }

    private List<ChatHistory> page(MemoryScope scope, long after, int limit) {
        return histories.select(query -> query.where(ChatHistoryDynamicSqlSupport.memoryId, isEqualTo(scope.chatId()))
                .and(ChatHistoryDynamicSqlSupport.enabled, isEqualTo((byte) 1))
                .and(ChatHistoryDynamicSqlSupport.id, isGreaterThan(after))
                .orderBy(ChatHistoryDynamicSqlSupport.id)
                .limit(limit));
    }

    private boolean hasMore(MemoryScope scope, long after) {
        // Metadata-only lookahead must not parse or materialize the next page's bodies.
        return histories
                .selectOne(select(ChatHistoryDynamicSqlSupport.id)
                        .from(ChatHistoryDynamicSqlSupport.chatHistory)
                        .where(ChatHistoryDynamicSqlSupport.memoryId, isEqualTo(scope.chatId()))
                        .and(ChatHistoryDynamicSqlSupport.enabled, isEqualTo((byte) 1))
                        .and(ChatHistoryDynamicSqlSupport.id, isGreaterThan(after))
                        .orderBy(ChatHistoryDynamicSqlSupport.id)
                        .limit(1)
                        .build()
                        .render(RenderingStrategies.MYBATIS3))
                .isPresent();
    }

    private static ChatMessage parse(ChatHistory row) {
        require(row.getMessage() != null);
        ChatMessage message = ChatMessageDeserializer.messageFromJson(row.getMessage());
        require(message instanceof UserMessage
                || message instanceof SystemMessage
                || message instanceof AiMessage
                || message instanceof ToolExecutionResultMessage);
        if (message instanceof UserMessage user) {
            require(user.contents() != null && !user.contents().isEmpty());
            user.contents().stream()
                    .filter(TextContent.class::isInstance)
                    .map(TextContent.class::cast)
                    .forEach(content -> unicode(content.text()));
        } else if (message instanceof AiMessage ai) {
            unicode(ai.text());
        } else if (message instanceof ToolExecutionResultMessage tool) {
            unicode(tool.text());
        }
        return message;
    }

    private static void unicode(String text) {
        if (text == null) {
            return;
        }
        for (int index = 0; index < text.length(); index++) {
            char value = text.charAt(index);
            if (Character.isHighSurrogate(value)) {
                require(++index < text.length() && Character.isLowSurrogate(text.charAt(index)));
            } else {
                require(!Character.isLowSurrogate(value));
            }
        }
    }

    private static String encode(Progress progress) {
        return InfoUtils.defaultMapper()
                .createObjectNode()
                .put("pendingTurnId", progress.pendingTurnId)
                .put("pendingStartId", progress.pendingStartId)
                .put("lastRowId", progress.lastRowId)
                .put("episode", progress.episode)
                .put("lastTimestamp", progress.lastTimestamp == null ? null : progress.lastTimestamp.toString())
                .put("templatePosition", progress.templatePosition)
                .put("templatePrefix", progress.templatePrefix)
                .toString();
    }

    private static Progress decode(String encoded) {
        require(encoded != null && encoded.length() <= 1024);
        JsonNode node;
        try {
            node = InfoUtils.defaultMapper().readTree(encoded);
        } catch (Exception ignored) {
            throw new Rejected();
        }
        require(node != null
                && node.isObject()
                && node.size() == 7
                && node.has("pendingTurnId")
                && node.has("lastTimestamp")
                && node.path("templatePrefix").isBoolean());
        for (String field : List.of("pendingStartId", "lastRowId", "episode", "templatePosition")) {
            require(node.path(field).isIntegralNumber()
                    && node.path(field).canConvertToLong()
                    && node.path(field).longValue() >= 0);
        }
        require(node.path("templatePosition").canConvertToInt());
        require(node.get("pendingTurnId").isNull()
                || node.get("pendingTurnId").isTextual()
                        && MemoryDocumentCodec.canonicalUuid(
                                node.get("pendingTurnId").textValue()));
        require(node.get("lastTimestamp").isNull() || node.get("lastTimestamp").isTextual());
        Progress progress = new Progress(node.get("templatePrefix").booleanValue());
        progress.pendingTurnId = node.get("pendingTurnId").textValue();
        progress.pendingStartId = node.get("pendingStartId").longValue();
        progress.lastRowId = node.get("lastRowId").longValue();
        progress.episode = node.get("episode").longValue();
        progress.templatePosition = node.get("templatePosition").intValue();
        progress.lastTimestamp = node.get("lastTimestamp").isNull()
                ? null
                : LocalDateTime.parse(node.get("lastTimestamp").textValue());
        require((progress.pendingTurnId == null) == (progress.pendingStartId == 0)
                && progress.pendingStartId <= progress.lastRowId);
        return progress;
    }

    private <T> T transaction(Supplier<T> action) {
        boolean[] fatal = {false};
        try {
            return transactions.execute(status -> {
                try {
                    return action.get();
                } catch (Rejected failure) {
                    throw new Rejected();
                } catch (Error failure) {
                    fatal[0] = true;
                    throw new Error("Memory reconciliation transaction failed");
                } catch (Throwable failure) {
                    throw new IllegalStateException("Memory reconciliation transaction failed");
                }
            });
        } catch (Throwable failure) {
            if (fatal[0] || failure instanceof Error) {
                throw new Error("Memory reconciliation transaction failed");
            }
            if (failure instanceof Rejected) {
                throw new Rejected();
            }
            throw new IllegalStateException("Memory reconciliation transaction failed");
        }
    }

    private static void require(boolean condition) {
        if (!condition) {
            throw new Rejected();
        }
    }

    private static final class Rejected extends IllegalStateException {
        private Rejected() {
            super("Memory reconciliation scope or source is no longer valid");
        }
    }

    private static final class Progress {
        private String pendingTurnId;
        private long pendingStartId;
        private long lastRowId;
        private long episode;
        private LocalDateTime lastTimestamp;
        private int templatePosition;
        private boolean templatePrefix;

        private Progress(boolean templatePrefix) {
            this.templatePrefix = templatePrefix;
        }
    }

    public record Result(boolean complete, long throughId) {}
}
