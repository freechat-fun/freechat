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
    public static final SqlColumn<String> userId = chatContext.userId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> characterId = chatContext.characterId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> context = chatContext.context;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final class ChatContext extends AliasableSqlTable<ChatContext> {
        public final SqlColumn<String> chatId = column("chat_id", JDBCType.VARCHAR);

        public final SqlColumn<Date> gmtCreate = column("gmt_create", JDBCType.TIMESTAMP);

        public final SqlColumn<Date> gmtModified = column("gmt_modified", JDBCType.TIMESTAMP);

        public final SqlColumn<String> userId = column("user_id", JDBCType.VARCHAR);

        public final SqlColumn<String> characterId = column("character_id", JDBCType.VARCHAR);

        public final SqlColumn<String> context = column("context", JDBCType.LONGVARCHAR);

        public ChatContext() {
            super("chat_context", ChatContext::new);
        }
    }
}