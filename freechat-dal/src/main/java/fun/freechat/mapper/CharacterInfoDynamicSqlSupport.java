package fun.freechat.mapper;

import jakarta.annotation.Generated;
import java.sql.JDBCType;
import java.util.Date;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class CharacterInfoDynamicSqlSupport {
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final CharacterInfo characterInfo = new CharacterInfo();

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Long> characterId = characterInfo.characterId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> characterUid = characterInfo.characterUid;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Date> gmtCreate = characterInfo.gmtCreate;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Date> gmtModified = characterInfo.gmtModified;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> userId = characterInfo.userId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> parentUid = characterInfo.parentUid;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> visibility = characterInfo.visibility;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> name = characterInfo.name;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> nickname = characterInfo.nickname;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> avatar = characterInfo.avatar;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> picture = characterInfo.picture;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> gender = characterInfo.gender;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> lang = characterInfo.lang;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Integer> version = characterInfo.version;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Integer> priority = characterInfo.priority;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> description = characterInfo.description;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> profile = characterInfo.profile;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> greeting = characterInfo.greeting;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> chatStyle = characterInfo.chatStyle;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> chatExample = characterInfo.chatExample;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> defaultScene = characterInfo.defaultScene;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> ext = characterInfo.ext;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> draft = characterInfo.draft;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final class CharacterInfo extends AliasableSqlTable<CharacterInfo> {
        public final SqlColumn<Long> characterId = column("character_id", JDBCType.BIGINT);

        public final SqlColumn<String> characterUid = column("character_uid", JDBCType.VARCHAR);

        public final SqlColumn<Date> gmtCreate = column("gmt_create", JDBCType.TIMESTAMP);

        public final SqlColumn<Date> gmtModified = column("gmt_modified", JDBCType.TIMESTAMP);

        public final SqlColumn<String> userId = column("user_id", JDBCType.VARCHAR);

        public final SqlColumn<String> parentUid = column("parent_uid", JDBCType.VARCHAR);

        public final SqlColumn<String> visibility = column("visibility", JDBCType.VARCHAR);

        public final SqlColumn<String> name = column("name", JDBCType.VARCHAR);

        public final SqlColumn<String> nickname = column("nickname", JDBCType.VARCHAR);

        public final SqlColumn<String> avatar = column("avatar", JDBCType.VARCHAR);

        public final SqlColumn<String> picture = column("picture", JDBCType.VARCHAR);

        public final SqlColumn<String> gender = column("gender", JDBCType.VARCHAR);

        public final SqlColumn<String> lang = column("lang", JDBCType.VARCHAR);

        public final SqlColumn<Integer> version = column("version", JDBCType.INTEGER);

        public final SqlColumn<Integer> priority = column("priority", JDBCType.INTEGER);

        public final SqlColumn<String> description = column("description", JDBCType.LONGVARCHAR);

        public final SqlColumn<String> profile = column("profile", JDBCType.LONGVARCHAR);

        public final SqlColumn<String> greeting = column("greeting", JDBCType.LONGVARCHAR);

        public final SqlColumn<String> chatStyle = column("chat_style", JDBCType.LONGVARCHAR);

        public final SqlColumn<String> chatExample = column("chat_example", JDBCType.LONGVARCHAR);

        public final SqlColumn<String> defaultScene = column("default_scene", JDBCType.LONGVARCHAR);

        public final SqlColumn<String> ext = column("ext", JDBCType.LONGVARCHAR);

        public final SqlColumn<String> draft = column("draft", JDBCType.LONGVARCHAR);

        public CharacterInfo() {
            super("character_info", CharacterInfo::new);
        }
    }
}