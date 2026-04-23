package fun.freechat.mapper;

import java.sql.JDBCType;
import java.time.LocalDateTime;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class PromptTaskDynamicSqlSupport {
    public static final PromptTask promptTask = new PromptTask();

    public static final SqlColumn<String> taskId = promptTask.taskId;

    public static final SqlColumn<LocalDateTime> gmtCreate = promptTask.gmtCreate;

    public static final SqlColumn<LocalDateTime> gmtModified = promptTask.gmtModified;

    public static final SqlColumn<LocalDateTime> gmtStart = promptTask.gmtStart;

    public static final SqlColumn<LocalDateTime> gmtEnd = promptTask.gmtEnd;

    public static final SqlColumn<String> promptUid = promptTask.promptUid;

    public static final SqlColumn<Byte> draft = promptTask.draft;

    public static final SqlColumn<String> modelId = promptTask.modelId;

    public static final SqlColumn<String> apiKeyName = promptTask.apiKeyName;

    public static final SqlColumn<String> cron = promptTask.cron;

    public static final SqlColumn<String> status = promptTask.status;

    public static final SqlColumn<String> variables = promptTask.variables;

    public static final SqlColumn<String> apiKeyValue = promptTask.apiKeyValue;

    public static final SqlColumn<String> params = promptTask.params;

    public static final SqlColumn<String> ext = promptTask.ext;

    public static final class PromptTask extends AliasableSqlTable<PromptTask> {
        public final SqlColumn<String> taskId = column("task_id", JDBCType.VARCHAR).withJavaProperty("taskId");

        public final SqlColumn<LocalDateTime> gmtCreate = column("gmt_create", JDBCType.TIMESTAMP).withJavaProperty("gmtCreate");

        public final SqlColumn<LocalDateTime> gmtModified = column("gmt_modified", JDBCType.TIMESTAMP).withJavaProperty("gmtModified");

        public final SqlColumn<LocalDateTime> gmtStart = column("gmt_start", JDBCType.TIMESTAMP).withJavaProperty("gmtStart");

        public final SqlColumn<LocalDateTime> gmtEnd = column("gmt_end", JDBCType.TIMESTAMP).withJavaProperty("gmtEnd");

        public final SqlColumn<String> promptUid = column("prompt_uid", JDBCType.VARCHAR).withJavaProperty("promptUid");

        public final SqlColumn<Byte> draft = column("draft", JDBCType.TINYINT).withJavaProperty("draft");

        public final SqlColumn<String> modelId = column("model_id", JDBCType.VARCHAR).withJavaProperty("modelId");

        public final SqlColumn<String> apiKeyName = column("api_key_name", JDBCType.VARCHAR).withJavaProperty("apiKeyName");

        public final SqlColumn<String> cron = column("cron", JDBCType.VARCHAR).withJavaProperty("cron");

        public final SqlColumn<String> status = column("status", JDBCType.VARCHAR).withJavaProperty("status");

        public final SqlColumn<String> variables = column("variables", JDBCType.LONGVARCHAR).withJavaProperty("variables");

        public final SqlColumn<String> apiKeyValue = column("api_key_value", JDBCType.LONGVARCHAR).withJavaProperty("apiKeyValue");

        public final SqlColumn<String> params = column("params", JDBCType.LONGVARCHAR).withJavaProperty("params");

        public final SqlColumn<String> ext = column("ext", JDBCType.LONGVARCHAR).withJavaProperty("ext");

        public PromptTask() {
            super("prompt_task", PromptTask::new);
        }
    }
}