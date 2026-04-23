package fun.freechat.mapper;

import java.sql.JDBCType;
import java.time.LocalDateTime;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class InteractiveStatsScoreDetailsDynamicSqlSupport {
    public static final InteractiveStatsScoreDetails interactiveStatsScoreDetails = new InteractiveStatsScoreDetails();

    public static final SqlColumn<Long> id = interactiveStatsScoreDetails.id;

    public static final SqlColumn<LocalDateTime> gmtCreate = interactiveStatsScoreDetails.gmtCreate;

    public static final SqlColumn<LocalDateTime> gmtModified = interactiveStatsScoreDetails.gmtModified;

    public static final SqlColumn<String> referType = interactiveStatsScoreDetails.referType;

    public static final SqlColumn<String> referId = interactiveStatsScoreDetails.referId;

    public static final SqlColumn<String> userId = interactiveStatsScoreDetails.userId;

    public static final SqlColumn<Long> score = interactiveStatsScoreDetails.score;

    public static final class InteractiveStatsScoreDetails extends AliasableSqlTable<InteractiveStatsScoreDetails> {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT).withJavaProperty("id");

        public final SqlColumn<LocalDateTime> gmtCreate = column("gmt_create", JDBCType.TIMESTAMP).withJavaProperty("gmtCreate");

        public final SqlColumn<LocalDateTime> gmtModified = column("gmt_modified", JDBCType.TIMESTAMP).withJavaProperty("gmtModified");

        public final SqlColumn<String> referType = column("refer_type", JDBCType.VARCHAR).withJavaProperty("referType");

        public final SqlColumn<String> referId = column("refer_id", JDBCType.VARCHAR).withJavaProperty("referId");

        public final SqlColumn<String> userId = column("user_id", JDBCType.VARCHAR).withJavaProperty("userId");

        public final SqlColumn<Long> score = column("score", JDBCType.BIGINT).withJavaProperty("score");

        public InteractiveStatsScoreDetails() {
            super("interactive_stats_score_details", InteractiveStatsScoreDetails::new);
        }
    }
}