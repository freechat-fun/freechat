package fun.freechat.mapper;

import java.sql.JDBCType;
import java.time.LocalDateTime;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class AgentInfoDynamicSqlSupport {
    public static final AgentInfo agentInfo = new AgentInfo();

    public static final SqlColumn<Long> agentId = agentInfo.agentId;

    public static final SqlColumn<String> agentUid = agentInfo.agentUid;

    public static final SqlColumn<LocalDateTime> gmtCreate = agentInfo.gmtCreate;

    public static final SqlColumn<LocalDateTime> gmtModified = agentInfo.gmtModified;

    public static final SqlColumn<String> userId = agentInfo.userId;

    public static final SqlColumn<String> parentUid = agentInfo.parentUid;

    public static final SqlColumn<String> visibility = agentInfo.visibility;

    public static final SqlColumn<String> name = agentInfo.name;

    public static final SqlColumn<String> format = agentInfo.format;

    public static final SqlColumn<Integer> version = agentInfo.version;

    public static final SqlColumn<String> description = agentInfo.description;

    public static final SqlColumn<String> config = agentInfo.config;

    public static final SqlColumn<String> example = agentInfo.example;

    public static final SqlColumn<String> parameters = agentInfo.parameters;

    public static final SqlColumn<String> ext = agentInfo.ext;

    public static final SqlColumn<String> draft = agentInfo.draft;

    public static final class AgentInfo extends AliasableSqlTable<AgentInfo> {
        public final SqlColumn<Long> agentId = column("agent_id", JDBCType.BIGINT).withJavaProperty("agentId");

        public final SqlColumn<String> agentUid = column("agent_uid", JDBCType.VARCHAR).withJavaProperty("agentUid");

        public final SqlColumn<LocalDateTime> gmtCreate = column("gmt_create", JDBCType.TIMESTAMP).withJavaProperty("gmtCreate");

        public final SqlColumn<LocalDateTime> gmtModified = column("gmt_modified", JDBCType.TIMESTAMP).withJavaProperty("gmtModified");

        public final SqlColumn<String> userId = column("user_id", JDBCType.VARCHAR).withJavaProperty("userId");

        public final SqlColumn<String> parentUid = column("parent_uid", JDBCType.VARCHAR).withJavaProperty("parentUid");

        public final SqlColumn<String> visibility = column("visibility", JDBCType.VARCHAR).withJavaProperty("visibility");

        public final SqlColumn<String> name = column("name", JDBCType.VARCHAR).withJavaProperty("name");

        public final SqlColumn<String> format = column("format", JDBCType.VARCHAR).withJavaProperty("format");

        public final SqlColumn<Integer> version = column("version", JDBCType.INTEGER).withJavaProperty("version");

        public final SqlColumn<String> description = column("description", JDBCType.LONGVARCHAR).withJavaProperty("description");

        public final SqlColumn<String> config = column("config", JDBCType.LONGVARCHAR).withJavaProperty("config");

        public final SqlColumn<String> example = column("example", JDBCType.LONGVARCHAR).withJavaProperty("example");

        public final SqlColumn<String> parameters = column("parameters", JDBCType.LONGVARCHAR).withJavaProperty("parameters");

        public final SqlColumn<String> ext = column("ext", JDBCType.LONGVARCHAR).withJavaProperty("ext");

        public final SqlColumn<String> draft = column("draft", JDBCType.LONGVARCHAR).withJavaProperty("draft");

        public AgentInfo() {
            super("agent_info", AgentInfo::new);
        }
    }
}