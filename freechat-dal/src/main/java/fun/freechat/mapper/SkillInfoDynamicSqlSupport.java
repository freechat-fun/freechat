package fun.freechat.mapper;

import java.sql.JDBCType;
import java.time.LocalDateTime;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class SkillInfoDynamicSqlSupport {
    public static final SkillInfo skillInfo = new SkillInfo();

    public static final SqlColumn<String> skillId = skillInfo.skillId;

    public static final SqlColumn<String> userId = skillInfo.userId;

    public static final SqlColumn<String> revision = skillInfo.revision;

    public static final SqlColumn<String> name = skillInfo.name;

    public static final SqlColumn<String> digest = skillInfo.digest;

    public static final SqlColumn<LocalDateTime> gmtCreate = skillInfo.gmtCreate;

    public static final SqlColumn<LocalDateTime> gmtModified = skillInfo.gmtModified;

    public static final SqlColumn<String> description = skillInfo.description;

    public static final SqlColumn<String> manifest = skillInfo.manifest;

    public static final class SkillInfo extends AliasableSqlTable<SkillInfo> {
        public final SqlColumn<String> skillId = column("skill_id", JDBCType.VARCHAR).withJavaProperty("skillId");

        public final SqlColumn<String> userId = column("user_id", JDBCType.VARCHAR).withJavaProperty("userId");

        public final SqlColumn<String> revision = column("revision", JDBCType.VARCHAR).withJavaProperty("revision");

        public final SqlColumn<String> name = column("name", JDBCType.VARCHAR).withJavaProperty("name");

        public final SqlColumn<String> digest = column("digest", JDBCType.VARCHAR).withJavaProperty("digest");

        public final SqlColumn<LocalDateTime> gmtCreate = column("gmt_create", JDBCType.TIMESTAMP).withJavaProperty("gmtCreate");

        public final SqlColumn<LocalDateTime> gmtModified = column("gmt_modified", JDBCType.TIMESTAMP).withJavaProperty("gmtModified");

        public final SqlColumn<String> description = column("description", JDBCType.LONGVARCHAR).withJavaProperty("description");

        public final SqlColumn<String> manifest = column("manifest", JDBCType.LONGVARCHAR).withJavaProperty("manifest");

        public SkillInfo() {
            super("skill_info", SkillInfo::new);
        }
    }
}