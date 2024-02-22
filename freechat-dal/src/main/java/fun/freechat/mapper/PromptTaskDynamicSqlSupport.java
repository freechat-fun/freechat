package fun.freechat.mapper;

import jakarta.annotation.Generated;
import java.sql.JDBCType;
import java.util.Date;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class PromptTaskDynamicSqlSupport {
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final PromptTask promptTask = new PromptTask();

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> taskId = promptTask.taskId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Date> gmtCreate = promptTask.gmtCreate;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Date> gmtModified = promptTask.gmtModified;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Date> gmtExecuted = promptTask.gmtExecuted;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> promptId = promptTask.promptId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Byte> draft = promptTask.draft;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> modelId = promptTask.modelId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> apiKeyName = promptTask.apiKeyName;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> cron = promptTask.cron;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> status = promptTask.status;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> variables = promptTask.variables;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> apiKeyValue = promptTask.apiKeyValue;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> params = promptTask.params;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final class PromptTask extends AliasableSqlTable<PromptTask> {
        public final SqlColumn<String> taskId = column("task_id", JDBCType.VARCHAR);

        public final SqlColumn<Date> gmtCreate = column("gmt_create", JDBCType.TIMESTAMP);

        public final SqlColumn<Date> gmtModified = column("gmt_modified", JDBCType.TIMESTAMP);

        public final SqlColumn<Date> gmtExecuted = column("gmt_executed", JDBCType.TIMESTAMP);

        public final SqlColumn<String> promptId = column("prompt_id", JDBCType.VARCHAR);

        public final SqlColumn<Byte> draft = column("draft", JDBCType.TINYINT);

        public final SqlColumn<String> modelId = column("model_id", JDBCType.VARCHAR);

        public final SqlColumn<String> apiKeyName = column("api_key_name", JDBCType.VARCHAR);

        public final SqlColumn<String> cron = column("cron", JDBCType.VARCHAR);

        public final SqlColumn<String> status = column("status", JDBCType.VARCHAR);

        public final SqlColumn<String> variables = column("variables", JDBCType.LONGVARCHAR);

        public final SqlColumn<String> apiKeyValue = column("api_key_value", JDBCType.LONGVARCHAR);

        public final SqlColumn<String> params = column("params", JDBCType.LONGVARCHAR);

        public PromptTask() {
            super("prompt_task", PromptTask::new);
        }
    }
}