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

    public static final SqlColumn<String> message = chatHistory.message;

    public static final SqlColumn<String> ext = chatHistory.ext;

    public static final class ChatHistory extends AliasableSqlTable<ChatHistory> {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT).withJavaProperty("id");

        public final SqlColumn<String> memoryId = column("memory_id", JDBCType.VARCHAR).withJavaProperty("memoryId");

        public final SqlColumn<LocalDateTime> gmtCreate = column("gmt_create", JDBCType.TIMESTAMP).withJavaProperty("gmtCreate");

        public final SqlColumn<LocalDateTime> gmtModified = column("gmt_modified", JDBCType.TIMESTAMP).withJavaProperty("gmtModified");

        public final SqlColumn<Byte> enabled = column("enabled", JDBCType.TINYINT).withJavaProperty("enabled");

        public final SqlColumn<Long> tgMessageId = column("tg_message_id", JDBCType.BIGINT).withJavaProperty("tgMessageId");

        public final SqlColumn<String> message = column("message", JDBCType.LONGVARCHAR).withJavaProperty("message");

        public final SqlColumn<String> ext = column("ext", JDBCType.LONGVARCHAR).withJavaProperty("ext");

        public ChatHistory() {
            super("chat_history", ChatHistory::new);
        }
    }
}