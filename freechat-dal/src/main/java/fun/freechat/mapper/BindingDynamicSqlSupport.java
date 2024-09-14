package fun.freechat.mapper;

import jakarta.annotation.Generated;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

import java.sql.JDBCType;
import java.util.Date;

public final class BindingDynamicSqlSupport {
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final Binding binding = new Binding();

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Long> id = binding.id;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Date> gmtCreate = binding.gmtCreate;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Date> gmtModified = binding.gmtModified;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> userId = binding.userId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> platform = binding.platform;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> sub = binding.sub;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> iss = binding.iss;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Date> issuedAt = binding.issuedAt;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Date> expiresAt = binding.expiresAt;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> aud = binding.aud;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> refreshToken = binding.refreshToken;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final class Binding extends AliasableSqlTable<Binding> {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT);

        public final SqlColumn<Date> gmtCreate = column("gmt_create", JDBCType.TIMESTAMP);

        public final SqlColumn<Date> gmtModified = column("gmt_modified", JDBCType.TIMESTAMP);

        public final SqlColumn<String> userId = column("user_id", JDBCType.VARCHAR);

        public final SqlColumn<String> platform = column("platform", JDBCType.VARCHAR);

        public final SqlColumn<String> sub = column("sub", JDBCType.VARCHAR);

        public final SqlColumn<String> iss = column("iss", JDBCType.VARCHAR);

        public final SqlColumn<Date> issuedAt = column("issued_at", JDBCType.TIMESTAMP);

        public final SqlColumn<Date> expiresAt = column("expires_at", JDBCType.TIMESTAMP);

        public final SqlColumn<String> aud = column("aud", JDBCType.LONGVARCHAR);

        public final SqlColumn<String> refreshToken = column("refresh_token", JDBCType.LONGVARCHAR);

        public Binding() {
            super("binding", Binding::new);
        }
    }
}