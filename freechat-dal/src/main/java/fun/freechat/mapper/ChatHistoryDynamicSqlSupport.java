package fun.freechat.mapper;

import jakarta.annotation.Generated;
import java.sql.JDBCType;
import java.util.Date;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class ChatHistoryDynamicSqlSupport {
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final ChatHistory chatHistory = new ChatHistory();

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Long> id = chatHistory.id;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> memoryId = chatHistory.memoryId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Date> gmtCreate = chatHistory.gmtCreate;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Date> gmtModified = chatHistory.gmtModified;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Byte> enabled = chatHistory.enabled;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> message = chatHistory.message;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> systemMessage = chatHistory.systemMessage;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final class ChatHistory extends AliasableSqlTable<ChatHistory> {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT);

        public final SqlColumn<String> memoryId = column("memory_id", JDBCType.VARCHAR);

        public final SqlColumn<Date> gmtCreate = column("gmt_create", JDBCType.TIMESTAMP);

        public final SqlColumn<Date> gmtModified = column("gmt_modified", JDBCType.TIMESTAMP);

        public final SqlColumn<Byte> enabled = column("enabled", JDBCType.TINYINT);

        public final SqlColumn<String> message = column("message", JDBCType.LONGVARCHAR);

        public final SqlColumn<String> systemMessage = column("system_message", JDBCType.LONGVARCHAR);

        public ChatHistory() {
            super("chat_history", ChatHistory::new);
        }
    }
}