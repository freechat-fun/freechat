package fun.freechat.mapper;

import java.sql.JDBCType;
import java.time.LocalDateTime;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class UserDynamicSqlSupport {
    public static final User user = new User();

    public static final SqlColumn<String> userId = user.userId;

    public static final SqlColumn<LocalDateTime> gmtCreate = user.gmtCreate;

    public static final SqlColumn<LocalDateTime> gmtModified = user.gmtModified;

    public static final SqlColumn<String> username = user.username;

    public static final SqlColumn<String> password = user.password;

    public static final SqlColumn<String> nickname = user.nickname;

    public static final SqlColumn<String> givenName = user.givenName;

    public static final SqlColumn<String> middleName = user.middleName;

    public static final SqlColumn<String> familyName = user.familyName;

    public static final SqlColumn<String> preferredUsername = user.preferredUsername;

    public static final SqlColumn<String> profile = user.profile;

    public static final SqlColumn<String> picture = user.picture;

    public static final SqlColumn<String> website = user.website;

    public static final SqlColumn<String> email = user.email;

    public static final SqlColumn<Byte> emailVerified = user.emailVerified;

    public static final SqlColumn<String> gender = user.gender;

    public static final SqlColumn<LocalDateTime> birthdate = user.birthdate;

    public static final SqlColumn<String> zoneinfo = user.zoneinfo;

    public static final SqlColumn<String> locale = user.locale;

    public static final SqlColumn<String> phoneNumber = user.phoneNumber;

    public static final SqlColumn<Byte> phoneNumberVerified = user.phoneNumberVerified;

    public static final SqlColumn<LocalDateTime> updatedAt = user.updatedAt;

    public static final SqlColumn<String> platform = user.platform;

    public static final SqlColumn<Byte> enabled = user.enabled;

    public static final SqlColumn<Byte> locked = user.locked;

    public static final SqlColumn<LocalDateTime> expiresAt = user.expiresAt;

    public static final SqlColumn<LocalDateTime> passwordExpiresAt = user.passwordExpiresAt;

    public static final SqlColumn<String> address = user.address;

    public static final class User extends AliasableSqlTable<User> {
        public final SqlColumn<String> userId = column("user_id", JDBCType.VARCHAR).withJavaProperty("userId");

        public final SqlColumn<LocalDateTime> gmtCreate = column("gmt_create", JDBCType.TIMESTAMP).withJavaProperty("gmtCreate");

        public final SqlColumn<LocalDateTime> gmtModified = column("gmt_modified", JDBCType.TIMESTAMP).withJavaProperty("gmtModified");

        public final SqlColumn<String> username = column("username", JDBCType.VARCHAR).withJavaProperty("username");

        public final SqlColumn<String> password = column("password", JDBCType.VARCHAR).withJavaProperty("password");

        public final SqlColumn<String> nickname = column("nickname", JDBCType.VARCHAR).withJavaProperty("nickname");

        public final SqlColumn<String> givenName = column("given_name", JDBCType.VARCHAR).withJavaProperty("givenName");

        public final SqlColumn<String> middleName = column("middle_name", JDBCType.VARCHAR).withJavaProperty("middleName");

        public final SqlColumn<String> familyName = column("family_name", JDBCType.VARCHAR).withJavaProperty("familyName");

        public final SqlColumn<String> preferredUsername = column("preferred_username", JDBCType.VARCHAR).withJavaProperty("preferredUsername");

        public final SqlColumn<String> profile = column("profile", JDBCType.VARCHAR).withJavaProperty("profile");

        public final SqlColumn<String> picture = column("picture", JDBCType.VARCHAR).withJavaProperty("picture");

        public final SqlColumn<String> website = column("website", JDBCType.VARCHAR).withJavaProperty("website");

        public final SqlColumn<String> email = column("email", JDBCType.VARCHAR).withJavaProperty("email");

        public final SqlColumn<Byte> emailVerified = column("email_verified", JDBCType.TINYINT).withJavaProperty("emailVerified");

        public final SqlColumn<String> gender = column("gender", JDBCType.VARCHAR).withJavaProperty("gender");

        public final SqlColumn<LocalDateTime> birthdate = column("birthdate", JDBCType.TIMESTAMP).withJavaProperty("birthdate");

        public final SqlColumn<String> zoneinfo = column("zoneinfo", JDBCType.VARCHAR).withJavaProperty("zoneinfo");

        public final SqlColumn<String> locale = column("locale", JDBCType.VARCHAR).withJavaProperty("locale");

        public final SqlColumn<String> phoneNumber = column("phone_number", JDBCType.VARCHAR).withJavaProperty("phoneNumber");

        public final SqlColumn<Byte> phoneNumberVerified = column("phone_number_verified", JDBCType.TINYINT).withJavaProperty("phoneNumberVerified");

        public final SqlColumn<LocalDateTime> updatedAt = column("updated_at", JDBCType.TIMESTAMP).withJavaProperty("updatedAt");

        public final SqlColumn<String> platform = column("platform", JDBCType.VARCHAR).withJavaProperty("platform");

        public final SqlColumn<Byte> enabled = column("enabled", JDBCType.TINYINT).withJavaProperty("enabled");

        public final SqlColumn<Byte> locked = column("locked", JDBCType.TINYINT).withJavaProperty("locked");

        public final SqlColumn<LocalDateTime> expiresAt = column("expires_at", JDBCType.TIMESTAMP).withJavaProperty("expiresAt");

        public final SqlColumn<LocalDateTime> passwordExpiresAt = column("password_expires_at", JDBCType.TIMESTAMP).withJavaProperty("passwordExpiresAt");

        public final SqlColumn<String> address = column("address", JDBCType.LONGVARCHAR).withJavaProperty("address");

        public User() {
            super("user", User::new);
        }
    }
}