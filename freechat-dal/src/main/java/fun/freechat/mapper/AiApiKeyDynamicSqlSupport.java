package fun.freechat.mapper;

import java.sql.JDBCType;
import java.time.LocalDateTime;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class AiApiKeyDynamicSqlSupport {
    public static final AiApiKey aiApiKey = new AiApiKey();

    public static final SqlColumn<Long> id = aiApiKey.id;

    public static final SqlColumn<LocalDateTime> gmtCreate = aiApiKey.gmtCreate;

    public static final SqlColumn<LocalDateTime> gmtModified = aiApiKey.gmtModified;

    public static final SqlColumn<LocalDateTime> gmtUsed = aiApiKey.gmtUsed;

    public static final SqlColumn<String> name = aiApiKey.name;

    public static final SqlColumn<String> provider = aiApiKey.provider;

    public static final SqlColumn<Byte> enabled = aiApiKey.enabled;

    public static final SqlColumn<String> userId = aiApiKey.userId;

    public static final SqlColumn<String> token = aiApiKey.token;

    public static final class AiApiKey extends AliasableSqlTable<AiApiKey> {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT).withJavaProperty("id");

        public final SqlColumn<LocalDateTime> gmtCreate = column("gmt_create", JDBCType.TIMESTAMP).withJavaProperty("gmtCreate");

        public final SqlColumn<LocalDateTime> gmtModified = column("gmt_modified", JDBCType.TIMESTAMP).withJavaProperty("gmtModified");

        public final SqlColumn<LocalDateTime> gmtUsed = column("gmt_used", JDBCType.TIMESTAMP).withJavaProperty("gmtUsed");

        public final SqlColumn<String> name = column("name", JDBCType.VARCHAR).withJavaProperty("name");

        public final SqlColumn<String> provider = column("provider", JDBCType.VARCHAR).withJavaProperty("provider");

        public final SqlColumn<Byte> enabled = column("enabled", JDBCType.TINYINT).withJavaProperty("enabled");

        public final SqlColumn<String> userId = column("user_id", JDBCType.VARCHAR).withJavaProperty("userId");

        public final SqlColumn<String> token = column("token", JDBCType.LONGVARCHAR).withJavaProperty("token");

        public AiApiKey() {
            super("ai_api_key", AiApiKey::new);
        }
    }
}