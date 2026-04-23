package fun.freechat.mapper;

import java.sql.JDBCType;
import java.time.LocalDateTime;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class ChatContextDynamicSqlSupport {
    public static final ChatContext chatContext = new ChatContext();

    public static final SqlColumn<String> chatId = chatContext.chatId;

    public static final SqlColumn<LocalDateTime> gmtCreate = chatContext.gmtCreate;

    public static final SqlColumn<LocalDateTime> gmtModified = chatContext.gmtModified;

    public static final SqlColumn<LocalDateTime> gmtRead = chatContext.gmtRead;

    public static final SqlColumn<String> chatType = chatContext.chatType;

    public static final SqlColumn<String> userId = chatContext.userId;

    public static final SqlColumn<String> userNickname = chatContext.userNickname;

    public static final SqlColumn<String> backendId = chatContext.backendId;

    public static final SqlColumn<String> characterNickname = chatContext.characterNickname;

    public static final SqlColumn<String> apiKeyName = chatContext.apiKeyName;

    public static final SqlColumn<Long> quota = chatContext.quota;

    public static final SqlColumn<String> quotaType = chatContext.quotaType;

    public static final SqlColumn<String> userProfile = chatContext.userProfile;

    public static final SqlColumn<String> about = chatContext.about;

    public static final SqlColumn<String> apiKeyValue = chatContext.apiKeyValue;

    public static final SqlColumn<String> ext = chatContext.ext;

    public static final class ChatContext extends AliasableSqlTable<ChatContext> {
        public final SqlColumn<String> chatId = column("chat_id", JDBCType.VARCHAR).withJavaProperty("chatId");

        public final SqlColumn<LocalDateTime> gmtCreate = column("gmt_create", JDBCType.TIMESTAMP).withJavaProperty("gmtCreate");

        public final SqlColumn<LocalDateTime> gmtModified = column("gmt_modified", JDBCType.TIMESTAMP).withJavaProperty("gmtModified");

        public final SqlColumn<LocalDateTime> gmtRead = column("gmt_read", JDBCType.TIMESTAMP).withJavaProperty("gmtRead");

        public final SqlColumn<String> chatType = column("chat_type", JDBCType.VARCHAR).withJavaProperty("chatType");

        public final SqlColumn<String> userId = column("user_id", JDBCType.VARCHAR).withJavaProperty("userId");

        public final SqlColumn<String> userNickname = column("user_nickname", JDBCType.VARCHAR).withJavaProperty("userNickname");

        public final SqlColumn<String> backendId = column("backend_id", JDBCType.VARCHAR).withJavaProperty("backendId");

        public final SqlColumn<String> characterNickname = column("character_nickname", JDBCType.VARCHAR).withJavaProperty("characterNickname");

        public final SqlColumn<String> apiKeyName = column("api_key_name", JDBCType.VARCHAR).withJavaProperty("apiKeyName");

        public final SqlColumn<Long> quota = column("quota", JDBCType.BIGINT).withJavaProperty("quota");

        public final SqlColumn<String> quotaType = column("quota_type", JDBCType.VARCHAR).withJavaProperty("quotaType");

        public final SqlColumn<String> userProfile = column("user_profile", JDBCType.LONGVARCHAR).withJavaProperty("userProfile");

        public final SqlColumn<String> about = column("about", JDBCType.LONGVARCHAR).withJavaProperty("about");

        public final SqlColumn<String> apiKeyValue = column("api_key_value", JDBCType.LONGVARCHAR).withJavaProperty("apiKeyValue");

        public final SqlColumn<String> ext = column("ext", JDBCType.LONGVARCHAR).withJavaProperty("ext");

        public ChatContext() {
            super("chat_context", ChatContext::new);
        }
    }
}