package fun.freechat.mapper;

import java.sql.JDBCType;
import java.time.LocalDateTime;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class CharacterInfoDynamicSqlSupport {
    public static final CharacterInfo characterInfo = new CharacterInfo();

    public static final SqlColumn<Long> characterId = characterInfo.characterId;

    public static final SqlColumn<String> characterUid = characterInfo.characterUid;

    public static final SqlColumn<LocalDateTime> gmtCreate = characterInfo.gmtCreate;

    public static final SqlColumn<LocalDateTime> gmtModified = characterInfo.gmtModified;

    public static final SqlColumn<String> userId = characterInfo.userId;

    public static final SqlColumn<String> parentUid = characterInfo.parentUid;

    public static final SqlColumn<String> visibility = characterInfo.visibility;

    public static final SqlColumn<String> name = characterInfo.name;

    public static final SqlColumn<String> nickname = characterInfo.nickname;

    public static final SqlColumn<String> avatar = characterInfo.avatar;

    public static final SqlColumn<String> picture = characterInfo.picture;

    public static final SqlColumn<String> video = characterInfo.video;

    public static final SqlColumn<String> gender = characterInfo.gender;

    public static final SqlColumn<String> lang = characterInfo.lang;

    public static final SqlColumn<Integer> version = characterInfo.version;

    public static final SqlColumn<Integer> priority = characterInfo.priority;

    public static final SqlColumn<Long> tgBotId = characterInfo.tgBotId;

    public static final SqlColumn<String> description = characterInfo.description;

    public static final SqlColumn<String> profile = characterInfo.profile;

    public static final SqlColumn<String> greeting = characterInfo.greeting;

    public static final SqlColumn<String> chatStyle = characterInfo.chatStyle;

    public static final SqlColumn<String> chatExample = characterInfo.chatExample;

    public static final SqlColumn<String> defaultScene = characterInfo.defaultScene;

    public static final SqlColumn<String> ext = characterInfo.ext;

    public static final SqlColumn<String> draft = characterInfo.draft;

    public static final class CharacterInfo extends AliasableSqlTable<CharacterInfo> {
        public final SqlColumn<Long> characterId = column("character_id", JDBCType.BIGINT).withJavaProperty("characterId");

        public final SqlColumn<String> characterUid = column("character_uid", JDBCType.VARCHAR).withJavaProperty("characterUid");

        public final SqlColumn<LocalDateTime> gmtCreate = column("gmt_create", JDBCType.TIMESTAMP).withJavaProperty("gmtCreate");

        public final SqlColumn<LocalDateTime> gmtModified = column("gmt_modified", JDBCType.TIMESTAMP).withJavaProperty("gmtModified");

        public final SqlColumn<String> userId = column("user_id", JDBCType.VARCHAR).withJavaProperty("userId");

        public final SqlColumn<String> parentUid = column("parent_uid", JDBCType.VARCHAR).withJavaProperty("parentUid");

        public final SqlColumn<String> visibility = column("visibility", JDBCType.VARCHAR).withJavaProperty("visibility");

        public final SqlColumn<String> name = column("name", JDBCType.VARCHAR).withJavaProperty("name");

        public final SqlColumn<String> nickname = column("nickname", JDBCType.VARCHAR).withJavaProperty("nickname");

        public final SqlColumn<String> avatar = column("avatar", JDBCType.VARCHAR).withJavaProperty("avatar");

        public final SqlColumn<String> picture = column("picture", JDBCType.VARCHAR).withJavaProperty("picture");

        public final SqlColumn<String> video = column("video", JDBCType.VARCHAR).withJavaProperty("video");

        public final SqlColumn<String> gender = column("gender", JDBCType.VARCHAR).withJavaProperty("gender");

        public final SqlColumn<String> lang = column("lang", JDBCType.VARCHAR).withJavaProperty("lang");

        public final SqlColumn<Integer> version = column("version", JDBCType.INTEGER).withJavaProperty("version");

        public final SqlColumn<Integer> priority = column("priority", JDBCType.INTEGER).withJavaProperty("priority");

        public final SqlColumn<Long> tgBotId = column("tg_bot_id", JDBCType.BIGINT).withJavaProperty("tgBotId");

        public final SqlColumn<String> description = column("description", JDBCType.LONGVARCHAR).withJavaProperty("description");

        public final SqlColumn<String> profile = column("profile", JDBCType.LONGVARCHAR).withJavaProperty("profile");

        public final SqlColumn<String> greeting = column("greeting", JDBCType.LONGVARCHAR).withJavaProperty("greeting");

        public final SqlColumn<String> chatStyle = column("chat_style", JDBCType.LONGVARCHAR).withJavaProperty("chatStyle");

        public final SqlColumn<String> chatExample = column("chat_example", JDBCType.LONGVARCHAR).withJavaProperty("chatExample");

        public final SqlColumn<String> defaultScene = column("default_scene", JDBCType.LONGVARCHAR).withJavaProperty("defaultScene");

        public final SqlColumn<String> ext = column("ext", JDBCType.LONGVARCHAR).withJavaProperty("ext");

        public final SqlColumn<String> draft = column("draft", JDBCType.LONGVARCHAR).withJavaProperty("draft");

        public CharacterInfo() {
            super("character_info", CharacterInfo::new);
        }
    }
}