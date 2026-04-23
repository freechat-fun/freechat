package fun.freechat.mapper;

import java.sql.JDBCType;
import java.time.LocalDateTime;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class BindingDynamicSqlSupport {
    public static final Binding binding = new Binding();

    public static final SqlColumn<Long> id = binding.id;

    public static final SqlColumn<LocalDateTime> gmtCreate = binding.gmtCreate;

    public static final SqlColumn<LocalDateTime> gmtModified = binding.gmtModified;

    public static final SqlColumn<String> userId = binding.userId;

    public static final SqlColumn<String> platform = binding.platform;

    public static final SqlColumn<String> sub = binding.sub;

    public static final SqlColumn<String> iss = binding.iss;

    public static final SqlColumn<LocalDateTime> issuedAt = binding.issuedAt;

    public static final SqlColumn<LocalDateTime> expiresAt = binding.expiresAt;

    public static final SqlColumn<String> aud = binding.aud;

    public static final SqlColumn<String> refreshToken = binding.refreshToken;

    public static final class Binding extends AliasableSqlTable<Binding> {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT).withJavaProperty("id");

        public final SqlColumn<LocalDateTime> gmtCreate = column("gmt_create", JDBCType.TIMESTAMP).withJavaProperty("gmtCreate");

        public final SqlColumn<LocalDateTime> gmtModified = column("gmt_modified", JDBCType.TIMESTAMP).withJavaProperty("gmtModified");

        public final SqlColumn<String> userId = column("user_id", JDBCType.VARCHAR).withJavaProperty("userId");

        public final SqlColumn<String> platform = column("platform", JDBCType.VARCHAR).withJavaProperty("platform");

        public final SqlColumn<String> sub = column("sub", JDBCType.VARCHAR).withJavaProperty("sub");

        public final SqlColumn<String> iss = column("iss", JDBCType.VARCHAR).withJavaProperty("iss");

        public final SqlColumn<LocalDateTime> issuedAt = column("issued_at", JDBCType.TIMESTAMP).withJavaProperty("issuedAt");

        public final SqlColumn<LocalDateTime> expiresAt = column("expires_at", JDBCType.TIMESTAMP).withJavaProperty("expiresAt");

        public final SqlColumn<String> aud = column("aud", JDBCType.LONGVARCHAR).withJavaProperty("aud");

        public final SqlColumn<String> refreshToken = column("refresh_token", JDBCType.LONGVARCHAR).withJavaProperty("refreshToken");

        public Binding() {
            super("binding", Binding::new);
        }
    }
}