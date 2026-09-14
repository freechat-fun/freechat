package fun.freechat.mapper;

import java.sql.JDBCType;
import java.time.LocalDateTime;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class ChatHistoryDynamicSqlSupport {
    public static final ChatHistory chatHistory = new ChatHistory();

    public static final SqlColumn<Long> id = chatHistory.id;

    public static final SqlColumn<String> memoryId = chatHistory.memoryId;

    public static final SqlColumn<LocalDateTime> gmtCreate = chatHistory.gmtCreate;

    public static final SqlColumn<LocalDateTime> gmtModified = chatHistory.gmtModified;

    public static final SqlColumn<Byte> enabled = chatHistory.enabled;

    public static final SqlColumn<Long> tgMessageId = chatHistory.tgMessageId;

    public static final SqlColumn<String> turnId = chatHistory.turnId;

    public static final SqlColumn<String> recordKind = chatHistory.recordKind;

    public static final SqlColumn<String> messageOrigin = chatHistory.messageOrigin;

    public static final SqlColumn<String> systemMessageRef = chatHistory.systemMessageRef;

    public static final SqlColumn<Long> episode = chatHistory.episode;

    public static final SqlColumn<String> message = chatHistory.message;

    public static final SqlColumn<String> ext = chatHistory.ext;

    public static final SqlColumn<String> sourceMessage = chatHistory.sourceMessage;

    public static final class ChatHistory extends AliasableSqlTable<ChatHistory> {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT).withJavaProperty("id");

        public final SqlColumn<String> memoryId = column("memory_id", JDBCType.VARCHAR).withJavaProperty("memoryId");

        public final SqlColumn<LocalDateTime> gmtCreate = column("gmt_create", JDBCType.TIMESTAMP).withJavaProperty("gmtCreate");

        public final SqlColumn<LocalDateTime> gmtModified = column("gmt_modified", JDBCType.TIMESTAMP).withJavaProperty("gmtModified");

        public final SqlColumn<Byte> enabled = column("enabled", JDBCType.TINYINT).withJavaProperty("enabled");

        public final SqlColumn<Long> tgMessageId = column("tg_message_id", JDBCType.BIGINT).withJavaProperty("tgMessageId");

        public final SqlColumn<String> turnId = column("turn_id", JDBCType.VARCHAR).withJavaProperty("turnId");

        public final SqlColumn<String> recordKind = column("record_kind", JDBCType.VARCHAR).withJavaProperty("recordKind");

        public final SqlColumn<String> messageOrigin = column("message_origin", JDBCType.VARCHAR).withJavaProperty("messageOrigin");

        public final SqlColumn<String> systemMessageRef = column("system_message_ref", JDBCType.VARCHAR).withJavaProperty("systemMessageRef");

        public final SqlColumn<Long> episode = column("episode", JDBCType.BIGINT).withJavaProperty("episode");

        public final SqlColumn<String> message = column("message", JDBCType.LONGVARCHAR).withJavaProperty("message");

        public final SqlColumn<String> ext = column("ext", JDBCType.LONGVARCHAR).withJavaProperty("ext");

        public final SqlColumn<String> sourceMessage = column("source_message", JDBCType.LONGVARCHAR).withJavaProperty("sourceMessage");

        public ChatHistory() {
            super("chat_history", ChatHistory::new);
        }
    }
}