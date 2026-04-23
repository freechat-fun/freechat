package fun.freechat.mapper;

import java.sql.JDBCType;
import java.time.LocalDateTime;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class OrgRelationshipDynamicSqlSupport {
    public static final OrgRelationship orgRelationship = new OrgRelationship();

    public static final SqlColumn<Long> id = orgRelationship.id;

    public static final SqlColumn<LocalDateTime> gmtCreate = orgRelationship.gmtCreate;

    public static final SqlColumn<LocalDateTime> gmtModified = orgRelationship.gmtModified;

    public static final SqlColumn<String> userId = orgRelationship.userId;

    public static final SqlColumn<String> ownerId = orgRelationship.ownerId;

    public static final SqlColumn<Byte> isVirtual = orgRelationship.isVirtual;

    public static final SqlColumn<Byte> enabled = orgRelationship.enabled;

    public static final class OrgRelationship extends AliasableSqlTable<OrgRelationship> {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT).withJavaProperty("id");

        public final SqlColumn<LocalDateTime> gmtCreate = column("gmt_create", JDBCType.TIMESTAMP).withJavaProperty("gmtCreate");

        public final SqlColumn<LocalDateTime> gmtModified = column("gmt_modified", JDBCType.TIMESTAMP).withJavaProperty("gmtModified");

        public final SqlColumn<String> userId = column("user_id", JDBCType.VARCHAR).withJavaProperty("userId");

        public final SqlColumn<String> ownerId = column("owner_id", JDBCType.VARCHAR).withJavaProperty("ownerId");

        public final SqlColumn<Byte> isVirtual = column("is_virtual", JDBCType.TINYINT).withJavaProperty("isVirtual");

        public final SqlColumn<Byte> enabled = column("enabled", JDBCType.TINYINT).withJavaProperty("enabled");

        public OrgRelationship() {
            super("org_relationship", OrgRelationship::new);
        }
    }
}