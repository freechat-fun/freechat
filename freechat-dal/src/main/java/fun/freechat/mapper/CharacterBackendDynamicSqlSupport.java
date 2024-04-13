package fun.freechat.mapper;

import jakarta.annotation.Generated;
import java.sql.JDBCType;
import java.util.Date;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class CharacterBackendDynamicSqlSupport {
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final CharacterBackend characterBackend = new CharacterBackend();

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> backendId = characterBackend.backendId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Date> gmtCreate = characterBackend.gmtCreate;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Date> gmtModified = characterBackend.gmtModified;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> characterUid = characterBackend.characterUid;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Byte> isDefault = characterBackend.isDefault;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> chatPromptTaskId = characterBackend.chatPromptTaskId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> greetingPromptTaskId = characterBackend.greetingPromptTaskId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> moderationModelId = characterBackend.moderationModelId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> moderationApiKeyName = characterBackend.moderationApiKeyName;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Byte> forwardToUser = characterBackend.forwardToUser;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Integer> messageWindowSize = characterBackend.messageWindowSize;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Byte> longTermMemoryEnabled = characterBackend.longTermMemoryEnabled;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Integer> longTermMemoryWindowSize = characterBackend.longTermMemoryWindowSize;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Long> initQuota = characterBackend.initQuota;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> quotaType = characterBackend.quotaType;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> moderationParams = characterBackend.moderationParams;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final class CharacterBackend extends AliasableSqlTable<CharacterBackend> {
        public final SqlColumn<String> backendId = column("backend_id", JDBCType.VARCHAR);

        public final SqlColumn<Date> gmtCreate = column("gmt_create", JDBCType.TIMESTAMP);

        public final SqlColumn<Date> gmtModified = column("gmt_modified", JDBCType.TIMESTAMP);

        public final SqlColumn<String> characterUid = column("character_uid", JDBCType.VARCHAR);

        public final SqlColumn<Byte> isDefault = column("is_default", JDBCType.TINYINT);

        public final SqlColumn<String> chatPromptTaskId = column("chat_prompt_task_id", JDBCType.VARCHAR);

        public final SqlColumn<String> greetingPromptTaskId = column("greeting_prompt_task_id", JDBCType.VARCHAR);

        public final SqlColumn<String> moderationModelId = column("moderation_model_id", JDBCType.VARCHAR);

        public final SqlColumn<String> moderationApiKeyName = column("moderation_api_key_name", JDBCType.VARCHAR);

        public final SqlColumn<Byte> forwardToUser = column("forward_to_user", JDBCType.TINYINT);

        public final SqlColumn<Integer> messageWindowSize = column("message_window_size", JDBCType.INTEGER);

        public final SqlColumn<Byte> longTermMemoryEnabled = column("long_term_memory_enabled", JDBCType.TINYINT);

        public final SqlColumn<Integer> longTermMemoryWindowSize = column("long_term_memory_window_size", JDBCType.INTEGER);

        public final SqlColumn<Long> initQuota = column("init_quota", JDBCType.BIGINT);

        public final SqlColumn<String> quotaType = column("quota_type", JDBCType.VARCHAR);

        public final SqlColumn<String> moderationParams = column("moderation_params", JDBCType.LONGVARCHAR);

        public CharacterBackend() {
            super("character_backend", CharacterBackend::new);
        }
    }
}