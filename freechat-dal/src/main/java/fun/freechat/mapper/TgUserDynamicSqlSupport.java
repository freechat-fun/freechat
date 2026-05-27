package fun.freechat.mapper;

import java.sql.JDBCType;
import java.time.LocalDateTime;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class TgUserDynamicSqlSupport {
    public static final TgUser tgUser = new TgUser();

    public static final SqlColumn<Long> id = tgUser.id;

    public static final SqlColumn<LocalDateTime> gmtCreate = tgUser.gmtCreate;

    public static final SqlColumn<LocalDateTime> gmtModified = tgUser.gmtModified;

    public static final SqlColumn<LocalDateTime> gmtActive = tgUser.gmtActive;

    public static final SqlColumn<String> botId = tgUser.botId;

    public static final SqlColumn<Long> tgUserId = tgUser.tgUserId;

    public static final SqlColumn<String> username = tgUser.username;

    public static final SqlColumn<String> firstName = tgUser.firstName;

    public static final SqlColumn<String> lastName = tgUser.lastName;

    public static final SqlColumn<String> lang = tgUser.lang;

    public static final SqlColumn<Byte> isBot = tgUser.isBot;

    public static final SqlColumn<Byte> isPremium = tgUser.isPremium;

    public static final SqlColumn<String> phoneNumber = tgUser.phoneNumber;

    public static final SqlColumn<Byte> enabled = tgUser.enabled;

    public static final SqlColumn<Byte> blocked = tgUser.blocked;

    public static final SqlColumn<String> ext = tgUser.ext;

    public static final class TgUser extends AliasableSqlTable<TgUser> {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT).withJavaProperty("id");

        public final SqlColumn<LocalDateTime> gmtCreate = column("gmt_create", JDBCType.TIMESTAMP).withJavaProperty("gmtCreate");

        public final SqlColumn<LocalDateTime> gmtModified = column("gmt_modified", JDBCType.TIMESTAMP).withJavaProperty("gmtModified");

        public final SqlColumn<LocalDateTime> gmtActive = column("gmt_active", JDBCType.TIMESTAMP).withJavaProperty("gmtActive");

        public final SqlColumn<String> botId = column("bot_id", JDBCType.VARCHAR).withJavaProperty("botId");

        public final SqlColumn<Long> tgUserId = column("tg_user_id", JDBCType.BIGINT).withJavaProperty("tgUserId");

        public final SqlColumn<String> username = column("username", JDBCType.VARCHAR).withJavaProperty("username");

        public final SqlColumn<String> firstName = column("first_name", JDBCType.VARCHAR).withJavaProperty("firstName");

        public final SqlColumn<String> lastName = column("last_name", JDBCType.VARCHAR).withJavaProperty("lastName");

        public final SqlColumn<String> lang = column("lang", JDBCType.VARCHAR).withJavaProperty("lang");

        public final SqlColumn<Byte> isBot = column("is_bot", JDBCType.TINYINT).withJavaProperty("isBot");

        public final SqlColumn<Byte> isPremium = column("is_premium", JDBCType.TINYINT).withJavaProperty("isPremium");

        public final SqlColumn<String> phoneNumber = column("phone_number", JDBCType.VARCHAR).withJavaProperty("phoneNumber");

        public final SqlColumn<Byte> enabled = column("enabled", JDBCType.TINYINT).withJavaProperty("enabled");

        public final SqlColumn<Byte> blocked = column("blocked", JDBCType.TINYINT).withJavaProperty("blocked");

        public final SqlColumn<String> ext = column("ext", JDBCType.LONGVARCHAR).withJavaProperty("ext");

        public TgUser() {
            super("tg_user", TgUser::new);
        }
    }
}