package fun.freechat.mapper;

import java.sql.JDBCType;
import java.time.LocalDateTime;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class PromptInfoDynamicSqlSupport {
    public static final PromptInfo promptInfo = new PromptInfo();

    public static final SqlColumn<Long> promptId = promptInfo.promptId;

    public static final SqlColumn<String> promptUid = promptInfo.promptUid;

    public static final SqlColumn<LocalDateTime> gmtCreate = promptInfo.gmtCreate;

    public static final SqlColumn<LocalDateTime> gmtModified = promptInfo.gmtModified;

    public static final SqlColumn<String> userId = promptInfo.userId;

    public static final SqlColumn<String> parentUid = promptInfo.parentUid;

    public static final SqlColumn<String> visibility = promptInfo.visibility;

    public static final SqlColumn<String> name = promptInfo.name;

    public static final SqlColumn<String> type = promptInfo.type;

    public static final SqlColumn<String> format = promptInfo.format;

    public static final SqlColumn<String> lang = promptInfo.lang;

    public static final SqlColumn<Integer> version = promptInfo.version;

    public static final SqlColumn<String> description = promptInfo.description;

    public static final SqlColumn<String> template = promptInfo.template;

    public static final SqlColumn<String> example = promptInfo.example;

    public static final SqlColumn<String> inputs = promptInfo.inputs;

    public static final SqlColumn<String> ext = promptInfo.ext;

    public static final SqlColumn<String> draft = promptInfo.draft;

    public static final class PromptInfo extends AliasableSqlTable<PromptInfo> {
        public final SqlColumn<Long> promptId = column("prompt_id", JDBCType.BIGINT).withJavaProperty("promptId");

        public final SqlColumn<String> promptUid = column("prompt_uid", JDBCType.VARCHAR).withJavaProperty("promptUid");

        public final SqlColumn<LocalDateTime> gmtCreate = column("gmt_create", JDBCType.TIMESTAMP).withJavaProperty("gmtCreate");

        public final SqlColumn<LocalDateTime> gmtModified = column("gmt_modified", JDBCType.TIMESTAMP).withJavaProperty("gmtModified");

        public final SqlColumn<String> userId = column("user_id", JDBCType.VARCHAR).withJavaProperty("userId");

        public final SqlColumn<String> parentUid = column("parent_uid", JDBCType.VARCHAR).withJavaProperty("parentUid");

        public final SqlColumn<String> visibility = column("visibility", JDBCType.VARCHAR).withJavaProperty("visibility");

        public final SqlColumn<String> name = column("name", JDBCType.VARCHAR).withJavaProperty("name");

        public final SqlColumn<String> type = column("type", JDBCType.VARCHAR).withJavaProperty("type");

        public final SqlColumn<String> format = column("format", JDBCType.VARCHAR).withJavaProperty("format");

        public final SqlColumn<String> lang = column("lang", JDBCType.VARCHAR).withJavaProperty("lang");

        public final SqlColumn<Integer> version = column("version", JDBCType.INTEGER).withJavaProperty("version");

        public final SqlColumn<String> description = column("description", JDBCType.LONGVARCHAR).withJavaProperty("description");

        public final SqlColumn<String> template = column("template", JDBCType.LONGVARCHAR).withJavaProperty("template");

        public final SqlColumn<String> example = column("example", JDBCType.LONGVARCHAR).withJavaProperty("example");

        public final SqlColumn<String> inputs = column("inputs", JDBCType.LONGVARCHAR).withJavaProperty("inputs");

        public final SqlColumn<String> ext = column("ext", JDBCType.LONGVARCHAR).withJavaProperty("ext");

        public final SqlColumn<String> draft = column("draft", JDBCType.LONGVARCHAR).withJavaProperty("draft");

        public PromptInfo() {
            super("prompt_info", PromptInfo::new);
        }
    }
}