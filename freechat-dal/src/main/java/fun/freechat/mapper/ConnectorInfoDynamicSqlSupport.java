package fun.freechat.mapper;

import java.sql.JDBCType;
import java.time.LocalDateTime;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class ConnectorInfoDynamicSqlSupport {
    public static final ConnectorInfo connectorInfo = new ConnectorInfo();

    public static final SqlColumn<String> connectorId = connectorInfo.connectorId;

    public static final SqlColumn<String> userId = connectorInfo.userId;

    public static final SqlColumn<String> revision = connectorInfo.revision;

    public static final SqlColumn<String> name = connectorInfo.name;

    public static final SqlColumn<String> transport = connectorInfo.transport;

    public static final SqlColumn<String> url = connectorInfo.url;

    public static final SqlColumn<Integer> timeout = connectorInfo.timeout;

    public static final SqlColumn<Byte> active = connectorInfo.active;

    public static final SqlColumn<LocalDateTime> gmtCreate = connectorInfo.gmtCreate;

    public static final SqlColumn<LocalDateTime> gmtModified = connectorInfo.gmtModified;

    public static final SqlColumn<String> headers = connectorInfo.headers;

    public static final SqlColumn<String> headerNames = connectorInfo.headerNames;

    public static final class ConnectorInfo extends AliasableSqlTable<ConnectorInfo> {
        public final SqlColumn<String> connectorId = column("connector_id", JDBCType.VARCHAR).withJavaProperty("connectorId");

        public final SqlColumn<String> userId = column("user_id", JDBCType.VARCHAR).withJavaProperty("userId");

        public final SqlColumn<String> revision = column("revision", JDBCType.VARCHAR).withJavaProperty("revision");

        public final SqlColumn<String> name = column("name", JDBCType.VARCHAR).withJavaProperty("name");

        public final SqlColumn<String> transport = column("transport", JDBCType.VARCHAR).withJavaProperty("transport");

        public final SqlColumn<String> url = column("url", JDBCType.VARCHAR).withJavaProperty("url");

        public final SqlColumn<Integer> timeout = column("timeout", JDBCType.INTEGER).withJavaProperty("timeout");

        public final SqlColumn<Byte> active = column("active", JDBCType.TINYINT).withJavaProperty("active");

        public final SqlColumn<LocalDateTime> gmtCreate = column("gmt_create", JDBCType.TIMESTAMP).withJavaProperty("gmtCreate");

        public final SqlColumn<LocalDateTime> gmtModified = column("gmt_modified", JDBCType.TIMESTAMP).withJavaProperty("gmtModified");

        public final SqlColumn<String> headers = column("headers", JDBCType.LONGVARCHAR).withJavaProperty("headers");

        public final SqlColumn<String> headerNames = column("header_names", JDBCType.LONGVARCHAR).withJavaProperty("headerNames");

        public ConnectorInfo() {
            super("connector_info", ConnectorInfo::new);
        }
    }
}