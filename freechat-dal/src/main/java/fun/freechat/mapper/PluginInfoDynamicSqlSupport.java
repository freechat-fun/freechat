package fun.freechat.mapper;

import jakarta.annotation.Generated;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

import java.sql.JDBCType;
import java.util.Date;

public final class PluginInfoDynamicSqlSupport {
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final PluginInfo pluginInfo = new PluginInfo();

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Long> pluginId = pluginInfo.pluginId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> pluginUid = pluginInfo.pluginUid;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Date> gmtCreate = pluginInfo.gmtCreate;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Date> gmtModified = pluginInfo.gmtModified;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> userId = pluginInfo.userId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> visibility = pluginInfo.visibility;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> name = pluginInfo.name;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> manifestFormat = pluginInfo.manifestFormat;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> apiFormat = pluginInfo.apiFormat;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> provider = pluginInfo.provider;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> manifestInfo = pluginInfo.manifestInfo;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> apiInfo = pluginInfo.apiInfo;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> ext = pluginInfo.ext;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final class PluginInfo extends AliasableSqlTable<PluginInfo> {
        public final SqlColumn<Long> pluginId = column("plugin_id", JDBCType.BIGINT);

        public final SqlColumn<String> pluginUid = column("plugin_uid", JDBCType.VARCHAR);

        public final SqlColumn<Date> gmtCreate = column("gmt_create", JDBCType.TIMESTAMP);

        public final SqlColumn<Date> gmtModified = column("gmt_modified", JDBCType.TIMESTAMP);

        public final SqlColumn<String> userId = column("user_id", JDBCType.VARCHAR);

        public final SqlColumn<String> visibility = column("visibility", JDBCType.VARCHAR);

        public final SqlColumn<String> name = column("name", JDBCType.VARCHAR);

        public final SqlColumn<String> manifestFormat = column("manifest_format", JDBCType.VARCHAR);

        public final SqlColumn<String> apiFormat = column("api_format", JDBCType.VARCHAR);

        public final SqlColumn<String> provider = column("provider", JDBCType.LONGVARCHAR);

        public final SqlColumn<String> manifestInfo = column("manifest_info", JDBCType.LONGVARCHAR);

        public final SqlColumn<String> apiInfo = column("api_info", JDBCType.LONGVARCHAR);

        public final SqlColumn<String> ext = column("ext", JDBCType.LONGVARCHAR);

        public PluginInfo() {
            super("plugin_info", PluginInfo::new);
        }
    }
}