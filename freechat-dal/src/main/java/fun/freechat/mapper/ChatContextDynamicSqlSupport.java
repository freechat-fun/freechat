package fun.freechat.mapper;

import jakarta.annotation.Generated;
import java.sql.JDBCType;
import java.util.Date;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class ChatContextDynamicSqlSupport {
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final ChatContext chatContext = new ChatContext();

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> chatId = chatContext.chatId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Date> gmtCreate = chatContext.gmtCreate;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Date> gmtModified = chatContext.gmtModified;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Date> gmtRead = chatContext.gmtRead;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> chatType = chatContext.chatType;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> userId = chatContext.userId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> userNickname = chatContext.userNickname;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> backendId = chatContext.backendId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> characterNickname = chatContext.characterNickname;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> userProfile = chatContext.userProfile;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> about = chatContext.about;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> ext = chatContext.ext;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final class ChatContext extends AliasableSqlTable<ChatContext> {
        public final SqlColumn<String> chatId = column("chat_id", JDBCType.VARCHAR);

        public final SqlColumn<Date> gmtCreate = column("gmt_create", JDBCType.TIMESTAMP);

        public final SqlColumn<Date> gmtModified = column("gmt_modified", JDBCType.TIMESTAMP);

        public final SqlColumn<Date> gmtRead = column("gmt_read", JDBCType.TIMESTAMP);

        public final SqlColumn<String> chatType = column("chat_type", JDBCType.VARCHAR);

        public final SqlColumn<String> userId = column("user_id", JDBCType.VARCHAR);

        public final SqlColumn<String> userNickname = column("user_nickname", JDBCType.VARCHAR);

        public final SqlColumn<String> backendId = column("backend_id", JDBCType.VARCHAR);

        public final SqlColumn<String> characterNickname = column("character_nickname", JDBCType.VARCHAR);

        public final SqlColumn<String> userProfile = column("user_profile", JDBCType.LONGVARCHAR);

        public final SqlColumn<String> about = column("about", JDBCType.LONGVARCHAR);

        public final SqlColumn<String> ext = column("ext", JDBCType.LONGVARCHAR);

        public ChatContext() {
            super("chat_context", ChatContext::new);
        }
    }
}