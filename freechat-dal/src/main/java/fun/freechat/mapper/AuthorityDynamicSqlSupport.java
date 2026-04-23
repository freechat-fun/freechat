package fun.freechat.mapper;

import java.sql.JDBCType;
import java.time.LocalDateTime;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class AuthorityDynamicSqlSupport {
    public static final Authority authority = new Authority();

    public static final SqlColumn<Long> id = authority.id;

    public static final SqlColumn<LocalDateTime> gmtCreate = authority.gmtCreate;

    public static final SqlColumn<LocalDateTime> gmtModified = authority.gmtModified;

    public static final SqlColumn<String> userId = authority.userId;

    public static final SqlColumn<String> scope = authority.scope;

    public static final class Authority extends AliasableSqlTable<Authority> {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT).withJavaProperty("id");

        public final SqlColumn<LocalDateTime> gmtCreate = column("gmt_create", JDBCType.TIMESTAMP).withJavaProperty("gmtCreate");

        public final SqlColumn<LocalDateTime> gmtModified = column("gmt_modified", JDBCType.TIMESTAMP).withJavaProperty("gmtModified");

        public final SqlColumn<String> userId = column("user_id", JDBCType.VARCHAR).withJavaProperty("userId");

        public final SqlColumn<String> scope = column("scope", JDBCType.LONGVARCHAR).withJavaProperty("scope");

        public Authority() {
            super("authority", Authority::new);
        }
    }
}