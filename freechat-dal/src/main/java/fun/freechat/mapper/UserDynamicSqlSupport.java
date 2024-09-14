package fun.freechat.mapper;

import jakarta.annotation.Generated;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

import java.sql.JDBCType;
import java.util.Date;

public final class UserDynamicSqlSupport {
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final User user = new User();

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> userId = user.userId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Date> gmtCreate = user.gmtCreate;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Date> gmtModified = user.gmtModified;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> username = user.username;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> password = user.password;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> nickname = user.nickname;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> givenName = user.givenName;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> middleName = user.middleName;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> familyName = user.familyName;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> preferredUsername = user.preferredUsername;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> profile = user.profile;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> picture = user.picture;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> website = user.website;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> email = user.email;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Byte> emailVerified = user.emailVerified;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> gender = user.gender;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Date> birthdate = user.birthdate;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> zoneinfo = user.zoneinfo;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> locale = user.locale;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> phoneNumber = user.phoneNumber;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Byte> phoneNumberVerified = user.phoneNumberVerified;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Date> updatedAt = user.updatedAt;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> platform = user.platform;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Byte> enabled = user.enabled;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Byte> locked = user.locked;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Date> expiresAt = user.expiresAt;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Date> passwordExpiresAt = user.passwordExpiresAt;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> address = user.address;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final class User extends AliasableSqlTable<User> {
        public final SqlColumn<String> userId = column("user_id", JDBCType.VARCHAR);

        public final SqlColumn<Date> gmtCreate = column("gmt_create", JDBCType.TIMESTAMP);

        public final SqlColumn<Date> gmtModified = column("gmt_modified", JDBCType.TIMESTAMP);

        public final SqlColumn<String> username = column("username", JDBCType.VARCHAR);

        public final SqlColumn<String> password = column("password", JDBCType.VARCHAR);

        public final SqlColumn<String> nickname = column("nickname", JDBCType.VARCHAR);

        public final SqlColumn<String> givenName = column("given_name", JDBCType.VARCHAR);

        public final SqlColumn<String> middleName = column("middle_name", JDBCType.VARCHAR);

        public final SqlColumn<String> familyName = column("family_name", JDBCType.VARCHAR);

        public final SqlColumn<String> preferredUsername = column("preferred_username", JDBCType.VARCHAR);

        public final SqlColumn<String> profile = column("profile", JDBCType.VARCHAR);

        public final SqlColumn<String> picture = column("picture", JDBCType.VARCHAR);

        public final SqlColumn<String> website = column("website", JDBCType.VARCHAR);

        public final SqlColumn<String> email = column("email", JDBCType.VARCHAR);

        public final SqlColumn<Byte> emailVerified = column("email_verified", JDBCType.TINYINT);

        public final SqlColumn<String> gender = column("gender", JDBCType.VARCHAR);

        public final SqlColumn<Date> birthdate = column("birthdate", JDBCType.TIMESTAMP);

        public final SqlColumn<String> zoneinfo = column("zoneinfo", JDBCType.VARCHAR);

        public final SqlColumn<String> locale = column("locale", JDBCType.VARCHAR);

        public final SqlColumn<String> phoneNumber = column("phone_number", JDBCType.VARCHAR);

        public final SqlColumn<Byte> phoneNumberVerified = column("phone_number_verified", JDBCType.TINYINT);

        public final SqlColumn<Date> updatedAt = column("updated_at", JDBCType.TIMESTAMP);

        public final SqlColumn<String> platform = column("platform", JDBCType.VARCHAR);

        public final SqlColumn<Byte> enabled = column("enabled", JDBCType.TINYINT);

        public final SqlColumn<Byte> locked = column("locked", JDBCType.TINYINT);

        public final SqlColumn<Date> expiresAt = column("expires_at", JDBCType.TIMESTAMP);

        public final SqlColumn<Date> passwordExpiresAt = column("password_expires_at", JDBCType.TIMESTAMP);

        public final SqlColumn<String> address = column("address", JDBCType.LONGVARCHAR);

        public User() {
            super("user", User::new);
        }
    }
}