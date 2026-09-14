package fun.freechat.mapper;

import java.sql.JDBCType;
import java.time.LocalDateTime;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class ChatMemoryStateDynamicSqlSupport {
    public static final ChatMemoryState chatMemoryState = new ChatMemoryState();

    public static final SqlColumn<String> chatId = chatMemoryState.chatId;

    public static final SqlColumn<String> userId = chatMemoryState.userId;

    public static final SqlColumn<String> characterUid = chatMemoryState.characterUid;

    public static final SqlColumn<String> storeType = chatMemoryState.storeType;

    public static final SqlColumn<Long> generation = chatMemoryState.generation;

    public static final SqlColumn<Long> version = chatMemoryState.version;

    public static final SqlColumn<String> status = chatMemoryState.status;

    public static final SqlColumn<String> fingerprint = chatMemoryState.fingerprint;

    public static final SqlColumn<LocalDateTime> lastActivity = chatMemoryState.lastActivity;

    public static final SqlColumn<Long> latestFinalizedId = chatMemoryState.latestFinalizedId;

    public static final SqlColumn<Long> episode = chatMemoryState.episode;

    public static final SqlColumn<Long> overflowThroughId = chatMemoryState.overflowThroughId;

    public static final SqlColumn<Long> idleThroughId = chatMemoryState.idleThroughId;

    public static final SqlColumn<String> summaryId = chatMemoryState.summaryId;

    public static final SqlColumn<String> profileId = chatMemoryState.profileId;

    public static final SqlColumn<Byte> profileRevalidationPending = chatMemoryState.profileRevalidationPending;

    public static final SqlColumn<String> turnToken = chatMemoryState.turnToken;

    public static final SqlColumn<LocalDateTime> turnLeaseUntil = chatMemoryState.turnLeaseUntil;

    public static final SqlColumn<LocalDateTime> turnDeadline = chatMemoryState.turnDeadline;

    public static final SqlColumn<Long> turnRevision = chatMemoryState.turnRevision;

    public static final SqlColumn<LocalDateTime> dueAt = chatMemoryState.dueAt;

    public static final SqlColumn<LocalDateTime> retryAt = chatMemoryState.retryAt;

    public static final SqlColumn<Integer> retryAttempts = chatMemoryState.retryAttempts;

    public static final SqlColumn<String> claimToken = chatMemoryState.claimToken;

    public static final SqlColumn<LocalDateTime> claimLeaseUntil = chatMemoryState.claimLeaseUntil;

    public static final SqlColumn<LocalDateTime> claimDeadline = chatMemoryState.claimDeadline;

    public static final SqlColumn<Long> reconciledThroughId = chatMemoryState.reconciledThroughId;

    public static final SqlColumn<LocalDateTime> gmtCreate = chatMemoryState.gmtCreate;

    public static final SqlColumn<LocalDateTime> gmtModified = chatMemoryState.gmtModified;

    public static final class ChatMemoryState extends AliasableSqlTable<ChatMemoryState> {
        public final SqlColumn<String> chatId = column("chat_id", JDBCType.VARCHAR).withJavaProperty("chatId");

        public final SqlColumn<String> userId = column("user_id", JDBCType.VARCHAR).withJavaProperty("userId");

        public final SqlColumn<String> characterUid = column("character_uid", JDBCType.VARCHAR).withJavaProperty("characterUid");

        public final SqlColumn<String> storeType = column("store_type", JDBCType.VARCHAR).withJavaProperty("storeType");

        public final SqlColumn<Long> generation = column("generation", JDBCType.BIGINT).withJavaProperty("generation");

        public final SqlColumn<Long> version = column("version", JDBCType.BIGINT).withJavaProperty("version");

        public final SqlColumn<String> status = column("status", JDBCType.VARCHAR).withJavaProperty("status");

        public final SqlColumn<String> fingerprint = column("fingerprint", JDBCType.VARCHAR).withJavaProperty("fingerprint");

        public final SqlColumn<LocalDateTime> lastActivity = column("last_activity", JDBCType.TIMESTAMP).withJavaProperty("lastActivity");

        public final SqlColumn<Long> latestFinalizedId = column("latest_finalized_id", JDBCType.BIGINT).withJavaProperty("latestFinalizedId");

        public final SqlColumn<Long> episode = column("episode", JDBCType.BIGINT).withJavaProperty("episode");

        public final SqlColumn<Long> overflowThroughId = column("overflow_through_id", JDBCType.BIGINT).withJavaProperty("overflowThroughId");

        public final SqlColumn<Long> idleThroughId = column("idle_through_id", JDBCType.BIGINT).withJavaProperty("idleThroughId");

        public final SqlColumn<String> summaryId = column("summary_id", JDBCType.VARCHAR).withJavaProperty("summaryId");

        public final SqlColumn<String> profileId = column("profile_id", JDBCType.VARCHAR).withJavaProperty("profileId");

        public final SqlColumn<Byte> profileRevalidationPending = column("profile_revalidation_pending", JDBCType.TINYINT).withJavaProperty("profileRevalidationPending");

        public final SqlColumn<String> turnToken = column("turn_token", JDBCType.VARCHAR).withJavaProperty("turnToken");

        public final SqlColumn<LocalDateTime> turnLeaseUntil = column("turn_lease_until", JDBCType.TIMESTAMP).withJavaProperty("turnLeaseUntil");

        public final SqlColumn<LocalDateTime> turnDeadline = column("turn_deadline", JDBCType.TIMESTAMP).withJavaProperty("turnDeadline");

        public final SqlColumn<Long> turnRevision = column("turn_revision", JDBCType.BIGINT).withJavaProperty("turnRevision");

        public final SqlColumn<LocalDateTime> dueAt = column("due_at", JDBCType.TIMESTAMP).withJavaProperty("dueAt");

        public final SqlColumn<LocalDateTime> retryAt = column("retry_at", JDBCType.TIMESTAMP).withJavaProperty("retryAt");

        public final SqlColumn<Integer> retryAttempts = column("retry_attempts", JDBCType.INTEGER).withJavaProperty("retryAttempts");

        public final SqlColumn<String> claimToken = column("claim_token", JDBCType.VARCHAR).withJavaProperty("claimToken");

        public final SqlColumn<LocalDateTime> claimLeaseUntil = column("claim_lease_until", JDBCType.TIMESTAMP).withJavaProperty("claimLeaseUntil");

        public final SqlColumn<LocalDateTime> claimDeadline = column("claim_deadline", JDBCType.TIMESTAMP).withJavaProperty("claimDeadline");

        public final SqlColumn<Long> reconciledThroughId = column("reconciled_through_id", JDBCType.BIGINT).withJavaProperty("reconciledThroughId");

        public final SqlColumn<LocalDateTime> gmtCreate = column("gmt_create", JDBCType.TIMESTAMP).withJavaProperty("gmtCreate");

        public final SqlColumn<LocalDateTime> gmtModified = column("gmt_modified", JDBCType.TIMESTAMP).withJavaProperty("gmtModified");

        public ChatMemoryState() {
            super("chat_memory_state", ChatMemoryState::new);
        }
    }
}