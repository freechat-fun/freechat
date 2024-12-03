package fun.freechat.mapper;

import jakarta.annotation.Generated;
import java.sql.JDBCType;
import java.util.Date;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class TagDynamicSqlSupport {
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final Tag tag = new Tag();

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Long> id = tag.id;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Date> gmtCreate = tag.gmtCreate;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Date> gmtModified = tag.gmtModified;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> content = tag.content;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> referType = tag.referType;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> referId = tag.referId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> userId = tag.userId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final class Tag extends AliasableSqlTable<Tag> {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT);

        public final SqlColumn<Date> gmtCreate = column("gmt_create", JDBCType.TIMESTAMP);

        public final SqlColumn<Date> gmtModified = column("gmt_modified", JDBCType.TIMESTAMP);

        public final SqlColumn<String> content = column("content", JDBCType.VARCHAR);

        public final SqlColumn<String> referType = column("refer_type", JDBCType.VARCHAR);

        public final SqlColumn<String> referId = column("refer_id", JDBCType.VARCHAR);

        public final SqlColumn<String> userId = column("user_id", JDBCType.VARCHAR);

        public Tag() {
            super("tag", Tag::new);
        }
    }
}