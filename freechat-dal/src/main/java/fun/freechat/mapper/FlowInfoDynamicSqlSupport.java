package fun.freechat.mapper;

import jakarta.annotation.Generated;
import java.sql.JDBCType;
import java.util.Date;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class FlowInfoDynamicSqlSupport {
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final FlowInfo flowInfo = new FlowInfo();

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> flowId = flowInfo.flowId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Date> gmtCreate = flowInfo.gmtCreate;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Date> gmtModified = flowInfo.gmtModified;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> userId = flowInfo.userId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> parentId = flowInfo.parentId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> visibility = flowInfo.visibility;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> name = flowInfo.name;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> format = flowInfo.format;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Integer> version = flowInfo.version;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> description = flowInfo.description;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> config = flowInfo.config;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> example = flowInfo.example;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> parameters = flowInfo.parameters;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> ext = flowInfo.ext;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> draft = flowInfo.draft;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final class FlowInfo extends AliasableSqlTable<FlowInfo> {
        public final SqlColumn<String> flowId = column("flow_id", JDBCType.VARCHAR);

        public final SqlColumn<Date> gmtCreate = column("gmt_create", JDBCType.TIMESTAMP);

        public final SqlColumn<Date> gmtModified = column("gmt_modified", JDBCType.TIMESTAMP);

        public final SqlColumn<String> userId = column("user_id", JDBCType.VARCHAR);

        public final SqlColumn<String> parentId = column("parent_id", JDBCType.VARCHAR);

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

        public FlowInfo() {
            super("flow_info", FlowInfo::new);
        }
    }
}