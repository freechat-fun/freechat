package fun.freechat.mapper;

import jakarta.annotation.Generated;
import java.sql.JDBCType;
import java.util.Date;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class AgentInfoDynamicSqlSupport {
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final AgentInfo agentInfo = new AgentInfo();

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Long> agentId = agentInfo.agentId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> agentUid = agentInfo.agentUid;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Date> gmtCreate = agentInfo.gmtCreate;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Date> gmtModified = agentInfo.gmtModified;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> userId = agentInfo.userId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> parentUid = agentInfo.parentUid;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> visibility = agentInfo.visibility;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> name = agentInfo.name;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> format = agentInfo.format;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Integer> version = agentInfo.version;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> description = agentInfo.description;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> config = agentInfo.config;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> example = agentInfo.example;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> parameters = agentInfo.parameters;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> ext = agentInfo.ext;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> draft = agentInfo.draft;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final class AgentInfo extends AliasableSqlTable<AgentInfo> {
        public final SqlColumn<Long> agentId = column("agent_id", JDBCType.BIGINT);

        public final SqlColumn<String> agentUid = column("agent_uid", JDBCType.VARCHAR);

        public final SqlColumn<Date> gmtCreate = column("gmt_create", JDBCType.TIMESTAMP);

        public final SqlColumn<Date> gmtModified = column("gmt_modified", JDBCType.TIMESTAMP);

        public final SqlColumn<String> userId = column("user_id", JDBCType.VARCHAR);

        public final SqlColumn<String> parentUid = column("parent_uid", JDBCType.VARCHAR);

        public final SqlColumn<String> visibility = column("visibility", JDBCType.VARCHAR);

        public final SqlColumn<String> name = column("name", JDBCType.VARCHAR);

        public final SqlColumn<String> format = column("format", JDBCType.VARCHAR);

        public final SqlColumn<Integer> version = column("version", JDBCType.INTEGER);

        public final SqlColumn<String> description = column("description", JDBCType.LONGVARCHAR);

        public final SqlColumn<String> config = column("config", JDBCType.LONGVARCHAR);

        public final SqlColumn<String> example = column("example", JDBCType.LONGVARCHAR);

        public final SqlColumn<String> parameters = column("parameters", JDBCType.LONGVARCHAR);

        public final SqlColumn<String> ext = column("ext", JDBCType.LONGVARCHAR);

        public final SqlColumn<String> draft = column("draft", JDBCType.LONGVARCHAR);

        public AgentInfo() {
            super("agent_info", AgentInfo::new);
        }
    }
}