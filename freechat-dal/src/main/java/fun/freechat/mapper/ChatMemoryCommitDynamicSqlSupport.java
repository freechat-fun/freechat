package fun.freechat.mapper;

import java.sql.JDBCType;
import java.time.LocalDateTime;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class ChatMemoryCommitDynamicSqlSupport {
    public static final ChatMemoryCommit chatMemoryCommit = new ChatMemoryCommit();

    public static final SqlColumn<String> attemptId = chatMemoryCommit.attemptId;

    public static final SqlColumn<String> chatId = chatMemoryCommit.chatId;

    public static final SqlColumn<Long> generation = chatMemoryCommit.generation;

    public static final SqlColumn<String> operation = chatMemoryCommit.operation;

    public static final SqlColumn<Long> sourceStartId = chatMemoryCommit.sourceStartId;

    public static final SqlColumn<Long> sourceEndId = chatMemoryCommit.sourceEndId;

    public static final SqlColumn<Long> expectedCursor = chatMemoryCommit.expectedCursor;

    public static final SqlColumn<String> expectedHead = chatMemoryCommit.expectedHead;

    public static final SqlColumn<String> leaseToken = chatMemoryCommit.leaseToken;

    public static final SqlColumn<String> status = chatMemoryCommit.status;

    public static final SqlColumn<String> fingerprint = chatMemoryCommit.fingerprint;

    public static final SqlColumn<Integer> schemaVersion = chatMemoryCommit.schemaVersion;

    public static final SqlColumn<String> modelId = chatMemoryCommit.modelId;

    public static final SqlColumn<String> errorCategory = chatMemoryCommit.errorCategory;

    public static final SqlColumn<LocalDateTime> leaseUntil = chatMemoryCommit.leaseUntil;

    public static final SqlColumn<LocalDateTime> gcAfter = chatMemoryCommit.gcAfter;

    public static final SqlColumn<LocalDateTime> gmtCreate = chatMemoryCommit.gmtCreate;

    public static final SqlColumn<LocalDateTime> gmtModified = chatMemoryCommit.gmtModified;

    public static final SqlColumn<String> manifest = chatMemoryCommit.manifest;

    public static final SqlColumn<String> progress = chatMemoryCommit.progress;

    public static final SqlColumn<String> tokenUsage = chatMemoryCommit.tokenUsage;

    public static final class ChatMemoryCommit extends AliasableSqlTable<ChatMemoryCommit> {
        public final SqlColumn<String> attemptId = column("attempt_id", JDBCType.VARCHAR).withJavaProperty("attemptId");

        public final SqlColumn<String> chatId = column("chat_id", JDBCType.VARCHAR).withJavaProperty("chatId");

        public final SqlColumn<Long> generation = column("generation", JDBCType.BIGINT).withJavaProperty("generation");

        public final SqlColumn<String> operation = column("operation", JDBCType.VARCHAR).withJavaProperty("operation");

        public final SqlColumn<Long> sourceStartId = column("source_start_id", JDBCType.BIGINT).withJavaProperty("sourceStartId");

        public final SqlColumn<Long> sourceEndId = column("source_end_id", JDBCType.BIGINT).withJavaProperty("sourceEndId");

        public final SqlColumn<Long> expectedCursor = column("expected_cursor", JDBCType.BIGINT).withJavaProperty("expectedCursor");

        public final SqlColumn<String> expectedHead = column("expected_head", JDBCType.VARCHAR).withJavaProperty("expectedHead");

        public final SqlColumn<String> leaseToken = column("lease_token", JDBCType.VARCHAR).withJavaProperty("leaseToken");

        public final SqlColumn<String> status = column("status", JDBCType.VARCHAR).withJavaProperty("status");

        public final SqlColumn<String> fingerprint = column("fingerprint", JDBCType.VARCHAR).withJavaProperty("fingerprint");

        public final SqlColumn<Integer> schemaVersion = column("schema_version", JDBCType.INTEGER).withJavaProperty("schemaVersion");

        public final SqlColumn<String> modelId = column("model_id", JDBCType.VARCHAR).withJavaProperty("modelId");

        public final SqlColumn<String> errorCategory = column("error_category", JDBCType.VARCHAR).withJavaProperty("errorCategory");

        public final SqlColumn<LocalDateTime> leaseUntil = column("lease_until", JDBCType.TIMESTAMP).withJavaProperty("leaseUntil");

        public final SqlColumn<LocalDateTime> gcAfter = column("gc_after", JDBCType.TIMESTAMP).withJavaProperty("gcAfter");

        public final SqlColumn<LocalDateTime> gmtCreate = column("gmt_create", JDBCType.TIMESTAMP).withJavaProperty("gmtCreate");

        public final SqlColumn<LocalDateTime> gmtModified = column("gmt_modified", JDBCType.TIMESTAMP).withJavaProperty("gmtModified");

        public final SqlColumn<String> manifest = column("manifest", JDBCType.LONGVARCHAR).withJavaProperty("manifest");

        public final SqlColumn<String> progress = column("progress", JDBCType.LONGVARCHAR).withJavaProperty("progress");

        public final SqlColumn<String> tokenUsage = column("token_usage", JDBCType.LONGVARCHAR).withJavaProperty("tokenUsage");

        public ChatMemoryCommit() {
            super("chat_memory_commit", ChatMemoryCommit::new);
        }
    }
}