package fun.freechat.mapper;

import jakarta.annotation.Generated;
import java.sql.JDBCType;
import java.util.Date;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class PromptInfoDynamicSqlSupport {
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final PromptInfo promptInfo = new PromptInfo();

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> promptId = promptInfo.promptId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Date> gmtCreate = promptInfo.gmtCreate;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Date> gmtModified = promptInfo.gmtModified;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> userId = promptInfo.userId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> parentId = promptInfo.parentId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> visibility = promptInfo.visibility;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> name = promptInfo.name;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> type = promptInfo.type;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> format = promptInfo.format;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> lang = promptInfo.lang;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Integer> version = promptInfo.version;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> description = promptInfo.description;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> template = promptInfo.template;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> example = promptInfo.example;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> inputs = promptInfo.inputs;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> ext = promptInfo.ext;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> draft = promptInfo.draft;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final class PromptInfo extends AliasableSqlTable<PromptInfo> {
        public final SqlColumn<String> promptId = column("prompt_id", JDBCType.VARCHAR);

        public final SqlColumn<Date> gmtCreate = column("gmt_create", JDBCType.TIMESTAMP);

        public final SqlColumn<Date> gmtModified = column("gmt_modified", JDBCType.TIMESTAMP);

        public final SqlColumn<String> userId = column("user_id", JDBCType.VARCHAR);

        public final SqlColumn<String> parentId = column("parent_id", JDBCType.VARCHAR);

        public final SqlColumn<String> visibility = column("visibility", JDBCType.VARCHAR);

        public final SqlColumn<String> name = column("name", JDBCType.VARCHAR);

        public final SqlColumn<String> type = column("type", JDBCType.VARCHAR);

        public final SqlColumn<String> format = column("format", JDBCType.VARCHAR);

        public final SqlColumn<String> lang = column("lang", JDBCType.VARCHAR);

        public final SqlColumn<Integer> version = column("version", JDBCType.INTEGER);

        public final SqlColumn<String> description = column("description", JDBCType.LONGVARCHAR);

        public final SqlColumn<String> template = column("template", JDBCType.LONGVARCHAR);

        public final SqlColumn<String> example = column("example", JDBCType.LONGVARCHAR);

        public final SqlColumn<String> inputs = column("inputs", JDBCType.LONGVARCHAR);

        public final SqlColumn<String> ext = column("ext", JDBCType.LONGVARCHAR);

        public final SqlColumn<String> draft = column("draft", JDBCType.LONGVARCHAR);

        public PromptInfo() {
            super("prompt_info", PromptInfo::new);
        }
    }
}