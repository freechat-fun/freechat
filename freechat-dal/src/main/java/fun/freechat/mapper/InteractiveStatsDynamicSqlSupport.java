package fun.freechat.mapper;

import jakarta.annotation.Generated;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

import java.sql.JDBCType;
import java.util.Date;

public final class InteractiveStatsDynamicSqlSupport {
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final InteractiveStats interactiveStats = new InteractiveStats();

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Long> id = interactiveStats.id;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Date> gmtCreate = interactiveStats.gmtCreate;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Date> gmtModified = interactiveStats.gmtModified;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> referType = interactiveStats.referType;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> referId = interactiveStats.referId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Long> viewCount = interactiveStats.viewCount;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Long> referCount = interactiveStats.referCount;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Long> recommendCount = interactiveStats.recommendCount;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Long> scoreCount = interactiveStats.scoreCount;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Long> score = interactiveStats.score;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final class InteractiveStats extends AliasableSqlTable<InteractiveStats> {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT);

        public final SqlColumn<Date> gmtCreate = column("gmt_create", JDBCType.TIMESTAMP);

        public final SqlColumn<Date> gmtModified = column("gmt_modified", JDBCType.TIMESTAMP);

        public final SqlColumn<String> referType = column("refer_type", JDBCType.VARCHAR);

        public final SqlColumn<String> referId = column("refer_id", JDBCType.VARCHAR);

        public final SqlColumn<Long> viewCount = column("view_count", JDBCType.BIGINT);

        public final SqlColumn<Long> referCount = column("refer_count", JDBCType.BIGINT);

        public final SqlColumn<Long> recommendCount = column("recommend_count", JDBCType.BIGINT);

        public final SqlColumn<Long> scoreCount = column("score_count", JDBCType.BIGINT);

        public final SqlColumn<Long> score = column("score", JDBCType.BIGINT);

        public InteractiveStats() {
            super("interactive_stats", InteractiveStats::new);
        }
    }
}