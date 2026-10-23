package fun.freechat.mapper;

import java.sql.JDBCType;
import java.time.LocalDateTime;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class SkillDraftDynamicSqlSupport {
    public static final SkillDraft skillDraft = new SkillDraft();

    public static final SqlColumn<String> draftId = skillDraft.draftId;

    public static final SqlColumn<String> skillId = skillDraft.skillId;

    public static final SqlColumn<String> userId = skillDraft.userId;

    public static final SqlColumn<String> revision = skillDraft.revision;

    public static final SqlColumn<String> expectedRevision = skillDraft.expectedRevision;

    public static final SqlColumn<String> name = skillDraft.name;

    public static final SqlColumn<String> digest = skillDraft.digest;

    public static final SqlColumn<String> status = skillDraft.status;

    public static final SqlColumn<LocalDateTime> expiresAt = skillDraft.expiresAt;

    public static final SqlColumn<LocalDateTime> gcAfter = skillDraft.gcAfter;

    public static final SqlColumn<LocalDateTime> gmtCreate = skillDraft.gmtCreate;

    public static final SqlColumn<LocalDateTime> gmtModified = skillDraft.gmtModified;

    public static final SqlColumn<String> description = skillDraft.description;

    public static final SqlColumn<String> manifest = skillDraft.manifest;

    public static final class SkillDraft extends AliasableSqlTable<SkillDraft> {
        public final SqlColumn<String> draftId = column("draft_id", JDBCType.VARCHAR).withJavaProperty("draftId");

        public final SqlColumn<String> skillId = column("skill_id", JDBCType.VARCHAR).withJavaProperty("skillId");

        public final SqlColumn<String> userId = column("user_id", JDBCType.VARCHAR).withJavaProperty("userId");

        public final SqlColumn<String> revision = column("revision", JDBCType.VARCHAR).withJavaProperty("revision");

        public final SqlColumn<String> expectedRevision = column("expected_revision", JDBCType.VARCHAR).withJavaProperty("expectedRevision");

        public final SqlColumn<String> name = column("name", JDBCType.VARCHAR).withJavaProperty("name");

        public final SqlColumn<String> digest = column("digest", JDBCType.VARCHAR).withJavaProperty("digest");

        public final SqlColumn<String> status = column("status", JDBCType.VARCHAR).withJavaProperty("status");

        public final SqlColumn<LocalDateTime> expiresAt = column("expires_at", JDBCType.TIMESTAMP).withJavaProperty("expiresAt");

        public final SqlColumn<LocalDateTime> gcAfter = column("gc_after", JDBCType.TIMESTAMP).withJavaProperty("gcAfter");

        public final SqlColumn<LocalDateTime> gmtCreate = column("gmt_create", JDBCType.TIMESTAMP).withJavaProperty("gmtCreate");

        public final SqlColumn<LocalDateTime> gmtModified = column("gmt_modified", JDBCType.TIMESTAMP).withJavaProperty("gmtModified");

        public final SqlColumn<String> description = column("description", JDBCType.LONGVARCHAR).withJavaProperty("description");

        public final SqlColumn<String> manifest = column("manifest", JDBCType.LONGVARCHAR).withJavaProperty("manifest");

        public SkillDraft() {
            super("skill_draft", SkillDraft::new);
        }
    }
}