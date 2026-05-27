package fun.freechat.mapper;

import java.sql.JDBCType;
import java.time.LocalDateTime;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class CharacterBackendDynamicSqlSupport {
    public static final CharacterBackend characterBackend = new CharacterBackend();

    public static final SqlColumn<String> backendId = characterBackend.backendId;

    public static final SqlColumn<LocalDateTime> gmtCreate = characterBackend.gmtCreate;

    public static final SqlColumn<LocalDateTime> gmtModified = characterBackend.gmtModified;

    public static final SqlColumn<String> characterUid = characterBackend.characterUid;

    public static final SqlColumn<Byte> isDefault = characterBackend.isDefault;

    public static final SqlColumn<String> chatPromptTaskId = characterBackend.chatPromptTaskId;

    public static final SqlColumn<String> greetingPromptTaskId = characterBackend.greetingPromptTaskId;

    public static final SqlColumn<String> moderationModelId = characterBackend.moderationModelId;

    public static final SqlColumn<String> moderationApiKeyName = characterBackend.moderationApiKeyName;

    public static final SqlColumn<String> imageModelId = characterBackend.imageModelId;

    public static final SqlColumn<String> imageApiKeyName = characterBackend.imageApiKeyName;

    public static final SqlColumn<Byte> forwardToUser = characterBackend.forwardToUser;

    public static final SqlColumn<Integer> messageWindowSize = characterBackend.messageWindowSize;

    public static final SqlColumn<Integer> longTermMemoryWindowSize = characterBackend.longTermMemoryWindowSize;

    public static final SqlColumn<Integer> proactiveChatWaitingTime = characterBackend.proactiveChatWaitingTime;

    public static final SqlColumn<Long> initQuota = characterBackend.initQuota;

    public static final SqlColumn<String> quotaType = characterBackend.quotaType;

    public static final SqlColumn<Byte> enableAlbumTool = characterBackend.enableAlbumTool;

    public static final SqlColumn<Byte> enableTts = characterBackend.enableTts;

    public static final SqlColumn<String> ttsSpeakerIdx = characterBackend.ttsSpeakerIdx;

    public static final SqlColumn<String> ttsSpeakerWav = characterBackend.ttsSpeakerWav;

    public static final SqlColumn<String> ttsSpeakerType = characterBackend.ttsSpeakerType;

    public static final SqlColumn<Long> tgBotId = characterBackend.tgBotId;

    public static final SqlColumn<String> moderationApiKeyValue = characterBackend.moderationApiKeyValue;

    public static final SqlColumn<String> moderationParams = characterBackend.moderationParams;

    public static final SqlColumn<String> imageApiKeyValue = characterBackend.imageApiKeyValue;

    public static final SqlColumn<String> imageParams = characterBackend.imageParams;

    public static final class CharacterBackend extends AliasableSqlTable<CharacterBackend> {
        public final SqlColumn<String> backendId = column("backend_id", JDBCType.VARCHAR).withJavaProperty("backendId");

        public final SqlColumn<LocalDateTime> gmtCreate = column("gmt_create", JDBCType.TIMESTAMP).withJavaProperty("gmtCreate");

        public final SqlColumn<LocalDateTime> gmtModified = column("gmt_modified", JDBCType.TIMESTAMP).withJavaProperty("gmtModified");

        public final SqlColumn<String> characterUid = column("character_uid", JDBCType.VARCHAR).withJavaProperty("characterUid");

        public final SqlColumn<Byte> isDefault = column("is_default", JDBCType.TINYINT).withJavaProperty("isDefault");

        public final SqlColumn<String> chatPromptTaskId = column("chat_prompt_task_id", JDBCType.VARCHAR).withJavaProperty("chatPromptTaskId");

        public final SqlColumn<String> greetingPromptTaskId = column("greeting_prompt_task_id", JDBCType.VARCHAR).withJavaProperty("greetingPromptTaskId");

        public final SqlColumn<String> moderationModelId = column("moderation_model_id", JDBCType.VARCHAR).withJavaProperty("moderationModelId");

        public final SqlColumn<String> moderationApiKeyName = column("moderation_api_key_name", JDBCType.VARCHAR).withJavaProperty("moderationApiKeyName");

        public final SqlColumn<String> imageModelId = column("image_model_id", JDBCType.VARCHAR).withJavaProperty("imageModelId");

        public final SqlColumn<String> imageApiKeyName = column("image_api_key_name", JDBCType.VARCHAR).withJavaProperty("imageApiKeyName");

        public final SqlColumn<Byte> forwardToUser = column("forward_to_user", JDBCType.TINYINT).withJavaProperty("forwardToUser");

        public final SqlColumn<Integer> messageWindowSize = column("message_window_size", JDBCType.INTEGER).withJavaProperty("messageWindowSize");

        public final SqlColumn<Integer> longTermMemoryWindowSize = column("long_term_memory_window_size", JDBCType.INTEGER).withJavaProperty("longTermMemoryWindowSize");

        public final SqlColumn<Integer> proactiveChatWaitingTime = column("proactive_chat_waiting_time", JDBCType.INTEGER).withJavaProperty("proactiveChatWaitingTime");

        public final SqlColumn<Long> initQuota = column("init_quota", JDBCType.BIGINT).withJavaProperty("initQuota");

        public final SqlColumn<String> quotaType = column("quota_type", JDBCType.VARCHAR).withJavaProperty("quotaType");

        public final SqlColumn<Byte> enableAlbumTool = column("enable_album_tool", JDBCType.TINYINT).withJavaProperty("enableAlbumTool");

        public final SqlColumn<Byte> enableTts = column("enable_tts", JDBCType.TINYINT).withJavaProperty("enableTts");

        public final SqlColumn<String> ttsSpeakerIdx = column("tts_speaker_idx", JDBCType.VARCHAR).withJavaProperty("ttsSpeakerIdx");

        public final SqlColumn<String> ttsSpeakerWav = column("tts_speaker_wav", JDBCType.VARCHAR).withJavaProperty("ttsSpeakerWav");

        public final SqlColumn<String> ttsSpeakerType = column("tts_speaker_type", JDBCType.VARCHAR).withJavaProperty("ttsSpeakerType");

        public final SqlColumn<Long> tgBotId = column("tg_bot_id", JDBCType.BIGINT).withJavaProperty("tgBotId");

        public final SqlColumn<String> moderationApiKeyValue = column("moderation_api_key_value", JDBCType.LONGVARCHAR).withJavaProperty("moderationApiKeyValue");

        public final SqlColumn<String> moderationParams = column("moderation_params", JDBCType.LONGVARCHAR).withJavaProperty("moderationParams");

        public final SqlColumn<String> imageApiKeyValue = column("image_api_key_value", JDBCType.LONGVARCHAR).withJavaProperty("imageApiKeyValue");

        public final SqlColumn<String> imageParams = column("image_params", JDBCType.LONGVARCHAR).withJavaProperty("imageParams");

        public CharacterBackend() {
            super("character_backend", CharacterBackend::new);
        }
    }
}