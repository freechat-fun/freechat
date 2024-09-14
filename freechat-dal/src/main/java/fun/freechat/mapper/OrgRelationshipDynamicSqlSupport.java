package fun.freechat.mapper;

import jakarta.annotation.Generated;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

import java.sql.JDBCType;
import java.util.Date;

public final class OrgRelationshipDynamicSqlSupport {
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final OrgRelationship orgRelationship = new OrgRelationship();

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Long> id = orgRelationship.id;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Date> gmtCreate = orgRelationship.gmtCreate;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Date> gmtModified = orgRelationship.gmtModified;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> userId = orgRelationship.userId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> ownerId = orgRelationship.ownerId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Byte> isVirtual = orgRelationship.isVirtual;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Byte> enabled = orgRelationship.enabled;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final class OrgRelationship extends AliasableSqlTable<OrgRelationship> {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT);

        public final SqlColumn<Date> gmtCreate = column("gmt_create", JDBCType.TIMESTAMP);

        public final SqlColumn<Date> gmtModified = column("gmt_modified", JDBCType.TIMESTAMP);

        public final SqlColumn<String> userId = column("user_id", JDBCType.VARCHAR);

        public final SqlColumn<String> ownerId = column("owner_id", JDBCType.VARCHAR);

        public final SqlColumn<Byte> isVirtual = column("is_virtual", JDBCType.TINYINT);

        public final SqlColumn<Byte> enabled = column("enabled", JDBCType.TINYINT);

        public OrgRelationship() {
            super("org_relationship", OrgRelationship::new);
        }
    }
}