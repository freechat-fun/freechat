package fun.freechat.mapper;

import java.sql.JDBCType;
import java.time.LocalDateTime;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class PluginInfoDynamicSqlSupport {
    public static final PluginInfo pluginInfo = new PluginInfo();

    public static final SqlColumn<Long> pluginId = pluginInfo.pluginId;

    public static final SqlColumn<String> pluginUid = pluginInfo.pluginUid;

    public static final SqlColumn<LocalDateTime> gmtCreate = pluginInfo.gmtCreate;

    public static final SqlColumn<LocalDateTime> gmtModified = pluginInfo.gmtModified;

    public static final SqlColumn<String> userId = pluginInfo.userId;

    public static final SqlColumn<String> visibility = pluginInfo.visibility;

    public static final SqlColumn<String> name = pluginInfo.name;

    public static final SqlColumn<String> manifestFormat = pluginInfo.manifestFormat;

    public static final SqlColumn<String> apiFormat = pluginInfo.apiFormat;

    public static final SqlColumn<String> provider = pluginInfo.provider;

    public static final SqlColumn<String> manifestInfo = pluginInfo.manifestInfo;

    public static final SqlColumn<String> apiInfo = pluginInfo.apiInfo;

    public static final SqlColumn<String> ext = pluginInfo.ext;

    public static final class PluginInfo extends AliasableSqlTable<PluginInfo> {
        public final SqlColumn<Long> pluginId = column("plugin_id", JDBCType.BIGINT).withJavaProperty("pluginId");

        public final SqlColumn<String> pluginUid = column("plugin_uid", JDBCType.VARCHAR).withJavaProperty("pluginUid");

        public final SqlColumn<LocalDateTime> gmtCreate = column("gmt_create", JDBCType.TIMESTAMP).withJavaProperty("gmtCreate");

        public final SqlColumn<LocalDateTime> gmtModified = column("gmt_modified", JDBCType.TIMESTAMP).withJavaProperty("gmtModified");

        public final SqlColumn<String> userId = column("user_id", JDBCType.VARCHAR).withJavaProperty("userId");

        public final SqlColumn<String> visibility = column("visibility", JDBCType.VARCHAR).withJavaProperty("visibility");

        public final SqlColumn<String> name = column("name", JDBCType.VARCHAR).withJavaProperty("name");

        public final SqlColumn<String> manifestFormat = column("manifest_format", JDBCType.VARCHAR).withJavaProperty("manifestFormat");

        public final SqlColumn<String> apiFormat = column("api_format", JDBCType.VARCHAR).withJavaProperty("apiFormat");

        public final SqlColumn<String> provider = column("provider", JDBCType.LONGVARCHAR).withJavaProperty("provider");

        public final SqlColumn<String> manifestInfo = column("manifest_info", JDBCType.LONGVARCHAR).withJavaProperty("manifestInfo");

        public final SqlColumn<String> apiInfo = column("api_info", JDBCType.LONGVARCHAR).withJavaProperty("apiInfo");

        public final SqlColumn<String> ext = column("ext", JDBCType.LONGVARCHAR).withJavaProperty("ext");

        public PluginInfo() {
            super("plugin_info", PluginInfo::new);
        }
    }
}