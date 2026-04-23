package fun.freechat.mapper;

import java.sql.JDBCType;
import java.time.LocalDateTime;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class ApiTokenDynamicSqlSupport {
    public static final ApiToken apiToken = new ApiToken();

    public static final SqlColumn<Long> id = apiToken.id;

    public static final SqlColumn<LocalDateTime> gmtCreate = apiToken.gmtCreate;

    public static final SqlColumn<LocalDateTime> gmtModified = apiToken.gmtModified;

    public static final SqlColumn<String> userId = apiToken.userId;

    public static final SqlColumn<String> token = apiToken.token;

    public static final SqlColumn<String> type = apiToken.type;

    public static final SqlColumn<LocalDateTime> issuedAt = apiToken.issuedAt;

    public static final SqlColumn<LocalDateTime> expiresAt = apiToken.expiresAt;

    public static final SqlColumn<String> policy = apiToken.policy;

    public static final class ApiToken extends AliasableSqlTable<ApiToken> {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT).withJavaProperty("id");

        public final SqlColumn<LocalDateTime> gmtCreate = column("gmt_create", JDBCType.TIMESTAMP).withJavaProperty("gmtCreate");

        public final SqlColumn<LocalDateTime> gmtModified = column("gmt_modified", JDBCType.TIMESTAMP).withJavaProperty("gmtModified");

        public final SqlColumn<String> userId = column("user_id", JDBCType.VARCHAR).withJavaProperty("userId");

        public final SqlColumn<String> token = column("token", JDBCType.VARCHAR).withJavaProperty("token");

        public final SqlColumn<String> type = column("type", JDBCType.VARCHAR).withJavaProperty("type");

        public final SqlColumn<LocalDateTime> issuedAt = column("issued_at", JDBCType.TIMESTAMP).withJavaProperty("issuedAt");

        public final SqlColumn<LocalDateTime> expiresAt = column("expires_at", JDBCType.TIMESTAMP).withJavaProperty("expiresAt");

        public final SqlColumn<String> policy = column("policy", JDBCType.LONGVARCHAR).withJavaProperty("policy");

        public ApiToken() {
            super("api_token", ApiToken::new);
        }
    }
}