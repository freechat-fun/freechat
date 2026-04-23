package fun.freechat.mapper;

import java.sql.JDBCType;
import java.time.LocalDateTime;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class TagDynamicSqlSupport {
    public static final Tag tag = new Tag();

    public static final SqlColumn<Long> id = tag.id;

    public static final SqlColumn<LocalDateTime> gmtCreate = tag.gmtCreate;

    public static final SqlColumn<LocalDateTime> gmtModified = tag.gmtModified;

    public static final SqlColumn<String> content = tag.content;

    public static final SqlColumn<String> referType = tag.referType;

    public static final SqlColumn<String> referId = tag.referId;

    public static final SqlColumn<String> userId = tag.userId;

    public static final class Tag extends AliasableSqlTable<Tag> {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT).withJavaProperty("id");

        public final SqlColumn<LocalDateTime> gmtCreate = column("gmt_create", JDBCType.TIMESTAMP).withJavaProperty("gmtCreate");

        public final SqlColumn<LocalDateTime> gmtModified = column("gmt_modified", JDBCType.TIMESTAMP).withJavaProperty("gmtModified");

        public final SqlColumn<String> content = column("content", JDBCType.VARCHAR).withJavaProperty("content");

        public final SqlColumn<String> referType = column("refer_type", JDBCType.VARCHAR).withJavaProperty("referType");

        public final SqlColumn<String> referId = column("refer_id", JDBCType.VARCHAR).withJavaProperty("referId");

        public final SqlColumn<String> userId = column("user_id", JDBCType.VARCHAR).withJavaProperty("userId");

        public Tag() {
            super("tag", Tag::new);
        }
    }
}