package fun.freechat.mapper;

import java.sql.JDBCType;
import java.time.LocalDateTime;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class ShortLinkDynamicSqlSupport {
    public static final ShortLink shortLink = new ShortLink();

    public static final SqlColumn<Long> id = shortLink.id;

    public static final SqlColumn<LocalDateTime> gmtCreate = shortLink.gmtCreate;

    public static final SqlColumn<LocalDateTime> gmtModified = shortLink.gmtModified;

    public static final SqlColumn<String> token = shortLink.token;

    public static final SqlColumn<String> path = shortLink.path;

    public static final SqlColumn<LocalDateTime> expiresAt = shortLink.expiresAt;

    public static final class ShortLink extends AliasableSqlTable<ShortLink> {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT).withJavaProperty("id");

        public final SqlColumn<LocalDateTime> gmtCreate = column("gmt_create", JDBCType.TIMESTAMP).withJavaProperty("gmtCreate");

        public final SqlColumn<LocalDateTime> gmtModified = column("gmt_modified", JDBCType.TIMESTAMP).withJavaProperty("gmtModified");

        public final SqlColumn<String> token = column("token", JDBCType.VARCHAR).withJavaProperty("token");

        public final SqlColumn<String> path = column("path", JDBCType.VARCHAR).withJavaProperty("path");

        public final SqlColumn<LocalDateTime> expiresAt = column("expires_at", JDBCType.TIMESTAMP).withJavaProperty("expiresAt");

        public ShortLink() {
            super("short_link", ShortLink::new);
        }
    }
}