package fun.freechat.mapper;

import jakarta.annotation.Generated;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

import java.sql.JDBCType;
import java.util.Date;

public final class InteractiveStatsScoreDetailsDynamicSqlSupport {
    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final InteractiveStatsScoreDetails interactiveStatsScoreDetails = new InteractiveStatsScoreDetails();

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Long> id = interactiveStatsScoreDetails.id;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Date> gmtCreate = interactiveStatsScoreDetails.gmtCreate;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Date> gmtModified = interactiveStatsScoreDetails.gmtModified;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> referType = interactiveStatsScoreDetails.referType;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> referId = interactiveStatsScoreDetails.referId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<String> userId = interactiveStatsScoreDetails.userId;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final SqlColumn<Long> score = interactiveStatsScoreDetails.score;

    @Generated("org.mybatis.generator.api.MyBatisGenerator")
    public static final class InteractiveStatsScoreDetails extends AliasableSqlTable<InteractiveStatsScoreDetails> {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT);

        public final SqlColumn<Date> gmtCreate = column("gmt_create", JDBCType.TIMESTAMP);

        public final SqlColumn<Date> gmtModified = column("gmt_modified", JDBCType.TIMESTAMP);

        public final SqlColumn<String> referType = column("refer_type", JDBCType.VARCHAR);

        public final SqlColumn<String> referId = column("refer_id", JDBCType.VARCHAR);

        public final SqlColumn<String> userId = column("user_id", JDBCType.VARCHAR);

        public final SqlColumn<Long> score = column("score", JDBCType.BIGINT);

        public InteractiveStatsScoreDetails() {
            super("interactive_stats_score_details", InteractiveStatsScoreDetails::new);
        }
    }
}