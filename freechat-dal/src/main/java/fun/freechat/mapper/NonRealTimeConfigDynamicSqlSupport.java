package fun.freechat.mapper;

import jakarta.annotation.Generated;
import java.sql.JDBCType;
import java.util.Date;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class NonRealTimeConfigDynamicSqlSupport {
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final NonRealTimeConfig nonRealTimeConfig = new NonRealTimeConfig();

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> name = nonRealTimeConfig.name;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Integer> version = nonRealTimeConfig.version;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Date> gmtCreate = nonRealTimeConfig.gmtCreate;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Date> gmtModified = nonRealTimeConfig.gmtModified;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> format = nonRealTimeConfig.format;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> userId = nonRealTimeConfig.userId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> content = nonRealTimeConfig.content;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final class NonRealTimeConfig extends AliasableSqlTable<NonRealTimeConfig> {
        public final SqlColumn<String> name = column("name", JDBCType.VARCHAR);

        public final SqlColumn<Integer> version = column("version", JDBCType.INTEGER);

        public final SqlColumn<Date> gmtCreate = column("gmt_create", JDBCType.TIMESTAMP);

        public final SqlColumn<Date> gmtModified = column("gmt_modified", JDBCType.TIMESTAMP);

        public final SqlColumn<String> format = column("format", JDBCType.VARCHAR);

        public final SqlColumn<String> userId = column("user_id", JDBCType.VARCHAR);

        public final SqlColumn<String> content = column("content", JDBCType.LONGVARCHAR);

        public NonRealTimeConfig() {
            super("non_real_time_config", NonRealTimeConfig::new);
        }
    }
}