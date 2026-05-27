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

    public static final SqlColumn<String> backendId = tgChat.backendId;

    public static final SqlColumn<Long> tgChatId = tgChat.tgChatId;

    public static final SqlColumn<String> chatType = tgChat.chatType;

    public static final SqlColumn<String> title = tgChat.title;

    public static final SqlColumn<String> ext = tgChat.ext;

    public static final class TgChat extends AliasableSqlTable<TgChat> {
        public final SqlColumn<String> chatId = column("chat_id", JDBCType.VARCHAR).withJavaProperty("chatId");

        public final SqlColumn<LocalDateTime> gmtCreate = column("gmt_create", JDBCType.TIMESTAMP).withJavaProperty("gmtCreate");

        public final SqlColumn<LocalDateTime> gmtModified = column("gmt_modified", JDBCType.TIMESTAMP).withJavaProperty("gmtModified");

        public final SqlColumn<String> backendId = column("backend_id", JDBCType.VARCHAR).withJavaProperty("backendId");

        public final SqlColumn<Long> tgChatId = column("tg_chat_id", JDBCType.BIGINT).withJavaProperty("tgChatId");

        public final SqlColumn<String> chatType = column("chat_type", JDBCType.VARCHAR).withJavaProperty("chatType");

        public final SqlColumn<String> title = column("title", JDBCType.VARCHAR).withJavaProperty("title");

        public final SqlColumn<String> ext = column("ext", JDBCType.LONGVARCHAR).withJavaProperty("ext");

        public TgChat() {
            super("tg_chat", TgChat::new);
        }
    }
}