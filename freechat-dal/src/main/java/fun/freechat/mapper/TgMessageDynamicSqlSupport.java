package fun.freechat.mapper;

import java.sql.JDBCType;
import java.time.LocalDateTime;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class TgMessageDynamicSqlSupport {
    public static final TgMessage tgMessage = new TgMessage();

    public static final SqlColumn<Long> id = tgMessage.id;

    public static final SqlColumn<LocalDateTime> gmtCreate = tgMessage.gmtCreate;

    public static final SqlColumn<LocalDateTime> gmtModified = tgMessage.gmtModified;

    public static final SqlColumn<String> chatId = tgMessage.chatId;

    public static final SqlColumn<Long> tgMessageId = tgMessage.tgMessageId;

    public static final SqlColumn<Long> tgUserId = tgMessage.tgUserId;

    public static final SqlColumn<String> direction = tgMessage.direction;

    public static final SqlColumn<String> messageType = tgMessage.messageType;

    public static final SqlColumn<String> content = tgMessage.content;

    public static final SqlColumn<String> payload = tgMessage.payload;

    public static final SqlColumn<String> ext = tgMessage.ext;

    public static final class TgMessage extends AliasableSqlTable<TgMessage> {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT).withJavaProperty("id");

        public final SqlColumn<LocalDateTime> gmtCreate = column("gmt_create", JDBCType.TIMESTAMP).withJavaProperty("gmtCreate");

        public final SqlColumn<LocalDateTime> gmtModified = column("gmt_modified", JDBCType.TIMESTAMP).withJavaProperty("gmtModified");

        public final SqlColumn<String> chatId = column("chat_id", JDBCType.VARCHAR).withJavaProperty("chatId");

        public final SqlColumn<Long> tgMessageId = column("tg_message_id", JDBCType.BIGINT).withJavaProperty("tgMessageId");

        public final SqlColumn<Long> tgUserId = column("tg_user_id", JDBCType.BIGINT).withJavaProperty("tgUserId");

        public final SqlColumn<String> direction = column("direction", JDBCType.VARCHAR).withJavaProperty("direction");

        public final SqlColumn<String> messageType = column("message_type", JDBCType.VARCHAR).withJavaProperty("messageType");

        public final SqlColumn<String> content = column("content", JDBCType.LONGVARCHAR).withJavaProperty("content");

        public final SqlColumn<String> payload = column("payload", JDBCType.LONGVARCHAR).withJavaProperty("payload");

        public final SqlColumn<String> ext = column("ext", JDBCType.LONGVARCHAR).withJavaProperty("ext");

        public TgMessage() {
            super("tg_message", TgMessage::new);
        }
    }
}