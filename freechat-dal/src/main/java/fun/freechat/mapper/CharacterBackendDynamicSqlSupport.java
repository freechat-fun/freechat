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
    public static final SqlColumn<String> characterId = characterBackend.characterId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Byte> isDefault = characterBackend.isDefault;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> chatPromptTaskId = characterBackend.chatPromptTaskId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> chatExamplePromptTaskId = characterBackend.chatExamplePromptTaskId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> greetingPromptTaskId = characterBackend.greetingPromptTaskId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> experiencePromptTaskId = characterBackend.experiencePromptTaskId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final class CharacterBackend extends AliasableSqlTable<CharacterBackend> {
        public final SqlColumn<String> backendId = column("backend_id", JDBCType.VARCHAR);

        public final SqlColumn<Date> gmtCreate = column("gmt_create", JDBCType.TIMESTAMP);

        public final SqlColumn<Date> gmtModified = column("gmt_modified", JDBCType.TIMESTAMP);

        public final SqlColumn<String> characterId = column("character_id", JDBCType.VARCHAR);

        public final SqlColumn<Byte> isDefault = column("is_default", JDBCType.TINYINT);

        public final SqlColumn<String> chatPromptTaskId = column("chat_prompt_task_id", JDBCType.VARCHAR);

        public final SqlColumn<String> chatExamplePromptTaskId = column("chat_example_prompt_task_id", JDBCType.VARCHAR);

        public final SqlColumn<String> greetingPromptTaskId = column("greeting_prompt_task_id", JDBCType.VARCHAR);

        public final SqlColumn<String> experiencePromptTaskId = column("experience_prompt_task_id", JDBCType.VARCHAR);

        public CharacterBackend() {
            super("character_backend", CharacterBackend::new);
        }
    }
}