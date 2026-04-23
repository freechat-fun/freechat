package fun.freechat.mapper;

import java.sql.JDBCType;
import java.time.LocalDateTime;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class InteractiveStatsDynamicSqlSupport {
    public static final InteractiveStats interactiveStats = new InteractiveStats();

    public static final SqlColumn<Long> id = interactiveStats.id;

    public static final SqlColumn<LocalDateTime> gmtCreate = interactiveStats.gmtCreate;

    public static final SqlColumn<LocalDateTime> gmtModified = interactiveStats.gmtModified;

    public static final SqlColumn<String> referType = interactiveStats.referType;

    public static final SqlColumn<String> referId = interactiveStats.referId;

    public static final SqlColumn<Long> viewCount = interactiveStats.viewCount;

    public static final SqlColumn<Long> referCount = interactiveStats.referCount;

    public static final SqlColumn<Long> recommendCount = interactiveStats.recommendCount;

    public static final SqlColumn<Long> scoreCount = interactiveStats.scoreCount;

    public static final SqlColumn<Long> score = interactiveStats.score;

    public static final class InteractiveStats extends AliasableSqlTable<InteractiveStats> {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT).withJavaProperty("id");

        public final SqlColumn<LocalDateTime> gmtCreate = column("gmt_create", JDBCType.TIMESTAMP).withJavaProperty("gmtCreate");

        public final SqlColumn<LocalDateTime> gmtModified = column("gmt_modified", JDBCType.TIMESTAMP).withJavaProperty("gmtModified");

        public final SqlColumn<String> referType = column("refer_type", JDBCType.VARCHAR).withJavaProperty("referType");

        public final SqlColumn<String> referId = column("refer_id", JDBCType.VARCHAR).withJavaProperty("referId");

        public final SqlColumn<Long> viewCount = column("view_count", JDBCType.BIGINT).withJavaProperty("viewCount");

        public final SqlColumn<Long> referCount = column("refer_count", JDBCType.BIGINT).withJavaProperty("referCount");

        public final SqlColumn<Long> recommendCount = column("recommend_count", JDBCType.BIGINT).withJavaProperty("recommendCount");

        public final SqlColumn<Long> scoreCount = column("score_count", JDBCType.BIGINT).withJavaProperty("scoreCount");

        public final SqlColumn<Long> score = column("score", JDBCType.BIGINT).withJavaProperty("score");

        public InteractiveStats() {
            super("interactive_stats", InteractiveStats::new);
        }
    }
}