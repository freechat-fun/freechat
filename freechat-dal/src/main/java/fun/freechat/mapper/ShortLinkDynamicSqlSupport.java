package fun.freechat.mapper;

import jakarta.annotation.Generated;
import java.sql.JDBCType;
import java.util.Date;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class ShortLinkDynamicSqlSupport {
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final ShortLink shortLink = new ShortLink();

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Long> id = shortLink.id;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Date> gmtCreate = shortLink.gmtCreate;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Date> gmtModified = shortLink.gmtModified;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> token = shortLink.token;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> path = shortLink.path;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Date> expiresAt = shortLink.expiresAt;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final class ShortLink extends AliasableSqlTable<ShortLink> {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT);

        public final SqlColumn<Date> gmtCreate = column("gmt_create", JDBCType.TIMESTAMP);

        public final SqlColumn<Date> gmtModified = column("gmt_modified", JDBCType.TIMESTAMP);

        public final SqlColumn<String> token = column("token", JDBCType.VARCHAR);

        public final SqlColumn<String> path = column("path", JDBCType.VARCHAR);

        public final SqlColumn<Date> expiresAt = column("expires_at", JDBCType.TIMESTAMP);

        public ShortLink() {
            super("short_link", ShortLink::new);
        }
    }
}