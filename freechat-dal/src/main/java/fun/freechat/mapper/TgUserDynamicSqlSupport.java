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

    public static final SqlColumn<String> backendId = tgUser.backendId;

    public static final SqlColumn<Long> tgUserId = tgUser.tgUserId;

    public static final SqlColumn<String> username = tgUser.username;

    public static final SqlColumn<String> firstName = tgUser.firstName;

    public static final SqlColumn<String> lastName = tgUser.lastName;

    public static final SqlColumn<String> ext = tgUser.ext;

    public static final class TgUser extends AliasableSqlTable<TgUser> {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT).withJavaProperty("id");

        public final SqlColumn<LocalDateTime> gmtCreate = column("gmt_create", JDBCType.TIMESTAMP).withJavaProperty("gmtCreate");

        public final SqlColumn<LocalDateTime> gmtModified = column("gmt_modified", JDBCType.TIMESTAMP).withJavaProperty("gmtModified");

        public final SqlColumn<String> backendId = column("backend_id", JDBCType.VARCHAR).withJavaProperty("backendId");

        public final SqlColumn<Long> tgUserId = column("tg_user_id", JDBCType.BIGINT).withJavaProperty("tgUserId");

        public final SqlColumn<String> username = column("username", JDBCType.VARCHAR).withJavaProperty("username");

        public final SqlColumn<String> firstName = column("first_name", JDBCType.VARCHAR).withJavaProperty("firstName");

        public final SqlColumn<String> lastName = column("last_name", JDBCType.VARCHAR).withJavaProperty("lastName");

        public final SqlColumn<String> ext = column("ext", JDBCType.LONGVARCHAR).withJavaProperty("ext");

        public TgUser() {
            super("tg_user", TgUser::new);
        }
    }
}