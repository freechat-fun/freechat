package fun.freechat.mapper;

import java.sql.JDBCType;
import java.time.LocalDateTime;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class TgChatDynamicSqlSupport {
    public static final TgChat tgChat = new TgChat();

    public static final SqlColumn<String> chatId = tgChat.chatId;

    public static final SqlColumn<LocalDateTime> gmtCreate = tgChat.gmtCreate;

    public static final SqlColumn<LocalDateTime> gmtModified = tgChat.gmtModified;

    public static final SqlColumn<LocalDateTime> gmtRead = tgChat.gmtRead;

    public static final SqlColumn<String> botId = tgChat.botId;

    public static final SqlColumn<Long> tgChatId = tgChat.tgChatId;

    public static final SqlColumn<Long> tgUserId = tgChat.tgUserId;

    public static final SqlColumn<String> chatType = tgChat.chatType;

    public static final SqlColumn<String> title = tgChat.title;

    public static final SqlColumn<Long> lastMessageId = tgChat.lastMessageId;

    public static final SqlColumn<Long> messageCount = tgChat.messageCount;

    public static final SqlColumn<Byte> enabled = tgChat.enabled;

    public static final SqlColumn<String> ext = tgChat.ext;

    public static final class TgChat extends AliasableSqlTable<TgChat> {
        public final SqlColumn<String> chatId = column("chat_id", JDBCType.VARCHAR).withJavaProperty("chatId");

        public final SqlColumn<LocalDateTime> gmtCreate = column("gmt_create", JDBCType.TIMESTAMP).withJavaProperty("gmtCreate");

        public final SqlColumn<LocalDateTime> gmtModified = column("gmt_modified", JDBCType.TIMESTAMP).withJavaProperty("gmtModified");

        public final SqlColumn<LocalDateTime> gmtRead = column("gmt_read", JDBCType.TIMESTAMP).withJavaProperty("gmtRead");

        public final SqlColumn<String> botId = column("bot_id", JDBCType.VARCHAR).withJavaProperty("botId");

        public final SqlColumn<Long> tgChatId = column("tg_chat_id", JDBCType.BIGINT).withJavaProperty("tgChatId");

        public final SqlColumn<Long> tgUserId = column("tg_user_id", JDBCType.BIGINT).withJavaProperty("tgUserId");

        public final SqlColumn<String> chatType = column("chat_type", JDBCType.VARCHAR).withJavaProperty("chatType");

        public final SqlColumn<String> title = column("title", JDBCType.VARCHAR).withJavaProperty("title");

        public final SqlColumn<Long> lastMessageId = column("last_message_id", JDBCType.BIGINT).withJavaProperty("lastMessageId");

        public final SqlColumn<Long> messageCount = column("message_count", JDBCType.BIGINT).withJavaProperty("messageCount");

        public final SqlColumn<Byte> enabled = column("enabled", JDBCType.TINYINT).withJavaProperty("enabled");

        public final SqlColumn<String> ext = column("ext", JDBCType.LONGVARCHAR).withJavaProperty("ext");

        public TgChat() {
            super("tg_chat", TgChat::new);
        }
    }
}